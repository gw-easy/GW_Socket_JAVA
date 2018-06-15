package GW_Socket;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

public class GW_WinCerter {

	public static void center(Window win) {
		Toolkit mtToolkit = Toolkit.getDefaultToolkit();
//		获取电脑屏幕尺寸
		Dimension sSize = mtToolkit.getScreenSize();
		System.out.println("sSize ="+sSize);
//		获取窗口尺寸
		Dimension wSize = win.getSize();
		System.out.println("wSize ="+wSize);
		
		if(wSize.height > sSize.height){
			wSize.height = sSize.height;
		}
		if(wSize.width > sSize.width){
			wSize.width = sSize.width;
		}
//		设置窗口的point
		win.setLocation((sSize.width - wSize.width)/2, (sSize.height-wSize.height)/2);
	}
}
