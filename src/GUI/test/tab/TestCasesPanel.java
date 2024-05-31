package GUI.test.tab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestCasesPanel extends JPanel {
    private JComboBox<TestCases> testCaseComboBox;
    private JTextArea console;
    private JPanel schemeParameterPanel;
    private JPanel testParameterPanel;
    private JTextField polyModulusDegreeField;
    private JTextField plainModulusField;
    private JTextField cipherModulusField;
    private JTextField dataRange;
    private JComboBox<String> operationsComboBox;
    private JTextField numIterationsField;
    private JLabel numIterationsLabel;
    private JLabel dataRangeLabel;
    private JButton numIterationsInfoButton;
    private JButton dataRangeInfoButton;
    private JButton testExplanationButton;
    private JButton clearButton;

    private JPanel infoPanel;
    private JButton lastInfoButton;

    private static final Map<String, String[]> infoMap = new HashMap<>();

    static {
        initializeInfoMap();
    }

    public TestCasesPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Margin around components

        initComponents();
        layoutComponents(gbc);
    }

    private static void initializeInfoMap() {
        infoMap.put("Polynomial Degree", new String[]{
                "Info about Polynomial Degree",
                "Controls the size of the polynomial degree.",
                "That is the maximum number of coefficients in a polynomial from the ciphertext.",
                "Constraints: Must be a power of 2.",
                "Examples: 2, 4, 8, 16..."
        });
        infoMap.put("Plaintext Modulus", new String[]{
                "Info about Plaintext Modulus",
                "Defines the modulus for plaintext coefficients.",
                "The coefficients of the plaintext polynomials will be in the range from 0 to the value provided.",
                "Constraints: Must be a prime number, value minus one must be divisible by the polynomial degree multiplied by two.",
                "Examples: if polynomial degree is 16, a suitable value is 257."
        });
        infoMap.put("Ciphertext Modulus", new String[]{
                "Info about Ciphertext Modulus",
                "Defines the modulus for ciphertext coefficients.",
                "The coefficients of the ciphertext polynomials will be in the range from 0 to the value provided.",
                "Constraints: Preferable to be a prime number."
        });
        infoMap.put("Data Range", new String[]{
                "Info about Data Range",
                "Defines the range of data values to be encrypted.",
                "Constraints: Must be integer greater than 0."
        });
        infoMap.put("Operations", new String[]{
                "Info about Operations",
                "Homomorphic operations, currently supported by JavaCrypt are 'multiply' or 'add'.",
                "You can choose for which operation to run a test."
        });
        infoMap.put("Number of Iterations", new String[]{
                "Info about Number of Iterations",
                "Specify the number of iterations for the test.",
                "Constraints: Must be an integer greater than 0."
        });

        // explanations for test cases
        infoMap.put("PARAMETER_ACCURACY_TEST", new String[]{
                "Parameter Accuracy Test Explanation",
                "This test evaluates the accuracy of the parameters.",
                "It ensures that the polynomial modulus degree, plaintext modulus, and ciphertext modulus are set correctly.",
                "Its goal is to perform homomorphic operations on generated randomly in specified bounds data, " +
                        "and check for correctness of the operation's result.",
                "It runs as many times as the user specifies as number of iterations."
        });
        infoMap.put("DEPTH_TEST", new String[]{
                "Depth Test Explanation",
                "This test measures the multiplicative and additive depth.",
                "It helps in understanding the limits of the operations.",
                "It tests how many operations are allowed, based on the scheme parameters given, " +
                        "before the noise corrupts the ciphertext"
        });
    }

    private void initComponents() {
        initTestCaseComboBox();
        initSchemeParameterPanel();
        initTestParameterPanel();
        initConsole();
        initTestExplanationButton();
        initClearButton(); // Initialize the clear button
    }

    private void initTestCaseComboBox() {
        testCaseComboBox = new JComboBox<>(TestCases.values());
        testCaseComboBox.setRenderer(new ToolTipComboBoxRenderer(Arrays.stream(TestCases.values()).map(TestCases::getTooltip).toArray(String[]::new)));
        testCaseComboBox.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                JComboBox<?> comboBox = (JComboBox<?>) e.getSource();
                int index = comboBox.getSelectedIndex();
                if (index > -1) {
                    comboBox.setToolTipText(TestCases.values()[index].getTooltip());
                } else {
                    comboBox.setToolTipText(null);
                }
            }
        });
        testCaseComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateUIForSelectedTestCase();
            }
        });
    }

    private void initSchemeParameterPanel() {
        schemeParameterPanel = createTitledPanel("Scheme Parameters");
        GridBagConstraints parameterGbc = createGridBagConstraints();

        addParameterField("Polynomial Modulus Degree:", polyModulusDegreeField = new JTextField(10), "Polynomial Degree", parameterGbc, schemeParameterPanel);
        addParameterField("Plaintext Modulus:", plainModulusField = new JTextField(10), "Plaintext Modulus", parameterGbc, schemeParameterPanel);
        addParameterField("Ciphertext Modulus:", cipherModulusField = new JTextField(10), "Ciphertext Modulus", parameterGbc, schemeParameterPanel);
    }

    private void initTestParameterPanel() {
        testParameterPanel = createTitledPanel("Test Parameters");
        GridBagConstraints parameterGbc = createGridBagConstraints();

        dataRangeLabel = new JLabel("Data range - upper bound:");
        dataRange = new JTextField(10);
        dataRangeInfoButton = createInfoButton("Data Range");

        parameterGbc.gridx = 0;
        parameterGbc.gridy = 0;
        testParameterPanel.add(dataRangeLabel, parameterGbc);

        parameterGbc.gridx++;
        testParameterPanel.add(dataRange, parameterGbc);

        parameterGbc.gridx++;
        testParameterPanel.add(dataRangeInfoButton, parameterGbc);

        operationsComboBox = new JComboBox<>(new String[]{"multiply", "add"});
        operationsComboBox.setPreferredSize(new Dimension(100, 20));
        addParameterField("Operations:", operationsComboBox, "Operations", parameterGbc, testParameterPanel);

        parameterGbc.gridx = 0;
        parameterGbc.gridy = testParameterPanel.getComponentCount() / 3;

        numIterationsLabel = new JLabel("Number of Iterations:");
        numIterationsField = new JTextField(10);
        numIterationsInfoButton = createInfoButton("Number of Iterations");

        parameterGbc.gridx = 0;
        parameterGbc.gridy++;
        testParameterPanel.add(numIterationsLabel, parameterGbc);

        parameterGbc.gridx++;
        testParameterPanel.add(numIterationsField, parameterGbc);

        parameterGbc.gridx++;
        testParameterPanel.add(numIterationsInfoButton, parameterGbc);
    }

    private void initTestExplanationButton() {
        testExplanationButton = new JButton("Test Explanation");
        testExplanationButton.setPreferredSize(new Dimension(150, 30));
        testExplanationButton.addActionListener(e -> showTestExplanation());
    }

    private void initClearButton() {
        clearButton = new JButton("Clear");
        clearButton.setPreferredSize(new Dimension(120, 30));
        clearButton.addActionListener(e -> clearFieldsAndConsole());
    }

    private void showTestExplanation() {
        TestCases selectedTestCases = (TestCases) testCaseComboBox.getSelectedItem();
        assert selectedTestCases != null;
        String[] explanation = infoMap.get(selectedTestCases.name());
        if (explanation != null) {
            String formattedText = formatExplanationText(explanation);
            JOptionPane.showMessageDialog(this, formattedText, "Test Explanation", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No explanation available.", "Test Explanation", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearFieldsAndConsole() {
        polyModulusDegreeField.setText("");
        plainModulusField.setText("");
        cipherModulusField.setText("");
        dataRange.setText("");
        numIterationsField.setText("");
        operationsComboBox.setSelectedIndex(0);
        console.setText("");
    }

    private String formatExplanationText(String[] explanation) {
        StringBuilder formattedText = new StringBuilder("<html>");
        for (String line : explanation) {
            formattedText.append(line).append("<br>");
        }
        formattedText.append("</html>");
        return formattedText.toString();
    }

    private void initConsole() {
        console = new JTextArea(10, 50);
        console.setEditable(false);
        console.setBackground(Color.BLACK);
        console.setForeground(Color.WHITE);
        console.setFont(new Font("Monospaced", Font.PLAIN, 16));
        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Console"));
    }

    private void layoutComponents(GridBagConstraints gbc) {
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton runButton = createButton("Run Test", new Color(70, 130, 180), new RunTestListener(this));

        northPanel.add(testCaseComboBox);
        northPanel.add(runButton);
        northPanel.add(testExplanationButton); // Add the test explanation button
        northPanel.add(clearButton); // Add the clear button

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(northPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        add(schemeParameterPanel, gbc);

        gbc.gridy = 2;
        add(testParameterPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.weightx = 1.5;
        gbc.weighty = 1.0;
        add(new JScrollPane(console), gbc);

        addMouseListeners();
    }

    private void addMouseListeners() {
        MouseAdapter closeInfoPanelListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (infoPanel != null && !infoPanel.getBounds().contains(e.getPoint())) {
                    closeInfoPanel();
                }
            }
        };

        addMouseListener(closeInfoPanelListener);
        console.addMouseListener(closeInfoPanelListener);
        schemeParameterPanel.addMouseListener(closeInfoPanelListener);
        testParameterPanel.addMouseListener(closeInfoPanelListener);
    }

    private JButton createButton(String text, Color color, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 30));
        button.addActionListener(listener);
        return button;
    }

    private void addParameterField(String labelText, JComponent component, String infoKey, GridBagConstraints gbc, JPanel panel) {
        gbc.gridx = 0;
        gbc.gridy = panel.getComponentCount() / 3;
        JLabel label = new JLabel(labelText);
        panel.add(label, gbc);

        gbc.gridx++;
        panel.add(component, gbc);

        gbc.gridx++;
        JButton infoButton = createInfoButton(infoKey);
        panel.add(infoButton, gbc);
    }

    private JButton createInfoButton(String infoKey) {
        JButton infoButton = new JButton("⮟");
        infoButton.setPreferredSize(new Dimension(50, 20));
        infoButton.addActionListener(new InfoButtonListener(infoKey, infoButton));
        return infoButton;
    }

    private void updateUIForSelectedTestCase() {
        TestCases selectedTestCases = (TestCases) testCaseComboBox.getSelectedItem();

        boolean isDepthTest = selectedTestCases == TestCases.DEPTH_TEST;

        numIterationsLabel.setVisible(!isDepthTest);
        numIterationsField.setVisible(!isDepthTest);
        numIterationsInfoButton.setVisible(!isDepthTest);

        //dataRangeInfoButton.removeActionListener(dataRangeInfoButton.getActionListeners()[0]);

        dataRangeLabel.setVisible(true);
        dataRange.setVisible(true);
        dataRangeInfoButton.setVisible(true);
    }

    private JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title));
        return panel;
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        return gbc;
    }

    // Add getter methods for the fields accessed by RunTestListener
    public JComboBox<TestCases> getTestCaseComboBox() {
        return testCaseComboBox;
    }

    public JTextField getPolyModulusDegreeField() {
        return polyModulusDegreeField;
    }

    public JTextField getPlainModulusField() {
        return plainModulusField;
    }

    public JTextField getCipherModulusField() {
        return cipherModulusField;
    }

    public JTextField getDataRange() {
        return dataRange;
    }

    public JComboBox<String> getOperationsComboBox() {
        return operationsComboBox;
    }

    public JTextField getNumIterationsField() {
        return numIterationsField;
    }

    public JTextArea getConsole() {
        return console;
    }

    private class InfoButtonListener implements ActionListener {
        private final String infoKey;
        private final JButton button;

        public InfoButtonListener(String infoKey, JButton button) {
            this.infoKey = infoKey;
            this.button = button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (infoPanel == null) {
                if (lastInfoButton != null && lastInfoButton != button) {
                    closeInfoPanel();
                }
                infoPanel = new InfoPanel(infoKey);
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(TestCasesPanel.this);
                JLayeredPane layeredPane = frame.getLayeredPane();
                Dimension size = infoPanel.getPreferredSize();
                Point location = button.getLocationOnScreen();
                SwingUtilities.convertPointFromScreen(location, layeredPane);

                if (infoKey.equals("Number of Iterations") || infoKey.equals("Operations")) {
                    // Display the info panel above the button
                    infoPanel.setBounds(location.x, location.y - size.height, size.width, size.height);
                } else {
                    // Display the info panel below the button
                    infoPanel.setBounds(location.x, location.y + button.getHeight(), size.width, size.height);
                }

                layeredPane.add(infoPanel, JLayeredPane.POPUP_LAYER);

                button.setText("⮞");
                lastInfoButton = button;

                // Add a mouse listener to the parent frame to detect clicks outside the popup
                frame.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        closeInfoPanel();
                    }
                });
            } else {
                closeInfoPanel();
            }
        }
    }

    private void closeInfoPanel() {
        if (infoPanel != null) {
            JLayeredPane layeredPane = (JLayeredPane) infoPanel.getParent();
            layeredPane.remove(infoPanel);
            infoPanel = null;
            if (lastInfoButton != null) {
                lastInfoButton.setText("⮟");
                lastInfoButton = null;
            }
            revalidate();
            repaint();
        }

        // Remove the mouse listener from the parent frame
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(TestCasesPanel.this);
        for (MouseListener ml : frame.getMouseListeners()) {
            frame.removeMouseListener(ml);
        }
    }

    private class InfoPanel extends JPanel {
        public InfoPanel(String infoKey) {
            setLayout(new BorderLayout());
            JLabel infoLabel = new JLabel(formatInfoText(infoMap.get(infoKey)));
            infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            add(infoLabel, BorderLayout.CENTER);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setBackground(Color.WHITE);
        }

        private String formatInfoText(String[] info) {
            StringBuilder formattedText = new StringBuilder("<html><span style='color:blue;'>").append(info[0]).append("</span><br>");
            for (int i = 1; i < info.length; i++) {
                String line = info[i];
                line = line.replaceAll("Constraints:", "<span style='color:red;'>Constraints:</span>");
                line = line.replaceAll("Examples:", "<span style='color:green;'>Examples:</span>");
                formattedText.append(line).append("<br>");
            }
            formattedText.append("</html>");
            return formattedText.toString();
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension preferredSize = super.getPreferredSize();
            return new Dimension(preferredSize.width + 40, preferredSize.height + 40); // Add padding
        }
    }
}
