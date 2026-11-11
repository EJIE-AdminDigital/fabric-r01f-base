"""Renames a FABRIC (r01f) persistable object in every layer: files, folders (packages) and identifiers.

    python rename_persistable_object.py --from MediaOutlet --to Media --root <dir> [--root <dir> ...]
                                        [--protect MediaGroup ...] [--keep "some text" ...]
                                        [--show <identifier> ...] [--backup <dir>] [--apply]
    python rename_persistable_object.py --undo <backup dir>

Without --apply it is a dry run: it only prints the plan (files touched, files moved, collisions).
With --apply it backs up every touched file to --backup (with a manifest.json), writes the renamed
contents, moves the files, removes the folders left empty and sorts the 'import {domain}.' blocks
that changed.

Case variants derived from --from / --to (e.g. MediaOutlet / Media):
    PascalCase  MediaOutlet -> Media     class names, getters, fields like _forMediaOutletCRUD
    camelCase   mediaOutlet -> media     fields, variables, @MarshallType names
    lowercase   mediaoutlet -> media     ONLY as a whole package / path segment (.mediaoutlet. \\mediaoutlet\\)
    UPPERCASE   MEDIAOUTLET -> MEDIA     table / index names, enum constants

The names given with --protect (and every other model class of the component found under the roots)
are never touched, in any of their case variants: that is what keeps 'Media' -> 'Outlet' from also
renaming MediaGroup or MediaRegister. Text given with --keep is left as it is (e.g. Javadoc prose).

The manifest lists, for each file, its old path, new path and the line numbers that changed, so the
formatter can be applied to those lines only. Line endings and line counts are preserved.
Standard library only (Python 3.8+). Long Windows paths are handled with the \\\\?\\ prefix."""
import argparse
import json
import os
import re
import shutil
import sys

SKIP_DIRS = {'target', 'bin', 'build', '.git', '.settings', '.idea', 'vscode-target', 'node_modules'}
EXTS = ('.java', '.xml', '.yml', '.yaml', '.properties', '.md', '.txt', '.json')
MODEL_CLASS_RE = re.compile(r'^[A-Z]{2}\d{2}([A-Z]\w*)\.java$')
COMPONENT_BASE_RE = re.compile(r'^[A-Z]{2}\d{2}([A-Z]\w*?)PersistableObject(?:WithID)?Base\.java$')


def long_path(p):
    p = os.path.abspath(p)
    if os.name == 'nt' and not p.startswith('\\\\?\\'):
        return '\\\\?\\' + p
    return p


def short_path(p):
    return p[4:] if p.startswith('\\\\?\\') else p


def variants(pascal):
    camel = pascal[0].lower() + pascal[1:]
    kebab = re.sub(r'(?<!^)(?=[A-Z])', '-', pascal).lower()
    return {'pascal': pascal, 'camel': camel, 'lower': pascal.lower(), 'upper': pascal.upper(),
            'kebab': kebab, 'snake': kebab.replace('-', '_').upper()}


def build_rules(old, new):
    o, n = variants(old), variants(new)
    seg = r'[.\\/;]'        # package / path separators (and the ';' that ends a package or import)
    return [
        # PascalCase: must end at a camel-case boundary (MediaOutletType -> MediaType, but not MediaOutlets)
        (re.compile(re.escape(o['pascal']) + r'(?![a-z0-9])'), n['pascal']),
        # camelCase: starts an identifier (_mediaOutlet, mediaOutletOid, "mediaOutletRef"); not a kebab-case
        # folder or artifact name (media-register)
        (re.compile(r'(?<![A-Za-z0-9-])' + re.escape(o['camel']) + r'(?![a-z0-9-])'), n['camel']),
        # lowercase: a whole package or path segment only
        (re.compile(r'(?<=' + seg + ')' + re.escape(o['lower']) + r'(?=' + seg + ')'), n['lower']),
        # UPPERCASE: table, index and enum constant names (OG11MEDIAOUTLET, OG11MEDIAOUTLETIDX_BY_ID)
        (re.compile(r'(?<![A-Z])' + re.escape(o['upper'])), n['upper']),
    ]


def make_subst(old, new, protect, keep):
    rules = build_rules(old, new)
    # protected tokens: every case variant of each protected name that contains a variant of the old name
    tokens = set(keep)
    old_variants = [v for k, v in variants(old).items() if k in ('pascal', 'camel', 'lower', 'upper')]
    for name in protect:
        words = variants(name)['kebab'].replace('-', ' ')      # prose: "media group", "Media group", "MEDIA GROUP"
        for v in list(variants(name).values()) + [words, words.capitalize(), words.title(), words.upper()]:
            if any(ov in v for ov in old_variants):
                tokens.add(v)
    tokens = sorted(tokens, key=len, reverse=True)

    def subst(s):
        shelf = {}
        for i, t in enumerate(tokens):
            if t in s:
                mark = '\0%d\0' % i
                shelf[mark] = t
                s = s.replace(t, mark)
        count = 0
        for rx, rep in rules:
            s, k = rx.subn(rep, s)
            count += k
        for mark, t in shelf.items():
            s = s.replace(mark, t)
        return s, count
    return subst


