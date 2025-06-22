package fstm.distibutedsystem;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implémentation du serveur de chat distribué avec support de l'algorithme de Lamport
 */
public class ChatServer extends UnicastRemoteObject implements ChatServerInterface {
    private static final long serialVersionUID = 1L;
    
    private final Map<String, ChatClientInterface> clients;
    private final Map<String, String> clientNames; // clientId -> clientName
    private final List<Message> messageHistory;
    private final LamportClock lamportClock;
    private final Object messageLock = new Object();
    
    public ChatServer() throws RemoteException {
        super();
        this.clients = new ConcurrentHashMap<>();
        this.clientNames = new ConcurrentHashMap<>();
        this.messageHistory = new CopyOnWriteArrayList<>();
        this.lamportClock = new LamportClock();
        
        System.out.println("Serveur de chat démarré avec l'horloge Lamport initialisée à 0");
    }
    
    @Override
    public String registerClient(ChatClientInterface clientCallback, String clientName) throws RemoteException {
        String clientId = UUID.randomUUID().toString();
        
        clients.put(clientId, clientCallback);
        clientNames.put(clientId, clientName);
        
        // Incrémenter l'horloge Lamport pour l'événement de connexion
        int timestamp = lamportClock.tick();
        
        System.out.println(String.format("[Lamport: %d] Client connecté: %s (ID: %s)", 
                                        timestamp, clientName, clientId));
        
        // Notifier tous les autres clients
        notifyClientConnection(clientName, clientId);
        
        return clientId;
    }
    
    @Override
    public void unregisterClient(String clientId) throws RemoteException {
        String clientName = clientNames.get(clientId);
        if (clientName != null) {
            clients.remove(clientId);
            clientNames.remove(clientId);
            
            // Incrémenter l'horloge Lamport pour l'événement de déconnexion
            int timestamp = lamportClock.tick();
            
            System.out.println(String.format("[Lamport: %d] Client déconnecté: %s (ID: %s)", 
                                            timestamp, clientName, clientId));
            
            // Notifier tous les autres clients
            notifyClientDisconnection(clientName, clientId);
        }
    }
    
    @Override
    public void broadcastMessage(Message message, int senderTimestamp) throws RemoteException {
        synchronized (messageLock) {
            // Mettre à jour l'horloge Lamport du serveur avec le timestamp de l'expéditeur
            int serverTimestamp = lamportClock.update(senderTimestamp);
            
            // Créer un nouveau message avec le timestamp du serveur
            Message timestampedMessage = new Message(
                message.getContent(),
                message.getSenderName(),
                serverTimestamp,
                message.getSenderId()
            );
            
            // Ajouter le message à l'historique
            messageHistory.add(timestampedMessage);
            
            // Trier l'historique selon l'ordre de Lamport
            messageHistory.sort(Message::compareTo);
            
            System.out.println(String.format("[Lamport: %d] Message diffusé de %s: %s", 
                                            serverTimestamp, message.getSenderName(), message.getContent()));
            
            // Diffuser le message à tous les clients connectés
            List<String> disconnectedClients = new ArrayList<>();
            
            for (Map.Entry<String, ChatClientInterface> entry : clients.entrySet()) {
                try {
                    entry.getValue().receiveMessage(timestampedMessage, serverTimestamp);
                } catch (RemoteException e) {
                    System.err.println("Erreur lors de l'envoi du message au client " + entry.getKey() + ": " + e.getMessage());
                    disconnectedClients.add(entry.getKey());
                }
            }
            
            // Supprimer les clients déconnectés
            for (String clientId : disconnectedClients) {
                unregisterClient(clientId);
            }
        }
    }
    
    @Override
    public List<Message> getMessageHistory() throws RemoteException {
        synchronized (messageLock) {
            return new ArrayList<>(messageHistory);
        }
    }
    
    @Override
    public List<String> getConnectedClients() throws RemoteException {
        return new ArrayList<>(clientNames.values());
    }
    
    private void notifyClientConnection(String clientName, String excludeClientId) {
        List<String> disconnectedClients = new ArrayList<>();
        
        for (Map.Entry<String, ChatClientInterface> entry : clients.entrySet()) {
            if (!entry.getKey().equals(excludeClientId)) {
                try {
                    entry.getValue().clientConnected(clientName);
                } catch (RemoteException e) {
                    System.err.println("Erreur lors de la notification de connexion au client " + entry.getKey() + ": " + e.getMessage());
                    disconnectedClients.add(entry.getKey());
                }
            }
        }
        
        // Supprimer les clients déconnectés
        for (String clientId : disconnectedClients) {
            try {
                unregisterClient(clientId);
            } catch (RemoteException e) {
                System.err.println("Erreur lors de la désinscription du client déconnecté: " + e.getMessage());
            }
        }
    }
    
    private void notifyClientDisconnection(String clientName, String excludeClientId) {
        List<String> disconnectedClients = new ArrayList<>();
        
        for (Map.Entry<String, ChatClientInterface> entry : clients.entrySet()) {
            if (!entry.getKey().equals(excludeClientId)) {
                try {
                    entry.getValue().clientDisconnected(clientName);
                } catch (RemoteException e) {
                    System.err.println("Erreur lors de la notification de déconnexion au client " + entry.getKey() + ": " + e.getMessage());
                    disconnectedClients.add(entry.getKey());
                }
            }
        }
        
        // Supprimer les clients déconnectés
        for (String clientId : disconnectedClients) {
            try {
                unregisterClient(clientId);
            } catch (RemoteException e) {
                System.err.println("Erreur lors de la désinscription du client déconnecté: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            int port = 1099;
            String portEnv = System.getenv("SERVER_PORT");
            if (portEnv != null && !portEnv.isEmpty()) {
                try {
                    port = Integer.parseInt(portEnv);
                } catch (NumberFormatException e) {
                    System.out.println("Port invalide dans SERVER_PORT, utilisation du port par défaut 1099");
                }
            }
            String serverName = System.getenv().getOrDefault("SERVER_NAME", "ChatServer");
            Registry registry = LocateRegistry.createRegistry(port);
            ChatServer server = new ChatServer();
            registry.rebind(serverName, server);
            System.out.println("Serveur de chat démarré et enregistré dans le registre RMI sous le nom : " + serverName + " sur le port : " + port);
            System.out.println("En attente de connexions clients...");
            System.out.println("Appuyez sur Ctrl+C pour arrêter le serveur");
            Thread.currentThread().join();
        } catch (java.rmi.RemoteException e) {
            System.err.println("Erreur RMI du serveur: " + e.getMessage());
        } catch (java.lang.InterruptedException e) {
            System.err.println("Le serveur a été interrompu: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
