package server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import io.netty.example.http.websocketx.server.WebSocketServerIndexPage;
public class MyWebSocketSeverHandler extends
		SimpleChannelInboundHandler<Object> {

	private final static String WEBSOCKET_PATH = "/websocket";
	private WebSocketServerHandshaker handshaker;

	@SuppressWarnings("deprecation")
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
	}
	
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
	
	private void handleWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) {
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof TextWebSocketFrame) {
            // Echo the frame
            ctx.write(frame.retain());
            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
            // Echo the frame
            ctx.write(frame.retain());
            return;
        }
		
	}

	private void handleHttpRequest(ChannelHandlerContext ctx,
			FullHttpRequest msg) {
		if(!msg.getDecoderResult().isSuccess()){
			sendHttpResponse(ctx,msg,new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
			return;
		}
		
		if(msg.method() != GET){
			sendHttpResponse(ctx, msg, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
			return;
		}
		
		if("/".equals(msg.uri()) || "/index.html".equals(msg.uri())){
			String webSocketLocation = getWebSocketLocation(msg);
			ByteBuf content = WebSocketServerIndexPage.getContent(webSocketLocation);
			FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK,content);
			
			res.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html; charset=UTF-8");
			HttpUtil.setContentLength(res, content.readableBytes());
			
			sendHttpResponse(ctx, msg, res);
		}/*else{
			sendHttpResponse(ctx, msg, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
		}*/
		
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				getWebSocketLocation(msg), null, true,5*1024*1024);
		handshaker = wsFactory.newHandshaker(msg);
		if(handshaker == null){
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		}else{
			handshaker.handshake(ctx.channel(), msg);
		}
		
	}

	private static void sendHttpResponse(ChannelHandlerContext ctx,
			FullHttpRequest msg, FullHttpResponse res) {
		if(res.status().code()!=200){
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
			HttpUtil.setContentLength(res, res.content().readableBytes());
		}
		
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if(!HttpUtil.isKeepAlive(msg) || res.status().code() != 200){
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
	private static String getWebSocketLocation(FullHttpRequest req) {
		String protocol = "ws";
		return protocol + "://"+ req.headers().get(HttpHeaderNames.HOST) + WEBSOCKET_PATH;
	}

	public WebSocketServerHandshaker getHandshaker() {
		return handshaker;
	}

	
}
