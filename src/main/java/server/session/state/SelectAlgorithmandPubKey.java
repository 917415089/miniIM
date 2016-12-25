package server.session.state;

import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.spec.SecretKeySpec;
import com.alibaba.fastjson.JSON;
import json.client.access.SendRandandSysKey;
import json.server.access.ServerACKwithRandom;
import util.EnDeCryProcess;

public class SelectAlgorithmandPubKey implements State {

	private final ServerStatemanagement management;
	
	public SelectAlgorithmandPubKey(ServerStatemanagement management) {
		super();
		this.management = management;
	}

	@Override
	public void  handle(String request) throws Exception {
		
		String jsonstr = EnDeCryProcess.priKeyDecryWithBase64(request, management.getPrivateKey());
		SendRandandSysKey sendRandandSysKey = JSON.parseObject(jsonstr,SendRandandSysKey.class);
		management.setRandom(new AtomicInteger(sendRandandSysKey.getRandom()));
		management.setSecretKey(new SecretKeySpec(sendRandandSysKey.getSyskeyend(),management.getSelectedSysKey()));
		
		management.getServerDealwithJSON().setSecretKey(management.getSecretKey());

		ServerACKwithRandom acKwithRandom = new ServerACKwithRandom();
		management.getRandom().incrementAndGet();
		acKwithRandom.setRandom(management.getRandom().get());
		String ret = JSON.toJSONString(acKwithRandom);
		ret = EnDeCryProcess.SysKeyEncryWithBase64(ret, management.getSecretKey());

		management.setState(management.getServerAck());
		management.WriteWebSocketChannel(ret);
	}

}
