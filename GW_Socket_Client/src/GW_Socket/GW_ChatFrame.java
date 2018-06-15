package GW_Socket;

import java.awt.Font;
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
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;




public class GW_ChatFrame extends JFrame implements ActionListener, ListSelectionListener{
	private static final long serialVersionUID = 1L;
	private JTextField inMessageTF;
	private JTextArea disMessageTA;
	private JButton transBtn;
	private JButton clearBtn;
	private JButton exitBtn;
	private JList<Object> usersList;
	private JButton singalChatBtn;
	private JScrollPane scrollPane_1;
	private GW_ChatClientThread mClientThread;
	
	public GW_ChatFrame(String name,GW_ChatClientThread clientThread) {
//		this.userName = name;
		this.mClientThread = clientThread;
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		setIconImage(Toolkit.getDefaultToolkit().getImage("Images/socket.jpg"));
		setTitle("\u804A\u5929\u5BA4" + "  " + name);
		setSize(450, 325);
		GW_WinCerter.center(this);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				exitBtn.doClick();
			}
		});
		getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createTitledBorder("聊天消息"));
		scrollPane.setBounds(10, 10, 283, 167);
		scrollPane.setWheelScrollingEnabled(true);
		getContentPane().add(scrollPane);

		disMessageTA = new JTextArea();
		disMessageTA.setEditable(false);
		scrollPane.setViewportView(disMessageTA);

		inMessageTF = new JTextField();
