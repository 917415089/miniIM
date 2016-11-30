package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.ClientManage;

public class GUIVerifyAddFriend extends JFrame {

	private String name;
	
	public GUIVerifyAddFriend(String name){
		this.name  = name;
		
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
		JLabel friendname = new JLabel(name,JLabel.CENTER);
		center.add(friendname);
		JLabel group = new JLabel("group",JLabel.CENTER);
		center.add(group);
		JComboBox selectgroup = new JComboBox();
		for(String s : ClientManage.getMainwindow().getGroup())
			selectgroup.addItem(s);
		center.add(selectgroup);
		
		JPanel bottom = new JPanel();
		JButton yes = new JButton("yes");
		bottom.add(yes);
		JButton no = new JButton("no");
		bottom.add(no);
		add(bottom,BorderLayout.SOUTH);
		setVisible(true);
	}
	
	
}
