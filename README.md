# NRscreen

Application Android pour les personnes ayant un écran tactile partiellement cassé.
100% hors ligne, sans serveur, sans publicité, sans API externe.

## Statut
MVP en développement — squelette de projet.

## Structure
- `onboarding/` — demande des autorisations (overlay, accessibilité)
- `test/` — test de grille pixel art, carte de zones mortes, monitoring continu, détection de faux positifs
- `control/` — gestes, boutons physiques externes, boutons flottants, service d'accessibilité
- `launcher/` — launcher temporaire qui réorganise les icônes
- `report/` — rapport visuel exportable
- `history/` — historique des apps les plus touchées par la casse
- `simulator/` — simulateur de zones mortes pour dev/test sans device cassé
- `accessibility/` — export de config pour que d'autres apps bénéficient de la détection
- `prevention/` — mode préventif pour écran fissuré non encore mort
- `data/` — stockage local des profils (100% offline)

## Build
Le build se fait via GitHub Actions (`.github/workflows/build.yml`), déclenché
à chaque push sur `main` ou manuellement. L'APK debug est disponible en
artifact téléchargeable depuis l'onglet Actions du repo.
