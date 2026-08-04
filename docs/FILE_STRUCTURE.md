# 📁 Struttura Completa dei File

Guida visuale su dove mettere ogni file nel progetto Android.

## 🏗️ Struttura Finale del Progetto

```
AltitudeOverlay/                          (Root del progetto)
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/altitudeoverlay/
│   │   │   │       ├── MainActivity.kt                    ← Sostituisci
│   │   │   │       └── LocationOverlayService.kt          ← Nuovo file
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml                 ← Sostituisci
│   │   │   │   │
│   │   │   │   ├── drawable/
│   │   │   │   │   └── info_background.xml               ← Nuovo file
│   │   │   │   │
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml                       ← Sostituisci
│   │   │   │   │   ├── styles.xml                        ← Nuovo file
│   │   │   │   │   ├── colors.xml                        (Auto-generato)
│   │   │   │   │   ├── themes.xml                        (Auto-generato)
│   │   │   │   │   └── dimens.xml                        (Auto-generato)
│   │   │   │   │
│   │   │   │   ├── mipmap/                               (Icone)
│   │   │   │   │   ├── ic_launcher.xml
│   │   │   │   │   └── ic_launcher_round.xml
│   │   │   │   │
│   │   │   │   └── values-night/                         (Auto-generato)
│   │   │   │       └── themes.xml
│   │   │   │
│   │   │   └── AndroidManifest.xml                       ← Sostituisci
│   │   │
│   │   └── test/                                         (Non toccare)
│   │
│   ├── build.gradle.kts                                   ← Sostituisci
│   ├── proguard-rules.pro                                (Non toccare)
│   └── .gitignore                                        (Non toccare)
│
├── gradle/
│   └── wrapper/                                          (Non toccare)
│
├── build.gradle.kts                                      (Non toccare)
├── settings.gradle.kts                                   (Non toccare)
├── gradle.properties                                     (Non toccare)
│
└── [File di configurazione IDE]

```

## 📄 Lista Completa di File da Aggiungere/Modificare

### 1️⃣ File Kotlin

#### `MainActivity.kt`
```
Posizione: app/src/main/java/com/example/altitudeoverlay/MainActivity.kt
Azione: SOSTITUISCI (esiste già, sovrascrivilo)
Versione: Base (sempre consigliato iniziare da qui)
```

#### `LocationOverlayService.kt`
```
Posizione: app/src/main/java/com/example/altitudeoverlay/LocationOverlayService.kt
Azione: CREA NUOVO FILE
Versione: Base
Como creare: Clicca destro su "altitudeoverlay" → New → Kotlin File/Class
```

#### `LocationOverlayServiceAdvanced.kt` (Opzionale)
```
Posizione: app/src/main/java/com/example/altitudeoverlay/LocationOverlayServiceAdvanced.kt
Azione: CREA NUOVO FILE
Versione: Avanzata (trascinabile)
Quando: Dopo che la versione base funziona
```

### 2️⃣ File XML Layout

#### `activity_main.xml`
```
Posizione: app/src/main/res/layout/activity_main.xml
Azione: SOSTITUISCI (esiste già)
Nota: Se non esiste la cartella 'layout', creala
```

### 3️⃣ File XML Drawable

#### `info_background.xml`
```
Posizione: app/src/main/res/drawable/info_background.xml
Azione: CREA NUOVO FILE
Come creare: Clicca destro su "drawable" → New → XML Resource File
Root Element: shape
```

### 4️⃣ File XML Values

#### `strings.xml`
```
Posizione: app/src/main/res/values/strings.xml
Azione: SOSTITUISCI (esiste già)
Nota: Contiene i testi dell'app
```

#### `styles.xml`
```
Posizione: app/src/main/res/values/styles.xml
Azione: CREA NUOVO FILE (potrebbe non esistere)
Come creare: Clicca destro su "values" → New → Values Resource File
Filename: styles
```

### 5️⃣ File di Configurazione

#### `AndroidManifest.xml`
```
Posizione: app/src/main/AndroidManifest.xml
Azione: SOSTITUISCI (esiste già)
IMPORTANTE: Contiene i permessi necessari
```

#### `build.gradle.kts` (a livello app)
```
Posizione: app/build.gradle.kts
Azione: SOSTITUISCI (esiste già)
IMPORTANTE: Contiene le dipendenze (Google Play Services)
Dopo: Sincronizza Gradle
```

### 6️⃣ File Documentazione

Questi sono di aiuto, non vanno nel progetto:
- `README.md` - Leggi per info generali
- `SETUP_GUIDE.md` - Leggi per setup passo-passo
- `ADVANCED_GUIDE.md` - Leggi per versione avanzata
- `QUICK_START.md` - Leggi per inizio veloce
- `FILE_STRUCTURE.md` - Questo file

## 🗺️ Mappa Azioni

