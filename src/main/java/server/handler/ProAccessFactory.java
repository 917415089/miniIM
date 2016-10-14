package server.handler;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;

import json.client.GetPubKey;
import json.client.SendRandandSysKey;
import json.server.SendPubKey;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

public class ProAccessFactory {

	private static PrivateKey privateKey=null;
	private static PublicKey publicKey = null;
	private static int random;

	static public  FullHttpResponse getResponse(FullHttpRequest req) throws NoSuchAlgorithmException{
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());
		Map<String, List<String>> parameters = queryStringDecoder.parameters();
		if(!parameters.isEmpty()){
			for(Entry<String,List<String>> p :parameters.entrySet()){
				String key = p.getKey();
				List<String> vals = p.getValue();
				if(key.equalsIgnoreCase("process")){
					if(vals.get(0).equalsIgnoreCase("getPubKey"))
						return SelecteKeyAndSendPubkey(req);
					if(vals.get(0).equalsIgnoreCase("sendRandom")){
						return GetRandomAndACK(req);
					}
				}
			}
		}
		return null;
	}

	private static FullHttpResponse GetRandomAndACK(FullHttpRequest req) {
		ByteBuf content = req.content();
		if(content.isReadable()){
			SendRandandSysKey json = JSON.parseObject(content.toString(CharsetUtil.UTF_8),SendRandandSysKey.class);
			random = json.getRandom();
		}
		return null;
	}

	private static FullHttpResponse SelecteKeyAndSendPubkey(FullHttpRequest req) throws NoSuchAlgorithmException {
		ByteBuf content = req.content();
    	String selPub = null;
    	String selSys = null;
    	
        if (content.isReadable()) {
        	GetPubKey cliContent = JSON.parseObject(content.toString(CharsetUtil.UTF_8),GetPubKey.class);
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
    		
    		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
    		SendPubKey rescontent = new SendPubKey();
    		rescontent.setProcess("sendPubKey");
    		rescontent.setSelPubKey(selPub);
    		rescontent.setSelSysKey(selSys);
    		rescontent.setPubKeyEncode(publicKey.getEncoded());
    		response.content().writeBytes(JSON.toJSONString(rescontent).getBytes());
    		response.headers().set(HttpHeaderNames.CONTENT_LENGTH,response.content().readableBytes());
    		return response;
        }
		return null;
	}
}


/*		if(msg instanceof HttpRequest){
HttpRequest request = (HttpRequest) msg;
cookieSet = new HashSet<Cookie>();
headers = request.headers();
if (!headers.isEmpty()) {
    for (Map.Entry<String, String> h: headers) {
        CharSequence key = h.getKey();
        CharSequence value = h.getValue();
        	if(key.equals("cookie")){
        		cookieSet.addAll(ServerCookieDecoder.LAX.decode((String) value));//静态类方法，使用单例模式。可以看看源码
        	}
    }
}


QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
params = queryStringDecoder.parameters();
if (!params.isEmpty()) {
    for (Entry<String, List<String>> p: params.entrySet()) {
        @SuppressWarnings("unused")
		String key = p.getKey();
        List<String> vals = p.getValue();
        for (@SuppressWarnings("unused") String val : vals) {
        	
        }
        }
    }
}

if(msg instanceof HttpContent){
httpContent = (HttpContent) msg;
ByteBuf content = httpContent.content();
if (content.isReadable()) {
	imHttpRequest cliContent = JSON.parseObject(content.toString(CharsetUtil.UTF_8),imHttpRequest.class);
	if(cliContent.getProcess().equalsIgnoreCase("getPubKey")){
		if(selectKey(cliContent)){
			if(generateKey()){
				 ctx.writeAndFlush(sendPubKey());
			}
		}
	}
	System.out.println();
}

}
}



private boolean generateKey() throws NoSuchAlgorithmException {
KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(pubKeyAlth);
keyPairGenerator.initialize(1024);
KeyPair keyPair = keyPairGenerator.generateKeyPair();
publicKey = keyPair.getPublic();
privateKey = keyPair.getPrivate();
if(publicKey ==null || privateKey == null)
return false;
return true;
}

private HttpResponse sendPubKey() {
DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
imHttpResponse rescontent = new imHttpResponse();
rescontent.setProcess("sendPubKey");
rescontent.setSelPubKey(pubKeyAlth);
rescontent.setSelSysKey(SysKeyAlth);
rescontent.setPubKey(publicKey);
response.content().writeBytes(JSON.toJSONString(rescontent).getBytes());
response.headers().set(HttpHeaderNames.CONTENT_LENGTH,response.content().readableBytes());
return response;
}*/