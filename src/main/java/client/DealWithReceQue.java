package client;

import java.util.concurrent.BlockingQueue;

import javax.swing.JOptionPane;

import org.junit.runner.Request;

import client.gui.LoginDialog;
import client.gui.MainWindow;

import com.alibaba.fastjson.JSON;

import json.client.session.RequestFriendList;
import json.client.session.SendMessage;
import json.server.login.RegisiterResult;
import json.server.session.FriendList;
import json.server.session.FriendMeta;
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
					break;
				case "json.server.login.SuccessLogin":
					dealwithSuccessLogin();
					break;
				case "json.client.session.SendMessage":
					dealwithSendMessage(take.getJSONStr());
					break;
				default:
					System.err.println("can't deal "+take.getJSONName());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		requestFriendList.setGroup("all");
		JSONNameandString json = new JSONNameandString();
		json.setJSONName(RequestFriendList.class.getName());
		json.setJSONStr(JSON.toJSONString(requestFriendList));
		ClientManage.getSendque().offer(json);
	}

	private void dealwithVerifyAddFriend(String jsonStr) {
		System.out.println(jsonStr);
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
