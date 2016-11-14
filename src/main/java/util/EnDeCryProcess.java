package util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class EnDeCryProcess {

	private final static BASE64Encoder base64Encoder = new BASE64Encoder();
	private final static BASE64Decoder base64decoder = new BASE64Decoder();
	
	static synchronized public String SysKeyEncryWithBase64(String input,SecretKey secretKey){
		try {
			Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] out = cipher.doFinal(input.getBytes());
			return base64Encoder.encode(out);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException |BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static synchronized public String SysKeyDecryWithBase64(String input,SecretKey secretKey){
		try {
			byte[] in = base64decoder.decodeBuffer(input);
			Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] out = cipher.doFinal(in);
			return new String(out);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException |BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;		
	}
	
	static synchronized public String pubKeyEncryWithBase64(String input,PublicKey publicKey){
		try {
			Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] out = cipher.doFinal(input.getBytes());
			return base64Encoder.encode(out);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException |BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static synchronized public String priKeyDecryWithBase64(String input, PrivateKey privateKey){
		try{
			byte[] in = base64decoder.decodeBuffer(input);
			Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] out = cipher.doFinal(in);
			return new String(out);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException |BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
