package GUI.use.tab;

import scheme.bfv.*;
import utils.structures.Ciphertext;
import utils.structures.Plaintext;
import utils.structures.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;

public class DecryptPanel extends JPanel {

    private JTextArea outputArea;
    private JFileChooser fileChooser;
    private Parameters parameters;
    private SecretKey secretKey;
    private BatchEncoder encoder;
    private Decryptor decryptor;
    private Ciphertext lastCiphertext;
    private BigInteger[] lastDecryptedResult;

    public DecryptPanel(JTextArea outputArea) {
        this.outputArea = outputArea;
        setLayout(new BorderLayout());
        initializeFileChooser();
        createDecryptPanel();
    }

    private void initializeFileChooser() {
        fileChooser = new JFileChooser();
    }

    private void createDecryptPanel() {
        JPanel decryptSection = new JPanel();
        decryptSection.setLayout(new BoxLayout(decryptSection, BoxLayout.Y_AXIS));
        decryptSection.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Decrypt and Decode"));

        JButton loadSecretKeyButton = createButton("Load Secret Key");
        loadSecretKeyButton.addActionListener(e -> loadSecretKeyFromFile());
        decryptSection.add(loadSecretKeyButton);

        JButton loadCiphertextButton = createButton("Load Ciphertext");
        loadCiphertextButton.addActionListener(e -> loadCiphertextFromFile());
        decryptSection.add(loadCiphertextButton);

        JButton decryptButton = createButton("Decrypt");
        decryptButton.addActionListener(e -> decryptCiphertext());
        decryptSection.add(decryptButton);

        JButton saveButton = createButton("Save Decrypted Output");
        saveButton.addActionListener(e -> saveDecryptedOutputAsCSV());
        decryptSection.add(saveButton);

        add(decryptSection, BorderLayout.CENTER);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension preferredSize = new Dimension(200, 30);
        button.setPreferredSize(preferredSize);
        button.setMaximumSize(preferredSize);
        return button;
    }

    private void loadSecretKeyFromFile() {
        fileChooser.setDialogTitle("Load Secret Key File");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToLoad))) {
                secretKey = (SecretKey) ois.readObject();
                parameters = (Parameters) ois.readObject();
                encoder = new BatchEncoder(parameters);
                decryptor = new Decryptor(parameters, secretKey);
                outputArea.append("Secret key and parameters loaded from file: " + fileToLoad.getAbsolutePath() + "\n");
            } catch (IOException | ClassNotFoundException e) {
                outputArea.append("Error loading secret key and parameters from file: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }

    private void loadCiphertextFromFile() {
        fileChooser.setDialogTitle("Load Ciphertext File");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToLoad))) {
                lastCiphertext = (Ciphertext) ois.readObject();
                outputArea.append("Ciphertext loaded from file: " + fileToLoad.getAbsolutePath() + "\n");
            } catch (IOException | ClassNotFoundException e) {
                outputArea.append("Error loading ciphertext from file: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }

    private void decryptCiphertext() {
        if (lastCiphertext == null) {
            outputArea.append("No ciphertext to decrypt.\n");
            return;
        }
        if (secretKey == null || parameters == null) {
            outputArea.append("No secret key or parameters loaded.\n");
            return;
        }
        try {
            Plaintext decrypted = decryptor.decrypt(lastCiphertext, null);
            lastDecryptedResult = encoder.decode(decrypted);
            String decryptedText = Arrays.toString(lastDecryptedResult);
            outputArea.append("Decrypted: " + decryptedText + "\n");
        } catch (Exception e) {
            outputArea.append("Decryption failed: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void saveDecryptedOutputAsCSV() {
        if (lastDecryptedResult == null) {
            outputArea.append("No decrypted result to save.\n");
            return;
        }
        fileChooser.setDialogTitle("Save Decrypted Output");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                for (BigInteger value : lastDecryptedResult) {
                    writer.write(value.toString());
                    writer.newLine();
                }
                outputArea.append("Decrypted output saved to file: " + fileToSave.getAbsolutePath() + "\n");
            } catch (IOException e) {
                outputArea.append("Error saving decrypted output to file: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }
}
