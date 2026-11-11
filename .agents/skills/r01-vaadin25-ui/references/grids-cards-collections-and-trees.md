# Grids, CardsBoard, File Grids, Collection Management & Trees

## 1. Styled Grids, Row Menus, Pagination & Image Popups (`/grids`, `/catalog`, `/images`)

### 1.1 R01F Styled Grid (`vaadin-grid.r01ui-grid`)
- Add class `r01ui-grid` to `<vaadin-grid>` to apply R01F row heights (`55px`), custom header borders, row hover (`--r01-grid-row-hover`), and selected row styling (`--r01-grid-row-selected`).
- Optional modifier classes:
  - `.r01ui-grid-no-border` — removes outer grid border.
  - `.r01ui-grid-cell-no-border` — removes cell borders.
  - `.r01-grid-no-results-label` / `.grid-lbl-no-results` — styled empty-state label below a hidden/empty grid.

### 1.2 Row Three-Dots Action Menu (`VaadinDotsVMenuBase`)
Used inside grid action columns or list rows:
```html
<vaadin-menu-bar class="r01-dots-vmenu" theme="tertiary icon">
  <!-- Single root item with vaadin:ellipsis-dots-v icon and submenu items -->
</vaadin-menu-bar>
```

### 1.3 Pagination Bar (`VaadinPagingComponent`)
```html
<vaadin-horizontal-layout class="r01-paging" style="align-items:center">
  <vaadin-button class="r01-paging-nav-btn" theme="tertiary-inline icon" disabled>
    <vaadin-icon icon="vaadin:angle-double-left"></vaadin-icon>
  </vaadin-button>
  <vaadin-button class="r01-paging-nav-btn" theme="tertiary-inline icon" disabled>
    <vaadin-icon icon="vaadin:angle-left"></vaadin-icon>
  </vaadin-button>
  <span class="r01-paging-info">Página 1 de 25</span>
  <vaadin-button class="r01-paging-nav-btn" theme="tertiary-inline icon">
    <vaadin-icon icon="vaadin:angle-right"></vaadin-icon>
  </vaadin-button>
  <vaadin-button class="r01-paging-nav-btn" theme="tertiary-inline icon">
    <vaadin-icon icon="vaadin:angle-double-right"></vaadin-icon>
  </vaadin-button>
  <span class="r01-paging-size-label">Mostrar:</span>
  <vaadin-select class="r01-paging-size-select"><!-- 10, 25, 50, 100 --></vaadin-select>
</vaadin-horizontal-layout>
```

### 1.4 Image Upload & `VaadinImagePopup` (`/images`, `/grids`)
- Upload: `<vaadin-upload accept="image/jpeg,image/png,image/gif" maxFiles="1">`.
- Clicking a thumbnail or grid row action opens `VaadinImagePopup` (a `<vaadin-dialog>` displaying the full-resolution image and caption).

---

## 2. CardsBoard & View Switcher (`R01UICardsBoard`, `R01UICardsListOrSearchButtons`)

Allows users to toggle between a draggable **Cards view**, a **Grid/List view**, and a collapsible **Search filter panel**:

```html
<vaadin-vertical-layout theme="spacing" style="width:100%">
  <!-- R01UICardsListOrSearchButtons Toolbar -->
  <vaadin-horizontal-layout class="toolbar">
    <vaadin-button class="selected" theme="tertiary-inline icon">
      <vaadin-icon icon="vaadin:grid-small"></vaadin-icon>
    </vaadin-button>
    <vaadin-button theme="tertiary-inline icon">
      <vaadin-icon icon="vaadin:lines"></vaadin-icon>
    </vaadin-button>
    <vaadin-button class="checked" theme="tertiary-inline icon" style="flex-grow:10">
      <vaadin-icon icon="vaadin:search"></vaadin-icon>
    </vaadin-button>
  </vaadin-horizontal-layout>

  <!-- Collapsible Search Filter Panel (shown when search button is .checked) -->
  <vaadin-vertical-layout hidden></vaadin-vertical-layout>

  <!-- Alternative Grid View (shown when vaadin:lines is .selected) -->
  <vaadin-grid hidden></vaadin-grid>

  <!-- R01UICardsBoard Container (shown when vaadin:grid-small is .selected) -->
  <div style="width:100%; height:100%">
    <div class="component">
      <vaadin-card class="component__content" draggable="true">
        <div class="component__content__tags" slot="header">
          <span class="component__content__update-info">
            <vaadin-icon icon="vaadin:file-font"></vaadin-icon> Euskadi
          </span>
          <vaadin-menu-bar></vaadin-menu-bar>
        </div>
        <div style="display:contents">
          <div class="component__content__basic-info">
            <vaadin-horizontal-layout theme="spacing" style="width:100%; height:100%">
              <div class="component__content__image-container" style="flex-grow:0">
                <img class="component__content__icon" src="..." alt="icon" style="width:80px" />
              </div>
              <vaadin-vertical-layout style="flex-grow:1; width:100%">
                <span class="component__content__info">Euskadi</span>
                <span class="component__content__summary">https://www.euskadi.eus</span>
              </vaadin-vertical-layout>
            </vaadin-horizontal-layout>
          </div>
        </div>
      </vaadin-card>
    </div>
  </div>
</vaadin-vertical-layout>
```

