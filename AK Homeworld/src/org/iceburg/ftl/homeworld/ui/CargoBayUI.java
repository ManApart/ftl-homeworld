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
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

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
import org.iceburg.ftl.homeworld.ui.ComboItem.WeaponItem;
import org.iceburg.ftl.homeworld.core.FTLHomeworld;
import org.iceburg.ftl.homeworld.model.ShipSave;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

//TODO
//Cargobay multiple items: Hull missle x2
//Need Missiles and drone parts!

//ShipState readship() is line 247 of SavedGameParser
public class CargoBayUI extends JPanel implements ActionListener {
	CargoBayUI self;

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
	JLabel shipName = null;
	
	JSpinner shipScrapSP = null;
	JSpinner shipFuelSP = null;
	JLabel tradeScrapSP = null;
	JLabel tradeFuelSP = null;
	
	JButton shipSelectJB = null;
	JButton saveJB = null;
	JButton refreshJB = null;
	
	JButton shipWeaponJB = null;
	JButton shipDroneJB = null;
	JButton shipAugJB = null;
	JButton shipCrewJB = null;
	JButton shipCargoJB = null;
	
	JButton tradeWeaponJB = null;
	JButton tradeAugJB = null;
	JButton tradeDroneJB = null;
	JButton tradeCrewJB = null;
	JButton tradeCargoJB = null;
	
	JComboBox shipSelectCB = null;
	
	JComboBox shipWeaponsCB = null;
	JComboBox shipDroneCB = null;
	JComboBox shipAugCB = null;
	JComboBox shipCrewCB = null;
	JComboBox shipCargoCB = null;
	
	JComboBox tradeWeaponsCB = null;
	JComboBox tradeAugCB = null;
	JComboBox tradeDroneCB = null;
	JComboBox tradeCrewCB = null;
	JComboBox tradeCargoCB = null;
	
