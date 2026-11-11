# Forms, Contact Info (NORA), Action Buttons, Dialogs & Notifications

## 1. Forms & Infrastructure (`/forms-infra`)

### 1.1 Dirty-State Badge & Responsive `FormLayout`
- **Header with Change-Tracking Badge**:
  ```html
  <vaadin-horizontal-layout theme="spacing" style="align-items:center">
    <h2>Formulario de referencia</h2>
    <span theme="badge success">Sin cambios</span> <!-- Changes to warning/unsaved when Binder has changes -->
  </vaadin-horizontal-layout>
  ```
- **Responsive Multi-Column `FormLayout`**:
  ```html
  <vaadin-form-layout maxColumns="3" autoResponsive="true" columnWidth="16em" expandColumns="true" expandFields="true" labelsAside="false">
    <vaadin-form-row>
      <vaadin-text-field label="Nombre completo" required colspan="2"></vaadin-text-field>
      <vaadin-integer-field label="Edad" has-controls min="0" max="150"></vaadin-integer-field>
    </vaadin-form-row>
    <vaadin-form-row>
      <vaadin-text-field label="Correo electrónico"></vaadin-text-field>
      <vaadin-date-picker label="Fecha de nacimiento"></vaadin-date-picker>
      <vaadin-multi-select-combo-box label="Etiquetas"></vaadin-multi-select-combo-box>
    </vaadin-form-row>
    <vaadin-form-row>
      <!-- Nested Subform with its own internal Binder -->
      <vaadin-custom-field label="Dirección (subformulario con Binder propio)" colspan="3">
        <vaadin-form-layout maxColumns="3" autoResponsive="true" columnWidth="12em" expandColumns="true" expandFields="true">
          <vaadin-form-row>
            <vaadin-text-field label="Calle"></vaadin-text-field>
            <vaadin-text-field label="Ciudad"></vaadin-text-field>
            <vaadin-text-field label="Código Postal"></vaadin-text-field>
          </vaadin-form-row>
        </vaadin-form-layout>
      </vaadin-custom-field>
    </vaadin-form-row>
  </vaadin-form-layout>
  <!-- Cross-Field Validation Error Output -->
  <div class="text-error"></div>
  ```

---

## 2. Contact Information & NORA Geographic Address (`/contact`)

### 2.1 `VaadinContactInfoManageComponent` Overview
Composes three white-bordered sections inside `<vaadin-vertical-layout class="no-padding" theme="padding spacing">`:
1. **Geographic Address (`VaadinNORAContactComponent`)**
2. **Contact Channels CRUD Grids (`.r01ui-object-grid-component`)** for:
   - `CORREOS ELECTRÓNICOS`
   - `TELÉFONOS`
   - `REDES SOCIALES`
   - `PORTALES WEB`
3. **Preferred Language (`Idioma preferido`)**

### 2.2 `VaadinNORAContactComponent` Structure
- Wrapped in `<vaadin-custom-field label="Dirección geográfica">` -> `<vaadin-horizontal-layout class="r01ui-white-bordered-layout" theme="padding spacing">`:
  - **Left Column (Cascading NORA Fields)**:
    - **Postal Code Search**: `<vaadin-text-field label="Código Postal">` + `<vaadin-button class="localizacion_boton" theme="icon"><vaadin-icon icon="vaadin:search"></vaadin-icon></vaadin-button>` + Clear button (`vaadin:eraser`, tooltip `"Limpiar"`).
    - **Cascading Hierarchy** (each field is a `<vaadin-custom-field>` containing both a `<vaadin-combo-box>` and a hidden `<vaadin-text-field>` for manual entry fallback):
      - Row 1: `País` (`flex-grow:2`) + `Comunidad Autónoma` (`flex-grow:3`)
      - Row 2: `Provincia` (`flex-grow:2`) + `Municipio` (`flex-grow:3`)
      - Row 3: `Localidad` (`flex-grow:2`) + `Calle` (`<vaadin-combo-box class="combobox-suggestion inline-icon" placeholder="Filtrar por texto">` + `VaadinToggleButton` with `vaadin:close-circle` and tooltip `"Alternar modo de entrada de calle"`).
      - Row 4: `Portal` (`flex-grow:2`) + `<vaadin-text-field label="Edificio/Piso/Puerta" style="flex-grow:3">`.
    - **Geo-Coordinates Row**:
      - System selector `<vaadin-combo-box>` (`ETRS89`) + `<vaadin-icon icon="vaadin:info">` + coordinates `<vaadin-text-field>` + search `<vaadin-button theme="icon"><vaadin-icon icon="vaadin:search"></vaadin-icon></vaadin-button>`.
  - **Right Column (Map Iframe)**:
    - `<iframe style="width:600px; height:400px; align-self:center"></iframe>`

### 2.3 Contact Channel Grid & Modal Editor (`.r01ui-object-grid-component`)
- **Channel Block**:
  ```html
  <vaadin-vertical-layout class="r01ui-object-grid-component" theme="padding spacing" style="width:100%">
    <vaadin-horizontal-layout class="lbl-add-button" theme="spacing" style="align-items:center">
      <span>CORREOS ELECTRÓNICOS</span>
      <vaadin-button class="add-button" theme="icon">
        <vaadin-icon icon="vaadin:plus"></vaadin-icon>
      </vaadin-button>
    </vaadin-horizontal-layout>
    <vaadin-grid></vaadin-grid>
    <span class="grid-lbl-no-results no-margin-top no-margin-bottom">No se encontraron resultados</span>
  </vaadin-vertical-layout>
  ```
