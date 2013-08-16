package org.iceburg.ftl.homeworld.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.iceburg.ftl.homeworld.model.ShipSave;
import org.iceburg.ftl.homeworld.resource.ResourceClass;



//This is the frame that runs the panels/subprograms. The main UI

public class HomeworldFrame extends JFrame {

	JTabbedPane tasksPane;
	private JPanel contentPane;
	public SpaceDockUI spaceDock;
	public CargoBayUI cargoBay;
	public ScienceStationUI scienceStation;
	public DryDockUI dryDock;
	JScrollPane cargoBayPane;
	JScrollPane scienceStationPane;
	//JScrollPane spaceDockPane;
	JPanel spaceDockPane;
	JScrollPane dryDockPane;
	
	public static ShipSave currentShip;

	/**
	 * Create the frame.
	 */
	public HomeworldFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 720);
		setTitle("FTL Homeworld");
		//TODO ImageIcon
		Image img = (new ImageIcon((new ResourceClass()).getClass().getResource("LogoIcon.png"))).getImage();
		setIconImage(img);
		//setIconImage(Toolkit.getDefaultToolkit().getImage((new ResourceClass()).getClass().getResource("Logo Icon.png")));
		tasksPane = new JTabbedPane();
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add( tasksPane, BorderLayout.CENTER );

		spaceDock = new SpaceDockUI();
		spaceDockPane = new SpaceDockScrollPane(spaceDock);
		
//		spaceDockPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		spaceDockPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		spaceDockPane.setOpaque(false);
//		spaceDockPane.getViewport().setOpaque(false);
		tasksPane.add( "Space Dock", spaceDockPane);
		
		cargoBay = new CargoBayUI();
		cargoBayPane = new JScrollPane(cargoBay);

		cargoBayPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		cargoBayPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		cargoBayPane.setOpaque(false);
		cargoBayPane.getViewport().setOpaque(false);	
		tasksPane.add( "Cargo Bay", cargoBayPane);
		
		scienceStation = new ScienceStationUI();
		scienceStationPane = new JScrollPane(scienceStation);
		
		scienceStationPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scienceStationPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scienceStationPane.setOpaque(false);
		scienceStationPane.getViewport().setOpaque(false);	
		tasksPane.add( "Science Station", scienceStationPane);
		
		dryDock = new DryDockUI();
		dryDockPane = new JScrollPane(dryDock);
		
		dryDockPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		dryDockPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		dryDockPane.setOpaque(false);
		dryDockPane.getViewport().setOpaque(false);	
		tasksPane.add( "Dry Dock", dryDockPane);
		
		
		//use this to refresh tabs on tab change
		tasksPane.addChangeListener(new ChangeListener() {
				@Override    
				public void stateChanged(ChangeEvent e) {
		          //  System.out.println("Tab: " + tasksPane.getSelectedIndex());
		            JScrollPane js = (JScrollPane) tasksPane.getSelectedComponent();
		            if (js == cargoBayPane) {
		            	//System.out.println("found");
		            	cargoBay.init();
		            	cargoBay.currentShipInit();
		            	cargoBay.tradeShipInit();
		            	
		            }
//		            else if (js == spaceDockPane) {
//		            	//System.out.println("found");
//		            	spaceDock.init();
//		            }
		            
		        }
		});		
		
		
	}
	
}
