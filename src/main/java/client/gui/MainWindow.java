package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class MainWindow extends JFrame implements Runnable{

	public final static int BUTTUN_HEIGHT=27;
	public final static   MainWindow  unique= new MainWindow();
	private JTree friendTree;
	private DefaultMutableTreeNode root;;
	
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
		left.add(friendlist,BorderLayout.NORTH);
		JButton AddFriend = new JButton("Add Friend");
		left.add(AddFriend,BorderLayout.SOUTH);
		
		
		JPanel right = new JPanel();
		add(right,BorderLayout.EAST);
		right.setBorder(BorderFactory.createTitledBorder("   Session List   "));
		right.setPreferredSize(new Dimension(200,getHeight()));
		BoxLayout boxLayout = new BoxLayout(right,BoxLayout.Y_AXIS);
		right.setLayout(boxLayout);

//		right.add(new GUISession("user2"));
				
		JPanel center = new JPanel();
		center.setBorder(BorderFactory.createTitledBorder("   Working space   "));
		center.setLayout(new GridBagLayout());
		add(center,BorderLayout.CENTER);
		
		JLabel sessionLabel = new JLabel("talk with user2");
		GridBagConstraints slc = new GridBagConstraints();
		slc.weightx=100;
		slc.weighty=100;
		slc.gridx=0;
		slc.gridy=0;
		slc.gridheight=1;
		slc.gridwidth=1;
		center.add(sessionLabel,slc);
		
		JTextArea sessionwindow = new JTextArea(100,100);
		sessionwindow.setBorder(BorderFactory.createTitledBorder("   Session window   "));
		sessionwindow.setMinimumSize(new Dimension(getWidth()-400,getHeight()/4*3-50));
//		sessionwindow.setPreferredSize(new Dimension(getWidth()-400,getHeight()/2-50));
		GridBagConstraints swc = new GridBagConstraints();
		swc.weightx=100;
		swc.weighty=100;
		swc.gridx=0;
		swc.gridy=1;
		swc.gridheight=1;
		swc.gridwidth=1;
		center.add(sessionwindow,swc);
				
		JTextArea talkwindow = new JTextArea(200,300);
		talkwindow.setBorder(BorderFactory.createTitledBorder("   talk window   "));
//		talkwindow.setPreferredSize(new Dimension(getWidth()-400,getHeight()/2-50));
		talkwindow.setMinimumSize(new Dimension(getWidth()-400,getHeight()/4-50));
		GridBagConstraints twc = new GridBagConstraints();
		twc.weightx=100;
		twc.weighty=100;
		twc.gridx=0;
		twc.gridy=2;
		twc.gridheight=1;
		twc.gridwidth=1;
		center.add(talkwindow,twc);	
		
		JButton Enter = new JButton("Enter");
		GridBagConstraints enc = new GridBagConstraints();
		enc.weightx=100;
		enc.weighty=100;
		enc.gridx=0;
		enc.gridy=3;
		enc.gridheight=1;
		enc.gridwidth=1;
		center.add(Enter,enc);
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

	public JTree getFriendTree() {
		return friendTree;
	}

	public DefaultMutableTreeNode getRoot() {
		return root;
	}

	public void setFriendTree(JTree friendTree) {
		this.friendTree = friendTree;
	}

	public void setRoot(DefaultMutableTreeNode root) {
		this.root = root;
	}

	@Override
	public void run() {
		unique.setVisible(true);
	}
}
