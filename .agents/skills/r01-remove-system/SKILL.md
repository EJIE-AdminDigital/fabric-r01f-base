---
name: r01-remove-system
description: >-
  Removes a whole OGOV system from presencia-web/ogov-pocs with a bundled
  script: the ogov-{system} folder (every component, security included), its
  CORE / API config folders in ogov-config and ogov-config-byenv (every
  environment), and its tests and registrations in ogov-test (sources, Spring
  db yml, pom version properties and dependencies, application.yml imports),
  optionally its section in OG00AppCodes. Dry run first, backup with manifest
  and undo. Use it whenever the user wants to delete, remove, drop or clean up
  a system and its traces, including Spanish requests such as "elimina el
  sistema communic", "borra el sistema X con todo su código", "quita todas las
  referencias del sistema X en config y test" or "deshaz el sistema que
  creamos", even if the skill is not named. Not for removing a single
  component or persistable object, and never for samplesys (the template of
  the r01-architecture generators).
---
# Remove an OGOV system

A system leaves traces in four repositories besides its own folder: the CORE config folders named after its app code (`og11/` in `ogov-config` and in every environment of `ogov-config-byenv`), its test sources and Spring db files in `ogov-test`, the version properties, dependencies and yml imports that register it there, and its app codes and model packages in `OG00AppCodes` (`ogov-common`). Removing the folder alone leaves the test project referencing artifacts that no longer exist, so its build breaks. The bundled script finds everything that belongs to the system, shows it, backs it up, removes it and lists any reference left anywhere in `ogov-pocs`.

## Workflow

### 1. Gather the inputs
- **Given:** the system id (`communic`). The app codes are read from `OG00AppCodes`. If the system isn't registered there, ask for them (or pass `--api-app-code` / `--core-app-code`): without them its config folders can't be identified.
- **`OG00AppCodes`:** it lives in `ogov-common`. Unless the user already said so, ask whether to remove the system from it too (`--app-codes`: its section of constants and its `MODEL_JAVA_PACKAGES` entries). Without it, the script lists those lines as remaining references.
- The script refuses `samplesys`: it is the template the `r01-architecture` and `r01-architecture-security` generators copy.

### 2. Dry run
The script is in `scripts/` next to this `SKILL.md` (Python 3, no extra packages; `python`, `py` or `python3`, whichever works). Paths are detected from the workspace; pass `--pocs` if it isn't found.

```bash
python <skill-folder>/scripts/remove_system.py --system communic [--app-codes]
```

It changes nothing. Read and summarize for the user:
- **`[remove folders]` / `[remove files]`**: the system folder and the config and test folders, with file counts.
- **`[edit]`**: the registration lines removed from each pom and `application.yml` (and `OG00AppCodes` with `--app-codes`), by block.
- **`WARNING … git repositories`**: projects of the system that are git repositories. Their local history goes with them (it stays in the backup); ask the user whether everything is pushed.
- **`[remaining references]`**: references to the system that would stay (another system's code depending on it, docs, `OG00AppCodes` without `--app-codes`). They are never edited: show them and ask what to do.

### 3. Confirm, then apply
This is a permanent deletion of many files: show the user the summary of step 2 and **wait for an explicit yes** before applying, even if the request already said "delete". Then:

```bash
python <skill-folder>/scripts/remove_system.py --system communic [--app-codes] --apply --backup <empty folder outside the project>
```

- Use a folder outside `ogov-pocs` (your session's scratch folder, or the system temp folder). Every removed or edited file is copied there, relative to `ogov-pocs`, with a `manifest.json`.
- It edits the registrations first, then deletes. Line endings of the edited files are kept.
- Undo, if needed: `python <skill-folder>/scripts/remove_system.py --undo <backup folder>` restores every removed and edited file (it refuses when a removed folder exists again).

### 4. Report back
- What was removed (folders with file counts, files) and edited, from the script's output.
- Remaining references and what you did with them.
- Where the backup is and the undo command.
- **Eclipse:** the system's projects stay in the workspace as missing projects: delete them from the workspace (Project Explorer → Delete, without "Delete project contents on disk", which is already done), and refresh (F5) + Maven → Update Project on `ogovTestCommonClasses`, `ogovTestGuiceClasses`, `ogovTestSpringClasses`, `ogovCOREConfig`, `ogovCOREConfigByEnv` (and `ogovCommonAPIClasses` with `--app-codes`).
- **Database:** the system's tables (`OG11…`) are not dropped. Say so; dropping them is up to the user.
- Nothing is committed: the edited test, config and common repositories show the changes in `git status`.

## What the script recognizes

| Where | What |
| :--- | :--- |
| `ogov-pocs/ogov-{system}/` | the whole folder |
| `ogov-config/…/ogovCOREConfig/src/main/config/` | `{core}/`, `{api}/` |
| `ogov-config-byenv/…/ogovCOREConfigByEnv/src/main/config/<env>/` | `{core}/`, `{api}/` in every environment |
| `ogov-test/…/<module>/src/{main,test}/java/ogov/{system}/` | the system's test sources |
| `ogov-test/…/src/test/resources/config/<env>/{system}-*.yml` | its Spring db properties |
| `ogov-test/…/pom.xml` (parent and modules) | `<ogov.{system}.*.version>` properties; `<dependency>` blocks with `groupId` `ogov.{system}.*` and their box comment |
| `ogov-test/…/application.yml` | the `{system}-*.yml` imports |
| `OG00AppCodes.java` (only `--app-codes`) | the section holding `{SYSTEM}_STR` and the `ogov.{system}.*` model packages |

The registration formats are the ones the `r01-architecture` and `r01-architecture-security` scripts write. A registration written by hand in another shape is left alone and shows up in `[remaining references]`.
