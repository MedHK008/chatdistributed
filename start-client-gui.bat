@echo off
echo === Démarrage du Client GUI de Chat Distribué ===

:: Vérifier si Maven est installé
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Erreur: Maven n'est pas installé ou pas dans le PATH
    echo Veuillez installer Maven ou utiliser: java -cp target/classes fstm.distibutedsystem.ChatClientGUI
    pause
    exit /b 1
)

echo Maven détecté

:: Vérifier si le projet est compilé
if not exist "target\classes" (
    echo Le projet n'est pas encore compilé. Compilation en cours...
    mvn clean compile
    
    if %errorlevel% neq 0 (
        echo Erreur de compilation!
        pause
        exit /b 1
    )
)

echo Démarrage du client GUI...
echo Assurez-vous que le serveur est démarré avant de continuer
echo Une fenêtre de connexion va s'ouvrir
echo ----------------------------------------

:: Démarrer le client GUI
mvn exec:java -Pclient-gui

pause
