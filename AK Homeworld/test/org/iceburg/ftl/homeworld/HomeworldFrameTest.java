package org.iceburg.ftl.homeworld;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.iceburg.ftl.homeworld.ui.SpaceDockUI;

//This is the frame that runs the panels/subprograms. The main UI (Test Varient)

public class HomeworldFrameTest extends JFrame {

	private JPanel contentPane;
	private SpaceDockUI spaceDockPanel;
	

	/**
	 * Create the frame.
	 */
	public HomeworldFrameTest() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 700);
		setTitle("FTL Homeworld");
		JTabbedPane tasksPane = new JTabbedPane();
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add( tasksPane, BorderLayout.CENTER );

	//	JScrollPane spaceDockPane = new JScrollPane(new SpaceDockUI());
		JScrollPane cargoBayPane = new JScrollPane(new CargoBayUITestGUI());
		//CargoBayUITestGUI cargoBayPane = new CargoBayUITestGUI();

		
//		spaceDockPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		spaceDockPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		spaceDockPane.setOpaque(false);
//		spaceDockPane.getViewport().setOpaque(false);
//		tasksPane.add( "Space Dock", spaceDockPane);
		
		cargoBayPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		cargoBayPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		cargoBayPane.setOpaque(false);
		cargoBayPane.getViewport().setOpaque(false);	
		tasksPane.add( "Cargo Bay", cargoBayPane);

		
		
		
		
	}

}
