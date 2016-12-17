package server.session;

import io.netty.channel.Channel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Future;
import javax.crypto.SecretKey;
import json.client.login.ClientLogin;
import json.client.login.ClientRegister;
import json.server.login.RegisiterResult;
import json.server.login.SuccessLogin;
import json.server.login.WrongNameorPassword;
import json.server.session.SendBackJSON;
import json.util.JSONNameandString;
import com.alibaba.fastjson.JSON;
import server.db.DBCallable;
import server.db.StatementManager;
import util.EnDeCryProcess;
import util.SessionTool;

public class ServerSession {

	private Channel ch;
	private boolean hasinit;
	private SecretKey secretKey;
	private String username;
	private String userpassword;
	private String useremail;
	
	public ServerSession(Channel channel){
		ch = channel;
	}
	
	@SuppressWarnings("unused")
	private ServerSession(){
		
	}

	public boolean isHasinit() {
		return hasinit;
	}
	
	
	private void setHasinit(boolean hasinit) {
		this.hasinit = hasinit;
	}

	public void init(String request){
		secretKey = ChannelManager.getSecreKeybyId(ch.id().asLongText());
		String str = EnDeCryProcess.SysKeyDecryWithBase64(request, secretKey);
		JSONNameandString json = JSON.parseObject(str, JSONNameandString.class);
		
		switch(json.getJSONName()){
		case "json.client.login.ClientLogin":
			deallogin(json.getJSONStr());
			break;
		case "json.client.login.ClientRegister":
			dealregister(json.getJSONStr());
		}

	}

	private void dealregister(String jsonStr) {
		ClientRegister clientRegister = JSON.parseObject(jsonStr, ClientRegister.class);
		username = clientRegister.getUserName();
		userpassword = clientRegister.getUserPassword();
		useremail = clientRegister.getEmail();
		
		StatementManager.sendDBCallable(new DBCallable(){

			@Override
			public SendBackJSON run(){
//				Statement sta = StatementManager.getStatement();
				String sql = "insert into user (username,userpassword,useremail) values (\""+username+"\",\""+userpassword+"\",\""+useremail+"\");";
				RegisiterResult regisiterResult = new RegisiterResult();
				SendBackJSON sendBackJSON = new SendBackJSON();
				sendBackJSON.setChannelID(ch.id().asLongText());
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
				return sendBackJSON;
			}
			
		});
	}

	private void deallogin(String jsonStr) {
		ClientLogin clientLogin = JSON.parseObject(jsonStr,ClientLogin.class);
		username = clientLogin.getName();
		userpassword = clientLogin.getPassword();
		
		Future<SendBackJSON> sendDBCallable = StatementManager.sendDBCallable(new DBCallable(){

			@Override
			public SendBackJSON run() {
//				Statement sta = StatementManager.getStatement();
				String sql = "select * from user where username=\""+username+"\";";
				String ret;
				try {
					ResultSet resultSet = protectsta.executeQuery(sql);
					SendBackJSON backJSON = new SendBackJSON();
					backJSON.setChannelID(ch.id().asLongText());
					if(resultSet.next() && username.equals(resultSet.getString("username"))&&userpassword.equals(resultSet.getString("userpassword"))){						
							SuccessLogin successLogin = new SuccessLogin();
							successLogin.setToken(SessionTool.GenerateSID(username, userpassword));
							ret = JSON.toJSONString(successLogin);
							backJSON.setJSONName(SuccessLogin.class.getName());
							backJSON.setJSONStr(ret);
							
							setHasinit(true);
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
//					if(sta!=null)
//						StatementManager.backStatement(sta);
				}
				return null;
			}
		});
	}

	public String getUsername() {
		return username;
	}

	public String getUserpassword() {
		return userpassword;
	}

	public String getUseremail() {
		return useremail;
	}

	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}

	
}
