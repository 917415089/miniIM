package server.session;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import server.db.DBCallable;
import server.db.StatementManager;
import util.VerifyLogin;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import json.client.session.AddFriend;
import json.client.session.RequestFriendList;
import json.client.session.SendMessage;
import json.server.session.FriendMeta;
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
				dealwithFriendList(json,channel.id().asLongText());
				break;
			case "json.client.session.AddFriend":
				dealwithAddFriend(json,channel.id().asLongText());
				break;
			case "json.client.session.SendMessage":
				dealwithSendMessage(json,channel.id().asLongText());
				break;
			case "json.client.session.AddFriendResult":
				dealwithAddFriendResult(json,channel.id().asLongText());
				break;
			default:
				System.out.println("Server: can't deal with "+name);
				/*System.out.println("can't find command"+name+" from"+ChannelManager.getUsernamebyId(channel.id().asLongText()));
				CannotFindCommand cannotFindCommand = new CannotFindCommand();
				cannotFindCommand.setWrongCommand(name);
				String Jsonstr = JSON.toJSONString(cannotFindCommand);
				JSONNameandString sendjson = new JSONNameandString();
				sendjson.setJSONName(CannotFindCommand.class.getName());
				sendjson.setJSONStr(Jsonstr);
				String send = JSON.toJSONString(sendjson);
				send = EnDeCryProcess.SysKeyEncryWithBase64(send, ChannelManager.getSecreKeybyId(channel.id().asLongText()));
				channel.writeAndFlush(new TextWebSocketFrame(send));*/
			}
		
	}

	private void dealwithAddFriendResult(JSONNameandString json, String asLongText) {
		System.out.println(json.getJSONStr());
		
	}

	private void dealwithSendMessage(JSONNameandString json, String asLongText) {
		SendMessage sendmessage = JSON.parseObject(json.getJSONStr(), SendMessage.class);
		SendBackJSON back = new SendBackJSON();
		back.setJSONName(SendMessage.class.getName());
		back.setJSONStr(JSON.toJSONString(sendmessage));
		back.setChannelID(ChannelManager.getIdbyName(sendmessage.getFriend()));
		ChannelManager.sendback(back);
	}

	private void dealwithAddFriend(JSONNameandString json, String channelid) {
		AddFriend addFriend = JSON.parseObject(json.getJSONStr(), AddFriend.class);
		if(VerifyLogin.verifyName(addFriend.getFriendname())){
			SendBackJSON back = new SendBackJSON();
			back.setChannelID(ChannelManager.getIdbyName(addFriend.getFriendname()));
			back.setJSONName(AddFriend.class.getName());
			back.setJSONStr(JSON.toJSONString(addFriend));
			ChannelManager.sendback(back);
		}else{
			System.out.println("unfinished");
		}
	}

	private void dealwithFriendList(JSONNameandString json, final String channelid) {
		RequestFriendList friendList = JSON.parseObject(json.getJSONStr(),RequestFriendList.class);
		if(friendList.getGroup().equalsIgnoreCase("friends")){
			
			 StatementManager.getService().submit(new DBCallable(){
				@Override
				public SendBackJSON run() {
					SendBackJSON ret = new SendBackJSON();
//					Statement sta = null;
					try{
//						sta = StatementManager.getStatement();
						String sql = "Select * from friend where mastername = \""+username+"\";";
						ResultSet set = protectsta.executeQuery(sql);
						FriendList list = new FriendList();
						List<FriendMeta> friends = new ArrayList<FriendMeta>();
						while(set.next()){
							String string = set.getString("friendname");
							FriendMeta meta = new FriendMeta();
							meta.setGroup(set.getString("group"));
							meta.setName(string);
							friends.add(meta);
						}
						list.setFriends(friends);
						ret.setJSONName(FriendList.class.getName());
						ret.setJSONStr(JSON.toJSONString(list));
						ret.setChannelID(channelid);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
//						if(sta!=null)
//							StatementManager.backStatement(sta);
					}
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
