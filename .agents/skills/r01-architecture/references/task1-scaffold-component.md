# Task 1 — Create a Component's Maven Projects and Base Classes (API and/or CORE)

Use this workflow when scaffolding a new component `{component id}` inside system `{system id}` (domain `{domain id}`).

> **Fast path:** `scripts/scaffold_component.py` does every step of this playbook (and the component part of Task 4: test base classes, Maven registration, db yml and CORE config), from the reference implementation. Use it and come back to this playbook only for what it doesn't cover (a component that doesn't follow the reference layout) or to check what it generated.

## Step 1.1: Register System AppCodes & Model Package in `{domain id}-common`
In `ogovCommonAPIClasses` (`{domain id}.common.api.OG00AppCodes`):
1. Register the component's `.api.model` package in `MODEL_JAVA_PACKAGES`:
   ```java
   JavaPackage.named("{domain id}.{system id}.{component id}.api.model")
   ```
2. Ensure the system's API and CORE app codes exist:
   ```java
   public static final String {SYSTEM}_STR = "{system id}";
   public static final String {SYSTEM}_API_APPCODE_STR = "{apiAppCode_lower}";
   public static final ClientApiAppCode {SYSTEM}_API_APPCODE = ClientApiAppCode.forId({SYSTEM}_API_APPCODE_STR);
   public static final String {SYSTEM}_CORE_APPCODE_STR = "{coreAppCode_lower}";
   public static final CoreAppCode {SYSTEM}_CORE_APPCODE = CoreAppCode.forId({SYSTEM}_CORE_APPCODE_STR);
   ```

