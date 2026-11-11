# Application Shell, Navigation, Language Tabs & Menus

## 1. Application Shell (`<vaadin-app-layout>`)

Standard R01F Vaadin 25 apps use `<vaadin-app-layout primary-section="navbar">` with three slots:
1. **`slot="navbar"`** — Top primary header bar.
2. **`slot="navbar touch-optimized"`** — Secondary breadcrumb bar immediately below the header.
3. **`slot="drawer"`** — Left navigation drawer with `<vaadin-scroller>` and `<vaadin-side-nav>`.

### 1.1 Top Navbar Structure (`slot="navbar"`)
```html
<vaadin-drawer-toggle slot="navbar" aria-label="Menu toggle"></vaadin-drawer-toggle>
<span class="r01-navbar-title" slot="navbar">App Title</span>
<div class="r01-navbar-right" slot="navbar">
  <!-- Language Switcher (ES | EU) -->
  <vaadin-horizontal-layout class="r01-navbar-lang-selector">
    <vaadin-button class="r01-navbar-lang-link r01-navbar-lang-active" theme="tertiary-inline">ES</vaadin-button>
    <span class="r01-navbar-lang-sep">|</span>
    <vaadin-button class="r01-navbar-lang-link" theme="tertiary-inline">EU</vaadin-button>
  </vaadin-horizontal-layout>

  <!-- Notification Bell + Badge + Popover -->
  <div class="r01-notif-wrapper">
    <vaadin-button class="r01-navbar-icon-btn" theme="tertiary-inline icon">
      <vaadin-icon icon="vaadin:bell"></vaadin-icon>
    </vaadin-button>
    <span class="r01-notif-badge">3</span>
  </div>
  <vaadin-popover position="bottom-end" modal="true">
    <div class="r01-notif-popover">
      <div class="r01-notif-popover-header">Notificaciones pendientes</div>
      <div class="r01-notif-popover-item">
        <vaadin-icon class="r01-notif-item-icon" icon="vaadin:bell"></vaadin-icon>
        <span>Nueva solicitud recibida</span>
      </div>
      <div class="r01-notif-popover-footer">
        <vaadin-button theme="small tertiary-inline">Marcar todas como leídas</vaadin-button>
      </div>
    </div>
  </vaadin-popover>

  <!-- User Profile + Popover -->
  <div class="r01-navbar-user">
    <vaadin-icon icon="vaadin:user"></vaadin-icon>
    <span class="r01-navbar-username">Juan Pérez</span>
  </div>
  <vaadin-popover position="bottom-end" modal="true">
    <div class="r01-user-popover">
      <div class="r01-user-popover-header">
        <span class="r01-user-popover-name">Juan Pérez García</span>
        <span class="r01-user-popover-role">Administrador</span>
      </div>
      <div class="r01-user-popover-details">
        <div class="r01-user-popover-row">
          <span class="r01-user-popover-label">Usuario:</span>
          <span class="r01-user-popover-value">jperez</span>
        </div>
      </div>
      <div class="r01-user-popover-footer">
        <vaadin-button theme="error small tertiary">
          <vaadin-icon icon="vaadin:sign-out" slot="prefix"></vaadin-icon>
          Cerrar sesión
        </vaadin-button>
      </div>
    </div>
  </vaadin-popover>
</div>
```

### 1.2 Secondary Breadcrumb Bar (`slot="navbar touch-optimized"`) & `VaadinBreadcrumbs`
- **Navbar Breadcrumbs**:
  ```html
  <vaadin-horizontal-layout class="r01-breadcrumbs r01-navbar-breadcrumb" slot="navbar touch-optimized" style="align-items:center">
    <a class="r01-breadcrumb-link" href="">Inicio</a>
    <vaadin-icon class="r01-breadcrumb-separator" icon="vaadin:chevron-right"></vaadin-icon>
    <span class="r01-breadcrumb-current">Current View</span>
  </vaadin-horizontal-layout>
  ```
- **In-View `VaadinBreadcrumbs` Component**:
  Uses `<vaadin-button class="r01-breadcrumb-link" theme="tertiary-inline">` for clickable ancestor items, `<vaadin-icon class="r01-breadcrumb-separator" icon="vaadin:chevron-right">`, and `<span class="r01-breadcrumb-current">` for the active page.

### 1.3 Left Navigation Drawer (`slot="drawer"`)
```html
<vaadin-scroller slot="drawer">
  <vaadin-vertical-layout theme="padding spacing" style="width:100%; align-items:stretch">
    <vaadin-side-nav>
      <vaadin-side-nav-item path="tabs">
        <vaadin-icon icon="vaadin:tabs" slot="prefix"></vaadin-icon>
        Tabs con idioma
      </vaadin-side-nav-item>
    </vaadin-side-nav>
    <hr aria-hidden="true" />
    <vaadin-side-nav collapsible="true">
      <span slot="label">Addons externos</span>
      <vaadin-side-nav-item path="codeeditor">
        <vaadin-icon icon="vaadin:code" slot="prefix"></vaadin-icon>
        Editor de código
      </vaadin-side-nav-item>
    </vaadin-side-nav>
  </vaadin-vertical-layout>
</vaadin-scroller>
```

---

## 2. Language-Aware Tabs (`VaadinUILangTabbedView`)

R01F provides 5 standard patterns for multi-language (`SPANISH`, `BASQUE`, etc.) tabbed views:

