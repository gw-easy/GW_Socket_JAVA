package GW_Socket;

public class GW_Server {
	
	public static void serverStart() {
		GW_ServerFrame mFrame = new GW_ServerFrame();
		mFrame.setVisible(true);
	}
	
	public static void main(String[] args) {
		GW_Server.serverStart();
	}
}
