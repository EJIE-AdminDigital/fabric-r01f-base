#!/usr/bin/env python3
"""
Removes an OGOV system from presencia-web/ogov-pocs: its folder (every component, security included),
its CORE / API config in ogov-config and ogov-config-byenv, and its tests and registrations in ogov-test.

    python remove_system.py --system communic                                   # dry run: lists everything
    python remove_system.py --system communic --apply --backup <dir> [--app-codes]
    python remove_system.py --undo <dir>

What it removes:
- ogov-pocs/ogov-{system}/                                    (all the system's projects)
- ogov-config/.../ogovCOREConfig/src/main/config/{core}/ and {api}/
- ogov-config-byenv/.../ogovCOREConfigByEnv/src/main/config/<env>/{core}/ and {api}/   (every env)
- ogov-test: the sources under */src/*/java/ogov/{system}/, the Spring db files config/*/{system}-*.yml,
  the ogov.{system}.*.version properties of the parent pom, the dependencies on ogov.{system}.* (with their
  box comment) of the module poms and the {system}-*.yml imports of application.yml
- with --app-codes: the system's section and its model packages in OG00AppCodes (ogov-common)

Nothing changes without --apply. --apply needs --backup: a folder outside the project where every removed
or modified file is copied (relative to ogov-pocs) with a manifest.json; --undo <dir> puts them back.
After the removal (or in the dry run, as a forecast) it lists the references to the system that remain in
ogov-pocs: they are reported, never edited.
"""
from __future__ import annotations

import argparse
import json
import os
import re
import shutil
import sys
from datetime import datetime
from pathlib import Path

PROTECTED = {"samplesys", "common", "test", "config", "pocs"}
SKIP_SCAN = {"target", "bin", ".git", ".settings", ".metadata", "node_modules"}
SCAN_EXT = {".java", ".xml", ".yml", ".yaml", ".properties", ".md", ".json", ".txt", ".sql"}


# =========================================================================================
#  IO
# =========================================================================================
def lp(path) -> str:
    s = os.path.abspath(str(path))
    if os.name == "nt" and not s.startswith("\\\\?\\"):
        s = "\\\\?\\UNC\\" + s[2:] if s.startswith("\\\\") else "\\\\?\\" + s
    return s


def exists(p: Path) -> bool:
    return os.path.exists(lp(p))


def is_dir(p: Path) -> bool:
    return os.path.isdir(lp(p))


def walk(root: Path, skip=()):
    for dirpath, dirs, files in os.walk(lp(root)):
        dirs[:] = sorted(d for d in dirs if d not in skip)
        for f in sorted(files):
            full = os.path.join(dirpath, f)
            yield Path(full[4:] if full.startswith("\\\\?\\") else full)


def read(p: Path) -> str:
    with open(lp(p), "rb") as f:
        return f.read().decode("utf-8")


def write(p: Path, text: str) -> None:
    with open(lp(p), "wb") as f:
        f.write(text.encode("utf-8"))


def copy_file(src: Path, dst: Path) -> None:
    os.makedirs(lp(dst.parent), exist_ok=True)
    shutil.copy2(lp(src), lp(dst))


def remove_tree(p: Path) -> None:
    def onerror(func, path, _exc):          # read-only files (e.g. .git objects)
        os.chmod(path, 0o600)
        func(path)
    shutil.rmtree(lp(p), onerror=onerror)


def find_workspace() -> Path:
    here = Path(__file__).resolve()
    for d in [Path.cwd().resolve(), *Path.cwd().resolve().parents, *here.parents]:
        if (d / "presencia-web" / "ogov-pocs").is_dir():
            return d
    sys.exit("ERROR: workspace not found (a folder containing presencia-web/ogov-pocs); pass --pocs")


# =========================================================================================
#  WHAT BELONGS TO THE SYSTEM
# =========================================================================================
class Plan:
    def __init__(self):
        self.remove_dirs: list[Path] = []
        self.remove_files: list[Path] = []
        self.modify: dict[Path, str] = {}         # path -> new text (CRLF/LF as the file)
        self.notes: list[str] = []


