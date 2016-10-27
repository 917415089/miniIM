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
import json.server.session.DataBaseResult;

@SuppressWarnings("unused")
public class StatementManager {
	

	static private int MAX_STATEMTN_NUMBER = 100;
	static private int THREAD_NUMBER_OF_DATABASE_ACCESS=4;
	static private int THREAD_NUMER_OF_SENDBACK_JSON = 2;
	static private int MAX_JSONque = 1000;
	static volatile private StatementManager UniqueInstance;
	static private ExecutorCompletionService<DataBaseResult> service;
	static private BlockingQueue<Future<DataBaseResult>> JSONque;
	
	private Statement statement;

	
	@SuppressWarnings("unchecked")
	private StatementManager(){
		try {
//			System.out.println("StatementManager");
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/test" ;    
		    String username = "root" ;   
		    String userpassword = "123456" ;   
		    Connection conn = DriverManager.getConnection(url , username , userpassword);
		    
		    statement = conn.createStatement();
		    
		    JSONque = new LinkedBlockingQueue<Future<DataBaseResult>>(MAX_JSONque);
		    ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUMBER_OF_DATABASE_ACCESS);
		    service = new ExecutorCompletionService<DataBaseResult>(threadPool,JSONque);
		    
		    ExecutorService sendBackPool = Executors.newFixedThreadPool(THREAD_NUMER_OF_SENDBACK_JSON);
		    for(int i = 0 ; i <THREAD_NUMER_OF_SENDBACK_JSON ; i++){
		    	sendBackPool.submit(new SendBackJSONThread(JSONque));
		    }
//		    service = new ExecutorCompletionService<JSONNameandString>(threadPool);
		    
		    
		} catch (ClassNotFoundException e) {
			System.err.println("can't find jdbc driver");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("");
			e.printStackTrace();
		}
	}

	static public Statement getStatement() {
		if(UniqueInstance ==null){
			synchronized (StatementManager.class) {
				if(UniqueInstance ==null){
					UniqueInstance = new StatementManager();				
				}
			}
		}
		return UniqueInstance.statement;
	}


	@SuppressWarnings("static-access")
	public static ExecutorCompletionService<DataBaseResult> getService() {
		if(UniqueInstance ==null){
			synchronized (StatementManager.class) {
				if(UniqueInstance ==null){
					UniqueInstance = new StatementManager();				
				}
			}
		}
//		System.out.println(UniqueInstance.hashCode());
		return UniqueInstance.service;
	}

	@SuppressWarnings("static-access")
	public static BlockingQueue<Future<DataBaseResult>> getJSONque() {
		if(UniqueInstance ==null){
			synchronized (StatementManager.class) {
				if(UniqueInstance ==null){
					UniqueInstance = new StatementManager();				
				}
			}
		}
		return UniqueInstance.JSONque;
	}
	
	

/*	public static CompletionService<JSONnameandString> getServerice(){
		if(UniqueInstance==null){
			UniqueInstance= new StatementManager();
		}
		return UniqueInstance.service;
	}*/
	
	

}
