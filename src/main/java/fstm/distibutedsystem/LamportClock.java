package fstm.distibutedsystem;

/**
 * Implémentation de l'algorithme d'horodatage logique de Lamport
 * Gère les timestamps logiques pour assurer la cohérence des événements
 */
public class LamportClock {
    private int timestamp;
    
    public LamportClock() {
        this.timestamp = 0;
    }
    
    public LamportClock(int initialValue) {
        this.timestamp = initialValue;
    }
    
    /**
     * Incrémente l'horloge logique (événement local)
     * @return Le nouveau timestamp
     */
    public synchronized int tick() {
        return ++timestamp;
    }
    
    /**
     * Met à jour l'horloge logique lors de la réception d'un message
     * Selon l'algorithme de Lamport: LC = max(LC, timestamp_reçu) + 1
     * @param receivedTimestamp Le timestamp du message reçu
     * @return Le nouveau timestamp local
     */
    public synchronized int update(int receivedTimestamp) {
        timestamp = Math.max(timestamp, receivedTimestamp) + 1;
        return timestamp;
    }
    
    /**
     * Récupère le timestamp actuel sans l'incrémenter
     * @return Le timestamp actuel
     */
    public synchronized int getCurrentTimestamp() {
        return timestamp;
    }
    
    /**
     * Définit explicitement la valeur du timestamp
     * @param timestamp La nouvelle valeur
     */
    public synchronized void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "LamportClock{timestamp=" + timestamp + "}";
    }
}
