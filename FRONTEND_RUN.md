Sahyadri Pool & Trip — integrated frontend

Run:
1. Set existing environment variables (DB_*, JWT_SECRET, MAIL_* etc.).
2. mvn clean compile
3. mvn spring-boot:run
4. Open http://localhost:8080/

The frontend is served by Spring Boot from src/main/resources/static.
Do not use Live Server for the integrated build.

New backend endpoints:
POST /api/partners/hotel/register
GET /api/admin/hotel-applications (ADMIN)
PUT /api/admin/hotel-applications/{id}/review (ADMIN)
POST /api/contact

Set CONTACT_ADMIN_EMAIL to the support/admin inbox. It falls back to MAIL_FROM.

Hotel Owner login flow:
- Registration creates a PENDING row in hotel_owner_applications.
- PENDING applications cannot log in.
- Admin APPROVED creates the HOTEL_OWNER user using the original BCrypt password hash.
- Admin REJECTED does not create a user and rejected applications cannot log in.
- Only PENDING applications can be reviewed; already reviewed applications cannot be changed accidentally.


Photo / destination pass:
- 390 forts + 74 spots are covered by the destination photo registry (464 records).
- Photo registry is served from /data/** and is public for anonymous visitors.
- Driver Registration uses #driver-register and direct /driver-register navigation is supported.
- Public role cards no longer expose an Admin card; Admin continues to use the normal Login page.
