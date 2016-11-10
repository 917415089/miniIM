package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.crypto.SecretKey;
import util.EnDeCryProcess;
import com.alibaba.fastjson.JSON;
import json.util.JSONNameandString;
import client.session.MessageFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
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
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
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
	
	private BlockingQueue<JSONNameandString> sendque = new ArrayBlockingQueue<JSONNameandString>(QUEUE_LENGTH);
	private BlockingQueue<JSONNameandString> receque = new ArrayBlockingQueue<JSONNameandString>(RECEQUE_LENGTH);
	
	private String RegisterName;
	private String RegisterPassword;
	private String RegisterEmail;
	
	private String userName;
	private String userPassword;
	private String userEmail;
	private SecretKey secretKey;
	private MyWebSocketClientHandler handler;


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

	public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException{
		BaseClient baseClient = new BaseClient();
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
		
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			final MyWebSocketClientHandler handler = 
					new MyWebSocketClientHandler(
							WebSocketClientHandshakerFactory.newHandshaker(
									uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()),baseClient.receque);

			baseClient.handler =  handler;
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

			handler.getSession().setUserName("user1").setUserPassword("123");
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
			MessageFactory messageFactory = new MessageFactory();


			while(handler.getAccessHandler()==null || handler.getAccessHandler().getSecretKey()==null) Thread.sleep(1);//while bug
    		messageFactory.setSecretKey(handler.getAccessHandler().getSecretKey());

			while(true){
				String msg = console.readLine();
				if(msg == null){
					break;
				}else if("bye".equals(msg.toLowerCase())) {
                    ch.writeAndFlush(new CloseWebSocketFrame());
                    ch.closeFuture().sync();
                    break;
                } else if ("ping".equals(msg.toLowerCase())) {
                    WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 }));
                    ch.writeAndFlush(frame);
                } else {
                	if(!handler.getSession().isHasLogin()){
                		System.out.println("pass");
                		String str = handler.getSession().setSecretKey(handler.getAccessHandler().getSecretKey()).login();
                		ch.writeAndFlush(new TextWebSocketFrame(str));
                		while( handler.getSession().isHasLogin());
                		messageFactory.setSecretKey(handler.getAccessHandler().getSecretKey());
                	}
                		String send = messageFactory.product(msg);
	                    WebSocketFrame frame = new TextWebSocketFrame(send);
	                    ch.writeAndFlush(frame);
	                    System.out.println(msg);
                }
			}
		}finally{
			group.shutdownGracefully();
		}
	}

	@Override
	public void run() {
		EventLoopGroup group = new NioEventLoopGroup();
		
		try{
			Thread thread = new Thread(new DealWithReceQue(receque));
			thread.start();
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
			
			this.handler = handler;
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
                	System.out.println(send);
            		send = EnDeCryProcess.SysKeyEncryWithBase64(send, secretKey);
                    WebSocketFrame frame = new TextWebSocketFrame(send);
                    ch.writeAndFlush(frame);
                    System.out.println(msg);
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

	public BlockingQueue<JSONNameandString> getSendque() {
		return sendque;
	}

	public void setSendque(BlockingQueue<JSONNameandString> sendque) {
		this.sendque = sendque;
	}

	public BlockingQueue<JSONNameandString> getReceque() {
		return receque;
	}

	public String getRegisterName() {
		return RegisterName;
	}

	public void setRegisterName(String registerName) {
		RegisterName = registerName;
	}

	public String getRegisterPassword() {
		return RegisterPassword;
	}

	public void setRegisterPassword(String registerPassword) {
		RegisterPassword = registerPassword;
	}

	public String getRegisterEmail() {
		return RegisterEmail;
	}

	public void setRegisterEmail(String registerEmail) {
		RegisterEmail = registerEmail;
	}
	
	
}
