#!/usr/bin/env python3
"""
Shared helpers of the r01-architecture generators (scaffold_component.py, add_persistable_object.py).

- Long-path safe file IO (the OGOV source paths often exceed Windows' MAX_PATH).
- Line endings: files are handled internally with "\\n" and written back with the line ending
  the file had (new files: CRLF, like the rest of the OGOV sources).
- Template identifiers of the samplesys / samplecomp reference implementation and their replacement.
- Import handling, brace matching and insertion helpers to add code to existing classes.
- House style: new Java files are formatted with the r01-java-formatter skill; modified files only on
  the lines that changed (and the ones aligned under them).
"""
from __future__ import annotations

import difflib
import hashlib
import os
import re
import subprocess
import sys
from dataclasses import dataclass, field
from pathlib import Path

SEP = "/" * 89
SKILL_DIR = Path(__file__).resolve().parents[1]
FORMATTER = SKILL_DIR.parent / "r01-java-formatter" / "scripts" / "format_java.py"

# --- template (reference implementation) identifiers -------------------------------------
T_DOMAIN = "ogov"
T_SYSTEM, T_SYSTEM_PASCAL = "samplesys", "SampleSys"
T_COMP, T_COMP_PASCAL = "samplecomp", "SampleComp"
T_BAI = "bai"
T_API, T_CORE, T_TEST = "OG90", "OG91", "OG99"
T_OBJ = "SamplePersistableObj"


# =========================================================================================
#  WORKSPACE
# =========================================================================================
def find_workspace(start: Path | None = None) -> Path:
    """The folder that contains presencia-web (and admindigital-common)."""
    for d in [Path(start or Path.cwd()).resolve(), *Path(start or Path.cwd()).resolve().parents,
              SKILL_DIR, *SKILL_DIR.parents]:
        if (d / "presencia-web" / "ogov-pocs").is_dir():
            return d
    sys.exit("ERROR: workspace not found (a folder containing presencia-web/ogov-pocs); pass --workspace")


def lp(path: Path | str) -> str:
    s = os.path.abspath(str(path))
    if os.name == "nt" and not s.startswith("\\\\?\\"):
        s = "\\\\?\\UNC\\" + s[2:] if s.startswith("\\\\") else "\\\\?\\" + s
    return s


def exists(path: Path) -> bool:
    return os.path.exists(lp(path))


def read_text(path: Path) -> str:
    """The file content with "\\n" line endings."""
    with open(lp(path), "rb") as f:
        return f.read().decode("utf-8").replace("\r\n", "\n")


def eol_of(path: Path) -> str:
    with open(lp(path), "rb") as f:
        return "\r\n" if b"\r\n" in f.read() else "\n"


def walk_files(root: Path):
    for dirpath, dirs, files in os.walk(lp(root)):
        dirs.sort()
        for f in sorted(files):
            full = os.path.join(dirpath, f)
            yield Path(full[4:] if full.startswith("\\\\?\\") else full)


# =========================================================================================
#  NAMES
# =========================================================================================
def pascal_from_id(s: str) -> str:
    """media-register -> MediaRegister, communic -> Communic"""
    return "".join(p[:1].upper() + p[1:] for p in re.split(r"[-_]", s) if p)


def camel(s: str) -> str:
    return s[:1].lower() + s[1:]


def words(pascal: str) -> str:
    """MediaGroup -> 'media group'"""
    return " ".join(w.lower() for w in re.findall(r"[A-Z][a-z0-9]*|[a-z0-9]+", pascal))


