package GUI.use.tab;

import javax.swing.*;
import java.awt.*;

public class UseCasesPanel extends JPanel {

    private CardLayout mainCardLayout;
    private JPanel mainCardPanel;
    private JTextArea outputArea;

    public UseCasesPanel() {
        setLayout(new BorderLayout());

        mainCardLayout = new CardLayout();
        mainCardPanel = new JPanel(mainCardLayout);

        outputArea = new JTextArea(20, 50);
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.WHITE);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(getScreenWidth() / 3, getScreenHeight()));  // Adjust to half the screen width
        add(scrollPane, BorderLayout.EAST);

        JPanel keyGenEncPanel = new KeyGenEncPanel(outputArea);
        JPanel homOpPanel = new HomOpPanel(outputArea);
        JPanel decryptPanel = new DecryptPanel(outputArea);

        mainCardPanel.add(keyGenEncPanel, "KeyGenEnc");
        mainCardPanel.add(homOpPanel, "HomOp");
        mainCardPanel.add(decryptPanel, "Decrypt");

        add(mainCardPanel, BorderLayout.CENTER);

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> menuComboBox = new JComboBox<>(new String[]{"Key Generation & Encryption", "Homomorphic Operations", "Decryption"});
        menuComboBox.addActionListener(e -> switchToPanel((String) menuComboBox.getSelectedItem()));

        menuPanel.add(new JLabel("Select Operation:"));
        menuPanel.add(menuComboBox);
        add(menuPanel, BorderLayout.NORTH);
    }

    private int getScreenWidth() {
        return Toolkit.getDefaultToolkit().getScreenSize().width;
    }

    private int getScreenHeight() {
        return Toolkit.getDefaultToolkit().getScreenSize().height;
    }

    private void switchToPanel(String panelName) {
        switch (panelName) {
            case "Key Generation & Encryption":
                mainCardLayout.show(mainCardPanel, "KeyGenEnc");
                break;
            case "Homomorphic Operations":
                mainCardLayout.show(mainCardPanel, "HomOp");
                break;
            case "Decryption":
                mainCardLayout.show(mainCardPanel, "Decrypt");
                break;
        }
    }
}
