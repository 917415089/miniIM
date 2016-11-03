package server.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import util.EnDeCryProcess;
import com.alibaba.fastjson.JSON;
import json.client.access.ClientACKwithRandom;
import json.client.access.SendRandandSysKey;
import json.client.access.SupportedAlgorithm;
import json.server.access.SelectAlgorithmandPubkey;
import json.server.access.ServerACKwithRandom;


public class ServerAccessHandler {

	private enum Status {ServerInit,SelectAlgorithmandPubKey, ServerACK,Access,ERROR}
	static final String[] SupportedPubKeyAlgorithm = new String[]{"RSA"};
	static final String[] SupportedSysKeyAlgorithm = new String[]{"AES"};
	
	private Status currStatus = null;
	private String SelectedPubKey = null;
	private String SelectedSysKey = null;
	private PublicKey publicKey = null;
	private PrivateKey privateKey = null;
	private String result = null;
	private int Random;
	private SecretKeySpec secretKeySpec;
	
	public ServerAccessHandler() {
		currStatus = Status.ServerInit;
	}
	
	public boolean handle(String request) {
		try{
			switch(currStatus){
			case ServerInit :
				result =SelectAlgorithm(request);
				if(result != null){
					currStatus = Status.SelectAlgorithmandPubKey;
//					System.out.println("ServerInit");
					return true;
				}else{
					currStatus = Status.ERROR;
					return false;
				}
			case SelectAlgorithmandPubKey:
				result = SendServerACK(request);
				if(result != null){
					currStatus = Status.ServerACK;
//					System.out.println("SelectAlgorithmandPubKey");
					return true;	
				}else{
					currStatus = Status.ERROR;
					return false;
				}
			case ServerACK:
				if(verify(request)){
					currStatus = Status.Access;
//					System.out.println("ServerACK");
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
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}


	private boolean verify(String request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		String json = EnDeCryProcess.SysKeyDecryWithBase64(request, secretKeySpec);
		ClientACKwithRandom clientAckwithRandom = JSON.parseObject(json,ClientACKwithRandom.class);
		if(clientAckwithRandom.getRandom()==++Random){
			Random++;
			return true;
		}
		return false;
	}
	
	private String SendServerACK(String request) throws UnsupportedEncodingException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String jsonstr = EnDeCryProcess.priKeyDecryWithBase64(request, privateKey);
		SendRandandSysKey sendRandandSysKey = JSON.parseObject(jsonstr,SendRandandSysKey.class);
		Random = sendRandandSysKey.getRandom();
		secretKeySpec = new SecretKeySpec(sendRandandSysKey.getSyskeyend(),SelectedSysKey);
		
		ServerACKwithRandom acKwithRandom = new ServerACKwithRandom();
		acKwithRandom.setRandom(++Random);
		String ret = JSON.toJSONString(acKwithRandom);
		ret = EnDeCryProcess.SysKeyEncryWithBase64(ret, secretKeySpec);
		return ret;
	}
	
	private String SelectAlgorithm(String request) throws NoSuchAlgorithmException {
		SupportedAlgorithm supportedAlgorithm = JSON.parseObject(request, SupportedAlgorithm.class);
		
		//selected pubkey;
		List<String> supPubKey = supportedAlgorithm.getSupPubKey();
		for(String pubkey : supPubKey){
			if(pubkey.equalsIgnoreCase("RSA")){
				SelectedPubKey = "RSA";
				break;
			}
		}
		if(SelectedPubKey == null) return null;
		
		//selected syskey
		List<String> supSysKey = supportedAlgorithm.getSupSysKey();
		for(String syskey : supSysKey){
			if(syskey.equalsIgnoreCase("AES")){
				SelectedSysKey = "AES";
				break;
			}
		}
		if(SelectedSysKey == null) return null;
		
		return SendPubkey();
	}

	private String SendPubkey() throws NoSuchAlgorithmException {
		SelectAlgorithmandPubkey selectAlgorithmandPubkey = new SelectAlgorithmandPubkey();
		selectAlgorithmandPubkey.setSelPubKey(SelectedPubKey);
		selectAlgorithmandPubkey.setSelSysKey(SelectedSysKey);
		
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(SelectedPubKey);
		if(SelectedPubKey.equalsIgnoreCase("RSA")){
			keyPairGenerator.initialize(1024);
		}else{
			return null;
		}
		KeyPair generateKeyPair = keyPairGenerator.generateKeyPair();
		publicKey = generateKeyPair.getPublic();
		privateKey = generateKeyPair.getPrivate();
		selectAlgorithmandPubkey.setPubKeyEncode(publicKey.getEncoded());

		return JSON.toJSONString(selectAlgorithmandPubkey);
	}

	public boolean getAccess() {
		return currStatus==Status.Access;
	}

	public String getResult() {
		String returnresponse = result;
		result = null;
		return returnresponse;
	}

	public SecretKeySpec getSecretKeySpec() {
		return secretKeySpec;
	}

	public int getRandom() {
		return Random;
	}
}
