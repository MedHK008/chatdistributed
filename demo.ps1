# Script de démonstration du système de chat distribué
Write-Host "=== Démonstration du Chat Distribué avec Lamport ===" -ForegroundColor Green

# Fonction pour afficher un message coloré
function Write-ColorMessage {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

Write-ColorMessage "Ce script va démontrer l'utilisation du système de chat distribué" "Cyan"
Write-ColorMessage "avec l'algorithme d'horodatage de Lamport." "Cyan"
Write-Host ""

# Vérifier Maven
Write-ColorMessage "Vérification de Maven..." "Yellow"
try {
    mvn --version | Out-Null
    Write-ColorMessage "✓ Maven détecté" "Green"
} catch {
    Write-ColorMessage "✗ Maven non trouvé" "Red"
    Write-ColorMessage "Veuillez installer Maven pour continuer" "Red"
    exit 1
}

# Compilation
Write-ColorMessage "Compilation du projet..." "Yellow"
mvn clean compile

if ($LASTEXITCODE -eq 0) {
    Write-ColorMessage "✓ Compilation réussie" "Green"
} else {
    Write-ColorMessage "✗ Erreur de compilation" "Red"
    exit 1
}

Write-Host ""
Write-ColorMessage "INSTRUCTIONS POUR LA DÉMONSTRATION :" "White"
Write-ColorMessage "====================================" "White"
Write-Host ""

Write-ColorMessage "1. DÉMARRAGE DU SERVEUR :" "Yellow"
Write-ColorMessage "   Dans un nouveau terminal PowerShell, exécutez :" "White"
Write-ColorMessage "   .\start-server.ps1" "Cyan"
Write-ColorMessage "   OU" "White"
Write-ColorMessage "   mvn exec:java -Dexec.mainClass=\"fstm.distibutedsystem.ChatServer\"" "Cyan"
Write-Host ""

Write-ColorMessage "2. DÉMARRAGE DES CLIENTS :" "Yellow"
Write-ColorMessage "   Dans d'autres terminaux PowerShell, exécutez :" "White"
Write-ColorMessage "   .\start-client.ps1" "Cyan"
Write-ColorMessage "   OU" "White"
Write-ColorMessage "   mvn exec:java -Dexec.mainClass=\"fstm.distibutedsystem.ChatClient\"" "Cyan"
Write-Host ""

Write-ColorMessage "3. TEST DE L'ALGORITHME DE LAMPORT :" "Yellow"
Write-ColorMessage "   - Connectez au moins 2 clients avec des noms différents" "White"
Write-ColorMessage "   - Envoyez des messages depuis différents clients" "White"
Write-ColorMessage "   - Utilisez /history pour voir l'ordre Lamport" "White"
Write-ColorMessage "   - Observez les timestamps logiques [Lamport: X]" "White"
Write-Host ""

Write-ColorMessage "4. COMMANDES DISPONIBLES :" "Yellow"
Write-ColorMessage "   /quit      - Quitter le chat" "White"
Write-ColorMessage "   /history   - Afficher l'historique (ordre Lamport)" "White"
Write-ColorMessage "   /clients   - Voir les clients connectés" "White"
Write-Host ""

Write-ColorMessage "5. POINTS À OBSERVER :" "Yellow"
Write-ColorMessage "   - Les messages s'affichent avec [timestamp] nom: message" "White"
Write-ColorMessage "   - L'historique respecte l'ordre causal de Lamport" "White"
Write-ColorMessage "   - Les événements de connexion/déconnexion sont horodatés" "White"
Write-Host ""

# Proposer de démarrer automatiquement
Write-ColorMessage "Voulez-vous démarrer automatiquement un serveur maintenant ? (O/N)" "Green"
$response = Read-Host

if ($response -eq "O" -or $response -eq "o" -or $response -eq "Y" -or $response -eq "y") {
    Write-ColorMessage "Démarrage du serveur..." "Yellow"
    Write-ColorMessage "Ouvrez d'autres terminaux pour les clients !" "Cyan"
    Write-Host ""
    
    # Démarrer le serveur
    mvn exec:java -Dexec.mainClass="fstm.distibutedsystem.ChatServer"
} else {
    Write-ColorMessage "Démarrez manuellement le serveur puis les clients." "Yellow"
    Write-ColorMessage "Bonne démonstration !" "Green"
}
