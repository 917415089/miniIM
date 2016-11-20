package server;

import org.junit.Test;

import client.ClientManage;
import client.gui.LoginDialog;
import client.gui.MainWindow;


public class GUITest {

	@Test
	public void starBaseServer() throws InterruptedException{
		Thread thread = new Thread(new BaseServer());
		thread.start();
		while(true) Thread.sleep(10000);
	}
	
	@Test
	public void GUILogin() throws InterruptedException{
//		BaseServer baseServer = new BaseServer();
		LoginDialog loginDialog = ClientManage.getLogindaialog();
		loginDialog.setVisible(true);
//		ExecutorService pool = Executors.newCachedThreadPool();
//		pool.submit(baseServer);
		Thread.sleep(2000);
		while(true);
	}
	
	@Test
	public void GUIFriendListAdd() throws InterruptedException{
		MainWindow mainwindow = ClientManage.getMainwindow();
		mainwindow.setVisible(true);
		mainwindow.addPathNode("group1.friend1");
		mainwindow.addPathNode("group1.friend2");
		mainwindow.addPathNode("group1.friend3");
		mainwindow.addPathNode("group1.friend4");
		Thread.sleep(2000);
	}
	
	@Test
	public void GUIFriendListRm() throws InterruptedException{
		MainWindow mainwindow = ClientManage.getMainwindow();
		mainwindow.setVisible(true);
		mainwindow.addPathNode("group1.friend1");
		mainwindow.addPathNode("group1.friend2");
		mainwindow.addPathNode("group1.friend3");
		mainwindow.addPathNode("group1.friend4");
		mainwindow.removePathNode("group1.friend1");
		mainwindow.removePathNode("group1.friend2");
		mainwindow.removePathNode("group1.friend3");
		mainwindow.removePathNode("group1.friend4");
		Thread.sleep(2000);
	}
	
	@Test
	public void GUISessionAdd() throws InterruptedException{
		MainWindow mainwindow = ClientManage.getMainwindow();
		mainwindow.setVisible(true);
		mainwindow.addSession("user2");
		mainwindow.addSession("user3");
		mainwindow.addSession("user4");
		Thread.sleep(2000);
		while(true);
	}
	
	@Test
	public void GUISessionRm() throws InterruptedException{
		MainWindow mainwindow = ClientManage.getMainwindow();
		mainwindow.setVisible(true);
		mainwindow.addSession("user2");
		mainwindow.rmSession("user2");
		Thread.sleep(2000);
	}
}
