# Task 3 — Given a Persistable Model Object & API Service Interfaces, Create the CORE Layer

Use this workflow to implement the 3-tier CORE persistence layer in `{domainId}{SystemId}{ComponentId}COREDBPersistenceClasses` and wire it in the Guice and Spring bootstrap modules.

> **Fast path:** `scripts/add_persistable_object.py --layers core` does this playbook for objects with business ID, with the full set of operations, from the reference implementation. Use it and come back to this playbook only for what it doesn't cover (objects without business ID, a subset of the operations, custom finders) or to check what it generated.

## Step 3.0: Read the API contract first
The service interfaces in `{domain id}.{system id}.{component id}.api.interfaces.{persistableobj_lower}` decide what this task creates:
- Build an inventory of the operations that have an interface (`CRUD`, `Find`, `Count`, `Search`, `Summarize`) and skip the steps below for operations that don't. Each step is marked *(If `X` is in scope)* where that applies.
- List every custom method declared beyond the base interfaces (e.g. `findByNameExact(SecurityContext,String)`). Each one needs a `@NamedQuery` on the entity (Step 3.1), an indexed `@Column` for each field it filters on, a DAO implementation (Step 3.3), a Tier 2 method that validates its parameters (Step 3.4) and a Tier 1 method that only delegates (Step 3.5). [`task5-custom-finder.md`](task5-custom-finder.md) has the code of each of those layers, with variants for refs, enums and several parameters.
- Check the model object's base class to choose between the `WithID` and plain variants of the base classes.

## Step 3.1: Create the JPA DB Entity & Register in `persistence.xml`
1. Create `{domain id}.{system id}.{component id}.core.db.entities.{persistableobj_lower}.{coreAppCode}DBEntityFor{PersistableObj}`:
   - Extends `OG01DBEntityWithIDBase` (if model has business ID) or `OG01DBEntityBase`.
   - Annotate (observing `r01-java-formatter` `@NamedQuery` right-alignment for `SELECT` / `FROM` / `WHERE`):
     ```java
     @Entity @Cacheable(false)
     @Table(name = "{CORE_APP_CODE}{PERSISTABLE_OBJ_UPPER}",
            indexes = {
                 @Index(name = "{CORE_APP_CODE}{PERSISTABLE_OBJ_UPPER}IDX_BY_ID",
                        columnList = "ID",unique = true)
            })
     @NamedQueries({
         @NamedQuery(name = "{coreAppCode}DBEntityFor{PersistableObj}.findByNameExact",
                     query = "SELECT dbObj " +
                               "FROM {coreAppCode}DBEntityFor{PersistableObj} dbObj " +
                              "WHERE dbObj._name = :name")
     })
     @Accessors(prefix="_")
     @NoArgsConstructor
     public class {coreAppCode}DBEntityFor{PersistableObj}
          extends OG01DBEntityWithIDBase {
     ```
   - Define `@Column` fields (`@Basic @Getter @Setter private ... _field;`) for fields queried by JPQL/SQL outside the XML `_descriptor`.
2. Add `<class>{domain id}.{system id}.{component id}.core.db.entities.{persistableobj_lower}.{coreAppCode}DBEntityFor{PersistableObj}</class>` to:
   - `src/main/resources/META-INF/persistence.xml`
   - `src/main/resources/META-INF/jpa/{coreAppCode_lower}.{componentId_lower}.persistence.xml`

## Step 3.2: Create Model <-> DBEntity Transformers (`.core.db.transfer.{persistableobj_lower}`)
1. `{coreAppCode}ModelObjFromDBEntityTransformerFor{PersistableObj}`:
   - Extends `OG01TransformerFromDBEntityToPersistableObjectBase<{coreAppCode}DBEntityFor{PersistableObj},{apiAppCode}{PersistableObj}>`.
   - Constructor passes `({apiAppCode}{PersistableObj}.class,modelObjectsMarshaller)`.
