package fstm.distibutedsystem;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Client de chat distribué avec support de l'algorithme de Lamport
 */
public class ChatClient extends UnicastRemoteObject implements ChatClientInterface {
    private static final long serialVersionUID = 1L;
    
    private final String clientName;
    private String clientId;
    private ChatServerInterface server;
    private final LamportClock lamportClock;
    private final BlockingQueue<Message> messageQueue;
    private final Scanner scanner;
    private boolean isRunning;
    
    public ChatClient(String clientName) throws RemoteException {
        super();
        this.clientName = clientName;
        this.lamportClock = new LamportClock();
        this.messageQueue = new LinkedBlockingQueue<>();
        this.scanner = new Scanner(System.in);
        this.isRunning = false;
        
        System.out.println("Client '" + clientName + "' créé avec horloge Lamport initialisée à 0");
    }
    
    /**
     * Se connecte au serveur de chat
     */
    public void connectToServer(String serverHost, int serverPort) throws Exception {
        try {
            // Localiser le registre RMI
            Registry registry = LocateRegistry.getRegistry(serverHost, serverPort);
            
            // Obtenir la référence du serveur
            server = (ChatServerInterface) registry.lookup("ChatServer");
            
            // S'enregistrer auprès du serveur
            clientId = server.registerClient(this, clientName);
            
            System.out.println("Connecté au serveur avec l'ID: " + clientId);
            System.out.println("Tapez vos messages et appuyez sur Entrée pour les envoyer");
            System.out.println("Tapez '/quit' pour quitter, '/history' pour voir l'historique, '/clients' pour voir les clients connectés");
            System.out.println("----------------------------------------");
            
            // Démarrer le thread d'affichage des messages
            startMessageDisplayThread();
            
            isRunning = true;
            
        } catch (java.rmi.RemoteException e) {
            System.err.println("Erreur RMI du serveur: " + e.getMessage());
        }
    }
    
    /**
     * Démarre la boucle principale du client
     */
    public void start() {
        while (isRunning) {
            try {
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    continue;
                }
                
                if (input.equals("/quit")) {
                    disconnect();
                    break;
                } else if (input.equals("/history")) {
                    displayHistory();
                } else if (input.equals("/clients")) {
                    displayConnectedClients();
                } else if (input.startsWith("/")) {
                    System.out.println("Commande inconnue. Commandes disponibles: /quit, /history, /clients");
                } else {
                    sendMessage(input);
                }
                
            } catch (Exception e) {
                System.err.println("Erreur lors de la saisie: " + e.getMessage());
            }
        }
    }
    
    /**
     * Envoie un message au serveur
     */
    private void sendMessage(String content) {
        try {
            // Incrémenter l'horloge Lamport pour l'événement d'envoi
            int timestamp = lamportClock.tick();
            
            // Créer le message
            Message message = new Message(content, clientName, timestamp, clientId);
            
            // Envoyer le message au serveur
            server.broadcastMessage(message, timestamp);
            
        } catch (RemoteException e) {
            System.err.println("Erreur lors de l'envoi du message: " + e.getMessage());
        }
    }
    
    /**
     * Affiche l'historique des messages
     */
    private void displayHistory() {
        try {
            List<Message> history = server.getMessageHistory();
            
            System.out.println("\n--- Historique des messages (ordre Lamport) ---");
            if (history.isEmpty()) {
                System.out.println("Aucun message dans l'historique.");
            } else {
                for (Message msg : history) {
                    System.out.println(msg.toDetailedString());
                }
            }
            System.out.println("--- Fin de l'historique ---\n");
            
        } catch (RemoteException e) {
            System.err.println("Erreur lors de la récupération de l'historique: " + e.getMessage());
        }
    }
    
    /**
     * Affiche la liste des clients connectés
     */
    private void displayConnectedClients() {
        try {
            List<String> clients = server.getConnectedClients();
            
            System.out.println("\n--- Clients connectés ---");
            if (clients.isEmpty()) {
                System.out.println("Aucun client connecté.");
            } else {
                for (String client : clients) {
                    System.out.println("- " + client);
                }
            }
            System.out.println("--- Fin de la liste ---\n");
            
        } catch (RemoteException e) {
            System.err.println("Erreur lors de la récupération des clients: " + e.getMessage());
        }
    }
    
    /**
     * Se déconnecte du serveur
     */
    private void disconnect() {
        try {
            if (server != null && clientId != null) {
                server.unregisterClient(clientId);
                System.out.println("Déconnecté du serveur.");
            }
            isRunning = false;
        } catch (RemoteException e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }
    
    /**
     * Démarre le thread d'affichage des messages
     */
    private void startMessageDisplayThread() {
        Thread displayThread = new Thread(() -> {
            while (isRunning || !messageQueue.isEmpty()) {
                try {
                    Message message = messageQueue.take();
                    System.out.println(">> " + message.toString());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        displayThread.setDaemon(true);
        displayThread.start();
    }
    
    // Implémentation des méthodes de l'interface ChatClientInterface
    
    @Override
    public void receiveMessage(Message message, int serverTimestamp) throws RemoteException {
        // Mettre à jour l'horloge Lamport avec le timestamp du serveur
        lamportClock.update(serverTimestamp);
        
        // Ajouter le message à la queue d'affichage
        messageQueue.offer(message);
    }
    
    @Override
    public void clientConnected(String clientName) throws RemoteException {
        // Incrémenter l'horloge pour l'événement de notification
        int timestamp = lamportClock.tick();
        
        System.out.println(String.format("[Lamport: %d] %s s'est connecté", timestamp, clientName));
    }
    
    @Override
    public void clientDisconnected(String clientName) throws RemoteException {
        // Incrémenter l'horloge pour l'événement de notification
        int timestamp = lamportClock.tick();
        
        System.out.println(String.format("[Lamport: %d] %s s'est déconnecté", timestamp, clientName));
    }
    
    @Override
    public String getClientName() throws RemoteException {
        return clientName;
    }
    
    @Override
    public String getClientId() throws RemoteException {
        return clientId;
    }
    
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Entrez votre nom: ");
            String clientName = scanner.nextLine().trim();
            
            if (clientName.isEmpty()) {
                System.out.println("Le nom ne peut pas être vide.");
                return;
            }
            
            System.out.print("Entrez l'adresse du serveur (par défaut localhost): ");
            String serverHost = scanner.nextLine().trim();
            if (serverHost.isEmpty()) {
                serverHost = "localhost";
            }
            
            System.out.print("Entrez le port du serveur (par défaut 1099): ");
            String portStr = scanner.nextLine().trim();
            int serverPort = 1099;
            if (!portStr.isEmpty()) {
                try {
                    serverPort = Integer.parseInt(portStr);
                } catch (NumberFormatException e) {
                    System.out.println("Port invalide, utilisation du port par défaut 1099");
                }
            }
            
            // Créer et connecter le client
            ChatClient client = new ChatClient(clientName);
            client.connectToServer(serverHost, serverPort);
            
            // Démarrer la boucle principale
            client.start();
            
        } catch (Exception e) {
            System.err.println("Erreur du client: " + e.getMessage());
        } finally {
            System.exit(0);
        }
    }
}
