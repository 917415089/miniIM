package client.gui;

import java.awt.AWTEvent;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
import json.client.session.SendGroupMessage;
import json.client.session.SendMessage;
import json.util.JSONNameandString;
import util.Guaranty;

public class GUIMainWindow extends JFrame{

	private static final long serialVersionUID = 1L;
	public final static int BUTTUN_HEIGHT=27;
	private final JTree friendTree;
	private final DefaultMutableTreeNode root;
	private final JPanel right;
	private final JPanel center;
	private final ConcurrentHashMap<List<String>, GUISession> name2GUISession = new ConcurrentHashMap<List<String>, GUISession>();
	private final AtomicBoolean isClosing = new AtomicBoolean(false);
	private final Lock displaymessage = new ReentrantLock();

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
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		
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
		//buttonJpanel button area
		JPanel buttonJpanel = new JPanel();
		buttonJpanel.setLayout(new GridLayout(3, 1));
		left.add(buttonJpanel, BorderLayout.SOUTH);
		JButton AddFriend = new JButton("Add Friend");
		buttonJpanel.add(AddFriend);
		AddFriend.addActionListener(new AddFriendButtun());
		JButton removeFriend = new JButton("RemoveFriend");
		buttonJpanel.add(removeFriend);
		removeFriend.addActionListener(new RemoveFriendButtun());
		JButton grouptalking = new JButton("grouptalking");
		buttonJpanel.add(grouptalking);
		grouptalking.addActionListener(new GroupTalkingButton());
		
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
		synchronized (root) {
			String[] ns = group_username.split("\\.");
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
	@Guaranty("displaymessage")
	public  void addSession(String friendname) {
		displaymessage.lock();
		try{
			List<String> list  = new ArrayList<>();
			list.add(friendname);
			if(name2GUISession.containsKey(list))	return;
			GUIButtun guiButtun = new GUIButtun(friendname);
			JPanel buildSessionwindow = buildSessionwindow(friendname);
			GUISession guiSession = new GUISession(guiButtun,buildSessionwindow);
			guiSession.button.getClose().addActionListener(new CloseSession(friendname));
			guiSession.button.getSession().addActionListener(new SessionSwitch(friendname));
			center.removeAll();
			right.add(guiSession.button);
			center.add(guiSession.jpanel);
			name2GUISession.put(list, guiSession);
			center.updateUI();
			right.updateUI();
		}finally{
			displaymessage.unlock();
		}
	}
	@Guaranty("displaymessage")
	public  void addSession(List<String> friendname) {
		displaymessage.lock();
		try{
			if(name2GUISession.containsKey(friendname))	return;
			String str = "";
			for(String s : friendname)
				str+= s+"  ";
			str=str.trim();
			GUIButtun guiButtun = new GUIButtun(str);
			JPanel buildSessionwindow = buildSessionwindow(friendname);
			GUISession guiSession = new GUISession(guiButtun,buildSessionwindow);
			guiSession.button.getClose().addActionListener(new CloseSession(str));
			guiSession.button.getSession().addActionListener(new SessionSwitch(str));
			center.removeAll();
			right.add(guiSession.button);
			center.add(guiSession.jpanel);

			name2GUISession.put(friendname, guiSession);
			center.updateUI();
			right.updateUI();
		}finally{
			displaymessage.unlock();
		}
	}
	@Guaranty("this")
	public synchronized void rmSession(String friendname){
		List<String> list = new ArrayList<>();
		list.add(friendname);
		right.remove(name2GUISession.get(list).button);
		center.remove(name2GUISession.get(list).jpanel);
		this.repaint();
		name2GUISession.remove(list);
	}
	public void displayMessage(SendGroupMessage groupMessage) {
		displaymessage.lock();
		try{
			GUISession guiSession = name2GUISession.get(groupMessage.getFriendlist());
			if(guiSession==null){
				addSession(groupMessage.getFriendlist());
				guiSession = name2GUISession.get(groupMessage.getFriendlist());
			}
			JTextArea display = (JTextArea)guiSession.jpanel.getComponent(1);
			display.insert("\n"+groupMessage.getName()+":\n"+groupMessage.getMessage(), display.getText().length());
			center.removeAll();
			center.add(guiSession.jpanel);
			center.updateUI();
			guiSession.jpanel.updateUI();		
		}finally{
			displaymessage.unlock();
		}
		
	}
	@Guaranty("displaymessage")
	public  void displayMessage(SendMessage sendmessage) {
		displaymessage.lock();
		try {
			List<String> list = new ArrayList<>();
			list.add(sendmessage.getName());
			GUISession guiSession = name2GUISession.get(list);
			if(guiSession==null){
				addSession(sendmessage.getName());
				guiSession = name2GUISession.get(list);
			}
			JTextArea display = (JTextArea)guiSession.jpanel.getComponent(1);
			display.insert("\n"+sendmessage.getName()+":\n"+sendmessage.getMessage(), display.getText().length());
			center.removeAll();
			center.add(guiSession.jpanel);
			center.updateUI();
			guiSession.jpanel.updateUI();		
		}finally{
			displaymessage.unlock();
		}
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
	/*
	 * protected
	 */
	protected void processWindowEvent(final WindowEvent pEvent) {
		if(pEvent.getID()==WindowEvent.WINDOW_CLOSING){
			if(!isClosing.get()) isClosing.set(true);
			else return ;
			ClosingChannel close = new ClosingChannel();
			close.setReaseon("user clsoe mainwindow");
			JSONNameandString json = new JSONNameandString();
			json.setJSONName(ClosingChannel.class.getName());
			json.setJSONStr(JSON.toJSONString(close));
			ClientManage.sendJSONNameandString(json);
			ClientManage.waiteforclose();
			dispose();
			System.exit(0);
		}else {
			super.processWindowEvent(pEvent);
		}
	}
	/*
	 * private method
	 */
	@Guaranty("displaymessage")
	private  JPanel buildSessionwindow(String friendname){
		displaymessage.lock();
		try{
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
		}finally{
			displaymessage.unlock();
		}
	}
	@Guaranty("displaymessage")
	private  JPanel buildSessionwindow(List<String> friendname){
		displaymessage.lock();
		try{
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
		}finally{
			displaymessage.unlock();
		}
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
	private class GroupTalkingButton implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			new GUIGrouptalking();
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
			this.name=name;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			center.removeAll();
			List<String> list = new ArrayList<>();
			list.add(name);
			center.add(name2GUISession.get(list).jpanel);
			center.updateUI();
		}
	}
	private class SendMessageButtonAction implements ActionListener {
		
		private final List<String> friendlist;
		private JTextArea talkwindow;
		private final boolean group;

		public SendMessageButtonAction(String friendname, JTextArea talkwindow) {
			ArrayList<String> list = new ArrayList<String>();
			list.add(friendname);
			friendlist = list;
			this.talkwindow = talkwindow;
			group=false;
		}

		public SendMessageButtonAction(List<String> friendlist, JTextArea talkwindow) {
			Collections.sort(friendlist);
			this.friendlist = friendlist;
			this.talkwindow = talkwindow;
			group = true;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			String sendstr = talkwindow.getText();
			
			if(!group){
				SendMessage message = new SendMessage();
				message.setName(ClientManage.getName());
				message.setFriend(friendlist.get(0));
				message.setMessage(sendstr);
				JSONNameandString json = new JSONNameandString();
				json.setJSONName(SendMessage.class.getName());
				json.setJSONStr(JSON.toJSONString(message));
				ClientManage.sendJSONNameandString(json);
			}else{
				SendGroupMessage groupMessage = new SendGroupMessage();
				groupMessage.setName(ClientManage.getName());
				groupMessage.setFriendlist(friendlist);
				groupMessage.setMessage(sendstr);
				JSONNameandString json = new JSONNameandString();
				json.setJSONName(SendGroupMessage.class.getName());
				json.setJSONStr(JSON.toJSONString(groupMessage));
				ClientManage.sendJSONNameandString(json);
			}
			GUISession guiSession = name2GUISession.get(friendlist);
			JTextArea display = (JTextArea)guiSession.jpanel.getComponent(1);
			display.insert("\n"+ClientManage.getName()+":\n"+sendstr, display.getText().length());
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

	/*
	 * private GUIModel
	 */
	private class GUIGrouptalking extends JFrame {

		private static final long serialVersionUID = -1834553272122923159L;
		
		public GUIGrouptalking() {
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension screenSize = kit.getScreenSize();
			int height = screenSize.height;
			int width = screenSize.width;
			setSize(width/4, height/4);
			setLocation(width/8*3, height/8*3);
			setResizable(false);
			BorderLayout totallayout = new BorderLayout();
			totallayout.setHgap(10);
			totallayout.setVgap(10);
			setLayout(totallayout);
			setTitle("Grouptalking");
			
			final JPanel center = new JPanel();
			add(center,BorderLayout.CENTER);
			GridLayout gridLayout = new GridLayout(1,2);
			gridLayout.setVgap(5);
			gridLayout.setHgap(5);
			center.setLayout(gridLayout);
			final JTree friendTree = new JTree(root);
			JScrollPane friendpane = new JScrollPane(friendTree);
			center.add(friendpane);
			final DefaultMutableTreeNode talkinglist = new DefaultMutableTreeNode("talkingwith...");
			final JTree talkingpan = new JTree(talkinglist);
			center.add(talkingpan);
			
			friendTree.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					int selRow = friendTree.getRowForLocation(e.getX(), e.getY());
					TreePath selPath = friendTree.getPathForLocation(e.getX(), e.getY());
					if(selRow != -1) {
						if(e.getClickCount() == 2) {
							if(selPath.getPathCount()==3)
								talkinglist.add(new DefaultMutableTreeNode(selPath.getLastPathComponent().toString()));
								talkingpan.expandPath(new TreePath(talkinglist));
								talkingpan.updateUI();
						}
					}
				}
			});
			
			talkingpan.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					int selRow = talkingpan.getRowForLocation(e.getX(), e.getY());
					TreePath selPath = talkingpan.getPathForLocation(e.getX(), e.getY());
					if(selRow != -1) {
						if(e.getClickCount() == 2) {
							if(selPath.getPathCount()==3)
								System.out.println("click"+selPath.getLastPathComponent().toString());
								String name = selPath.getLastPathComponent().toString();
								int count = talkinglist.getChildCount();
								for(int i = 0 ; i < count;i++){
									 if(((DefaultMutableTreeNode)talkinglist.getChildAt(i)).getUserObject().equals(name)){
										 talkinglist.remove(i);
										 break;
									 }
								}
						}
					}
					talkingpan.expandPath(new TreePath(talkinglist));
					talkingpan.updateUI();
				}
			});
			
			
			JButton enter = new JButton("Enter");
			add(enter,BorderLayout.SOUTH);
			enter.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					ArrayList<String> list = new ArrayList<String>();
					for(int i = 0;i<talkinglist.getChildCount();i++){
						list.add((String) ((DefaultMutableTreeNode)talkinglist.getChildAt(i)).getUserObject());
						
					}
					GroupTalking groupTalking = new GroupTalking();
					groupTalking.setNamelist(list);
					
					addSession(groupTalking.getNamelist());
					//unfinished
					
				}
			});
			
			setVisible(true);
			
		}

		
	}


}