### Pattern A: Simple Horizontal `TabSheet` with Suffix Copy/Translate Button
- `<vaadin-tabsheet>`
  - `<div slot="prefix">` -> `<vaadin-tabs>` with `<vaadin-tab>SPANISH</vaadin-tab>`, `<vaadin-tab>BASQUE</vaadin-tab>`
  - `<vaadin-button slot="suffix" theme="icon">` with `<vaadin-icon icon="vaadin:exchange">` and `<vaadin-tooltip slot="tooltip" text="Copiar/Traducir a la otra pestaña">`
  - Per-language tab panels (`<div tab="tab-0">`) containing localized fields (`Título`, `Descripción`).

### Pattern B: Tabs with Top-Right Language `MenuBar`
- `<div style="position:relative; width:100%">`
  - `<vaadin-tabsheet>` with localized form panels.
  - `<vaadin-menu-bar class="main-menu-bar inf-lang-menu" style="position:absolute; top:0; right:0">` (allows checking/switching languages from the corner menu).

### Pattern C: Configurable Orientation (`VaadinUILangTabbedView`)
- Supports switching `<vaadin-tabs orientation="horizontal|vertical">` dynamically via a `RadioButtonGroup`.
- Wraps `<vaadin-tabs>` + `<div class="r01-tabbed-content">` inside a `<vaadin-custom-field>`.

### Pattern D: Localized Tree + Detail (`SplitLayout` inside Language Tabs)
- Each language tab contains a `<vaadin-custom-field>` with a `<vaadin-split-layout splitterPosition="45.0">`:
  - **Primary (Left)**: `.r01-hierarchical-data-tree` (header with expand-all, search filter, delete button; draggable `TreeGrid`; footer with `Añadir raíz`, `Añadir hijo`, `Subir`, `Bajar`).
  - **Secondary (Right)**: Node detail form bound to the selected tree node in that language.

### Pattern E: Tabs with Embedded `VaadinLangCopyTranslateComponent`
- Embeds `.r01-lang-copy-translate` directly below or beside the localized tabs for bulk copy/translation between languages.

---

## 3. Menu Components (`/menubar`)

### 3.1 `VaadinMenuBar`
- **Java Class**: `r01fVaadin25UIServerClasses/.../menu/VaadinMenuBar.java`
- **Tag**: `<vaadin-menu-bar theme="icon">`
- **Checked State**: Apply `.r01-menu-item-checked` to checked `MenuItem`s.
- **SubMenu Icons**: Styled automatically by `r01f/components/menu-bar.css` (`vaadin-menu-bar-item vaadin-icon`).

### 3.2 `VaadinLanguageMenuBar`
- **Java Class**: `r01fVaadin25UIServerClasses/.../menu/VaadinLanguageMenuBar.java`
- **Structure**:
  ```html
  <vaadin-horizontal-layout theme="spacing" style="align-items:center">
    <vaadin-menu-bar theme="icon"><!-- sub-items have class .lang-menu-item --></vaadin-menu-bar>
    <span id="r01-lang-menu-status">Idioma seleccionado: SPANISH</span>
  </vaadin-horizontal-layout>
  ```

### 3.3 `VaadinVerticalMenu2Level`
- **Java Class**: `r01fVaadin25UIServerClasses/.../menu/VaadinVerticalMenu2Level.java`
- **Required CSS**: `r01f/components/vertical-menu.css`
- **DOM Structure**:
  ```html
  <div class="r01-vertical-menu">
    <vaadin-vertical-layout class="r01-vertical-menu-section r01-selected">
      <div class="r01-vertical-menu-section-title">
        <div class="r01-vertical-menu-section-icon"><vaadin-icon icon="vaadin:folder"></vaadin-icon></div>
        <span class="r01-vertical-menu-section-caption">Sección 1</span>
        <div class="r01-vertical-menu-section-handler"><vaadin-icon icon="vaadin:angle-down"></vaadin-icon></div>
      </div>
      <vaadin-vertical-layout class="r01-vertical-menu-section-items">
        <vaadin-button class="r01-vertical-menu-item r01-selected" theme="tertiary">Subsección 1.1</vaadin-button>
        <vaadin-button class="r01-vertical-menu-item" theme="tertiary">Subsección 1.2</vaadin-button>
      </vaadin-vertical-layout>
    </vaadin-vertical-layout>
  </div>
  ```

---

## 4. Dashboard Layout (`/dashboard`)

- **Root**: `<vaadin-split-layout style="width:100%; height:100%">`
- **Left Bookmark Rail (`slot="primary"`)**:
  - `<vaadin-vertical-layout theme="padding spacing" style="width:70px">`
  - Circular icon buttons:
    ```html
    <vaadin-button class="round" theme="icon">
      <vaadin-icon icon="vaadin:bookmarks"></vaadin-icon>
      <vaadin-tooltip slot="tooltip" text="Bookmark 1"></vaadin-tooltip>
    </vaadin-button>
    ```
- **Main Content Area (`slot="secondary"`)**:
  - Stacked `<vaadin-vertical-layout class="r01ui-example" theme="padding spacing">` panels:
    1. **Collapsible Filter Panel**: Toggle button (`vaadin:angle-down` / `vaadin:angle-up`, `"Filtros de búsqueda"`) + collapsible `<vaadin-vertical-layout>` with filter inputs and `"Aplicar filtros"` primary button.
    2. **Results Panel**: Header row (`<h3>Elementos</h3>` + `<vaadin-menu-bar class="borderless grid blue">`) + `<vaadin-grid theme="row-stripes" allRowsVisible="true">` + `<span class="r01-no-results-label" hidden>No se encontraron resultados</span>`.
