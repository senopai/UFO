/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fungsi;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Owner
 */
public class WarnaTableKamarInap extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String status = table.getValueAt(row, 16).toString();

        if (status.equals("Rencana Pulang")) {
            component.setBackground(new Color(255, 51, 255)); // Kuning sebagai tanda rencana pulang
            component.setForeground(Color.BLACK);
        } else if (status.equals("-")) {
            component.setBackground(Color.WHITE); // Putih untuk pasien aktif biasa
            component.setForeground(Color.BLACK);
        }
        return component;
    }
}   