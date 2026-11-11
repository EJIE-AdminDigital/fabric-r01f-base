#!/usr/bin/env python3
"""
Generates the security component (-api and -core projects) of an OGOV system
from the security template of the samplesys system:

    presencia-web/ogov-pocs/ogov-samplesys/bai-ogov-samplesys-security-api
    presencia-web/ogov-pocs/ogov-samplesys/bai-ogov-samplesys-security-core

Every file is copied byte by byte, replacing only the template identifiers
(system id in its three casings, API / CORE app codes, domain BAI and the list
of the system's components in the README), so line endings, encodings and
formatting stay exactly as in the template.

Only pom.xml, README.md and everything under src/ are copied: no IDE files
(.project, .classpath, .settings), no build output (target, bin), no git files.

Usage:
    python generate_security.py --system communic [--domain-bai e80a177]
                                [--system-pascal Communic]
                                [--api-app-code OG10] [--core-app-code OG11]
                                [--components media-register]
                                [--out-root DIR] [--only api|core]
                                [--dry-run] [--force]

Values that are not given are detected from the workspace (see --help).
"""
from __future__ import annotations

import argparse
import os
import re
import shutil
import subprocess
import sys
from pathlib import Path

# --- template identifiers --------------------------------------------------------------
TEMPLATE_DOMAIN_ID = "ogov"
TEMPLATE_SYSTEM = "samplesys"
TEMPLATE_SYSTEM_PASCAL = "SampleSys"
TEMPLATE_BAI = "bai"
TEMPLATE_API_CODE = "OG90"
TEMPLATE_CORE_CODE = "OG91"
TEMPLATE_COMPONENTS = "samplecomp"
COMPONENT = "security"
COMPONENTS_PHRASE = "({} and the ones to come)"
COMPONENTS_JAVADOC = "({}...)"

# --- what gets copied ------------------------------------------------------------------
ROOT_FILES = {"pom.xml", "README.md"}
SKIP_DIRS = {"target", "bin", ".settings", ".git", "vscode-target", ".idea", ".vscode"}


def is_copied(rel: Path) -> bool:
    """pom.xml / README.md at any module level, and everything under src/."""
    parts = rel.parts
    if any(p in SKIP_DIRS for p in parts):
        return False
    if "src" in parts:
        return True
    return rel.name in ROOT_FILES


def lp(path: Path) -> str:
    """Long-path safe string: on Windows the deep OGOV source paths often exceed MAX_PATH (260)."""
    s = str(Path(path).resolve())
    if os.name == "nt" and not s.startswith("\\\\?\\"):
        s = "\\\\?\\UNC\\" + s[2:] if s.startswith("\\\\") else "\\\\?\\" + s
    return s


def read_bytes(path: Path) -> bytes:
    with open(lp(path), "rb") as f:
        return f.read()


def write_bytes(path: Path, data: bytes) -> None:
    os.makedirs(lp(path.parent), exist_ok=True)
    with open(lp(path), "wb") as f:
        f.write(data)


def list_files(project_dir: Path) -> list[Path]:
    """Walks the project pruning build / IDE folders (target paths can exceed MAX_PATH on Windows)."""
    out = []
    base = lp(project_dir)
    for root, dirs, files in os.walk(base):
        root = os.path.relpath(root, base)
        root = project_dir / ("" if root == "." else root)
        dirs[:] = [d for d in dirs if d not in SKIP_DIRS]
        for f in files:
            rel = (Path(root) / f).relative_to(project_dir)
            if is_copied(rel):
                out.append(rel)
    return sorted(out)


# --- workspace discovery ---------------------------------------------------------------
def find_workspace() -> Path:
    """The folder that holds presencia-web/ogov-pocs, searched up from the current dir and from this script."""
    for start in (Path.cwd().resolve(), Path(__file__).resolve().parent):
        for d in [start, *start.parents]:
            if (d / "presencia-web" / "ogov-pocs").is_dir():
                return d
    sys.exit("ERROR: workspace root (folder containing presencia-web/ogov-pocs) not found; use --workspace")


def find_app_codes_file(pocs: Path) -> Path | None:
    hits = [p for p in (pocs / "ogov-common").rglob("OG00AppCodes.java") if "target" not in p.parts]
    return hits[0] if hits else None


