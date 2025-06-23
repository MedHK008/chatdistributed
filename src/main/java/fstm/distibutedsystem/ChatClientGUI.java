package fstm.distibutedsystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Interface graphique Swing pour le client de chat distribué
 */
public class ChatClientGUI extends UnicastRemoteObject implements ChatClientInterface {
    private static final long serialVersionUID = 1L;
    
    private JFrame frame;
    private JTextPane chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton historyButton;
    private JButton clientsButton;
    private JLabel statusLabel;
    private JLabel clockLabel;
    
    private final String clientName;
    private String clientId;
    private ChatServerInterface server;
    private final LamportClock lamportClock;
    private final BlockingQueue<Message> messageQueue;
    private boolean isConnected;
    
    private SimpleAttributeSet myMessageStyle;
    private SimpleAttributeSet otherMessageStyle;
    private SimpleAttributeSet systemMessageStyle;
    private SimpleAttributeSet timestampStyle;
    
    private static ChatClientGUI clientInstance;
    
    public ChatClientGUI(String clientName) throws RemoteException {
        super();
        this.clientName = clientName;
        this.lamportClock = new LamportClock();
        this.messageQueue = new LinkedBlockingQueue<>();
        this.isConnected = false;
        
        initializeStyles();
        createGUI();
        startMessageProcessor();
    }
    
    private void initializeStyles() {
        // Style pour mes messages
        myMessageStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(myMessageStyle, new Color(0, 102, 204));
        StyleConstants.setBold(myMessageStyle, true);
        
        // Style pour les messages des autres
        otherMessageStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(otherMessageStyle, new Color(51, 51, 51));
        
        // Style pour les messages système
        systemMessageStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(systemMessageStyle, new Color(153, 153, 153));
        StyleConstants.setItalic(systemMessageStyle, true);
        
        // Style pour les timestamps
        timestampStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(timestampStyle, new Color(128, 128, 128));
        StyleConstants.setFontSize(timestampStyle, 10);
    }
    
