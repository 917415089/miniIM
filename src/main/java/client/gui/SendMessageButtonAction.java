package client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;

import com.alibaba.fastjson.JSON;

import client.ClientManage;
import json.client.session.SendMessage;
import json.util.JSONNameandString;

public class SendMessageButtonAction implements ActionListener {
	
	private String friendname;
	private JTextArea talkwindow;

	public SendMessageButtonAction(String friendname, JTextArea talkwindow) {
		this.friendname = friendname;
		this.talkwindow = talkwindow;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String sendstr = talkwindow.getText();
		SendMessage message = new SendMessage();
		message.setName(ClientManage.getName());
		message.setFriend(friendname);
		message.setMessage(sendstr);
		
		JSONNameandString json = new JSONNameandString();
		json.setJSONName(SendMessage.class.getName());
		json.setJSONStr(JSON.toJSONString(message));
		
		ClientManage.getSendque().offer(json);
	}

}
