# Sahyadri Pool & Trip — Ecosystem UI Update

## Included
- Home page simplified to show only major product pillars: Explore, Travel, Stay, Community, Safety, Smart.
- Secondary features moved into grouped Menu & Settings sections.
- Added front-end hub routes for Community, Trek Mate Finder, Leaderboard & Badges, Offline Trails & GPX, Local Guides & Experiences, Live Weather, and AI Trek Recommender.
- Added responsive ecosystem cards and community preview section.
- Kept existing Trips, Agency, Hotel/Stay, Forts, SOS, authentication and dashboards untouched at the routing level.
- Added Marathi/Hindi translations for the new home/ecosystem and hub labels using the existing language system.
- Mobile header revised so Login stays visible and aligned with Search/Menu controls on small screens.

## Important implementation note
The new hub pages are professional UI/UX entry points and backend-ready. They do not invent live data. Weather, AI recommendations, GPX files, community matching and guide booking should be connected to their respective backend/API modules when those modules are implemented.

## Validation
- JavaScript syntax checked with Node.js `node --check`.
- Maven package could not be run in the Linux build environment because Maven is not installed and the Windows Maven wrapper cannot execute directly in Linux. Run `mvnw.cmd -DskipTests package` from Windows PowerShell in the project folder for the final Java build check.
