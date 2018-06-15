package GW_Socket;

import java.io.IOException;

public class GW_BroadCast extends Thread{
	private boolean flag_exit = false;
	
	GW_ServerThread serverThread;
	GW_ServerClientThread clientThread;
	String mString;
	public GW_BroadCast(GW_ServerThread serverThead) {
		this.serverThread = serverThead;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		boolean flag = true;
		while(flag_exit){
			synchronized (serverThread.messageArr) {
				if(serverThread.messageArr.isEmpty()){
					continue;
				}else{
//					array里面有消息，把消息拿出来
					mString = (String)serverThread.messageArr.firstElement();
//					再把array里面的消息删除
					serverThread.messageArr.removeElement(mString);
					
					System.out.println("mString ="+mString);
					if(mString.contains(GW_ServerCommon.GW_ClientThread)){
						flag = false;
					}
				}
			}
			synchronized (serverThread.clientArr) {
				for(int i=0; i < serverThread.clientArr.size(); i++)
                {
                    clientThread = serverThread.clientArr.elementAt(i);
                    if(flag){
                    	try
                        {
                        	//向纪录的每一个客户端发送数据信息
                            if(mString.contains(GW_ServerCommon.GW_Exit)){
                            	serverThread.clientArr.remove(i);
                            	clientThread.closeClienthread(clientThread);
                            	clientThread.dataOutputStream.writeUTF(mString);
                            }
                            if(mString.contains(GW_ServerCommon.GW_Chat) || mString.contains(GW_ServerCommon.GW_UserList) || mString.contains(GW_ServerCommon.GW_ServerExit)){
                            	clientThread.dataOutputStream.writeUTF(mString);
                            }
                            if(mString.contains(GW_ServerCommon.GW_Single)){
                            	String[] info = mString.split(GW_ServerCommon.GW_Single);
                            	int id_thread = Integer.parseInt(info[2]);
                            	for(int j = 0; j < serverThread.clientArr.size(); j++){
                            		if(id_thread == serverThread.clientArr.get(j).getId()){
                            			serverThread.clientArr.get(j).dataOutputStream.writeUTF(mString);
                            			i = serverThread.clientArr.size();
                            			break;
                            		}
                            	}
                            }
                        }
                        catch(IOException E){}
                    }else{
                    	String value = serverThread.users.get((int)clientThread.getId());
                    	if(value.equals(GW_ServerCommon.GW_UserJoin)){
                    		flag = true;
                    		try
                            {
                            	//向纪录的每一个客户端发送数据信息
                    			System.out.println("mString ="+mString);
                                clientThread.dataOutputStream.writeUTF(mString);
                                if(mString.contains(GW_ServerCommon.GW_Exit)){
                                	serverThread.clientArr.remove(i);
                                	clientThread.closeClienthread(clientThread);
                                }
                            }
                            catch(IOException E){}
                    		break;
                    	}
                    }
                }
			}
			if(mString.contains(GW_ServerCommon.GW_ServerExit)){
				serverThread.users.clear();
				flag_exit = false;
			}
		}
	}
	
	public void setFlag_exit(boolean flag_exit) {
		this.flag_exit = flag_exit;
	}
}
