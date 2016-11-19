package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import client.gui.GUIManage;
import client.gui.LoginDialog;
import client.gui.MainWindow;


public class GUITest {

	@Test
	public void GUILogin() throws InterruptedException{
		BaseServer baseServer = new BaseServer();
		LoginDialog loginDialog = new LoginDialog();
		ExecutorService pool = Executors.newCachedThreadPool();
		pool.submit(baseServer);
		pool.submit(loginDialog);
		Thread.sleep(2000);
	}
	
	@Test
	public void GUIFriendListAdd() throws InterruptedException{
		MainWindow mainwindow = GUIManage.getMainwindow();
		mainwindow.setVisible(true);
		mainwindow.addPathNode("group1.friend1");
		mainwindow.addPathNode("group1.friend2");
		mainwindow.addPathNode("group1.friend3");
		mainwindow.addPathNode("group1.friend4");
		Thread.sleep(2000);
	}
	
	@Test
	public void GUIFriendListRm() throws InterruptedException{
		MainWindow mainwindow = GUIManage.getMainwindow();
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
		MainWindow mainwindow = GUIManage.getMainwindow();
		mainwindow.setVisible(true);
		mainwindow.addSession("user2");
		Thread.sleep(2000);
	}
	
	@Test
	public void GUISessionRm() throws InterruptedException{
		MainWindow mainwindow = GUIManage.getMainwindow();
		mainwindow.setVisible(true);
		mainwindow.addSession("user2");
		mainwindow.rmSession("user2");
		Thread.sleep(2000);
	}
}
