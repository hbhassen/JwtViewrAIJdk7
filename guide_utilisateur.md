# Guide utilisateur

## Prérequis
- Java 17 (JDK) dans le PATH.
- Maven 3.8+.

## Lancement de l’application
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
L’application écoute sur `http://localhost:8080`.

### Console H2
- URL : `http://localhost:8080/h2-console`
- JDBC : `jdbc:h2:mem:jwtapp`
- User : `sa` / mot de passe vide.

## Configuration
- `jwt.certificate-path` : chemin du certificat X.509 public (défaut `classpath:certs/public.crt`).
- `jwt.max-token-length` : longueur max du token (défaut 4096).
- Profils :
  - `dev` : H2 en mémoire, console H2 active.
  - `test` : H2 en mémoire, création/rollback automatique.

## APIs
1) `POST /api/jwt/validate`
   - Corps JSON : `{"token": "<JWT_HERE>"}`
   - Réponse : `{"tokenId": <id>, "valid": true|false}`
   - Le token est stocké même s’il est invalide (payload décodé, statut de validation).

2) `GET /api/jwt/payload/{id}`
   - Réponse : payload JSON du token stocké.
   - Si le payload n’est pas un JSON valide, renvoie le payload brut avec un avertissement.

## Exemples curl
```bash
# Validation et stockage
token="VOTRE_JWT_ICI"
curl -X POST http://localhost:8080/api/jwt/validate \
  -H "Content-Type: application/json" \
  -d "{\"token\":\"${token}\"}"

# Lecture du payload (remplacer 1 par l’ID retourné)
curl http://localhost:8080/api/jwt/payload/1
```

## Codes de retour
- 200 : succès (validation ou lecture).
- 400 : requête invalide (token manquant ou trop long).
- 404 : ID inexistant sur la lecture du payload.
- 500 : erreur interne (ex. certificat indisponible).

## Tests
```bash
mvn verify
```
- Exécute tests unitaires + intégration.
- Rapport de couverture : `target/site/jacoco/index.html` (seuil 70 %).

## Limitations H2
- Base en mémoire : données perdues à l’arrêt.
- Pour un stockage persistant, basculer `spring.datasource.url` vers un SGBD durable.
