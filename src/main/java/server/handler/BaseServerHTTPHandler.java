package server.handler;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import json.client.SupportedAlgorithm;
import json.client.SendRandandSysKey;
import json.server.ServerACKwithRandom;
import json.server.SelectAlgorithmandPubkey;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
@Deprecated
public class BaseServerHTTPHandler extends SimpleChannelInboundHandler<Object> {
	
	private PrivateKey privateKey=null;
	private PublicKey publicKey = null;
	private SecretKeySpec secretKeySpec;
	private int random = -1;//security issue

	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception{

		if(msg instanceof FullHttpRequest){
			FullHttpRequest req = (FullHttpRequest) msg;
			URI uri = URI.create(req.uri());
			HttpResponse response = null;
			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());
			Map<String, List<String>> parameters = queryStringDecoder.parameters();
			
			if(uri.getPath().equalsIgnoreCase("/access/")){
				
				if(!parameters.isEmpty()){
					for(Entry<String,List<String>> p :parameters.entrySet()){
						String key = p.getKey();
						List<String> vals = p.getValue();
						if(key.equalsIgnoreCase("process")){
							if(vals.get(0).equalsIgnoreCase("getPubKey"))
								response = SelecteKeyAndSendPubkey(req);
							if(vals.get(0).equalsIgnoreCase("sendRandom")){
								response = GetRandomAndACK(req);
								
							}
						}
					}
				}
				ctx.writeAndFlush(response);
			}

		}
	}
	
	private FullHttpResponse GetRandomAndACK(FullHttpRequest req) {
		ByteBuf content = req.content();
		FullHttpResponse res = null;
		if(content.isReadable()){
			try {
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				byte[] bytes = new byte[content.readableBytes()];
				content.readBytes(bytes);
				String out = new String(cipher.doFinal(bytes));
				SendRandandSysKey json = JSON.parseObject(out,SendRandandSysKey.class);
				secretKeySpec = new SecretKeySpec(json.getSyskeyend(), "AES");
				random = json.getRandom();
				
				ServerACKwithRandom acKwithRandom = new ServerACKwithRandom();
				acKwithRandom.setRandom(++random);
				String con = JSON.toJSONString(acKwithRandom);
				
				Cipher cipher2 = Cipher.getInstance("AES");
				cipher2.init(Cipher.ENCRYPT_MODE, secretKeySpec);
				byte[] out1 = cipher2.doFinal(con.getBytes());
				res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
				res.content().writeBytes(out1);
				res.headers().set(HttpHeaderNames.CONTENT_LENGTH,res.content().readableBytes());	
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
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
		}
		return res;
	}

	private FullHttpResponse SelecteKeyAndSendPubkey(FullHttpRequest req) throws NoSuchAlgorithmException {
		ByteBuf content = req.content();
    	String selPub = null;
    	String selSys = null;
    	
        if (content.isReadable()) {
        	SupportedAlgorithm cliContent = JSON.parseObject(content.toString(CharsetUtil.UTF_8),SupportedAlgorithm.class);
        	List<String> pubkeyal = cliContent.getSupPubKey();
        	List<String> syskeyal = cliContent.getSupSysKey();

        	for(String s : pubkeyal){
        		if(s.equalsIgnoreCase("RSA")){
        			selPub = s;
        			break;
        		}
        	}
        	for(String s : syskeyal){
        		if(s.equalsIgnoreCase("AES")){
        			selSys = s;
        			break;
        		}
        	}
    		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(selPub);
    		keyPairGenerator.initialize(1024);
    		KeyPair keyPair = keyPairGenerator.generateKeyPair();
    		publicKey = keyPair.getPublic();
    		privateKey = keyPair.getPrivate();
    		
    		SelectAlgorithmandPubkey rescontent = new SelectAlgorithmandPubkey();
    		rescontent.setProcess("sendPubKey");
    		rescontent.setSelPubKey(selPub);
    		rescontent.setSelSysKey(selSys);
    		rescontent.setPubKeyEncode(publicKey.getEncoded());
    		
    		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
    		response.content().writeBytes(JSON.toJSONString(rescontent).getBytes());
    		response.headers().set(HttpHeaderNames.CONTENT_LENGTH,response.content().readableBytes());
    		return response;
        }
		return null;
	}
}
