package server.session;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ChannelManager {

	private static ChannelManager channelmanager = new ChannelManager();
	
	volatile private ConcurrentHashMap<String,Channel> id2channel = new ConcurrentHashMap<String,Channel> ();
	//key is Channelid ,value is ChannelObject;
	volatile private ConcurrentHashMap<String,SecretKey> id2secrekey = new ConcurrentHashMap<String,SecretKey>();
	//key is Channelid ,value is Secrekey;
	volatile private ConcurrentHashMap<String,String> id2username = new ConcurrentHashMap<String,String>();
	//key is Channelid ,value is username;
	volatile private ConcurrentHashMap<String, String>uername2channel = new ConcurrentHashMap<String, String>(); 
	//key is username ,value is Channelid;
	
	private ChannelManager(){
		super();
	}

	public static  void addId2Channel(String asLongText, Channel channel) {
		channelmanager.id2channel.put(asLongText, channel);
	}

	public static  void rmId2Channel(String asLongText) {
		channelmanager.id2channel.remove(asLongText);
	}

	public static  Channel getChannelbyId(String asLongText){
		return channelmanager.id2channel.get(asLongText);
	}

	public static  void addId2Secrekey(String asLongText, SecretKeySpec secretKeySpec) {
		channelmanager.id2secrekey.put(asLongText, secretKeySpec);	
	}

	public static void rmId2Secrekey(String asLongText){
		channelmanager.id2secrekey.remove(asLongText);
	}

	public static  SecretKey getSecreKeybyId(String channelID) {
		return channelmanager.id2secrekey.get(channelID);
	}
	
	public static synchronized void addId2Username(String asLongText, String Name){
		channelmanager.id2username.put(asLongText, Name);
		channelmanager.uername2channel.put(Name,asLongText);
	}
	
	public static synchronized void rmId2Username(String asLongText) {
		channelmanager.id2username.remove(asLongText);
		channelmanager.uername2channel.remove(asLongText);
	}

	public static  String getUsernamebyId(String asLongText){
		return channelmanager.id2username.get(asLongText);
	}

	public static String getIdbyName(String name) {
		return channelmanager.uername2channel.get(name);
	}


	
}
