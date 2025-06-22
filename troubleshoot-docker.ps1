#!/usr/bin/env pwsh

# Script de dépannage pour le serveur de chat Docker

Write-Host "=== Dépannage de la connexion Docker RMI ===" -ForegroundColor Cyan

Write-Host "`n1. Solution recommandée pour Windows:" -ForegroundColor Green
Write-Host "   Utilisez le fichier docker-compose-windows.yml au lieu de docker-compose.yml"
Write-Host "   Commande: docker-compose -f docker-compose-windows.yml up -d"

Write-Host "`n2. Vérification de l'environnement:" -ForegroundColor Yellow

# Vérifier Docker Desktop
Write-Host "   - Version Docker:"
docker --version

# Vérifier si c'est Windows
if ($IsWindows -or $env:OS -eq "Windows_NT") {
    Write-Host "   - Système: Windows détecté" -ForegroundColor Green
    Write-Host "   - Recommandation: Utilisez docker-compose-windows.yml" -ForegroundColor Cyan
} else {
    Write-Host "   - Système: Non-Windows détecté" -ForegroundColor Yellow
    Write-Host "   - Recommandation: Utilisez docker-compose.yml avec network_mode: host" -ForegroundColor Cyan
}

Write-Host "`n3. Instructions de test étape par étape:" -ForegroundColor Yellow

Write-Host "`nÉtape 1 - Nettoyer l'environnement:"
Write-Host "docker-compose down" -ForegroundColor White
Write-Host "docker-compose -f docker-compose-windows.yml down" -ForegroundColor White

Write-Host "`nÉtape 2 - Construire l'image:"
Write-Host "docker-compose -f docker-compose-windows.yml build" -ForegroundColor White

Write-Host "`nÉtape 3 - Démarrer le serveur:"
Write-Host "docker-compose -f docker-compose-windows.yml up -d" -ForegroundColor White

Write-Host "`nÉtape 4 - Vérifier les logs:"
Write-Host "docker-compose -f docker-compose-windows.yml logs -f chat-server" -ForegroundColor White

Write-Host "`nÉtape 5 - Tester la connexion du client:"
Write-Host ".\start-client-gui.ps1" -ForegroundColor White
Write-Host "Dans la GUI, utilisez:" -ForegroundColor White
Write-Host "  - Serveur: localhost" -ForegroundColor White
Write-Host "  - Port: 1099" -ForegroundColor White

Write-Host "`n4. Diagnostic des problèmes courants:" -ForegroundColor Yellow

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

Write-Host "`n   Vérification du port 1099..."
if (Test-Port "localhost" 1099) {
    Write-Host "   ✓ Port 1099 accessible sur localhost" -ForegroundColor Green
} else {
    Write-Host "   ✗ Port 1099 non accessible sur localhost" -ForegroundColor Red
    Write-Host "     Solutions possibles:" -ForegroundColor Yellow
    Write-Host "     - Vérifiez que le conteneur est démarré" -ForegroundColor White
    Write-Host "     - Utilisez docker-compose-windows.yml" -ForegroundColor White
    Write-Host "     - Vérifiez les logs du conteneur" -ForegroundColor White
}

Write-Host "`n5. Commandes utiles pour le débogage:" -ForegroundColor Yellow
Write-Host "   # Voir tous les conteneurs"
Write-Host "   docker ps -a" -ForegroundColor White
Write-Host "`n   # Voir les logs en temps réel"
Write-Host "   docker-compose -f docker-compose-windows.yml logs -f chat-server" -ForegroundColor White
Write-Host "`n   # Se connecter au conteneur pour diagnostics"
Write-Host "   docker exec -it chat-distributed-server sh" -ForegroundColor White
Write-Host "`n   # Tester la connectivité réseau depuis le conteneur"
Write-Host "   docker exec -it chat-distributed-server netstat -an | grep 1099" -ForegroundColor White

Write-Host "`n6. Si le problème persiste:" -ForegroundColor Red
Write-Host "   - Essayez de démarrer le serveur directement (sans Docker):"
Write-Host "     .\start-server.ps1" -ForegroundColor White
Write-Host "   - Vérifiez votre firewall Windows"
Write-Host "   - Vérifiez les paramètres de Docker Desktop"

Write-Host "`n=== Fin du diagnostic ===" -ForegroundColor Cyan
