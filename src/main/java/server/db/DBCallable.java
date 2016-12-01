package server.db;

import java.sql.Statement;
import java.util.concurrent.Callable;

import json.server.session.SendBackJSON;
import json.util.JSONNameandString;

public class DBCallable implements Callable<SendBackJSON> {
	protected Statement protectsta;
	protected JSONNameandString json;
	
	public DBCallable() {
		// TODO Auto-generated constructor stub
	}
	public DBCallable(JSONNameandString json){
		this.json=json;
	}
	
	@Override
	public SendBackJSON call() throws Exception {
		protectsta = StatementManager.getStatement();
		SendBackJSON json = run();
		StatementManager.backStatement(protectsta);
		return json;
	}

	protected SendBackJSON run() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
