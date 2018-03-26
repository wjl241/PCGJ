/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Auto;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.print.StreamPrintService;
import javax.sound.midi.SysexMessage;
import javax.swing.ImageIcon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.ssl.HttpsURLConnection;
import com.sun.net.ssl.internal.www.protocol.https.Handler;

import RTPower.RTHttp;
import RTPower.RTdate;
import callback.CallBack;
import panel.FriendsPanel;
import util.MD5Util;
import weixin.AdminMain;
import weixin.QFMain;
import weixin.qrcodeMain;

/**
 * 微信自动主类
 * <br>全部方法在里面,
 * <br>每次实例就是一个新的微信
 *
 * @author jerry
 */
public class WeiXinAuto {

    /**
     * 当前微信的cookies记录
     */
    public HashMap WXCookies = new HashMap();

    /**
     * 当前微信的个人信息记录
     * <br>包括UUID 头像
     */
    public HashMap MyInfo = new HashMap();

    /**
     * 朋友列表信息记录
     */
    public JsonArray AllfriendsList = new JsonArray();

    /**
     * 最近联系的朋友列表
     * <br>ArrayList 方便倒序 从最新的开始排列,
     * <br>仅仅记录最新的联系用户的username
     */
    public ArrayList AllLastCallFriends = new ArrayList();
    
    /**
     * 群组列表信息记录
     */
    public JsonArray AllGroupList = new JsonArray();

    /**
     * 组信息记录
     */
    public HashMap GroupList = new HashMap();

    /**
     * 扫码监听状态
     */
    public boolean login_scan_status = true;

    /**
     * 循环值守的线程的状态
     */
    public boolean ThreadStatus = false;

    /**
     * 心跳线程
     */
    public Thread HeartJumpThread;
    
    /**
     * 心跳线程2
     */
    public Thread HeartJumpThread2;
    
    
    
    /**
     * 手机端是否开启
     */
    public boolean useIng = false;

    /**
     * 记录下自己账号的ImageIcon 方柏霓使用
     */
    public Image MyFaceImage;

    /**
     * 存储所有最新接收到的一次新消息
     * <br>这里存入以后,如果没有读取,则是一直存在,
     * <br>再有新消息进入,继续累加
     * <br>真正读取显示后,才从这里消失.
     */
    public JsonObject AllNewMsg = new JsonObject();

    /**
     * 此处存放所有用户的老聊天记录
     * <br>格式为 [用户名][时间][消息];
     */
    public JsonObject AllOldMsg = new JsonObject();

    /**
     * 此账号的username id,在初始化微信的时候赋予
     * <br>方便使用
     */
    public String AdminUserName = "";

    
    public int contactCount =0;
    
    public String phone ="";
    
    private int wxMode = 2;//微信接口模式，1就用wx 2就用wx2
    /**
     * 初始化就开始运行
     */
    public WeiXinAuto() {
        //初始化的时候就获取UUID
        GetUUID();
    }

    public void setPhone(String phone) {
    	this.phone = phone;
    }
    /**
     * 第一步获取UUID
     * <br>并且记录,用来获得二维码使用
     */
    private void GetUUID() {

        System.setProperty("jsse.enableSNIExtension", "false");
        //请求http获得uuid
        String set_url = "https://login.weixin.qq.com/jslogin?appid=wx782c26e4c19acffb"
                + "&fun=new&lang=zh_CN&_=" + RTdate.GetNowStemp();
        RTHttp rhttp = new RTHttp(set_url);
        String result = rhttp.Get();
        rhttp.close();
        //解析内容,判断是否状态正常,以及获得UUID并记录
        //检查200;是否存在,不存在则终止
        if (!result.contains("200;")) {
            System.err.println("获取UUID返回状态出错:" + result);
            return;
        }
        //获得切割数据 得到UUID
        //截取其中的UUID
        String UUID = result.split("\"")[1];

        //记录下这个UUID
        MyInfo.put("LoginUUID", UUID);
        System.err.println("获得UUID:" + UUID);
    }

    /**
     * 获得二维码图片
     * <br>获取数据并返回byte[]
     *
     * @return 图片数据
     */
    public byte[] GetImage() {

        //根据UUID获取验证码
        String set_url = "https://login.weixin.qq.com/qrcode/"
                + MyInfo.get("LoginUUID");
        RTHttp rhttp = new RTHttp(set_url);
        byte[] result_image = rhttp.GetImage();
        rhttp.close();
        return result_image;
    }

    /**
     * 持续检查是否扫码以及点击登录
     * <br>并更新扫码图片的显示
     *
     * @param login_main
     */
    public void ScanLoginStatus(qrcodeMain login_main,CallBack callback) {

        //设置tip默认是1 就是还没有扫码
        MyInfo.put("login_tip", "1");
        //进行轮训每2秒
        new Thread(() -> {

            while (login_scan_status) {

                //如果返回false 则终止线程
                if (!GetScanStatus(login_main)) {
                    login_scan_status = false;
                    //关闭二维码页面
                    //login_main.dispose();
                    System.err.println("关闭微信二维码界面.");
                    callback.execute();
                }
                try {
                    Thread.sleep(1000);
                    System.err.println("状态:循环中......");
                } catch (InterruptedException ex) {
                    System.err.println("出错:循环检查扫码状态");
                }
            }
        }).start();
    }

    //=======================================================================
    /**
     * 检查扫码状态一次,获得状态码
     */
    private boolean GetScanStatus(qrcodeMain login_main) {
        if (!login_scan_status) {
            return false;
        }
        String set_url = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login"
                + "?loginicon=true&uuid=" + MyInfo.get("LoginUUID") + ""
                + "&tip=" + MyInfo.get("login_tip").toString() + "&_=" + RTdate.GetNowStemp();
        RTHttp rhttp = new RTHttp(set_url);
        String result = rhttp.Get();
        rhttp.close();

        System.err.println(set_url);
        //检查状态码
        //如果是201 则有头像返回,如果是200 有URL返回,如果是408则是登录超时了
        if (result.contains("408;")) {
            //登录超时,弹出提示关闭窗体,终止进程
            System.err.println("扫码超时408.....");
            return false;
        }

        //是201 就代表扫码了,记录头像
        if (result.contains("201;")) {

            String result_image = result.split("\'")[1];
            result_image = result_image.replace("data:img/jpg;base64,", "");
            System.err.println("扫码完毕,等待点击登录.....");
            //截取头像信息
            MyInfo.put("UserFaceImage", result_image);
//            System.err.println(result_image);
            byte[] use_byte = Base64.getDecoder().decode(result_image);
            //将图片解码,并显示给控件
            //改变图片大小

            login_main.qrcodeLbl.setIcon(new ImageIcon(SetImageSize(new ImageIcon(use_byte), 200, 200)));
            login_main.messageLbl.setText("请手机上点击确认");

            MyInfo.put("login_tip", "0");
            return true;
        }

        //扫码并登录成功,记录下详细的url和cookies
        if (result.contains("200;")) {
            String result_url = result.split("\"")[1];
            System.err.println("扫码以及登录完成.....");
            System.err.println("获得URL:" + result_url);
           // login_main.dispose();
            //获得cookies
            GetLoginInfo(result_url);
            return false;
        }

        //以上情况都不是就一直循环
        return true;
    }

    /**
     * 获取登录后其他必要步骤
     * <br>获得cookies,初始化,开启通知,执行心跳
     *
     * @param result_url
     */
    private void GetLoginInfo(String result_url) {

        //生成一个通用的did
        int radomInt = new Random().nextInt(99999);
        int radomInt2 = new Random().nextInt(99999);
        int radomInt3 = new Random().nextInt(99999);
        int radomInt4 = new Random().nextInt(99999);
        String devidString = radomInt + "" + radomInt2 + "" + radomInt3 + "" + radomInt4;
        MyInfo.put("DeviceID", "e" + devidString.substring(0, 15));

        GetCookies(result_url);
        //至此登录已经成功,先初始化一次,获得一些基本信息
        GetWeiXinInit();

        
        if(!QFMain.test) {
        	//如果在微信自动类里面已经有了这个账号id,则先清除这个id,防止一个账号重复
            if (AdminMain.AllWeiXinAutos.containsKey(WXCookies.get("wxuin").toString())) {
                ChangeShow.CloseOneWxAdmin(WXCookies.get("wxuin").toString());
            }
            //将这个实例赋予主控台的 实例列表,这里以wxuin作为key 这是微信号的id
            AdminMain.AllWeiXinAutos.put(WXCookies.get("wxuin").toString(), this);

            //增加账号显示在界面的第一栏
            ChangeShow.AddNewAdminFace(WXCookies.get("wxuin").toString());
            //激活刷新一次界面的账号列表显示
        }
  
     

        //开启消息通知
        GetWeiXinStatusNotify();
        //获得微信联系人
       
        if(QFMain.test) {
        	// GetContactGroup();
        	// GetContact();
        	// GetContact();
        	 GetContact();
        }else{
        	 GetContact();
        }
       

        //保持心跳,不断刷新消息检查
        ThreadStatus = true;
        HeartJump();
    }

    /**
     * 获得微信登录的cookies
     */
    private void GetCookies(String result_url) {
        RTHttp http = new RTHttp(result_url + "&fun=new&&version=v2");
        String cookies_webString = http.Get();//获得xml
        http.close();
        //解析xml
        Document xml = Jsoup.parse(cookies_webString, "", Parser.xmlParser());

        //记录进cookieshash
        WXCookies.put("skey", xml.select("skey").text());
        WXCookies.put("wxsid", xml.select("wxsid").text());
        WXCookies.put("wxuin", xml.select("wxuin").text());
        WXCookies.put("pass_ticket", xml.select("pass_ticket").text());
        //以上更新好了cookies,就说明登录成功
        System.err.println("微信获取cookies完毕.....");
        System.err.println("Skey:" + WXCookies.get("skey").toString());
    }

    /**
     * 根据本地文件信息进行直接登录
     *
     * @param wx_cookies
     * @param DeviceID
     * @param UserFaceImage
     */
    public void ScanFileAdminLogin(HashMap wx_cookies, String DeviceID, String UserFaceImage) {
        //传入并覆盖cookies 和info信息
        WXCookies = wx_cookies;
        MyInfo.put("DeviceID", DeviceID);
        MyInfo.put("UserFaceImage", UserFaceImage);
        //先初始化一次,获得一些基本信息
        GetWeiXinInit();
        //界面显示
        //将这个实例赋予主控台的 实例列表,这里以wxuin作为key 这是微信号的id
        AdminMain.AllWeiXinAutos.put(WXCookies.get("wxuin").toString(), this);

        //增加账号显示在界面的第一栏
        ChangeShow.AddNewAdminFace(WXCookies.get("wxuin").toString());
//开启消息通知
        if (!GetWeiXinStatusNotify()) {
            return;
        }
        //获得微信联系人
        GetContact();

        //保持心跳,不断刷新消息检查
        ThreadStatus = true;
        HeartJump();
    }

