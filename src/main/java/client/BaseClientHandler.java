package client;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import client.message.ClientHttpRequestFactory;

import com.alibaba.fastjson.JSON;

import json.client.SendRandandSysKey;
import json.server.ACKwithRandom;
import json.server.SendPubKey;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;


public class BaseClientHandler extends SimpleChannelInboundHandler<HttpObject> {

	private boolean access = false;
	private boolean hasPubkey = false;
	private boolean hasACK = false;
	private String pubKeyAl;
	private String sysKeyAl;
	private PublicKey serPubKey;
	private int randomint;
	private SecretKey  secretKey;
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
			throws Exception {
		if(msg instanceof FullHttpResponse){
			FullHttpResponse res = (FullHttpResponse) msg;
			ByteBuf content = res.content();
			HttpRequest req = null;
			if(content.isReadable()){
				if(!access){
					if(!hasPubkey){
						VerifyPubKey(content);
						req =SendRandandSyskey();
					}else{
						if(!hasACK){
							VerifyRandom(content);
						}
					}
				}
				if(req!=null){
					ctx.writeAndFlush(req);
					if(hasPubkey)
						randomint++;
				}
			}
		}
	}

	private void VerifyPubKey(ByteBuf content) throws NoSuchAlgorithmException, InvalidKeySpecException {
		SendPubKey serContent = JSON.parseObject(content.toString(CharsetUtil.UTF_8),SendPubKey.class);
		setPubKeyAl(serContent.getSelPubKey());
		setSysKeyAl(serContent.getSelSysKey());
		byte[] bytes = serContent.getPubKeyEncode();
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
		setSerPubKey(keyFactory.generatePublic(keySpec));
		if(serPubKey != null)
			hasPubkey = true;
	}
	
	private boolean VerifyRandom(ByteBuf content) {
		String outs = contentdecry(content);
		ACKwithRandom ack = JSON.parseObject(outs,ACKwithRandom.class);
		if(randomint==ack.getRandom()){
			hasACK = true;
			System.out.println("safe");
			return true;
		}else{
			randomint--;
			return false;
		}
	}
	
	private HttpRequest SendRandandSyskey() {
		Random random = new Random();
		randomint = random.nextInt();
		
		KeyGenerator keyPairGen = null;
		try {
			keyPairGen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		keyPairGen.init(128);
		secretKey = keyPairGen.generateKey();  
		
		SendRandandSysKey json = new SendRandandSysKey();
		json.setRandom(randomint);
		json.setSyskeyend(secretKey.getEncoded());
		
		String str = JSON.toJSONString(json);
		
		Cipher cipher = null;
		ClientHttpRequestFactory initRequest = null;
		
		byte[] content = null;
		try {
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, serPubKey); 
			content = cipher.doFinal(str.getBytes());
			initRequest = new ClientHttpRequestFactory();
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		initRequest.addUri("access/?process=sendRandom");
		initRequest.addContent(content);
		return initRequest.product();
	}
	
	private String contentdecry(ByteBuf content){
		byte[] bytes = new byte[content.readableBytes()];
		content.readBytes(bytes);
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte [] out = cipher.doFinal(bytes);
			return new String(out);
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getPubKeyAl() {
		return pubKeyAl;
	}
	public void setPubKeyAl(String pubKeyAl) {
		this.pubKeyAl = pubKeyAl;
	}
	
	public String getSysKeyAl() {
		return sysKeyAl;
	}
	public void setSysKeyAl(String sysKeyAl) {
		this.sysKeyAl = sysKeyAl;
	}
	
	public PublicKey getSerPubKey() {
	return serPubKey;
}
	public void setSerPubKey(PublicKey serPubKey) {
		this.serPubKey = serPubKey;
	}
}
