package fstm.distibutedsystem;

/**
 * Tests unitaires simples pour valider l'algorithme de Lamport
 */
public class LamportClockTest {
    
    public static void main(String[] args) {
        System.out.println("=== Tests de l'Horloge de Lamport ===");
        
        // Test 1: Initialisation
        testInitialization();
        
        // Test 2: Tick local
        testLocalTick();
        
        // Test 3: Update avec message reçu
        testMessageUpdate();
        
        // Test 4: Scénario complexe
        testComplexScenario();
        
        System.out.println("=== Tous les tests réussis! ===");
    }
    
    private static void testInitialization() {
        System.out.println("\nTest 1: Initialisation");
        
        LamportClock clock1 = new LamportClock();
        LamportClock clock2 = new LamportClock(5);
        
        assert clock1.getCurrentTimestamp() == 0 : "Clock1 devrait être initialisée à 0";
        assert clock2.getCurrentTimestamp() == 5 : "Clock2 devrait être initialisée à 5";
        
        System.out.println("✓ Initialisation correcte");
    }
    
    private static void testLocalTick() {
        System.out.println("\nTest 2: Événements locaux (tick)");
        
        LamportClock clock = new LamportClock();
        
        int timestamp1 = clock.tick();
        int timestamp2 = clock.tick();
        int timestamp3 = clock.tick();
        
        assert timestamp1 == 1 : "Premier tick devrait être 1";
        assert timestamp2 == 2 : "Deuxième tick devrait être 2";
        assert timestamp3 == 3 : "Troisième tick devrait être 3";
        assert clock.getCurrentTimestamp() == 3 : "Timestamp actuel devrait être 3";
        
        System.out.println("✓ Ticks locaux corrects");
    }
    
    private static void testMessageUpdate() {
        System.out.println("\nTest 3: Mise à jour avec messages reçus");
        
        LamportClock clock = new LamportClock();
        clock.tick(); // clock = 1
        
        // Recevoir un message avec timestamp 5
        int newTimestamp = clock.update(5);
        assert newTimestamp == 6 : "Update(5) depuis 1 devrait donner 6";
        
        // Recevoir un message avec timestamp plus petit
        newTimestamp = clock.update(3);
        assert newTimestamp == 7 : "Update(3) depuis 6 devrait donner 7";
        
        System.out.println("✓ Mises à jour de messages correctes");
    }
    
    private static void testComplexScenario() {
        System.out.println("\nTest 4: Scénario complexe multi-processus");
        
        // Simuler trois processus
        LamportClock processA = new LamportClock();
        LamportClock processB = new LamportClock();
        LamportClock processC = new LamportClock();
        
        
        System.out.println("État final:");
        System.out.println("Processus A: " + processA.getCurrentTimestamp());
        System.out.println("Processus B: " + processB.getCurrentTimestamp());
        System.out.println("Processus C: " + processC.getCurrentTimestamp());
        
        assert processA.getCurrentTimestamp() == 7 : "Processus A devrait être à 7";
        assert processB.getCurrentTimestamp() == 4 : "Processus B devrait être à 4";
        assert processC.getCurrentTimestamp() == 6 : "Processus C devrait être à 6";
        
        System.out.println("✓ Scénario complexe correct");
    }
}
