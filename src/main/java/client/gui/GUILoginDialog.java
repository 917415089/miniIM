package client.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import client.BaseClient;
import client.ClientManage;
import json.client.login.ClientLogin;
import json.util.JSONNameandString;

public class GUILoginDialog extends JFrame{

	private static final long serialVersionUID = 1L;
	JTextField textField;
	JPasswordField password;
	JButton login;
	JButton register;
	
	public GUILoginDialog() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		setSize(width/4, height/4);
		setLocation(width/8*3, height/8*3);
		setResizable(false);
		setLayout(null);
		
		JPanel panel = new JPanel();
		GridLayout gridLayout = new GridLayout(3,2);
		gridLayout.setHgap(10);
		gridLayout.setVgap(10);
		panel.setLayout(gridLayout);
		add(panel);
		panel.setBounds(getWidth()/4, getHeight()/4, getWidth()/2, getHeight()/2);
		
		panel.add(new JLabel("User Name",JLabel.CENTER));
		textField = new JTextField("user1",30);
		panel.add(textField);
		panel.add(new JLabel("User Password",JLabel.CENTER));
		password = new JPasswordField("123",30);
		panel.add(password);
		login = new JButton("login");
		panel.add(login);
		register = new JButton("register");
		panel.add(register);
		
		LoginAction loginAction = new LoginAction();
		login.addActionListener(loginAction);
		RegisterAction registerAction = new RegisterAction();
		register.addActionListener(registerAction);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private class LoginAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			setEnabled(false);
			
			String name = textField.getText();
			ClientManage.setName(name);
			ClientManage.setPassword(new String(password.getPassword()));
			BaseClient baseClient = new BaseClient();
			
			ExecutorService singleBaseClient = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("BaseClient").build());
			singleBaseClient.submit(baseClient);
			
			ClientLogin login = new ClientLogin();
			login.setName(name);
			login.setPassword(ClientManage.getPassword());
			
			JSONNameandString json = new JSONNameandString();
			json.setJSONName(ClientLogin.class.getName());
			json.setJSONStr(JSON.toJSONString(login));
			
			ClientManage.sendJSONNameandString(json);
			setVisible(false);
		}
		
	}
	
	private class RegisterAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {

			BaseClient registerclient = new BaseClient();
			ExecutorService singleBaseClient = Executors.newSingleThreadExecutor();
			singleBaseClient.submit(registerclient);
			@SuppressWarnings("unused")
			GUIRegister guiRegister = new GUIRegister();
		}
		
	}

	public void setEnabled(boolean flag){
		login.setEnabled(flag);
		register.setEnabled(flag);
	}

}
