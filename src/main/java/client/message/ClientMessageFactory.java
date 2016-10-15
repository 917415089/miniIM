package client.message;

import io.netty.handler.codec.http.HttpRequest;
@Deprecated
public interface ClientMessageFactory {

	public boolean addUri(String parturi);
	
	public boolean addContent(String con);
	
	public boolean setHeaders(CharSequence name, Object value);
	
	public HttpRequest product();
}
