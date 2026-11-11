# Tag/Value Selectors, Date/Time Pickers & External Addons

## 1. TagList & Value Selector Components (`/taglist`)

### 1.1 `VaadinTagListComponent`
Renders a list of selectable, removable tag chips (with optional separator `|` between chips):
```html
<vaadin-custom-field label="Etiquetas">
  <div class="r01-tag-list" style="display:flex; flex-wrap:wrap; align-items:center; gap:var(--lumo-space-xs, 4px)">
    <div class="r01-tag-list-item" style="display:inline-flex; align-items:center; background-color:var(--lumo-contrast-10pct); border-radius:var(--lumo-border-radius-m, 4px)">
      <vaadin-button class="r01-tag-list-item-delete" theme="small tertiary-inline icon">
        <vaadin-icon icon="vaadin:close-small"></vaadin-icon>
        <vaadin-tooltip slot="tooltip" text="Eliminar etiqueta"></vaadin-tooltip>
      </vaadin-button>
      <vaadin-button class="r01-tag-list-item-label" theme="small tertiary-inline">
        vaadin
        <vaadin-tooltip slot="tooltip" text="Etiqueta: vaadin"></vaadin-tooltip>
      </vaadin-button>
    </div>
    <!-- Optional when withSeparator=true -->
    <span class="r01-tag-list-separator">|</span>
  </div>
</vaadin-custom-field>
```

### 1.2 `VaadinTagInputField`
Combines a `<vaadin-combo-box allowCustomValue>` with an embedded `VaadinTagListComponent` so users can pick suggested tags or type free-form tags:
```html
<vaadin-custom-field label="Añadir etiquetas" style="width:100%">
  <div class="r01-tag-input-field" style="display:flex; flex-wrap:wrap; align-items:center; gap:var(--lumo-space-xs, 4px); width:100%">
    <vaadin-combo-box class="r01-tag-input-text" allowCustomValue placeholder="Escribe y pulsa Enter..."></vaadin-combo-box>
    <div class="r01-tag-input-chips" style="display:flex; flex-wrap:wrap; align-items:center">
      <!-- .r01-tag-list -->
    </div>
  </div>
</vaadin-custom-field>
```

### 1.3 `VaadinValueSelectorComponent`
Selects items from a predefined catalog `<vaadin-combo-box>` into a chip list, with Clear All (`vaadin:close-big`) and Select All (`vaadin:check-square-o`) buttons:
```html
<vaadin-custom-field label="Seleccionar tecnologías" style="width:100%">
  <div class="r01-value-selector" style="display:flex; flex-wrap:wrap; align-items:center; gap:var(--lumo-space-xs, 4px); width:100%">
    <vaadin-combo-box class="r01-value-selector-combo" placeholder="Seleccionar..."></vaadin-combo-box>
    <div class="r01-value-selector-buttons" style="display:inline-flex; gap:2px">
      <vaadin-button class="r01-value-selector-btn" theme="small tertiary-inline icon">
        <vaadin-icon icon="vaadin:close-big"></vaadin-icon>
        <vaadin-tooltip slot="tooltip" text="Borrar todos"></vaadin-tooltip>
      </vaadin-button>
      <vaadin-button class="r01-value-selector-btn" theme="small tertiary-inline icon">
        <vaadin-icon icon="vaadin:check-square-o"></vaadin-icon>
        <vaadin-tooltip slot="tooltip" text="Seleccionar todos"></vaadin-tooltip>
      </vaadin-button>
    </div>
    <!-- .r01-tag-list of selected items -->
  </div>
</vaadin-custom-field>
```

---

## 2. Date & Time Components (`/dates`)

| Component Class | Value Type | Structure & Features |
| :--- | :--- | :--- |
| **`R01UIDateRangeField`** | `Range<LocalDate>` | `<vaadin-custom-field>` -> `<vaadin-horizontal-layout theme="spacing">` with two `<vaadin-date-picker>` (`placeholder="Desde"`, `placeholder="Hasta"`, `dd/MM/yyyy` format, auto-synced min/max). |
| **`R01UIDateTimeRangeField`** | `Range<LocalDateTime>` | `<vaadin-custom-field>` -> `<vaadin-horizontal-layout theme="spacing">` with two `<vaadin-date-time-picker step="60.0">` (`Desde` / `Hasta`, auto-synced min/max). |
| **`R01UIDateNavigatorField`** | `LocalDate` | `<vaadin-custom-field>` -> `<vaadin-horizontal-layout theme="spacing">` with prev button (`vaadin:angle-left`, `theme="icon tertiary"`), `<vaadin-date-picker>`, and next button (`vaadin:angle-right`, `theme="icon tertiary"`). |
| **`R01UIHourMinutePicker`** | `LocalTime` | `<vaadin-custom-field>` -> `<vaadin-horizontal-layout style="align-items:center">` with `<vaadin-select class="r01-hourminute-hour-select" style="width:5.5em">`, `<span>:</span>`, and `<vaadin-select class="r01-hourminute-minute-select" style="width:5.5em">`. Minute resolution configurable (`0, 1, 5, 10, 15, 20, 30`). |
| **`R01UIDateTimePicker`** | `LocalDateTime` | Combines `<vaadin-date-picker>` + `R01UIHourMinutePicker` inside a single `<vaadin-custom-field>` with configurable minute step resolution. |

