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

import org.iceburg.ftl.homeworld.core.FTLHomeworld;
import org.iceburg.ftl.homeworld.model.ShipSave;

//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;

import net.blerf.ftl.parser.Parser;
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
public class ShipSaveParser extends Parser{
	
	public ShipSave readShipSave(File sav)  {
		//private static final Logger log = LogManager.getLogger(ShipSaveParser.class);
		FileInputStream in = null;
		InputStream layoutStream = null;
		ShipSave saveFile = new ShipSave();
		try {
			
			in = new FileInputStream(sav);

			// This should always be 2.
			int headerAlpha = readInt(in);
			if ( headerAlpha != 2 )
				System.out.println("Header wasn't right!");
				//log.warn( "Unexpected first byte ("+ headerAlpha +"): it's either a bad file, or possibly too new for this tool" );

			saveFile.setDifficultyEasy( readBool(in) );
			saveFile.setTotalShipsDefeated( readInt(in) );
			saveFile.setTotalBeaconsExplored( readInt(in) );
			saveFile.setTotalScrapCollected( readInt(in) );
			saveFile.setTotalCrewHired( readInt(in) );

			String playerShipName = readString(in);         // Redundant.
			saveFile.setPlayerShipName( playerShipName );

			String playerShipBlueprintId = readString(in);  // Redundant.
			saveFile.setPlayerShipBlueprintId( playerShipBlueprintId );
			
			saveFile.setshipFilePath(sav);
			
		} catch (FileNotFoundException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();
		} finally {
			try {if (in != null) in.close();}
			catch (IOException e) {}

			try {if (layoutStream != null) in.close();}
			catch (IOException e) {}
		}
		return saveFile;
	}

	public boolean writeShipSave(ShipSave ss) {
		boolean saved = false;
		OutputStream out = null;
		try {
			out = new FileOutputStream(ss.getshipFilePath());
			// This should always be 2.
			writeInt( out, 2 );

			writeBool( out, ss.isDifficultyEasy() );
			writeInt( out, ss.getTotalShipsDefeated() );
			writeInt( out, ss.getTotalBeaconsExplored() );
			writeInt( out, ss.getTotalScrapCollected() );
			writeInt( out, ss.getTotalCrewHired() );

			writeString( out, ss.getPlayerShipName() );
			writeString( out, ss.getPlayerShipBlueprintId() );
			saved = true;
		} catch (IOException e) {e.printStackTrace();}
		finally {
			try {if (out != null) out.close();}
			catch (IOException e) {}
	
		}
		return saved;		
	}



	public static ShipSave[] getShipsList() {
		File[] fileList = FTLHomeworld.save_location.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.toLowerCase().endsWith(".sav") && !name.contains("prof") );
			}
		});
		
		ShipSave[] shipList = new ShipSave[fileList.length];
		ShipSaveParser parser = new ShipSaveParser();
		for (int i = 0; i < shipList.length; i++) {			
			ShipSave ss1 = parser.readShipSave(fileList[i]);
			shipList[i] = ss1;
		}
		return shipList;
	}
	
	
	//TODO Test
	public static ShipSave findCurrentShip(ShipSave[] myShips, File currentFile) {
		for (int i = 0; i < myShips.length; i++) {			
			if (myShips[i].getshipFilePath().equals(currentFile)) {
				//add to UI's current ship
				return myShips[i];
			}
			
		}
		return null;
	}
	
}
