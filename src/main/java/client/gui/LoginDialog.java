package client.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import json.util.JSONNameandString;
import client.BaseClient;


public class LoginDialog extends JFrame implements Runnable{
	JTextField textField;
	JPasswordField password;
	JButton login;
	JButton register;
	
	BlockingQueue<JSONNameandString> receque;
	
	public LoginDialog() {
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
		textField = new JTextField("",30);
		panel.add(textField);
		panel.add(new JLabel("User Password",JLabel.CENTER));
		password = new JPasswordField("",30);
		panel.add(password);
		login = new JButton("login");
		panel.add(login);
		register = new JButton("register");
		panel.add(register);
		
		LoginAction loginAction = new LoginAction();
		login.addActionListener(loginAction);
		RegisterAction registerAction = new RegisterAction();
		register.addActionListener(registerAction);
	}
	
	@Override
	public void run() {
		LoginDialog login = new LoginDialog();
		GUIManage.getUniqueGUIManage().setLogindaialog(login);
		login.setTitle("Login");
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login.setVisible(true);
	}
	
	private class LoginAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			setEnabled(false);
			
			String name = textField.getText();
			String pass = new String(password.getPassword());
			BaseClient baseClient = new BaseClient(name,pass);
			Thread thread = new Thread(baseClient);
			receque = baseClient.getReceque();
			thread.start();
		}
		
	}
	
	private class RegisterAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("registering...");
		}
		
	}

	public void setEnabled(boolean flag){
		login.setEnabled(flag);
		register.setEnabled(flag);
	}

}
