package server;

import server.handler.MyWebSocketFrameHandler;
import server.handler.MyWebSocketSeverHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

public class BaseServerInitializer extends ChannelInitializer<SocketChannel>
		implements ChannelHandler {
    private static final String WEBSOCKET_PATH = "/websocket";
    @Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
//		pipeline.addLast(new HttpRequestDecoder());
//        // Uncomment the following line if you don't want to handle HttpChunks.
//        pipeline.addLast(new HttpObjectAggregator(65536));
//		pipeline.addLast(new HttpResponseEncoder());
//        // Remove the following line if you don't want automatic content compression.
//        pipeline.addLast(new HttpContentCompressor());
        
		pipeline.addLast(new HttpServerCodec());
//        pipeline.addLast(new BaseServerHTTPHandler());
//        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new HttpObjectAggregator(65536));
//        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
        pipeline.addLast("http",new MyWebSocketSeverHandler());
//        pipeline.addLast(new MyWebSocketFrameHandler());
	}

}
