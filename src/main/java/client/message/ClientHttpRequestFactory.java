package client.message;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import java.net.URI;
import java.net.URISyntaxException;

public class ClientHttpRequestFactory implements ClientMessageFactory {
	
	static final String DEFAULT_URI = "http://127.0.0.1:8008/";
	

	private String scheme = null;
	private String host = null;
	private int port = -1;
	private URI uri;
	private FullHttpRequest request;

	public ClientHttpRequestFactory() throws URISyntaxException{
		this(DEFAULT_URI);
	}
	
	public ClientHttpRequestFactory(String strURI) throws URISyntaxException{
			this(strURI==null?new URI(DEFAULT_URI):new URI(strURI));
	}
	
	public ClientHttpRequestFactory(URI uri){
		this.uri = uri;
		scheme = uri.getScheme() == null? "http" : uri.getScheme();
		host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
		port = uri.getPort();
		if (port == -1) {
		    if ("http".equalsIgnoreCase(scheme)) {
		        port = 80;
		    } else if ("https".equalsIgnoreCase(scheme)) {
		        port = 443;
		    }
		}
		if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
		    System.err.println("Only HTTP(S) is supported.");
		}
		request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getSchemeSpecificPart().substring((uri.getHost()+uri.getPort()).length()+3));
		request.headers().set(HttpHeaderNames.HOST, host);
		request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
		request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
	}
	
	public HttpRequest product(){
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
		return request;
	}

	public ClientHttpRequestFactory add(String string) {
		uri = URI.create(scheme+"://"+host+":"+port+"/"+string);
		return this;
	}

	@Override
	public boolean addUri(String parturi) {
		uri = URI.create(scheme+"://"+host+":"+port+"/"+parturi);
		request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getSchemeSpecificPart().substring((uri.getHost()+uri.getPort()).length()+3));
		return false;
	}

	@Override
	public boolean addContent(String con) {
		request.content().writeBytes(con.getBytes());
		return false;
	}
	
	public boolean addContent(byte[] con) {
		request.content().writeBytes(con);
		return false;
	}
	
	@Override
	public boolean setHeaders(CharSequence name, Object value) {
		request.headers().set(name, value);
		return false;
	}
}
