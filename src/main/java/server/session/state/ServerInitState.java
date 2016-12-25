package server.session.state;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.List;
import com.alibaba.fastjson.JSON;
import exception.NullSelectPubKey;
import exception.NullSelectSysKey;
import json.client.access.SupportedAlgorithm;
import json.server.access.SelectAlgorithmandPubkey;

public class ServerInitState implements State {

	private final ServerStatemanagement manage;
	
	public ServerInitState(ServerStatemanagement manage) {
		super();
		this.manage = manage;
	}

	@SuppressWarnings("unused")
	@Override
	public void handle(String request) throws Exception {
		
		
		SupportedAlgorithm supportedAlgorithm = JSON.parseObject(request, SupportedAlgorithm.class);

		String SelectedPubKey = "RSA";
		List<String> supPubKey = supportedAlgorithm.getSupPubKey();
		for(String pubkey : supPubKey){
			if(pubkey.equalsIgnoreCase("RSA")){
				SelectedPubKey = "RSA";
				break;
			}
		}
		if(SelectedPubKey == null) throw new NullSelectPubKey();
		
		String SelectedSysKey = "AES";
		List<String> supSysKey = supportedAlgorithm.getSupSysKey();
		for(String syskey : supSysKey){
			if(syskey.equalsIgnoreCase("AES")){
				SelectedSysKey = "AES";
				break;
			}
		}
		if(SelectedSysKey == null) throw new NullSelectSysKey();
		
		SelectAlgorithmandPubkey selectAlgorithmandPubkey = new SelectAlgorithmandPubkey();
		selectAlgorithmandPubkey.setSelPubKey(SelectedPubKey);
		selectAlgorithmandPubkey.setSelSysKey(SelectedSysKey);
		
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(SelectedPubKey);
		if(SelectedPubKey.equalsIgnoreCase("RSA")){
			keyPairGenerator.initialize(1024);
		}else{
			return;
		}
		KeyPair generateKeyPair = keyPairGenerator.generateKeyPair();
		
		manage.setSelectedSysKey(SelectedSysKey);
		PublicKey publicKey = generateKeyPair.getPublic();
		manage.setPrivateKey(generateKeyPair.getPrivate());
		selectAlgorithmandPubkey.setPubKeyEncode(publicKey.getEncoded());
		
		manage.setState(manage.getSelectAlgorithmandPubKey());
		manage.WriteWebSocketChannel(JSON.toJSONString(selectAlgorithmandPubkey));
	}

}
