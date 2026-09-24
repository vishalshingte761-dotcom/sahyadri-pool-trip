# Sahyadri Pool & Trip — Destination Photo + Information Final Pass

This build keeps the responsive frontend and expands the destination registry to **390 forts + 74 spots = 464 destination records**.

## Photo strategy
1. Destination photos are resolved at runtime from exact entity matches on Wikipedia/Wikimedia Commons where available.
2. Curated Commons images are used for the requested Rajgad Suvela Machi, Kalsubai Temple/sunrise, and the eight Ashtavinayak entries.
3. Kalu Waterfall and Darya Ghat have an exact-name Commons-first lookup plus a clearly credited online fallback because exact Commons media availability is limited.
4. The UI does not silently replace a destination with an unrelated location. If no reliable match is found, it shows `Photo pending verified match`.
5. Photo cache keys are versioned (`v3`) so the new resolver does not reuse the previous bad photo cache.

## Fort photo audit rule
All 390 fort records remain in the master registry. The resolver now uses **strict exact-name matching** for runtime Commons fallback and no longer accepts an unrelated first Wikipedia search result. This is intentionally conservative: a missing/ambiguous match stays pending instead of showing the wrong fort photo.

This is an automated photo-source audit, not a claim that every one of the 390 images has been manually field-verified by a human. The supplied fort registry remains the source of truth for verification status.

## New spots
- Kalu Waterfall — WATERFALL
- Naneghat — GHAT
- Darya Ghat — GHAT

The spot CSV now contains 74 rows. Numeric fields that are not known are left blank so the importer stores them as `NULL`; the UI displays them as `PENDING`.

## Homepage
The three destination cards display the requested customer-facing prices: **₹1,200, ₹1,500 and ₹1,700**. These are presentation prices for the homepage cards and are not connected to a trip booking record.

## Registration / navigation fixes
- Admin role card has been removed from the public “Built for every role” section.
- Admin can still use the normal Login page directly.
- Driver Registration is now a real hash link as well as an explicit router action, so clicking it reliably opens `#driver-register`.
- Direct browser routes such as `/driver-register` and `/trips` forward to the SPA entry page.
- `/data/**` is public so the photo registry can load for anonymous visitors.

## Responsive
The existing site-wide responsive CSS is preserved, including mobile/tablet/laptop layouts, forms, cards, dashboards, modals, forts, spots, footer and navigation.
