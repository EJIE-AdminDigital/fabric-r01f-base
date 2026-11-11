#!/usr/bin/env python3
"""
Regression check of the generators: regenerates, in a temp folder, a component that already exists
(by default communic / media-register with OG10MediaGroup and OG10Media) and compares every generated
file with the real one. The workspace is never modified: the test project, the config and OG00AppCodes
are copied to the temp folder first.

    python validate_roundtrip.py [--system communic --component media-register --objects MediaGroup,Media]
                                 [--keep]

The only accepted difference is the value of serialVersionUID (normalized before comparing).
Ends with "RESULT: IDENTICAL" (exit 0) or the list of different / missing / extra files (exit 1).
"""
from __future__ import annotations

import argparse
import difflib
import os
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from r01gen import app_codes_file, exists, find_workspace, lp, read_text, walk_files  # noqa: E402

HERE = Path(__file__).resolve().parent
IGNORED_PARTS = {"target", "bin", ".settings", ".git", ".idea", ".vscode"}
IGNORED_NAMES = {".project", ".classpath", ".flattened-pom.xml", ".gitlab-ci.yml"}


def norm(t: str) -> str:
    return re.sub(r"serialVersionUID = -?\d+L", "serialVersionUID = <n>L", t)


def project_files(root: Path) -> set[str]:
    res = set()
    if not exists(root):
        return res
    for f in walk_files(root):
        rel = f.relative_to(root)
        if any(p in IGNORED_PARTS for p in rel.parts) or rel.name in IGNORED_NAMES:
            continue
        res.add(rel.as_posix())
    return res


def compare(expected: Path, generated: Path, rels, label: str, problems: list) -> int:
    same = 0
    for rel in sorted(rels):
        e, g = expected / rel, generated / rel
        if not exists(g):
            problems.append(f"MISSING   {label}/{rel}")
            continue
        if not exists(e):
            problems.append(f"EXTRA     {label}/{rel}")
            continue
        a, b = open(lp(e), "rb").read(), open(lp(g), "rb").read()
        if a == b or norm(a.decode("utf-8", "replace")) == norm(b.decode("utf-8", "replace")):
            same += 1
            continue
        problems.append(f"DIFFERENT {label}/{rel}")
        for line in list(difflib.unified_diff(a.decode("utf-8", "replace").splitlines(), b.decode("utf-8", "replace").splitlines(),
                                              "expected", "generated", n=0, lineterm=""))[:16]:
            problems.append("    " + line)
    return same


def copy(src: Path, dst: Path) -> None:
    os.makedirs(lp(dst.parent), exist_ok=True)
    shutil.copyfile(lp(src), lp(dst))


def copy_tree(src: Path, dst: Path) -> None:
    for f in walk_files(src):
        copy(f, dst / f.relative_to(src))


