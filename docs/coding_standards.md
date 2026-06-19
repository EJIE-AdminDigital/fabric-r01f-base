Java coding standards
=================================================

Project names
-------------------------------------------------
Projects MUST ALWAYS start with the `{appCode}` as `{appCode}{Project}`

ie: if [appCode]=TCK01 a project might be named `TCK01ModelClasses`

There are some main conventions with [project] names:

    Model objects: {appCode}ModelClasses
    Client API   : {appCode}ClientAPIClasses
    CORE         : {appCode}COREClasses
    UI           : {appCode}UIWar
    REST services: {appCode}CORERESTServicesWar
    Config       : {appCode}ConfigByEnv

Package names
-------------------------------------------------
Packages MUST start ALWAYS with the `[appCode]` as `{appCode}.{package}`

ie: if [appCode]=TCK01 a package might be `tck01.util`

Class names
-------------------------------------------------
Class names MUST start ALWAYS with the `[appCode]` as `{appCode}{Type}`

ie: if [appCode]=TCK01 the a class should be named like `TCK01MyType`


Bracing style:
-------------------------------------------------
Use this brace style:

```java
    public void doSomenting() {
    }
```

**NOT** this:

```java
    public void doSomenting() 
    {
        ...
    }
```

Spacing:
-------------------------------------------------
Use **4 character tabs**

The `golden rule`: **PUT AN SPACE AFTER A RESERVED WORD**

Wrong:

```java
    try {
        if(something) {
            for(int i=0; i<0; i++) { 
                ...
            }
        }
    } catch(Throwable th) {
        ...
    }
```

Right (beware the space char AFTER the reserved word):

```java
    try {
        if (something) {
            for (int i=0; i<0; i++) { 
                ...
            }
        }
    } catch (Throwable th) {
        ...
    }
```

Comments:
-------------------------------------------------
Leave an space AFTER a line comments:

Wrong:

```java
    //this is a line comment
```
    
Right: 

```java
    // this is a line comment 
```
    
Use `separators` wisely:

```java
    /////////////////////////////////////////////////////////////////////////////////////////
    //    SECTION NAME
    /////////////////////////////////////////////////////////////////////////////////////////
```
    
Indentation:
-------------------------------------------------
Use indentation wisely:

Wrong:

```java
    public void myLongParamsMethod(final String name,final String surname,final Date birthDate,final Phone phone) {
        ...
    }
    public void myOtherMethod() {
        this.myLongParamsMethod("Evo","Morales",Phone.of("67456732"),new Date());
    }
```

Right: try to group params with the same context 

```java
    public void myLongParamsMethod(final String name,final String surname,
                                            final Date birthDate,
                                            final Phone phone) {
        ...
    }
    public void myOtherMethod() {
        this.myLongParamsMethod("Evo","Morales",
                                      birthDate,
                                      Phone.of("67456732"));
    }
```

Wrong indentation of lambda expressions:

```java
    textField.addChangeListener(changeEvent -> {
        String val = changeEvent.getValue();
        ...
    });
```
    
Right indentation of lambda expression:

```java
    textField.addChangeListener(changeEvent -> {
                                            String val = changeEvent.getValue();
                                            ...
                                        });
```


Class definitions
-------------------------------------------------
Wrong:

```java
    public class MyType extends MyTypeBase implements MyInterface1,MyInterface2 {
        ...
    }
```
    
Right (beware the indentation):

```java
    public class MyType
         extends MyTypeBase
      implements MyInterface1,
                 MyInterface2 {
        ...
    }
         class MyPackageProtectedType
       extends MyTypeBase
    implements MyInterface {
            ...
    }
```

Private and protected fields and methods
-------------------------------------------------
Private [fields] & [methods] MUST ALWAYS named starting with an hipen and
they MUST ALWAYS be refered to using JUST the name (never with `this.{field}`)

Wrong:

```java
    public class MyType {
        private final String myStringField;
        
        private String myMethod(final String param) {
            this.myStringField = "Hey!";
        }
    }
```
    
Right:

```java
    public class MyType {
        private final String _myStringField;
        
        private String _myMethod(final String param) {
            _myStringField = "Hey!";
        }
    }
```

