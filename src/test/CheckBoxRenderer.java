package test;

import java.awt.Component;  
import javax.swing.JTable;  
import javax.swing.table.TableCellRenderer;  
  
class CheckBoxRenderer implements TableCellRenderer {  
  
    public Component getTableCellRendererComponent(JTable table, Object value,  
            boolean isSelected, boolean hasFocus, int row, int column) {  
        if (value == null)  
            return null;  
        System.err.println("row:"+row);
        System.err.println("column:"+column);
        System.err.println("value:"+value);
        System.err.println("isSelected:"+isSelected);
        System.err.println(hasFocus);
  
        return (Component) value;  
    }  
}  