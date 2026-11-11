---
name: r01-rename-persistable-object
description: >-
  Renames a FABRIC (r01f) persistable object of the OGOV projects in every
  layer at once: model, OID/ID/Ref, service interfaces, Client API delegates
  and RefSolver (API), entity, table, DAOs, searchers, summarizers, services
  and bootstrap (CORE), mock factory and Guice/Spring tests, persistence.xml,
  packages and folders. Uses a bundled script with a dry run, backups and a
  manifest, then formats only the changed lines. Use it whenever the user wants
  to rename a persistable object or its whole family of classes, including
  Spanish requests such as "renombra OG10MediaOutlet a OG10Media", "cambia el
  nombre del persistable object", "¿podemos llamarlo X en vez de Y?" or
  "quiero renombrarlo en todas las capas", even if the skill is not named. Not
  for renaming a single field (use r01-architecture) or a component or system.
---
# Rename a FABRIC persistable object

A persistable object's name is spread over about sixty files in three repositories: class names (`OG10MediaOutlet`, `OG11DBEntityForMediaOutlet`, `OG99MockMediaOutletFactory`…), package segments (`.api.interfaces.mediaoutlet`), fields and getters (`_forMediaOutletCRUD`, `getMediaOutletTest()`), XML names (`@MarshallType(as="mediaOutletOid")`), the table and index (`OG11MEDIAOUTLET`), enum constants (`MEDIAOUTLET`) and `persistence.xml`. An IDE refactor only catches the Java types it can resolve, and a plain search-and-replace hits other names that contain the old one (`Media` is inside `MediaGroup` and `MediaRegister`). So the rename is done with the bundled script, which knows the FABRIC case variants, protects the other names of the component and keeps a backup.

## How to work

### Step 1: Gather the inputs
- **Old and new name**, without the app code and in PascalCase (`MediaOutlet` → `Media`). Find the model class (`OG10MediaOutlet.java`) to learn the component, the app codes and the folders, as in [`r01-architecture`](../r01-architecture/SKILL.md) (Sections 1 and 6).
- **Check the new name once.** If it already exists in the component, is a common word that also appears in prose (`media`, `item`, `type`), or is ambiguous in the domain, say so in one or two sentences and suggest an alternative. If the user still wants it, do it without arguing again: it's their call.
- **Ask about stored data only if it matters.** A rename changes the XML names stored in each row's `DESCRIPTOR` (`@MarshallType(as="mediaOutlet")`, `mediaOutletOid`…) and the table name. In a development database that's fine; if the user has data they want to keep, offer to keep the old XML names and table name (Step 3, `--keep`).

### Step 2: Dry run
The script is in `scripts/` next to this `SKILL.md`. It needs Python 3 with no extra packages (`python`, `py` or `python3`, whichever works; on Windows `python3` is often a Microsoft Store shortcut). Run it from `presencia-web/ogov-pocs` with one `--root` per repository that can mention the object:

```bash
python <skill-folder>/scripts/rename_persistable_object.py --from MediaOutlet --to Media \
       --root ogov-communic/e80a177-ogov-communic-media-register-api \
       --root ogov-communic/e80a177-ogov-communic-media-register-core \
       --root ogov-test/e80a180-ogov-test
```

Add other components' repositories if they reference the object (search for the model class name first, e.g. a Ref to it in another model). `{domain id}-config` holds per-component config, not per-object, so it normally needs no changes; check it with a search.

Without `--apply` nothing changes. Read the output:
- **`protected names`**: the other model classes and the component name, found automatically. Their case variants and their prose forms (`media group`, `MEDIA REGISTER`) are never touched. Add `--protect Name` for anything else that contains the old name, such as a third-party type (`MediaType`) or an object of another component.
- **`identifier changes`**: every distinct identifier that changes, with its file count. This is the list to review. Anything unexpected (`MediaType -> OutletType`) needs a `--protect`.
- **Plain-word names**: when the old name is a common word (`Media`), the camelCase rule also matches it in comments and strings. Run again with `--show media --show Media --show MEDIA` to list those lines (package and import lines are left out), and add `--keep "text"` for each piece of prose or literal that must stay as it is.
- **Collisions**: if a target file already exists the script stops without changing anything.

