package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import com.alibaba.fastjson.JSON;
import client.ClientManage;
import json.client.access.ClosingChannel;
import json.client.session.SendMessage;
import json.util.JSONNameandString;
import util.Guaranty;

public class GUIMainWindow extends JFrame{

	private static final long serialVersionUID = 1L;
	public final static int BUTTUN_HEIGHT=27;
	private JTree friendTree;
	private DefaultMutableTreeNode root;
	private JPanel right;
	private JPanel center;
	private ConcurrentHashMap<String, GUISession> name2GUISession = new ConcurrentHashMap<String, GUISession>();

	public GUIMainWindow(){
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		setSize(width/2, height/2);
		setLocation(width/4, height/4);
		setResizable(false);
		setLayout(new BorderLayout());
		setTitle("main");
//		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JPanel left = new JPanel();
		left.setLayout(new BorderLayout());
		add(left,BorderLayout.WEST);
		
		root = new DefaultMutableTreeNode("Friends");
		friendTree = new JTree(root);
		friendTree.setVisible(true);
		JScrollPane friendlist = new JScrollPane(friendTree);
		friendlist.setPreferredSize(new Dimension(200,getHeight()-BUTTUN_HEIGHT));
		friendlist.setBounds(0, 0, 500, 500);
		friendlist.setBorder(BorderFactory.createTitledBorder("   Friend List   "));
		left.add(friendlist,BorderLayout.CENTER);
		JPanel buttonJpanel = new JPanel();
		buttonJpanel.setLayout(new GridLayout(2, 1));
		left.add(buttonJpanel, BorderLayout.SOUTH);
		JButton AddFriend = new JButton("Add Friend");
		buttonJpanel.add(AddFriend);
		AddFriend.addActionListener(new AddFriendButtun());
		JButton removeFriend = new JButton("RemoveFriend");
		buttonJpanel.add(removeFriend);
		removeFriend.addActionListener(new RemoveFriendButtun());
		
		right = new JPanel();
		add(right,BorderLayout.EAST);
		right.setBorder(BorderFactory.createTitledBorder("   Session List   "));
		right.setPreferredSize(new Dimension(200,getHeight()));
		BoxLayout boxLayout = new BoxLayout(right,BoxLayout.Y_AXIS);
		right.setLayout(boxLayout);
				
		center = new JPanel();
		center.setBorder(BorderFactory.createTitledBorder("   Working space   "));
		center.setLayout(new GridBagLayout());
		add(center,BorderLayout.CENTER);
		friendTree.addMouseListener(new DoubleClickonFriendTree());
	}
	
