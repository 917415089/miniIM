package server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import json.client.session.RequestFriendList;
import json.util.JSONMessage;
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
		List<JSONNameandString> list = new ArrayList<JSONNameandString>();
		list.add(jsonNameandString);
		JSONMessage msg = new JSONMessage();
		msg.setJson(list);

		BlockingQueue<JSONMessage> send = baseclient.getQue();
		BlockingQueue<JSONNameandString> receive = baseclient.getReceiveque();
		send.add(msg);
		JSONNameandString take = receive.take();
		String jsonString = JSON.toJSONString(take);
		System.out.println(jsonString);

		Thread.sleep(1000);
	}
}
