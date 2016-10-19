package server;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

import json.client.session.AddFriend;
import json.util.JSONNameandString;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RefelctTest {
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		AddFriend friend = new AddFriend();
		List<String> list = new ArrayList<String>();
		list.add("user2");
		list.add("user2");
		list.add("user3");
		list.add("user4");
		friend.setFriends(list);
		JSONNameandString json = new JSONNameandString();
		json.setJSONName(friend.getClass().getName());
		json.setJSONStr(JSON.toJSONString(friend));
		
		String jsonstr = JSON.toJSONString(json);
		
		JSONNameandString receive = JSON.parseObject(jsonstr,JSONNameandString.class);
		Class class1 = Class.forName(receive.getJSONName());
		Object rebuild =JSON.parseObject(receive.getJSONStr(),class1);
		
	}
}
