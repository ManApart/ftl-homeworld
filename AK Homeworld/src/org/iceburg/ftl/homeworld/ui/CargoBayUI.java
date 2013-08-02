package org.iceburg.ftl.homeworld.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.SavedGameParser.CrewState;
import net.blerf.ftl.parser.SavedGameParser.DroneState;
import net.blerf.ftl.parser.SavedGameParser.SavedGameState;
import net.blerf.ftl.parser.SavedGameParser.ShipState;
import net.blerf.ftl.parser.SavedGameParser.WeaponState;
import net.blerf.ftl.xml.ShipBlueprint;

import org.iceburg.ftl.homeworld.parser.ShipSaveParser;
import org.iceburg.ftl.homeworld.resource.ResourceClass;
import org.iceburg.ftl.homeworld.core.FTLHomeworld;
import org.iceburg.ftl.homeworld.model.ShipSave;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

//TODO
//Event Handlers
//Cargo Parser

//ShipState readship() is line 247 of SavedGameParser
public class CargoBayUI extends JPanel {
	ShipSave currentShip;	//the ship selected from spacedock
	SavedGameState currentSave; //the actual save that the above represents
	SavedGameState tradeSave; 
	ShipSave homeSave; 
	ShipState currentState;		//the shipstate for the currentsave	
	ShipState tradeState;		
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
		this.init(spaceDock);
	}	
	public void init(ShipSave spaceDock) {
		this.removeAll();
		SavedGameParser parser = new SavedGameParser();
		ShipSaveParser shipParser = new ShipSaveParser();
		//First, get the current ship we're working with
		this.currentShip = spaceDock;
		//if there is no current ship, set some null values
		if (currentShip == null || (currentShip.getshipFilePath().exists() == false)) {
			currentSave = new SavedGameState();
			currentSave.setPlayerShipName("No Ship Selected");
			currentState = new ShipState("No Ship Selected", new ShipBlueprint(), false);
		}
		else {	
			try {
				currentSave = parser.readSavedGame(currentShip.getshipFilePath());
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
			
			currentState = currentSave.getPlayerShipState();
		}
		
		//Next get the Homeworld save file
			try {
			//	homeSave = parser.readSavedGame(FTLHomeworld.homeworld_save);
				homeSave = shipParser.readShipSave(new ShipSave(FTLHomeworld.homeworld_save));
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		
		
		//now, let's make the UI
		setPreferredSize(new Dimension(1280, 720));
		setLayout(null);
		String s;
		
		//Create shipSelect
		ArrayList<ShipSave> shipSelect = new ArrayList<ShipSave>();
		shipSelect.add(homeSave);
		shipSelect.addAll(SpaceDockUI.myShips);
		
		
		//TODO
		//display shipSelect
		
		JComboBox shipSelectCB = new JComboBox(shipToString(shipSelect).toArray());
		
		shipSelectCB.setBounds(1005, 97, 125, 20);
		
		add(shipSelectCB);
		
		tradeSave = getSelectedShip(shipSelectCB, shipSelect);
		currentShipInit();
		tradeShipInit();
	}
	
	
	
	
	public void currentShipInit() {
		//Populate with currentSave Data
		JLabel shipName = new JLabel(currentSave.getPlayerShipName());
		shipName.setForeground(Color.WHITE);
		shipName.setBounds(50, 200, 97, 14);
		add(shipName);
		
		
		JSpinner shipScrapSP = new JSpinner();
		shipScrapSP.setBounds(110, 305, 60, 20);
		shipScrapSP.setValue(currentState.getScrapAmt());
		add(shipScrapSP);
		
		JSpinner shipFuelSP = new JSpinner();
		shipFuelSP.setBounds(33, 423, 60, 20);
		shipFuelSP.setValue(currentState.getFuelAmt());
		add(shipFuelSP);
		
		String s;
		
		//Convert weapons list to string array for combo box
		ArrayList<String> shipWeaponList = new ArrayList<String>();
		s = new String("No Weapons!");
		if (currentState.getWeaponList().size() > 0){
			for (WeaponState w: currentState.getWeaponList()) {
				s = DataManager.get().getWeapon(w.getWeaponId()).getTitle();
				shipWeaponList.add(s);
			}
		}
		else {
			shipWeaponList.add(s);
		}
		//display weapons' names
		JComboBox shipWeaponsCB = new JComboBox(shipWeaponList.toArray());
		shipWeaponsCB.setBounds(430, 550, 200, 20);
		add(shipWeaponsCB);
			
		//Convert augment list to string array for combo box
		ArrayList<String> shipAugList = new ArrayList<String>();
		s = new String("No Augments!");
		if (currentState.getAugmentIdList().size() > 0){
			for (String aug: currentState.getAugmentIdList()) {
				s = DataManager.get().getAugment(aug).getTitle();
				shipAugList.add(s);
			}
		}
		else {
			shipAugList.add(s);
		}
		//display shipAugment names
		JComboBox shipAugCB = new JComboBox(shipAugList.toArray());
		shipAugCB.setBounds(1010, 550, 200, 20);
		add(shipAugCB);
		
		//Convert drone list to string array for combo box
		ArrayList<String> shipDroneList = new ArrayList<String>();
		s = new String("No Drones!");
		if (currentState.getDroneList().size() > 0){
			for (DroneState d: currentState.getDroneList()) {
				s = DataManager.get().getDrone(d.getDroneId()).getTitle();	
				shipDroneList.add(s);
			}
		}
		else {
			shipDroneList.add(s);
		}
		//display shipDrone names 
		JComboBox shipDroneCB = new JComboBox(shipDroneList.toArray());
		shipDroneCB.setBounds(430, 650, 200, 20);
		add(shipDroneCB);
		
		//Convert crew list to string array for combo box
		ArrayList<String> shipCrewList = new ArrayList<String>();
		s = new String("No Crew!");
		if (currentState.getDroneList().size() > 0){
			for (CrewState c: currentState.getCrewList()) {
				shipCrewList.add(c.getName());
			}
		}
		else {
			shipCrewList.add(s);
		}
		//display shipCrew names 
		JComboBox shipCrewCB = new JComboBox(shipCrewList.toArray());
		shipCrewCB.setBounds(45, 650, 125, 20);
		add(shipCrewCB);
		
		//display shipCargo names 
		ArrayList<String> shipCargoList = new ArrayList<String>();
		shipCargoList.addAll(shipWeaponList);
		shipCargoList.addAll(shipAugList);
		shipCargoList.addAll(shipDroneList);
		shipCargoList.addAll(shipCrewList);
		JComboBox shipCargoCB = new JComboBox(shipCargoList.toArray());
		shipCargoCB.setBounds(45, 550, 125, 20);
		add(shipCargoCB);
		
	}
	public void tradeShipInit() {
		//TODO Populate with Tradeship data
		tradeState = tradeSave.getPlayerShipState();
		JSpinner tradeScrapSP = new JSpinner();
		tradeScrapSP.setBounds(110, 260, 60, 20);
		tradeScrapSP.setValue(tradeSave.getPlayerShipState().getScrapAmt());
		add(tradeScrapSP);
		
		JSpinner tradeFuelSP = new JSpinner();
		tradeFuelSP.setBounds(148, 423, 60, 20);
//		tradeFuelSP.setValue(tradeSave.getPlayerShipState().getFuelAmt());
		add(tradeFuelSP);
		
		String s;
		
		//Convert weapons list to string array for combo box
		ArrayList<String> tradeWeaponList = new ArrayList<String>();
		s = new String("No Weapons!");
		if (currentState.getWeaponList().size() > 0){
			for (WeaponState w: currentState.getWeaponList()) {
				s = DataManager.get().getWeapon(w.getWeaponId()).getTitle();
				tradeWeaponList.add(s);
			}
		}
		else {
			tradeWeaponList.add(s);
		}
		//display weapons' names
		JComboBox tradeWeaponsCB = new JComboBox(currentState.getWeaponList().toArray());
		tradeWeaponsCB.setBounds(720, 550, 200, 20);
		add(tradeWeaponsCB);
		
		JComboBox tradeAugCB = new JComboBox();
		tradeAugCB.setBounds(1010, 650, 200, 20);
		add(tradeAugCB);
		
		JComboBox tradeDroneCB = new JComboBox();
		tradeDroneCB.setBounds(720, 650, 200, 20);
		add(tradeDroneCB);
		
		JComboBox tradeCrewCB = new JComboBox();
		tradeCrewCB.setBounds(230, 650, 125, 20);
		add(tradeCrewCB);
		
		JComboBox tradeCargoCB = new JComboBox();
		tradeCargoCB.setBounds(230, 550, 125, 20);
		add(tradeCargoCB);
		
	}
	//TODO
	public ShipSave getSelectedShip(JComboBox box, ArrayList<ShipSave> list) {
		return list.get(box.getSelectedIndex());
	}
	
	public ArrayList<String> shipToString(ArrayList<ShipSave> als) {
		ArrayList<String> al = new ArrayList<String>();
		if (als.size() > 0){
			for (ShipSave ss: als) {
				String s = ss.getPlayerShipName();
				al.add(s);
			}
		}
		return al;
	}
}
