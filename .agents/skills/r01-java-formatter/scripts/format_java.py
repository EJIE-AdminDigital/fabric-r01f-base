"""Whitespace-only formatter for the house Java style described in SKILL.md.

    python format_java.py FILE [FILE ...] [--lines 12-30,41] [--check]

It only inserts or removes spaces and tabs inside lines: line breaks, code, comments and literals never change,
so its output passes verify_whitespace_only.py. The rules it applies (SKILL.md explains each one):

  - tabs -> spaces (4-column tab stops), outside string and char literals
  - a continuation line inside '(' or '<' starts under the first character after the opener
  - fluent chains: a line starting with '.' starts under the dot of the root call of its chain
  - multi-line '&&' / '||': the operand after the operator starts under the operand above
  - multi-line ternaries: a line starting with ':' starts under its '?'
  - type headers: 'extends' / 'implements' end where 'class' / 'interface' / 'enum' / 'record' ends,
    and the following types of the list start under the first one
  - JPQL in a @NamedQuery: FROM / WHERE / AND / OR / ... JOIN end where SELECT (DELETE, UPDATE) ends
  - 89-slash separator blocks (and the comment between them) at column 0
  - the body of a lambda or anonymous class opened inside (...) moves with the line that opens it
  - within a line: one space after if/for/while/switch/catch/synchronized, one space before a '{' that ends
    the line, one space around '&&' / '||', no space after ',' inside (...) and <...>

Anything else is left as it is: a missed alignment costs nothing, a wrong one costs a review. Lines inside block
comments and text blocks are never touched.

--lines restricts the edits to those 1-based lines (of the first FILE) plus the lines whose alignment depends on a
line that changed: the continuation lines aligned under a renamed name, the body of a moved lambda, and so on.
--check reports what would change without writing, and exits with 1 if anything would.
Standard library only (Python 3.8+). Long Windows paths get the \\\\?\\ prefix automatically."""
import os
import re
import sys

TAB = 4
SEPARATOR_RE = re.compile(r'^/{20,}\s*$')
HEADER_RE = re.compile(r'^(?:@[\w.]+(?:\([^()]*\))?\s+)*'
                       r'(?:(?:public|protected|private|abstract|final|static|sealed|non-sealed|strictfp)\s+)*'
                       r'(class|interface|enum|record)\s+[\w$]')
HEADER_KW_RE = re.compile(r'^(extends|implements|permits)(\s+)')
JPQL_START_RE = re.compile(r'^(SELECT|DELETE|UPDATE)\b')
JPQL_LINE_RE = re.compile(r'^"((?:(?:INNER|LEFT|RIGHT|FULL|CROSS)\s+(?:OUTER\s+)?)?JOIN|FROM|WHERE|AND|OR)\b')
KEYWORD_PAREN_RE = re.compile(r'\b(if|for|while|switch|catch|synchronized)\(')
BOOL_OP_RE = re.compile(r'[ \t]*(&&|\|\|)[ \t]*')
BOOL_LINE_RE = re.compile(r'^(&&|\|\|)\s*')
ANNOTATIONS_ONLY_RE = re.compile(r'^(@[\w.]+(\([^()]*(\([^()]*\)[^()]*)*\))?\s*)+(//.*)?$')


def long_path(p):
    p = os.path.abspath(p)
    return '\\\\?\\' + p if os.name == 'nt' and not p.startswith('\\\\?\\') else p


def indent_of(line):
    v = line.expandtabs(TAB)
    return len(v) - len(v.lstrip(' '))


# ---------------------------------------------------------------------------------------------------------------
#   Lexing: split a line into code / literal / comment pieces; block comments and text blocks span lines
# ---------------------------------------------------------------------------------------------------------------
class Lex:
    def __init__(self, other=None):
        self.block_comment = other.block_comment if other else False
        self.text_block = other.text_block if other else False


def pieces(line, lex):
    """[(kind, start, end)] with kind in code / str / chr / comment; updates 'lex' for the next line"""
    out, i, n = [], 0, len(line)
    while i < n:
        if lex.text_block:
            j = line.find('"""', i)
            end = n if j < 0 else j + 3
            out.append(('str', i, end))
            lex.text_block = j < 0
            i = end
            continue
        if lex.block_comment:
            j = line.find('*/', i)
            end = n if j < 0 else j + 2
            out.append(('comment', i, end))
            lex.block_comment = j < 0
            i = end
            continue
        j = i
        while j < n and line[j] not in '"\'/':
            j += 1
        if j > i:
            out.append(('code', i, j))
        if j >= n:
            break
        if line.startswith('//', j):
            out.append(('comment', j, n))
            break
        if line.startswith('/*', j):
            lex.block_comment = True
            i = j
            continue
        if line[j] == '/':
            out.append(('code', j, j + 1))
            i = j + 1
            continue
        if line.startswith('"""', j):
            lex.text_block = True
            out.append(('str', j, j + 3))
            i = j + 3
            continue
        q, k = line[j], j + 1
        while k < n and line[k] != q:
            k += 2 if line[k] == '\\' else 1
        out.append(('str' if q == '"' else 'chr', j, min(k + 1, n)))
        i = k + 1
    return out


