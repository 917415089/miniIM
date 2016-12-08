package server.session;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import server.db.DBCallable;
import server.db.StatementManager;
import util.VerifyLogin;
import com.alibaba.fastjson.JSON;

import client.ClientManage;
import io.netty.channel.Channel;
import json.client.session.AddFriend;
import json.client.session.AddFriendResult;
import json.client.session.RemoveFriend;
import json.client.session.RequestFriendList;
import json.client.session.SendMessage;
import json.server.session.FriendMeta;
import json.server.session.RemoveFriendResult;
import json.server.session.RmFriendSlid;
import json.server.session.SendBackJSON;
import json.server.session.FriendList;
import json.util.JSONNameandString;

public class DealWithJSON {

	private String username;
	private String userpassword;
	public void dealwith(JSONNameandString json, Channel channel) {

			String name = json.getJSONName();
			System.out.println("receive :"+json.getJSONStr()+"(in DealWithJSON 30 line)");
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
			case "json.client.session.RemoveFriend":
				dealwithRemoveFriend(json,channel.id().asLongText());
				break;
			case "json.client.session.OfflineRequest":
				dealwithOfflineRequest(channel.id().asLongText());
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

	private void dealwithOfflineRequest(final String asLongText) {
		final String name = username;
		StatementManager.sendDBCallable(new DBCallable() {
			
			@Override
			protected SendBackJSON run() {
				String sql = "SELECT * FROM offline WHERE username='"+name+"';";
				try {
					ResultSet executeQuery = protectsta.executeQuery(sql);
					while(executeQuery.next()){
						SendBackJSON back = new SendBackJSON();
						back.setJSONName(executeQuery.getString("jsonclass"));
						back.setJSONStr(executeQuery.getString("jsonstring"));

						ChannelManager.sendback(back, name);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//unfinish remove
				return null;
			}
		});
		
	}

	private void dealwithRemoveFriend(JSONNameandString json, final String asLongText) {
		final RemoveFriend removeFriend = JSON.parseObject(json.getJSONStr(), RemoveFriend.class);
		StatementManager.sendDBCallable(new DBCallable() {
			
			@Override
			protected SendBackJSON run() {
				String sql = "DELETE FROM friend WHERE (mastername='"+username+"' and friendname='"+removeFriend.getName()+"') OR (mastername='"+removeFriend.getName()+"' and friendname='"+username+"');";
				try {
					protectsta.executeUpdate(sql);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				RemoveFriendResult removeFriendResult = new RemoveFriendResult();
				removeFriendResult.setName(removeFriend.getName());
				removeFriendResult.setSuccess(true);
				
				SendBackJSON back = new SendBackJSON();
				back.setChannelID(asLongText);
				back.setJSONName(RemoveFriendResult.class.getName());
				back.setJSONStr(JSON.toJSONString(removeFriendResult));
				
				RmFriendSlid side = new RmFriendSlid();
				side.setName(username);
				
				SendBackJSON sidejson = new SendBackJSON();
				sidejson.setJSONName(RmFriendSlid.class.getName());
				sidejson.setJSONStr(JSON.toJSONString(side));
				ChannelManager.sendback(sidejson,removeFriend.getName());
				
				return back;
			}
		});
		
		
	}

	private void dealwithAddFriendResult(JSONNameandString json, String asLongText) {
		final AddFriendResult friendResult = JSON.parseObject(json.getJSONStr(), AddFriendResult.class);
		SendBackJSON back = new SendBackJSON();
		back.setJSONName(json.getJSONName());
		back.setJSONStr(json.getJSONStr());
		ChannelManager.sendback(back,friendResult.getRequestorname());
		if(friendResult.isReceiverestate()){
			StatementManager.sendDBCallable(new DBCallable(){

				@Override
				protected SendBackJSON run() {
					String  sql = "INSERT INTO friend VALUES (\'"+friendResult.getRequestorname()+"\',\'"+friendResult.getReceivername()+"\',\'"+friendResult.getRequestorgroup()+"\'),(\'"+friendResult.getReceivername()+"\',\'"+friendResult.getRequestorname()+"\',\'"+friendResult.getReceivergroup()+"\');";
					try {
						protectsta.executeUpdate(sql);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}

				
			});
		}
		
//		StatementManager.getService().submit(new Cal);
	}

	private void dealwithSendMessage(JSONNameandString json, String asLongText) {
		SendMessage sendmessage = JSON.parseObject(json.getJSONStr(), SendMessage.class);
		SendBackJSON back = new SendBackJSON();
		back.setJSONName(SendMessage.class.getName());
		back.setJSONStr(JSON.toJSONString(sendmessage));
		ChannelManager.sendback(back,sendmessage.getFriend());
	}

	private void dealwithAddFriend(JSONNameandString json, String channelid) {
		AddFriend addFriend = JSON.parseObject(json.getJSONStr(), AddFriend.class);
		if(VerifyLogin.verifyName(addFriend.getFriendname())){
			SendBackJSON back = new SendBackJSON();

			back.setJSONName(AddFriend.class.getName());
			back.setJSONStr(JSON.toJSONString(addFriend));
			ChannelManager.sendback(back,addFriend.getFriendname());
		}else{
			System.out.println("unfinished");
		}
	}

	private void dealwithFriendList(JSONNameandString json, final String channelid) {
		RequestFriendList friendList = JSON.parseObject(json.getJSONStr(),RequestFriendList.class);
		if(friendList.getGroup().equalsIgnoreCase("Friends")){
			 StatementManager.sendDBCallable(new DBCallable(){
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
