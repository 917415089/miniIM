package server;

import server.handler.MyWebSocketFrameHandler;
import server.handler.MyWebSocketSeverHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

public class BaseServerInitializer extends ChannelInitializer<SocketChannel>
		implements ChannelHandler {

    @Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast("httpcode",new HttpServerCodec());
        pipeline.addLast("compress",new WebSocketServerCompressionHandler());
        pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
        pipeline.addLast("shaker",new MyWebSocketSeverHandler());
        pipeline.addLast("frame",new MyWebSocketFrameHandler());
	}

}