def expand_tabs(line, lex):
    """tabs -> spaces outside literals (4-column stops)"""
    if '\t' not in line:
        return line
    res, col = [], 0
    for kind, a, b in pieces(line, Lex(lex)):
        for ch in line[a:b]:
            if ch == '\t' and kind not in ('str', 'chr'):
                pad = TAB - col % TAB
                res.append(' ' * pad)
                col += pad
            else:
                res.append(ch)
                col += 1
    return ''.join(res)


def is_generic_open(s, i):
    """'<' that opens type arguments (List<String>, <T> void, new HashMap<>()) rather than 'less than'"""
    prv = s[i - 1] if i > 0 else ' '
    nxt = s[i + 1] if i + 1 < len(s) else ' '
    if nxt in '<=' or prv == '<':
        return False
    if prv == ' ':
        return nxt != ' ' and (nxt.isalpha() or nxt in '?>')
    return prv.isalnum() or prv in '_$.'


# ---------------------------------------------------------------------------------------------------------------
#   Contexts: one per open bracket, plus the statement level of each block
# ---------------------------------------------------------------------------------------------------------------
class Ctx:
    def __init__(self, ch, line, align=None, shift=0, base=None):
        self.ch = ch            # '(', '<', '[', '{' or 'root'
        self.line = line        # line of the opener
        self.align = align      # column of the first character after the opener, on its line (None: nothing after)
        self.shift = shift      # '{' opened inside (...): how far its opening line moved
        self.base = base        # '{' of a lambda / anonymous class: column where the lambda or the 'new' starts
        self.jpql = None        # JPQL concatenation in progress: (line, [('sel', end col) | ('par', quote col)])
        self.array_align = None # '{' of an array initializer with elements after it: column of the first one
        self.reset(line, align)

    def reset(self, line, col):
        """a new element starts: argument, right side of '=', statement, lambda body, 'return' value..."""
        self.elem = (line, col) if col is not None else None
        self.elem_new = False   # the element starts with 'new' (an anonymous class may follow)
        self.first_dot = None   # (line, col) of the root dot of the chain of the current element
        self.qmarks = []        # pending '?' of ternaries
        self.lambda_col = None  # column where the lambda whose '->' was just seen starts


class Header:
    """a type header in progress (from 'public class X' to its '{')"""
    def __init__(self, depth, line, kw_end):
        self.depth, self.line, self.kw_end = depth, line, kw_end
        self.list_col = self.list_line = None


def scan_jpql(text, a, num, ctx):
    """follows the nesting of a JPQL literal: every '(SELECT' starts a nested query, any other '(' a group"""
    inner = text[1:]
    if ctx.jpql is None:
        m = JPQL_START_RE.match(inner)
        if not m:
            return
        ctx.jpql = (num, [('sel', a + 1 + m.end(1))])
    levels = ctx.jpql[1]
    for k, ch in enumerate(inner):
        col = a + 1 + k
        if ch == '(':
            m = re.match(r'\(\s*(SELECT|DELETE|UPDATE)\b', inner[k:])
            levels.append(('sel', col + m.end(1)) if m else ('par', col + 1))
        elif ch == ')' and len(levels) > 1:
            levels.pop()
    ctx.jpql = (num, levels)


