package fstm.distibutedsystem;

/**
 * Implémentation de l'algorithme d'horodatage logique de Lamport.
 * Gère les timestamps logiques pour assurer la cohérence des événements distribués.
 */
public class LamportClock {
    private int timestamp; // Horodatage logique courant

    /**
     * Constructeur par défaut initialisant l'horloge à 0
     */
    public LamportClock() {
        this.timestamp = 0;
    }

    /**
     * Constructeur avec une valeur initiale
     * @param initialValue Valeur initiale du timestamp
     */
    public LamportClock(int initialValue) {
        this.timestamp = initialValue;
    }

    /**
     * Incrémente l'horloge logique pour un événement local
     * @return Le nouveau timestamp
     */
    public synchronized int tick() {
        return ++timestamp;
    }

    /**
     * Met à jour l'horloge logique lors de la réception d'un message.
     * Utilise la règle de Lamport : LC = max(LC, timestamp_reçu) + 1
     * @param receivedTimestamp Le timestamp du message reçu
     * @return Le nouveau timestamp local
     */
    public synchronized int update(int receivedTimestamp) {
        timestamp = Math.max(timestamp, receivedTimestamp) + 1;
        return timestamp;
    }

    /**
     * Récupère le timestamp actuel sans modification
     * @return Le timestamp courant
     */
    public synchronized int getCurrentTimestamp() {
        return timestamp;
    }

    /**
     * Définit explicitement la valeur du timestamp
     * @param timestamp La nouvelle valeur du timestamp
     */
    public synchronized void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Représentation textuelle de l'horloge
     * @return Chaîne contenant le timestamp courant
     */
    @Override
    public String toString() {
        return "LamportClock{timestamp=" + timestamp + "}";
    }
}