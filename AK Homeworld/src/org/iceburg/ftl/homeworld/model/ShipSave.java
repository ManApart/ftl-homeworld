package org.iceburg.ftl.homeworld.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.iceburg.ftl.homeworld.core.FTLHomeworld;

import net.blerf.ftl.parser.SavedGameParser.SavedGameState;

public class ShipSave extends SavedGameState {
	public File shipFilePath;
			
	public void setshipFilePath( File filePath) {
		shipFilePath = filePath;
	}
	public File getshipFilePath() { return shipFilePath; }

	
	public static boolean dockShip(ShipSave ss1) {
		boolean success = false;
		File oldFile = ss1.getshipFilePath();
		File newFile = null;
		String fileName;
		int i = 1;
		while (oldFile.exists() && i <= 50) {
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
		if (i > 50) {
			FTLHomeworld.showErrorDialog("Over 50 ships, no room means the dock ship failed!");
			ss1.setshipFilePath(oldFile);
		}
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