def find_model_classes(roots):
    """Names (without app code) of the model classes under '.api.model' (e.g. MediaGroup, Media)
    and of the component, taken from its base classes in '.api.model.base' (e.g. MediaRegister)"""
    out = set()
    for root in roots:
        for d, dirs, fs in os.walk(root):
            dirs[:] = [x for x in dirs if x not in SKIP_DIRS]
            d_ = d.replace('\\', '/')
            for fn in fs:
                if d_.endswith('/api/model'):
                    m = MODEL_CLASS_RE.match(fn)
                elif d_.endswith('/api/model/base'):
                    m = COMPONENT_BASE_RE.match(fn)
                else:
                    continue
                if m:
                    out.add(m.group(1))
    return out


def changed_identifiers(before, after):
    """{old identifier: new identifier} for the identifiers that differ between two versions of a text"""
    out = {}
    for x, y in zip(before.splitlines(), after.splitlines()):
        if x != y:
            tx, ty = re.findall(r'\w+', x), re.findall(r'\w+', y)
            if len(tx) == len(ty):
                out.update({a: b for a, b in zip(tx, ty) if a != b})
    return out


def read(p):
    with open(p, encoding='utf-8', newline='') as f:
        return f.read()


def write(p, s):
    with open(p, 'w', encoding='utf-8', newline='') as f:
        f.write(s)


def sort_import_blocks(s, prefix, changed_lines):
    """Sorts each contiguous block of 'import {prefix}' lines that contains a changed line (1-based numbers)"""
    nl = '\r\n' if '\r\n' in s else '\n'
    lines = s.split(nl)
    i = 0
    while i < len(lines):
        if lines[i].startswith(prefix):
            j = i
            while j < len(lines) and lines[j].startswith(prefix):
                j += 1
            if any(i < k <= j for k in changed_lines):
                lines[i:j] = sorted(lines[i:j])
            i = j
        else:
            i += 1
    return nl.join(lines)


def changed_line_numbers(before, after):
    b = before.replace('\r\n', '\n').split('\n')
    a = after.replace('\r\n', '\n').split('\n')
    if len(a) != len(b):
        raise SystemExit('line count changed: this script must never add or remove lines')
    return [k + 1 for k, (x, y) in enumerate(zip(b, a)) if x != y]


def undo(backup):
    """Restores a rename from its backup folder: deletes the new files and copies back the originals"""
    backup = long_path(backup)
    with open(os.path.join(backup, 'manifest.json'), encoding='utf-8') as f:
        m = json.load(f)
    base = long_path(m['base'])
    for e in m['files']:
        new = os.path.join(base, e['new'])
        if e['new'] != e['old'] and os.path.exists(new):
            os.remove(new)
            d = os.path.dirname(new)
            while d != base and os.path.isdir(d) and not os.listdir(d):
                os.rmdir(d)
                d = os.path.dirname(d)
        old = os.path.join(base, e['old'])
        os.makedirs(os.path.dirname(old), exist_ok=True)
        shutil.copyfile(os.path.join(backup, e['old']), old)
    print(f"restored {len(m['files'])} files ({m['to']} -> {m['from']}) under {m['base']}")