---

## 3. File Attachment Grid (`#r01fui-filelist` on `/grids`)

```html
<vaadin-custom-field style="width:100%">
  <vaadin-vertical-layout id="r01fui-filelist" theme="padding spacing" style="width:100%">
    <vaadin-vertical-layout theme="padding spacing" style="width:100%; height:100%">
      <vaadin-horizontal-layout theme="spacing" style="justify-content:space-between; width:100%">
        <span>Ficheros adjuntos</span>
        <vaadin-button theme="primary">Nuevo fichero</vaadin-button>
      </vaadin-horizontal-layout>
      <vaadin-grid allRowsVisible="true" style="width:100%">
        <!-- Columns: Fecha (width=9em), Título del fichero, Nombre del fichero, Acciones -->
      </vaadin-grid>
    </vaadin-vertical-layout>
  </vaadin-vertical-layout>
</vaadin-custom-field>
```

---

## 4. Accordion Collection Manager (`VaadinCollectionManageComponent`)

Manages an ordered list of items where each item has a one-line summary bar (`VaadinCollectionItemSummaryBase`) and an expandable inline detail editor:

```html
<vaadin-vertical-layout class="r01-collection-manage" theme="spacing" style="width:100%">
  <!-- Header Action Bar -->
  <vaadin-horizontal-layout style="justify-content:flex-end; width:100%">
    <!-- Optional Up/Down arrows in header when configured -->
    <vaadin-button theme="primary">
      <vaadin-icon icon="vaadin:plus-square-o" slot="prefix"></vaadin-icon>
      Nuevo
    </vaadin-button>
  </vaadin-horizontal-layout>

  <!-- Rows Container -->
  <vaadin-vertical-layout style="width:100%">
    <vaadin-vertical-layout class="r01-collection-manage-row" theme="spacing" style="width:100%">
      <vaadin-horizontal-layout class="r01-collection-manage-summary" theme="spacing" style="align-items:center; width:100%">
        <vaadin-button theme="icon tertiary">
          <vaadin-icon icon="vaadin:chevron-down"></vaadin-icon>
        </vaadin-button>
        <span class="r01-collection-manage-item-summary" style="cursor:pointer; flex-grow:1">Euskadi</span>
        <vaadin-button theme="icon tertiary">
          <vaadin-icon icon="vaadin:edit"></vaadin-icon>
          <vaadin-tooltip slot="tooltip" text="Editar"></vaadin-tooltip>
        </vaadin-button>
        <vaadin-button theme="icon tertiary">
          <vaadin-icon icon="vaadin:trash"></vaadin-icon>
          <vaadin-tooltip slot="tooltip" text="Quitar"></vaadin-tooltip>
        </vaadin-button>
      </vaadin-horizontal-layout>
      <!-- Expandable Inline Detail Editor (hidden when collapsed) -->
      <vaadin-vertical-layout hidden></vaadin-vertical-layout>
    </vaadin-vertical-layout>
  </vaadin-vertical-layout>
</vaadin-vertical-layout>
```

---

## 5. Hierarchical Draggable Tree (`/tree`)

```html
<vaadin-vertical-layout class="r01-hierarchical-data-tree" theme="spacing" style="width:100%">
  <!-- Header Toolbar: Expand/Collapse All + Search Filter + Delete -->
  <vaadin-horizontal-layout class="r01-hierarchical-data-tree-header" theme="spacing" style="align-items:center; width:100%">
    <vaadin-button>
      <vaadin-icon icon="vaadin:expand-square" slot="prefix"></vaadin-icon>
      Expandir todo
    </vaadin-button>
    <vaadin-text-field placeholder="Filtrar..." clear-button-visible style="flex-grow:1">
      <vaadin-icon icon="vaadin:search" slot="prefix"></vaadin-icon>
    </vaadin-text-field>
    <vaadin-button theme="error" disabled>
      <vaadin-icon icon="vaadin:trash" slot="prefix"></vaadin-icon>
      Eliminar
    </vaadin-button>
  </vaadin-horizontal-layout>

  <!-- TreeGrid with Drag & Drop -->
  <vaadin-grid rowsDraggable="true" dropMode="on-top-or-between" allRowsVisible="true" style="width:100%">
    <!-- LitRenderer column using <vaadin-grid-tree-toggle> -->
  </vaadin-grid>
</vaadin-vertical-layout>
```
