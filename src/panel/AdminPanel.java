/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panel;

import Auto.ChangeShow;

/**
 *
 * @author jerry
 */
public class AdminPanel extends javax.swing.JPanel {

    /**
     * Creates new form AdminPanel
     */
    public AdminPanel() {
        initComponents();
        msg_count.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        msg_count = new javax.swing.JLabel();
        face_image_button = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(55, 55));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(55, 55));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        msg_count.setBackground(new java.awt.Color(255, 0, 0));
        msg_count.setForeground(new java.awt.Color(255, 255, 0));
        msg_count.setText("1");
        msg_count.setOpaque(true);
        add(msg_count, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 10, 10));

        face_image_button.setBorder(null);
        face_image_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                face_image_buttonActionPerformed(evt);
            }
        });
        add(face_image_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 7, 40, 40));
    }// </editor-fold>//GEN-END:initComponents

    /**
     * 微信账号点击头像后的操作
     *
     * @param evt
     */
    private void face_image_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_face_image_buttonActionPerformed
        String WxUid = evt.getActionCommand();
        ChangeShow.RESelectAdminUid(WxUid);
    }//GEN-LAST:event_face_image_buttonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton face_image_button;
    public javax.swing.JLabel msg_count;
    // End of variables declaration//GEN-END:variables
}