### Step 3: Apply
Run the same command with `--apply --backup <dir>`, using a folder outside the project (your session's scratch folder, or the system temp folder):

```bash
python <skill-folder>/scripts/rename_persistable_object.py --from MediaOutlet --to Media --root … \
       --keep "A media outlet (newspaper, radio station, tv channel, digital media...)" \
       --backup <scratch>/rename_mediaoutlet --apply
```

The script:
- Copies every touched file to `--backup`, keeping its relative path, and writes `manifest.json` with each file's old path, new path and changed line numbers.
- Writes the renamed contents with the same line endings and line count, moves the files to their new folders and removes the folders left empty.
- Re-sorts each `import ogov.` block that changed, because the new name can change the order Eclipse expects (`…media.*` sorts before `…mediagroup.*`).
- Prints the **leftovers**: lines that still mention the old name in another form, such as prose (`a media outlet`), UPPER_SNAKE or kebab-case. Fix the ones that are part of the name by hand (mock values like `"a media outlet id"`, section comments like `////////// media outlet services`) and leave real prose alone. Ask if unsure.

To keep stored data readable, pass the old XML names and table name as `--keep` (e.g. `--keep 'as="mediaOutlet"' --keep 'as="mediaOutletOid"' --keep OG11MEDIAOUTLET`), and say in the report that the Java names changed but the persisted names didn't.

### Step 4: Format
Renaming changes the length of names, so continuation lines aligned under them drift: parameters under an opening parenthesis, the root dot of a fluent chain, `extends` type arguments. Apply [`r01-java-formatter`](../r01-java-formatter/SKILL.md) (if your tool doesn't load it automatically, read that `SKILL.md` and follow it) to every `.java` file in the manifest:
- Copy each file first (the formatter's "original copy" is the file as the rename script left it, not the backup: the backup has different names, so the whitespace check would fail on it).
- Run its formatter with the file's `changed_lines` from the manifest. It realigns those lines **and the lines that align under them**, even if their text didn't change:
  ```bash
  python <formatter-skill-folder>/scripts/format_java.py <file> --lines <changed_lines joined with commas>
  ```
  Don't format the rest of the file, especially in files the user wrote by hand (usually the model and the OID/ID/Ref holders).
- Run `verify_whitespace_only.py` on each file against its copy.

Fix the leftovers by hand (Step 3) **before** formatting, and keep each line's ending as it was: the files are mostly CRLF, and some editing tools rewrite a whole file with LF. The whitespace check reports that as "line ending changed"; a file that fails it must be fixed, not reported as done.

### Step 5: Verify
- Search the roots for the old name in every case (`MediaOutlet`, `mediaOutlet`, `mediaoutlet`, `MEDIAOUTLET`, `media outlet`). Only the text you kept on purpose should remain.
- For every `import {domain id}.…` in the touched files, check that the imported file exists. A wrong package segment shows up here before the compiler does.
- If a Java compiler and the dependency jars are available, compile; otherwise say in the report that nothing was compiled.

### Step 6: Report
- What changed, as the script's summary gives it: files touched, files moved, packages renamed, and the main renamed types per layer (model, service interfaces, entity and table, tests).
- What you kept on purpose (`--keep`) and what you fixed by hand among the leftovers.
- **What the user has to do:**
  - Refresh (F5) and **`Project > Clean`** the renamed Eclipse projects (the component's `-api` and `-core` modules and the test modules). The old classes stay in `target/classes` under the old packages until a clean build, and the Guice/Spring classpath scan may then find both the old and the new service.
  - The old table stays in the database: `hbm2ddl.auto: update` creates the new one but doesn't drop or rename the old one. Rows stored with the old `@MarshallType` names can't be read with the new ones.
  - Rerun the persistence tests (`{testAppCode}TestGuiceFor{ComponentId}PersistableObjects`, `{testAppCode}TestSpringFor{ComponentId}PersistableObjects`).
- Where the backup and manifest are, and how to undo the rename: `python <skill-folder>/scripts/rename_persistable_object.py --undo <backup dir>` deletes the new files and restores the originals (it also discards any edits made after the rename, such as the formatting).