def app_codes_file(pocs: Path) -> Path:
    return pocs / "ogov-common/e80a180-ogov-common-api/ogovCommonAPIClasses/src/main/java/ogov/common/api/OG00AppCodes.java"


def detect_codes(pocs: Path, system: str) -> tuple[str | None, str | None]:
    f = app_codes_file(pocs)
    if exists(f):
        t = read(f)
        api = re.search(rf'\b{system.upper()}_API_APPCODE_STR\s*=\s*"(\w+)"', t)
        core = re.search(rf'\b{system.upper()}_CORE_APPCODE_STR\s*=\s*"(\w+)"', t)
        if api and core:
            return api.group(1).lower(), core.group(1).lower()
    return None, None


def _nl(t: str) -> str:
    return "\r\n" if "\r\n" in t else "\n"


def edit_text(p: Path, fn) -> str | None:
    """Applies fn to the file with \\n line endings; returns the new text with the original endings, or None."""
    raw = read(p)
    eol = _nl(raw)
    new = fn(raw.replace("\r\n", "\n"))
    new = new.replace("\n", eol)
    return None if new == raw else new


def drop_lines(t: str, pred) -> str:
    """Removes the lines matching pred; when a removed run sat between two blank lines, one blank goes too."""
    lines = t.split("\n")
    keep = [not pred(l) for l in lines]
    i = 0
    while i < len(lines):
        if not keep[i]:
            j = i
            while j < len(lines) and not keep[j]:
                j += 1
            before = next((k for k in range(i - 1, -1, -1) if keep[k]), None)
            if before is not None and j < len(lines) and not lines[before].strip() and not lines[j].strip():
                keep[before] = False
            i = j
        else:
            i += 1
    return "\n".join(l for l, k in zip(lines, keep) if k)


def plan_removal(pocs: Path, system: str, api: str | None, core: str | None, with_app_codes: bool,
                 tests_root: Path, cfg_root: Path, byenv_root: Path) -> Plan:
    plan = Plan()
    sys_dir = pocs / f"ogov-{system}"
    if is_dir(sys_dir):
        plan.remove_dirs.append(sys_dir)
        repos = [d for d in sorted(os.listdir(lp(sys_dir))) if is_dir(sys_dir / d / ".git")]
        if repos:
            plan.notes.append(f"WARNING: these projects are git repositories; their local history (unpushed commits, "
                              f"stashes) goes too (it is kept in the backup): {', '.join(repos)}")

    # --- config
    codes = [c for c in (core, api) if c]
    if is_dir(cfg_root):
        for c in codes:
            if is_dir(cfg_root / c):
                plan.remove_dirs.append(cfg_root / c)
    if is_dir(byenv_root):
        for env in sorted(os.listdir(lp(byenv_root))):
            for c in codes:
                if is_dir(byenv_root / env / c):
                    plan.remove_dirs.append(byenv_root / env / c)

    # --- tests
    if is_dir(tests_root):
        for mod in sorted(os.listdir(lp(tests_root))):
            for kind in ("main", "test"):
                d = tests_root / mod / "src" / kind / "java" / "ogov" / system
                if is_dir(d):
                    plan.remove_dirs.append(d)
            res = tests_root / mod / "src/test/resources/config"
            if is_dir(res):
                for f in walk(res):
                    if re.fullmatch(rf"{re.escape(system)}-[\w.-]+\.ya?ml", f.name):
                        plan.remove_files.append(f)
        # registrations
        prop = re.compile(rf"^\s*<ogov\.{re.escape(system)}\.[\w.]+\.version>.*</ogov\.{re.escape(system)}\.[\w.]+\.version>\s*$")
        dep = re.compile(r"\n[ \t]*\n(?:[ \t]*<!-- /+ -->\n[ \t]*<!--[^\n]*-->\n[ \t]*<!-- /+ -->\n)?"
                         rf"[ \t]*<dependency>\s*<groupId>ogov\.{re.escape(system)}\.[^<]*</groupId>(?:(?!</dependency>).)*</dependency>[ \t]*(?=\n)", re.S)
        dep_bare = re.compile(rf"\n[ \t]*<dependency>\s*<groupId>ogov\.{re.escape(system)}\.[^<]*</groupId>(?:(?!</dependency>).)*</dependency>[ \t]*(?=\n)", re.S)
        for pom in [tests_root / "pom.xml", *[tests_root / m / "pom.xml" for m in sorted(os.listdir(lp(tests_root)))]]:
            if not exists(pom):
                continue
            new = edit_text(pom, lambda t: dep_bare.sub("", dep.sub("", drop_lines(t, prop.match))))
            if new is not None:
                plan.modify[pom] = new
        imp = re.compile(rf"^\s*-\s*optional:classpath:config/[^/\s]+/{re.escape(system)}-[\w.-]+\.ya?ml\s*$")
        for f in walk(tests_root, SKIP_SCAN):
            if f.name in ("application.yml", "application.yaml") and "src" in f.parts:
                new = edit_text(f, lambda t: drop_lines(t, imp.match))
                if new is not None:
                    plan.modify[f] = new

    # --- OG00AppCodes
    if with_app_codes:
        f = app_codes_file(pocs)
        if exists(f):
            new = edit_text(f, lambda t: _clean_app_codes(t, system, plan))
            if new is not None:
                plan.modify[f] = new
    return plan


