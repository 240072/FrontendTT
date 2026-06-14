# FrontendTT 🚗✈️ (TravelTogether)

**FrontendTT** es una aplicación móvil para Android desarrollada en **Kotlin** con **Jetpack Compose** y **Material 3**. La aplicación está diseñada para facilitar la planificación y gestión de viajes compartidos (estilo carpooling), permitiendo a los usuarios registrarse, publicar viajes con preferencias específicas (como mascotas, tabaco, etc.), unirse a viajes existentes, y realizar búsquedas avanzadas de destinos mediante filtros de rango de fechas y cercanía geográfica gracias a la integración con mapas.

---

## 🚀 Características Principales

*   **Autenticación y Seguridad:**
    *   Registro e Inicio de sesión integrados con **Supabase Auth**.
    *   Persistencia de sesión: si el usuario ya está autenticado, la aplicación se inicia directamente en el menú principal.
    *   Cierre de sesión seguro.
*   **Gestión de Viajes:**
    *   **Crear Viaje:** Los usuarios pueden crear un viaje detallando el nombre, descripción, fechas de inicio y fin, destino, y preferencias personalizadas (por ejemplo, tolerancia/preferencias sobre mascotas y tabaco).
    *   **Detalle del Viaje:** Visualización completa de los datos del viaje, incluyendo la lista de usuarios participantes y las etapas asociadas.
    *   **Mis Viajes:** Listado personalizado que separa o incluye tanto los viajes en los que el usuario participa como los que ha creado.
    *   **Editar Viaje:** Permite modificar las opciones del viaje existente para adaptarlo a nuevos requerimientos.
*   **Búsqueda y Filtros Geográficos:**
    *   Búsqueda avanzada de destinos utilizando un mapa interactivo.
    *   Filtros dinámicos por **rango de fechas** y por **rango de distancia** (en kilómetros) a partir de coordenadas geográficas específicas.
*   **Mapas Integrados:**
    *   Uso de **OpenStreetMap (osmdroid)** para visualizar ubicaciones y definir puntos en el mapa mediante marcadores interactivos.
    *   Uso de **Google Play Services Location** para obtener la ubicación del dispositivo en tiempo real.

---

## 🛠️ Stack Tecnológico y Librerías

*   **Lenguaje:** [Kotlin](https://kotlinlang.org/) (v2.2.10)
*   **Diseño de Interfaz:** [Jetpack Compose](https://developer.android.com/compose) con **Material Design 3**
*   **Arquitectura:** MVVM (Model-View-ViewModel) con flujos de estado reactivos (`StateFlow` / `collectAsState`).
*   **Base de Datos y Backend:** [Supabase](https://supabase.com/)
    *   `supabase-kt` (v3.0.0) para Auth y base de datos (Postgrest).
*   **Cliente HTTP:** [Ktor](https://ktor.io/) con motor OkHttp (v3.0.0).
*   **Mapas y Geolocalización:**
    *   [osmdroid](https://github.com/osmdroid/osmdroid) (v6.1.18) para la renderización de mapas de OpenStreetMap.
    *   `play-services-location` (v21.3.0) para servicios de ubicación.
*   **Serialización:** `kotlinx-serialization-json` (v1.6.3) para el parsing de datos JSON desde/hacia Supabase.

---

## 📂 Estructura del Proyecto

El código fuente principal está estructurado de la siguiente manera dentro de `app/src/main/java/com/example/frontendtt`:

```text
├── components/          # Componentes de UI reutilizables (DatePicker, diálogos, etc.)
├── data/                # Modelos de datos (Viaje, Destino, Etapa, Mascota, Tabaco, etc.)
├── navigation/          # Definición de rutas (AppScreens) y host de navegación (AppNavigation)
├── network/             # Configuración del SupabaseClient y funciones API/consultas
├── screens/             # Pantallas principales de la interfaz de usuario:
│   ├── LoginScreen.kt
│   ├── RegisterScreen.kt
│   ├── MenuScreen.kt (Panel de búsqueda y mapa)
│   ├── ListaViajesScreen.kt
│   ├── ViajeScreen.kt
│   ├── NuevoViajeScreen.kt
│   └── EditarViajeScreen.kt
├── states/              # Estados de UI para las pantallas (LoginState, MenuState, etc.)
├── ui/                  # Temas, tipografías y paleta de colores de Material 3 (Theme.kt, Color.kt)
└── viewmodels/          # Lógica de negocio y control del estado de las vistas
```

---

## 🔑 Permisos del Sistema

La aplicación requiere los siguientes permisos declarados en el archivo `AndroidManifest.xml`:

*   `android.permission.INTERNET`: Para conectarse a los servicios de Supabase y descargar los mapas de OpenStreetMap.
*   `android.permission.ACCESS_NETWORK_STATE`: Para verificar la conectividad a la red.
*   `android.permission.ACCESS_FINE_LOCATION`: Para obtener la ubicación precisa por GPS del usuario.
*   `android.permission.ACCESS_COARSE_LOCATION`: Para la ubicación aproximada basada en red móvil y Wi-Fi.

---

## 🚀 Instalación y Configuración

### Requisitos Previos
*   **Android Studio** Ladybug o superior recomendado.
*   **JDK 11** o superior (configurado en las opciones de compilación del proyecto).
*   **Min SDK:** 30 (Android 11) | **Target SDK:** 36 (Android 15).

### Pasos para Ejecutar

1.  **Clonar el repositorio:**
    ```bash
    git clone <url-del-repositorio>
    ```
2.  **Configurar Supabase:**
    Abre el archivo [SupabaseClient.kt](file:///c:/Users/lalal/AndroidStudioProjects/FrontendTT/app/src/main/java/com/example/frontendtt/network/SupabaseClient.kt) y asegúrate de que el `supabaseUrl` y la `supabaseKey` apunten a tu instancia correspondiente de Supabase:
    ```kotlin
    val supabase = createSupabaseClient(
        supabaseUrl = "TU_SUPABASE_URL",
        supabaseKey = "TU_SUPABASE_ANON_KEY"
    ) {
        install(Auth)
        install(Postgrest)
    }
    ```
3.  **Compilar y Ejecutar:**
    *   Sincroniza el proyecto con Gradle.
    *   Compila y ejecuta la aplicación en un emulador o dispositivo físico Android con nivel de API 30 o superior.
