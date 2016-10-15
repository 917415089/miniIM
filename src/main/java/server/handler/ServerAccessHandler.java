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
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import com.alibaba.fastjson.JSON;
import json.client.ClientACKwithRandom;
import json.client.SendRandandSysKey;
import json.client.SupportedAlgorithm;
import json.server.ServerACKwithRandom;
import json.server.SelectAlgorithmandPubkey;

@SuppressWarnings("restriction")
public class ServerAccessHandler {

	private enum Status {ServerInit,SelectAlgorithmandPubKey, ServerACK,Access,ERROR}
	static final String[] SupportedPubKeyAlgorithm = new String[]{"RSA"};
	static final String[] SupportedSysKeyAlgorithm = new String[]{"AES"};
	
	private Status currStatus = null;
	private String SelectedPubKey = null;
	private String SelectedSysKey = null;
	private PublicKey publicKey = null;
	private PrivateKey privateKey = null;
	private final BASE64Encoder base64Encoder = new BASE64Encoder();
	private final BASE64Decoder base64decoder = new BASE64Decoder();
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
				currStatus = Status.SelectAlgorithmandPubKey;
				return true;
			case SelectAlgorithmandPubKey:
				result = SendServerACK(request);
				currStatus = Status.ServerACK;
				return true;
			case ServerACK:
				if(verify(request)){
					currStatus = Status.Access;
					return true;
				}else{
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
		String json = CommenDecry(request);
		ClientACKwithRandom clientAckwithRandom = JSON.parseObject(json,ClientACKwithRandom.class);
		if(clientAckwithRandom.getRandom()==++Random){
			Random++;
			System.out.println("pass");
			return true;
		}
		return false;
	}

	private String CommenDecry(String request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException {
		byte[] in = base64decoder.decodeBuffer(request);
		Cipher cipher = Cipher.getInstance(SelectedSysKey);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		byte[] out = cipher.doFinal(in);
		return new String(out);
	}

	private String SendServerACK(String request) throws UnsupportedEncodingException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String jsonstr = pubkeyDecry(request);
		SendRandandSysKey sendRandandSysKey = JSON.parseObject(jsonstr,SendRandandSysKey.class);
		Random = sendRandandSysKey.getRandom();
		secretKeySpec = new SecretKeySpec(sendRandandSysKey.getSyskeyend(),SelectedSysKey);
		
		ServerACKwithRandom acKwithRandom = new ServerACKwithRandom();
		acKwithRandom.setRandom(++Random);
		String ret = JSON.toJSONString(acKwithRandom);
		ret = commenEncry(ret);
		return ret;
	}

	private String commenEncry(String ret) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(SelectedSysKey);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		byte[] out = cipher.doFinal(ret.getBytes());
		return base64Encoder.encode(out);
	}

	private String pubkeyDecry(String request) throws UnsupportedEncodingException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] in = base64decoder.decodeBuffer(request);
		Cipher cipher = Cipher.getInstance(SelectedPubKey);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] out = cipher.doFinal(in);
		return new String(out);
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
}
