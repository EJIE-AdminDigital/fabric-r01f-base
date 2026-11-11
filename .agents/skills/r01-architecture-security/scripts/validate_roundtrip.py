#!/usr/bin/env python3
"""
Regression check of the security template: regenerates the security component of a
reference system that already has one (default: communic) in a temp folder and
compares it byte by byte with the existing one.

  [1] -api / -core   generate_security.py --out-root <tmp>  vs  ogov-{system}/{bai}-ogov-{system}-security-{api,core}
  [2] tests          a copy of ogov-test WITHOUT the reference system's security tests and
                     registrations is regenerated with --only tests  vs  the real ogov-test
                     (so the test files AND the pom / application.yml blocks, at their place, are checked)

Nothing in the workspace is modified.

Usage:
    python validate_roundtrip.py [--system communic] [--api-app-code OG10 --core-app-code OG11]
                                 [--workspace <root>] [--keep]

Exit code 0 when everything is identical.
"""
from __future__ import annotations

import argparse
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

HERE = Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
from generate_security import (TEST_MODULES, find_workspace, list_files, lp, read_bytes,  # noqa: E402
                               single_dir, write_bytes)


def strip_tests(src: Path, dst: Path, system: str, pascal: str) -> None:
    """Copies the test project without the system's security tests and registrations."""
    for rel in list_files(src):
        p = rel.as_posix()
        if f"/{system}/security/test/" in "/" + p or p.endswith(f"/{system}-security-database.yml"):
            continue
        write_bytes(dst / rel, read_bytes(src / rel))
    s = re.escape(system).encode()

    def cut(path: Path, rx: bytes) -> None:
        b = read_bytes(path)
        ms = list(re.finditer(rx, b, re.S))
        if len(ms) != 1:
            sys.exit(f"ERROR: the {system} security registration was not found once in {path} ({len(ms)})")
        write_bytes(path, b[:ms[0].start()] + b[ms[0].end():])
    cut(dst / "pom.xml", rb"[ \t]*<ogov\." + s + rb"\.security\.api\.version>[^\n]*\n[ \t]*<ogov\." + s + rb"\.security\.core\.version>[^\n]*\n")
    for m in TEST_MODULES:
        cut(dst / m / "pom.xml", rb"\r?\n[ \t]*<!-- /+ -->\r?\n[ \t]*<!-- " + re.escape(pascal).encode()
            + rb" / Security *-->\r?\n[ \t]*<!-- /+ -->\r?\n[ \t]*<dependency>.*?</dependency>\r?\n")
    cut(dst / "ogovTestSpringClasses/src/test/resources/application.yml",
        rb"[ \t]*- optional:classpath:config/\$\{env:default\}/" + s + rb"-security-database\.yml\r?\n")


def run(args: list[str]) -> int:
    r = subprocess.run([sys.executable, "-I", *args], capture_output=True, text=True)
    out = (r.stdout + r.stderr).strip().splitlines()
    for line in out:
        if line.startswith(("ERROR", "WARNING", "PENDING", "MISSING", "EXTRA", "DIFFERENT", "    ", "e80a", "OK")) or "identical" in line:
            print("   ", line)
    return r.returncode


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--system", default="communic")
    ap.add_argument("--system-pascal")
    ap.add_argument("--api-app-code")
    ap.add_argument("--core-app-code")
    ap.add_argument("--workspace", type=Path)
    ap.add_argument("--keep", action="store_true", help="keep the temp folder")
    a = ap.parse_args()

    ws = a.workspace.resolve() if a.workspace else find_workspace()
    pocs = ws / "presencia-web" / "ogov-pocs"
    sys_dir = pocs / f"ogov-{a.system}"
    refs = sorted(sys_dir.glob(f"*-ogov-{a.system}-security-api"))
    if len(refs) != 1:
        sys.exit(f"ERROR: no reference security component in {sys_dir}")
    bai = refs[0].name.split("-ogov-")[0]
    pascal = a.system_pascal or a.system[0].upper() + a.system[1:]
    opts = ["--system", a.system, "--workspace", str(ws)]
    for k in ("system_pascal", "api_app_code", "core_app_code"):
        if getattr(a, k):
            opts += [f"--{k.replace('_', '-')}", getattr(a, k)]

    tmp = Path(tempfile.mkdtemp(prefix="r01-security-roundtrip-"))
    ok = True
    try:
        print(f"[1] -api / -core of {a.system}")
        ok &= run([str(HERE / "generate_security.py"), *opts, "--out-root", str(tmp / "gen")]) == 0
        for layer in ("api", "core"):
            name = f"{bai}-ogov-{a.system}-security-{layer}"
            ok &= run([str(HERE / "compare_trees.py"), str(sys_dir / name), str(tmp / "gen" / name)]) == 0

        test_root = single_dir(pocs / "ogov-test", "*-ogov-test", "test project")
        cfg_root = single_dir(pocs / "ogov-config", "*-ogov-config", "config project") / "ogovCOREConfig" / "src" / "main" / "config"
        print(f"\n[2] tests of {a.system} ({test_root.name})")
        copy = tmp / "test"
        strip_tests(test_root, copy, a.system, pascal)
        shutil.copytree(lp(cfg_root), lp(tmp / "config"))
        ok &= run([str(HERE / "generate_security.py"), *opts, "--only", "tests",
                   "--tests-root", str(copy), "--config-root", str(tmp / "config")]) == 0
        ok &= run([str(HERE / "compare_trees.py"), str(test_root), str(copy)]) == 0
    finally:
        if a.keep:
            print(f"\nkept: {tmp}")
        else:
            shutil.rmtree(lp(tmp), ignore_errors=True)
    print("\nRESULT:", "IDENTICAL" if ok else "DIFFERENCES FOUND")
    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main())
