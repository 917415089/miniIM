package server.session;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import json.client.login.ClientLogin;
import json.server.login.SuccessLogin;
import json.server.login.WrongNameorPassword;

import com.alibaba.fastjson.JSON;

import util.EnDeCryProcess;
import util.SessionTool;
import util.VerifyLogin;

public class ServerSession {


	private boolean hasinit;
	private SecretKey secretKey;
	private String username;
	private String userpassword;

	public boolean isHasinit() {
		return hasinit;
	}
	
	public String init(String request,SecretKey secretKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		String str = EnDeCryProcess.SysKeyDecryWithBase64(request, secretKey);
		ClientLogin clientLogin = JSON.parseObject(str,ClientLogin.class);
		username = clientLogin.getName();
		userpassword = clientLogin.getPassword();
		this.secretKey = secretKey;
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
	


}
