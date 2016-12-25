package client.state;

import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSON;
import exception.CannottransforSupportedAlgorithmJson;
import exception.InvalidParameterforSendSupportedKey;
import json.client.access.SupportedAlgorithm;
import server.session.state.State;

public class SendSupportedKey implements State {
	
	static final String[] SupportedPubKeyAlgorithm = new String[]{"RSA"};
	static final String[] SupportedSysKeyAlgorithm = new String[]{"AES"};
	private final ClientStatemanagement management;
	
	public SendSupportedKey(ClientStatemanagement initStatemanagement) {
		this.management = initStatemanagement;
	}

	@SuppressWarnings("unused")
	@Override
	public void handle(String s) throws InvalidParameterforSendSupportedKey, CannottransforSupportedAlgorithmJson {		
		
		if(s!=null) throw new InvalidParameterforSendSupportedKey();
		SupportedAlgorithm supportedAlgorithm = new SupportedAlgorithm();
		if(supportedAlgorithm!=null){
			List<String> pubkeyList = new ArrayList<String>();
			for(String key : SupportedPubKeyAlgorithm){
				pubkeyList.add(key);
			}
			List<String> syskeyList = new ArrayList<String>();
			for(String key : SupportedSysKeyAlgorithm){
				syskeyList.add(key);
			}
			supportedAlgorithm.setSupPubKey(pubkeyList);
			supportedAlgorithm.setSupSysKey(syskeyList);
			
			management.WriteWebSocketChannel(JSON.toJSONString(supportedAlgorithm));
			management.setState(management.getSendSysKeyandRandom());
		}else{
			throw new CannottransforSupportedAlgorithmJson();
		}

	}

}
