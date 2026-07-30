# Regüerta Design System — Layout & Insets

Guía práctica para maquetar pantallas en Compose **sin pelearse con barras del sistema, teclado ni navegación**. Aquí definimos el patrón único que usamos en la app, ejemplos listos para copiar y un checklist de errores comunes.

---

## Objetivos
- **Un solo Scaffold por pantalla** para controlar insets de forma centralizada.
- **Contenido que respeta barras del sistema** (status/navigation) y el **teclado**.
- Estructura predecible: todos los equipos pueden leer una pantalla en 10 segundos.

---

## Reglas de oro
1) **Usa `ReguertaScaffold`** en cada pantalla. Internamente aplica `contentWindowInsets = WindowInsets.systemBars`.
2) **Envuelve todo con `Screen { ... }`** (tema + Surface). No metas más Scaffold ahí.
3) **Propaga el padding de Scaffold** al contenedor raíz con `.padding(it)`.
4) **Formularios**: añade `.imePadding()` al contenedor principal (o al scroll) para que el teclado no tape contenido.
5) **Barras inferiores** (botoneras/paneles): añade `.navigationBarsPadding()` a su contenedor para evitar solaparse con la barra de navegación.
6) **Nada de slots vacíos expansivos** en la top bar (p. ej. `actions = { Column(Modifier.fillMaxWidth()) { } }`).
7) **Unidades de espaciado**: siempre `Dimens.Spacing.*`.

---

## Plantilla base de pantalla
```kotlin
@Composable
fun ExampleScreen(...) {
    Screen {
        ReguertaScaffold(
            topBar = {
                ReguertaTopBar(
                    topBarText = "Título",
                    navActionClick = { /* navegar atrás */ }
                )
            },
            bottomBar = {
                // Si hay barra inferior, respeta la nav bar
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(Dimens.Spacing.sm)
                ) {
                    ReguertaButton(textButton = "Acción", onClick = { ... })
                }
            }
        ) { it ->
            // El contenido *siempre* recibe el padding del Scaffold
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(Dimens.Spacing.md)
                    .fillMaxSize(),
            ) {
                // … contenido …
            }
        }
    }
}
```

---

## Formularios y teclado (IME)
- Añade `.imePadding()` al contenedor principal del formulario **o** al scroll que envuelve el contenido:
```kotlin
Column(
    modifier = Modifier
        .padding(it)
        .padding(Dimens.Spacing.md)
        .fillMaxSize()
        .imePadding()
) { /* inputs, botones */ }
```
- Si usas `LazyColumn`, colócalo en el `Modifier` de la lista:
```kotlin
LazyColumn(
    modifier = Modifier
        .padding(it)
        .imePadding(),
    contentPadding = PaddingValues(Dimens.Spacing.md)
) { /* items */ }
```

---

## Top Bar (AppBar)
- Usa `ReguertaTopBar`/`ReguertaHomeTopBar` dentro del `topBar` del Scaffold.
- **No añadas** paddings extra ni `statusBarsPadding()` en la barra; los insets vienen del Scaffold.
- **Evita** `actions` vacíos que hagan `fillMaxWidth()` (crea huecos artificiales).

Ejemplo correcto:
```kotlin
ReguertaTopBar(
    topBarText = "Mi pantalla",
    navActionClick = onBack
)
```

---

## Barra inferior (botoneras, paneles)
- Estructura típica con forma redondeada y padding seguro:
```kotlin
bottomBar = {
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(Dimens.Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ReguertaButton(
            textButton = "Continuar",
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

Si usas un contenedor con fondo/shape propio, aplica la forma y luego el padding:
```kotlin
Column(
    modifier = Modifier
        .navigationBarsPadding()
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(Dimens.Spacing.md))
        .padding(Dimens.Spacing.sm)
) { ... }
```

---

## Listas (LazyColumn/LazyRow)
- Propaga `it` y usa `contentPadding` con `Dimens` para respiración:
```kotlin
LazyColumn(
    modifier = Modifier.padding(it),
    verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.sm),
    contentPadding = PaddingValues(
        start = Dimens.Spacing.md,
        end = Dimens.Spacing.md,
        top = Dimens.Spacing.sm,
        bottom = Dimens.Spacing.lg
    )
) { /* items */ }
```
- Si la lista convive con una **bottom bar**, considera dejar un `bottom` generoso (`lg`/`xl`) para evitar solapes visuales.

---

## Drawer y navegación
- Para `ModalNavigationDrawer`, la estructura recomendada es: Drawer fuera, `ReguertaScaffold` dentro.
```kotlin
ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = { DrawerContent(...) }
) {
    ReguertaScaffold(topBar = { ... }, bottomBar = { ... }) { it ->
        Box(Modifier.padding(it)) { /* contenido */ }
    }
}
```
- No metas un `Scaffold` adicional dentro del contenido del Drawer.

---

## Edge-to-edge (opcional)
- La app está preparada para funcionar edge-to-edge con los insets centralizados. Si habilitas barras transparentes a nivel de actividad, **no cambies** este patrón; los insets seguirán llegando a `ReguertaScaffold`.

---

## Anti‑patrones (errores comunes)
- ❌ **Scaffold anidados**: duplica insets y crea márgenes fantasma.
- ❌ **No usar `.padding(it)`**: el contenido se mete bajo la top bar.
- ❌ **Slots vacíos expansivos en top bar** (acciones con `fillMaxWidth()`): inflan la altura.
- ❌ **Paddings deprecados** (`PADDING_*`): usar `Dimens.Spacing.*`.
- ❌ **Placeholders con VisualTransformation**: usar `placeholder { … }`.

---

## Checklist por pantalla
- [ ] `Screen { ReguertaScaffold { it -> … } }` aplicado.
- [ ] `.padding(it)` en el contenedor raíz.
- [ ] `Dimens.Spacing.*` para todos los paddings/espacios.
- [ ] `imePadding()` en formularios / `navigationBarsPadding()` en barras inferiores.
- [ ] Sin `Scaffold` anidados ni slots vacíos en top bar.

---

## Snippets rápidos
**Pantalla con formulario y botón inferior:**
```kotlin
Screen {
  ReguertaScaffold(topBar = { ReguertaTopBar(topBarText = "Editar") }) { it ->
    Column(
      Modifier
        .padding(it)
        .padding(Dimens.Spacing.md)
        .fillMaxSize()
        .imePadding()
    ) {
      // Inputs…
      Spacer(Modifier.weight(1f))
      ReguertaButton(textButton = "Guardar", onClick = onSave, modifier = Modifier.fillMaxWidth())
    }
  }
}
```

**Pantalla de lista:**
```kotlin
Screen {
  ReguertaScaffold(topBar = { ReguertaTopBar(topBarText = "Productos") }) { it ->
    LazyColumn(
      modifier = Modifier.padding(it),
      verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.sm),
      contentPadding = PaddingValues(Dimens.Spacing.md)
    ) {
      items(items) { item -> ProductItem(item) }
    }
  }
}
```
