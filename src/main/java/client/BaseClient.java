package client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;

public class BaseClient extends Thread {

	static final String HOST = System.getProperty("HOST", "127.0.0.1");
	static final int PORT =  Integer.parseInt(System.getProperty("port", "8080"));
	static final String URL = System.getProperty("url", "ws://127.0.0.1:8080/websocket");
	static final int QUEUE_LENGTH = 100;
	static public  final int RECEQUE_LENGTH = 100; 
	

	
	@Override
	public void run() {
		final ThreadFactory threadname = new ThreadFactoryBuilder().setNameFormat("NIOThread-%d").build();
		EventLoopGroup group = new NioEventLoopGroup(0,threadname);
		
		try{

			URI uri = new URI(URL);
			String scheme = uri.getScheme() ==null?"ws":uri.getScheme();
	//		final String host = uri.getHost() == null ?"127.0.0.1":uri.getHost();
			final int port;
			if(uri.getPort() == -1){
				if("ws".equalsIgnoreCase(scheme)){
					port = 80;
				}else{
					port = -1;
				}
			}else{
				port = uri.getPort();
			}
			
			if(!"ws".equalsIgnoreCase(scheme)){
				System.err.println("Only WS(S) is supported");
				return;
			}
			
			final MyWebSocketClientHandler handler = 
					new MyWebSocketClientHandler(
							WebSocketClientHandshakerFactory.newHandshaker(
									uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()),ClientManage.getReceque());
			

			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					p.addLast(
							new HttpClientCodec(),
							new HttpObjectAggregator(8192),
							WebSocketClientCompressionHandler.INSTANCE,
							handler);
				}
			});
			b.connect(uri.getHost(),port).sync();
			handler.handshakeFuture().sync();
			Object object = new Object();
			object.wait();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			group.shutdownGracefully();
		}
	}

}
