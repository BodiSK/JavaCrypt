package GUI;

import scheme.bfv.*;
import utils.operations.SamplingOperations;
import utils.structures.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.Arrays;

class BasicOperationsPanel extends JPanel {

    private JTextField plaintextInput;
    private JTextArea console;
    private JTextArea theoryArea;
    private JCheckBox batchingCheckbox;

    // Fields to store keys, parameters, and ciphertexts
    private Parameters parameters;
    private PublicKey publicKey;
    private SecretKey secretKey;
    private BatchEncoder encoder;
    private Encryptor encryptor;
    private Decryptor decryptor;
    private Evaluator evaluator;

    // Store the last ciphertext for addition and multiplication
    private Ciphertext lastCiphertext;

    public BasicOperationsPanel() {
        setLayout(new BorderLayout());

        // Initialize parameters
        initializeEncryptionComponents();

        // Input and buttons panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Plaintext input panel
        JPanel plaintextInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        plaintextInputPanel.add(new JLabel("Plaintext:"));
        plaintextInput = new JTextField(20);
        plaintextInputPanel.add(plaintextInput);
        inputPanel.add(plaintextInputPanel);

        // Batching checkbox
        JPanel batchingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        batchingCheckbox = new JCheckBox("Enable Batching");
        batchingPanel.add(batchingCheckbox);
        inputPanel.add(batchingPanel);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        JButton encryptButton = createButton("Encrypt", e -> encryptPlaintext());
        JButton decryptButton = createButton("Decrypt", e -> decryptCiphertext());
        JButton addButton = createButton("Add", e -> performHomomorphicAddition());
        JButton multiplyButton = createButton("Multiply", e -> performHomomorphicMultiplication());

        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(addButton);
        buttonPanel.add(multiplyButton);

        inputPanel.add(buttonPanel);

        // Console
        console = new JTextArea(20, 40);
        console.setEditable(false);
        console.setBackground(Color.BLACK);
        console.setForeground(Color.WHITE);
        console.setFont(new Font("Monospaced", Font.PLAIN, 14));
        console.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane consoleScrollPane = new JScrollPane(console);

        // Theory area
        theoryArea = new JTextArea(20, 40);
        theoryArea.setEditable(false);
        theoryArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        theoryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane theoryScrollPane = new JScrollPane(theoryArea);

        // Split pane to hold both console and theory area
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, consoleScrollPane, theoryScrollPane);
        splitPane.setDividerLocation(200);

        // Main layout
        add(inputPanel, BorderLayout.WEST);
        add(splitPane, BorderLayout.CENTER);
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 30));
        button.addActionListener(actionListener);
        return button;
    }

    private void initializeEncryptionComponents() {
        int polynomialDegree = 4;  // Changed polynomial degree to 4
        BigInteger plaintextModulus = new BigInteger("257");
        BigInteger ciphertextModulus = new BigInteger("99999999999999999991");
        parameters = new Parameters(polynomialDegree, plaintextModulus, ciphertextModulus);

        KeyGenerator generator = new KeyGenerator(parameters);
        publicKey = generator.getPublicKey();
        secretKey = generator.getSecretKey();
        encoder = new BatchEncoder(parameters);
        encryptor = new Encryptor(parameters, publicKey);
        decryptor = new Decryptor(parameters, secretKey);
        evaluator = new Evaluator(parameters);
    }

    private void encryptPlaintext() {
        try {
            String plaintext = plaintextInput.getText();
            BigInteger[] coefficients = Arrays.stream(plaintext.split(","))
                    .map(BigInteger::new)
                    .toArray(BigInteger[]::new);
            Plaintext plain = encoder.encode(coefficients);
            lastCiphertext = encryptor.encrypt(plain);
            console.append("Encrypted: " + plaintext + "\n");
            updateTheoryArea("Encrypt", plaintext);
        } catch (Exception e) {
            console.append("Encryption failed: " + e.getMessage() + "\n");
        }
    }

    private void decryptCiphertext() {
        try {
            Plaintext decrypted = decryptor.decrypt(lastCiphertext, null);
            BigInteger[] coefficients = encoder.decode(decrypted);
            String decryptedText = Arrays.toString(coefficients);
            console.append("Decrypted: " + decryptedText + "\n");
            updateTheoryArea("Decrypt", decryptedText);
        } catch (Exception e) {
            console.append("Decryption failed: " + e.getMessage() + "\n");
        }
    }

    private void performHomomorphicAddition() {
        try {
            String secondPlaintext = JOptionPane.showInputDialog(this, "Enter second plaintext for addition (comma-separated integers):");
            BigInteger[] coefficients = Arrays.stream(secondPlaintext.split(","))
                    .map(BigInteger::new)
                    .toArray(BigInteger[]::new);
            Plaintext secondPlain = encoder.encode(coefficients);
            Ciphertext secondCiphertext = encryptor.encrypt(secondPlain);

            lastCiphertext = evaluator.add(lastCiphertext, secondCiphertext);
            console.append("Performed homomorphic addition.\n");
            updateTheoryArea("Add", secondPlaintext);
        } catch (Exception e) {
            console.append("Addition failed: " + e.getMessage() + "\n");
        }
    }

    private void performHomomorphicMultiplication() {
        try {
            String secondPlaintext = JOptionPane.showInputDialog(this, "Enter second plaintext for multiplication (comma-separated integers):");
            BigInteger[] coefficients = Arrays.stream(secondPlaintext.split(","))
                    .map(BigInteger::new)
                    .toArray(BigInteger[]::new);
            Plaintext secondPlain = encoder.encode(coefficients);
            Ciphertext secondCiphertext = encryptor.encrypt(secondPlain);

            lastCiphertext = evaluator.multiply(lastCiphertext, secondCiphertext, null);
            console.append("Performed homomorphic multiplication.\n");
            updateTheoryArea("Multiply", secondPlaintext);
        } catch (Exception e) {
            console.append("Multiplication failed: " + e.getMessage() + "\n");
        }
    }

    private void updateTheoryArea(String operation, String input) {
        switch (operation) {
            case "Encrypt":
                theoryArea.append("Step 1: Encrypting plaintext: " + input + "\n");
                theoryArea.append("Step 2: Encoding plaintext\n");
                theoryArea.append("Step 3: Encrypting using public key\n\n");
                break;
            case "Decrypt":
                theoryArea.append("Step 1: Decrypting ciphertext\n");
                theoryArea.append("Step 2: Decoding plaintext\n\n");
                break;
            case "Add":
                theoryArea.append("Step 1: Adding plaintext: " + input + "\n");
                theoryArea.append("Step 2: Encoding second plaintext\n");
                theoryArea.append("Step 3: Encrypting second plaintext\n");
                theoryArea.append("Step 4: Performing homomorphic addition\n\n");
                break;
            case "Multiply":
                theoryArea.append("Step 1: Multiplying plaintext: " + input + "\n");
                theoryArea.append("Step 2: Encoding second plaintext\n");
                theoryArea.append("Step 3: Encrypting second plaintext\n");
                theoryArea.append("Step 4: Performing homomorphic multiplication\n\n");
                break;
        }
    }
}
