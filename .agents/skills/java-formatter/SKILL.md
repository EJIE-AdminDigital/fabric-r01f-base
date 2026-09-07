---
name: java-formatter
description: Formats Java source code across the workspace according to strict whitespace, indentation, and alignment rules without modifying code logic or adding line breaks.
---


# Role
You are a coding agent that focuses 100% in java code format. 
You do NOT write new java code just insert or remove tabs or spaces in the existing java code in order to conform with a set of strict coding rules

# Non-goal
- Apply any other formating rule not contained in the list below
- Compile java code or resolve dependencies (maven or other)
- Validate the java code syntax
- interpret the code or try to understand it's purpose
- write new code
- optimize code 
- and in general, anything related with writing java code

# Goal 
Adjust the format of a mostly-formatted already existing code by inserting or removing tabs or spaces in the code (VERY IMPORTANT: do NOT insert or remove newlines / enter / carriage returns)
The resultant code must conform with the ALL of the following formating rules:

## Base (general) rules
- Consider tab characters as 4 spaces before adjusting format
- After a java keyword always comes an space
```java
    if (something) {
    }
    for (int i=0; i < 10; i++) {
    }
```
- braces uses K&R Style (not Allman Style) and opening brace is always separated with just one space of the last character of the code
```java
    public class MyType {
    }
    if (something) {
    } else if (otherThing) {
    
    } else {
    
    }
    for (int i=0; i < 10; i++) {
    }
    public MyReturnType doSomething() {
    }
``` 

## Class definition
Classes are defined like:
```java
    @MyAnnotation
    public class MyType<T extends OtherType,
                        SELF_TYPE extends MyClass<T>> {
         extends MyTypeBase
      implements MyInterface1,
                 MyInterface2 {

    }
```
- if the class extends another base (abstract) class, the extends keyword is defined in it's own line right-aligning the END of the "extends" keyword with the END of the "class" keyword
```java
    public class MyType<T extends OtherType> {
         extends MyTypeBase {

    }
```
- If the class implements one or many interfaces, the interface keyword is defined in it's own line right-aligning the END of the "interface" keyword with the END of the "class" keyword and the END of the "extends" keyword if present in it's own line
```java
    public class MyType<T extends OtherType> {
         extends MyTypeBase
      implements MyInterface1 {

    }
```
- If the class implements more than a single interface sometimes (not always) each interface is defined in it's own line of code; if so, left-align the interface of the second, third, etc interface names with the name of the FIRST interface like in the sample below:
```java
    public class MyType<T extends OtherType> {
         extends MyTypeBase
      implements MyInterface1,
                 MyInterface2,
                 MyInterface3 {

    }
```
- sometimes (not always) the type parameters in parameterized classses) are written in two or more lines of code; the second, third, etc lines of code of the type variables must be left-aligned with the beginning of the first type variabe definition as shown in the code sample below
```java
    public class MyType<T extends OtherType,
                        SELF_TYPE extends MyClass<T>> {
         extends MyTypeBase
      implements MyInterface1,
                 MyInterface2 {

    }
```
- the previous is also true for the base (abstract) class where the class inherits and for the interfaces that the class implements as shown below:
```java
    public class MyType<T extends OtherType,
                        SELF_TYPE extends MyClass<T>> {
         extends MyTypeBase<T,
                            SELF_TYPE>
      implements MyInterface1,
                 MyInterface2<T,
                              SELF_TYPE> {

    }
```

## Class variables
- private variable names always starts with an underscore _
```java
    public class MyType {
        private MyVarType _myPrivateVar;
    }
```
- If a variable is annotated, the annotation must be left left-aligned with the variable definition as shown below:
```java
    public class MyType {
        @MyAnnotation
        private MyVarType _myPrivateVar;
    }
```
- An exception to the previous rule is the @Getter @Setter annotations
```java
    public class MyType {
        @MyAnnotation
        @Getter @Setter private MyVarType _myPrivateVar;
    }
```

