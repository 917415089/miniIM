package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import client.BaseClient;

public class MultiClientandServer {
	@Test
	public void TestBaseLogin() throws InterruptedException{
		BaseClient baseclient1 = new BaseClient("user1","123");
		BaseClient baseclient2 = new BaseClient("user2","123");
		BaseClient baseclient3 = new BaseClient("user3","123");
		BaseClient baseclient4 = new BaseClient("user4","123");
		BaseClient baseclient5 = new BaseClient("user5","123");
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
		BaseClient baseclient1 = new BaseClient("1user1","123");
		BaseClient baseclient2 = new BaseClient("1user2","123");
		BaseClient baseclient3 = new BaseClient("1user3","123");
		BaseClient baseclient4 = new BaseClient("1user4","123");
		BaseClient baseclient5 = new BaseClient("1user5","123");
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
		BaseClient baseclient1 = new BaseClient("user1","123456");
		BaseClient baseclient2 = new BaseClient("user2","123456");
		BaseClient baseclient3 = new BaseClient("user3","123456");
		BaseClient baseclient4 = new BaseClient("user4","123456");
		BaseClient baseclient5 = new BaseClient("user5","123456");
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
