# Struttura dei file

Mappa del repository Fastigum.

## Albero del progetto

```
gps-altitude-overlay/
├── app/
│   ├── build.gradle.kts                 ← versioni, firma, dipendenze
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/altitudeoverlay/
│       │   ├── MainActivity.kt
│       │   ├── LocationOverlayService.kt
│       │   └── GeoidCorrection.kt
│       └── res/
│           ├── drawable/                ← sfondi, pulsanti, linea ondulata, icona
│           ├── font/                    ← Manrope (regular, medium, bold, extrabold)
│           ├── layout/activity_main.xml
│           ├── mipmap-anydpi-v26/       ← icona adattiva (normale e rotonda)
│           ├── values/                  ← colors.xml, strings.xml (EN), styles.xml
│           └── values-it/strings.xml    ← traduzione italiana
├── build.gradle.kts                     ← versioni dei plugin (AGP, Kotlin)
├── settings.gradle.kts
├── gradle.properties
├── gradle/wrapper/                      ← Gradle 8.13 (wrapper)
├── gradlew, gradlew.bat
├── .devcontainer/                       ← ambiente Codespaces riproducibile
│   ├── devcontainer.json
│   └── setup.sh                         ← installa l'Android SDK
├── scripts/save.sh                      ← salvataggio di fine sessione
├── docs/                                ← documentazione e screenshot
├── README.md
├── LICENCE
└── .gitignore
```

Nota: il namespace dell'app è `com.trevi020.fastigum`, mentre i file Kotlin si trovano ancora nel package `com.example.altitudeoverlay`. La migrazione della cartella è un'operazione di pulizia rimandata.

## Codice Kotlin

| File | Ruolo |
|---|---|
| `MainActivity.kt` | Schermata principale: scheda altitudine grezza/corretta, slider della correzione geoide, preset Italia/UK/Custom, interruttore accuratezza, gestione dei permessi, avvio e arresto dell'overlay, animazione della linea ondulata |
| `LocationOverlayService.kt` | Servizio in primo piano: legge il GPS, disegna l'overlay con `WindowManager`, gestisce trascinamento, doppio tap e chiusura per trascinamento |
| `GeoidCorrection.kt` | Salva e legge le preferenze (correzione geoide, mostra accuratezza) e calcola l'altitudine corretta |

## Risorse

| Percorso | Contenuto |
|---|---|
| `res/layout/activity_main.xml` | Layout della schermata principale |
| `res/values/colors.xml` | Palette Trail (vedi ADVANCED_GUIDE.md) |
| `res/values/strings.xml` | Testi in inglese (lingua predefinita) |
| `res/values-it/strings.xml` | Testi in italiano |
| `res/values/styles.xml` | Tema, barre di sistema trasparenti (edge-to-edge) |
| `res/drawable/bg_card_white.xml` | Sfondo della scheda altitudine |
| `res/drawable/bg_pill_button.xml` | Pulsante Avvia/Ferma Overlay |
| `res/drawable/bg_preset_selected.xml`, `bg_preset_unselected.xml` | Stati dei pulsanti preset |
| `res/drawable/info_background.xml` | Sfondo del blocco informativo |
| `res/drawable/ic_wavy_line_double.xml` | Linea ondulata animata |
| `res/drawable/ic_launcher_*.xml` | Icona dell'app: sfondo, primo piano, versione monocromatica |
| `res/mipmap-anydpi-v26/ic_launcher*.xml` | Icona adattiva (normale e rotonda) |
| `res/font/manrope_*.ttf` | Font Manrope in quattro pesi |

## Configurazione e build

| File | Scopo |
|---|---|
| `app/build.gradle.kts` | `namespace`, `applicationId`, `compileSdk`/`targetSdk`, `versionCode`/`versionName`, firma release, dipendenze |
| `build.gradle.kts` (radice) | Versioni di Android Gradle Plugin e Kotlin |
| `gradle.properties` | Opzioni Gradle, tra cui la memoria della JVM |
| `gradle/wrapper/gradle-wrapper.properties` | Versione di Gradle usata dal wrapper |
| `AndroidManifest.xml` | Permessi, activity, servizio in primo piano di tipo `location` |

## Fuori dal repository (non committare mai)

| File | Perché |
|---|---|
| `keystore.properties` | Credenziali di firma: da creare a mano (vedi SETUP_GUIDE.md) |
| `*.keystore`, `*.jks` | Chiave di firma dell'app |
| `local.properties` | Percorso dell'Android SDK della tua macchina; lo genera `setup.sh` |
| `app/build/`, `.gradle/` | File generati dalla compilazione |

Tutti questi percorsi sono già elencati in `.gitignore`.

## Documentazione

| File | Contenuto |
|---|---|
| `README.md` | Panoramica del progetto |
| `docs/QUICK_START.md` | Uso dell'app e problemi comuni |
| `docs/SETUP_GUIDE.md` | Compilazione, firma, bundle per Google Play |
| `docs/FILE_STRUCTURE.md` | Questo file |
| `docs/ADVANCED_GUIDE.md` | Funzionamento interno e personalizzazione |
| `docs/images/` | Screenshot usati nel README |
