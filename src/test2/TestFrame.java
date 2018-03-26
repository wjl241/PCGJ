package test2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TestFrame extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private JButton btnNewButton;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    TestFrame frame = new TestFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public TestFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("CheckBox Table");
        this.setPreferredSize(new Dimension(400, 300));
        // setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        btnNewButton = new JButton("New button");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
                  	int  coln=2;
                  	for(int i=0;i<table.getRowCount();i++){
                  	  Object  onecell= table.getValueAt(i,coln);
                  	  System.out.println(onecell);
                  	}
        	}
        });
        contentPane.add(btnNewButton, BorderLayout.NORTH);
        initTable();
        pack();
    }

    private void initTable() {
        String[] columnNames = {"name","age", "choose"};
        Object[][] data = this.getData();
        TableModelProxy tableModel = new TableModelProxy(columnNames, data);
        table.setModel(tableModel);
        table.getTableHeader().setDefaultRenderer(new CheckHeaderCellRenderer(table));
    }

    /**
     * 获得数据
     * 
     * @return
     */
    private Object[][] getData() {
        Object[][] data = {
                { "Kathy", 5, new Boolean(false) },
            { "John", 15, new Boolean(true) },
            { "Sue", 16, new Boolean(false) },
            { "Jane",17, new Boolean(true) },
            { "Joe", 18, new Boolean(false) } };
        return data;
    }

    
    private void getValue() {
    	int  coln=2;
    	for(int i=0;i<table.getRowCount();i++){
    	  Object  onecell= table.getValueAt(i,coln);
    	  System.out.println(onecell);
    	}
    }
}