2. `{coreAppCode}ModelObjToDBEntityTransferDelegateFor{PersistableObj}`:
   - Extends `OG01TransferFromPersistableObjectToDBEntityBase<{apiAppCode}{PersistableObj},{coreAppCode}DBEntityFor{PersistableObj}>`.
   - Overrides `setDBEntityFieldsFromModelObject(SecurityContext,ModelObj,DBEntity)` calling `super.setDBEntityFieldsFromModelObject(...)` and copying indexed fields (`dbEntity.setXxx(modelObj.getXxx())`).

## Step 3.3: Create Tier 3 Classes (DAOs, Searchers & Summarizers)
1. **DB DAOs in `.core.db.dao.{persistableobj_lower}`**:
   - **CRUD DAO** (`{coreAppCode}DBCRUDFor{PersistableObj}`):
     - Extends `OG01DBCRUDForPersistableObjectWithIDBase<OID,ID,ModelObj,DBEntity>` (or `OG01DBCRUDForPersistableObjectBase<OID,ModelObj,DBEntity>`).
     - Implements `{apiAppCode}CRUDServicesFor{PersistableObj}`.
     - Passes `new {coreAppCode}ModelObjFromDBEntityTransformerFor{PersistableObj}(marshaller)` to `super(...)` and delegates `setDBEntityFieldsFromModelObject(...)` to `{coreAppCode}ModelObjToDBEntityTransferDelegateFor{PersistableObj}`.
   - **Find DAO** (`{coreAppCode}DBFindFor{PersistableObj}`):
     - Extends `OG01DBFindForPersistableObjectBase<OID,ModelObj,DBEntity>`.
     - Implements `{apiAppCode}FindServicesFor{PersistableObj}`.
     - Implements custom finder methods by executing `this.getEntityManager().createNamedQuery(...)` and wrapping results with:
       ```java
       return FindResultBuilder.using(securityContext)
                               .on(_modelObjectType)
                               .foundDBEntities(dbEntities)
                               .transformedToModelObjectsUsing(this);
       ```
   - **Count DAO** (`{coreAppCode}DBCountFor{PersistableObj}`):
     - Extends `OG01DBCountForPersistableObjectBase<OID,ModelObj,DBEntity>`.
     - Implements `{apiAppCode}CountServicesFor{PersistableObj}`.

2. *(If `SEARCH` is in scope)* **DB Search Classes in `.core.db.search.{persistableobj_lower}`**:
   - `{coreAppCode}DBEntityToSearchResultItemTransformerFor{PersistableObj}` (implements `TransformsDBEntityToSearchResultItem<DBEntity,ResultItem>`, unmarshals `ModelObj` from `dbEntity.getDescriptor()` via `_marshaller.forReading().fromXml(...)`).
   - `{coreAppCode}DBSearchQueryFor{PersistableObj}` (extends `DBSearchQuery<Filter,DBEntity>`, with static inner class `{coreAppCode}DBSearchQueryFor{PersistableObj}ToJPQLTranslator` extending `DBSearchQueryToJPQLTranslator<Filter,DBEntity>`).
   - `{coreAppCode}DBSearcherFor{PersistableObj}` (extends `DBSearcherBase<Filter,ResultItem,DBEntity>`, implements `{apiAppCode}SearchServicesFor{PersistableObj}`).
   - In `{coreAppCode}DBSearcherProvidersFor{ComponentId}PersistableObjs` (component level, package `.core.db.search`; create it with the first object): add static inner class `{coreAppCode}SearcherProviderFor{PersistableObj}` extending `DBSearcherProviderBase<Filter,ResultItem>` whose `get()` returns `new {coreAppCode}DBSearcherFor{PersistableObj}(_dbModuleConfig,_entityManagerProvider.get(),_marshaller)`.

