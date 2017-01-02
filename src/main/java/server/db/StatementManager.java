package server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import server.session.SendBackJSONThread;
import json.server.session.SendBackJSON;

@SuppressWarnings("unused")
public class StatementManager {

	static final private int MAX_STATEMTN_NUMBER = 100;
	static final private int THREAD_NUMBER_OF_DATABASE_ACCESS=4;
	static final private int THREAD_NUMER_OF_SENDBACK_JSON = 2;
	static final private int MAX_JSONque = 1000;
	static final private int STATEMENT_NUMBER=10;
	
	static volatile private StatementManager UniqueInstance = new StatementManager();
	static private ExecutorCompletionService<SendBackJSON> service;
	static private BlockingQueue<Future<SendBackJSON>> JSONque;
	static private Connection conn;
	
	private StatementManager(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/test" ;    
		    String username = "root" ;   
		    String userpassword = "123456" ;   
		    conn = DriverManager.getConnection(url , username , userpassword);
		    
		    JSONque = new LinkedBlockingQueue<Future<SendBackJSON>>(MAX_JSONque);
		    
		    final ThreadFactory DBStatementName = new ThreadFactoryBuilder()
		    	    .setNameFormat("DBStatementThread-%d")
		    	    .build();
		    ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUMBER_OF_DATABASE_ACCESS,DBStatementName);
		    service = new ExecutorCompletionService<SendBackJSON>(threadPool,JSONque);
		    
		    final ThreadFactory SendBackName = new ThreadFactoryBuilder()
		    	    .setNameFormat("SendbackThreadDB-%d")
		    	    .build();
		    ExecutorService sendBackPool = Executors.newFixedThreadPool(THREAD_NUMER_OF_SENDBACK_JSON,SendBackName);
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

	
	static Statement createStatement(){
		Statement statement = null;
		try {
			return statement  = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return statement;
	}

	@SuppressWarnings("static-access")
	public static Future<SendBackJSON> sendDBCallable(DBCallable task){
		return UniqueInstance.service.submit(task);
	}


}
