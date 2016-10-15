package server;

import io.netty.util.CharsetUtil;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson.JSON;

import json.server.SelectAlgorithmandPubkey;

public class Test {
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		KeyGenerator keyPairGen = KeyGenerator.getInstance("AES");
		keyPairGen.init(128);
		SecretKey  secretKey = keyPairGen.generateKey();  
		
		byte[] b = secretKey.getEncoded();
		 SecretKeySpec secretKeySpec = new SecretKeySpec(b,"AES"); 
		
		 String s = "123";
		 Cipher cipher = Cipher.getInstance("AES");
		 cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		 byte[] m = cipher.doFinal(s.getBytes());
		 
		 cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		 byte[] data = cipher.doFinal(m);
		 String rm = new String(data);
		 System.out.println(rm);
		
/*		KeyPair generateKeyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) generateKeyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) generateKeyPair.getPrivate();
		
		byte[] encoded = publicKey.getEncoded();
		
		imHttpResponse rescontent = new imHttpResponse();
		rescontent.setProcess("sendPubKey");
		rescontent.setSelPubKey("RSA");
		rescontent.setSelSysKey("AES");
		rescontent.setPubkey(publicKey);
		
		byte[] StrJson = JSON.toJSONString(rescontent).getBytes();
		
		imHttpResponse serContent = JSON.parseObject(new String(StrJson),imHttpResponse.class);
		System.out.println();
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
		RSAPublicKey pubkey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		System.out.println();*/
		
	} 
}
