package fstm.distibutedsystem;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface RMI pour les callbacks des clients
 * Permet au serveur de notifier les clients
 */
public interface ChatClientInterface extends Remote {
    
    /**
     * Méthode appelée par le serveur pour envoyer un message au client
     * @param message Le message reçu
     * @param serverTimestamp Le timestamp Lamport du serveur
     * @throws RemoteException
     */
    void receiveMessage(Message message, int serverTimestamp) throws RemoteException;
    
    /**
     * Méthode appelée par le serveur pour notifier qu'un client s'est connecté
     * @param clientName Le nom du client qui s'est connecté
     * @throws RemoteException
     */
    void clientConnected(String clientName) throws RemoteException;
    
    /**
     * Méthode appelée par le serveur pour notifier qu'un client s'est déconnecté
     * @param clientName Le nom du client qui s'est déconnecté
     * @throws RemoteException
     */
    void clientDisconnected(String clientName) throws RemoteException;
    
    /**
     * Récupère le nom du client
     * @return Le nom du client
     * @throws RemoteException
     */
    String getClientName() throws RemoteException;
    
    /**
     * Récupère l'ID du client
     * @return L'ID unique du client
     * @throws RemoteException
     */
    String getClientId() throws RemoteException;
}
