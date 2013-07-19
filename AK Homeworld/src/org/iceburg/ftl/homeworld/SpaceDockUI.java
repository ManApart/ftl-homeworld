package org.iceburg.ftl.homeworld;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.xml.ShipBlueprint;

import org.iceburg.ftl.homeworld.ShipSaveParser.ShipSave;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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

//Credit to by Vhati and ComaToes for their FTLEditor and allowing me to use their source
//FTL Editor found here: http://www.ftlgame.com/forum/viewtopic.php?f=7&t=10959&start=70
//In order to get pics of each ship, I borrowed a lot of their code in order to interact with their datamanager
//I made the custom background from FTL resources and a modified space dock based on schematics from: http://www.shipschematics.net/

//TODO outline:
//clean up code/ organize it better


public class SpaceDockUI extends JPanel {

	private static final Logger log = LogManager.getLogger(SpaceDockUI.class);
//	private JFrame frmSpaceDock;
	//private HomeworldFrame frame;
	public ShipSave[] myShips;
	public JButton[] buttonList;
	public ShipSave currentShip;
	public File currentFile;
	private HashMap<String,BufferedImage> imageCache;
	BufferedImage bg  = null;
	ImageIcon img = null;
	//Image bg = Toolkit.getDefaultToolkit().createImage("resource/SpaceDockSplash.png");
	
    @Override
    public void paintComponent(Graphics g) {
		//bg = ImageIO.read(new File("./resource/SpaceDockSplash.png")); //this guy worked in eclipse but not in jar
		img = new ImageIcon(this.getClass().getResource("resource/SpaceDockSplash.png"));
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
		//this.frame = frame;
		//initializ - get ships/file to display
		myShips = ShipSaveParser.getShipsList();
		buttonList = new JButton[myShips.length];
		File currentFile = 
   				new File(myShips[0].getshipFilePath().getParentFile() + "\\continue.sav");
		currentShip = ShipSaveParser.findCurrentShip(myShips, currentFile);
		imageCache = new HashMap<String, BufferedImage>();
		//frmSpaceDock = new JFrame();
//		frmSpaceDock.setTitle("FTL Space Dock");
//		frmSpaceDock.setBounds(100, 100, 850, 600);
//		frmSpaceDock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frmSpaceDock.setLayout(new GridLayout(0, 1, 0, 0));
//		BgPanel bgPanel = new BgPanel();
		JPanel subPanel = new JPanel();
		//JPanel mainPanel = new JPanel();
		subPanel.setLayout(new GridLayout(0, 2, 0, 0));
		setLayout(new GridLayout(0, 2, 0, 0));
		subPanel.setOpaque(false);
		setOpaque(false);
		
				
		ImageIO.setUseCache(false);  // Small images don't need extra buffering.
		
		for (int i = 0; i < myShips.length; i++) {			
			//create panel/ basic data
			JPanel loopPanel = new JPanel();
			loopPanel.setLayout(new BoxLayout(loopPanel, BoxLayout.Y_AXIS));
			//loopPanel.setBackground(Color.gray);
			//loopPanel.setBackground(new Color(0,0,0,100));
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
			//lblShipID.setPreferredSize(new Dimension(200, 140));
			loopPanel.add(lblShipID);
			
			//add the board / dock button
			if (myShips[i].getshipFilePath().equals(currentFile)) {
				buttonList[i] =  new JButton("Dock");		
			}
			else {
				buttonList[i] =  new JButton("Board");
			}
			//add to a button array so we can use the index to match the button to the ship		
			loopPanel.add(buttonList[i]);
			buttonList[i].addActionListener(new BoardListener());
			
			
			
			loopPanel.add(Box.createRigidArea(new Dimension(25, 10)));
			//frmSpaceDock.add(loopPanel);
			subPanel.add(loopPanel);
		}
		add(subPanel);
//		bgPanel.setLayout(new BorderLayout());
//		bgPanel.add(scrollPanel);
//		frame.add(mainPanel);
//		frmSpaceDock.add(bgPanel);
		//frmSpaceDock.add(scrollPanel);
		//frmSpaceDock.pack();
	}
	
	class BoardListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {				
			//connect the button to the proper ship (there must be a better way to do this!)
			//Well, this is much better than the old for loop, anyway
			JButton sourceButton = (JButton) ae.getSource();
			int i = Arrays.asList(buttonList).indexOf(sourceButton);
			ShipSaveParser parser = new ShipSaveParser();
			if (sourceButton.getText().equals("Dock")) {
	    	   sourceButton.setText("Board");	    	   
	    	   parser.dockShip(myShips[i]);
	    	   currentShip = myShips[i];
	    	   
			} else if (sourceButton.getText().equals("Board")) {
	    	   sourceButton.setText("Dock");
	          //if they have boarded a ship, dock it before boarding new one; 
	    	   if  (currentShip != null) {
	    		   //Find which ship has the file, dock it, and then update it's button
	    		  // System.out.println("Already manning a ship!");
	    		   parser.dockShip(currentShip);
	    		   int b = Arrays.asList(myShips).indexOf(currentShip);
	    		   buttonList[b].setText("Board");
	    		   currentShip = null;
	    	   }  
	    	   parser.boardShip(myShips[i]);
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
		 if (image.getWidth() > 200 || image.getHeight() > 130) {
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