@dataclass
class Ids:
    system: str            # communic
    system_pascal: str     # Communic
    comp: str              # media-register (directory form)
    comp_pascal: str       # MediaRegister
    bai: str               # e80a177
    api: str               # OG10
    core: str              # OG11
    domain: str = T_DOMAIN

    @property
    def comp_pkg(self) -> str:       # mediaregister
        return self.comp.replace("-", "").replace("_", "").lower()

    @property
    def system_upper(self) -> str:
        return self.system.upper()

    @property
    def comp_upper(self) -> str:
        return self.comp_pkg.upper()

    @property
    def base_pkg(self) -> str:       # ogov.communic.mediaregister
        return f"{self.domain}.{self.system}.{self.comp_pkg}"

    @property
    def artifact(self) -> str:       # ogovCommunicMediaRegister
        return f"{self.domain}{self.system_pascal}{self.comp_pascal}"

    @property
    def repo_prefix(self) -> str:    # e80a177-ogov-communic-media-register
        return f"{self.bai}-{self.domain}-{self.system}-{self.comp}"

    def component_replacements(self) -> list[tuple[str, str]]:
        """template token -> value, in application order (longest / most specific first)"""
        return [
            (f"of the {T_COMP} component", f"of the {self.comp} component"),
            (f"{T_BAI}-{T_DOMAIN}-{T_SYSTEM}-{T_COMP}", self.repo_prefix),
            (f"{T_DOMAIN}{T_SYSTEM_PASCAL}{T_COMP_PASCAL}", self.artifact),
            (f"{T_DOMAIN}.{T_SYSTEM}.{T_COMP}", self.base_pkg),
            (f"{T_DOMAIN}/{T_SYSTEM}/{T_COMP}", self.base_pkg.replace(".", "/")),
            (f"{T_SYSTEM.upper()}_{T_COMP.upper()}", f"{self.system_upper}_{self.comp_upper}"),
            (T_COMP.upper(), self.comp_upper),
            (T_SYSTEM.upper(), self.system_upper),
            (T_COMP_PASCAL, self.comp_pascal),
            (camel(T_COMP_PASCAL), camel(self.comp_pascal)),
            (T_COMP, self.comp_pkg),
            (T_SYSTEM_PASCAL, self.system_pascal),
            (T_SYSTEM, self.system),
            (T_API, self.api), (T_API.lower(), self.api.lower()),
            (T_CORE, self.core), (T_CORE.lower(), self.core.lower()),
        ]

    def sub(self, t: str) -> str:
        for a, b in self.component_replacements():
            t = t.replace(a, b)
        return t


@dataclass
class Obj:
    name: str        # MediaGroup

    @property
    def lname(self) -> str:   # mediaGroup
        return camel(self.name)

    @property
    def pkg(self) -> str:     # mediagroup
        return self.name.lower()

    @property
    def words(self) -> str:   # media group
        return words(self.name)


def sub_object(t: str, obj: Obj, ids: Ids) -> str:
    """Replaces the template object (SamplePersistableObj) and then the component identifiers."""
    # variables named after the type (e.g. "final OG90SamplePersistableObj SamplePersistableObj") -> camel case
    t = re.sub(rf"(?<=[\s(]){T_OBJ}(?=(Oid)?[,\r\n])", camel(T_OBJ), t)
    for a, b in ((T_OBJ.upper(), obj.name.upper()), (T_OBJ, obj.name), (camel(T_OBJ), obj.lname), (T_OBJ.lower(), obj.pkg)):
        t = t.replace(a, b)
    return ids.sub(t)


def serial_version_uid(fqcn: str) -> int:
    """Deterministic serialVersionUID (so regenerating a class gives the same value)."""
    return int.from_bytes(hashlib.sha256(fqcn.encode()).digest()[:8], "big", signed=True)


def renumber_serial_version_uids(t: str, fqcn: str) -> str:
    return re.sub(r"serialVersionUID = -?\d+L", lambda m: f"serialVersionUID = {serial_version_uid(fqcn)}L", t)


def fqcn_of(path: Path, text: str) -> str:
    m = re.search(r"^package\s+([\w.]+);", text, re.M)
    return (m.group(1) + "." if m else "") + path.stem


# =========================================================================================
#  JAVA TEXT HELPERS
# =========================================================================================
def import_lines(t: str) -> list[str]:
    return [l for l in t.split("\n") if l.startswith("import ")]


def _import_group(line: str) -> int:
    for i, p in enumerate(("import java.", "import javax.", "import org.", "import com.")):
        if line.startswith(p):
            return 0 if i < 2 else i - 1
    return 3


def regroup_imports(t: str, add=(), remove=()) -> str:
    """Rewrites the import block: java/javax, org, com and the rest, each sorted, separated by a blank line."""
    lines = t.split("\n")
    idx = [i for i, l in enumerate(lines) if l.startswith("import ")]
    if not idx:
        return add_imports(t, add)
    first, last = idx[0], idx[-1]
    imps = {l for l in lines[first:last + 1] if l.startswith("import ")}
    imps |= {f"import {a};" for a in add}
    imps -= {f"import {r};" for r in remove}
    block: list[str] = []
    for g in range(4):
        grp = sorted(i for i in imps if _import_group(i) == g)
        if grp:
            if block:
                block.append("")
            block += grp
    return "\n".join(lines[:first] + block + lines[last + 1:])


