# Sahyadri Pool & Trip — Audit Status

This source package contains the audited/hardened application source.

## Verified during audit
- Spring Boot main class is explicitly configured in `pom.xml`.
- Public self-registration is restricted to Traveler; Driver/Agency use document-verification onboarding and Stay Owner accounts require admin-approved onboarding.
- Driver trip creation requires an existing APPROVED DriverProfile.
- Driver trip updates re-calculate booked seats from the pre-update capacity and cannot edit cancelled trips.
- Trip updates use a pessimistic lock.
- Traveler booking cancellation uses a transaction and locks the trip row before releasing seats.
- Driver emergency-contact API supports verified DriverProfile data and legacy driver fallback stored on the user account.
- Complaint text is validated and attachment count is capped at 10.
- Upload storage uses a restricted MIME allow-list and safe server-generated extensions.
- Property availability rejects inactive properties.
- OTP dev mode defaults to false and secrets are environment-driven.

## Runtime note
Run `mvn clean compile` and `mvn spring-boot:run` in the local environment before runtime API tests. Payment/Razorpay end-to-end activation remains intentionally deferred until the final payment phase.

## Security note
Do not commit database passwords, JWT secrets, SMTP app passwords, Razorpay secrets, or uploaded KYC documents to source control. Rotate any credentials that have been exposed outside the local environment.

## Trip Pricing / Vehicle Model
- Added server-side TripPricingService with configurable internal vehicle rate catalogue.
- Customer-facing trip results show only the calculated per-person fare and booking/service total; internal per-km rates are not exposed to travelers.
- Added vehicle catalogue endpoint for traveler vehicle filtering without rates.
- Added admin-only internal rate-card endpoint.
- New driver trips calculate fare from verified vehicle type, one-way distance and return-trip flag.
- Return trips calculate at 2x one-way route distance.
- Sahyadri platform margin is 10% of vehicle base fare and service fee is ₹69 per booking.
- Booking totals include the service fee once per booking, not once per passenger.
- Legacy pricePerSeat remains for backward compatibility with existing trips.
