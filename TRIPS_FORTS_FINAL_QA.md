# Sahyadri Pool & Trip — Trips + Forts Final QA Notes

## Trips
- `GET /api/trips` and `GET /api/trips/search` are public read-only endpoints so Find Trips works before login.
- Vehicle catalog and customer quote endpoints are public read-only.
- Every live trip card shows vehicle, capacity, seats, one-way/return and final customer fare.
- Booking is available to Traveler accounts; non-travelers are sent to Login.
- Customer UI does not display internal rate/km or platform margin.
- Driver Create Trip loads the verified vehicle and shows a server-calculated fare preview.

## Forts
- The 390-fort registry is preserved.
- `MAHARASHTRA_FORT_PHOTO_REGISTRY.json` contains one photo record per fort.
- Photos are resolved at runtime from Wikimedia Commons only when the returned file title contains the exact fort name.
- If no exact match is found, the UI shows `Photo pending verified match` rather than using an unrelated image.
- Fort detail includes location/directions, route information, and a location-based fare estimator.
- The fare estimator calculates road distance from the user's current location using OSRM and shows final per-seat fare only.
- `Find Trips to this Fort` sends the fort name into the live trip search; actual booking still requires an available driver-published trip.

## UI
- How It Works removed from primary navbar and retained in Menu & Settings.
- Dark section heading/body contrast fixed.
- Fort cards have a dedicated photo corner and responsive layout.
