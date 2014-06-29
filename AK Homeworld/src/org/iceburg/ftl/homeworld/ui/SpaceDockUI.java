package org.iceburg.ftl.homeworld.ui;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.SavedGameParser.CrewState;
import net.blerf.ftl.parser.SavedGameParser.DroneState;
import net.blerf.ftl.parser.SavedGameParser.SavedGameState;
import net.blerf.ftl.parser.SavedGameParser.ShipState;
import net.blerf.ftl.parser.SavedGameParser.WeaponState;
import net.blerf.ftl.xml.ShipBlueprint;

import org.iceburg.ftl.homeworld.core.FTLHomeworld;
import org.iceburg.ftl.homeworld.parser.ShipSaveParser;
import org.iceburg.ftl.homeworld.resource.ResourceClass;
import org.iceburg.ftl.homeworld.model.ShipSave;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;

//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
//TODO
public class SpaceDockUI extends JPanel implements ActionListener {
	// private static final Logger log = LogManager.getLogger(SpaceDockUI.class);
	public static ArrayList<ShipSave> myShips;
	HashMap<JButton, ShipSave> btnToShipMap;
	HashMap<JButton, ShipSave> imageToShipMap;
	public File currentFile;
//	private HashMap<String, BufferedImage> imageCache;
	BufferedImage bg = null;
	ImageIcon img = null;
	JButton loadSavesbtn = null;
	JButton refreshbtn = null;
	JButton launchbtn = null;
	SpaceDockUI self = null;
	HomeworldFrame parent;

