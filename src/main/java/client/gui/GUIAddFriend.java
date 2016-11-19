package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUIAddFriend extends JFrame{

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
		JTextField inputgroup = new JTextField();
		center.add(inputgroup);
		JLabel name = new JLabel("name",JLabel.CENTER);
		center.add(name);
		JTextField inputname = new JTextField();
		center.add(inputname);
		
		JButton enter = new JButton("Enter");
		add(enter,BorderLayout.SOUTH);
		
		setVisible(true);
	}
}
