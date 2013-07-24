package org.iceburg.ftl.homeworld;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


//This is the frame that runs the panels/subprograms. The main UI

public class HomeworldFrame extends JFrame {

	private JPanel contentPane;
	public SpaceDockUI spaceDock;
	

	/**
	 * Create the frame.
	 */
	public HomeworldFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 700);
		setTitle("FTL Homeworld");
		JTabbedPane tasksPane = new JTabbedPane();
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add( tasksPane, BorderLayout.CENTER );

		SpaceDockUI spaceDock = new SpaceDockUI();
		JScrollPane spaceDockPane = new JScrollPane(spaceDock);
		
		spaceDockPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		spaceDockPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		spaceDockPane.setOpaque(false);
		spaceDockPane.getViewport().setOpaque(false);
		tasksPane.add( "Space Dock", spaceDockPane);
		
		
		JScrollPane cargoBayPane = new JScrollPane(new CargoBayUI(spaceDock.getCurrentShip()));

		cargoBayPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		cargoBayPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		cargoBayPane.setOpaque(false);
		cargoBayPane.getViewport().setOpaque(false);	
		tasksPane.add( "Cargo Bay", cargoBayPane);

		//use this to refresh tabs on tab change
		tasksPane.addChangeListener(new ChangeListener() {
				@Override    
				public void stateChanged(ChangeEvent e) {
		           // System.out.println("Tab: " + tasksPane.getSelectedIndex());
		        }
		});		
		
		
	}
}
