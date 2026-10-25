# trixit-api

REST API tier of Trixit (Java 21, Spring Boot 4, Maven, WAR). See [CLAUDE.md](CLAUDE.md) for the architecture and decisions.

## Layout

```
com.trixit
  domain                      pure domain types, no framework
  application                 use cases + repository/port interfaces
  infrastructure
    persistence               port adapters (JDBC/JPA, Flyway migrations in resources/db/migration)
    web                       controllers
    config                    Spring wiring and security
```

Dependencies point inward only: web/persistence -> application -> domain.

## Build and test

Requires a JDK 21 and Maven.

```
mvn test          # unit + slice tests
mvn package       # produces target/api.war
```

## Run locally

```
mvn spring-boot:run
curl http://localhost:8080/api/health
curl -i http://localhost:8080/api/auth/csrf
```

## Deploy

1. `mvn package`
2. Copy `target/api.war` to the `webapps/` directory of a standalone Tomcat 11 (Servlet 6.1).
3. The API is then served at `/api` on the same origin as the UI (`/ui/`).

Data lives in in-memory H2 and is lost on restart/redeploy.
