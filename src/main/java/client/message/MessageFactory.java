package client.message;

import io.netty.channel.Channel;
import javax.crypto.SecretKey;
import json.client.login.ClientLogin;

public class MessageFactory {

	private String userName;
	private String userPassword;
	private int random;
	private  SecretKey secretKey;
	private Channel channel; 
	private boolean hasLogin;
	
	public void init(String name, String password,int rand,SecretKey secre, Channel ch){
		userName = name;
		userPassword = password;
		random =rand;
		secretKey = secre;
		channel = ch;
		hasLogin = false;
	}
	

	public boolean login(){
		ClientLogin clientLogin = new ClientLogin();
		clientLogin.setName(userName);
		clientLogin.setPassword(userPassword);
		clientLogin.setRandom(random);

		return false;
	}
	public String getUserName() {
		return userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public int getRandom() {
		return random;
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public boolean isHasLogin() {
		return hasLogin;
	}
	

}
