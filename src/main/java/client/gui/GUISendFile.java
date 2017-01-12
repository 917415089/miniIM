package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import com.alibaba.fastjson.JSON;
import client.ClientManage;
import json.client.session.SendFile;
import json.util.JSONNameandString;

@SuppressWarnings("serial")
public class GUISendFile extends JFrame {
	
	private final static long FILESIZETHREAD = 60000;
	public GUISendFile(final DefaultMutableTreeNode root) {
		super();

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
		JLabel group = new JLabel("group",JLabel.CENTER);
		center.add(group);
		final JComboBox<String> groupname = new JComboBox<String>();
		
		int len = root.getChildCount();
		for(int i  = 0; i < len ; i++){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) (root.getChildAt(i));
			groupname.addItem((String)(node.getUserObject()));
		}
		
		center.add(groupname);
		JLabel name = new JLabel("name",JLabel.CENTER);
		center.add(name);
		final JComboBox<String> friendname = new JComboBox<String>();
		center.add(friendname);
		DefaultMutableTreeNode firstchild = (DefaultMutableTreeNode)root.getChildAt(0);
		if(firstchild!=null){
			int length = firstchild.getChildCount();
			for(int i = 0 ; i < length;i++){
				DefaultMutableTreeNode fn = (DefaultMutableTreeNode) firstchild.getChildAt(i);
				friendname.addItem((String)(fn.getUserObject()));
			}
		}
		groupname.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {

				if(e.getStateChange()==ItemEvent.SELECTED){
					friendname.removeAllItems();
					int len = root.getChildCount();
					for(int i  = 0; i < len ; i++){
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) (root.getChildAt(i));
						if(e.getItem().equals((String)(node.getUserObject()))){
							int length = node.getChildCount();
							for(int j = 0 ; j < length;j++){
								DefaultMutableTreeNode n = (DefaultMutableTreeNode) (node.getChildAt(j));
								friendname.addItem((String)(n.getUserObject()));
							}
						}
					}
				}
				friendname.updateUI();

			}
			
		});
		
		JButton enter = new JButton("Enter");
		add(enter,BorderLayout.SOUTH);
		enter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				jfc.showDialog(new JLabel(), "选择");
				File file = jfc.getSelectedFile();
				
				try {
					BigFileReader reader = new BigFileReader(file,(String)(friendname.getSelectedItem()));
					reader.read();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "can't read file", "SendFileError", JOptionPane.ERROR_MESSAGE);
				}
				dispose();
			}
		});
		
		setVisible(true);
	}
	
	private class BigFileReader{
		private MappedByteBuffer[] mappedBufArray;
		private int count = 0;
		private int number;
		private FileInputStream input;
		private long filesize;
		private File file;
		private String name;
		

		public BigFileReader(File file,String name) throws IOException {
			try{
				this.file = file;
				this.name = name;
				input = new FileInputStream(file);
				FileChannel channel = input.getChannel();
				filesize = channel.size();
				number = 	(int) Math.ceil((double) filesize/(double) FILESIZETHREAD);
				mappedBufArray = new MappedByteBuffer[number];
				
				//map memory
				long current = 0;
				long regionSize = FILESIZETHREAD;
				for(int i =0 ; i < number;i++){
					if(filesize - current < FILESIZETHREAD){
						regionSize = filesize-current;
					}
					mappedBufArray[i] = channel.map(FileChannel.MapMode.READ_ONLY, current, regionSize);
					current += regionSize;
				}
			}catch(FileNotFoundException e){
				System.err.println("File chooser has been closed");
			}
		}
		
		private int read0() throws IOException{
			if(count >= number)return -1;
			int limit = mappedBufArray[count].limit();
			int position = mappedBufArray[count].position();
			if(limit-position > FILESIZETHREAD){
				byte[] sendbyte = new byte[(int) FILESIZETHREAD];
				warp(sendbyte);
				mappedBufArray[count].get(sendbyte);
				return (int) FILESIZETHREAD;
			}else{
				byte[] sendbyte = new byte[limit-position];
				warp(sendbyte);
				mappedBufArray[count].get(sendbyte);
				if(count<number){
					count++;
				}
				return limit-position;
			}
		}
		
		private void warp(byte[] sendbyte) {
			SendFile sendFile = new SendFile();
			sendFile.setFriendname(name);
			sendFile.setFilename(file.getName());
			sendFile.setContent(sendbyte);
		
			JSONNameandString json = new JSONNameandString();
			json.setJSONName(SendFile.class.getName());
			json.setJSONStr(JSON.toJSONString(sendFile));
			ClientManage.sendJSONNameandString(json);
		}

		void read() throws IOException{
			while(read0()!=-1);
		}
	}

	
}
