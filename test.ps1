# Script PowerShell pour tester rapidement le système
param(
    [string]$Component = "help"
)

function Show-Help {
    Write-Host "=== Test du Chat Distribué ===" -ForegroundColor Green
    Write-Host ""
    Write-Host "Usage: .\test.ps1 [composant]" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Composants disponibles:" -ForegroundColor White
    Write-Host "  server    - Démarrer le serveur de chat" -ForegroundColor Cyan
    Write-Host "  client    - Démarrer un client de chat" -ForegroundColor Cyan
    Write-Host "  lamport   - Tester l'algorithme de Lamport" -ForegroundColor Cyan
    Write-Host "  compile   - Compiler le projet" -ForegroundColor Cyan
    Write-Host "  demo      - Démonstration guidée" -ForegroundColor Cyan
    Write-Host "  help      - Afficher cette aide" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Exemples:" -ForegroundColor Yellow
    Write-Host "  .\test.ps1 compile   # Compiler le projet" -ForegroundColor White
    Write-Host "  .\test.ps1 lamport   # Tester l'algorithme de Lamport" -ForegroundColor White
    Write-Host "  .\test.ps1 server    # Démarrer le serveur" -ForegroundColor White
    Write-Host "  .\test.ps1 client    # Démarrer un client" -ForegroundColor White
}

function Test-Compilation {
    Write-Host "=== Compilation du Projet ===" -ForegroundColor Green
    mvn clean compile test-compile
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Compilation réussie!" -ForegroundColor Green
        return $true
    } else {
        Write-Host "✗ Erreur de compilation!" -ForegroundColor Red
        return $false
    }
}

function Test-Lamport {
    Write-Host "=== Test de l'Algorithme de Lamport ===" -ForegroundColor Green
    
    if (-not (Test-Path "target/classes")) {
        Write-Host "Compilation nécessaire..." -ForegroundColor Yellow
        if (-not (Test-Compilation)) {
            return
        }
    }
    
    java -cp "target/classes;target/test-classes" fstm.distibutedsystem.LamportClockTest
}

function Start-Server {
    Write-Host "=== Démarrage du Serveur ===" -ForegroundColor Green
    
    if (-not (Test-Path "target/classes")) {
        Write-Host "Compilation nécessaire..." -ForegroundColor Yellow
        if (-not (Test-Compilation)) {
            return
        }
    }
    
    Write-Host "Serveur démarré sur localhost:1099" -ForegroundColor Cyan
    Write-Host "Appuyez sur Ctrl+C pour arrêter" -ForegroundColor Yellow
    java -cp target/classes fstm.distibutedsystem.ChatServer
}

function Start-Client {
    Write-Host "=== Démarrage du Client ===" -ForegroundColor Green
    
    if (-not (Test-Path "target/classes")) {
        Write-Host "Compilation nécessaire..." -ForegroundColor Yellow
        if (-not (Test-Compilation)) {
            return
        }
    }
    
    java -cp target/classes fstm.distibutedsystem.ChatClient
}

function Start-Demo {
    Write-Host "=== Démonstration Guidée ===" -ForegroundColor Green
    .\demo.ps1
}

# Exécution basée sur le paramètre
switch ($Component.ToLower()) {
    "compile" {
        Test-Compilation
    }
    "lamport" {
        Test-Lamport
    }
    "server" {
        Start-Server
    }
    "client" {
        Start-Client
    }
    "demo" {
        Start-Demo
    }
    default {
        Show-Help
    }
}
