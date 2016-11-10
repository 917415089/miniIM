package server.session;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import server.db.StatementManager;
import util.EnDeCryProcess;

import com.alibaba.fastjson.JSON;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import json.client.session.RequestFriendList;
import json.server.session.CannotFindCommand;
import json.server.session.SendBackJSON;
import json.server.session.FriendList;
import json.util.JSONNameandString;

public class DealWithJSON {

	private String username;
	private String userpassword;
	public void dealwith(JSONNameandString json, Channel channel) {

			String name = json.getJSONName();
			switch(name){
			case "json.client.session.RequestFriendList":
				{
					dealwithFriendList(json,channel.id().asLongText());
					break;
				}
			default:
					System.out.println("can't find command"+name+" from"+ChannelManager.getName(channel.id().asLongText()));
					CannotFindCommand cannotFindCommand = new CannotFindCommand();
					cannotFindCommand.setWrongCommand(name);
					String Jsonstr = JSON.toJSONString(cannotFindCommand);
					JSONNameandString sendjson = new JSONNameandString();
					sendjson.setJSONName(CannotFindCommand.class.getName());
					sendjson.setJSONStr(Jsonstr);
					String send = JSON.toJSONString(sendjson);
					send = EnDeCryProcess.SysKeyEncryWithBase64(send, ChannelManager.getKey(channel.id().asLongText()));
					channel.writeAndFlush(new TextWebSocketFrame(send));
			}
		
	}

	private void dealwithFriendList(JSONNameandString json, final String channelid) {
		RequestFriendList friendList = JSON.parseObject(json.getJSONStr(),RequestFriendList.class);
		if(friendList.getGroup().equalsIgnoreCase("all")){
			
			 StatementManager.getService().submit(new Callable<SendBackJSON>(){
				@Override
				public SendBackJSON call() throws Exception {
					String sql = "Select * from friends where mastername = \""+username+"\";";
					ResultSet set = StatementManager.getStatement().executeQuery(sql);
					FriendList list = new FriendList();
					List<String> friends = new ArrayList<String>();
					while(set.next()){
						String string = set.getString("friendname");
						friends.add(string);
					}
					list.setFriends(friends);
					SendBackJSON ret = new SendBackJSON();
					ret.setJSONName(FriendList.class.getName());
					ret.setJSONStr(JSON.toJSONString(list));
					ret.setChannelID(channelid);
					return ret;
				}
			 });
			 
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
