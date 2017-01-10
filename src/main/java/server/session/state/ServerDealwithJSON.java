package server.session.state;

import javax.crypto.SecretKey;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import json.util.JSONNameandString;
import server.session.DealWithJSON;
import util.EnDeCryProcess;

public class ServerDealwithJSON implements State {

	private final ServerStatemanagement management;
	private final DealWithJSON dealwithJSON;
	
	public ServerDealwithJSON(ServerStatemanagement management) {
		this.management = management;
		dealwithJSON = new DealWithJSON();
	}

	@Override
	public void handle(String s) {
		management.cleanReset();
		s = EnDeCryProcess.SysKeyDecryWithBase64(s, management.getSecretKey());
		dealwithJSON.dealwith(JSON.parseObject(s, JSONNameandString.class),management.getCh());
	}
	
	void setSecretKey(SecretKey key){
		dealwithJSON.setSecretKey(key);
	}
	
	void setChannel(Channel ch){
		dealwithJSON.setChannel(ch);
	}

	public String getUserName() {
		return dealwithJSON.getUserName();
	}
}
