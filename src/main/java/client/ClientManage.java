package client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import json.client.session.SendMessage;
import json.util.JSONNameandString;
import client.gui.LoginDialog;
import client.gui.MainWindow;

public class ClientManage {
	static public final int QUEUE_LENGTH = 100;
	static public final int RECEQUE_LENGTH = 100; 
	private static LoginDialog logindaialog = new LoginDialog();
	private static MainWindow mainwindow = new MainWindow();
	private static String name;
	private static BlockingQueue<JSONNameandString> sendque = new ArrayBlockingQueue<JSONNameandString>(QUEUE_LENGTH);
	private static BlockingQueue<JSONNameandString> receque = new ArrayBlockingQueue<JSONNameandString>(RECEQUE_LENGTH);
	
	private  ClientManage(){
		
	}
	
	static public LoginDialog getLogindaialog() {
		return logindaialog;
	}

	static public MainWindow getMainwindow() {
		return mainwindow;
	}
	
	static public void setMainWindowVisible(boolean flag){
		mainwindow.setVisible(flag);
	}
	
	static public void addPathNode(String s){
		mainwindow.addPathNode(s);
	}

	public static int getQueueLength() {
		return QUEUE_LENGTH;
	}

	public static int getRecequeLength() {
		return RECEQUE_LENGTH;
	}

	public static BlockingQueue<JSONNameandString> getSendque() {
		return sendque;
	}

	public static BlockingQueue<JSONNameandString> getReceque() {
		return receque;
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
}
