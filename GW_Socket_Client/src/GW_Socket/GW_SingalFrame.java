package GW_Socket;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class GW_SingalFrame extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JTextArea disMessageTA;
	private JTextField inMessageTF;
	private JButton transBtn;
	public int userThreadID = 0;
	private GW_ChatClientThread mClientThread;
	public GW_SingalFrame(GW_ChatClientThread mClientThread,String userName) {
		this.mClientThread = mClientThread;
		init(userName);
	}
	
	private void init(String title) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		setIconImage(Toolkit.getDefaultToolkit().getImage("socket.jpg"));
		GW_WinCerter.center(this);
		setTitle(title);
		setSize(400, 400);
		setResizable(false);
		setContentPane(createContentPanel());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeSingleFrame();
			}
		});
	}
	
	private Container createContentPanel() {
		JPanel jp = new JPanel();
		jp.setBorder(BorderFactory.createTitledBorder("聊天消息"));
		jp.setLayout(new BorderLayout());
		disMessageTA = new JTextArea();
		disMessageTA.setEditable(false);
		jp.add(BorderLayout.CENTER, new JScrollPane(disMessageTA));
		jp.add(BorderLayout.SOUTH, createInput());
		return jp;
	}
	
	private Component createInput() {
		JPanel jp = new JPanel();
		jp.setBorder(BorderFactory.createTitledBorder("发送消息"));
		jp.setLayout(new BorderLayout());
		inMessageTF = new JTextField();
//		inMessageTF.addKeyListener((KeyListener) this);
		transBtn = new JButton("发送");
		transBtn.addActionListener((ActionListener) this);
		jp.add(inMessageTF, BorderLayout.CENTER);
		jp.add(transBtn, BorderLayout.EAST);
		return jp;
	}
	
	public void setDisMess(String chat_re) {
		disMessageTA.append(chat_re + "\n");
		disMessageTA.setCaretPosition(disMessageTA.getText().length());
	}
	
	public void closeSingleFrame(){
		mClientThread.singleFrames.remove(this.getTitle());
		setVisible(false);
	}

	public void setExitNotify() {
		disMessageTA.append(this.getTitle() + "已下线.....");
		transBtn.setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == transBtn){
			String str = inMessageTF.getText();
			str.trim();
			inMessageTF.setText("");
			if(str.equals("")){
				JOptionPane.showMessageDialog(this, "信息不能为空");
			}else{
				SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
				String date = form.format(new Date());
				String mess = mClientThread.userName + "  " + date + "\n" + str;
				disMessageTA.append(mess + "\n");
				disMessageTA.setCaretPosition(disMessageTA.getText().length());
				int index = mClientThread.username_online.indexOf(this.getTitle());
				String info = mClientThread.userName + GW_ChatCommon.GW_Single + mClientThread.getThreadID() + GW_ChatCommon.GW_Single +
								(int)mClientThread.clientuserid.get(index) + GW_ChatCommon.GW_Single + 
								mess + GW_ChatCommon.GW_Single;
				try {
					mClientThread.mDataOutputStream.writeUTF(info);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}
}