Public methods
-------------------------------------------------
Public methods MUST ALWAYS be refered to with `this.{method}` as opposite to private ones
that MUST ALWAYS be refered to using the name (which starts with _)
... this way one can know if a method is public or private just with a glance

Wrong:

```java
    public class MyType {        
        public String myPublicMethod() {
            myOtherPublicMethod();
        }
        public String myOtherPublicMethod() {
            myPrivateMethod();
        }
        private String myPrivateMethod() {
        }
    }
```
    
Right:

```java
    public class MyType {        
        public String myPublicMethod() {
            this.myOtherPublicMethod();
        }
        public String myOtherPublicMethod() {
            _myPrivateMethod();
        }
        private String _myPrivateMethod() {
        }
    }    
```


Type's definition: fields, constructors & methods
-------------------------------------------------
A type's elements SHOULD ALWAYS be defined in this order:

    1.- Constants (static final fields)
    2.- Instance Fields (NEVER public)
    3.- Constructors
    4.- Public & private methods grouped by function
    
Use a separator like:

```java
    /////////////////////////////////////////////////////////////////////////////////////////
    //    SECTION NAME
    /////////////////////////////////////////////////////////////////////////////////////////
```

Wrong:

```java
    public class MyType {
        public MyType(final String fieldVal) {
            _myStringField = fieldVal;
        }
        
        public String myMethod() {
    
        }
    
        private final String _myStringField;
    }
```

Right:

```java
    public class MyType {
    /////////////////////////////////////////////////////////////////////////////////////////
    //    SECTION NAME
    /////////////////////////////////////////////////////////////////////////////////////////
        private final String _myStringField;
        
    /////////////////////////////////////////////////////////////////////////////////////////
    //    CONSTRUCTORS
    /////////////////////////////////////////////////////////////////////////////////////////        
        public MyType(final String fieldVal) {
            _myStringField = fieldVal;
        }
    /////////////////////////////////////////////////////////////////////////////////////////
    //    METHODS
    /////////////////////////////////////////////////////////////////////////////////////////
        public String myMethod() {
    
        }
    }
```

Method params
-------------------------------------------------
If possible ALL method's params MUST be final

Wrong:

```java
        private String myMethod(String param) {
            this.myStringField = "Hey!";
        }
```

Right:

```java
        private String myMethod(final String param) {
            this.myStringField = "Hey!";
        }
```

Abstract types
-------------------------------------------------
Name abstract type as {typeName}Base

Wrong:
    
```java
    public abstract class MyAbstractType {
        ...
    }
```

Right:
    
```java
    public abstract class MyTypeBase {
        ...
    }
```
    
Accessors: use Lombok!
-------------------------------------------------
By genral means DO NOT write accessor methods by hand: use lombok

Wrong:

```java
    public class MyType {
        private String _myField;
        
        public String getMyField() {
            return _myField;
        }
        public void setMyField(final String val) {
            _myField = val;
        }
    }
```

Right:
    
```java
    @Accessors(prefix="_")
    public class MyType {
        @Getter @Setter private String _myField;
    } 
```

Use interfaces; avoid concrete implementations
-------------------------------------------------
When defining a field or a method NEVER use a concrete implementation, 
use an interface if possible

Wrong:

```java
    public class MyType {
        private final ArrayList<String> _myCol;
        
        public MyType() {
            _myCol = new ArrayList<String>();
        }
    }
```
    
Right:

```java
    public class MyType {
        private final Collection<String> _myCol;
        
        public MyType() {
            _myCol = new ArrayList<String>();
        }
    }
```

Use functional style programming if possible
-------------------------------------------------
Avoid traditional `for`, `while`... loops if possible: *use java's stream API* (_or Guava_)

Avoid checked exceptions
-------------------------------------------------
If possible DO NOT use or create checked exceptions

ie: DO NOT create exception types like

```java
    public class MyException
         extends Exception {
        ...        
    }
    public class MyType {
        public void myMethod() throws MyException {
            ...
        }
    }
```

If possible use UNCHECKED exceptions instead:

```java
    public class MyUncheckedExceptions
         extends RuntimeException {
        ...
    }
    public class MyType {
        public void myMethod() {        // no throws clause
            ...
        }
    }
```
    
... BUT it's better to avoid creating custom exception types and reuse common types like
`IllegalArgumentException`, `UnsupportedOperationException`, etc
