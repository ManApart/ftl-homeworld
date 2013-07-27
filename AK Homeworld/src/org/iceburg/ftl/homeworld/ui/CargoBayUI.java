package org.iceburg.ftl.homeworld.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JSpinner;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.SavedGameParser.SavedGameState;
import net.blerf.ftl.parser.SavedGameParser.ShipState;
import net.blerf.ftl.parser.SavedGameParser.WeaponState;
import net.blerf.ftl.xml.ShipBlueprint;

import org.iceburg.ftl.homeworld.parser.ShipSaveParser;
import org.iceburg.ftl.homeworld.resource.ResourceClass;
import org.iceburg.ftl.homeworld.model.ShipSave;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

//TODO
//Event Handlers
//Cargo Parser

public class CargoBayUI extends JPanel {
	ShipSave currentShip;	//the ship selected from spacedock
	SavedGameState currentSave; //the actual save that the above represents
	ShipState currentState;		//the shipstate for the currentsave	
	BufferedImage bg  = null;
	ImageIcon img = null;
	//JLabel shipName = null;
		 @Override
		    public void paintComponent(Graphics g) {
				img = new ImageIcon((new ResourceClass()).getClass().getResource("CargoBaySplash.png"));
				bg = new BufferedImage(
				img.getIconWidth(),
				img.getIconHeight(),
				BufferedImage.TYPE_INT_RGB);
				Graphics gg = bg.createGraphics();
				img.paintIcon(null, gg, 0,0);
				gg.dispose();
		        g.drawImage(bg, 0, 0,img.getIconWidth(), img.getIconHeight(), this);
		    }
  
    
	public CargoBayUI(ShipSave spaceDock) {
		//First, get the current ship we're working with
		this.currentShip = spaceDock;
		//if there is no current ship, set some null values
		if (currentShip == null) {
			currentSave = new SavedGameState();
			currentSave.setPlayerShipName("No Ship Selected");
			currentState = new ShipState("No Ship Selected", new ShipBlueprint(), false);
		}
		else {
			SavedGameParser parser = new SavedGameParser();
			try {
				currentSave = parser.readSavedGame(currentShip.getshipFilePath());
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
			
			currentState = currentSave.getPlayerShipState();
		}
		//now, let's make the UI
		setPreferredSize(new Dimension(1280, 720));
		setLayout(null);
		
		JLabel shipName = new JLabel(currentSave.getPlayerShipName());
		shipName.setForeground(Color.WHITE);
		shipName.setBounds(50, 200, 97, 14);
		add(shipName);
		
		JSpinner cargoScrapSP = new JSpinner();
		cargoScrapSP.setBounds(110, 260, 60, 20);
		add(cargoScrapSP);
		
		JSpinner shipScrapSP = new JSpinner();
		shipScrapSP.setBounds(110, 305, 60, 20);
		shipScrapSP.setValue(currentState.getScrapAmt());
		add(shipScrapSP);
		
		JSpinner shipFuelSP = new JSpinner();
		shipFuelSP.setBounds(33, 423, 60, 20);
		shipFuelSP.setValue(currentState.getFuelAmt());
		add(shipFuelSP);
		
		JSpinner cargoFuelSP = new JSpinner();
		cargoFuelSP.setBounds(148, 423, 60, 20);
		add(cargoFuelSP);
		
		//Convert weapons list to string array for combo box
		ArrayList<String> shipWeaponList = new ArrayList<String>();
		for (WeaponState w: currentState.getWeaponList()) {
			String s = DataManager.get().getWeapon(w.getWeaponId()).getTitle();
			shipWeaponList.add(s);
		}
		//display weapons' names
		JComboBox shipWeaponsCB = new JComboBox(shipWeaponList.toArray());
		shipWeaponsCB.setBounds(430, 550, 200, 20);
		add(shipWeaponsCB);
		
		
		//TODO Cargo weapons' names
		JComboBox cargoWeaponsCB = new JComboBox();
		cargoWeaponsCB.setBounds(720, 550, 200, 20);
		add(cargoWeaponsCB);
		
		//TODO
		//Convert augment list to string array for combo box
		ArrayList<String> shipAugList = new ArrayList<String>();
		for (WeaponState w: currentState.getWeaponList()) {
			String s = DataManager.get().getWeapon(w.getWeaponId()).getTitle();
			shipAugList.add(s);
		}
		//display shipAugment names
		JComboBox shipAugCB = new JComboBox(shipAugList.toArray());
		shipAugCB.setBounds(1010, 530, 200, 20);
		add(shipAugCB);
		
		//TODO Cargo Augment names
		JComboBox cargoAugCB = new JComboBox();
		cargoAugCB.setBounds(720, 550, 200, 20);
		add(cargoAugCB);
		
		//TODO
		//Convert drone list to string array for combo box
		ArrayList<String> shipDroneList = new ArrayList<String>();
		for (WeaponState w: currentState.getWeaponList()) {
			String s = DataManager.get().getWeapon(w.getWeaponId()).getTitle();
			shipDroneList.add(s);
		}
		//display shipAugment names 
		JComboBox shipDroneCB = new JComboBox(shipDroneList.toArray());
		shipAugCB.setBounds(1010, 530, 200, 20);
		add(shipDroneCB);
		
	}
}
