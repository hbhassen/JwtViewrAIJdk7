# JWT Viewer modernisé (Java 17 / Spring Boot 3.5.6)

Application REST pédagogique qui valide des JWT RS256 à l’aide d’un certificat X.509, stocke le résultat en H2 et expose la lecture du payload.

## Démarrer
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
H2 console : `http://localhost:8080/h2-console` (JDBC `jdbc:h2:mem:jwtapp`).

## APIs
- `POST /api/jwt/validate` — body `{ "token": "JWT_HERE" }`, répond `{ "tokenId": <id>, "valid": true|false }`.
- `GET /api/jwt/payload/{id}` — renvoie le payload JSON stocké (ou le payload brut si non parsable).

## Config
- Certificat configuré via `jwt.certificate-path` (par défaut `classpath:certs/public.crt`).
- Taille max token configurable `jwt.max-token-length` (4096).

## Tests & couverture
```bash
mvn verify
```
Rapport JaCoCo : `target/site/jacoco/index.html` (seuil 70 %).***
