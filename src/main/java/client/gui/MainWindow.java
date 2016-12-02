package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import json.client.session.SendMessage;

public class MainWindow extends JFrame implements Runnable{

	private static final long serialVersionUID = 1L;
	public final static int BUTTUN_HEIGHT=27;
	private JTree friendTree;
	private DefaultMutableTreeNode root;
	private JPanel right;
	private JPanel center;
	private ConcurrentHashMap<String, GUISession> name2GUISession = new ConcurrentHashMap<String, GUISession>();

	public MainWindow(){
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		setSize(width/2, height/2);
		setLocation(width/4, height/4);
		setResizable(false);
		setLayout(new BorderLayout());
		setTitle("main");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
		buttonJpanel.setLayout(new BoxLayout(buttonJpanel, BoxLayout.Y_AXIS));
		left.add(buttonJpanel, BorderLayout.SOUTH);
		JButton AddFriend = new JButton("Add Friend");

		buttonJpanel.add(AddFriend);
		AddFriend.addActionListener(new AddFriendAction());
		JButton removeFriend = new JButton("RemoveFriend");
		buttonJpanel.add(removeFriend);
		
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
		AddDoubleClickTreeAction();
	}
	
	public DefaultMutableTreeNode addPathNode(String pathnode) {
        String[] ns = pathnode.split("\\.");
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
	
	public boolean removePathNode(String pathnode){
		String[] ns = pathnode.split("\\.");
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

	private void clearNode() {
        DefaultMutableTreeNode node = root;
        int i = node.getChildCount()-1;
        for(;i>=0;i--){
        	TreeNode tmp = node.getChildAt(i);
        	if(tmp.getChildCount()==0)
        		root.remove(i);
        }
		
	}

	@Override
	public void run() {
	}

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
	
	public void rmSession(String friendname){
		right.remove(name2GUISession.get(friendname).button);
		center.remove(name2GUISession.get(friendname).jpanel);
		this.repaint();
		name2GUISession.remove(friendname);
	}
	
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
	
	private void AddDoubleClickTreeAction(){
		friendTree.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				int selRow = friendTree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = friendTree.getPathForLocation(e.getX(), e.getY());
				if(selRow != -1) {
					if(e.getClickCount() == 2) {
						addSession(selPath.getLastPathComponent().toString());
					}
				}
			}
		});
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
	
	private class GUISession{
		GUIButtun button;
		JPanel jpanel;
		public GUISession(GUIButtun button, JPanel jpanel) {
			super();
			this.button = button;
			this.jpanel = jpanel;
		}
		
	}

	private class AddFriendAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unused")
			GUIAddFriend guiAddFriend = new GUIAddFriend();
		}
		
	}

	public  void displayMessage(SendMessage sendmessage) {
		GUISession guiSession = name2GUISession.get(sendmessage.getName());
		if(guiSession==null){
			addSession(sendmessage.getName());
			guiSession = name2GUISession.get(sendmessage.getName());
		}
		JTextArea display = (JTextArea)guiSession.jpanel.getComponent(1);
		display.insert("\n"+sendmessage.getName()+":\n"+sendmessage.getMessage(), display.getText().length());
//		display.insert("\n"+sendmessage.getMessage(),display.getText().length());//if I insert message by two step,GUI will dump
		center.removeAll();
		center.add(guiSession.jpanel);
		center.updateUI();
		guiSession.jpanel.updateUI();
	}
	
	public List<String> getGroup(){
		int count = root.getChildCount();
		List<String> ret = new ArrayList<String>();
		for(int i = 0 ; i< count;i++){
			DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) root.getChildAt(i);
			ret.add((String)tmp.getUserObject());
		}
		return ret;		
	}
}

