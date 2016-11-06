package server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import json.client.access.ClosingChannel;
import json.util.JSONMessage;
import json.util.JSONNameandString;

import org.junit.Test;

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
		
		List<JSONNameandString> msg = new ArrayList<JSONNameandString>();
		msg.add(json);
		
		JSONMessage sendmsg = new JSONMessage();
		sendmsg.setJson(msg);
		BlockingQueue<JSONMessage> que = baseclient.getSendque();
		que.add(sendmsg);
		Thread.sleep(1000);
	}
}
