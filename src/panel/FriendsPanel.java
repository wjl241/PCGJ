/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panel;

import Auto.ChangeShow;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import javax.swing.JLabel;
import weixin.AdminMain;

/**
 *
 * @author jerry
 */
public class FriendsPanel extends javax.swing.JPanel {

    /**
     * 当前账号下的所有朋友的panel集合
     */
    public HashMap one_admin_all_friends_panel = new HashMap();

    /**
     * 当前选中的要显示的朋友类型
     * <br>
     */
    public JLabel SelectJLabel;

    /**
     * Creates new form FriendsPanel
     */
    public FriendsPanel() {

        initComponents();
        friends_list.getVerticalScrollBar().setUI(new ColorScrollBarUI(friends_list.getBackground(), Color.BLACK, 5));
        friends_list.getViewport().setOpaque(false);
        SelectJLabel = select_friend;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        my_nick_name = new javax.swing.JLabel();
        out_login = new javax.swing.JButton();
        search_friend = new javax.swing.JTextField();
        friends_list = new javax.swing.JScrollPane();
        show_friends_list = new javax.swing.JPanel();
        select_type = new javax.swing.JPanel();
        select_new_msg = new javax.swing.JLabel();
        select_friend = new javax.swing.JLabel();
        select_group = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        setBackground(new java.awt.Color(46, 50, 56));
        setMinimumSize(new java.awt.Dimension(237, 651));
        setPreferredSize(new java.awt.Dimension(237, 651));
        setSize(new java.awt.Dimension(237, 651));

        my_nick_name.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        my_nick_name.setForeground(new java.awt.Color(255, 255, 255));
        my_nick_name.setText("NickName");

        out_login.setBackground(new java.awt.Color(46, 50, 56));
        out_login.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/out.png"))); // NOI18N
        out_login.setToolTipText("点击退出账号!");
        out_login.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        out_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                out_loginActionPerformed(evt);
            }
        });

        search_friend.setBackground(new java.awt.Color(0, 0, 0));
        search_friend.setForeground(new java.awt.Color(204, 204, 204));
        search_friend.setText("搜索");
        search_friend.setToolTipText("输入关键字后点击回车!进行搜索");
        search_friend.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 5, true));
        search_friend.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                search_friendFocusGained(evt);
            }
        });
        search_friend.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                search_friendKeyPressed(evt);
            }
        });

        friends_list.setBackground(new java.awt.Color(46, 50, 56));
        friends_list.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        friends_list.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        friends_list.setOpaque(false);
        friends_list.setPreferredSize(new java.awt.Dimension(0, 0));

        show_friends_list.setOpaque(false);
        show_friends_list.setPreferredSize(new java.awt.Dimension(237, 500));

        javax.swing.GroupLayout show_friends_listLayout = new javax.swing.GroupLayout(show_friends_list);
        show_friends_list.setLayout(show_friends_listLayout);
        show_friends_listLayout.setHorizontalGroup(
            show_friends_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 237, Short.MAX_VALUE)
        );
        show_friends_listLayout.setVerticalGroup(
            show_friends_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 530, Short.MAX_VALUE)
        );

        friends_list.setViewportView(show_friends_list);

        select_type.setBackground(new java.awt.Color(46, 50, 56));

        select_new_msg.setBackground(new java.awt.Color(46, 50, 56));
        select_new_msg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        select_new_msg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_new_msg.png"))); // NOI18N
        select_new_msg.setToolTipText("点击查看新消息列表!");
        select_new_msg.setName("last"); // NOI18N
        select_new_msg.setOpaque(true);
        select_new_msg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                select_MouseClicked(evt);
            }
        });

        select_friend.setBackground(new java.awt.Color(0, 0, 0));
        select_friend.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        select_friend.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_friend.png"))); // NOI18N
        select_friend.setToolTipText("点击查看所有好友!");
        select_friend.setName("friends"); // NOI18N
        select_friend.setOpaque(true);
        select_friend.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                select_MouseClicked(evt);
            }
        });

        select_group.setBackground(new java.awt.Color(46, 50, 56));
        select_group.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        select_group.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_friends.png"))); // NOI18N
        select_group.setToolTipText("点击查看所有群聊!");
        select_group.setName("group"); // NOI18N
        select_group.setOpaque(true);
        select_group.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                select_MouseClicked(evt);
            }
        });

        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout select_typeLayout = new javax.swing.GroupLayout(select_type);
        select_type.setLayout(select_typeLayout);
        select_typeLayout.setHorizontalGroup(
            select_typeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(select_typeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(select_new_msg, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(select_friend, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(select_group)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        select_typeLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {select_friend, select_group, select_new_msg});

        select_typeLayout.setVerticalGroup(
            select_typeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(select_typeLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(select_typeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(select_new_msg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(select_typeLayout.createSequentialGroup()
                        .addGroup(select_typeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addComponent(select_friend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(select_group, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(friends_list, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(my_nick_name)
                .addGap(93, 93, 93)
                .addComponent(out_login, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(select_type, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(search_friend)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(out_login, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(my_nick_name, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addComponent(search_friend, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(select_type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(friends_list, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleParent(this);
    }// </editor-fold>//GEN-END:initComponents
/**
     * 点击朋友列表后的退出按钮,对当前账号进行退出
     *
     * @param evt
     */
    private void out_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_out_loginActionPerformed

        String WXUid = evt.getActionCommand();
        ChangeShow.CloseOneWxAdmin(WXUid);
        //还要清理消息框内容
        AdminMain.all_msg_panel.removeAll();
        AdminMain.all_msg_panel.repaint();
        AdminMain.push_msg.setActionCommand("");
        AdminMain.friend_name.setText("未选择好友");
    }//GEN-LAST:event_out_loginActionPerformed

    /**
     * 选择显示用户类型的图标事件
     *
     * @param evt
     */
    private void select_MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_select_MouseClicked
        String WXUid = out_login.getActionCommand();
        String type_name = evt.getComponent().getName();
        //一样就不刷新
        if (SelectJLabel.getName().equals(type_name)) {
            return;
        }
        //不一样就改变老的颜色
        SelectJLabel.setBackground(new Color(46, 50, 56));
        //再改变新的颜色
        evt.getComponent().setBackground(new Color(0, 0, 0));
        //开始刷新
        ChangeShow.REShowSelectTypeFriends(WXUid, type_name);
        //最后改变控件
        SelectJLabel = (JLabel) evt.getComponent();

    }//GEN-LAST:event_select_MouseClicked

    /**
     * 搜索框获得焦点事件
     *
     * @param evt
     */
    private void search_friendFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_search_friendFocusGained
        String text_string = search_friend.getText();
        if (text_string.equals("搜索")) {
            search_friend.setText("");
        }
    }//GEN-LAST:event_search_friendFocusGained

    /**
     * 搜索框对 回车键的响应
     *
     * @param evt
     */
    private void search_friendKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_search_friendKeyPressed
        String WXUid = out_login.getActionCommand();
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            System.err.println("提交的搜索内容是:" + search_friend.getText());
            ChangeShow.REShowSelectTypeFriends(WXUid, "search");
        }
    }//GEN-LAST:event_search_friendKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JScrollPane friends_list;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    public javax.swing.JLabel my_nick_name;
    public javax.swing.JButton out_login;
    public javax.swing.JTextField search_friend;
    private javax.swing.JLabel select_friend;
    private javax.swing.JLabel select_group;
    private javax.swing.JLabel select_new_msg;
    private javax.swing.JPanel select_type;
    public javax.swing.JPanel show_friends_list;
    // End of variables declaration//GEN-END:variables
}