def _clean_app_codes(t: str, system: str, plan: Plan) -> str:
    up = system.upper()
    # model packages
    lines = t.split("\n")
    out = []
    for l in lines:
        if re.search(rf'JavaPackage\.named\("ogov\.{re.escape(system)}\.[\w.]*"\)\s*,\s*$', l) and "List.of(" not in l:
            continue
        if re.search(rf'JavaPackage\.named\("ogov\.{re.escape(system)}\.', l):
            plan.notes.append(f"NOTE: remove by hand from MODEL_JAVA_PACKAGES: {l.strip()}")
        out.append(l)
    t = "\n".join(out)
    # system section: from its '/////  // ...  /////' header to the next section (or the class end)
    m = re.search(rf"^\s*public static final String {up}_STR\s*=", t, re.M)
    if not m:
        return t
    sep = re.compile(r"^/{80,}[ \t]*$", re.M)
    heads = [x for x in sep.finditer(t, 0, m.start())]
    start = heads[-2].start() if len(heads) >= 2 else t.rfind("\n", 0, m.start()) + 1
    nxt = sep.search(t, m.end())
    end = nxt.start() if nxt else t.rindex("}")
    block = t[start:end]
    if re.search(r"public static final \w+ (?!" + up + r"_)\w+\s*=", block):
        plan.notes.append(f"NOTE: the {up} section of OG00AppCodes holds constants of other systems: remove it by hand")
        return t
    return t[:start] + t[end:] if nxt else t[:start].rstrip("\n") + "\n" + t[end:]


# =========================================================================================
#  REMAINING REFERENCES
# =========================================================================================
def remaining_references(pocs: Path, system: str, api: str | None, core: str | None, excluded: list[Path],
                         new_texts: dict[Path, str]) -> list[str]:
    pats = [rf"\bogov[./]{re.escape(system)}[./]", rf"\b{re.escape(system.upper())}_\w+", rf"\b{re.escape(system)}-[\w-]+\.ya?ml\b"]
    if api:
        pats.append(rf"\b{api.upper()}[A-Z]\w+")
    if core:
        pats.append(rf"\b{core.upper()}[A-Z]\w+|\b{core}/{core}\.")
    rx = re.compile("|".join(pats))
    excl = [lp(e) for e in excluded]
    res = []
    for top in sorted(os.listdir(lp(pocs))):
        root = pocs / top
        if not is_dir(root):
            continue
        for f in walk(root, SKIP_SCAN):
            if f.suffix not in SCAN_EXT or any(lp(f).startswith(e + os.sep) or lp(f) == e for e in excl):
                continue
            try:
                t = new_texts[f].replace("\r\n", "\n") if f in new_texts else read(f).replace("\r\n", "\n")
            except (UnicodeDecodeError, OSError):
                continue
            for i, line in enumerate(t.split("\n"), 1):
                if rx.search(line):
                    res.append(f"{f.relative_to(pocs).as_posix()}:{i}: {line.strip()[:140]}")
    return res


