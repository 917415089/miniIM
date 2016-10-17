package client.message;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import io.netty.channel.Channel;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import util.EnDeCryProcess;

import com.alibaba.fastjson.JSON;

import json.client.login.ClientLogin;

@Deprecated
public class MessageFactory {

	private String userName;
	private String userPassword;
	private  SecretKey secretKey;
	private Channel channel; 
	private boolean hasLogin;
	
	public void init(String name, String password,SecretKey secre, Channel ch){
		userName = name;
		userPassword = password;
		secretKey = secre;
		channel = ch;
		hasLogin = false;
	}
	

	public boolean login() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		if(userName==null || userPassword == null) 
			return false;
		
		ClientLogin clientLogin = new ClientLogin();
		clientLogin.setName(userName);
		clientLogin.setPassword(userPassword);
		
		String jsonString = JSON.toJSONString(clientLogin);
		String ret = EnDeCryProcess.SysKeyEncryWithBase64(jsonString, secretKey);
		
		channel.writeAndFlush(ret);
		return true;
	}
	public String getUserName() {
		return userName;
	}

	public String getUserPassword() {
		return userPassword;
	}
	
	public SecretKey getSecretKey() {
		return secretKey;
	}

	public boolean isHasLogin() {
		return hasLogin;
	}
	

}
