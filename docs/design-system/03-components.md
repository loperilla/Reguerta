# Regüerta Design System — Components

Catálogo de componentes reutilizables y **reglas de uso**. Aquí no hay tamaños mágicos ni colores sueltos: todo se apoya en **ReguertaTypography**, **Dimens** y el **ColorScheme** de Material 3.

> Si algo no cuadra de tamaño o color, **no** lo hackees en la pantalla: ajusta el rol tipográfico en `ReguertaTypography` o el token en `Dimens`. Así toda la app se corrige a la vez.

---

## 0. Convenciones generales
- Espaciados: `Dimens.Spacing.xxs/xs/sm/md/lg/xl`.
- Tipografía: usa **roles** → `MaterialTheme.typography.<rol>`; si necesitas afinar puntualmente, `style.copy(fontSize = …)`.
- Colores en contenido: cuando el contenedor define `contentColor` (Button, Dialog, TopBar), usa **`Color.Unspecified`** en `Text/Icon` para **heredar** el color correcto.
- Accesibilidad: `contentDescription` en imágenes/íconos, tamaño táctil ≥ **40–48dp**.
- Insets: los **componentes no añaden insets**. La pantalla (Scaffold) gestiona `imePadding()`/`navigationBarsPadding()`.

---

## 1. Top Bars
### `ReguertaTopBar` / `ReguertaHomeTopBar`
**Uso básico**
```kotlin
ReguertaTopBar(
  topBarText = "Mi pantalla",
  navActionClick = onBack
)
```
**Colores y scroll**
```kotlin
val scroll = TopAppBarDefaults.enterAlwaysScrollBehavior()
ReguertaScaffold(
  scrollBehavior = scroll,
  topBar = {
    ReguertaTopBar(
      topBarText = "Mi pantalla",
      navActionClick = onBack,
      scrollBehavior = scroll // sincroniza con Scaffold
    )
  }
) { inner -> /* contenido */ }
```
**Reglas**
- `colors = Dimens.Components.TopBar.colors()` (ya aplicado por defecto).
- Iconos a `Dimens.Components.TopBar.iconSize` (leading y action).
- No añadir paddings extras ni `statusBarsPadding()`.

---

## 2. Botones
### `ReguertaButton(btnType: UiType)` / `InverseReguertaButton(btnType: UiType)`
- Texto → `Dimens.Components.Button.labelStyle / secondaryLabelStyle`.
- Altura mínima → `Dimens.Components.Button.minHeight`.
- Iconos → `Dimens.Components.Button.iconSize`.
- Colores por semántica → `UiType { INFO, ERROR, WARNING }`.

```kotlin
ReguertaButton(
  textButton = "Guardar",
  onClick = onSave,
  btnType = UiType.INFO,
)

// Con icono en el contenido
ReguertaButton(textButton = "Eliminar", onClick = onDelete, btnType = UiType.ERROR) {
  Icon(
    Icons.Default.Delete,
    contentDescription = null,
    modifier = Modifier.size(Dimens.Components.Button.iconSize),
    tint = Color.Unspecified // hereda del botón
  )
}
```
**Do**
- `fillMaxWidth()` en barras inferiores; contenedor con `navigationBarsPadding()`.
**Don’t**
- No fijes `textColor`; usa `Color.Unspecified` para heredar.

---

## 3. Inputs
### `ReguertaInput` / `SecondaryReguertaInput`
- `shape = RoundedCornerShape(Dimens.Components.Input.cornerRadius)`.
- `textStyle = Dimens.Components.Input.textStyle`.
- Errores en `supportingText { }` con `Dimens.Components.Input.supportingStyle`.
- Mostrar error cuando `touched && hasError`.

```kotlin
var touched by remember { mutableStateOf(false) }
val showError = touched && uiError.hasError

ReguertaInput(
  text = email,
  onValueChanged = { email = it },
  placeholderText = "tu@email.com",
  isError = showError,
  supportingText = {
    if (showError) TextBody(uiError.message, style = Dimens.Components.Input.supportingStyle)
  },
  onFocusChanged = { if (!it.isFocused) touched = true }
)
```

### `CustomTextField` / `CustomPhoneNumberInput`
- Basados en `BasicTextField`. Altura mínima → `Dimens.Components.Input.minHeight`.
- `textStyle = Dimens.Components.Input.textStyle.copy(fontWeight = Bold, textAlign = Center)`.