def scan(line, num, stack, lex, state):
    """updates the contexts with the characters of a final line"""
    code_end = ''
    last = ''                   # last significant code token ('->' counts as one)
    for kind, a, b in pieces(line, lex):
        top = stack[-1]
        if kind == 'comment':
            continue
        if kind in ('str', 'chr'):
            if top.elem is None:
                top.elem = (num, a)
            text = line[a:b]
            if kind == 'str' and text.startswith('"') and not text.startswith('"""'):
                scan_jpql(text, a, num, top)
            code_end = last = 'lit'
            continue
        i = a
        while i < b:
            c = line[i]
            top = stack[-1]
            if c in ' \t':
                i += 1
                continue
            code_end = c
            nxt = line[i + 1] if i + 1 < len(line) else ''
            prv = line[i - 1] if i > 0 else ''
            if top.elem is None:
                top.elem = (num, i)
                top.elem_new = bool(re.match(r'new\b', line[i:]))
            prev_last, last = last, c
            if c in '([':
                j = i + 1
                while j < len(line) and line[j] in ' \t':
                    j += 1
                # the first character after the opener may be a comment: '(// the security context'
                stack.append(Ctx(c, num, j if line[j:].strip() else None))
            elif c == '{':
                h = state.get('header')
                if h and len(stack) == h.depth:
                    state['header'] = None
                # the body of a lambda or an anonymous class is indented from where they start;
                # a block opened inside (...) also moves with its line
                base = None
                if prev_last == '->' and top.lambda_col is not None:
                    base = top.lambda_col
                elif prev_last == ')' and top.elem_new and top.elem:
                    base = top.elem[1]
                # a block moves with its line when it is opened inside (...) or inside a lambda / anonymous class
                # body (or a block that already moves); a class or method body at the top level never does
                moves = top.ch in '([' or (top.ch == '{' and (top.base is not None or top.shift))
                ctx = Ctx('{', num, None, shift=state['delta'] if moves else 0, base=base)
                # an array initializer ({A.class, B.class}) aligns its elements like the arguments of a call
                if prev_last in ('=', '(', ',', '{', ']') and base is None:
                    j = i + 1
                    while j < len(line) and line[j] in ' \t':
                        j += 1
                    if line[j:].strip() and not line[j:].startswith('//'):
                        ctx.array_align = j
                        ctx.reset(num, j)
                stack.append(ctx)
            elif c == '<' and is_generic_open(line, i):
                stack.append(Ctx('<', num, i + 1 if nxt not in ' \t' and nxt else None))
            elif c == '>' and top.ch == '<' and prv != '-':
                stack.pop()
            elif c in ')]}':
                while len(stack) > 1 and stack[-1].ch == '<':
                    stack.pop()
                if len(stack) > 1:
                    stack.pop()
                if c == '}' and stack[-1].ch in ('{', 'root'):
                    stack[-1].reset(num, None)
            elif c == ';':
                while len(stack) > 1 and stack[-1].ch == '<':
                    stack.pop()
                if stack[-1].ch in ('{', 'root'):
                    stack[-1].reset(num, None)
            elif c == ',' and top.ch in '(<[':
                top.reset(num, None)
            elif c == '=' and nxt != '=' and prv not in '=!<>' and top.ch != '<':
                top.reset(num, None)
            elif c == '-' and nxt == '>':
                start = top.elem[1] if top.elem and top.elem[0] == num else None
                top.reset(num, None)
                top.lambda_col = start
                last = '->'
                i += 2
                continue
            elif c == '?' and top.ch != '<':
                top.qmarks.append((num, i))
            elif c == ':' and nxt != ':' and prv != ':' and top.qmarks:
                top.qmarks.pop()
            elif c == '.' and top.first_dot is None and nxt != '.' and prv != '.':
                before = line[:i].rstrip()
                tok = re.search(r'[\w$]+$', before)
                if before and (before[-1] in ')]>' or (tok and not tok.group(0).isdigit())) \
                        and (nxt.isalpha() or nxt in '_$<') and top.elem and (num, i) > top.elem:
                    top.first_dot = (num, i)
            elif line.startswith('return', i) and not re.match(r'[\w$]', line[i + 6:i + 7]) \
                    and not re.match(r'[\w$]', prv):
                top.reset(num, None)
                i += 6
                continue
            i += 1
    # a line with only annotations (@Override, @Getter @Setter...) does not start the statement below it
    if stack[-1].ch in ('{', 'root') and ANNOTATIONS_ONLY_RE.match(line.strip()):
        stack[-1].reset(num, None)
    # a JPQL concatenation goes on only while its lines end with '+'
    if code_end != '+':
        for ctx in stack:
            ctx.jpql = None


