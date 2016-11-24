package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.alibaba.fastjson.JSON;

import json.client.login.ClientRegister;
import json.util.JSONNameandString;
import client.ClientManage;

public class GUIRegister extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField inputname;
	private JTextField inputpassword;
	private JTextField inputemail;

	public GUIRegister(){
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		setSize(width/8, height/8);
		setLocation(width/8*3, height/8*3);
		setResizable(false);
		setLayout(new BorderLayout());
		setTitle("Register");
		
		JPanel main = new JPanel();
		add(main,BorderLayout.CENTER);
		main.setLayout(new GridLayout(3,2));
		JLabel name = new JLabel("name",JLabel.CENTER);
		main.add(name);
		inputname = new JTextField("name");
		main.add(inputname);
		JLabel password = new JLabel("password",JLabel.CENTER);
		main.add(password);
		inputpassword = new JTextField("password");
		main.add(inputpassword);
		JLabel email = new JLabel("email",JLabel.CENTER);
		main.add(email);
		inputemail = new JTextField("email");
		main.add(inputemail);
		
		JButton Enter = new JButton("Enter");
		add(Enter,BorderLayout.SOUTH);
		Enter.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ClientRegister register = new ClientRegister();
				register.setEmail(inputemail.getText());
				register.setUserName(inputname.getText());
				register.setUserPassword(inputpassword.getText());
				
				JSONNameandString json = new JSONNameandString();
				json.setJSONName(ClientRegister.class.getName());
				json.setJSONStr(JSON.toJSONString(register));
				System.out.println(json.getJSONStr());
				ClientManage.sendJSONNameandString(json);
			}
		});
		
		setVisible(true);
	}
}
