package json.server.session;

import javax.crypto.SecretKey;

import io.netty.channel.Channel;
import json.server.ServerJSON;

public class SendBackJSON implements ServerJSON{

	private String JSONName;
	private String JSONStr;
	private Channel channel;
	private SecretKey secretKey;
	
	
	public String getJSONName() {
		return JSONName;
	}
	public void setJSONName(String jSONName) {
		JSONName = jSONName;
	}
	public String getJSONStr() {
		return JSONStr;
	}
	public void setJSONStr(String jSONStr) {
		JSONStr = jSONStr;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public SecretKey getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}
	
	
}
