package GW_Socket;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GW_ServerThread extends Thread{

	public GW_ServerFrame mFrame;
	public ServerSocket mServerSocket;
	public Socket mSocket;
	public Vector<String> messageArr;
	public Vector<GW_ServerClientThread> clientArr;
	public Map<Integer, String> users;
	public GW_BroadCast mBroadCast;
	private String ipString;
	private int port = 2018;
	public boolean login = true;
	private boolean flagExit = false;
	public GW_ServerThread(GW_ServerFrame frame) {
		mFrame = frame;
		try {
			ipString = (String)Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getMessageArr();
		getClientArr();
		getUsers();
		getmServerSocket();
		getmBroadCast();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		while (flagExit) {
			try {
				if (mServerSocket.isClosed()) {
//					服务器处于关闭状态
					flagExit = false;
				}else {
					try {
//						获取响应的socket
						mSocket = mServerSocket.accept();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						mSocket = null;
						flagExit = false;
					}
					
					if (mSocket != null) {
//						创建新线程运行socket
						GW_ServerClientThread clientThread = new GW_ServerClientThread(mSocket,this);
						clientThread.setFlagExit(true);
						clientThread.start();
						
						synchronized (clientArr) {
//							加锁，添加加入的线程
							clientArr.addElement(clientThread);
						}
						synchronized (messageArr) {
//							添加用户map消息，线程id/value
							users.put((int) clientThread.getId(), GW_ServerCommon.GW_UserJoin);
							System.out.println("users ="+users);
//							添加连接线程消息
							messageArr.add(clientThread.getId() + GW_ServerCommon.GW_ClientThread);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	
	
	public void stopServer() {
		try {
			if(this.isAlive()){
				setFlagExit(false);
				mServerSocket.close();
				mServerSocket =null;
				mSocket.close();
			}
		} catch (Throwable e) {}
	}
	
	public void setFlagExit(boolean flagExit) {
		this.flagExit = flagExit;
	}
	
	public ServerSocket getmServerSocket() {
		if (mServerSocket == null) {
			try {
				mServerSocket = new ServerSocket();
				mServerSocket.bind(new InetSocketAddress(ipString, port));
			} catch (Exception e) {
				// TODO: handle exception
				mFrame.setStartAndStopUnable();
				e.printStackTrace();
				System.exit(0);
			}
		}
		return mServerSocket;
	}
	
	public Vector<String> getMessageArr() {
		if (messageArr == null) {
			messageArr = new Vector<String>();
		}
		return messageArr;
	}
	
	public Vector<GW_ServerClientThread> getClientArr() {
		if (clientArr == null) {
			clientArr = new Vector<GW_ServerClientThread>();
		}
		return clientArr;
	}
	
	public Map<Integer, String> getUsers() {
		if (users == null) {
			users = new HashMap<Integer, String>();
		}
		return users;
	}
	
	public GW_BroadCast getmBroadCast() {
		if (mBroadCast == null) {
			mBroadCast = new GW_BroadCast(this);
			mBroadCast.setFlag_exit(true);
			mBroadCast.start();
		}
		return mBroadCast;
	}
	
}
