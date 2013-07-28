package org.iceburg.ftl.homeworld;

import java.awt.EventQueue;

import javax.swing.JFrame;

import org.iceburg.ftl.homeworld.parser.ShipSaveParser;
import org.iceburg.ftl.homeworld.model.ShipSave;

import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SpaceDockUITestGUI {

	private JFrame frmSpaceDock;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SpaceDockUITestGUI window = new SpaceDockUITestGUI();
					window.frmSpaceDock.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SpaceDockUITestGUI() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//initializ - get ships/file to display
		ArrayList<ShipSave> myShips = ShipSaveParser.getShipsList();
		
		
		frmSpaceDock = new JFrame();
		frmSpaceDock.setTitle("Space Dock");
		frmSpaceDock.setBounds(100, 100, 450, 300);
		frmSpaceDock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSpaceDock.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		
		
		int i = 4;
		//for (int i = 0; i < myShips.length; i++) {			
		//create panel
		String panelName = ("shipPanel" + i);
		JPanel loopPanel = new JPanel();
		frmSpaceDock.getContentPane().add(loopPanel);
		
		JButton btnButtonbutton = new JButton("buttonbutton");
		btnButtonbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Button pressed!");
			}
		});
		loopPanel.add(btnButtonbutton);
		JLabel lblShipName = new JLabel(myShips.get(i).getPlayerShipName());
		frmSpaceDock.getContentPane().add(lblShipName);
		JLabel lblExplored = new JLabel(myShips.get(i).getTotalBeaconsExplored() + " beacons explored.");
		frmSpaceDock.getContentPane().add(lblExplored);

		System.out.println(myShips.get(i).getshipFilePath());
		File currentFile = 
				new File(myShips.get(i).getshipFilePath().getParentFile() + "\\continue.sav");
		System.out.println(currentFile);
		JButton btnBoard = null;
		if (myShips.get(i).getshipFilePath().equals(currentFile)) {
		btnBoard = new JButton("Dock");
		}
		else {
			btnBoard = new JButton("Board");
		}
		frmSpaceDock.getContentPane().add(btnBoard);
		
		
				
		
	}

}

