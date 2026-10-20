# Guida per sviluppatori

Come funziona Fastigum al suo interno e come personalizzarlo. Per compilare il progetto vedi [SETUP_GUIDE.md](SETUP_GUIDE.md).

## Architettura

L'app ha tre componenti Kotlin:

- **`MainActivity`**: interfaccia, permessi, avvio e arresto del servizio.
- **`LocationOverlayService`**: servizio in primo piano (`foregroundServiceType="location"`) che legge il GPS e disegna l'overlay.
- **`GeoidCorrection`**: oggetto che salva le preferenze in `SharedPreferences` e calcola l'altitudine corretta.

```
MainActivity ──avvia──▶ LocationOverlayService ──legge──▶ GeoidCorrection
     │                          │                              ▲
     └── salva slider/switch ───┴──────── SharedPreferences ───┘
```

L'activity e il servizio comunicano solo tramite le preferenze condivise e l'avvio/arresto del servizio: non c'è binding (`onBind` restituisce `null`).

## Calcolo dell'altitudine

Il GPS fornisce `location.altitude`, riferita all'ellissoide WGS84. L'altitudine mostrata è:

```
altitudine corretta = altitudine grezza - correzione geoide
```

La correzione (l'ondulazione del geoide, detta N) dipende dalla zona: circa 47 m nell'Italia settentrionale, circa 55 m nel Regno Unito. Il valore predefinito, se l'utente non ha mai scelto nulla, è 0.

## Preferenze salvate

Tutte in `SharedPreferences` con nome `overlay_prefs`:

| Chiave | Tipo | Predefinito | Scritta da |
|---|---|---|---|
| `geoid_undulation_n` | float | 0 | slider e preset in `MainActivity` |
| `show_accuracy` | boolean | true | interruttore in `MainActivity` |
| `overlay_size` | stringa (`PICCOLA`, `MEDIA`, `GRANDE`) | `PICCOLA` | doppio tap in `LocationOverlayService` |

## Comportamento dell'overlay

- **Aggiornamento**: ogni secondo, con priorità `PRIORITY_HIGH_ACCURACY`.
- **Trascinamento**: l'overlay segue il dito (`ACTION_MOVE`).
- **Doppio tap**: passa ciclicamente tra le tre dimensioni e salva la scelta.
- **Chiusura**: se l'overlay viene rilasciato nel 12% inferiore dello schermo, il servizio si ferma (`stopSelf()`). Durante il trascinamento in quella zona lo sfondo diventa rosso.

## Personalizzazioni

Tutti i punti seguenti sono in `LocationOverlayService.kt`.

### Dimensioni dell'overlay

```kotlin
private enum class OverlaySize(val textSizeSp: Float, val paddingPx: Int) {
    PICCOLA(16f, 24),
    MEDIA(24f, 32),
    GRANDE(34f, 40)
}
```

Modifica la dimensione del testo (sp) e il margine interno (px) di ogni livello, oppure aggiungi un livello: il doppio tap li scorre tutti in ordine.

### Frequenza di aggiornamento

```kotlin
val locationRequest = LocationRequest.Builder(
    Priority.PRIORITY_HIGH_ACCURACY,
    1000
).build()
```

Il secondo parametro è l'intervallo in millisecondi. Valori più alti riducono il consumo di batteria, valori più bassi rendono più fluido l'aggiornamento.

### Zona di chiusura

```kotlin
dismissZoneTopY = (screenHeight * 0.88).toInt()
```

Il numero indica da che altezza dello schermo (0 = in alto, 1 = in basso) parte la zona di chiusura.

### Colori dell'overlay

`COLOR_NORMAL` e `COLOR_DISMISS` (all'inizio della classe) definiscono lo sfondo normale e quello mostrato nella zona di chiusura, nel formato ARGB.

### Testo mostrato

In `updateOverlay()`:

```kotlin
"⛰ ${altitudeCorretta}m (±${accuracy}m)"
```

Puoi cambiare il formato, aggiungere campi dalla classe `Location` o convertire le unità.

## Aggiungere un preset della correzione geoide

Esempio: un nuovo pulsante per un'altra zona.

1. In `res/layout/activity_main.xml` aggiungi un pulsante accanto a quelli esistenti (`btn_preset_italia`, `btn_preset_uk`, `btn_preset_custom`).
2. In `res/values/strings.xml` e `res/values-it/strings.xml` aggiungi il testo, per esempio `preset_francia`.
3. In `MainActivity.kt` collega il pulsante con `applyPreset(valore)`, come già fatto per Italia (47) e UK (55), e includilo in `updatePresetSelection()` perché si evidenzi quando il valore corrisponde.

## Palette e stile

I colori dell'interfaccia sono in `res/values/colors.xml` (palette Trail):

| Nome | Valore | Uso |
|---|---|---|
| `trail_background` | `#F6F2E9` | sfondo |
| `trail_text_primary` | `#22301F` | testo principale |
| `trail_text_secondary` | `#5C6B53` | testo secondario |
| `trail_text_hint` | `#8A9280` | testo attenuato |
| `trail_border` | `#DDD4C0` | bordi e separatori |
| `trail_green` | `#5C8A3F` | accento principale, pallino di slider e switch |
| `trail_orange` | `#E8622C` | accento secondario, pulsante principale |
| `trail_orange_bg` | `#FFF3EC` | sfondo del preset selezionato |
| `trail_card_white` | `#FFFFFF` | schede |
| `trail_track` | `#948B76` | traccia dell'interruttore accuratezza |

Il font è Manrope, dichiarato su ogni `TextView` con `android:fontFamily`. Non impostarlo a livello di tema in `styles.xml`: sovrascriverebbe i singoli pesi.

## Localizzazione

I testi sono in `res/values/strings.xml` (inglese, predefinito) e `res/values-it/strings.xml` (italiano). Per aggiungere una lingua crea la cartella `values-xx` con le stesse chiavi.

## Aspetti da conoscere per Google Play

- Il servizio dichiara `foregroundServiceType="location"` e il permesso `FOREGROUND_SERVICE_LOCATION`: sono richiesti dalle versioni recenti di Android e Play chiede una dichiarazione d'uso in Console.
- `compileSdk` e `targetSdk` sono 36, come richiesto da Play per nuove app e aggiornamenti.
- `onBackPressed` non è usato; resta da sostituire `startActivityForResult` in `MainActivity` (deprecato) con l'API Activity Result, intervento di pulizia non urgente.

## Idee di estensione

- Mostrare altre informazioni dalla classe `Location`, come velocità o direzione.
- Unità di misura in piedi.
- Altri preset della correzione geoide, oppure calcolo automatico della correzione dalla posizione.
- Impostazioni per colore e trasparenza dell'overlay.