//		inMessageTF.addKeyListener((KeyListener) this);
		inMessageTF.setBounds(10, 242, 192, 32);
		getContentPane().add(inMessageTF);
		inMessageTF.setColumns(10);
		
		transBtn = new JButton("\u53D1  \u9001");
		transBtn.setFont(new Font("宋体", Font.PLAIN, 14));
		transBtn.setBounds(212, 241, 93, 32);
		transBtn.addActionListener(this);
		getContentPane().add(transBtn);

		clearBtn = new JButton("\u6E05\u9664\u804A\u5929\u8BB0\u5F55");
		clearBtn.setFont(new Font("宋体", Font.PLAIN, 14));
		clearBtn.setBounds(158, 187, 135, 37);
		clearBtn.addActionListener(this);
		getContentPane().add(clearBtn);

		exitBtn = new JButton("\u9000\u51FA\u804A\u5929\u5BA4");
		exitBtn.setFont(new Font("宋体", Font.PLAIN, 14));
		exitBtn.setBounds(20, 189, 128, 37);
		exitBtn.addActionListener(this);
		getContentPane().add(exitBtn);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBorder(BorderFactory.createTitledBorder("在线用户"));
		scrollPane_1.setBounds(303, 10, 128, 214);
		getContentPane().add(scrollPane_1);
		
		usersList = new JList<Object>();
		usersList.setVisibleRowCount(4);
		usersList.setSelectedIndex(0);
		usersList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		usersList.addListSelectionListener(this);
		scrollPane_1.setViewportView(usersList);
		
		singalChatBtn = new JButton("\u5355\u4EBA\u804A\u5929");
		singalChatBtn.setBounds(315, 241, 116, 32);
		singalChatBtn.addActionListener(this);
		getContentPane().add(singalChatBtn);
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == clearBtn) {
			disMessageTA.setText("");
		}
		if (e.getSource() == transBtn) {
			String mess = inMessageTF.getText();
			mess.trim();
			inMessageTF.setText("");
			if (mess.equals("")) {
				JOptionPane.showMessageDialog(this, "不能发送空消息");
				inMessageTF.setText("");
			} else {
				mClientThread.transMess(mess);
			}
		}
		if (e.getSource() == exitBtn) {
			if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(this,
					"是否确定要退出聊天室？", "提示", JOptionPane.OK_CANCEL_OPTION)) {
				this.setVisible(false);
				mClientThread.exitChat();
				System.exit(0);
			}
		}
		
		if (e.getSource() == singalChatBtn) {
			String user_names = (String) usersList.getSelectedValue();
			if (user_names == null) {
				JOptionPane.showMessageDialog(this, "您未选择聊天对象\n请选择要单独聊天的对象");
			} else {
				if (!mClientThread.singleFrames.containsKey(user_names)) {
					createSingleChatFrame(user_names);
				} else {
					mClientThread.singleFrames.get(user_names)
							.setFocusableWindowState(true);
				}
			}
		}
	}

	public void setDisUsers(String chat_re) {
		String[] infos = chat_re.split(GW_ChatCommon.GW_UserList);
		String[] info = new String[infos.length / 2];
		for (int i = 1; i < infos.length; i++) {
			int id_user = 0;
			try {
				id_user = Integer.parseInt(infos[i]);
				if (mClientThread.getThreadID() == id_user) {
					if (!mClientThread.userName.equals(infos[i - 1])) {
						JOptionPane.showMessageDialog(this,
								"由于有同名的用户登录，所以您的用户名后面加上了编号");
						mClientThread.userName = infos[i - 1];
						this.setTitle("聊天室    " + mClientThread.userName);
						break;
					} else {
						break;
					}
				} else {
					i++;
				}
			} catch (Exception e) {
			}
		}
		if (infos.length == 2) {
			String[] s = new String[] {};
			if (!mClientThread.singleFrames.isEmpty()) {
				ListModel<Object> list = usersList.getModel();
				for (int i = 0; i < list.getSize(); i++) {
					if (mClientThread.singleFrames.get(list.getElementAt(i)) != null) {
						mClientThread.singleFrames.get(list.getElementAt(i))
								.setExitNotify();
					}
				}
			}
			usersList.removeAll();
			System.out.println("s ="+s);
			usersList.setListData(s);
		} else {
			if ((infos.length / 2 - 1) < mClientThread.username_online.size()) {
				// 有人下线
				ArrayList<String> rec = new ArrayList<String>();
				int i = 0;
				for (; i < infos.length; i++) {
					System.out.println("infos[i] ="+infos[i]+"i ="+i);
					rec.add(0, infos[i++]);
				}
				for (i = 0; i < mClientThread.username_online.size(); i++) {
					System.out.println("get(i) ="+mClientThread.username_online.get(i));
					if (!rec.contains(mClientThread.username_online.get(i))) {
						break;
					}
				}
				String name = mClientThread.username_online.get(i);
				mClientThread.username_online.remove(i);
				try {
					mClientThread.clientuserid.remove(i);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (mClientThread.singleFrames.containsKey(name)) {
					mClientThread.singleFrames.get(name).closeSingleFrame();
					mClientThread.singleFrames.remove(name);
				}
			} else {
				ArrayList<Integer> online = new ArrayList<Integer>();
				for (int i = 0; i < mClientThread.username_online.size(); i++) {
					online.add(0, mClientThread.clientuserid.get(i));
				}
				if (online.isEmpty()) {
					for (int i = 1; i < infos.length; i++) {
						if ((int) Integer.parseInt(infos[i]) != mClientThread
								.getThreadID()) {
							mClientThread.username_online.add(0, infos[i - 1]);
							mClientThread.clientuserid.add(0,
									Integer.parseInt(infos[i]));
						}
						i++;
					}
				} else {
					for (int i = 1; i < infos.length; i++) {
						if (Integer.parseInt(infos[i]) != mClientThread.getThreadID()) {
							if (!online.contains(Integer.parseInt(infos[i]))) {
								mClientThread.username_online.add(0, infos[i - 1]);
								mClientThread.clientuserid.add(0,
										Integer.parseInt(infos[i]));
							} else {
								String name = mClientThread.username_online
										.get(mClientThread.clientuserid
												.indexOf(Integer
														.parseInt(infos[i])));
								if (!name.equals(infos[i - 1])) {
									if (mClientThread.singleFrames.containsKey(name)) {
										GW_SingalFrame cf = mClientThread.singleFrames
												.get(name);
										cf.setTitle(name);
										mClientThread.singleFrames.remove(name);
										mClientThread.singleFrames.put(name, cf);
										cf.setVisible(false);

									}
									mClientThread.username_online.remove(name);
									mClientThread.clientuserid.remove(new Integer(
											Integer.parseInt(infos[i])));
									mClientThread.username_online.add(0, infos[i - 1]);
									mClientThread.clientuserid.add(0,
											Integer.parseInt(infos[i]));
								}
							}
						}
						i++;
					}
				}

			}
			try {
				for (int i = 0; i < mClientThread.username_online.size(); i++) {
					info[i] = mClientThread.username_online.get(i);
				}

			} catch (Exception e) {
			}
			usersList.removeAll();
			usersList.setListData(info);
		}
	}
	
	public void closeClient() {
		JOptionPane.showMessageDialog(this, "服务器已关闭", "提示",
				JOptionPane.OK_OPTION);
		mClientThread.exitClient();
		setVisible(false);
	}
	
	public void createSingleChatFrame(String name) {
		GW_SingalFrame c_singlFrame = new GW_SingalFrame(mClientThread, name);
		mClientThread.singleFrames.put(name, c_singlFrame);
		try {
			c_singlFrame.userThreadID = mClientThread.clientuserid
					.get(mClientThread.username_online.indexOf(name));
		} catch (Exception e) {
		}

		c_singlFrame.setVisible(true);
	}
	
	public void setSingleFrame(String chat_re) {
		String[] infos = chat_re.split(GW_ChatCommon.GW_Single);
		try {
			if (mClientThread.singleFrames.containsKey(infos[0])) {
				mClientThread.singleFrames.get(infos[0]).setDisMess(infos[3]);
			} else {
				createSingleChatFrame(infos[0]);
				mClientThread.singleFrames.get(infos[0]).setDisMess(infos[3]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public void setDisMess(String substring) {
		int local = substring.indexOf(GW_ChatCommon.GW_Chat);
		disMessageTA.append(substring.substring(0, local) + "\n");
		disMessageTA.setCaretPosition(disMessageTA.getText().length());
		System.out.println("disMessageTA = "+substring);
	}
}
