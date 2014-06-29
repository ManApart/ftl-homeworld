package org.iceburg.ftl.homeworld.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.swing.JButton;

import org.iceburg.ftl.homeworld.core.FTLHomeworld;
import org.iceburg.ftl.homeworld.model.ShipSave;

//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;

import net.blerf.ftl.constants.Difficulty;
import net.blerf.ftl.parser.Parser;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.SavedGameParser.SavedGameState;

//Credit to by Vhati and ComaToes for their FTLEditor and allowing me to use their source
//FTL Editor found here: http://www.ftlgame.com/forum/viewtopic.php?f=7&t=10959&start=70
//I've modeled this class after SavedGameState (from FTL Editor), as well as copied and then modified chunks of its code.
//Anything that reads or writes savegame data was either inspired by, copied from, or depends on FTL Editor
//I'm especially indebted to Vhati who gave me permission to use the code as well as answered my questions as I worked through my first java program.
//Used to select save file
//TODO
//move save_location finder to FTLHomeworld
//Open custom folder for save location
//Ships the same size
public class ShipSaveParser extends Parser {

	
	public static ShipSave readShipSave(File saveFile) {
		// private static final Logger log = LogManager.getLogger(ShipSaveParser.class);
		ShipSave s = new ShipSave();
		System.out.println("Reading " + saveFile.toString());
		try {
			s.setSave(FTLHomeworld.savedGameParser.readSavedGame(saveFile));
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		s.setshipFilePath(saveFile);
		s.setBoardbtn(new JButton());
		return s;
		
	}

	public static ArrayList<ShipSave> getShipsList() {
		File[] fileList = FTLHomeworld.save_location.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.toLowerCase().endsWith(".sav") && !name.contains("prof") && !name.contains("Homeworld.sav") && !name.contains("HomeworldAE.sav"));
			}
		});
		ArrayList<ShipSave> shipList = new ArrayList<ShipSave>();
		// ShipSaveParser parser = new ShipSaveParser();
		for (int i = 0; i < fileList.length; i++) {
			ShipSave ss1 = readShipSave(fileList[i]);
			shipList.add(ss1);
		}
		return shipList;
	}
	// TODO Test
	public static ShipSave findCurrentShip(ArrayList<ShipSave> myShips, File currentFile) {
		for (int i = 0; i < myShips.size(); i++) {
			if (myShips.get(i).getshipFilePath().equals(currentFile)) {
				// add to UI's current ship
				return myShips.get(i);
			}
		}
		return null;
	}
}
