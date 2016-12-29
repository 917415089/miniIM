package server;

import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class BaseServer implements Runnable {
	
    static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));
    
	@Override
	public void run() {
		final ThreadFactory AcceptName = new ThreadFactoryBuilder()
			    .setNameFormat("Accpect-%d")
			    .build();
		EventLoopGroup boss = new NioEventLoopGroup(1,AcceptName);
		final ThreadFactory Workername = new ThreadFactoryBuilder()
			    .setNameFormat("NIOWorkThread-%d")
			    .build();
		EventLoopGroup worker = new NioEventLoopGroup(0,Workername);
		
		try{
			ServerBootstrap boot = new ServerBootstrap();
			boot.group(boss, worker)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new BaseServerInitializer());
			
			boot.bind(PORT).sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
		
	}
}
