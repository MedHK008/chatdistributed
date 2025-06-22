package fstm.distibutedsystem;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Tests unitaires pour le serveur de chat, incluant les tests de limite de messages
 */
public class ChatServerTest {
    
    public static void main(String[] args) {
        System.out.println("=== Tests du Serveur de Chat ===");
          try {
            // Test de la limite des messages
            testMessageLimit();
            
            // Test de l'ordre des messages selon Lamport
            testMessageOrdering();
            
            System.out.println("=== Tous les tests réussis! ===");
        } catch (RemoteException e) {
            System.err.println("Erreur RMI lors des tests: " + e.getMessage());
        }
    }
    
    private static void testMessageLimit() throws RemoteException {
        System.out.println("\nTest: Limite des messages (50 messages maximum)");
        
        // Créer un serveur de test
        ChatServer server = new ChatServer(0); // Port 0 pour test local
        
        // Créer un client mock pour les tests
        MockChatClient mockClient = new MockChatClient("TestClient");
        
        // Enregistrer le client
        String clientId = server.registerClient(mockClient, "TestClient");
          // Vérifier l'historique initial vide
        List<Message> history = server.getMessageHistory();
        assert history.isEmpty() : "L'historique devrait être vide au début";
        
        // Ajouter 45 messages (sous la limite)
        System.out.println("Ajout de 45 messages...");
        for (int i = 1; i <= 45; i++) {
            Message msg = new Message("Message " + i, "TestClient", i, clientId);
            server.broadcastMessage(msg, i);
        }
        
        history = server.getMessageHistory();
        assert history.size() == 45 : "L'historique devrait contenir 45 messages, mais contient: " + history.size();
        
        // Ajouter 10 messages supplémentaires (pour dépasser la limite)
        System.out.println("Ajout de 10 messages supplémentaires pour dépasser la limite...");
        for (int i = 46; i <= 55; i++) {
            Message msg = new Message("Message " + i, "TestClient", i, clientId);
            server.broadcastMessage(msg, i);
            System.out.println("Message  count in the server: " + server.getMessageCount());
            if(server.getMessageCount() > 50) {
                System.out.println("Message " + i + " ajouté, mais l'historique dépasse la limite de 50 messages.");
            }
        }
        
        history = server.getMessageHistory();
        assert history.size() == 50 : "L'historique devrait être limité à 50 messages, mais contient: " + history.size();
        
        // Vérifier que les anciens messages ont été supprimés
        // Le premier message devrait maintenant être "Message 6" (messages 1-5 supprimés)
        Message firstMessage = history.get(0);
        assert firstMessage.getContent().equals("Message 6") : 
            "Le premier message devrait être 'Message 6', mais c'est: " + firstMessage.getContent();
        
        Message lastMessage = history.get(history.size() - 1);
        assert lastMessage.getContent().equals("Message 55") : 
            "Le dernier message devrait être 'Message 55', mais c'est: " + lastMessage.getContent();
        
        System.out.println("✓ Test de limite des messages réussi");
        System.out.println("  - Historique maintenu à 50 messages maximum");
        System.out.println("  - Anciens messages correctement supprimés");
        
        // Test supplémentaire: ajouter un message de plus
        Message extraMsg = new Message("Message 56", "TestClient", 56, clientId);
        server.broadcastMessage(extraMsg, 56);
        
        history = server.getMessageHistory();
        assert history.size() == 50 : "L'historique devrait toujours être limité à 50 messages";
        
        firstMessage = history.get(0);
        assert firstMessage.getContent().equals("Message 7") : 
            "Après ajout d'un message supplémentaire, le premier devrait être 'Message 7'";
        
        lastMessage = history.get(history.size() - 1);
        assert lastMessage.getContent().equals("Message 56") : 
            "Le dernier message devrait être 'Message 56'";
        
        System.out.println("✓ Test de rotation des messages réussi");
    }
    
    private static void testMessageOrdering() throws RemoteException {
        System.out.println("\nTest: Ordre des messages selon l'algorithme de Lamport");
        
        ChatServer server = new ChatServer(0);
        MockChatClient mockClient = new MockChatClient("TestClient");
        String clientId = server.registerClient(mockClient, "TestClient");
        
        // Ajouter des messages avec des timestamps dans le désordre
        Message msg1 = new Message("Message tardif", "TestClient", 10, clientId);
        Message msg2 = new Message("Message précoce", "TestClient", 5, clientId);
        Message msg3 = new Message("Message moyen", "TestClient", 7, clientId);
        
        server.broadcastMessage(msg1, 10);
        server.broadcastMessage(msg2, 5);
        server.broadcastMessage(msg3, 7);
        
        List<Message> history = server.getMessageHistory();
        
        // Vérifier que les messages sont triés par timestamp Lamport
        assert history.size() == 3 : "L'historique devrait contenir 3 messages";
          // Les messages devraient être triés par timestamp (après mise à jour par le serveur)
        for (int i = 0; i < history.size() - 1; i++) {
            int currentTimestamp = history.get(i).getLamportTimestamp();
            int nextTimestamp = history.get(i + 1).getLamportTimestamp();
            assert currentTimestamp <= nextTimestamp : 
                "Les messages devraient être triés par timestamp: " + currentTimestamp + " > " + nextTimestamp;
        }
        
        System.out.println("✓ Test d'ordre des messages réussi");
        System.out.println("  - Messages triés correctement selon l'algorithme de Lamport");
    }
      /**
     * Classe mock pour simuler un client de chat dans les tests
     */
    private static class MockChatClient implements ChatClientInterface {
        private final String clientName;
        private final String clientId;
        
        public MockChatClient(String clientName) {
            this.clientName = clientName;
            this.clientId = "mock-" + clientName;
        }
        
        @Override
        public void receiveMessage(Message message, int serverTimestamp) throws RemoteException {
            // Mock implementation - ne fait rien pour les tests
        }
        
        @Override
        public void clientConnected(String clientName) throws RemoteException {
            // Mock implementation - ne fait rien pour les tests
        }
        
        @Override
        public void clientDisconnected(String clientName) throws RemoteException {
            // Mock implementation - ne fait rien pour les tests
        }
        
        @Override
        public String getClientName() throws RemoteException {
            return this.clientName;
        }
        
        @Override
        public String getClientId() throws RemoteException {
            return this.clientId;
        }
    }
}
