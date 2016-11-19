package client;

import java.util.concurrent.BlockingQueue;
import javax.swing.JOptionPane;
import client.gui.LoginDialog;
import client.gui.MainWindow;
import com.alibaba.fastjson.JSON;
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
				default:
					System.err.println("can't deal "+take.getJSONName());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void dealwithSuccessLogin() {
		System.out.println("try to enable main windows");
		LoginDialog logindaialog = ClientManage.getLogindaialog();
		logindaialog.setVisible(false);
		MainWindow mainwindow = ClientManage.getMainwindow();
		mainwindow.setVisible(true);
	}

	private void dealwithVerifyAddFriend(String jsonStr) {
		System.out.println(jsonStr);
	}

	private void DealWithFriendList(String jsonStr) {
		FriendList friendList = JSON.parseObject(jsonStr, FriendList.class);
		System.out.println("FriendList:");
		for(FriendMeta s : friendList.getFriends()){
			System.out.println("      "+s.getName());
		}
		System.out.println("end of Friendlists");
		
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
