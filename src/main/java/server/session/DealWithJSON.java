package server.session;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.crypto.SecretKey;
import server.db.DBCallable;
import server.db.StatementManager;
import util.SessionTool;
import util.VerifyLogin;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import json.client.login.ClientLogin;
import json.client.login.ClientRegister;
import json.client.session.AddFriend;
import json.client.session.AddFriendResult;
import json.client.session.RemoveFriend;
import json.client.session.RequestFriendList;
import json.client.session.SendGroupMessage;
import json.client.session.SendMessage;
import json.server.session.FriendMeta;
import json.server.session.RemoveFriendResult;
import json.server.session.RmFriendSlid;
import json.server.session.SendBackJSON;
import json.server.login.RegisiterResult;
import json.server.login.SuccessLogin;
import json.server.login.WrongNameorPassword;
import json.server.session.FriendList;
import json.util.JSONNameandString;

public class DealWithJSON {

	private volatile String username;
	private volatile String userpassword;
	private volatile String useremail;
	private Channel ch;
	private volatile SecretKey secretKey;

	public void dealwith(JSONNameandString json, Channel channel) {
			
			String name = json.getJSONName();
			System.out.println("receive :"+json.getJSONName()+"——"+json.getJSONStr()+"(in DealWithJSON 30 line)");
			switch(name){
			case "json.client.login.ClientLogin":
				dealwithClientLogin(json.getJSONStr(),channel);
				break;
			case "json.client.login.ClientRegister":
				dealwithClientRegister(json.getJSONStr(),channel);
				break;
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
			case "json.client.session.SendGroupMessage":
				dealwithSendGroupMessage(json,channel.id().asLongText());
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

	private void dealwithClientRegister(String jsonStr, final Channel channel) {
		ClientRegister clientRegister = JSON.parseObject(jsonStr, ClientRegister.class);
		username = clientRegister.getUserName();
		userpassword = clientRegister.getUserPassword();
		useremail = clientRegister.getEmail();
		
		StatementManager.sendDBCallable(new DBCallable(){

			@Override
			public SendBackJSON run(){
				String sql = "insert into user (username,userpassword,useremail) values (\""+username+"\",\""+userpassword+"\",\""+useremail+"\");";
				RegisiterResult regisiterResult = new RegisiterResult();
				SendBackJSON sendBackJSON = new SendBackJSON();
				sendBackJSON.setChannel(ch);
				sendBackJSON.setJSONName(RegisiterResult.class.getName());
				try {
					int updatelinenumber = protectsta.executeUpdate(sql);
					if(updatelinenumber==1){
						regisiterResult.setSuccess(true);
					}
				} catch (SQLException e) {
					regisiterResult.setSuccess(false);
					if(e.getMessage().substring(0,15).equalsIgnoreCase("Duplicate entry")){
						regisiterResult.setReason("username is exist");
					}else{
						regisiterResult.setReason("other");
					}
				}finally{
/*					if(sta!=null)
						StatementManager.backStatement(sta);*/
				}
				sendBackJSON.setJSONStr(JSON.toJSONString(regisiterResult));
				sendBackJSON.setSecretKey(secretKey);
				return sendBackJSON;
			}
			
		});
		
	}

	private void dealwithClientLogin(String jsonStr, final Channel channel) {

			ClientLogin clientLogin = JSON.parseObject(jsonStr,ClientLogin.class);
			username = clientLogin.getName();
			userpassword = clientLogin.getPassword();

			StatementManager.sendDBCallable(new DBCallable(){

				@Override
				public SendBackJSON run() {
					String sql = "select * from user where username=\'"+username+"\';";
					String ret;
					try {
						ResultSet resultSet = protectsta.executeQuery(sql);
						SendBackJSON backJSON = new SendBackJSON();
//						backJSON.setChannelID(channel.id().asLongText());
						backJSON.setChannel(ch);
						if(resultSet.next()
								&& username.equals(resultSet.getString("username"))
								&& userpassword.equals(resultSet.getString("userpassword"))){
								ChannelManager.addusermeta(username,ch,secretKey);
								SuccessLogin successLogin = new SuccessLogin();
								successLogin.setToken(SessionTool.GenerateSID(username, userpassword));
								ret = JSON.toJSONString(successLogin);
								backJSON.setJSONName(SuccessLogin.class.getName());
								backJSON.setJSONStr(ret);
								backJSON.setChannel(ch);
								backJSON.setSecretKey(secretKey);
						}else{
							WrongNameorPassword wrongNameorPassword = new WrongNameorPassword();
							wrongNameorPassword.setVerifyName(false);
							wrongNameorPassword.setVerfyPassword(false);
							ret = JSON.toJSONString(wrongNameorPassword);
							backJSON.setJSONName(WrongNameorPassword.class.getName());
							backJSON.setJSONStr(ret);
						}
						
						return backJSON;
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
//						if(sta!=null)
//							StatementManager.backStatement(sta);
					}
					return null;
				}
			});	
	}

	private void dealwithSendGroupMessage(JSONNameandString json, String asLongText) {
		SendGroupMessage groupMessage = JSON.parseObject(json.getJSONStr(), SendGroupMessage.class);
		groupMessage.getFriendlist().add(groupMessage.getName());
		Collections.sort(groupMessage.getFriendlist());
		for(String s : groupMessage.getFriendlist()){
			if(!s.equals(groupMessage.getName())){
				SendBackJSON back = new SendBackJSON();
				back.setJSONName(SendGroupMessage.class.getName());
				back.setJSONStr(JSON.toJSONString(groupMessage));
				ChannelManager.sendback(back, s);
			}
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
						back.setSecretKey(secretKey);
						back.setChannel(ch);
						ChannelManager.sendback(back, name);
					}
					String deletesql = "DELETE FROM offline WHERE username='"+name+"';";
					protectsta.executeUpdate(deletesql);
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
				back.setChannel(ch);
				back.setSecretKey(secretKey);
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
			back.setChannel(ch);
			back.setSecretKey(secretKey);
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
					SendBackJSON json = new SendBackJSON();
					try{
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
						json.setJSONName(FriendList.class.getName());
						json.setJSONStr(JSON.toJSONString(list));
						json.setChannel(ch);
						json.setSecretKey(secretKey);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
//						if(sta!=null)
//							StatementManager.backStatement(sta);
					}
					return json;
				}
			 });
		}
		
	}

	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}
	
	public void setChannel(Channel ch){
		this.ch = ch;
	}
	
	public String getUserName(){
		return username;
	}
}
