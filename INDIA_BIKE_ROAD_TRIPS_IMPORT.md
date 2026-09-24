# India Biker Routes Import

The biker route master is bundled at:
`src/main/resources/data/INDIA_BIKE_ROAD_TRIPS_MASTER.csv`

After starting the Spring Boot application and logging in as an ADMIN, import it with:

`POST http://localhost:8080/api/bike-road-trips/import`

No multipart file body is required. The server imports the bundled CSV.

Verify with:

`GET http://localhost:8080/api/bike-road-trips/count`

and:

`GET http://localhost:8080/api/bike-road-trips`
