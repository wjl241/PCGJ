package weixin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;

import Auto.WeiXinAuto;
import callback.CallBack;
import showDialog.InfoDialog;
import showDialog.OKInofDialog;
import test2.CheckHeaderCellRenderer;
import test2.TableModelProxy;
import test3.ShadowBorder;
import util.HttpClientUtil;
import util.ImageUtil;
import util.showInfoUtil;

/**
 * 群发工具主页面
 * @author Administrator
 *
 */
public class QFMain extends JFrame{
	public static JTable jtbl = new JTable();
	private static DefaultTableModel dtm; //表格用的数据模型
	private JScrollPane jsp;//存放表格的，表格必须放在里面
	private static JCheckBox jbAll;
	public static boolean test = true;
	static WeiXinAuto one_weixin = new WeiXinAuto();
	private static int count =0;
	private boolean send = false;//开始发送
	private HttpClientUtil httpClientUtil = new HttpClientUtil();
	public String phone = "";
	public int user_id;
	private JLabel  lblNewLabel_3;
	private JLabel wxLbl;
	private JPanel firstPanel;
	private boolean sendIng = true;
	private String sendHour ="";
	private List<String> sendCovers = new ArrayList<String>();
	public boolean running = true;//程序正在运行
	// 全局的位置变量，用于表示鼠标在窗口上的位置
	static Point origin = new Point();
	static JScrollPane wxPanel2;
	private boolean firstSave = true;
	private boolean  isSend= false;//已发送
    /**
     * 心跳线程3
     */
    public Thread HeartJumpThread3;
    /**
     * 手机端是否开启
     */
    public boolean useIng = false;
	public QFMain() {
		initCompents();
		ThreadRefersh();
	}
	
	public QFMain(String phone) {
		this.phone = phone;
		initCompents();
		ThreadRefersh();
		HeartJump();
	
	}
	public QFMain(String phone,int user_id) {
		this.phone = phone;
		this.user_id = user_id;
		initCompents();
		ThreadRefersh();
		HeartJump();
	
	}
	private void initCompents() {
		setUndecorated(true);	
		this.setSize(960, 660);
		this.setLocationRelativeTo(null);//窗口在屏幕中间显示
		this.setIconImage(ImageUtil.getImageIcon("/images/qfzs/tubiao.png").getImage());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.err.println(LoginMain.class.getResource("title.png"));
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
	
		
	     //监听关闭事件,关闭后终止进程
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
//           	new exitMain(one_weixin).setVisible(true);
//                //如果关闭的时候循环扫描线程还在,就先终止线程
                if (one_weixin.login_scan_status) {
                    one_weixin.login_scan_status = false;
                    useIng =false;
                }
                if (one_weixin.ThreadStatus) {
                    one_weixin.ThreadStatus = false;
                    useIng = false;
                }
            }

        });
		JPanel centerPanel = new JPanel();
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		centerPanel.setSize(960, 660);
		//不知道为什么会缺少几个 组建
		centerPanel.setBorder(BorderFactory.createCompoundBorder(
				    ShadowBorder.newBuilder().shadowAlpha(0.7f).top().build(),
		            BorderFactory.createLineBorder(Color.WHITE)
		 ));
		
		JPanel titlePanel = new JPanel() {
            public void paintComponent(Graphics g) {
                ImageIcon icon =
                		ImageUtil.getImageIcon("/images/qfzs/title2.png");
                // 图片随窗体大小而变化
                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
            }
        };
		titlePanel.setPreferredSize(new Dimension(960, 60));
		centerPanel.add(titlePanel, BorderLayout.NORTH);
		titlePanel.setLayout(null);
		
		
		JLabel deleteLbl = new JLabel("");
		deleteLbl.setBounds(857, 22, 18, 18);
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
		addLbl.setBounds(890, 22, 18, 18);
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
		closeLbl.setBounds(923, 22, 18, 18);
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
				new exitMain(one_weixin).setVisible(true);
