package client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import json.client.session.SendMessage;
import json.util.JSONNameandString;
import client.gui.GUILoginDialog;
import client.gui.GUIMainWindow;

public class ClientManage {
	static public final int QUEUE_LENGTH = 100;
	static public final int RECEQUE_LENGTH = 100; 
	private final static GUILoginDialog logindaialog = new GUILoginDialog();
	private final static GUIMainWindow mainwindow = new GUIMainWindow();
	private final static CountDownLatch close = new CountDownLatch(1);
	private final static CountDownLatch init = new CountDownLatch(1);
	private static String name;
	private final static BlockingQueue<JSONNameandString> sendque = new ArrayBlockingQueue<JSONNameandString>(QUEUE_LENGTH);
	private final static BlockingQueue<JSONNameandString> receque = new ArrayBlockingQueue<JSONNameandString>(RECEQUE_LENGTH);
	
	private  ClientManage(){
		
	}
	
	static public GUILoginDialog getLogindaialog() {
		return logindaialog;
	}

	static public GUIMainWindow getMainwindow() {
		return mainwindow;
	}
	
	static public void setMainWindowVisible(boolean flag){
		mainwindow.setVisible(flag);
	}
	
	static public void addPathNode(String s){
		mainwindow.addPathNode(s);
	}

	public static boolean sendJSONNameandString( JSONNameandString json) {
		return sendque.offer(json);
	}

	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		ClientManage.name = name;
	}

	public static void displayMessage(SendMessage sendmessage) {
		mainwindow.displayMessage(sendmessage);
	}
	//Only be used by BaseClient
	static BlockingQueue<JSONNameandString> getSendque() {
		return sendque;
	}
	
	//Only be used by BaseClient
	public static BlockingQueue<JSONNameandString> getReceque() {
		return receque;
	}

	public static void rmPathNode(String name2) {
		mainwindow.rmPathNode(name2);
	}
	
	public static boolean waiteforclose(){
		try {
			close.await();
			System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static void hasSendCloseSign(){
		close.countDown();
	}
	
	public static boolean waitforinit(){
		try {
			init.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static void hasInit(){
		init.countDown();
	}
}
