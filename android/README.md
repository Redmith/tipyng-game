# Tipeo Veloz (Android)

Port a Android nativo del juego de velocidad de tipeo en consola. Está escrito en Kotlin, usa solo clases del framework de Android (sin librerías externas) y el APK resultante no necesita Python.

## Qué cambió respecto a la versión de consola

- La lógica de `calcular_velocidad` y `calcular_precision` está portada tal cual en `Logica.kt`.
- El tiempo empieza a correr con la **primera letra** que escribís (en el celular, "Enter para empezar" y escribir se mezclan con la aparición del teclado).
- Flujo: **Comenzar** → escribís la frase → **Terminar** (o la tecla "Listo" del teclado) → resultados → **Jugar de nuevo**.
- Si el texto no coincide, se muestran el original y el tuyo para comparar.

## Cómo generar el APK

### Opción A: Android Studio (la más simple)

1. `File > Open` y elegí esta carpeta. Esperá a que termine el sync de Gradle.
2. `Build > Build Bundle(s) / APK(s) > Build APK(s)`.
3. El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

### Opción B: GitHub Actions (sin instalar nada)

1. Subí esta carpeta a un repositorio de GitHub.
2. En la pestaña **Actions** corre el workflow *Build APK* (se dispara solo con cada push).
3. Descargá el artefacto `tipeo-veloz-apk` con el `app-debug.apk` adentro.

### Opción C: línea de comandos

Necesitás JDK 17, el Android SDK (variable `ANDROID_HOME`) y Gradle 8.10 o superior:

```
gradle assembleDebug
```

## Instalarlo en el celular

Copiá `app-debug.apk` al teléfono, abrilo y permití la instalación desde orígenes desconocidos cuando Android lo pida. El APK de debug viene firmado con la clave de debug, así que se instala directo.

## Notas

- `minSdk 24` (Android 7.0 o superior), `targetSdk 34`.
- Para publicar en Google Play hace falta un build `release` firmado con tu propia clave y subir a `targetSdk` 35.
