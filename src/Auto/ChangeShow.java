/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Auto;

import RTPower.RTFile;
import RTPower.RTHttp;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import panel.AdminPanel;
import panel.FriendMsgBoxPanel;
import panel.FriendsPanel;
import panel.MeMsgBoxPanel;
import panel.OneFriendPanel;
import weixin.AdminMain;
import weixin.QFMain;

import static weixin.AdminMain.old_msg_list;

/**
 * 改变显示类
 *
 * @author jerry
 */
public class ChangeShow {

    /**
     * 添加一个新的登录的管理账号(微信)
     *
     * @param AddWXUid 这个是自动类里的cookies记录的wxuin 代表微信的ID
     */
    public static void AddNewAdminFace(String AddWXUid) {

        //如果账号列表是空,则不添加
        if (AdminMain.AllWeiXinAutos.isEmpty()) {
            return;
        }

        //获得所有微信的总数量,而这个要加入的就是这个总数量的位置
        int all_count = AdminMain.AllWeiXinAutos.size() - 1;
        //把要加入的新的微信显示在置顶的位置
        //先得到要加入的微信的实例
        WeiXinAuto add_wx_auto = (WeiXinAuto) AdminMain.AllWeiXinAutos.get(AddWXUid);
        //获得头像数据,以及昵称
        JsonObject add_wx_user = (JsonObject) add_wx_auto.MyInfo.get("User");
        String add_face_image = add_wx_auto.MyInfo.get("UserFaceImage").toString();
        String add_nick_name = add_wx_user.get("NickName").getAsString();
        //头像大小
        byte[] use_face_byte = Base64.getDecoder().decode(add_face_image);
        add_wx_auto.MyFaceImage = add_wx_auto.SetImageSize(use_face_byte, 40, 40);
        //记录下缩小的头像供以后使用

        //开始加入到界面
        AdminPanel add_panel = new AdminPanel();
        //加入头像背景
        add_panel.face_image_button.setIcon(new ImageIcon(add_wx_auto.MyFaceImage));
        //加入用户名显示
        add_panel.face_image_button.setToolTipText(add_nick_name);
        //加入这个用户的weixinid,用来点击后的事件
        add_panel.face_image_button.setActionCommand(AddWXUid);
        //设置显示位置
        add_panel.setBounds(0, all_count * 50, 55, 55);
        
      
        //显示到界面
        AdminMain.admin_list.add(add_panel);
        AdminMain.admin_list.updateUI();

        //放入全局方便以后管理
        AdminMain.AllAdminPanels.put(AddWXUid, add_panel);
        //加入完毕账号显示,立即刷新出好友列表
        //这里由于是add 所以要先new 一个friendslist 的panel 到全局变量,方便控制
        AddFriendsListPanel(AddWXUid, add_wx_user);
        //更新uid记录

        RESelectAdminUid(AddWXUid);
        //完成登陆显示
    }

    /**
     * 增加一个friendspanel 并显示在面板上
     * <br>默认为显示状态
     *
     * @param AddWXUid
     * @param add_wx_user
     */
    public static void AddFriendsListPanel(String AddWXUid, JsonObject add_wx_user) {
        //创建一个新实例
        FriendsPanel add_friends_panel = new FriendsPanel();
        add_friends_panel.setBounds(0, 0, 237, 0);
        //设置用户名
        add_friends_panel.my_nick_name.setText(add_wx_user.get("NickName").getAsString());
        //设置按钮的uin
        add_friends_panel.out_login.setActionCommand(AddWXUid);
        //放入主界面的面板内
        AdminMain.panel_friends.add(add_friends_panel);
        AdminMain.panel_friends.updateUI();//刷新一下
        //放入全局方便以后控制
        AdminMain.AllPanels.put(AddWXUid, add_friends_panel);

        //刷新此用户下的所有朋友显示在面板
//        ShowAllFriendsList(AddWXUid);
    }

