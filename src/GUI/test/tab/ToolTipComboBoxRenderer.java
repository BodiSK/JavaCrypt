package GUI.test.tab;

import javax.swing.*;
import java.awt.*;

public class ToolTipComboBoxRenderer extends DefaultListCellRenderer {
    private String[] tooltips;

    public ToolTipComboBoxRenderer(String[] tooltips) {
        this.tooltips = tooltips;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (-1 < index && null != value && tooltips != null && index < tooltips.length) {
            list.setToolTipText(tooltips[index]);
        } else {
            list.setToolTipText(null); // No tooltip
        }
        return c;
    }
}

