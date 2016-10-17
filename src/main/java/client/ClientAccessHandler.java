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
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import com.alibaba.fastjson.JSON;
import json.client.ClientACKwithRandom;
import json.client.SendRandandSysKey;
import json.client.SupportedAlgorithm;
import json.server.ServerACKwithRandom;
import json.server.SelectAlgorithmandPubkey;

@SuppressWarnings("restriction")
public class ClientAccessHandler {

	enum Status{ClientInit,SupportedAlgorithm,SyskeyandRandom,Access,ERROR}
	static final String[] SupportedPubKeyAlgorithm = new String[]{"RSA"};
	static final String[] SupportedSysKeyAlgorithm = new String[]{"AES"};

	private Status currStatus = null;
	private  String SelectedPubKey = null;
	private String SelectedSysKey = null;
	private  PublicKey publicKey = null;
	private  SecretKey secretKey;
	private final BASE64Encoder base64Encoder = new BASE64Encoder();
	private final BASE64Decoder base64decoder = new BASE64Decoder();
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
				currStatus=Status.SupportedAlgorithm;
				return true;
			case SupportedAlgorithm:
				result = SendSysKeyandRandom(request);
				currStatus = Status.SyskeyandRandom;
				return true;
			case SyskeyandRandom:
				result = SendClientACK(request);
				currStatus = Status.Access;
				return true;
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
		String strjson = CommenDecry(request);
		ServerACKwithRandom acKwithRandom = JSON.parseObject(strjson,ServerACKwithRandom.class);
		
		if(acKwithRandom.getRandom()==++Random){
			ClientACKwithRandom clientACKwithRandom = new ClientACKwithRandom();
			clientACKwithRandom.setRandom(++Random);
			String ret = JSON.toJSONString(clientACKwithRandom);
			ret = CommenEncry(ret);
			return ret;
		}else{
			return null;
		}
	}

	private String CommenEncry(String ret) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(SelectedSysKey);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] out = cipher.doFinal(ret.getBytes());
		return base64Encoder.encode(out);
	}

	private String CommenDecry(String request) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		byte[] in = base64decoder.decodeBuffer(request);
		Cipher cipher = Cipher.getInstance(SelectedSysKey);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] out = cipher.doFinal(in);
		return new String(out);
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
        ret = pubkeyEncry(ret);
        
		return ret;
	}
	
	private String pubkeyEncry(String ret) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		Cipher cipher = Cipher.getInstance(SelectedPubKey);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] out = cipher.doFinal(ret.getBytes());
		return base64Encoder.encode(out);
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
	
}
