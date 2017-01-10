package client.state;

import com.alibaba.fastjson.JSON;
import client.ClientManage;
import exception.NullJSONException;
import exception.WrongRandom;
import json.client.access.ClientACKwithRandom;
import json.client.session.OfflineRequest;
import json.server.access.ServerACKwithRandom;
import json.util.JSONNameandString;
import server.session.state.State;
import util.EnDeCryProcess;

public class Access implements State {

	private ClientStatemanagement management;

	public Access(ClientStatemanagement management) {
		this.management = management;
	}

	@Override
	public void handle(String request) throws Exception {
		String strjson = EnDeCryProcess.SysKeyDecryWithBase64(request, management.getSecretKey());
		ServerACKwithRandom acKwithRandom = JSON.parseObject(strjson,ServerACKwithRandom.class);
		
		int incrementAndGet = management.getRandom().incrementAndGet();
		if(acKwithRandom.getRandom()==incrementAndGet){
			ClientACKwithRandom clientACKwithRandom = new ClientACKwithRandom();
			clientACKwithRandom.setRandom(management.getRandom().incrementAndGet());
			String ret = JSON.toJSONString(clientACKwithRandom);
			ret = EnDeCryProcess.SysKeyEncryWithBase64(ret, management.getSecretKey());
			management.setState(management.getClientDealwithJSON());
			management.WriteWebSocketChannel(ret);
			management.getClientDealwithJSON().init();
			
			JSONNameandString json = new JSONNameandString();
			json.setJSONName(OfflineRequest.class.getName());
			
			check(json);
			ClientManage.sendJSONNameandString(json);
			
		}else{
			throw new WrongRandom();
		}
	}

	private void check(JSONNameandString json) throws NullJSONException {
		if(json.getJSONName().length()==0)
			throw new NullJSONException();
	}

}