- **Popup Editor Dialog (`.r01ui-create-edit-detail-view`)**:
  ```html
  <vaadin-dialog opened>
    <vaadin-vertical-layout theme="padding spacing" style="width:100%">
      <vaadin-vertical-layout class="r01ui-create-edit-detail-view" theme="padding spacing" style="width:100%">
        <vaadin-horizontal-layout theme="spacing" style="width:100%">
          <vaadin-combo-box label="Uso" required style="width:100%"></vaadin-combo-box>
          <vaadin-text-field label="Correo electrónico" required style="width:100%"></vaadin-text-field>
        </vaadin-horizontal-layout>
        <vaadin-horizontal-layout theme="spacing">
          <vaadin-checkbox class="checkbox-default" label="Predeterminado"></vaadin-checkbox>
          <vaadin-checkbox class="checkbox-default" label="Privado"></vaadin-checkbox>
        </vaadin-horizontal-layout>
      </vaadin-vertical-layout>
      <!-- VaadinAcceptCancelDeleteButtons footer -->
    </vaadin-vertical-layout>
  </vaadin-dialog>
  ```

---

## 3. Action Buttons (`/buttons`)

### 3.1 `VaadinToggleButton`
```html
<vaadin-custom-field class="toggle-button">
  <vaadin-horizontal-layout theme="spacing" style="align-items:flex-end">
    <span class="v-caption">Activar filtro</span>
    <vaadin-button theme="icon tertiary">
      <vaadin-icon icon="vaadin:unlock"></vaadin-icon>
    </vaadin-button>
  </vaadin-horizontal-layout>
  <vaadin-tooltip slot="tooltip" text="Haz clic para cambiar el estado"></vaadin-tooltip>
</vaadin-custom-field>
```

### 3.2 `VaadinAcceptCancelDeleteButtons` & `VaadinAcceptCancelDeleteButtonsWithDeleteConfirmation`
- **Modes**:
  - **Creating (`Nuevo registro`)**: `Eliminar` is hidden; `Cancelar` and `Guardar` (`theme="primary"`) are visible.
  - **Editing (`Editando registro`)**: `Eliminar` (`theme="error"`), `Cancelar`, and `Guardar` (`theme="primary"`) are all visible.
  - **Disabled (`Deshabilitado`)**: All three buttons carry `disabled`.
- **With Delete Confirmation**: Adds `<vaadin-confirm-dialog header="Confirmar eliminación" message="¿Estás seguro de que deseas eliminar este registro?" confirmText="Eliminar" confirmTheme="error primary" cancelText="Cancelar" cancelButtonVisible="true">`.

### 3.3 `VaadinDownloadButton`
Built on Vaadin 25 `Anchor` + `DownloadHandler`:
```html
<a class="r01-download-button" href="VAADIN/dynamic/resource/..." router-ignore="">
  <span>Descargar CSV</span>
  <vaadin-icon class="r01-download-button-icon" icon="vaadin:download"></vaadin-icon>
</a>
```

---

## 4. Text-Verified Delete Confirmation Dialog (`/catalog`)

### `VaadinDeleteConfirmDialogVerificationBase`
Requires the user to type an exact verification word (e.g. `"ELIMINAR"`) before enabling the confirm button:
```html
<vaadin-confirm-dialog header="Confirmar eliminación" cancelText="Cancelar" cancelButtonVisible="true">
  <vaadin-button slot="confirm-button" theme="error primary" disabled>Eliminar</vaadin-button>
  <vaadin-vertical-layout theme="spacing" style="width:100%">
    <p class="r01-delete-confirm-message">Esta acción es irreversible. Para confirmar, escribe la palabra de verificación.</p>
    <span class="r01-delete-confirm-hint">Escribe "ELIMINAR" para confirmar</span>
    <vaadin-text-field class="r01-delete-confirm-field" style="width:100%"></vaadin-text-field>
  </vaadin-vertical-layout>
</vaadin-confirm-dialog>
```

---

## 5. Language Copy / Translate (`/langtranslate`)

### `VaadinLangCopyTranslateComponent`
```html
<vaadin-custom-field label="Copiar / Traducir contenido entre idiomas" style="width:100%">
  <div class="r01-lang-copy-translate" style="display:flex; flex-direction:column; gap:var(--lumo-space-s, 8px); width:100%">
    <div class="r01-lang-copy-translate-langs" style="display:flex; flex-wrap:wrap; gap:var(--lumo-space-m, 16px); align-items:baseline">
      <vaadin-combo-box label="Idioma origen"></vaadin-combo-box>
      <vaadin-combo-box label="Idioma destino"></vaadin-combo-box>
      <vaadin-combo-box label="Modo"><!-- Copiar | Traducir --></vaadin-combo-box>
    </div>
    <vaadin-text-area label="Texto origen" style="width:100%"></vaadin-text-area>
    <div class="r01-lang-copy-translate-actions" style="display:flex; gap:var(--lumo-space-s, 8px)">
      <vaadin-button theme="primary">
        <vaadin-icon icon="vaadin:copy" slot="prefix"></vaadin-icon>
        Ejecutar
      </vaadin-button>
    </div>
    <vaadin-text-area label="Resultado" readonly style="width:100%"></vaadin-text-area>
  </div>
</vaadin-custom-field>
```

---

## 6. Notifications (`/notifications`)

- **Positions**: `TOP_STRETCH`, `TOP_START`, `TOP_CENTER`, `TOP_END`, `MIDDLE`, `BOTTOM_START`, `BOTTOM_CENTER`, `BOTTOM_END`, `BOTTOM_STRETCH`.
- **Theme Variants (`NotificationVariant`)**:
  - Default (no theme)
  - `LUMO_SUCCESS` (`theme="success"`)
  - `LUMO_WARNING` (`theme="warning"`)
  - `LUMO_ERROR` (`theme="error"`)
  - `LUMO_CONTRAST` (`theme="contrast"`)
  - `LUMO_PRIMARY` (`theme="primary"`)