3. *(If `SUMMARIZE` is in scope)* **Summarizer Classes in `.core.db.summarizers`**:
   - In `{coreAppCode}SummarizersInterfacesFor{ComponentId}PersistaleObjects`: add interface `{coreAppCode}SummarizerServiceFor{PersistableObj}` extending `{coreAppCode}Is{ComponentId}PersistableObjSummarizerService<OID,ModelObj,SummarizedObj>`.
   - Create `{coreAppCode}SummarizerServiceImplFor{PersistableObj}` (`@CoreService @Singleton @Slf4j`, extends `{coreAppCode}SummarizerServiceFor{ComponentId}PersistableObjBase<OID,ModelObj,SummarizedObj>`, implements `{coreAppCode}SummarizerServiceFor{PersistableObj}`, injects `{apiAppCode}CRUDServicesFor{PersistableObj}`).
   - Register the enum constant and visitor methods in `{coreAppCode}Summarizable{ComponentId}PersistableObjType`, `{coreAppCode}Summarizable{ComponentId}PersistableObjVisitors`, and `{coreAppCode}SummarizersFor{ComponentId}PersistableObjs`.

## Step 3.4: Create Tier 2 Services Delegates (`.core.db.services.delegates.{persistableobj_lower}`)
Each delegate implements the corresponding API service interface:
1. `{coreAppCode}CORECRUDServicesDelegateFor{PersistableObj}`:
   - Extends `OG01CORECRUDServicesDelegateForPersistableObjectWithIDBase<OID,ID,ModelObj>` (or `...PersistableObjectBase`), implements `{apiAppCode}CRUDServicesFor{PersistableObj}`.
   - Constructor instantiates `new {coreAppCode}DBCRUDFor{PersistableObj}(DBModuleConfigBuilderForHibernate.dbModuleConfigFrom(coreCfg),entityManager,marshaller)`.
2. `{coreAppCode}FindServicesDelegateFor{PersistableObj}`:
   - Extends `OG01COREFindServicesDelegateForPersistableObjectBase<OID,ModelObj>`, implements `{apiAppCode}FindServicesFor{PersistableObj}`.
   - Constructor instantiates `new {coreAppCode}DBFindFor{PersistableObj}(...)`.
   - Implements custom finder methods: validates parameters (e.g., `if (Strings.isNullOrEmpty(name)) throw new IllegalArgumentException(...)`) and delegates to `this.getServiceImplAs({apiAppCode}FindServicesFor{PersistableObj}.class).findBy...(securityContext,...)`.
3. `{coreAppCode}CORECountServicesDelegateFor{PersistableObj}`:
   - Extends `OG01CORECountServicesDelegateForPersistableObjectBase<OID,ModelObj>`, implements `{apiAppCode}CountServicesFor{PersistableObj}`.
   - Constructor instantiates `new {coreAppCode}DBCountFor{PersistableObj}(...)`.
4. `{coreAppCode}CORESearchServicesDelegateFor{PersistableObj}`:
   - Extends `OG01CORESearchServicesDelegateForPersistableObjectBase<Filter,ResultItem>`, implements `{apiAppCode}SearchServicesFor{PersistableObj}`.
5. `{coreAppCode}CORESummarizeServicesDelegateFor{PersistableObj}`:
   - Extends `ServicesDelegateBase`, implements `{apiAppCode}SummarizeServicesFor{PersistableObj}`.

## Step 3.5: Create Tier 1 Services Impl (`.core.db.services.{persistableobj_lower}`)
All Tier 1 classes are annotated with `@CoreService @Singleton @Accessors(prefix="_")` and implement both `{apiAppCode}<Op>ServicesFor{PersistableObj}` and `{coreAppCode}IsCOREPersistenceServiceFor{ComponentId}`:
1. `{coreAppCode}CORECRUDServicesFor{PersistableObj}`:
   - Extends `OG01CORECRUDServicesForPersistableObjectWithIDBase<OID,ID,ModelObj>`.
   - Injects `@ServicesCoreConfigFor({apiAppCode}AppCodesFor{ComponentId}.{SYSTEM}_{COMPONENT}_CORE_FQN) ServicesCoreModuleConfig`, `Provider<SecurityContext>`, `@ModelObjectsMarshaller Marshaller`, `EventBus`, and `@EntityManagerFor({apiAppCode}AppCodesFor{ComponentId}.{SYSTEM}_{COMPONENT}_CORE_FQN) Provider<EntityManager>`.
   - Sets `_delegateProvider = securityContext -> new {coreAppCode}CORECRUDServicesDelegateFor{PersistableObj}(_coreConfig,securityContextProvider,this.getFreshNewEntityManager(),_modelObjectsMarshaller,_eventBus);`.