### ✏️ SOSTITUISCI (questi file esistono già)
```
1. MainActivity.kt
2. activity_main.xml
3. strings.xml
4. AndroidManifest.xml
5. build.gradle.kts
```

### ✨ CREA NUOVO FILE
```
1. LocationOverlayService.kt (cartella java)
2. LocationOverlayServiceAdvanced.kt (opzionale, cartella java)
3. info_background.xml (cartella drawable)
4. styles.xml (cartella values)
```

## 🎯 Procedura Passo-Passo

### Fase 1: Setup Iniziale (2 min)
```
1. Apri Android Studio
2. File → New → New Android Project
3. Usa: com.example.altitudeoverlay
4. Clicca Finish
5. Aspetta sincronizzazione
```

### Fase 2: Aggiungi Dipendenze (3 min)
```
1. Apri: app/build.gradle.kts
2. Sostituisci il contenuto
3. Sincronizza Gradle
4. Aspetta completamento
```

### Fase 3: Configura Manifest (2 min)
```
1. Apri: app/src/main/AndroidManifest.xml
2. Sostituisci il contenuto
3. Salva
```

### Fase 4: Aggiungi File Kotlin (3 min)
```
1. Sostituisci MainActivity.kt
2. Crea LocationOverlayService.kt
3. Salva entrambi
```

### Fase 5: Aggiungi File XML (2 min)
```
1. Sostituisci activity_main.xml
2. Crea info_background.xml
3. Sostituisci/Crea strings.xml
4. Crea styles.xml
5. Salva tutti
```

### Fase 6: Build e Test (3 min)
```
1. Build → Rebuild Project
2. Aspetta "Build Successful"
3. Run → Run 'app'
4. Seleziona dispositivo
5. Testa l'app
```

**Tempo totale: ~15 minuti**

## 📋 Checklist Veloce

Prima di compilare, verifica:

- [ ] MainActivity.kt sostituito
- [ ] LocationOverlayService.kt creato
- [ ] activity_main.xml sostituito
- [ ] info_background.xml creato
- [ ] strings.xml sostituito
- [ ] styles.xml creato
- [ ] AndroidManifest.xml sostituito
- [ ] build.gradle.kts sostituito
- [ ] Gradle sincronizzato
- [ ] Nessun errore rosso nell'editor

## 🐛 Problemi Comuni & Soluzioni

### "File not found"
- Verifica il percorso esatto
- Assicurati di usare la giusta cartella

### "Unresolved reference"
- Sincronizza Gradle (Sync Now)
- Invalida cache (File → Invalidate Caches)

### "Cannot create class in package"
- Verifica che il package sia corretto: `com.example.altitudeoverlay`
- Crea la struttura di cartelle manualmente se necessario

### "Build failed"
- Controlla che build.gradle.kts sia corretto
- Sincronizza di nuovo

## ✅ Verifiche Finali

Dopo che tutto è setup:

```kotlin
// MainActivity.kt deve importare:
import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

// LocationOverlayService.kt deve importare:
import com.google.android.gms.location.LocationServices
import android.view.WindowManager
```

Se il codice è rosso, sincronizza Gradle di nuovo.

## 🎨 Cartelle che Dovrebbero Esistere

```
app/src/main/res/
├── drawable/          ← Aggiungi qui info_background.xml
├── layout/            ← Sostituisci activity_main.xml
├── values/            ← Sostituisci strings.xml, aggiungi styles.xml
├── values-night/      (auto)
└── mipmap/            (non toccare)
```

Se cartelle non esistono, creale:
- Clicca destro su `res/`
- New → Folder

## 📱 Struttura App

```
┌─────────────────────────────┐
│  MainActivity               │
│  (2 pulsanti: Avvia/Ferma) │
└────────────┬────────────────┘
             │ Clicca Avvia
             ▼
┌─────────────────────────────┐
│  LocationOverlayService     │
│  (Crea overlay con GPS)     │
└────────────┬────────────────┘
             │ Mostra
             ▼
┌─────────────────────────────┐
│  Overlay su tutte le app    │
│  Altitudine: 324m           │
│  Accuratezza: ±12m          │
└─────────────────────────────┘
```

## 🎯 Scorciatoie Utili

### In Android Studio:
- `Ctrl+Shift+A` - Cerca azioni
- `Ctrl+N` - Crea nuovo file
- `Ctrl+Alt+S` - Impostazioni
- `Ctrl+B` - Compila
- `Shift+F10` - Run

### Nel File System:
- Copia/Incolla direttamente i file XML
- Oppure copia contenuto e incolla in Android Studio

## 📞 Se Hai Dubbi

1. Leggi `SETUP_GUIDE.md` - Ha screenshot e dettagli
2. Leggi `QUICK_START.md` - Ha una versione condensata
3. Controlla il tuo percorso - Molto importante!

---

**Struttura = Fondamento dell'app. Non saltare passaggi!** 🏗️