---

## 3. External Addons (`/codeeditor`, `/camera`, `/richtext`, `/fileexplorer`)

### 3.1 CodeMirror Code Editor (`/codeeditor`)
- **Client Connector**: `window.CodeMirrorConnector`
- **DOM**: `<vaadin-custom-field>` containing a `<div style="border:1px solid #ccc">` initialized with:
  - `mode`: `"javascript"`, `"text/xml"`, `"text/x-java"`, etc.
  - `theme`: `"default"`
  - `lineNumbers`: `true`, `lineWrapping`: `true`, `foldGutter`: `true`, `readOnly`: `false|true`.

### 3.2 Webcam & Photo Upload Field (`/camera`)
- **Client Connector**: `globalThis.VaadinCameraDialog`
- **DOM Structure**:
  ```html
  <vaadin-custom-field class="vaadin-camera-field">
    <vaadin-vertical-layout theme="padding spacing">
      <vaadin-horizontal-layout class="vaadin-camera-field__image-layout" theme="spacing">
        <!-- Captured/uploaded image thumbnails + .vaadin-camera-field__main-image-button + .vaadin-camera-field__move-buttons + .vaadin-camera-field__delete-image-button -->
      </vaadin-horizontal-layout>
      <vaadin-horizontal-layout theme="spacing">
        <vaadin-button class="vaadin-camera-field__take-image-button" theme="primary">Sacar foto</vaadin-button>
        <vaadin-upload class="vaadin-camera-field__upload" accept="image/*" maxFiles="1"></vaadin-upload>
      </vaadin-horizontal-layout>
    </vaadin-vertical-layout>
  </vaadin-custom-field>
  ```

### 3.3 TinyMCE Rich Text Editor (`/richtext`)
- **Client Connectors**: `window.Vaadin.Flow.tinymceConnector` and `window.Vaadin.Flow.tinyMceR01Connector`
- **Two Variants**:
  1. **Toolbar Edit Button (`class="tinymce"`)**:
     ```html
     <vaadin-custom-field class="tinymce" label="HTML Editor" style="width:100%; height:100%">
       <vaadin-horizontal-layout class="html-editor-toolbar" theme="spacing">
         <vaadin-button class="ml-auto" theme="primary">Editar</vaadin-button>
       </vaadin-horizontal-layout>
       <div class="output"></div>
     </vaadin-custom-field>
     ```
  2. **Click-to-Edit (`class="tinymce click-to-edit"`)**:
     Same structure, plus `role="button" tabindex="0"` on `<div class="output">` so clicking the rendered HTML directly opens the `<vaadin-dialog class="tiny-dialog" width="800px" height="625px">` modal containing `<div class="tiny">` with R01 plugins (`r01advlist`, `r01image`, `r01code`, `r01templates`, `r01urlbuilder`, `r01linkanchor`, `axe_frame`).

### 3.4 elFinder File Explorer (`/fileexplorer`)
- **Client Connector**: `globalThis.elFinderConnector`
- Opens a draggable, resizable `<vaadin-dialog theme="no-padding" width="clamp(320px, 80vw, 1200px)" height="clamp(400px, 70vh, 900px)" draggable="true" resizable="true" noCloseOnOutsideClick="true">`:
  - **Full Explorer Mode (`Open File Explorer`)**: Full toolbar (`back/forward`, `mkdir/mkfile/upload`, `open/download/getfile`, `info`, `quicklook`, `copy/cut/paste`, `rm`, `duplicate/rename/edit/resize`, `extract/archive`, `search`, `view`, `help`).
  - **File Picker Mode (`Select file`)**: Reduced toolbar + footer button `<vaadin-button id="accept-file" theme="primary" style="align-self:flex-end">Seleccionar fichero</vaadin-button>`.
