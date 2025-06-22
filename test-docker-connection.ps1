#!/usr/bin/env pwsh

# Script pour tester la connexion Docker du serveur de chat

Write-Host "=== Test de connexion Docker pour le serveur de chat ===" -ForegroundColor Cyan

# Fonction pour vérifier si un port est ouvert
function Test-Port {
    param($hostname, $port)
    try {
        $tcpClient = New-Object System.Net.Sockets.TcpClient
        $tcpClient.Connect($hostname, $port)
        $tcpClient.Close()
        return $true
    } catch {
        return $false
    }
}

# 1. Vérifier que Docker est en cours d'exécution
Write-Host "`n1. Vérification de Docker..." -ForegroundColor Yellow
try {
    docker --version | Out-Null
    Write-Host "✓ Docker est installé et accessible" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker n'est pas installé ou accessible" -ForegroundColor Red
    exit 1
}

# 2. Construire l'image Docker
Write-Host "`n2. Construction de l'image Docker..." -ForegroundColor Yellow
docker-compose build chat-server
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Image construite avec succès" -ForegroundColor Green
} else {
    Write-Host "✗ Erreur lors de la construction de l'image" -ForegroundColor Red
    exit 1
}

# 3. Démarrer le conteneur
Write-Host "`n3. Démarrage du conteneur..." -ForegroundColor Yellow
docker-compose up -d chat-server
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Conteneur démarré" -ForegroundColor Green
} else {
    Write-Host "✗ Erreur lors du démarrage du conteneur" -ForegroundColor Red
    exit 1
}

# 4. Attendre que le serveur soit prêt
Write-Host "`n4. Attente du démarrage du serveur (30 secondes)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# 5. Vérifier les logs du conteneur
Write-Host "`n5. Logs du serveur:" -ForegroundColor Yellow
docker-compose logs chat-server

# 6. Tester la connectivité du port
Write-Host "`n6. Test de connectivité du port 1099..." -ForegroundColor Yellow
if (Test-Port "localhost" 1099) {
    Write-Host "✓ Port 1099 accessible" -ForegroundColor Green
} else {
    Write-Host "✗ Port 1099 non accessible" -ForegroundColor Red
}

# 7. Vérifier l'état du conteneur
Write-Host "`n7. État du conteneur:" -ForegroundColor Yellow
docker-compose ps

# 8. Instructions pour le client
Write-Host "`n8. Instructions pour tester avec le client GUI:" -ForegroundColor Cyan
Write-Host "   - Exécutez: .\start-client-gui.ps1" -ForegroundColor White
Write-Host "   - Dans la boîte de dialogue de connexion:" -ForegroundColor White
Write-Host "     * Serveur: localhost" -ForegroundColor White
Write-Host "     * Port: 1099" -ForegroundColor White

Write-Host "`n=== Test terminé ===" -ForegroundColor Cyan
Write-Host "Pour arrêter le serveur: docker-compose down" -ForegroundColor Yellow
