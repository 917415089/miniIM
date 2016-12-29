package server.db;

import java.sql.Statement;
import java.util.concurrent.Callable;
import json.server.session.SendBackJSON;

public abstract class DBCallable implements Callable<SendBackJSON> {
	
	static protected ThreadLocal<Statement> sta = new ThreadLocal<Statement>(){
		@Override
		protected Statement initialValue() {
			Thread thread = Thread.currentThread();
			System.out.println("Statement is created : "+thread.getName());
			return StatementManager.createStatement();
		}
	};
	
	@Override
	public SendBackJSON call(){
		SendBackJSON json = run();
		return json;
	}

	protected  abstract SendBackJSON run();
	
}
