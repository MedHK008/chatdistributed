# Script PowerShell pour démarrer le client GUI de chat
Write-Host "=== Démarrage du Client GUI de Chat Distribué ===" -ForegroundColor Green

# Vérifier si Maven est installé
try {
    mvn --version | Out-Null
    Write-Host "Maven détecté" -ForegroundColor Green
} catch {
    Write-Host "Erreur: Maven n'est pas installé ou pas dans le PATH" -ForegroundColor Red
    Write-Host "Veuillez installer Maven ou utiliser: java -cp target/classes fstm.distibutedsystem.ChatClientGUI"
    exit 1
}

# Vérifier si le projet est compilé
if (-not (Test-Path "target/classes")) {
    Write-Host "Le projet n'est pas encore compilé. Compilation en cours..." -ForegroundColor Yellow
    mvn clean compile
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Erreur de compilation!" -ForegroundColor Red
        exit 1
    }
}

Write-Host "Démarrage du client GUI..." -ForegroundColor Yellow
Write-Host "Assurez-vous que le serveur est démarré avant de continuer" -ForegroundColor Cyan
Write-Host "Une fenêtre de connexion va s'ouvrir" -ForegroundColor Cyan
Write-Host "----------------------------------------" -ForegroundColor White

# Démarrer le client GUI
mvn exec:java -Pclient-gui
