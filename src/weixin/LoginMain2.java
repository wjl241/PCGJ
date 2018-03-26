package weixin;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import com.alibaba.fastjson.JSONObject;

import net.miginfocom.swing.MigLayout;
import util.HttpClientUtil;
import util.ImageUtil;

public class LoginMain2 extends JFrame{
	private JTextField userText;
	private JTextField passwordText;
	// 全局的位置变量，用于表示鼠标在窗口上的位置
	static Point origin = new Point();
	public LoginMain2() {
		initCompents();
	}
	private void initCompents() {
		setUndecorated(true);	
		this.setLocationRelativeTo(null);//窗口在屏幕中间显示
		this.setSize(408, 390);
		
		System.err.println(LoginMain2.class.getResource("title.png"));
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
		this.setVisible(true);
		setLayout(new BorderLayout(0, 0));
		JPanel titlePanel = new JPanel() {
            public void paintComponent(Graphics g) {
                ImageIcon icon =
                		ImageUtil.getImageIcon("/images/title.png");
                // 图片随窗体大小而变化
                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
            }
        };
		add(titlePanel, BorderLayout.NORTH);
		titlePanel.setLayout(new MigLayout("", "[]", "[]"));
		//titlePanel.setIcon(new ImageIcon(LoginMain.class.getResource("title.png"))); // NOI18N
		
		JLabel lblNewLabel = new JLabel("集合群发测试版");
		lblNewLabel.setFont(new Font("宋体",Font.BOLD, 14));
		titlePanel.add(lblNewLabel, "cell 0 0,grow");
		titlePanel.setPreferredSize(new Dimension(this.getWidth(),32));
		
		JPanel infoPanel = new JPanel() {
			 public void paintComponent(Graphics g) {//login_bg.png
				 System.err.println( getClass().getResource("/images/add_user.png"));
				 System.err.println( getClass().getResource("/images/login_bg.png"));
				
	                ImageIcon icon =
	                        new ImageIcon(getClass().getResource("/images/login_bg.png"));
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel switchPanel = new JPanel() {
			 public void paintComponent(Graphics g) {//login_bg.png
	                ImageIcon icon = ImageUtil.getImageIcon("/images/login_bg.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		infoPanel.add(switchPanel, BorderLayout.NORTH);
		switchPanel.setPreferredSize(new Dimension(this.getWidth(),75));
		switchPanel.setLayout(new MigLayout("", "[136px][][10px][][][][]", "[]"));
		
		JLabel loginLbl = new JLabel("登录");
		switchPanel.add(loginLbl, "cell 1 0,grow");
		loginLbl.setPreferredSize(new Dimension(65, 70));
		loginLbl.setFont(new Font("宋体",Font.BOLD, 20));
		
		JLabel registerLbl = new JLabel("注册");
		switchPanel.add(registerLbl, "cell 3 0,grow");
		registerLbl.setPreferredSize(new Dimension(65, 70));
		registerLbl.setFont(new Font("宋体",Font.BOLD, 20));
		
		JPanel textPanel = new JPanel() {
			 public void paintComponent(Graphics g) {//login_bg.png
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/login_bg.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		infoPanel.add(textPanel, BorderLayout.CENTER);
		textPanel.setLayout(new MigLayout("", "[57px,right][145px,center][145px]", "[32px][-1.00px][32px][32px][40px][32px][23px][25px][]"));
		
		userText = new JTextField();
		textPanel.add(userText, "cell 1 0 2 1,grow");
		userText.setColumns(10);
		
		passwordText = new JTextField();
		textPanel.add(passwordText, "cell 1 2,grow");
		passwordText.setColumns(10);
		
		JButton validButton = new JButton("获取验证码");
		textPanel.add(validButton, "cell 2 2,grow");
		
		JButton loginButton = createJButton("登录", "x", new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String user = userText.getText() == null ? "" :userText.getText();
				String password = passwordText.getText() == null ? "" :passwordText.getText();
				if(user == null || user.equals("")) {
					JOptionPane.showMessageDialog(null, "用户名为空");
					return;
				}
				if(password == null || password.equals("")) {
					JOptionPane.showInputDialog("密码为空");
					return;
				}
				HttpClientUtil httpClientUtil = new HttpClientUtil();
				//String api_url = "https://apiway.jihes.com/temai?os_type=1&os_version=10.2&app_version=1.0.0&api_version=2.0.0&device_name=myphone&client_id=83a14e030ad94096b4cdbfc2a94dc203&api=user_login/login";
				String api_url = "http://localhost:8080/qfzs/user/showTp2?phone=13895107582";
				Map<String,String> createMap = new HashMap<String,String>();
				String ret = httpClientUtil.doPost2(api_url,createMap,"utf-8");
				if(ret == null) {
					JOptionPane.showInputDialog("连接失败，请联系管理员");
					return;
				}
				JSONObject jsob = JSONObject.parseObject(ret);
				jsob.get("code");
				String code = String.valueOf(jsob.get("code") == null? "-1" : jsob.get("code") ) ;
				String message =(String) jsob.get("message");
				if(code.equals("200") && !message.equals("-1")) {
					//setVisible(false);
					//new AdminMain().setVisible(true);
				}else {
					JOptionPane.showInputDialog("手机号:"+user+"不是集合特卖的用户，请下载集合特卖APP注册");
					return;
				}
				
				
			}
		});
		textPanel.add(loginButton, "cell 1 4 2 1");
		loginButton.setPreferredSize(new Dimension(225, 32));
//		loginButton.setIcon(ImageUtil.getImageIcon("title.png")); 
//		loginButton.setRolloverIcon(ImageUtil.getImageIcon("login_bg.png")); 
//		loginButton.setPressedIcon(ImageUtil.getImageIcon("title.png")); 
		
		JLabel lblNewLabel_1 = new JLabel("忘记密码");
		textPanel.add(lblNewLabel_1, "cell 2 5,alignx right");
	
	
		
		
		
		
	}
	
	
	/** 
     * 根据一些参数快速地构造出按钮来 
     * 这些按钮从外观上看都是一些特殊的按钮 
     * @param name 按钮图片的相对地址 
     * @param cmd 命令 
     * @param listener 监听器 
     * @return 按钮 
     */  
    public static JButton createJButton(String name, String cmd, ActionListener listener) {  
        JButton jb = new JButton(name,ImageUtil.getImageIcon("/images/login_bg.png"));  
        jb.setBorderPainted(false);  
        jb.setFocusPainted(false);  
        jb.setContentAreaFilled(false);  
        jb.setDoubleBuffered(true);  
        jb.setRolloverIcon(ImageUtil.getImageIcon("/images/login_bg2.png"));  
        jb.setPressedIcon(ImageUtil.getImageIcon("/images/login_bg.png"));  
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
}
