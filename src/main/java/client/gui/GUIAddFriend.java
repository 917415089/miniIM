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
import javax.swing.JTextField;
import com.alibaba.fastjson.JSON;
import client.ClientManage;
import json.client.session.AddFriend;
import json.util.JSONNameandString;

public class GUIAddFriend extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private JComboBox<String> box = new JComboBox<String>();
	private JTextField inputname;
	
	public GUIAddFriend() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		setSize(width/8, height/8);
		setLocation(width/8*3, height/8*3);
		setResizable(false);
		setLayout(new BorderLayout());
		setTitle("Add Friend");
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel center = new JPanel();
		add(center,BorderLayout.CENTER);
		center.setLayout(new GridLayout(2,2));
		JLabel group = new JLabel("group",JLabel.CENTER);
		center.add(group);
		for(String s : ClientManage.getMainwindow().getGroup())
			box.addItem(s);
		box.addItem("new Group");
		center.add(box);
		JLabel name = new JLabel("name",JLabel.CENTER);
		center.add(name);
		inputname = new JTextField();
		center.add(inputname);
		
		JButton enter = new JButton("Enter");
		add(enter,BorderLayout.SOUTH);
		
		enter.addActionListener( new AddFriendListener());
		setVisible(true);
	}
	
	private class AddFriendListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			AddFriend friend = new AddFriend();
			friend.setFriendname(inputname.getText());
			if(box.getSelectedItem().equals("new Group")){
				friend.setGroup(JOptionPane.showInputDialog("input group name:"));
			}else{
				friend.setGroup((String)(box.getSelectedItem()));
			}
			friend.setName(ClientManage.getName());
			if(friend.getFriendname().equals(ClientManage.getName())){
				JOptionPane.showMessageDialog(null, "invalid username");
				dispose();
				return;
			}
/*			if(friend.getGroup()==null){
				JOptionPane.showInputDialog("musthave group");
			}*/
			
			JSONNameandString json = new JSONNameandString();
			json.setJSONName(AddFriend.class.getName());
			json.setJSONStr(JSON.toJSONString(friend));
			
			ClientManage.sendJSONNameandString(json);
			dispose();
			//unfinished
			
		}
		
	}
}