def add_imports(t: str, imports) -> str:
    """Inserts each import at its sorted position in its group, without touching the existing ones."""
    for imp in imports:
        line = f"import {imp};"
        lines = t.split("\n")
        if line in lines:
            continue
        g = _import_group(line)
        idx = [i for i, l in enumerate(lines) if l.startswith("import ")]
        same = [i for i in idx if _import_group(lines[i]) == g]
        if same:
            pos = next((i for i in same if lines[i] > line), same[-1] + 1)
            lines.insert(pos, line)
        elif idx:
            before = [i for i in idx if _import_group(lines[i]) < g]
            if before:                                  # after the last import of a previous group
                pos = before[-1] + 1
                lines[pos:pos] = ["", line]
            else:                                       # before the first group
                pos = idx[0]
                lines[pos:pos] = [line, ""]
        else:                                           # no imports: after the package line
            pos = next(i for i, l in enumerate(lines) if l.startswith("package ")) + 1
            lines[pos:pos] = ["", line]
        t = "\n".join(lines)
    return t


def _skip_literal(t: str, i: int) -> int:
    """i points to a quote / comment start; returns the index after it."""
    if t.startswith("//", i):
        j = t.find("\n", i)
        return len(t) if j < 0 else j
    if t.startswith("/*", i):
        j = t.find("*/", i + 2)
        return len(t) if j < 0 else j + 2
    if t.startswith('"""', i):
        j = t.find('"""', i + 3)
        return len(t) if j < 0 else j + 3
    q = t[i]
    j = i + 1
    while j < len(t) and t[j] != q:
        j += 2 if t[j] == "\\" else 1
    return j + 1


def matching_brace(t: str, open_idx: int) -> int:
    """Index of the '}' closing the '{' at open_idx (strings, chars and comments are skipped)."""
    depth, i = 0, open_idx
    while i < len(t):
        c = t[i]
        if c in "\"'" or t.startswith("//", i) or t.startswith("/*", i):
            i = _skip_literal(t, i)
            continue
        if c == "{":
            depth += 1
        elif c == "}":
            depth -= 1
            if depth == 0:
                return i
        i += 1
    raise ValueError("unbalanced braces")


def type_body(t: str, type_name: str) -> tuple[int, int]:
    """(index after the '{' that opens the type, index of its closing '}')"""
    m = re.search(rf"\b(class|interface|enum|record)\s+{re.escape(type_name)}\b", t)
    if not m:
        raise ValueError(f"type {type_name} not found")
    o = t.index("{", m.end())
    return o + 1, matching_brace(t, o)


def insert_before_closing_line(t: str, close_idx: int, code: str) -> str:
    """Inserts code (ending with \\n) right before the line that holds the closing brace at close_idx."""
    line_start = t.rfind("\n", 0, close_idx) + 1
    return t[:line_start] + code + t[line_start:]


def insert_at_type_end(t: str, type_name: str, code: str) -> str:
    _, close = type_body(t, type_name)
    return insert_before_closing_line(t, close, code)


def method_body(t: str, signature_regex: str) -> tuple[int, int]:
    m = re.search(signature_regex, t)
    if not m:
        raise ValueError(f"method {signature_regex} not found")
    o = t.index("{", m.end() - 1)
    return o + 1, matching_brace(t, o)


def remove_method(t: str, name: str) -> str:
    """Removes a method declaration (with its javadoc and annotations) by name."""
    m = re.search(rf"\n((?:[ \t]*/\*\*(?:.|\n)*?\*/\n)?(?:[ \t]*@\w+[^\n]*\n)*)[ \t]*(?:public|protected|private)[^\n;{{]*\b{re.escape(name)}\s*\(", t)
    if not m:
        raise ValueError(f"method {name} not found")
    start = m.start() + 1
    o = t.index("{", m.end())
    close = matching_brace(t, o)
    end = t.index("\n", close) + 1
    return t[:start] + t[end:]


def remove_section_placeholder(t: str, comment_regex: str) -> tuple[str, int]:
    """Removes a 'SEP / // comment / SEP' block whose comment matches; returns (text, index where it was)."""
    m = re.search(rf"^/{{89}}[ \t]*\n//[ \t]*{comment_regex}[^\n]*\n/{{89}}[ \t]*\n", t, re.M)
    if not m:
        raise ValueError(f"placeholder section {comment_regex} not found")
    return t[:m.start()] + t[m.end():], m.start()


def replace_placeholder_line(t: str, comment_regex: str, code: str) -> tuple[str, bool]:
    """Replaces a '// add ...' placeholder comment line by code; returns (text, found)."""
    m = re.search(rf"^[ \t]*//[ \t]*{comment_regex}[^\n]*\n", t, re.M)
    if not m:
        return t, False
    return t[:m.start()] + code + t[m.end():], True


