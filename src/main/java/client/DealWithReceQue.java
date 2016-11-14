package client;

import java.util.concurrent.BlockingQueue;

import com.alibaba.fastjson.JSON;

import json.server.login.RegisiterResult;
import json.server.session.FriendList;
import json.server.session.VerifyAddFriend;
import json.util.JSONNameandString;

public class DealWithReceQue implements Runnable{

    private BlockingQueue<JSONNameandString> receque;
    
	public DealWithReceQue(BlockingQueue<JSONNameandString> receque) {
		super();
		this.receque = receque;
	}

	@Override
	public void run() {
		while(true){
			try {
				JSONNameandString take = receque.take();
				switch(take.getJSONName()){
				case "json.server.login.WrongNameorPassword":
					dealwithWrongNameorPassword(take.getJSONStr());
					break;
				case "json.server.session.FriendList":
					DealWithFriendList(take.getJSONStr());
					break;
				case "json.server.login.RegisiterResult"://json.server.login.RegisiterResult
					dealwithRegisterResult(take.getJSONStr());
					break;
				case "json.server.session.VerifyAddFriend":
					dealwithVerifyAddFriend(take.getJSONStr());
				default:
					System.err.println("can't deal "+take.getJSONName());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void dealwithVerifyAddFriend(String jsonStr) {
//		VerifyAddFriend verifyAddFriend = JSON.parseObject(jsonStr, VerifyAddFriend.class);
		
		System.out.println(jsonStr);
		
	}

	private void DealWithFriendList(String jsonStr) {
		FriendList friendList = JSON.parseObject(jsonStr, FriendList.class);
		System.out.println("FriendList:");
		for(String s : friendList.getFriends()){
			System.out.println("      "+s);
		}
		System.out.println("end of Friendlists");
		
	}

	private void dealwithRegisterResult(String jsonStr) {
		RegisiterResult regisiterResult = JSON.parseObject(jsonStr, RegisiterResult.class);
		if (regisiterResult.isSuccess()) {
			System.out.println("Regisiter successfully");
		}else{
			System.out.println("Can't register : ");
			System.out.println(regisiterResult.getReason());
		}
	}

	private void dealwithWrongNameorPassword(String jsonStr) {
		System.out.println("send wrong name or password message to gui");
		//this is not a good idea cause this method handle similar work as ClientSeesion, but I can't find a better idea;
	}
	
}
