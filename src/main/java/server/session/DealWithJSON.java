package server.session;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

import server.db.StatementManager;

import com.alibaba.fastjson.JSON;

import io.netty.channel.Channel;
import json.client.session.AddFriend;
import json.client.session.RequestFriendList;
import json.server.session.DataBaseResult;
import json.server.session.FriendList;
import json.util.JSONMessage;
import json.util.JSONNameandString;

public class DealWithJSON {

	private String username;
	private String userpassword;
	public void dealwith(JSONMessage jsons, Channel channel) {
		for(JSONNameandString json :jsons.getJson()){
			String name = json.getJSONName();
			switch(name){
			case "json.client.session.RequestFriendList":
				{
					dealwithFriendList(json,channel.id().asLongText());
				}break;
			}
		}
		
	}

	private void dealwithFriendList(JSONNameandString json, final String channelid) {
		RequestFriendList friendList = JSON.parseObject(json.getJSONStr(),RequestFriendList.class);
		if(friendList.getGroup().equalsIgnoreCase("all")){
			
			 StatementManager.getService().submit(new Callable<DataBaseResult>(){
				@Override
				public DataBaseResult call() throws Exception {
					String sql = "Select * from friends where mastername = \""+username+"\";";
					ResultSet set = StatementManager.getStatement().executeQuery(sql);
					FriendList list = new FriendList();
					List<String> friends = new ArrayList<String>();
					while(set.next()){
						String string = set.getString("friendname");
						friends.add(string);
					}
					list.setFriends(friends);
					DataBaseResult ret = new DataBaseResult();
					ret.setJSONName(FriendList.class.getName());
					ret.setJSONStr(JSON.toJSONString(list));
					ret.setChannelID(channelid);
					return ret;
				}
			 });
			 
/*			 try {
				JSONNameandString string = StatementManager.getService().take().get();
				System.out.println(string.getJSONName());
				System.out.println(string.getJSONStr());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			 
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserpassword() {
		return userpassword;
	}

	public void setUserpassword(String userpassword) {
		this.userpassword = userpassword;
	}
	
	

}
