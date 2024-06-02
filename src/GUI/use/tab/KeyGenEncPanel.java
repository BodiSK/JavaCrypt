package GUI.use.tab;

import scheme.bfv.*;
import utils.structures.Ciphertext;
import utils.structures.Plaintext;
import utils.structures.PublicKey;
import utils.structures.SecretKey;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class KeyGenEncPanel extends JPanel {

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
    private JButton saveKeysButton;
    private BigInteger lastPlaintextModulus;
    private JLabel customInputLabel;
    private JButton loadCsvButton;
    private JButton saveButton;
    private JButton nextButton;
    private JButton prevButton;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel parameterInputPanel;

    public KeyGenEncPanel(JTextArea outputArea) {
        this.outputArea = outputArea;
        setLayout(new BorderLayout());
        initializeFileChooser();
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel keyGenPanel = createKeyGenPanel();
        JPanel encryptPanel = createEncryptPanel();

        cardPanel.add(keyGenPanel, "KeyGen");
        cardPanel.add(encryptPanel, "Encrypt");

        add(cardPanel, BorderLayout.CENTER);

        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        prevButton = new JButton("Previous");
        prevButton.addActionListener(e -> cardLayout.show(cardPanel, "KeyGen"));
        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> cardLayout.show(cardPanel, "Encrypt"));
        saveButton = new JButton("Save to File");
        saveButton.addActionListener(e -> saveCiphertextToFile());
        saveButton.setVisible(false);

        navigationPanel.add(prevButton);
        navigationPanel.add(nextButton);
        navigationPanel.add(saveButton);

        add(navigationPanel, BorderLayout.SOUTH);
    }

    private void initializeFileChooser() {
        fileChooser = new JFileChooser();
    }

    private JPanel createKeyGenPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel keyGenSection = new JPanel(new BorderLayout());
        keyGenSection.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Key Generation"));

        JPanel keyOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton loadKeysRadio = new JRadioButton("Load Keys from File");
        loadKeysRadio.addActionListener(e -> {
            toggleParameterInput(false);
            loadKeysFromFile();
        });
        JRadioButton generateKeysRadio = new JRadioButton("Generate Keys");
        generateKeysRadio.addActionListener(e -> toggleParameterInput(true));
        ButtonGroup keyOptionsGroup = new ButtonGroup();
        keyOptionsGroup.add(loadKeysRadio);
        keyOptionsGroup.add(generateKeysRadio);

        keyOptionsPanel.add(loadKeysRadio);
        keyOptionsPanel.add(generateKeysRadio);
        keyGenSection.add(keyOptionsPanel, BorderLayout.NORTH);

        parameterInputPanel = new JPanel();
        parameterInputPanel.setLayout(new BoxLayout(parameterInputPanel, BoxLayout.Y_AXIS));

        JPanel lambdaInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lambdaInputPanel.add(new JLabel("Security input parameter (lambda):"));
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
        JPanel plaintextModulusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        plaintextModulusField = new JTextField(10);
        JButton nextPlaintextButton = new JButton("→");
        nextPlaintextButton.setToolTipText("Generate next possible plaintext modulus");
        nextPlaintextButton.addActionListener(e -> generateNextPlaintextModulus());
        plaintextModulusPanel.add(plaintextModulusField);
        plaintextModulusPanel.add(nextPlaintextButton);
        manualParamsPanel.add(plaintextModulusPanel);
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

        toggleParameterInput(false);

        return panel;
    }

    private JPanel createEncryptPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel encryptionSection = new JPanel(new BorderLayout());
        encryptionSection.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Encrypt"));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton inputManualRadio = new JRadioButton("Input data manually");
        JRadioButton loadCsvRadio = new JRadioButton("Load from CSV file");
        ButtonGroup inputGroup = new ButtonGroup();
        inputGroup.add(inputManualRadio);
        inputGroup.add(loadCsvRadio);
        inputPanel.add(inputManualRadio);
        inputPanel.add(loadCsvRadio);
        encryptionSection.add(inputPanel, BorderLayout.NORTH);

        customInputLabel = new JLabel("Custom Input:");
        customInputField = new JTextField(20);
        customInputLabel.setVisible(false);
        customInputField.setVisible(false);
        inputPanel.add(customInputLabel);
        inputPanel.add(customInputField);

        inputManualRadio.addActionListener(e -> {
            customInputLabel.setVisible(true);
            customInputField.setVisible(true);
        });
        loadCsvRadio.addActionListener(e -> {
            customInputLabel.setVisible(false);
            customInputField.setVisible(false);
            loadCSVFile();
        });

        encryptionSection.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(e -> encryptPlaintext());
        buttonPanel.add(encryptButton);
        saveButton = new JButton("Save to File");
        saveButton.addActionListener(e -> saveCiphertextToFile());
        buttonPanel.add(saveButton);

        encryptionSection.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(encryptionSection);

        return panel;
    }

    private void toggleParameterInput(boolean show) {
        for (Component component : parameterInputPanel.getComponents()) {
            component.setVisible(show);
        }
    }

    private void generateParametersFromLambda() {
        try {
            int lambda = Integer.parseInt(lambdaField.getText());
            int polynomialDegree = 1 << (lambda - 1);
            BigInteger base = BigInteger.valueOf(2L * polynomialDegree);
            BigInteger plaintextModulus = base.add(BigInteger.ONE);
            while (!plaintextModulus.isProbablePrime(100)) {
                plaintextModulus = plaintextModulus.add(base);
            }
            lastPlaintextModulus = plaintextModulus;

            BigInteger ciphertextModulus = (plaintextModulus.compareTo(BigInteger.valueOf(10000)) > 0) ?
                    new BigInteger("9999999991") :
                    new BigInteger("799999999");

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

    private void generateNextPlaintextModulus() {
        if (lastPlaintextModulus == null) {
            outputArea.append("Generate initial parameters first.\n");
            return;
        }
        int polynomialDegree = Integer.parseInt(polyModulusDegreeField.getText());
        BigInteger base = BigInteger.valueOf(2 * polynomialDegree);
        BigInteger nextPlaintextModulus = lastPlaintextModulus.add(base);
        while (!nextPlaintextModulus.isProbablePrime(100)) {
            nextPlaintextModulus = nextPlaintextModulus.add(base);
        }
        lastPlaintextModulus = nextPlaintextModulus;
        plaintextModulusField.setText(nextPlaintextModulus.toString());
        outputArea.append("Next plaintext modulus generated: " + nextPlaintextModulus.toString() + "\n");

        BigInteger ciphertextModulus = (nextPlaintextModulus.compareTo(BigInteger.valueOf(10000)) > 0) ?
                new BigInteger("9999999991") :
                new BigInteger("799999999");
        ciphertextModulusField.setText(ciphertextModulus.toString());
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
            int polynomialDegree = Integer.parseInt(polyModulusDegreeField.getText());
            BigInteger plaintextModulus = new BigInteger(plaintextModulusField.getText());
            BigInteger ciphertextModulus = new BigInteger(ciphertextModulusField.getText());
            parameters = new Parameters(polynomialDegree, plaintextModulus, ciphertextModulus);
            encoder = new BatchEncoder(parameters);
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

                // Save the secret key and parameters in one file
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(fileToSave, "secretKey.key")))) {
                    oos.writeObject(secretKey);
                    oos.writeObject(parameters);
                }

                // Save the public key and relinearization keys in a different file
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(fileToSave, "publicKeysParams.key")))) {
                    oos.writeObject(publicKey);
                    oos.writeObject(relinearizationKeys);
                    oos.writeObject(parameters);
                }

                outputArea.append("Keys saved to folder: " + fileToSave.getAbsolutePath() + "\n");
            } catch (IOException e) {
                outputArea.append("Error saving keys to folder: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }

    private void loadKeysFromFile() {
        fileChooser.setDialogTitle("Load Keys");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToLoad))) {
                parameters = (Parameters) ois.readObject();
                publicKey = (PublicKey) ois.readObject();
                relinearizationKeys = (RelinearizationKeys) ois.readObject();
                secretKey = (SecretKey) ois.readObject();
                encoder = new BatchEncoder(parameters);
                encryptor = new Encryptor(parameters, publicKey);
                decryptor = new Decryptor(parameters, secretKey);
                evaluator = new Evaluator(parameters);
                outputArea.append("Keys loaded from file: " + fileToLoad.getAbsolutePath() + "\n");
            } catch (IOException | ClassNotFoundException e) {
                outputArea.append("Error loading keys from file: " + e.getMessage() + "\n");
                e.printStackTrace();
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
                e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    private void saveCiphertextToFile() {
        if (lastCiphertext == null) {
            outputArea.append("No ciphertext to save.\n");
            return;
        }
        fileChooser.setDialogTitle("Save Ciphertext");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
                oos.writeObject(lastCiphertext);
                outputArea.append("Ciphertext saved to file: " + fileToSave.getAbsolutePath() + "\n");
            } catch (IOException e) {
                outputArea.append("Error saving ciphertext to file: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }
}
