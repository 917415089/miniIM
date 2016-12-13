package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class GUIGrouptalking extends JFrame {

	private static final long serialVersionUID = -1834553272122923159L;
	private DefaultMutableTreeNode root;
	
	public GUIGrouptalking(DefaultMutableTreeNode root) {
		this.root=root;
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
				//unfinished
				
			}
		});
		
		setVisible(true);
		
	}

	
}