2. `{coreAppCode}COREFindServicesFor{PersistableObj}`:
   - Extends `OG01COREFindServicesForPersistableObjectBase<OID,ModelObj>`.
   - Sets `_delegateProvider = securityContext -> new {coreAppCode}FindServicesDelegateFor{PersistableObj}(_coreConfig,this.getFreshNewEntityManager(),_modelObjectsMarshaller,_eventBus);`.
   - Implements custom finder methods solely by delegating:
     ```java
     @Override
     public FindResult<{apiAppCode}{PersistableObj}> findByNameExact(final SecurityContext securityContext,
                                                                     final String name) {
         return this.forSecurityContext(securityContext)
                    .createDelegateAs({apiAppCode}FindServicesFor{PersistableObj}.class)
                    .findByNameExact(securityContext,
                                     name);
     }
     ```
3. `{coreAppCode}CORECountServicesFor{PersistableObj}`:
   - Extends `OG01CORECountServicesForPersistableObjectBase<OID,ModelObj>`.
   - Sets `_delegateProvider = securityContext -> new {coreAppCode}CORECountServicesDelegateFor{PersistableObj}(_coreConfig,this.getFreshNewEntityManager(),_modelObjectsMarshaller,_eventBus);`.
4. `{coreAppCode}CORESearchServicesFor{PersistableObj}`:
   - Extends `OG01CORESearchServicesForPersistableObjectBase<Filter,ResultItem>`.
   - Injects `{coreAppCode}SearcherProviderFor{PersistableObj}` and sets `_delegateProvider = securityContext -> new {coreAppCode}CORESearchServicesDelegateFor{PersistableObj}(_coreConfig,this.getFreshNewSearcher(),_modelObjectsMarshaller,_eventBus);`.
5. `{coreAppCode}CORESummarizeServicesFor{PersistableObj}`:
   - Extends `CorePersistenceServicesBase`.
   - Injects `{apiAppCode}CRUDServicesFor{PersistableObj}` and `{coreAppCode}SummarizerServiceFor{PersistableObj}`, sets `_delegateProvider`, and delegates both `summarize(...)` overloads via `this.forSecurityContext(securityContext).createDelegateAs({apiAppCode}SummarizeServicesFor{PersistableObj}.class).summarize(...)`.

## Step 3.6: Wire SearcherProvider & Summarizer in CORE Bootstrap Modules
1. **Guice (`{domainId}{SystemId}{ComponentId}COREBootstrapGuiceClasses`)**:
   - In `{coreAppCode}COREPersistenceBootstrapGuiceModuleFor{ComponentId}.configureMoreBindings(Binder)`:
     ```java
     binder.bind({coreAppCode}SummarizerServiceFor{PersistableObj}.class)
           .to({coreAppCode}SummarizerServiceImplFor{PersistableObj}.class)
           .in(Singleton.class);
     ```
   - In `{coreAppCode}SearchGuiceModuleFor{ComponentId}`: add a `@Provides @Singleton` method returning `new {coreAppCode}SearcherProviderFor{PersistableObj}(marshaller,serviceCoreModuleConfig.getSubModuleConfigFor(CoreSubModule.DBPERSISTENCE,DBModuleConfig.class),entityManagerProvider)`.
2. **Spring (`{domainId}{SystemId}{ComponentId}COREBootstrapSpringClasses`)**:
   - In `{coreAppCode}CORESpringBootstrapConfigurationFor{ComponentId}`: add a `@Bean` method returning `new {coreAppCode}SearcherProviderFor{PersistableObj}(marshaller,serviceCoreModuleConfig.getSubModuleConfigFor(CoreSubModule.DBPERSISTENCE,DBModuleConfig.class),entityManagerProvider)`.