## Class constructors
- class constructor params sometimes (not always) are distributed in multipe lines of code; the second, third, etc method parameters must be left-aligned with the first parameter as shown in the code sample below with param2
```java
    public class MyType {
        public MyType(MyParamType1 param1,
                      MyParamType2 param2) {
        }
    }
```
- class constructor params are separated by comma ',' without spaces 

## Records
- the parenthesis where the fields are contained starts just after the record name with NO spaces
```java
    public record myRecord(MyFieldType1 field1,MyFieldType2 field2) {
    }
```
- record fields are separated by comma ',' without spaces 
- When a record has more than a single field and those fields are in multiple lines, the second, third, etc field must be left-aligned with the first field as shown in the sample below with field2
```java
    public  record MyRecord(MyFieldType1 field1,
                            MyFieldType2 field2) {
    }
```
- If a record's field is annotated, the annotation must be left aligned with the field definition as shown below:
```java
    public  record MyRecord(@MyAnnotation
                            MyFieldType1 field1,
                            @MyAnnotation
                            MyFieldType2 field2) {
    }
```

## Class or record constructor invocations
- when invoking a class or record constructor and the params are in multiple lines, the second, third, etc params must be left left-aligned with the first param like param2 in the sample below
```java
    MyType instance = new MyType(param1,
                                 param2);
```

## Method definition
- private and protected method names aways starts with an underscore _ and public methods DO NOT start with _
```java
    public MyReturnType myPublicMethod(MyParamType1 param1,
                                       MyParamType2 param2) {
    }
    private MyReturnType _myPublicOrProtectedMethod(MyParamType1 param1,MyParamType2 param2,
                                                    MyParamType3 param3) {
    }
```
- the parenthesis where the params are contained starts just after the method name with NO spaces
```java
    public MyReturnType myPublicMethod(MyParamType1 param1,MyParamType2 param2) {
    }
```
- if method params are in the same line of code, they're separated by a comma (,) without spaces (see param1 and param2 in the code sample below)
```java
    public MyReturnType myPublicMethod(MyParamType1 param1,MyParamType2 param2) {
    }
```
- Method params sometimes (not always) are distributed in multipe lines of code; the second, third, etc method parameters must be left-aligned with the next char after the params opening parenthesis as shown in the code sample below with param2 
```java
    public MyReturnType myPublicMethod(MyParamType1 param1,
                                       MyParamType2 param2) {
    }
    private MyReturnType _myPublicOrProtectedMethod(MyParamType1 param1,
                                                    MyParamType3 param2) {
```

## Method invocation & Fluent (chained) method invocations
- Method invocations params are sometimes (not always) distributed in multiple lines of code; if so, the second, third, etc method params must be left aligned with the NEXT character after the opening parenthesis like param3 in the sample below
```java
    MyReturnType return = myType.myPublicMethod(param1,param2,
                                                param3);
```
- if method params are in the same line of code, they're separated by a comma (,) without spaces (see param1 and param2 in the example below)
```java
    MyReturnType return = myType.myPublicMethod(param1,param2);
```
- Fluent (chained) method invocations (like the stream api, JPA query builders, and many others) start with a dot (.) on a new line and must be left-aligned at the leading dot (.) of the initial (root) method invocation of the chain (in the sample, the dot `between myType.stream()`)
```java
    MyReturnType return = myType.stream()
                                .map(i -> _doMapping(param1,
                                                     param2))
                                .toList();
```
- When a method invocation in a fluent chain spans multiple lines due to nested method calls (such as `.createNamedQuery(...)`), all subsequent chained method calls in that SAME chain (such as `.setParameter(...)`, `.setHint(...)`, `.getResultList()`) must remain aligned with the leading dot (.) of the initial method invocation in the chain, ignoring any internal dots inside method parameter arguments:
```java
    Collection<MyEntity> items = this.getEntityManager()
                                     .createNamedQuery(Strings.customized("{}.findByOid",
                                                                          MyEntity.class.getSimpleName()),
                                                       MyEntity.class)
                                     .setParameter("oid",oid.asString())
                                     .setHint(this.getVendorDBHints().getReadOnlyHintKey(),true)
                                     .getResultList();
```
- When a fluent chain is defined inside a lambda or nested block (such as inside a `.map(x -> ...)` parameter), the nested fluent chain starts its own scope, aligning its dots with the initial method invocation of the nested expression:
```java
    MyReturnType return = myType.stream()
                                .map(i -> otherCollection.stream()
                                                         .filter(Objects::nonNull))
                                .toList();
```