    /**
     * 微信初始化(初始化的时候已经获得了个人基本信息)
     */
    private void GetWeiXinInit() {
        //接着微信初始化
       // RTHttp http2 = new RTHttp("https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxinit");
        RTHttp http2 = null;
		try {
			wxMode =2;
			http2 = new RTHttp("https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxinit?r="+System.currentTimeMillis()+"&pass_ticket="+URLEncoder.encode(WXCookies.get("pass_ticket").toString(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
       
 
        //准备post的jsonstring
        JsonObject jsonob = new JsonObject();
        jsonob.addProperty("Uin", WXCookies.get("wxuin").toString());
        jsonob.addProperty("Sid", WXCookies.get("wxsid").toString());
        jsonob.addProperty("Skey", WXCookies.get("skey").toString());
        jsonob.addProperty("DeviceID", MyInfo.get("DeviceID").toString());

        JsonObject new_jsonob = new JsonObject();
        new_jsonob.add("BaseRequest", jsonob);
        String post_json_string = HashToJsonString(new_jsonob);

        http2.SetCookies(http2.MakeCookies(WXCookies));
        String formatString = "";//获得xml
        
        try {
        	formatString = http2.Post(post_json_string);//获得xml
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"微信webwxinit接口异常1", new Object());
			http2.close();
			return ;
		}
        
        http2.close();
        //解析xml,其中最关键的Synckey 记录下来

        Gson json = new Gson();
        JsonObject new_json = json.fromJson(formatString, JsonObject.class);
        int code =  new_json.getAsJsonObject("BaseResponse").get("Ret").getAsInt();
        if(code != 0) {
        	wxMode = 1;
        	try {
    			http2 = new RTHttp("https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit?r="+System.currentTimeMillis()+"&pass_ticket="+URLEncoder.encode(WXCookies.get("pass_ticket").toString(), "UTF-8"));
    		} catch (UnsupportedEncodingException e) {
    			e.printStackTrace();
    		}
        	http2.SetCookies(http2.MakeCookies(WXCookies));
            
            try {
                formatString = http2.Post(post_json_string);//获得xml
    		} catch (Exception e) {
    			e.printStackTrace();
    			Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"微信webwxinit接口异常2", new Object());
    			http2.close();
    			return ;
    		}
            
            http2.close();
            //解析xml,其中最关键的Synckey 记录下来

            json = new Gson();
            new_json = json.fromJson(formatString, JsonObject.class);
            code = new_json.getAsJsonObject("BaseResponse").get("Ret").getAsInt();
            if(code != 0) {
            	System.err.println("微信初始化异常："+new_json);
            	return ;
            }
        }
        

        MyInfo.put("SyncKey", new_json.get("SyncKey"));
        MyInfo.put("User", new_json.get("User"));
        //加入最近的好友联系列表
        JsonArray ContactList = new_json.get("ContactList").getAsJsonArray();
        //同时也写出给All
        AllfriendsList = ContactList;

        //循环解析这里获得的是最近联系人,仅仅把用户名存储下来
        Iterator<JsonElement> iter = ContactList.iterator();
        JsonObject group = new JsonObject();
        while (iter.hasNext()) {
        	group  = new JsonObject();
            JsonObject next = (JsonObject) iter.next();
            String user_name = next.get("UserName").getAsString();
            String nick_name = next.get("NickName").getAsString();
            if(nick_name.contains("\"")) {
            	 nick_name = nick_name.replace("\"", "");
            }
            if(user_name.contains("\"")) {
            	user_name = user_name.replace("\"", "");
            }
            //把用户名加入数组,存在的就不加入
            if (!AllLastCallFriends.contains(user_name)) {
                AllLastCallFriends.add(user_name);
            }
            if(user_name.contains("@@")) {
            	group.addProperty("UserName", user_name);
            	group.addProperty("NickName", nick_name);
            	 if (!AllGroupList.contains(group)) {
            		 AllGroupList.add(group);
                 }
            }
         
        }
        System.err.println("AllGroupList init:=================>"+AllGroupList);
        System.err.println("AllGroupList.size1:=================>"+AllGroupList.size());
        //执行一次降序
        ArrayList new_all_last_call_friends = new ArrayList();
        ListIterator liter = AllLastCallFriends.listIterator(AllLastCallFriends.size());
        while (liter.hasPrevious()) {
            String next = liter.previous().toString();
            new_all_last_call_friends.add(next);
        }
        AllLastCallFriends = new_all_last_call_friends;

        System.err.println("微信初始化完毕.....");
    }

    /**
     * 开启微信状态通知
     */
    private boolean GetWeiXinStatusNotify() {
        //RTHttp ohttp = new RTHttp("https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxstatusnotify?lang=zh_CN&pass_ticket=" + WXCookies.get("pass_ticket"));
        RTHttp ohttp = new RTHttp("https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxstatusnotify?lang=zh_CN&pass_ticket=" + WXCookies.get("pass_ticket"));
        JsonObject use_info = (JsonObject) MyInfo.get("User");
        AdminUserName = use_info.get("UserName").getAsString();
        //准备post的jsonstring
        JsonObject jsonob = new JsonObject();
        jsonob.addProperty("Uin", WXCookies.get("wxuin").toString());
        jsonob.addProperty("Sid", WXCookies.get("wxsid").toString());
        jsonob.addProperty("Skey", WXCookies.get("skey").toString());
        jsonob.addProperty("DeviceID", MyInfo.get("DeviceID").toString());

        JsonObject new_jsonob = new JsonObject();
        new_jsonob.add("BaseRequest", jsonob);
        new_jsonob.addProperty("Code", 3);
        new_jsonob.addProperty("FromUserName", AdminUserName);
        new_jsonob.addProperty("ToUserName", AdminUserName);
        new_jsonob.addProperty("ClientMsgId", System.currentTimeMillis());
        String post_json_string = HashToJsonString(new_jsonob);

        ohttp.SetCookies(ohttp.MakeCookies(WXCookies));
        String s_webString = "";//获得xml
        
        try {
        	s_webString = ohttp.Post(post_json_string);//获得xml
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"微信webwxstatusnotify接口异常", new Object());
			ohttp.close();
			return false;
		}
        ohttp.close();

        System.err.println("开启微信状态通知....");
        Gson us_gson = new Gson();
        JsonObject us_json = us_gson.fromJson(s_webString, JsonObject.class);
        JsonObject BaseResponse = us_json.getAsJsonObject("BaseResponse");
        String ret_text = BaseResponse.get("Ret").toString();
        if (!ret_text.equals("0")) {
            //同步出错则销毁自己
           // ChangeShow.CloseOneWxAdmin(WXCookies.get("wxuin").toString());
        	System.err.println();
            return false;
        }
        System.err.println(s_webString);
        return true;
    }

    /**
     * 获取微信联系人列表(使用新线程)
     */
    private void GetContact() {
       // new Thread(() -> {
        	 String post_url;
        	if(contactCount==0) {
        		 post_url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact";
        		 contactCount =1;
        	}else {
        		 post_url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact";
        		 contactCount =0;
        	}
           // String post_url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact";

        	
            JsonObject jsonob = new JsonObject();
            jsonob.addProperty("Uin", WXCookies.get("wxuin").toString());
            jsonob.addProperty("Sid", WXCookies.get("wxsid").toString());
            jsonob.addProperty("Skey", WXCookies.get("skey").toString());
            jsonob.addProperty("DeviceID", MyInfo.get("DeviceID").toString());

            JsonObject new_jsonob = new JsonObject();
            new_jsonob.add("BaseRequest", jsonob);
            String post_json_string = HashToJsonString(new_jsonob);

            RTHttp rhttp = new RTHttp(post_url);
            rhttp.SetCookies(rhttp.MakeCookies(WXCookies));
            String  result_string = "";
            
            try {
            	 result_string = rhttp.Post(post_json_string);
    		} catch (Exception e) {
    			e.printStackTrace();
    			Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"微信webwxgetcontact接口异常", new Object());
    			rhttp.close();
    			return ;
    		}
            
            rhttp.close();

            //更新之前获取的最近联系人列表
            Gson json = new Gson();
            JsonObject new_json = json.fromJson(result_string, JsonObject.class);
            //因为之前初始化的时候获得了最近联系人,这里要把之前的追加进这里
            JsonArray all_friends = new_json.get("MemberList").getAsJsonArray();
            //循环之前的,一个个加入
            Iterator<JsonElement> json_iter = AllfriendsList.iterator();
            JsonObject group =new JsonObject();
            while (json_iter.hasNext()) {
            	group =new JsonObject();
                JsonObject next = (JsonObject) json_iter.next();
                //只加入不存在的,并且@存在
                if (!all_friends.contains(next)) {
                    all_friends.add(next);
                }
                String user_name = next.get("UserName").getAsString();
                String nick_name = next.get("NickName").getAsString();
                if(nick_name.contains("\"")) {
               	 nick_name = nick_name.replace("\"", "");
               }
               if(user_name.contains("\"")) {
               	user_name = user_name.replace("\"", "");
               }
                if(user_name.contains("@@")) {
                	group.addProperty("UserName", user_name);
                	group.addProperty("NickName", nick_name);
                	 if (!AllGroupList.contains(group)) {
                		 AllGroupList.add(group);
                     }
                }
                

            }
           // System.err.println("AllGroupList222==============>:"+AllGroupList);
            System.err.println("AllGroupList.size222==============>:"+AllGroupList.size());
            AllfriendsList = all_friends.getAsJsonArray();

            System.err.println("正在刷新朋友列表");
            //刷新界面
            ChangeShow.ShowAllFriendsList(WXCookies.get("wxuin").toString());

            System.err.println("获取联系人信息完毕!");
//        System.err.println(result_string);
      //  }).start();
        
    }

