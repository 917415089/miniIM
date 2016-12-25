package server.session;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.crypto.SecretKey;
import com.alibaba.fastjson.JSON;
import json.server.session.SendBackJSON;
import json.util.JSONNameandString;
import server.db.DBCallable;
import server.db.StatementManager;
import util.EnDeCryProcess;

public class ChannelManager {

	private static class userMeta{
		final Channel ch;
		final SecretKey key;
		
		public userMeta(Channel ch, SecretKey key) {
			this.ch = ch;
			this.key = key;
		}

	}
	private static ConcurrentHashMap<String, userMeta> usermap = new ConcurrentHashMap<>();
	public static void addusermeta(String username,Channel ch, SecretKey key){
		usermap.put(username, new userMeta(ch, key));
	}
	public static void remove(String username) {
		usermap.remove(username);
	}
	public static userMeta getuserMeta(String username){
		return usermap.get(username);
	}
	
	
	private static BlockingQueue<SendBackJSON> sendback = new ArrayBlockingQueue<SendBackJSON>(100);

	static {
		ExecutorService sendBackThreadPool = Executors.newFixedThreadPool(2);
		sendBackThreadPool.submit(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					try {
						SendBackJSON DBResult =sendback.take();
						JSONNameandString SendBack = new JSONNameandString();
						SendBack.setJSONName(DBResult.getJSONName());
						SendBack.setJSONStr(DBResult.getJSONStr());
						String ret = JSON.toJSONString(SendBack);
						ret = EnDeCryProcess.SysKeyEncryWithBase64(ret, DBResult.getSecretKey());
						DBResult.getChannel().writeAndFlush(new TextWebSocketFrame(ret));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});
	}
	
	public static boolean sendback(final SendBackJSON back,final String name){
		
		userMeta userMeta = usermap.get(name);
		if(userMeta==null){
			StatementManager.sendDBCallable(new DBCallable() {
				
				@Override
				protected SendBackJSON run() {
//					back.setChannelID("defalut");
					String sql = "INSERT INTO offline (username,jsonclass,jsonstring) VALUES('"+name+"','"+back.getJSONName()+"','"+back.getJSONStr()+"');";
					System.out.println(JSON.toJSONString(sql));
					try {
						protectsta.executeUpdate(sql);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
			});
			return false;
		}
		back.setChannel(userMeta.ch);
		back.setSecretKey(userMeta.key);
		try {
			sendback.put(back);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