# ---------------------------------------------------------------------------------------------------------------
#   Spacing within a line (only on the lines being formatted)
# ---------------------------------------------------------------------------------------------------------------
def normalize_spacing(line, lex, stack):
    parts = pieces(line, Lex(lex))
    code_spans = [(a, b) for k, a, b in parts if k == 'code']
    if not code_spans:
        return line
    content = [(a, b) for k, a, b in parts if k != 'comment' and line[a:b].strip()]
    first_content = content[0][0] if content else 0
    last_content = content[-1][1] if content else 0
    depth = [c.ch for c in stack]
    out, pos = [], 0
    for kind, a, b in parts:
        seg = line[a:b]
        if kind != 'code':
            out.append(seg)
            continue
        seg = KEYWORD_PAREN_RE.sub(r'\1 (', seg)
        res, i = [], 0
        while i < len(seg):
            c = seg[i]
            if c in '([{':
                depth.append(c)
            elif c == '<' and is_generic_open(seg, i):
                depth.append('<')
            elif c == '>' and depth and depth[-1] == '<' and (i == 0 or seg[i - 1] != '-'):
                depth.pop()
            elif c in ')]}':
                while depth and depth[-1] == '<':
                    depth.pop()
                if depth:
                    depth.pop()
            if c == ',' and depth and depth[-1] in '(<':
                j = i + 1
                while j < len(seg) and seg[j] in ' \t':
                    j += 1
                res.append(',')
                i = j if j < len(seg) else i + 1     # spaces before a comment or a literal at the end stay
                continue
            m = BOOL_OP_RE.match(seg, i) if c in ' \t&|' else None
            if m and m.group(1):
                start_abs, end_abs = a + m.start(), a + m.end()
                if start_abs > first_content and end_abs < last_content:
                    res.append(' ' + m.group(1) + ' ')
                    i = m.end()
                    continue
                if m.start() == i and c in '&|':
                    res.append(m.group(1))
                    i += 2
                    continue
            res.append(c)
            i += 1
        out.append(''.join(res))
    new = ''.join(out)
    # one space before a '{' that ends the code of the line
    parts = pieces(new, Lex(lex))
    code = [(a, b) for k, a, b in parts if k == 'code' and new[a:b].strip()]
    if code:
        a, b = code[-1]
        seg = new[a:b].rstrip()
        if seg.endswith('{') and new[:a + len(seg) - 1].strip():
            left = new[:a + len(seg) - 1].rstrip(' \t')
            if left and (left[-1].isalnum() or left[-1] in ')]>"_$'):
                new = left + ' {' + new[a + len(seg):]
    return new


# ---------------------------------------------------------------------------------------------------------------
#   Alignment of one line
# ---------------------------------------------------------------------------------------------------------------
def target(body, stack, state, next_line):
    """(indent, anchor line) for a line whose text without leading blanks is 'body'; (None, None) for no rule"""
    top = stack[-1]
    if SEPARATOR_RE.match(body):
        return 0, None
    if state['prev_sep'] and body.startswith('//') and SEPARATOR_RE.match(next_line.strip()):
        return 0, None
    if top.jpql and body.startswith('"'):
        kind, col = top.jpql[1][-1]
        if kind == 'par':                       # inside '( ... )' of the JPQL: under its first character
            return col, top.jpql[0]
        m = JPQL_LINE_RE.match(body)
        ind = col - 1 - len(m.group(1)) if m else -1
        return (ind, top.jpql[0]) if ind >= 0 else (None, None)
    if body.startswith('.') and not body.startswith('..'):
        return (top.first_dot[1], top.first_dot[0]) if top.first_dot else (None, None)
    if body.startswith(':') and not body.startswith('::'):
        return (top.qmarks[-1][1], top.qmarks[-1][0]) if top.qmarks else (None, None)
    m = BOOL_LINE_RE.match(body)
    if m:
        if top.elem is None:
            return None, None
        ind = top.elem[1] - len(m.group(0))       # the operand (after the operator and its spaces) goes under
        return (ind, top.elem[0]) if ind >= 0 else (None, None)
    h = state.get('header')
    if h and len(stack) == h.depth and top.ch in ('{', 'root'):
        m = HEADER_KW_RE.match(body)
        if m:
            ind = h.kw_end - len(m.group(1))
            return (ind, h.line) if ind >= 0 else (None, None)
        if h.list_col is not None and not body.startswith(('{', '<', '(', '@', '/')):
            return h.list_col, h.list_line
        return None, None
    operator = body.startswith((')', '>', '+', '-', '*', '?', '|', '&')) \
               or (body.startswith('/') and not body.startswith(('//', '/*')))
    if top.ch in '(<' and top.align is not None and not operator:
        return top.align, top.line           # an argument, a parameter, or a comment between them
    if top.ch == '{' and top.array_align is not None and not body.startswith('}'):
        return top.array_align, top.line
    if top.ch == '{' and top.base is not None:
        if body.startswith('}'):
            return top.base, top.line
        if top.elem is None and not body.startswith(('//', '/*', '*')):
            return top.base + TAB, top.line     # a statement of the body
    if top.ch == '{' and top.shift:
        return 'shift', top.line
    return None, None


