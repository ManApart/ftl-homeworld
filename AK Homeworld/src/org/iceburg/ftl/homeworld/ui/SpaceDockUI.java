package org.iceburg.ftl.homeworld.ui;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.xml.ShipBlueprint;

import org.iceburg.ftl.homeworld.core.FTLHomeworld;
import org.iceburg.ftl.homeworld.parser.ShipSaveParser;
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
import java.util.Arrays;
import java.util.HashMap;


import javax.swing.JLabel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO

public class SpaceDockUI extends JPanel {

	private static final Logger log = LogManager.getLogger(SpaceDockUI.class);
	public ShipSave[] myShips;
	public JButton[] buttonList;
	public ShipSave currentShip;
	public File currentFile;
	private HashMap<String,BufferedImage> imageCache;
	BufferedImage bg  = null;
	ImageIcon img = null;
	JButton loadSavesbtn = null;
	JButton refreshbtn = null;
	JButton launchbtn = null;
	
    @Override
    public void paintComponent(Graphics g) {
		//bg = ImageIO.read(new File("./resource/SpaceDockSplash.png")); //this guy worked in eclipse but not in jar
		img = new ImageIcon(this.getClass().getResource("../resource/SpaceDockSplash.png"));
		bg = new BufferedImage(
		img.getIconWidth(),
		img.getIconHeight(),
		BufferedImage.TYPE_INT_RGB);
		Graphics gg = bg.createGraphics();
		img.paintIcon(null, gg, 0,0);
		gg.dispose();
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }
	
	//currentShip getter and setter
	public void setCurrentShip( ShipSave ss1) {
		currentShip = ss1;
	}
	public ShipSave getCurrentShip() { return currentShip; }


	/**
	 * Create the application.
	 */
	public SpaceDockUI() {
		this.init();
	}
	
	public void init() {
		//initializ - get ships/file to display
		this.removeAll();
		this.myShips = ShipSaveParser.getShipsList();
		if (myShips.length > 0);
		{
			this.buttonList = new JButton[myShips.length];
			File currentFile = 
	   				new File(FTLHomeworld.save_location + "\\continue.sav");
			this.currentShip = ShipSaveParser.findCurrentShip(myShips, currentFile);
		}	
		imageCache = new HashMap<String, BufferedImage>();
		setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(0, 2, 0, 0));
		subPanel.setOpaque(false);
		setOpaque(false);
		
		//for misc buttons
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		rightPanel.setOpaque(false);
		setOpaque(false);
		
		launchbtn = new JButton("Launch FTL");
		launchbtn.addActionListener(new SaveFolderListener());
		rightPanel.add(launchbtn);
		
		refreshbtn = new JButton("Refresh");
		refreshbtn.addActionListener(new SaveFolderListener());
		rightPanel.add(refreshbtn);
		
		loadSavesbtn = new JButton("Saves Folder");
		loadSavesbtn.addActionListener(new SaveFolderListener());
		rightPanel.add(loadSavesbtn);
		
