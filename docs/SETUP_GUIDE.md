# Guida alla compilazione da sorgente

Come compilare Fastigum dal codice sorgente: in GitHub Codespaces, in Android Studio o da riga di comando.

## Prerequisiti

| Componente | Versione |
|---|---|
| JDK | 17 |
| Android SDK | piattaforma 36 (`platforms;android-36`), build-tools 35.0.0 |
| Android Gradle Plugin | 8.13.2 |
| Gradle | 8.13 (scaricato dal wrapper `./gradlew`) |
| Kotlin | 2.1.21 |
| Dispositivo | Android 5.0 o successivo (minSdk 21) |

## Struttura del progetto

```
app/
├── build.gradle.kts            ← versioni, firma, dipendenze
└── src/main/
    ├── AndroidManifest.xml
    ├── java/com/example/altitudeoverlay/   ← sorgenti Kotlin
    └── res/                    ← layout, drawable, values (IT/EN), font
.devcontainer/                  ← ambiente Codespaces riproducibile
scripts/save.sh                 ← salvataggio di fine sessione
```

Nota: il namespace è `com.trevi020.fastigum`, mentre i file Kotlin stanno ancora nella cartella `com/example/altitudeoverlay`.

## Opzione A: GitHub Codespaces (consigliata)

1. Dalla pagina del repository: **Code → Codespaces → New with options**, macchina **4-core / 16 GB**.
2. Alla creazione parte da solo `.devcontainer/setup.sh`: installa l'Android SDK, accetta le licenze e scrive `local.properties`.
3. Verifica l'ambiente:
```bash
   echo $ANDROID_HOME
   cat local.properties
   java -version
```
4. Compila una build di prova (non richiede la chiave di firma):
```bash
   ./gradlew assembleDebug --no-daemon
```
   L'APK è in `app/build/outputs/apk/debug/app-debug.apk`.

Un Codespace inattivo viene eliminato dopo alcuni giorni: prima di fermarti esegui `bash scripts/save.sh`, che salva anche il lavoro a metà sul branch `wip/codespace-backup`.

## Opzione B: Android Studio

1. Installa una versione recente di Android Studio (Narwhal o successiva).
2. **File → Open** e scegli la cartella del progetto. Attendi la sincronizzazione di Gradle.
3. Se richiesto, installa dall'SDK Manager la piattaforma 36 e le build-tools 35.0.0.
4. Collega il telefono (debug USB attivo) e premi **Run**.

## Opzione C: riga di comando su PC

1. Installa JDK 17 e gli [Android command-line tools](https://developer.android.com/studio#command-line-tools-only).
2. Imposta `ANDROID_HOME` e installa i pacchetti:
```bash
   sdkmanager --licenses
   sdkmanager "platform-tools" "platforms;android-36" "build-tools;35.0.0"
```
3. Crea `local.properties` nella radice:
```
   sdk.dir=/percorso/del/tuo/android-sdk
```
4. Compila con `./gradlew assembleDebug`.

## Firma per le build release

`assembleRelease` e `bundleRelease` richiedono una chiave di firma. Se non ne hai una, creala:

```bash
keytool -genkeypair -v -keystore mia-chiave.keystore -alias mio_alias \
  -keyalg RSA -keysize 2048 -validity 10000
```

Poi crea nella radice del progetto il file `keystore.properties`:

```
storeFile=/percorso/assoluto/mia-chiave.keystore
storePassword=la_tua_password
keyAlias=mio_alias
keyPassword=la_tua_password
```

Note importanti:

- `storeFile` viene risolto a partire dalla cartella `app/`: usa un percorso assoluto, oppure `../mia-chiave.keystore` se il file sta nella radice.
- `keystore.properties` e i file `*.keystore` sono esclusi da git: **non committarli mai**.
- Conserva keystore e password in almeno due posti sicuri fuori dal repository. Senza la chiave non puoi più pubblicare aggiornamenti compatibili.

Comandi di build:

```bash
./gradlew assembleRelease --no-daemon   # APK in app/build/outputs/apk/release/
./gradlew bundleRelease --no-daemon     # AAB in app/build/outputs/bundle/release/
```

Per Google Play serve l'**AAB**. Prima di ogni nuovo caricamento aumenta `versionCode` in `app/build.gradle.kts`.

## Installare e provare l'app

1. Installa l'APK sul telefono (`adb install -r app-debug.apk`, oppure copia il file e aprilo).
2. Apri l'app e tocca **Avvia Overlay**.
3. Concedi i permessi: localizzazione precisa e "Visualizza sopra altre app".
4. Apri Google Maps o un'altra app: l'overlay con l'altitudine resta visibile. Trascinalo per spostarlo, doppio tap per cambiarne la dimensione, trascinalo in basso per chiuderlo.

## Risoluzione dei problemi

**`SigningConfig "release" is missing required property "storeFile"`**
Manca `keystore.properties` nella radice del progetto, oppure `storeFile` non punta al keystore. Vedi la sezione sulla firma.

**`SDK location not found`**
Manca `local.properties` con `sdk.dir`. In Codespaces lo crea `setup.sh`: rilancialo con `bash .devcontainer/setup.sh`.

**Pacchetto SDK non trovato o licenze non accettate**
```bash
sdkmanager --licenses
sdkmanager "platforms;android-36" "build-tools;35.0.0"
```

**La build termina per memoria esaurita**
In `gradle.properties` imposta `org.gradle.jvmargs=-Xmx2048m` e usa una macchina con almeno 4 core.

**Errori su versione di Java**
Gradle deve girare con JDK 17 (`java -version`).

**Non vedo l'overlay**
- GPS acceso
- permesso "Visualizza sopra altre app" concesso
- app non chiusa forzatamente
- su alcuni telefoni (per esempio Xiaomi) consenti l'esecuzione in background nelle impostazioni dell'app
