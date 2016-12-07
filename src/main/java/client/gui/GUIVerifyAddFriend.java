package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.alibaba.fastjson.JSON;
import client.ClientManage;
import json.client.session.AddFriend;
import json.client.session.AddFriendResult;
import json.util.JSONNameandString;

public class GUIVerifyAddFriend extends JFrame {

	private static final long serialVersionUID = -3747369813497310375L;
	private AddFriend requestjson;
	private final JComboBox<String> selectgroup = new JComboBox<String>();
	
	public GUIVerifyAddFriend(AddFriend addfriend){
		this.requestjson  = addfriend;
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		setSize(width/8, height/8);
		setLocation(width/8*3, height/8*3);
		setResizable(false);
		setLayout(new BorderLayout());
		setTitle("Add Friend");
		
		JPanel center = new JPanel();
		add(center,BorderLayout.CENTER);
		center.setLayout(new GridLayout(2,2));
		JLabel username = new JLabel("name",JLabel.CENTER);
		center.add(username);
		JLabel friendname = new JLabel(requestjson.getName(),JLabel.CENTER);
		center.add(friendname);
		JLabel group = new JLabel("group",JLabel.CENTER);
		center.add(group);
		for(String s : ClientManage.getMainwindow().getGroup())
			selectgroup.addItem(s);
		selectgroup.addItem("new Group");
		center.add(selectgroup);
		
		JPanel bottom = new JPanel();
		JButton yes = new JButton("yes");
		yes.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AddFriendResult addFriendResult = new AddFriendResult();
				addFriendResult.setReceiverestate(true);
				addFriendResult.setRequestorname(requestjson.getName());
				addFriendResult.setRequestorgroup(requestjson.getGroup());
				addFriendResult.setReceivername(requestjson.getFriendname());
				if(selectgroup.getSelectedItem().equals("new Group")){
					addFriendResult.setReceivergroup(JOptionPane.showInputDialog("input group name:"));
				}else{
					addFriendResult.setReceivergroup((String)(selectgroup.getSelectedItem()));
				}
				JSONNameandString json = new JSONNameandString();
				json.setJSONName(AddFriendResult.class.getName());
				json.setJSONStr(JSON.toJSONString(addFriendResult));
				ClientManage.addPathNode(addFriendResult.getReceivergroup()+"."+addFriendResult.getRequestorname());
				ClientManage.sendJSONNameandString(json);
				dispose();
			}
		});
		bottom.add(yes);
		JButton no = new JButton("no");
		no.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AddFriendResult addFriendResult = new AddFriendResult();
				addFriendResult.setReceiverestate(false);
				addFriendResult.setRequestorname(requestjson.getName());
				addFriendResult.setRequestorgroup(requestjson.getGroup());
				addFriendResult.setReceivername(requestjson.getFriendname());
				JSONNameandString json = new JSONNameandString();
				json.setJSONName(AddFriendResult.class.getName());
				json.setJSONStr(JSON.toJSONString(addFriendResult));
				ClientManage.sendJSONNameandString(json);
				dispose();
			}
		});
		bottom.add(no);
		add(bottom,BorderLayout.SOUTH);
		setVisible(true);
	}
	
	
}
