package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class MainWindow extends JFrame implements Runnable{

	public MainWindow(){
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		setSize(width/2, height/2);
		setLocation(width/4, height/4);
		setResizable(false);
		setLayout(new BorderLayout());
		
		JPanel left = new JPanel();
		left.setLayout(new BorderLayout());
		add(left,BorderLayout.WEST);
		
		DefaultMutableTreeNode all = new DefaultMutableTreeNode("default");
		all.add(new DefaultMutableTreeNode("test1"));
		all.add(new DefaultMutableTreeNode("test2"));
		JTree jTree = new JTree(all);
		jTree.setVisible(true);
		JScrollPane friendlist = new JScrollPane(jTree);
		friendlist.setBorder(BorderFactory.createTitledBorder("   Friend List   "));
		left.add(friendlist,BorderLayout.NORTH);
		
		JButton AddFriend = new JButton("Add Friend");

		left.add(AddFriend,BorderLayout.SOUTH);
		JPanel right = new JPanel();
		right.setBorder(BorderFactory.createTitledBorder("   Session List   "));
		add(right,BorderLayout.EAST);
		BoxLayout boxLayout = new BoxLayout(right,BoxLayout.Y_AXIS);
		right.setLayout(boxLayout);
		//fill test;
		JButton session1 = new JButton("Session1");
		right.add(session1);
		right.add(new JButton("Session2"));
		right.remove(session1);
		
		JPanel center = new JPanel();
		center.setBorder(BorderFactory.createTitledBorder("   Working space   "));
		add(center,BorderLayout.CENTER);
	}

	@Override
	public void run() {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				MainWindow mainJFrame = new MainWindow();
				GUIManage.getUniqueGUIManage().setMainwindow(mainJFrame);
				mainJFrame.setTitle("main");
				mainJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainJFrame.setVisible(true);
				
			}
		});
	}
	
	public static void main(String[] args) {
		Thread thread = new Thread(new MainWindow());
		thread.start();
	}
}

class Friend{
	private String name;
	private String group;
	
	public Friend(String name){
		this.name=name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
}