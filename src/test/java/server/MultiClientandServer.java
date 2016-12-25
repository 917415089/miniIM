package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import client.BaseClient;

public class MultiClientandServer {
	@Test
	public void TestBaseLogin() throws InterruptedException{
		BaseClient baseclient1 = new BaseClient();
		BaseClient baseclient2 = new BaseClient();
		BaseClient baseclient3 = new BaseClient();
		BaseClient baseclient4 = new BaseClient();
		BaseClient baseclient5 = new BaseClient();
		BaseServer baseserver = new BaseServer();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.submit(baseserver);
		threadPool.submit(baseclient1);
		threadPool.submit(baseclient2);
		threadPool.submit(baseclient3);
		threadPool.submit(baseclient4);
		threadPool.submit(baseclient5);
		Thread.sleep(3000);
	}
	
	@Test
	public void TestBaseLoginWithWrongName() throws InterruptedException{
		BaseClient baseclient1 = new BaseClient();
		BaseClient baseclient2 = new BaseClient();
		BaseClient baseclient3 = new BaseClient();
		BaseClient baseclient4 = new BaseClient();
		BaseClient baseclient5 = new BaseClient();
		BaseServer baseserver = new BaseServer();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.submit(baseserver);
		threadPool.submit(baseclient1);
		threadPool.submit(baseclient2);
		threadPool.submit(baseclient3);
		threadPool.submit(baseclient4);
		threadPool.submit(baseclient5);
		Thread.sleep(3000);
	}
	
	@Test
	public void TestBaseLoginWithWrongPassword() throws InterruptedException{
		BaseClient baseclient1 = new BaseClient();
		BaseClient baseclient2 = new BaseClient();
		BaseClient baseclient3 = new BaseClient();
		BaseClient baseclient4 = new BaseClient();
		BaseClient baseclient5 = new BaseClient();
		BaseServer baseserver = new BaseServer();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.submit(baseserver);
		threadPool.submit(baseclient1);
		threadPool.submit(baseclient2);
		threadPool.submit(baseclient3);
		threadPool.submit(baseclient4);
		threadPool.submit(baseclient5);
		Thread.sleep(3000);
	}
}
