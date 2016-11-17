package client.gui;

public class GUIManage {

	public static volatile GUIManage UniqueGUIManage = new GUIManage();
	
	private LoginDialog logindaialog;
	private MainWindow mainwindow;
	
	private  GUIManage(){
		
	}

	public LoginDialog getLogindaialog() {
		return logindaialog;
	}

	void setLogindaialog(LoginDialog logindaialog) {
		this.logindaialog = logindaialog;
	}

	public MainWindow getMainwindow() {
		return mainwindow;
	}

	void setMainwindow(MainWindow mainwindow) {
		this.mainwindow = mainwindow;
	}

	public static GUIManage getUniqueGUIManage() {
		return UniqueGUIManage;
	}
	
	
}
