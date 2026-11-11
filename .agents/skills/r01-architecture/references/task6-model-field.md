# Task 6 — Add, Remove or Rename a Field of an Existing Persistable Object

Use this workflow when the user wants to add, remove, rename or retype a field of a persistable object whose API, CORE and tests already exist (Tasks 2–4), and to carry the change through every layer that depends on it.

A field is declared once, in the hand-written model object, but other layers may depend on it. Most of the time it's fewer than people expect, because of how FABRIC stores objects:

- **The whole model object is stored as XML in the entity's `DESCRIPTOR` column.** The transfer delegate's `super.setDBEntityFieldsFromModelObject(...)` marshalls it on every create and update, and the `ModelObjFromDBEntityTransformer` unmarshalls it on read. Any `@MarshallField` of the model is therefore persisted and loaded without touching the CORE.
- **A DB entity `@Column` is a copy of a field, written only so JPQL can filter or sort on it.** The transfer delegate (`{coreAppCode}ModelObjToDBEntityTransferDelegateFor{PersistableObj}`) copies the value on every create and update; nothing reads it back into the model. A field needs a column only if a `@NamedQuery` (a custom finder) uses it.

So, before editing, find out which layers this field really reaches. Adding columns, summaries or mock values nobody uses adds code to maintain; missing one that a finder depends on breaks at runtime.

## Step 6.1: Locate the object and its family
- Find the model class (e.g. `OG10Media.java`) and derive the component, the app codes and the folders as in Sections 1 and 6 of `SKILL.md`. The object's classes are in the component's `-api` and `-core` repositories and in `{domain id}-test`.
- The model is the user's hand-written code: change only the field and what goes with it (its imports, an enum used only by it, a convenience method like `belongsToMediaGroup()` that reads it). Don't reformat or reorganize the rest.

## Step 6.2: Find what depends on the field
Search the three repositories for every form of the field. For `_webSite`: the field `_webSite`, the accessors `getWebSite` / `setWebSite` / `isWebSite` (and `hasWebSite`-style helpers), the XML name in `@MarshallField(as="webSite")`, the column (`WEBSITE`, `WEB_SITE`) and the JPQL path (`dbObj._webSite`). The usual places are:

| Layer | Class | When it uses the field |
| :--- | :--- | :--- |
| API model | `{apiAppCode}{PersistableObj}` | Always: the `@MarshallField` itself |
| API model | Enum used only by this field (nested or in `.api.model`) | If the field is an enum |
| API model | `{apiAppCode}Summarized{PersistableObj}` | If the summary shows it (it already copies `_name`) |
| API | `{apiAppCode}FindServicesFor{PersistableObj}` + Client API Find delegate | If a custom finder takes it as a parameter |
| CORE | `{coreAppCode}DBEntityFor{PersistableObj}` | If a `@NamedQuery` filters on it: `@Column`, maybe an `@Index` |
| CORE | `{coreAppCode}ModelObjToDBEntityTransferDelegateFor{PersistableObj}` | If the entity has a column for it |
| CORE | Find DAO, Find delegate, Tier 1 Find service | If a custom finder uses it |
| Tests | `{testAppCode}Mock{PersistableObj}Factory` | If the factory sets it (it should when a finder or the test reads it) |
| Tests | `{testAppCode}TestFor{PersistableObj}Persistence` | If the test sets, updates or asserts it, or runs a finder on it |

Other persistable objects can reference the field too (a summarizer or a Ref resolver in another component). Search beyond the object's own package before deciding the change is local.

## Step 6.3: Make the change

### Adding a field
1. **Model.** Add it in the `FIELDS` section, following the existing fields:
   ```java
       @MarshallField(as="email",escape=true)
       @Getter @Setter private String _email;
   ```
   - `as` is the camelCase name without the underscore. Use `escape=true` for free-text strings, as the existing `_name` does.
   - For a reference to another persistable object, store its Ref, never the object itself: `@MarshallField(as="mediaGroup") @Getter @Setter private OG10MediaGroupRef _mediaGroupRef;`.
   - For an enum, put it in the model's `TYPES` section (or its own file in `.api.model` if other classes use it). The marshaller writes the constant's name, so renaming a constant later makes stored values unreadable.
   - The model is marshalled with its no-args constructor and setters, so don't make the field `final` or add it to the constructors unless the user asks.
