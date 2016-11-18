package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import client.gui.GUIManage;
import client.gui.LoginDialog;
import client.gui.MainWindow;


public class GUITest {

	@Test
	public void GUILogin(){
		BaseServer baseServer = new BaseServer();
		LoginDialog loginDialog = new LoginDialog();
		ExecutorService pool = Executors.newCachedThreadPool();
		pool.submit(baseServer);
		pool.submit(loginDialog);
		while(true);
	}
	
	@Test
	public void GUIFriendList(){
		MainWindow mainJFrame = new MainWindow();
		GUIManage.getUniqueGUIManage().setMainwindow(mainJFrame);

		mainJFrame.addPathNode("group1.friend1");
		mainJFrame.addPathNode("group1.friend2");
		mainJFrame.addPathNode("group1.friend3");
		mainJFrame.addPathNode("group1.friend4");
		mainJFrame.removePathNode("group1.friend1");
		while(true);
	}
}
