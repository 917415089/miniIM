package server;

import java.util.concurrent.ExecutionException;

import json.util.JSONNameandString;
import server.db.StatementManager;

public class Test2  extends Thread{

	public static void main(String[] args) {
		Test2 test2 = new Test2();
		test2.start();
	}
	
	@Override
	public void run() {
		 try {
			JSONNameandString string = StatementManager.getService().take().get();
			System.out.println(string.getJSONName());
			System.out.println(string.getJSONStr());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