def detect_app_codes(pocs: Path, system: str) -> tuple[str | None, str | None]:
    f = find_app_codes_file(pocs)
    if not f:
        return None, None
    txt = f.read_text(encoding="utf-8", errors="replace")
    key = system.upper().replace("-", "_")

    def code(kind: str) -> str | None:
        m = re.search(rf'\b{key}_{kind}_APPCODE_STR\s*=\s*"([a-zA-Z0-9]+)"', txt)
        return m.group(1).upper() if m else None
    return code("API"), code("CORE")


def system_projects(sys_dir: Path, system: str) -> list[Path]:
    rx = re.compile(rf"^(.+)-{TEMPLATE_DOMAIN_ID}-{re.escape(system)}-(.+)-(api|core|rest)$")
    return [d for d in sorted(sys_dir.iterdir()) if d.is_dir() and rx.match(d.name)] if sys_dir.is_dir() else []


def detect_bai(sys_dir: Path, system: str) -> str | None:
    rx = re.compile(rf"^(.+)-{TEMPLATE_DOMAIN_ID}-{re.escape(system)}-")
    bais = {rx.match(d.name).group(1) for d in system_projects(sys_dir, system)}
    return bais.pop() if len(bais) == 1 else None


def detect_components(sys_dir: Path, system: str) -> list[str]:
    rx = re.compile(rf"^.+-{TEMPLATE_DOMAIN_ID}-{re.escape(system)}-(.+)-(api|core|rest)$")
    comps = {rx.match(d.name).group(1) for d in system_projects(sys_dir, system)}
    comps.discard(COMPONENT)
    return sorted(comps)


def detect_pascal(sys_dir: Path, system: str) -> str | None:
    """Reads the system casing from the artifactId of an existing component pom (ogov{System}...)."""
    rx = re.compile(rf"<artifactId>{TEMPLATE_DOMAIN_ID}({re.escape(system)})", re.IGNORECASE)
    for d in system_projects(sys_dir, system):
        pom = d / "pom.xml"
        if pom.is_file():
            m = rx.search(pom.read_text(encoding="utf-8", errors="replace"))
            if m:
                return m.group(1)
    return None


# --- substitution ----------------------------------------------------------------------
def build_replacer(mapping: dict[str, str], bounded: dict[str, str]):
    """Single-pass replacement (no chained substitutions). `bounded` tokens only match
    when not surrounded by letters/digits."""
    alts = [re.escape(k) for k in sorted(mapping, key=len, reverse=True)]
    alts += [rf"(?<![A-Za-z0-9]){re.escape(k)}(?![A-Za-z0-9])" for k in bounded]
    rx = re.compile("|".join(alts).encode())
    table = {k.encode(): v.encode() for k, v in {**mapping, **bounded}.items()}

    def apply_bytes(data: bytes) -> bytes:
        return rx.sub(lambda m: table[m.group(0)], data)

    def apply_str(s: str) -> str:
        return apply_bytes(s.encode()).decode()
    return apply_bytes, apply_str


# --- house style ------------------------------------------------------------------------
FORMATTER = Path(__file__).resolve().parents[2] / "r01-java-formatter" / "scripts" / "format_java.py"


def changed_lines(tpl: bytes, out: bytes) -> list[int]:
    """Lines whose length changed with the substitution (a longer / shorter name moves what is aligned
    under it). package / import lines never align anything."""
    res = []
    for i, (x, y) in enumerate(zip(tpl.split(b"\n"), out.split(b"\n"))):
        if len(x) != len(y) and not y.lstrip().startswith((b"package ", b"import ")):
            res.append(i + 1)
    return res


def format_java(queue: list) -> None:
    """Re-aligns the substituted lines with the r01-java-formatter skill (only spaces / tabs change)."""
    if not queue:
        return
    if not FORMATTER.is_file():
        print(f"WARNING: {FORMATTER} not found: the substituted lines were NOT re-aligned; "
              f"apply the r01-java-formatter skill to them")
        return
    for f, lines in queue:
        r = subprocess.run([sys.executable, "-I", str(FORMATTER), str(f), "--lines", ",".join(map(str, lines))],
                           capture_output=True, text=True)
        if r.returncode:
            print(f"WARNING: formatter failed on {f}: {r.stdout}{r.stderr}")
        elif "no changes" not in r.stdout:
            print(f"    re-aligned: {f.name} ({r.stdout.strip().split(': ')[-1]})")


