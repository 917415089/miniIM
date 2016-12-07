package client;

import java.util.concurrent.BlockingQueue;
import javax.swing.JOptionPane;
import client.gui.GUIVerifyAddFriend;
import client.gui.LoginDialog;
import com.alibaba.fastjson.JSON;
import json.client.session.AddFriend;
import json.client.session.AddFriendResult;
import json.client.session.RequestFriendList;
import json.client.session.SendMessage;
import json.server.login.RegisiterResult;
import json.server.session.FriendList;
import json.server.session.FriendMeta;
import json.server.session.RemoveFriendResult;
import json.server.session.RmFriendSlid;
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
				System.out.println("receive json"+take.getJSONStr()+"(in DealWithReceQue 32 line)");
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
				case "json.client.session.AddFriend":
					dealwithAddFriend(take.getJSONStr());
					break;
				case "json.server.login.SuccessLogin":
					dealwithSuccessLogin();
					break;
				case "json.client.session.SendMessage":
					dealwithSendMessage(take.getJSONStr());
					break;
				case "json.client.session.AddFriendResult":
					dealwithAddFriendResult(take.getJSONStr());
					break;
				case "json.server.session.RemoveFriendResult":
					dealwithRemoveFriendResult(take.getJSONStr());
					break;
				case "json.server.session.RmFriendSlid":
					dealwithRmFriendSlid(take.getJSONStr());
					break;
				default:
					System.err.println("Client : can't deal "+take.getJSONName());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void dealwithRmFriendSlid(String jsonStr) {
		RmFriendSlid slid = JSON.parseObject(jsonStr, RmFriendSlid.class);
		ClientManage.rmPathNode(slid.getName());
		
	}

	private void dealwithRemoveFriendResult(String jsonStr) {
		RemoveFriendResult result = JSON.parseObject(jsonStr, RemoveFriendResult.class);
		ClientManage.rmPathNode(result.getName());
		
	}

	private void dealwithAddFriendResult(String jsonStr) {
		AddFriendResult friendResult = JSON.parseObject(jsonStr, AddFriendResult.class);
		if(friendResult.isReceiverestate()){
			ClientManage.addPathNode(friendResult.getRequestorgroup()+"."+friendResult.getReceivername());
			JOptionPane.showMessageDialog(null, "new friend:"+friendResult.getReceivername()+"\ngroup:"+friendResult.getRequestorgroup());			
		}else{
			JOptionPane.showMessageDialog(null, "Adding Friend request was rejected by "+friendResult.getReceivername());
		}
		
	}

	private void dealwithSendMessage(String jsonStr) {
		SendMessage sendmessage = JSON.parseObject(jsonStr, SendMessage.class);
		ClientManage.displayMessage(sendmessage);
	}

	private void dealwithSuccessLogin() {
		System.out.println("try to enable main windows");
		LoginDialog logindaialog = ClientManage.getLogindaialog();
		logindaialog.setVisible(false);
		ClientManage.setMainWindowVisible(true);
		RequestFriendList requestFriendList = new RequestFriendList();
		requestFriendList.setGroup("Friends");
		JSONNameandString json = new JSONNameandString();
		json.setJSONName(RequestFriendList.class.getName());
		json.setJSONStr(JSON.toJSONString(requestFriendList));
		ClientManage.sendJSONNameandString(json);
	}

	private void dealwithAddFriend(String jsonStr) {
		AddFriend addfriend = JSON.parseObject(jsonStr, AddFriend.class);
		@SuppressWarnings("unused")
		GUIVerifyAddFriend verifyAddFriend = new GUIVerifyAddFriend(addfriend);
	}

	private void DealWithFriendList(String jsonStr) {
		FriendList friendList = JSON.parseObject(jsonStr, FriendList.class);
		for( FriendMeta fm : friendList.getFriends()){
			ClientManage.addPathNode(fm.getGroup()+"."+fm.getName());
		}
	}

	private void dealwithRegisterResult(String jsonStr) {
		RegisiterResult regisiterResult = JSON.parseObject(jsonStr, RegisiterResult.class);
		if (regisiterResult.isSuccess()) {
			JOptionPane.showMessageDialog(null, "register successfully");
		}else{
			JOptionPane.showMessageDialog(null, "Can't register : "+regisiterResult.getReason());
		}
	}

	private void dealwithWrongNameorPassword(String jsonStr) {
		System.out.println("send wrong name or password message to gui");
		JOptionPane.showMessageDialog(null, "Wrong name or password");
		ClientManage.getLogindaialog().setEnabled(true);
	}
	
}
