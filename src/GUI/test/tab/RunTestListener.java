package GUI.test.tab;

import demos.DepthTestCaseDemo;
import demos.ParametersAccuracyTestCaseDemo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

public class RunTestListener implements ActionListener {
    private final TestCasesPanel panel;

    public RunTestListener(TestCasesPanel panel) {
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TestCases selectedTestCases = (TestCases) panel.getTestCaseComboBox().getSelectedItem();
        try {
            int polyModulusDegree = Integer.parseInt(panel.getPolyModulusDegreeField().getText());
            BigInteger plainModulus = new BigInteger(panel.getPlainModulusField().getText());
            BigInteger cipherModulus = new BigInteger(panel.getCipherModulusField().getText());
            int dataRange = panel.getDataRange().isVisible() ? Integer.parseInt(panel.getDataRange().getText()) : 0;
            int numIterations = panel.getNumIterationsField().isVisible() ? Integer.parseInt(panel.getNumIterationsField().getText()) :0;
            String operation = (String) panel.getOperationsComboBox().getSelectedItem();

            switch (selectedTestCases) {
                case PARAMETER_ACCURACY_TEST:
                    runParameterAccuracyTest(polyModulusDegree, plainModulus, cipherModulus,dataRange, operation, numIterations);
                    break;
                case DEPTH_TEST:
                    runDepthTest(polyModulusDegree, plainModulus, cipherModulus, operation, dataRange);
                    break;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel,
                    "Please enter valid integer values for all parameters.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception exception) {
            panel.getConsole().setText("");
            JOptionPane.showMessageDialog(panel,
                    "There was an error with, processing your input." +
                            " If you are not familiar wth the constraints please refer to the more info sections.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runParameterAccuracyTest(int polyModulusDegree, BigInteger plainModulus, BigInteger cipherModulus,
                                          int dataRange, String operation, int numIterations) {
        panel.getConsole().append("Running Parameter Accuracy Test...\n");
        panel.getConsole().append("Parameters: Polynomial Modulus Degree = " + polyModulusDegree
                + ", Plaintext Modulus = " + plainModulus
                + ", Ciphertext Modulus = " + cipherModulus
                + ", Data Range = " + dataRange
                + ", Operation = " + operation
                + ", Number of Iterations = " + numIterations + "\n");
        ParametersAccuracyTestCaseDemo test = new ParametersAccuracyTestCaseDemo();

        panel.getConsole().append(String.format("Result: Accuracy is %s...\n\n",
                test.run(polyModulusDegree, plainModulus, cipherModulus, dataRange, operation, numIterations)));
    }

    private void runDepthTest(int polyModulusDegree, BigInteger plainModulus, BigInteger cipherModulus, String operation, int dataRange) {
        panel.getConsole().append("Running Depth Test...\n");
        panel.getConsole().append("Parameters: Polynomial Modulus Degree = " + polyModulusDegree
                + ", Plaintext Modulus = " + plainModulus
                + ", Ciphertext Modulus = " + cipherModulus
                + ", Operation = " + operation
                + ", Data Range = " + dataRange + "\n");

        DepthTestCaseDemo test = new DepthTestCaseDemo();
        panel.getConsole().append(String.format("Result: %s...\n\n",
                test.run(polyModulusDegree, plainModulus, cipherModulus, dataRange, operation)));
    }
}