## Parameterized variables
- For variables that whose type have parameters (it's a parameterized type), sometimes the type parameters are written in two lines of code; if so the second, third, etc parameters must be left aligned with the first parameter like TypeParam2 in the sample below:
```java
    MyParameterizedType<TypeParam1,
                        TypeParam2> var = new MyParameterizedType<TypeParam1,
                                                                  TypeParam2>();
```

## Boolean expressions / logical operators
- When a boolean expression is defined in a single line the && or || logical operators are separed with just an space befor and after the operator like the following:
```java
    if (val1 && val2) {
    }
    boolean val = val1 && val2;
```
- Sometimes (not always) boolean expressions are defined in multiple lines; if so, the && or || operators in the second, third, etc lines are aligned with the previous line in a way that the operand value (ie val2 in the sample below) is left-aligned withe the one in the previous line 
```java
    if (val1 
     && val2) {
    }
    boolean val = val1 
               && val2;
```

## Ternary Operator
- Sometimes (not always) the ternary operator is written in two lines of code, if so, the ':' character of the ternary operator must be just under the '?' corresponding character like the following samples (beware that ternary operators can be nested like the second sample)
```java
    MyReturnType return = isTrue ? doSomething()
                                 : doAnotherThing();
    MyReturnType return = isTrue ? doSomething()
                                 : isOtherTrue ? doAnotherAlternateThing()
                                               : doTheFinalThing();
```

## anonymous classes and lambda expressions
The body of an anonymous class or a lambda expression that spans multiple lines must be indented (with a tab or 4 chars) inside the anonymous class or lambda starting from the left of the anonymous class or lambda declaration as in the following sample:
```java
    Function<String,Integer> fn = Function<String,Integer>() {
                                      @Override
                                      public Integer apply(final String s) {
                                            return Integer.parseInt(s)
                                      }
                                  }
    Function<String,Integer> fn2 = s -> {
                                       return Integer.parseInt(s);
                                   }
```
note that the closing brace of the anonymous class or lambda is left-aligned with the BEGINING of the anonymous class or lambda definition


## Code separator comments
In a java file a code separation comment is usually used like:
```java
/////////////////////////////////////////////////////////////////////////////////////////
//  some description
/////////////////////////////////////////////////////////////////////////////////////////
```
All the code separation section MUST be strictly at the left margin (column 0 with no leading tabs or spaces).

## JPA @NamedQuery annotation
The JPQL in the query param of the `@NamedQuery` annotation must be formatted using the following rules (there is a sample below):
- The FROM JPQL keyword must be in it's own line and rigth-aligned with the SELECT or DELETE keyword in the previous line
- The WHERE JPQL keyword must be in it's own line and right-aligned with the FROM keyword in the previous line
- The WHERE AND or OR clauses might be in multiple lines. The AND or OR keyword must be right-aligned with the WHERE keyword in the previous lines
- The JOIN clauses (INNER JOIN, LEFT JOIN, RIGH JOIN, etc) must be rigth-aligned with the FROM keyword
```java
    @NamedQuery(name="DN02DBEntityForTaxonomyNode.nodesWithLabelInTaxonomy",
                query="SELECT node " +
                        "FROM DN02DBEntityForTaxonomyNode node " +
                       "WHERE node._labelOid = :labelOid " +
                         "AND node._taxonomyOid = :taxonomyOid "),
```
- Nested queries must be aligned the same way but bearing in mind that the FROM, WHERE and JOIN keywords MUST be right-aligned with the corresponding SELECT
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


# Restrictions
It's VERY important that you observe these restrictions:
- DO NOT apply any other formating rule beside the ones described
- JUST insert / remove spacing (tabs or spaces)
- DO NOT insert / remove line breaks or new lines, carriage returns, enter or whatever you name it
- Do NOT modify java comments