	// @Override
	// public void paintComponent(Graphics g) {
	// //bg = ImageIO.read(new File("./resource/SpaceDockSplash.png")); //this guy worked in eclipse but not in jar
	// img = new ImageIcon((new ResourceClass()).getClass().getResource("SpaceDockSplash.png"));
	// bg = new BufferedImage(
	// img.getIconWidth(),
	// img.getIconHeight(),
	// BufferedImage.TYPE_INT_RGB);
	// Graphics gg = bg.createGraphics();
	// img.paintIcon(null, gg, 0,0);
	// gg.dispose();
	// g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
	// }
	// HomeworldFrame.currentShip getter and setter
	public void setcurrentShip(ShipSave ss1) {
		HomeworldFrame.currentShip = ss1;
	}
	public ShipSave getcurrentShip() {
		return HomeworldFrame.currentShip;
	}
	/**
	 * Create the application.
	 */
	public SpaceDockUI(HomeworldFrame p) {
		this.parent = p;
		this.init();
	}
	public void init() {
		// initialize - get ships/file to display
		this.removeAll();
		self = this;

		this.myShips = ShipSaveParser.getShipsList();
		if (myShips.size() > 0)
			;
		{
			File currentFile = new File(FTLHomeworld.save_location + "\\continue.sav");
			HomeworldFrame.currentShip = ShipSaveParser.findCurrentShip(myShips, currentFile);
		}
//		imageCache = new HashMap<String, BufferedImage>();
		setLayout(new GridLayout(0, 2, 0, 0));
		btnToShipMap = new HashMap<JButton, ShipSave>();
		imageToShipMap = new HashMap<JButton, ShipSave>();
		setOpaque(false);
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(0, 2, 0, 0));
		subPanel.setOpaque(false);
		setOpaque(false);
		// for misc buttons
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		rightPanel.setOpaque(false);
		setOpaque(false);
		launchbtn = new JButton("Launch FTL");
		launchbtn.addActionListener(this);
		launchbtn.setToolTipText("Play FTL");
		rightPanel.add(launchbtn);
		refreshbtn = new JButton("Refresh");
		refreshbtn.addActionListener(this);
		refreshbtn.setToolTipText("Update Spacedock with changes from playing FTL");
		rightPanel.add(refreshbtn);
		loadSavesbtn = new JButton("Saves Folder");
		loadSavesbtn.addActionListener(this);
		loadSavesbtn.setToolTipText("Choose saves folder");
		rightPanel.add(loadSavesbtn);
		for (int i = 0; i < myShips.size(); i++) {
			// create panel/ basic data
			JPanel loopPanel = new JPanel();
			loopPanel.setLayout(new BoxLayout(loopPanel, BoxLayout.Y_AXIS));
			loopPanel.setOpaque(false);
			JLabel lblShipName = new JLabel(myShips.get(i).getSave().getPlayerShipName());
			lblShipName.setForeground(Color.white);
			loopPanel.add(lblShipName);
			JLabel lblExplored = new JLabel(myShips.get(i).getSave().getTotalBeaconsExplored()
					+ " beacons explored.");
			lblExplored.setForeground(Color.white);
			loopPanel.add(lblExplored);
			// add the ship's miniship picture
			// baseImage = getResourceImage("img/customizeUI/miniship_"+ ship.getGraphicsBaseName()+ ".png");
			// TODO - ^will crash if no miniship, for instance on enemy ships. Therefore custom ships must have miniship to work with this program.
			// So let's use base image until I can test if miniship exists.
			ShipBlueprint ship = DataManager.get().getShips().get(myShips.get(i).getSave().getPlayerShipBlueprintId());
			if (ship == null) {
				ship = DataManager.get().getAutoShips().get(myShips.get(i).getSave().getPlayerShipBlueprintId());
			}
			BufferedImage baseImage = parent.getResourceImage("img/ship/"
					+ ship.getGraphicsBaseName() + "_base.png", true);
			// JLabel lblShipID = new JLabel("", new ImageIcon(baseImage), JLabel.CENTER);
			JButton lblShipID = new JButton("", new ImageIcon(baseImage));
			lblShipID.setOpaque(false);
			lblShipID.setContentAreaFilled(false);
			lblShipID.setBorderPainted(false);
			lblShipID.addActionListener(this);
			imageToShipMap.put(lblShipID, myShips.get(i));
			loopPanel.add(lblShipID);
			// add rigid space below picture so buttons line up
			if (baseImage.getHeight() < 150) {
				loopPanel.add(Box.createRigidArea(new Dimension(25, (150 - baseImage.getHeight()))));
			}
			// add the board / dock button
			if (myShips.get(i) == HomeworldFrame.currentShip) {
				myShips.get(i).getBoardbtn().setText("Dock");
				myShips.get(i).getBoardbtn().setToolTipText("Store this ship to play with later");
			} else {
				myShips.get(i).getBoardbtn().setText("Board");
				myShips.get(i).getBoardbtn().setToolTipText("Play with this ship/set active");
			}
			btnToShipMap.put(myShips.get(i).getBoardbtn(), myShips.get(i));
			// add to a button array so we can use the index to match the button to the ship
			loopPanel.add(myShips.get(i).getBoardbtn());
			myShips.get(i).getBoardbtn().addActionListener(this);
			loopPanel.add(Box.createRigidArea(new Dimension(25, 10)));
			subPanel.add(loopPanel);
		}
		add(subPanel);
		add(rightPanel);
	}
	public void actionPerformed(ActionEvent ae) {
		JButton o = (JButton) ae.getSource();
		ShipSave myShip = btnToShipMap.get(o);
		if (btnToShipMap.containsKey(o)) {
			// connect the button to the proper ship
			// Thanks to KartoFlane and Vhati for finally giving me a better way to do this!
			if (myShip == HomeworldFrame.currentShip) {
				o.setText("Board");
				o.setToolTipText("Play with this ship/set active");
				ShipSave.dockShip(myShip, myShips.size());
				HomeworldFrame.currentShip = null;
			} else {
				o.setText("Dock");
				o.setToolTipText("Store this ship to play with later");
				// if they have boarded a ship, dock it before boarding new one;
				if (HomeworldFrame.currentShip != null) {
					// Find which ship has the file, dock it, and then update it's button
					// System.out.println("Already manning a ship!");
					ShipSave.dockShip(HomeworldFrame.currentShip, myShips.size());
					HomeworldFrame.currentShip.getBoardbtn().setText("Board");
					HomeworldFrame.currentShip = null;
				}
				ShipSave.boardShip(myShip);
				HomeworldFrame.currentShip = myShip;
			}
			self.updateUI();
		} else {
			if (o == loadSavesbtn) {
				// System.out.println("save folders");
				File newSaves = FTLHomeworld.promptForSavePath();
				if (newSaves != null) {
					FTLHomeworld.save_location = newSaves;
				}
				init();
			} else if (o == refreshbtn) {
				// System.out.println("Refreshed");
				init();
			} else if (o == launchbtn) {
				FTLHomeworld.launchFTL();
			}
			// else, it's a ship image being listened to
			else {
				o.setFocusPainted(false);
				myShip = imageToShipMap.get(o);
				// Get the save file
				SavedGameParser parser = new SavedGameParser();
				SavedGameState sgs = null;
				try {
					sgs = parser.readSavedGame(myShip.getshipFilePath());
				} catch (IOException e) {
					// Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println(sgs.getPlayerShipState().toString());
				JOptionPane.showConfirmDialog(null, shipSummaryString(sgs), String.format("Report for ship %s", sgs.getPlayerShipName()), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
			}
		}
	}
	public String shipSummaryString(SavedGameState sgs) {
		ShipState state = sgs.getPlayerShipState();
		ShipBlueprint shipBlueprint = DataManager.get().getShip(state.getShipBlueprintId());
		ShipBlueprint.SystemList blueprintSystems = shipBlueprint.getSystemList();
		// ShipLayout shipLayout = DataManager.get().getShipLayout(shipLayoutId);
		// if ( shipLayout == null )
		// throw new RuntimeException( String.format("Could not find layout for%s ship: %s", (auto ? " auto" : ""), shipName) );
		StringBuilder result = new StringBuilder();
		boolean first = true;
		result.append("Supplies...\n");
		result.append(String.format("Hull:        %3d\n", state.getHullAmt()));
		result.append(String.format("Fuel:        %3d\n", state.getFuelAmt()));
		result.append(String.format("Drone Parts: %3d\n", state.getDronePartsAmt()));
		result.append(String.format("Missiles:    %3d\n", state.getMissilesAmt()));
		result.append(String.format("Scrap:       %3d\n", state.getScrapAmt()));
		result.append("\nCrew...\n");
		first = true;
		for (CrewState c : state.getCrewList()) {
			if (first) {
				first = false;
			} else {
				result.append(",\n");
			}
			result.append(c.getName());
		}
		result.append("\n\nWeapons...\n");
		first = true;
		for (WeaponState w : state.getWeaponList()) {
			if (first) {
				first = false;
			} else {
				result.append(",\n");
			}
			result.append(DataManager.get().getWeapon(w.getWeaponId()).getTitle());
		}
		result.append("\n\nDrones...\n");
		first = true;
		for (DroneState d : state.getDroneList()) {
			if (first) {
				first = false;
			} else {
				result.append(",\n");
			}
			result.append(DataManager.get().getDrone(d.getDroneId()).getTitle());
		}
		result.append("\n\nAugments...\n");
		first = true;
		for (String augmentId : state.getAugmentIdList()) {
			if (first) {
				first = false;
			} else {
				result.append(",\n");
			}
			// result.append(String.format("AugmentId: %s\n", augmentId));
			result.append(DataManager.get().getAugment(augmentId).getTitle());
		}
		return result.toString();
	}
//	private BufferedImage getResourceImage(String innerPath, boolean scale) {
//		// If caching, you can get(innerPath) from a HashMap and return the pre-loaded pic.
//		BufferedImage result = imageCache.get(innerPath);
//		if (result != null) {
//			return result;
//		} else {
//			InputStream in = null;
//			try {
//				in = DataManager.get().getResourceInputStream(innerPath);
//				result = ImageIO.read(in);
//				if (scale = true) {
//					result = scaleImage(result);
//				}
//				imageCache.put(innerPath, result);
//				return result; // If caching, put result in the map before returning.
//			} catch (IOException e) {
//				// log.error( "Failed to load resource image ("+ innerPath +")", e );
//				e.printStackTrace();
//			} finally {
//				try {
//					if (in != null)
//						in.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			return result;
//		}
//	}
//	private BufferedImage scaleImage(BufferedImage image) {
//		BufferedImage scaledBI = null;
//		if (image.getWidth() > 191 || image.getHeight() > 121) {
//			int scaledWidth = 0;
//			int scaledHeight = 0;
//			if (image.getWidth() > image.getHeight()) {
//				scaledWidth = 191;
//				scaledHeight = (image.getHeight() / (image.getWidth() / 191));
//			} else {
//				scaledHeight = 121;
//				scaledWidth = (image.getWidth() / (image.getHeight() / 121));
//			}
//			scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TRANSLUCENT);
//			Graphics2D g = scaledBI.createGraphics();
//			g.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
//			g.dispose();
//			return scaledBI;
//		} else {
//			return image;
//		}
//	}
}
