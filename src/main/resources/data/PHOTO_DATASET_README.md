# Maharashtra Fort & Travel Spot Photo Dataset

## Coverage
- 390 Maharashtra fort records from `MAHARASHTRA_FORTS_MASTER_FINAL_390.csv`
- 71 Maharashtra travel-spot records from `MAHARASHTRA_SPOTS_MASTER.csv`
- Total photo registry records: 461

## Provider
Wikimedia Commons is used for online image resolution. The registry stores a per-destination online search URL and API query rather than downloading copyrighted images into the application.

## Mismatch protection
The frontend requests candidate images from Wikimedia Commons and accepts an image only when the image title contains all meaningful tokens of the destination name (with generic words such as Fort/Killa removed). If no matching result is found, the UI keeps a `Photo pending verified match` placeholder.

This prevents a generic Maharashtra landscape or another fort/spot from silently being assigned to the wrong destination.

## Registry files
- `MAHARASHTRA_PHOTO_REGISTRY.json` — combined 461-record registry
- `MAHARASHTRA_PHOTO_REGISTRY.csv` — combined spreadsheet-friendly registry
- `MAHARASHTRA_FORT_PHOTO_REGISTRY_FULL.json` — 390 fort records
- `MAHARASHTRA_FORT_PHOTO_REGISTRY_FULL.csv` — 390 fort records
- `MAHARASHTRA_SPOT_PHOTO_REGISTRY.json` — 71 spot records
- `MAHARASHTRA_SPOT_PHOTO_REGISTRY.csv` — 71 spot records

Each record includes `assetId`, `assetType`, `name`, district/category where available, `photoProvider`, `photoQuery`, `photoSearchUrl`, `photoApiQuery`, and a mismatch-protection rule.

## Licensing
The application does not copy the image files into the project. When an image is resolved, the browser loads it from Wikimedia Commons and the UI credits Wikimedia Commons. The individual file's license should be checked on its Commons file page before any future permanent download or redistribution.
