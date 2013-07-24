package org.iceburg.ftl.homeworld;

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
public class ShipSaveParser extends Parser{

	public static File save_location = null;
	
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
		if (ShipSaveParser.save_location == null){
			File folder = null;
			for ( File file : FTLHomeworld.getPossibleUserDataLocations("prof.sav") ) {
			      if ( file.exists() ) {
			        folder = file.getParentFile();
			        break;
			      }
			}
		}
		File[] fileList = ShipSaveParser.save_location.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.toLowerCase().endsWith(".sav") && !name.contains("prof") );
			}
		});
		
		ShipSave[] shipList = new ShipSave[fileList.length];
		ShipSaveParser parser = new ShipSaveParser();
		for (int i = 0; i < shipList.length; i++) {			
			ShipSaveParser.ShipSave ss1 = parser.readShipSave(fileList[i]);
			shipList[i] = ss1;
		}
		return shipList;
	}
	
	
	
	public boolean dockShip(ShipSave ss1) {
		boolean success = false;
		File oldFile = ss1.getshipFilePath();
		File newFile = null;
		String fileName;
		int i = 1;
		while (oldFile.exists() && i < 50) {
			fileName = "continue_" + i + ".sav";
			newFile = new File(ShipSaveParser.save_location, fileName);
			if (!newFile.exists()) {
				//success = oldFile.renameTo(newFile);
				
				Path source = oldFile.toPath();
			          try {
						Files.move(source, newFile.toPath());
						success = true;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
			i = (i + 1);
		}
		//TODO - add i < 100 error so they can't make more than 100 ships / loop doesn't go on forever?
		//update ship info
		ss1.setshipFilePath(newFile);
		return success;
	}
	
	
	public boolean boardShip(ShipSave ss1) {
		boolean success = false;
		File oldFile = ss1.getshipFilePath();
		File newFile =  new File(ShipSaveParser.save_location, "continue.sav");
		if (!newFile.exists()) {
			//success = oldFile.renameTo(newFile);
			
			Path source = oldFile.toPath();
		          try {
					Files.move(source, newFile.toPath());
					success = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		ss1.setshipFilePath(newFile);
		return success;
	}

	
	public static class ShipSave extends SavedGameState {
		public File shipFilePath;
				
		public void setshipFilePath( File filePath) {
			shipFilePath = filePath;
		}
		public File getshipFilePath() { return shipFilePath; }
		
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
