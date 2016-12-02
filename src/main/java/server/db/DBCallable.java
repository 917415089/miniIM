package server.db;

import java.sql.Statement;
import java.util.concurrent.Callable;
import json.server.session.SendBackJSON;

public abstract class DBCallable implements Callable<SendBackJSON> {
	protected Statement protectsta;
	
	public DBCallable() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public SendBackJSON call(){
		protectsta = StatementManager.getStatement();
		SendBackJSON json = run();
		StatementManager.backStatement(protectsta);
		return json;
	}

	protected  abstract SendBackJSON run();
	
}