def main():
    if '--undo' in sys.argv:
        i = sys.argv.index('--undo')
        if i + 1 >= len(sys.argv):
            raise SystemExit('usage: rename_persistable_object.py --undo <backup dir>')
        return undo(sys.argv[i + 1])
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument('--from', dest='old', required=True, help='old name without app code (PascalCase), e.g. MediaOutlet')
    ap.add_argument('--to', dest='new', required=True, help='new name without app code (PascalCase), e.g. Media')
    ap.add_argument('--root', action='append', required=True, help='folder to process (repeatable)')
    ap.add_argument('--protect', action='append', default=[], help='name that must not be touched (repeatable)')
    ap.add_argument('--keep', action='append', default=[], help='literal text that must not be touched (repeatable)')
    ap.add_argument('--show', action='append', default=[], help='print every line where this identifier changes (repeatable)')
    ap.add_argument('--import-prefix', default='import ogov.', help="import blocks to re-sort (default 'import ogov.')")
    ap.add_argument('--backup', help='backup folder (required with --apply)')
    ap.add_argument('--apply', action='store_true')
    args = ap.parse_args()

    for name in (args.old, args.new):
        if not re.fullmatch(r'[A-Z][A-Za-z0-9]*', name):
            raise SystemExit(f'{name!r}: give the name in PascalCase without the app code (e.g. MediaOutlet)')
    if args.apply and not args.backup:
        raise SystemExit('--apply needs --backup <dir>')

    roots = [long_path(r) for r in args.root]
    for r in roots:
        if not os.path.isdir(r):
            raise SystemExit(f'not a folder: {short_path(r)}')
    base = os.path.commonpath(roots) if len(roots) > 1 else os.path.dirname(roots[0])

    protect = (set(args.protect) | find_model_classes(roots)) - {args.old}
    subst = make_subst(args.old, args.new, protect, args.keep)
    print('protected names:', ', '.join(sorted(protect)) or '(none)')

    plan = []      # (old path, new path, replacements, new content)
    for root in roots:
        for d, dirs, fs in os.walk(root):
            dirs[:] = [x for x in dirs if x not in SKIP_DIRS]
            for fn in fs:
                if not fn.endswith(EXTS):
                    continue
                p = os.path.join(d, fn)
                s = read(p)
                new_s, n = subst(s)
                rel_new, _ = subst(p[len(root):])
                new_p = root + rel_new
                if n or new_p != p:
                    plan.append((p, new_p, n, new_s))

    rel = lambda p: os.path.relpath(short_path(p), short_path(base))
    moved = [(o, n) for o, n, *_ in plan if o != n]
    targets = [n for _, n in moved]
    collisions = sorted({n for n in targets if os.path.exists(n) or targets.count(n) > 1})
    print(f'{len(plan)} files touched, {len(moved)} renamed/moved, {sum(c for _, _, c, _ in plan)} replacements')
    for o, n, c, _ in plan:
        print(f'  {c:4d}  {rel(o)}' + (f'\n        -> {rel(n)}' if o != n else ''))
    # every distinct identifier that changes: the place to spot a third-party type (e.g. MediaType) to --protect
    idents = {}
    for o, _, _, new_s in plan:
        for a, b in changed_identifiers(read(o), new_s).items():
            idents.setdefault((a, b), set()).add(o)
    print('identifier changes (files):')
    for (a, b), files in sorted(idents.items()):
        print(f'  {len(files):4d}  {a} -> {b}')
    # --show <identifier>: every line where that identifier changes (to tell code from prose before applying);
    # package, import and persistence.xml <class> lines are always code, so they are not listed
    for ident in args.show:
        print(f'lines where {ident!r} changes (package / import / <class> lines omitted):')
        for o, _, _, new_s in plan:
            for k, (x, y) in enumerate(zip(read(o).splitlines(), new_s.splitlines()), 1):
                if x.lstrip().startswith(('package ', 'import ', '<class>')):
                    continue
                if x != y and ident in re.findall(r'\w+', x) and ident not in re.findall(r'\w+', y):
                    print(f'    {rel(o)}:{k}: {x.strip()[:140]}')
    if collisions:
        print('COLLISIONS (target already exists):')
        for c in collisions:
            print('   ', rel(c))
        raise SystemExit('aborted: nothing was changed')
    if not args.apply:
        print('(dry run: nothing was changed; rerun with --apply --backup <dir>)')
        return

    backup = long_path(args.backup)
    manifest = []
    for o, n, _, _ in plan:
        b = os.path.join(backup, rel(o))
        os.makedirs(os.path.dirname(b), exist_ok=True)
        shutil.copyfile(o, b)
    for o, n, _, new_s in plan:
        before = read(o)
        lines = changed_line_numbers(before, new_s)
        if n.endswith('.java'):
            new_s = sort_import_blocks(new_s, args.import_prefix, lines)
            lines = changed_line_numbers(before, new_s)
        os.makedirs(os.path.dirname(n), exist_ok=True)
        write(n, new_s)
        manifest.append({'old': rel(o), 'new': rel(n), 'changed_lines': lines})
    for o, _ in moved:
        os.remove(o)
    for o, _ in moved:
        d = os.path.dirname(o)
        while d not in roots and os.path.isdir(d) and not os.listdir(d):
            os.rmdir(d)
            print('removed empty folder', rel(d))
            d = os.path.dirname(d)
    with open(os.path.join(backup, 'manifest.json'), 'w', encoding='utf-8') as f:
        json.dump({'base': short_path(base), 'from': args.old, 'to': args.new, 'files': manifest}, f, indent=1)

    # what is left: any spelling of the old name the rules did not cover (prose, UPPER_SNAKE, plurals...)
    words = re.sub(r'(?<!^)(?=[A-Z])', ' ', args.old).lower()
    leftover_rx = re.compile('|'.join(re.escape(v) for v in {args.old.lower(), words, words.replace(' ', '_'),
                                                                 words.replace(' ', '-')}), re.IGNORECASE)
    print('applied; backup and manifest at', short_path(backup))
    print('leftovers to review (not changed):')
    found = False
    for root in roots:
        for d, dirs, fs in os.walk(root):
            dirs[:] = [x for x in dirs if x not in SKIP_DIRS]
            for fn in fs:
                if not fn.endswith(EXTS):
                    continue
                p = os.path.join(d, fn)
                for k, line in enumerate(read(p).replace('\r\n', '\n').split('\n'), 1):
                    if leftover_rx.search(line):
                        found = True
                        print(f'    {rel(p)}:{k}: {line.strip()[:140]}')
    if not found:
        print('    (none)')


if __name__ == '__main__':
    sys.exit(main())
