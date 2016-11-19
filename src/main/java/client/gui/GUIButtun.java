package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class GUIButtun extends JPanel {

	private JButton session;
	private JButton close;
	
	public GUIButtun(String name){
		session = new JButton(name);
		close = new JButton("X");
		session.setPreferredSize(new Dimension(100,27));
		close.setPreferredSize(new Dimension(50,27));
		add(session);
		add(close);
		setMaximumSize(new Dimension(180,40));
		setMinimumSize(new Dimension(180,40));
		setLayout(new FlowLayout());
//		setBorder(BorderFactory.createTitledBorder("session"));
	}

	public JButton getSession() {
		return session;
	}

	public JButton getClose() {
		return close;
	}
	
	
	
}
