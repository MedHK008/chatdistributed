# Script PowerShell pour démarrer un client de chat
Write-Host "=== Démarrage du Client de Chat Distribué ===" -ForegroundColor Green

# Vérifier si Maven est installé
try {
    mvn --version | Out-Null
    Write-Host "Maven détecté" -ForegroundColor Green
} catch {
    Write-Host "Erreur: Maven n'est pas installé ou pas dans le PATH" -ForegroundColor Red
    Write-Host "Veuillez installer Maven ou utiliser: java -cp target/classes fstm.distibutedsystem.ChatClient"
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

Write-Host "Démarrage du client..." -ForegroundColor Yellow
Write-Host "Assurez-vous que le serveur est démarré avant de continuer" -ForegroundColor Cyan
Write-Host "----------------------------------------" -ForegroundColor White

# Démarrer le client
mvn exec:java -Dexec.mainClass="fstm.distibutedsystem.ChatClient"
