# Overlay Altitudine GPS

Un'app Android che mostra un overlay persistente con l'altitudine GPS in tempo reale. Perfetto per l'uso durante la navigazione con Google Maps.

## 📋 Caratteristiche

- ✅ Overlay sempre visibile anche sopra altre app (Maps, etc.)
- ✅ Aggiornamento dell'altitudine ogni 1 secondo
- ✅ Mostra anche l'accuratezza del rilevamento GPS
- ✅ Funziona in background
- ✅ Basso consumo di batteria (usa FusedLocationProviderClient)
- ✅ Interfaccia semplice e intuitiva

## 🗂️ Struttura del Progetto

```
├── LocationOverlayService.kt    # Servizio che gestisce l'overlay
├── MainActivity.kt               # Activity principale
├── activity_main.xml            # Layout dell'activity
├── info_background.xml          # Drawable per gli stili
├── strings.xml                  # Stringhe dell'app
├── styles.xml                   # Tema dell'app
├── AndroidManifest.xml          # Configurazione app
└── build.gradle.kts             # Dipendenze
```

## 🔧 Installazione

### Prerequisiti
- Android Studio 2022.1 o superiore
- SDK minimo: Android 5.0 (API 21)
- SDK target: Android 14 (API 34)

### Passaggi

1. **Clona o crea il progetto in Android Studio**
   ```bash
   File → New → New Android Project
   ```

2. **Copia i file**
   - `LocationOverlayService.kt` → `app/src/main/java/com/example/altitudeoverlay/`
   - `MainActivity.kt` → `app/src/main/java/com/example/altitudeoverlay/`
   - `activity_main.xml` → `app/src/main/res/layout/`
   - `info_background.xml` → `app/src/main/res/drawable/`
   - `strings.xml` → `app/src/main/res/values/`
   - `styles.xml` → `app/src/main/res/values/`
   - `AndroidManifest.xml` → `app/src/main/`
   - `build.gradle.kts` → `app/`

3. **Sincronizza Gradle**
   ```
   File → Sync Now
   ```

4. **Compila e installa**
   ```
   Run → Run 'app'
   ```

## 📱 Come Usare

1. **Avvia l'app**
2. **Tocca "Avvia Overlay"**
3. **Concedi i permessi richiesti:**
   - Localizzazione (precisa)
   - Permesso di overlay
4. **Apri Google Maps e naviga normalmente**
5. **L'overlay con l'altitudine apparirà in alto a destra dello schermo**

## 🎨 Personalizzazione

### Posizione dell'Overlay
Modifica il file `LocationOverlayService.kt`:

```kotlin
val params = WindowManager.LayoutParams().apply {
    // ...
    gravity = Gravity.TOP or Gravity.END  // TOP/BOTTOM, START/CENTER/END
    x = 0      // Offset orizzontale
    y = 100    // Offset verticale
}
```

### Dimensioni dell'Overlay
Sempre in `LocationOverlayService.kt`:

```kotlin
width = 300   // Larghezza in pixel
height = 120  // Altezza in pixel
```

### Stile e Colori
Modifica in `LocationOverlayService.kt`:

```kotlin
overlayView = TextView(this).apply {
    textSize = 16f                           // Dimensione testo
    setTextColor(0xFFFFFFFF.toInt())         // Colore testo (bianco)
    setBackgroundColor(0xFF000000.toInt())   // Colore sfondo (nero)
}
```

### Frequenza Aggiornamenti
Modifica in `LocationOverlayService.kt`:

```kotlin
val locationRequest = LocationRequest.Builder(
    Priority.PRIORITY_HIGH_ACCURACY,
    1000  // Millisecondi tra gli aggiornamenti (1000 = 1 secondo)
).build()
```

## 📊 Mostra

L'overlay mostra:
- **Altitudine**: Altezza rilevata dal GPS in metri
- **Accuratezza**: Margine di errore del rilevamento GPS

Esempio:
```
Altitudine: 324m
Accuratezza: ±15m
```

## ⚙️ Permessi Richiesti

L'app richiede i seguenti permessi (AndroidManifest.xml):

- `ACCESS_FINE_LOCATION` - Accesso al GPS preciso
- `ACCESS_COARSE_LOCATION` - Accesso al GPS approssimativo
- `SYSTEM_ALERT_WINDOW` - Permesso di mostrare overlay
- `FOREGROUND_SERVICE` - Servizio in foreground
- `FOREGROUND_SERVICE_LOCATION` - Servizio foreground con localizzazione

## 🔋 Ottimizzazione Batteria

L'app utilizza:
- **FusedLocationProviderClient** - Più efficiente di LocationManager
- **Foreground Service** - Permette aggiornamenti precisi anche in background
- **FLAG_KEEP_SCREEN_ON** - Facoltativo, abilita il display durante l'uso

Per risparmiare batteria:
1. Aumenta l'intervallo di aggiornamento (es: 2000ms invece di 1000ms)
2. Cambia `Priority.PRIORITY_HIGH_ACCURACY` a `Priority.PRIORITY_BALANCED_POWER_ACCURACY`

## 🐛 Troubleshooting

### L'overlay non appare
- Controlla che tu abbia concesso il permesso "Visualizza sopra altre app"
- Riavvia l'app
- Verifica che il GPS sia attivo

### L'altitudine è 0 o -1
- Aspetta qualche secondo affinché il GPS si agganci
- Spostati all'aperto per migliore ricezione
- Controlla che Maps abbia localizzazione attiva

### L'app si chiude in background
- Verificare che il servizio foreground sia avviato correttamente
- Controllare i log di Android per eventuali errori

### Alto consumo di batteria
- Aumenta l'intervallo di aggiornamento
- Riduci la precisione del GPS
- Ferma l'overlay quando non lo usi

## 📝 Versioni Android Supportate

- **Minimo**: Android 5.0 (API 21)
- **Target**: Android 14 (API 34)
- **Testato su**: Android 5.0 - 14

## 🎯 Possibili Miglioramenti Futuri

- [ ] Settingpage per la personalizzazione
- [ ] Widget sulla home per il controllo rapido
- [ ] Registrazione della cronologia altitudine
- [ ] Grafici dell'andamento dell'altitudine
- [ ] Modalità scura automatica
- [ ] Notifiche per altitudini significative
- [ ] Esportazione dati in CSV/KML

## 📄 Licenza

Questo progetto è fornito come esempio educativo.

## 👨‍💻 Autore

Creato come esempio di overlay GPS per Android.

## 🤝 Note Importanti

1. L'app utilizza il GPS con alta precisione (HIGH_ACCURACY) - assicurati di avere il GPS abilitato
2. Gli aggiornamenti in foreground continuano anche quando l'app è in background
3. L'overlay rimane sempre visibile finché il servizio è in esecuzione
4. Per migliori risultati, usa la modalità "Alta Precisione" nelle impostazioni di localizzazione

---

**Buone navigazioni!** 🧭📍