# =========================================================================================
#  APPLY / UNDO
# =========================================================================================
def apply(plan: Plan, pocs: Path, backup: Path, system: str) -> None:
    if exists(backup) and os.listdir(lp(backup)):
        sys.exit(f"ERROR: the backup folder {backup} is not empty")
    if lp(backup).startswith(lp(pocs) + os.sep):
        sys.exit("ERROR: the backup folder must be outside ogov-pocs")
    manifest = {"system": system, "date": datetime.now().isoformat(timespec="seconds"), "pocs": str(pocs),
                "removed_dirs": [], "removed_files": [], "modified_files": []}
    # 1. backup everything first
    for d in plan.remove_dirs:
        for f in walk(d):
            copy_file(f, backup / "files" / f.relative_to(pocs))
        manifest["removed_dirs"].append(d.relative_to(pocs).as_posix())
    for f in plan.remove_files:
        copy_file(f, backup / "files" / f.relative_to(pocs))
        manifest["removed_files"].append(f.relative_to(pocs).as_posix())
    for f in plan.modify:
        copy_file(f, backup / "files" / f.relative_to(pocs))
        manifest["modified_files"].append(f.relative_to(pocs).as_posix())
    os.makedirs(lp(backup), exist_ok=True)
    with open(lp(backup / "manifest.json"), "w", encoding="utf-8") as fh:
        json.dump(manifest, fh, indent=1)
    # 2. change
    for f, text in plan.modify.items():
        write(f, text)
    for f in plan.remove_files:
        os.remove(lp(f))
    for d in sorted(plan.remove_dirs, key=lambda p: -len(p.parts)):
        if exists(d):
            remove_tree(d)


def undo(backup: Path) -> None:
    m = json.loads(Path(lp(backup / "manifest.json")).read_text(encoding="utf-8"))
    pocs = Path(m["pocs"])
    for d in m["removed_dirs"]:
        if exists(pocs / d):
            sys.exit(f"ERROR: {pocs / d} exists again: move it away before undoing")
    files = backup / "files"
    for f in walk(files):
        copy_file(f, pocs / f.relative_to(files))
    print(f"restored {sum(1 for _ in walk(files))} files of the system {m['system']} into {pocs}")


