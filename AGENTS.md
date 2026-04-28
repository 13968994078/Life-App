# AGENTS.md

## Repo Shape
- This repo is a two-project app skeleton: `backend/` is Spring Boot, `frontend/` is uni-app, `docs/` contains schema and API notes.
- Trust code over docs if they diverge, but `docs/schema.sql` should now stay aligned with the runtime schema.

## Backend
- Entry point: `backend/src/main/java/com/lifeapp/LifeAppApplication.java`.
- Runtime stack is Spring Boot `2.7.18`, Java `8`, MyBatis-Plus, and MySQL. There is still no auth, Redis, JPA, or multi-user support.
- Mapper interfaces live under `backend/src/main/java/com/lifeapp/mapper/`; service behavior is in `backend/src/main/java/com/lifeapp/service/impl/`.
- Startup seeds sample food and a default setting through `backend/src/main/java/com/lifeapp/config/DataInitializer.java` when the database is empty.
- Backend responses use the `ApiResponse` wrapper with `{ success, message, data }`. Frontend request helpers assume this exact shape.
- Default server port is `8080`; datasource defaults come from `backend/src/main/resources/application.yml` and can be overridden with `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.
- Effective local run command: `mvn spring-boot:run` from `backend/` after MySQL is available and `docs/schema.sql` has been applied.
- Verification requires a real JDK, not just a JRE. In this workspace `mvn test` failed with `No compiler is provided in this environment` because `javac` was unavailable.

## Frontend
- Frontend is a uni-app app intended to be opened from `frontend/` in HBuilderX. README is the only verified run path; do not assume a full Node CLI workflow is set up.
- `frontend/package.json` has only placeholder scripts and no dependencies/lockfile. Treat HBuilderX import/run as the source of truth unless more config is added.
- Frontend entry is `frontend/main.js`; app pages and tab bar are defined in `frontend/pages.json`.
- API base URL is resolved by `frontend/config/network.js`; request calls read `apiBaseUrl` from local storage first, then fall back to `http://localhost:8080/api`.
- Frontend request helper now attaches `Authorization: Bearer <token>` when `authToken` exists in local storage.

## Implemented Surface Area
- Food endpoints are under `/api/food` in `FoodController`.
- Check-in endpoints are under `/api/check-in` in `CheckInController`.
- Settings endpoints are under `/api/settings` in `SettingController`.
- There is no login/user session yet. Backend services currently act as a single hardcoded user (`userId = 1`).
- Auth endpoints now exist under `/api/auth`; current MVP login uses the seeded `user_info` record (`demo` / `123456`) and services fall back to `userId = 1` when no token is present.

## Change Guidance
- Keep `docs/schema.sql` and runtime code aligned when schema changes.
- If you change the backend response envelope, update `frontend/utils/request.js` and the API modules together.
- Before claiming backend verification passed, check `javac -version` first or expect Maven compile/test commands to fail on JRE-only machines.
