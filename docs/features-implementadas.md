# Features implementadas en Reguerta

Inventario funcional extraído del código actual y agrupado por pantallas/módulos.

## 1. App / Arranque / Sesión

- Splash screen nativa en arranque (`SplashScreen API`) con retención hasta resolver estado de sesión.
- Resolución de `startDestination` automática: entra a Home si la sesión es válida, si no va al flujo Auth.
- Refresco de token/sesión al abrir app y al volver a foreground.
- Aviso visual (Toast) cuando la sesión expira.
- Registro de snapshot de dispositivo (modelo, fabricante, versión SO, versión app, deviceId) al iniciar sesión y al refrescar sesión.
- Gestión de lifecycle a nivel proceso (`ProcessLifecycleOwner`) para disparar sincronización en foreground.
- Soporte de entorno Firestore por build (`develop` en debug, `production` en release).
- Configuración de caché persistente de Firestore y activación de Firebase Performance.
- Tema visual propio Material 3 y control de barras del sistema.
- En Android < 10 fuerza modo nocturno para coherencia visual de splash.

## 2. Navegación (graphs y rutas)

- Graph de autenticación con 4 pantallas: bienvenida, login, registro, recuperación.
- Graph Home con rutas internas: Home root, Orders placeholder, Received Orders, Settings placeholder.
- Graph de productos con rutas root/add/edit por `id`.
- Graph de usuarios con rutas root/add/edit por `id`.
- Ruta dedicada de nuevo pedido.
- Drawer con navegación contextual según rol (usuario/admin/productor).
- Mostrar versión visible de app en el drawer.

## 3. Autenticación (pantallas Auth)

- Pantalla de bienvenida con entrada a login y acceso a registro.
- Login con validación de email y password antes de habilitar botón.
- Login con feedback de error en snackbar.
- Recuperación de contraseña por email con diálogos de éxito y error.
- Registro con validación de email, password mínimo y confirmación de contraseña.
- Navegación de retorno entre pantallas auth.
- Guardado de datos básicos del usuario autenticado en DataStore.
- Modo debug con autologin y fecha simulada configurable.

## 4. Home (pantalla principal)

- Loader inicial de primer arranque con mensajes rotativos.
- Watchdog de loader (20s) y botón "Reintentar".
- Carga y evaluación de configuración global (`config/global`).
- Comprobación de versionado remoto: actualización obligatoria o recomendada.
- Diálogo de usuario no autorizado.
- Botón "Mi pedido" bloqueado hasta que los datos críticos estén sincronizados/frescos.
- Revalidación de frescura al pulsar "Mi pedido" antes de navegar.
- Bloqueo por día reservado (día siguiente al reparto) con diálogo explicativo.
- Botón "Ver tus pedidos" para productores, con habilitación según calendario.
- Cierre de sesión con diálogo de confirmación.

## 5. Nuevo Pedido (`NewOrder`)

- Determina flujo automáticamente según día actual y día de reparto:
- Flujo "pedido de semana actual".
- Flujo "mostrar/editar pedido anterior".
- Detección de pedido existente y carga de líneas existentes.
- Fallback robusto de recarga si la pantalla se queda en loading (intentos automáticos).
- Carga de productos disponibles con preferencia por servidor y fallback a cache/listener.
- Agrupación de productos por empresa productora.
- Alta de línea de pedido desde listado de productos ("Añadir").
- Incremento/decremento de cantidad por producto.
- Eliminación de línea al llegar a cantidad 0.
- Carrito de compra con total dinámico.
- Selector animado entre vista de productos y carrito.
- Búsqueda de productos con debounce y normalización (insensible a acentos).
- Envío de pedido a Firebase.
- Popup de confirmación de pedido enviado.
- Popup de error por reglas de compromiso incumplidas.
- Reglas de compromiso: exige compromiso/renuncia por paridad cuando aplica.
- Reglas de compromiso: impide incluir compromiso y renuncia a la vez.
- Reglas de compromiso tropical: obliga incluir compromiso de mangos/aguacates si el usuario tiene kg comprometidos.
- Ajuste automático de cantidades para líneas tropicales según compromiso del usuario.
- Edición de pedido existente por empresas con totales por bloque y total global.
- Borrado de pedido completo con confirmación.
- Restauración de stock de productos al borrar pedido.

