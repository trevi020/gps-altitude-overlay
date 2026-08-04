# Guida Versione Avanzata - Overlay Trascinabile

Questa guida spiega come passare alla versione avanzata del servizio che include funzionalità aggiuntive.

## 🚀 Nuove Funzionalità nella Versione Avanzata

### 1. **Overlay Trascinabile**
L'overlay può essere spostato sullo schermo trascinandolo con le dita.

### 2. **Mostra Velocità**
Visualizza la velocità di movimento in km/h (oltre all'altitudine).

### 3. **Layout Migliorato**
Migliore presentazione dei dati con layout verticale ordinato.

### 4. **Tocco Ottimizzato**
Gestione intelligente dei tocchi per distinguere tra trascinamento e tocchi accidentali.

## 📦 Installazione

### Opzione A: Usa la Versione Avanzata (Consigliato)

1. **Sostituisci il servizio**
   - Elimina `LocationOverlayService.kt`
   - Rinomina `LocationOverlayServiceAdvanced.kt` → `LocationOverlayService.kt`
   - Aggiorna i riferimenti nel `MainActivity.kt` (dovrebbero già essere corretti)

2. **Sincronizza e compila**
   ```bash
   Gradle → Sync Now
   Run → Run 'app'
   ```

### Opzione B: Mantieni Entrambe le Versioni

Se vuoi mantenere entrambe le versioni:

1. **Copia `LocationOverlayServiceAdvanced.kt`** così com'è
2. **Aggiungi un toggle nel MainActivity** per scegliere quale servizio usare

Esempio di toggle nel `MainActivity.kt`:

```kotlin
private var useAdvanced = true  // Aggiungi questa variabile

private fun startOverlay() {
    val serviceClass = if (useAdvanced) {
        LocationOverlayServiceAdvanced::class.java
    } else {
        LocationOverlayService::class.java
    }
    
    val intent = Intent(this, serviceClass)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
    // ... resto del codice
}
```

E aggiorna il manifest per registrare entrambi i servizi:

```xml
<service
    android:name=".LocationOverlayService"
    android:foregroundServiceType="location"
    android:exported="false" />

<service
    android:name=".LocationOverlayServiceAdvanced"
    android:foregroundServiceType="location"
    android:exported="false" />
```

## 🎨 Personalizzazione Avanzata

### Cambia Colore dello Sfondo

Modifica in `LocationOverlayServiceAdvanced.kt`:

```kotlin
overlayContainer = LinearLayout(this).apply {
    // ...
    setBackgroundColor(0xCC000000.toInt())  // CC = trasparenza (0-FF)
    // Esempi:
    // 0x99000000 - Più trasparente
    // 0xFF000000 - Completamente opaco
    // 0xFFFF5722 - Arancione scuro
}
```

### Cambia Formato della Velocità

Sostituisci le righe nel metodo `updateOverlay()`:

```kotlin
// Da km/h a m/s
val speed = location.speed  // Mantieni m/s

// Da km/h a mph (miglia orarie)
val speedMph = (location.speed * 2.237).toInt()
```

### Personalizza il Sensibilità del Trascinamento

Nel metodo `handleOverlayTouch()`, modifica il valore di 5:

```kotlin
if (abs(diffX) > 5 || abs(diffY) > 5) {  // 5 pixel è il threshold
    // Aumenta il numero per renderlo meno sensibile
    // Riduci per renderlo più sensibile
}
```

### Aggiungi più Informazioni all'Overlay

Aggiungi campi aggiuntivi nell'overlay:

```kotlin
// Aggiungi dopo speedText:
val accuracyText = TextView(this).apply {
    text = "Accuratezza: --"
    textSize = 12f
    setTextColor(0xFF999999.toInt())
    setPadding(16, 2, 16, 2)
}

overlayContainer.addView(accuracyText)

// E aggiorna in updateOverlay():
accuracyText.text = "Accuratezza: ±${accuracy}m"
```

## 📊 Confronto Base vs Avanzata

| Funzionalità | Base | Avanzata |
|--------------|------|----------|
| Mostra Altitudine | ✅ | ✅ |
| Mostra Accuratezza | ✅ | ✅ |
| Mostra Velocità | ❌ | ✅ |
| Overlay Trascinabile | ❌ | ✅ |
| Aggiornamenti GPS | ✅ | ✅ |
| Servizio Foreground | ✅ | ✅ |

## 🔧 Troubleshooting Versione Avanzata

### L'overlay non si sposta
- Assicurati di usare `LocationOverlayServiceAdvanced`
- Verifica che il flag `FLAG_NOT_FOCUSABLE` sia impostato (permet al tocco di passare)

### La velocità mostra 0
- Aspetta che l'app rilevi il movimento
- La velocità è calcolata dal GPS, non sempre disponibile all'inizio

### Overlay salta quando trascinato
- È normale se il tocco viene interpretato come scroll
- Aumenta il threshold di sensibilità (cambia 5 a 10)

## 💡 Tips & Tricks

1. **Posiziona l'overlay strategicamente**
   - Sopra l'indicatore di connessione
   - In un angolo non utilizzato da Maps
   - Utilizza `lastX` e `lastY` per salvare la posizione

2. **Riduci consumo batteria**
   ```kotlin
   Priority.PRIORITY_BALANCED_POWER_ACCURACY  // Meno preciso ma più efficiente
   ```

3. **Aggiorna frequenza**
   ```kotlin
   LocationRequest.Builder(Priority, 500)  // Ogni 0.5 secondi (più fluido)
   ```

4. **Nasconde l'overlay durante inattività**
   Puoi aggiungere un timer che nasconde l'overlay se non c'è movimento GPS per 30 secondi

## 🎯 Possibili Estensioni

1. **Salvare Posizione dell'Overlay**
   - Salva x, y in SharedPreferences
   - Ripristina quando il servizio si avvia di nuovo

2. **Cambiare Tema**
   - Aggiungi un Intent con i colori personalizzati
   - Permetti all'utente di scegliere tra scuro/chiaro

3. **Notifiche Intelligenti**
   - Avvisa quando si raggiunge un'altitudine target
   - Registra i valori massimi/minimi

4. **Esportazione Dati**
   - Salva cronologia altitudine/velocità
   - Esporta in CSV o KML per MapMyRun, Strava, ecc.

## 📝 Note Importanti

- La versione avanzata ha un consumo batteria leggermente superiore (carica aggiuntiva per il trascinamento)
- Il touch listener potrebbe interferire con app con overlay propri
- Testare su dispositivi diversi per garantire compatibilità

---

**Goditi l'overlay avanzato!** 🚀
