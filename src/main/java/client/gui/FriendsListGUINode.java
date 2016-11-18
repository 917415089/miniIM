package client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import json.server.session.FriendList;
import json.server.session.FriendMeta;
@Deprecated
public class FriendsListGUINode extends DefaultMutableTreeNode{
	
	public FriendsListGUINode(String name){
		super(name);
	}
	
	static public FriendsListGUINode generate(FriendList fl){
		FriendsListGUINode root = new FriendsListGUINode("All");
		HashMap<String,List<String>> map = new HashMap<String,List<String>>();
		for(FriendMeta fm : fl.getFriends()){
			if(!map.keySet().contains(fm.getGroup())){
				map.put(fm.getGroup(), new ArrayList<String>());
			}
			map.get(fm.getGroup()).add(fm.getName());
		}
		for(String group : map.keySet()){
			FriendsListGUINode gn = new FriendsListGUINode(group);
			root.add(gn);
			for(String name : map.get(group)){
				FriendsListGUINode tmp = new FriendsListGUINode(name);
				gn.add(tmp);
			}
		}
		return root;
	}
}