# =========================================================================================
#  MAIN
# =========================================================================================
def main() -> None:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--system", help="system id, lowercase (e.g. communic)")
    ap.add_argument("--api-app-code", help="API app code, e.g. OG10 (detected from OG00AppCodes)")
    ap.add_argument("--core-app-code", help="CORE app code, e.g. OG11 (detected from OG00AppCodes)")
    ap.add_argument("--app-codes", action="store_true", help="also remove the system from OG00AppCodes (ogov-common)")
    ap.add_argument("--pocs", type=Path, help="presencia-web/ogov-pocs (detected)")
    ap.add_argument("--tests-root", type=Path)
    ap.add_argument("--config-root", type=Path)
    ap.add_argument("--config-byenv-root", type=Path)
    ap.add_argument("--apply", action="store_true", help="really remove (needs --backup)")
    ap.add_argument("--backup", type=Path, help="empty folder outside the project for the backup and manifest.json")
    ap.add_argument("--undo", type=Path, metavar="BACKUP", help="restore what a previous --apply removed")
    ap.add_argument("--max-refs", type=int, default=60, help="remaining references to print (default 60)")
    args = ap.parse_args()

    if args.undo:
        undo(args.undo)
        return
    if not args.system:
        ap.error("--system is required")
    system = args.system.lower()
    if system in PROTECTED:
        sys.exit(f"ERROR: '{system}' is not a removable system (samplesys is the template of the generators)")
    pocs = (args.pocs or find_workspace() / "presencia-web/ogov-pocs").resolve()
    tests_root = args.tests_root or pocs / "ogov-test/e80a180-ogov-test"
    cfg_root = args.config_root or pocs / "ogov-config/e80a180-ogov-config/ogovCOREConfig/src/main/config"
    byenv_root = args.config_byenv_root or pocs / "ogov-config/e80a180-ogov-config-byenv/ogovCOREConfigByEnv/src/main/config"
    api, core = detect_codes(pocs, system)
    api = (args.api_app_code or api or "").lower() or None
    core = (args.core_app_code or core or "").lower() or None

    print(f"system={system}  api={api or '?'}  core={core or '?'}  pocs={pocs}")
    if not core:
        print("WARNING: the system's app codes are not in OG00AppCodes: its config folders are not removed "
              "(pass --api-app-code / --core-app-code)")
    plan = plan_removal(pocs, system, api, core, args.app_codes, tests_root, cfg_root, byenv_root)
    if not (plan.remove_dirs or plan.remove_files or plan.modify):
        sys.exit(f"ERROR: nothing of the system '{system}' was found in {pocs}")

    def rel(p):
        return p.relative_to(pocs).as_posix() if str(p).startswith(str(pocs)) else str(p)
    n_files = sum(sum(1 for _ in walk(d)) for d in plan.remove_dirs)
    print(f"\n[remove folders] ({n_files} files)")
    for d in plan.remove_dirs:
        print(f"    {rel(d)}/  ({sum(1 for _ in walk(d))} files)")
    if plan.remove_files:
        print("\n[remove files]")
        for f in plan.remove_files:
            print(f"    {rel(f)}")
    if plan.modify:
        print("\n[edit] (registrations of the system)")
        import difflib
        for f, new in plan.modify.items():
            old = read(f).replace("\r\n", "\n").split("\n")
            ops = difflib.SequenceMatcher(None, old, new.replace("\r\n", "\n").split("\n"), autojunk=False).get_opcodes()
            blocks = [(i1, old[i1:i2]) for tag, i1, i2, _j1, _j2 in ops if tag in ("delete", "replace")]
            print(f"    {rel(f)}  (-{sum(len(b) for _, b in blocks)} lines)")
            for i1, b in blocks:
                print(f"        @ line {i1 + 1}:")
                for l in b:
                    print(f"          - {l.rstrip()[:130]}")
    for n in plan.notes:
        print(f"\n{n}")
    if not args.app_codes:
        print(f"\nOG00AppCodes is kept (pass --app-codes to remove the system's section and model packages)")

    refs = remaining_references(pocs, system, api, core, plan.remove_dirs + plan.remove_files, plan.modify)
    print(f"\n[remaining references] {len(refs)} {'(after the removal)' if args.apply else '(forecast: they would stay)'}")
    for r in refs[: args.max_refs]:
        print(f"    {r}")
    if len(refs) > args.max_refs:
        print(f"    ... and {len(refs) - args.max_refs} more (--max-refs)")

    if not args.apply:
        print("\nDRY RUN: nothing was changed. Run again with --apply --backup <empty folder outside the project>.")
        return
    if not args.backup:
        sys.exit("ERROR: --apply needs --backup <empty folder outside the project>")
    apply(plan, pocs, args.backup.resolve(), system)
    print(f"\nDONE: the system {system} was removed. Backup and manifest in {args.backup.resolve()}"
          f"\nUndo: python {Path(__file__).name} --undo {args.backup.resolve()}")


if __name__ == "__main__":
    main()
