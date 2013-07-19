package org.iceburg.ftl.homeworld;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

//This is the frame that runs the panels/subprograms. The main UI

public class HomeworldFrame extends JFrame {

	private JPanel contentPane;
	private SpaceDockUI spaceDockPanel;
	

	/**
	 * Create the frame.
	 */
	public HomeworldFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 700);
		setTitle("FTL Homeworld");
		JTabbedPane tasksPane = new JTabbedPane();
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add( tasksPane, BorderLayout.CENTER );

//		spaceDockPanel = new SpaceDockUI(this);
		JPanel spaceDockPane = new SpaceDockUI();
		JScrollPane scrollPanel = new JScrollPane(spaceDockPane);
		
		
		//JScrollPane scrollPanel = new JScrollPane(new SpaceDockUI);
		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPanel.setOpaque(false);
		scrollPanel.getViewport().setOpaque(false);
		tasksPane.add( "Space Dock", scrollPanel);
		
		
		
		
		
	}

}
