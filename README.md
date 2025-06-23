# Chat Distribué avec Algorithme de Lamport

<div align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-007396?logo=java" alt="Java 17+">
  <img src="https://img.shields.io/badge/Maven-3.6%2B-C71A36?logo=apache-maven" alt="Maven 3.6+">
  <img src="https://img.shields.io/badge/Docker-Ready-blue?logo=docker" alt="Docker Ready">
  <img src="https://img.shields.io/badge/Status-Stable-brightgreen.svg" alt="Status: Stable">
</div>

## 📋 Description

Ce projet implémente un système de chat distribué utilisant Java RMI (Remote Method Invocation) avec l'algorithme d'horodatage logique de Lamport. Il permet une communication en temps réel entre plusieurs utilisateurs avec une cohérence garantie des messages, même dans un environnement distribué.

## ✨ Fonctionnalités Principales

### 🖥️ Serveur RMI
- Gestion centralisée des connexions clients
- Diffusion des messages à tous les clients connectés
- Implémentation de l'algorithme de Lamport pour la synchronisation
- Gestion des déconnexions propres
- Journalisation des événements

### 🎨 Client GUI (Swing)
- Interface graphique moderne et intuitive
- Fenêtre de chat avec historique des messages
- Liste des utilisateurs connectés
- Indicateur d'état de connexion
- Thème clair/sombre (selon le système)

### ⏱️ Algorithme de Lamport
- Horodatage logique des événements
- Ordonnancement causal des messages
- Synchronisation distribuée
- Cohérence garantie entre tous les nœuds

## 🚀 Démarrage Rapide

### Prérequis
- Java 17 ou supérieur ([Télécharger](https://adoptium.net/))
- Maven 3.6+ ([Guide d'installation](https://maven.apache.org/install.html))
- Docker

### Installation

1. **Cloner le dépôt**
   ```bash
   git clone https://github.com/MedHK008/chatdistributed.git
   cd chat-distributed
   ```

3. **Exécuter le serveur**
   ```bash
      docker-compose up -d
   ```

4. **Lancer un client**
   ```bash
   .\start-client-gui.ps1
   ```

### Variables d'environnement

| Variable | Description | Valeur par défaut |
|----------|-------------|-------------------|
| `SERVER_PORT` | Port du registre RMI | `1099` |
| `SERVER_NAME` | Nom du service RMI | `localhost` |

### Flux de Données

1. **Connexion Client**
   - Le client se connecte au registre RMI
   - Le serveur valide les informations de connexion
   - Une horloge Lamport est initialisée pour le client

2. **Envoi de Message**
   - Le client incrémente son horloge locale
   - Le message est horodaté et envoyé au serveur
   - Le serveur synchronise les horloges et diffuse le message

3. **Réception de Message**
   - Le serveur reçoit un message avec un timestamp
   - L'horloge du serveur est mise à jour
   - Le message est placé dans une file d'attente ordonnée
   - Les messages sont délivrés dans l'ordre logique correct

## 📚 Documentation Technique

### Structure du Projet

```
src/main/java/fstm/distibutedsystem/
├── Main.java                    # Point d'entrée principal
├── ChatServer.java             # Implémentation du serveur
├── ChatClientGUI.java          # Implémentation du client GUI (Swing)
├── ChatServerInterface.java    # Interface RMI du serveur
├── ChatClientInterface.java    # Interface RMI du client
├── Message.java                # Classe des messages
└── LamportClock.java           # Implémentation de l'horloge Lamport
```

### Algorithme de Lamport

#### Principe de Base
L'algorithme de Lamport permet d'ordonner les événements dans un système distribué en utilisant des horodatages logiques. Chaque événement se voit attribuer un numéro unique qui reflète son ordre causal.

#### Implémentation

```java
public class LamportClock {
    private int counter;
    
    public synchronized int tick() {
        return ++counter;
    }
    
    public synchronized void update(int receivedTime) {
        counter = Math.max(counter, receivedTime) + 1;
    }
}
```

#### Règles d'Horodatage
1. **Événement local** : `time = max(time, time + 1)`
2. **Envoi de message** : `time = tick(); send(message, time)`
3. **Réception de message** : `update(receivedTime); time++`





## Utilisation

### Démarrage du Système
1. **Démarrer le serveur** en premier
2. **Démarrer un ou plusieurs clients**
3. **Interface GUI** : Une boîte de dialogue de connexion s'ouvre automatiquement

### Interface Graphique (GUI)
L'interface Swing offre une expérience moderne :
- **Fenêtre de connexion** : Saisie du nom
- **Zone de chat principale** : Affichage en temps réel des messages
- **Barre de statut** : Statut de connexion et horloge Lamport
- **Champ de saisie** : Zone de texte pour taper les messages
- **Boutons d'action** : Historique et liste des clients connectés
- **Coloration des messages** : Distinction visuelle entre vos messages et ceux des autres

## Licence
Projet éducatif - FSTM/ILISI/Arch distribuée et Cloud Computing
---

<div align="center">
  Fait avec ❤️ par l'équipe du projet Chat Distribué
</div>