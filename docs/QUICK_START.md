# 🚀 Quick Start - Overlay Altitudine GPS

Hai ricevuto un'app Android completa! Ecco il riassunto in 5 minuti.

## 📦 Cosa Hai

### Versione Base (Consigliato per iniziare)
- ✅ Overlay semplice che mostra altitudine
- ✅ Mostra anche l'accuratezza del GPS
- ✅ Interfaccia con pulsanti Avvia/Ferma
- ✅ Funziona anche mentre usi Maps

### Versione Avanzata (Opzionale dopo)
- ✨ Overlay trascinabile con il dito
- ✨ Mostra anche la velocità
- ✨ Interfaccia migliorata

## 📋 File Principali

| File | Scopo |
|------|-------|
| `MainActivity.kt` | Interfaccia dell'app (pulsanti) |
| `LocationOverlayService.kt` | La magia - gestisce GPS e overlay |
| `activity_main.xml` | Layout della schermata |
| `AndroidManifest.xml` | Configurazione permessi |
| `build.gradle.kts` | Dipendenze (Maps, GPS, ecc.) |

## ⚡ Setup Ultra-Veloce (10 minuti)

```
1. Apri Android Studio
2. New Project → Empty Activity
3. Copia i file nella giusta posizione (vedi SETUP_GUIDE.md)
4. Sincronizza Gradle
5. Run su dispositivo/emulatore
6. Done! 🎉
```

Per istruzioni dettagliate → **leggi `SETUP_GUIDE.md`**

## 🎮 Come Funziona

1. **Accendi l'overlay**: Premi "Avvia Overlay" nell'app
2. **Minimizza l'app**: Home o apri Maps
3. **Naviga**: L'overlay rimane sempre visibile in alto a destra
4. **Ferma**: Riapri l'app e premi "Ferma Overlay"

```
┌─────────────────────────────┐
│ ╱ 📶 Battery   ⏰ 14:35      │  ← Barra di stato
├─────────────────────────────┤
│  ┌──────────────────────┐   │
│  │ Altitudine: 324 m    │ ← OVERLAY (parte dell'app!)
│  │ Accuratezza: ±12 m   │
│  └──────────────────────┘   │
│                             │
│                             │
│  Google Maps (altra app)    │
│  ...                        │
└─────────────────────────────┘
```

## 📱 Permessi (che ti chiederà)

L'app ha bisogno di:
- ✓ **Localizzazione (precisa)** - Per il GPS
- ✓ **Visualizza sopra altre app** - Per l'overlay
- ✓ **Servizio in foreground** - Per continuare in background

## 🎨 Personalizzazioni Facili

### Cambia Position dell'Overlay
File: `LocationOverlayService.kt`
```kotlin
gravity = Gravity.TOP or Gravity.END  // Angolo in alto a destra
// Opzioni: TOP/BOTTOM/CENTER_VERTICAL
//         START/CENTER_HORIZONTAL/END
```

### Cambia Colore dell'Overlay
```kotlin
setTextColor(0xFFFFFFFF.toInt())       // Bianco
setBackgroundColor(0xFF000000.toInt()) // Nero
```

### Cambia Frequenza Aggiornamenti
```kotlin
1000  // Ogni 1 secondo (1000 millisecondi)
// Prova: 500 (più fluido), 2000 (meno batteria)
```

## 📊 Cosa Mostra l'Overlay

```
Altitudine: 324m
Accuratezza: ±12m
```

- **Altitudine**: Quota in metri sopra il livello del mare
- **Accuratezza**: Margine di errore (±)

**Nota**: Il primo fix GPS può richiedere 5-10 secondi

## ❓ FAQ Rapide

### Q: Funziona senza Maps?
**A**: Sì! Funziona con qualsiasi app. Prova il browser, foto, ecc.

### Q: Consuma molta batteria?
**A**: No, è ottimizzato. Consuma poco se aumenti l'intervallo di aggiornamento.

### Q: Posso usare mentre dormo il telefono?
**A**: Sì! Il servizio continua in background.

### Q: Si vede bene al sole?
**A**: Usa la versione avanzata per personalizzare i colori.

### Q: Posso spostarla?
**A**: Nella versione base no, ma sì nella versione avanzata (trascinabile).

## 🆘 Se Non Funziona

### L'overlay non appare
1. Controlla: GPS acceso? ✓
2. Controlla: Permesso overlay concesso? ✓
3. Aspetta: Il primo GPS fix richiede tempo

### L'altitudine mostra 0
1. Spostati all'aperto
2. Aspetta 30 secondi che il GPS si agganci
3. Se usi emulatore: È normale, il GPS virtuale è scarso

### Crashes all'avvio
1. Dai i permessi quando chiesti
2. Controlla i Log (Logcat in Android Studio)

## 📚 Documenti Completi

- **`README.md`** - Documentazione completa
- **`SETUP_GUIDE.md`** - Setup dettagliato passo-passo
- **`ADVANCED_GUIDE.md`** - Versione trascinabile e personalizzazioni

## 🎯 Prossimi Step

### Subito
1. ✅ Setup il progetto (SETUP_GUIDE.md)
2. ✅ Testa su dispositivo
3. ✅ Personalizza colori/posizione

### Dopo
1. 🚀 Upgrade a versione avanzata (ADVANCED_GUIDE.md)
2. 🎨 Crea icona personalizzata
3. 📤 Pubblicare su Play Store

## 💡 Bonus Tips

- **Posizionamento**: Metti l'overlay in un angolo non coperto da Maps
- **Batteria**: Aumenta `LocationRequest` a 2000ms per risparmiare
- **Accuatezza**: Usa "Alta Precisione" nelle impostazioni GPS Android
- **Responsive**: Abbassa `LocationRequest` a 500ms per aggiornamenti più fluidi

## 🎓 Concetti Usati

L'app usa tecnologie moderne di Android:
- **FusedLocationProviderClient** - API GPS moderna
- **Foreground Service** - Esecuzione in background
- **WindowManager** - Overlay sempre in primo piano
- **LocationCallback** - Aggiornamenti in tempo reale

## 📞 Supporto Tecnico

Se hai problemi:
1. Leggi la sezione "Troubleshooting" in README.md
2. Controlla i Log (Logcat)
3. Verifica i permessi in Impostazioni → App

---

**Sei pronto! Buona navigazione con l'altitudine!** 🧭📍

Per iniziare subito → **SETUP_GUIDE.md**
