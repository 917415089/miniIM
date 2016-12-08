package server;

import org.junit.Test;

import client.ClientManage;
import client.gui.GUILoginDialog;
import client.gui.GUIMainWindow;


public class GUITest {

	@Test
	public void starBaseServer() throws InterruptedException{
		Thread thread = new Thread(new BaseServer());
		thread.start();
		while(true) Thread.sleep(10000);
	}
	
	@Test
	public void GUILogin() throws InterruptedException{
		GUILoginDialog loginDialog = ClientManage.getLogindaialog();
		loginDialog.setVisible(true);
		Thread.sleep(2000);
		while(true) Thread.sleep(10000);
	}
	
	@Test
	public synchronized void GUIFriendListAdd() throws InterruptedException{
		GUIMainWindow mainwindow = ClientManage.getMainwindow();
		mainwindow.setVisible(true);
		mainwindow.addPathNode("group1.friend1");
		mainwindow.addPathNode("group1.friend2");
		mainwindow.addPathNode("group1.friend3");
		mainwindow.addPathNode("group1.friend4");
		Thread.sleep(2000);
	}
	
	@Test
	public void GUIFriendListRm() throws InterruptedException{
		GUIMainWindow mainwindow = ClientManage.getMainwindow();
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
		GUIMainWindow mainwindow = ClientManage.getMainwindow();
		mainwindow.setVisible(true);
		mainwindow.addSession("user2");
		mainwindow.addSession("user3");
		mainwindow.addSession("user4");
		Thread.sleep(2000);
		while(true);
	}
	
	@Test
	public void GUISessionRm() throws InterruptedException{
		GUIMainWindow mainwindow = ClientManage.getMainwindow();
		mainwindow.setVisible(true);
		mainwindow.addSession("user2");
		mainwindow.rmSession("user2");
		Thread.sleep(2000);
	}
}
