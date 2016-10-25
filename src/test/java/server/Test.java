package server;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

import com.alibaba.fastjson.JSON;

import json.server.session.FriendList;
import json.util.JSONNameandString;
import server.db.StatementManager;

public class Test extends Thread {
	public static void main(String[] args) {
		Test test = new Test();
		test.start();
	}

	@Override
	public void run() {
		StatementManager.getService().submit(new Callable<JSONNameandString>(){
			@Override
			public JSONNameandString call() throws Exception {
				String sql = "Select * from friends where mastername = \""+"user1"+"\";";
				ResultSet set = StatementManager.getStatement().executeQuery(sql);
				FriendList list = new FriendList();
				List<String> friends = new ArrayList<String>();
				while(set.next()){
					String string = set.getString("friendname");
					friends.add(string);
				}
				list.setFriends(friends);
				JSONNameandString ret = new JSONNameandString();
				ret.setJSONName(FriendList.class.getName());
				ret.setJSONStr(JSON.toJSONString(list));
				return ret;
			}
		 });
	}
}