# =========================================================================================
#  OUTPUT: write, format, report
# =========================================================================================
@dataclass
class Output:
    dry_run: bool = False
    formatted: bool = True
    created: list = field(default_factory=list)
    modified: list = field(default_factory=list)
    unchanged: list = field(default_factory=list)
    kept: list = field(default_factory=list)
    notes: list = field(default_factory=list)

    def write_new(self, path: Path, text: str, eol: str = "\r\n", force: bool = False, fqcn_uids: bool = True) -> bool:
        """Creates a file. Java files get deterministic serialVersionUIDs and are formatted in full."""
        if exists(path) and not force:
            self.kept.append(path)
            return False
        if path.suffix == ".java" and fqcn_uids:
            text = renumber_serial_version_uids(text, fqcn_of(path, text))
        self.created.append(path)
        if self.dry_run:
            return True
        os.makedirs(lp(path.parent), exist_ok=True)
        with open(lp(path), "wb") as f:
            f.write(text.replace("\n", eol).encode("utf-8"))
        if path.suffix == ".java" and self.formatted:
            n = len(text.split("\n"))
            format_java(path, f"1-{n}")
        return True

    def write_modified(self, path: Path, new_text: str) -> bool:
        """Rewrites an existing file keeping its line endings; formats only the changed Java lines."""
        old = read_text(path)
        if old == new_text:
            self.unchanged.append(path)
            return False
        self.modified.append(path)
        if self.dry_run:
            return True
        eol = eol_of(path)
        with open(lp(path), "wb") as f:
            f.write(new_text.replace("\n", eol).encode("utf-8"))
        if path.suffix == ".java" and self.formatted:
            lines = changed_lines(old, new_text)
            if lines:
                format_java(path, ",".join(map(str, lines)))
        return True

    def report(self, root: Path) -> None:
        def rel(p):
            try:
                return Path(p).resolve().relative_to(root.resolve()).as_posix()
            except ValueError:
                return str(p)
        tag = " (dry run)" if self.dry_run else ""
        for p in self.created:
            print(f"    created{tag}:  {rel(p)}")
        for p in self.modified:
            print(f"    modified{tag}: {rel(p)}")
        if self.unchanged:
            print(f"    {len(self.unchanged)} file(s) already up to date")
        if self.kept:
            print(f"    {len(self.kept)} file(s) already exist and were kept (--force regenerates them):")
            for p in self.kept:
                print(f"        {rel(p)}")
        for n in self.notes:
            print(f"    {n}")


def changed_lines(old: str, new: str) -> list[int]:
    a, b = old.split("\n"), new.split("\n")
    res: list[int] = []
    for tag, _i1, _i2, j1, j2 in difflib.SequenceMatcher(None, a, b, autojunk=False).get_opcodes():
        if tag in ("replace", "insert"):
            res += range(j1 + 1, j2 + 1)
    return res


def format_java(path: Path, lines: str) -> None:
    if not FORMATTER.is_file():
        print(f"WARNING: {FORMATTER} not found: apply the r01-java-formatter skill to {path}")
        return
    r = subprocess.run([sys.executable, "-I", str(FORMATTER), lp(path), "--lines", lines], capture_output=True, text=True)
    if r.returncode:
        print(f"WARNING: formatter failed on {path}: {r.stdout}{r.stderr}")


# =========================================================================================
#  DETECTION
# =========================================================================================
def app_codes_file(pocs: Path) -> Path:
    return pocs / "ogov-common/e80a180-ogov-common-api/ogovCommonAPIClasses/src/main/java/ogov/common/api/OG00AppCodes.java"


def detect_app_codes(codes_file: Path, system: str) -> tuple[str | None, str | None]:
    if not exists(codes_file):
        return None, None
    t = read_text(codes_file)
    def code(kind):
        m = re.search(rf'{system.upper()}_{kind}_APPCODE_STR\s*=\s*"(\w+)"', t)
        return m.group(1).upper() if m else None
    return code("API"), code("CORE")


def detect_bai(sys_dir: Path, system: str) -> str | None:
    if not sys_dir.is_dir():
        return None
    for d in sorted(sys_dir.iterdir()):
        m = re.match(rf"(\w+)-ogov-{re.escape(system)}-", d.name)
        if m and d.is_dir():
            return m.group(1)
    return None


def detect_system_pascal(sys_dir: Path, system: str) -> str | None:
    if not sys_dir.is_dir():
        return None
    for pom in sorted(sys_dir.glob("*/pom.xml")):
        m = re.search(rf"<artifactId>ogov({re.escape(system)})", read_text(pom), re.I)
        if m:
            return m.group(1)       # the casing used in the existing artifactIds (ogovCommunicXxx -> Communic)
    return None