//			   if (one_weixin.login_scan_status) {
//                    one_weixin.login_scan_status = false;
//                }
//                if (one_weixin.ThreadStatus) {
//                    one_weixin.ThreadStatus = false;
//                }
//                System.exit(0);  
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
		
		
		
		//中间主面板
		JPanel mainPanel = new JPanel(){
			 public void paintComponent(Graphics g) {//login_bg.png
					
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/qfzs/infobg.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		mainPanel.setPreferredSize(new Dimension(923, 575));
		centerPanel.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(null);
		
		
		firstPanel = new JPanel(){
			 public void paintComponent(Graphics g) {//login_bg.png
					
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/qfzs/mainBg.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		firstPanel.setBounds(16, 34, 292, 511);
		mainPanel.add(firstPanel);
		firstPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("第一步 开启微信发送");
		lblNewLabel.setBounds(58, 25, 197, 28);
		lblNewLabel.setFont(new Font("Microsoft Yahei", Font.BOLD, 20));
		firstPanel.add(lblNewLabel);
		
		lblNewLabel_3 = new JLabel("（需要桌面登录微信）");
		lblNewLabel_3.setFont(new Font("Microsoft Yahei", Font.PLAIN, 14));
		lblNewLabel_3.setBounds(79, 397, 140, 20);
		firstPanel.setBorder(BorderFactory.createLineBorder(new Color(193,206,221), 1));
		firstPanel.add(lblNewLabel_3);
		
		JButton startButton = createJButton("", "s","kq1.png","kq1.png", new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ClickGetImage();
			}
			
			 
		});
		startButton.setBounds(42, 436, 209, 48);
		firstPanel.add(startButton);
		wxLbl = new JLabel("");
//		wxLbl = new JLabel("") {
//			 public void paintComponent(Graphics g) {//login_bg.png
//					
//	                ImageIcon icon =
//	                		ImageUtil.getImageIcon("/images/qfzs/wxpg.png");
//	                // 图片随窗体大小而变化
//	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
//	            }
//		};
		wxLbl.setIcon(	ImageUtil.getImageIcon("/images/qfzs/wxpg.png"));
		wxLbl.setBounds(88, 194, 114, 92);
		firstPanel.add(wxLbl);
		
		JPanel secondPanel = new JPanel(){
			 public void paintComponent(Graphics g) {//login_bg.png
					
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/qfzs/mainBg.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		secondPanel.setBounds(328, 34, 292, 511);
		mainPanel.add(secondPanel);
		secondPanel.setBorder(BorderFactory.createLineBorder(new Color(193,206,221), 1));
		secondPanel.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("第二步 选择发送微信窗口");
		lblNewLabel_1.setBounds(40, 25, 253, 28);
		lblNewLabel_1.setFont(new Font("Microsoft Yahei", Font.BOLD, 20));
		secondPanel.add(lblNewLabel_1);
		
		JLabel lblNewLabel_7 = new JLabel("选择要发送的微信群窗口");
		lblNewLabel_7.setFont(new Font("Microsoft Yahei", Font.PLAIN, 13));
		lblNewLabel_7.setBounds(20, 75, 160, 17);
		lblNewLabel_7.setForeground(new Color(150,157,167));//FF7300
		secondPanel.add(lblNewLabel_7);
		
		JPanel wxPanel = new JPanel(){
			 public void paintComponent(Graphics g) {//login_bg.png
					
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/qfzs/mainBg.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		wxPanel.setBounds(20, 104, 254, 381);
		secondPanel.add(wxPanel);
		wxPanel.setBorder(BorderFactory.createLineBorder(new Color(193,206,221), 1));
		wxPanel.setLayout(null);
		
		JPanel wxPanel1 = new JPanel();
		wxPanel1.setBounds(0, 0, 254, 30);
		wxPanel1.setBorder(BorderFactory.createLineBorder(new Color(193,206,221), 1));
		wxPanel.add(wxPanel1);
		wxPanel1.setLayout(null);
		
		JPanel panel = new JPanel(){
			 public void paintComponent(Graphics g) {//login_bg.png
					
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/qfzs/mainBg.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		panel.setBounds(0, 0, 40, 30);
		panel.setBorder(BorderFactory.createLineBorder(new Color(193,206,221), 1));
		wxPanel1.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel_8 = new JLabel("选择");
		lblNewLabel_8.setFont(new Font("Microsoft Yahei", Font.PLAIN, 12));
		lblNewLabel_8.setForeground(new Color(150,157,167));//FF7300灰色
		lblNewLabel_8.setBounds(8, 6, 24, 17);
		panel.add(lblNewLabel_8);
		
		JPanel panel_1 = new JPanel(){
			 public void paintComponent(Graphics g) {//login_bg.png
					
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/qfzs/mainBg.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		panel_1.setBounds(40, 0, 214, 30);
		panel_1.setBorder(BorderFactory.createLineBorder(new Color(193,206,221), 1));
		wxPanel1.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel_9 = new JLabel("微信群窗口");
		lblNewLabel_9.setFont(new Font("Microsoft Yahei", Font.PLAIN, 12));
		lblNewLabel_9.setForeground(new Color(150,157,167));//FF7300灰色
		lblNewLabel_9.setBounds(60, 6, 60, 17);
		panel_1.add(lblNewLabel_9);
		
		
		
		
		
        wxPanel2 = new JScrollPane(getJtbl2());
		wxPanel2.setBounds(0, 30, 254, 321);
		wxPanel2.setBorder(BorderFactory.createLineBorder(new Color(193,206,221), 1));
	
		wxPanel.add(wxPanel2);
		
		JPanel wxPanel3 = new JPanel();
		wxPanel3.setBounds(0, 351, 254, 30);
		wxPanel3.setBorder(BorderFactory.createLineBorder(new Color(193,206,221), 1));
		wxPanel.add(wxPanel3);
		wxPanel3.setLayout(null);
		
		
		
		JPanel panel_2 = new JPanel(){
			 public void paintComponent(Graphics g) {//login_bg.png
					
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/qfzs/mainBg.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		panel_2.setBounds(0, 0, 45, 30);
		panel_2.setBorder(BorderFactory.createLineBorder(new Color(193,206,221), 1));
		wxPanel3.add(panel_2);
		panel_2.setLayout(null);
		
		jbAll = new JCheckBox();
		jbAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO
				for(int i=0;i<jtbl.getRowCount();i++) {
					jtbl.setValueAt(jbAll.isSelected() , i, 0);
				}
				jtbl.repaint();
				jtbl.updateUI();
				
			}
		});
//		lblNewLabel_10.setFont(new Font("Microsoft Yahei", Font.PLAIN, 12));
//		lblNewLabel_10.setForeground(new Color(150,157,167));//FF7300灰色
		jbAll.setBounds(14, 6, 18, 18);
		panel_2.add(jbAll);
		
		
		JPanel panel_3 = new JPanel(){
			 public void paintComponent(Graphics g) {//login_bg.png
					
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/qfzs/mainBg.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		panel_3.setBounds(40, 0, 214, 30);
		panel_3.setBorder(BorderFactory.createLineBorder(new Color(193,206,221), 1));
		wxPanel3.add(panel_3);
		panel_3.setLayout(null);
		
		JLabel lblNewLabel_11 = new JLabel("全选");
		lblNewLabel_11.setFont(new Font("Microsoft Yahei", Font.PLAIN, 12));
		lblNewLabel_11.setForeground(new Color(150,157,167));//FF7300灰色
		lblNewLabel_11.setBounds(15, 6, 60, 17);
		panel_3.add(lblNewLabel_11);
		
		
		
		
		JPanel thirdPanel = new JPanel(){
			 public void paintComponent(Graphics g) {//login_bg.png
					
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/qfzs/mainBg.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		thirdPanel.setBounds(639, 34, 292, 511);
		thirdPanel.setBorder(BorderFactory.createLineBorder(new Color(193,206,221), 1));
		mainPanel.add(thirdPanel);
		thirdPanel.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("第三步 保存设置");
		lblNewLabel_2.setBounds(64, 25, 165, 28);
		lblNewLabel_2.setFont(new Font("Microsoft Yahei", Font.BOLD, 20));
		thirdPanel.add(lblNewLabel_2);
		
		JButton saveButton = createJButton("", "s","save1.png","save1.png", new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {//保存之后启动线程，重复发送
				doSaveButton();
			}
			
			 
		});
		saveButton.setBounds(36, 436, 209, 48);
		thirdPanel.add(saveButton);
		
		JLabel lblNewLabel_4 = new JLabel("保存设置后去APP");
		lblNewLabel_4.setFont(new Font("Microsoft Yahei", Font.PLAIN, 14));
		lblNewLabel_4.setBounds(90, 224, 227, 20);
		thirdPanel.add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("蓝色端-工具-群发工具");
		lblNewLabel_5.setFont(new Font("Microsoft Yahei", Font.PLAIN, 14));
		lblNewLabel_5.setForeground(new Color(255, 155, 0));//FF7300
		lblNewLabel_5.setBounds(34, 244, 143, 20);
		thirdPanel.add(lblNewLabel_5);
		
		JLabel lblNewLabel_6 = new JLabel("选品群发吧～");
		lblNewLabel_6.setFont(new Font("Microsoft Yahei", Font.PLAIN, 14));
		lblNewLabel_6.setBounds(177, 244, 84, 20);
		thirdPanel.add(lblNewLabel_6);
		
		JPanel versionPanel = new JPanel() {
			
			 public void paintComponent(Graphics g) {//login_bg.png
				
                ImageIcon icon =
                		ImageUtil.getImageIcon("/images/qfzs/versionBg.png");
                // 图片随窗体大小而变化
                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
            }
		};
		versionPanel.setPreferredSize(new Dimension(960, 25));
		centerPanel.add(versionPanel, BorderLayout.SOUTH);
		versionPanel.setLayout(null);
		
		JLabel versionLbl = new JLabel("版本号：v1.0.6");
		versionLbl.setBounds(850, 5, 120, 17);
		versionPanel.add(versionLbl);
		Logger.getLogger(QFMain.class.getName()).log(Level.WARNING, "版本号：v1.0.6", new Object());
		
		JLabel qqLbl = new JLabel("");//交流QQ群:3131222
		qqLbl.setBounds(10, 5, 160, 17);
		versionPanel.add(qqLbl);
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
    
    
    public static JTable getJtbl() {
    	if(jtbl == null){
            jtbl = new JTable(getDtm());
        }else {
        	 if(count<  one_weixin.AllGroupList.size() ) {
        		 count = one_weixin.AllGroupList.size() ;
        		 jtbl.setModel(getDtm());
            	 jtbl.repaint();
            	 jtbl.updateUI();
        	 }
        }
    	
    	  // 方法一：直接方式 使用TableColumn的setCellRenderer方法（推荐）
        // 此方法可以设置某一列的渲染（即使用某一个组件--即控件来显示单元格数据）
        jtbl.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer(){

             /*(non-Javadoc)
             * 此方法用于向方法调用者返回某一单元格的渲染器（即显示数据的组建--或控件）
             * 可以为JCheckBox JComboBox JTextArea 等
             * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
             */
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                // 创建用于返回的渲染组件
                JCheckBox ck = new JCheckBox();
                // 使具有焦点的行对应的复选框选中
                ck.setSelected(isSelected);
                // 设置单选box.setSelected(hasFocus);
                // 使复选框在单元格内居中显示
                ck.setHorizontalAlignment((int) 0.5f);
                return ck;
            }});
        jtbl.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        return jtbl;
    }
    
    
    public static JTable getJtbl2() {
    	   String[] columnNames = {"选择","群名","id"};
           Object[][] data = getData();
           TableModelProxy tableModel = new TableModelProxy(columnNames, data);
           jtbl.setModel(tableModel);
           jtbl.getTableHeader().setDefaultRenderer(new CheckHeaderCellRenderer(jtbl));
           jtbl.getColumnModel().getColumn(2).setMaxWidth(0);
           jtbl.getColumnModel().getColumn(2).setMinWidth(0);
           jtbl.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(0);
           jtbl.getTableHeader().getColumnModel().getColumn(2).setMinWidth(0);
           
           
           jtbl.getColumnModel().getColumn(0).setMaxWidth(40);
           jtbl.setBackground(new Color(255, 255, 255));
           jtbl.repaint();
           jtbl.updateUI();
           return jtbl;
    }
    
    public  static DefaultTableModel getDtm() {
        
        Vector cols = new Vector();
        cols.add("id");cols.add("remark");
   
        
        Vector data = new Vector();
        
       // StudentDao sd = new StudentDao();
       // ArrayList alist = sd.getAllStudent();
        Jihes_qf_mqueue qun = new Jihes_qf_mqueue();
        List<Jihes_qf_mqueue> quns = new ArrayList<Jihes_qf_mqueue>();
        JsonObject group = new JsonObject();
        
        for(int i=0;i<one_weixin.AllGroupList.size();i++) {
			group = (JsonObject) one_weixin.AllGroupList.get(i);
			//group.addProperty("UserName", user_name);
        	//group.addProperty("NickName", nick_name);
			String UserName = group.get("UserName").toString();
			String NickName = group.get("NickName").toString();
			if(NickName.contains("\"")) {
				 NickName = NickName.replace("\"", "");
            }
            if(UserName.contains("\"")) {
            	UserName = UserName.replace("\"", "");
            }
			qun = new Jihes_qf_mqueue();
			qun.setId(i);
			System.err.println(NickName);
			qun.setRemark(NickName);
			quns.add(qun);
		
		}
		
        for(Object obj : quns){
            Vector v = new Vector();
            Jihes_qf_mqueue s = (Jihes_qf_mqueue)obj;
            v.add(s.getId());
            v.add(s.getRemark());
            data.add(v);
        }
        dtm = new DefaultTableModel(data,cols);
        return dtm;
    }
    
    
    /**
     * 建立实例获得二维码
     */
    public void ClickGetImage() {
        //实例化一个
    	one_weixin = new WeiXinAuto();
    	one_weixin.setPhone(phone);
        //获取图片,并传递给登录界面的图片显示
        byte[] get_image = one_weixin.GetImage();
        //打开登录界面
        //AllLoginMain login_main = new AllLoginMain();
        qrcodeMain login_main = new qrcodeMain();
        login_main.qrcodeLbl.setIcon(new ImageIcon(one_weixin.SetImageSize(new ImageIcon(get_image), 200, 200)));
        login_main.setVisible(true);
        //赋予二维码显示
        //login_main.erweima_image.setIcon(new ImageIcon(one_weixin.SetImageSize(new ImageIcon(get_image), 200, 200)));
        //循环获取扫码状态
        one_weixin.ScanLoginStatus(login_main,new CallBack() {
			
			@Override
			public void execute() {
				lblNewLabel_3.setText("");
				wxLbl.setIcon(	ImageUtil.getImageIcon("/images/qfzs/yishouquan.png"));
				firstPanel.repaint();
				firstPanel.updateUI();
				login_main.dispose();
				
			}
		});
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
     * 独立线程刷新新消息的进入并显示
     * <br>这个线程一直运行到程序终止
     */
    public static void ThreadRefersh() {
        new Thread(() -> {
            while (true) {
            	 if(count<  one_weixin.AllGroupList.size() ) {
            			refreshPanel();
            	 }
                try {
                    //每500毫秒刷新一次
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.err.println("消息值守休眠出错");
                }
            }
        }).start();
    }
    
    
    public static void refreshPanel() {
    	 if(count<  one_weixin.AllGroupList.size() ) {
    		 count = one_weixin.AllGroupList.size() ;
    			getJtbl2();
    	 }
    	
    	
//    	wxPanel2 = new JScrollPane(getJtbl());
//    	wxPanel2.updateUI();
    }
    
    
    
    /**
     * 获得数据
     * 
     * @return
     */
    private static Object[][] getData() {
//        Object[][] data = {
//                { "Kathy", 5, new Boolean(false) },
//            { "John", 15, new Boolean(true) },
//            { "Sue", 16, new Boolean(false) },
//            { "Jane",17, new Boolean(true) },
//            { "Joe", 18, new Boolean(false) } };
        
        Object[][] data = new Object[one_weixin.AllGroupList.size()][3];
        // StudentDao sd = new StudentDao();
        // ArrayList alist = sd.getAllStudent();
         Jihes_qf_mqueue qun = new Jihes_qf_mqueue();
         List<Jihes_qf_mqueue> quns = new ArrayList<Jihes_qf_mqueue>();
         JsonObject group = new JsonObject();
         
         for(int i=0;i<one_weixin.AllGroupList.size();i++) {
 			group = (JsonObject) one_weixin.AllGroupList.get(i);
 			//group.addProperty("UserName", user_name);
         	//group.addProperty("NickName", nick_name);
 			String UserName = group.get("UserName").toString();
 			String NickName = group.get("NickName").toString();
 			if(NickName.contains("\"")) {
 				 NickName = NickName.replace("\"", "");
             }
             if(UserName.contains("\"")) {
             	UserName = UserName.replace("\"", "");
             }
             
             if(jtbl.getRowCount()<=0 || jtbl.getRowCount()<=i) {
            	  data[i][0] =false;
                  data[i][1] = NickName;
                  data[i][2] = UserName;
             }else {
            	 data[i][0] = jtbl.getValueAt(i, 0) == null ? false :jtbl.getValueAt(i, 0);
                 data[i][1] = NickName;
                 data[i][2] = UserName;
             }
             
             
 		
 		}
        return data;
    }
    
    /**
     * 保存后续操作
     */
    private void doSaveButton() {
    	if(one_weixin.AllGroupList.size()>0) {
    		boolean okQun =false;
    		for(int i=0;i<jtbl.getRowCount();i++) {
				if((boolean) jtbl.getValueAt(i, 0)) {
					okQun = true;
					break;
				}
    		}
    		
    		if(okQun) {
    			new OKInofDialog().setVisible(true);
    			if(!firstSave) {//第一次保存才有效，开启线程
    				return;
    			}
    			firstSave = false;
    			getSendQueue();
    			
    			
    		}else {
    			showInfoUtil.showInfoDialog("请勾选需要发送的微信群");
    		}
    		
    	}else{
    		new InfoDialog("未开启微信或未获取到微信群信息").setVisible(true);
    	}
    //	sendTest2();
    }
    private void sendTest(){

    	send =true;
		JsonObject group = new JsonObject();
		for(int i=0;i<one_weixin.AllGroupList.size();i++) {
			group = (JsonObject) one_weixin.AllGroupList.get(i);
			//group.addProperty("UserName", user_name);
        	//group.addProperty("NickName", nick_name);
			String UserName = group.get("UserName").toString();
			String NickName = group.get("NickName").toString();
			 if(NickName.contains("\"")) {
				 NickName = NickName.replace("\"", "");
            }
            if(UserName.contains("\"")) {
            	UserName = UserName.replace("\"", "");
            }
			NickName = NickName.replace("\"", "");
			if("三贱客".equals(NickName)) {
				one_weixin.SendImgToFriend3(UserName, "","http://p.jihes.com//1688/avatar/10677_1504086951.jpg");
			}
		
		}
		
		
    
    }
    
    private void sendTest2(){
    	boolean isSend = false;
    	for(int j=0;j<jtbl.getRowCount();j++) {
			isSend = (boolean) jtbl.getValueAt(j, 0);
			if(isSend) {
				//one_weixin.SendImgToFriend3((String)jtbl.getValueAt(j, 2), "","http://p.jihes.com//1688/avatar/10677_1504086951.jpg");
				one_weixin.SendImgToFriend4((String)jtbl.getValueAt(j, 2), "","http://p.jihes.com//1688/avatar/10157_1503589570.jpg",1);
			}
				
		}
    }
    private void getSendQueue() {
    	 new Thread(() -> {
    		 Logger.getLogger(QFMain.class.getName()).log(Level.WARNING, "线程开始useIng:"+useIng+",one_weixin.ThreadStatus:"+one_weixin.ThreadStatus , new Object());
             while (one_weixin.ThreadStatus && useIng) {
            	Logger.getLogger(QFMain.class.getName()).log(Level.WARNING, "线程开始useIng2:"+useIng+",one_weixin.ThreadStatus2:"+one_weixin.ThreadStatus , new Object());
            	boolean tg = false;//用来跳过，重新开始循环
            	//每隔一分钟操作一次
            	httpClientUtil = new HttpClientUtil();
 				//String api_url = "https://apiway.jihes.com/temai?os_type=1&os_version=10.2&app_version=1.0.0&api_version=2.0.0&device_name=myphone&client_id=83a14e030ad94096b4cdbfc2a94dc203&api=user_login/login";
            	//String api_url = "http://localhost:8080/qfzs/queue/showInfo?phone="+phone;
            	String api_url = "http://api.jihes.com/jsp//qfzs/queue/showInfo?phone="+phone;
            	//String api_url = "http://api.jihes.com/jsp//qfzs/queue/showInfo?phone=13857149723";
 				Map<String,String> createMap = new HashMap<String,String>();
 				String ret = httpClientUtil.doPost2(api_url,createMap,"utf-8");
 				if(ret == null) {
 					//showInfoUtil.showInfoDialog("连接失败，请联系管理员");
 					Logger.getLogger(QFMain.class.getName()).log(Level.WARNING, "连接失败，请联系管理员", new Object());
 					continue;
 				}
 				JSONObject jsob = JSONObject.parseObject(ret);
 				jsob.get("code");
 				String code = String.valueOf(jsob.get("code") == null? "-1" : jsob.get("code") ) ;
 				String message =(String) jsob.get("message");
 				System.err.println("showInfo请求返回code:"+code+",手机号："+phone);
 				Logger.getLogger(QFMain.class.getName()).log(Level.WARNING, "showInfo请求返回code:"+code+",手机号："+phone, new Object());
 				if(code.equals("200") && !message.equals("-1")) {
 					JSONObject data =  (JSONObject)jsob.get("data");
 					if(data ==null) {
 						System.err.println("data为null");
 						continue;
 					}
 					
 					Calendar cal = Calendar.getInstance();
 					int hour = cal.get(Calendar.HOUR_OF_DAY);
 					int minute = cal.get(Calendar.MINUTE);
 					String now ="";
 					if(minute <10) {
 						if(hour<10) {
 							now = "0"+hour+":0"+minute;
 						}else {
 							now = hour+":0"+minute;
 						}
 						
 					}else {
 						if(hour<10) {
 							now = "0"+hour+":"+minute;
 						}else {
 							now = hour+":"+minute;
 						}
 						
 					}
 					boolean bf = false;//补发
 					if(minute==0 || minute==10 || minute==20|| minute==30|| minute==40|| minute==50) {
 						isSend = false;
 					}
 					if(minute==9 || minute==19 || minute==29|| minute==39|| minute==49|| minute==59) {
 						if(!isSend) {//还未发送
 							bf = true;
 						}
 					}
 					
 					
 					
 					System.err.println("群发循环请求："+now);
 					Logger.getLogger(QFMain.class.getName()).log(Level.WARNING, "群发循环请求："+now, new Object());
 					JSONArray zdlb = (JSONArray) data.get("zdlb");
 					JSONObject zd = new JSONObject();
 					String sendCover = "";
 					String item_id = "";
// 					now = "17:21";
// 					sendIng = true;
 					if(!sendIng && !now.equals(sendHour) ) {
 						Object obj2 = new Object();//申请一个对象  
 						synchronized (obj2) {
 							System.err.println("成功修改sendIng状态");
 							Logger.getLogger(QFMain.class.getName()).log(Level.WARNING, "成功修改sendIng状态", new Object());
								sendIng=true;
						}
 					}
 					System.err.println("sendIng状态:"+sendIng);
 					Logger.getLogger(QFMain.class.getName()).log(Level.WARNING, "sendIng状态:"+sendIng, new Object());
 					//TODO 这里需要引起
 					for(int i=0;i<zdlb.size();i++) {
 					
 						zd = new JSONObject();
 						zd = (JSONObject) zdlb.get(i);
 						//若为当前发送时间且未发送状态
 						if(now.equals(zd.get("sendTime")) && sendIng) {
 							sendCover = zd.get("send_Cover") ==null?"":(String)zd.get("send_Cover");
 							System.err.println(now+"开始自动群发，群发图片地址："+sendCover);
 							Logger.getLogger(QFMain.class.getName()).log(Level.WARNING, now+"开始自动群发，群发图片地址："+sendCover, new Object());
 							if(sendCover.equals("")) {
 								System.err.println("图片地址为空群发失败");
 								Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"图片地址为空群发失败", new Object());
 								tg =true;
 								break;
 							}
 							item_id = zd.get("item_id") ==null?"":(String)zd.get("item_id");
 							Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"item_id是222："+item_id, new Object());
 							if(item_id.equals("")) {
 								System.err.println("商品item_id为空");
 								Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"商品item_id为空", new Object());
 								tg =true;
 								break;
 							}
 							Object obj = new Object();//申请一个对象  
 							synchronized (obj) {
 								sendIng=false;
 								sendHour = now;
							}
 							break;
 							
 						}
 					}
 					if(tg) {
 						tg = false;
 						try {
 							Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"跳过本次1，休息十秒", new Object());
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
 						continue;
 					}
 					if(sendCover.equals("")) {
 						JSONArray sdfs = (JSONArray) data.get("sdfs");
 	 					JSONObject sd = new JSONObject();
 	 					for(int i=0;i<sdfs.size();i++) {
 	 						sd = new JSONObject();
 	 						sd = (JSONObject) sdfs.get(i);
 	 						//若为当前发送时间且未发送状态
 	 						if(now.equals(sd.get("sendTime")) &&  sendIng) {
 	 							sendCover = sd.get("send_Cover") ==null?"":(String)sd.get("send_Cover");
 	 							System.err.println(now+"开始手动群发，群发图片地址："+sendCover);
 	 							Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,now+"开始手动群发，群发图片地址："+sendCover, new Object());
 	 							if(sendCover.equals("")) {
 	 								System.err.println("图片地址为空群发失败");
 	 								Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"图片地址为空群发失败", new Object());
 	 								tg = true;
 	 								break;
 	 							}
 	 							item_id = sd.get("item_id") ==null?"":(String)sd.get("item_id");
 	 							Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"item_id是222："+item_id, new Object());
 	 							if(item_id.equals("")) {
 	 								System.err.println("商品item_id为空");
 	 								Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"商品item_id为空", new Object());
 	 								tg =true;
 	 								break;
 	 							}
 	 							Object obj = new Object();//申请一个对象  
 	 							synchronized (obj) {
 	 								sendIng=false;
 	 								sendHour = now;
 								}
 	 							break;
 	 						}
 	 					}
 					}
 				
 					if(tg) {
 						tg = false;
 						try {
 							Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"跳过本次2，休息十秒", new Object());
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
 						continue;
 					}
 					if(sendCover.equals("") && bf) {//到了补发的时候，9次还未发送，则直接发送当前
 						Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,now+"进入补发流程：", new Object());
 						String m = String.valueOf(minute);
 	 					if(minute>=10) {
 	 						m=m.substring(0,1);
 	 					}else {
 	 						m=String.valueOf(0);
 	 					}
 	 					System.err.println("hour:"+hour);
	 					System.err.println("m:"+m);
 						for(int i=0;i<zdlb.size();i++) {
 							zd = new JSONObject();
 	 						zd = (JSONObject) zdlb.get(i);
 	 					
 	 						//若为当前发送时间且未发送状态
 	 						if(String.valueOf(hour).equals(zd.get("expireTime")) && m.equals(zd.get("number"))) {
 	 							sendCover = zd.get("send_Cover") ==null?"":(String)zd.get("send_Cover");
 	 							System.err.println(now+"开始自动群发（补发），群发图片地址："+sendCover);
 	 							Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,now+"开始自动群发（补发），群发图片地址："+sendCover, new Object());
 	 							if(sendCover.equals("")) {
 	 								System.err.println("图片地址为空群发失败");
 	 								Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"图片地址为空群发失败", new Object());
 	 								tg =true;
 	 								break;
 	 							}
 	 							
 	 							item_id = zd.get("item_id") ==null?"":(String)zd.get("item_id");
 	 							Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"item_id是222："+item_id, new Object());
 	 							if(item_id.equals("")) {
 	 								System.err.println("商品item_id为空");
 	 								Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"商品item_id为空", new Object());
 	 								tg=true;
 	 								break;
 	 							}
 	 							Object obj = new Object();//申请一个对象  
 	 							synchronized (obj) {
 	 								sendIng=false;
 	 								sendHour = now;
 								}
 	 							break;
 	 						}
 						}
 					}
 					
 					if(tg) {
 						tg = false;
 						try {
 							Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"跳过本次3，休息十秒", new Object());
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
 						continue;
 					}
 					System.err.println("sendCover:"+sendCover);
 					Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"sendCover:"+sendCover, new Object());
 					if(!sendCover.equals("") && !sendCovers.contains(sendCover)) {//开始发送
 						boolean isSend =false;
 						for(int i=0;i<jtbl.getRowCount();i++) {
 							
 							isSend = (boolean) jtbl.getValueAt(i, 0);
 							if(isSend) {
 								System.err.println("发送群为:"+(String)jtbl.getValueAt(i, 1));
 								Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"发送群为:"+(String)jtbl.getValueAt(i, 1), new Object());
 								
 								
 								Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"开始生成图片信息", new Object());
 								Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"item_id是333："+item_id, new Object());
 								String  send = createSendCover(item_id, user_id);
 								if(!send.equals("")) {
 									one_weixin.SendImgToFriend4((String)jtbl.getValueAt(i, 2), "",send,i);
 								}else {
 									continue;
 								}
 								
 							}
 					
 							
 						}
 						sendCovers.add(sendCover);
 						isSend = true;
						System.err.println("sendIng:"+sendIng);
 					}
 					
 					
 					
 				}else {
 					//showInfoUtil.showInfoDialog("读取信息失败"+message);
 					Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"读取信息失败，休息1分钟", new Object());
 					tg=false;
 					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
 					continue;
 				}
 				if(tg) {
						tg = false;
						try {
							Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"跳过本次，休息十秒", new Object());
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
						continue;
				}
            	 try {
                     Thread.sleep(60000);
                     System.err.println("状态:循环中......");
                 } catch (InterruptedException ex) {
                     System.err.println("出错:循环发送失败");
                 }
             }

         }).start();
    }
    
    public void setPhone(String phone) {
    	this.phone = phone;
    }
    
    public static void main(String[] args) {
		new QFMain().setVisible(true);
    	//new exitMain().setVisible(true);
	}
    
    public static void test() {
    	one_weixin.SendImgToFriend3("", "","http://p.jihes.com//1688/avatar/10677_1504086951.jpg");
    }
    
    
	  /**
     * 保持与服务器的心跳 循环线程执行，直到结束
     */
    private void HeartJump() {
    	  HeartJumpThread3 = new Thread(() -> {
              while (true) {

                  //只运行一个群组信息获取
              	  heartBeat();
                  try {
                      Thread.sleep(10000);
                  } catch (InterruptedException ex) {
                      System.err.println("心跳休眠2失败...");
                  }
              }
          });
          HeartJumpThread3.start();
    	
    }
    
    /**
     * 获取当前手机号是否打开微信,并且与服务端心跳链接
     */
    private void heartBeat() {
        new Thread(() -> {
        	HttpClientUtil httpClientUtil = new HttpClientUtil();
			//String api_url = "https://apiway.jihes.com/temai?os_type=1&os_version=10.2&app_version=1.0.0&api_version=2.0.0&device_name=myphone&client_id=83a14e030ad94096b4cdbfc2a94dc203&api=user_login/login";
			//String api_url = "http://localhost:8080/qfzs/user/validUser?phone="+phone;
        	//String api_url = "http://localhost:8080/qfzs/user/heartBeat?phone="+phone;
			String api_url = "http://api.jihes.com/jsp/qfzs/user/heartBeat?phone="+phone;
			Map<String,String> createMap = new HashMap<String,String>();
			String ret = httpClientUtil.doPost2(api_url,createMap,"utf-8");
			if(ret == null || ret.equals("")) {
				//showInfoUtil.showInfoDialog("连接失败，请联系管理员");
				useIng= false;
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
				String use = jb.get("use") == null ? "" :(String)jb.get("use");
				if(use.equals("1")) {//0关闭  1开启
					useIng = true;
				}else {
					useIng = false;
				}
				
			}
        }).start();
    }
    
    private String createSendCover(String item_id,int user_id) {
	//生成小程序二维码图片
		
		Map<String,String> createMap = new HashMap<String,String>();
		String api_url = "http://api.jihes.com/weixin_small_program/wx_code?os_type=1&os_version=10.2&app_version=1.0.0&api_version=2.0.0&device_name=myphone&client_id=83a14e030ad94096b4cdbfc2a94dc203&page=1";
		//api_url = api_url.replace(" ", "%20");
		createMap.put("page", "pages/index/index");
		//createMap.put("scene", "type=goodsdetail&id="+goods.getId());
		createMap.put("scene", "tp=g&i="+item_id+"&pi="+user_id);
		createMap.put("user_id", user_id+"");
		createMap.put("platform", "java1");//java1 是服务端 java2是群发小程序客户端
		
		HttpClientUtil httpClientUtil2 = new HttpClientUtil();
		
		String httpOrgCreateTestRtn2 = httpClientUtil2.doPost2(api_url,createMap,"utf-8"); 
		int code =200;
		String message = "";
		String send_cover ="";
		if(httpOrgCreateTestRtn2 == null || httpOrgCreateTestRtn2.equals("")) {
			Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"生成小程序二维码图片失败", new Object());
			return "";
		}else {
			JSONObject jb = new JSONObject();
			
			jb = (JSONObject) JSONObject.parse(httpOrgCreateTestRtn2);
			if(jb== null ) {//回滚数据
				Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"生成小程序二维码图片失败，空指针", new Object());
				return "";
			}else {
				code = (Integer) jb.get("code");
				message =  jb.get("message") == null ? "" : (String) jb.get("message");
				if(code != 200) {//回滚数据
					Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"生成小程序二维码图片失败，错误信息："+message, new Object());
					return "";
				}else {
					JSONObject retData = (JSONObject) jb.get("data");
					if( retData == null) {
						Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"返回data为空", new Object());
						return "";
					}
					send_cover = retData.getString("qrcode") ==null ? "" : retData.getString("qrcode") ;
					if(send_cover.equals("")) {
						Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"返回qrcode为空", new Object());
						return "";
					}
				}
			}
		}
		
		
		api_url = "http://api.jihes.com/weixin_small_program/detail_share_img?os_type=1&os_version=10.2&app_version=1.0.0&api_version=2.0.0&device_name=myphone&client_id=83a14e030ad94096b4cdbfc2a94dc203&page=1";
		//api_url = api_url.replace(" ", "%20");
		//createMap.put("goods_id", String.valueOf(goods.getId()));
		createMap.put("goods_id", item_id);
		createMap.put("small_url", send_cover);
		createMap.put("platform", "1");
		createMap.put("type", "1");
		createMap.put("user_id", user_id+"");
		httpClientUtil = new HttpClientUtil();
		
		httpOrgCreateTestRtn2 = httpClientUtil2.doPost2(api_url,createMap,"utf-8"); 
		code =200;
		message = "";
		send_cover ="";
		if(httpOrgCreateTestRtn2 == null || httpOrgCreateTestRtn2.equals("")) {
			Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"生成七牛推送图片失败", new Object());
			return "";
		}else {
			JSONObject jb = new JSONObject();
			
			jb = (JSONObject) JSONObject.parse(httpOrgCreateTestRtn2);
			if(jb== null ) {//回滚数据
				Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"生成七牛推送图片失败，空指针", new Object());
				return "";
			}else {
				code = (Integer) jb.get("code");
				message =  jb.get("message") == null ? "" : (String) jb.get("message");
				if(code != 200) {//回滚数据
					Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"生成七牛推送图片失败，错误信息："+message, new Object());
					return "";
				}else {
					JSONObject retData = (JSONObject) jb.get("data");
					if( retData == null) {
						Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"返回data为空", new Object());
						return "";
					}
					send_cover = retData.getString("url") ==null ? "" : retData.getString("url") ;
					if(send_cover.equals("")) {
						Logger.getLogger(QFMain.class.getName()).log(Level.WARNING,"返回url为空", new Object());
						return "";
					}
				}
			}
		}
		return send_cover;
    }
}
