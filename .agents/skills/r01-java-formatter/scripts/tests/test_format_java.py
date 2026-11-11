"""Tests for format_java.py (and its agreement with verify_whitespace_only.py).

    python scripts/tests/test_format_java.py            from the skill folder (or any folder)
    python -m unittest discover -s scripts/tests        same, through unittest discovery

Optional: R01_FORMAT_CORPUS=<folder> also checks every .java under that folder (e.g. a copy of presencia-web)
for whitespace-only output and idempotence. It never writes there.
Standard library only; run them after any change to format_java.py or to the examples in SKILL.md.
"""
import contextlib
import importlib.util
import io
import os
import random
import re
import shutil
import subprocess
import sys
import tempfile
import unittest

HERE = os.path.dirname(os.path.abspath(__file__))
SCRIPTS = os.path.dirname(HERE)
SKILL_MD = os.path.join(os.path.dirname(SCRIPTS), 'SKILL.md')
GOLDEN = os.path.join(HERE, 'fixtures', 'golden.java')


def _load(name):
    spec = importlib.util.spec_from_file_location(name, os.path.join(SCRIPTS, name + '.py'))
    mod = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(mod)
    return mod


fmt = _load('format_java')
verify = _load('verify_whitespace_only')
LITERAL = re.compile(r'"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'')
BLANKS = re.compile(r'[ \t\r]')


def whitespace_only(before, after):
    """None if 'after' differs from 'before' only in spaces and tabs (same lines, endings and literals)"""
    a, b = before.split('\n'), after.split('\n')
    if len(a) != len(b):
        return 'line count changed'
    for k, (x, y) in enumerate(zip(a, b), 1):
        if x.endswith('\r') != y.endswith('\r'):
            return f'line {k}: line ending changed'
        if BLANKS.sub('', x) != BLANKS.sub('', y):
            return f'line {k}: content changed'
        if LITERAL.findall(x) != LITERAL.findall(y):
            return f'line {k}: literal changed'
    return None


def golden():
    with open(GOLDEN, encoding='utf-8', newline='') as f:
        return f.read()


def skill_examples():
    with open(SKILL_MD, encoding='utf-8') as f:
        return re.findall(r'```java\n(.*?)```', f.read(), re.S)


class SpecExamples(unittest.TestCase):
    """The examples of SKILL.md are the spec: they are already in house style"""

    def test_examples_are_fixed_points(self):
        blocks = skill_examples()
        self.assertGreater(len(blocks), 10)
        for i, block in enumerate(blocks):
            new, changed = fmt.format_text(block)
            self.assertEqual(changed, [], f'SKILL.md java example #{i} would change at lines {changed}')

    def test_golden_is_a_fixed_point(self):
        new, changed = fmt.format_text(golden())
        self.assertEqual(changed, [])


class Safety(unittest.TestCase):
    """Whatever the input, only spaces and tabs change, and a second pass changes nothing"""

    def _check(self, text):
        new, _ = fmt.format_text(text)
        self.assertIsNone(whitespace_only(text, new))
        again, changed = fmt.format_text(new)
        self.assertEqual(changed, [], 'not idempotent')
        return new

    def test_messy_versions_of_the_golden(self):
        rnd = random.Random(1)
        lines = golden().split('\n')
        for _ in range(20):
            messy = []
            for l in lines:
                s = l.lstrip(' ')
                if s and rnd.random() < 0.6:
                    n = max(0, len(l) - len(s) + rnd.randint(-6, 6))
                    l = ('\t' * (n // 4) + ' ' * (n % 4)) if rnd.random() < 0.5 else ' ' * n
                    l += s
                messy.append(l)
            self._check('\n'.join(messy))

    def test_crlf_is_kept(self):
        text = golden().replace('\n', '\r\n').replace('    public MyType(', '\tpublic MyType(')
        new = self._check(text)
        self.assertEqual(new.count('\r\n'), text.count('\r\n'))

    def test_literals_comments_and_text_blocks_are_untouched(self):
        text = ('class A {\n'
                '    /*\n'
                '\t   tabbed comment   (x,  y)\n'
                '     */\n'
                '    String s = """\n'
                '\t    text, block (a,  b)\n'
                '\t    """;\n'
                '    String t = "x,  y\t(z)";\n'
                '    char c = \'\\t\';\n'
                '}\n')
        new = self._check(text)
        for line in ('\t   tabbed comment   (x,  y)', '\t    text, block (a,  b)', '\t    """;',
                     '    String t = "x,  y\t(z)";'):
            self.assertIn(line, new.split('\n'))

    def test_agrees_with_the_verifier(self):
        """the bundled verifier accepts what the formatter writes (through the command line, in place)"""
        with tempfile.TemporaryDirectory() as tmp:
            src = os.path.join(tmp, 'orig.java')
            out = os.path.join(tmp, 'formatted.java')
            messy = golden().replace('\n          final', '\n\t\t  final').replace('\n', '\r\n')
            with open(src, 'w', encoding='utf-8', newline='') as f:
                f.write(messy)
            shutil.copyfile(src, out)
            r = subprocess.run([sys.executable, os.path.join(SCRIPTS, 'format_java.py'), out], capture_output=True, text=True)
            self.assertEqual(r.returncode, 0, r.stdout + r.stderr)
            with contextlib.redirect_stdout(io.StringIO()):
                self.assertEqual(verify.main(src, out), 0)


class Restoration(unittest.TestCase):
    """Continuation lines of a formatted file that are shifted at random are put back where they were"""

    def test_shifted_lines_are_restored(self):
        rnd = random.Random(7)
        base = golden()
        lines = base.split('\n')
        picks = []
        for k, l in enumerate(lines[1:], 1):
            s = l.lstrip(' ')
            prev = lines[k - 1].rstrip()
            if not s or s.startswith(('/*', '*', '/**')):
                continue
            # a line that opens a block is not shifted alone: its body moves with it (as after a rename),
            # so shifting only the opening line would make the expected positions ambiguous
            if l.rstrip().endswith('{'):
                continue
            # the elements of '{' with nothing after it (@NamedQueries({ / indexes = {) have no alignment rule
            if s.startswith('@'):
                continue
            if s.startswith(('.', ':', '&&', '||', 'extends', 'implements', '"FROM', '"WHERE', '"AND', '"LEFT')) \
                    or prev.endswith((',', '(', '+')):
                picks.append(k)
        self.assertGreater(len(picks), 25)
        for _ in range(10):
            shifted = list(lines)
            chosen = [k for k in picks if rnd.random() < 0.5]
            for k in chosen:
                s = shifted[k].lstrip(' ')
                ind = len(shifted[k]) - len(s)
                shifted[k] = ' ' * max(0, ind + rnd.choice([-5, -3, -1, 1, 2, 6])) + s
            new, _ = fmt.format_text('\n'.join(shifted))
            got = new.split('\n')
            wrong = [(k + 1, lines[k], got[k]) for k in chosen if got[k] != lines[k]]
            self.assertEqual(wrong, [], f'not restored: {wrong[:3]}')


class Rules(unittest.TestCase):
    """One small input per rule, with the exact expected output"""

    def assertFormats(self, text, expected, only=None):
        new, _ = fmt.format_text(text, only)
        self.assertEqual(new, expected)

    def test_tabs_become_spaces(self):
        self.assertFormats('class A {\n\tint _a;\n\t\tint _b;\n}\n', 'class A {\n    int _a;\n        int _b;\n}\n')

    def test_parameters_under_the_first_one(self):
        self.assertFormats('    void m(final A a,\n  final B b) {\n',
                           '    void m(final A a,\n           final B b) {\n')

    def test_comment_right_after_the_parenthesis(self):
        self.assertFormats('    R(// provider\n  final P p,\n    // services\n final S s) {\n',
                           '    R(// provider\n      final P p,\n      // services\n      final S s) {\n')

    def test_fluent_chain_on_the_root_dot(self):
        self.assertFormats('    X r = a.b()\n  .c()\n          .d();\n',
                           '    X r = a.b()\n           .c()\n           .d();\n')

    def test_chain_after_return(self):
        self.assertFormats('        return this.forSecurityContext(s)\n  .createDelegateAs(F.class)\n  .find(s);\n',
                           '        return this.forSecurityContext(s)\n                   .createDelegateAs(F.class)\n                   .find(s);\n')

    def test_boolean_operator(self):
        self.assertFormats('    if (a\n  && b) {\n', '    if (a\n     && b) {\n')

    def test_nested_ternary(self):
        self.assertFormats('    X r = a ? b()\n  : c ? d()\n  : e();\n',
                           '    X r = a ? b()\n            : c ? d()\n                : e();\n')

    def test_extends_and_implements(self):
        self.assertFormats('public class A\nextends B\nimplements C,\nD {\n}\n',
                           'public class A\n     extends B\n  implements C,\n             D {\n}\n')

    def test_interface_extends(self):
        self.assertFormats('public interface A\nextends B,\nC {\n}\n',
                           'public interface A\n         extends B,\n                 C {\n}\n')

    def test_jpql(self):
        self.assertFormats('    @NamedQuery(name = "q",\n'
                           '                query = "SELECT o " +\n'
                           '"FROM E o " +\n'
                           '"WHERE o._a = :a " +\n'
                           '"AND o._b = :b")\n',
                           '    @NamedQuery(name = "q",\n'
                           '                query = "SELECT o " +\n'
                           '                          "FROM E o " +\n'
                           '                         "WHERE o._a = :a " +\n'
                           '                           "AND o._b = :b")\n')

    def test_separator_block_at_column_0(self):
        sep = '/' * 89
        self.assertFormats(f'class A {{\n    {sep}\n    //  FIELDS\n    {sep}\t\n}}\n',
                           f'class A {{\n{sep}\n//  FIELDS\n{sep}\n}}\n')

    def test_lambda_body(self):
        self.assertFormats('        call(a -> {\n  doIt();\n      });\n',
                           '        call(a -> {\n                 doIt();\n             });\n')

    def test_anonymous_class_body(self):
        self.assertFormats('        F f = new F() {\n  @Override\n  public void m() {\n  }\n  };\n',
                           '        F f = new F() {\n                  @Override\n                  public void m() {\n                  }\n              };\n')

    def test_array_initializer(self):
        self.assertFormats('@Scan(classes = {A.class,\n  B.class})\n', '@Scan(classes = {A.class,\n                 B.class})\n')

    def test_spacing_within_a_line(self):
        self.assertFormats('        if(a&&b){\n        } catch(E e){\n        x = m(a, b,  c);\n        Map<String, Integer> m;\n',
                           '        if (a && b) {\n        } catch (E e) {\n        x = m(a,b,c);\n        Map<String,Integer> m;\n')

    def test_blank_line_with_tabs_becomes_empty(self):
        self.assertFormats('class A {\n\t\t\n}\n', 'class A {\n\n}\n')

    def test_class_body_does_not_move_with_its_header(self):
        self.assertFormats('public class A\n    extends B {\n    int _a;\n}\n', 'public class A\n     extends B {\n    int _a;\n}\n')

    def test_no_rule_no_change(self):
        text = '    void m()\n            throws E {\n        String s = "a" +\n                "b";\n    }\n'
        self.assertFormats(text, text)

    def test_lines_after_a_rename(self):
        """only the renamed lines are given; the continuation lines under them follow"""
        renamed = ('class A {\n'
                   '    void m() {\n'
                   '        _forMediaCRUD = new OG10ClientAPIDelegateForMediaCRUDServices(securityContextProvider,\n'
                   '                                                                                modelObjectsMarshaller,\n'
                   '                                                                                services);\n'
                   '        Collection<X> found = _api.getForMediaFind()\n'
                   '                                        .findByName("a");\n'
                   '        int  untouched;\n'
                   '    }\n'
                   '}\n')
        expected = renamed.replace('                                                                                m',
                                   '                                                                      m') \
                          .replace('                                                                                s',
                                   '                                                                      s') \
                          .replace('                                        .findByName', '                                  .findByName')
        self.assertFormats(renamed, expected, only={3, 6})


class CommandLine(unittest.TestCase):
    def _run(self, *args):
        return subprocess.run([sys.executable, os.path.join(SCRIPTS, 'format_java.py')] + list(args),
                              capture_output=True, text=True)

    def test_check_reports_without_writing(self):
        with tempfile.TemporaryDirectory() as tmp:
            p = os.path.join(tmp, 'A.java')
            with open(p, 'w', encoding='utf-8', newline='') as f:
                f.write('class A {\n\tint _a;\n}\n')
            r = self._run('--check', p)
            self.assertEqual(r.returncode, 1)
            self.assertIn('would change: 2', r.stdout)
            with open(p, encoding='utf-8', newline='') as f:
                self.assertIn('\t', f.read())
            self.assertEqual(self._run(p).returncode, 0)
            self.assertEqual(self._run('--check', p).returncode, 0)

    def test_lines_option(self):
        with tempfile.TemporaryDirectory() as tmp:
            p = os.path.join(tmp, 'A.java')
            with open(p, 'w', encoding='utf-8', newline='') as f:
                f.write('class A {\n\tint _a;\n\tint _b;\n\tint _c;\n}\n')
            r = self._run(p, '--lines', '2,4')
            self.assertIn('2 lines changed: 2,4', r.stdout)
            with open(p, encoding='utf-8', newline='') as f:
                self.assertEqual(f.read(), 'class A {\n    int _a;\n\tint _b;\n    int _c;\n}\n')


@unittest.skipUnless(os.environ.get('R01_FORMAT_CORPUS'), 'set R01_FORMAT_CORPUS=<folder> to check real code')
class Corpus(unittest.TestCase):
    def test_real_code(self):
        root = os.path.abspath(os.environ['R01_FORMAT_CORPUS'])
        if os.name == 'nt' and not root.startswith('\\\\?\\'):
            root = '\\\\?\\' + root
        n = 0
        for d, dirs, fs in os.walk(root):
            dirs[:] = [x for x in dirs if x not in ('target', 'bin', 'build', '.git')]
            for f in fs:
                if not f.endswith('.java'):
                    continue
                p = os.path.join(d, f)
                with open(p, 'rb') as fh:
                    text = fh.read().decode('utf-8', errors='surrogateescape')
                new, _ = fmt.format_text(text)
                self.assertIsNone(whitespace_only(text, new), p)
                self.assertEqual(fmt.format_text(new)[1], [], f'not idempotent: {p}')
                n += 1
        self.assertGreater(n, 0)
        print(f'\n    corpus: {n} java files checked')


if __name__ == '__main__':
    unittest.main(verbosity=2)
