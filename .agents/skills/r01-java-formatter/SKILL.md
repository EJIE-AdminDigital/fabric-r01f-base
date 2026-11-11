---
name: r01-java-formatter
description: Re-aligns existing Java source files to the team's house style (K&R braces, right-aligned extends/implements, parameters aligned under the first one, fluent chains aligned on the root dot, multi-line && / || and ternaries, JPQL keywords in @NamedQuery) by changing ONLY spaces and tabs, never line breaks, names, comments or logic. Use it whenever the user asks to format, re-format, align, indent, tidy up or "apply our style" to .java files, a class, a package or the whole workspace, including Spanish requests such as "formatea", "alinea", "ajusta el formato", "pasa el formateador" or "deja esta clase con nuestro estilo", even if the skill is not named. Do not use it to write, refactor, rename or fix Java code.
---

# Java formatter

Adjust the layout of existing, mostly-formatted Java code so it follows the house style below. The only edits allowed are inserting or removing spaces and tabs inside a line.

This narrow scope is deliberate: the output is reviewed as a diff, and a diff that only moves whitespace can be reviewed (or ignored with `git diff -w`) at a glance, keeps line numbers and `git blame` intact, and cannot break the build. So:

- Never add or remove line breaks, never join or split lines, never add or remove blank lines.
- Never rename, reorder, add or remove code, and never change comment text.
- Never change whitespace inside string or char literals, or on lines inside a text block (`"""`): there it is data, not layout.
- Apply only the rules in this file. If a line is ambiguous or no rule covers it, leave it as it is: a missed alignment costs nothing, a changed line costs a review.
- Do not compile, resolve dependencies, validate syntax or try to understand what the code does.

The bundled `scripts/format_java.py` applies these rules for you (see "Workflow"), so most of the work is running it and reviewing the result. The rules below remain the reference: they say what the script does, and what to do by hand where it can't decide.

## Workflow

