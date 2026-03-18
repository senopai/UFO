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
public class WarnaTableKasirRalan extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row % 2 == 1){
            component.setBackground(new Color(255,244,244));
            component.setForeground(new Color(50,50,50));
        }else{
            component.setBackground(new Color(255,255,255));
            component.setForeground(new Color(50,50,50));
        } 
        if(table.getValueAt(row,10).toString().equals("Sudah")){
            component.setBackground(new Color(252,37,126));
            component.setForeground(new Color(255,255,255));
        }else if(table.getValueAt(row,10).toString().equals("Batal")){
            component.setBackground(new Color(255,244,102));
            component.setForeground(new Color(50,50,50));
        }else if (table.getValueAt(row, 10).toString().equals("Berkas Diterima")) {
            component.setBackground(new Color(6, 180, 18));
            component.setForeground(new Color(50, 50, 50));
        }else if (table.getValueAt(row, 10).toString().equals("Kirim Berkas")) {
            component.setBackground(new Color(195, 155, 211));
            component.setForeground(new Color(50, 50, 50));
        }else if(table.getValueAt(row,10).toString().equals("Dirujuk")||table.getValueAt(row,10).toString().equals("Meninggal")||table.getValueAt(row,10).toString().equals("Pulang Paksa")){
            component.setBackground(new Color(0,128,128));
            component.setForeground(new Color(245,245,255));
        }else if(table.getValueAt(row,10).toString().equals("Dirawat")){
            component.setBackground(new Color(180,95,6));
            component.setForeground(new Color(255,255,255));
        }
        if(table.getValueAt(row,15).toString().equals("Sudah Bayar")){
            component.setBackground(new Color(68,68,68));
            component.setForeground(new Color(255,255,255));
        }
        return component;
    }

}
