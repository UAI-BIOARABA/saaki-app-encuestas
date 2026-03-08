<div align="center">

<h1> APP de Encuestas para Saaki - Unitree G1 </h1>

<p>
  <a href="README.md">English</a> |
  <a href="README_es.md">Español</a>
</p>

[![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Gradle](https://img.shields.io/badge/Gradle-9E9E9E?logo=gradle&logoColor=02303A)](https://gradle.org/)
![API](https://img.shields.io/badge/API-23%2B-brightgreen)

![Research](https://img.shields.io/badge/Type-Research%20Project-lightgrey)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
</div>

## 📖 Descripción

Este repositorio es una aplicación de Android que se utilizará en el proyecto Saaki. Las personas que interactuen con el Unitree G1 realizarán estas encuestas y nos servirá para saber que impacto tiene este robot sobre las personas.

La app será usada de forma local e interna en el proyecto, únicamente en un dispositivo, por lo que los datos no saldrán de este.

Además, será utilizada únicamente en esukera y castellano, por lo que esos serán los idiomas disponibles. La documentación dentro del código como los comentarios, también estarán en alguno de esos idiomas.

Este README describe características de la aplicación y **cómo reproducir la APP de encuestas** en otro equipo.

---

## 🛠️ Requisitos previos

Asegúrate de tener instalado [Android Studio](https://developer.android.com/studio?hl=es-419) en tu sistema.

---

## ♻️ Reproducir el entorno en otro equipo

1. Instalar dependencias base:

   ```bash
   sudo apt update && sudo apt install openjdk-17-jdk git qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils virt-manager -y
   ```

2. Instalar Android Studio (`snap install android-studio --classic`)
3. Clonar el proyecto:

   ```bash
   git clone https://github.com/UAI-BIOARABA/saaki-app-encuestas.git
   ```

4. Abrir el proyecto en Android Studio
5. Android Studio descargará automáticamente el SDK y las librerías Gradle necesarias
6. (Solo para dispositivos físicos) En caso de necesitar un SDK específico para un dispositivo, ir a 'Tools → SDK Manager' y ahí buscar el SDK para la versión de Android del dispositivo. Como ejemplo, en nuestro caso, disponemos de una tablet con Android 6.0, por lo que necesitamos descargar el SDK 23 para Android 6.0 (Marshmallow).

---

## ✅ Verificación final

Para comprobar que todo funciona:

1. Abre el proyecto
2. Espera la sincronización de Gradle
3. Click en:

   ``` Andorid Studio
   Build → Clean Project
   ```

   Después click en:

   ``` Android Studio
   Build → Assemble 'app' Run Configuration
   ```

4. Abre el emulador o conecta un dispositivo físico
5. Pulsa **Run ▶️** en Android Studio

Si la app se ejecuta correctamente: ¡el entorno se ha reproducido con éxito! 🎉

---

## 🧩 Exportar configuración del IDE (opcional)

Desde Android Studio:

``` Android Studio
File → Manage IDE Settings → Export Settings...
```

Esto genera un `.zip` que puedes importar en otro equipo con:

``` Android Studio
File → Manage IDE Settings → Import Settings...
```

---

## 📸 Imagenes de la APP

#### Hasiera

![Hasiera](appimages/eu-0-inicio.png)

#### Datuak sartzea

![Sartu_datuak](appimages/eu-1-datos.png)

#### Inkestaren hautaketa

![Hautatu_inkesta](appimages/eu-2-seleccionarencuesta.png)

#### Inkesta A

![Inkesta_A_2](appimages/eu-3-encuestaa2.png)

#### Laburpena A

![Laburpena_A](appimages/eu-4-resumena.png)

#### Inkesta B

![Inkesta_B_2](appimages/eu-3-encuestab2.png)

#### Laburpena B

![Laburpena_B](appimages/eu-4-resumenb.png)

---

## 💾 Cómo se guardan los datos

Por motivos como facilidad de lectura y edición, simplicidad en el almacenamiento o exportación y análisis de respuestas, esta app almacena los datos de usuarios y las respuestas a las encuestas en **formatos CSV**.

Para guadar los archivos usamos:

```Kotlin
val file = File(requireContext().getExternalFilesDir(null), "usuarios.csv")
```

Entonces, los archivos se almacenan en el almacenamiento privado externo de la app, en la siguiente ruta:

``` Files
/storage/emulated/0/Android/data/org.bioaraba.saakiappencuestas/files/
```

Dentro de esa carpeta se encontrarán los siguientes archivos:

``` Files
usuaios.csv
encuesta_a.csv
encuesta_b.csv
us.bak (backup de usuarios)
ea.bak (backup de encuestas_a)
eb.bak (backup de encuestas_b)
```

Ya que nuestro dispositivo ustiliza Android 6.0, podemos acceder a estos archivos desde el propio explorador de archivos de la tablet, lo cual simplifica mucho el acceso a los datos y no necesitamos añadir funcionalidades para exportarlos.

---

## 💾 Cómo se ven los datos almacenados

Los datos se almacenan de la siguiente forma en los archivos CSV:

### Usuarios

![Usuarios](appimages/datos-usuarios.png)

### Encuesta_A

![Encuesta_A](appimages/datos-encuesta_a.png)

### Encuesta_B

![Encuesta_B](appimages/datos-encuesta_b.png)

---

## 🚨 NOTAS IMPORTANTES

- No almacenamos emoticonos, almacenamos numeros en la escala de 1 a 5.
- Los datos se almacenan en castellano independientemente del idioma seleccionado.

---

## 🧑‍💻 Autores

- **Project Manager:** [Juan Fernández](https://github.com/jfbioaraba)
- **Lead Developer:** [Andoni González](https://github.com/andoni92)

---

## Descargo de responsabilidad

Este software y los materiales asociados se proporcionan “tal cual”, sin garantías de ningún tipo, ni expresas ni implícitas, incluyendo —pero no limitándose a— garantías de comercialización, idoneidad para un propósito particular o ausencia de errores.

Los/as autores/as y Bioaraba – Instituto de Investigación Sanitaria no asumen responsabilidad alguna por el uso, la redistribución o la modificación de este repositorio ni por los posibles daños directos o indirectos derivados de su utilización.

Este proyecto tiene fines exclusivos de investigación y/o docencia.

No está destinado a su uso clínico, diagnóstico, terapéutico ni asistencial,
ni sustituye herramientas certificadas ni la evaluación profesional en entornos sanitarios.