1. **Pick the files and the lines.** Use the file, folder or glob the user gave; otherwise the files they named. For "the workspace" or a folder, take every `*.java` under it except build output (`target/`, `build/`, `bin/`) and generated sources. When the request is to format only what you (or a script) wrote or changed, note those line numbers: only they get formatted, plus the lines that align with them.
2. **Keep the original.** Before formatting a file, copy it to a temporary folder outside the project (your session's scratch folder, or the system temp folder); step 5 needs it.
3. **Run the formatter.** The scripts are in `scripts/` next to this `SKILL.md`, so build the path from the folder this file was loaded from (e.g. `.agents/skills/r01-java-formatter/scripts/format_java.py`). They need Python 3 with no extra packages; the command is `python`, `python3` or, on Windows, `py`, whichever works (on Windows `python3` is often just a Microsoft Store shortcut that prints "Python was not found"; try `python` or `py` instead):
   ```bash
   python <skill-folder>/scripts/format_java.py <file> [<file> ...]
   python <skill-folder>/scripts/format_java.py <file> --lines 12-30,41
   ```
   It applies, in place and deterministically, every rule of this file that can be decided mechanically: tabs, parameters and arguments under the first one, type arguments, fluent chains on the root dot, `&&` / `||` and ternaries, `extends` / `implements`, `@NamedQuery` JPQL (nested queries included), separator blocks, lambda and anonymous-class bodies, array initializers, and the single-line spacing rules. With `--lines` it formats only those lines (1-based, of the first file), plus the lines whose alignment depends on them: the continuation lines under a renamed name, or the body of a lambda whose opening line moved. It prints the lines it changed. `--check` reports what would change without writing anything.
4. **Review what is left.** Read the changed lines and the parts no rule covers, and fix by hand only what clearly breaks a rule: a statement indented with the wrong depth, a brace that is not K&R, a continuation line the script left alone because it had no rule (for example after a `throws` or inside a multi-line string concatenation that is not JPQL). Use targeted in-place edits of the affected lines, never a rewrite of the file, and keep each line's ending (CRLF or LF) exactly as it was: some editing tools rewrite a whole file with LF, which the check in step 5 reports as a failure. If a line is ambiguous, leave it.
5. **Verify.** Run the checker:
   ```bash
   python <skill-folder>/scripts/verify_whitespace_only.py <original-copy> <formatted-file>
   ```
   It fails if the line count, a line ending, any non-whitespace character or a literal changed. If it fails, fix the reported lines and run it again; do not report a file as done until it passes. If no Python is available at all, format by hand following the rules below and fall back to `git diff -w -- <formatted-file>` (it must print nothing), and say in the report that this fallback does not check line endings or whitespace inside literals.
6. **Report.** List the files changed (the formatter prints the changed lines of each one) and anything you fixed by hand in step 4. Naming conventions (see "Naming conventions" below) are not fixed by this skill: list the members that break them so the user can decide.

## Columns and tabs

Every alignment rule counts columns from the start of the line, with a tab counting as 4 columns. Write the result with spaces only: replace tabs with the equivalent spaces, because tab-based alignment breaks in any editor with a different tab width.

## Base rules

- Statements inside a block are indented 4 columns more than the line that opens the block.
- A Java keyword is followed by one space (`if (`, `for (`, `while (`, `switch (`, `catch (`, `synchronized (`):
```java
    if (something) {
    }
    for (int i=0; i < 10; i++) {
    }
```
- Braces use K&R style (not Allman): the opening brace goes on the same line, separated by exactly one space from the last character of code:
```java
    public class MyType {
    }
    if (something) {
    } else if (otherThing) {

    } else {

    }
    public MyReturnType doSomething() {
    }
```

## Class definition

The complete shape of a class header:
```java
    @MyAnnotation
    public class MyType<T extends OtherType,
                        SELF_TYPE extends MyClass<T>>
         extends MyTypeBase
      implements MyInterface1,
                 MyInterface2 {

    }
```
- If the class extends a base class, `extends` goes on its own line, right-aligned so that the END of `extends` lines up with the END of `class`.
- If the class implements interfaces, `implements` goes on its own line, right-aligned the same way: its END lines up with the END of `class` (and of `extends`, when present).
- The opening brace goes after the last element of the header (last interface, base class or type parameter), never on the `class` line when `extends` / `implements` follow on their own lines.
```java
    public class MyType<T extends OtherType>
         extends MyTypeBase
      implements MyInterface1 {

    }
```
- When the interfaces are on several lines (not always the case), the second, third, etc. interface names are left-aligned with the FIRST interface name:
```java
    public class MyType<T extends OtherType>
         extends MyTypeBase
      implements MyInterface1,
                 MyInterface2,
                 MyInterface3 {

    }
```
- When type parameters are on several lines (not always the case), the second, third, etc. lines are left-aligned with the first type parameter. The same applies to the type arguments of the base class and of each interface:
```java
    public class MyType<T extends OtherType,
                        SELF_TYPE extends MyClass<T>>
         extends MyTypeBase<T,
                            SELF_TYPE>
      implements MyInterface1,
                 MyInterface2<T,
                              SELF_TYPE> {

    }
```

## Class variables

- An annotation goes on its own line, left-aligned with the variable declaration:
```java
    public class MyType {
        @MyAnnotation
        private MyVarType _myPrivateVar;
    }
```
- The exception is `@Getter @Setter` (Lombok), which stay on the same line as the declaration:
```java
    public class MyType {
        @MyAnnotation
        @Getter @Setter private MyVarType _myPrivateVar;
    }
```

## Constructors and methods (definition)

- The parenthesis that opens the parameters goes right after the name, with no space.
- Parameters on the same line are separated by a comma with no space after it.
- When the parameters are on several lines (not always the case), the second, third, etc. lines are left-aligned with the character right after the opening parenthesis:
```java
    public class MyType {
        public MyType(MyParamType1 param1,
                      MyParamType2 param2) {
        }
    }
    public MyReturnType myPublicMethod(MyParamType1 param1,MyParamType2 param2) {
    }
    public MyReturnType myPublicMethod(MyParamType1 param1,
                                       MyParamType2 param2) {
    }
    private MyReturnType _myPrivateMethod(MyParamType1 param1,MyParamType2 param2,
                                          MyParamType3 param3) {
    }
```

## Records

- The parenthesis that holds the fields goes right after the record name, with no space; fields on the same line are separated by a comma with no space after it.
```java
    public record MyRecord(MyFieldType1 field1,MyFieldType2 field2) {
    }
```
- When the fields are on several lines, the second, third, etc. fields are left-aligned with the first one:
```java
    public record MyRecord(MyFieldType1 field1,
                           MyFieldType2 field2) {
    }
```
- A field annotation is left-aligned with the field definition:
```java
    public record MyRecord(@MyAnnotation
                           MyFieldType1 field1,
                           @MyAnnotation
                           MyFieldType2 field2) {
    }
```

## Constructor and method invocations

- Arguments on the same line are separated by a comma with no space after it.
- When the arguments are on several lines (not always the case), the second, third, etc. lines are left-aligned with the character right after the opening parenthesis:
```java
    MyType instance = new MyType(param1,
                                 param2);
    MyReturnType result = myType.myPublicMethod(param1,param2,
                                                param3);
```

## Fluent (chained) invocations

- Each call of a fluent chain (stream API, JPA query builders, builders in general) that starts a new line begins with its dot, left-aligned with the dot of the first (root) call of the chain:
```java
    MyReturnType result = myType.stream()
                                .map(i -> _doMapping(param1,
                                                     param2))
                                .toList();
```
- When one call of the chain spans several lines because of nested calls in its arguments (such as `.createNamedQuery(...)` below), the following calls of the SAME chain (`.setParameter(...)`, `.setHint(...)`, `.getResultList()`) still align with the dot of the root call. Dots inside the arguments do not count:
```java
    Collection<MyEntity> items = this.getEntityManager()
                                     .createNamedQuery(Strings.customized("{}.findByOid",
                                                                          MyEntity.class.getSimpleName()),
                                                       MyEntity.class)
                                     .setParameter("oid",oid.asString())
                                     .setHint(this.getVendorDBHints().getReadOnlyHintKey(),true)
                                     .getResultList();
```
- A fluent chain nested inside a lambda or an argument (such as inside `.map(x -> ...)`) starts its own alignment, on the dot of its own root call:
```java
    MyReturnType result = myType.stream()
                                .map(i -> otherCollection.stream()
                                                         .filter(Objects::nonNull))
                                .toList();
```

## Parameterized types

When the type arguments of a variable type are on several lines (not always the case), the second, third, etc. arguments are left-aligned with the first one:
```java
    MyParameterizedType<TypeParam1,
                        TypeParam2> value = new MyParameterizedType<TypeParam1,
                                                                    TypeParam2>();
```

## Boolean expressions

- On a single line, `&&` and `||` have exactly one space before and after:
```java
    if (val1 && val2) {
    }
    boolean val = val1 && val2;
```
- When the expression spans several lines (not always the case), the operator starts the line and is placed so that the operand after it (`val2` below) is left-aligned with the operand on the previous line:
```java
    if (val1
     && val2) {
    }
    boolean val = val1
               && val2;
```

## Ternary operator

When a ternary spans two lines (not always the case), its `:` goes right under its matching `?`. Ternaries can be nested:
```java
    MyReturnType result = isTrue ? doSomething()
                                 : doAnotherThing();
    MyReturnType result = isTrue ? doSomething()
                                 : isOtherTrue ? doAnotherAlternateThing()
                                               : doTheFinalThing();
```

## Anonymous classes and lambdas

The body of an anonymous class or a multi-line lambda is indented 4 columns from the column where the anonymous class (`new`) or the lambda starts, and its closing brace is left-aligned with that same column:
```java
    Function<String,Integer> fn = new Function<String,Integer>() {
                                      @Override
                                      public Integer apply(final String s) {
                                          return Integer.parseInt(s);
                                      }
                                  };
    Function<String,Integer> fn2 = s -> {
                                       return Integer.parseInt(s);
                                   };
```

## Code separator comments

Separator comments go at column 0, with no leading spaces or tabs. Move the whole block, but do not change its text:
```java
/////////////////////////////////////////////////////////////////////////////////////////
//  some description
/////////////////////////////////////////////////////////////////////////////////////////
```

## JPA @NamedQuery

The JPQL in the `query` attribute is a concatenation of string literals, one clause per line. Align it by moving the whitespace BEFORE each opening quote, never inside the literal:
- `FROM` is on its own line, right-aligned (its END) with the `SELECT` or `DELETE` on the previous line.
- `WHERE` is on its own line, right-aligned with `FROM`.
- `AND` / `OR` conditions may be on several lines, each right-aligned with `WHERE`.
- `JOIN` clauses (`INNER JOIN`, `LEFT JOIN`, `RIGHT JOIN`, `LEFT OUTER JOIN`, etc.) are right-aligned with `FROM`.
```java
    @NamedQuery(name="DN02DBEntityForTaxonomyNode.nodesWithLabelInTaxonomy",
                query="SELECT node " +
                        "FROM DN02DBEntityForTaxonomyNode node " +
                       "WHERE node._labelOid = :labelOid " +
                         "AND node._taxonomyOid = :taxonomyOid "),
```
- A nested query is aligned the same way, relative to its own `SELECT`:
```java
    @NamedQuery(name="DN02DBEntityForTaxonomyNode.leafNodesDescendantsOf",
                query="SELECT node " +
                        "FROM DN02DBEntityForTaxonomyNode node " +
                       "WHERE node._oid IN (SELECT descendantLeafs._descendantOid " +
                                             "FROM DN02DBEntityForTaxonomyNodeRelation descendantLeafs " +
                                            "WHERE descendantLeafs._ancestorOid = :parentNodeOid " +
                                              "AND descendantLeafs._descendantOid IN (SELECT rel1._ancestorOid " +
                                                                                       "FROM DN02DBEntityForTaxonomyNodeRelation rel1 " +
                                                                            "LEFT OUTER JOIN DN02DBEntityForTaxonomyNodeRelation rel2 ON (rel1._ancestorOid = rel2._ancestorOid " +
                                                                                                                                          "AND " +
                                                                                                                                          "rel1._descendantOid <> rel2._descendantOid) " +
                                                                                      "WHERE rel2._descendantOid IS NULL))")
```

## Naming conventions (report only)

The house style also has naming rules. Renaming is a code change, so do not apply them; list the violations in the final report:
- Private fields start with an underscore (`_myPrivateVar`).
- Private and protected methods start with an underscore (`_doMapping`); public methods do not.

## Changing the formatter or these rules

This section is for whoever maintains the skill, not for formatting a file. After changing `scripts/format_java.py`, or any Java example in this file (the examples are the spec, and the tests check that the formatter leaves each of them unchanged), run the tests bundled with the skill:
```bash
python <skill-folder>/scripts/tests/test_format_java.py
```
Besides the examples, they check one input per rule against its expected output, the `--lines` behaviour after a rename, whitespace-only output on messy inputs, idempotence, CRLF and literals, and the command line. They use `scripts/tests/fixtures/golden.java`, a file in house style that must stay a fixed point: if you add a rule, add a case to it. To also check real code, without writing to it, set `R01_FORMAT_CORPUS` to a folder (e.g. a copy of `presencia-web`).
