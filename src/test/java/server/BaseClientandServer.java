package server;

import io.netty.handler.codec.http2.Http2Stream.State;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import json.client.access.ClosingChannel;
import json.client.login.ClientRegister;
import json.util.JSONNameandString;

import org.junit.Test;

import server.db.StatementManager;

import com.alibaba.fastjson.JSON;

import client.BaseClient;

public class BaseClientandServer {

	@Test
	public void TestBaseLogin() throws InterruptedException{
		BaseClient baseclient = new BaseClient("user1","123");
		BaseServer baseserver = new BaseServer();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.submit(baseserver);
		threadPool.submit(baseclient);
		Thread.sleep(3000);
	}
	
	@Test
	public void TestBaseLoginWithWrongName() throws InterruptedException{
		BaseClient baseclient = new BaseClient("1user1","123");
		BaseServer baseserver = new BaseServer();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.submit(baseserver);
		threadPool.submit(baseclient);
		Thread.sleep(3000);
	}
	
	@Test
	public void TestBaseLoginWithWrongPassword() throws InterruptedException{
		BaseClient baseclient = new BaseClient("1user1","123");
		BaseServer baseserver = new BaseServer();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.submit(baseserver);
		threadPool.submit(baseclient);
		Thread.sleep(3000);
	}
	
	
	@Test
	public void TestBaseRegister() throws InterruptedException{
		BaseServer baseserver = new BaseServer();
		ClientRegister clientRegister = new ClientRegister();
		clientRegister.setUserName("user10");
		clientRegister.setUserPassword("123");
		clientRegister.setEmail("user10email@123.com");
		
		JSONNameandString json = new JSONNameandString();
		json.setJSONName(ClientRegister.class.getName());
		json.setJSONStr(JSON.toJSONString(clientRegister));
		
		BaseClient baseclient = new BaseClient();
		BlockingQueue<JSONNameandString> send = baseclient.getSendque();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.submit(baseserver);
		threadPool.submit(baseclient);
		Thread.sleep(3000);
		send.offer(json);
		Thread.sleep(1000);
		
		String query = "select * from user where username =\'user10\';";
		try {
			ResultSet resultSet = StatementManager.getStatement().executeQuery(query);
			while(resultSet.next()){
				System.out.println("find "+resultSet.getString("username")+" in database");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			String Sql = "delete from user where username=\'user10\';";
			System.out.println(Sql);
			StatementManager.getStatement().executeUpdate(Sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Thread.sleep(1000);

	}
	
	@Test
	public void TestCloseChannel() throws InterruptedException{
		BaseClient baseclient = new BaseClient("user1","123");
		BaseServer baseserver = new BaseServer();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.submit(baseserver);
		threadPool.submit(baseclient);
		Thread.sleep(3000);
		
		ClosingChannel closingChannel = new ClosingChannel();
		JSONNameandString json = new JSONNameandString();
		json.setJSONName(ClosingChannel.class.getName());
		json.setJSONStr(JSON.toJSONString(closingChannel));
		
		BlockingQueue<JSONNameandString> que = baseclient.getSendque();
		que.add(json);
		Thread.sleep(1000);
	}
}
