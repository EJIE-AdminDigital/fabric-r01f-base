# R01F CSS Variables & Utility Classes (`r01f-base-styles.css`)

## 1. Stylesheet Import Structure

`r01f-base-styles.css` is the single entry point for all R01F Vaadin 25 styles:
```css
@import './r01f/variables.css';
@import './r01f/utilities.css';
@import './r01f/layouts/spacing.css';
@import './r01f/components/vertical-menu.css';
@import './r01f/components/menu-bar.css';
@import './r01f/components/datetime-fields.css';
@import './r01f/components/buttons.css';
@import './r01f/components/grid.css';
@import './r01f/components/forms.css';
@import './r01f/components/breadcrumb.css';
@import './r01f/components/paging.css';
@import './r01f/components/dialog.css';
@import './r01f/components/collectionmanage.css';
@import './r01f/components/cardsboard.css';
```

To retheme a WAR, import `./r01f-base-styles.css` in `styles.css` and override `--r01-color-primary` in `:root`.

---

## 2. CSS Custom Properties (`r01f/variables.css`)

```css
:root {
    /* Primary palette (override per WAR) */
    --r01-color-primary:                #1C6EA4;
    --r01-color-primary-50pct:          rgba(28, 110, 164, 0.5);
    --r01-color-primary-10pct:          rgba(28, 110, 164, 0.1);
    --r01-color-primary-contrast:       #ffffff;

    /* Navbar & Notification Badge */
    --r01-color-navbar-bg:              var(--r01-color-primary);
    --r01-color-navbar-text:            #ffffff;
    --r01-color-badge-bg:               #e53935;
    --r01-color-badge-text:             #ffffff;

    /* Selection & highlight */
    --r01-color-selection:              #007ca9;
    --r01-color-selection-highlight:    #8CC4D9;
    --r01-color-text-dark:              #3a3a3a;

    /* Grid / table colors */
    --r01-grid-bg:                      #f8f9f9;
    --r01-grid-row-hover:               #eceeee;
    --r01-grid-row-selected:            #4da1bf;
    --r01-grid-row-selected-hover:      #76b5cb;
    --r01-grid-border:                  #e1e3e5;
    --r01-grid-caption-color:           #838c91;
    --r01-grid-text-color:              #3a3a3a;
    --r01-grid-text-bold-color:         #3c3c3c;

    /* Vertical menu */
    --r01-vmenu-section-hover-bg:       var(--r01-color-primary-10pct);
    --r01-vmenu-selected-bg:            var(--r01-color-primary-10pct);
    --r01-vmenu-item-selected-bg:       var(--r01-color-primary);
    --r01-vmenu-item-selected-text:     #ffffff;

    /* Collection management */
    --r01-collection-summary-bg:          beige;
    --r01-collection-summary-selected-bg: burlywood;
}
```

---

## 3. Utility, Layout, Form & Button Classes

### 3.1 Display, Spacing & Containers (`r01f/utilities.css`, `r01f/layouts/spacing.css`)
- **Visibility & Scroll**: `.r01-hide` (`display: none`), `.r01-overflow-y-auto` (`overflow-y: auto`)
- **Remove Padding**: `.r01-no-padding`, `.r01-no-padding-top`, `.r01-no-padding-left`, `.r01-no-padding-right`, `.r01-no-padding-bottom` (also unprefixed `.no-padding`, `.no-padding-top`, `.no-padding-left` in legacy/contact views)
- **Add Padding**: `.r01-padding-top` (`20px`), `.r01-padding-bottom` (`20px`), `.r01-margin-tiny` (`5px`), `.r01-margin-little` (`12px 15px`), `.r01-margin-small` (`15px`)
- **Remove Margin**: `.r01-no-margin-top`, `.r01-no-margin-bottom` (and `.no-margin-top`, `.no-margin-bottom`)
- **Bordered & Collapsible Panels**:
  - `.r01-white-bordered` / `.r01ui-white-bordered-layout` (`background: white; border: 1px solid lightgrey`)
  - `.r01-collapsable-border-layout` (`background: #ECEFEF; border-radius: 4px; padding: 10px 5px`) + inner `.r01-content-layout` (`background: #FFF; border: 1px solid #d5d5d5; padding: 10px 20px`)
  - `.r01-splitpanel-first-container-edit-view` (`width: 277px !important`)

### 3.2 Typography (`r01f/utilities.css`, `r01f/components/forms.css`)
- `.r01-text-dark`, `.r01-text-highlight`, `.r01-text-bold`, `span.r01-highlight`, `.r01-upper-case`, `.r01-word-wrap`
- `.r01-label-with-icon` (bold primary-colored label with `24px` `vaadin-icon`)

### 3.3 Form Field Sizing & Options (`r01f/components/forms.css`)
- **ComboBoxes**: `.r01-combo-medium` / `.combo-medium` (`220px`), `.r01-combo-big` (`550px`), `vaadin-combo-box.r01-inline-icon-input`
- **TextFields / TextAreas**: `.r01-input-medium` (`220px`), `.r01-input-big` (`550px`), `.r01-input-huge` (`650px`), `.r01-textarea-big` (`550px`)
- **DatePickers**: `.r01-datefield-medium` (`300px`)
- **Checkboxes & OptionGroups**: `.r01-checkbox-default` / `.checkbox-default`, `.r01-optiongroup-inline`
- **Detail Views**: `.r01-create-edit-detail-view` / `.r01ui-create-edit-detail-view`, `.r01-edit-detail-window`

### 3.4 Buttons (`r01f/components/buttons.css`)
- **Action Buttons**: `.r01-add-button` / `.add-button`, `.r01-lbl-add-button` / `.lbl-add-button`, `.r01-button-borderless-grey`, `.r01-secondary-button`, `.r01-primary-button`, `a.r01-download-button`
- **Identity / Login Provider Buttons**:
  - `vaadin-button.r01-xlnets-button`, `vaadin-button.r01-elkarlan-button` (`rgb(24, 80, 120)`)
  - `vaadin-button.r01-google-button` (`rgb(181, 52, 42)`)
  - `vaadin-button.r01-giltza-button` (`#bfcbce`, black text)
  - `vaadin-button.r01-justizia-button` (`rgb(30, 34, 170)`)
  - `vaadin-button.r01-osakidetza-button` (`rgb(0, 119, 73)`)
