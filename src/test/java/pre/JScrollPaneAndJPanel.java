package pre;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class JScrollPaneAndJPanel extends JFrame {
	 public JScrollPaneAndJPanel() {
		  super("TestJScrollPane");
		  this.setLayout(new BorderLayout());
		  this.setBounds(200, 200, 300, 300);
		  JPanel panel = new JPanel();
		  panel.setPreferredSize(new Dimension(200,100));//主要是这句代码，设置panel的首选大小，同时保证宽高大于JScrollPane的宽高，这样下面的JScrollPane才会出现滚动条
		  JButton button1  = new JButton("1");  
		  panel.add(button1);
		  JButton button2  = new JButton("2");  
		  panel.add(button2);
		  JButton button3  = new JButton("3");  
		  panel.add(button3);
		  JButton button4  = new JButton("4");  
		  panel.add(button4);
		  JButton button5  = new JButton("5");  
		  panel.add(button5);
		  JButton button6  = new JButton("6");  
		  panel.add(button6);
		  JButton button7  = new JButton("7");  
		  panel.add(button7);
		  panel.add(new JButton());
		  panel.add(new JButton());
		  panel.add(new JButton());
		  panel.add(new JButton());
		  panel.add(new JButton());
		  panel.add(new JButton());
		  panel.add(new JButton());
		  panel.add(new JButton());
		  panel.add(new JButton());
		  JScrollPane scrollPane = new JScrollPane(panel);
		  scrollPane.setBounds(10, 10, 175, 70);
		  this.getContentPane().add(scrollPane,BorderLayout.SOUTH);
		  this.setVisible(true);
		  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		 }
		 public static void main(String[] args) {
		  new JScrollPaneAndJPanel();
		 }
}
