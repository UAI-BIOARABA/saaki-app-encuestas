<div align="center">

<h1> Survey APP for Saaki - Unitree G1 </h1>

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

## 📖 Description

This repository contains an experimental Android application developed within the context of the Saaki project, intended exclusively for testing and preliminary evaluation purposes.

It is not a final version and is not intended for use in real, production, or clinical environments.

The included content (questions and answers) has been created based on existing surveys, for illustrative purposes only and without validation for clinical or scientific use.

The application is designed for local and internal use, running on a single device without any external data transmission. However, this repository does not guarantee compliance with the legal, security, or data protection requirements necessary for production use.

The development has been carried out in Basque and Spanish, which are the only available languages. Internal documentation and code comments may be written in either of these languages.

This README describes the features of the application and **how to reproduce the survey app environment** on another machine.

---

## 🛠️ Prerequisites

Make sure you have [Android Studio](https://developer.android.com/studio?hl=es-419) installed on your system.

---

## ♻️ Reproducing the environment on another machine

1. Install base dependencies:

```bash
sudo apt update && sudo apt install openjdk-17-jdk git qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils virt-manager -y
```

2. Install Android Studio:

```
snap install android-studio --classic
```

3. Clone the project:

```bash
git clone https://github.com/UAI-BIOARABA/saaki-app-encuestas.git
```

4. Open the project in Android Studio  
5. Android Studio will automatically download the required SDK and Gradle libraries  
6. (Only for physical devices) If a specific SDK is required for a device, go to **Tools → SDK Manager** and search for the Android SDK corresponding to the device version.

For example, in our case we use a tablet with **Android 6.0**, so we need to download **SDK 23 for Android 6.0 (Marshmallow)**.

---

## ✅ Final verification

To verify that everything works correctly:

1. Open the project  
2. Wait for Gradle synchronization  
3. Click:

```Android Studio
Build → Clean Project
```

Then click:

```Android Studio
Build → Assemble 'app' Run Configuration
```

4. Open the emulator or connect a physical device  
5. Press **Run ▶️** in Android Studio  

If the app runs correctly, the environment has been successfully reproduced! 🎉

---

## 🧩 Export IDE configuration (optional)

From Android Studio:

```Android Studio
File → Manage IDE Settings → Export Settings...
```

This generates a `.zip` file that can be imported on another machine using:

```Android Studio
File → Manage IDE Settings → Import Settings...
```

---

## 📸 App Screenshots

#### Home

![Hasiera](appimages/eu-0-inicio.png)

#### Entering user data

![Sartu_datuak](appimages/eu-1-datos.png)

#### Survey selection

![Hautatu_inkesta](appimages/eu-2-seleccionarencuesta.png)

#### Survey A

![Inkesta_A_2](appimages/eu-3-encuestaa2.png)

#### Summary A

![Laburpena_A](appimages/eu-4-resumena.png)

#### Survey B

![Inkesta_B_2](appimages/eu-3-encuestab2.png)

#### Summary B

![Laburpena_B](appimages/eu-4-resumenb.png)

---
## 🏠 Device home page

Our device's home page will have 2 icons:
- The survey app
- A shortcut to access the folder with the saved files and data

![device_home_page](appimages/tablet_home.jpg)

#### The survey app icon allows direct access to the app

![app_home](appimages/tablet_app.jpg)

#### The files shortcut icon allows direct access to the folder with the saved survey data files

![data_folder](appimages/tablet_files.jpg)

---

## 💾 How data is stored

For reasons such as **ease of reading and editing, simplicity in storage or export, and easier analysis of responses**, this app stores user data and survey responses in **CSV format**.

To store the files we use:

```kotlin
val file = File(requireContext().getExternalFilesDir(null), "usuarios.csv")
```

The files are stored in the app's **private external storage**, in the following path:

```Files
/storage/emulated/0/Android/data/org.bioaraba.saakiappencuestas/files/
```

Inside this folder you will find the following files:

```Files
usuarios.csv
encuesta_a.csv
encuesta_b.csv
us.bak (users backup)
ea.bak (survey_a backup)
eb.bak (survey_b backup)
```

Since our device runs **Android 6.0**, these files can be accessed directly from the tablet’s file explorer. This greatly simplifies access to the collected data and avoids the need to implement additional export functionality.

---

## 💾 How stored data looks

The data is stored in the CSV files in the following format:

### Users

![Usuarios](appimages/datos-usuarios.png)

### Survey_A

![Encuesta_A](appimages/datos-encuesta_a.png)

### Survey_B

![Encuesta_B](appimages/datos-encuesta_b.png)

---

## 🚨 IMPORTANT NOTES

- We do not store emoticons; instead we store **numbers on a scale from 1 to 5**.
- Data is stored **in Spanish regardless of the selected interface language**.

---

## 🧑‍💻 Authors

- **Project Manager:** [Juan Fernández](https://github.com/jfbioaraba)
- **Lead Developer:** [Andoni González](https://github.com/andoni92)

---

## Disclaimer

This software and the accompanying materials are provided “as is,” without warranties of any kind, either express or implied, including—but not limited to—warranties of merchantability, fitness for a particular purpose, or absence of errors.

The authors and Bioaraba – Health Research Institute assume no responsibility for the use, redistribution, or modification of this repository, nor for any direct or indirect damages arising from its use.

This project is intended exclusively for research and/or educational purposes. It is not intended for clinical, diagnostic, therapeutic, or healthcare use, nor does it replace certified tools or professional judgment in healthcare settings.

Notwithstanding the foregoing, use of the software and the robotic system shall be at the user’s own risk. The user must verify its suitability for the intended purpose and adopt all necessary safety, supervision, and control measures based on the environment and conditions of use.

The user shall be responsible for any consequences arising from improper, negligent, or non-compliant use, including any personal injury or property damage that may occur.

To the fullest extent permitted by applicable law, Bioaraba shall not be liable for any damages arising from the use of the software or robotic system when such damages result from defective implementation, insufficient supervision, inappropriate use decisions by the user, or failure to observe recommended safety measures.
