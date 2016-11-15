package server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import json.client.session.AddFriend;
import json.client.session.RequestFriendList;
import json.util.JSONNameandString;

import org.junit.Test;

import com.alibaba.fastjson.JSON;

import client.BaseClient;

public class BaseClientFriendSession {

	@Test
	public void TestshowAllFriendList() throws InterruptedException{
		
		BaseClient baseclient = new BaseClient("user1","123");
		BaseServer baseserver = new BaseServer();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.submit(baseserver);
		threadPool.submit(baseclient);
		Thread.sleep(3000);
		
		RequestFriendList requestFriendList = new RequestFriendList();
		requestFriendList.setGroup("all");
		JSONNameandString jsonNameandString = new JSONNameandString();
		jsonNameandString.setJSONName(requestFriendList.getClass().getName());
		jsonNameandString.setJSONStr(JSON.toJSONString(requestFriendList));

		BlockingQueue<JSONNameandString> send = baseclient.getSendque();
		Thread.sleep(1000);//if I remove this sentence, send JSONNameandString may be send to early so that session's username haven't be set; 
		send.add(jsonNameandString);
		Thread.sleep(2000);
	}
	
	@Test
	public void TestAddFriend() throws InterruptedException{
		BaseClient baseclient1 = new BaseClient("user1","123");
		BaseClient baseclient4 = new BaseClient("user4","123");
		BaseServer baseserver = new BaseServer();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		threadPool.submit(baseserver);
		threadPool.submit(baseclient1);
		threadPool.submit(baseclient4);
		Thread.sleep(3000);
		AddFriend addFriend = new AddFriend();
		addFriend.setFriendname("user4");
		addFriend.setGroup("friends");
		
		JSONNameandString json = new JSONNameandString();
		json.setJSONName(AddFriend.class.getName());
		json.setJSONStr(JSON.toJSONString(addFriend));
		
		BlockingQueue<JSONNameandString> send1 = baseclient1.getSendque();
		BlockingQueue<JSONNameandString> receive1 = baseclient1.getReceque();
		Thread.sleep(1000);//if I remove this sentence, send JSONNameandString may be send to early so that session's username haven't be set; 
		send1.add(json);
		while(true);
	}
}
