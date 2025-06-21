# Script PowerShell pour tester le système de chat
param([string]$Component = "help")

function Show-Help {
    Write-Host "=== Test du Chat Distribue ===" -ForegroundColor Green
    Write-Host "Usage: .\test.ps1 [composant]" -ForegroundColor Yellow
    Write-Host "Composants: server, client, lamport, compile, help" -ForegroundColor White
}

function Test-Compilation {
    Write-Host "=== Compilation ===" -ForegroundColor Green
    mvn clean compile test-compile
    return $LASTEXITCODE -eq 0
}

function Test-Lamport {
    Write-Host "=== Test Algorithme de Lamport ===" -ForegroundColor Green
    if (-not (Test-Path "target/classes")) {
        Test-Compilation
    }
    java -cp "target/classes;target/test-classes" fstm.distibutedsystem.LamportClockTest
}

function Start-Server {
    Write-Host "=== Serveur ===" -ForegroundColor Green
    if (-not (Test-Path "target/classes")) {
        Test-Compilation
    }
    java -cp target/classes fstm.distibutedsystem.ChatServer
}

function Start-Client {
    Write-Host "=== Client ===" -ForegroundColor Green
    if (-not (Test-Path "target/classes")) {
        Test-Compilation
    }
    java -cp target/classes fstm.distibutedsystem.ChatClient
}

switch ($Component.ToLower()) {
    "compile" { Test-Compilation }
    "lamport" { Test-Lamport }
    "server" { Start-Server }
    "client" { Start-Client }
    default { Show-Help }
}
