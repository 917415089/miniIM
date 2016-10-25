package server.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.nio.channels.Channels;
import java.util.HashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ChannelManager {

	private static ChannelManager channelmanager = new ChannelManager();
	
	volatile private HashMap<String,Channel> ChannelMap = new HashMap<String,Channel> ();
	volatile private HashMap<String,SecretKey> KeyMap = new HashMap<String,SecretKey>();
	
	private ChannelManager(){
		super();
	}


	public static synchronized void add(String asLongText, Channel channel) {
		channelmanager.ChannelMap.put(asLongText, channel);
	}


	public static synchronized void remove(String asLongText) {
		channelmanager.ChannelMap.remove(asLongText);
	}

	public static synchronized Channel getChannel(String ID){
		return channelmanager.ChannelMap.get(ID);
	}


	public static synchronized void addKey(String asLongText, SecretKeySpec secretKeySpec) {
		channelmanager.KeyMap.put(asLongText, secretKeySpec);
		
	}


	public static synchronized SecretKey getKey(String channelID) {
		return channelmanager.KeyMap.get(channelID);
	}

	
}