    /**
     * 保持与服务器的心跳 循环线程执行
     */
    private void HeartJump() {

    	//不进行消息检测
    	if(QFMain.test ) {
    		  HeartJumpThread = new Thread(() -> {
    	            while (ThreadStatus) {
    	                //只运行一个同步检测,如果有消息,则内部再执行获取消息
    	                GetSyncCheck();
    	                try {
    	                    Thread.sleep(1000);
    	                } catch (InterruptedException ex) {
    	                    System.err.println("心跳休眠失败...");
    	                }
    	            }
    	        });
    	        HeartJumpThread.start();
    	}
      
        
        
        HeartJumpThread2 = new Thread(() -> {
            while (ThreadStatus) {

                //只运行一个群组信息获取
            	  GetContact();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    System.err.println("心跳休眠2失败...");
                }
            }
        });
        HeartJumpThread2.start();
        
        
      
    }

    /**
     * 检查同步状态
     */
    private void GetSyncCheck() {

        String get_url = "";
        try {
           // get_url = "https://webpush.wx2.qq.com/cgi-bin/mmwebwx-bin/synccheck"
            get_url = "https://webpush.weixin.qq.com/cgi-bin/mmwebwx-bin/synccheck"
                    + "?skey=" + URLEncoder.encode(WXCookies.get("skey").toString(), "UTF-8")
                    + "&r=" + System.currentTimeMillis()
                    + "&sid=" + URLEncoder.encode(WXCookies.get("wxsid").toString(), "UTF-8")
                    + "&uin=" + WXCookies.get("wxuin").toString()
                    + "&deviceid=" + MyInfo.get("DeviceID").toString()
                    + "&synckey=" + URLEncoder.encode(MakeSynckey(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            System.err.println("检查同步出错");
        }

        //这是一个长链接,加超时为60秒
        RTHttp http = new RTHttp(get_url) {
            @Override
            public void ChangeTimeOut() {
                TimeOut = 60000;
            }
        };

        http.SetCookies(http.MakeCookies(WXCookies));
        String result_string = http.Get();
        String show_cookies = http.GetCookies();

        http.close();

        //System.err.println(WXCookies.get("wxuin").toString() + "同步结果:" + result_string);

        //只是心跳链接，而不做后续处理
        if(!QFMain.test) {
        	//过滤结果,得出2个参数,来判断是否要获取消息
            String[] new_json_string = result_string.split("\"");

            //得到2个参数,第一个不是0就说明断线了,第二个不是0就是有消息
            if (!new_json_string[1].equals("0")) {
                System.err.println("同步出错:" + new_json_string[1]);
                //同步出错则销毁自己
                ChangeShow.CloseOneWxAdmin(WXCookies.get("wxuin").toString());
            }

            //如果消息通知不是0就是有消息,则进行同步一下消息
            if (!new_json_string[3].equals("0")) {
                GetSyncMsg();
            }
        }
        

    }

    /**
     * 执行一次获取最新消息的检查
     *
     */
    private void GetSyncMsg() {

        String get_url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsync"
                + "?sid=" + WXCookies.get("wxsid").toString() + ""
                + "&r=" + System.currentTimeMillis();

        JsonObject jsonob = new JsonObject();
        jsonob.addProperty("Uin", WXCookies.get("wxuin").toString());
        jsonob.addProperty("Sid", WXCookies.get("wxsid").toString());
        jsonob.addProperty("Skey", WXCookies.get("skey").toString());
        jsonob.addProperty("DeviceID", MyInfo.get("DeviceID").toString());

        JsonObject new_jsonob = new JsonObject();
        new_jsonob.add("BaseRequest", jsonob);
        new_jsonob.add("SyncKey", (JsonObject) MyInfo.get("SyncKey"));

        String post_json_string = HashToJsonString(new_jsonob);

        //值守心跳
        RTHttp http = new RTHttp(get_url);

        http.SetCookies(http.MakeCookies(WXCookies));
        String sny_webString = "";
        
        try {
        	sny_webString = http.Post(post_json_string);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"微信webwxsync接口异常", new Object());
			http.close();
			return ;
		}
        http.close();
        //有新的SyncKey就要更新
        Gson gson = new Gson();
        JsonObject usjson = gson.fromJson(sny_webString, JsonObject.class);
        MyInfo.put("SyncKey", usjson.get("SyncKey"));
//        System.err.println(get_url);
//        System.err.println("获取最新消息:" + sny_webString);
        //这里已经获得了最新的消息,需要转存储到Jsob数组里面
        int new_msg_count = usjson.get("AddMsgCount").getAsInt();
        //大于0是有新消息
        if (new_msg_count > 0) {
            //执行消息处理
            SaveNewMsg(usjson.get("AddMsgList").getAsJsonArray());
        }
    }

    /**
     * 对新的消息进行记录进全局
     *
     * @param AddMsgList
     */
    private void SaveNewMsg(JsonArray AddMsgList) {

        int add_msg_num = 0;
        Iterator<JsonElement> iter = AddMsgList.iterator();
        while (iter.hasNext()) {
            JsonObject next = (JsonObject) iter.next();
            String msgid = next.get("MsgId").getAsString();
            //獲得消息時間戳
            int msg_create_time = next.get("CreateTime").getAsInt();
            //獲得消息 内容
            String msg_content = next.get("Content").getAsString();
            //获得消息从哪里来的,由于自己永远是接收方,所以只记录from
            String from_username = next.get("FromUserName").getAsString();
            String who_msg = "friend";
            //如果发送人士自己,那么调整接收人为from
            if (AdminUserName.equals(from_username)) {
                from_username = next.get("ToUserName").getAsString();
                who_msg = "me";
            }
            //获得消息类型
            int msg_type = next.get("MsgType").getAsInt();

            //除了类型为1的文本其他的全部不要
            //3 图片 49小程序
            if (msg_type != 1  ) {
                //跳过以下的处理
                continue;
            }

            //更新一下最近联系人,先删除老的 再加入新的
            AllLastCallFriends.remove(from_username);
            AllLastCallFriends.add(from_username);
            //刷新页面
            //如果界面当前选择的是最新消息栏目,则刷新
            FriendsPanel friend_panel = (FriendsPanel) AdminMain.AllPanels.get(WXCookies.get("wxuin").toString());
            if (friend_panel.SelectJLabel.getName().equals("last")) {
                ChangeShow.REShowSelectTypeFriends(WXCookies.get("wxuin").toString(), "last");
            }
//开始吧消息存储,按指定格式
            //每次执行,就账号新消息+1
            add_msg_num++;

            //如果消息里没有这个用户的字段,则创建一下
            if (!AllNewMsg.has(from_username)) {
                AllNewMsg.add(from_username, new JsonObject());
            }
            //这是要使用的用户的消息记录json
            JsonObject use_json = AllNewMsg.get(from_username).getAsJsonObject();
            //网里面新加一个
            JsonObject new_msg = new JsonObject();
            new_msg.addProperty("msg_content", msg_content);
            new_msg.addProperty("msg_time", msg_create_time);
            new_msg.addProperty("who", who_msg);
            //按时间写出
            use_json.add("" + msg_create_time, new_msg);

            System.err.println("获得一个新消息:" + msg_content);
            //让消息显示在对应的朋友的名称下面(最后一条消息)
            ChangeShow.ChangeFriendMsgCountNum(WXCookies.get("wxuin").toString(), from_username, msg_content);

        }

        //这里得到了统计的微信账号的所有新消息统计数
        //刷新一次界面数量
        ChangeShow.ChangeAdminMsgCountNumber(WXCookies.get("wxuin").toString());
        //消息记录结束
    }

    /**
     * 关闭实例
     */
    public void Close() {
        ThreadStatus = false;//关闭值守线程
        HeartJumpThread = null;
    }

    /**
     * 组装Synckey
     *
     * @return
     */
    private String MakeSynckey() {
        String use_synkey = "";
        //循环

        JsonObject SyncKey = (JsonObject) MyInfo.get("SyncKey");
      //  System.err.println("SyncKey:"+SyncKey);
        JsonArray key_list = SyncKey.get("List").getAsJsonArray();

        for (Iterator iterator = key_list.iterator(); iterator.hasNext();) {
            JsonObject next = (JsonObject) iterator.next();

            String key = next.get("Key").toString();
            String value = next.get("Val").toString();
            use_synkey += key + "_" + value + "|";

        }
      //  System.err.println("use_synkey:"+use_synkey);
        String new_use_synkey = use_synkey.substring(0, use_synkey.length() - 1);
//        System.err.println(new_use_synkey);
        return new_use_synkey;

    }

    /**
     * 改变图片的大小并返回
     *
     * @param old_image
     * @param width
     * @param height
     * @return
     */
    public Image SetImageSize(Image old_image, int width, int height) {
        Image use_image = old_image.getScaledInstance(width, height, Image.SCALE_FAST);
        return use_image;
    }

    /**
     * 改变图片大小并返回
     *
     * @param old_image
     * @param width
     * @param height
     * @return
     */
    public Image SetImageSize(ImageIcon old_image, int width, int height) {
        Image new_image = old_image.getImage();
        Image use_image = new_image.getScaledInstance(width, height, Image.SCALE_FAST);
        return use_image;
    }

    /**
     * 把byte转指定大小的image
     *
     * @param b
     * @param width
     * @param height
     * @return
     */
    public Image SetImageSize(byte[] b, int width, int height) {
        Image new_image = new ImageIcon(b).getImage();
        Image use_image = new_image.getScaledInstance(width, height, Image.SCALE_FAST);
        return use_image;
    }

    /**
     * 把hashmap 转换成json 字符串
     *
     * @param jsonob
     * @return json 的string
     */
    public String HashToJsonString(JsonObject jsonob) {
        Gson gson = new Gson();
        String jsonstring = gson.toJson(jsonob);
//        System.err.println(jsonstring);
        return jsonstring;
    }

    
    
    /**
     * 发送图片给自己的好友
     *
     * @param FriendUserName 朋友用户名
     * @param MsgContent 消息内容
     * @return boolean 成功 时报
     */
    public  boolean SendImgToFriend(String FriendUserName, String MsgContent) {
        JsonObject use_info = (JsonObject) MyInfo.get("User");

        //要请求的页面
//        String post_url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg?pass_ticket="
//                + WXCookies.get("pass_ticket").toString() + "&lang=zh_CN";
        String post_url = "https://file.wx.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json";
        post_url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsgimg?fun=async&f=json&lang=zh_CN&pass_ticket="
                + WXCookies.get("pass_ticket").toString();
        String filename = "";
        String filepath = "D:\\test";
        try {
			String url ="http://p.jihes.com//1688/avatar/10677_1504086951.jpg";
    		filename = url.split("/")[url.split("/").length-1];
			download(url, filename, "D:\\test");
			filepath = "D:\\test"+"\\"+filename;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        String MediaId = upload4(filepath, WXCookies.get("wxuin").toString(), WXCookies.get("wxsid").toString(), WXCookies.get("skey").toString(), 
        		MyInfo.get("DeviceID").toString(), "RK=kJ/mQF3uGo; pgv_pvi=6488696832; eas_sid=V1N5Y0p5R4C604J342i1C6B4e7; tvfe_boss_uuid=5460debd316da1b2; webwxuvid=cc628d84a7ee6b57131133b63e6319f155321e8cc1ffa1de2213355263de31c85152db45f214dde8d7a978f8f6e8479f; pac_uid=1_577409819; o_cookie=577409819; ptui_loginuin=577409819; ptcz=8104c20f19153f8e39b5169e2536ef6aece8755c710ae24e582e9090480b59a6; pt2gguin=o0577409819; pgv_pvid=4065025336; mm_lang=zh_CN; webwx_auth_ticket=CIsBEPS57p4GGoABthVcsOJfxarVyuhFlFfvO7aspK8oEZ+qXDpbjfBHmz4As54zZbSwuRuO7P7u/VEhSMizONeYLGVaj723HDw7tqRT1fHzEhoJAH51LXPATEVGlasEnsvLSPyvsvc5ZTmF/jVFHZqgJfHG0SAYveWdfDrkC6/r6Fm/iJy7SGvoN5Q=; wxloadtime=1512107879_expired; wxpluginkey=1512101025; wxuin=1746033210; wxsid=1D1+KxvjLwDd5Pth; webwx_data_ticket=gScgeVZfSQg8Qtgzrn5Pobfs", 
        		WXCookies.get("pass_ticket").toString(), use_info.get("UserName").getAsString(), FriendUserName, "POST","gSfJ7HZBpoGlbACIDIrctJ1H",wxMode);
       
        System.err.println("post_url=" + post_url);
        //要请求的数据
        //BaseRequest部分
        //TODO
        JsonObject jsonob = new JsonObject();
        jsonob.addProperty("Uin", WXCookies.get("wxuin").toString());
        jsonob.addProperty("Sid", WXCookies.get("wxsid").toString());
        jsonob.addProperty("Skey", WXCookies.get("skey").toString());
        jsonob.addProperty("DeviceID", MyInfo.get("DeviceID").toString());

        String use_msgid = MakeMsgId();
        //msg部分
        JsonObject jsonmsg = new JsonObject();
        //jsonmsg.addProperty("Type", 1);
        
        jsonmsg.addProperty("ClientMsgId", use_msgid);
        jsonmsg.addProperty("Content", "");
        jsonmsg.addProperty("FromUserName", use_info.get("UserName").getAsString());
        jsonmsg.addProperty("LocalID", use_msgid);
        jsonmsg.addProperty("MediaId", MediaId);
        jsonmsg.addProperty("ToUserName", FriendUserName);
        jsonmsg.addProperty("Type", 3);
       

        JsonObject new_json = new JsonObject();
        new_json.addProperty("UploadType", 2);
        new_json.add("BaseRequest", jsonob);
        new_json.add("Msg", jsonmsg);
        new_json.addProperty("Scene", 0);

        //请求http
        RTHttp rhttp = new RTHttp(post_url);
        rhttp.SetCookies(rhttp.MakeCookies(WXCookies));
        String post_json_string = HashToJsonString(new_json);
        System.err.println("发消息请求参数:" + post_json_string);
        String result_back = rhttp.Post(post_json_string);
        rhttp.close();
        System.err.println(result_back);

        //检查消息结果
        Gson us_gson = new Gson();
        JsonObject us_json = us_gson.fromJson(result_back, JsonObject.class);
        JsonObject BaseResponse = us_json.getAsJsonObject("BaseResponse");
        String ret_text = BaseResponse.get("Ret").toString();
        if (ret_text.equals("0")) {
            //成功后把消息加入老消息记录里
            JsonObject add_to_old = new JsonObject();
            add_to_old.addProperty("msg_content", MsgContent);
            add_to_old.addProperty("who", "me");
            //读出所有词用户的老数据
            if (!AllOldMsg.has(FriendUserName)) {
                AllOldMsg.add(FriendUserName, new JsonObject());
            }
            JsonObject all_friend_msg = AllOldMsg.get(FriendUserName).getAsJsonObject();
            //加入这一条
            all_friend_msg.add("" + System.currentTimeMillis(), add_to_old);
            //再把记录寸回去
            AllOldMsg.add(FriendUserName, all_friend_msg);
            return true;
        } else {
            return false;
        }

    }
    
    
    
    /**
     * 发送图片给自己的好友
     *
     * @param FriendUserName 朋友用户名
     * @param MsgContent 消息内容
     * @return boolean 成功 时报
     */
    public boolean SendImgToFriend2(String FriendUserName, String MsgContent) {
        JsonObject use_info = (JsonObject) MyInfo.get("User");

        //要请求的页面
//        String post_url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg?pass_ticket="
//                + WXCookies.get("pass_ticket").toString() + "&lang=zh_CN";
        String post_url = "https://file.wx.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json";
        post_url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsgimg?fun=async&f=json&lang=zh_CN&pass_ticket="
                + WXCookies.get("pass_ticket").toString();
        String filename = "";
        String filepath = "D:\\test";
        try {
			String url ="http://p.jihes.com//1688/avatar/10677_1504086951.jpg";
    		filename = url.split("/")[url.split("/").length-1];
			download(url, filename, "D:\\test");
			filepath = "D:\\test"+"\\"+filename;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        String MediaId = upload4(filepath, WXCookies.get("wxuin").toString(), WXCookies.get("wxsid").toString(), WXCookies.get("skey").toString(), 
        		MyInfo.get("DeviceID").toString(), "RK=kJ/mQF3uGo; eas_sid=V1N5Y0p5R4C604J342i1C6B4e7; tvfe_boss_uuid=5460debd316da1b2; webwxuvid=cc628d84a7ee6b57131133b63e6319f155321e8cc1ffa1de2213355263de31c85152db45f214dde8d7a978f8f6e8479f; pac_uid=1_577409819; pgv_pvi=6488696832; o_cookie=577409819; ptui_loginuin=577409819; ptcz=8104c20f19153f8e39b5169e2536ef6aece8755c710ae24e582e9090480b59a6; pt2gguin=o0577409819; pgv_pvid=4065025336; mm_lang=zh_CN; webwx_auth_ticket=CIsBEKOC5ecCGoABLeoF5TT7DLr0qJxIhiMutJGf0B0W5vNirHIARZ59/+ZnBIRsgbJXQDiuYkoMg3QfGN6MBnbj3Dvgw9vckriaYKaqsllt/JiAkAeEqIrFeiyifF2g/ZWrRR7WV+Ka/igXejiGg6MDpbQZngtiRzcqbeEmIEoYObUnn70omiXqqxs=; wxloadtime=1513565031_expired; wxpluginkey=1513558082; wxuin=1821705001; wxsid=ts4xdiWjxNcYlRAB; webwx_data_ticket=gScMqKVutPuL4y9xKRqBXV0f", 
        		WXCookies.get("pass_ticket").toString(), use_info.get("UserName").getAsString(), FriendUserName, "POST","gScMqKVutPuL4y9xKRqBXV0f",wxMode);
       
        System.err.println("post_url=" + post_url);
        //要请求的数据
        //BaseRequest部分
        //TODO
        JsonObject jsonob = new JsonObject();
        jsonob.addProperty("Uin", WXCookies.get("wxuin").toString());
        jsonob.addProperty("Sid", WXCookies.get("wxsid").toString());
        jsonob.addProperty("Skey", WXCookies.get("skey").toString());
        jsonob.addProperty("DeviceID", MyInfo.get("DeviceID").toString());

        String use_msgid = MakeMsgId();
        //msg部分
        JsonObject jsonmsg = new JsonObject();
        //jsonmsg.addProperty("Type", 1);
        
        jsonmsg.addProperty("ClientMsgId", use_msgid);
        jsonmsg.addProperty("Content", "");
        jsonmsg.addProperty("FromUserName", use_info.get("UserName").getAsString());
        jsonmsg.addProperty("LocalID", use_msgid);
        jsonmsg.addProperty("MediaId", MediaId);
        jsonmsg.addProperty("ToUserName", FriendUserName);
        jsonmsg.addProperty("Type", 3);
       

        JsonObject new_json = new JsonObject();
        new_json.addProperty("UploadType", 2);
        new_json.add("BaseRequest", jsonob);
        new_json.add("Msg", jsonmsg);
        new_json.addProperty("Scene", 0);

        //请求http
        RTHttp rhttp = new RTHttp(post_url);
        rhttp.SetCookies(rhttp.MakeCookies(WXCookies));
        String post_json_string = HashToJsonString(new_json);
        System.err.println("发消息请求参数:" + post_json_string);
        String result_back = rhttp.Post(post_json_string);
        rhttp.close();
        System.err.println(result_back);

        //检查消息结果
        Gson us_gson = new Gson();
        JsonObject us_json = us_gson.fromJson(result_back, JsonObject.class);
        JsonObject BaseResponse = us_json.getAsJsonObject("BaseResponse");
        String ret_text = BaseResponse.get("Ret").toString();
        if (ret_text.equals("0")) {
            //成功后把消息加入老消息记录里
            JsonObject add_to_old = new JsonObject();
            add_to_old.addProperty("msg_content", MsgContent);
            add_to_old.addProperty("who", "me");
            //读出所有词用户的老数据
            if (!AllOldMsg.has(FriendUserName)) {
                AllOldMsg.add(FriendUserName, new JsonObject());
            }
            JsonObject all_friend_msg = AllOldMsg.get(FriendUserName).getAsJsonObject();
            //加入这一条
            all_friend_msg.add("" + System.currentTimeMillis(), add_to_old);
            //再把记录寸回去
            AllOldMsg.add(FriendUserName, all_friend_msg);
            return true;
        } else {
            return false;
        }

    }
    
    /**
     * 发送图片给自己的好友
     *
     * @param FriendUserName 朋友用户名
     * @param MsgContent 消息内容
     * @return boolean 成功 时报
     */
    public  boolean SendImgToFriend3(String FriendUserName, String MsgContent,String url) {
        JsonObject use_info = (JsonObject) MyInfo.get("User");

        //要请求的页面
//        String post_url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg?pass_ticket="
//                + WXCookies.get("pass_ticket").toString() + "&lang=zh_CN";
        String post_url = "https://file.wx.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json";
        post_url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsgimg?fun=async&f=json&lang=zh_CN&pass_ticket="
                + WXCookies.get("pass_ticket").toString();
        System.err.println("wxMode:"+wxMode);
        if(wxMode ==1 ) {
        	  post_url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsgimg?fun=async&f=json&lang=zh_CN&pass_ticket="
                      + WXCookies.get("pass_ticket").toString();
        }
       
        String filename = "";
        String filepath = "D:\\test";
        try {
			//url ="http://p.jihes.com//1688/avatar/10677_1504086951.jpg";
    		filename = url.split("/")[url.split("/").length-1];
    		url = url.replace("https", "http");
			download(url, filename, "D:\\test");
			filepath = "D:\\test"+"\\"+filename;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        String MediaId = upload4(filepath, WXCookies.get("wxuin").toString(), WXCookies.get("wxsid").toString(), WXCookies.get("skey").toString(), 
        		MyInfo.get("DeviceID").toString(), "RK=kJ/mQF3uGo; pgv_pvi=6488696832; eas_sid=V1N5Y0p5R4C604J342i1C6B4e7; tvfe_boss_uuid=5460debd316da1b2; webwxuvid=cc628d84a7ee6b57131133b63e6319f155321e8cc1ffa1de2213355263de31c85152db45f214dde8d7a978f8f6e8479f; pac_uid=1_577409819; o_cookie=577409819; ptui_loginuin=577409819; ptcz=8104c20f19153f8e39b5169e2536ef6aece8755c710ae24e582e9090480b59a6; pt2gguin=o0577409819; pgv_pvid=4065025336; mm_lang=zh_CN; webwx_auth_ticket=CIsBEPS57p4GGoABthVcsOJfxarVyuhFlFfvO7aspK8oEZ+qXDpbjfBHmz4As54zZbSwuRuO7P7u/VEhSMizONeYLGVaj723HDw7tqRT1fHzEhoJAH51LXPATEVGlasEnsvLSPyvsvc5ZTmF/jVFHZqgJfHG0SAYveWdfDrkC6/r6Fm/iJy7SGvoN5Q=; wxloadtime=1512107879_expired; wxpluginkey=1512101025; wxuin=1746033210; wxsid=1D1+KxvjLwDd5Pth; webwx_data_ticket=gScgeVZfSQg8Qtgzrn5Pobfs", 
        		WXCookies.get("pass_ticket").toString(), use_info.get("UserName").getAsString(), FriendUserName, "POST","gSfJ7HZBpoGlbACIDIrctJ1H",wxMode);
       
        System.err.println("post_url=" + post_url);
        //要请求的数据
        //BaseRequest部分
        //TODO
        JsonObject jsonob = new JsonObject();
        jsonob.addProperty("Uin", WXCookies.get("wxuin").toString());
        jsonob.addProperty("Sid", WXCookies.get("wxsid").toString());
        jsonob.addProperty("Skey", WXCookies.get("skey").toString());
        jsonob.addProperty("DeviceID", MyInfo.get("DeviceID").toString());

        String use_msgid = MakeMsgId();
        //msg部分
        JsonObject jsonmsg = new JsonObject();
        //jsonmsg.addProperty("Type", 1);
        
        jsonmsg.addProperty("ClientMsgId", use_msgid);
        jsonmsg.addProperty("Content", "");
        jsonmsg.addProperty("FromUserName", use_info.get("UserName").getAsString());
        jsonmsg.addProperty("LocalID", use_msgid);
        jsonmsg.addProperty("MediaId", MediaId);
        jsonmsg.addProperty("ToUserName", FriendUserName);
        jsonmsg.addProperty("Type", 3);
       

        JsonObject new_json = new JsonObject();
        new_json.addProperty("UploadType", 2);
        new_json.add("BaseRequest", jsonob);
        new_json.add("Msg", jsonmsg);
        new_json.addProperty("Scene", 0);

        //请求http
        RTHttp rhttp = new RTHttp(post_url);
        rhttp.SetCookies(rhttp.MakeCookies(WXCookies));
        String post_json_string = HashToJsonString(new_json);
        System.err.println("发消息请求参数:" + post_json_string);
        String result_back = rhttp.Post(post_json_string);
        rhttp.close();
        System.err.println(result_back);

        //检查消息结果
        Gson us_gson = new Gson();
        JsonObject us_json = us_gson.fromJson(result_back, JsonObject.class);
        JsonObject BaseResponse = us_json.getAsJsonObject("BaseResponse");
        String ret_text = BaseResponse.get("Ret").toString();
        if (ret_text.equals("0")) {
            //成功后把消息加入老消息记录里
            JsonObject add_to_old = new JsonObject();
            add_to_old.addProperty("msg_content", MsgContent);
            add_to_old.addProperty("who", "me");
            //读出所有词用户的老数据
            if (!AllOldMsg.has(FriendUserName)) {
                AllOldMsg.add(FriendUserName, new JsonObject());
            }
            JsonObject all_friend_msg = AllOldMsg.get(FriendUserName).getAsJsonObject();
            //加入这一条
            all_friend_msg.add("" + System.currentTimeMillis(), add_to_old);
            //再把记录寸回去
            AllOldMsg.add(FriendUserName, all_friend_msg);
            return true;
        } else {
            return false;
        }

    }
    
    /**
     * 发送消息给自己的好友
     *
     * @param FriendUserName 朋友用户名
     * @param MsgContent 消息内容
     * @return boolean 成功 时报
     */
    public boolean SendMsgToFriend(String FriendUserName, String MsgContent) {
        JsonObject use_info = (JsonObject) MyInfo.get("User");

        //要请求的页面
//        String post_url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg?pass_ticket="
//                + WXCookies.get("pass_ticket").toString() + "&lang=zh_CN";
        String post_url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg?pass_ticket="
                + WXCookies.get("pass_ticket").toString() + "&lang=zh_CN";
//       post_url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsgimg?pass_ticket="
//                + WXCookies.get("pass_ticket").toString() + "&lang=zh_CN";
        
        
        System.err.println("post_url=" + post_url);
        //要请求的数据
        //BaseRequest部分
        JsonObject jsonob = new JsonObject();
        jsonob.addProperty("Uin", WXCookies.get("wxuin").toString());
        jsonob.addProperty("Sid", WXCookies.get("wxsid").toString());
        jsonob.addProperty("Skey", WXCookies.get("skey").toString());
        jsonob.addProperty("DeviceID", MyInfo.get("DeviceID").toString());

        String use_msgid = MakeMsgId();
        //msg部分
        JsonObject jsonmsg = new JsonObject();
        //jsonmsg.addProperty("Type", 1);
        jsonmsg.addProperty("Type", 3);
        jsonmsg.addProperty("Content", MsgContent);
        jsonmsg.addProperty("FromUserName", use_info.get("UserName").getAsString());
        jsonmsg.addProperty("ToUserName", FriendUserName);
        jsonmsg.addProperty("LocalID", use_msgid);
        jsonmsg.addProperty("ClientMsgId", use_msgid);

        JsonObject new_json = new JsonObject();
        new_json.add("BaseRequest", jsonob);
        new_json.add("Msg", jsonmsg);
        new_json.addProperty("Scene", 0);

        //请求http
        RTHttp rhttp = new RTHttp(post_url);
        rhttp.SetCookies(rhttp.MakeCookies(WXCookies));
        String post_json_string = HashToJsonString(new_json);
        System.err.println("发消息请求参数:" + post_json_string);
        String result_back = rhttp.Post(post_json_string);
        rhttp.close();
        System.err.println(result_back);

        //检查消息结果
        Gson us_gson = new Gson();
        JsonObject us_json = us_gson.fromJson(result_back, JsonObject.class);
        JsonObject BaseResponse = us_json.getAsJsonObject("BaseResponse");
        String ret_text = BaseResponse.get("Ret").toString();
        if (ret_text.equals("0")) {
            //成功后把消息加入老消息记录里
            JsonObject add_to_old = new JsonObject();
            add_to_old.addProperty("msg_content", MsgContent);
            add_to_old.addProperty("who", "me");
            //读出所有词用户的老数据
            if (!AllOldMsg.has(FriendUserName)) {
                AllOldMsg.add(FriendUserName, new JsonObject());
            }
            JsonObject all_friend_msg = AllOldMsg.get(FriendUserName).getAsJsonObject();
            //加入这一条
            all_friend_msg.add("" + System.currentTimeMillis(), add_to_old);
            //再把记录寸回去
            AllOldMsg.add(FriendUserName, all_friend_msg);
            return true;
        } else {
            return false;
        }

    }

    /**
     * 生成时间戳加随机4位
     *
     * @return
     */
    private String MakeMsgId() {
        String time_1 = "" + System.currentTimeMillis();

//        System.err.println("1获得时间戳:" + time_1);
        //最后补上4位随机
        int rand_num = new Random().nextInt(99999);
        while(rand_num<1000) {
        	rand_num = new Random().nextInt(99999);
        }
        String rand_string = ("" + rand_num).substring(0, 4);

//        System.err.println("获得随机4位" + rand_string);
        String use_msgid = time_1.substring(0, 4);
//        System.err.println("组装前4位:" + use_msgid);
        use_msgid = use_msgid + rand_string;
//        System.err.println("加入随机数" + use_msgid);
        use_msgid = use_msgid + time_1.substring(4, time_1.length());
        System.err.println("组装完毕:" + use_msgid);
        return use_msgid;
    }
    
    
    
    
    
    
    
    
//    public String upload(String filePath, String Uin, String Sid, String Skey,
//            String DeviceID, String FromUserName, String ToUserName,
//            long ClientMediaId, String webwx_data_ticket, String pass_ticket) {
//        String boundary = "----WebKitFormBoundaryRVBoUr68fsPbK733"; // 区分每个参数之间
//        String freFix = "--";
//        String newLine = "\r\n";
//        HttpRequest request = HttpRequest
//                .options("https://file.wx.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json");
//        System.out.println("options****"+request.body());
//        request.disconnect();   
//                 
//        String response = "";
//        try {
//             URL uploadUrl = new URL("https://file.wx2.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json");
//             HttpURLConnection uploadConn = (HttpURLConnection)uploadUrl.openConnection();
//             uploadConn.setDoOutput(true);
//             uploadConn.setDoInput(true);
//             uploadConn.setRequestMethod("POST");
//             File file = new File(filePath);
//            if (!file.exists() || !file.isFile()) {
//                throw new IOException("文件不存在");
//            }   
//             uploadConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//             uploadConn.setRequestProperty("Content-Length",file.length()+"");      
//             OutputStream outputStream = uploadConn.getOutputStream();       
// 
//               StringBuffer sb = new StringBuffer();
//               sb.append(freFix+boundary).append(newLine); //id
//               sb.append("Content-Disposition: form-data; name=\"id\"");
//               sb.append(newLine).append(newLine);
//               sb.append("WU_FILE_0").append(newLine);
//                          
//               sb.append(freFix+boundary).append(newLine); //name
//               sb.append("Content-Disposition: form-data; name=\"name\"");
//               sb.append(newLine).append(newLine);
//               sb.append(file.getName()).append(newLine);
// 
//               sb.append(freFix+boundary).append(newLine); //id
//               sb.append("Content-Disposition: form-data; name=\"type\"");
//               sb.append(newLine).append(newLine);
//               sb.append("image/png").append(newLine);
//                
//               sb.append(freFix+boundary).append(newLine);
//               sb.append("Content-Disposition: form-data; name=\"lastModifiedDate\"");
//               sb.append(newLine).append(newLine);
//               sb.append("Wed Dec 21 2016 18:16:37 GMT+0800").append(newLine);
// 
//               sb.append(freFix+boundary).append(newLine);
//               sb.append("Content-Disposition: form-data; name=\"size\"");
//               sb.append(newLine).append(newLine);
//               sb.append(file.length()).append(newLine);
// 
//               sb.append(freFix+boundary).append(newLine);
//               sb.append("Content-Disposition: form-data; name=\"mediatype\"");
//               sb.append(newLine).append(newLine);
//               sb.append("pic").append(newLine);
// 
//               sb.append(freFix+boundary).append(newLine);
//               sb.append("Content-Disposition: form-data; name=\"uploadmediarequest\"");
//               sb.append(newLine).append(newLine);
//               sb.append("{\"UploadType\":2,\"BaseRequest\":{\"Uin\":"+Uin+",\"Sid\":\"+"+Sid+"\",\"Skey\":\"@"+Skey+"\",\"DeviceID\":\""+DeviceID+"\"},\"ClientMediaId\":"+ClientMediaId+",\"TotalLen\":"+file.length()+",\"StartPos\":0,\"DataLen\":"+file.length()+",\"MediaType\":4,\"FromUserName\":\""+FromUserName+"\",\"ToUserName\":\""+ToUserName+"\",\"FileMd5\":\""+getMd5ByFile(file)+"\"}").append(newLine); 
//                
//               sb.append(freFix+boundary).append(newLine);
//               sb.append("Content-Disposition: form-data; name=\"webwx_data_ticket\"");
//               sb.append(newLine).append(newLine);
//               sb.append(webwx_data_ticket).append(newLine);
//                
//               pass_ticket=pass_ticket.toString().replace("%2B", "+").replace("%2F", "/");
//               sb.append(freFix+boundary).append(newLine);
//               sb.append("Content-Disposition: form-data; name=\"pass_ticket\"");
//               sb.append(newLine).append(newLine);
//               sb.append(pass_ticket).append(newLine);
//                
//               sb.append(freFix+boundary).append(newLine);
//               sb.append("Content-Disposition: form-data; name=\"filename\"; filename=\""+file.getName()+"\"");
//               sb.append(newLine);
//               sb.append("Content-Type:image/png");
//               sb.append(newLine).append(newLine);                
//            byte[] sbBytes=sb.toString().getBytes();
//            outputStream.write(sbBytes);
//            StringBuffer endSb = new StringBuffer();
//            endSb.append(newLine).append(freFix+boundary+freFix+newLine);
//                    
//           BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File("d:/1.png")));   
//           byte[] buf = new byte[8096];
//            int size = 0;
//            while((size = bis.read(buf)) != -1){
//                outputStream.write(buf, 0, size);
//            }
//            outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
//            outputStream.close();
// 
//            InputStream inputStream = uploadConn.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            StringBuffer buffer = new StringBuffer();
//            String str = null;
//            while((str = bufferedReader.readLine()) != null){
//                buffer.append(str);
//            }
//            bufferedReader.close();
//            inputStreamReader.close();
//            inputStream.close();
//            inputStream = null;
//            uploadConn.disconnect();
// 
//            System.out.println("buffer.toString()==========="+buffer.toString());
//           
//        } catch (Exception e) {
//            e.printStackTrace();
//        } 
//        System.out.println("response**********" + response);
//        return response;
//    }
    
    
    
    public String upload2(String filePath,
    		String uIn,String sId,String sKey,String deviceid,String cookie,
    		String passTicket,String fromUserName,
    		String toUserName,String type,String webwx_data_ticket) {
        String domain="wx";
        //https://file.wx.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json
//        String uIn = wechatMeta.getWxuin();
//        String sId = wechatMeta.getWxsid();
//        String sKey = wechatMeta.getSkey();
//        String deviceid = wechatMeta.getDeviceId();
        String webwxDataTicket = "";
//        String[] split = wechatMeta.getCookie().split(";");
        String[] split = cookie.split(";");
        for (String str : split) {
            if(str.indexOf("webwx_data_ticket") != -1){
                webwxDataTicket = (str.split("="))[1];
            }
        }
//        String passTicket =  wechatMeta.getPass_ticket();
//        String fromUserName = wechatMeta.getUser().getString("UserName");

        String response = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        HttpsURLConnection conn = null;
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                throw new IOException("文件不存在");
            }

            //请求头参数
            String boundary = "----WebKitFormBoundary4JBBJbdXfb3CXZtq"; //区分每个参数之间
            String freFix = "--";
            String newLine = "\r\n";
            URL urlObj = new URL(null,"https://file.wx.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json",new Handler());
            conn = (HttpsURLConnection) urlObj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setUseCaches(false);
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
            //conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Length", Long.toString(file.length()));//22830
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
            conn.setRequestProperty("Host", "file.wx.qq.com");
            conn.setRequestProperty("Origin", "https://wx.qq.com");
            //conn.setRequestProperty("Pragma", "no-cache");
            conn.setRequestProperty("Referer", "https://wx.qq.com/");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            conn.setRequestProperty("Cookoie", cookie);
            // 请求主体
            StringBuffer sb = new StringBuffer();

            sb.append(freFix+boundary).append(newLine); //这里注意多了个freFix，来区分去请求头中的参数
            sb.append("Content-Disposition: form-data; name=\"id\"");
            sb.append(newLine).append(newLine);
            sb.append("WU_FILE_1").append(newLine);
            
            sb.append(freFix+boundary).append(newLine); //这里注意多了个freFix，来区分去请求头中的参数
            sb.append("Content-Disposition: form-data; name=\"name\"");
            sb.append(newLine).append(newLine);
            sb.append(file.getName()).append(newLine);
            
            sb.append(freFix+boundary).append(newLine); //这里注意多了个freFix，来区分去请求头中的参数
            sb.append("Content-Disposition: form-data; name=\"type\"");
            sb.append(newLine).append(newLine);
            sb.append("image/png").append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"lastModifiedDate\"");
            sb.append(newLine).append(newLine);
            //sb.append("Fri Dec 01 2017 09:46:18 GMT+0800 (中国标准时间)").append(newLine);
            sb.append(new Date()).append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"size\"");
            sb.append(newLine).append(newLine);
            sb.append(file.length()).append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"mediatype\"");
            sb.append(newLine).append(newLine);
            sb.append("pic").append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"uploadmediarequest\"");
            sb.append(newLine).append(newLine);
            sb.append("{\"UploadType\":2,\"BaseRequest\":{\"Uin\":"+uIn+",\"Sid\":\""+sId+"\",\"Skey\":\""+sKey+"\",\"DeviceID\":\""+deviceid+"\"},\"ClientMediaId\":"+System.currentTimeMillis()+",\"TotalLen\":"+file.length()+",\"StartPos\":0,\"DataLen\":"+file.length()+",\"MediaType\":4,\"FromUserName\":"+fromUserName+"\",\"ToUserName\":\""+toUserName+"\",\"FileMd5\":\""+MD5Util.getMD5(file)+"\"}").append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"webwx_data_ticket\"");
            sb.append(newLine);
            sb.append(webwx_data_ticket);
            sb.append(newLine).append(newLine);


            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"pass_ticket\"");
            sb.append(newLine);
            sb.append(passTicket);
            sb.append(newLine).append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"filename\"; filename=\""+filePath+"\"");
            sb.append(newLine);
            sb.append("Content-Type: image/png");
            sb.append(newLine).append(newLine);
            
            // System.out.println(sb.toString());
            //FileOutputStream writer = new FileOutputStream(new File("e:\\img\\Resulsssst.txt"));  
            OutputStream outputStream = new DataOutputStream(conn.getOutputStream());
            outputStream.write(sb.toString().getBytes("utf-8"));//写入请求参数

            DataInputStream dis = new DataInputStream(new FileInputStream(file));  
            int bytes = 0;  
            byte[] bufferOut = new byte[1024];  
            while ((bytes = dis.read(bufferOut)) != -1) {  
                outputStream.write(bufferOut,0,bytes);//写入图片
            }
            outputStream.write(newLine.getBytes());
            outputStream.write((freFix+boundary+freFix+newLine).getBytes("utf-8"));//标识请求数据写入结束
            dis.close();  
            outputStream.close();
            //读取响应信息
            inputStream = conn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            response = buffer.toString();
            System.out.println("response++++++++"+response);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.disconnect();
            }
            try {
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException execption) {

            }
        }
        return response;
    }

