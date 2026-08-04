# Guida Completa - Setup Android Studio

Una guida passo dopo passo per creare e configurare l'app dal zero in Android Studio.

## 🎯 Prerequisiti

- Android Studio 2022.1+
- Android SDK 21-34 installati
- Dispositivo Android 5.0+ o emulatore

## 📋 Step 1: Crea un Nuovo Progetto

### 1.1 Avvia Android Studio
- Clicca su "File" → "New" → "New Android Project"

### 1.2 Configura il Progetto
```
Project Name: Altitude Overlay
Package Name: com.example.altitudeoverlay
Save Location: [Cartella desiderata]
Language: Kotlin
Minimum SDK: API 21 (Android 5.0)
```

### 1.3 Seleziona il Template
- Scegli "Empty Activity"
- Clicca "Finish"

## 🗂️ Step 2: Organizza la Struttura

Android Studio genererà una struttura di base. Ecco come dovrebbe essere:

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/altitudeoverlay/
│   │   │   ├── MainActivity.kt          ← Modifica/Sostituisci
│   │   │   └── LocationOverlayService.kt ← Aggiungi nuovo file
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml    ← Modifica/Sostituisci
│   │   │   ├── drawable/
│   │   │   │   └── info_background.xml  ← Aggiungi nuovo file
│   │   │   ├── values/
│   │   │   │   ├── strings.xml          ← Modifica/Sostituisci
│   │   │   │   └── styles.xml           ← Modifica/Sostituisci
│   │   └── AndroidManifest.xml          ← Modifica/Sostituisci
│   └── build.gradle.kts                 ← Modifica/Sostituisci
└── [File di configurazione]
```

## 🔧 Step 3: Modifica build.gradle.kts (a livello app)

1. **Apri**: `app/build.gradle.kts`
2. **Sostituisci tutto il contenuto** con il file `build.gradle.kts` fornito
3. **Sincronizza Gradle**: Click su "Sync Now" (compare in alto)

Aspetta che si completi la sincronizzazione.

## 📱 Step 4: Configura AndroidManifest.xml

1. **Apri**: `app/src/main/AndroidManifest.xml`
2. **Sostituisci il contenuto** con il file `AndroidManifest.xml` fornito
3. **Salva**: Ctrl+S (Cmd+S su Mac)

### Verifica i permessi:
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

## 💻 Step 5: Aggiungi i File Kotlin

### 5.1 MainActivity.kt
1. **Vai a**: `app/src/main/java/com/example/altitudeoverlay/MainActivity.kt`
2. **Sostituisci il contenuto** con il file `MainActivity.kt` fornito
3. **Salva**: Ctrl+S

### 5.2 LocationOverlayService.kt
1. **Clicca destro** su `app/src/main/java/com/example/altitudeoverlay/`
2. **New** → **Kotlin File/Class**
3. **Name**: `LocationOverlayService`
4. **Copia il contenuto** dal file `LocationOverlayService.kt` fornito
5. **Salva**: Ctrl+S

## 🎨 Step 6: Aggiungi i File di Risorse

### 6.1 activity_main.xml
1. **Vai a**: `app/src/main/res/layout/`
2. **Se esiste `activity_main.xml`**, eliminalo o sostituiscilo
3. **Clicca destro** → **New** → **Layout Resource File**
4. **Filename**: `activity_main`
5. **Copia il contenuto** dal file XML fornito
6. **Salva**

### 6.2 info_background.xml (Drawable)
1. **Vai a**: `app/src/main/res/drawable/`
2. **Se la cartella non esiste**, creala
3. **Clicca destro** → **New** → **XML Resource File**
4. **Filename**: `info_background`
5. **Root Element**: `shape`
6. **Copia il contenuto** dal file XML fornito
7. **Salva**

### 6.3 strings.xml
1. **Vai a**: `app/src/main/res/values/strings.xml`
2. **Sostituisci il contenuto** con il file `strings.xml` fornito
3. **Salva**

### 6.4 styles.xml
1. **Vai a**: `app/src/main/res/values/styles.xml`
2. **Se non esiste**, clicca destro nella cartella → **New** → **Values Resource File**
3. **Filename**: `styles`
4. **Sostituisci il contenuto** con il file `styles.xml` fornito
5. **Salva**

## ✅ Step 7: Verifica i File

Dopo aver aggiunto tutti i file, la struttura dovrebbe essere:

```
app/src/main/
├── java/com/example/altitudeoverlay/
│   ├── MainActivity.kt ✓
│   └── LocationOverlayService.kt ✓
├── res/
│   ├── layout/activity_main.xml ✓
│   ├── drawable/info_background.xml ✓
│   ├── values/
│   │   ├── strings.xml ✓
│   │   ├── styles.xml ✓
│   │   └── colors.xml (auto-generato)
│   └── [altre cartelle standard]
└── AndroidManifest.xml ✓
```

## 🧪 Step 8: Sincronizza e Compila

1. **Sincronizza Gradle**
   - Click "Sync Now" (se appare)
   - Oppure: File → Sync Project with Gradle Files

2. **Compila il Progetto**
   - Build → Rebuild Project
   - Aspetta fino a vedere "Build Successful"

## 📱 Step 9: Esegui l'App

### Opzione A: Su Dispositivo Fisico
1. **Connetti il telefono via USB**
2. **Abilita "USB Debugging"**
   - Impostazioni → Opzioni Sviluppatore → USB Debugging
3. **Run** → **Run 'app'**
4. **Seleziona il dispositivo** dalla lista

### Opzione B: Su Emulatore
1. **Avvia un emulatore**
   - Tools → Device Manager → Crea/Avvia dispositivo virtuale
2. **Run** → **Run 'app'**
3. **Seleziona l'emulatore** dalla lista

## 🚀 Step 10: Test dell'App

### Primo Avvio
1. L'app si apre con due pulsanti
2. Tocca "Avvia Overlay"
3. Concedi i permessi:
   - ✓ Localizzazione (precisa)
   - ✓ Visualizza sopra altre app

### Verifica del Funzionamento
1. Apri Google Maps
2. Naviga in una zona
3. Dovresti vedere l'overlay in alto a destra con:
   ```
   Altitudine: XXXX m
   Accuratezza: ±XX m
   ```

## 🐛 Risoluzione Problemi Comuni

### Error: "Unresolved reference 'LocationServices'"
**Causa**: Google Play Services non sincronizzato
**Soluzione**:
1. File → Project Structure
2. Dependencies → Add
3. Aggiungi: `com.google.android.gms:play-services-location`
4. Sincronizza

### Error: "Cannot resolve symbol 'MainActivity'"
**Soluzione**:
1. File → Invalidate Caches
2. Restart
3. Rebuild Project

### L'app crasha al click su "Avvia Overlay"
**Soluzione**:
1. Controlla che tutti i permessi siano stati concessi
2. Abilita il GPS del dispositivo
3. Verifica che non ci siano errori nei Log (Logcat)

### Non vedo l'overlay
**Checklist**:
- [ ] GPS è acceso
- [ ] Permesso "Visualizza sopra altre app" concesso
- [ ] App non è stata force-closed
- [ ] La app sta lavorando in background

## 📝 Personalizzazioni Finali

Dopo che l'app funziona:

### Cambia l'Icona dell'App
1. Right-click su `app/res/mipmap/`
2. Image Asset → Seleziona foto
3. Configura come desiderato

### Cambia il Nome dell'App
1. **File**: `app/src/main/res/values/strings.xml`
2. **Modifica**: `<string name="app_name">Mio Nome App</string>`

### Cambia Colori e Stili
1. **File**: `app/src/main/res/values/styles.xml`
2. **Modifica i valori di colore**

## ✨ Congratulazioni!

La tua app è pronta. Prossimi step facoltativi:

- [ ] Scarica la versione avanzata (vedi ADVANCED_GUIDE.md)
- [ ] Personalizza colori e layout
- [ ] Pubblica su Google Play Store (richiede account sviluppatore)

## 🎓 Prossimi Passi (Opzionali)

1. **Aggiungi SettingsActivity** per configurazioni
2. **Implementa SharedPreferences** per salvare posizione overlay
3. **Aggiungi Widget** per controllo rapido
4. **Crea Notifiche** per altitudini significative

---

**Buona programmazione!** 👨‍💻
