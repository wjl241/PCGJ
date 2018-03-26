package panel;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class LoginPanel extends JPanel{
	private JTextField userText;
	private JTextField passwordText;
	public LoginPanel() {
		initCompnet();
	}
	
	private void initCompnet() {
		this.setVisible(true);
		setLayout(new BorderLayout(0, 0));
		JPanel titlePanel = new JPanel();
		add(titlePanel, BorderLayout.NORTH);
		titlePanel.setLayout(new MigLayout("", "[]", "[]"));
		
		JLabel lblNewLabel = new JLabel("集合群发测试版");
		lblNewLabel.setFont(new Font("宋体",Font.BOLD, 14));
		titlePanel.add(lblNewLabel, "cell 0 0,grow");
		titlePanel.setPreferredSize(new Dimension(this.getWidth(),32));
		
		JPanel infoPanel = new JPanel();
		add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel switchPanel = new JPanel();
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
		
		JPanel textPanel = new JPanel();
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
		
		JButton loginButton = new JButton("登录");
		textPanel.add(loginButton, "cell 1 4 2 1");
		loginButton.setPreferredSize(new Dimension(225, 32));
		
		JLabel lblNewLabel_1 = new JLabel("忘记密码");
		textPanel.add(lblNewLabel_1, "cell 2 5,alignx right");
	
	}

}
