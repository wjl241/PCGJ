/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weixin;

import Auto.ChangeShow;
import Auto.WeiXinAuto;
import RTPower.RTFile;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import panel.ColorScrollBarUI;
import panel.OneFriendPanel;

/**
 *
 * @author jerry
 */
public class AdminMain extends javax.swing.JFrame {

    /**
     * 所有微信账号实例的记录集
     */
    public static HashMap AllWeiXinAutos = new HashMap();

    /**
     * 记录了所有已经实例了的panel key(账号的第二面板)
     * <br>名称就是uin
     */
    public static HashMap AllPanels = new HashMap();

    /**
     * 所有管理员账号显示的panel列表(管理员第一面板)
     */
    public static HashMap AllAdminPanels = new HashMap();
    /**
     * 被选中的uid
     * <br>每次登陆都会更新为最新的uid
     * <br>点击后也会更新记录为被点击的uid
     */
    public static String SelectWeiXinUid = "";

    /**
     * 已经被选中的朋友panel
     */
    public static OneFriendPanel SelectFriendJpanel = null;

    /**
     * Creates new form AdminMain
     */
    public AdminMain() {
        initComponents();
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/images/weixin_logo.png"));
        this.setIconImage(icon.getImage());

        setSize(1072, 700);
        //设置居中
        this.setLocationRelativeTo(null);
        admin_list.getViewport().setOpaque(false);
        old_msg_list.getVerticalScrollBar().setUI(new ColorScrollBarUI(old_msg_list.getBackground(), Color.LIGHT_GRAY, 5));
        old_msg_list.getViewport().setOpaque(false);
        edit_spanel.getViewport().setOpaque(false);
        edit_spanel.getVerticalScrollBar().setUI(new ColorScrollBarUI(edit_spanel.getBackground(), Color.LIGHT_GRAY, 5));

        //监听关闭事件,关闭时候有账号,则保存账号的基本信息
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                //保存cookies 的所有信息
                SaveNowAdminInfo();
            }
        });

        //检查如果本地有配置文件,则进行一次读取文件的登录
        ScanLocalFileLogin();

        //值守显示新消息
        ChangeShow.ThreadStartRENewMsg();
    }

    /**
     * 保存当前的所有账号的cookies和D 还有头像 到本地文件
     */
    public void SaveNowAdminInfo() {

        //保存路径
        String PathString = "." + RTFile.FG + "config";
        //循环所有在的账号
        Iterator Iter = AllWeiXinAutos.entrySet().iterator();
        while (Iter.hasNext()) {
            Map.Entry next = (Map.Entry) Iter.next();
            String key = next.getKey().toString();
            WeiXinAuto one_weixin = (WeiXinAuto) next.getValue();
            //开始保存需要的信息
            String file_name = PathString + RTFile.FG + key + ".txt";
            //保存所有本账号的cookies
            HashMap SaveHash = new HashMap();
            //保存cookies
            SaveHash.put("Cookies", (HashMap) one_weixin.WXCookies);
            //保存did
            SaveHash.put("DeviceID", one_weixin.MyInfo.get("DeviceID").toString());

            try {
                //保存头像,base 加上 urlencode,使用的时候 解密一下
                SaveHash.put("UserFaceImage", URLEncoder.encode(one_weixin.MyInfo.get("UserFaceImage").toString(), "UTF-8"));

            } catch (UnsupportedEncodingException ex) {
                System.err.println("头像字符串encode出错");
            }

            RTFile.WriteJsonFile(file_name, SaveHash);

        }
    }

    /**
     * 扫描本地文件夹内是否有配置文件,有则文件登录
     */
    public void ScanLocalFileLogin() {
//没有目录自动创建
        String file_path = "." + RTFile.FG + "config";
        RTFile.CreateDirectory(file_path);

        //遍历目录下的文件
        File root = new File(file_path);
        File[] files = root.listFiles();
 
        //文件夹内有文件则读取,没有则弹出扫码框
        if (files.length > 0) {
            new Thread(() -> {
                for (File file : files) {
                    if (file.isFile()) {
                        String use_file_path = file.getPath();
                        //确定是txt文件
                        if (use_file_path.contains(".txt")) {
                            //读取文件内容为json
                            HashMap admin_cookies = (HashMap) RTFile.ReadJsonFileKeyToOther(use_file_path, "Cookies", HashMap.class);
                            String DeviceID = (String) RTFile.ReadJsonFileKey(use_file_path, "DeviceID");

                            String UserFaceImage = (String) RTFile.ReadJsonFileKey(use_file_path, "UserFaceImage");
                            try {
                                UserFaceImage = URLDecoder.decode(UserFaceImage, "UTF-8");
                            } catch (UnsupportedEncodingException ex) {
                                System.err.println("头像decoder出错");
                            }

                            //建立新自动实例,并进行登录
                            WeiXinAuto one_weixin_auto = new WeiXinAuto();
                            one_weixin_auto.ScanFileAdminLogin(admin_cookies, DeviceID, UserFaceImage);

                        }
                    }

                }
            }).start();
        } else {
            ClickGetImage();
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_user = new javax.swing.JPanel();
        new_user_login_button = new javax.swing.JLabel();
        admin_list = new javax.swing.JScrollPane();
        panel_friends = new javax.swing.JPanel();
        panel_msg = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        friend_name = new javax.swing.JLabel();
        push_msg = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        old_msg_list = new javax.swing.JScrollPane();
        all_msg_panel = new javax.swing.JPanel();
        edit_spanel = new javax.swing.JScrollPane();
        edit_msg = new javax.swing.JTextArea();
        panel_fast = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("群发测试版");
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setLocation(new java.awt.Point(0, 0));
        setPreferredSize(new java.awt.Dimension(1072, 670));
        setSize(new java.awt.Dimension(0, 670));

        panel_user.setBackground(new java.awt.Color(38, 41, 46));
        panel_user.setPreferredSize(new java.awt.Dimension(55, 670));

        new_user_login_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add_user.png"))); // NOI18N
        new_user_login_button.setToolTipText("点击进行新用户登录!");
        new_user_login_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new_user_login_buttonMouseClicked(evt);
            }
        });

        admin_list.setBackground(new java.awt.Color(38, 41, 46));
        admin_list.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        admin_list.setOpaque(false);

        javax.swing.GroupLayout panel_userLayout = new javax.swing.GroupLayout(panel_user);
        panel_user.setLayout(panel_userLayout);
        panel_userLayout.setHorizontalGroup(
            panel_userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_userLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(new_user_login_button)
                .addContainerGap(8, Short.MAX_VALUE))
            .addComponent(admin_list, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        panel_userLayout.setVerticalGroup(
            panel_userLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_userLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(admin_list, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(new_user_login_button)
                .addGap(10, 10, 10))
        );

        panel_friends.setBackground(new java.awt.Color(46, 50, 56));

        panel_msg.setPreferredSize(new java.awt.Dimension(567, 670));
        panel_msg.setSize(new java.awt.Dimension(0, 670));

        jSeparator1.setEnabled(false);

        friend_name.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        friend_name.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        friend_name.setText("未选择朋友");

        push_msg.setBackground(new Color(233,233,233,0));
        push_msg.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        push_msg.setForeground(new java.awt.Color(255, 255, 255));
        push_msg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/button_black.png"))); // NOI18N
        push_msg.setText("发送");
        push_msg.setBorder(null);
        push_msg.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        push_msg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                push_msgActionPerformed(evt);
            }
        });

        jSeparator2.setEnabled(false);

        old_msg_list.setBorder(null);
        old_msg_list.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        old_msg_list.setOpaque(false);

        all_msg_panel.setOpaque(false);
        all_msg_panel.setPreferredSize(new java.awt.Dimension(566, 0));
        all_msg_panel.setSize(new java.awt.Dimension(566, 0));

        javax.swing.GroupLayout all_msg_panelLayout = new javax.swing.GroupLayout(all_msg_panel);
        all_msg_panel.setLayout(all_msg_panelLayout);
        all_msg_panelLayout.setHorizontalGroup(
            all_msg_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 566, Short.MAX_VALUE)
        );
        all_msg_panelLayout.setVerticalGroup(
            all_msg_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 350, Short.MAX_VALUE)
        );

        old_msg_list.setViewportView(all_msg_panel);

        edit_spanel.setBorder(null);
        edit_spanel.setOpaque(false);

        edit_msg.setColumns(20);
        edit_msg.setLineWrap(true);
        edit_msg.setRows(5);
        edit_msg.setWrapStyleWord(true);
        edit_msg.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        edit_msg.setOpaque(false);
        edit_spanel.setViewportView(edit_msg);

        javax.swing.GroupLayout panel_msgLayout = new javax.swing.GroupLayout(panel_msg);
        panel_msg.setLayout(panel_msgLayout);
        panel_msgLayout.setHorizontalGroup(
            panel_msgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(friend_name, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator2)
            .addGroup(panel_msgLayout.createSequentialGroup()
                .addComponent(old_msg_list)
                .addGap(1, 1, 1))
            .addComponent(edit_spanel)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_msgLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(push_msg)
                .addContainerGap())
        );
        panel_msgLayout.setVerticalGroup(
            panel_msgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_msgLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(friend_name, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(old_msg_list, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(edit_spanel, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 21, Short.MAX_VALUE)
                .addComponent(push_msg)
                .addGap(10, 10, 10))
        );

        panel_fast.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setOpaque(false);
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel1MouseClicked(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/all_send.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel2.setText("消息群发");

        jLabel3.setForeground(new java.awt.Color(102, 102, 102));
        jLabel3.setText("<html>对所有好友和群进行<br>选择性消息群发</html>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)))
                .addGap(7, 7, 7))
        );

        javax.swing.GroupLayout panel_fastLayout = new javax.swing.GroupLayout(panel_fast);
        panel_fast.setLayout(panel_fastLayout);
        panel_fastLayout.setHorizontalGroup(
            panel_fastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panel_fastLayout.setVerticalGroup(
            panel_fastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_fastLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panel_friends, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panel_msg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(panel_fast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_user, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_friends, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_msg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_fast, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * 点击添加微信账号时候的事件
     * <br>点击后实例化一个新的自动类
     * <br>登录成功后才进行全局记录,打开验证码的窗口,关闭后,没有登录则销毁实例
     *
     * @param evt
     */
    private void new_user_login_buttonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_new_user_login_buttonMouseClicked

        ClickGetImage();

    }//GEN-LAST:event_new_user_login_buttonMouseClicked

    /**
     * 建立实例获得二维码
     */
    public void ClickGetImage() {
        //实例化一个
        WeiXinAuto one_weixin = new WeiXinAuto();

        //获取图片,并传递给登录界面的图片显示
        byte[] get_image = one_weixin.GetImage();
        //打开登录界面
        AllLoginMain login_main = new AllLoginMain();
        login_main.setVisible(true);
        //赋予二维码显示
        login_main.erweima_image.setIcon(new ImageIcon(one_weixin.SetImageSize(new ImageIcon(get_image), 200, 200)));
        //循环获取扫码状态
       // one_weixin.ScanLoginStatus(login_main);
        //监听关闭事件,关闭后终止进程
        login_main.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                //如果关闭的时候循环扫描线程还在,就先终止线程
                if (one_weixin.login_scan_status) {
                    one_weixin.login_scan_status = false;
                }
            }

        });
    }

    /**
     * 点击发送消息的事件
     *
     * @param evt
     */
    private void push_msgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_push_msgActionPerformed
        //空内容不发送
        String send_msg = edit_msg.getText();
        if (send_msg.equals("")) {
            System.err.println("空消息,不进行发送");
            return;
        }
        //非空则开始发送消息,
        //调用当前选择的微信id 调用发送方法
        String use_wxuid = SelectWeiXinUid;//获得微信id
        //朋友名称
        String friend_user_name = push_msg.getActionCommand();

        if (friend_user_name.equals("")) {
            return;
        }
        //获得实例
        WeiXinAuto use_weixin_auto = (WeiXinAuto) AdminMain.AllWeiXinAutos.get(use_wxuid);
        //调用发送消息
        //TODO 回头网这搞
        //boolean send_status = use_weixin_auto.SendImgToFriend(friend_user_name, send_msg);
        boolean send_status = use_weixin_auto.SendMsgToFriend(friend_user_name, send_msg);
        //发送成功和失败的界面处理
        //群发的时候是没有界面处理的,所以仅仅针对手动发送给出界面处理
        if (send_status) {
            //如果成功了,就清除编辑框,并在消息栏目里显示消息
            edit_msg.setText("");
            //在界面添加消息框

            //把消息记录在用户的消息记录中,用一个循环线程,来不停的刷新当前界面选择的朋友用户名的消息记录并显示
            //这里仅仅显示一条
            ChangeShow.ShowOneMsg(use_wxuid, friend_user_name, "me", send_msg);
        } else {

        }
    }//GEN-LAST:event_push_msgActionPerformed

    /**
     * 点击群发按钮事件
     *
     * @param evt
     */
    private void jPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseClicked
        //弹出群发窗体
        //如果没有登录账号,则这个页面就提示并关闭
        if (AllWeiXinAutos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "您没有登录任何的微信账号,不能群发,请登录账号后再进入此页面.");
            return;
        }
        AllSendMain all_send_main = new AllSendMain();
        all_send_main.setVisible(true);
    }//GEN-LAST:event_jPanel1MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new AdminMain().setVisible(true);
//            }
//        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JScrollPane admin_list;
    public static javax.swing.JPanel all_msg_panel;
    public javax.swing.JTextArea edit_msg;
    private javax.swing.JScrollPane edit_spanel;
    public static javax.swing.JLabel friend_name;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel new_user_login_button;
    public static javax.swing.JScrollPane old_msg_list;
    private javax.swing.JPanel panel_fast;
    public static javax.swing.JPanel panel_friends;
    private javax.swing.JPanel panel_msg;
    public javax.swing.JPanel panel_user;
    public static javax.swing.JButton push_msg;
    // End of variables declaration//GEN-END:variables
}