def write_generated(dst: Path, tpl: bytes, sub_bytes, queue: list) -> None:
    out = sub_bytes(tpl)
    write_bytes(dst, out)
    if dst.suffix == ".java":
        lines = changed_lines(tpl, out)
        if lines:
            queue.append((dst, lines))


# --- tests, config & registrations -------------------------------------------------------
TEST_MODULES = ("ogovTestCommonClasses", "ogovTestGuiceClasses", "ogovTestSpringClasses")


def single_dir(parent: Path, pattern: str, what: str) -> Path:
    hits = sorted(d for d in parent.glob(pattern) if d.is_dir()) if parent.is_dir() else []
    if len(hits) != 1:
        sys.exit(f"ERROR: expected one {what} ({parent / pattern}), found {len(hits)}; pass it explicitly")
    return hits[0]


def template_test_files(test_root: Path) -> list[Path]:
    """The samplesys security test sources (package ogov.samplesys.security.test) and its spring db yml."""
    out = []
    pkg = f"/{TEMPLATE_DOMAIN_ID}/{TEMPLATE_SYSTEM}/{COMPONENT}/test/"
    yml = f"/{TEMPLATE_SYSTEM}-{COMPONENT}-database.yml"
    for m in TEST_MODULES:
        for rel in list_files(test_root / m):
            p = rel.as_posix()
            if pkg in "/" + p or (p.startswith("src/test/resources/config/") and p.endswith(yml)):
                out.append(Path(m) / rel)
    return out


def _pad_comments(tpl: bytes, new: bytes) -> bytes:
    """Keeps the ' -->' of box comments (<!-- SampleSys / Security      -->) where the template has it."""
    out = []
    for a, b in zip(tpl.split(b"\n"), new.split(b"\n")):
        m = re.match(rb"^([ \t]*<!-- )(.*?)( +)(-->\r?)$", b)
        if m and len(a) != len(b):
            b = m.group(1) + m.group(2) + b" " * max(1, len(m.group(3)) + len(a) - len(b)) + m.group(4)
        out.append(b)
    return b"\n".join(out)


def registrations(test_root: Path) -> list:
    """(file, regex of the template block, separator put between the template block and the new one)."""
    S = TEMPLATE_SYSTEM.encode()
    dep = (rb"([ \t]*<!-- /+ -->\r?\n[ \t]*<!-- " + TEMPLATE_SYSTEM_PASCAL.encode() + rb" / Security *-->\r?\n"
           rb"[ \t]*<!-- /+ -->\r?\n[ \t]*<dependency>.*?</dependency>\r?\n)")
    return [(test_root / "pom.xml",
             rb"([ \t]*<ogov\." + S + rb"\.security\.api\.version>[^\n]*\n[ \t]*<ogov\." + S + rb"\.security\.core\.version>[^\n]*\n)", b""),
            *[(test_root / m / "pom.xml", dep, b"NL") for m in TEST_MODULES],
            (test_root / "ogovTestSpringClasses/src/test/resources/application.yml",
             rb"([ \t]*- optional:classpath:config/\$\{env:default\}/" + S + rb"-security-database\.yml\r?\n)", b"")]


def register(path: Path, rx: bytes, sep: bytes, sub_bytes, dry: bool) -> str:
    data = read_bytes(path)
    ms = list(re.finditer(rx, data, re.S))
    if len(ms) != 1:
        return f"ERROR: template block not found once in {path} ({len(ms)} matches): register it by hand"
    tpl = ms[0].group(1)
    new = _pad_comments(tpl, sub_bytes(tpl))
    if new == tpl:
        return f"template itself: {path}"
    if new in data:
        return f"already registered: {path}"
    if sep == b"NL":
        sep = b"\r\n" if b"\r\n" in tpl else b"\n"
    if not dry:
        write_bytes(path, data[:ms[0].end()] + sep + new + data[ms[0].end():])
    return f"registered{' (dry run)' if dry else ''}: {path}"


