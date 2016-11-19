package server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import client.ClientManage;

public class BaseClientandServer {

	@Test
	public void TestBaseLogin() throws InterruptedException{
		BaseClient baseclient1 = new BaseClient("user1","123");
		BaseServer baseserver = new BaseServer();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.submit(baseserver);
		threadPool.submit(baseclient1);
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
		BaseClient baseclient = new BaseClient("user1","123456");
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
		BlockingQueue<JSONNameandString> send = ClientManage.getSendque();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.submit(baseserver);
		threadPool.submit(baseclient);
		Thread.sleep(3000);
		send.offer(json);
		Thread.sleep(1000);
		
		String query = "select * from user where username =\'user10\';";
		Statement sta = StatementManager.getStatement();
		try {
			ResultSet resultSet = sta.executeQuery(query);
			if(resultSet.next()){
				System.out.println("find "+resultSet.getString("username")+" in database");
			}
			String Sql = "delete from user where username=\'user10\';";
			System.out.println(Sql);
			sta.executeUpdate(Sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(sta!=null)
				StatementManager.backStatement(sta);
		}
		Thread.sleep(1000);
	}
	
	@Test
	public void TestBaseRegisterWithExistName() throws InterruptedException{
		BaseServer baseserver = new BaseServer();
		ClientRegister clientRegister = new ClientRegister();
		clientRegister.setUserName("user1");
		clientRegister.setUserPassword("123");
		clientRegister.setEmail("user10email@123.com");
		
		JSONNameandString json = new JSONNameandString();
		json.setJSONName(ClientRegister.class.getName());
		json.setJSONStr(JSON.toJSONString(clientRegister));
		
		BaseClient baseclient = new BaseClient();
		BlockingQueue<JSONNameandString> send = ClientManage.getSendque();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.submit(baseserver);
		threadPool.submit(baseclient);
		Thread.sleep(3000);
		send.offer(json);
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
		
		BlockingQueue<JSONNameandString> que = ClientManage.getSendque();
		que.add(json);
		Thread.sleep(1000);
	}
}
