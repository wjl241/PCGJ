package showDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import test3.ShadowBorder;
import util.ImageUtil;

public class OKInofDialog extends JDialog{
	public OKInofDialog() {
		initCompents();
		 new Thread(() -> {
			 try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setVisible(false);
		 }).start();
	}
	
	private void initCompents() {
		//无边框
		setUndecorated(true);	
		this.setSize(209, 133);
		this.setLocationRelativeTo(null);//窗口在屏幕中间显示
		
		JPanel centerPanel = new JPanel();
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		
		
		centerPanel.setBorder(BorderFactory.createCompoundBorder(
	        ShadowBorder.newInstance(),
	        BorderFactory.createLineBorder(Color.WHITE)
		));
		
		
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
		
		JLabel messageLbl = new JLabel("群发设置已保存");
		messageLbl.setFont(new Font("Microsoft Yahei", Font.BOLD, 16));
		messageLbl.setForeground(new Color(255, 155, 0));//FF7300
		messageLbl.setBounds(50, 80, 112, 22);
		infoPanel.add(messageLbl);
		
		JLabel okLbl = new JLabel(""){
			 public void paintComponent(Graphics g) {//login_bg.png
					
	                ImageIcon icon =
	                		ImageUtil.getImageIcon("/images/qfzs/ok.png");
	                // 图片随窗体大小而变化
	                g.drawImage(icon.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
	            }
		};
		okLbl.setBounds(81, 23, 40, 40);
		infoPanel.add(okLbl);
		
		infoPanel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
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
				dispose();
				
			}
		});
			
	}
	
	public static void main(String[] args) {
		new OKInofDialog().setVisible(true);
	}
}
