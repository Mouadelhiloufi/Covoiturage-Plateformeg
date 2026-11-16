# Covoiturage

Application de covoiturage développée avec **Spring Boot** et **Java 17**.

Le projet constitue actuellement le socle de l’application : configuration Spring Boot, accès aux données avec JPA, validation des données et exposition de services Web MVC.

## Technologies utilisées

- Java 17
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- PostgreSQL
- Maven
- Lombok
- MapStruct
- JUnit 5

## Prérequis

Avant de lancer le projet, assurez-vous d’avoir installé :

- Java 17 ou une version compatible ;
- PostgreSQL si la connexion à une base de données est nécessaire ;
- Git, pour récupérer le projet.

Le projet inclut le **Maven Wrapper** : une installation globale de Maven n’est donc pas obligatoire.

## Installation

Clonez le dépôt puis placez-vous dans son répertoire :

```bash
git clone <url-du-depot>
cd Covoiturage
```

## Configuration

La configuration principale se trouve dans :

```text
src/main/resources/application.yaml
```

La configuration actuelle définit le nom de l’application :

```yaml
spring:
  application:
    name: Covoiturage
```

Si une base PostgreSQL est utilisée, ajoutez les propriétés de connexion adaptées à votre environnement dans `application.yaml` ou dans un fichier de configuration local non versionné.

Exemple :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/covoiturage
    username: votre_utilisateur
    password: votre_mot_de_passe
  jpa:
    hibernate:
      ddl-auto: update
```

## Lancement de l’application

Sous Windows :

```powershell
.\mvnw.cmd spring-boot:run
```

Sous Linux ou macOS :

```bash
./mvnw spring-boot:run
```

L’application démarre sur le port `8080` par défaut.

## Compilation et tests

Pour compiler le projet :

```powershell
.\mvnw.cmd clean package
```

Pour exécuter les tests :

```powershell
.\mvnw.cmd test
```

Le test actuel vérifie que le contexte Spring Boot se charge correctement.

## Structure du projet

```text
Covoiturage/
├── src/
│   ├── main/
│   │   ├── java/com/youcode/covoiturage/
│   │   │   └── CovoiturageApplication.java
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
│       └── java/com/youcode/covoiturage/
│           └── CovoiturageApplicationTests.java
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## Point d’entrée

La classe principale de l’application est :

```text
com.youcode.covoiturage.CovoiturageApplication
```

## État du projet

Les fonctionnalités métier du covoiturage, telles que la gestion des utilisateurs, des trajets, des réservations et des paiements, restent à implémenter.

## Licence

Aucune licence n’est actuellement définie pour ce projet.
