# JWT Viewer (Java 7 / Spring Boot 1.5.22)

Application didactique REST pour valider des JWT sign?s RS256 ? l?aide d?un certificat X.509, stocker le r?sultat en H2 en m?moire et restituer le payload JSON.

## Pr?requis
- Java 7 (JDK 7) install? dans le PATH
- Maven 3

## D?marrage
```bash
mvn spring-boot:run
```
L?application d?marre sur `http://localhost:8080` et expose la console H2 sur `http://localhost:8080/h2-console` (JDBC URL : `jdbc:h2:mem:jwtapp`).

## Fonctionnement de la validation
1. Le contr?leur re?oit le JWT (`POST /api/jwt/validate`).
2. `CertificateUtils` charge le certificat `classpath:certs/public.crt` et en extrait la cl? publique.
3. `JwtUtils.validateToken` tente de v?rifier la signature RS256 et l?expiration via jjwt 0.9.1. Toute exception rend la validation invalide (`valid=false`).
4. Le payload est d?cod? manuellement en Base64 URL-safe, m?me si la signature est invalide.
5. Le token, son payload d?cod?, le statut de validation et la date sont sauvegard?s en H2.

## Mod?le de donn?es
`JwtTokenEntity` contient : id (auto), originalToken (TEXT), payloadJson (TEXT), isValid (BOOLEAN), createdAt (TIMESTAMP).

## APIs
- `POST /api/jwt/validate`
  - Body JSON : `{ "token": "JWT_STRING" }`
  - R?ponse : `{ "tokenId": 1, "valid": true|false }` (stockage effectu? m?me si invalide).
- `GET /api/jwt/payload/{id}`
  - R?ponse : payload JSON du token stock? (ou payload brut + avertissement si le JSON est illisible).

## Commandes curl d?exemple
```bash
# Validation et stockage
token="VOTRE_JWT_ICI"
curl -X POST http://localhost:8080/api/jwt/validate \
  -H "Content-Type: application/json" \
  -d "{\"token\":\"${token}\"}"

# Lecture du payload (remplacer 1 par l?ID retourn?)
curl http://localhost:8080/api/jwt/payload/1
```

## Remarques
- Le certificat fourni (`src/main/resources/certs/public.crt`) est un exemple ; remplacez-le par votre certificat public r?el pour v?rifier vos JWT.
- Aucun usage de Java 8 : pas de lambdas, Optional ou Streams.
- Spring Security n?est pas inclus ; seules les API REST JSON sont expos?es.
