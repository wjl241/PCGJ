package showDialog;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import test3.ShadowBorder;
import util.ImageUtil;

public class InfoDialog extends JDialog{
	static Point origin = new Point();
	private String message ="";
	private JLabel messageLbl;
	public InfoDialog() {
		initCompents();
	}
	
	
	public InfoDialog(String message) {
		this.message = message;
		initCompents();
	
		if(messageLbl.getText().length()>=19) {
			message = "未知原因的异常，请联系管理员";
		}
		messageLbl.setText(message);
		messageLbl.setBounds((330-messageLbl.getText().length()*17)/2,31, 330, 22);
	}
	private void initCompents() {
		//无边框
		setUndecorated(true);	
		this.setSize(330, 205);
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
                		ImageUtil.getImageIcon("/images/qfzs/infoTitle.png");
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
		infoPanel.setSize(new Dimension(330, 155));
		centerPanel.add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(null);
		
		messageLbl = new JLabel("提示信息异常");
		messageLbl.setFont(new Font("Microsoft Yahei", Font.BOLD, 16));
		messageLbl.setBounds(125, 31, 330, 22);
		infoPanel.add(messageLbl);
		
		JButton okButton =  createJButton("", "o","queding.png","queding2.png", new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//ClickGetImage();
				dispose();
			}
			
			 
		});
		okButton.setBounds(122, 90, 83, 36);
		infoPanel.add(okButton);
		
		
		JLabel closeLbl = new JLabel("");
		closeLbl.setBounds(292, 16, 18, 18);
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
				dispose();
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
	}
	
	
	public static void main(String[] args) {
		InfoDialog in = new InfoDialog();
		in.setVisible(true);
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