    /**
     * 根据uin 刷新出下面的所有好友列表在独立的friendpanel里面
     * <br>如果不存在则不刷新
     * <br>这里使用一个线程来刷新,不然主线程卡死
     *
     * @param AddWXUid
     */
    public static void ShowAllFriendsList(String AddWXUid) {
        //如果账号列表是空,则不添加
        if (AdminMain.AllWeiXinAutos.isEmpty()) {
            return;
        }

        //面板是空的页不执行
        if (AdminMain.AllPanels.isEmpty()) {
            return;
        }

        new Thread(() -> {
            int all_count = 0;
            //账号存在则强制刷新一次好友列表
            //先得到要加入的微信的实例
            WeiXinAuto add_wx_auto = (WeiXinAuto) AdminMain.AllWeiXinAutos.get(AddWXUid);
            FriendsPanel use_panel = (FriendsPanel) AdminMain.AllPanels.get(AddWXUid);

            //先清空之前的所有朋友在界面上行的控件
            use_panel.show_friends_list.removeAll();
            //获得好友信息,循环刷新进面板
            JsonArray admin_all_friends = add_wx_auto.AllfriendsList;
            for (int i = 0; i < admin_all_friends.size(); i++) {
                JsonObject one_info = (JsonObject) admin_all_friends.get(i);
                String user_name = one_info.get("UserName").getAsString();
                //存在控件则跳过
                if (use_panel.one_admin_all_friends_panel.containsKey(user_name)) {
                    continue;
                }
                //建立新的朋友panel
                OneFriendPanel one_panel = new OneFriendPanel(AddWXUid);

                //显示昵称
                one_panel.friend_nick_name.setText(deleteAllHTMLTag(one_info.get("NickName").getAsString()));
                //显示头像
                String face_image_url = "https://wx2.qq.com" + one_info.get("HeadImgUrl").getAsString();
                //设置username
                one_panel.friend_username.setText(user_name);

                ImageIcon use_face_imageIcon = FindFriendFaceImage(
                        one_info.get("NickName").getAsString(),
                        face_image_url,
                        add_wx_auto.WXCookies);
                one_panel.friend_face_image.setIcon(use_face_imageIcon);
                one_panel.setBounds(0, all_count * 55, 237, 55);
                use_panel.show_friends_list.add(one_panel);

                //这里进行每20个好友就刷新一下界面,不然就会一直等到几百个好友搞完才能滚动
                if (all_count % 10 == 0) {
                    //变更show的大小
                    use_panel.show_friends_list.setPreferredSize(new Dimension(0, all_count * 55));
                    use_panel.friends_list.repaint();
                }
                all_count++;
                //这里把一个好友的面板放入 所有朋友hash记录里,用username来做名称
                use_panel.one_admin_all_friends_panel.put(user_name, one_panel);

            }
            //变更show的大小
            use_panel.show_friends_list.setPreferredSize(new Dimension(0, all_count * 55));
//        use_panel.friends_list.updateUI();
            use_panel.friends_list.repaint();

        }).start();

    }

