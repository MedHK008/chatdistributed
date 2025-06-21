package fstm.distibutedsystem;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface RMI pour les opérations du serveur de chat
 */
public interface ChatServerInterface extends Remote {
    
    /**
     * Enregistre un nouveau client auprès du serveur
     * @param clientCallback L'interface callback du client
     * @param clientName Le nom du client
     * @return L'ID unique attribué au client
     * @throws RemoteException
     */
    String registerClient(ChatClientInterface clientCallback, String clientName) throws RemoteException;
    
    /**
     * Désenregistre un client du serveur
     * @param clientId L'ID du client à désenregistrer
     * @throws RemoteException
     */
    void unregisterClient(String clientId) throws RemoteException;
    
    /**
     * Envoie un message à tous les clients connectés
     * @param message Le message à diffuser
     * @param senderTimestamp Le timestamp Lamport de l'expéditeur
     * @throws RemoteException
     */
    void broadcastMessage(Message message, int senderTimestamp) throws RemoteException;
    
    /**
     * Récupère l'historique des messages
     * @return La liste ordonnée des messages selon Lamport
     * @throws RemoteException
     */
    List<Message> getMessageHistory() throws RemoteException;
    
    /**
     * Récupère la liste des clients connectés
     * @return La liste des noms des clients connectés
     * @throws RemoteException
     */
    List<String> getConnectedClients() throws RemoteException;
}
