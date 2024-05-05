package view;

import view.main.App;
import view.main.states.PlayingState;
import view.ui.UIFactory;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import javax.swing.Box;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.imageio.ImageIO;
import java.awt.CardLayout;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MainMenuView extends JPanel {

    private JPanel choicePanel = new JPanel(new GridBagLayout());
    private BufferedImage backgroundImage;
    private CardLayout switcher = new CardLayout();
    private AkropolisTitleLabel titleLabel;
    private Clip backgroundMusicClip;

    public MainMenuView() {
        super();
        this.setLayout(switcher);
        // Charger l'image de fond
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/menu/akropolisBG.jpg")));
            // Rendre le fond transparent pour afficher l'image
            setOpaque(false);
        } catch (IOException e) {
            // Remplacer l'image manquante par une couleur de fond grise
            setBackground(Color.GRAY);
        }
        choicePanel.setOpaque(false);
        add(choicePanel, "choicePanel");
        add(new SettingsPanel(switcher), "settingsPanel");
        switcher.show(this, "choicePanel");
        addTitleLabel();
        addButtonsPanel();
        playBackgroundMusic();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Dessiner l'image de fond
        if (backgroundImage != null)
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        stopBackgroundMusic();
        titleLabel.stop();
    }

    private void addTitleLabel() {
        titleLabel = new AkropolisTitleLabel();
        GridBagConstraints titleGbc = new GridBagConstraints();
        titleGbc.gridx = 0;
        titleGbc.gridy = 0;
        titleGbc.weightx = 1;
        titleGbc.weighty = 0.1;
        titleGbc.fill = GridBagConstraints.NONE;
        titleGbc.anchor = GridBagConstraints.NORTH;
        choicePanel.add(titleLabel, titleGbc);
    }

    private void addButtonsPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(2,2, 20, 20));
        buttonPanel.setOpaque(false);
    
        // Bouton démarrer
        JButton startButton = UIFactory.createStyledButton("Démarrer", e -> startNewGame());
        // Bouton règles
        JButton rulesButton = UIFactory.createStyledButton("Règles du jeu", e -> UIFactory.showRulesPanel());
        // Bouton crédits
        JButton creditsButton = UIFactory.createStyledButton("Crédits", e -> showCreditsPanel());
        // Bouton quitter
        JButton quitButton = UIFactory.createStyledButton("Quitter", e -> System.exit(0));
    
        buttonPanel.add(rulesButton);
        buttonPanel.add(startButton);
        buttonPanel.add(creditsButton);
        buttonPanel.add(quitButton);
    
        buttonPanel.add(Box.createVerticalStrut(10));
    
        // Settings button
        JButton settingsButton = UIFactory.createStyledButton("Paramètres", e -> switcher.show(this, "settingsPanel"));
        buttonPanel.add(settingsButton);
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.NONE;
    
        choicePanel.add(buttonPanel, gbc);
    }

    private void playBackgroundMusic() {
        try {
            // Utilisation de getResourceAsStream pour lire le fichier depuis les ressources
            InputStream audioSrc = getClass().getResourceAsStream("/sound/Akropolis.wav");
            assert audioSrc != null;
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            // stay in loop if you want to play the clip continuously
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusicClip != null) {
            backgroundMusicClip.stop();
        }
    }

    /**
     * Start a new game
     */
    private void startNewGame() {
        stopBackgroundMusic();
        System.out.println("Nouvelle partie démarrée");

        // Utilisation de JComboBox pour sélectionner le nombre de joueurs
        JComboBox<Integer> playerNumberComboBox = new JComboBox<>(new Integer[] { 1, 2, 3, 4 });
        playerNumberComboBox.setSelectedIndex(0); // Sélectionne la première option par défaut

        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dialogPanel.add(new JLabel("Choisissez le nombre de joueurs pour la partie:"), BorderLayout.NORTH);
        dialogPanel.add(playerNumberComboBox, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                this,
                dialogPanel,
                "Nombre de Joueurs",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int numberOfPlayers = (int) playerNumberComboBox.getSelectedItem();
            List<String> playerNames = new ArrayList<>();

            for (int i = 1; i <= numberOfPlayers; i++) {
                String playerName = collectPlayerName(i);
                if (playerName == null) {
                    // That means the user clicked cancel
                    // So we should stop the game creation process
                    System.out.println("Annulation de la création de la partie");
                    return;
                }
                playerNames.add(playerName);
            }
            if (!playerNames.isEmpty()) {
                PlayingState playingState = PlayingState.getInstance();
                playingState.setPlayers(playerNames);
                App.getInstance().appState.changeState(playingState);
            }
        } else {
            System.out.println("Aucun choix fait, partie non démarrée");
            JOptionPane.showMessageDialog(this, "Aucune sélection effectuée. La partie ne démarrera pas.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Collect the player name
     *
     * @param playerNumber the player number
     * @return the player name
     */
    private String collectPlayerName(int playerNumber) {
        String playerName;
        do {
            playerName = JOptionPane.showInputDialog(this, "Entrez le nom du joueur " + playerNumber + " :",
                    "Nom du Joueur", JOptionPane.PLAIN_MESSAGE);
            if (playerName == null) {
                return null;
            } else if (playerName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le nom du joueur ne peut pas être vide.", "Erreur",
                        JOptionPane.WARNING_MESSAGE);
                playerName = null;
            } else if (!playerName.matches("[a-zA-Z0-9]{1,10}")) {
                JOptionPane.showMessageDialog(this,
                        "Le nom du joueur ne peut contenir que des caractères alphanumériques et doit être de 10 caractères maximum.",
                        "Erreur",
                        JOptionPane.WARNING_MESSAGE);
                playerName = null;
            }
        } while (playerName == null);
        return playerName.trim();
    }

    /**
     * showCreditsPanel
     */
    private void showCreditsPanel() {
        // Création du dialogue pour les crédits
        JDialog creditsDialog = new JDialog();
        creditsDialog.setTitle("Crédits");
        creditsDialog.setSize(400, 300); // Taille ajustable selon vos besoins
        creditsDialog.setLocationRelativeTo(this); // Centre par rapport à MainMenuView
        creditsDialog.setLayout(new BorderLayout());

        // Panel semi-transparent qui assombrit l'arrière-plan
        JPanel overlayPanel = new JPanel();
        overlayPanel.setLayout(new BorderLayout());
        overlayPanel.setBackground(new Color(0, 0, 0, 150)); // Couleur semi-transparente

        // Panel pour les noms des développeurs
        JPanel creditsPanel = new JPanel();
        creditsPanel.setLayout(new GridLayout(0, 1)); // Disposition en colonne pour les noms
        // Ajoutez ici les noms des membres de l'équipe
        creditsPanel.add(createStyledCreditLabel("Développeur 1: CHETOUANI Bilal"));
        creditsPanel.add(createStyledCreditLabel("Développeur 2: BENZERDJEB Rayene"));
        creditsPanel.add(createStyledCreditLabel("Développeur 3: MOUSSA Nidhal"));
        creditsPanel.add(createStyledCreditLabel("Développeur 4: PIGET Matheo"));
        creditsPanel.add(createStyledCreditLabel("Développeur 5: GBAGUIDI Nerval"));

        // Ajoutez autant de JLabel que nécessaire pour les noms des membres de votre
        // équipe

        // Ajout des panels au dialogue
        overlayPanel.add(creditsPanel, BorderLayout.CENTER);
        creditsDialog.add(overlayPanel);

        creditsDialog.setModal(true); // Bloque l'interaction avec la fenêtre principale
        creditsDialog.setVisible(true); // Affiche le dialogue des crédits
    }

    private JLabel createStyledCreditLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.BLACK);
        return label;
    }

}
