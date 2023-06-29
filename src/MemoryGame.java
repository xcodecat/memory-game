import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryGame extends JFrame {

    private final List<MemoryButton> buttons;
    private final ImageIcon coverIcon;
    private final List<ImageIcon> imageIcons;

    private MemoryButton selectedButton;
    private int foundPairs;

    public MemoryGame() {
        setTitle("Memory Spiel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        JPanel panel = new JPanel(new GridLayout(4, 4, 5, 5));
        buttons = new ArrayList<>();
        coverIcon = new ImageIcon("src/images/cover.png");
        imageIcons = loadImages();

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
                if (foundPairs == imageIcons.size()) {
                    // Alle Paare gefunden
                    Object[] options = {"Neue Runde", "Beenden"};
                    int choice = JOptionPane.showOptionDialog(this,
                            "Herzlichen Glückwunsch! Du hast alle Paare gefunden!",
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
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    private void resetGame() {
        selectedButton = null;
        foundPairs = 0;

        // Setze das Spiel zurück, indem alle Karten versteckt und neu gemischt werden
        for (MemoryButton button : buttons) {
            button.hideImage();
            button.setMatched(false);
            button.setEnabled(true);
            button.setBackground(null); // Setze Hintergrundfarbe zurück
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
            ImageIcon icon = new ImageIcon(filename);
            icons.add(icon);
        }
        return icons;
    }

    class MemoryButton extends JButton {
        private ImageIcon imageIcon;
        private boolean matched;

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
        }
    }
}