		for (int i = 0; i < myShips.length; i++) {			
			//create panel/ basic data
			JPanel loopPanel = new JPanel();
			loopPanel.setLayout(new BoxLayout(loopPanel, BoxLayout.Y_AXIS));
			loopPanel.setOpaque(false);
			JLabel lblShipName = new JLabel(myShips[i].getPlayerShipName());
			lblShipName.setForeground(Color.white);
			loopPanel.add(lblShipName);
			JLabel lblExplored = new JLabel(myShips[i].getTotalBeaconsExplored() + " beacons explored.");
			lblExplored.setForeground(Color.white);
			loopPanel.add(lblExplored);
			
			//add the ship's miniship picture
			//baseImage = getResourceImage("img/customizeUI/miniship_"+ ship.getGraphicsBaseName()+ ".png");
			//TODO - ^will crash if no miniship, for instance on enemy ships. Therefore custom ships must have miniship to work with this program. 
			//So let's use base image until I can test if miniship exists.
			
			ShipBlueprint ship = DataManager.get().getShips()
					.get(myShips[i].getPlayerShipBlueprintId());
			BufferedImage baseImage;
			if (ship == null) {
				ship = DataManager.get().getAutoShips()
						.get(myShips[i].getPlayerShipBlueprintId());
			}
			
			baseImage = getResourceImage("img/ship/"+ ship.getGraphicsBaseName() +"_base.png", true);
			JLabel lblShipID = new JLabel("", new ImageIcon(baseImage), JLabel.CENTER);
			loopPanel.add(lblShipID);
			
			//add rigid space below picture so buttons line up
			if (baseImage.getHeight() < 150) {
				loopPanel.add(Box.createRigidArea(new Dimension(25, (150 - baseImage.getHeight()))));
			}
			
			//add the board / dock button
			if (myShips[i] == this.currentShip) {
				buttonList[i] =  new JButton("Dock");		
			}
			else {
				buttonList[i] =  new JButton("Board");
			}
			//add to a button array so we can use the index to match the button to the ship		
			loopPanel.add(buttonList[i]);
			buttonList[i].addActionListener(new BoardListener());
			
			
			
			loopPanel.add(Box.createRigidArea(new Dimension(25, 10)));
			subPanel.add(loopPanel);
		}
		add(subPanel);
		add(rightPanel);
	}
	class SaveFolderListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton o = (JButton) e.getSource();
			if (o.equals(loadSavesbtn)) {
				//System.out.println("save folders");
				File newSaves = FTLHomeworld.promptForSavePath();
				if (newSaves != null){FTLHomeworld.save_location = newSaves;}
				init();
			}
			else if (o.equals(refreshbtn)) {
				//System.out.println("Refreshed");
				init();
				
			}
			else if (o.equals(launchbtn)) {
				FTLHomeworld.launchFTL();
			}
			
		}
	}
	
	class BoardListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {				
			//connect the button to the proper ship (there must be a better way to do this!)
			//Well, this is much better than the old for loop, anyway
			JButton sourceButton = (JButton) ae.getSource();
			int i = Arrays.asList(buttonList).indexOf(sourceButton);
			if (sourceButton.getText().equals("Dock")) {
	    	   sourceButton.setText("Board");	    	   
	    	   ShipSave.dockShip(myShips[i]);
	    	   currentShip = null;
	    	   
			} else if (sourceButton.getText().equals("Board")) {
	    	   sourceButton.setText("Dock");
	          //if they have boarded a ship, dock it before boarding new one; 
	    	   if  (currentShip != null) {
	    		   //Find which ship has the file, dock it, and then update it's button
	    		  // System.out.println("Already manning a ship!");
	    		   ShipSave.dockShip(currentShip);
	    		   int b = Arrays.asList(myShips).indexOf(currentShip);
	    		   buttonList[b].setText("Board");
	    		   currentShip = null;
	    	   }  
	    	   ShipSave.boardShip(myShips[i]);
	    	   currentShip = myShips[i];
		       
			}
		}
	}
	
	
	
	private BufferedImage getResourceImage(String innerPath, boolean scale) {
		  // If caching, you can get(innerPath) from a HashMap and return the pre-loaded pic.
		BufferedImage result = imageCache.get(innerPath);
		if (result != null)	{ 
			return result;
		}	
		else {
			InputStream in = null;
			  try {
			    in = DataManager.get().getResourceInputStream(innerPath);
			    result = ImageIO.read(in);
			   if (scale = true) {
				   result = scaleImage(result);
			   }
			   imageCache.put(innerPath, result);
			   return result; // If caching, put result in the map before returning.
			    
			  }
			  catch (IOException e) {
			    log.error( "Failed to load resource image ("+ innerPath +")", e );
			    e.printStackTrace();
			  }
			  finally {
			    try {if (in != null) in.close();}
			    catch (IOException e) {e.printStackTrace();}
			  }
			  return result;
		}
	}
	
	private BufferedImage scaleImage(BufferedImage image) {		 
		 BufferedImage scaledBI = null;
		 if (image.getWidth() > 191 || image.getHeight() > 121) {
		    	int scaledWidth = 0;
		    	int scaledHeight = 0;
		    	if (image.getWidth() > image.getHeight()){
		    		scaledWidth = 191;
		    		scaledHeight = (image.getHeight()/(image.getWidth()/191));
		    	}
		    	else {
		    		scaledHeight = 121;
		    		scaledWidth = (image.getWidth()/(image.getHeight()/121));
		    	}
		    	scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TRANSLUCENT);
		    	Graphics2D g = scaledBI.createGraphics();
		    	g.drawImage(image, 0, 0, scaledWidth, scaledHeight, null); 
		    	g.dispose();
		    	return scaledBI;
		    }
		    else {
		    	return image;
		    }
	}


}
