package org.iceburg.ftl.homeworld;

import java.io.File;

import org.iceburg.ftl.homeworld.parser.ShipSaveParser;
import org.iceburg.ftl.homeworld.model.ShipSave;
import org.junit.*;
import static org.junit.Assert.*;

public class ShipSaveParserTest {
	
	public ShipSave createShipSave() {
		ShipSave ss1 = new ShipSave();
		ss1.setDifficultyEasy(true);
		ss1.setTotalShipsDefeated(2);
		ss1.setTotalBeaconsExplored(3);
		ss1.setTotalScrapCollected(4);
		ss1.setTotalCrewHired(5);
		
		ss1.setPlayerShipName("Enterprise");
		ss1.setPlayerShipBlueprintId("NCC-1701-D");
		ss1.setshipFilePath(new File("NextGen.sav"));
		return ss1;
	}
	@Test
	public void ShipSave() {
		ShipSave ss1 = createShipSave();
		assertEquals("Enterprise", ss1.getPlayerShipName());
		assertEquals("NCC-1701-D", ss1.getPlayerShipBlueprintId());
	}
	
	@Test
	public void ShipRead() {
		//reads an actual game save to make sure read works...
		File testfile = new File("TestShip.sav");
		//File testfile = new File("NextGen.sav");
		ShipSaveParser parser = new ShipSaveParser();
		ShipSave ss1 = parser.readShipSave(testfile);
		assertEquals(true, ss1.isDifficultyEasy());
		assertEquals(5, ss1.getTotalShipsDefeated());
		assertEquals(7, ss1.getTotalBeaconsExplored());
		assertEquals(37, ss1.getTotalScrapCollected());
		assertEquals(6, ss1.getTotalCrewHired());
		assertEquals("Test Ship", ss1.getPlayerShipName());
		assertEquals("PLAYER_SHIP_HARD", ss1.getPlayerShipBlueprintId());
	}
		
	
	@Test
	public void getShipsList() {
		//add some save files to the directory
		File testFile1 = new File("NextGen.sav");
		File testFile2 = new File("continue_1.sav");
		File testFile3 = new File("continue.sav");
		testFile1.delete();
		testFile2.delete();
		testFile3.delete();
		assertFalse("File should not exist",
				testFile1.exists());
		ShipSave ss1 = createShipSave();
		ShipSave ss2 = createShipSave();
		ShipSave ss3 = createShipSave();
		ss1.setshipFilePath(testFile1);
		ss2.setshipFilePath(testFile2);
		ss3.setshipFilePath(testFile3);
		ShipSaveParser parser = new ShipSaveParser();
		assertTrue("File 1 should have been saved", 
				parser.writeShipSave(ss1));
		assertTrue("File 2 should have been saved", 
				parser.writeShipSave(ss2));
		assertTrue("File 3 should have been saved", 
				parser.writeShipSave(ss3));
		
		//get the list of ships
		ShipSave[] myShips = ShipSaveParser.getShipsList();
		
		//compare
		assertTrue("Failed to get proper number of files", myShips.length == 4); 
		assertTrue("File path did not match", 
				myShips[1].getshipFilePath().getName().equals("continue_1.sav"));
		assertEquals("Ship's name did not match", 
				"Enterprise", myShips[1].getPlayerShipName());
		assertEquals("Ship's blueprint ID did not match",
				"NCC-1701-D", myShips[1].getPlayerShipBlueprintId());
		//may emit a false positive if out/in are iterative and therefore the whole file must be written at once
		
	}
	
	@Test
	public void dockShip() {
		//delete the renamed ship and create the origional
		File deleteFile = new File("continue_1.sav");
		deleteFile.delete();
		assertFalse("DeleteFile should not exist",
				deleteFile.exists());
		ShipSave ss1 = new ShipSave();
		ss1.setshipFilePath(new File("continue.sav"));
		ss1.getshipFilePath().delete();
		ShipSaveParser parser = new ShipSaveParser();
		parser.writeShipSave(ss1);
		assertTrue("Origional continue should exist",
				ss1.getshipFilePath().exists());
		assertTrue("Ship should have been docked", ShipSave.dockShip(ss1));
		
		assertTrue("Shipfile should have been renamed", deleteFile.exists());
			
	}
		

	
	
	@Test
	public void boardShip() {
		//delete the renamed ship and create the origional
		File deleteFile = new File("continue.sav");
		deleteFile.delete();
		assertFalse("DeleteFile should not exist",
				deleteFile.exists());
		ShipSave ss1 = new ShipSave();
		ss1.setshipFilePath(new File("continue_1.sav"));
		ss1.getshipFilePath().delete();
		ShipSaveParser parser = new ShipSaveParser();
		parser.writeShipSave(ss1);
		assertTrue("Origional continue should exist",
				ss1.getshipFilePath().exists());
		assertTrue("Ship should have been boarded", ShipSave.boardShip(ss1));
		ShipSave ss2 = parser.readShipSave(deleteFile);
		assertTrue("Shipfile should have been renamed to continue.sav", 
				ss2.getshipFilePath().equals(deleteFile));
	}
	
	
	
	

	//Depercatitatedeish, I really need to learn to spell that word!
//	@Test
//	public void ShipSaveParser() {
//		//save a ship to file
//		ShipSave ss1 = createShipSave();
//		String fileName = "NextGen.sav";
//		File testFile = new File(fileName);
//		testFile.delete();
//		assertFalse("File should not exist",
//				testFile.exists());
//		ShipSaveParser parser = new ShipSaveParser();
//		assertTrue("File should have been saved", 
//				parser.writeShipSave(ss1));
//		//get the ship from file	
//		ShipSaveParser.ShipSave ss2 = parser.readShipSave(testFile);;
//		assertEquals("Enterprise", ss2.getPlayerShipName());
//		assertEquals("NCC-1701-D", ss2.getPlayerShipBlueprintId());
//	}	
}

