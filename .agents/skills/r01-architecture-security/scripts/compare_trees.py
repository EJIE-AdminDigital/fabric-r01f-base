#!/usr/bin/env python3
"""
Compares two security projects byte by byte (only the files the generator copies:
pom.xml, README.md and src/**; IDE, build and git files are ignored).

Usage:
    python compare_trees.py <expected project dir> <generated project dir>

Exit code 0 when both trees hold the same files with identical bytes.
"""
from __future__ import annotations

import difflib
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from generate_security import list_files, read_bytes  # noqa: E402


def main() -> int:
    if len(sys.argv) != 3:
        print(__doc__)
        return 2
    exp, gen = Path(sys.argv[1]), Path(sys.argv[2])
    ef, gf = set(list_files(exp)), set(list_files(gen))
    only_e, only_g = sorted(ef - gf), sorted(gf - ef)
    diff = []
    for rel in sorted(ef & gf):
        a, b = read_bytes(exp / rel), read_bytes(gen / rel)
        if a != b:
            diff.append(rel)
    for rel in only_e:
        print(f"MISSING in generated: {rel.as_posix()}")
    for rel in only_g:
        print(f"EXTRA in generated:   {rel.as_posix()}")
    for rel in diff:
        print(f"DIFFERENT:            {rel.as_posix()}")
        a = read_bytes(exp / rel).decode("utf-8", "replace").splitlines(keepends=True)
        b = read_bytes(gen / rel).decode("utf-8", "replace").splitlines(keepends=True)
        for line in list(difflib.unified_diff(a, b, "expected", "generated", n=0))[:20]:
            print("    " + line.rstrip("\n"))
    same = len(ef & gf) - len(diff)
    print(f"\n{exp.name}: {same} identical, {len(diff)} different, {len(only_e)} missing, {len(only_g)} extra")
    return 0 if not (diff or only_e or only_g) else 1


if __name__ == "__main__":
    sys.exit(main())
