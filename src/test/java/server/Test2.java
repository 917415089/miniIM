package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Test2 {
	public static void main(String[] args) throws IOException {
		final BASE64Encoder base64Encoder = new BASE64Encoder();
		final BASE64Decoder base64decoder = new BASE64Decoder();
		
		String a = "aaaa";
		String encode = base64Encoder.encode(a.getBytes());
		System.out.println(encode);
		String ret = new String(base64decoder.decodeBuffer(encode));
		System.out.println(ret);
		
	}
}
