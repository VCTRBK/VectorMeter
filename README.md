# VectorMeter 1.0.0 — FINAL SOURCE

This is the final free-routing source project for VectorMeter.

## Features
- RealVector: realtime GPS recording, start/stop, live distance and route trace.
- VectorPlan: tap the OpenStreetMap map to create multiple planned stops.
- Routing/distance: OSRM using OpenStreetMap road data; no Google Maps API/billing.
- Manual KM Awal, automatic KM Akhir, per-leg KM and total.
- VectorMeter branding assets supplied by the user.
- Local persistence for the last VectorPlan.
- No third-party Android libraries are required; the map/routing web assets are loaded online.

## Important
1. An internet connection is needed for the OpenStreetMap tiles and OSRM routing.
2. The OSRM public demo server is for limited use. For heavy/private production use, replace the router endpoint in `app/src/main/assets/index.html` with an appropriate routing provider/server.
3. OpenStreetMap tile usage must follow the OSM tile policy and attribution requirements.
4. This project must be built with a current Android SDK/JDK/Gradle environment. Android's official documentation describes Android Studio + Gradle as the standard build workflow.

## Build
Open the project root in Android Studio and build the app module. A debug APK can be built for testing; a release APK should be signed with a release key before distribution.
