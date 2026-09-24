# Maharashtra Spots Master Import

The supplied `src/main/resources/data/MAHARASHTRA_SPOTS_MASTER.csv` is the single master dataset for Maharashtra spots. It contains 74 records across WATERFALL, BEACH, HILL_STATION, VALLEY, PEAK, CLOUD_POINT, GHAT and ASHTAVINAYAK. Blank numeric fields remain null and the frontend displays them as PENDING. Verification/record status values are preserved exactly; DRAFT_FOR_REVIEW and AI-CURATED records are not presented as officially verified.

## Import once (same pattern as forts)
1. Start Spring Boot and log in as an ADMIN.
2. Obtain the JWT from the normal login endpoint.
3. In Postman send `POST http://localhost:8080/api/spots/import` with header `Authorization: Bearer <ADMIN_JWT>`.
4. Expected response includes `success: true` and `spotCount: 74`.

The import is repeatable because `spot_id` is the primary key; running it again updates the same records instead of creating duplicates.

## Verify in MySQL
```sql
USE sahyadri_pool_trip;
SELECT COUNT(*) AS spot_count FROM spots;
SELECT spot_id, name, category, district, record_status FROM spots ORDER BY spot_id;
```

## Public API
- `GET /api/spots`
- `GET /api/spots/{spotId}`
- `GET /api/spots/name/{name}`
- `GET /api/spots/category/{category}`
- `GET /api/spots/district/{district}`
- `GET /api/spots/status/{status}`
- `GET /api/spots/verification/{status}`
- `GET /api/spots/count`
