"""Check that a formatted Java file differs from its original only in spaces and tabs.

Usage:
    python verify_whitespace_only.py ORIGINAL FORMATTED

Exit code 0 when the change is whitespace-only, 1 otherwise (offending lines are listed).

Checks, line by line:
  - the number of lines and the line endings (LF / CRLF) are unchanged
  - removing every space and tab, both lines are identical (no code, names or comments changed)
  - the contents of string / char literals are identical (spacing inside literals is data)
"""
import os
import re
import sys

LITERAL_RE = re.compile(r'"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'')
WHITESPACE_RE = re.compile(r'[ \t\r]')


def long_path(path):
    """Windows paths longer than 260 characters need the \\\\?\\ prefix (and an absolute path)"""
    path = os.path.abspath(path)
    if os.name == 'nt' and not path.startswith('\\\\?\\'):
        return '\\\\?\\' + path
    return path


def read_lines(path):
    with open(long_path(path), 'rb') as f:
        # split on \n only, so a trailing \r (CRLF) stays attached and can be compared
        return f.read().decode('utf-8', errors='surrogateescape').split('\n')


def main(original_path, formatted_path):
    original = read_lines(original_path)
    formatted = read_lines(formatted_path)

    if len(original) != len(formatted):
        print(f"FAIL: line count changed ({len(original)} -> {len(formatted)}); "
              "line breaks must never be added or removed")
        return 1

    problems = []
    for number, (before, after) in enumerate(zip(original, formatted), start=1):
        if before.endswith('\r') != after.endswith('\r'):
            problems.append(f"line {number}: line ending changed")
        if WHITESPACE_RE.sub('', before) != WHITESPACE_RE.sub('', after):
            problems.append(f"line {number}: non-whitespace content changed\n"
                            f"    - {before.strip()}\n"
                            f"    + {after.strip()}")
        elif LITERAL_RE.findall(before) != LITERAL_RE.findall(after):
            problems.append(f"line {number}: whitespace inside a string/char literal changed\n"
                            f"    - {before.strip()}\n"
                            f"    + {after.strip()}")

    if problems:
        print("FAIL:")
        print("\n".join(problems))
        return 1
    print("OK: only spaces/tabs changed")
    return 0


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print(__doc__)
        sys.exit(2)
    sys.exit(main(sys.argv[1], sys.argv[2]))
