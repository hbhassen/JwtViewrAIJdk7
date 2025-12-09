# Guide de migration (legacy → moderne)

## 1. Contexte
- **Legacy** : Java 7, Spring Boot 1.5.22 (Spring 4), javax.\*, jjwt 0.9.1, H2 en mémoire, aucun profil, peu de tests.
- **Cible** : Java 17, Spring Boot 3.5.6 (Spring 6), jakarta.\*, jjwt 0.12.5, H2, profils `dev` / `test`, couverture minimale 70 %.

## 2. Étapes clés de migration
1. **Monter la JVM** : passer le build Maven en `java.version=17`, compiler en release 17.
2. **Parent Spring Boot** : remplacer le starter-parent 1.5 par 3.5.6.
3. **Jakarta** : migrer toutes les dépendances javax.\* vers jakarta.\* (JPA, Validation, Servlet).
4. **Dépendances** : remplacer `jjwt 0.9.1` par `jjwt-api/impl/jackson 0.12.5`; ajouter `spring-boot-starter-validation`.
5. **Code** :
   - Injection par constructeur uniquement, records pour les DTO.
   - Remplacer `Date` par `Instant`/API temps.
   - Ajouter services dédiés : chargeur de certificat, validateur JWT, décodage payload, sanitisation des tokens pour logs.
   - Séparer le domaine (`domain/entity`), DTO (`dto`), JWT (`jwt`), configuration (`config`), exceptions/handler (`exception`), services (`service`), contrôleur (`controller`).
6. **Configuration** :
   - Passer en `application.yml` avec profils `dev`/`test`.
   - Externaliser `jwt.certificate-path` et `jwt.max-token-length`.
7. **Tests** :
   - JUnit 5, MockMvc, AssertJ, Mockito.
   - Tests unitaires : validation JWT, chargement du certificat, décodage payload, service (valide/invalid/expiré).
   - Tests d’intégration : POST `/api/jwt/validate` (3 cas) et GET `/api/jwt/payload/{id}` (existant / manquant).
   - JaCoCo : seuil 70 % (règle sur LINE coverage) et rapports générés en phase `verify`.

## 3. Migration javax → jakarta
- Packages à remplacer : `javax.persistence` → `jakarta.persistence`, `javax.validation` → `jakarta.validation`, `javax.servlet` → `jakarta.servlet`.
- Vérifier les annotations JPA/Validation dans les entités et DTO.
- Adapter les importations des tests Spring (JUnit 5 natif, plus de runner JUnit 4).

## 4. Migration JWT
- Ancien : `Jwts.parser().setSigningKey(...)` (jjwt 0.9.1).
- Nouveau : `Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token)` (jjwt 0.12.5).
- Exceptions plus typées (`SecurityException`, `ExpiredJwtException`).
- Décodage manuel du payload Base64 URL pour conserver le comportement (même quand signature invalide).
- Interdiction de logger le JWT complet : utiliser un `TokenSanitizer` (préfixe + hash).

## 5. Java 17
- DTO en `record`.
- Usage possible de `var`, `switch expressions`, `text blocks`, Streams/Optional (sans excès).
- API temps (`Instant`) pour `createdAt`.

## 6. Sécurité
- Validation des entrées : @NotBlank, @Size, limite de longueur configurable.
- Gestion des erreurs centralisée (`@RestControllerAdvice`) : 400 (requête invalide), 404 (ID manquant), 500 (certificat ou erreur interne). Les réponses de l’API principale restent fonctionnellement identiques (renvoi d’un `valid` booléen).
- Pas de log du token brut.

## 7. Tests & couverture
- Plugins Maven : Surefire/Failsafe 3.2.x, JaCoCo 0.8.12 avec règle > 70 %.
- Commandes : `mvn verify` génère `target/site/jacoco/index.html`.

## 8. Bonnes pratiques post-migration
- Profil par défaut `dev`, `test` pour la CI.
- Externaliser le certificat en prod (`jwt.certificate-path`).
- Garder les DTO en records, préférer l’injection par constructeur, limiter les utilitaires statiques.
