# Chat Distribué avec Algorithme de Lamport

## Description

Ce projet implémente un système de chat distribué utilisant Java RMI avec l'algorithme d'horodatage logique de Lamport pour assurer la cohérence des messages.

## Fonctionnalités

### Serveur RMI
- Gère l'enregistrement et la désinscription des clients
- Reçoit et relaie les messages à tous les clients connectés
- Implémente l'algorithme de Lamport pour l'horodatage logique
- Maintient un historique ordonné des messages

### Client RMI
- Connexion au serveur de chat
- Envoi et réception de messages
- Affichage des messages avec timestamps Lamport
- Commandes intégrées (/quit, /history, /clients)
- **Interface graphique Swing** avec fenêtre de chat moderne

### Client GUI (Swing)
- Interface graphique intuitive avec fenêtres de chat
- Affichage en temps réel des messages de tous les utilisateurs
- Boîte de dialogue de connexion au démarrage
- Historique et liste des clients dans des fenêtres séparées
- Coloration différentielle des messages (vos messages vs autres)
- Affichage de l'horloge Lamport en temps réel

### Algorithme de Lamport
- Horodatage logique pour tous les événements
- Ordre cohérent des messages même en cas de latence
- Synchronisation automatique des horloges logiques

## Structure du Projet

```
src/main/java/fstm/distibutedsystem/
├── Main.java                    # Point d'entrée principal
├── ChatDemo.java               # Démonstration du système
├── ChatServer.java             # Implémentation du serveur
├── ChatClient.java             # Implémentation du client console
├── ChatClientGUI.java          # Implémentation du client GUI (Swing)
├── ChatServerInterface.java    # Interface RMI du serveur
├── ChatClientInterface.java    # Interface RMI du client
├── Message.java                # Classe des messages
└── LamportClock.java           # Implémentation de l'horloge Lamport
```

## Compilation et Exécution

### Prérequis
- Java 17 ou supérieur
- Maven

### Compilation
```bash
mvn clean compile
```

### Démarrage du Serveur
```bash
# Option 1: Via Maven
mvn exec:java -Dexec.mainClass="fstm.distibutedsystem.ChatServer"

# Option 2: Via Java direct
java -cp target/classes fstm.distibutedsystem.ChatServer

# Option 3: Via la classe démonstration
java -cp target/classes fstm.distibutedsystem.ChatDemo server
```

### Démarrage d'un Client Console
```bash
# Option 1: Via Maven
mvn exec:java -Dexec.mainClass="fstm.distibutedsystem.ChatClient"

# Option 2: Via Java direct
java -cp target/classes fstm.distibutedsystem.ChatClient

# Option 3: Via la classe démonstration
java -cp target/classes fstm.distibutedsystem.ChatDemo client
```

### Démarrage d'un Client GUI (Recommandé)
```bash
# Option 1: Via Maven avec profil
mvn exec:java -Pclient-gui

# Option 2: Via Java direct
java -cp target/classes fstm.distibutedsystem.ChatClientGUI

# Option 3: Via script PowerShell (Windows)
.\start-client-gui.ps1
```

## Utilisation

### Démarrage du Système
1. **Démarrer le serveur** en premier
2. **Démarrer un ou plusieurs clients** (GUI recommandé)
3. **Interface GUI** : Une boîte de dialogue de connexion s'ouvre automatiquement
4. **Interface Console** : Saisir le nom du client quand demandé
5. **Spécifier l'adresse et le port du serveur** (par défaut: localhost:1099)

### Interface Graphique (GUI)
L'interface Swing offre une expérience moderne :
- **Fenêtre de connexion** : Saisie du nom, serveur et port
- **Zone de chat principale** : Affichage en temps réel des messages
- **Barre de statut** : Statut de connexion et horloge Lamport
- **Champ de saisie** : Zone de texte pour taper les messages
- **Boutons d'action** : Historique et liste des clients connectés
- **Coloration des messages** : Distinction visuelle entre vos messages et ceux des autres

### Commandes Client Console
- Tapez votre message et appuyez sur Entrée pour l'envoyer
- `/quit` - Quitter le chat
- `/history` - Afficher l'historique des messages (ordre Lamport)
- `/clients` - Afficher la liste des clients connectés

### Exemple de Session

**Terminal 1 (Serveur):**
```
Serveur de chat démarré avec l'horloge Lamport initialisée à 0
Serveur de chat démarré et enregistré dans le registre RMI
En attente de connexions clients...
[Lamport: 1] Client connecté: Alice (ID: 12345...)
[Lamport: 2] Client connecté: Bob (ID: 67890...)
[Lamport: 3] Message diffusé de Alice: Bonjour tout le monde!
```

**Terminal 2 (Client Alice):**
```
Entrez votre nom: Alice
Connecté au serveur avec l'ID: 12345...
[Lamport: 2] Bob s'est connecté
Bonjour tout le monde!
>> [3] Alice: Bonjour tout le monde!
>> [4] Bob: Salut Alice!
```

**Terminal 3 (Client Bob):**
```
Entrez votre nom: Bob
Connecté au serveur avec l'ID: 67890...
>> [3] Alice: Bonjour tout le monde!
Salut Alice!
>> [4] Bob: Salut Alice!
```

## Algorithme de Lamport - Détails Techniques

### Principe
L'algorithme de Lamport assure un ordre causal cohérent des événements dans un système distribué:

1. **Événement local** : L = L + 1
2. **Envoi de message** : L = L + 1, envoyer (message, L)
3. **Réception de message** : L = max(L, timestamp_reçu) + 1

### Implémentation
- Chaque client et le serveur maintiennent une horloge logique
- Les messages sont ordonnés selon leur timestamp Lamport
- En cas d'égalité, l'ID de l'expéditeur sert de départage
- L'historique est automatiquement trié selon cet ordre

## Architecture Technique

### Composants RMI
- **Registry RMI** : Port 1099 par défaut
- **Interface distante** : `ChatServerInterface`
- **Callbacks** : `ChatClientInterface`
- **Sérialisation** : Classe `Message` implémente `Serializable`

### Gestion de la Concurrence
- `ConcurrentHashMap` pour les clients connectés
- `CopyOnWriteArrayList` pour l'historique des messages
- Synchronisation sur l'horloge Lamport
- Thread séparé pour l'affichage des messages côté client

### Gestion des Erreurs
- Détection automatique des clients déconnectés
- Nettoyage automatique des références invalides
- Messages d'erreur informatifs
- Graceful shutdown

## Tests et Validation

### Scénarios de Test
1. **Connexion/Déconnexion** de multiples clients
2. **Envoi simultané** de messages par différents clients
3. **Vérification de l'ordre Lamport** dans l'historique
4. **Gestion des déconnexions** inattendues
5. **Persistance de l'historique** pendant la session

### Validation de l'Algorithme de Lamport
- Messages affichés dans l'ordre causal correct
- Timestamps croissants selon l'algorithme
- Cohérence entre tous les clients connectés

### Améliorations Possibles

### Fonctionnalités Avancées
- Sauvegarde persistante de l'historique
- Salles de chat multiples
- Messages privés entre clients
- ✅ **Interface graphique (Swing)** - Implémentée
- Authentification des utilisateurs
- Émojis et formatting des messages
- Notifications système
- Transfert de fichiers

### Optimisations Techniques
- Compression des messages
- Limitation de la taille de l'historique
- Configuration via fichier de propriétés
- Logging avancé avec framework (Log4j)
- Métriques de performance
- Thèmes personnalisables pour l'interface GUI

## Licence
Projet éducatif - FSTM Distributed Systems
