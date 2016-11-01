package client.session;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import json.client.login.ClientLogin;
import json.client.login.ClientRegister;
import json.server.login.SuccessLogin;
import json.util.JSONNameandString;
import util.EnDeCryProcess;

import com.alibaba.fastjson.JSON;

public class ClientSession {
	
	private String token;
	private String userName;
	private String userPassword;
	private String userEmail;
	private  SecretKey secretKey;
	private boolean register;
	private boolean hasLogin;

	public void receiveACK(String json, SecretKey secretKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
			
			this.secretKey = secretKey;
			String str = EnDeCryProcess.SysKeyDecryWithBase64(json, secretKey);
			SuccessLogin successLogin = JSON.parseObject(str, SuccessLogin.class);
			token = successLogin.getToken();
			setHasLogin(true);
			System.out.println("login");
	}
	public String login(){
		if(userName==null || userPassword == null) 
			System.err.println("please input Name or password! ");

		ClientLogin clientLogin = new ClientLogin();
		clientLogin.setName(userName);
		clientLogin.setPassword(userPassword);

		String jsonString = JSON.toJSONString(clientLogin);
		JSONNameandString json = new JSONNameandString();
		json.setJSONName(ClientLogin.class.getName());
		json.setJSONStr(jsonString);
		String ret = JSON.toJSONString(json);
		ret = EnDeCryProcess.SysKeyEncryWithBase64(ret, secretKey);
		return ret;
	}
	
	public String register(){
		if(userName == null || userPassword == null)
			System.err.println("please input Name or password! ");
		
		ClientRegister register = new ClientRegister();
		register.setUserName(userName);
		register.setUserPassword(userPassword);
		register.setEmail(userEmail);
		String jsonString = JSON.toJSONString(register);
		JSONNameandString json = new JSONNameandString();
		json.setJSONName(ClientRegister.class.getName());
		json.setJSONStr(jsonString);
		String ret = JSON.toJSONString(json);
		ret = EnDeCryProcess.SysKeyEncryWithBase64(ret, secretKey);
		return ret;
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
	
	private ClientSession setHasLogin(boolean hasLogin) {
		this.hasLogin = hasLogin;
		return this;
	}
	
	public ClientSession setUserName(String userName) {
		this.userName = userName;
		setHasLogin(false);
		return this;
	}
	public ClientSession setUserPassword(String userPassword) {
		this.userPassword = userPassword;
		setHasLogin(false);
		return this;
	}
	public ClientSession setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
		return this;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public boolean isRegister() {
		return register;
	}
	public void setRegister(boolean register) {
		this.register = register;
	}
	

}
	
	