---

## 4. Selectores
### `DropdownSelectable`
- Ancla accesible: `.semantics { role = Role.Button }` + `.focusable(true)`.
- Ancla: `heightIn(Dimens.Components.Dropdown.anchorHeight)` + `padding` de tokens.
- Menú: `width(anchorWidth)` + `heightIn(max = Dimens.Components.Dropdown.menuMaxHeight)`.
- **Sin** offsets manuales: el menú se posiciona relativo al ancla.

```kotlin
DropdownSelectable(
  dropdownItems = items,
  selectedItem = selected,
  onSelectedItem = setSelected
)
```

---

## 5. Cards
### `ReguertaCard`
- Variante `kind` → `Filled | Elevated | Outlined`.
- Elevación y borde se resuelven por tokens.

```kotlin
ReguertaCard(kind = Dimens.Components.Card.Kind.Outlined) {
  TextBody("Contenido", style = MaterialTheme.typography.bodyLarge)
}
```

---

## 6. Diálogos
### `ReguertaAlertDialog(type: UiType)`
- Icono con doble círculo: `badgeSize` + `innerBadgeFraction` + `iconSize` (tokens).
- Título → `Dimens.Components.Dialog.titleStyle` (hereda color del dialog)
- Cuerpo → `Dimens.Components.Dialog.bodyStyle` (hereda color del dialog)
- Botones internos **heredan** `btnType = type`.

```kotlin
ReguertaAlertDialog(
  onDismissRequest = onDismiss,
  icon = Icons.Default.Info,
  titleText = "Cerrar sesión",
  bodyText = "¿Seguro que quieres cerrar sesión?",
  confirmText = "Cerrar sesión",
  onConfirm = onConfirm,
  dismissText = "Volver",
  onDismissButton = onDismiss,
  type = UiType.INFO,
  properties = DialogProperties(dismissOnClickOutside = true)
)
```

---

## 7. Imágenes
### `ImageUrl`
- `cornerSize = Dimens.Components.Image.cornerRadius`.
- `crossfadeMillis = Dimens.Components.Image.crossfadeMillis`.

```kotlin
ImageUrl(
  imageUrl = product.imageUrl,
  name = product.name,
  modifier = Modifier.size(Dimens.Components.Image.productThumb)
)
```

### `ProductImage`
- Fondo `surfaceVariant` + borde `outlineVariant` (`borderThickness` token) + `clip`.

---

## 8. Miscelánea
### `ReguertaCounter`
- `minHeight`, `horizontalPadding`, `verticalPadding`, `iconSize` + colores desde tokens.

### `ReguertaCheckBox`
- `size = Dimens.Components.Checkbox.size` y `colors()` desde tokens.

### `ReguertaDivider`
- Variantes: `Kind.Subtle | Kind.Strong` con `thickness(kind)` y `color(kind)`.

### `LoadingAnimation` (Lottie)
- Tokens: `Dimens.Components.Lottie.Loading.size` y `speed`.

---

## 9. Patrón de barras inferiores
```kotlin
bottomBar = {
  Column(
    modifier = Modifier
      .navigationBarsPadding()
      .padding(Dimens.Spacing.sm)
  ) {
    ReguertaButton(
      textButton = "Continuar",
      onClick = onContinue,
      modifier = Modifier.fillMaxWidth()
    )
  }
}
```

---

## 10. Testing & QA checklist
- [ ] Usa `Dimens.Spacing.*` (sin `PADDING_*`).
- [ ] Tipografía por **rol** (sin `sp` sueltos).
- [ ] Estados disabled/readOnly visibles y accesibles.
- [ ] `Color.Unspecified` en contenido de contenedores con `contentColor`.
- [ ] `contentDescription` en imágenes.
- [ ] Tamaño táctil ≥ 40–48dp.
- [ ] No introduce insets por su cuenta.

---

## 11. Migración (resumen)
- `Dimens.Text.Button.*` y `Dimens.Text.Dialog.*` → **DEPRECADO**. Sustituir por `Dimens.Components.Button.*` y `Dimens.Components.Dialog.*` o `MaterialTheme.typography`.
- Tamaños legacy/deprecados (p. ej. `ultra_small`) → migrar a `Spacing.xxs/xs`.
- Donde se forzaba `textSize`, preferir `style` con rol y `Color.Unspecified` cuando el contenedor pone `contentColor`.
