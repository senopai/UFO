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
public class WarnaTablePenilaianLanjutanRisikoJatuhPsikiatri extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row % 2 == 1){
            component.setBackground(new Color(255,246,244));
            component.setForeground(new Color(50,50,50));
        }else{
            component.setBackground(new Color(255,255,255));
            component.setForeground(new Color(50,50,50));
        } 
        
        if ((column >= 6)&&(column <= 19)){
            try{
                double total = Double.parseDouble(table.getValueAt(row,18).toString().trim());
                if(total<90){
                    component.setBackground(Color.GREEN);
                    component.setForeground(new Color(50,50,50));
                }else if(total>=90){
                    component.setBackground(Color.RED);
                    component.setForeground(Color.WHITE);
                }
            }catch(Exception e){
                System.out.println("Notif WarnaTablePenilaianLanjutanRisikoJatuhPsikiatri : "+e);
            }
        }
           
        return component;
    }

}