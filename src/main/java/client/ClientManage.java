package client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import json.util.JSONNameandString;
import client.gui.LoginDialog;
import client.gui.MainWindow;

public class ClientManage {
	static public final int QUEUE_LENGTH = 100;
	static public final int RECEQUE_LENGTH = 100; 
	private static LoginDialog logindaialog = new LoginDialog();
	private static MainWindow mainwindow = new MainWindow();
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
	
}
