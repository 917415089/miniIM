package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import com.alibaba.fastjson.JSON;
import json.client.session.JSONMessage;
import util.EnDeCryProcess;
import client.session.MessageFactory;

public class FriendSessionTest {
	public static void main(String[] args) throws Exception {

		KeyGenerator instance = KeyGenerator.getInstance("AES");
		instance.init(128);
		SecretKey secretKey =instance.generateKey();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		MessageFactory messageFactory = new MessageFactory();
		messageFactory.setSecretKey(secretKey);
		String msg = bufferedReader.readLine();
		String send = messageFactory.product(msg);
		String receive = EnDeCryProcess.SysKeyDecryWithBase64(send, secretKey);
		JSONMessage jsons = JSON.parseObject(receive,JSONMessage.class);
		System.out.println(jsons);
		
	} 
}
