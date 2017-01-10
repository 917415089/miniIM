package server.session.state;

import com.alibaba.fastjson.JSON;
import exception.WrongRandom;
import json.client.access.ClientACKwithRandom;
import util.EnDeCryProcess;

public class ServerACK implements State {

	private final ServerStatemanagement management;
	
	public ServerACK(ServerStatemanagement management) {
		this.management = management;
	}

	@Override
	public void handle(String jsonStr) throws WrongRandom {
		
		String rejson = EnDeCryProcess.SysKeyDecryWithBase64(jsonStr, management.getSecretKey());
		ClientACKwithRandom clientAckwithRandom = JSON.parseObject(rejson,ClientACKwithRandom.class);
		
		if(clientAckwithRandom.getRandom()==management.getRandom().incrementAndGet()){
			management.getRandom().incrementAndGet();
			management.setState(management.getServerDealwithJSON());
			return ;
		}else
			throw new WrongRandom();
	}
}
