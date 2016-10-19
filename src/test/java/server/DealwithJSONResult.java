package server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import json.util.JSONNameandString;
import server.db.StatementManager;
import client.session.MessageFactory;

public class DealwithJSONResult {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		/*BlockingQueue jsonQue = StatementManager.getJSONque();
		Future<JSONNameandString> peek = (Future<JSONNameandString>) jsonQue.peek();
		
		JSONNameandString jsonNameandString = peek.get();
		System.out.println(jsonNameandString.getJSONName());
		System.out.println(jsonNameandString.getJSONStr());*/
		
		
		JSONNameandString jsonNameandString = StatementManager.getService().take().get();
		System.out.println(jsonNameandString.getJSONName());
		System.out.println(jsonNameandString.getJSONStr());
		
		/*LinkedBlockingQueue<Future<String>> queue = new LinkedBlockingQueue<Future<String>>();
		ExecutorService threadPool = Executors.newFixedThreadPool(3);
		ExecutorCompletionService<String> service = new ExecutorCompletionService<String>(threadPool,queue);
		service.submit(new Callable<String>(){

			@Override
			public String call() throws Exception {
				String s = "testcase";
				return s;
			}
			
		});
		String s = queue.peek().get();
		System.out.println(s);*/
	}
}
