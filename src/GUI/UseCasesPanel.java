package GUI;

import scheme.bfv.*;
import utils.structures.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

class UseCasesPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTextField lambdaField;
    private JTextField polyModulusDegreeField;
    private JTextField plaintextModulusField;
    private JTextField ciphertextModulusField;
    private JTextField customInputField;
    private JTextArea outputArea;
    private JFileChooser fileChooser;
    private Parameters parameters;
    private PublicKey publicKey;
    private SecretKey secretKey;
    private RelinearizationKeys relinearizationKeys;
    private BatchEncoder encoder;
    private Encryptor encryptor;
    private Decryptor decryptor;
    private Evaluator evaluator;
    private Ciphertext lastCiphertext;
    private JButton nextButton;
    private JButton previousButton;
    private JButton finishButton;
    private int currentStep = 0;

    private JPanel keyOptionsPanel;
    private JPanel parameterInputPanel;
    private JButton saveKeysButton;

    public UseCasesPanel() {
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Initialize fileChooser
        fileChooser = new JFileChooser();

        // Step panels
        JPanel step1Panel = createStep1Panel();
        JPanel step2Panel = createStep2Panel();
        JPanel step3Panel = createStep3Panel();
        JPanel step4Panel = createStep4Panel();
        JPanel step5Panel = createStep5Panel();

        cardPanel.add(step1Panel, "Step1");
        cardPanel.add(step2Panel, "Step2");
        cardPanel.add(step3Panel, "Step3");
        cardPanel.add(step4Panel, "Step4");
        cardPanel.add(step5Panel, "Step5");

        JPanel wizardPanel = new JPanel(new BorderLayout());
        wizardPanel.add(cardPanel, BorderLayout.NORTH);

        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        previousButton = new JButton("Previous");
        previousButton.addActionListener(e -> showPreviousStep());
        navigationPanel.add(previousButton);
        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> showNextStep());
        navigationPanel.add(nextButton);
        finishButton = new JButton("Finish");
        finishButton.addActionListener(e -> finishWizard());
        navigationPanel.add(finishButton);
        wizardPanel.add(navigationPanel, BorderLayout.SOUTH);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(wizardPanel, BorderLayout.NORTH);

        add(leftPanel, BorderLayout.WEST);

        // Console output area
        outputArea = new JTextArea(20, 30);
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.WHITE);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        updateNavigationButtons();
    }

    private JPanel createStep1Panel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Key Generation Section
        JPanel keyGenSection = new JPanel(new BorderLayout());
        keyGenSection.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Key Generation"));

        keyOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton loadKeysRadio = new JRadioButton("Load Keys from File");
        loadKeysRadio.addActionListener(e -> toggleParameterInput(false, false));
        JRadioButton generateKeysRadio = new JRadioButton("Generate Keys");
        generateKeysRadio.addActionListener(e -> toggleParameterInput(true, false));
        ButtonGroup keyOptionsGroup = new ButtonGroup();
        keyOptionsGroup.add(loadKeysRadio);
        keyOptionsGroup.add(generateKeysRadio);

        keyOptionsPanel.add(loadKeysRadio);
        keyOptionsPanel.add(generateKeysRadio);
        keyGenSection.add(keyOptionsPanel, BorderLayout.NORTH);

        parameterInputPanel = new JPanel();
        parameterInputPanel.setLayout(new BoxLayout(parameterInputPanel, BoxLayout.Y_AXIS));

        JPanel lambdaInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lambdaInputPanel.add(new JLabel("Lambda:"));
        lambdaField = new JTextField(10);
        lambdaInputPanel.add(lambdaField);
        JButton generateParamsButton = new JButton("Generate Parameters from Lambda");
        generateParamsButton.addActionListener(e -> generateParametersFromLambda());
        lambdaInputPanel.add(generateParamsButton);
        parameterInputPanel.add(lambdaInputPanel);

        JPanel manualParamsPanel = new JPanel(new GridLayout(6, 2));
        manualParamsPanel.add(new JLabel("Polynomial Modulus Degree:"));
        polyModulusDegreeField = new JTextField(10);
        manualParamsPanel.add(polyModulusDegreeField);
        manualParamsPanel.add(new JLabel("Plaintext Modulus:"));
        plaintextModulusField = new JTextField(10);
        manualParamsPanel.add(plaintextModulusField);
        manualParamsPanel.add(new JLabel("Ciphertext Modulus:"));
        ciphertextModulusField = new JTextField(10);
        manualParamsPanel.add(ciphertextModulusField);
        parameterInputPanel.add(manualParamsPanel);

        JButton setParamsButton = new JButton("Manually Input Parameters");
        setParamsButton.addActionListener(e -> setParametersManually());
        parameterInputPanel.add(setParamsButton);

        JButton generateKeysButton = new JButton("Generate Keys");
        generateKeysButton.addActionListener(e -> generateKeys());
        parameterInputPanel.add(generateKeysButton);

        saveKeysButton = new JButton("Save Keys to File");
        saveKeysButton.addActionListener(e -> saveKeysToFile());
        parameterInputPanel.add(saveKeysButton);

        keyGenSection.add(parameterInputPanel, BorderLayout.CENTER);
        panel.add(keyGenSection);

        // Add key listener to lambda field to auto-set parameters
        lambdaField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!lambdaField.getText().isEmpty()) {
                    generateParametersFromLambda();
                }
            }
        });

        // Initial state
        toggleParameterInput(false, true);

        return panel;
    }

    private JPanel createStep2Panel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Custom Input:"));
        customInputField = new JTextField(20);
        panel.add(customInputField);
        JButton encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(e -> encryptPlaintext());
        panel.add(encryptButton);
        JButton loadCsvButton = new JButton("Load CSV");
        loadCsvButton.addActionListener(e -> loadCSVFile());
        panel.add(loadCsvButton);
        return panel;
    }

    private JPanel createStep3Panel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> performHomomorphicAddition());
        panel.add(addButton);
        JButton multiplyButton = new JButton("Multiply");
        multiplyButton.addActionListener(e -> performHomomorphicMultiplication());
        panel.add(multiplyButton);
        return panel;
    }

    private JPanel createStep4Panel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton decryptButton = new JButton("Decrypt");
        decryptButton.addActionListener(e -> decryptCiphertext());
        panel.add(decryptButton);
        return panel;
    }

    private JPanel createStep5Panel() {
        JPanel panel = new JPanel(new BorderLayout());

        outputArea = new JTextArea(20, 50);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void toggleParameterInput(boolean show, boolean init) {
        parameterInputPanel.setVisible(show);
        saveKeysButton.setVisible(show);
        polyModulusDegreeField.setEditable(show);
        plaintextModulusField.setEditable(show);
        ciphertextModulusField.setEditable(show);
        if (!show && !init) {
            loadKeysFromFile();
        }
    }

    private void generateParametersFromLambda() {
        try {
            int lambda = Integer.parseInt(lambdaField.getText());
            int polynomialDegree = (int) Math.pow(2, lambda / 8);
            BigInteger plaintextModulus = new BigInteger("257");
            BigInteger ciphertextModulus = new BigInteger("99999999999999999991");
            parameters = new Parameters(polynomialDegree, plaintextModulus, ciphertextModulus);
            encoder = new BatchEncoder(parameters);
            outputArea.append("Parameters generated with lambda: " + lambda + "\n");
            polyModulusDegreeField.setText(String.valueOf(polynomialDegree));
            plaintextModulusField.setText(plaintextModulus.toString());
            ciphertextModulusField.setText(ciphertextModulus.toString());
            polyModulusDegreeField.setEditable(false);
            plaintextModulusField.setEditable(false);
            ciphertextModulusField.setEditable(false);
        } catch (NumberFormatException e) {
            outputArea.append("Invalid lambda value.\n");
        }
    }

    private void setParametersManually() {
        try {
            int polynomialDegree = Integer.parseInt(polyModulusDegreeField.getText());
            BigInteger plaintextModulus = new BigInteger(plaintextModulusField.getText());
            BigInteger ciphertextModulus = new BigInteger(ciphertextModulusField.getText());
            parameters = new Parameters(polynomialDegree, plaintextModulus, ciphertextModulus);
            encoder = new BatchEncoder(parameters);
            outputArea.append("Parameters set manually.\n");
            lambdaField.setText("");
            polyModulusDegreeField.setEditable(true);
            plaintextModulusField.setEditable(true);
            ciphertextModulusField.setEditable(true);
        } catch (NumberFormatException e) {
            outputArea.append("Invalid parameter values.\n");
        }
    }

    private void generateKeys() {
        if (parameters == null) {
            outputArea.append("Set parameters first.\n");
            return;
        }
        KeyGenerator generator = new KeyGenerator(parameters);
        publicKey = generator.getPublicKey();
        secretKey = generator.getSecretKey();
        relinearizationKeys = generator.getRelinearizationKeys();
        encryptor = new Encryptor(parameters, publicKey);
        decryptor = new Decryptor(parameters, secretKey);
        evaluator = new Evaluator(parameters);
        outputArea.append("Keys generated.\n");
    }

    private void saveKeysToFile() {
        if (publicKey == null || secretKey == null || relinearizationKeys == null) {
            outputArea.append("Generate keys first.\n");
            return;
        }
        fileChooser.setDialogTitle("Save Keys");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                Files.createDirectories(fileToSave.toPath());
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(fileToSave, "publicKey.key")))) {
                    oos.writeObject(publicKey);
                }
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(fileToSave, "secretKey.key")))) {
                    oos.writeObject(secretKey);
                }
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(fileToSave, "relinearizationKeys.key")))) {
                    oos.writeObject(relinearizationKeys);
                }
                outputArea.append("Keys saved to folder: " + fileToSave.getAbsolutePath() + "\n");
            } catch (IOException e) {
                outputArea.append("Error saving keys to folder: " + e.getMessage() + "\n");
            }
        }
    }

    private void loadKeysFromFile() {
        fileChooser.setDialogTitle("Load Keys");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToLoad))) {
                publicKey = (PublicKey) ois.readObject();
                secretKey = (SecretKey) ois.readObject();
                relinearizationKeys = (RelinearizationKeys) ois.readObject();
                parameters = (Parameters) ois.readObject(); // Assuming parameters are stored within keys
                encoder = new BatchEncoder(parameters);
                encryptor = new Encryptor(parameters, publicKey);
                decryptor = new Decryptor(parameters, secretKey);
                evaluator = new Evaluator(parameters);
                outputArea.append("Keys loaded from file: " + fileToLoad.getAbsolutePath() + "\n");
            } catch (IOException | ClassNotFoundException e) {
                outputArea.append("Error loading keys from file: " + e.getMessage() + "\n");
            }
        }
    }

    private void loadCSVFile() {
        fileChooser.setDialogTitle("Load CSV File");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File csvFile = fileChooser.getSelectedFile();
            try {
                List<String> lines = Files.readAllLines(Paths.get(csvFile.toURI()));
                for (String line : lines) {
                    encryptPlaintext(line);
                }
                outputArea.append("Data loaded from CSV file: " + csvFile.getAbsolutePath() + "\n");
            } catch (IOException e) {
                outputArea.append("Error loading CSV file: " + e.getMessage() + "\n");
            }
        }
    }

    private void encryptPlaintext() {
        String plaintext = customInputField.getText();
        encryptPlaintext(plaintext);
    }

    private void encryptPlaintext(String plaintext) {
        try {
            BigInteger[] coefficients = Arrays.stream(plaintext.split(","))
                    .map(BigInteger::new)
                    .toArray(BigInteger[]::new);
            Plaintext plain = encoder.encode(coefficients);
            lastCiphertext = encryptor.encrypt(plain);
            outputArea.append("Encrypted: " + plaintext + "\n");
        } catch (Exception e) {
            outputArea.append("Encryption failed: " + e.getMessage() + "\n");
        }
    }

    private void decryptCiphertext() {
        if (lastCiphertext == null) {
            outputArea.append("No ciphertext to decrypt.\n");
            return;
        }
        try {
            Plaintext decrypted = decryptor.decrypt(lastCiphertext, null);
            BigInteger[] coefficients = encoder.decode(decrypted);
            String decryptedText = Arrays.toString(coefficients);
            outputArea.append("Decrypted: " + decryptedText + "\n");
        } catch (Exception e) {
            outputArea.append("Decryption failed: " + e.getMessage() + "\n");
        }
    }

    private void performHomomorphicAddition() {
        if (lastCiphertext == null) {
            outputArea.append("No ciphertext to add.\n");
            return;
        }
        try {
            String secondPlaintext = JOptionPane.showInputDialog(this, "Enter second plaintext for addition (comma-separated integers):");
            BigInteger[] coefficients = Arrays.stream(secondPlaintext.split(","))
                    .map(BigInteger::new)
                    .toArray(BigInteger[]::new);
            Plaintext secondPlain = encoder.encode(coefficients);
            Ciphertext secondCiphertext = encryptor.encrypt(secondPlain);

            lastCiphertext = evaluator.add(lastCiphertext, secondCiphertext);
            outputArea.append("Performed homomorphic addition.\n");
        } catch (Exception e) {
            outputArea.append("Addition failed: " + e.getMessage() + "\n");
        }
    }

    private void performHomomorphicMultiplication() {
        if (lastCiphertext == null) {
            outputArea.append("No ciphertext to multiply.\n");
            return;
        }
        try {
            String secondPlaintext = JOptionPane.showInputDialog(this, "Enter second plaintext for multiplication (comma-separated integers):");
            BigInteger[] coefficients = Arrays.stream(secondPlaintext.split(","))
                    .map(BigInteger::new)
                    .toArray(BigInteger[]::new);
            Plaintext secondPlain = encoder.encode(coefficients);
            Ciphertext secondCiphertext = encryptor.encrypt(secondPlain);

            lastCiphertext = evaluator.multiply(lastCiphertext, secondCiphertext, relinearizationKeys);
            outputArea.append("Performed homomorphic multiplication.\n");
        } catch (Exception e) {
            outputArea.append("Multiplication failed: " + e.getMessage() + "\n");
        }
    }

    private void showNextStep() {
        if (currentStep < 4) {
            currentStep++;
            cardLayout.show(cardPanel, "Step" + (currentStep + 1));
            updateNavigationButtons();
        }
    }

    private void showPreviousStep() {
        if (currentStep > 0) {
            currentStep--;
            cardLayout.show(cardPanel, "Step" + (currentStep + 1));
            updateNavigationButtons();
        }
    }

    private void finishWizard() {
        JOptionPane.showMessageDialog(this, "Wizard completed!");
    }

    private void updateNavigationButtons() {
        previousButton.setEnabled(currentStep > 0);
        nextButton.setEnabled(currentStep < 4);
        finishButton.setEnabled(currentStep == 4);
    }
}
