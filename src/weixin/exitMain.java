package weixin;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Auto.WeiXinAuto;
import test3.ShadowBorder;
import util.ImageUtil;

/**
 * 扫码登录界面
 * @author Administrator
 *
 */
public class exitMain extends JFrame{

	static Point origin = new Point();
	private String message ="";
	public JLabel messageLbl;
	static WeiXinAuto one_weixin;
	public exitMain() {
		initCompents();
	   
	}
	public exitMain(WeiXinAuto one_weixin) {
		this.one_weixin = one_weixin;
		initCompents();
	   
	}
	
	public static void main(String[] args) {
		new exitMain().setVisible(true);
	}
	
	private void initCompents() {
		//无边框
		setUndecorated(true);	
		this.setSize(366, 254);
		this.setLocationRelativeTo(null);//窗口在屏幕中间显示
		
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
		
		
		JPanel centerPanel = new JPanel();
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		
		
		centerPanel.setBorder(BorderFactory.createCompoundBorder(
	        ShadowBorder.newInstance(),
	        BorderFactory.createLineBorder(Color.WHITE)
		));
		JPanel titlePanel = new JPanel(){
            public void paintComponent(Graphics g) {
                ImageIcon icon =
                		ImageUtil.getImageIcon("/images/qfzs/tishi.png");
                // 图片随窗体大小而变化
                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
            }
        };
		titlePanel.setSize(330,50);
		titlePanel.setPreferredSize(new Dimension(330, 50));
		titlePanel.setLayout(null);
		centerPanel.add(titlePanel, BorderLayout.NORTH);
		
		JPanel infoPanel = new JPanel() {
			 public void paintComponent(Graphics g) {//login_bg.png
					
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/qfzs/infobg.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		infoPanel.setSize(new Dimension(330, 290));
		centerPanel.add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(null);
		
		messageLbl = new JLabel() {
			 public void paintComponent(Graphics g) {//login_bg.png
					
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/qfzs/stopMessage.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		messageLbl.setBounds(111, 46, 150, 28);
		infoPanel.add(messageLbl);
		
		JButton cancelButton = createJButton("", "q","quxiao1.png","quxiao2.png", new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
					dispose();
			}	
			
			 
		});
		cancelButton.setBounds(162, 146, 83, 35);
		infoPanel.add(cancelButton);
		
		JButton okButton = createJButton("", "o","qd1.png","qd2.png", new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
	            //如果关闭的时候循环扫描线程还在,就先终止线程
                if (one_weixin.login_scan_status) {
                    one_weixin.login_scan_status = false;
                    one_weixin.useIng = false;
                }
                if (one_weixin.ThreadStatus) {
                    one_weixin.ThreadStatus = false;
                    one_weixin.useIng = false;
                }
                System.exit(0);
			}
			
			 
		});
		okButton.setBounds(260, 146, 83, 35);
		infoPanel.add(okButton);
		
		
		
		
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
    
    
}
