package GW_Socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GW_ChatClientThread extends Thread{

	public String userName;
	public Socket mSocket;
	private GW_ChatFrame mChatFrame;
	public DataInputStream mDataInputStream = null;
	public DataOutputStream mDataOutputStream = null;
	private boolean flagExit = false;
	private int threadID;
	public String readStr;
	public Map<String, GW_SingalFrame> singleFrames;
	public  List<String> username_online;
	public  List<Integer> clientuserid;
	public GW_ChatClientThread(Socket socket,String name) {
		userName = name;
		mSocket = socket;
		getSingleFrames();
		getUsername_online();
		getClientuserid();
		try {
			mDataInputStream = new DataInputStream(mSocket.getInputStream());
			mDataOutputStream = new DataOutputStream(mSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<String, GW_SingalFrame> getSingleFrames() {
		if (singleFrames == null) {
			singleFrames = new HashMap<String, GW_SingalFrame>();
		}
		return singleFrames;
	}
	
	public List<String> getUsername_online() {
		if (username_online == null) {
			username_online = new ArrayList<String>();
		}
		return username_online;
	}
	
	public List<Integer> getClientuserid() {
		if (clientuserid == null) {
			clientuserid = new ArrayList<Integer>();
		}
		return clientuserid;
	}
	
	public void setFlagExit(boolean flagExit) {
		this.flagExit = flagExit;
	}
	
	public void setThreadID(int threadID) {
		this.threadID = threadID;
	}
	
	public int getThreadID() {
		return threadID;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		while (flagExit) {
			try {
				readStr = mDataInputStream.readUTF();
				System.out.println("readStr = "+readStr);
			} catch (IOException e) {
				e.printStackTrace();
				flagExit = false;
				if(!readStr.contains(GW_ChatCommon.GW_ServerExit)){
					readStr = null;
				}
			}
			
			if (readStr != null) {
				if(readStr.contains(GW_ChatCommon.GW_ClientThread)){
					int local = readStr.indexOf(GW_ChatCommon.GW_ClientThread);
					setThreadID(Integer.parseInt(readStr.substring(0, local)));
					try {
						mDataOutputStream.writeUTF(userName + GW_ChatCommon.GW_login + getThreadID() + GW_ChatCommon.GW_login);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					if(readStr.contains(GW_ChatCommon.GW_UserList)){
						System.out.println("readStr ="+readStr);
						mChatFrame.setDisUsers(readStr);
					}else{
						if(readStr.contains(GW_ChatCommon.GW_Chat)){
							mChatFrame.setDisMess(readStr);
						}else{
							if(readStr.contains(GW_ChatCommon.GW_ServerExit)){
								mChatFrame.closeClient();
							}else{
								if(readStr.contains(GW_ChatCommon.GW_Single)){
									mChatFrame.setSingleFrame(readStr);
								}
							}
						}
					}
				}
			}
		}
	}

	public GW_ChatFrame getmChatFrame() {
		return mChatFrame;
	}

	public void setmChatFrame(GW_ChatFrame mChatFrame) {
		this.mChatFrame = mChatFrame;
	}
	
	public void exitChat() {
		try {
			System.out.println("exit = "+userName + GW_ChatCommon.GW_Exit + getThreadID() + GW_ChatCommon.GW_Exit);
			mDataOutputStream.writeUTF(userName + GW_ChatCommon.GW_Exit + getThreadID() + GW_ChatCommon.GW_Exit);
			exitClient();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void exitClient() {
		flagExit = false;
		System.exit(0);
	}
	
	public void transMess(String mess) {
		try {
			mDataOutputStream.writeUTF(userName  + GW_ChatCommon.GW_Chat + threadID + GW_ChatCommon.GW_Chat+ mess + GW_ChatCommon.GW_Chat);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