2. **Only if a finder will filter on it**, add the column and copy it (or let Task 5 do it as part of the finder):
   - In the entity's `FIELDS` section, following the sample `OG91DBEntityForSamplePersistableObj` (replace its placeholder comment `// add here a @Column…` if it's still there):
     ```java
     ////////// --- Email info
         @Column(name="EMAIL",
                 nullable=true,
                 insertable=true,updatable=true) @Basic
         @Getter @Setter private String _email;
     ```
     For a Ref, store the referenced OID as a string, sized like the base `ID` column: `@Column(name="MEDIAGROUP_OID",length=OID.OID_LENGTH,nullable=true) @Basic` on `private String _mediaGroupOid;` (`OID` is `r01f.oids.OID`). If the finder is frequent or the value is unique, add an `@Index(name = "{TABLE}IDX_BY_{COLUMN}",columnList = "{COLUMN}")` to `@Table.indexes`, next to the `…IDX_BY_ID` one.
   - In the transfer delegate's `setDBEntityFieldsFromModelObject(...)`, after the `super` call (replace its placeholder comment if it's still there):
     ```java
         dbEntity.setEmail(modelObj.getEmail());
         dbEntity.setMediaGroupOid(modelObj.getMediaGroupRef() != null ? modelObj.getMediaGroupRef().getOid().asString()
                                                                       : null);
     ```
3. **Summary**, only if the user wants it shown in lists: add a `@MarshallField` to `{apiAppCode}Summarized{PersistableObj}` and copy it in both constructors (from the object and from another summary), as `_name` is.
4. **Tests.** Set a recognisable value in the mock factory (`outObj.setEmail("a-media@mail.test")`) when a finder or an assertion will use it. Otherwise it's optional; ask or skip it.

### Removing a field
1. Remove every use found in Step 6.2, from the outside in: test and mock factory, then the CORE (finders that filter on it through all tiers, `@NamedQuery`, `@Index`, `@Column`, the copy in the transfer delegate), then the summary, then the field and its accessors in the model.
2. A custom finder whose only purpose was this field goes too, in every layer: remove it as [Task 5](task5-custom-finder.md) describes (Step 5.3). If the finder filters on other fields as well, stop and ask what it should do now.
3. Remove what only existed for the field: an enum type, a helper method, imports left unused, a `TYPES` section left empty (with its 89-slash separator block). Search before deleting a type: another class may use it.
4. Leave the database alone and tell the user: `hbm2ddl.auto: update` doesn't drop columns, so the old column stays, and the rows already stored keep the old element in their XML `DESCRIPTOR`. Don't claim how the marshaller treats an element it no longer knows; if the user has data to keep, suggest loading an old row once in a test to check.

### Renaming a field
- Rename the Java field and its accessors everywhere Step 6.2 found them, including the JPQL path in `@NamedQuery` (`dbObj._webSite`) and the finder parameter names if they mirror it.
- **The XML name is a separate decision.** Keeping `@MarshallField(as="webSite")` while renaming the Java field keeps every stored row readable. Changing `as` makes old rows lose that value. Default to keeping the old `as` and say so; change it only if the user wants the names to match and has no data to keep.
- A column can keep its name too (`@Column(name="WEBSITE")`); if renamed, the old column stays in the database next to the new, empty one.

### Changing a field's type
Treat it as a rename that also changes the value: check every place in Step 6.2 for conversions (`asString()`, `Strings.isNullOrEmpty`, enum `valueOf`), and warn that rows stored with the old type may not unmarshall.

## Step 6.4: Format and verify
- Apply [`r01-java-formatter`](../../r01-java-formatter/SKILL.md) to the lines you wrote or changed (if your tool doesn't load it automatically, read that `SKILL.md` and follow it, including its `verify_whitespace_only.py` check). In the user's model, format only your lines.
- Search the three repositories again for the old names (field, accessors, XML name, column). After a removal or rename nothing should be left except what you kept on purpose.
- Check that each `import` you left still resolves and that no import became unused.
- Nothing is compiled by this task unless a compiler and the dependency jars are available; say so in the report.

## Step 6.5: Report
- The files changed, grouped by repository and Maven module, and which layers didn't need a change and why (e.g. "no column: no finder filters on `_email`").
- Changes to hand-written code (the model) listed one by one.
- Database and compatibility notes: columns left behind, XML names kept or changed, stored rows that may be affected.
- What the user has to do: refresh (F5) and build the changed Eclipse projects, and rerun the persistence tests (`{testAppCode}TestGuiceFor{ComponentId}PersistableObjects`, `{testAppCode}TestSpringFor{ComponentId}PersistableObjects`).
