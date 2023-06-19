import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryGame extends JFrame {

    private final List<MemoryButton> buttons; // The list of memory buttons
    private final ImageIcon coverIcon; // The icon for the covered card
    private final List<ImageIcon> imageIcons; // The list of image icons for the game

    private MemoryButton selectedButton; // The currently selected button
    private int matchedPairs; // The number of matched pairs

    public MemoryGame() {
        setTitle ("Memory Game");
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setResizable (true);

        // The panel that contains the memory buttons
        JPanel panel = new JPanel (new GridLayout (4, 4, 5, 5)); // The panel is set up as a 4x4 grid
        buttons = new ArrayList<> (); // Initializing the list for memory buttons
        coverIcon = new ImageIcon ("src/images/cover.png"); // The icon for the covered card
        imageIcons = loadImages (); // Loading the image icons for the game

        // Create two copies of each image icon
        List<ImageIcon> pairedIcons = new ArrayList<> ();
        for (ImageIcon icon : imageIcons) {
            pairedIcons.add (icon);
            pairedIcons.add (icon);
        }

        // Shuffle the paired icons
        Collections.shuffle (pairedIcons);

        // Create memory buttons for each icon and add them to the panel
        for (ImageIcon icon : pairedIcons) {
            MemoryButton button = new MemoryButton (icon);
            button.addActionListener (event -> buttonClicked (button));
            buttons.add (button);
            panel.add (button);
        }

        add (panel);
        pack ();
        setLocationRelativeTo (null);
        setVisible (true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater (MemoryGame::new);
    }

    private void buttonClicked(MemoryButton button) {
        button.showImage ();

        if (selectedButton == null) {
            // Keine andere Karte ausgewählt
            selectedButton = button;
        } else {
            // Eine andere Karte ist bereits ausgewählt
            if (selectedButton.getImageIcon () == button.getImageIcon ()) {
                // Die beiden Karten passen zusammen
                selectedButton.setMatched (true);
                button.setMatched (true);
                selectedButton.setEnabled (false);
                button.setEnabled (false);
                selectedButton = null;

                hideMatchedPairs (); // Verstecke gefundene Paare

                // Überprüfe, ob alle Paare gefunden wurden
                int numMatchedPairs = 0;
                for (MemoryButton btn : buttons) {
                    if (btn.isMatched ()) {
                        numMatchedPairs++;
                    }
                }
                if (numMatchedPairs == buttons.size ()) {
                    // Alle Paare wurden gefunden
                    Object[] options = {"Neue Runde", "Beenden"};
                    int choice = JOptionPane.showOptionDialog (this,
                            "Herzlichen Glückwunsch! Du hast das Spiel gewonnen!",
                            "Spiel beendet",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            options,
                            options[0]);

                    if (choice == 0) {
                        resetGame ();
                    } else {
                        System.exit (0);
                    }
                }
            } else {
                // Die beiden Karten passen nicht zusammen
                Timer timer = new Timer (1000, e -> {
                    selectedButton.hideImage ();
                    button.hideImage ();
                    selectedButton = null;
                });
                timer.setRepeats (false);
                timer.start ();
            }
        }
    }

    private void hideMatchedPairs() {
        matchedPairs += 2; // Increment matchedPairs by 2
        for (MemoryButton button : buttons) {
            if (button.isMatched ()) {
                button.setEnabled (false);
            }
        }
    }

    private void resetGame() {
        selectedButton = null;
        matchedPairs = 0;

        // Reset the game by hiding all buttons and reshuffling the icons
        for (MemoryButton button : buttons) {
            button.hideImage ();
            button.setMatched (false);
            button.setEnabled (true);
            button.setBackground (null); // Reset background color
        }
        Collections.shuffle (imageIcons);
        int i = 0;
        for (MemoryButton button : buttons) {
            button.setImageIcon (imageIcons.get (i));
            i++;
        }
    }

    private List<ImageIcon> loadImages() {
        List<ImageIcon> icons = new ArrayList<> ();
        for (int i = 0; i < 15; i++) {
            String filename = "src/images/" + i + ".png";
            ImageIcon icon = new ImageIcon (filename);
            icons.add (icon);
        }
        return icons;
    }

    class MemoryButton extends JButton {
        private ImageIcon imageIcon;
        private boolean matched;

        public MemoryButton(ImageIcon icon) {
            setIcon (coverIcon);
            setPreferredSize (new Dimension (100, 100));
            setMargin (new Insets (0, 0, 0, 0));
            setContentAreaFilled (false);
            setBorderPainted (false);
            setOpaque (true);
            setBackground (Color.WHITE);

            imageIcon = icon;
            matched = false;
        }

        public void showImage() {
            setIcon (imageIcon);
        }

        public void hideImage() {
            setIcon (coverIcon);
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
