import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryGame extends JFrame {

    private final List<MemoryButton> buttons;
    private final ImageIcon coverIcon;
    private final List<ImageIcon> imageIcons;
    private final JLabel currentPlayerLabel;
    private final JLabel scoreLabel;
    private MemoryButton selectedButton;
    private int foundPairs;
    private int currentPlayer;

    public MemoryGame() {
        setTitle("Memory Spiel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        JPanel panel = new JPanel(new GridLayout(4, 4, 5, 5));
        buttons = new ArrayList<>();
        coverIcon = new ImageIcon((new ImageIcon("src/images/cover.png")).getImage().getScaledInstance(200, 200, 4));
        imageIcons = loadImages();
        // currentPlayer auf "1" setzen, da er sonst mit "0" startet und am Anfang sonst 2 Züge hat.
        currentPlayer = 1;

        // Erstelle zwei Kopien von jedem Bild (Kartenpaar)
        List<ImageIcon> pairedIcons = new ArrayList<>();
        for (ImageIcon icon : imageIcons) {
            pairedIcons.add(icon);
            pairedIcons.add(icon);
        }

        // Mische die Karten
        Collections.shuffle(pairedIcons);

        for (ImageIcon icon : pairedIcons) {
            MemoryButton button = new MemoryButton(icon);
            button.addActionListener(event -> buttonClicked(button));
            buttons.add(button);
            panel.add(button);
        }

        // new
        currentPlayerLabel = new JLabel("Spieler 1 ist an der Reihe");
        scoreLabel = new JLabel("Punkte: Spieler 1 - 0, Spieler 2 - 0");
        scoreLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(scoreLabel, BorderLayout.EAST);

        add(currentPlayerLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        //

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryGame::new);
    }

    private void buttonClicked(MemoryButton button) {
        if (button.isMatched() || button == selectedButton) {
            // Bereits gefunden oder doppelt geklickt, ignoriere den Klick
            return;
        }

        button.showImage();

        if (selectedButton == null) {
            // Keine Karte ist ausgewählt
            selectedButton = button;
        } else {
            // Eine andere Karte ist bereits ausgewählt
            if (selectedButton.getImageIcon() == button.getImageIcon()) {
                // Paar gefunden
                selectedButton.setMatched(true);
                button.setMatched(true);
                selectedButton.setEnabled(false);
                button.setEnabled(false);
                selectedButton = null;

                foundPairs++;
                updateScore();
                System.out.println(foundPairs + " - " + imageIcons.size());
                if (foundPairs == imageIcons.size()) {
                    // Alle Paare gefunden
                    int winner = (getMatchedPairsCount(1) > getMatchedPairsCount(2)) ? 1 : 2;
                    Object[] options = {"Neue Runde", "Beenden"};
                    int choice = JOptionPane.showOptionDialog(this,
                            "Herzlichen Glückwunsch, Spieler " + winner + "! Du hast " + getMatchedPairsCount(winner) + " Paare gefunden!",
                            "Spiel beendet",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            options,
                            options[0]);

                    if (choice == 0) {
                        resetGame();
                    } else {
                        System.exit(0);
                    }
                }
            } else {

                // Kein Paar gefunden
                Timer timer = new Timer(1000, e -> {
                    selectedButton.hideImage();
                    button.hideImage();
                    selectedButton = null;
                    // player switcher
                    switchPlayers();
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    private void switchPlayers() {
        System.out.println("Aktueller Spieler: " + currentPlayer);
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        currentPlayerLabel.setText("Spieler " + currentPlayer + " ist an der Reihe");
    }

    private void updateScore() {
        int player1Score = getMatchedPairsCount(1);
        int player2Score = getMatchedPairsCount(2);
        scoreLabel.setText("Punkte: Spieler 1 - " + player1Score + ", Spieler 2 - " + player2Score);
    }

    private void resetGame() {
        selectedButton = null;
        currentPlayer = 1;
        foundPairs = 0;
        currentPlayerLabel.setText("Spieler 1 ist an der Reihe");
        scoreLabel.setText("Punkte: Spieler 1 - 0, Spieler 2 - 0");

        // Setze das Spiel zurück, indem alle Karten versteckt und neu gemischt werden
        for (MemoryButton button : buttons) {
            button.hideImage();
            button.setMatched(false);
            button.setMatchedPlayer(0);
            button.setEnabled(true);
        }
        Collections.shuffle(imageIcons);
        int i = 0;
        for (MemoryButton button : buttons) {
            button.setImageIcon(imageIcons.get(i));
            i++;
        }
    }

    private List<ImageIcon> loadImages() {
        List<ImageIcon> icons = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            String filename = "src/images/" + i + ".png";
            // scale icon up
            ImageIcon rawIcon = new ImageIcon(filename);
            Image img = rawIcon.getImage().getScaledInstance(200, 200, 4);
            icons.add(new ImageIcon(img));
        }
        return icons;
    }

    private int getMatchedPairsCount(int player) {
        int count = 0;
        for (MemoryButton button : buttons) {
            if (button.isMatched() && button.getMatchedPlayer() == player) {
                count++;
            }
        }
        // durch zwei teilen, da ein paar = 2 buttons
        return count / 2;
    }

    private void showEndDialog() {
        int player1Score = getMatchedPairsCount(1);
        int player2Score = getMatchedPairsCount(2);

        String winner;
        if (player1Score > player2Score) {
            winner = "Spieler 1";
        } else if (player1Score < player2Score) {
            winner = "Spieler 2";
        } else {
            winner = "Unentschieden";
        }

        Object[] options = {"Neue Runde", "Beenden"};
        int choice = JOptionPane.showOptionDialog(this,
                "Herzlichen Glückwunsch! " + winner + " hat das Spiel gewonnen und die meisten Paare gefunden!",
                "Spiel beendet",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            resetGame();
        } else {
            System.exit(0);
        }
    }


    class MemoryButton extends JButton {
        private ImageIcon imageIcon;
        private boolean matched;
        private int matchedPlayer;

        public MemoryButton(ImageIcon icon) {
            setIcon(coverIcon);
            setPreferredSize(new Dimension(100, 100));
            setMargin(new Insets(0, 0, 0, 0));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(true);
            setBackground(Color.WHITE);
            imageIcon = icon;
            matched = false;
            matchedPlayer = 0;
        }

        public void showImage() {
            setIcon(imageIcon);
        }

        public void hideImage() {
            setIcon(coverIcon);
        }

        public ImageIcon getImageIcon() {
            return imageIcon;
        }

        public void setImageIcon(ImageIcon icon) {
            imageIcon = icon;
        }

        public boolean isMatched() {
            return matched;
        }

        public void setMatched(boolean matched) {
            this.matched = matched;
            if (matched) {
                setMatchedPlayer(currentPlayer);
            }
        }

        public int getMatchedPlayer() {
            return matchedPlayer;
        }

        public void setMatchedPlayer(int player) {
            matchedPlayer = player;
        }
    }
}