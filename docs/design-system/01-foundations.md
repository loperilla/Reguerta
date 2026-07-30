# Regüerta Design System — Foundations

Este documento define la **fuente de verdad** de estilos para la app. Todo lo que no esté aquí tiende al caos 😉. Las capas superiores (composables, pantallas) **solo** deberían consumir estos tokens/roles, nunca valores sueltos.

---

## Fuente de verdad
- **Tipografía** → `ReguertaTypography` (Typography.kt) aplicada en `MaterialTheme(typography = ReguertaTypography)`.
- **Espaciados y tamaños** → `Dimens` (p. ej., `Dimens.Spacing.*`, `Dimens.Radius.*`, `Dimens.Size.*`, `Dimens.Border.*`). No hardcodear `dp`/`sp`.
- **Colores** → `LightColorScheme` / `DarkColorScheme` (Material 3). Preferir *Container*/*OnContainer* y *OutlineVariant* cuando corresponda.

> Cualquier ajuste global de tamaños de texto se hace **solo** en `ReguertaTypography`.

---

## Convenciones de uso
- **Importar `Dimens`**: usar referencias cualificadas (`Dimens.Spacing.md`, `Dimens.Components.Button.minHeight`).
- **Textos**: preferir `style = MaterialTheme.typography.<rol>` antes que fijar `fontSize` suelto. Si hay que ajustar, `style.copy(fontSize = …)`.
- **Colores en contenidos**: cuando el contenedor (Button/Dialog/TopBar) define `contentColor`, usar `Color.Unspecified` en los `Text/Icon` para heredar.
- **Insets/Layout**: un único `ReguertaScaffold` por pantalla. El contenido usa `.padding(innerPadding)`; añadir `.imePadding()` en formularios cuando proceda.

---

## Tipografía (roles y usos)
> Usar **roles**, no números. Si algo queda pequeño/grande, se ajusta el rol en `ReguertaTypography`.

- **displaySmall** → hero / nombre de app.
- **headlineSmall** → título de pantalla.
- **titleLarge / titleMedium** → títulos de sección / diálogos.
- **bodyLarge / bodyMedium / bodySmall** → texto principal / secundario / ayuda.
- **labelLarge** → botones primarios.
- **labelMedium** → enlaces y acciones sutiles.

**Ejemplos (API de nuestros wrappers):**
```kotlin
TextTitle(
  text = "Productos",
  style = MaterialTheme.typography.headlineSmall
)

TextBody(
  text = "Ver todos",
  style = MaterialTheme.typography.labelMedium
)
```

---

## Tokens base (Fundación)
- `Dimens.Spacing` → `xxs, xs, sm, md, lg, xl`.
- `Dimens.Radius` / `Dimens.Border` / `Dimens.Size` → ladrillos atómicos; **no** conocen componentes.

---

## Semántica UI
- `UiType { INFO, ERROR, WARNING }` (domain) expresa intención/severidad.
- Presentation mapea `UiType` → estilos/colores en tokens de componente.

> Si en el futuro se extrae el design system a un módulo independiente, usar un mapper `UiType → Intent` propio de presentation.

---

## Tokens de componente (Components)
> Los componentes consumen **solo** estos tokens. Cambios globales se hacen aquí.

### TopBar
- `Dimens.Components.TopBar.iconSize`, `leadingIconSize`, `actionIconSize`.
- `colors()` centraliza `container/title/navIcon/actionIcon`.
- Sugerencia: pasar `scrollBehavior` a `ReguertaTopBar` y a `ReguertaScaffold`.

### Dialog
- `ReguertaAlertDialog(..., type: UiType)`
- `colorsFor(type)` → `DialogColorSet(badgeContainer, badgeInner, icon)`
- Tamaños: `badgeSize`, `innerBadgeFraction`, `iconSize`.
- Los botones internos usan `btnType = type` (coherencia visual).

### Button
- `colors(type: UiType)` / `inverseColors(type: UiType)` / `borderColor(type)`.
- `minHeight`, `horizontalPadding`, `verticalPadding`, `labelStyle`, `secondaryLabelStyle`, `iconSize`.
- **Deprecado**: parámetros visuales ad-hoc en composables (se gobiernan por tokens).

### Input
- `textStyle`, `supportingStyle`, `labelStyle`, `cornerRadius`, `minHeight`.
- Iconos: `leadingIconSize`, `trailingIconSize`, `clearButtonSize`.

### Card
- `Kind { Filled, Elevated, Outlined }`, `elevation(kind)`, `border(kind)`, `cornerRadius`.

### Checkbox
- `size`, `colors()` (checked/unchecked/disabled desde colorScheme).

### Counter
- `minHeight`, `horizontalPadding`, `verticalPadding`, `iconSize`, `containerColor`, `contentColor`, `disabledContentColor`.

### Divider
- `Kind { Subtle, Strong }`, `thickness(kind)`, `color(kind)`.

### Dropdown
- `anchorHeight`, `contentPaddingHorizontal/Vertical`, `itemHeight`, `menuMaxHeight`, `arrowIconSize`.
- Colocación: usar `DropdownMenu` por defecto (evitar offsets manuales salvo casos especiales).

### Image
- `cornerRadius`, `productThumb`, `borderThickness`, `crossfadeMillis`.

### Lottie
- `Loading.size`, `Loading.speed`.

---

## Accesibilidad y escalado
- Textos en **sp** (respetan escala del sistema). Mantener `lineHeight` desde `ReguertaTypography`.
- Contraste: preferir pares `*Container` / `on*Container`.
- Área táctil mínima: 40–48 dp. Nuestros tokens definen `minHeight` en botones, inputs y counter.
- Semántica: usar `semantics { role = Role.Button }`, `focusable(true)` cuando aplique.

---

## Reglas de oro (Do / Don’t)
**Do**
- ✅ Un solo `ReguertaScaffold` por pantalla; contenido con `.padding(innerPadding)`.
- ✅ Espaciados con `Dimens.Spacing.*`.
- ✅ Textos con `style = MaterialTheme.typography.<rol>`.
- ✅ `Color.Unspecified` en contenido de contenedores con `contentColor`.

**Don’t**
- ❌ Hardcodear `sp`/`dp` en pantallas.
- ❌ Repetir colores a mano en diálogos/botones (usar `UiType`).
- ❌ `Scaffold` anidados.

---

## Deprecaciones y migración
- `Dimens.Text.Button.*` y `Dimens.Text.Dialog.*` → **DEPRECADO**. Sustituir por `Dimens.Components.Button.*` y `Dimens.Components.Dialog.*` o `MaterialTheme.typography`.
- Tamaños “legacy”/deprecated (p. ej. `ultra_small`) → migrar a `Spacing.xxs/xs`.
- Donde se forzaba `textSize`, preferir `style` y, si hace falta, `style.copy(fontSize = …)`.

---

## Referencias cruzadas
- **Layout & Insets** → `docs/design-system/02-layout-and-insets.md`
- **Componentes** → `docs/design-system/03-components.md`
- **Migración** → `docs/design-system/MIGRATION-Fase1-2.md`
