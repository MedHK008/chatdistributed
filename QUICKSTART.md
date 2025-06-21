# Guide de Démarrage Rapide

## Chat Distribué avec Algorithme de Lamport

### 🚀 Démarrage Express

1. **Compilation :**
   ```bash
   mvn clean compile
   ```

2. **Démarrer le serveur :**
   ```bash
   java -cp target/classes fstm.distibutedsystem.ChatServer
   ```

3. **Démarrer des clients (dans de nouveaux terminaux) :**
   ```bash
   java -cp target/classes fstm.distibutedsystem.ChatClient
   ```

### 🛠️ Scripts PowerShell (Windows)

```powershell
# Compilation et test
.\test-simple.ps1 compile
.\test-simple.ps1 lamport

# Démarrage
.\test-simple.ps1 server   # Terminal 1
.\test-simple.ps1 client   # Terminal 2
.\test-simple.ps1 client   # Terminal 3
```

### 📝 Test Rapide

1. Démarrez le serveur
2. Connectez 2-3 clients avec des noms différents
3. Envoyez des messages depuis différents clients
4. Utilisez `/history` pour voir l'ordre Lamport
5. Observez les timestamps `[Lamport: X]`

### 🎯 Commandes Client

- `/quit` - Quitter
- `/history` - Historique avec ordre Lamport  
- `/clients` - Liste des clients connectés

### 🔬 Validation de Lamport

Les messages doivent respecter l'ordre causal :
- Timestamps croissants selon l'algorithme
- Cohérence entre tous les clients
- Affichage : `[timestamp] nom: message`
