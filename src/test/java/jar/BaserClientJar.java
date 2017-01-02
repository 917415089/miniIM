package jar;

import client.ClientManage;
import client.gui.GUILoginDialog;

public class BaserClientJar {

	public static void main(String[] args) {
		GUILoginDialog loginDialog = ClientManage.getLogindaialog();
		loginDialog.setVisible(true);
		try {
			Thread.sleep(2000);
			while(true) Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