    /**
     * 刷新界面的被选中的微信账号效果
     * <br>被选中的会显示面板,没有被选中的,全部隐藏面板
     * <br>刚登陆的默认为最新选中的
     *
     * @param WXUid
     */
    public static void RESelectAdminUid(String WXUid) {
        AdminMain.SelectWeiXinUid = WXUid;
        //循环所有的Allpanel面板,根据选中值来决定是显示还是不显示
        HashMap use_all_panel = AdminMain.AllPanels;
        Iterator iter = use_all_panel.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();
            FriendsPanel one_jpanel = (FriendsPanel) entry.getValue();
            //等于则显示 否则全部隐藏
            if (key.equals(WXUid)) {
                one_jpanel.setVisible(true);
                //改变背景色
            } else {
                one_jpanel.setVisible(false);
            }

        }
    }

    /**
     * 重刷所有界面账号显示
     */
    public static void REAllAdminFace() {
        HashMap admin_all_panel = AdminMain.AllAdminPanels;
        Iterator iter = admin_all_panel.entrySet().iterator();
        int count = 0;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();
            AdminPanel one_jpanel = (AdminPanel) entry.getValue();
            //重新刷新位置
            one_jpanel.setBounds(0, count * 50, 55, 55);
        }
        AdminMain.admin_list.repaint();
    }

    /**
     * 传入URL头像地址,寻找本地的头像,如果本地没有则在网络上读取一次下载到本地
     *
     * @param UserNameid 要读取的本地文件名,也是朋友的id
     * @param UrlString 网络图片地址
     * @param use_cookies 如果要网络读取的话,要使用的cookies
     * @return
     */
    public static ImageIcon FindFriendFaceImage(String UserNameid, String UrlString, HashMap use_cookies) {
        //对名称进行base64
        String en_UserNameid = UserNameid;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            en_UserNameid = Base64.getEncoder().encodeToString(md5.digest(UserNameid.getBytes("utf-8")));
            en_UserNameid = URLEncoder.encode(en_UserNameid, "UTF-8");
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Md5加密错误");
        } catch (UnsupportedEncodingException ex) {
            System.err.println("MD5加密错误");
        }

        String FG = System.getProperty("file.separator");
        ImageIcon use_image;
        BufferedInputStream in;
        String file_path = "." + FG + "images" + FG + "face_images" + FG + en_UserNameid + ".jpg";
        try {
            //读取图片文件
            in = new BufferedInputStream(new FileInputStream(file_path));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int size = 0;
            byte[] temp = new byte[1024];
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            in.close();
            byte[] use_image_byte = out.toByteArray();
            //完成 返回图片
            use_image = new ImageIcon(use_image_byte);

        } catch (IOException ex) {
            //出错则读取网络图片一次病写出到本地
            System.err.println(ex.toString());
//            System.err.println("读取网络图片``````"+UserNameid);
            RTHttp rhttp = new RTHttp(UrlString);
            rhttp.SetCookies(rhttp.MakeCookies(use_cookies));
            byte[] back_image_byte = rhttp.GetImage();
            rhttp.close();

            //把byte转Image
            Image new_image = new ImageIcon(back_image_byte).getImage();
            //改变的大小后的图片
            Image change_image = new_image.getScaledInstance(40, 40, Image.SCALE_FAST);

            use_image = new ImageIcon(change_image);
//准备容器
            BufferedImage desc = new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);  //缩放图像    
            Graphics gg = desc.getGraphics();
            gg.drawImage(change_image, 0, 0, null);
            gg.dispose();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                ImageIO.write(desc, "JPEG", bos);
                SaveImageToFile(file_path, bos.toByteArray());

            } catch (IOException ex1) {
                Logger.getLogger(ChangeShow.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return use_image;
    }

    /**
     * 图片保存到本地
     *
     * @param save_path
     * @param image_byte
     */
    public static void SaveImageToFile(String save_path, byte[] image_byte) {
        try {
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(save_path));//打开输入流
            imageOutput.write(image_byte, 0, image_byte.length);//将byte写入硬盘
            imageOutput.close();

        } catch (IOException ex) {
            System.err.println("保存图片到文件失败");
        }

    }

    /**
     * 根据UID来关闭一个微信的,显示界面以及全局变量
     *
     * @param WXUid
     */
    public static void CloseOneWxAdmin(String WXUid) {
        //先终止线程
        WeiXinAuto use_auto = (WeiXinAuto) AdminMain.AllWeiXinAutos.get(WXUid);
     
        //干掉当前的朋友列表界面面板
        AdminMain.panel_friends.remove((JPanel) AdminMain.AllPanels.get(WXUid));
        //干掉管理员账号列表里的头像面板
        AdminMain.admin_list.remove((JPanel) AdminMain.AllAdminPanels.get(WXUid));
        //移除全局变量里记录的
        AdminMain.AllPanels.remove(WXUid);
        AdminMain.AllWeiXinAutos.remove(WXUid);
        AdminMain.AllAdminPanels.remove(WXUid);

        //刷新界面ui显示
        AdminMain.panel_friends.repaint();
        REAllAdminFace();

        //退出删除配置文件
        String file_path = "." + RTFile.FG + "config" + RTFile.FG + WXUid + ".txt";
        File file = new File(file_path);
        boolean del_status = file.delete();
        System.err.println("退出完毕!");
    }

    /**
     *  显示一条消息到面板
     *
     * @param WXUID 哪一个微信
     * @param to_username 跟谁之间的消息
     * @param who_send_msg 谁的消息,消息主人 me|friend
     * @param send_msg_text 消息内容
     */
    public static void ShowOneMsg(String WXUID, String to_username, String who_send_msg, String send_msg_text) {

//获得实例
        WeiXinAuto use_auto = (WeiXinAuto) AdminMain.AllWeiXinAutos.get(WXUID);
        //判断当聊天框的账号是否是对应的用户
        if (!to_username.equals(AdminMain.push_msg.getActionCommand())) {
            return;
        }
        //是当前用户,则开始进行显示
        //检查是me 还是friend 来决定实例那个面板
        //采用动态加载
        //获得消息总面板内最后一个控件的位置
        JPanel all_msg_panel = AdminMain.all_msg_panel;
        int count = all_msg_panel.getComponentCount();
        float use_new_y = 0;
        if (count > 0) {
            Component find_comp = all_msg_panel.getComponent(count - 1);
            use_new_y = find_comp.getY() + find_comp.getHeight();
        }

        JPanel use_panel;
        int height_size = 50;
        if (who_send_msg.equals("me")) {
            //根据传入的加载对应的类文件
//            自己的头像
            MeMsgBoxPanel box_panel = new MeMsgBoxPanel(send_msg_text, use_auto.MyFaceImage);
            height_size = box_panel.GetMsgSize();
            use_panel = box_panel;
        } else {
            JLabel friend_image = AdminMain.SelectFriendJpanel.friend_face_image;
            ImageIcon icon = (ImageIcon) friend_image.getIcon();
            Image imageicon = icon.getImage();
            FriendMsgBoxPanel box_panel = new FriendMsgBoxPanel(send_msg_text, imageicon);
            height_size = box_panel.GetMsgSize();
            use_panel = box_panel;
        }

        if (height_size < 50) {
            height_size = 50;
        }
//        System.err.println("聊天框高度:" + height_size);
        //显示的大小和位置
        use_panel.setBounds(0, (int) use_new_y, AdminMain.all_msg_panel.getWidth(), height_size);
        //加上内容和头像

        //面板上显示
        all_msg_panel.add(use_panel);
        //改变面板大小
        all_msg_panel.setPreferredSize(new Dimension(0, (int) use_new_y + use_panel.getHeight()));

        all_msg_panel.updateUI();
        all_msg_panel.repaint();

        //改变bar的滚动位置
        JScrollBar sbar = old_msg_list.getVerticalScrollBar();
        sbar.setValue(sbar.getMaximum());
    }

    /**
     * 清理聊天框消息记录显示一次
     * <br>并初始化显示老的消息
     *
     * @param WXUID
     * @param to_username
     */
    public static void ClearAndFormatMsgshow(String WXUID, String to_username) {
        AdminMain.all_msg_panel.removeAll();//删除所有消息
        //初始化一下消息框的大小
        AdminMain.all_msg_panel.setPreferredSize(new Dimension(0, 0));
        AdminMain.all_msg_panel.updateUI();
        AdminMain.all_msg_panel.repaint();

        new Thread(() -> {
            //整理i完毕,刷新一次老的消息
            //1读取当前用户的所有老的消息记录,循环显示在界面
            WeiXinAuto weixin_auto = (WeiXinAuto) AdminMain.AllWeiXinAutos.get(WXUID);
            //没有这个用户的消息,则挺尸
            if (!weixin_auto.AllOldMsg.has(to_username)) {
                return;
            }

            //有则刷新列表
            JsonObject user_msg_json = weixin_auto.AllOldMsg.get(to_username).getAsJsonObject();
            Iterator<Map.Entry<String, JsonElement>> iter = user_msg_json.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<String, JsonElement> one_iter = iter.next();
                JsonObject one_msg = (JsonObject) one_iter.getValue();
                String msg_content = one_msg.get("msg_content").getAsString();
                String msg_who = one_msg.get("who").getAsString();
                //显示消息
                //得到了消息,显示到界面
                ShowOneMsg(WXUID,
                        to_username, msg_who, msg_content);
            }
        }).start();

    }

    /**
     * 独立线程刷新新消息的进入并显示
     * <br>这个线程一直运行到程序终止
     */
    public static void ThreadStartRENewMsg() {
        new Thread(() -> {
            while (true) {

                ShowOneNewMsg();
                try {
                    //每500毫秒刷新一次
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    System.err.println("消息值守休眠出错");
                }
            }
        }).start();
    }

    /**
     * 刷新一次新消息显示,仅针对当前已经选择的账号
     */
    public static void ShowOneNewMsg() {
        //如果没有选择账号
        if (AdminMain.SelectWeiXinUid.equals("")) {
            return;
        }
        //如果没有选择好友
        if (AdminMain.SelectFriendJpanel == null) {
            return;
        }
        //如果show按钮记录的好友账号为空
        String use_msg_username = AdminMain.push_msg.getActionCommand();
        if (use_msg_username.equals("")) {
            return;
        }

        //如果新消息是空,则不处理
        WeiXinAuto use_weixin_auto = (WeiXinAuto) AdminMain.AllWeiXinAutos.get(AdminMain.SelectWeiXinUid);

        JsonObject use_all_new_msg = use_weixin_auto.AllNewMsg;

        //消息记录条数等于0 页不运行
        int all_msg_count = use_all_new_msg.entrySet().size();
        if (all_msg_count < 1) {
            return;
        }
        //现在消息大于0
        //检查有没有当前选中的用户的消息
        if (!use_all_new_msg.has(use_msg_username)) {
            //没有也不运行
            return;
        }

        //获得此朋友的老消息组,没有就创建
        if (!use_weixin_auto.AllOldMsg.has(use_msg_username)) {
            use_weixin_auto.AllOldMsg.add(use_msg_username, new JsonObject());
        }
        /**
         * 所有当前朋友的聊天记录,要包括老的
         */
        JsonObject use_all_old_msg = use_weixin_auto.AllOldMsg.get(use_msg_username).getAsJsonObject();
        //正式读取消息,得到此用户的消息列表
        JsonObject use_msg_list = use_all_new_msg.get(use_msg_username).getAsJsonObject();

        //朋友的消息列表里没有 记录页停止
        Iterator<Map.Entry<String, JsonElement>> iter = use_msg_list.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, JsonElement> iter_next = iter.next();
            String msg_key = iter_next.getKey();
            JsonObject use_one_msg = (JsonObject) iter_next.getValue();

            String msg_content = use_one_msg.get("msg_content").getAsString();
            String who_msg = use_one_msg.get("who").getAsString();
            //得到了消息,显示到界面
            ShowOneMsg(AdminMain.SelectWeiXinUid,
                    use_msg_username, who_msg, msg_content);
            //追加金老数据内 一条
            use_one_msg.addProperty("who", who_msg);
            use_all_old_msg.add("" + System.currentTimeMillis(), use_one_msg);
        }
        //循环完毕后,代表这个用户的消息已经全部显示使用了,就要删除消息记录,以及把消息记录进老消息记录里了
        //将整理好的老数据再放回全局变量里
        use_weixin_auto.AllOldMsg.add(use_msg_username, use_all_old_msg);

        //清空新消息记录
        use_weixin_auto.AllNewMsg.remove(use_msg_username);

        //刷新新消息数量在管理员头像上
        ChangeShow.ChangeAdminMsgCountNumber(AdminMain.SelectWeiXinUid);
    }

    /**
     * 改变管理员账号的新消息统计数字
     *
     * @param WXUID 管理员账号的id
     */
    public static void ChangeAdminMsgCountNumber(String WXUID) {
        AdminPanel use_admin_panel = (AdminPanel) AdminMain.AllAdminPanels.get(WXUID);
        WeiXinAuto one_weixin = (WeiXinAuto) AdminMain.AllWeiXinAutos.get(WXUID);
        int new_count = one_weixin.AllNewMsg.entrySet().size();
        if (new_count > 0) {
            use_admin_panel.msg_count.setText("" + new_count);
            use_admin_panel.msg_count.setVisible(true);
        } else {
            use_admin_panel.msg_count.setVisible(false);
        }
    }

    /**
     * 对指定朋友的新消息统计给予刷新以及显示最后一条新消息
     *
     * @param WXUID 账号id
     * @param FriendUserName 朋友名称id
     * @param new_msg 新消息
     */
    public static void ChangeFriendMsgCountNum(String WXUID, String FriendUserName, String new_msg) {
        FriendsPanel use_friend_panel = (FriendsPanel) AdminMain.AllPanels.get(WXUID);
        //没有这个朋友就终止
        if (!use_friend_panel.one_admin_all_friends_panel.containsKey(FriendUserName)) {
            return;
        }
        OneFriendPanel one_frined_panel = (OneFriendPanel) use_friend_panel.one_admin_all_friends_panel.get(FriendUserName);
        one_frined_panel.last_mag.setText(new_msg);

        //获得新消息的统计数量
        WeiXinAuto weixin_auto = (WeiXinAuto) AdminMain.AllWeiXinAutos.get(WXUID);
        int one_new_count = weixin_auto.AllNewMsg.get(FriendUserName).getAsJsonObject().entrySet().size();

        //如果当前被选中的疲软,消息统计不显示为0
        if (AdminMain.SelectFriendJpanel != null) {
            String select_username = AdminMain.SelectFriendJpanel.friend_username.getText();
            if (select_username.equals(FriendUserName)) {
                one_new_count = 0;
            }
        }

        if (one_new_count > 0) {
            one_frined_panel.new_msg_count.setText("" + one_new_count);
            one_frined_panel.new_msg_count.setVisible(true);
        } else {
            one_frined_panel.new_msg_count.setVisible(false);
        }
    }

    /**
     * 根据类型来排列以及显示隐藏不同的朋友或组或最新消息
     * <br>循环所有朋友,对符合的显示不符合的隐藏
     * <br>排列位置 根据当前使用的数组的key顺序(最新消息采用倒序)
     *
     * @param WXUID
     * @param FType
     */
    public static void REShowSelectTypeFriends(String WXUID, String FType) {
        //获得自动实例
        WeiXinAuto weixin_auto = (WeiXinAuto) AdminMain.AllWeiXinAutos.get(WXUID);
        //获得第二操作面板
        FriendsPanel friends_panel = (FriendsPanel) AdminMain.AllPanels.get(WXUID);
        //获得当前账号下所有朋友的panel集合
        HashMap friends_list_panel = friends_panel.one_admin_all_friends_panel;

        //根据类型进行不同的处理,得到真正要使用的名称集合
        ArrayList use_show_list = new ArrayList();

        //针对最新消息列表用户的处理
        if (FType.equals("last")) {
            //倒序
            ListIterator liter = weixin_auto.AllLastCallFriends.listIterator(weixin_auto.AllLastCallFriends.size());
            while (liter.hasPrevious()) {
                String next = liter.previous().toString();
                use_show_list.add(next);
            }
        }

        //针对搜索进行的处理
        if (FType.equals("search")) {
            //获得搜索的关键字
            String search_key = friends_panel.search_friend.getText();
            //循环所有朋友获得匹配,存放进要使用的数组
            Iterator<JsonElement> siter = weixin_auto.AllfriendsList.iterator();
            while (siter.hasNext()) {
                JsonObject s_one = (JsonObject) siter.next();
                //获得nickname
                String nick_name = s_one.get("NickName").getAsString();
                String user_name = s_one.get("UserName").getAsString();
                //进行搜索匹配
                if (nick_name.contains(search_key)) {
                    //存在则加入
                    use_show_list.add(user_name);
                }
            }
        }

        //针对朋友列表和组列表的处理
        if (FType.equals("friends") || FType.equals("group")) {
            //朋友和组使用的是同一个数组,排位不讲究
            Iterator<JsonElement> fgiter = weixin_auto.AllfriendsList.iterator();
            while (fgiter.hasNext()) {
                JsonObject next_one = (JsonObject) fgiter.next();
                String user_name = next_one.get("UserName").getAsString();
                //这里根据type的不同,进行不同的处理
                if (FType.equals("group") && user_name.contains("@@")) {
                    use_show_list.add(user_name);
                }
                if (FType.equals("friends") && !user_name.contains("@@")) {
                    use_show_list.add(user_name);
                }

            }
        }

        //这里产生要要使用的数组
        //循环一次所有朋友控件,来改变位置以及是否显示
        Iterator as_iter = friends_list_panel.entrySet().iterator();
        while (as_iter.hasNext()) {
            Map.Entry nextEntry = (Map.Entry) as_iter.next();
            String key = nextEntry.getKey().toString();
            OneFriendPanel one_panel = (OneFriendPanel) nextEntry.getValue();
            //存在再列表
            int order_id = use_show_list.indexOf(key);
            if (order_id != -1) {
                //不等于-1 就是存在
                one_panel.setVisible(true);
                one_panel.setBounds(0, order_id * 55, 237, 55);

            } else {
                one_panel.setVisible(false);
            }

        }

        //变更show的大小
        friends_panel.show_friends_list.setPreferredSize(new Dimension(0, use_show_list.size() * 55));
        friends_panel.friends_list.repaint();

    }

    /**
     * 过滤所有html标签
     *
     * @param source
     * @return
     */
    public static String deleteAllHTMLTag(String source) {

        if (source == null) {
            return "";
        }

        String s = source;
        /**
         * 删除普通标签
         */
        s = s.replaceAll("<(S*?)[^>]*>.*?|<.*? />", "");
        /**
         * 删除转义字符
         */
        s = s.replaceAll("&.{2,6}?;", "");
        return s;
    }
}