## 6. Pedidos Recibidos (productores)

- Carga de pedidos recibidos a preparar con loader y recarga.
- Consulta de líneas de pedido por empresa del productor y semana objetivo.
- Vista por pestañas:
- "Por producto".
- "Por regüertense".
- Agrupación por producto con suma de unidades.
- Agrupación por usuario con detalle de líneas y subtotal.
- Cálculo de total general.
- Estado vacío cuando no hay pedidos recibidos.

## 7. Productos

- Listado de productos del productor autenticado en tiempo real.
- Alta de producto con formulario completo.
- Edición de producto existente con precarga de datos.
- Borrado de producto con diálogo de confirmación.
- Campos de producto: nombre, descripción, precio, stock, disponibilidad, envase y medida.
- Selector de imagen desde galería con permisos adaptados por versión Android.
- Redimensionado y recorte de imagen antes de subida.
- Subida de imagen de producto a Firebase Storage.
- Cambio singular/plural automático en envase y unidad según cantidad.
- Validación de formulario para habilitar botón de guardar.
- Actualización de timestamp remoto de tabla `products` al crear/editar.

## 8. Usuarios (admin)

- Listado de usuarios autorizados en tiempo real.
- Alta de usuario autorizado.
- Edición de usuario autorizado.
- Eliminación de usuario con confirmación.
- Campos de usuario: email, nombre, apellidos, teléfono, flags admin/productor, empresa.
- Lógica de tipos productor/consumidor (normal, compras, par/impar/sin) con normalización al cambiar flags.
- Campo empresa condicional cuando el usuario es productor.
- Validación de datos antes de habilitar guardar.
- Actualización de timestamp remoto de tabla `users` al crear/editar.

## 9. Sincronización y reglas de frescura (transversal)

- Definición de tablas críticas para habilitar flujos (usuarios, productos, envases, medidas y pedidos según día).
- Comparativa de timestamps remotos vs locales para decidir qué sincronizar.
- Orquestador de sync que sincroniza solo tablas necesarias.
- Preload de datos críticos en arranque frío.
- Sync en foreground con throttle temporal.
- Estrategia distinta de retry según éxito/fallo previo del sync.
- Persistencia local de timestamps de sync en DataStore.
- Recheck on-demand antes de abrir "Mi pedido".

## 10. Persistencia local

- DataStore para sesión y flags de usuario.
- DataStore para `isFirstRun`.
- DataStore para timestamps de sincronización por tabla.
- Conservación de `deviceId` al cerrar sesión (no se pierde identidad del dispositivo).
- Room para líneas de pedido locales (carrito/edición en local).
- Room para caché local de medidas.
- Servicio temporal de semanas/días con soporte de fecha simulada para pruebas.

## 11. Integraciones Firebase / Infra

- Firebase Auth: login, registro, refresh, logout, reset password.
- Firestore: users, products, measures, containers, orders, orderLines.
- Storage: imágenes de productos.
- Subcolección `devices` por usuario con `firstSeenAt`, `lastSeenAt` y `lastDeviceId`.
- Plugins de Google Services y Crashlytics configurados en app.

## 12. Módulos/pantallas presentes pero aún no desarrollados funcionalmente

- Pantalla `Orders` está creada pero sin contenido funcional.
- Pantalla `Settings` está creada pero sin contenido funcional.
- Entradas de drawer para noticias/ajustes/pedidos adicionales aparecen comentadas.
- Hay clases/casos de uso vacíos o stub (`GetAppVersionUseCase`, `CheckIsFirstRunUseCase`, `AppInfoProviderImpl`).
- `ProductsRepositoryImpl` aparece como base de refactor, no como capa principal en uso actual.
