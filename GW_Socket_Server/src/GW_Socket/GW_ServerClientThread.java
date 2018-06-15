package GW_Socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GW_ServerClientThread extends Thread{

	public Socket mSocket;
	public GW_ServerThread mServerThead;
	public DataInputStream dInputStream;
	public DataOutputStream dataOutputStream;
	public String client_userID;
	private boolean flagExit = false;
	public GW_ServerClientThread(Socket socket,GW_ServerThread serverThead) {
		this.mSocket = socket;
		this.mServerThead = serverThead;
		try {
			dInputStream = new DataInputStream(mSocket.getInputStream());
			dataOutputStream = new DataOutputStream(mSocket.getOutputStream());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while (flagExit) {
			try {
//				读取数据
				String message = dInputStream.readUTF();
				System.out.println("message = "+message);
				if (message.contains(GW_ServerCommon.GW_login)) {
//					用@login切分字符串
					String[] userInfo = message.split(GW_ServerCommon.GW_login);
					
					System.out.println("userInfo = "+userInfo);
					
					int userID = Integer.parseInt(userInfo[1]);
					mServerThead.users.remove(userID);
					if (mServerThead.users.containsValue(userInfo[0])) {
						for (int i = 0; i < mServerThead.clientArr.size(); i++) {
							int id = (int) mServerThead.clientArr.get(i).getId();
							System.out.println("mServerThead.users.get(id) ="+mServerThead.users.get(id));
							if (mServerThead.users.get(id).equals(userInfo[0])) {
								mServerThead.users.remove(id);
								mServerThead.users.put(id, userInfo[0]+"_"+id);
								break;
							}
						}
						mServerThead.users.put(Integer.parseInt(userInfo[1]),  userInfo[0]+"_"+userInfo[1]);
					}else {
						mServerThead.users.put(userID, userInfo[0]);
					}
					
					message = null;
					StringBuffer sBuffer = new StringBuffer();
					synchronized (mServerThead.clientArr) {
						for (int i = 0; i <mServerThead.clientArr.size(); i++) {
							int threadID = (int) mServerThead.clientArr.elementAt(i).getId();
							System.out.println("threadID = "+ threadID);
							sBuffer.append((String)mServerThead.users.get(new Integer(threadID)) + GW_ServerCommon.GW_UserList);
							sBuffer.append(threadID + GW_ServerCommon.GW_UserList);
						}
					}
					String userNames = new String(sBuffer);
					System.out.println("userNames ="+userNames);
					mServerThead.mFrame.setDisUsers(userNames);
					message = userNames;
				}else {
					if (message.contains(GW_ServerCommon.GW_Exit)) {
						String [] userInfo = message.split(GW_ServerCommon.GW_Exit);
						int userID = Integer.parseInt(userInfo[1]);
						System.out.println("userID ="+userID);
						mServerThead.users.remove(userID);
						message = null;
						StringBuffer sb = new StringBuffer();
						System.out.println("mServerThead.clientArr ="+mServerThead.clientArr);
						synchronized (mServerThead.clientArr) {
							for(int i = 0; i < mServerThead.clientArr.size(); i++){
								int threadID = (int) mServerThead.clientArr.elementAt(i).getId();
								if(userID == threadID){
									mServerThead.clientArr.removeElementAt(i);
									i--;
								}else{
									sb.append((String)mServerThead.users.get(new Integer(threadID)) + GW_ServerCommon.GW_UserList);
									sb.append(threadID + GW_ServerCommon.GW_UserList);
								}
							}
						}
						String userNames = new String(sb);
						System.out.println("userNames ="+userNames);
						if(userNames.equals("")){
							mServerThead.mFrame.setDisUsers(GW_ServerCommon.GW_UserList);
						}else{
							mServerThead.mFrame.setDisUsers(userNames);
						}
						message = userNames;
					}else{
						if(message.contains(GW_ServerCommon.GW_Chat)){
							String[] chat = message.split(GW_ServerCommon.GW_Chat);
							StringBuffer sb = new StringBuffer();
							SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
							String date = form.format(new Date());
							sb.append(chat[0] + "  " + date + "\n");
							sb.append(chat[2] + GW_ServerCommon.GW_Chat);
							String str = new String(sb);
							message = str;
							mServerThead.mFrame.setDisMess(message);
						}else{
							if(message.contains(GW_ServerCommon.GW_Single)){
								
							}
						}
					}
				}
				synchronized (mServerThead.messageArr) {
					if(message != null){
						mServerThead.messageArr.addElement(message);
					}
				}
				if(message.contains(GW_ServerCommon.GW_Exit)){
					this.mSocket.close();
					flagExit = false;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	public void closeClienthread(GW_ServerClientThread clientThread) {
		try {
			setFlagExit(false);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		if(clientThread.mSocket != null){
			try {
				clientThread.mSocket.close();
			} catch (IOException e) {
				System.out.println("server's clientSocket is null");
			}
		}
	}
	
	public void setFlagExit(boolean flagExit) {
		this.flagExit = flagExit;
	}
}
