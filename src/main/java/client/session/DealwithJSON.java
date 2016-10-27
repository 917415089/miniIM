package client.session;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import util.EnDeCryProcess;
import json.server.session.CannotFindCommand;
import json.server.session.FriendList;
import json.util.JSONNameandString;

import com.alibaba.fastjson.JSON;


	
public class DealwithJSON {
	private SecretKey secretKey;
	
	public void product(String request) {
		try {
			request = EnDeCryProcess.SysKeyDecryWithBase64(request, secretKey);
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONNameandString Json = JSON.parseObject(request, JSONNameandString.class);
		switch(Json.getJSONName()){
		case "json.server.session.CannotFindCommand":
			DealWithCannotFindCommand(Json.getJSONStr());
			break;
		case "json.server.session.FriendList":
			DealWithFriendList(Json.getJSONStr());
			break;
		default:
				System.out.println("receive wrong JSON");
		}
		
	}

	private void DealWithFriendList(String jsonStr) {
		FriendList friendList = JSON.parseObject(jsonStr, FriendList.class);
		System.out.println("FriendList:");
		for(String s : friendList.getFriends()){
			System.out.println("      "+s);
		}
		System.out.println("end of Friendlists");
		
	}

	private void DealWithCannotFindCommand(String jsonStr) {
		CannotFindCommand parseObject = JSON.parseObject(jsonStr,CannotFindCommand.class);
		System.out.println("can't find command"+parseObject.getWrongCommand());
		
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}
	
	

}
