package fstm.distibutedsystem;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface RMI défin она les opérations disponibles sur le serveur de chat.
 */
public interface ChatServerInterface extends Remote {

    /**
     * Enregistre un nouveau client auprès du serveur
     * @param clientCallback Interface de rappel du client
     * @param clientName Nom du client
     * @return ID unique attribué au client
     * @throws RemoteException en cas d'erreur RMI
     */
    String registerClient(ChatClientInterface clientCallback, String clientName) throws RemoteException;

    /**
     * Désenregistre un client du serveur
     * @param clientId ID du client à désenregistrer
     * @throws RemoteException en cas d'erreur RMI
     */
    void unregisterClient(String clientId) throws RemoteException;

    /**
     * Diffuse un message à tous les clients connectés
     * @param message Message à diffuser
     * @param senderTimestamp Horodatage Lamport de l'expéditeur
     * @throws RemoteException en cas d'erreur RMI
     */
    void broadcastMessage(Message message, int senderTimestamp) throws RemoteException;

    /**
     * Récupère l'historique des messages
     * @return Liste ordonnée des messages selon l'algorithme de Lamport
     * @throws RemoteException en cas d'erreur RMI
     */
    List<Message> getMessageHistory() throws RemoteException;

    /**
     * Récupère la liste des clients connectés
     * @return Liste des noms des clients connectés
     * @throws RemoteException en cas d'erreur RMI
     */
    List<String> getConnectedClients() throws RemoteException;
}