package server.session;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.alibaba.fastjson.JSON;

import util.EnDeCryProcess;
import json.server.session.SendBackJSON;
import json.util.JSONNameandString;

@SuppressWarnings("rawtypes")
public class SendBackJSONThread implements Callable {

	private BlockingQueue<Future<SendBackJSON>> que;
	
	
	@SuppressWarnings("unchecked")
	public SendBackJSONThread(BlockingQueue jSONque) {
		super();
		this.que = jSONque;
	}


	@Override
	public Object call() throws Exception {
		while(true){
			SendBackJSON DBResult = que.take().get();
			JSONNameandString SendBack = new JSONNameandString();
			SendBack.setJSONName(DBResult.getJSONName());
			SendBack.setJSONStr(DBResult.getJSONStr());
			String ret = JSON.toJSONString(SendBack);
			ret = EnDeCryProcess.SysKeyEncryWithBase64(ret, ChannelManager.getKey(DBResult.getChannelID()));
			Channel channel = ChannelManager.getChannel(DBResult.getChannelID());
			channel.writeAndFlush(new TextWebSocketFrame(ret));
		}
	}
}
