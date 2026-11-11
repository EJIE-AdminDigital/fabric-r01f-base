---
name: r01-vaadin25-ui
description: >-
  Builds, designs, and modifies web user interfaces using the R01F (FABRIC)
  Vaadin 25 Flow component library (`r01fVaadin25UIServerClasses` and
  `r01f-base-styles.css`). Use this skill whenever the user asks to create,
  scaffold, style, or update a Vaadin 25 UI, view, screen, form, grid, menu,
  dialog, dashboard, or component using the R01F showcase components — including
  Spanish requests such as "crea una pantalla", "monta una interfaz Vaadin",
  "añade un formulario/grid/pestañas con idioma", or "usa los componentes de
  vaadin25ui".
---

# R01F Vaadin 25 UI Component Library Skill

Use this skill to build views and user interfaces with the **R01F Vaadin 25.0.7 Flow** component catalog (`r01fVaadin25UIServerClasses`) and stylesheet system (`r01f-base-styles.css`).

## How to Work

1. **Identify the UI requirements** from the user's prompt (layout shell, navigation, localized tabs, forms, CRUD grids, cards, trees, date/tag pickers, or external addons).
2. **Select the matching R01F components** from the Quick Component Selection Matrix below instead of reinventing raw HTML or ad-hoc Vaadin widgets.
3. **Read only the relevant reference file(s)** under [`references/`](./references/) to get exact Java class names, DOM/Web Component structures, Lumo theme variants, and R01F CSS classes:
   - [`references/shell-and-navigation.md`](./references/shell-and-navigation.md) — `AppLayout` shell, top navbar (language selector, notifications popover, user popover), breadcrumbs (`VaadinBreadcrumbs`), drawer `SideNav`, `VaadinUILangTabbedView` (5 localized tab patterns), `VaadinMenuBar`, `VaadinLanguageMenuBar`, and `VaadinVerticalMenu2Level`.
   - [`references/forms-buttons-and-dialogs.md`](./references/forms-buttons-and-dialogs.md) — Responsive `FormLayout` + `Binder` + dirty-state badge, nested subform `CustomField`, NORA contact & geo-address (`VaadinContactInfoManageComponent`, `VaadinNORAContactComponent`), action buttons (`VaadinToggleButton`, `VaadinAcceptCancelDeleteButtons`, `VaadinAcceptCancelDeleteButtonsWithDeleteConfirmation`, `VaadinDownloadButton`), `VaadinDeleteConfirmDialogVerificationBase`, `VaadinLangCopyTranslateComponent`, and `Notification` variants.
   - [`references/grids-cards-collections-and-trees.md`](./references/grids-cards-collections-and-trees.md) — Styled `Grid` (`r01ui-grid`), row `⋮` menu (`VaadinDotsVMenuBase`), `VaadinImagePopup`, `VaadinPagingComponent`, `R01UICardsBoard` & `R01UICardsListOrSearchButtons`, file attachment grid (`#r01fui-filelist`), `VaadinCollectionManageComponent`, and draggable hierarchical `TreeGrid`.
   - [`references/fields-tags-dates-and-addons.md`](./references/fields-tags-dates-and-addons.md) — Tag/chip fields (`VaadinTagListComponent`, `VaadinTagInputField`, `VaadinValueSelectorComponent`), date/time pickers (`R01UIDateRangeField`, `R01UIDateTimeRangeField`, `R01UIDateNavigatorField`, `R01UIHourMinutePicker`, `R01UIDateTimePicker`), and external addons (CodeMirror, `VaadinCameraField`, TinyMCE R01, elFinder).
   - [`references/css-variables-and-utilities.md`](./references/css-variables-and-utilities.md) — Complete `r01f-base-styles.css` variable palette (`--r01-*`) and `.r01-*` utility, sizing, layout, button, form, and grid classes.
4. **Apply R01F Java & CSS conventions**:
   - Ensure `r01f-base-styles.css` is imported in the application's frontend styles and override `--r01-color-primary` in `styles.css` if a custom brand color is requested.
   - Use the R01F Java formatting style (see [`../r01-java-formatter/SKILL.md`](../r01-java-formatter/SKILL.md) when writing Java code).

---

## Quick Component Selection Matrix