//    public String send(String picPath,WechatMeta wechatMeta,String toUserName) throws Exception{
//
//        String filePath = "D:/1.jpg";
//        String fileName = "1.jpg";
//        String uIn = wechatMeta.getWxuin();
//        String sId = wechatMeta.getWxsid();
//        String sKey = wechatMeta.getSkey();
//        String deviceid = wechatMeta.getDeviceId();
//        String cookie = wechatMeta.getCookie();
//        String fromUserName = wechatMeta.getUser().getString("UserName");
//        String result = upload(filePath,wechatMeta,toUserName,"POST"); 
//
//        JSONObject json = JSONObject.parseObject(result);
//        Long currentTimeMillis = System.currentTimeMillis();
//        //String jsonParamsByFile = "{\"BaseRequest\":{\"Uin\":"+uIn+",\"Sid\":\""+sId+"\",\"Skey\":\""+sKey+"\",\"DeviceID\":\""+deviceid+"\"},\"Msg\":{\"Type\":6,\"Content\":\"<appmsg appid=\'wxeb7ec651dd0aefa9\' sdkver=\'\'><title>"+fileName+"</title><des></des><action></action><type>6</type><content></content><url></url><lowurl></lowurl><appattach><totallen>9879</totallen><attachid>"+json.get("MediaId").toString()+"</attachid><fileext>jpg</fileext></appattach><extinfo></extinfo></appmsg>\",\"FromUserName\":\""+fromUserName+"\",\"ToUserName\":\""+toUserName+"\",\"LocalID\":\""+currentTimeMillis+"\",\"ClientMsgId\":\""+currentTimeMillis+"\"}}";
//        String jsonParamsByFile = "{\"BaseRequest\":{\"Uin\":"+uIn+",\"Sid\":\""+sId+"\",\"Skey\":\""+sKey+"\",\"DeviceID\":\""+deviceid+"\"},\"Msg\":{\"Type\":3,"+"\"MediaId\":\""+json.get("MediaId").toString()+"\",\"FromUserName\":\""+fromUserName+"\",\"ToUserName\":\""+toUserName+"\",\"LocalID\":\""+currentTimeMillis+"\",\"ClientMsgId\":\""+currentTimeMillis+"\"},\"Scene\":0}";
//        System.out.println("jsonParamsByFile********"+jsonParamsByFile);     
//        String url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsgimg?fun=async&f=json&lang=zh_CN";  
//
//        HttpClient client = new DefaultHttpClient();
//        HttpPost post = new HttpPost(url);
//        post.addHeader(new BasicHeader("cookie", cookie));//发送文件必须设置,cookie
//
//        try {
//            StringEntity s = new StringEntity(jsonParamsByFile);
//            post.setEntity(s);
//            HttpResponse res = client.execute(post);
//            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
//                HttpEntity entity = res.getEntity();
//                System.out.println(EntityUtils.toString(entity, "utf-8"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }  
//        return json.get("MediaId").toString();
//    }

    
    public static void main(String[] args) {
//    	try {
//    		String url ="https://p.jihes.com//1688/avatar/10677_1504086951.jpg";
//    		String filename = url.split("/")[url.split("/").length-1];
//			download(url, filename, "D:\\test");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	// upload3("d:\\2.png", "1746033210", "9r3HQmgC49YFdyQo", "@crypt_23d175d0_60aa0bf7cc97b98514e2efccbd708c3a", 
    	//		 "e864726629831183", "RK=kJ/mQF3uGo; pgv_pvi=6488696832; eas_sid=V1N5Y0p5R4C604J342i1C6B4e7; tvfe_boss_uuid=5460debd316da1b2; webwxuvid=cc628d84a7ee6b57131133b63e6319f155321e8cc1ffa1de2213355263de31c85152db45f214dde8d7a978f8f6e8479f; pac_uid=1_577409819; o_cookie=577409819; ptui_loginuin=577409819; ptcz=8104c20f19153f8e39b5169e2536ef6aece8755c710ae24e582e9090480b59a6; pt2gguin=o0577409819; pgv_pvid=4065025336; mm_lang=zh_CN; webwx_auth_ticket=CIsBEOq3qssOGoABpGOkyt9OWnUuy3kT7hv/YLaspK8oEZ+qXDpbjfBHmz4As54zZbSwuRuO7P7u/VEhSMizONeYLGVaj723HDw7tqRT1fHzEhoJAH51LXPATEVGlasEnsvLSPyvsvc5ZTmF/jVFHZqgJfHG0SAYveWdfDrkC6/r6Fm/iJy7SGvoN5Q=; wxloadtime=1512108745_expired; wxpluginkey=1512108053; wxuin=1746033210; wxsid=9r3HQmgC49YFdyQo; webwx_data_ticket=gSfJ7HZBpoGlbACIDIrctJ1H", 
        // 		"+Pf49sPLsTABYWttHb+GJuSmneuXL0jvEGRyoY32Fm+GuOob/SfQ7P/4tp8o1ab3", "@70b579ec368b74703042767e68cd32d370fe0851d314b79dede99d047acb1915", "@@034fdebe782acfdce5f7cdcbb6f0b53b65a031eb38968e64ab8acbf15bb15a8d", "POST","gSfJ7HZBpoGlbACIDIrctJ1H");
    	test1();
    
    
    }
    
    private static void test4() {
    	upload4("d:\\test\\10157_15035895701.jpg", "532154160", "aeKLk4FjIoLuRblg", "@crypt_e5d3d3a0_618adbaee85b886013b4d3a5b591279b", "e017324449124551", 
    			"RK=kJ/mQF3uGo; eas_sid=V1N5Y0p5R4C604J342i1C6B4e7; tvfe_boss_uuid=5460debd316da1b2; webwxuvid=cc628d84a7ee6b57131133b63e6319f155321e8cc1ffa1de2213355263de31c85152db45f214dde8d7a978f8f6e8479f; pac_uid=1_577409819; pgv_pvi=6488696832; o_cookie=577409819; luin=o0577409819; lskey=00010000cfebaa5a2b16ff972570c9a4ea21f0e6011446dced0733659899e84f62d00df3347b78723b5ed97a; ptui_loginuin=577409819; ptcz=8104c20f19153f8e39b5169e2536ef6aece8755c710ae24e582e9090480b59a6; pt2gguin=o0577409819; pgv_info=ssid=s8778318818; pgv_pvid=4065025336; mm_lang=zh_CN; webwx_auth_ticket=CIsBEOWBwTwagAFeJAjlJ75yinyKYPdIxa5bkZ/QHRbm82KscgBFnn3/5mcEhGyBsldAOK5iSgyDdB8Y3owGduPcO+DD29ySuJpgpqqyWW38mICQB4SoisV6LKJ8XaD9latFHtZX4pr+KBd6OIaDowOltBmeC2JHNypt4SYgShg5tSefvSiaJeqrGw==; wxloadtime=1515399367_expired; wxpluginkey=1515398757; wxuin=1821705001; wxsid=9ffuoud/iUbcZt9O; webwx_data_ticket=gScZOiOM8FAZIsnUJciwGto6",
    			"o17GpOKaJDOzehtUjxU2UjhOIWv+QD9mvKdJJ7CM5ccJRFBDveT1MGCjFynSZH/h", "@2ff6b83c978ca189da811ae6bed01b00", "@@de0b8eaee8a6f6752239194bcb9c3b09a4bc9a4e4fa58cb63300d62b67ff7500", "POST", "gSdxKtvB7AO17tzdFYh81Wu6",2);
    }
    private static void test3() {
    	upload3("d:\\test\\10157_15035895701.jpg", "1821705001", "aMpBFcx33unnUKp6", "@crypt_edf64d05_c96cb813901a336849a1e6e731181884", "e385885568907452", 
    			"RK=kJ/mQF3uGo; eas_sid=V1N5Y0p5R4C604J342i1C6B4e7; tvfe_boss_uuid=5460debd316da1b2; webwxuvid=cc628d84a7ee6b57131133b63e6319f155321e8cc1ffa1de2213355263de31c85152db45f214dde8d7a978f8f6e8479f; pac_uid=1_577409819; pgv_pvi=6488696832; o_cookie=577409819; luin=o0577409819; lskey=00010000cfebaa5a2b16ff972570c9a4ea21f0e6011446dced0733659899e84f62d00df3347b78723b5ed97a; ptui_loginuin=577409819; ptcz=8104c20f19153f8e39b5169e2536ef6aece8755c710ae24e582e9090480b59a6; pt2gguin=o0577409819; pgv_info=ssid=s8778318818; pgv_pvid=4065025336; mm_lang=zh_CN; webwx_auth_ticket=CIsBEOWBwTwagAFeJAjlJ75yinyKYPdIxa5bkZ/QHRbm82KscgBFnn3/5mcEhGyBsldAOK5iSgyDdB8Y3owGduPcO+DD29ySuJpgpqqyWW38mICQB4SoisV6LKJ8XaD9latFHtZX4pr+KBd6OIaDowOltBmeC2JHNypt4SYgShg5tSefvSiaJeqrGw==; wxloadtime=1515399367_expired; wxpluginkey=1515398757; wxuin=1821705001; wxsid=9ffuoud/iUbcZt9O; webwx_data_ticket=gScZOiOM8FAZIsnUJciwGto6",
    			"isSnwsh7IFqQG9Fj8zhDh8U7kQkh1i7zD46IyFFozIiYL2hjffJ+evAeg5BEwO3b", "@ec29f444d0cebf9da46c8be1f03c05b39d9f6825dc4698a24c047d252b3cea3d", "@@9527446d6adb1ba014adb6fa58fefb377fede3de1fe8ecd8e031970edca22da5", "POST", "gScZOiOM8FAZIsnUJciwGto6");
    }
    private static void test1() {
    	upload4("d:\\test\\10157_15035895701.jpg", "1821705001", "aMpBFcx33unnUKp6", "@crypt_edf64d05_c96cb813901a336849a1e6e731181884", "e385885568907452", 
    			"RK=kJ/mQF3uGo; eas_sid=V1N5Y0p5R4C604J342i1C6B4e7; tvfe_boss_uuid=5460debd316da1b2; webwxuvid=cc628d84a7ee6b57131133b63e6319f155321e8cc1ffa1de2213355263de31c85152db45f214dde8d7a978f8f6e8479f; pac_uid=1_577409819; pgv_pvi=6488696832; o_cookie=577409819; luin=o0577409819; lskey=00010000cfebaa5a2b16ff972570c9a4ea21f0e6011446dced0733659899e84f62d00df3347b78723b5ed97a; ptui_loginuin=577409819; ptcz=8104c20f19153f8e39b5169e2536ef6aece8755c710ae24e582e9090480b59a6; pt2gguin=o0577409819; pgv_info=ssid=s8778318818; pgv_pvid=4065025336; mm_lang=zh_CN; webwx_auth_ticket=CIsBENmAteoMGoABynUf2iIah8YRdorgH4bHGZGf0B0W5vNirHIARZ59/+ZnBIRsgbJXQDiuYkoMg3QfGN6MBnbj3Dvgw9vckriaYKaqsllt/JiAkAeEqIrFeiyifF2g/ZWrRR7WV+Ka/igXejiGg6MDpbQZngtiRzcqbeEmIEoYObUnn70omiXqqxs=; wxloadtime=1515404984_expired; wxpluginkey=1515404625; wxuin=1821705001; wxsid=aMpBFcx33unnUKp6; webwx_data_ticket=gScwomU8jB7USVK2sbWfKa33",
    			"bcEVvonbI0EmrLjKxYrIrVmbGwrzeplDsoXG5JWv5LCVUwCNrF2jR7Pg0On69Rdz", "@0b2a69dcf1919e1e4989f542239da069c3ac8971c7ea997e165698986ce0f1fe", "@@d3e7e5301de785256ff00317d8b57ee8f61192220781d9ba99c58ff34c9446d4", "POST", "80a21cae4e2f4e5528a4aab24c6bfce7", 1);
    }
    
    private static void test2() {
    	upload4("d:\\test\\10157_15035895701.jpg", "2599855613", "QQtjOuOUJPVpkcVG", "@crypt_5325521a_69e82abbb5d00ff8249a664391832248", "e594457129459963", 
    			"RK=kJ/mQF3uGo; eas_sid=V1N5Y0p5R4C604J342i1C6B4e7; tvfe_boss_uuid=5460debd316da1b2; pac_uid=1_577409819; pgv_pvi=6488696832; o_cookie=577409819; webwxuvid=8760abf7abe7953ce3fa948c65100075674cf5363e9a3b84df4a2b5b8954c000d162f8872be5d4a4ce2ac1c8987687ba; luin=o0577409819; lskey=00010000cfebaa5a2b16ff972570c9a4ea21f0e6011446dced0733659899e84f62d00df3347b78723b5ed97a; _qpsvr_localtk=0.784900798253874; pgv_si=s6276167680; ptui_loginuin=577409819; ptisp=cm; ptcz=8104c20f19153f8e39b5169e2536ef6aece8755c710ae24e582e9090480b59a6; uin=o0577409819; skey=@vUsSsNLzE; pt2gguin=o0577409819; pgv_info=ssid=s3105928614; pgv_pvid=4065025336; IED_LOG_INFO2=userUin%3D577409819%26nickName%3D%2525E9%2525BB%252591%2525E9%2525AD%252594%2525E5%2525AF%2525BC%26userLoginTime%3D1515388737; webwx_auth_ticket=CIsBEPjIr+EOGoABgIKTMl6qlS+T+CZZ5C50ky37nEusHmd+fO0TQMnjl/zhdGu82Q3vCwW9jjZzeqCgjzdsBu8cRcxqEBcU6dAkDQ0775/rENU3GX8EAC6clT0qujDSbO1YXHZaaWE8mvbfSw97dGZu7S/hF9bGEYgCYr9MxuT8g4u75e3n/r5QTXc=; mm_lang=zh_CN; wxloadtime=1515389419_expired; wxpluginkey=1515375962; wxuin=2599855613; wxsid=hEdD4UBE7ERv+C95; webwx_data_ticket=gScO9jKEfaxTYMx2cSVq1LG8",
    			"undefined", "@9b5d6682fdb45149ae1eb634a0482935f61771f59f05fc2c0b9239990eca152a", "@@5c4d45400d0a201a42a00df1e977e252d0b30f36cf7bd9c6a320b69c5feb4b2e", "POST", "gScUZYJz5Cl9j8OQB0uLJXC0", 2);
    }
    
    public static String upload3(String filePath,
    		String uIn,String sId,String sKey,String deviceid,String cookie,
    		String passTicket,String fromUserName,
    		String toUserName,String type,String webwx_data_ticket) {
        String domain="wx";
        //https://file.wx.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json
//        String uIn = wechatMeta.getWxuin();
//        String sId = wechatMeta.getWxsid();
//        String sKey = wechatMeta.getSkey();
//        String deviceid = wechatMeta.getDeviceId();
        String webwxDataTicket = "";
//        String[] split = wechatMeta.getCookie().split(";");
        String[] split = cookie.split(";");
        for (String str : split) {
            if(str.indexOf("webwx_data_ticket") != -1){
                webwxDataTicket = (str.split("="))[1];
            }
        }
//        String passTicket =  wechatMeta.getPass_ticket();
//        String fromUserName = wechatMeta.getUser().getString("UserName");

        String response = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        HttpsURLConnection conn = null;
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                throw new IOException("文件不存在");
            }

            //请求头参数
            String boundary = "----WebKitFormBoundaryw0XBZvHInjXnXAkB"; //区分每个参数之间
            String freFix = "--";
            String newLine = "\r\n";
            URL urlObj = new URL(null,"https://file.wx.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json",new Handler());
            conn = (HttpsURLConnection) urlObj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setUseCaches(false);
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
            //conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Length", Long.toString(file.length()));//22830
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
            conn.setRequestProperty("Host", "file.wx.qq.com");
            conn.setRequestProperty("Origin", "https://wx.qq.com");
            //conn.setRequestProperty("Pragma", "no-cache");
            conn.setRequestProperty("Referer", "https://wx.qq.com/");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            conn.setRequestProperty("Cookoie", cookie);
            // 请求主体
            StringBuffer sb = new StringBuffer();

            sb.append(freFix+boundary).append(newLine); //这里注意多了个freFix，来区分去请求头中的参数
            sb.append("Content-Disposition: form-data; name=\"id\"");
            sb.append(newLine).append(newLine);
            sb.append("WU_FILE_0").append(newLine);
            
            sb.append(freFix+boundary).append(newLine); //这里注意多了个freFix，来区分去请求头中的参数
            sb.append("Content-Disposition: form-data; name=\"name\"");
            sb.append(newLine).append(newLine);
            sb.append(file.getName()).append(newLine);
            
            sb.append(freFix+boundary).append(newLine); //这里注意多了个freFix，来区分去请求头中的参数
            sb.append("Content-Disposition: form-data; name=\"type\"");
            sb.append(newLine).append(newLine);
            sb.append("image/png").append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"lastModifiedDate\"");
            sb.append(newLine).append(newLine);
            sb.append("Fri Dec 01 2017 11:59:21 GMT+0800 (中国标准时间)").append(newLine);
            //sb.append(new Date()).append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"size\"");
            sb.append(newLine).append(newLine);
            sb.append(file.length()).append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"mediatype\"");
            sb.append(newLine).append(newLine);
            sb.append("pic").append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"uploadmediarequest\"");
            sb.append(newLine).append(newLine);
            sb.append("{\"UploadType\":2,\"BaseRequest\":{\"Uin\":"+uIn+",\"Sid\":\""+sId+"\",\"Skey\":\""+sKey+"\",\"DeviceID\":\""+deviceid+"\"},\"ClientMediaId\":"+System.currentTimeMillis()+",\"TotalLen\":"+file.length()+",\"StartPos\":0,\"DataLen\":"+file.length()+",\"MediaType\":4,\"FromUserName\":\""+fromUserName+"\",\"ToUserName\":\""+toUserName+"\",\"FileMd5\":\""+MD5Util.getMD5(file)+"\"}").append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"webwx_data_ticket\"");
            sb.append(newLine).append(newLine);
            sb.append(webwx_data_ticket);


            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"pass_ticket\"");
            sb.append(newLine).append(newLine);
            sb.append(passTicket).append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"filename\"; filename=\""+file.getName()+"\"");
            sb.append(newLine);
            sb.append("Content-Type: image/png");
            sb.append(newLine).append(newLine);
            
            // System.out.println(sb.toString());
            //FileOutputStream writer = new FileOutputStream(new File("e:\\img\\Resulsssst.txt"));  
            OutputStream outputStream = new DataOutputStream(conn.getOutputStream());
            outputStream.write(sb.toString().getBytes("utf-8"));//写入请求参数

            DataInputStream dis = new DataInputStream(new FileInputStream(file));  
            int bytes = 0;  
            byte[] bufferOut = new byte[1024];  
            while ((bytes = dis.read(bufferOut)) != -1) {  
                outputStream.write(bufferOut,0,bytes);//写入图片
            }
            outputStream.write(newLine.getBytes());
            outputStream.write((freFix+boundary+freFix+newLine).getBytes("utf-8"));//标识请求数据写入结束
            dis.close();  
            outputStream.close();
            //读取响应信息
            inputStream = conn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            response = buffer.toString();
            System.out.println("response++++++++"+response);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.disconnect();
            }
            try {
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException execption) {

            }
        }
        return response;
    }
    
    
    
    public static String upload4(String filePath,
    		String uIn,String sId,String sKey,String deviceid,String cookie,
    		String passTicket,String fromUserName,
    		String toUserName,String type,String webwx_data_ticket,int wxMode) {
        String domain="wx";
        System.setProperty ("jsse.enableSNIExtension", "false");
        System.err.println("wxMode:"+wxMode);
        //https://file.wx.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json
//        String uIn = wechatMeta.getWxuin();
//        String sId = wechatMeta.getWxsid();
//        String sKey = wechatMeta.getSkey();
//        String deviceid = wechatMeta.getDeviceId();
        String webwxDataTicket = "";
//        String[] split = wechatMeta.getCookie().split(";");
        String[] split = cookie.split(";");
        for (String str : split) {
            if(str.indexOf("webwx_data_ticket") != -1){
                webwxDataTicket = (str.split("="))[1];
            }
        }
//        String passTicket =  wechatMeta.getPass_ticket();
//        String fromUserName = wechatMeta.getUser().getString("UserName");

        String response = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        HttpsURLConnection conn = null;
        String MediaId = "";
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                throw new IOException("文件不存在");
            }

            //请求头参数555
            String boundary = "----WebKitFormBoundaryWzAjHw4uBBtcmJ9s"; //区分每个参数之间
            String freFix = "--";
            String newLine = "\r\n";
            URL urlObj;
            if(wxMode==2) {
            	 urlObj = new URL(null,"https://file.wx2.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json",new Handler());
            }else {
            	 urlObj = new URL(null,"https://file.wx.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json",new Handler());
            }
         
            conn = (HttpsURLConnection) urlObj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setUseCaches(false);
            conn.setRequestProperty("Accept", "*/*");
            //conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");//wx域名加这个出事
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
            //conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Encoding", "gzip");//wx1
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Length", Long.toString(file.length()));//22830
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
            conn.setRequestProperty("Host", "file.wx.qq.com");
            conn.setRequestProperty("Origin", "https://wx.qq.com");
            //conn.setRequestProperty("Pragma", "no-cache");
            conn.setRequestProperty("Referer", "https://wx.qq.com/?&lang=zh_CN");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            conn.setRequestProperty("Cookoie", cookie);
            // 请求主体
            StringBuffer sb = new StringBuffer();

            sb.append(freFix+boundary).append(newLine); //这里注意多了个freFix，来区分去请求头中的参数
            sb.append("Content-Disposition: form-data; name=\"id\"");
            sb.append(newLine).append(newLine);
            sb.append("WU_FILE_1").append(newLine);
            
            sb.append(freFix+boundary).append(newLine); //这里注意多了个freFix，来区分去请求头中的参数
            sb.append("Content-Disposition: form-data; name=\"name\"");
            sb.append(newLine).append(newLine);
            sb.append(file.getName()).append(newLine);
            
            sb.append(freFix+boundary).append(newLine); //这里注意多了个freFix，来区分去请求头中的参数
            sb.append("Content-Disposition: form-data; name=\"type\"");
            sb.append(newLine).append(newLine);
            sb.append("image/jpeg").append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"lastModifiedDate\"");
            sb.append(newLine).append(newLine);
            Date now = new Date();
    		String[] nows = now.toString().split(" ");
    		//String sj = nows[0] + " " + nows[1] + " " + nows[2] + " " + nows[5] + " " + nows[3] + " " + "GMT+0800 (中国标准时间)";
            sb.append("Mon Jan 08 2018 11:46:57 GMT+0800 (中国标准时间)").append(newLine);
           // sb.append(sj).append(newLine);
            //sb.append(new Date()).append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"size\"");
            sb.append(newLine).append(newLine);
            sb.append(file.length()).append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"mediatype\"");
            sb.append(newLine).append(newLine);
            sb.append("pic").append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"uploadmediarequest\"");
            sb.append(newLine).append(newLine);
            sb.append("{\"UploadType\":2,\"BaseRequest\":{\"Uin\":"+uIn+",\"Sid\":\""+sId+"\",\"Skey\":\""+sKey+"\",\"DeviceID\":\""+deviceid+"\"},\"ClientMediaId\":"+System.currentTimeMillis()+",\"TotalLen\":"+file.length()+",\"StartPos\":0,\"DataLen\":"+file.length()+",\"MediaType\":4,\"FromUserName\":\""+fromUserName+"\",\"ToUserName\":\""+toUserName+"\",\"FileMd5\":\""+MD5Util.getMD5(file)+"\"}").append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"webwx_data_ticket\"");
            sb.append(newLine).append(newLine);
            sb.append(webwx_data_ticket).append(newLine);


            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"pass_ticket\"");
            sb.append(newLine).append(newLine);
            sb.append(passTicket).append(newLine);

            sb.append(freFix+boundary).append(newLine);
            sb.append("Content-Disposition: form-data; name=\"filename\"; filename=\""+file.getName()+"\"");
            sb.append(newLine);
            sb.append("Content-Type: image/jpeg");
            sb.append(newLine).append(newLine);
            //sb.append(newLine);
            
          // sb.append(freFix+boundary).append(freFix);
            // System.out.println(sb.toString());
            //FileOutputStream writer = new FileOutputStream(new File("e:\\img\\Resulsssst.txt"));  
            OutputStream outputStream = new DataOutputStream(conn.getOutputStream());
            String charsetName = "utf-8";
            outputStream.write(sb.toString().getBytes(charsetName));//写入请求参数

            DataInputStream dis = new DataInputStream(new FileInputStream(file));  
            int bytes = 0;  
            byte[] bufferOut = new byte[1024];  
            while ((bytes = dis.read(bufferOut)) != -1) {  
                outputStream.write(bufferOut,0,bytes);//写入图片
            }
            outputStream.write(newLine.getBytes());
            outputStream.write((freFix+boundary+freFix+newLine).getBytes(charsetName));//标识请求数据写入结束
            dis.close();  
            outputStream.close();
            //读取响应信息
            inputStream = conn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, charsetName);
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            response = buffer.toString();
            System.err.println("response:"+response);
            JSONObject jsob =(JSONObject) JSONObject.parse(response);
            MediaId = (String) jsob.get("MediaId");
            System.out.println("response++++++++"+response);
            Logger.getLogger(WeiXinAuto.class.getName()).log(Level.WARNING, "response++++++++"+response , new Object());
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(WeiXinAuto.class.getName()).log(Level.WARNING, "上传图片异常" , new Object());
            return "";
        }finally{
            try {
		    	if(conn!=null){
		             conn.disconnect();
		        }
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException execption) {
            	 execption.printStackTrace();
                 Logger.getLogger(WeiXinAuto.class.getName()).log(Level.WARNING, "上传图片异常finally" , new Object());
                 return "";
            }
        }
        return MediaId;
    }
    
    
