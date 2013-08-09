package org.iceburg.ftl.homeworld.ui;

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

import org.iceburg.ftl.homeworld.parser.ShipSaveParser;
import org.iceburg.ftl.homeworld.resource.ResourceClass;
import org.iceburg.ftl.homeworld.core.FTLHomeworld;
import org.iceburg.ftl.homeworld.model.CrewComboItem;
import org.iceburg.ftl.homeworld.model.ShipSave;
import org.iceburg.ftl.homeworld.model.CargoComboItem;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

//TODO
//Cargobay multiple items: Hull missle x2 - cargo
//No crew check!

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
	JSpinner shipDPartsSP = null;
	JSpinner shipMPartsSP = null;
	JLabel tradeScrapSP = null;
	JLabel tradeFuelSP = null;
	JLabel tradeDPartsSP = null;
	JLabel tradeMPartsSP = null;
	
	JButton shipSelectJB = null;
	JButton saveJB = null;
	JButton refreshJB = null;
	
	JButton shipWeaponJB = null;
	JButton shipDroneJB = null;
	JButton shipAugJB = null;
	JButton shipCrewJB = null;
	JButton shipCrewInfoJB = null;
	JButton shipCargoJB = null;
	
	JButton tradeWeaponJB = null;
	JButton tradeAugJB = null;
	JButton tradeDroneJB = null;
	JButton tradeCrewJB = null;
	JButton tradeCrewInfoJB = null;
	JButton tradeCargoJB = null;
	
	JComboBox shipSelectCB = null;
	
	JComboBox shipWeaponCB = null;
	JComboBox shipDroneCB = null;
	JComboBox shipAugCB = null;
	JComboBox shipCrewCB = null;
	JComboBox shipCargoCB = null;
	
	JComboBox tradeWeaponCB = null;
	JComboBox tradeAugCB = null;
	JComboBox tradeDroneCB = null;
	JComboBox tradeCrewCB = null;
	JComboBox tradeCargoCB = null;
	
	//Hashmaps
	HashMap<String, Integer> shipWeaponMap;
	HashMap<String, Integer> shipAugmentMap;
	HashMap<String, Integer> shipDroneMap;
	HashMap<String, Integer> shipCargoMap;
	HashMap<String, Integer> tradeWeaponMap;
	HashMap<String, Integer> tradeAugmentMap;
	HashMap<String, Integer> tradeDroneMap;
	HashMap<String, Integer> tradeCargoMap;
	
	
	
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
		
		
		//missile count labels
		JLabel MLabelShip = new JLabel("Missile parts:");
		MLabelShip.setBounds(430, 550, 80, 20);
		MLabelShip.setOpaque(true);
		add(MLabelShip);
		
		JLabel MLabelTrade = new JLabel("Missile parts:");
		MLabelTrade.setBounds(720, 550, 80, 20);
		MLabelTrade.setOpaque(true);
		add(MLabelTrade);
		
		//drone part labels
		JLabel DLabelShip = new JLabel("Drone parts:");
		DLabelShip.setBounds(430, 650, 75, 20);
		DLabelShip.setOpaque(true);
		add(DLabelShip);
		
		JLabel DLabelTrade = new JLabel("Drone parts:");
		DLabelTrade.setBounds(720, 650, 75, 20);
		DLabelTrade.setOpaque(true);
		add(DLabelTrade);
	}
	
	
	
	
	public void currentShipInit() {
		//remove components
		if (shipAugCB != null){ this.remove(shipAugCB);}
		if (shipAugJB != null){this.remove(shipAugJB); }
		if (shipCrewCB != null){this.remove(shipCrewCB);}
		if (shipCrewJB != null){this.remove(shipCrewJB);}
		if (shipCrewInfoJB != null){this.remove(shipCrewInfoJB);}
		if (shipDroneCB != null){this.remove(shipDroneCB);}
		if (shipDroneJB != null){this.remove(shipDroneJB);}
		if (shipWeaponJB != null){this.remove(shipWeaponJB);}
		if (shipWeaponCB != null){this.remove(shipWeaponCB);}
		if (shipCargoJB != null){this.remove(shipCargoJB);}
		if (shipCargoCB != null){this.remove(shipCargoCB);}
		if (shipName != null){this.remove(shipName);}
		if (shipScrapSP != null){this.remove(shipScrapSP);}
		if (shipFuelSP != null){this.remove(shipFuelSP);}
		if (shipDPartsSP != null){this.remove(shipDPartsSP);}
		if (shipMPartsSP != null){this.remove(shipMPartsSP);}
		
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
		shipWeaponCB = new JComboBox(CargoComboItem.weaponToCombo(currentState, shipWeaponMap).toArray());
		shipWeaponCB.setBounds(430, 520, 200, 20);
		shipWeaponCB.setToolTipText("Current ship's weapons");
		add(shipWeaponCB);
		//System.out.println(shipWeaponMap.toString());
		
		shipWeaponJB = new JButton("Send");
		shipWeaponJB.setBounds(523, 495, 65, 20);
		shipWeaponJB.addActionListener(this);
		shipWeaponJB.setToolTipText("Send weapon to trade partner");
		add(shipWeaponJB);
		
		shipMPartsSP = new JSpinner(new SpinnerNumberModel(currentState.getMissilesAmt(), 0, null, 1));
		shipMPartsSP.setBounds(510, 550, 80, 20);
		shipMPartsSP.addChangeListener(listener);
		shipMPartsSP.setToolTipText("Current ship's missile count");
		txt = ((JSpinner.NumberEditor) shipMPartsSP.getEditor()).getTextField();
		((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
		shipMPartsSP.setToolTipText("Current ship's missile count");
		add(shipMPartsSP);
		
		//display shipAugment names
		shipAugmentMap = new HashMap<String, Integer>();
		shipAugCB = new JComboBox(CargoComboItem.augmentToCombo(currentState, shipAugmentMap).toArray());
		shipAugCB.setBounds(1010, 550, 200, 20);
		shipAugCB.setToolTipText("Current ship's weapons");
		add(shipAugCB);
		
		shipAugJB = new JButton("Send");
		shipAugJB.setBounds(1145, 530, 65, 20);
		shipAugJB.addActionListener(this);
		shipAugJB.setToolTipText("Send augment to trade partner");
		add(shipAugJB);
		
		//display shipDrone names 
		shipDroneMap = new HashMap<String, Integer>();
		shipDroneCB = new JComboBox(CargoComboItem.droneToCombo(currentState, shipDroneMap).toArray());
		shipDroneCB.setBounds(430, 630, 200, 20);
		shipDroneCB.setToolTipText("Current ship's drones");
		add(shipDroneCB);
		
		shipDroneJB = new JButton("Send");
		shipDroneJB.setBounds(523, 606, 65, 20);
		shipDroneJB.addActionListener(this);
		shipDroneJB.setToolTipText("Send drone to trade partner");
		add(shipDroneJB);
		
		shipDPartsSP = new JSpinner(new SpinnerNumberModel(currentState.getDronePartsAmt(), 0, null, 1));
		shipDPartsSP.setBounds(495, 650, 80, 20);
		shipDPartsSP.addChangeListener(listener);
		shipDPartsSP.setToolTipText("Current ship's missile count");
		txt = ((JSpinner.NumberEditor) shipDPartsSP.getEditor()).getTextField();
		((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
		shipDPartsSP.setToolTipText("Current ship's drone parts");
		add(shipDPartsSP);
		
		//display shipCrew names 
		shipCrewCB = new JComboBox(CrewComboItem.crewToCombo(currentState).toArray());
		shipCrewCB.setBounds(45, 650, 125, 20);
		shipCrewCB.setToolTipText("Current ship's crew");
		add(shipCrewCB);
		
		shipCrewJB = new JButton("Send");
		shipCrewJB.setBounds(105, 630, 65, 20);
		shipCrewJB.addActionListener(this);
		shipCrewJB.setToolTipText("Send crew member to trade partner");
		add(shipCrewJB);
		
		shipCrewInfoJB = new JButton("Info");
		shipCrewInfoJB.setBounds(105, 670, 65, 20);
		shipCrewInfoJB.addActionListener(this);
		shipCrewInfoJB.setToolTipText("Get info on this crew member");
		add(shipCrewInfoJB);
		
		// display shipCargo names 	
		shipCargoMap = new HashMap<String, Integer>();
		shipCargoCB = new JComboBox(CargoComboItem.cargoToCombo(currentSave, shipCargoMap).toArray());
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
		if (tradeCrewInfoJB != null){this.remove(tradeCrewInfoJB);}
		if (tradeDroneCB != null){this.remove(tradeDroneCB);}
		if (tradeDroneJB != null){this.remove(tradeDroneJB);}
		if (tradeWeaponJB != null){this.remove(tradeWeaponJB);}
		if (tradeWeaponCB != null){this.remove(tradeWeaponCB);}
		if (tradeCargoJB != null){this.remove(tradeCargoJB);}
		if (tradeCargoCB != null){this.remove(tradeCargoCB);}
		if (tradeScrapSP != null){this.remove(tradeScrapSP);}
		if (tradeFuelSP != null){this.remove(tradeFuelSP);}
		if (tradeDPartsSP != null){this.remove(tradeDPartsSP);}
		if (tradeMPartsSP != null){this.remove(tradeMPartsSP);}
		
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
		tradeWeaponCB = new JComboBox(CargoComboItem.weaponToCombo(tradeState, tradeWeaponMap).toArray());
		tradeWeaponCB.setBounds(720, 520, 200, 20);
		tradeWeaponCB.setToolTipText("Trade partner's weapons");
		add(tradeWeaponCB);
		//System.out.println(tradeWeaponMap.toString());
		
		tradeWeaponJB = new JButton("Send");
		tradeWeaponJB.setBounds(758, 495, 65, 20);
		tradeWeaponJB.addActionListener(this);
		tradeWeaponJB.setToolTipText("Send weapon to current ship");
		add(tradeWeaponJB);
		
		tradeMPartsSP = new JLabel("" + tradeSave.getPlayerShipState().getMissilesAmt());
		tradeMPartsSP.setBounds(800, 550, 80, 20);
		tradeMPartsSP.setOpaque(true);
		tradeMPartsSP.setToolTipText("Trade partner's missile count");
		add(tradeMPartsSP);
		
		tradeAugmentMap = new HashMap<String, Integer>();
		tradeAugCB = new JComboBox(CargoComboItem.augmentToCombo(tradeState, tradeAugmentMap).toArray());
		tradeAugCB.setBounds(1010, 650, 200, 20);
		tradeAugCB.setToolTipText("Trade partner's augments");
		add(tradeAugCB);
		
		tradeAugJB = new JButton("Send");
		tradeAugJB.setBounds(1145, 630, 65, 20);
		tradeAugJB.addActionListener(this);
		tradeAugJB.setToolTipText("Send augment to current ship");
		add(tradeAugJB);
		
		tradeDroneMap = new HashMap<String, Integer>();
		tradeDroneCB = new JComboBox(CargoComboItem.droneToCombo(tradeState, tradeDroneMap).toArray());
		tradeDroneCB.setBounds(720, 630, 200, 20);
		tradeDroneCB.setToolTipText("Trade partner's drones");
		add(tradeDroneCB);
		
		tradeDroneJB = new JButton("Send");
		tradeDroneJB.setBounds(758, 606, 65, 20);
		tradeDroneJB.addActionListener(this);
		tradeDroneJB.setToolTipText("Send drone to current ship");
		add(tradeDroneJB);
		
		tradeDPartsSP = new JLabel("" + tradeSave.getPlayerShipState().getDronePartsAmt());
		tradeDPartsSP.setBounds(795, 650, 80, 20);
		tradeDPartsSP.setOpaque(true);
		tradeDPartsSP.setToolTipText("Trade partner's drone parts");
		add(tradeDPartsSP);
		
		tradeCrewCB = new JComboBox(CrewComboItem.crewToCombo(tradeState).toArray());
		tradeCrewCB.setBounds(230, 650, 125, 20);
		tradeCrewCB.setToolTipText("Trade partner's crew");
		add(tradeCrewCB);
		
		tradeCrewJB = new JButton("Send");
		tradeCrewJB.setBounds(230, 630, 65, 20);
		tradeCrewJB.addActionListener(this);
		tradeCrewJB.setToolTipText("Send crew member to current ship");
		add(tradeCrewJB);
		
		tradeCrewInfoJB = new JButton("Info");
		tradeCrewInfoJB.setBounds(230, 670, 65, 20);
		tradeCrewInfoJB.addActionListener(this);
		tradeCrewInfoJB.setToolTipText("Get info on this crew member");
		add(tradeCrewInfoJB);
		
		// Cargo for Trade
		if (tradeShip == homeSave) {
			ArrayList<String> al = new ArrayList<String>();
			String s = new String("Already Sorted");
			al.add(s);
			tradeCargoCB = new JComboBox(al.toArray());
			tradeCargoCB.setToolTipText("Spacedock storage sorts items to proper category");
		}
		else {
			tradeCargoMap = new HashMap<String, Integer>();
			tradeCargoCB = new JComboBox(CargoComboItem.cargoToCombo(tradeSave, tradeCargoMap).toArray());
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
		

	
	//TODO Trade with weapon count
	//Trading weapons
	/**
     * Trade's a cargo item between ships
     * cargoType is 0 for weapons, 1 for augments, and 2 for drones
     */
	public void tradeCargo(JComboBox startBox, JComboBox destBox, JComboBox destCargo, 
			ShipState startState, ShipState destState, 
			SavedGameState startSave, SavedGameState destSave,
			HashMap<String, Integer> startMap, HashMap<String, Integer> destMap,
			int cargoType) {
		
		String id = ((CargoComboItem) startBox.getSelectedItem()).getId();
		String title = null;
		String warnText = null;
		int destRoom = 0;
		int availRoom = 0;
		boolean isCargo = false;
		
		if (cargoType == 0){
			title = DataManager.get().getWeapon(id).getTitle();
			warnText = new String("Weapons");
			destRoom = DataManager.get().getShip(destState.getShipBlueprintId()).getWeaponSlots();
			availRoom = destState.getWeaponList().size();
		}
		else if (cargoType == 1) {
			title = DataManager.get().getAugment(id).getTitle();
			warnText = new String("Augments");
			destRoom = 3;
			availRoom = destState.getAugmentIdList().size();
		}
		else {
			title = DataManager.get().getDrone(id).getTitle();
			warnText = new String("Drones");
			destRoom = DataManager.get().getShip(destState.getShipBlueprintId()).getDroneSlots();
			availRoom = destState.getDroneList().size();
		}
			
		if (((CargoComboItem)startBox.getSelectedItem()).getTitle().equals(String.format("No %s!", warnText))){
			FTLHomeworld.showErrorDialog(String.format("No %s to send!", warnText));
			return;
		}
		// only check for available room if dest is not Homeworld.sav
		//size based on xml (weaponSlots in ShipBlueprint)\
		//TODO
		else if (availRoom >= destRoom 
				&& ((destState == tradeState && tradeShip != homeSave) || destState == currentState)) 
		{
			//if destination is full, but cargo has room, prompt to move to cargobay
			if (destSave.getCargoIdList().size() < 4) {
				int response = JOptionPane.showConfirmDialog(null, String.format("No more room for %s, send to cargo?", warnText), "Move to cargo?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if ( response == JOptionPane.YES_OPTION ){
					destBox = destCargo;
					isCargo = true;
				}
				else {
					return;
				}

			}
			else {
				FTLHomeworld.showErrorDialog(String.format("No room for more %s!", warnText));
				return;
			}
			
		}
		//System.out.println("Transfering cargo");
		if (cargoType == 0){
			WeaponState w = new WeaponState(id, false, 0);		
			startState.getWeaponList().remove(CargoComboItem.getWSFromID(startState.getWeaponList(), id));
			if (isCargo == false){
				destState.getWeaponList().add(w);
			}
			else {
				destSave.getCargoIdList().add(id);
			}
			
		}
		else if (cargoType == 1) {	
			startState.getAugmentIdList().remove(id);
			if (isCargo == false){
				destState.getAugmentIdList().add(id);
			}
			else {
				destSave.getCargoIdList().add(id);
			}
		}
		else {
			DroneState d = new DroneState(id);		
			startState.getDroneList().remove(CargoComboItem.getDSFromID(startState.getDroneList(), id));
			if (isCargo == false){
				destState.getDroneList().add(d);
			}
			else {
				destSave.getCargoIdList().add(id);
			}
		}
		
		
		//update UI
		if (((CargoComboItem)destBox.getItemAt(0)).getTitle().equals(warnText) || ((CargoComboItem)destBox.getItemAt(0)).getTitle().equals("No Cargo!")) {
			destBox.removeItemAt(0);
		}
		//Must increase first
		//Increase count
		if (destMap.get(id) != null) {
			destMap.put(id, (destMap.get(id) + 1));
			((CargoComboItem) CargoComboItem.getCargoItem(destBox, id)).setTitle(
					title + " x"+ destMap.get(id));
			destBox.updateUI();
		}
		else {
			destMap.put(id, 1);
			destBox.addItem(new CargoComboItem(id, 
					title + " x"+ destMap.get(id)));
		}
		//Decrease count
		if (startMap.get(id) > 1) {
			startMap.put(id, (startMap.get(id) - 1));
			((CargoComboItem) startBox.getSelectedItem()).setTitle(
					title + " x"+ startMap.get(id));
			startBox.updateUI();
		}
		else {
			startMap.remove(id);
			startBox.removeItem(startBox.getSelectedItem());
		//	if (startState.getWeaponList().size() == 0) {
			if (startBox.getItemCount() == 0) {
				if (startBox == tradeCargoCB || startBox == shipCargoCB){
					startBox.addItem(new CargoComboItem(id, "No Cargo!"));
				}
				else {
					startBox.addItem(new CargoComboItem(id, String.format("No %s!", warnText)));
				}
				
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
		     

		    if (o == shipScrapSP){
	    		spinStart = shipScrapSP.getModel();
	    		spinEnd = tradeScrapSP;
	    		otherVal = tradeSave.getPlayerShipState().getScrapAmt();
	    		startVal = currentSave.getPlayerShipState().getScrapAmt();
		    }
		    else if (o == shipFuelSP){
	    		spinStart = shipFuelSP.getModel();
	    		spinEnd = tradeFuelSP;
	    		otherVal = tradeSave.getPlayerShipState().getFuelAmt();
	    		startVal = currentSave.getPlayerShipState().getFuelAmt();
		    }
		    else if (o == shipDPartsSP){
		    	spinStart = shipDPartsSP.getModel();
		    	spinEnd = tradeDPartsSP;
		    	otherVal = tradeSave.getPlayerShipState().getDronePartsAmt();
		    	startVal = currentSave.getPlayerShipState().getDronePartsAmt();
		    }
		    else if (o == shipMPartsSP){
		    	spinStart = shipMPartsSP.getModel();
		    	spinEnd = tradeMPartsSP;
		    	otherVal = tradeSave.getPlayerShipState().getMissilesAmt();
		    	startVal = currentSave.getPlayerShipState().getMissilesAmt();
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
		    if (o == shipScrapSP){
		    	tradeSave.getPlayerShipState().setScrapAmt(otherVal);
		    	currentSave.getPlayerShipState().setScrapAmt(currentVal);
		    }
		    else if (o == shipFuelSP) {
		    	tradeSave.getPlayerShipState().setFuelAmt(otherVal);
		    	currentSave.getPlayerShipState().setFuelAmt(currentVal);
		    }
		    else if (o == shipDPartsSP) {
		    	tradeSave.getPlayerShipState().setDronePartsAmt(otherVal);
		    	currentSave.getPlayerShipState().setDronePartsAmt(currentVal);
		    }
		    else if (o == shipMPartsSP) {
		    	tradeSave.getPlayerShipState().setMissilesAmt(otherVal);
		    	currentSave.getPlayerShipState().setMissilesAmt(currentVal);
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
		HashMap<String, Integer> startMap = null;
		HashMap<String, Integer> destMap = null;
		
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
		
		else if (o == shipCrewInfoJB || o == tradeCrewInfoJB){
			JComboBox sourceBox = null;
			if (o == shipCrewInfoJB) {
				sourceBox = shipCrewCB;
			}
			else {
				sourceBox = tradeCrewCB;
			}
			CrewState cs = ((CrewComboItem)sourceBox.getSelectedItem()).getCrewState();
			JOptionPane.showConfirmDialog(null, CrewComboItem.toStringSummary(cs),String.format("Report for crewman %s", cs.getName()), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
			
			
		}
		
		//Trading weapons
		else if (o == shipWeaponJB || o== tradeWeaponJB){
			
			if (o == shipWeaponJB) {
				startBox = shipWeaponCB;
				destBox = tradeWeaponCB;
				destCargo = tradeCargoCB;
				startState = currentState;
				destState = tradeState;
				startSave = currentSave;
				destSave = tradeSave;
				startMap = shipWeaponMap;
				destMap = tradeWeaponMap;
			}
			else {
				startBox = tradeWeaponCB;
				destBox = shipWeaponCB;
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
			tradeCargo(startBox, destBox, destCargo, startState, destState, startSave, destSave, startMap, destMap, 0);
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
				startMap = shipAugmentMap;
				destMap = tradeAugmentMap;
			}
			else {
				startBox = tradeAugCB;
				destBox = shipAugCB;
				destCargo = shipCargoCB;
				startState = tradeState;
				destState = currentState;
				startSave = tradeSave;
				destSave = currentSave;
				startMap = tradeAugmentMap;
				destMap = shipAugmentMap;
			}
			tradeCargo(startBox, destBox, destCargo, startState, destState, startSave, destSave, startMap, destMap, 1);
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
				startMap = shipDroneMap;
				destMap = tradeDroneMap;
			}
			else {
				startBox = tradeDroneCB;
				destBox = shipDroneCB;
				destCargo = shipCargoCB;
				startState = tradeState;
				destState = currentState;
				startSave = tradeSave;
				destSave = currentSave;
				startMap = tradeDroneMap;
				destMap = shipDroneMap;
			}
			tradeCargo(startBox, destBox, destCargo, startState, destState, startSave, destSave, startMap, destMap, 2);
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
			CrewComboItem selected = (CrewComboItem) startBox.getSelectedItem();
			if (selected.getTitle().equals("No Crew!")){
				FTLHomeworld.showErrorDialog("No crew to send!");
			}
			else if (startState.getCrewList().size() <= 1 
					&& ((startState == tradeState && tradeShip != homeSave) || startState == currentState)) {
				FTLHomeworld.showErrorDialog("At least one crew must man the ship!");
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
				if (((CrewComboItem)destBox.getItemAt(0)).getTitle().equals("No Crew!")) {
					destBox.removeItemAt(0);
				}
				destBox.addItem(selected);
				startBox.removeItemAt(i);
				if (startState.getCrewList().size() == 0) {
					startBox.addItem(new CrewComboItem("No Crew!", new CrewState()));
				}
			}
		}
		//Trading cargo
		else if (o == shipCargoJB || o== tradeCargoJB){
			
			if (o == shipCargoJB) {
				startBox = shipCargoCB;
				startState = currentState;
				startSave = currentSave;
				startMap = shipCargoMap;
			}
			else {
				startBox = tradeCargoCB;
				startState = tradeState;
				startSave = tradeSave;			
				startMap = tradeCargoMap;
				
				
				
			}
			String warn = (((CargoComboItem)startBox.getSelectedItem()).getTitle());
			if (warn.equals("No Cargo!") 
					|| warn.equals("Already Sorted")){
				FTLHomeworld.showErrorDialog("No cargo to send!");
			}
			else {
				String id = ((CargoComboItem) startBox.getSelectedItem()).getId();
				int cargoType = 0;
				if (DataManager.get().getAugment(id) != null) {
					if (o == shipCargoJB){
						destBox = tradeAugCB;
						destCargo = tradeCargoCB;			
						destState = tradeState;
						destMap = tradeAugmentMap;
						destSave = tradeSave;
						cargoType = 1;
					}
					else {
						destBox = shipAugCB;
						destCargo = shipCargoCB;			
						destState = currentState;
						destMap = shipAugmentMap;
						destSave = currentSave;
						cargoType = 1;
					}
					
				}
				else if (DataManager.get().getDrone(id) != null) {
					if (o == shipCargoJB){
						destBox = tradeDroneCB;
						destCargo = tradeCargoCB;			
						destState = tradeState;
						destMap = tradeDroneMap;
						destSave = tradeSave;
						cargoType = 2;
					}
					else {
						destBox = shipDroneCB;
						destCargo = shipCargoCB;			
						destState = currentState;
						destMap = shipDroneMap;
						destSave = currentSave;
						cargoType = 2;
					}
				}
				else if (DataManager.get().getWeapon(id) != null) {
					if (o == shipCargoJB){
						destBox = tradeWeaponCB;
						destCargo = tradeCargoCB;			
						destState = tradeState;
						destMap = tradeWeaponMap;
						destSave = tradeSave;
						cargoType = 0;
					}
					else {
						destBox = shipWeaponCB;
						destCargo = shipCargoCB;			
						destState = currentState;
						destMap = shipWeaponMap;
						destSave = currentSave;
						cargoType = 0;
					}
				}
				
				else {
					FTLHomeworld.showErrorDialog("Cargo Item not found!");
					return;
				}
				tradeCargo(startBox, destBox, destCargo, startState, destState, startSave, destSave, startMap, destMap, cargoType);
			}
			
		}
	}	
	
	

}