	//Hashmaps
	HashMap<String, Integer> shipWeaponMap;
	HashMap<String, Integer> tradeWeaponMap;
	
	
	
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
  
    
	public CargoBayUI() {
		this.init();
	}	
	public void init() {
		self = this;
		this.removeAll();
		
		//Get the Homeworld save file
		ShipSaveParser shipParser = new ShipSaveParser();
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
		shipSelect.remove(HomeworldFrame.currentShip);
		
		//display shipSelect	
		shipSelectCB = new JComboBox(shipToString(shipSelect).toArray());	
		shipSelectCB.setBounds(1002, 97, 140, 20);
		shipSelectCB.setToolTipText("List of your ships");
		add(shipSelectCB);
		
		//ShipSelect button
		shipSelectJB = new JButton("Pick");
		shipSelectJB.setBounds(1145, 97, 65, 20);
		shipSelectJB.addActionListener(this);
		shipSelectJB.setToolTipText("Set this ship as your trading partner");
		add(shipSelectJB);
		
		//Save button
		saveJB = new JButton("Save");
		saveJB.setBounds(990, 27, 65, 20);
		saveJB.addActionListener(this);
		saveJB.setToolTipText("Save the unsaved changes you've made");
		add(saveJB);
		
		//reset button
		refreshJB = new JButton("Reset");
		refreshJB.setBounds(1108, 27, 100, 20);
		refreshJB.addActionListener(this);
		refreshJB.setToolTipText("Cancel the unsaved changes you've made");
		add(refreshJB);
		
	}
	
	
	
	
	public void currentShipInit() {
		//remove components
		if (shipAugCB != null){ this.remove(shipAugCB);}
		if (shipAugJB != null){this.remove(shipAugJB); }
		if (shipCrewCB != null){this.remove(shipCrewCB);}
		if (shipCrewJB != null){this.remove(shipCrewJB);}
		if (shipDroneCB != null){this.remove(shipDroneCB);}
		if (shipDroneJB != null){this.remove(shipDroneJB);}
		if (shipWeaponJB != null){this.remove(shipWeaponJB);}
		if (shipWeaponsCB != null){this.remove(shipWeaponsCB);}
		if (shipCargoJB != null){this.remove(shipCargoJB);}
		if (shipCargoCB != null){this.remove(shipCargoCB);}
		if (shipName != null){this.remove(shipName);}
		if (shipScrapSP != null){this.remove(shipScrapSP);}
		if (shipFuelSP != null){this.remove(shipFuelSP);}
		
		SavedGameParser parser = new SavedGameParser();
		//get the current ship we're working with... If there is no current ship, set some null values
		if (HomeworldFrame.currentShip == null || (HomeworldFrame.currentShip.getshipFilePath().exists() == false)) {
			currentSave = new SavedGameState();
			currentSave.setPlayerShipName("No Ship Selected");
			currentState = new ShipState("No Ship Selected", new ShipBlueprint(), false);
		}
		else {	
			currentPath = HomeworldFrame.currentShip.getshipFilePath();
			try {
				currentSave = parser.readSavedGame(HomeworldFrame.currentShip.getshipFilePath());
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
			
			currentState = currentSave.getPlayerShipState();
		}
		
		
		//Populate with currentSave Data
		shipName = new JLabel(currentSave.getPlayerShipName());
		shipName.setForeground(Color.WHITE);
		shipName.setBounds(50, 200, 97, 14);
		shipName.setToolTipText("You're current ship");
		add(shipName);
		
		
		shipScrapSP = new JSpinner(new SpinnerNumberModel(currentState.getScrapAmt(), 0, null, 1));
		shipScrapSP.setBounds(110, 305, 60, 20);
		shipScrapSP.addChangeListener(listener);
		shipScrapSP.setToolTipText("Current ship's scrap");
		JFormattedTextField txt = ((JSpinner.NumberEditor) shipScrapSP.getEditor()).getTextField();
		((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
		add(shipScrapSP);
		
		shipFuelSP = new JSpinner(new SpinnerNumberModel(currentState.getFuelAmt(), 0, null, 1));
		shipFuelSP.setBounds(33, 423, 60, 20);
		shipFuelSP.addChangeListener(listener);
		shipFuelSP.setToolTipText("Current ship's fuel");
		txt = ((JSpinner.NumberEditor) shipFuelSP.getEditor()).getTextField();
		((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
		add(shipFuelSP);
		
		//display weapons' names
		shipWeaponMap = new HashMap<String, Integer>();
		shipWeaponsCB = new JComboBox(weaponToString(currentState, shipWeaponMap).toArray());
		shipWeaponsCB.setBounds(430, 550, 200, 20);
		shipWeaponsCB.setToolTipText("Current ship's weapons");
		add(shipWeaponsCB);
		//System.out.println(shipWeaponMap.toString());
		
		shipWeaponJB = new JButton("Send");
		shipWeaponJB.setBounds(565, 530, 65, 20);
		shipWeaponJB.addActionListener(this);
		shipWeaponJB.setToolTipText("Send weapon to trade partner");
		add(shipWeaponJB);
			
		
		//display shipAugment names
		shipAugCB = new JComboBox(augmentToString(currentState).toArray());
		shipAugCB.setBounds(1010, 550, 200, 20);
		shipAugCB.setToolTipText("Current ship's weapons");
		add(shipAugCB);
		
		shipAugJB = new JButton("Send");
		shipAugJB.setBounds(1145, 530, 65, 20);
		shipAugJB.addActionListener(this);
		shipAugJB.setToolTipText("Send augment to trade partner");
		add(shipAugJB);
		
		//display shipDrone names 
		shipDroneCB = new JComboBox(droneToString(currentState).toArray());
		shipDroneCB.setBounds(430, 650, 200, 20);
		shipDroneCB.setToolTipText("Current ship's drones");
		add(shipDroneCB);
		
		shipDroneJB = new JButton("Send");
		shipDroneJB.setBounds(565, 630, 65, 20);
		shipDroneJB.addActionListener(this);
		shipDroneJB.setToolTipText("Send drone to trade partner");
		add(shipDroneJB);
		
		//display shipCrew names 
		shipCrewCB = new JComboBox(crewToString(currentState).toArray());
		shipCrewCB.setBounds(45, 650, 125, 20);
		shipCrewCB.setToolTipText("Current ship's crew");
		add(shipCrewCB);
		
		shipCrewJB = new JButton("Send");
		shipCrewJB.setBounds(105, 630, 65, 20);
		shipCrewJB.addActionListener(this);
		shipCrewJB.setToolTipText("Send crew to trade partner");
		add(shipCrewJB);
		
		// display shipCargo names 	
		shipCargoCB = new JComboBox(cargoToString(currentSave).toArray());
		shipCargoCB.setBounds(45, 550, 125, 20);
		shipCargoCB.setToolTipText("Current ship's cargo");
		add(shipCargoCB);
		
		shipCargoJB = new JButton("Send");
		shipCargoJB.setBounds(105, 530, 65, 20);
		shipCargoJB.addActionListener(this);
		shipCargoJB.setToolTipText("Send cargo to trade partner (and sort)");
		add(shipCargoJB);
		
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
		if (tradeCargoJB != null){this.remove(tradeCargoJB);}
		if (tradeCargoCB != null){this.remove(tradeCargoCB);}
		if (tradeScrapSP != null){this.remove(tradeScrapSP);}
		if (tradeFuelSP != null){this.remove(tradeFuelSP);}
		
		//Populate with Tradeship data
		tradeSave = getSelectedShip(shipSelectCB, shipSelect);
		tradeState = tradeSave.getPlayerShipState();
	
		tradeScrapSP = new JLabel("" + tradeSave.getPlayerShipState().getScrapAmt());
		tradeScrapSP.setBounds(110, 260, 60, 20);
		tradeScrapSP.setOpaque(true);
		tradeScrapSP.setToolTipText("Trade partner's scrap");
		add(tradeScrapSP);
		
		tradeFuelSP = new JLabel("" + tradeSave.getPlayerShipState().getFuelAmt());
		tradeFuelSP.setBounds(148, 423, 60, 20);
		tradeFuelSP.setOpaque(true);
		tradeFuelSP.setToolTipText("Trade partner's fuel");
		add(tradeFuelSP);
		
		//display weapons' names
		tradeWeaponMap = new HashMap<String, Integer>();
		tradeWeaponsCB = new JComboBox(weaponToString(tradeState, tradeWeaponMap).toArray());
		tradeWeaponsCB.setBounds(720, 550, 200, 20);
		tradeWeaponsCB.setToolTipText("Trade partner's weapons");
		add(tradeWeaponsCB);
		//System.out.println(tradeWeaponMap.toString());
		
		tradeWeaponJB = new JButton("Send");
		tradeWeaponJB.setBounds(720, 530, 65, 20);
		tradeWeaponJB.addActionListener(this);
		tradeWeaponJB.setToolTipText("Send weapon to current ship");
		add(tradeWeaponJB);
		
		tradeAugCB = new JComboBox(augmentToString(tradeState).toArray());
		tradeAugCB.setBounds(1010, 650, 200, 20);
		tradeAugCB.setToolTipText("Trade partner's augments");
		add(tradeAugCB);
		
		tradeAugJB = new JButton("Send");
		tradeAugJB.setBounds(1145, 630, 65, 20);
		tradeAugJB.addActionListener(this);
		tradeAugJB.setToolTipText("Send augment to current ship");
		add(tradeAugJB);
		
		tradeDroneCB = new JComboBox(droneToString(tradeState).toArray());
		tradeDroneCB.setBounds(720, 650, 200, 20);
		tradeDroneCB.setToolTipText("Trade partner's drones");
		add(tradeDroneCB);
		
		tradeDroneJB = new JButton("Send");
		tradeDroneJB.setBounds(720, 630, 65, 20);
		tradeDroneJB.addActionListener(this);
		tradeDroneJB.setToolTipText("Send drone to current ship");
		add(tradeDroneJB);
		
		tradeCrewCB = new JComboBox(crewToString(tradeState).toArray());
		tradeCrewCB.setBounds(230, 650, 125, 20);
		tradeCrewCB.setToolTipText("Trade partner's crew");
		add(tradeCrewCB);
		
		tradeCrewJB = new JButton("Send");
		tradeCrewJB.setBounds(230, 630, 65, 20);
		tradeCrewJB.addActionListener(this);
		tradeCrewJB.setToolTipText("Send crew to current ship");
		add(tradeCrewJB);
		
		// Cargo for Trade
		if (tradeShip == homeSave) {
			ArrayList<String> al = new ArrayList<String>();
			String s = new String("Already Sorted");
			al.add(s);
			tradeCargoCB = new JComboBox(al.toArray());
			tradeCargoCB.setToolTipText("Spacedock storage sorts items to proper category");
		}
		else {
			tradeCargoCB = new JComboBox(cargoToString(tradeSave).toArray());
			tradeCargoCB.setToolTipText("Trade partner's cargo");
		}
		tradeCargoCB.setBounds(230, 550, 125, 20);
		add(tradeCargoCB);
		
		tradeCargoJB = new JButton("Send");
		tradeCargoJB.setBounds(230, 530, 65, 20);
		tradeCargoJB.addActionListener(this);
		tradeCargoJB.setToolTipText("Send cargo to current ship (and sort)");
		add(tradeCargoJB);
		
		
	}
	public WeaponItem getWeaponItem(JComboBox box, String id){
		int i = 0;
		WeaponItem wi= null;
		while (i < box.getItemCount()){
			wi = (WeaponItem) box.getItemAt(i);
			if (wi.getWeapon().getWeaponId().equals(id)){
				return wi;
			}
			i = (i + 1);
		}
		
		return wi;
	}
	//Returns the first weaponstate with this ID
	public WeaponState getWSFromID( ArrayList<WeaponState> weaponList, String id){
		int i = 0;
		WeaponState ws= null;
		while (i < weaponList.size()){
			ws = weaponList.get(i);
			if (ws.getWeaponId().equals(id)){
				return ws;
			}
			i = (i + 1);
		}
		
		return ws;
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
	
	public ArrayList<WeaponItem> hashToString(HashMap<String, Integer> map){
		ArrayList<WeaponItem> al = new ArrayList<WeaponItem>();
		//convert hashmap to string
		Iterator it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	       // System.out.println(pairs.getKey() + " = " + pairs.getValue());
	        WeaponState w = new WeaponState((String)pairs.getKey(), false, 0);
	        al.add(new WeaponItem(w, DataManager.get().getWeapon(w.getWeaponId()).getTitle() 
	        		+ " x"+ pairs.getValue()));
	       // it.remove(); // avoids a ConcurrentModificationException
	    }
		return al;
	}
	
//	//Convert weapons list to string array for combo box
//	public ArrayList<String> weaponToString(ShipState state) {
//		ArrayList<String> al = new ArrayList<String>();	
//		String s = new String("No Weapons!");
//		if (state.getWeaponList().size() > 0){
//			for (WeaponState w: state.getWeaponList()) {
//				s = DataManager.get().getWeapon(w.getWeaponId()).getTitle();
//				al.add(s);
//			}
//		}
//		else {
//			al.add(s);
//		}
//		return al;
//	}	
	//Convert weapons list to string array for combo box - use hashmap for count
	public ArrayList<WeaponItem> weaponToString(ShipState state, HashMap<String, Integer> map) {
		ArrayList<WeaponItem> al = new ArrayList<WeaponItem>();	
		if (state.getWeaponList().size() > 0){
			for (WeaponState w: state.getWeaponList()) {
				//multiple missles = Hull Missiles x2
				if (map.containsKey(w.getWeaponId())) {					
					//increase the item's count
					map.put(w.getWeaponId(), (map.get(w.getWeaponId()) + 1));
				}
				else {
					//add the first item
					map.put(w.getWeaponId(), 1);				
				}
				
			}
			al = hashToString(map);
		}
		else {
			al.add(new WeaponItem(new WeaponState(), "No Weapons!"));
		}
		//System.out.println(map.toString());
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
	//Convert cargo list to string array for combo box
	public ArrayList<String> cargoToString(SavedGameState save) {
		ArrayList<String> al = new ArrayList<String>();	
		String s = new String("No Cargo!");
			if (save.getCargoIdList().size() > 0){
				for (String st: save.getCargoIdList()) {
					//find out what type it is and add the appropriate name to the list
					if (DataManager.get().getAugment(st) != null) {
						al.add(DataManager.get().getAugment(st).getTitle());
					}
					else if (DataManager.get().getWeapon(st) != null) {
						al.add(DataManager.get().getWeapon(st).getTitle());
					}
					else if (DataManager.get().getDrone(st) != null) {
						al.add(DataManager.get().getDrone(st).getTitle());
					}
					
				}
			}
			else {
				al.add(s);
			}

		return al;
	}	
	
	//TODO bookmark: Trading methods
	
	//TODO Trade with weapon count
	//Trading weapons
	public void tradeWeapon(JComboBox startBox, JComboBox destBox, JComboBox destCargo, 
			ShipState startState, ShipState destState, 
			SavedGameState startSave, SavedGameState destSave,
			HashMap<String, Integer> startMap, HashMap<String, Integer> destMap) {
		if (((WeaponItem)startBox.getSelectedItem()).getTitle().equals("No Weapons!")){
			FTLHomeworld.showErrorDialog("No weapon to send!");
		}
		// only check for available room if dest is not Homeworld.sav
		//size based on xml (weaponSlots in ShipBlueprint)
		else if (destState.getWeaponList().size() >= 
				DataManager.get().getShip(destState.getShipBlueprintId()).getWeaponSlots() 
				&& ((destState == tradeState && tradeShip != homeSave) || destState == currentState)) 
		{
			//TODO if weapons are full, but cargo has room, prompt to move to cargobay
			if (destSave.getCargoIdList().size() < 4) {
				int response = JOptionPane.showConfirmDialog(null, "No more room for weapons, send to cargo?", "Move to cargo?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if ( response == JOptionPane.YES_OPTION ){
					int i = startBox.getSelectedIndex();
					//	move weapon
					WeaponState w = ((WeaponItem) startBox.getSelectedItem()).getWeapon();
					startState.getWeaponList().remove(w);
					destSave.addCargoItemId(w.getWeaponId());
					//TODO update the UI
					if (destCargo.getItemAt(0).equals("No Cargo!")) {
						destCargo.removeItemAt(0);
					} 
					destCargo.addItem(startBox.getSelectedItem());
					startBox.removeItemAt(i);
					if (startState.getWeaponList().size() == 0) {
						startBox.addItem(new WeaponItem(new WeaponState(), "No Weapons!"));
					}
				}

			}
			else {
				FTLHomeworld.showErrorDialog("No room for more weapons!");
			}
			
		}
		else {
			//move weapon
			//WeaponState w = ((WeaponItem) startBox.getSelectedItem()).getWeapon();
			WeaponState w = ((WeaponItem) startBox.getSelectedItem()).getWeapon();
			startState.getWeaponList().remove(getWSFromID(startState.getWeaponList(), w.getWeaponId()));
			destState.getWeaponList().add(w);
			//update UI
			if (((WeaponItem)destBox.getItemAt(0)).getTitle().equals("No Weapons!")) {
				destBox.removeItemAt(0);
			}
			//Must increase first
			//Increase count
			if (destMap.get(w.getWeaponId()) != null) {
				destMap.put(w.getWeaponId(), (destMap.get(w.getWeaponId()) + 1));
				((WeaponItem) getWeaponItem(destBox, w.getWeaponId())).setTitle(
						DataManager.get().getWeapon(w.getWeaponId()).getTitle() 
		        		+ " x"+ destMap.get(w.getWeaponId()));
				destBox.updateUI();
			}
			else {
				destMap.put(w.getWeaponId(), 1);
				destBox.addItem(new WeaponItem(w, DataManager.get().getWeapon(w.getWeaponId()).getTitle() 
		        		+ " x"+ destMap.get(w.getWeaponId())));
			}
			//Decrease count
			if (startMap.get(w.getWeaponId()) > 1) {
				startMap.put(w.getWeaponId(), (startMap.get(w.getWeaponId()) - 1));
				((WeaponItem) startBox.getSelectedItem()).setTitle(
						DataManager.get().getWeapon(w.getWeaponId()).getTitle() 
		        		+ " x"+ startMap.get(w.getWeaponId()));
				startBox.updateUI();
			}
			else {
				startMap.remove(w.getWeaponId());
				startBox.removeItem(startBox.getSelectedItem());
				if (startState.getWeaponList().size() == 0) {
					startBox.addItem(new WeaponItem(new WeaponState(), "No Weapons!"));
				}
			}
			
		//	self.updateUI();
		//	System.out.println("transfered Weapon!" + currentState.getWeaponList().size());
		}
	}
	public void tradeAug(JComboBox startBox, JComboBox destBox, JComboBox destCargo, 
			ShipState startState, ShipState destState, 
			SavedGameState startSave, SavedGameState destSave) {
		if (startBox.getSelectedItem().equals("No Augments!")){
			FTLHomeworld.showErrorDialog("No augment to send!");
		}
		// only check for available room if dest is not Homeworld.sav
		else if (destState.getAugmentIdList().size() >= 3 
				&& ((destState == tradeState && tradeShip != homeSave) || destState == currentState)) {
			//if augments are full, but cargo has room, prompt to move to cargobay
			if (destSave.getCargoIdList().size() < 4) {
				int response = JOptionPane.showConfirmDialog(null, "No more room for augments, send to cargo?", "Move to cargo?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if ( response == JOptionPane.YES_OPTION ){
					int i = startBox.getSelectedIndex();
					String s = startState.getAugmentIdList().get(i);
					startState.getAugmentIdList().remove(s);
					destSave.addCargoItemId(s);
					if (destCargo.getItemAt(0).equals("No Cargo!")) {
						destCargo.removeItemAt(0);
					}
					destCargo.addItem(startBox.getSelectedItem());
					startBox.removeItemAt(i);
					if (startState.getAugmentIdList().size() == 0) {
						startBox.addItem("No Augments!");
					}
				}

			}
			else {
				FTLHomeworld.showErrorDialog("No room for more augments!");
			}
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
	public void tradeDrone(JComboBox startBox, JComboBox destBox, JComboBox destCargo, 
			ShipState startState, ShipState destState, 
			SavedGameState startSave, SavedGameState destSave) {
		if (startBox.getSelectedItem().equals("No Drones!")){
			FTLHomeworld.showErrorDialog("No drone to send!");
		}
		// only check for available room if dest is not Homeworld.sav
		//droneSlotes in ShipBlueprint.xml
		else if ((destState.getDroneList().size() >= 
				DataManager.get().getShip(destState.getShipBlueprintId()).getDroneSlots())
				&& ((destState == tradeState && tradeShip != homeSave) || destState == currentState)) {
			//if drones are full, but cargo has room, prompt to move to cargobay
			if (destSave.getCargoIdList().size() < 4) {
				int response = JOptionPane.showConfirmDialog(null, "No more room for drones, send to cargo?", "Move to cargo?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if ( response == JOptionPane.YES_OPTION ){
					int i = startBox.getSelectedIndex();
					DroneState d = startState.getDroneList().get(i);
					startState.getDroneList().remove(d);
					destSave.addCargoItemId(d.getDroneId());
					if (destBox.getItemAt(0).equals("No Drones!")) {
						destBox.removeItemAt(0);
					}
					destCargo.addItem(startBox.getSelectedItem());
					startBox.removeItemAt(i);
					if (startState.getDroneList().size() == 0) {
						startBox.addItem("No Drones!");
					}
				}

			}
			else {
				FTLHomeworld.showErrorDialog("No room for more augments!");
			}
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
	
	
	
	
	//TODO Bookmark: Event Handlers
	
	ChangeListener listener = new ChangeListener() {
		  public void stateChanged(ChangeEvent e) {
		     JSpinner o = (JSpinner) e.getSource();
		     SpinnerModel spinStart = null;
		     JLabel spinEnd = null;
		     int otherVal = 0;
		     int startVal = 0;
		     boolean isScrap = false;

		    if (o == shipScrapSP){
	    		spinStart = shipScrapSP.getModel();
	    		spinEnd = tradeScrapSP;
	    		otherVal = tradeSave.getPlayerShipState().getScrapAmt();
	    		startVal = currentSave.getPlayerShipState().getScrapAmt();
	    		isScrap = true;
		    }
		    else if (o == shipFuelSP){
	    		spinStart = shipFuelSP.getModel();
	    		spinEnd = tradeFuelSP;
	    		otherVal = tradeSave.getPlayerShipState().getFuelAmt();
	    		startVal = currentSave.getPlayerShipState().getFuelAmt();
	    		isScrap = false;
		    }
		      
		    int currentVal = (Integer) spinStart.getValue();
		    int total = otherVal + startVal;
		    otherVal = (total - currentVal);
		    if (otherVal < 0){
		    	currentVal = (currentVal - Math.abs(otherVal));
		    	otherVal = 0;
		    	spinStart.setValue(currentVal);
		    }
		    
		    spinEnd.setText("" + otherVal);
		    if (isScrap == true){
		    	tradeSave.getPlayerShipState().setScrapAmt(otherVal);
		    	currentSave.getPlayerShipState().setScrapAmt(currentVal);
		    }
		    else {
		    	tradeSave.getPlayerShipState().setFuelAmt(otherVal);
		    	currentSave.getPlayerShipState().setFuelAmt(currentVal);
		    }
		   // System.out.println("Spin: "+ startVal + " Label: " + otherVal); 
		  }
		};

	public void actionPerformed(ActionEvent ae) {				
		JButton o = (JButton) ae.getSource();

		JComboBox startBox = null;
		JComboBox destBox = null;
		JComboBox destCargo = null;
		ShipState startState = null;
		ShipState destState = null;
		SavedGameState startSave = null;
		SavedGameState destSave = null;
		
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
				//Refresh everything, so we know it's in sync
				tradeShipInit();
				currentShipInit();
				self.updateUI();
				System.out.println("Saved!");
			}	
			
		}
		//pick ship
		else if (o == shipSelectJB) {
			tradeSave = getSelectedShip(shipSelectCB, shipSelect);		
			tradeShipInit();
			currentShipInit();
			self.updateUI();
		//	System.out.println("New ship picked");
		}
		//refresh
		else if (o == refreshJB) {
			currentShipInit();
			tradeShipInit();
			self.updateUI();
			//	System.out.println("refreshed");
		}
		
		
		
		//Trading weapons
		else if (o == shipWeaponJB || o== tradeWeaponJB){
			HashMap<String, Integer> startMap;
			HashMap<String, Integer> destMap;
			
			if (o == shipWeaponJB) {
				startBox = shipWeaponsCB;
				destBox = tradeWeaponsCB;
				destCargo = tradeCargoCB;
				startState = currentState;
				destState = tradeState;
				startSave = currentSave;
				destSave = tradeSave;
				startMap = shipWeaponMap;
				destMap = tradeWeaponMap;
			}
			else {
				startBox = tradeWeaponsCB;
				destBox = shipWeaponsCB;
				destCargo = shipCargoCB;
				startState = tradeState;
				destState = currentState;
				startSave = tradeSave;
				destSave = currentSave;
				startMap = tradeWeaponMap;
				destMap = shipWeaponMap;
			}
			//System.out.println(startMap.toString());
			//System.out.println(destMap.toString());
			tradeWeapon(startBox, destBox, destCargo, startState, destState, startSave, destSave, startMap, destMap);
		}

		//Trading augments
		else if (o == shipAugJB || o== tradeAugJB){
			
			if (o == shipAugJB) {
				startBox = shipAugCB;
				destBox = tradeAugCB;
				destCargo = tradeCargoCB;
				startState = currentState;
				destState = tradeState;
				startSave = currentSave;
				destSave = tradeSave;
			}
			else {
				startBox = tradeAugCB;
				destBox = shipAugCB;
				destCargo = shipCargoCB;
				startState = tradeState;
				destState = currentState;
				startSave = tradeSave;
				destSave = currentSave;
			}
			tradeAug(startBox, destBox, destCargo, startState, destState, startSave, destSave);
		}
		//Trading drones
		else if (o == shipDroneJB || o== tradeDroneJB){
			
			if (o == shipDroneJB) {
				startBox = shipDroneCB;
				destBox = tradeDroneCB;
				destCargo = tradeCargoCB;
				startState = currentState;
				destState = tradeState;
				startSave = currentSave;
				destSave = tradeSave;
			}
			else {
				startBox = tradeDroneCB;
				destBox = shipDroneCB;
				destCargo = shipCargoCB;
				startState = tradeState;
				destState = currentState;
				startSave = tradeSave;
				destSave = currentSave;
			}
			tradeDrone(startBox, destBox, destCargo, startState, destState, startSave, destSave);
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
				FTLHomeworld.showErrorDialog("No crew to send!");
			}
			// only check for available room if dest is not Homeworld.sav
			else if (destState.getCrewList().size() >= 8 
					&& ((destState == tradeState && tradeShip != homeSave) || destState == currentState)) {
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
		//TODO Trading cargo
		else if (o == shipCargoJB || o== tradeCargoJB){
			JComboBox cargoBox;
			JComboBox weaponBox;
			JComboBox droneBox;
			JComboBox augBox;
			
			if (o == shipCargoJB) {
				startBox = shipCargoCB;
				cargoBox = tradeCargoCB;
				weaponBox = tradeWeaponsCB;
				droneBox = tradeDroneCB;		
				augBox = tradeAugCB;
				
				startSave = currentSave;
				destSave = tradeSave;
				startState = currentState;
				destState = tradeState;
			}
			else {
				startBox = tradeCargoCB;
				cargoBox = tradeCargoCB;
				weaponBox = tradeWeaponsCB;
				droneBox = tradeDroneCB;		
				augBox = tradeAugCB;
				
				startSave = tradeSave;
				destSave = currentSave;
				startState = tradeState;
				destState = currentState;
				
				
			}
			if (startBox.getSelectedItem().equals("No Cargo!") 
					|| startBox.getSelectedItem().equals("Already Sorted") 
					|| startBox.getSelectedItem() == null){
				FTLHomeworld.showErrorDialog("No cargo to send!");
			}
			else {
				int i = startBox.getSelectedIndex();
				String id = startSave.getCargoIdList().get(i);
				if (DataManager.get().getAugment(id) != null) {
					String s = id; 
					if (destState.getAugmentIdList().size() >= 3 
							&& ((destState == tradeState && tradeShip != homeSave) || destState == currentState)) {
						//if augments are full, but cargo has room, prompt to move to cargobay
						if (destSave.getCargoIdList().size() < 4) {
							int response = JOptionPane.showConfirmDialog(null, "No more room for augments, send to cargo?", "Move to cargo?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							if ( response == JOptionPane.YES_OPTION ){
								startSave.getCargoIdList().remove(s);
								destSave.addCargoItemId(s);
								if (cargoBox.getItemAt(0).equals("No Cargo!")) {
									cargoBox.removeItemAt(0);
								}
								cargoBox.addItem(startBox.getSelectedItem());
								startBox.removeItemAt(i);
								if (startState.getAugmentIdList().size() == 0) {
									startBox.addItem("No Cargo!");
								}
							}

						}
						else {
							FTLHomeworld.showErrorDialog("No room for more augments!");
						}
					}
					else {
						startSave.removeCargoItemId(s);
						destState.getAugmentIdList().add(s);
						if (augBox.getItemAt(0).equals("No Augments!")) {
							augBox.removeItemAt(0);
						}
						augBox.addItem(startBox.getSelectedItem());
						startBox.removeItemAt(i);
						if (startSave.getCargoIdList().size() == 0) {
							startBox.addItem("No Cargo!");
						}
					}
				}
				else if (DataManager.get().getDrone(id) != null) {
					DroneState d = new DroneState();
					d.setDroneId(id);
					if ((destState.getDroneList().size() >= 
							DataManager.get().getShip(destState.getShipBlueprintId()).getDroneSlots())
							&& ((destState == tradeState && tradeShip != homeSave) || destState == currentState)) {
						//if drones are full, but cargo has room, prompt to move to cargobay
						if (destSave.getCargoIdList().size() < 4) {
							int response = JOptionPane.showConfirmDialog(null, "No more room for drones, send to cargo?", "Move to cargo?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							if ( response == JOptionPane.YES_OPTION ){
								startSave.removeCargoItemId(id);
								destSave.addCargoItemId(id);
								if (cargoBox.getItemAt(0).equals("No Drones!")) {
									cargoBox.removeItemAt(0);
								}
								cargoBox.addItem(startBox.getSelectedItem());
								startBox.removeItemAt(i);
								if (startSave.getCargoIdList().size() == 0) {
									startBox.addItem("No Cargo!");
								}
							}

						}
						else {
							FTLHomeworld.showErrorDialog("No room for more drones!");
						}
					}
					else {
						startSave.removeCargoItemId(id);
						destSave.addCargoItemId(id);
						if (droneBox.getItemAt(0).equals("No Drones!")) {
							droneBox.removeItemAt(0);
						}
						droneBox.addItem(startBox.getSelectedItem());
						startBox.removeItemAt(i);
						if (startSave.getCargoIdList().size() == 0) {
							startBox.addItem("No Cargo!");
						}
					}
				}
				else if (DataManager.get().getWeapon(id) != null) {
					//TODO Is this a proper way to convert between blueprints and objects? Test
					//DataManager.get().getWeapon(id)
					WeaponState w = new WeaponState();
					w.setWeaponId(id);
					
					if (destState.getWeaponList().size() >= 
							DataManager.get().getShip(destState.getShipBlueprintId()).getWeaponSlots() 
							&& ((destState == tradeState && tradeShip != homeSave) || destState == currentState)) 
					{
						//if weapons are full, but cargo has room, prompt to move to cargobay
						if (destSave.getCargoIdList().size() < 4) {
							int response = JOptionPane.showConfirmDialog(null, "No more room for weapons, send to cargo?", "Move to cargo?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							if ( response == JOptionPane.YES_OPTION ){
								startSave.removeCargoItemId(id);
								destSave.addCargoItemId(id);
								if (destCargo.getItemAt(0).equals("No Cargo!")) {
									destCargo.removeItemAt(0);
								} 
								destCargo.addItem(startBox.getSelectedItem());
								startBox.removeItemAt(i);
								if (startSave.getCargoIdList().size() == 0) {
									startBox.addItem("No Cargo!");
								}
							}

						}
						else {
							FTLHomeworld.showErrorDialog("No room for more weapons!");
						}
						
					}
					else {
						startSave.removeCargoItemId(id);
						destState.getWeaponList().add(w);
						if (weaponBox.getItemAt(0).equals("No Weapons!")) {
							weaponBox.removeItemAt(0);
						}
						weaponBox.addItem(startBox.getSelectedItem());
						startBox.removeItemAt(i);
						if (startSave.getCargoIdList().size() == 0) {
							startBox.addItem("No Cargo!");
						}
					}
				}
				
				else {
					FTLHomeworld.showErrorDialog("Cargo Item not found!");
				}
			}
			
		}
	}	
	
	

}
