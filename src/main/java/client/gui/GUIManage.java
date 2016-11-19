package client.gui;

public class GUIManage {

	public static volatile GUIManage UniqueGUIManage = new GUIManage();
	
	private static LoginDialog logindaialog;
	private static MainWindow mainwindow = new MainWindow();
	
	private  GUIManage(){
		
	}

	static public LoginDialog getLogindaialog() {
		return logindaialog;
	}

	static void setLogindaialog(LoginDialog daialog) {
		logindaialog = daialog;
	}

	static public MainWindow getMainwindow() {
		return mainwindow;
	}

	public static GUIManage getUniqueGUIManage() {
		return UniqueGUIManage;
	}
		
}
