# Covoiturage

API REST de covoiturage développée avec **Java 17** et **Spring Boot 3.4.3**.
L’application permet de gérer les conducteurs, passagers, véhicules, trajets,
points d’arrêt et réservations.

## Technologies

- Java 17
- Spring Boot 3.4.3
- Spring Web
- Spring Data JPA / Hibernate
- Bean Validation
- PostgreSQL 16
- Maven Wrapper
- Lombok et MapStruct
- JUnit 5 / Mockito

## Prérequis

- JDK 17 ou version supérieure compatible ;
- Docker et Docker Compose (recommandé pour PostgreSQL) ;
- Git.

Une installation globale de Maven n’est pas nécessaire : le projet fournit
le Maven Wrapper.

## Installation

```bash
git clone <url-du-depot>
cd Covoiturage
```

### Démarrer PostgreSQL avec Docker

Le fichier `docker-compose.yml` démarre une instance PostgreSQL 16 avec les
paramètres utilisés par l’application :

```bash
docker compose up -d
```

| Paramètre | Valeur |
|---|---|
| Base de données | `covoiturage_db` |
| Utilisateur | `postgres` |
| Mot de passe | `password` |
| Port hôte | `5435` |

Pour arrêter le conteneur :

```bash
docker compose down
```

## Configuration

La configuration se trouve dans
`src/main/resources/application.yaml`. Par défaut, l’API utilise :

```yaml
server:
  port: 8087
spring:
  datasource:
    url: jdbc:postgresql://localhost:5435/covoiturage_db
    username: postgres
    password: password
```

Adaptez ces valeurs à votre environnement. Hibernate utilise actuellement
`ddl-auto: update` et crée ou met à jour automatiquement les tables.

## Lancer l’application

Sous Windows :

```powershell
.\mvnw.cmd spring-boot:run
```

Sous Linux ou macOS :

```bash
./mvnw spring-boot:run
```

L’API est disponible à l’adresse <http://localhost:8087>.

## API disponible

Toutes les routes sont préfixées par `/api`.

| Ressource | Opérations principales |
|---|---|
| `/conducteurs` | créer, consulter, modifier, lister et rechercher (`?q=`) |
| `/passagers` | créer, consulter, modifier, lister et rechercher (`?q=`) |
| `/vehicules` | créer, consulter par conducteur, modifier et supprimer |
| `/trajets` | publier, consulter, rechercher, terminer et annuler |
| `/reservations` | réserver, annuler et lister par passager |

Les listes paginées acceptent les paramètres Spring Data `page`, `size` et
`sort`. La recherche de trajets accepte `villeDepart`, `villeArrivee`,
`date`, ainsi que les paramètres optionnels `prixMax` et `nbPlaces`.

## Tester avec Postman

Les fichiers suivants sont fournis à la racine du projet :

- `Covoiturage.postman_collection.json` : collection des requêtes REST ;
- `Covoiturage.postman_environment.json` : environnement local avec
  `baseUrl=http://localhost:8087`.

Importez les deux fichiers dans Postman après avoir démarré PostgreSQL et
l’application.

## Compilation et tests

Compiler et empaqueter le projet :

```powershell
.\mvnw.cmd clean package
```

Exécuter les tests :

```powershell
.\mvnw.cmd test
```

Les tests couvrent notamment le chargement du contexte Spring et les règles
des services de trajets et de réservations.

## Structure du projet

```text
Covoiturage/
├── src/main/java/com/youcode/covoiturage/
│   ├── controller/       # Contrôleurs REST
│   ├── dto/              # Objets d’entrée et de sortie
│   ├── entity/           # Entités JPA
│   ├── mapper/           # Mappers MapStruct
│   ├── repository/       # Repositories Spring Data
│   ├── service/          # Services métier
│   └── exception/        # Exceptions et gestion globale
├── src/test/             # Tests unitaires et d’intégration
├── docker-compose.yml
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## État du projet

Le socle REST et les principales fonctionnalités métier sont implémentés.
L’authentification, l’autorisation et le paiement ne sont pas encore
intégrés.

## Licence

Aucune licence n’est actuellement définie pour ce projet.
