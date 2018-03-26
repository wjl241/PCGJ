package test;

import java.awt.Dimension;  
import java.awt.event.MouseEvent;  
import java.awt.event.MouseListener;  
  
import javax.swing.JCheckBox;  
import javax.swing.JFrame;  
import javax.swing.JScrollPane;  
import javax.swing.JTable;  
import javax.swing.event.TableModelEvent;  
import javax.swing.table.DefaultTableModel;  
  
public class Tables implements MouseListener {  
    JTable table = new JTable();  
  
    public Tables() {  
        JFrame frame = new JFrame("sjh");  
        frame.setLayout(null);  
  
        table = this.gettable();  
        table.addMouseListener(this);  
        JScrollPane src = new JScrollPane(table);  
        src.setBounds(0, 0, 400, 200);  
        frame.setSize(new Dimension(400, 200));  
        frame.add(src);  
        frame.setVisible(true);  
    }  
  
    public JTable gettable() {  
        DefaultTableModel dm = new DefaultTableModel();  
        dm.setDataVector(new Object[][] {  
                { new JCheckBox("111"), new JCheckBox("111"),  
                        new JCheckBox("111"), new JCheckBox("111"),  
                        new JCheckBox("111"), new JCheckBox("111") },  
                { new JCheckBox("222"), new JCheckBox("222"),  
                        new JCheckBox("222"), new JCheckBox("222"),  
                        new JCheckBox("222"), new JCheckBox("222") },  
                { new JCheckBox("333"), new JCheckBox("333"),  
                        new JCheckBox("333"), new JCheckBox("333"),  
                        new JCheckBox("333"), new JCheckBox("333") }, },  
                new Object[] { "选择", "结果物", "说明", "类型", "发包要求文档", "操作" });  
  
        JTable table = new JTable(dm) {  
            public void tableChanged(TableModelEvent e) {  
                super.tableChanged(e);  
                repaint();  
            }  
        };  
        table.getColumn("选择").setCellEditor(  
                new CheckBoxEditor(new JCheckBox()));  
        table.getColumn("结果物").setCellEditor(  
                new CheckBoxEditor(new JCheckBox()));  
        table.getColumn("说明").setCellEditor(  
                new CheckBoxEditor(new JCheckBox()));  
        table.getColumn("类型").setCellEditor(  
                new CheckBoxEditor(new JCheckBox()));  
  
        table.getColumn("发包要求文档").setCellEditor(  
                new CheckBoxEditor(new JCheckBox()));  
        table.getColumn("操作").setCellEditor(  
                new CheckBoxEditor(new JCheckBox()));  
        // // table.getColumn("选择").setCellRenderer(new RadioButtonRenderer());  
        // table.getColumn("选择").setCellRenderer(new CheckBoxRenderer());  
        // table.setCellEditor(new CheckButtonEditor(new JCheckBox ()));  
        table.getColumn("选择").setCellRenderer(new CheckBoxRenderer());  
        table.getColumn("结果物").setCellRenderer(new CheckBoxRenderer());  
        table.getColumn("说明").setCellRenderer(new CheckBoxRenderer());  
        table.getColumn("类型").setCellRenderer(new CheckBoxRenderer());  
        table.getColumn("发包要求文档").setCellRenderer(new CheckBoxRenderer());  
        table.getColumn("操作").setCellRenderer(new CheckBoxRenderer());  
  
        return table;  
    }  
  
    public static void main(String args[]) {  
        new Tables();  
    }  
  
    public void mouseClicked(MouseEvent arg0) {  
    }  
  
    public void mouseEntered(MouseEvent arg0) {  
    }  
  
    public void mouseExited(MouseEvent arg0) {  
    }  
  
    public void mousePressed(MouseEvent arg0) {  
    }  
  
    public void mouseReleased(MouseEvent arg0) {  
    }  
}  