def main() -> None:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--system", required=True, help="system id, lowercase (e.g. communic)")
    ap.add_argument("--system-pascal", help="system id as in Maven artifacts / classes (e.g. SampleSys). "
                                            "Default: read from an existing component pom, else capitalized")
    ap.add_argument("--domain-bai", help="repo folder prefix (e.g. e80a177). Default: the prefix of the system's existing projects")
    ap.add_argument("--api-app-code", help="e.g. OG10. Default: {SYSTEM}_API_APPCODE_STR in OG00AppCodes")
    ap.add_argument("--core-app-code", help="e.g. OG11. Default: {SYSTEM}_CORE_APPCODE_STR in OG00AppCodes")
    ap.add_argument("--components", help="comma separated components named in the README. Default: the system's existing components")
    ap.add_argument("--workspace", type=Path, help="workspace root (folder containing presencia-web). Default: auto")
    ap.add_argument("--template-dir", type=Path, help="folder holding bai-ogov-samplesys-security-{api,core}. Default: ogov-pocs/ogov-samplesys")
    ap.add_argument("--out-root", type=Path, help="folder where the -api / -core folders are created. Default: ogov-pocs/ogov-{system}")
    ap.add_argument("--only", choices=["api", "core", "tests"], help="generate only one of the parts")
    ap.add_argument("--with-tests", action="store_true",
                    help="also generate the tests in ogov-test (+ their registration in its poms / application.yml) "
                         "and the CORE config component in ogov-config")
    ap.add_argument("--tests-root", type=Path, help="the ogov-test project. Default: ogov-pocs/ogov-test/*-ogov-test")
    ap.add_argument("--config-root", type=Path, help="the CORE config folder. Default: ogov-pocs/ogov-config/*-ogov-config/ogovCOREConfig/src/main/config")
    ap.add_argument("--no-format", action="store_true", help="don't re-align the substituted Java lines")
    ap.add_argument("--dry-run", action="store_true", help="print what would be generated, write nothing")
    ap.add_argument("--force", action="store_true", help="overwrite existing target folders / test files")
    a = ap.parse_args()

    system = a.system.strip().lower()
    if not re.fullmatch(r"[a-z][a-z0-9]*", system):
        sys.exit(f"ERROR: system id '{a.system}' must be lowercase letters/digits (e.g. communic)")

    ws = a.workspace.resolve() if a.workspace else find_workspace()
    pocs = ws / "presencia-web" / "ogov-pocs"
    tpl_dir = a.template_dir or (pocs / f"{TEMPLATE_DOMAIN_ID}-{TEMPLATE_SYSTEM}")
    sys_dir = pocs / f"{TEMPLATE_DOMAIN_ID}-{system}"
    out_root = a.out_root or sys_dir

    det_api, det_core = detect_app_codes(pocs, system)
    api_code = (a.api_app_code or det_api or "").upper()
    core_code = (a.core_app_code or det_core or "").upper()
    bai = a.domain_bai or detect_bai(sys_dir, system)
    pascal = a.system_pascal or detect_pascal(sys_dir, system) or system[0].upper() + system[1:]
    comps = [c.strip() for c in a.components.split(",")] if a.components is not None else detect_components(sys_dir, system)
    comps = [c for c in comps if c]

    missing = [n for n, v in [("--api-app-code", api_code), ("--core-app-code", core_code), ("--domain-bai", bai)] if not v]
    if missing:
        sys.exit(f"ERROR: could not detect {', '.join(missing)}; pass them explicitly "
                 f"(app codes are read from OG00AppCodes: {system.upper()}_API_APPCODE_STR / {system.upper()}_CORE_APPCODE_STR)")
    for n, v in [("api", api_code), ("core", core_code)]:
        if not re.fullmatch(r"[A-Z]{2}[0-9A-Z]{2}", v):
            sys.exit(f"ERROR: {n} app code '{v}' is not like OG10")
    if pascal.lower() != system:
        sys.exit(f"ERROR: --system-pascal '{pascal}' does not match --system '{system}' ignoring case")

    mapping = {
        TEMPLATE_SYSTEM: system,
        TEMPLATE_SYSTEM_PASCAL: pascal,
        TEMPLATE_SYSTEM.upper(): system.upper(),
        TEMPLATE_API_CODE: api_code,
        TEMPLATE_API_CODE.lower(): api_code.lower(),
        TEMPLATE_CORE_CODE: core_code,
        TEMPLATE_CORE_CODE.lower(): core_code.lower(),
        # README: "(samplecomp and the ones to come)"
        COMPONENTS_PHRASE.format(TEMPLATE_COMPONENTS): (COMPONENTS_PHRASE.format(", ".join(comps)) if comps else "(the ones to come)"),
        # OG90AppCodesForSecurity javadoc: "(samplecomp...)", components in words ("media register")
        COMPONENTS_JAVADOC.format(TEMPLATE_COMPONENTS): COMPONENTS_JAVADOC.format(", ".join(c.replace("-", " ") for c in comps)),
    }
    bounded = {TEMPLATE_BAI: bai}
    sub_bytes, sub_str = build_replacer(mapping, bounded)

    print(f"system={system} ({pascal}, {system.upper()})  bai={bai}  api={api_code}  core={core_code}  components={comps or '-'}")
    print(f"template: {tpl_dir}\nout-root: {out_root}")

    parts = [a.only] if a.only else ["api", "core"] + (["tests"] if a.with_tests else [])
    fmt_queue: list = []
    total = 0
    for layer in [p for p in parts if p in ("api", "core")]:
        src = tpl_dir / f"{TEMPLATE_BAI}-{TEMPLATE_DOMAIN_ID}-{TEMPLATE_SYSTEM}-{COMPONENT}-{layer}"
        if not src.is_dir():
            sys.exit(f"ERROR: template project not found: {src}")
        dst = out_root / sub_str(src.name)
        if dst.exists() and not a.force and not a.dry_run:
            sys.exit(f"ERROR: {dst} already exists (use --force to overwrite, or --out-root to generate elsewhere)")
        files = list_files(src)
        print(f"\n[{layer}] {dst}  ({len(files)} files)")
        if a.dry_run:
            for rel in files:
                print("   ", sub_str(rel.as_posix()))
            continue
        if dst.exists():
            shutil.rmtree(lp(dst))
        for rel in files:
            write_generated(dst / sub_str(rel.as_posix()), read_bytes(src / rel), sub_bytes, fmt_queue)
        total += len(files)

    if "tests" in parts:
        test_root = a.tests_root or single_dir(pocs / f"{TEMPLATE_DOMAIN_ID}-test", f"*-{TEMPLATE_DOMAIN_ID}-test", "test project")
        cfg_root = a.config_root or (single_dir(pocs / f"{TEMPLATE_DOMAIN_ID}-config", f"*-{TEMPLATE_DOMAIN_ID}-config", "config project")
                                     / "ogovCOREConfig" / "src" / "main" / "config")
        files = template_test_files(test_root)
        if not files:
            sys.exit(f"ERROR: no template tests (ogov/{TEMPLATE_SYSTEM}/{COMPONENT}/test) found in {test_root}")
        targets = [(test_root / rel, test_root / sub_str(rel.as_posix())) for rel in files]
        existing = [t for _, t in targets if t.exists()]
        print(f"\n[tests] {test_root}  ({len(files)} files)")
        if existing and not a.force and not a.dry_run:
            sys.exit("ERROR: these test files already exist (use --force to overwrite):\n    "
                     + "\n    ".join(str(t.relative_to(test_root)) for t in existing))
        for src, dst in targets:
            print("   ", dst.relative_to(test_root).as_posix())
            if not a.dry_run:
                write_generated(dst, read_bytes(src), sub_bytes, fmt_queue)
                total += 1

        # CORE config component read by the guice bootstrap: {core}/components/{core}.security.dbpersistence.xml
        tpl_cfg = cfg_root / TEMPLATE_CORE_CODE.lower() / "components" / f"{TEMPLATE_CORE_CODE.lower()}.{COMPONENT}.dbpersistence.xml"
        cfg = cfg_root / sub_str(tpl_cfg.relative_to(cfg_root).as_posix())
        props = cfg_root / core_code.lower() / f"{core_code.lower()}.dbpersistence.properties.xml"
        print(f"\n[config] {cfg}")
        if not tpl_cfg.is_file():
            print(f"    WARNING: template config not found ({tpl_cfg}): create it by hand")
        elif not props.is_file():
            print(f"    PENDING: {props} does not exist (the system has no CORE db config yet), "
                  f"so {cfg.name} was NOT created: the guice tests need both")
        elif cfg.is_file() and read_bytes(cfg) == sub_bytes(read_bytes(tpl_cfg)):
            print("    already present")
        elif cfg.is_file() and not a.force:
            print("    WARNING: exists with other contents: kept (use --force to overwrite)")
        elif a.dry_run:
            print("    would be written")
        else:
            write_bytes(cfg, sub_bytes(read_bytes(tpl_cfg)))
            print("    written")

        print("\n[registrations]")
        for path, rx, sep in registrations(test_root):
            print("   ", register(path, rx, sep, sub_bytes, a.dry_run))

    if not a.dry_run:
        if not a.no_format:
            format_java(fmt_queue)
        print(f"\nOK: {total} files written")


if __name__ == "__main__":
    main()
