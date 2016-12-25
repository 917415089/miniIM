package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import json.client.session.AddFriend;
import json.client.session.RequestFriendList;
import json.util.JSONNameandString;
import org.junit.Test;
import com.alibaba.fastjson.JSON;
import client.BaseClient;
import client.ClientManage;

public class BaseClientFriendSession {

	@Test
	public void TestshowAllFriendList() throws InterruptedException{
		
		BaseClient baseclient = new BaseClient();
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

		Thread.sleep(1000);//if I remove this sentence, send JSONNameandString may be send to early so that session's username haven't be set; 
		ClientManage.sendJSONNameandString(jsonNameandString);
		Thread.sleep(2000);
	}
	
	@Test
	public void TestAddFriend() throws InterruptedException{
		BaseClient baseclient1 = new BaseClient();
		BaseClient baseclient4 = new BaseClient();
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
		

//		BlockingQueue<JSONNameandString> receive1 = ClientManage.getReceque();
		Thread.sleep(1000);//if I remove this sentence, send JSONNameandString may be send to early so that session's username haven't be set; 
		ClientManage.sendJSONNameandString(json);
		while(true);
	}
}
