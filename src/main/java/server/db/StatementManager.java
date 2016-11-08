package server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import server.session.SendBackJSONThread;
import json.server.session.SendBackJSON;

@SuppressWarnings("unused")
public class StatementManager {
	

	static private int MAX_STATEMTN_NUMBER = 100;
	static private int THREAD_NUMBER_OF_DATABASE_ACCESS=4;
	static private int THREAD_NUMER_OF_SENDBACK_JSON = 2;
	static private int MAX_JSONque = 1000;
	static volatile private StatementManager UniqueInstance = new StatementManager();
	static private ExecutorCompletionService<SendBackJSON> service;
	static private BlockingQueue<Future<SendBackJSON>> JSONque;
	
	private Statement statement;

	
	private StatementManager(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/test" ;    
		    String username = "root" ;   
		    String userpassword = "123456" ;   
		    Connection conn = DriverManager.getConnection(url , username , userpassword);
		    
		    statement = conn.createStatement();
		    
		    JSONque = new LinkedBlockingQueue<Future<SendBackJSON>>(MAX_JSONque);
		    ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUMBER_OF_DATABASE_ACCESS);
		    service = new ExecutorCompletionService<SendBackJSON>(threadPool,JSONque);
		    
		    ExecutorService sendBackPool = Executors.newFixedThreadPool(THREAD_NUMER_OF_SENDBACK_JSON);
		    for(int i = 0 ; i <THREAD_NUMER_OF_SENDBACK_JSON ; i++){
		    	sendBackPool.submit(new SendBackJSONThread(JSONque));
		    }
		    
		} catch (ClassNotFoundException e) {
			System.err.println("can't find jdbc driver");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("");
			e.printStackTrace();
		}
	}

	static public Statement getStatement() {
		return UniqueInstance.statement;
	}

	@SuppressWarnings("static-access")
	public static ExecutorCompletionService<SendBackJSON> getService() {
		return UniqueInstance.service;
	}

	@SuppressWarnings("static-access")
	public static BlockingQueue<Future<SendBackJSON>> getJSONque() {
		return UniqueInstance.JSONque;
	}
}
