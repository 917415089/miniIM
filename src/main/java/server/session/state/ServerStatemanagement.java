package server.session.state;

import java.security.PrivateKey;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import json.util.AccessReset;

public class ServerStatemanagement {


	private String SelectedSysKey;
	private PrivateKey privateKey;
	private AtomicInteger Random;
	private SecretKey secretKey;
	private Channel ch;
	private int ResetTime = 0; 
	
	private volatile State state;
	
	private final ServerInitState serverInitState;
	private final SelectAlgorithmandPubKey selectAlgorithmandPubKey;
	private final ServerACK serverAck;
	private final ServerDealwithJSON serverDealwithJSON;
	
	public ServerStatemanagement() {
		serverInitState = new ServerInitState(this);
		selectAlgorithmandPubKey = new SelectAlgorithmandPubKey(this);
		serverAck = new ServerACK(this);
		serverDealwithJSON = new ServerDealwithJSON(this);

		state = serverInitState;
	}
	
	public void handle(String s){
		try {
			state.handle(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(ResetTime>=10){
				ch.close();
			}
			e.printStackTrace();
			AccessReset reset = new AccessReset();
			reset.setReset(true);
			reset.setFromclient(false);
			state = serverInitState;
			WriteWebSocketChannel(JSON.toJSONString(reset));
			ResetTime++;
		}
	}

	String getSelectedSysKey() {
		return SelectedSysKey;
	}

	void setSelectedSysKey(String selectedSysKey) {
		SelectedSysKey = selectedSysKey;
	}
	
	PrivateKey getPrivateKey() {
		return privateKey;
	}

	void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	AtomicInteger getRandom() {
		return Random;
	}

	void setRandom(AtomicInteger random) {
		Random = random;
	}

	void setCh(Channel ch) {
		this.ch = ch;
	}

	SecretKey getSecretKey() {
		return secretKey;
	}

	void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}

	public String getUserName() {
		return serverDealwithJSON.getUserName();
	}

	State getState() {
		return state;
	}

	void setState(State state) {
		this.state = state;
	}

	ServerInitState getServerInitState() {
		return serverInitState;
	}

	SelectAlgorithmandPubKey getSelectAlgorithmandPubKey() {
		return selectAlgorithmandPubKey;
	}

	ServerACK getServerAck() {
		return serverAck;
	}

	public ServerDealwithJSON getServerDealwithJSON() {
		return serverDealwithJSON;
	}

	public void WriteWebSocketChannel(String s){
		ch.writeAndFlush(new TextWebSocketFrame(s));
	}
	
	public void WriteChannel(Object s){
		ch.writeAndFlush(s);
	}
	
	public void CloseandSyn(){
		ch.close();
		try {
			ch.closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setChannel(Channel channel) {
		ch=channel;
		serverDealwithJSON.setChannel(ch);
	}

	public Channel getCh() {
		return ch;
	}
	
	void cleanReset(){
		ResetTime=0;
	}
}
