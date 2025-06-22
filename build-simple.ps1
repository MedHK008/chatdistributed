Write-Host "Building chat client executable..." -ForegroundColor Green

# Check Maven
mvn --version
if ($LASTEXITCODE -ne 0) {
    Write-Host "Maven not found!" -ForegroundColor Red
    exit 1
}

# Clean and compile
Write-Host "Compiling project..." -ForegroundColor Yellow
mvn clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit 1
}

# Package
Write-Host "Creating JARs..." -ForegroundColor Yellow
mvn package

if ($LASTEXITCODE -eq 0) {
    Write-Host "SUCCESS: JARs created in target/" -ForegroundColor Green
    if (Test-Path "target/chat-client-gui.jar") {
        Write-Host "Client JAR: target/chat-client-gui.jar" -ForegroundColor Cyan
    }
    if (Test-Path "target/chat-server.jar") {
        Write-Host "Server JAR: target/chat-server.jar" -ForegroundColor Cyan
    }
} else {
    Write-Host "Package failed!" -ForegroundColor Red
    exit 1
}
