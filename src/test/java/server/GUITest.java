package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;
import client.gui.LoginDialog;


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
}
