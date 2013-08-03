package org.iceburg.ftl.homeworld.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import net.blerf.ftl.xml.ShipBlueprint.AugmentId;

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
//Get proper room for cargo, crew, weapons, etc
//Add limitations to handlers (can only have X crew, etc)
//Do actual ship cargo
//Do JSpinners
//Add limitations to Spinners
//Tooltips


//ShipState readship() is line 247 of SavedGameParser
public class CargoBayUI extends JPanel implements ActionListener {
	CargoBayUI self;
	ShipSave currentShip;	//the ship selected from spacedock
	ShipSave tradeShip;
	ShipSave homeSave; 
	
	ArrayList<ShipSave> shipSelect;
	
	SavedGameState currentSave; //the actual save that the above represents
	SavedGameState tradeSave; 
	
	ShipState currentState;		//the shipstate for the currentsave	
	ShipState tradeState;		
	
	File currentPath;	//store the getfilepath when the ships become saves...
	File tradePath;

	BufferedImage bg  = null;
	ImageIcon img = null;
	
	//Any way to make less of these fields / is it bad there are so many?
	JButton shipSelectJB = null;
	JButton saveJB = null;
	
	JButton shipWeaponJB = null;
	JButton shipDroneJB = null;
	JButton shipAugJB = null;
	JButton shipCrewJB = null;
	
	JButton tradeWeaponJB = null;
	JButton tradeAugJB = null;
	JButton tradeDroneJB = null;
	JButton tradeCrewJB = null;
	
	JComboBox shipSelectCB = null;
	
	JComboBox shipWeaponsCB = null;
	JComboBox shipDroneCB = null;
	JComboBox shipAugCB = null;
	JComboBox shipCrewCB = null;
	
	JComboBox tradeWeaponsCB = null;
	JComboBox tradeAugCB = null;
	JComboBox tradeDroneCB = null;
	JComboBox tradeCrewCB = null;
	
	
	
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
		self = this;
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
			currentPath = currentShip.getshipFilePath();
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
			homeSave = shipParser.readShipSave(new ShipSave(FTLHomeworld.homeworld_save));
			//System.out.println("homeSave= "+ homeSave);
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//now, let's make the UI
		setPreferredSize(new Dimension(1280, 720));
		setLayout(null);
		String s;
		
		//Create shipSelect
		shipSelect = new ArrayList<ShipSave>();
		shipSelect.add(homeSave);
		shipSelect.addAll(SpaceDockUI.myShips);
		
		//display shipSelect	
		shipSelectCB = new JComboBox(shipToString(shipSelect).toArray());	
		shipSelectCB.setBounds(1002, 97, 140, 20);
		add(shipSelectCB);
		
		//ShipSelect button
		shipSelectJB = new JButton("Pick");
		shipSelectJB.setBounds(1145, 97, 65, 20);
		shipSelectJB.addActionListener(this);
		add(shipSelectJB);
		
		//Save button
		saveJB = new JButton("Save");
		saveJB.setBounds(990, 27, 65, 20);
		saveJB.addActionListener(this);
		saveJB.setToolTipText("Save the changes you've made");
		add(saveJB);
		
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
		
		//display weapons' names
		shipWeaponsCB = new JComboBox(weaponToString(currentState).toArray());
		shipWeaponsCB.setBounds(430, 550, 200, 20);
		add(shipWeaponsCB);
		
		shipWeaponJB = new JButton("Send");
		shipWeaponJB.setBounds(565, 530, 65, 20);
		shipWeaponJB.addActionListener(this);
		add(shipWeaponJB);
			
		
		//display shipAugment names
		shipAugCB = new JComboBox(augmentToString(currentState).toArray());
		shipAugCB.setBounds(1010, 550, 200, 20);
		add(shipAugCB);
		
		shipAugJB = new JButton("Send");
		shipAugJB.setBounds(1145, 530, 65, 20);
		shipAugJB.addActionListener(this);
		add(shipAugJB);
		
		//display shipDrone names 
		shipDroneCB = new JComboBox(droneToString(currentState).toArray());
		shipDroneCB.setBounds(430, 650, 200, 20);
		add(shipDroneCB);
		
		shipDroneJB = new JButton("Send");
		shipDroneJB.setBounds(565, 630, 65, 20);
		shipDroneJB.addActionListener(this);
		add(shipDroneJB);
		
