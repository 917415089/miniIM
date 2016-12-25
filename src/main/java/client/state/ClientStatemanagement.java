package client.state;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import server.session.state.State;

public class ClientStatemanagement {

	private volatile State state;
//	private String SelectedPubKey;
//	private String SelectedSysKey;
//	private PublicKey publicKey;
	private SecretKey secretKey;
	private AtomicInteger random;
	private CountDownLatch accessSign = new CountDownLatch(1);
	private Channel ch;
	
	private final SendSupportedKey sendSupportedKey;
	private final SendSysKeyandRandom sendSysKeyandRandom;
	private final Access access;
	private final ClientDealwithJSON clientDealwithJSON;
	
	public ClientStatemanagement() {
		sendSupportedKey = new SendSupportedKey(this);
		sendSysKeyandRandom = new SendSysKeyandRandom(this);
		access = new Access(this);
		clientDealwithJSON = new ClientDealwithJSON(this);
		
		state= sendSupportedKey;
	}
	
	public void handle(String str){
		try {
			state.handle(str);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void waitforAccessSign(){
		try {
			accessSign.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void downAccessSign(){
		accessSign.countDown();
	}
	
	synchronized void  setState(State sta){
		state=sta;
	}

	SendSysKeyandRandom getSendSysKeyandRandom() {
		return sendSysKeyandRandom;
	}

/*	synchronized void setSelectedPubKey(String selectedPubKey) {
		SelectedPubKey = selectedPubKey;
	}
*/
//	synchronized void setSelectedSysKey(String selectedSysKey) {
//		SelectedSysKey = selectedSysKey;
//	}

//	synchronized void setPublicKey(PublicKey publicKey) {
//		this.publicKey = publicKey;
//	}

	SecretKey getSecretKey() {
		return secretKey;
	}

	synchronized void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}

	public Access getAccess() {
		return access;
	}

	public AtomicInteger getRandom() {
		return random;
	}

	public void setRandom(AtomicInteger random) {
		this.random = random;
	}

	public void WriteWebSocketChannel(String s){
		ch.writeAndFlush(new TextWebSocketFrame(s));
	}
	
	public void WriteChannel(Object s){
		ch.writeAndFlush(s);
	}
	
	public void CloseandSyn(){
		ch.writeAndFlush(new CloseWebSocketFrame());
		try {
			ch.closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setChannel(Channel ch2) {
		ch=ch2;
	}

	public ClientDealwithJSON getClientDealwithJSON() {
		return clientDealwithJSON;
	}
	
}
