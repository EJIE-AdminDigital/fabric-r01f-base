---
name: r01-architecture
description: >-
  Generates and changes code following the FABRIC (r01f) layered architecture
  of the OGOV projects (modules, packages, naming). Use it to: (1) scaffold the -api / -core projects of a component ("crea toda
  la estructura de proyectos para el API / CORE siendo {domain id}=...");
  (2) create the service interfaces, OIDs/IDs/Refs and Client API of a
  hand-written persistable object ("crea los service interfaces y API para el
  persistable object OG10MediaGroup"); (3) create its CORE layers ("crea las
  layers del CORE del persistable object X"); (4) create its Guice/Spring
  tests ("crea los tests del persistable object X"); (5) add or remove a
  custom finder in every layer ("crea un finder findByMediaGroup en
  OG10Media", "añade un findByName"); (6) add, remove or rename a field of an
  object across layers ("añade el campo _email a OG10MediaGroup", "elimina el
  campo _webSite en todas las capas"). Also for where a FABRIC class goes or
  how it is named. Not for renaming a whole object
  (r01-rename-persistable-object) nor for a system's security component
  (r01-architecture-security).
---
# FABRIC (r01f) Java API / CORE Layered Architecture

FABRIC applications expose **CORE** logic (persistence or business logic) through a **Client API** and an optional **REST** layer. Each layer is its own Git repository and multi-module Maven project, and every class has a fixed module, package and name derived from a handful of identifiers. Getting those right matters more than anything else: Guice/Spring bootstrap, JPA persistence units and the `@ExposedServiceInterface` discovery all locate classes by these conventions, so a misplaced or misnamed class compiles but fails at runtime.

## How to work

The skill serves six kinds of request. Identify which one you've got, gather its inputs, then follow its playbook.

### Generators first (request types 1–4)

Scaffolding a component and adding a persistable object (its API, CORE and tests) are done by the bundled scripts, which copy the reference implementation replacing the identifiers, insert the object in the component classes that list every object, register what the runtime looks up and format the result. Use them instead of writing the classes one by one; the playbooks remain the reference for what each class is, and for what the scripts don't do.

```bash
python scripts/scaffold_component.py --system communic --component media-register [--domain-bai e80a177] [--api-app-code OG10 --core-app-code OG11] [--description "Registro de medios"] [--only api|core|tests] --dry-run
python scripts/add_persistable_object.py --system communic --component media-register --object MediaGroup [--layers api,core,tests] --dry-run
```

- Run with `--dry-run` first and check the first output line (system, component, BAI, app codes): those values go into every file. Values not given are detected (app codes from `OG00AppCodes`, BAI and system casing from the system's existing projects).
- **`scaffold_component.py`** (request type 1, plus the component part of type 4): the `-api` / `-core` projects with their README, the model package (and, when `--api-app-code` / `--core-app-code` are given for an unregistered system, its app codes) in `OG00AppCodes`, the component's empty Guice / Spring test classes, its Spring db yml, the test project's Maven dependencies and `application.yml` import, and the CORE config in `ogov-config` (copied from `og91`; the system's files only when missing). An existing project folder is not touched without `--force`.
- **`add_persistable_object.py`** (request types 2, 3 and 4): needs the hand-written model class (`{apiAppCode}{Object}` in `.api.model`, extending `…PersistableObjectWithIDBase`). Generates the full set of operations (CRUD, FIND, COUNT, SEARCH, SUMMARIZE) and inserts the object next to the existing ones in the Client API, OIDs/IDs/Refs, summarizers, the other objects' Summarize services, bootstrap modules, `persistence.xml` and test classes, keeping hand-made code. Running it twice changes nothing; existing files are kept unless `--force`.
- **Not generated, follow the playbooks:** custom finders ([`task5`](references/task5-custom-finder.md); the sample's `findByNameExact` is left out), objects without business ID (`…PersistableObjectBase`), a subset of the operations, and components that don't follow the reference layout. Tell the user when you fall back to a playbook and why.
- The report of each script lists what it created and modified. Use it for the report to the user and the projects to refresh (see "For every request").

### Request types

**1. Scaffold a system / component** → [`references/task1-scaffold-component.md`](references/task1-scaffold-component.md)
> *"Crea toda la estructura de proyectos para el API / CORE siendo {domain id}=ogov {domain bai}=e80a177 {system id}=communic {component id}=media-register"*

- **Given:** `{domain id}`, `{domain bai}`, `{system id}`, `{component id}` (a `{system bai}` may also be given, but it is not used in folder names). They may also say only API or only CORE; otherwise create both.
- **Repo folder prefix:** the component's `-api` / `-core` folders are prefixed with the `{domain bai}`: `{domain bai}-{domain id}-{system id}-{component id}-api` (e.g. `e80a177-ogov-communic-media-register-api`). If the `{domain bai}` given doesn't match the system's BAI in a diagram or existing folders, ask.
- **Files to generate:** only `.gitignore`, `pom.xml` (parent and children), a `README.md` adapted to the project (what it is, its modules, app codes, how to build), and the Java sources and resources under `src/`. **Don't** copy IDE or CI files from the sample: no `.project`, `.classpath`, `.settings/`, `.gitlab-ci.yml`, `bin/`, `target/`, `.flattened-pom.xml`, and not the sample's GitLab template README.
- **Derive:** every name, using the casing matrix in Section 1. Identifiers can contain hyphens: `media-register` is `media-register` in directory names, `mediaregister` in packages and runtime FQNs, and `MediaRegister` in Maven artifacts and class names.
- **App codes:** look for the system in `OG00AppCodes` (`ogov-common/.../ogov/common/api/OG00AppCodes.java`). If the system isn't registered and the user didn't give its codes, **ask before generating anything**: class names, packages and config all depend on them. Propose the next free pair following the existing pattern (API = even code `OGx0`, CORE = the next odd code `OGx1`; e.g. `OG90`/`OG91` for samplesys).
- **New system:** if the system's folder (`{domain id}-{system id}/`, e.g. `ogov-communic`) doesn't exist yet, create it next to `ogov-samplesys`, and register the system's app codes in `OG00AppCodes` (Step 1.1).

**2. Create the API of a hand-written persistable object** → [`references/task2-api-layer.md`](references/task2-api-layer.md)
> *"Crea los service interfaces y API para el persistable object OG10MediaGroup"*

- **Given:** only the model class name. The class already exists, written by hand, so find it (search for `OG10MediaGroup.java`).
- **Derive from its location and code:** domain, system and component from its package (`{domain id}.{system id}.{component id}.api.model`); `{apiAppCode}` from its prefix; whether it has a business ID from its base class (`…PersistableObjectWithIDBase` = with ID, `…PersistableObjectBase` = without); and the fields available for finders.
- **Operations:** unless the user lists them, create the full set the sample has (CRUD, FIND, COUNT, SEARCH, SUMMARIZE), and say so in the report. Add custom finders only if the user asks for them; the code of each layer of a finder is in [`task5-custom-finder.md`](references/task5-custom-finder.md).
- **Don't rewrite the model object.** It's the user's hand-written code. Create its companions (OID/ID/Ref, search filter, summary) around it. If it's missing something the architecture needs (`@Accessors(prefix="_")`, `getReference()`, the right base class), make only that minimal change and list it in the report.
- **Prerequisite:** the component must already be scaffolded (request type 1). If its base classes (`{apiAppCode}{ComponentId}BaseOIDs`, `{apiAppCode}ClientAPIFor{ComponentId}`, …) are missing, stop and tell the user rather than improvising them.

**3. Create the CORE of a persistable object from its API** → [`references/task3-core-layer.md`](references/task3-core-layer.md)
> *"Crea las layers del CORE correspondiente al persistable object OG10MediaGroup y los service interfaces definidos en el API"*

- **Given:** only the model class name.
- **Derive from the API, which is the contract:** read the service interfaces in `.api.interfaces.{persistableobj_lower}`. Create CORE classes only for the operations that have an interface. Implement every custom method declared there (e.g. `findByXxx`) through all three tiers: Tier 1 delegates, Tier 2 validates and calls Tier 3, and Tier 3 runs a `@NamedQuery`. Give the DB entity an indexed `@Column` for each field those queries filter on.
- **`{coreAppCode}`** comes from `OG00AppCodes` or from the component's existing CORE classes.
- **Prerequisites:** the API layer (request type 2) and the component's `-core` scaffold (request type 1) must exist. If either is missing, stop and say which.

**4. Create the tests of a persistable object** → [`references/task4-tests.md`](references/task4-tests.md)
> *"Crea los tests del persistable object OG10MediaGroup"*

- **Given:** only the model class name.
- **Derive:** the component and app codes as in request types 2–3, and what to test from its CRUD and Find service interfaces (CRUD life cycle plus every custom finder).
- **What gets created:** container-agnostic test logic in `{domainId}TestCommonClasses`, run from both Guice (`{domainId}TestGuiceClasses`) and Spring (`{domainId}TestSpringClasses`) in `{domain id}-test`, plus the Maven dependencies and Spring DB config. These are integration tests against the real database, not unit tests with mocks.
- **Prerequisites:** API and CORE (request types 2–3), and the component's CORE config in `{domain id}-config`. If the config is missing, create it as the playbook says and tell the user.
- **Don't run the tests** unless asked. They need the database and the AspectJ agent.

**5. Add or remove a custom finder of an existing object** → [`references/task5-custom-finder.md`](references/task5-custom-finder.md)
> *"Crea un finder findByMediaGroup en OG10Media para obtener todos los medios de un grupo"*

- **Given:** the model class and what to filter on. The finder name and matching rule may be implicit; default to `findBy{Field}` with exact equality and say so.
- **Derive:** the component and app codes as in request types 2–3; the parameter types from the model's fields (a reference to another object is filtered by its OID).
- **What gets changed:** the same eight places for every finder: Find interface and Client API Find delegate (API); entity column, index and `@NamedQuery`, transfer delegate, Find DAO, Tier 2 and Tier 1 (CORE); a test block. The playbook has the code of each layer.
- **Prerequisites:** the object's API, CORE and tests (request types 2–4). Don't add a finder that already exists in the base interfaces (`findAll`, `findByCreateDate`…, or CRUD's `load` by OID).

**6. Add, remove or rename a field of an existing object** → [`references/task6-model-field.md`](references/task6-model-field.md)
> *"Añade un campo _email (String) a OG10MediaGroup"*, *"Elimina el campo _webSite de OG10Media en todas las capas"*

- **Given:** the model class and the field (and its type when adding).
- **Key point:** the whole model is stored as XML in the entity's `DESCRIPTOR` column, so most fields need no CORE change. A DB `@Column` (and its copy in the transfer delegate) exists only for fields a custom finder filters on. Find out which layers the field really reaches before editing.
- **Don't rewrite the model object.** Change only the field and what goes with it (imports, an enum used only by it, a helper method that reads it).
- **Stored data:** removing or renaming a field affects rows already stored. Say what happens to them; when renaming, default to keeping the old `@MarshallField(as="…")` name.

### For every request

1. **Mirror the reference implementation.** `sampleComp` / `SamplePersistableObj` in `presencia-web/ogov-pocs/ogov-samplesys/` (relative to the workspace root, the folder that contains `admindigital-common` and `presencia-web`) is a working example of every class the playbooks describe (paths in Section 6). Before creating a class, open its `sampleComp` counterpart and follow its imports, annotations, constructors and generics, substituting identifiers. The playbooks say *what* to create; the sample shows *exactly how*. It is also the template the generators copy, so it is kept free of copy-paste leftovers.
2. **Keep the existing names, even misspelled ones.** Some framework types are spelled oddly and must be used as they are, e.g. `OG00IsCRUDServicesForPersistablObjectWithID` and `{coreAppCode}SummarizersInterfacesFor{ComponentId}PersistaleObjects`. Don't "fix" them: other code depends on those exact names.
3. **Register what you create.** New DB entities go in both `persistence.xml` files, new searchers and summarizers in the Guice and Spring bootstrap modules, new delegates in `{apiAppCode}ClientAPIFor{ComponentId}`, and new model packages in `OG00AppCodes.MODEL_JAVA_PACKAGES`. A class that is not registered is silently ignored at runtime.
4. **Format what you wrote.** Once the Java files are written, apply the [`r01-java-formatter`](../r01-java-formatter/SKILL.md) skill (if your tool doesn't load it automatically, read that `SKILL.md` and follow it) to the lines you created or changed (house alignment for `extends`/`implements`, parameters, fluent chains and `@NamedQuery` JPQL). Write the code close to that style from the start, as the snippets in the playbooks do, so the formatting pass only adjusts whitespace.
5. **Report back** with the files you created or modified, grouped by Maven module; any change to hand-written code; and anything you couldn't infer and assumed (operations, app codes, finders).
6. **Tell the user which projects to refresh and rebuild.** The files are written outside the IDE, and Eclipse (m2e) only copies resources to `target/classes`, the folder that reaches the classpath, after a refresh (F5) and a build. This matters most for config files under `src/main/config` (`{domain id}-config`), `persistence.xml` and other resources. Until those projects are rebuilt, the new files exist in `src` but the runtime can't find them. List the affected Eclipse projects by name in the report, e.g. `ogovCOREConfig` and `ogovCOREConfigByEnv`.

For a question about where a class goes or how it's named, Sections 1–4 below are enough.

## Code conventions (all layers)

- Every class, interface, record and enum is prefixed with its layer's app code (`{apiAppCode}` in `-api`, `{coreAppCode}` in `-core`, `{testAppCode}` in `-test`).
- Lombok `@Accessors(prefix="_")`. Private fields start with `_` (`private String _name;`), and so do `private`/`protected` methods (`_doSomething()`); public methods don't. `@Getter @Setter` go on the same line as the field: `@Getter @Setter private String _name;`.
- Separate sections inside a class with the 89-slash comment block at column 0:
  ```java
  /////////////////////////////////////////////////////////////////////////////////////////
  //  FIELDS
  /////////////////////////////////////////////////////////////////////////////////////////
  ```
- Parameters and type arguments on a single line are separated by `,` with no space (`<O,M>`, `(securityContext,oid)`).

---

## 1. Hierarchy, Identifiers & Casing Rules

The architecture is organized into three hierarchical levels, built atop domain-wide common libraries (`{domain id}-common`) and FABRIC base libraries (`r01f`):

1. **Domain**: Contains multiple *systems* plus shared domain projects (`{domain id}-common`, `{domain id}-config`, `{domain id}-test`).
   - `{domain BAI}`: Business Application ID (optional in folder names, e.g., `e80a180`)
   - `{domain id}`: Domain identifier (e.g., `ogov`)
2. **System**: Contains multiple *components* (also referred to as *modules* / `CoreModule`).
   - `{system BAI}`: Business Application ID (not used in folder names; component repos take the `{domain BAI}` prefix)
   - `{system id}`: System identifier (lowercase: `samplesys`; PascalCase in Maven artifacts: `SampleSys`)
3. **Component (Module)**: Contains the Git repositories and Maven projects for each architectural layer.
   - `{component id}`: Component identifier (lowercase: `samplecomp`; PascalCase in Maven artifacts and class names: `SampleComp`)

### Application Code Prefixes (`AppCode`)
Each system/layer uses a short application code prefix:
- `{CommonApiAppCode}` / `{CommonCoreAppCode}`: Domain-common prefixes (e.g., `OG00` for common API, `OG01` for common CORE).
- `{apiAppCode}`: Prefix for all Java classes/interfaces in the system's **API** layer (e.g., `OG90` in classes, `"og90"` in runtime IDs/config).
- `{coreAppCode}`: Prefix for all Java classes in the system's **CORE** layer (e.g., `OG91` in classes, `"og91"` in runtime IDs/config).
- `{testAppCode}`: Prefix for test classes in `{domain id}-test` (e.g., `OG99`).

### Casing Matrix Across Contexts
| Context | Casing Rule | Example (`domain=ogov`, `system=sampleSys`, `component=sampleComp`) |
| :--- | :--- | :--- |
| **Git Repo & Directory Names** | `kebab-case` (all lowercase) | `ogov-samplesys/bai-ogov-samplesys-samplecomp-api` |
| **Maven `groupId` & Base Java Package** | `lowercase.dot.separated` | `ogov.samplesys.samplecomp.api` / `ogov.samplesys.samplecomp.core` |
| **Maven `artifactId` & `<module>` Names** | `camelCase` (`{domainId}{SystemId}{ComponentId}...`) | `ogovSampleSysSampleCompAPI` / `ogovSampleSysSampleCompCOREDBPersistenceClasses` |
| **Java Class Prefix** | `UPPERCASE` app code | `OG90...` (API), `OG91...` (CORE), `OG99...` (Test) |
| **Persistable Object Subpackage** | `lowercase` (no separators) | `...samplepersistableobj` |
| **Runtime Component FQN & Config Files** | `lowercase` `{appCode}.{componentId}` | `og90.samplecomp`, `og91.samplecomp` |

---

## 2. Git Repositories & Filesystem Layout

A component is split across separate Git repositories per layer (`-api`, `-core`, and optionally `-rest`):

```text
{domain BAI}-{domain id}/                                      # {domain BAI}- is optional
├── {domain id}-common/
│   ├── {domain BAI}-{domain id}-common-api/                   # Shared domain API base classes (OG00...)
│   ├── {domain BAI}-{domain id}-common-core/                  # Shared domain CORE base classes (OG01...)
│   ├── {comp BAI}-{domain id}-common-{comp id}-api/           # Common component API (e.g., person)
│   └── {comp BAI}-{domain id}-common-{comp id}-core/          # Common component CORE (e.g., person)
├── {domain id}-config/
│   ├── {domain BAI}-{domain id}-config/                       # Base XML properties & logback config
│   └── {domain BAI}-{domain id}-config-byenv/                 # Environment-specific config
├── {domain id}-test/
│   └── {domain BAI}-{domain id}-test/                         # Integration tests (Common, Guice, Spring)
└── {domain id}-{system id}/                                   # e.g., ogov-communic
    ├── {domain BAI}-{domain id}-{system id}-{component id}-api/   # e.g., e80a177-ogov-communic-media-register-api
    ├── {domain BAI}-{domain id}-{system id}-{component id}-core/
    └── {domain BAI}-{domain id}-{system id}-{component id}-rest/
```

---

## 3. Maven Multi-Module Layout & Dependencies

Each layer repository is a multi-module Maven parent project (`<packaging>pom</packaging>`) using `${revision}` and `flatten-maven-plugin`.
> **Important:** Maven parent `artifactId` and child `<module>` names concatenate `{domainId}`, `{SystemId}`, and `{ComponentId}` in **camelCase** (e.g., `ogovSampleSysSampleComp...`).

### API Repository Maven Modules
Parent `groupId`: `{domain id}.{system id}.{component id}.api` | Parent `artifactId`: `{domainId}{SystemId}{ComponentId}API`

```text
{domain BAI}-{domain id}-{system id}-{component id}-api/
├── {domainId}{SystemId}{ComponentId}APIModelClasses           # Depends on: {domainId}CommonAPIModelClasses
├── {domainId}{SystemId}{ComponentId}APIClasses                # Depends on: ...APIModelClasses, {domainId}CommonAPIClasses
├── {domainId}{SystemId}{ComponentId}APIBootstrapGuiceClasses  # Depends on: ...APIClasses, {domainId}CommonAPIBootstrapGuiceClasses
└── {domainId}{SystemId}{ComponentId}APIBootstrapSpringClasses # Depends on: ...APIClasses, {domainId}CommonAPIBootstrapSpringClasses
```

### CORE Repository Maven Modules
Parent `groupId`: `{domain id}.{system id}.{component id}.core` | Parent `artifactId`: `{domainId}{SystemId}{ComponentId}CORE`

```text
{domain BAI}-{domain id}-{system id}-{component id}-core/
├── {domainId}{SystemId}{ComponentId}COREDBPersistenceClasses   # Depends on: ...APIClasses, {domainId}CommonCOREDBPersistenceClasses (or COREBusinessClasses for non-DB business CORE)
├── {domainId}{SystemId}{ComponentId}COREBootstrapGuiceClasses  # Depends on: ...COREDBPersistenceClasses, ...APIBootstrapGuiceClasses, {domainId}CommonCOREBootstrapGuiceClasses
└── {domainId}{SystemId}{ComponentId}COREBootstrapSpringClasses # Depends on: ...COREDBPersistenceClasses, ...APIBootstrapSpringClasses, {domainId}CommonCOREBootstrapSpringClasses
```

---

## 4. Logical Architecture & Layer Responsibilities

### A. Client API Layer (`-api`)
Base package: `{domain id}.{system id}.{component id}.api`

1. **`{domainId}{SystemId}{ComponentId}APIModelClasses` (`.api.model`)**:
   - **Base Model Types (`.api.model.base`)**: Component-level marker interfaces, base OID/ID interfaces, and abstract base classes (`PersistableObjectBase`, `PersistableObjectWithIDBase`).
   - **OIDs, Business IDs & Refs (`.api.model.oids`)**: Holder classes `{apiAppCode}{ComponentId}OIDs`, `{apiAppCode}{ComponentId}IDs`, and `{apiAppCode}{ComponentId}Refs`.
   - **Model Objects & Validators (`.api.model`)**: Persistable POJOs (`{apiAppCode}{PersistableObj}`) and `{apiAppCode}{ComponentId}ObjectsValidators`.
   - **Search Models (`.api.model.search`)**: `{apiAppCode}SearchFilterFor{PersistableObj}` and `{apiAppCode}SearchResultItemFor{PersistableObj}`.
   - **Summary Models (`.api.model.summaries` & `.summaries.base`)**: `{apiAppCode}Summarized{PersistableObj}` and component summary base types.
2. **`{domainId}{SystemId}{ComponentId}APIClasses` (`.api`)**:
   - **AppCodes (`.api`)**: `{apiAppCode}AppCodesFor{ComponentId}`.
   - **Service Interfaces (`.api.interfaces.{persistableobj_lower}`)**: Annotated with `@ExposedServiceInterface`, defining the contract implemented by the CORE layer:
     - `{apiAppCode}CRUDServicesFor{PersistableObj}`
     - `{apiAppCode}FindServicesFor{PersistableObj}`
     - `{apiAppCode}CountServicesFor{PersistableObj}`
     - `{apiAppCode}SearchServicesFor{PersistableObj}`
     - `{apiAppCode}SummarizeServicesFor{PersistableObj}`
   - **Client API, Delegates & RefSolver (`.api` & `.api.{persistableobj_lower}`)**:
     - `{apiAppCode}ClientAPIFor{ComponentId}` (entry point exposing getters for each sub-API delegate and RefSolver).
     - `{apiAppCode}ClientAPIDelegateFor{PersistableObj}<Op>Services` (wraps service proxies, supplies `SecurityContext`, and unwraps results via `.getOrThrow()`).
     - `{apiAppCode}{PersistableObj}RefSolver` (caches bidirectional `OID <-> ID` lookups).
3. **`...APIBootstrapGuiceClasses` & `...APIBootstrapSpringClasses`**:
   - Guice module (`{apiAppCode}APIBootstrapGuiceModuleFor{ComponentId}`) and Spring `@Configuration` (`{apiAppCode}APISpringBootstrapConfigurationFor{ComponentId}`) that wire `{apiAppCode}ClientAPIFor{ComponentId}`.

### B. CORE Layer (`-core`)
Base package: `{domain id}.{system id}.{component id}.core.db` (in `{domainId}{SystemId}{ComponentId}COREDBPersistenceClasses`).

**Crucial Rule:** All three internal tiers of the CORE layer implement the **service interfaces** (`{apiAppCode}<Op>ServicesFor{PersistableObj}`) defined in `APIClasses`:

1. **Tier 1 — Services Impl Layer (`.core.db.services.{persistableobj_lower}`)**:
   - Annotated with `@CoreService @Singleton`.
   - Implements `{apiAppCode}<Op>ServicesFor{PersistableObj}` and `{coreAppCode}IsCOREPersistenceServiceFor{ComponentId}`.
   - Verifies the **security context**, establishes **transactional / EntityManager boundaries**, and holds a `ServiceDelegateProvider` that creates a Tier 2 Delegate per request with `this.getFreshNewEntityManager()` or `this.getFreshNewSearcher()`.
   - **Never** contains business logic; custom methods strictly delegate via `this.forSecurityContext(securityContext).createDelegateAs(...).method(...)`.
2. **Tier 2 — Services Delegate Layer (`.core.db.services.delegates.{persistableobj_lower}`)**:
   - Implements `{apiAppCode}<Op>ServicesFor{PersistableObj}`.
   - Validates input parameters / model objects (`SelfValidates`), orchestrates calls to Tier 3 (DAOs, Searchers, Summarizers) via `this.getServiceImplAs(...)`, and publishes `EventBus` CRUD events.
3. **Tier 3 — Data Access, Search & Summarization Layer**:
   - **DB DAOs (`.core.db.dao.{persistableobj_lower}`)**: `{coreAppCode}DBCRUDFor{PersistableObj}`, `{coreAppCode}DBFindFor{PersistableObj}`, `{coreAppCode}DBCountFor{PersistableObj}` implementing the corresponding API service interfaces.
   - **DB Entities (`.core.db.entities.{persistableobj_lower}`)**: `{coreAppCode}DBEntityFor{PersistableObj}` (JPA `@Entity` extending `OG01DBEntityWithIDBase` or `OG01DBEntityBase`) + `META-INF/jpa/{coreAppCode_lower}.{componentId_lower}.persistence.xml`.
   - **Transformers (`.core.db.transfer.{persistableobj_lower}`)**: `{coreAppCode}ModelObjFromDBEntityTransformerFor{PersistableObj}` and `{coreAppCode}ModelObjToDBEntityTransferDelegateFor{PersistableObj}`.
   - **DB Searchers (`.core.db.search.{persistableobj_lower}`)**: `{coreAppCode}DBSearcherFor{PersistableObj}` (implements `{apiAppCode}SearchServicesFor{PersistableObj}`), `{coreAppCode}DBSearchQueryFor{PersistableObj}`, `{coreAppCode}DBEntityToSearchResultItemTransformerFor{PersistableObj}`, and `{coreAppCode}DBSearcherProvidersFor{ComponentId}PersistableObjs`.
   - **Summarizers (`.core.db.summarizers`)**: `{coreAppCode}SummarizerServiceImplFor{PersistableObj}` and component summarizer visitors/registry.


---

## 5. Playbooks

The step-by-step playbooks live in `references/` (see "How to work" for when to use each one). Tasks 1–4 are automated by `scripts/` (see "Generators first"); their playbooks describe what the scripts generate and cover the cases they don't:
- [`task1-scaffold-component.md`](references/task1-scaffold-component.md): register the AppCodes, scaffold the `-api` / `-core` Maven projects, and create the component's base classes, interfaces and resources.
- [`task2-api-layer.md`](references/task2-api-layer.md): model companions, service interfaces, Client API delegates and RefSolver, and wiring into `ClientAPIFor{ComponentId}`.
- [`task3-core-layer.md`](references/task3-core-layer.md): JPA entity and `persistence.xml`, transformers, Tier 3 / Tier 2 / Tier 1 classes, and the Guice/Spring bootstrap wiring.
- [`task4-tests.md`](references/task4-tests.md): Maven registration in the test project, mock factory and persistence test, Guice and Spring test classes, and the Spring DB config.
- [`task5-custom-finder.md`](references/task5-custom-finder.md): the code of a custom finder in each of its eight places, its variants (string, `LIKE`, enum, boolean, several fields) and how to remove one.
- [`task6-model-field.md`](references/task6-model-field.md): which layers a field reaches, and how to add, remove, rename or retype it.

---

## 6. Reference Example (`sampleSys` / `sampleComp`)

Identifiers: domain `ogov`; system `sampleSys` (`samplesys`, BAI `bai`); component `sampleComp` (`samplecomp`); app codes `OG90` (API), `OG91` (CORE), `OG99` (test); persistable object `SamplePersistableObj`.

All paths are relative to `presencia-web/ogov-pocs/ogov-samplesys/` under the workspace root. If they've moved, search for `OG90SamplePersistableObj.java`.

| Maven module | Source root (`…/src/main/java/ogov/samplesys/samplecomp/`) | Key types |
| :--- | :--- | :--- |
| `bai-ogov-samplesys-samplecomp-api/ogovSampleSysSampleCompAPIModelClasses` | `api/model/` (`base`, `oids`, `search`, `summaries`) | `OG90SamplePersistableObj`, `OG90SampleCompBaseOIDs`/`BaseIDs`, `OG90SampleCompOIDs`/`IDs`/`Refs`, `OG90SearchFilterForSamplePersistableObj`, `OG90SummarizedSamplePersistableObj` |
| `…-api/ogovSampleSysSampleCompAPIClasses` | `api/`, `api/interfaces/`, `api/interfaces/samplepersistableobj/`, `api/samplepersistableobj/` | `OG90AppCodesForSampleComp`, `OG90ClientAPIForSampleComp`, `OG90IsServiceInterfaceForSampleComp[PersistableObject]` (component markers, in `api/interfaces/`), `OG90<Op>ServicesForSamplePersistableObj`, `OG90ClientAPIDelegateForSamplePersistableObj<Op>Services`, `OG90SamplePersistableObjRefSolver` |
| `…-api/ogovSampleSysSampleCompAPIBootstrap{Guice,Spring}Classes` | `api/bootstrap/{guice,spring}/` | `OG90APIBootstrapGuiceModuleForSampleComp`, `OG90APISpringBootstrapConfigurationForSampleComp` |
| `bai-ogov-samplesys-samplecomp-core/ogovSampleSysSampleCompCOREDBPersistenceClasses` | `core/db/` (`entities`, `transfer`, `dao`, `search`, `summarizers`, `services`, `services/delegates`) | `OG91DBEntityForSamplePersistableObj`, `OG91DB{CRUD,Find,Count}ForSamplePersistableObj`, `OG91DBSearcherForSamplePersistableObj`, `OG91DBSearcherProvidersForSampleCompPersistableObjs` (component level, in `core/db/search/`), `OG91SummarizerServiceImplForSamplePersistableObj`, `OG91CORE<Op>ServicesForSamplePersistableObj`, `OG91CORE<Op>ServicesDelegateForSamplePersistableObj` (but `OG91FindServicesDelegateForSamplePersistableObj`, without `CORE`) |
| `…-core/ogovSampleSysSampleCompCOREBootstrap{Guice,Spring}Classes` | `core/bootstrap/{guice,spring}/` | `OG91COREPersistenceBootstrapGuiceModuleForSampleComp`, `OG91SearchGuiceModuleForSampleComp`, `OG91CORESpringBootstrapConfigurationForSampleComp` |

Integration tests for the sample live in `presencia-web/ogov-pocs/ogov-test/e80a180-ogov-test/` (`OG99…ForSamplePersistableObj…` and `OG99…ForSampleComp…` in the `…TestCommonClasses`, `…TestGuiceClasses` and `…TestSpringClasses` modules). Task 4 mirrors them.

## 7. Changing the reference implementation or the generators

The `samplecomp` component and its tests **are** the template of `scripts/`: what gets generated is the sample with its identifiers replaced. The component classes that list every object (Client API, OIDs/IDs/Refs, summarizers, bootstrap modules, `persistence.xml`, test base classes) are generated empty from the templates in `scaffold_component.py` and filled by `add_persistable_object.py`.

To keep them in sync:
- Write identifiers in the sample with its tokens (`samplesys`, `SampleSys`, `SAMPLESYS`, `samplecomp`, `SampleComp`, `sampleComp`, `SAMPLECOMP`, `SamplePersistableObj`, `samplePersistableObj`, `samplepersistableobj`, `OG90`, `OG91`, `bai`) and avoid them where they must not be replaced (e.g. `SampleComponent` would become `MediaRegisteronent`).
- Keep the sample's custom finders (`findByNameExact`) as the last members of its Find classes: the generator leaves them out by cutting from the first one to the end of the class.
- After changing the sample or a script, run the regression check. It regenerates communic / media-register (`OG10MediaGroup`, `OG10Media`) in a temp folder and compares it file by file with the real one, without touching the workspace:

```bash
python scripts/validate_roundtrip.py
```

It must end with `RESULT: IDENTICAL` (only `serialVersionUID` values are normalized). If a change is intended, apply it to communic too, so the reference stays a working, tested component.
