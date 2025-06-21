package fstm.distibutedsystem;

/**
 * Point d'entrée principal du système de chat distribué
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Système de Chat Distribué avec Algorithme de Lamport ===");
        System.out.println();
        System.out.println("Ce projet implémente un chat multi-utilisateurs distribué");
        System.out.println("utilisant Java RMI et l'algorithme d'horodatage de Lamport.");
        System.out.println();
        System.out.println("Pour démarrer le système :");
        System.out.println();
        System.out.println("1. Serveur :");
        System.out.println("   java fstm.distibutedsystem.ChatServer");
        System.out.println("   ou");
        System.out.println("   mvn exec:java -Dexec.mainClass=\"fstm.distibutedsystem.ChatServer\"");
        System.out.println();
        System.out.println("2. Client(s) :");
        System.out.println("   java fstm.distibutedsystem.ChatClient");
        System.out.println("   ou");
        System.out.println("   mvn exec:java -Dexec.mainClass=\"fstm.distibutedsystem.ChatClient\"");
        System.out.println();
        System.out.println("3. Scripts PowerShell (Windows) :");
        System.out.println("   .\\start-server.ps1    # Démarrer le serveur");
        System.out.println("   .\\start-client.ps1    # Démarrer un client");
        System.out.println();
        System.out.println("Fonctionnalités :");
        System.out.println("- Chat en temps réel entre multiples clients");
        System.out.println("- Horodatage logique avec l'algorithme de Lamport");
        System.out.println("- Historique cohérent des messages");
        System.out.println("- Commandes : /quit, /history, /clients");
        System.out.println();
        System.out.println("Consultez le README.md pour plus de détails.");
        System.out.println("========================================================");
    }
}