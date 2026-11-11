# Task 4 — Given a Persistable Model Object with API & CORE, Create its Tests

Use this workflow to create the persistence tests of `{apiAppCode}{PersistableObj}` in the domain test project `{domain BAI}-{domain id}-test` (e.g. `ogov-test/e80a180-ogov-test`). They are **integration tests**: they boot the component through Guice or Spring and go through the Client API down to the real database, so they check that the API, the CORE and their bootstrap wiring fit together.

> **Fast path:** `scripts/add_persistable_object.py --layers tests` does Steps 4.2–4.4 for one object (the component's classes, registrations and config come from `scaffold_component.py`), from the reference implementation. Use it and come back to this playbook only for what it doesn't cover (custom finder blocks, objects without a `_name` field get a test without name checks) or to check what it generated.

The test project has three Maven modules:

| Module | Source folder | Package | What goes there |
| :--- | :--- | :--- | :--- |
| `{domainId}TestCommonClasses` | `src/main/java` | `{domain id}.{system id}.{component id}.test.persistence` | Container-agnostic test logic: mock factory + persistence test |
| `{domainId}TestGuiceClasses` | `src/test/java` | `{domain id}.{system id}.{component id}.test.guice.persistence` | JUnit tests that boot the component with Guice |
| `{domainId}TestSpringClasses` | `src/test/java` + `src/test/resources` | `{domain id}.{system id}.{component id}.test.spring.persistence` | JUnit tests that boot the component with Spring Boot |

The test logic is written once in `TestCommonClasses` and run twice, from Guice and from Spring. All classes take the domain test app code `{testAppCode}` (`OG99`).

Reference implementation: the `samplecomp` classes in each module (`OG99…SamplePersistableObj…` and `OG99…SampleComp…`). Open the counterpart before writing each class.

## Step 4.0: Check the prerequisites
- The object's API and CORE exist (Tasks 2 and 3). Read the CRUD and Find service interfaces: the test uses the CRUD operations and each custom finder declared there.
- The CORE configuration of `{coreAppCode}` exists in `{domain id}-config`. The Guice test loads it through `XMLProperties` (`forApp({SYSTEM}_CORE_APPCODE)`), and without it the bootstrap fails. Look for:
  - `…-config/{domainId}COREConfig/src/main/config/{coreAppCode_lower}/{coreAppCode_lower}.services.properties.xml` and `{coreAppCode_lower}.dbpersistence.properties.xml`
  - `…/{coreAppCode_lower}/components/{coreAppCode_lower}.services.xml` and `{coreAppCode_lower}.{componentId_lower}.dbpersistence.xml`
  - `…-config-byenv/{domainId}COREConfigByEnv/src/main/config/default/{coreAppCode_lower}/{coreAppCode_lower}.dbpersistence.properties.xml`

  If they're missing, create them by copying the `og91` files and replacing the app code and component. Tell the user, because they hold the database connection.
- Learn what's already there for the component. The base and bootstrap classes in Steps 4.3 and 4.4 are **one per component**: if the component already has tests for another object, extend those classes instead of creating new ones.

## Step 4.1: Register the component in the test Maven project (once per component)
1. Parent `pom.xml` (`{domainId}Test`): add the version properties next to the `samplecomp` ones, following their naming (`{domain id}.{system id}.{component id}.api.version` and `….core.version`). Add them in every profile that overrides those properties too.
2. `{domainId}TestCommonClasses/pom.xml`: add a dependency on `{domain id}.{system id}.{component id}.api:{domainId}{SystemId}{ComponentId}APIClasses`.
3. `{domainId}TestGuiceClasses/pom.xml`: add a dependency on `{domain id}.{system id}.{component id}.core:{domainId}{SystemId}{ComponentId}COREBootstrapGuiceClasses`.
4. `{domainId}TestSpringClasses/pom.xml`: add a dependency on `{domain id}.{system id}.{component id}.core:{domainId}{SystemId}{ComponentId}COREBootstrapSpringClasses`.

## Step 4.2: Create the common test logic (`{domainId}TestCommonClasses`)
1. `{testAppCode}Mock{PersistableObj}Factory` implements `r01f.patterns.Factory<{apiAppCode}{PersistableObj}>`. Its `create()` returns a new object filled with recognisable sample values: the business ID (`{apiAppCode}{PersistableObj}ID.forId("…")`) if the object has one, plus every field the finders filter on. Finders are tested with those same values.
2. `{testAppCode}TestFor{PersistableObj}Persistence` receives `{apiAppCode}ClientAPIFor{ComponentId}` in its constructor and has one `public void doTest{PersistableObj}Persistence()` that runs the whole life cycle through the Client API:
   ```java
   public void doTest{PersistableObj}Persistence() {
       // create
       {apiAppCode}{PersistableObj} obj = _{persistableObj}Factory.create();
       {apiAppCode}{PersistableObj} persistedObj = _{componentId}API.getFor{PersistableObj}CRUD()
                                                                     .create(obj);
       // load
       {apiAppCode}{PersistableObj} retrievedObj = _{componentId}API.getFor{PersistableObj}CRUD()
                                                                      .load(persistedObj.getOid());
       Assertions.assertNotNull(retrievedObj);

       // one block per custom finder, using the factory's values
       Collection<{apiAppCode}{PersistableObj}> foundObjs = _{componentId}API.getFor{PersistableObj}Find()
                                                                               .findByNameExact("a name");
       Assertions.assertTrue(foundObjs.size() > 0);

       // update
       retrievedObj.setName("UpdatedName");
       {apiAppCode}{PersistableObj} updatedObj = _{componentId}API.getFor{PersistableObj}CRUD()
                                                                    .update(retrievedObj);
       // delete: always, so reruns start from a clean table
       _{componentId}API.getFor{PersistableObj}CRUD()
                        .delete(updatedObj.getOid());
   }
   ```
   The sample stops at CRUD and finders. Add Count, Search or Summarize checks only if the user asks, using the methods their Client API delegates expose.

## Step 4.3: Create the Guice tests (`{domainId}TestGuiceClasses`)
1. *(Once per component)* `{testAppCode}TestGuiceFor{ComponentId}Base`: package-private `abstract class` extending `TestAPIGuiceBase`. Copy `OG99TestGuiceForSampleCompBase`:
   - `@BeforeAll` loads `XMLProperties`, builds the component's modules with `{coreAppCode}COREBootstrapGuiceConfigBuilderFor{ComponentId}.buildBootstrapGuiceModulesUsing(xmlProps,securityContextProvider)`, adds the core event bus module and `OG00CommonAPIBootstrapGuiceModule`, and calls `_setUpGuiceModulesBeforeClass(...)`.
   - `@AfterAll` calls `_tearDownAfterClass()`.
   - It has a static `get{ComponentId}Api()` (`TestAPIGuiceBase.getClientApi({apiAppCode}ClientAPIFor{ComponentId}.class)`) and `getSecurityContext()`.

   For each persistable object, add `get{PersistableObj}Test()` returning `new {testAppCode}TestFor{PersistableObj}Persistence(get{ComponentId}Api())`.
2. *(Once per component)* `{testAppCode}TestGuiceFor{ComponentId}PersistableObjects` extends the base. It mirrors the Spring test class (Step 4.4) and person's `OG99TestGuiceForPersonPersistableObjects`: one class per component, with one `@Test` per persistable object. If it already exists, add the new object's test method to it:
   ```java
   @Test @Tag("persistence")
   public void test{PersistableObj}Persistence() {
       this.get{PersistableObj}Test()
           .doTest{PersistableObj}Persistence();
   }
   ```
   Keep the Javadoc with the AspectJ `-javaagent` JVM arguments that the sample has: the tests need it to run.

## Step 4.4: Create the Spring tests (`{domainId}TestSpringClasses`)
1. *(Once per component)* `{testAppCode}TestSpringBootstrapFor{ComponentId}`: `@SpringBootFabricApplication`, `@ComponentScan(basePackageClasses = {…})` with `OG00CommonAPIBootstrapSpringConfiguration`, `OG01CommonCOREBootstrapSpringConfiguration`, `{apiAppCode}APISpringBootstrapConfigurationFor{ComponentId}` and `{coreAppCode}CORESpringBootstrapConfigurationFor{ComponentId}`. Add one `@Bean` per persistable object that returns its `{testAppCode}TestFor{PersistableObj}Persistence` built from the injected `{apiAppCode}ClientAPIFor{ComponentId}`.
2. *(Once per component)* `{testAppCode}TestSpringFor{ComponentId}PersistenceBase`: package-private `abstract class` extending `TestAPISpringBase`. It has `get{ComponentId}ClientApi()` and one `protected {testAppCode}TestFor{PersistableObj}Persistence _get{PersistableObj}Test()` per object, using `_context.getBean(...)`.
3. *(Once per component)* `{testAppCode}TestSpringFor{ComponentId}PersistableObjects`: `@SpringBootTest(classes = {testAppCode}TestSpringBootstrapFor{ComponentId}.class)`, `@ActiveProfiles("test")`, extends the base. Add one `@Test @Tag("persistence") public void test{PersistableObj}Persistence()` per object, calling `_get{PersistableObj}Test().doTest{PersistableObj}Persistence()`.
4. *(Once per component)* Database config in `src/test/resources`:
   - `config/default/{system id}-{componentId_lower}-database.yml`: copy `samplesys-samplecomp-database.yml` and change the root key to `db.{componentId_lower}`.
   - `application.yml`: add `- optional:classpath:config/${env:default}/{system id}-{componentId_lower}-database.yml` to `spring.config.import`.

## Step 4.5: Wrap up
- Format the Java files you wrote with `r01-java-formatter`.
- If you created or changed config in `{domain id}-config`, tell the user to refresh and rebuild `{domainId}COREConfig` and `{domainId}COREConfigByEnv` before running the tests. If they don't, the tests fail in `@BeforeAll` with `XMLPropertiesException … {coreAppCode_lower}/{coreAppCode_lower}.{componentId_lower}.dbpersistence.properties.xml (CLASSPATH) was NOT found`, even though the files are in `src/main/config`.
- Don't run the tests unless the user asks. They need the database from the config, and the JVM argument `-javaagent:…/aspectjweaver.jar`. In the report, say how to run them: the JUnit classes `{testAppCode}TestGuiceFor{ComponentId}PersistableObjects` and `{testAppCode}TestSpringFor{ComponentId}PersistableObjects`, tag `persistence`.

