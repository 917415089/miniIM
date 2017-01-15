package server.handler;

import server.session.ChannelManager;
import server.session.state.ServerStatemanagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private final ServerStatemanagement State = new ServerStatemanagement();
	private final Logger logger = LoggerFactory.getLogger(MyWebSocketFrameHandler.class);
	
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
			State.handle(request);

		}else{
			String message = "unsupported frame type:" + frame.getClass().getName();
			throw new UnsupportedOperationException(message);
		}
		
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		logger.info("active");
		State.setChannel(ctx.channel());

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		logger.info("user : {} close connection",ChannelManager.getuserMeta(State.getUserName()));
		ChannelManager.remove(State.getUserName());
	}
	
}
