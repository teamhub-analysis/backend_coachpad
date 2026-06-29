# CoachPad

Application Spring Boot pour la gestion d'équipes de football (coaching, joueurs, formations, projets tactiques).

## Stack

- **Java 17** — Spring Boot 3.5.7, Spring Security, Spring Data JPA
- **Base de données** — PostgreSQL
- **Build** — Maven
- **Mapping** — MapStruct 1.6.0
- **Authentification** — JWT

## Architecture

Le projet suit une architecture hexagonale (clean architecture) avec 3 couches :

```
com.coachpad
├── domain/                       # Cœur métier
│   ├── model/                    # Modèles métier (TeamModel, PlayerModel, ...)
│   ├── repository/               # Interfaces des repositories
│   └── usecase/                  # Interfaces des cas d'utilisation
├── infrastructure/               # Adaptateurs techniques
│   ├── config/                   # Configuration Spring (Security, Web, ...)
│   ├── persistance/postgresql/   # Implémentation JPA (entities, repositories, mappers)
│   ├── security/                 # Auth JWT (JwtService, JwtAuthenticationFilter, AuthService)
│   └── service/                  # Implémentations des services
│       ├── impl/                 # Implémentations des interfaces usecase
│       ├── project/              # Gestion des projets tactiques
│       ├── dataimport/           # Import Excel/CSV
│       └── storage/              # Stockage de fichiers
└── presentation/                 # Interface REST
    └── rest/
        ├── controller/           # Endpoints REST
        ├── dto/                  # Objets de transfert
        └── mapper/               # Mappers DTO <-> Modèles
```

## Prérequis

- Java 17+
- PostgreSQL
- Maven 3.8+

## Configuration

Variables dans `src/main/resources/application.properties` :

| Propriété | Valeur |
|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/coach-padd` |
| `spring.datasource.username` | `postgres` |
| `spring.datasource.password` | `sahar123` |
| `server.port` | `8080` |
| `jwt.secret-key` | clé secrète JWT (256 bits) |
| `jwt.expiration` | 86400000 ms (24h) |

## Installation

```bash
# Cloner le projet
git clone https://github.com/teamhub-analysis/backend_coachpad.git

# Créer la base de données PostgreSQL
createdb coach-padd

# Lancer l'application
mvn spring-boot:run
```

## API

### Authentification

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/signup` | Inscription |
| POST | `/api/auth/login` | Connexion (retourne un JWT) |

### Équipes

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/teams` | Liste toutes les équipes |
| GET | `/api/teams/{id}` | Détail d'une équipe |
| POST | `/api/teams` | Créer une équipe |
| PUT | `/api/teams/{id}` | Modifier une équipe |
| DELETE | `/api/teams/{id}` | Supprimer une équipe |
| POST | `/api/teams/{id}/import/excel` | Importer des joueurs via Excel |
| POST | `/api/teams/{id}/import/csv` | Importer des joueurs via CSV |

### Joueurs

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/players` | Liste tous les joueurs |
| GET | `/api/players/{id}` | Détail d'un joueur |
| POST | `/api/players` | Créer un joueur |
| PUT | `/api/players/{id}` | Modifier un joueur |
| DELETE | `/api/players/{id}` | Supprimer un joueur |

### Formations

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/formations` | Liste toutes les formations |
| GET | `/api/formations/{id}` | Détail d'une formation |
| POST | `/api/formations` | Créer une formation |
| PUT | `/api/formations/{id}` | Modifier une formation |
| DELETE | `/api/formations/{id}` | Supprimer une formation |

### Projets tactiques

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/projects` | Liste les projets de l'utilisateur |
| GET | `/api/projects/{id}` | Détail d'un projet |
| POST | `/api/projects` | Créer un projet |
| PUT | `/api/projects/{id}` | Modifier un projet |
| DELETE | `/api/projects/{id}` | Supprimer un projet |
| GET | `/api/projects/{id}/content` | Contenu tactique du projet |
| PUT | `/api/projects/{id}/content` | Synchroniser le contenu tactique |

### Planning

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/planning/upcoming` | Événements à venir |
| POST | `/api/planning` | Créer un événément |
| PUT | `/api/planning/{id}` | Modifier un événément |
| DELETE | `/api/planning/{id}` | Supprimer un événément |

## Base de données

Le `ddl-auto=update` de Hibernate crée/met à jour les tables automatiquement au démarrage.

Un `DatabaseSeeder` insère des données initiales (équipes, utilisateurs) si la base est vide.
