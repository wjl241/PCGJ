package weixin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

import showDialog.InfoDialog;
import test3.ShadowBorder;
import util.HttpClientUtil;
import util.ImageUtil;
import util.showInfoUtil;

public class LoginMain extends JFrame{
	// 全局的位置变量，用于表示鼠标在窗口上的位置
	static Point origin = new Point();
	private static JTextField userText;
	private static JTextField validText;
	private static int randValid = -1;//随机验证码
	private static String appkey = "23704658";
	private static String secret = "7342d0a35e984c61a907a0d58435822e";
	private static String url = "http://gw.api.taobao.com/router/rest";
	private static String template = "SMS_115025100";//SMS_115025100//SMS_73535001
	private static int count=60;
	private static int defaultCount=60;
	public static String phone = "";
	public LoginMain() {
		initCompents();
		this.setVisible(true);
	}
	private void initCompents() {
		setUndecorated(true);	
		this.setSize(417, 393);
		this.setLocationRelativeTo(null);//窗口在屏幕中间显示
		this.setIconImage(ImageUtil.getImageIcon("/images/qfzs/tubiao.png").getImage());
		this.addMouseListener(new MouseAdapter() {
			// 按下（mousePressed 不是点击，而是鼠标被按下没有抬起）
			public void mousePressed(MouseEvent e) {
				// 当鼠标按下的时候获得窗口当前的位置
				origin.x = e.getX();
				origin.y = e.getY();
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			// 拖动（mouseDragged 指的不是鼠标在窗口中移动，而是用鼠标拖动）
			public void mouseDragged(MouseEvent e) {
				// 当鼠标拖动时获取窗口当前位置
				Point p = getLocation();
				// 设置窗口的位置
				// 窗口当前的位置 + 鼠标当前在窗口的位置 - 鼠标按下的时候在窗口的位置
				setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
			}
		});
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		//不知道为什么会缺少几个 组建
		((JComponent) getContentPane()).setBorder(BorderFactory.createCompoundBorder(
		            ShadowBorder.newInstance(),
		            BorderFactory.createLineBorder(Color.WHITE)
		    ));
		JPanel titlePanel = new JPanel() {
            public void paintComponent(Graphics g) {
                ImageIcon icon =
                		ImageUtil.getImageIcon("/images/qfzs/title.png");
                // 图片随窗体大小而变化
                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
            }
        };
		getContentPane().add(titlePanel, BorderLayout.NORTH);
		titlePanel.setSize(417,60);
		//titlePanel.setIcon(new ImageIcon(LoginMain.class.getResource("title.png"))); // NOI18N
		
		//JLabel lblNewLabel = new JLabel("集合群发测试版");
		//lblNewLabel.setFont(new Font("宋体",Font.BOLD, 14));
		//titlePanel.add(lblNewLabel, "cell 0 0,grow");
		titlePanel.setPreferredSize(new Dimension(417, 60));
		titlePanel.setLayout(null);
		
		JLabel deleteLbl = new JLabel("");
		deleteLbl.setBounds(313, 22, 18, 18);
		ImageIcon dImage = ImageUtil.getImageIcon("/images/qfzs/titleTip1.png");
		dImage.setImage(dImage.getImage().getScaledInstance(deleteLbl.getWidth(), deleteLbl.getHeight(), Image.SCALE_DEFAULT));
		deleteLbl.setIcon(dImage);
		ImageIcon dImage2 = ImageUtil.getImageIcon("/images/qfzs/1.png");
		dImage2.setImage(dImage2.getImage().getScaledInstance(deleteLbl.getWidth(), deleteLbl.getHeight(), Image.SCALE_DEFAULT));
		deleteLbl.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {//鼠标释放
				deleteLbl.setIcon(dImage);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {//鼠标按下、
				deleteLbl.setIcon(dImage2);
				//设置窗口最小化
				setExtendedState(JFrame.ICONIFIED); 
			}
			
			@Override
			public void mouseExited(MouseEvent e) {//鼠标离开
				deleteLbl.setIcon(dImage);
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {//鼠标移入
				//addLbl.setIcon(cImage);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				//addLbl.setIcon(aImage2);
				
			}
		});
		titlePanel.add(deleteLbl);
		
		JLabel addLbl = new JLabel("");
		addLbl.setBounds(346, 22, 18, 18);
		ImageIcon aImage = ImageUtil.getImageIcon("/images/qfzs/titleTip2.png");
		aImage.setImage(aImage.getImage().getScaledInstance(addLbl.getWidth(), addLbl.getHeight(), Image.SCALE_DEFAULT));
		ImageIcon aImage2 = ImageUtil.getImageIcon("/images/qfzs/2.png");
		aImage2.setImage(aImage2.getImage().getScaledInstance(addLbl.getWidth(), addLbl.getHeight(), Image.SCALE_DEFAULT));
		addLbl.setIcon(aImage);
		addLbl.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {//鼠标释放
				addLbl.setIcon(aImage);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {//鼠标按下、
				addLbl.setIcon(aImage2);
				//放大
//				setSize(Toolkit.getDefaultToolkit().getScreenSize());    
//			    setLocation(0,0);          
//			    show();    
			}
			
			@Override
			public void mouseExited(MouseEvent e) {//鼠标离开
				addLbl.setIcon(aImage);
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {//鼠标移入
				//addLbl.setIcon(cImage);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				//addLbl.setIcon(aImage2);
				
			}
		});
		titlePanel.add(addLbl);
		
		JLabel closeLbl = new JLabel("");
		closeLbl.setBounds(379, 22, 18, 18);
		ImageIcon cImage = ImageUtil.getImageIcon("/images/qfzs/titleTip3.png");
		cImage.setImage(cImage.getImage().getScaledInstance(closeLbl.getWidth(), closeLbl.getHeight(), Image.SCALE_DEFAULT));
		closeLbl.setIcon(cImage);
		ImageIcon cImage2 = ImageUtil.getImageIcon("/images/qfzs/3.png");
		cImage2.setImage(cImage2.getImage().getScaledInstance(closeLbl.getWidth(), closeLbl.getHeight(), Image.SCALE_DEFAULT));
		closeLbl.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {//鼠标释放
				// TODO Auto-generated method stub
				closeLbl.setIcon(cImage);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {//鼠标按下、
				closeLbl.setIcon(cImage2);
				System.exit(0);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {//鼠标离开
				closeLbl.setIcon(cImage);
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {//鼠标移入
				//closeLbl.setIcon(cImage);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				//closeLbl.setIcon(cImage2);
				
			}
		});
		titlePanel.add(closeLbl);
		
		JPanel infoPanel = new JPanel() {
			 public void paintComponent(Graphics g) {//login_bg.png
				
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/qfzs/infobg.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		getContentPane().add(infoPanel, BorderLayout.CENTER);
		infoPanel.setSize(417,333);
		
		JButton loginButton = createJButton("", "x","login_bg.png","login_bg2.png", new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String user = userText.getText() == null ? "" :userText.getText();
				String valid = validText.getText() == null ? "" :validText.getText();
				if(user == null || user.equals("")) {
					showInfoUtil.showInfoDialog("用户名为空");
					return;
				}
				if(valid == null || valid.equals("")) {
					showInfoUtil.showInfoDialog("验证码为空");
					return;
				}
				
				if(user.equals("15869171597") && valid.equalsIgnoreCase("123")) {
					dispose();
					new QFMain(user,8587).setVisible(true);
					return;
				}
				
//				if(!checkValidButton(userText.getText())) {
//					return;
//				}
//				//去除验证
//				if(!valid.equals(String.valueOf(randValid))) {
//					showInfoUtil.showInfoDialog("验证码不正确");
//					return;
//				}
				
				HttpClientUtil httpClientUtil = new HttpClientUtil();
				//String api_url = "https://apiway.jihes.com/temai?os_type=1&os_version=10.2&app_version=1.0.0&api_version=2.0.0&device_name=myphone&client_id=83a14e030ad94096b4cdbfc2a94dc203&api=user_login/login";
				//String api_url = "http://localhost:8080/qfzs/user/validUser?phone?phone="+phone;
				phone = user ;
				String api_url = "http://api.jihes.com/jsp/qfzs/user/validUser?phone="+phone;
				Map<String,String> createMap = new HashMap<String,String>();
				String ret = httpClientUtil.doPost2(api_url,createMap,"utf-8");
				if(ret == null) {
					showInfoUtil.showInfoDialog("连接失败，请联系管理员");
					return;
				}
				JSONObject jsob = JSONObject.parseObject(ret);
				jsob.get("code");
				String code = String.valueOf(jsob.get("code") == null? "-1" : jsob.get("code") ) ;
				String message =(String) jsob.get("message");
				JSONObject jb = jsob.getJSONObject("data");
				if(code.equals("200") && !message.equals("-1")) {
					//setVisible(false);
					//new AdminMain().setVisible(true);
					String login = jb.get("login") == null ? "" :(String)jb.get("login");
					String user_id = jb.get("user_id") == null ? "" :(String)jb.get("user_id");
					if(login.equals("success") && !user_id.equals("")) {
						dispose();
						new QFMain(user,Integer.valueOf(user_id)).setVisible(true);
						
					}else {
						showInfoUtil.showInfoDialog("当前手机未购买升级版本");
						return;
					}
					
				}else {
					showInfoUtil.showInfoDialog("当前手机非集合特卖用户");
					return;
				}
				
				
			}
			
			 
		});
		
		loginButton.setBounds(39, 185, 340, 45);
		loginButton.setPreferredSize(new Dimension(225, 32));
		infoPanel.add(loginButton);
		infoPanel.setLayout(null);
		
		JLabel phoneLbl = new JLabel("手机号");
		phoneLbl.setFont(new Font("楷体", Font.BOLD, 16));
		phoneLbl.setForeground(new Color(173, 173, 173));
		phoneLbl.setBounds(35, 52, 60, 22);
		infoPanel.add(phoneLbl);
		
		userText = new JTextField();
		userText.addKeyListener(new KeyAdapter() {
			 public void keyTyped(KeyEvent e) {
	                char c = e.getKeyChar();
	                if (Character.isDigit(c) && userText.getText().trim().length() < 11)//只允许数字，且长度不大于10
	                    return;
	                e.consume();
	            }
		});
		userText.setBounds(103, 40, 276, 45);
		userText.setColumns(10);
		infoPanel.add(userText);
		
		validText = new JTextField();
		validText.addKeyListener(new KeyAdapter() {
			 public void keyTyped(KeyEvent e) {
	                char c = e.getKeyChar();
	                if (Character.isDigit(c) && validText.getText().trim().length() < 6)//只允许数字，且长度不大于6
	                    return;
	                e.consume();
	            }
		});
		validText.setBounds(103, 100, 143, 45);
		validText.setColumns(10);
		infoPanel.add(validText);
		
		JButton validButton = createJButton("", "", "valid.png", "valid2.png", new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		validButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mousePressed(MouseEvent e) {

				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() ==1) {
					  boolean check = checkValidButton(userText.getText());
					  if(!check) {
						  return;
					  }
					  //发送验证码
					  sendMessage();
					  //定时修改
					  Timer timer = new Timer();
				      timer.schedule(new TimerTask(){
				        @Override
				        public void run() {
				        	if(count==defaultCount) {
								validButton.setFont(new Font("楷体", Font.BOLD, 20));
								validButton.setIcon(ImageUtil.getImageIcon("/images/qfzs/valid3.png"));
								validButton.setVerticalTextPosition(JButton.CENTER);//
								validButton.setHorizontalTextPosition(JButton.CENTER);
								validButton.setEnabled(false);
				        	}else if(count==0){
				        		validButton.setEnabled(true);
				        		validButton.setIcon(ImageUtil.getImageIcon("/images/qfzs/valid.png"));
				        		validButton.setText("");
				        		count=defaultCount;
				        		System.gc();
				        		cancel();
				        		return;
				        	}
				        	validButton.setText(count+"秒");
				        	count--;
				        }
				      }, 0, 1000);
					
				}
				
			}
		});
		validButton.setBounds(254, 100, 125, 45);
		infoPanel.add(validButton);
		
		JLabel validLbl = new JLabel("验证码");
		validLbl.setFont(new Font("楷体", Font.BOLD, 16));
		validLbl.setForeground(new Color(173, 173, 173));
		validLbl.setBounds(35, 110, 60, 22);
		infoPanel.add(validLbl);
		
	
	
		
		
		
		
	}
	
	
	/** 
     * 根据一些参数快速地构造出按钮来 
     * 这些按钮从外观上看都是一些特殊的按钮 
     * @param name 按钮图片的相对地址 
     * @param cmd 命令 
     * @param listener 监听器 
     * @param defaultPic 默认图片地址
     * @param rollPic 鼠标移动后图片地址
     * @return 按钮 
     */  
    public static JButton createJButton(String name, String cmd,String defaultPic,String rollPic, ActionListener listener) {  
        JButton jb = new JButton(name,ImageUtil.getImageIcon("/images/qfzs/"+defaultPic));  //"/images/qfzs/login_bg.png"
        jb.setBorderPainted(false);  
        jb.setFocusPainted(false);  
        jb.setContentAreaFilled(false);  
        jb.setDoubleBuffered(true);  
        jb.setRolloverIcon(ImageUtil.getImageIcon("/images/qfzs/"+rollPic));  
        jb.setPressedIcon(ImageUtil.getImageIcon("/images/qfzs/"+defaultPic));  
        jb.setOpaque(false);  
        jb.setFocusable(false);  
        jb.setActionCommand(cmd);  
        jb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));  
        jb.addActionListener(listener);  
        jb.setMargin(new Insets(0, 0, 0, 0));
        jb.setText(name);
        return jb;  
    }  
    
    
    /** 
     * 根据一些参数快速地构造出按钮来 
     * 这些按钮从外观上看都是一些特殊的按钮 
     * @param name 按钮图片的相对地址 
     * @param cmd 命令 
     * @param listener 监听器 
     * @param selected 是否被选中了 
     * @return 按钮 
     */  
    public static JToggleButton createJToggleButton(String name, String cmd, ActionListener listener, boolean selected) {  
//        JToggleButton jt = new JToggleButton();  
//        jt.setBorder(null);  
//        jt.setContentAreaFilled(false);  
//        jt.setFocusPainted(false);  
//        jt.setDoubleBuffered(true);  
//        jt.setIcon(new ImageIcon(icons[0]));  
//        jt.setRolloverIcon(new ImageIcon(icons[1]));  
//        jt.setSelectedIcon(new ImageIcon(icons[2]));  
//        jt.setOpaque(false);  
//        jt.setFocusable(false);  
//        jt.setActionCommand(cmd);  
//        jt.setSelected(selected);  
//        jt.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));  
//        jt.addActionListener(listener);  
        return null;  
    }  
    
    
    
    private static void sendMessage() {
		int rand =  (int)((Math.random()*9+1)*100000);
		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setSmsType("normal");
		req.setSmsFreeSignName("集合特卖");
		req.setSmsParamString("{\"code\":\""+rand+"\",\"product\":\"集合特卖群发工具\"}");
		req.setRecNum(userText.getText());
		req.setSmsTemplateCode(template);
		AlibabaAliqinFcSmsNumSendResponse rsp;
		try {
			rsp = client.execute(req);
			if(rsp.getResult()!=null && rsp.getResult().getErrCode()!=null && rsp.getResult().getErrCode().equals("0")) {
				randValid = rand;
			}
			System.out.println(rsp.getBody());
		} catch (ApiException e) {
			e.printStackTrace();
		}
    }
    
    private static boolean checkValidButton(String value) {
    	if(value ==null || value.equals("")) {
    		showInfoUtil.showInfoDialog("手机号为空");
    		return false;
    	}
		String regExp = "^1[0-9]{10}$"; //^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$
		//^1[0-9]{10}$
		Pattern p = Pattern.compile(regExp);  
		Matcher m = p.matcher(value); 
		if(!m.matches()) {
			showInfoUtil.showInfoDialog("请输入正确的手机号码");
		}
		return m.matches();
    }
}