    /**
     * Crée l'interface graphique
     */
    private void createGUI() {
        frame = new JFrame("Chat Distribué - " + clientName);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel du haut avec titre et statut
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Zone de chat
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(Color.WHITE);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Messages"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel du bas pour saisie et boutons
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        
        // Gestionnaire de fermeture
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                disposeWindow();
            }
        });
        
       
    }
    

    
    /**
     * Crée le panel du haut avec titre et informations
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Chat Distribué avec Algorithme de Lamport", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        statusLabel = new JLabel("Statut: Non connecté", JLabel.LEFT);
        statusLabel.setForeground(Color.RED);
        
        clockLabel = new JLabel("Horloge Lamport: 0", JLabel.RIGHT);
        clockLabel.setForeground(new Color(102, 102, 102));
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(clockLabel, BorderLayout.EAST);
        
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(statusPanel, BorderLayout.SOUTH);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        return topPanel;
    }
    
    /**
     * Crée le panel du bas pour saisie et boutons
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Panel pour saisie de message
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        messageField.addActionListener(e -> sendMessage());
        
        sendButton = new JButton("Envoyer");
        sendButton.setBackground(new Color(0, 102, 204));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.addActionListener(e -> sendMessage());
        
        inputPanel.add(new JLabel("Message: "), BorderLayout.WEST);
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        // Panel pour boutons d'action
        JPanel actionPanel = new JPanel(new FlowLayout());
        
        historyButton = new JButton("Historique");
        historyButton.addActionListener(e -> showHistory());
        
        clientsButton = new JButton("Clients Connectés");
        clientsButton.addActionListener(e -> showConnectedClients());
        
        actionPanel.add(historyButton);
        actionPanel.add(clientsButton);
        
        bottomPanel.add(inputPanel, BorderLayout.NORTH);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        return bottomPanel;
    }
      /**
     * Se connecte au serveur
     */
    public void connectToServer(String serverHost, int serverPort) throws Exception {
        try {
            appendSystemMessage("Tentative de connexion à " + serverHost + ":" + serverPort + "...");
            updateStatus("Connexion en cours...", Color.ORANGE);
            
            Registry registry = LocateRegistry.getRegistry(serverHost, serverPort);
            appendSystemMessage("Registre RMI localisé, recherche du serveur 'ChatServer'...");
            
            server = (ChatServerInterface) registry.lookup("ChatServer");
            appendSystemMessage("Serveur trouvé, inscription du client...");
            
            // Essayer de s'inscrire avec le nom choisi
            clientId = server.registerClient(this, clientName);
            
            // Si le nom est déjà pris, le serveur retourne null
            if (clientId == null) {
                throw new RemoteException("Le nom \"" + clientName + "\" est déjà utilisé. Veuillez choisir un autre nom.");
            }
            
            isConnected = true;
            updateStatus("Connecté au serveur", Color.GREEN);
            appendSystemMessage("Connecté au serveur avec l'ID: " + clientId);
            appendSystemMessage("Bienvenue dans le chat distribué !");
            // Afficher la fenêtre dans l'EDT
            SwingUtilities.invokeLater(() -> {
                showWindow();
                frame.requestFocus();
                messageField.requestFocusInWindow();
            });
            
        } catch (java.rmi.NotBoundException e) {
            updateStatus("Serveur non trouvé", Color.RED);
            appendSystemMessage("Erreur: Le serveur 'ChatServer' n'est pas enregistré dans le registre RMI");
            appendSystemMessage("Vérifiez que le serveur est démarré et accessible");
            throw e;
        } catch (java.rmi.ConnectException e) {
            updateStatus("Connexion refusée", Color.RED);
            appendSystemMessage("Erreur: Impossible de se connecter au registre RMI sur " + serverHost + ":" + serverPort);
            appendSystemMessage("Vérifiez que le serveur est démarré et que le port est correct");
            throw e;
        } catch (java.rmi.RemoteException e) {
            updateStatus("Erreur RMI", Color.RED);
            appendSystemMessage("Erreur RMI: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            updateStatus("Erreur de connexion", Color.RED);
            appendSystemMessage("Erreur inattendue: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Met à jour le statut affiché
     */
    private void updateStatus(String status, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Statut: " + status);
            statusLabel.setForeground(color);
        });
    }
      /**
     * Met à jour l'affichage de l'horloge Lamport
     */
    private void updateClockDisplay() {
        SwingUtilities.invokeLater(() -> {
            clockLabel.setText("Horloge Lamport: " + lamportClock.getCurrentTimestamp());
        });
    }
    
    /**
     * Envoie un message
     */
    private void sendMessage() {
        String content = messageField.getText().trim();
        if (content.isEmpty() || !isConnected) {
            return;
        }
        
        try {
            int timestamp = lamportClock.tick();
            Message message = new Message(content, clientName, timestamp, clientId);
            server.broadcastMessage(message, timestamp);
            
            messageField.setText("");
            updateClockDisplay();
            
        } catch (RemoteException e) {
            appendSystemMessage("Erreur lors de l'envoi: " + e.getMessage());
        }
    }
    
    /**
     * Affiche l'historique dans une nouvelle fenêtre
     */
    private void showHistory() {
        if (!isConnected) return;
        
        try {
            List<Message> history = server.getMessageHistory();
            showHistoryDialog(history);
        } catch (RemoteException e) {
            appendSystemMessage("Erreur lors de la récupération de l'historique: " + e.getMessage());
        }
    }
    
    /**
     * Affiche la liste des clients connectés
     */
    private void showConnectedClients() {
        if (!isConnected) return;
        
        try {
            List<String> clients = server.getConnectedClients();
            showClientsDialog(clients);
        } catch (RemoteException e) {
            appendSystemMessage("Erreur lors de la récupération des clients: " + e.getMessage());
        }
    }
    
    /**
     * Affiche l'historique dans une boîte de dialogue
     */
    private void showHistoryDialog(List<Message> history) {
        JDialog dialog = new JDialog(frame, "Historique des Messages", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(frame);
        
        JTextPane historyArea = new JTextPane();
        historyArea.setEditable(false);
        
        StyledDocument doc = historyArea.getStyledDocument();
        
        if (history.isEmpty()) {
            try {
                doc.insertString(doc.getLength(), "Aucun message dans l'historique.", systemMessageStyle);
            } catch (BadLocationException e) {
                appendSystemMessage(" - Erreur lors de l'affichage de l'historique: " + e.getMessage());
            }
        } else {
            for (Message msg : history) {
                appendMessageToDocument(doc, msg, false);
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(historyArea);
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }
    
    /**
     * Affiche la liste des clients dans une boîte de dialogue
     */
    private void showClientsDialog(List<String> clients) {
        StringBuilder sb = new StringBuilder("Clients connectés :\n\n");
        
        if (clients.isEmpty()) {
            sb.append("Aucun client connecté.");
        } else {
            for (String client : clients) {
                sb.append("• ").append(client);
                if (client.equals(clientName)) {
                    sb.append(" (vous)");
                }
                sb.append("\n");
            }
        }
        
        JOptionPane.showMessageDialog(frame, sb.toString(), "Clients Connectés", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Ajoute un message système à la zone de chat
     */
    private void appendSystemMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = chatArea.getStyledDocument();
                doc.insertString(doc.getLength(), "[SYSTÈME] " + message + "\n", systemMessageStyle);
                chatArea.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                System.err.println("Erreur lors de l'ajout du message système: " + e.getMessage());
            }
        });
    }
    
    /**
     * Ajoute un message de chat à la zone d'affichage
     */
    private void appendChatMessage(Message message) {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = chatArea.getStyledDocument();
            appendMessageToDocument(doc, message, true);
            chatArea.setCaretPosition(doc.getLength());
        });
    }
    
    /**
     * Ajoute un message à un document donné
     */
    private void appendMessageToDocument(StyledDocument doc, Message message, boolean showTimestamp) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String timeStr = message.getPhysicalTimestamp().format(formatter);
            
            // Timestamp et horloge Lamport
            if (showTimestamp) {
                doc.insertString(doc.getLength(), "[" + timeStr + " | L:" + message.getLamportTimestamp() + "] ", timestampStyle);
            } else {
                doc.insertString(doc.getLength(), "[L:" + message.getLamportTimestamp() + "] ", timestampStyle);
            }
            
            // Nom de l'expéditeur
            SimpleAttributeSet nameStyle = message.getSenderName().equals(clientName) ? myMessageStyle : otherMessageStyle;
            doc.insertString(doc.getLength(), message.getSenderName() + ": ", nameStyle);
            
            // Contenu du message
            doc.insertString(doc.getLength(), message.getContent() + "\n", otherMessageStyle);
            
        } catch (BadLocationException e) {
            System.err.println("Erreur lors de l'ajout du message système: " + e.getMessage());
        }
    }
    
    /**
     * Se déconnecte du serveur
     */
    private void disconnect() {
        if (isConnected && server != null && clientId != null) {
            try {
                server.unregisterClient(clientId);
                appendSystemMessage("Déconnecté du serveur.");
            } catch (RemoteException e) {
                appendSystemMessage("Erreur lors de la déconnexion: " + e.getMessage());
            }
        }
        isConnected = false;
        updateStatus("Déconnecté", Color.RED);
    }
    
    /**
     * Démarre le processeur de messages
     */
    private void startMessageProcessor() {
        Thread messageThread = new Thread(() -> {
            while (true) {
                try {
                    Message message = messageQueue.take();
                    appendChatMessage(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        messageThread.setDaemon(true);
        messageThread.start();
    }
    
    // Implémentation des méthodes de ChatClientInterface
    
    @Override
    public void receiveMessage(Message message, int serverTimestamp) throws RemoteException {
        lamportClock.update(serverTimestamp);
        messageQueue.offer(message);
        updateClockDisplay();
    }
    
    @Override
    public void clientConnected(String clientName) throws RemoteException {
        int timestamp = lamportClock.tick();
        updateClockDisplay();
        appendSystemMessage(clientName + " s'est connecté (Lamport: " + timestamp + ")");
    }
    
    @Override
    public void clientDisconnected(String clientName) throws RemoteException {
        int timestamp = lamportClock.tick();
        updateClockDisplay();
        appendSystemMessage(clientName + " s'est déconnecté (Lamport: " + timestamp + ")");
    }
    
    @Override
    public String getClientName() throws RemoteException {
        return clientName;
    }
    
    @Override
    public String getClientId() throws RemoteException {
        return clientId;
    }
    
    /**
     * Boîte de dialogue pour la connexion au serveur
     */
    private static class ConnectionDialog extends JDialog {
        private String clientName;
        private String serverHost;
        private int serverPort;
        private boolean cancelled = true;
        
        public ConnectionDialog(JFrame parent) {
            super(parent, "Connexion au Chat", true);
            createDialog();
        }
        
        private void createDialog() {
            setSize(350, 200);
            setLocationRelativeTo(getParent());
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // Nom du client
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Votre nom:"), gbc);
            
            JTextField nameField = new JTextField(15);
            gbc.gridx = 1;
            panel.add(nameField, gbc);
            
            // Adresse du serveur
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Serveur:"), gbc);
            
            JTextField hostField = new JTextField("localhost", 15);
            gbc.gridx = 1;
            panel.add(hostField, gbc);
            
            // Port du serveur
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Port:"), gbc);
            
            JTextField portField = new JTextField("1099", 15);
            gbc.gridx = 1;
            panel.add(portField, gbc);
            
            // Boutons
            JPanel buttonPanel = new JPanel();
            JButton connectButton = new JButton("Connexion");
            JButton cancelButton = new JButton("Annuler");
            
            connectButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez entrer votre nom.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    clientName = name;
                    serverHost = hostField.getText().trim();
                    if (serverHost.isEmpty()) serverHost = "localhost";
                    
                    String portText = portField.getText().trim();
                    serverPort = portText.isEmpty() ? 1099 : Integer.parseInt(portText);
                    
                    cancelled = false;
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Port invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(connectButton);
            buttonPanel.add(cancelButton);
            
            gbc.gridx = 0; gbc.gridy = 3;
            gbc.gridwidth = 2;
            panel.add(buttonPanel, gbc);
            
            add(panel);
            
            // Définir le bouton par défaut
            getRootPane().setDefaultButton(connectButton);
            nameField.requestFocus();
        }
        
        public String getClientName() { return clientName; }
        public String getServerHost() { return serverHost; }
        public int getServerPort() { return serverPort; }
        public boolean isCancelled() { return cancelled; }
    }
      /**
     * Point d'entrée principal
     */

    private void disposeWindow() {
        if (frame != null) {
            frame.dispose();
        }
    }
    
    private void showWindow() {
        if (frame != null) {
            frame.setVisible(true);
            messageField.requestFocus();
        }
    }

    public static void main(String[] args) {
        // Afficher la boîte de dialogue de connexion
        ConnectionDialog dialog = new ConnectionDialog(null);
        // Boucle pour redemander les informations de connexion en cas d'erreur
        while (true) {
            dialog.setVisible(true);
            if (dialog.isCancelled()) {
                System.exit(0);
                return;
            }
            try {
                String clientName = dialog.getClientName();
                String serverHost = dialog.getServerHost();
                int serverPort = dialog.getServerPort();
                // Créer le client GUI
                clientInstance = new ChatClientGUI(clientName);
                // Essayer de se connecter
                // Fermer l'instance précédente si elle existe
                if (clientInstance != null) {
                    clientInstance.disconnect();
                    clientInstance.disposeWindow();
                    clientInstance = null;
                }
                
                try {
                    // Créer une nouvelle instance avec le nouveau nom
                    clientInstance = new ChatClientGUI(clientName);
                    clientInstance.connectToServer(serverHost, serverPort);
                    // Fermer la boîte de dialogue de connexion après succès
                    dialog.dispose();
                } catch (RemoteException e) {
                    if (e.getMessage() != null && e.getMessage().contains("déjà utilisé")) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Nom déjà utilisé. Veuillez en choisir un autre.", 
                            "Erreur", 
                            JOptionPane.WARNING_MESSAGE);
                        if (clientInstance != null) {
                            clientInstance.disposeWindow();
                            clientInstance = null;
                        }
                        continue;  // Revenir au début de la boucle
                    }
                    throw e;
                }
                break; // Sortir de la boucle si la connexion réussit
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(dialog, 
                    e.getMessage(), 
                    "Erreur de connexion", 
                    JOptionPane.WARNING_MESSAGE);
                dialog = new ConnectionDialog(null);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(dialog, 
                    "Erreur lors de la connexion: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    }
}
