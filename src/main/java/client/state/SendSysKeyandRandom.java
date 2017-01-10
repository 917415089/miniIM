package client.state;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import com.alibaba.fastjson.JSON;

import exception.UnsupportedSysEncryAlthgorithm;
import json.client.access.SendRandandSysKey;
import json.server.access.SelectAlgorithmandPubkey;
import server.session.state.State;
import util.EnDeCryProcess;

public class SendSysKeyandRandom implements State {

	private ClientStatemanagement management;
	
	public SendSysKeyandRandom(ClientStatemanagement management) {
		super();
		this.management = management;
	}

	@Override
	public void handle(String request) throws Exception {
		
		SelectAlgorithmandPubkey selectAlgorithmandPubkey = JSON.parseObject(request,SelectAlgorithmandPubkey.class);
		String SelectedSysKey = selectAlgorithmandPubkey.getSelSysKey();
		String SelectedPubKey = selectAlgorithmandPubkey.getSelPubKey();
		
		byte[] encode = selectAlgorithmandPubkey.getPubKeyEncode();
		KeyFactory keyFactory = KeyFactory.getInstance(SelectedPubKey);  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encode); 
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        
        KeyGenerator keyGenerator = KeyGenerator.getInstance(SelectedSysKey);
        if(SelectedSysKey.equalsIgnoreCase("AES")){
        	keyGenerator.init(128);
        }else{
        	throw new UnsupportedSysEncryAlthgorithm();
        }
        SecretKey secretKey = keyGenerator.generateKey();
        Random ran = new Random();
        int random = ran.nextInt();
        management.setRandom(new AtomicInteger(random));
        SendRandandSysKey sendRandandSysKey = new SendRandandSysKey();
        sendRandandSysKey.setRandom(management.getRandom().incrementAndGet());
        sendRandandSysKey.setSyskeyend(secretKey.getEncoded());
        
        String ret = JSON.toJSONString(sendRandandSysKey);
        ret = EnDeCryProcess.pubKeyEncryWithBase64(ret, publicKey);

        management.setSecretKey(secretKey);
        management.downAccessSign();
        management.WriteWebSocketChannel(ret);
        
        management.setState(management.getAccess());
	}

}
