package GW_Socket;

public class GW_Client {
	
	public static void clientStart() {
		GW_EnterFrame mEnterFrame = new GW_EnterFrame();
		mEnterFrame.setVisible(true);
	}
	
	public static void main(String[] args) {
		try {
			GW_Client.clientStart();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
