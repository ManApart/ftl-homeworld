package org.iceburg.ftl.homeworld.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JButton;

import org.iceburg.ftl.homeworld.core.FTLHomeworld;
import org.iceburg.ftl.homeworld.parser.ShipSaveParser;
import org.iceburg.ftl.homeworld.ui.SpaceDockUI;

import net.blerf.ftl.parser.SavedGameParser.SavedGameState;

public class ShipSave extends SavedGameState {
	public File shipFilePath;
	public JButton boardbtn;
	public String imageInnerPath;
	public ShipSave self; //KartoFlane, you're a genius
			
	public void setshipFilePath( File filePath) {
		shipFilePath = filePath;
	}
	public File getshipFilePath() { return shipFilePath; }

	public ShipSave(File file) {
		shipFilePath = file;
	//	boardbtn = new JButton();
		new ShipSaveParser().readShipSave(this);
		self = this;

		
	}
	
	public static boolean dockShip(ShipSave ss1, int listLength) {
		boolean success = false;
		File oldFile = ss1.getshipFilePath();
		File newFile = null;
		String fileName;
		int i = 1;
		//&& i <= 50
		while (oldFile.exists() && i <= (listLength + 2)) {
			fileName = "continue_" + i + ".sav";
			newFile = new File(FTLHomeworld.save_location, fileName);
			if (!newFile.exists()) {
				//success = oldFile.renameTo(newFile);
				
				Path source = oldFile.toPath();
			          try {
						Files.move(source, newFile.toPath());
						success = true;
					} catch (IOException e) {
						// Auto-generated catch block
						e.printStackTrace();
					}
			}
			
			i = (i + 1);
		}
		ss1.setshipFilePath(newFile);
//		if (i > 50) {
//			FTLHomeworld.showErrorDialog("Over 50 ships, no room means the dock ship failed!");
//			ss1.setshipFilePath(oldFile);
//		}
		return success;
	}
	
	
	public static boolean boardShip(ShipSave ss1) {
		boolean success = false;
		File oldFile = ss1.getshipFilePath();
		File newFile =  new File(FTLHomeworld.save_location, "continue.sav");
		if (!newFile.exists()) {
			//success = oldFile.renameTo(newFile);
			
			Path source = oldFile.toPath();
		          try {
					Files.move(source, newFile.toPath());
					success = true;
				} catch (IOException e) {
					// Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		ss1.setshipFilePath(newFile);
		return success;
	}
	
}
