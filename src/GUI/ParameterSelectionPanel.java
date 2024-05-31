package GUI;

import javax.swing.*;
import java.awt.*;

class ParameterSelectionPanel extends JPanel {

    private JTextField polyModulusDegreeField;
    private JTextField plainModulusField;
    private JTextField cipherModulusField;

    public ParameterSelectionPanel() {
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Polynomial Modulus Degree:"));
        polyModulusDegreeField = new JTextField(20);
        polyModulusDegreeField.setSize(new Dimension(50, 50));
        add(polyModulusDegreeField);

        add(new JLabel("Plaintext Modulus:"));
        plainModulusField = new JTextField(20);
        add(plainModulusField);

        add(new JLabel("Ciphertext Modulus:"));
        cipherModulusField = new JTextField(20);
        add(cipherModulusField);

        JButton applyButton = new JButton("Apply Parameters");
        applyButton.addActionListener(e -> applyParameters());
        add(applyButton);
    }

    private void applyParameters() {
        int polyModulusDegree = Integer.parseInt(polyModulusDegreeField.getText());
        int plainModulus = Integer.parseInt(plainModulusField.getText());
        int cipherModulus = Integer.parseInt(cipherModulusField.getText());

        // TODO: Apply these parameters to your homomorphic encryption scheme

        JOptionPane.showMessageDialog(this, "Parameters applied successfully.");
    }
}

