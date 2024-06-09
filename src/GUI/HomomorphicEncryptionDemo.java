package GUI;

import GUI.test.tab.TestCasesPanel;
import GUI.use.tab.UseCasesPanel;

import javax.swing.*;

public class HomomorphicEncryptionDemo extends JFrame {

    public HomomorphicEncryptionDemo() {
        setTitle("JavaCrypt - Java implementation of BFV homomorphic cryptographic scheme");
        //setIconImage();
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Adding tabs
        tabbedPane.addTab("Test Cases", new TestCasesPanel());
        tabbedPane.setToolTipTextAt(0, "Run various test cases to evaluate encryption parameters");

        tabbedPane.addTab("Use Cases", new UseCasesPanel());
        tabbedPane.setToolTipTextAt(1, "Define and run custom use cases");

        add(tabbedPane);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HomomorphicEncryptionDemo::new);
    }
}

