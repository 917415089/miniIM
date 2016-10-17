package client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import util.EnDeCryProcess;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import com.alibaba.fastjson.JSON;
import json.client.access.ClientACKwithRandom;
import json.client.access.SendRandandSysKey;
import json.client.access.SupportedAlgorithm;
import json.server.access.SelectAlgorithmandPubkey;
import json.server.access.ServerACKwithRandom;


public class ClientAccessHandler {

	enum Status{ClientInit,SupportedAlgorithm,SyskeyandRandom,Access,ERROR}
	static final String[] SupportedPubKeyAlgorithm = new String[]{"RSA"};
	static final String[] SupportedSysKeyAlgorithm = new String[]{"AES"};

	private Status currStatus = null;
	private  String SelectedPubKey = null;
	private String SelectedSysKey = null;
	private  PublicKey publicKey = null;
	private  SecretKey secretKey;

	private String result = null;
	private int Random;
	
	
	public ClientAccessHandler(){
		currStatus = Status.ClientInit;
	}
	
	public boolean handle(String request){
		try{
			switch(currStatus){
			case ClientInit:
				result = SendSupportedKey();
				if(result != null){
					currStatus=Status.SupportedAlgorithm;
					return true;
				}else{
					currStatus = Status.ERROR;
					return false;
				}
			case SupportedAlgorithm:
				result = SendSysKeyandRandom(request);
				if(result != null){
					currStatus = Status.SyskeyandRandom;
					return true;
				}else{
					currStatus = Status.ERROR;
					return false;
				}
			case SyskeyandRandom:
				result = SendClientACK(request);
				if(result != null){
					currStatus = Status.Access;
					return true;
				}else{
					currStatus = Status.ERROR;
					return false;
				}
			case Access:
				break;
			case ERROR:
				//deal with Error;
				break;
			default:
				currStatus = Status.ERROR;
			}
		}catch(Exception e ){
			e.printStackTrace();
		}
		return false;
	}
	
	private String SendClientACK(String request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		String strjson = EnDeCryProcess.SysKeyDecryWithBase64(request, secretKey);
		ServerACKwithRandom acKwithRandom = JSON.parseObject(strjson,ServerACKwithRandom.class);
		
		if(acKwithRandom.getRandom()==++Random){
			ClientACKwithRandom clientACKwithRandom = new ClientACKwithRandom();
			clientACKwithRandom.setRandom(++Random);
			String ret = JSON.toJSONString(clientACKwithRandom);
			ret = EnDeCryProcess.SysKeyEncryWithBase64(ret, secretKey);
			return ret;
		}else{
			return null;
		}
	}

	private String SendSysKeyandRandom(String request) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		SelectAlgorithmandPubkey selectAlgorithmandPubkey = JSON.parseObject(request,SelectAlgorithmandPubkey.class);
		SelectedSysKey = selectAlgorithmandPubkey.getSelSysKey();
		SelectedPubKey = selectAlgorithmandPubkey.getSelPubKey();
		
		byte[] encode = selectAlgorithmandPubkey.getPubKeyEncode();
		KeyFactory keyFactory = KeyFactory.getInstance(SelectedPubKey);  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encode); 
        publicKey = keyFactory.generatePublic(keySpec);
        
        KeyGenerator keyGenerator = KeyGenerator.getInstance(SelectedSysKey);
        if(SelectedSysKey.equalsIgnoreCase("AES")){
        	keyGenerator.init(128);
        }
        secretKey = keyGenerator.generateKey();
        
        Random ran = new Random();
        Random = ran.nextInt();
        
        SendRandandSysKey sendRandandSysKey = new SendRandandSysKey();
        sendRandandSysKey.setRandom(++Random);
        sendRandandSysKey.setSyskeyend(secretKey.getEncoded());
        
        String ret = JSON.toJSONString(sendRandandSysKey);
        ret = EnDeCryProcess.pubKeyEncryWithBase64(ret, publicKey);
        
		return ret;
	}
	
	private String SendSupportedKey() {
		SupportedAlgorithm supportedAlgorithm = new SupportedAlgorithm();
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
		
		return JSON.toJSONString(supportedAlgorithm);
	}

	public String getResult() {
		String returnresponse = result;
		result = null;
		return returnresponse;
	}
	
	public boolean getAccess() {
		return currStatus==Status.Access;
	}

	public Status getCurrStatus() {
		return currStatus;
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public int getRandom() {
		return Random;
	}
}