		//display shipCrew names 
		shipCrewCB = new JComboBox(crewToString(currentState).toArray());
		shipCrewCB.setBounds(45, 650, 125, 20);
		add(shipCrewCB);
		
		shipCrewJB = new JButton("Send");
		shipCrewJB.setBounds(105, 630, 65, 20);
		shipCrewJB.addActionListener(this);
		add(shipCrewJB);
		
		//TODO display shipCargo names 
//		ArrayList<String> shipCargoList = new ArrayList<String>();
//		shipCargoList.addAll(shipWeaponList);
//		shipCargoList.addAll(shipAugList);
//		shipCargoList.addAll(shipDroneList);
//		shipCargoList.addAll(shipCrewList);
//		JComboBox shipCargoCB = new JComboBox(shipCargoList.toArray());
//		shipCargoCB.setBounds(45, 550, 125, 20);
//		add(shipCargoCB);
		
	}
	public void tradeShipInit() {
		//remove components
		if (tradeAugCB != null){ this.remove(tradeAugCB);}
		if (tradeAugJB != null){this.remove(tradeAugJB); }
		if (tradeCrewCB != null){this.remove(tradeCrewCB);}
		if (tradeCrewJB != null){this.remove(tradeCrewJB);}
		if (tradeDroneCB != null){this.remove(tradeDroneCB);}
		if (tradeDroneJB != null){this.remove(tradeDroneJB);}
		if (tradeWeaponJB != null){this.remove(tradeWeaponJB);}
		if (tradeWeaponsCB != null){this.remove(tradeWeaponsCB);}
		
		//Populate with Tradeship data
		tradeState = tradeSave.getPlayerShipState();
		JSpinner tradeScrapSP = new JSpinner();
		tradeScrapSP.setBounds(110, 260, 60, 20);
		tradeScrapSP.setValue(tradeSave.getPlayerShipState().getScrapAmt());
		add(tradeScrapSP);
		
		JSpinner tradeFuelSP = new JSpinner();
		tradeFuelSP.setBounds(148, 423, 60, 20);
		tradeFuelSP.setValue(tradeSave.getPlayerShipState().getFuelAmt());
		add(tradeFuelSP);
		
		//display weapons' names
		tradeWeaponsCB = new JComboBox(weaponToString(tradeState).toArray());
		tradeWeaponsCB.setBounds(720, 550, 200, 20);
		add(tradeWeaponsCB);
		
		tradeWeaponJB = new JButton("Send");
		tradeWeaponJB.setBounds(720, 530, 65, 20);
		tradeWeaponJB.addActionListener(this);
		add(tradeWeaponJB);
		
		tradeAugCB = new JComboBox(augmentToString(tradeState).toArray());
		tradeAugCB.setBounds(1010, 650, 200, 20);
		add(tradeAugCB);
		
		tradeAugJB = new JButton("Send");
		tradeAugJB.setBounds(1145, 630, 65, 20);
		tradeAugJB.addActionListener(this);
		add(tradeAugJB);
		
		tradeDroneCB = new JComboBox(droneToString(tradeState).toArray());
		tradeDroneCB.setBounds(720, 650, 200, 20);
		add(tradeDroneCB);
		
		tradeDroneJB = new JButton("Send");
		tradeDroneJB.setBounds(720, 630, 65, 20);
		tradeDroneJB.addActionListener(this);
		add(tradeDroneJB);
		
		tradeCrewCB = new JComboBox(crewToString(tradeState).toArray());
		tradeCrewCB.setBounds(230, 650, 125, 20);
		add(tradeCrewCB);
		
		tradeCrewJB = new JButton("Send");
		tradeCrewJB.setBounds(230, 630, 65, 20);
		tradeCrewJB.addActionListener(this);
		add(tradeCrewJB);
		
		//TODO
		JComboBox tradeCargoCB = new JComboBox();
		tradeCargoCB.setBounds(230, 550, 125, 20);
		add(tradeCargoCB);
		
		
		
		
	}
	
	//TODO
	//To String methods
	//Ship select
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
	public SavedGameState getSelectedShip(JComboBox box, ArrayList<ShipSave> list) {
		SavedGameState Sgs = null;
		tradeShip = list.get(box.getSelectedIndex());
		SavedGameParser parser = new SavedGameParser();
		tradePath = tradeShip.getshipFilePath();
		try {
		Sgs = parser.readSavedGame(tradeShip.getshipFilePath());
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		//need finally?
		return Sgs;
	}
	
	//Convert weapons list to string array for combo box
	public ArrayList<String> weaponToString(ShipState state) {
		ArrayList<String> al = new ArrayList<String>();	
		String s = new String("No Weapons!");
		if (state.getWeaponList().size() > 0){
			for (WeaponState w: state.getWeaponList()) {
				s = DataManager.get().getWeapon(w.getWeaponId()).getTitle();
				al.add(s);
			}
		}
		else {
			al.add(s);
		}
		return al;
	}	
	
	//Convert augment list to string array for combo box
	public ArrayList<String> augmentToString(ShipState state) {
		ArrayList<String> al = new ArrayList<String>();	
		String s = new String("No Augments!");
		if (state.getAugmentIdList().size() > 0){
			for (String aug: state.getAugmentIdList()) {
				s = DataManager.get().getAugment(aug).getTitle();;
				al.add(s);
			}
		}
		else {
			al.add(s);
		}
		return al;
	}	
	
	//Convert drone list to string array for combo box
	public ArrayList<String> droneToString(ShipState state) {
		ArrayList<String> al = new ArrayList<String>();	
		String s = new String("No Drones!");
		if (state.getDroneList().size() > 0){
			for (DroneState d: state.getDroneList()) {
				s = DataManager.get().getDrone(d.getDroneId()).getTitle();
				al.add(s);
			}
		}
		else {
			al.add(s);
		}
		return al;
	}	
	//Convert crew list to string array for combo box
	public ArrayList<String> crewToString(ShipState state) {
		ArrayList<String> al = new ArrayList<String>();	
		String s = new String("No Crew!");
		if (state.getCrewList().size() > 0){
			for (CrewState c: state.getCrewList()) {
				al.add(c.getName());
			}
		}
		else {
			al.add(s);
		}
		return al;
	}	
	
	//TODO Event Handlers
	public void actionPerformed(ActionEvent ae) {				
		JButton o = (JButton) ae.getSource();

		JComboBox startBox = null;
		JComboBox destBox = null;
		ShipState startState = null;
		ShipState destState = null;
		
	//	System.out.println("pressed: " + o.getText());
		
		//Save
		if (o == saveJB){
			if (currentPath == null) {
				FTLHomeworld.showErrorDialog("Can't save because there is no current ship!");
			}
			else {
				OutputStream out = null;
				SavedGameParser parser = new SavedGameParser();
				try {
					out = new FileOutputStream(currentPath);
					parser.writeSavedGame(out, currentSave);
					
					out = new FileOutputStream(tradePath);
					parser.writeSavedGame(out, tradeSave);
					
				} catch (FileNotFoundException e) {
					// Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// Auto-generated catch block
					e.printStackTrace();
				} finally {
					if ( out != null ) { try { out.close(); } catch (IOException e) {e.printStackTrace();} }
				}
				System.out.println("Saved!");
			}	
			
		}
		//pick ship
		else if (o == shipSelectJB) {
			tradeSave = getSelectedShip(shipSelectCB, shipSelect);		
			tradeShipInit();
			self.repaint();
		//	System.out.println("New ship picked");
		}
		
		
		
		//Trading weapons
		else if (o == shipWeaponJB || o== tradeWeaponJB){
			
			if (o == shipWeaponJB) {
				startBox = shipWeaponsCB;
				destBox = tradeWeaponsCB;
				startState = currentState;
				destState = tradeState;
			}
			else {
				startBox = tradeWeaponsCB;
				destBox = shipWeaponsCB;
				startState = tradeState;
				destState = currentState;
			}
			if (startBox.getSelectedItem().equals("No Weapons!")){
				FTLHomeworld.showErrorDialog("No weapon to send!");
			}
			// only check for available room if dest is not Homeworld.sav
			//size based on xml (weaponSlots in ShipBlueprint)
			else if (destState.getWeaponList().size() >= 
					DataManager.get().getShip(destState.getShipBlueprintId()).getWeaponSlots() 
					&& ((destState == tradeState && tradeShip != homeSave)) || destState == currentState) {
				FTLHomeworld.showErrorDialog("No room for more weapons!");
			}
			else {
				int i = startBox.getSelectedIndex();
				WeaponState w = startState.getWeaponList().get(i);
				startState.getWeaponList().remove(w);
				destState.getWeaponList().add(w);
				if (destBox.getItemAt(0).equals("No Weapons!")) {
					destBox.removeItemAt(0);
				}
				destBox.addItem(startBox.getSelectedItem());
				startBox.removeItemAt(i);
				if (startState.getWeaponList().size() == 0) {
				//	System.out.println("No Weapons!");
					startBox.addItem("No Weapons!");
				}
			//	System.out.println("transfered Weapon!" + currentState.getWeaponList().size());
			}
		}

		//Trading augments
		else if (o == shipAugJB || o== tradeAugJB){
			
			if (o == shipAugJB) {
				startBox = shipAugCB;
				destBox = tradeAugCB;
				startState = currentState;
				destState = tradeState;
			}
			else {
				startBox = tradeAugCB;
				destBox = shipAugCB;
				startState = tradeState;
				destState = currentState;
			}
			if (startBox.getSelectedItem().equals("No Augments!")){
				FTLHomeworld.showErrorDialog("No augment to send!");
			}
			// only check for available room if dest is not Homeworld.sav
			else if (destState.getAugmentIdList().size() >= 3 
					&& ((destState == tradeState && tradeShip != homeSave)) || destState == currentState) {
				FTLHomeworld.showErrorDialog("No room for more augments!");
			}
			else {
				int i = startBox.getSelectedIndex();
				String s = startState.getAugmentIdList().get(i);
				startState.getAugmentIdList().remove(s);
				destState.getAugmentIdList().add(s);
				if (destBox.getItemAt(0).equals("No Augments!")) {
					destBox.removeItemAt(0);
				}
				destBox.addItem(startBox.getSelectedItem());
				startBox.removeItemAt(i);
				if (startState.getAugmentIdList().size() == 0) {
					startBox.addItem("No Augments!");
				}
			}
		}
		//Trading drones
		else if (o == shipDroneJB || o== tradeDroneJB){
			
			if (o == shipDroneJB) {
				startBox = shipDroneCB;
				destBox = tradeDroneCB;
				startState = currentState;
				destState = tradeState;
			}
			else {
				startBox = tradeDroneCB;
				destBox = shipDroneCB;
				startState = tradeState;
				destState = currentState;
			}
			if (startBox.getSelectedItem().equals("No Drones!")){
				FTLHomeworld.showErrorDialog("No drone to send!");
			}
			// only check for available room if dest is not Homeworld.sav
			//TODO droneSlotes in ShipBlueprint.xml
			else if ((destState.getDroneList().size() >= 
					DataManager.get().getShip(destState.getShipBlueprintId()).getDroneSlots())
					&& ((destState == tradeState && tradeShip != homeSave)) || destState == currentState) {
				FTLHomeworld.showErrorDialog("No room for more drones!");
			}
			else {
				int i = startBox.getSelectedIndex();
				DroneState d = startState.getDroneList().get(i);
				startState.getDroneList().remove(d);
				destState.getDroneList().add(d);
				if (destBox.getItemAt(0).equals("No Drones!")) {
					destBox.removeItemAt(0);
				}
				destBox.addItem(startBox.getSelectedItem());
				startBox.removeItemAt(i);
				if (startState.getDroneList().size() == 0) {
					startBox.addItem("No Drones!");
				}
			}
		}
		//Trading crew
		else if (o == shipCrewJB || o== tradeCrewJB){
			
			if (o == shipCrewJB) {
				startBox = shipCrewCB;
				destBox = tradeCrewCB;
				startState = currentState;
				destState = tradeState;
			}
			else {
				startBox = tradeCrewCB;
				destBox = shipCrewCB;
				startState = tradeState;
				destState = currentState;
			}
			if (startBox.getSelectedItem().equals("No Crew!")){
				FTLHomeworld.showErrorDialog("No drone to send!");
			}
			// only check for available room if dest is not Homeworld.sav
			else if (destState.getCrewList().size() >= 8 
					&& ((destState == tradeState && tradeShip != homeSave)) || destState == currentState) {
				FTLHomeworld.showErrorDialog("No room for more crew!");
			}
			else {
				int i = startBox.getSelectedIndex();
				CrewState cs = startState.getCrewList().get(i);
				startState.getCrewList().remove(cs);
				destState.getCrewList().add(cs);
				if (destBox.getItemAt(0).equals("No Crew!")) {
					destBox.removeItemAt(0);
				}
				destBox.addItem(startBox.getSelectedItem());
				startBox.removeItemAt(i);
				if (startState.getCrewList().size() == 0) {
					startBox.addItem("No Crew!");
				}
			}
		}
	}	
	
	

}
