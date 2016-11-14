package client.session;

import javax.crypto.SecretKey;

import util.EnDeCryProcess;
import json.server.session.CannotFindCommand;
import json.server.session.FriendList;
import json.util.JSONNameandString;

import com.alibaba.fastjson.JSON;

@Deprecated
public class DealwithJSON {
	private SecretKey secretKey;
	
	public JSONNameandString product(String request) {
		request = EnDeCryProcess.SysKeyDecryWithBase64(request, secretKey);
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
		return Json;
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
