# India Nature + Special Heritage Master

## Scope

`src/main/resources/data/INDIA_NATURE_SPECIAL_MASTER.csv` is the new destination discovery registry for Sahyadri Pool & Trip.

It contains:
- Maharashtra-first nature destinations
- India-wide nature destinations: waterfalls, beaches, caves, valleys, lakes, peaks, wildlife areas, islands and mountain landscapes
- 12 Jyotirlinga special section
- UNESCO World Heritage special section
- selected UNESCO Tentative List natural/geological/cave candidates
- online photo/info lookup fields and verification state

## Important data-status rule

This is a **discovery/draft registry**, not a claim that every row is officially verified. Exact attraction rows are marked `AI_CURATED_NEEDS_VERIFICATION` unless the source is explicitly official UNESCO. Regional long-tail candidate rows are marked `CANDIDATE` and `DRAFT_FOR_REVIEW`.

The frontend therefore:
1. prioritizes the Maharashtra section;
2. shows special Jyotirlinga and UNESCO sections separately;
3. resolves destination photos from Wikipedia/Wikimedia Commons at runtime;
4. shows `PENDING` rather than inventing coordinates or a mismatched photo;
5. preserves source/verification status on the destination card/detail view.

## Current size

- 2,133 rows
- 12 Jyotirlinga rows
- 55 UNESCO/heritage rows: 45 current UNESCO World Heritage properties plus 10 selected UNESCO Tentative List natural/geological/cave candidates
- 270 Maharashtra-priority rows
- remaining rows are India-wide nature/discovery candidates; some regional rows are intentionally draft candidates rather than claimed named attractions

## Import

Admin only:

```http
POST /api/destinations/import
```

After import:

```http
GET /api/destinations/count
GET /api/destinations/section/MAHARASHTRA_PRIORITY
GET /api/destinations/section/JYOTIRLINGA
GET /api/destinations/section/WORLD_HERITAGE
GET /api/destinations/section/INDIA_NATURE
```

## Sources / provenance

UNESCO official India World Heritage list is the authoritative source for the World Heritage section. Maharashtra nature coverage follows the Government of Maharashtra tourism material where applicable. Long-tail discovery candidates are intentionally marked as draft and should be progressively verified before being presented as officially confirmed locations.