| UI Need | R01F Component / Pattern | Key CSS Classes / Themes | Reference |
| :--- | :--- | :--- | :--- |
| **App Shell & Top Navbar** | `AppLayout` (`primarySection=navbar`) + Drawer `SideNav` | `.r01-navbar-title`, `.r01-navbar-right`, `.r01-navbar-lang-selector`, `.r01-notif-wrapper`, `.r01-navbar-user` | [shell-and-navigation.md](./references/shell-and-navigation.md) |
| **Breadcrumbs** | `VaadinBreadcrumbs` | `.r01-breadcrumbs`, `.r01-breadcrumb-link`, `.r01-breadcrumb-separator`, `.r01-breadcrumb-current` | [shell-and-navigation.md](./references/shell-and-navigation.md) |
| **Multi-Language Tabs (`ES`/`EU`)** | `VaadinUILangTabbedView` (5 patterns: simple, top-right lang menu, H/V orientation, Tree+Detail per lang, inline copy/translate) | `vaadin-tabsheet`, `.main-menu-bar.inf-lang-menu`, `.r01-tabbed-content` | [shell-and-navigation.md](./references/shell-and-navigation.md) |
| **Language Dropdown Menu** | `VaadinLanguageMenuBar` / `VaadinMenuBar` | `theme="icon"`, `.lang-menu-item`, `.r01-menu-item-checked`, `#r01-lang-menu-status` | [shell-and-navigation.md](./references/shell-and-navigation.md) |
| **2-Level Collapsible Vertical Menu** | `VaadinVerticalMenu2Level` | `.r01-vertical-menu`, `.r01-vertical-menu-section`, `.r01-vertical-menu-section-title`, `.r01-vertical-menu-item`, `.r01-selected` | [shell-and-navigation.md](./references/shell-and-navigation.md) |
| **Dashboard (Rail + Filters + Grid)** | `SplitLayout` + bookmark rail + `.r01ui-example` panels | `vaadin-button.round`, `.borderless.grid.blue`, `theme="row-stripes"`, `.r01-no-results-label` | [shell-and-navigation.md](./references/shell-and-navigation.md) |
| **Multi-Column Form + Dirty Tracking** | `FormLayout` (`maxColumns=3`, `autoResponsive`) + `Binder` + Subform `CustomField` | `<span theme="badge success">`, `.text-error`, `.r01-input-medium/big/huge`, `.r01-combo-medium/big` | [forms-buttons-and-dialogs.md](./references/forms-buttons-and-dialogs.md) |
| **Contact & NORA Geo-Address** | `VaadinContactInfoManageComponent` + `VaadinNORAContactComponent` | `.r01ui-white-bordered-layout`, `.localizacion_boton`, `.combobox-suggestion.inline-icon`, `.r01ui-object-grid-component`, `.r01ui-create-edit-detail-view` | [forms-buttons-and-dialogs.md](./references/forms-buttons-and-dialogs.md) |
| **CRUD Form Footer Buttons** | `VaadinAcceptCancelDeleteButtons` / `...WithDeleteConfirmation` | `Eliminar` (`theme="error"`), `Cancelar`, `Guardar` (`theme="primary"`), `vaadin-confirm-dialog` | [forms-buttons-and-dialogs.md](./references/forms-buttons-and-dialogs.md) |
| **Toggle / Download Buttons** | `VaadinToggleButton`, `VaadinDownloadButton` (`Anchor` + `DownloadHandler`) | `.toggle-button`, `a.r01-download-button`, `.r01-download-button-icon` | [forms-buttons-and-dialogs.md](./references/forms-buttons-and-dialogs.md) |
| **Text-Verified Delete Dialog** | `VaadinDeleteConfirmDialogVerificationBase` | `.r01-delete-confirm-message`, `.r01-delete-confirm-hint`, `.r01-delete-confirm-field` | [forms-buttons-and-dialogs.md](./references/forms-buttons-and-dialogs.md) |
| **Copy / Translate Between Languages** | `VaadinLangCopyTranslateComponent` | `.r01-lang-copy-translate`, `.r01-lang-copy-translate-langs`, `.r01-lang-copy-translate-actions` | [forms-buttons-and-dialogs.md](./references/forms-buttons-and-dialogs.md) |
| **Data Grid + Row `⋮` Action Menu** | `Grid` + `VaadinDotsVMenuBase` + `VaadinPagingComponent` | `vaadin-grid.r01ui-grid`, `.r01-dots-vmenu` (`theme="tertiary icon"`), `.r01-paging` | [grids-cards-collections-and-trees.md](./references/grids-cards-collections-and-trees.md) |
| **Draggable Cards Board & View Switcher** | `R01UICardsBoard` + `R01UICardsListOrSearchButtons` | `.toolbar`, `.selected`, `.checked`, `.component`, `vaadin-card.component__content` | [grids-cards-collections-and-trees.md](./references/grids-cards-collections-and-trees.md) |
| **Accordion Collection Manager** | `VaadinCollectionManageComponent` / `VaadinCollectionItemSummaryBase` | `.r01-collection-manage`, `.r01-collection-manage-row`, `.r01-collection-manage-summary`, `.r01-collection-manage-item-summary` | [grids-cards-collections-and-trees.md](./references/grids-cards-collections-and-trees.md) |
| **Draggable Hierarchical Tree** | `TreeGrid` (`rowsDraggable`, `dropMode="on-top-or-between"`) | `.r01-hierarchical-data-tree`, `.r01-hierarchical-data-tree-header`, `vaadin-grid-tree-toggle` | [grids-cards-collections-and-trees.md](./references/grids-cards-collections-and-trees.md) |
| **Tags, Chips & Multi-Value Selector** | `VaadinTagListComponent`, `VaadinTagInputField`, `VaadinValueSelectorComponent` | `.r01-tag-list`, `.r01-tag-list-item`, `.r01-tag-input-field`, `.r01-value-selector` | [fields-tags-dates-and-addons.md](./references/fields-tags-dates-and-addons.md) |
| **Date/Time Ranges & Hour:Minute** | `R01UIDateRangeField`, `R01UIDateTimeRangeField`, `R01UIDateNavigatorField`, `R01UIHourMinutePicker`, `R01UIDateTimePicker` | `.r01-hourminute-hour-select`, `.r01-hourminute-minute-select`, `.datetimepicker-time-only` | [fields-tags-dates-and-addons.md](./references/fields-tags-dates-and-addons.md) |
| **External Addons** | CodeMirror, `VaadinCameraField`, TinyMCE R01, elFinder | `.vaadin-camera-field`, `.tinymce`, `.tinymce.click-to-edit`, `.tiny-dialog`, `elFinderConnector` | [fields-tags-dates-and-addons.md](./references/fields-tags-dates-and-addons.md) |
