package server.session.callablejson;

import java.util.concurrent.Callable;
import com.alibaba.fastjson.JSON;
import server.session.ChannelManager;
import json.server.session.SendBackJSON;
import json.server.session.VerifyAddFriend;

public class RunAddFriend implements Callable<SendBackJSON> {

	private String friendname;
	private String group;
	private String username;
	
	@Override
	public SendBackJSON call() throws Exception {
		if(group==null){
			group = "friends";
		}
		VerifyAddFriend verifyAddFriend = new VerifyAddFriend();
		verifyAddFriend.setRequestname(username);
		
		SendBackJSON back = new SendBackJSON();
		back.setChannelID(ChannelManager.getIdbyName(friendname));
		back.setJSONName(VerifyAddFriend.class.getName());
		back.setJSONStr(JSON.toJSONString(verifyAddFriend));
//		System.out.println(JSON.toJSONString(back));
		return back;
	}

	public void setFriendname(String friendname) {
		this.friendname = friendname;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