def run(cmd: list[str]) -> None:
    r = subprocess.run([sys.executable, "-I", *cmd], capture_output=True, text=True)
    print(r.stdout.rstrip())
    if r.returncode:
        print(r.stderr)
        sys.exit(f"ERROR: {' '.join(cmd[:1])} failed")


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--system", default="communic")
    ap.add_argument("--component", default="media-register")
    ap.add_argument("--objects", default="MediaGroup,Media", help="in the order they were added")
    ap.add_argument("--workspace", type=Path)
    ap.add_argument("--keep", action="store_true", help="keep the temp folder")
    ap.add_argument("--verbose", action="store_true", help="print the generators' output")
    args = ap.parse_args()
    ws = args.workspace or find_workspace()
    pocs = ws / "presencia-web/ogov-pocs"
    real_sys = pocs / f"ogov-{args.system}"
    real_tests = pocs / "ogov-test/e80a180-ogov-test"
    real_cfg = pocs / "ogov-config/e80a180-ogov-config/ogovCOREConfig/src/main/config"
    real_byenv = pocs / "ogov-config/e80a180-ogov-config-byenv/ogovCOREConfigByEnv/src/main/config/default"
    real_codes = app_codes_file(pocs)
    projects = sorted(d.name for d in real_sys.iterdir() if d.name.endswith((f"-{args.component}-api", f"-{args.component}-core")))
    if len(projects) != 2:
        sys.exit(f"ERROR: {real_sys} must hold the -api and -core projects of {args.component}")
    bai = projects[0].split("-ogov-")[0]

    tmp = Path(tempfile.mkdtemp(prefix="r01-roundtrip-"))
    try:
        out_root, tests, cfg, byenv = tmp / "sys", tmp / "test", tmp / "config", tmp / "byenv"
        codes = tmp / "OG00AppCodes.java"
        copy(real_codes, codes)
        for rel in ("pom.xml", "ogovTestCommonClasses/pom.xml", "ogovTestGuiceClasses/pom.xml", "ogovTestSpringClasses/pom.xml",
                    "ogovTestSpringClasses/src/test/resources/application.yml",
                    "ogovTestSpringClasses/src/test/resources/config/default/samplesys-samplecomp-database.yml"):
            copy(real_tests / rel, tests / rel)
        for rel in ("ogovTestGuiceClasses/src/test/java/ogov/samplesys/samplecomp", "ogovTestSpringClasses/src/test/java/ogov/samplesys/samplecomp"):
            copy_tree(real_tests / rel, tests / rel)
        copy_tree(real_cfg / "og91", cfg / "og91")
        copy_tree(real_byenv / "og91", byenv / "og91")
        common = ["--system", args.system, "--component", args.component, "--domain-bai", bai, "--workspace", str(ws),
                  "--out-root", str(out_root), "--tests-root", str(tests), "--config-root", str(cfg),
                  "--config-byenv-root", str(byenv), "--app-codes-file", str(codes)]
        log = []
        def gen(script, extra):
            r = subprocess.run([sys.executable, "-I", str(HERE / script), *common, *extra], capture_output=True, text=True)
            log.append(r.stdout)
            if r.returncode or "ERROR" in r.stdout:
                print(r.stdout, r.stderr)
                sys.exit(f"ERROR: {script} failed")
        readme = real_sys / projects[0] / "README.md"
        m = re.search(r"component \(\*(.+?)\*\)", read_text(readme)) if exists(readme) else None
        gen("scaffold_component.py", ["--description", m.group(1)] if m else [])
        # the hand-written model classes are an input, not generated
        api_proj = next(p for p in projects if p.endswith("-api"))
        model_rel = next(f.relative_to(real_sys / api_proj).as_posix() for f in walk_files(real_sys / api_proj)
                         if f.name == f"OG10{args.objects.split(',')[0]}.java" or re.search(rf"/api/model/\w+{args.objects.split(',')[0]}\.java$", f.as_posix()))
        model_dir = Path(model_rel).parent
        for o in args.objects.split(","):
            m = next(f for f in walk_files(real_sys / api_proj / model_dir) if re.fullmatch(rf"OG\d\d{o}\.java", f.name))
            copy(m, out_root / api_proj / model_dir / m.name)
        for o in args.objects.split(","):
            gen("add_persistable_object.py", ["--object", o])
        if args.verbose:
            print("\n".join(log))

        problems: list[str] = []
        total = 0
        for p in projects:
            rels = project_files(real_sys / p) | project_files(out_root / p)
            total += compare(real_sys / p, out_root / p, rels, p, problems)
        comp_pkg = args.component.replace("-", "")
        test_rels = {f.relative_to(real_tests).as_posix() for f in walk_files(real_tests)
                     if f"/ogov/{args.system}/{comp_pkg}/" in f.as_posix()
                     and not IGNORED_PARTS & set(f.relative_to(real_tests).parts)}
        test_rels |= {f.relative_to(tests).as_posix() for f in walk_files(tests) if f"/ogov/{args.system}/{comp_pkg}/" in f.as_posix()}
        test_rels |= {"pom.xml", "ogovTestCommonClasses/pom.xml", "ogovTestGuiceClasses/pom.xml", "ogovTestSpringClasses/pom.xml",
                      "ogovTestSpringClasses/src/test/resources/application.yml",
                      f"ogovTestSpringClasses/src/test/resources/config/default/{args.system}-{comp_pkg}-database.yml"}
        total += compare(real_tests, tests, test_rels, "ogov-test", problems)
        core_l = next(f.name for f in (real_cfg).iterdir() if (real_cfg / f.name / "components" / f"{f.name}.{comp_pkg}.dbpersistence.xml").is_file())
        cfg_rels = {f.relative_to(cfg).as_posix() for f in walk_files(cfg) if not f.relative_to(cfg).as_posix().startswith("og91/")}
        total += compare(real_cfg, cfg, cfg_rels, "ogovCOREConfig", problems)
        byenv_rels = {f.relative_to(byenv).as_posix() for f in walk_files(byenv) if not f.relative_to(byenv).as_posix().startswith("og91/")}
        total += compare(real_byenv, byenv, byenv_rels, "ogovCOREConfigByEnv", problems)
        if read_text(codes) != read_text(real_codes):
            problems.append("DIFFERENT OG00AppCodes.java (should be already registered)")
        else:
            total += 1
        print(f"{total} files identical (core config {core_l})")
        for p in problems:
            print(p)
        print("RESULT: IDENTICAL" if not problems else f"RESULT: {sum(1 for p in problems if not p.startswith('    '))} DIFFERENCES")
        return 0 if not problems else 1
    finally:
        if args.keep:
            print(f"temp folder kept: {tmp}")
        else:
            shutil.rmtree(lp(tmp), ignore_errors=True)


if __name__ == "__main__":
    sys.exit(main())
