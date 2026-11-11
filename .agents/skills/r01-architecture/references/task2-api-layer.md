# Task 2 — Given a Persistable Model Object, Create the API Layer

Use this workflow when given a persistable model object `{apiAppCode}{PersistableObj}` (e.g., `OG90SamplePersistableObj`) to create its model companions, service interfaces (`CRUD`, `FIND`, `COUNT`, `SEARCH`, `SUMMARIZE`), Client API delegates, and `ClientAPI` wiring.

> **Fast path:** `scripts/add_persistable_object.py --layers api` does this playbook for objects with business ID, with the full set of operations, from the reference implementation. Use it and come back to this playbook only for what it doesn't cover (objects without business ID, a subset of the operations, custom finders) or to check what it generated.

The model object is usually written by hand before this task runs. Locate it first, then derive the component, `{apiAppCode}`, whether it has a business ID and its fields from the class itself (see "How to work" in `SKILL.md`). Treat it as the user's code: build everything else around it.

## Step 2.1: Create Model Companions in `{domainId}{SystemId}{ComponentId}APIModelClasses`
1. **OID, ID & Ref in `.api.model.oids`**:
   - In `{apiAppCode}{ComponentId}OIDs`: add `public record {apiAppCode}{PersistableObj}OID(@Getter String id) implements {apiAppCode}Is{ComponentId}PersistableObjectOID` annotated with `@MarshallType(as="{persistableObj}Oid")`, providing `forId(String)`, `valueOf(String)`, `supply()` (`JavaOIDDispenser.generateGUID()`), `FROM_STRING_FUNC`, and `toString()`.
   - *(If object has a business ID)* In `{apiAppCode}{ComponentId}IDs`: add `@MarshallType(as="{persistableObj}Id") @Immutable public record {apiAppCode}{PersistableObj}ID(@Getter String id) implements {apiAppCode}Is{ComponentId}PersistableObjectID<{apiAppCode}{PersistableObj}OID>` with `valueOf`, `forId`, `FROM_STRING_FUNC`, `asString()`, and `toString()`.
   - *(If object has a business ID)* In `{apiAppCode}{ComponentId}Refs`: add `@MarshallType(as="{persistableObj}Ref") @Accessors(prefix="_") public static class {apiAppCode}{PersistableObj}Ref extends PersistableObjectWithIDRefBase<OID,ID> implements OG00IsPersistableObjectWithIDRef<OID,ID>,SelfValidates<{apiAppCode}{PersistableObj}Ref>`.
