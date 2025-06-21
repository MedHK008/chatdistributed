package fstm.distibutedsystem;

/**
 * Classe principale pour démontrer le système de chat distribué
 */
public class ChatDemo {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            showUsage();
            return;
        }
        
        String mode = args[0].toLowerCase();
        
        switch (mode) {
            case "server":
                startServer();
                break;
            case "client":
                startClient();
                break;
            default:
                showUsage();
                break;
        }
    }
    
    private static void showUsage() {
        System.out.println("Usage:");
        System.out.println("  java fstm.distibutedsystem.ChatDemo server");
        System.out.println("  java fstm.distibutedsystem.ChatDemo client");
        System.out.println();
        System.out.println("Ou bien lancez directement:");
        System.out.println("  java fstm.distibutedsystem.ChatServer");
        System.out.println("  java fstm.distibutedsystem.ChatClient");
    }
    
    private static void startServer() {
        System.out.println("=== Démarrage du serveur de chat ===");
        ChatServer.main(new String[]{});
    }
    
    private static void startClient() {
        System.out.println("=== Démarrage du client de chat ===");
        ChatClient.main(new String[]{});
    }
}
