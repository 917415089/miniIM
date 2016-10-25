package server.session;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.alibaba.fastjson.JSON;

import server.db.StatementManager;
import util.EnDeCryProcess;
import json.server.session.DataBaseResult;
import json.util.JSONNameandString;

public class SendBackJSONThread implements Callable {

	private BlockingQueue<Future<JSONNameandString>> que;
	
	
	public SendBackJSONThread(BlockingQueue jSONque) {
		super();
		this.que = jSONque;
	}


	@Override
	public Object call() throws Exception {
		while(true){
			DataBaseResult DBResult = StatementManager.getService().take().get();
			JSONNameandString SendBack = new JSONNameandString();
			SendBack.setJSONName(SendBack.getJSONName());
			SendBack.setJSONStr(DBResult.getJSONStr());
			String ret = JSON.toJSONString(SendBack);
			ret = EnDeCryProcess.SysKeyEncryWithBase64(ret, ChannelManager.getKey(DBResult.getChannelID()));
			Channel channel = ChannelManager.getChannel(DBResult.getChannelID());
			channel.writeAndFlush(new TextWebSocketFrame(ret));
		}
	}
}
