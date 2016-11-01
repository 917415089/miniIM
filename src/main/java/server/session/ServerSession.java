package server.session;

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
import json.server.login.WrongNameorPassword;
import json.util.JSONNameandString;

import com.alibaba.fastjson.JSON;

import util.EnDeCryProcess;
import util.SessionTool;
import util.VerifyLogin;

public class ServerSession {


	private boolean hasinit;
	private SecretKey secretKey;
	private String username;
	private String userpassword;
	private String useremail;

	public boolean isHasinit() {
		return hasinit;
	}
	
	public String init(String request,SecretKey secretKey){
		String str = EnDeCryProcess.SysKeyDecryWithBase64(request, secretKey);
		JSONNameandString json = JSON.parseObject(str, JSONNameandString.class);
		this.secretKey = secretKey;
		
		switch(json.getJSONName()){
		case "json.client.login.ClientLogin":
			return deallogin(json.getJSONStr());
		case "json.client.login.ClientRegister":
			return dealregister(json.getJSONStr());
		}
		
		return null;
	}

	private String dealregister(String jsonStr) {
		ClientRegister clientRegister = JSON.parseObject(jsonStr, ClientRegister.class);
		username = clientRegister.getUserName();
		userpassword = clientRegister.getUserPassword();
		useremail = clientRegister.getEmail();
		//unfinished
		return null;
	}

	private String deallogin(String jsonStr) {
		ClientLogin clientLogin = JSON.parseObject(jsonStr,ClientLogin.class);
		username = clientLogin.getName();
		userpassword = clientLogin.getPassword();
		if(VerifyLogin.verifyNameandPassword(username,userpassword)){
			hasinit = true;
			SuccessLogin successLogin = new SuccessLogin();
			successLogin.setToken(SessionTool.GenerateSID(username, userpassword));
			String ret = JSON.toJSONString(successLogin);
			ret = EnDeCryProcess.SysKeyEncryWithBase64(ret, secretKey);
			return ret;
		}else{
			hasinit = false;
			WrongNameorPassword wrongNameorPassword = new WrongNameorPassword();
			wrongNameorPassword.setVerifyName(VerifyLogin.verifyName(username));
			wrongNameorPassword.setVerfyPassword(VerifyLogin.verifyPassword(userpassword, userpassword));
			String ret = JSON.toJSONString(wrongNameorPassword);
			ret = EnDeCryProcess.SysKeyEncryWithBase64(ret, secretKey);
			return ret;
		}
	}

	public String getUsername() {
		return username;
	}

	public String getUserpassword() {
		return userpassword;
	}

	public String getUseremail() {
		return useremail;
	}

	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}

	
}
