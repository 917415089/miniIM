package client.session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import javax.crypto.SecretKey;
import util.EnDeCryProcess;
import com.alibaba.fastjson.JSON;
import json.client.session.AddFriend;
import json.client.session.RequestFriendList;
import json.client.session.RemoveFriend;
import json.util.JSONMessage;
import json.util.JSONNameandString;

@Deprecated
public class MessageFactory {

	static HashSet<String> param;
	
	static{
		param = new HashSet<String>();
		param.add("lf");//list friend list;
		param.add("+f");//add friend;
		param.add("-f");//remove friend;
	}
	
	private SecretKey secretKey;
	
	public String product(String s){		
		String[] inputList  = s.split(" ");
		
		Hashtable<String, List<String>> paramlist = new Hashtable<>();
		for(int i = 0 ; i < inputList.length ; ){
			if(param.contains(inputList[i])){
				String Key = inputList[i++];
				List<String> Value= new ArrayList<String>();
				while(i<inputList.length && !param.contains(inputList[i])){
					Value.add(inputList[i++]);
				}
				paramlist.put(Key, Value);
			}else{
				i++;
			}
		}
		
		List<JSONNameandString> jsonlist = new ArrayList<JSONNameandString>();
		for(String key : paramlist.keySet()){
			JSONNameandString singleJSON = parseJSON(key,paramlist.get(key));
			jsonlist.add(singleJSON);
		}
		
		JSONMessage jsonMessage = assembleJSON(jsonlist);
		String ret = JSON.toJSONString(jsonMessage);
		ret  = EnDeCryProcess.SysKeyEncryWithBase64(ret, secretKey);
		return ret;
	}

	private JSONMessage assembleJSON(List<JSONNameandString> jsonlist) {
		JSONMessage jsonMessage = new JSONMessage();
		jsonMessage.setJson(jsonlist);
		return jsonMessage;
	}

	private JSONNameandString parseJSON(String key, List<String> list) {
		switch(key){
		case "lf":
			return getFriendList(list);
		case "+f":
			return getAddFriend(list);
		case "-f":
			return getRemoveFriend(list);
		default:
			System.out.println("please input right command!");
		}
		return null;
	}
	

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}

	private JSONNameandString getRemoveFriend(List<String> list) {
		RemoveFriend rf = new RemoveFriend();
		rf.setFriends(list);
		JSONNameandString jsonNameandString = new JSONNameandString();
		jsonNameandString.setJSONName(rf.getClass().getName());
		jsonNameandString.setJSONStr(JSON.toJSONString(rf));
		return jsonNameandString;
	}

	private JSONNameandString getAddFriend(List<String> list) {
		AddFriend addFriend = new AddFriend();
		addFriend.setFriends(list);
		JSONNameandString jsonNameandString = new JSONNameandString();
		jsonNameandString.setJSONName(addFriend.getClass().getName());
		jsonNameandString.setJSONStr(JSON.toJSONString(addFriend));
		return jsonNameandString;
	}

	private JSONNameandString getFriendList(List<String> list) {
		RequestFriendList fl = new RequestFriendList();
		if(list==null || list.size()==0){
			fl.setGroup("all");
		}else{
			String added = ""; 
			for(String s : list)
				added += s;
			fl.setGroup(added);
		}
		JSONNameandString jsonNameandString = new JSONNameandString();
		jsonNameandString.setJSONName(fl.getClass().getName());
		jsonNameandString.setJSONStr(JSON.toJSONString(fl));
		return jsonNameandString;
	}
}
