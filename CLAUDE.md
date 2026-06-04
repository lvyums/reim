# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Travel reimbursement (报销管理系统) — a full-stack application for managing business trip expense reimbursements. Frontend and backend are in separate directories under the repo root.

## Tech Stack

**Backend** (`reim-reim-backend/`):
- Java 17, Spring Boot 3.2.12
- MyBatis-Plus 3.5.11 (with `mybatis-plus-spring-boot3-starter`)
- MySQL, Lombok 1.18.36
- All monetary amounts stored in **分 (cents)**, not yuan

**Frontend** (`reim-reim-frontend/`):
- Vue 3.4 + Vue Router 4 + Pinia
- Element Plus UI library
- Axios for HTTP, Vite for build

## Build & Run Commands

```bash
# Backend (from reim-reim-backend/)
./mvnw spring-boot:run          # Run dev server (port 8000 per application.yml)
./mvnw clean package -DskipTests # Build JAR
./mvnw test                      # Run tests

# Frontend (from reim-reim-frontend/)
npm install                      # Install deps
npm run dev                      # Dev server (default Vite port 5173)
npm run build                    # Production build
npm run lint                     # ESLint fix
npm run format                   # Prettier format
```

## Architecture

### Backend Layers (`com.viessmart.reimburse`)

| Layer | Package | Notes |
|-------|---------|-------|
| Controller | `controller` | REST endpoints under `/api/reimbursement/`. Returns `Result<T>` wrapper. |
| Service | `service` (interface) → `service.impl` | Business logic. Services extend `IService<Entity>`. |
| Mapper | `mapper` | MyBatis-Plus mappers extend `BaseMapper<Entity>`. XML maps in `resources/mapper/`. |
| Entity | `entity` | DB table mappings. Use `@TableName`, `@TableId(type=AUTO)`. |
| DTO | `dto` | Input objects (save/query). |
| VO | `vo` | Output objects returned to frontend. |
| Config | `config` | MyBatis-Plus pagination, Jackson, CORS, Tomcat, subsidy standards. |
| Common | `common` | `Result<T>` response wrapper, `FormStatusEnum`. |

### Key Domain

- **ReimForm** (报销单): The core entity. Status flow: DRAFT(1) → SUBMITTED(2), can → CANCELED(4). DELETED(3) is logical delete.
- **ReimItinerary** (行程): Travel segments linked to a form (departure/arrival city + dates).
- **ReimSubsidy** (补助): Subsidy records per itinerary.
- **ReimSubsidyCalendar** (补助日历): Daily breakdown of meal/transport/communication subsidies.
- **SubsidyStandardConfig**: City-tier-based subsidy rates (一线/二线/三线 cities).

### Frontend Structure

- `src/api/` — Axios wrappers matching backend endpoints
- `src/views/MainView.vue` — Form list page
- `src/views/FormView.vue` — Form detail/edit page
- `src/router/index.js` — Two routes: `/` (list), `/form` (detail)
- `src/utils/request.js` — Axios instance pointing to `http://localhost:8000`

### Database

- Database name: `vimanage_reim`
- Schema SQL: `reim-reim-backend/src/main/resources/sql/vimanage_reim.sql`
- All tables use BIGINT UNSIGNED auto-increment primary keys
- Logical delete via `deleted` field (0=active, 1=deleted)
- Optimistic locking via `version` column on `reim_form`

## Coding Conventions

- All classes, methods, and fields must have Chinese comments
- Service interfaces in `service/`, implementations in `service/impl/`
- Use Lombok `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` on entities
- Entity classes must NOT be returned directly to frontend — use VOs
- `@Transactional` only on Service layer methods
- Use `@Slf4j` for logging, never `System.out.println`
- Validation: `jakarta.validation.constraints.*` (Spring Boot 3.x)
- All amounts in 分 (integer cents). Display conversion to 元 happens in frontend.