2. **Check the hand-written Persistable Model Object in `.api.model`** (only add what's missing, keep the rest as it is, and list any change in the report):
   - `{apiAppCode}{PersistableObj}` extends `{apiAppCode}{ComponentId}PersistableObjectWithIDBase<OID,ID,{apiAppCode}{PersistableObj}>` (or `...PersistableObjectBase<OID,...>` if no business ID).
   - Has `@Accessors(prefix="_")`, `@MarshallField` on fields (`@Getter @Setter private ... _field;`), and `public {apiAppCode}{PersistableObj}Ref getReference() { return {apiAppCode}{PersistableObj}Ref.from(_oid,_id); }`.
3. *(If `SEARCH` is required)* **Create Search Filter & Result Item in `.api.model.search`**:
   - `{apiAppCode}SearchFilterFor{PersistableObj}` extending `OG00SearchFilterBase<{apiAppCode}SearchFilterFor{PersistableObj}>` with `@MarshallType(as="searchFilterFor{PersistableObj}")`, constructors passing `{apiAppCode}{PersistableObj}.class`, and `valueOf(String)` / `fromString(String)`.
   - `{apiAppCode}SearchResultItemFor{PersistableObj}` extending `OG00SearchResultItemBase<OID,{apiAppCode}{PersistableObj},{apiAppCode}SearchResultItemFor{PersistableObj}>` with `@MarshallType(as="searchResultItemFor{PersistableObj}")`.
4. *(If `SUMMARIZE` is required)* **Create Summary Object in `.api.model.summaries`**:
   - `{apiAppCode}Summarized{PersistableObj}` extending `{apiAppCode}Summarized{ComponentId}PersistableObjectBase<OID,{apiAppCode}{PersistableObj},{apiAppCode}Summarized{PersistableObj}>` with `@MarshallType(as="summarized{PersistableObj}")`.

## Step 2.2: Create Service Interfaces in `{domainId}{SystemId}{ComponentId}APIClasses`
Place all service interfaces for `{PersistableObj}` in package `{domain id}.{system id}.{component id}.api.interfaces.{persistableobj_lower}` and annotate each with `@ExposedServiceInterface`:

1. **CRUD Service Interface** (`{apiAppCode}CRUDServicesFor{PersistableObj}`):
   - Extends `OG00IsCRUDServicesForPersistablObjectWithID<OID,ID,ModelObj>` (or `OG00IsCRUDServicesForPersistableObject<OID,ModelObj>` if no ID) and `{apiAppCode}IsServiceInterfaceFor{ComponentId}`.
   - If the object has a `Ref`, include `default PersistenceOperationResult<{apiAppCode}{PersistableObj}Ref> getRefOf(SecurityContext,OID)` and `getRefOf(SecurityContext,ID)` calling `this.getIdOf(...)` / `this.getOidOf(...)`.
2. **Find Service Interface** (`{apiAppCode}FindServicesFor{PersistableObj}`):
   - Extends `OG00IsFindServicesForPersistableObject<OID,ModelObj>` and `{apiAppCode}IsServiceInterfaceFor{ComponentId}PersistableObject`.
   - Declare custom finder methods taking `SecurityContext` as first parameter and returning `FindResult<ModelObj>`:
     ```java
     public FindResult<{apiAppCode}{PersistableObj}> findByNameExact(final SecurityContext securityContext,
                                                                     final String name);
     ```
3. **Count Service Interface** (`{apiAppCode}CountServicesFor{PersistableObj}`):
   - Extends `OG00IsCountServicesForPersistableObject<OID,ModelObj>` and `{apiAppCode}IsServiceInterfaceFor{ComponentId}`.
4. **Search Service Interface** (`{apiAppCode}SearchServicesFor{PersistableObj}`):
   - Extends `OG00IsSearchServicesForPersistableObject<{apiAppCode}SearchFilterFor{PersistableObj},{apiAppCode}SearchResultItemFor{PersistableObj}>` and `{apiAppCode}IsServiceInterfaceFor{ComponentId}`.
5. **Summarize Service Interface** (`{apiAppCode}SummarizeServicesFor{PersistableObj}`):
   - Extends `{apiAppCode}IsServiceInterfaceFor{ComponentId}`.
   - Declares:
     - `COREServiceMethodExecResult<{apiAppCode}Summarized{PersistableObj}> summarize(final SecurityContext securityContext,final {apiAppCode}{PersistableObj} obj,final Language lang);`
     - `COREServiceMethodExecResult<{apiAppCode}Summarized{PersistableObj}> summarize(final SecurityContext securityContext,final {apiAppCode}{PersistableObj}OID oid,final Language lang);`

## Step 2.3: Create Client API Delegates & RefSolver in `{domainId}{SystemId}{ComponentId}APIClasses`
Place all delegates in package `{domain id}.{system id}.{component id}.api.{persistableobj_lower}`:
1. `{apiAppCode}ClientAPIDelegateFor{PersistableObj}CRUDServices`:
   - Extends `OG00ClientAPIDelegateForPersistableObjectWithIDCRUDServicesBase<OID,ID,ModelObj>` (or `...PersistableObjectCRUDServicesBase<OID,ModelObj>`).
   - Constructor takes `(Provider<SecurityContext>,Marshaller,{apiAppCode}CRUDServicesFor{PersistableObj})`.
   - Exposes `getRefOf(OID)` and `getRefOf(ID)` delegating to `this.getServiceProxyAs({apiAppCode}CRUDServicesFor{PersistableObj}.class).getRefOf(this.getSecurityContext(),...).getOrThrow()`.
2. `{apiAppCode}ClientAPIDelegateFor{PersistableObj}FindServices`:
   - Extends `OG00ClientAPIDelegateForPersistableObjectFindServicesBase<OID,ModelObj>`.
   - Constructor takes `(Provider<SecurityContext>,Marshaller,{apiAppCode}FindServicesFor{PersistableObj})`.
   - For every custom finder in `{apiAppCode}FindServicesFor{PersistableObj}`, expose a matching method **without** `SecurityContext` returning `Collection<ModelObj>` via `.getOrThrow()`.
3. `{apiAppCode}ClientAPIDelegateFor{PersistableObj}CountServices`:
   - Extends `OG00ClientAPIDelegateForPersistableObjectCountServicesBase<OID,ModelObj>`.
4. `{apiAppCode}ClientAPIDelegateFor{PersistableObj}SearchServices`:
   - Extends `OG00ClientAPIDelegateForPersistableObjectSearchServicesBase<Filter,ResultItem>`.
5. `{apiAppCode}ClientAPIDelegateFor{PersistableObj}SummarizeServices`:
   - Extends `ClientAPIServiceDelegateBase<{apiAppCode}SummarizeServicesFor{PersistableObj}>`.
   - Exposes `summarize(ModelObj,Language)` and `summarize(OID,Language)` returning `{apiAppCode}Summarized{PersistableObj}` via `.getOrThrow()`.
6. *(If `WithID`)* `{apiAppCode}{PersistableObj}RefSolver`:
   - `@Singleton @Accessors(prefix="_")`, extends `PersistableObjectRefSolverBase<OID,ID>`, injects `@SecurityContextProviderForSystemUser Provider<SecurityContext>` and `{apiAppCode}CRUDServicesFor{PersistableObj}`, and provides `completeRefIfNeeded({apiAppCode}{PersistableObj}Ref)`.

## Step 2.4: Wire Delegates into `{apiAppCode}ClientAPIFor{ComponentId}`
In `{domain id}.{system id}.{component id}.api.{apiAppCode}ClientAPIFor{ComponentId}`:
1. Declare `@Getter private final` fields for each delegate:
   - `_for{PersistableObj}CRUD`
   - `_for{PersistableObj}Find`
   - `_for{PersistableObj}Count`
   - `_for{PersistableObj}Search`
   - `_for{PersistableObj}Summarize`
   - `_{persistableObj}RefSolver`
2. Instantiate each in the `@Inject` constructor using `this.getServiceInterfaceCoreImplOrProxy({apiAppCode}<Op>ServicesFor{PersistableObj}.class)`.