//    public static void send(String filePath,String cookie,String uIn,String sId,String sKey,String fileName,String fromUserName,String toUserName) throws JSONException{
//        filePath = "C:/Users/klay/Desktop/helloWorld.xlsx";
//        uIn = "1";
//        sId = "1";
//        sKey = "11";
//        fileName = "helloWorld.xls";
//        int mediaType = 4;
//        String result = upload(filePath,uIn,sId,sKey,mediaType);//执行图片上传，返回流媒体id。PS：微信网页版中的发送文件/图片/等分为两步1.上传到服务器拿到返回的mediaId,2.发送通知消息
//
//        JSONObject json = new JSONObject(result);
//        String mediaId = json.get("MediaId").toString();
//        System.out.println(json.get("MediaId"));
//
//        //发送图片
//        Long currentTimeMillis = System.currentTimeMillis();
//
//        String jsonParamsByFile = "{\"BaseRequest\":{\"Uin\":"+uIn+",\"Sid\":\""+sId+"\",\"Skey\":\"@"+sKey+"\",\"DeviceID\":\"e640359774620125\"},\"Msg\":{\"Type\":6,\"Content\":\"<appmsg appid=\'wxeb7ec651dd0aefa9\' sdkver=\'\'><title>"+fileName+"</title><des></des><action></action><type>6</type><content></content><url></url><lowurl></lowurl><appattach><totallen>9879</totallen><attachid>"+mediaId+"</attachid><fileext>xlsx</fileext></appattach><extinfo></extinfo></appmsg>\",\"FromUserName\":\"@"+fromUserName+"\",\"ToUserName\":\"@@"+toUserName+"\",\"LocalID\":\""+currentTimeMillis+"\",\"ClientMsgId\":\""+currentTimeMillis+"\"}}";
//
//        cookie  = "";
//        System.out.println(jsonParamsByFile);
//        String url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxsendappmsg?fun=async&f=json&lang=zh_CN";  
//        HttpClient client = new DefaultHttpClient();
//        HttpPost post = new HttpPost(url);
//        post.addHeader(new BasicHeader("cookie", cookie));//发送文件必须设置,cookie
//
//        try {
//            StringEntity s = new StringEntity(jsonParamsByFile);
//            post.setEntity(s);
//            HttpResponse res = client.execute(post);
//            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
//                HttpEntity entity = res.getEntity();
//                System.out.println(EntityUtils.toString(entity, "utf-8"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }   
//    }
    
    
	public static void download(String urlString, String filename, String savePath) throws Exception {
		// 构造URL
		URL url = new URL(urlString);
		// 打开连接
		URLConnection con = url.openConnection();
		// 设置请求超时为5s
		con.setConnectTimeout(5 * 1000);
		// 输入流
		InputStream is = con.getInputStream();

		// 1K的数据缓冲
		byte[] bs = new byte[1024];
		// 读取到的数据长度
		int len;
		// 输出的文件流
		File sf = new File(savePath);
		if (!sf.exists()) {
			sf.mkdirs();
		}
		OutputStream os = new FileOutputStream(sf.getPath() + "\\" + filename);
		// 开始读取
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		// 完毕，关闭所有链接
		os.close();
		is.close();
		
	}   
	
	
	
	
	 /**
     * 获取微信群组列表 (使用新线程)
     */
    private void GetContactGroup() {
        new Thread(() -> {
            String post_url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxbatchgetcontact?type=ex&lang=zh_CN&r="+System.currentTimeMillis()+"&pass_ticket="+URLEncoder.encode(WXCookies.get("pass_ticket").toString());
//	https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxbatchgetcontact?type=ex&r=xxx&lang=zh_CN&pass_ticket=xxx
            //?type=ex&lang=zh_CN&r="+System.currentTimeMillis()+"&pass_ticket="+URLEncoder.encode(WXCookies.get("pass_ticket").toString();
            JsonObject jsonob = new JsonObject();
            jsonob.addProperty("Uin", WXCookies.get("wxuin").toString());
            jsonob.addProperty("Sid", WXCookies.get("wxsid").toString());
            jsonob.addProperty("Skey", WXCookies.get("skey").toString());
            jsonob.addProperty("DeviceID", MyInfo.get("DeviceID").toString());

            JsonObject new_jsonob = new JsonObject();
            new_jsonob.add("BaseRequest", jsonob);
            
            
           JsonObject jb = new JsonObject();
           JsonArray ja = new JsonArray();
          // JSONArray ja = new JSONArray();
           int count = 0;
            for(Object friend : AllLastCallFriends) {
            	jb = new JsonObject();
            	String f =(String) friend;
            	if(f.contains("@@")) {
            		jb.addProperty("UserName", f);
            		jb.addProperty("EncryChatRoomId", "");
            		ja.add(jb);
            	}
            }
            new_jsonob.add("List", ja);
            new_jsonob.addProperty("Count", ja.size());
            
            String post_json_string = HashToJsonString(new_jsonob);

            RTHttp rhttp = new RTHttp(post_url);
            rhttp.SetCookies(rhttp.MakeCookies(WXCookies));
            String result_string = rhttp.Post(post_json_string);
            rhttp.close();

            //更新之前获取的最近联系人列表
            Gson json = new Gson();
            JsonObject new_json = json.fromJson(result_string, JsonObject.class);
            //因为之前初始化的时候获得了最近联系人,这里要把之前的追加进这里
            JsonArray all_friends = new_json.get("MemberList").getAsJsonArray();
            //循环之前的,一个个加入
            Iterator<JsonElement> json_iter = AllfriendsList.iterator();
            while (json_iter.hasNext()) {
                JsonObject next = (JsonObject) json_iter.next();
                //只加入不存在的,并且@存在
                if (!all_friends.contains(next)) {
                    all_friends.add(next);
                }

            }
            AllfriendsList = all_friends.getAsJsonArray();

            System.err.println("正在刷新朋友列表");
            //刷新界面
            ChangeShow.ShowAllFriendsList(WXCookies.get("wxuin").toString());

            System.err.println("获取联系人信息完毕!");
//        System.err.println(result_string);
        }).start();

    }
    
    
    /**
     * 发送图片给自己的好友
     *
     * @param FriendUserName 朋友用户名
     * @param MsgContent 消息内容
     * @return boolean 成功 时报
     */
    public  boolean SendImgToFriend4(String FriendUserName, String MsgContent,String url,int number) {
        JsonObject use_info = (JsonObject) MyInfo.get("User");

        //要请求的页面
//        String post_url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg?pass_ticket="
//                + WXCookies.get("pass_ticket").toString() + "&lang=zh_CN";
        String post_url = "https://file.wx.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json";
        post_url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsgimg?fun=async&f=json&lang=zh_CN&pass_ticket="
                + WXCookies.get("pass_ticket").toString();
        System.err.println("wxMode:"+wxMode);
        if(wxMode ==1 ) {
        	  post_url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsgimg?fun=async&f=json&lang=zh_CN&pass_ticket="
                      + WXCookies.get("pass_ticket").toString();
        }
       
        String filename = "";
        String filepath = "D:\\test";
        try {
			//url ="http://p.jihes.com//1688/avatar/10677_1504086951.jpg";
    		filename = url.split("/")[url.split("/").length-1];
    		String[] f =filename.split("\\.");
    		filename = f[0]+number +"."+f[1];
    		url = url.replace("https", "http");
			download(url, filename, "D:\\test");
			filepath = "D:\\test"+"\\"+filename;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        String MediaId = upload4(filepath, WXCookies.get("wxuin").toString(), WXCookies.get("wxsid").toString(), WXCookies.get("skey").toString(), 
        		MyInfo.get("DeviceID").toString(), "RK=kJ/mQF3uGo; eas_sid=V1N5Y0p5R4C604J342i1C6B4e7; tvfe_boss_uuid=5460debd316da1b2; webwxuvid=cc628d84a7ee6b57131133b63e6319f155321e8cc1ffa1de2213355263de31c85152db45f214dde8d7a978f8f6e8479f; pac_uid=1_577409819; pgv_pvi=6488696832; o_cookie=577409819; ptui_loginuin=577409819; pgv_si=s9543344128; ptisp=cm; ptcz=8104c20f19153f8e39b5169e2536ef6aece8755c710ae24e582e9090480b59a6; luin=o0577409819; lskey=00010000430a335546b357251f93d53e0846f1bbf39f2ee167e4b2528d52a1f5283b1504c65f5abf5c3eea03; uin=o0577409819; skey=@ue0Liy0Fe; pt2gguin=o0577409819; pgv_info=ssid=s8165772225; pgv_pvid=4065025336; IED_LOG_INFO2=userUin%3D577409819%26nickName%3D%2525E9%2525BB%252591%2525E9%2525AD%252594%2525E5%2525AF%2525BC%26userLoginTime%3D1515049071; mm_lang=zh_CN; webwx_auth_ticket=CIsBEP6BjYUCGoABbGp804jL4g962nuNXmoES5Gf0B0W5vNirHIARZ59/+ZnBIRsgbJXQDiuYkoMg3QfGN6MBnbj3Dvgw9vckriaYKaqsllt/JiAkAeEqIrFeiyifF2g/ZWrRR7WV+Ka/igXejiGg6MDpbQZngtiRzcqbeEmIEoYObUnn70omiXqqxs=; wxloadtime=1515056726_expired; wxpluginkey=1515054600; wxuin=1821705001; wxsid=9INFkBsuQoXNH6RZ; webwx_data_ticket=gSdrmvJzPB/bSBsM/UoRKg9X", 
        		WXCookies.get("pass_ticket").toString(), use_info.get("UserName").getAsString(), FriendUserName, "POST","gSdrmvJzPB/bSBsM/UoRKg9X",wxMode);
        if(MediaId.equals("")) {
        	Logger.getLogger(WeiXinAuto.class.getName()).log(Level.WARNING,"上传图片接口失败，mediaId为空", new Object());
        	return false;
        }
        System.err.println("post_url=" + post_url);
        //要请求的数据
        //BaseRequest部分
        //TODO
        JsonObject jsonob = new JsonObject();
        jsonob.addProperty("Uin", WXCookies.get("wxuin").toString());
        jsonob.addProperty("Sid", WXCookies.get("wxsid").toString());
        jsonob.addProperty("Skey", WXCookies.get("skey").toString());
        jsonob.addProperty("DeviceID", MyInfo.get("DeviceID").toString());

        String use_msgid = MakeMsgId();
        //msg部分
        JsonObject jsonmsg = new JsonObject();
        //jsonmsg.addProperty("Type", 1);
        
        jsonmsg.addProperty("ClientMsgId", use_msgid);
        jsonmsg.addProperty("Content", "");
        jsonmsg.addProperty("FromUserName", use_info.get("UserName").getAsString());
        jsonmsg.addProperty("LocalID", use_msgid);
        jsonmsg.addProperty("MediaId", MediaId);
        jsonmsg.addProperty("ToUserName", FriendUserName);
        jsonmsg.addProperty("Type", 3);
       

        JsonObject new_json = new JsonObject();
        new_json.addProperty("UploadType", 2);
        new_json.add("BaseRequest", jsonob);
        new_json.add("Msg", jsonmsg);
        new_json.addProperty("Scene", 0);

        //请求http
        RTHttp rhttp = new RTHttp(post_url);
        rhttp.SetCookies(rhttp.MakeCookies(WXCookies));
        String post_json_string = HashToJsonString(new_json);
        System.err.println("发消息请求参数:" + post_json_string);
        String result_back = "";
		try {
			result_back = rhttp.Post(post_json_string);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(WeiXinAuto.class.getName()).log(Level.WARNING,"微信发送图片接口异常", new Object());
			rhttp.close();
			return false;
		}
        rhttp.close();
        System.err.println(result_back);

        //检查消息结果
        Gson us_gson = new Gson();
        JsonObject us_json = us_gson.fromJson(result_back, JsonObject.class);
        JsonObject BaseResponse = us_json.getAsJsonObject("BaseResponse");
        String ret_text = BaseResponse.get("Ret").toString();
        if (ret_text.equals("0")) {
            //成功后把消息加入老消息记录里
            JsonObject add_to_old = new JsonObject();
            add_to_old.addProperty("msg_content", MsgContent);
            add_to_old.addProperty("who", "me");
            //读出所有词用户的老数据
            if (!AllOldMsg.has(FriendUserName)) {
                AllOldMsg.add(FriendUserName, new JsonObject());
            }
            JsonObject all_friend_msg = AllOldMsg.get(FriendUserName).getAsJsonObject();
            //加入这一条
            all_friend_msg.add("" + System.currentTimeMillis(), add_to_old);
            //再把记录寸回去
            AllOldMsg.add(FriendUserName, all_friend_msg);
            return true;
        } else {
            return false;
        }

    }
    
}