	/*
	 * public method 
	 */
	/**
	 * this method is used to add friend by specific group_username
	 * @param group_username
	 * @return
	 */
	@Guaranty("root")
	public DefaultMutableTreeNode addPathNode(String group_username) {
        String[] ns = group_username.split("\\.");
        synchronized (root) {
        	DefaultMutableTreeNode node = root;
            for (String n : ns) {
                int i = node.getChildCount() - 1;
                for (; i >= 0; i--) {
                	DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) node.getChildAt(i);
                    if (tmp.getUserObject().equals(n)) {
                        node = tmp;
                        break;
                    }
                }
                if (i < 0) {
                	DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(n);
                    node.add(tmp);
                    node = tmp;
                }
            }
            friendTree.expandPath(new TreePath(root));
            friendTree.updateUI();
            return node;
		}
    }
	/**
	 * this method is used to remove friend by specific group_username
	 * @param pathnode
	 * @return
	 */
	@Guaranty("root")
	@Deprecated
	public boolean removePathNode(String pathnode){
		String[] ns = pathnode.split("\\.");
		synchronized (root) {
			DefaultMutableTreeNode node = root;
	        for (String n : ns) {
	            int i = node.getChildCount() - 1;
	            for (; i >= 0; i--) {
	            	DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) node.getChildAt(i);
	                if (tmp.getUserObject().equals(n)) {
	                    tmp.remove(i);
	                    clearNode();
	                    friendTree.expandPath(new TreePath(root));
	                    friendTree.updateUI();
	                    return true;
	                }
	            }
	            if (i < 0) {
	            	return false;
	            }
	        }
	        return false;
		}
	}
	/**
	 * this method is used to remove friend by specific username
	 * @param username
	 */
	@Guaranty("root")
	public void rmPathNode(String username) {
		synchronized (root) {
			DefaultMutableTreeNode node = root;
			int len = node.getChildCount();
			for(int i = 0 ; i < len ;i++){
				DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
				int m = child.getChildCount();
				for(int j = 0 ; j < m ;j++){
					DefaultMutableTreeNode name = (DefaultMutableTreeNode)child.getChildAt(j);
					if(name.getUserObject().equals(username)){
						child.remove(j);
						if(child.getChildCount()==0){
							node.remove(i);
						}
						friendTree.expandPath(new TreePath(root));
						friendTree.updateUI();
						return;
					}
				}
			}
			friendTree.expandPath(new TreePath(root));
			friendTree.updateUI();
		}
	}
	@Guaranty("this")
	public synchronized void addSession(String friendname) {
		if(name2GUISession.containsKey(friendname))	return;
		GUISession guiSession = new GUISession(new GUIButtun(friendname),buildSessionwindow(friendname));
		guiSession.button.getClose().addActionListener(new CloseSession(friendname));
		guiSession.button.getSession().addActionListener(new SessionSwitch(friendname));
		center.removeAll();
		right.add(guiSession.button);
		center.add(guiSession.jpanel);
		name2GUISession.put(friendname, guiSession);
		center.updateUI();
		right.updateUI();
	}
	@Guaranty("this")
	public synchronized void rmSession(String friendname){
		right.remove(name2GUISession.get(friendname).button);
		center.remove(name2GUISession.get(friendname).jpanel);
		this.repaint();
		name2GUISession.remove(friendname);
	}
	@Guaranty("this")
	public synchronized void displayMessage(SendMessage sendmessage) {
		GUISession guiSession = name2GUISession.get(sendmessage.getName());
		if(guiSession==null){
			addSession(sendmessage.getName());
			guiSession = name2GUISession.get(sendmessage.getName());
		}
		JTextArea display = (JTextArea)guiSession.jpanel.getComponent(1);
		display.insert("\n"+sendmessage.getName()+":\n"+sendmessage.getMessage(), display.getText().length());
		center.removeAll();
		center.add(guiSession.jpanel);
		center.updateUI();
		guiSession.jpanel.updateUI();
	}
	@Guaranty("root")
	public List<String> getGroup(){
		synchronized (root) {
			int count = root.getChildCount();
			List<String> ret = new ArrayList<String>();
			for(int i = 0 ; i< count;i++){
				DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) root.getChildAt(i);
				ret.add((String)tmp.getUserObject());
			}
			return ret;			
		}		
	}
	@Override
	public void dispose(){
		System.out.println("pass");
		ClosingChannel close = new ClosingChannel();
		close.setReaseon("user clsoe mainwindow");
		JSONNameandString json = new JSONNameandString();
		json.setJSONName(ClosingChannel.class.getName());
		json.setJSONStr(JSON.toJSONString(close));
		ClientManage.sendJSONNameandString(json);
		super.dispose();
	}
	/*
	 * private method
	 */	
	private JPanel buildSessionwindow(String friendname){
		JPanel container = new JPanel();
		container.setLayout(new GridBagLayout());
		JLabel sessionLabel = new JLabel("talk with "+friendname);
		GridBagConstraints slc = new GridBagConstraints();
		slc.weightx=100;
		slc.weighty=100;
		slc.gridx=0;
		slc.gridy=0;
		slc.gridheight=1;
		slc.gridwidth=1;
		container.add(sessionLabel,slc);
		
		JTextArea sessionwindow = new JTextArea(100,100);
		sessionwindow.setBorder(BorderFactory.createTitledBorder("   Session window   "));
		sessionwindow.setMinimumSize(new Dimension(getWidth()-400,getHeight()/4*3-50));
		GridBagConstraints swc = new GridBagConstraints();
		swc.weightx=100;
		swc.weighty=100;
		swc.gridx=0;
		swc.gridy=1;
		swc.gridheight=1;
		swc.gridwidth=1;
		container.add(sessionwindow,swc);
				
		JTextArea talkwindow = new JTextArea(200,300);
		talkwindow.setBorder(BorderFactory.createTitledBorder("   talk window   "));
		talkwindow.setMinimumSize(new Dimension(getWidth()-400,getHeight()/4-50));
		GridBagConstraints twc = new GridBagConstraints();
		twc.weightx=100;
		twc.weighty=100;
		twc.gridx=0;
		twc.gridy=2;
		twc.gridheight=1;
		twc.gridwidth=1;
		container.add(talkwindow,twc);	
		
		JButton Enter = new JButton("Enter");
		GridBagConstraints enc = new GridBagConstraints();
		enc.weightx=100;
		enc.weighty=100;
		enc.gridx=0;
		enc.gridy=3;
		enc.gridheight=1;
		enc.gridwidth=1;
		container.add(Enter,enc);
		Enter.addActionListener(new SendMessageButtonAction(friendname,talkwindow));
		return container;
	}
	private void clearNode() {
        DefaultMutableTreeNode node = root;
        int i = node.getChildCount()-1;
        for(;i>=0;i--){
        	TreeNode tmp = node.getChildAt(i);
        	if(tmp.getChildCount()==0)
        		root.remove(i);
        }
	}

	
	/*
	 * private GUI component
	 */	
	private class GUISession{
		GUIButtun button;
		JPanel jpanel;
		public GUISession(GUIButtun button, JPanel jpanel) {
			super();
			this.button = button;
			this.jpanel = jpanel;
		}
	}
	
	

	/*
	 * private ActionListener
	 */
	private class AddFriendButtun implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			new GUIAddFriend();
		}
	}
	private class RemoveFriendButtun implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			new GUIRmFriend(root);
		}
	}
	private class CloseSession implements ActionListener{
		private String name;
		public CloseSession(String name) {
			this.name = name;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			rmSession(name);
			right.updateUI();
		}
	}
	/**
	 * this method is used to choose which session display in GUIMainwindow;
	 * @author zhaoch93
	 *
	 */
	private class SessionSwitch implements ActionListener{
		private String name;
		public SessionSwitch(String name){
			this.name =name;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			center.removeAll();
			center.add(name2GUISession.get(name).jpanel);
			center.updateUI();
		}
	}
	private class SendMessageButtonAction implements ActionListener {
		
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
			
			ClientManage.sendJSONNameandString(json);
			
			GUISession guiSession = name2GUISession.get(friendname);
			JTextArea display = (JTextArea)guiSession.jpanel.getComponent(1);
			display.insert("\n"+friendname+":\n"+sendstr, display.getText().length());
			guiSession.jpanel.updateUI();
			talkwindow.setText("");
		}

	}

	
	/*
	 * private ActionAdapter
	 */	
	private class DoubleClickonFriendTree extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			int selRow = friendTree.getRowForLocation(e.getX(), e.getY());
			TreePath selPath = friendTree.getPathForLocation(e.getX(), e.getY());
			if(selRow != -1) {
				if(e.getClickCount() == 2) {
					if(selPath.getPathCount()==3)
					addSession(selPath.getLastPathComponent().toString());
				}
			}
		}
	}
}

