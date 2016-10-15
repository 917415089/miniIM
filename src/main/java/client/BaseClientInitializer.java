package client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
@Deprecated
public class BaseClientInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// TODO Auto-generated method stub
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpClientCodec());
        // Remove the following line if you don't want automatic content decompression.
        p.addLast(new HttpContentDecompressor());
        // Uncomment the following line if you don't want to handle HttpContents.
        p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new BaseClientHandler());
	}

}