## Step 1.2: Scaffold the `-api` Multi-Module Maven Project
Create directory `{domain BAI}-{domain id}-{system id}-{component id}-api/` (e.g. `e80a177-ogov-communic-media-register-api`) with:
- **`.gitignore`** (same as the sample's) and a **`README.md`** adapted to the project: what the component is, its Maven modules, its app codes and runtime FQN, and how to build it. Don't copy the sample's GitLab template README.
- **No IDE or CI files**: don't create `.project`, `.classpath`, `.settings/` or `.gitlab-ci.yml` (nor copy `bin/`, `target/`, `.flattened-pom.xml`). Only `.gitignore`, `pom.xml` files, `README.md` and what goes under `src/`.
- **Parent `pom.xml`**:
  - `groupId`: `{domain id}.{system id}.{component id}.api`
  - `artifactId` & `name`: `{domainId}{SystemId}{ComponentId}API`
  - `version`: `${revision}` (`<packaging>pom</packaging>`)
  - `<modules>`:
    - `{domainId}{SystemId}{ComponentId}APIModelClasses`
    - `{domainId}{SystemId}{ComponentId}APIClasses`
    - `{domainId}{SystemId}{ComponentId}APIBootstrapGuiceClasses`
    - `{domainId}{SystemId}{ComponentId}APIBootstrapSpringClasses`
- **Child `pom.xml` files** (`<packaging>jar</packaging>`, parent `{domainId}{SystemId}{ComponentId}API`), with dependencies as shown in Section 3.

## Step 1.3: Create Base Classes & Interfaces in `-api`
1. **In `{domainId}{SystemId}{ComponentId}APIModelClasses`**:
   - **Package `{domain id}.{system id}.{component id}.api.model.base`**:
     - `{apiAppCode}Is{ComponentId}ModelObject` (extends `OG00IsModelObject`)
     - `{apiAppCode}Is{ComponentId}PersistableObject<O extends {apiAppCode}Is{ComponentId}PersistableObjectOID>` (extends `OG00IsPersistableObject<O>`, `{apiAppCode}Is{ComponentId}ModelObject`)
     - `{apiAppCode}Is{ComponentId}PersistableObjectWithID<O extends {apiAppCode}Is{ComponentId}PersistableObjectOID,I extends {apiAppCode}Is{ComponentId}PersistableObjectID<O>>` (extends `OG00IsPersistableObjectWithID<O,I>`, `{apiAppCode}Is{ComponentId}PersistableObject<O>`)
     - `{apiAppCode}{ComponentId}BaseOIDs`:
       ```java
       @NoArgsConstructor(access = AccessLevel.PRIVATE)
       public abstract class {apiAppCode}{ComponentId}BaseOIDs {
       /////////////////////////////////////////////////////////////////////////////////////////
       //  BASE INTERFACES
       /////////////////////////////////////////////////////////////////////////////////////////
           public interface {apiAppCode}Is{ComponentId}ObjectOID
                    extends OG00IsObjectOID {
               // just extend
           }
           public interface {apiAppCode}Is{ComponentId}PersistableObjectOID
                    extends OG00IsPersistableObjectOID,
                            {apiAppCode}Is{ComponentId}ObjectOID {
               // marker interface
           }
       }
       ```
     - `{apiAppCode}{ComponentId}BaseIDs`:
       ```java
       @NoArgsConstructor(access = AccessLevel.PRIVATE)
       public abstract class {apiAppCode}{ComponentId}BaseIDs {
       /////////////////////////////////////////////////////////////////////////////////////////
       //  BASE INTERFACES
       /////////////////////////////////////////////////////////////////////////////////////////
           public interface {apiAppCode}Is{ComponentId}ObjectID
                    extends OG00IsObjectID {
               // just extends
           }
           public interface {apiAppCode}Is{ComponentId}PersistableObjectID<O extends {apiAppCode}Is{ComponentId}PersistableObjectOID>
                    extends OG00IsPersistableObjectID<O>,
                            {apiAppCode}Is{ComponentId}ObjectID {
               /* a marker interface */
           }
       }
       ```
     - `{apiAppCode}{ComponentId}PersistableObjectBase<O,SELF_TYPE>` (extends `PersistableModelObjectBase<O,SELF_TYPE>`, implements `{apiAppCode}Is{ComponentId}PersistableObject<O>`, `SelfValidates<SELF_TYPE>`, delegates `validate()` to `{apiAppCode}{ComponentId}ObjectsValidators.createObjectValidatorBase()`).
     - `{apiAppCode}{ComponentId}PersistableObjectWithIDBase<O,I,SELF_TYPE>` (`@Accessors(prefix="_")`, extends `PersistableModelObjectWithIDBase<O,I,SELF_TYPE>`, implements `{apiAppCode}Is{ComponentId}PersistableObjectWithID<O,I>`, `SelfValidates<SELF_TYPE>`, delegates `validate()` to `{apiAppCode}{ComponentId}ObjectsValidators.createObjectWithIDValidatorBase()`).
   - **Package `{domain id}.{system id}.{component id}.api.model`**:
     - `{apiAppCode}{ComponentId}ObjectsValidators` (provides `createObjectValidatorBase()` and `createObjectWithIDValidatorBase()` delegating to `OG00CommonObjectValidators`).
   - **Package `{domain id}.{system id}.{component id}.api.model.oids`**:
     - Empty holder classes `{apiAppCode}{ComponentId}OIDs`, `{apiAppCode}{ComponentId}IDs`, `{apiAppCode}{ComponentId}Refs` annotated with `@NoArgsConstructor(access = AccessLevel.PRIVATE)`.
   - **Package `{domain id}.{system id}.{component id}.api.model.summaries.base`**:
     - `{apiAppCode}IsSummarized{ComponentId}Object<M extends {apiAppCode}Is{ComponentId}ModelObject>` (extends `OG00IsSummarizedObject<M>`)
     - `{apiAppCode}IsSummarized{ComponentId}PersistableObject<O,M>` (extends `OG00IsSummarizedPersistableObject<O,M>`, `{apiAppCode}IsSummarized{ComponentId}Object<M>`, `HasTrackableFacet`)
     - `{apiAppCode}Summarized{ComponentId}PersistableObjectBase<O,M,SELF_TYPE>` (extends `OG00SummarizedPersistableObjectBase<O,M,SELF_TYPE>`, implements `{apiAppCode}IsSummarized{ComponentId}PersistableObject<O,M>`)

2. **In `{domainId}{SystemId}{ComponentId}APIClasses`**:
   - **Package `{domain id}.{system id}.{component id}.api`**:
     - `{apiAppCode}AppCodesFor{ComponentId}`:
       ```java
       @NoArgsConstructor(access = AccessLevel.PROTECTED)
       public abstract class {apiAppCode}AppCodesFor{ComponentId} {
       /////////////////////////////////////////////////////////////////////////////////////////
       //  COMPONENT
       /////////////////////////////////////////////////////////////////////////////////////////
           public static final String {SYSTEM}_{COMPONENT}_STR = "{component id}";
           public static final CoreModule {SYSTEM}_{COMPONENT}_MOD = CoreModule.forId({SYSTEM}_{COMPONENT}_STR);
           public static final String {SYSTEM}_{COMPONENT}_API_FQN = OG00AppCodes.{SYSTEM}_API_APPCODE_STR + "." + {SYSTEM}_{COMPONENT}_STR;
           public static final String {SYSTEM}_{COMPONENT}_CORE_FQN = OG00AppCodes.{SYSTEM}_CORE_APPCODE_STR + "." + {SYSTEM}_{COMPONENT}_STR;
       }
       ```
     - `{apiAppCode}ClientAPIFor{ComponentId}` (extends `ClientAPIImplBase`, `@Singleton @Accessors(prefix="_")`).
   - **Package `{domain id}.{system id}.{component id}.api.interfaces`** (or `.api.interfaces.{persistableobj_lower}`):
     - `{apiAppCode}IsServiceInterfaceFor{ComponentId}` (extends `OG00IsServiceInterface`)
     - `{apiAppCode}IsServiceInterfaceFor{ComponentId}PersistableObject` (extends `{apiAppCode}IsServiceInterfaceFor{ComponentId}`, `OG00IsServiceInterfaceForPersistableObject`)

3. **In `{domainId}{SystemId}{ComponentId}APIBootstrapGuiceClasses`** (`.api.bootstrap.guice`):
   - `{apiAppCode}APIBootstrapGuiceModuleFor{ComponentId}` (extends `ServicesBootstrapClientAPIGuiceModuleBase`, binds `{apiAppCode}ClientAPIFor{ComponentId}` and provides `@Provides @ClientAPIForSystemUser @Singleton`).

4. **In `{domainId}{SystemId}{ComponentId}APIBootstrapSpringClasses`** (`.api.bootstrap.spring`):
   - `{apiAppCode}APISpringBootstrapConfigurationFor{ComponentId}` (`@Configuration`, implements `IsSpringConfiguration`, provides `@Bean public {apiAppCode}ClientAPIFor{ComponentId} clientAPI(ApplicationContext)` using `APISpringBeanFinder` on `{apiAppCode}IsServiceInterfaceFor{ComponentId}.class`).

## Step 1.4: Scaffold the `-core` Multi-Module Maven Project
Create directory `{domain BAI}-{domain id}-{system id}-{component id}-core/` with:
- **`.gitignore`** and an adapted **`README.md`**, as in Step 1.2, and no IDE or CI files.
- **Parent `pom.xml`**:
  - `groupId`: `{domain id}.{system id}.{component id}.core`
  - `artifactId` & `name`: `{domainId}{SystemId}{ComponentId}CORE`
  - `version`: `${revision}` (`<packaging>pom</packaging>`)
  - `<modules>`:
    - `{domainId}{SystemId}{ComponentId}COREDBPersistenceClasses`
    - `{domainId}{SystemId}{ComponentId}COREBootstrapGuiceClasses`
    - `{domainId}{SystemId}{ComponentId}COREBootstrapSpringClasses`
- **Child `pom.xml` files** (`<packaging>jar</packaging>`, parent `{domainId}{SystemId}{ComponentId}CORE`), with dependencies as shown in Section 3.

## Step 1.5: Create Base Classes, Interfaces & Resources in `-core`
1. **In `{domainId}{SystemId}{ComponentId}COREDBPersistenceClasses`**:
   - `.core.db.services.{coreAppCode}IsCOREPersistenceServiceFor{ComponentId}` (extends `OG01IsCOREService`)
   - `.core.db.entities.{coreAppCode}IsDBEntityFor{ComponentId}PersistableObj` (extends `OG01IsDBEntity`)
   - `.core.db.summarizers`:
     - `{coreAppCode}SummarizersInterfacesFor{ComponentId}PersistaleObjects` (defines base `{coreAppCode}Is{ComponentId}PersistableObjSummarizerService<O,M,S>`)
     - `{coreAppCode}SummarizerServiceFor{ComponentId}PersistableObjBase<O,M,S>` (extends `OG01SummarizerServiceBase<O,M,S>`)
     - `{coreAppCode}Summarizable{ComponentId}PersistableObjType` (enum implementing `SummarizableObjectType`)
     - `{coreAppCode}Summarizable{ComponentId}PersistableObjVisitors`
     - `{coreAppCode}SummarizersFor{ComponentId}PersistableObjs`
   - `src/main/resources/META-INF/persistence.xml` & `src/main/resources/META-INF/jpa/{coreAppCode_lower}.{componentId_lower}.persistence.xml`:
     - Defines `<persistence-unit name="persistenceUnit.{coreAppCode_lower}.{componentId_lower}" transaction-type="RESOURCE_LOCAL">` with `org.hibernate.jpa.HibernatePersistenceProvider` and `<exclude-unlisted-classes>true</exclude-unlisted-classes>`.
2. **In `{domainId}{SystemId}{ComponentId}COREBootstrapGuiceClasses`** (`.core.bootstrap.guice`):
   - `{coreAppCode}COREBootstrapGuiceConfigBuilderFor{ComponentId}`
   - `{coreAppCode}COREPersistenceBootstrapGuiceModuleFor{ComponentId}` (extends `ServicesBootstrapCoreDBPersistenceGuiceModuleBase`)
   - `{coreAppCode}DBGuiceModuleFor{ComponentId}` (extends `DBPersistenceGuiceModuleBase`)
   - `{coreAppCode}SearchGuiceModuleFor{ComponentId}` (extends `SearchPersistenceGuiceModuleBase`, implements `DBSearchPersistenceGuiceModule`)
3. **In `{domainId}{SystemId}{ComponentId}COREBootstrapSpringClasses`** (`.core.bootstrap.spring`):
   - `{coreAppCode}CORESpringBootstrapConfigurationFor{ComponentId}` (`@Configuration`, `@CoreSpringComponentScan`, implements `IsSpringConfiguration`, defines `@Bean @ServicesCoreConfigFor({apiAppCode}AppCodesFor{ComponentId}.{SYSTEM}_{COMPONENT}_CORE_FQN) ServicesCoreModuleConfig`)
   - `{coreAppCode}CORESpringCoreBootstrapPersistenceModulePostProcessorFor{ComponentId}` (`@Component`, extends `CoreSpringBootstrapPersistenceModulePostProcessor` passing `{coreAppCode}IsDBEntityFor{ComponentId}PersistableObj.class` and `{coreAppCode}IsCOREPersistenceServiceFor{ComponentId}.class`).

