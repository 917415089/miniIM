package server.handler;

import json.util.JSONMessage;
import com.alibaba.fastjson.JSON;
import server.session.ChannelManager;
import server.session.DealWithJSON;
import server.session.ServerSession;
import util.EnDeCryProcess;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class MyWebSocketFrameHandler extends
		SimpleChannelInboundHandler<WebSocketFrame> {

	private ServerAccessHandler accessHandler = null;
	private ServerSession session;
	private DealWithJSON dealexcutor;
	
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame)
			throws Exception {
		if (frame instanceof CloseWebSocketFrame) {
			MyWebSocketSeverHandler tmp = (MyWebSocketSeverHandler) ctx.pipeline().get("shaker");
			tmp.getHandshaker().close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
            ctx.write(frame.retain());
            return;
        }
		if(frame instanceof TextWebSocketFrame){
			String request  = ((TextWebSocketFrame) frame).text();
			
			if(accessHandler ==null){
				accessHandler = new ServerAccessHandler();
			}
			
			if(!accessHandler.getAccess()){
				accessHandler.handle(request);
				ctx.channel().writeAndFlush(new TextWebSocketFrame(accessHandler.getResult()));
				ChannelManager.addKey(ctx.channel().id().asLongText(),accessHandler.getSecretKeySpec());
			}else{
				if(!session.isHasinit()){
					System.out.println("session.isnot Hasinit()");
					ctx.channel().writeAndFlush(new TextWebSocketFrame(session.init(request, accessHandler.getSecretKeySpec())));
					dealexcutor.setUsername(session.getUsername());
					dealexcutor.setUserpassword(session.getUserpassword());
					ChannelManager.addName(ctx.channel().id().asLongText(), session.getUsername());
				}else{
					JSONMessage jsons = JSON.parseObject(EnDeCryProcess.SysKeyDecryWithBase64(request, accessHandler.getSecretKeySpec()),JSONMessage.class);
					dealexcutor.dealwith(jsons,ctx.channel());
					System.out.println("receive");
				}
			}
		}else{
			String message = "unsupported frame type:" + frame.getClass().getName();
			throw new UnsupportedOperationException(message);
		}
		
	}


	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		System.out.println("active");
		session = new ServerSession();
		dealexcutor = new DealWithJSON();
		ChannelManager.add(ctx.channel().id().asLongText(),ctx.channel());
	}


	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		ChannelManager.remove(ctx.channel().id().asLongText());
	}
	

}
