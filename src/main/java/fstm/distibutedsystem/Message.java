package fstm.distibutedsystem;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe représentant un message dans le système de chat distribué
 * Implémente Serializable pour permettre la transmission via RMI
 */
public class Message implements Serializable, Comparable<Message> {
    private static final long serialVersionUID = 1L;
      private final String content;
    private final String senderName;
    private final int lamportTimestamp;
    private final LocalDateTime physicalTimestamp;
    private final String senderId;
    
    public Message(String content, String senderName, int lamportTimestamp, String senderId) {
        this.content = content;
        this.senderName = senderName;
        this.lamportTimestamp = lamportTimestamp;
        this.senderId = senderId;
        this.physicalTimestamp = LocalDateTime.now();
    }
    
    // Getters
    public String getContent() {
        return content;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public int getLamportTimestamp() {
        return lamportTimestamp;
    }
    
    public LocalDateTime getPhysicalTimestamp() {
        return physicalTimestamp;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    /**
     * Comparaison basée sur l'algorithme de Lamport
     * Si les timestamps sont égaux, on utilise l'ID de l'expéditeur pour garantir un ordre total
     */
    @Override
    public int compareTo(Message other) {
        if (this.lamportTimestamp != other.lamportTimestamp) {
            return Integer.compare(this.lamportTimestamp, other.lamportTimestamp);
        }
        // Si les timestamps Lamport sont égaux, on utilise l'ID pour un ordre déterministe
        return this.senderId.compareTo(other.senderId);
    }
    
    @Override
    public String toString() {
        return String.format("[%d] %s: %s", lamportTimestamp, senderName, content);
    }
    
    public String toDetailedString() {
        return String.format("[Lamport: %d, Physical: %s] %s: %s", 
                           lamportTimestamp, 
                           physicalTimestamp.toString(), 
                           senderName, 
                           content);
    }
}
