package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainWindow implements Runnable{

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				MainJFrame mainJFrame = new MainJFrame();
				mainJFrame.setTitle("main");
				mainJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainJFrame.setVisible(true);
				
			}
		});
	}
	@Override
	public void run() {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				MainJFrame mainJFrame = new MainJFrame();
				mainJFrame.setTitle("main");
				mainJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainJFrame.setVisible(true);
				
			}
		});
	}
}


class MainJFrame extends JFrame{
	public MainJFrame(){
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		setSize(width/2, height/2);
		setLocation(width/4, height/4);
		setResizable(true);
		setLayout(new BorderLayout());
		
		JPanel friendJframe = new JPanel();
		add(friendJframe,BorderLayout.WEST);
		friendJframe.setLayout(new BorderLayout());
		friendJframe.add(new JLabel("all",JLabel.CENTER));
	}
}