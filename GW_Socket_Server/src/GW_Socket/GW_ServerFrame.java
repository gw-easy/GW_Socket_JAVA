package GW_Socket;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class GW_ServerFrame extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;

	private GW_ServerThread mServerThread;
	private JButton startBtn;
	private JButton stopBtn;
	private JButton exitBtn;
	private JTextArea textArea;
	private JList<String> userList;
	public ArrayList<String> serverArr = new ArrayList<String>();
	public ArrayList<Integer> serveridArr = new ArrayList<Integer>();
	public GW_ServerFrame() {
		// TODO Auto-generated constructor stub

		try {
//			���ô������
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
		setTitle("������");
		setIconImage(Toolkit.getDefaultToolkit().getImage("socket.jpg"));
//		���ô��ڳߴ�
		setSize(500, 400);
//		���ڴ�С���Ƿ�������ɱ任
		setResizable(false);
		GW_WinCerter.center(this);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
//				super.windowClosing(arg0);
				exitBtn.doClick();
			}
		});
		getContentPane().setLayout(null);
		
		
		startBtn = new JButton("\u542F\u52A8\u670D\u52A1\u5668");
		startBtn.setBounds(32, 23, 103, 34);
		startBtn.addActionListener(this);
		getContentPane().add(startBtn);
		
		stopBtn = new JButton("\u505C\u6B62\u670D\u52A1\u5668");
		stopBtn.setBounds(145, 23, 103, 34);
		stopBtn.setEnabled(false);
		stopBtn.addActionListener(this);
		getContentPane().add(stopBtn);
		
		exitBtn = new JButton("\u9000\u51FA\u670D\u52A1\u5668");
		exitBtn.setBounds(258, 23, 103, 34);
		exitBtn.addActionListener(this);
		getContentPane().add(exitBtn);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 64, 221, 192);
//		�Ƿ���Ӧ�����¼�
		scrollPane.setWheelScrollingEnabled(true);
		scrollPane.setBorder(BorderFactory.createTitledBorder("������Ϣ"));
		getContentPane().add(scrollPane);
		
//		�����ı���
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(258, 65, 157, 191);
		scrollPane_1.setBorder(BorderFactory.createTitledBorder("�����û�"));
		getContentPane().add(scrollPane_1);
		
		userList = new JList<String>();
		userList.setVisibleRowCount(4);
		scrollPane_1.setViewportView(userList);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
//		e.getSource() ��ȡ�¼���ִ����
		if (e.getSource() == startBtn) {
			startBtn.setEnabled(false);
			stopBtn.setEnabled(true);
			startServer();
		}else if (e.getSource() == stopBtn) {
			int flag = JOptionPane.showConfirmDialog(this, "�Ƿ�Ҫֹͣ��������", "��ʾ", 
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (flag == JOptionPane.OK_OPTION) {
				stopServer();
				startBtn.setEnabled(true);
				stopBtn.setEnabled(false);
			}
		}else if (e.getSource() == exitBtn) {
			if (stopBtn.isEnabled()) {
				stopBtn.doClick();
			}
			close();
		}
	}
	
	public void setDisUsers(String userNames) {
		if(userNames.equals(GW_ServerCommon.GW_UserList)){
			userList.removeAll();
			String[] user_null = new String[]{};
			userList.setListData(user_null);
		}else{
			if(userNames.contains(GW_ServerCommon.GW_UserList)){
				String[] dis = userNames.split(GW_ServerCommon.GW_UserList);
				String [] disUsernames = new String[dis.length / 2];
				int j = 0;
				for(int i = 0; i < dis.length; i++){
					disUsernames[j++] = dis[i++];
				}
				userList.removeAll();
				userList.setListData(disUsernames);
			}
			if(userNames.contains(GW_ServerCommon.GW_Exit)){
				String[] dis = {};
				userList.setListData(dis);
				System.out.println("userList ="+userList);
			}
		}
	}
	
	public void setDisMess(String message) {
		if(message.contains(GW_ServerCommon.GW_Chat)){
			int local = message.indexOf(GW_ServerCommon.GW_Chat);
			textArea.append(message.substring(0, local) + "\n");
			textArea.setCaretPosition(textArea.getText().length());
		}
		if(message.contains(GW_ServerCommon.GW_Exit)){
			textArea.setText("");
		}
	}
	
//	����������
	public void startServer() {
		try{
			mServerThread = new GW_ServerThread(this);
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		mServerThread.setFlagExit(true);
		mServerThread.start();
	}
//	�رշ�����
	public void stopServer() {
		synchronized (mServerThread.messageArr) {
			String str = GW_ServerCommon.GW_ServerExit;
			mServerThread.messageArr.add(str);
		}
		setDisMess(GW_ServerCommon.GW_Exit);
		setDisUsers(GW_ServerCommon.GW_Exit);
		mServerThread.stopServer();
	}
	
//	�˳�
	public void close() {
		if(mServerThread != null){
//			�߳��Ƿ��ǻ�Ծ״̬
			if(mServerThread.isAlive()){
				mServerThread.stopServer();
			}
		}
		System.exit(0);
	}

	public void setStartAndStopUnable() {
		JOptionPane.showMessageDialog(this, "����ͬʱ��������������");
		startBtn.setEnabled(false);
		stopBtn.setEnabled(false);
	}
	
	public GW_ServerThread getmServerThread() {
		if (mServerThread == null) {
			
		}
		return mServerThread;
	}
	
}
