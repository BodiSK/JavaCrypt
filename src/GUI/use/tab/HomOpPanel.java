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

public class HomOpPanel extends JPanel {

    private JTextField firstOperandField;
    private JTextField secondOperandField;
    private JTextField resultField;
    private JTextArea outputArea;
    private JFileChooser fileChooser;
    private Parameters parameters;
    private PublicKey publicKey;
    private RelinearizationKeys relinearizationKeys;
    private BatchEncoder encoder;
    private Encryptor encryptor;
    private Evaluator evaluator;
    private Ciphertext firstOperand;
    private Ciphertext secondOperand;
    private Ciphertext currentResult;
    private JButton loadFirstOperandButton;
    private JButton loadSecondOperandButton;
    private JButton addButton;
    private JButton multiplyButton;
    private JButton loadResultAsFirstOperandButton;

    public HomOpPanel(JTextArea outputArea) {
        this.outputArea = outputArea;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        initializeFileChooser();
        createHomOpPanel();
        initializeOperationButtons();
    }

    private void initializeFileChooser() {
        fileChooser = new JFileChooser();
    }

    private void createHomOpPanel() {
        JPanel homOpSection = new JPanel(new BorderLayout());
        homOpSection.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Homomorphic Operations"));

        JPanel operandPanel = new JPanel(new GridLayout(3, 3));
        operandPanel.add(new JLabel("First Operand:"));
        firstOperandField = new JTextField(20);
        firstOperandField.setEditable(false);
        operandPanel.add(firstOperandField);
        loadFirstOperandButton = new JButton("Load First Operand");
        loadFirstOperandButton.addActionListener(e -> loadOperandFromFile(true));
        operandPanel.add(loadFirstOperandButton);

        operandPanel.add(new JLabel("Second Operand:"));
        secondOperandField = new JTextField(20);
        secondOperandField.setEditable(false);
        operandPanel.add(secondOperandField);
        loadSecondOperandButton = new JButton("Load Second Operand");
        loadSecondOperandButton.addActionListener(e -> loadOperandFromFile(false));
        operandPanel.add(loadSecondOperandButton);

        operandPanel.add(new JLabel("Result:"));
        resultField = new JTextField(20);
        resultField.setEditable(false);
        operandPanel.add(resultField);
        loadResultAsFirstOperandButton = new JButton("Use Result as First Operand");
        loadResultAsFirstOperandButton.addActionListener(e -> useResultAsFirstOperand());
        operandPanel.add(loadResultAsFirstOperandButton);

        homOpSection.add(operandPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Add");
        addButton.addActionListener(e -> performHomomorphicOperation("Add"));
        multiplyButton = new JButton("Multiply");
        multiplyButton.addActionListener(e -> performHomomorphicOperation("Multiply"));
        buttonPanel.add(addButton);
        buttonPanel.add(multiplyButton);

        homOpSection.add(buttonPanel, BorderLayout.CENTER);

        JButton saveResultButton = new JButton("Save Result");
        saveResultButton.addActionListener(e -> saveResultToFile());
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        savePanel.add(saveResultButton);
        homOpSection.add(savePanel, BorderLayout.SOUTH);

        add(homOpSection);
    }

    private void initializeOperationButtons() {
        addButton.setEnabled(false);
        multiplyButton.setEnabled(false);
        loadResultAsFirstOperandButton.setEnabled(false);
    }

    private void loadOperandFromFile(boolean isFirstOperand) {
        fileChooser.setDialogTitle("Load Operand File");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToLoad))) {
                Ciphertext operand = (Ciphertext) ois.readObject();
                if (isFirstOperand) {
                    firstOperand = operand;
                    firstOperandField.setText(fileToLoad.getAbsolutePath());
                } else {
                    secondOperand = operand;
                    secondOperandField.setText(fileToLoad.getAbsolutePath());
                }
                if (firstOperand != null && secondOperand != null) {
                    enableOperationButtons();
                }
            } catch (IOException | ClassNotFoundException e) {
                outputArea.append("Error loading operand from file: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }

    private void enableOperationButtons() {
        addButton.setEnabled(true);
        multiplyButton.setEnabled(true);
    }

    private void performHomomorphicOperation(String operation) {
        if (firstOperand == null || secondOperand == null) {
            outputArea.append("Both operands must be loaded.\n");
            return;
        }

        if (parameters == null || publicKey == null || relinearizationKeys == null) {
            loadKeys();
        }

        if (parameters != null && publicKey != null && relinearizationKeys != null) {
            try {
                evaluator = new Evaluator(parameters);
                if ("Add".equals(operation)) {
                    currentResult = evaluator.add(firstOperand, secondOperand);
                    outputArea.append("Performed homomorphic addition.\n");
                } else if ("Multiply".equals(operation)) {
                    currentResult = evaluator.multiply(firstOperand, secondOperand, relinearizationKeys);
                    outputArea.append("Performed homomorphic multiplication.\n");
                }
                resultField.setText("Operation successful");
                loadResultAsFirstOperandButton.setEnabled(true);
            } catch (Exception e) {
                outputArea.append("Error performing homomorphic operation: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }

    private void useResultAsFirstOperand() {
        if (currentResult != null) {
            firstOperand = currentResult;
            firstOperandField.setText("Result of last operation");
            secondOperand = null;
            secondOperandField.setText("");
            enableOperationButtons();
        }
    }

    private void loadKeys() {
        fileChooser.setDialogTitle("Load Public Keys and Params");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToLoad))) {
                publicKey = (PublicKey) ois.readObject();
                relinearizationKeys = (RelinearizationKeys) ois.readObject();
                parameters = (Parameters) ois.readObject();
                encoder = new BatchEncoder(parameters);
                encryptor = new Encryptor(parameters, publicKey);
                outputArea.append("Public keys and parameters loaded from file: " + fileToLoad.getAbsolutePath() + "\n");
            } catch (IOException | ClassNotFoundException e) {
                outputArea.append("Error loading keys and parameters from file: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }

    private void saveResultToFile() {
        if (currentResult == null) {
            outputArea.append("No result to save.\n");
            return;
        }
        fileChooser.setDialogTitle("Save Result");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
                oos.writeObject(currentResult);
                outputArea.append("Result saved to file: " + fileToSave.getAbsolutePath() + "\n");
            } catch (IOException e) {
                outputArea.append("Error saving result to file: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }
}
