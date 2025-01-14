package uk.ac.ebi.pride.gui.component.table.renderer;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 17-Aug-2010
 * Time: 15:33:01
 */
public class RowNumberRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            label.setBackground(header.getBackground());
            label.setForeground(header.getForeground());
            label.setFont(header.getFont());
        }

        if (isSelected) {
            label.setFont(label.getFont().deriveFont(Font.BOLD));
        }

        label.setText((value == null) ? "" : value.toString());
        //label.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        return label;
    }
}
