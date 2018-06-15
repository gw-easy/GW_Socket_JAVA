package GW_Socket;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;



public class GW_EnterFrame extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private int port = 2018;
	private JTextField nameTF;
	private JTextField hostIPTF;
	private JTextField hostportTF;
	private JButton enterBtn;
	private JButton exitBtn;
	public GW_EnterFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		setIconImage(Toolkit.getDefaultToolkit().getImage("socket.jpg"));
		setTitle("\u804A\u5929\u5BA4");
		getContentPane().setLayout(null);
		setSize(296, 249);
		GW_WinCerter.center(this);
		setResizable(false);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exitBtn.doClick();
			}
		});
		
		JLabel lblNewLabel = new JLabel("\u7528\u6237\u540D");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel.setBounds(23, 30, 81, 34);
		getContentPane().add(lblNewLabel);
		
		nameTF = new JTextField();
//		nameTF.addKeyListener((KeyListener) this);
		nameTF.setBounds(114, 30, 143, 34);
		getContentPane().add(nameTF);
		nameTF.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("\u670D\u52A1\u5668\u5730\u5740");
		lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(23, 74, 81, 34);
		getContentPane().add(lblNewLabel_1);
		
		hostIPTF = new JTextField();
		hostIPTF.setBounds(114, 74, 143, 34);
//		hostIPTF.addKeyListener((KeyListener) this);
		getContentPane().add(hostIPTF);
		try {
			String ip = (String)Inet4Address.getLocalHost().getHostAddress();
			hostIPTF.setText(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		hostIPTF.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("\u7AEF\u53E3\u53F7");
		lblNewLabel_2.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_2.setBounds(23, 118, 81, 34);
		getContentPane().add(lblNewLabel_2);
		
		hostportTF = new JTextField();
//		hostportTF.addKeyListener((KeyListener) this);
		hostportTF.setBounds(114, 118, 143, 34);
		getContentPane().add(hostportTF);
		hostportTF.setText(String.valueOf(port));
		hostportTF.setColumns(10);
		
		enterBtn = new JButton("\u8FDB\u5165\u804A\u5929\u5BA4");
		enterBtn.addActionListener(this);
//		enterBtn.addKeyListener((KeyListener) this);
		enterBtn.setFont(new Font("宋体", Font.PLAIN, 14));
		enterBtn.setBounds(23, 162, 108, 39);
		getContentPane().add(enterBtn);
		
		exitBtn = new JButton("\u9000\u51FA\u804A\u5929\u5BA4");
		exitBtn.setFont(new Font("宋体", Font.PLAIN, 14));
		exitBtn.setBounds(144, 162, 113, 39);
		exitBtn.addActionListener(this);
		getContentPane().add(exitBtn);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == exitBtn){
			setVisible(false);
			exitLogin();
		}
		
		if(e.getSource() == enterBtn){
			String username = nameTF.getText();
			username.trim();
			String hostIp = hostIPTF.getText();
			hostIp.trim();
			String hostPort = hostportTF.getText();
			hostPort.trim();
			if(!username.equals("")){
				if(!hostIp.equals("")){
					if(!hostPort.equals("")){
						login(username, hostIp, hostPort);
					}else{
						JOptionPane.showMessageDialog(this, "服务器连接端口号不能为空！");
					}
				}else{
					JOptionPane.showMessageDialog(this, "服务器地址不能为空！");
				}
			}else{
				JOptionPane.showMessageDialog(this, "用户名不能为空！");
			}
		}
	}

	public void login(String username, String hostIp, String hostPort) {
		String login_mess = null;
		try {
			GW_ChatClientThread mClientThread = new GW_ChatClientThread(new Socket(hostIp, Integer.parseInt(hostPort)), username);
			this.setVisible(false);
			GW_ChatFrame mChatFrame = new GW_ChatFrame(username,mClientThread);
			mChatFrame.setVisible(true);
			mClientThread.setmChatFrame(mChatFrame);
			mClientThread.setFlagExit(true);
			mClientThread.start();
		} catch (NumberFormatException e) {
			login_mess = "连接的服务器端口号port为整数,取值范围为：1024<port<65535";
			JOptionPane.showMessageDialog(this, login_mess);
		} catch (UnknownHostException e) {
			login_mess = "主机地址错误";
			JOptionPane.showMessageDialog(this, login_mess);
		} catch (IOException e) {
			login_mess = "连接服务其失败，请稍后再试";
			JOptionPane.showMessageDialog(this, login_mess);
		}
		
	}
	
	public void exitLogin() {
		System.exit(0);
	}
}
