package jar;

import server.BaseServer;

public class BaseServerJar {

	public static void main(String[] args) {
		
		Thread thread = new Thread(new BaseServer(),"baseServer");
		thread.start();
		while(true)
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
