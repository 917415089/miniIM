package client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.crypto.SecretKey;
import util.EnDeCryProcess;
import com.alibaba.fastjson.JSON;
import json.util.JSONNameandString;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;

public class BaseClient extends Thread {

	static final String HOST = System.getProperty("HOST", "127.0.0.1");
	static final int PORT =  Integer.parseInt(System.getProperty("port", "8080"));
	static final String URL = System.getProperty("url", "ws://127.0.0.1:8080/websocket");
	static final int QUEUE_LENGTH = 100;
	static public  final int RECEQUE_LENGTH = 100; 
	
	private BlockingQueue<JSONNameandString> sendque = ClientManage.getSendque();
	private BlockingQueue<JSONNameandString> receque = ClientManage.getReceque();
	
	private String userName;
	private String userPassword;
	private String userEmail;
	private SecretKey secretKey;


	public BaseClient(){
		
	}
	
	public BaseClient(String userName,String userPassword){
		this.userName =userName;
		this.userPassword = userPassword;
	}
	public BaseClient(String userName, String userPassword, String useremail) {
		super();
		this.userName = userName;
		this.userPassword = userPassword;
		this.userEmail = useremail;
	}

	@Override
	public void run() {
		EventLoopGroup group = new NioEventLoopGroup();
		
		try{
			ExecutorService DealWithReceQueThreadPool = Executors.newFixedThreadPool(2);
			DealWithReceQueThreadPool.submit(new DealWithReceQue(receque));
			DealWithReceQueThreadPool.submit(new DealWithReceQue(receque));
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
									uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()),receque);
			
			handler.setClient(this);
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
			
			Channel ch = b.connect(uri.getHost(),port).sync().channel();
			handler.handshakeFuture().sync();
			handler.getSession().setUserName(userName).setUserPassword(userPassword);
			if(userEmail!=null) handler.getSession().setUserEmail(userEmail);

			while(handler.getAccessHandler()==null || handler.getAccessHandler().getSecretKey()==null) Thread.sleep(1);//while bug
			secretKey = handler.getAccessHandler().getSecretKey();

			while(true){
				JSONNameandString msg = sendque.take();
				if(msg == null){
					break;
				}else if("json.client.access.ClosingChannel".equals(msg.getJSONName())) {
                    ch.writeAndFlush(new CloseWebSocketFrame());
                    ch.closeFuture().sync();
                    break;
                } else {
                	String send = JSON.toJSONString(msg);
                	System.out.println("Send:"+send+"in BaseClient 126 line");
            		send = EnDeCryProcess.SysKeyEncryWithBase64(send, secretKey);
                    WebSocketFrame frame = new TextWebSocketFrame(send);
                    ch.writeAndFlush(frame);
                }
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			group.shutdownGracefully();
		}
	}
	
}
