package server.session;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import client.state.ClientDealwithJSON;
import util.EnDeCryProcess;
import json.server.session.SendBackJSON;
import json.util.JSONNameandString;

public class SendBackJSONThread implements Callable<Future<SendBackJSON>> {

	private BlockingQueue<Future<SendBackJSON>> que;
	private final Logger logger = LoggerFactory.getLogger(SendBackJSONThread.class);

	public SendBackJSONThread(BlockingQueue<Future<SendBackJSON>> jSONque) {
		super();
		this.que = jSONque;
	}

	@Override
	public Future<SendBackJSON> call() throws Exception {
		while(true){
			SendBackJSON DBResult = que.take().get();
			if(DBResult==null) continue;
			JSONNameandString SendBack = new JSONNameandString();
			SendBack.setJSONName(DBResult.getJSONName());
			SendBack.setJSONStr(DBResult.getJSONStr());
			String ret = JSON.toJSONString(SendBack);
			logger.info("Send: CHANNEL:[{}]  JSON:[{}]",DBResult.getChannel(),ret);
			ret = EnDeCryProcess.SysKeyEncryWithBase64(ret, DBResult.getSecretKey());
			DBResult.getChannel().writeAndFlush(new TextWebSocketFrame(ret));
		}
	}
}