def track_header(line, num, stack, state):
    top = stack[-1]
    if top.ch not in ('{', 'root'):
        return
    body = line.lstrip(' ')
    base = len(line) - len(body)
    m = HEADER_RE.match(body)
    if m:
        h = Header(len(stack), num, base + m.end(1))
        rest = body[m.end(1):]
        mm = re.search(r'\b(extends|implements|permits)\s+(?=\S)', rest)
        if mm and '<' not in rest[:mm.start()]:
            h.list_col, h.list_line = base + m.end(1) + mm.end(), num
        state['header'] = h if '{' not in rest else None
        return
    h = state.get('header')
    if h and len(stack) == h.depth:
        mk = HEADER_KW_RE.match(body)
        if mk:
            h.list_col, h.list_line = base + mk.end(), num


def format_text(text, only=None):
    raw = text.split('\n')
    lines = [l[:-1] if l.endswith('\r') else l for l in raw]
    eols = ['\r' if l.endswith('\r') else '' for l in raw]
    lex = Lex()
    stack = [Ctx('root', 0)]
    state = {'header': None, 'prev_sep': False, 'delta': 0}
    changed = set()
    for idx, line in enumerate(lines):
        num = idx + 1
        in_range = only is None or num in only
        new = line
        if in_range and not (lex.block_comment or lex.text_block) and line and not line.strip():
            new = ''                            # a blank line with only spaces or tabs becomes empty
        elif not (lex.block_comment or lex.text_block) and line.strip():
            cur = expand_tabs(line, lex) if in_range else line
            if in_range:
                cur = normalize_spacing(cur, lex, stack)
            body = cur.lstrip(' \t')
            nxt = lines[idx + 1] if idx + 1 < len(lines) else ''
            ind, anchor = target(body, stack, state, nxt)
            if ind == 'shift':
                ind = indent_of(line) + stack[-1].shift
                ind = ind if ind >= 0 else None
            # outside --lines, a line is realigned only when the line it aligns with was given or has changed
            if ind is not None and (in_range or anchor in changed or (only is not None and anchor in only)):
                cur = ' ' * ind + (body.rstrip(' \t') if SEPARATOR_RE.match(body) else body)
            elif not in_range:
                cur = line
            new = cur
        if new != line:
            changed.add(num)
            lines[idx] = new
        state['delta'] = indent_of(new) - indent_of(line) if num in changed else 0
        vis = new.expandtabs(TAB)     # columns as an editor with 4-column tabs shows them
        if not (lex.block_comment or lex.text_block):
            track_header(vis, num, stack, state)
        scan(vis, num, stack, lex, state)
        state['prev_sep'] = bool(SEPARATOR_RE.match(new.strip()))
    return '\n'.join(l + e for l, e in zip(lines, eols)), sorted(changed)


# ---------------------------------------------------------------------------------------------------------------
#   Command line
# ---------------------------------------------------------------------------------------------------------------
def ranges(nums):
    out, start, prev = [], None, None
    for n in nums + [None]:
        if start is not None and n == prev + 1:
            prev = n
            continue
        if start is not None:
            out.append(str(start) if start == prev else f'{start}-{prev}')
        start = prev = n
    return ','.join(out)


def main(argv):
    files, only, check, i = [], None, False, 0
    while i < len(argv):
        a = argv[i]
        if a == '--lines' and i + 1 < len(argv):
            only = set()
            for part in argv[i + 1].split(','):
                x, _, y = part.strip().partition('-')
                only.update(range(int(x), int(y or x) + 1))
            i += 2
            continue
        if a == '--check':
            check = True
        elif a in ('-h', '--help'):
            print(__doc__)
            return 0
        else:
            files.append(a)
        i += 1
    if not files:
        print(__doc__)
        return 2
    total = 0
    for k, f in enumerate(files):
        p = long_path(f)
        with open(p, 'rb') as fh:
            text = fh.read().decode('utf-8', errors='surrogateescape')
        new, changed = format_text(text, only if k == 0 else None)
        total += len(changed)
        if not changed:
            print(f'{f}: no changes')
        elif check:
            print(f'{f}: {len(changed)} lines would change: {ranges(changed)}')
        else:
            with open(p, 'wb') as fh:
                fh.write(new.encode('utf-8', errors='surrogateescape'))
            print(f'{f}: {len(changed)} lines changed: {ranges(changed)}')
    return 1 if check and total else 0


if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
