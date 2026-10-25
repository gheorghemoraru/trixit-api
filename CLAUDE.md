# Trixit API (trixit-api)

Spring backend of Trixit, the REST API tier of a simple 3-tier application.
Codename: Trixit. What the application does is not defined yet; keep the domain generic until told otherwise.

## Architecture
- 3 tiers, each built and deployed independently:
  1. UI: React app, separate project (`trixit-ui`)
  2. API: this project, Java + Spring
  3. Database: separate process
- Never bundle UI files into this project and never serve the UI from it.
- Clean architecture: keep domain logic free of framework and persistence details. Dependencies point inward (web/persistence -> application -> domain).
- Base package (proposed): `com.trixit`

## Packaging and deployment
- Java 21, Spring Boot 4.1.x, Maven (decided when the skeleton was created).
- Packaging is `war`, final name `api`, so the file is `api.war` and the context path is `/api`.
- It is deployed into a standalone Tomcat 11 (Servlet 6.1), which is what Spring Boot 4 requires. Do not rely on embedded Tomcat in production.
- `spring-boot-starter-tomcat` has scope `provided`.
- The entry class extends `SpringBootServletInitializer` and has no `main` method: the app only runs when deployed as a WAR into Tomcat 11 (`mvn spring-boot:run` and IDE runs do not work).
- The UI is served by the same Tomcat under `/ui/`, same origin: no CORS, no proxy, no redirects. Do not add CORS configuration.

## Database
- For now: in-memory H2 (`jdbc:h2:mem:trixit;DB_CLOSE_DELAY=-1`). Data is lost on every restart or redeploy; that is accepted for now.
- Persistence stays behind repository interfaces defined in the application layer, so switching to a persistent database later is a configuration and adapter change, not a rewrite.
- Schema is created with Flyway migrations, not `ddl-auto`, so the schema is the same when the engine changes.
- The H2 console is disabled.

## Authentication
- Own authentication inside this project. No Google/Facebook or other social login, no external identity provider.
- User registration with email verification only. No identity verification.
- Server-side sessions (`HttpSession`, cookie), not JWT. UI and API share one origin, so the browser sends the session cookie.
- CSRF protection stays ON. The CSRF token cookie uses path `/` (or is served by `GET /auth/csrf`) because the UI page at `/ui/` cannot read a cookie scoped to `/api`.
- Unauthenticated requests get `401`, never a redirect to a login page.
- Passwords: Spring `DelegatingPasswordEncoder` (BCrypt/Argon2). Verification tokens: random, only their hash is stored, with expiry, single use. Email link points to the UI (`/ui/#/verify?token=...`), which calls `POST /auth/verify`.
- Login errors must not reveal whether an email exists. Add rate limiting or temporary lockout on login.
- Authorisation: roles `USER` and `ADMIN` with `@PreAuthorize`, plus object-level checks: every query for user data is scoped to the current user (a user can never read another user's conversations).
- When deployed as a WAR, `server.servlet.session.cookie.*` properties may not apply: set HttpOnly/Secure in `web.xml` and SameSite in Tomcat `context.xml`, and verify on first deployment.
- Require HTTPS (Secure cookies) before real users.

## Testing
- Unit tests are required. Write tests with the code, not afterwards.
- JUnit 5 + Mockito (`@MockitoBean` in Spring tests, not `@MockBean`) + AssertJ.
- Domain and application layers: plain unit tests, no Spring context, ports mocked.
- Web layer: MockMvc slice tests with Spring Security Test (`@WithMockUser`, `csrf()`).
- Persistence: slice test against in-memory H2.
- Security tests are mandatory: 401 when unauthenticated, 403 on missing CSRF token, and a user cannot access another user's data.
- Run with `mvn test` and keep the suite fast.

## Rules
- Never commit secrets (passwords, tokens, SMTP credentials, keys). Use environment variables or untracked config files, and keep `.gitignore` up to date.
- Free tooling wherever possible for CI/CD and hosting.
- Target machine is an old Linux PC with 8 GB RAM: keep builds and runtime light.
- Before pushing: run the tests, and check `git status` and `git diff` so no secrets or build output are included.
- Ask before adding a new dependency or changing a decision recorded in this file.

## Open decisions
- Persistent database engine (in-memory H2 for now)
- Email provider for verification emails
