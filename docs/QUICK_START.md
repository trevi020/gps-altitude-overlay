# Quick Start - Fastigum

Fastigum mostra l'altitudine GPS in un overlay flottante sopra qualsiasi altra app, ideale in navigazione. L'app è distribuita a pagamento su Google Play (in arrivo); su GitHub trovi il codice sorgente di ogni versione.

## Come si usa

1. Apri l'app e imposta la **correzione geoide** per la tua zona (in Italia è circa 47).
2. Tocca **Avvia Overlay**.
3. Concedi i permessi richiesti: localizzazione precisa e "Visualizza sopra altre app".
4. Apri Google Maps o un'altra app: l'overlay resta visibile.
5. Per fermarlo, trascinalo in basso oppure riapri Fastigum e tocca **Ferma Overlay**.

## Cosa mostra l'overlay

```
⛰ 324m (±12m)
```

- **Altitudine**: quota sul livello del mare, cioè l'altitudine grezza del GPS (ellissoide WGS84) meno la correzione geoide.
- **Accuratezza** (±): margine di errore stimato dal GPS. Si può nascondere con l'interruttore **Mostra accuratezza GPS** nella schermata principale.

La schermata principale mostra anche, nella scheda in alto, l'altitudine **grezza (WGS84)** e quella **corretta** affiancate.

## Gesti sull'overlay

| Gesto | Effetto |
|---|---|
| Trascina | Sposta l'overlay dove preferisci |
| Doppio tap | Cambia dimensione: piccola, media, grande (la scelta viene ricordata) |
| Trascina nella parte bassa dello schermo | L'overlay diventa rosso: rilascia per chiuderlo |

## Correzione geoide

Il GPS restituisce l'altitudine rispetto all'ellissoide WGS84, che differisce dal livello del mare di un valore dipendente dalla zona. Nell'app puoi scegliere:

- **Italia (47)**
- **UK (55)**
- **Custom**: sposta lo slider da -100 a +100 metri

Il valore scelto viene salvato e usato dall'overlay.

## Permessi

- **Localizzazione precisa**: per leggere il GPS.
- **Visualizza sopra altre app**: per disegnare l'overlay.
- **Servizio in primo piano di tipo posizione**: mantiene il GPS attivo in background. Android mostra per questo una notifica permanente finché l'overlay è attivo.

## Per sviluppatori

```bash
git clone https://github.com/trevi020/gps-altitude-overlay.git
cd gps-altitude-overlay
./gradlew assembleDebug --no-daemon
```

La build di prova non richiede la chiave di firma. Per Codespaces, Android Studio, firma e bundle per Google Play vedi [SETUP_GUIDE.md](SETUP_GUIDE.md).

## FAQ

**Funziona con qualsiasi app?**
Sì, l'overlay si disegna sopra tutte le altre app, non solo Maps.

**Funziona a schermo spento o con l'app in background?**
Il servizio resta attivo in background finché non lo chiudi, ma l'overlay è visibile solo quando lo schermo è acceso.

**Consuma molta batteria?**
La posizione viene aggiornata ogni secondo con priorità ad alta precisione (tramite `FusedLocationProviderClient`), quindi in uso prolungato il GPS incide sul consumo.

**Che lingue supporta?**
Italiano e inglese; l'inglese è la lingua predefinita.

## Problemi comuni

**L'overlay non appare**
- GPS acceso
- permesso "Visualizza sopra altre app" concesso
- app non chiusa forzatamente
- su alcuni telefoni (per esempio Xiaomi) consenti l'esecuzione in background dalle impostazioni dell'app

**L'altitudine non compare o resta "-m"**
Spostati all'aperto e attendi qualche secondo che il GPS si agganci. Con l'emulatore il GPS virtuale non è affidabile: prova su un telefono.

**L'app si chiude all'avvio dell'overlay**
Controlla che entrambi i permessi siano stati concessi e che il GPS sia attivo.

## Altri documenti

- [README.md](README.md) - panoramica
- [SETUP_GUIDE.md](SETUP_GUIDE.md) - compilazione da sorgente e firma
- [FILE_STRUCTURE.md](FILE_STRUCTURE.md) - mappa dei file del progetto
- [ADVANCED_GUIDE.md](ADVANCED_GUIDE.md) - funzionamento interno e personalizzazione
