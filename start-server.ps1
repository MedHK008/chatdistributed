# Script PowerShell pour démarrer le serveur de chat
Write-Host "=== Démarrage du Serveur de Chat Distribué ===" -ForegroundColor Green

# Vérifier si Maven est installé
try {
    mvn --version | Out-Null
    Write-Host "Maven détecté" -ForegroundColor Green
} catch {
    Write-Host "Erreur: Maven n'est pas installé ou pas dans le PATH" -ForegroundColor Red
    Write-Host "Veuillez installer Maven ou utiliser: java -cp target/classes fstm.distibutedsystem.ChatServer"
    exit 1
}

# Compiler le projet
Write-Host "Compilation du projet..." -ForegroundColor Yellow
mvn clean compile

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation réussie!" -ForegroundColor Green
    Write-Host "Démarrage du serveur..." -ForegroundColor Yellow
    Write-Host "Le serveur sera accessible sur localhost:1099" -ForegroundColor Cyan
    Write-Host "Appuyez sur Ctrl+C pour arrêter le serveur" -ForegroundColor Cyan
    Write-Host "----------------------------------------" -ForegroundColor White    # Démarrer le serveur
    mvn exec:java -Pserver
} else {
    Write-Host "Erreur de compilation!" -ForegroundColor Red
    exit 1
}
