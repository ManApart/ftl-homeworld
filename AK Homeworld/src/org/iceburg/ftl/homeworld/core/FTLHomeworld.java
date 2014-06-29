package org.iceburg.ftl.homeworld.core;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;



import net.blerf.ftl.model.ShipLayout;
import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.DefaultDataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.SavedGameParser.EncounterState;
import net.blerf.ftl.parser.SavedGameParser.EnvironmentState;
import net.blerf.ftl.parser.SavedGameParser.ExtendedShipInfo;
import net.blerf.ftl.parser.SavedGameParser.ExtendedSystemInfo;
import net.blerf.ftl.parser.SavedGameParser.RebelFlagshipState;
import net.blerf.ftl.parser.SavedGameParser.SavedGameState;
import net.blerf.ftl.parser.SavedGameParser.ShieldsInfo;
import net.blerf.ftl.parser.SavedGameParser.ShipState;
import net.blerf.ftl.parser.SavedGameParser.SquareState;
import net.blerf.ftl.parser.SavedGameParser.SystemType;
import net.blerf.ftl.parser.SavedGameParser.UnknownZeus;
//import net.blerf.ftl.ui.FTLFrame;
import net.blerf.ftl.xml.ShipBlueprint;
import net.blerf.ftl.xml.SystemBlueprint;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.jdom2.JDOMException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBException;

//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.iceburg.ftl.homeworld.resource.ResourceClass;
import org.iceburg.ftl.homeworld.ui.HomeworldFrame;

//CREDITS:
//Credit to by Vhati and ComaToes for their FTLEditor and allowing me to use their source
//Their code was used as a model and I borrowed or referred to it whenever possible in order to save time/ reduce waste
//In order to get pics of each ship, I borrowed a lot of their code in order to interact with their datamanager
//In Ship Parser issues I borrowed a lot to.
//FTL Editor found here: http://www.ftlgame.com/forum/viewtopic.php?f=7&t=10959&start=70
//I made the custom background from FTL resources and a modified space dock based on schematics from: http://www.shipschematics.net/
//So the idea is to make this the main program runner, and then each 'sub program' be it's own panel
//So this class contains the main file, and the main GUI
//Panels:
//-Warehouse (cargobay)- store items
//-Personal - change crew name / looks
//-Hanger - Make ships
//-Science Bay - breakdown items, then can be bought
// - Personel - rename and customize look of crew
//TODO outline:
//test!
//Not creating config file?
//Add look and feel code
public class FTLHomeworld {
	// private static final Logger log = LogManager.getLogger(FTLHomeworld.class);
	public static File save_location = null;
	public static File homeworld_save = null, homeworld_saveAE = null;
	public static File datsPath = null;
	public static SavedGameParser savedGameParser;
	public static final String APP_NAME = "FTL Homeworld - AE";
	private static final float APP_VERSION = (float) 3.1;
	private static final Logger log = LogManager.getLogger(FTLHomeworld.class);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// a lot of this stuff in the main method from Vhati's program as well.
		// Don't use the hard drive to buffer streams during ImageIO.read().
		ImageIO.setUseCache(false); // Small images don't need extra buffering.
		File propFile = new File("ftl-homeworld.cfg");
		boolean writeConfig = false;
		Properties config = new Properties();
		ImageIO.setUseCache(false); // Small images don't need extra buffering.
		savedGameParser = new SavedGameParser();
		// Read the config file.
		InputStream in = null;
		try {
			if (propFile.exists()) {
				// log.trace( "Loading properties from config file." );
				in = new FileInputStream(propFile);
				config.load(in);
			} else {
				writeConfig = true; // Create a new cfg, but only if necessary.
				// System.out.println("Config doesn't exist, writing at the end");
			}
		} catch (IOException e) {
			// log.error( "Error loading config.", e );
			showErrorDialog("Error loading config from " + propFile.getPath());
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// FTL Resources Path.
		String datsPathString = config.getProperty("ftlDatsPath");
		if (datsPathString != null && datsPathString.length() > 0) {
			// log.info( "Using FTL dats path from config: " + datsPathString );
			datsPath = new File(datsPathString);
			if (isDatsPathValid(datsPath) == false) {
				// log.error( "The config's ftlDatsPath does not exist, or it lacks data.dat." );
				datsPath = null;
			}
		} else {
			// log.trace( "No FTL dats path previously set." );
		}
		// FTL Save Path.
		String savePathString = config.getProperty("ftlSavePath");
		if (savePathString != null) {
			// log.info( "Using FTL dats path from config: " + savePathString );
			save_location = new File(savePathString);
			if (isSavePathValid(save_location) == false) {
				// log.error( "The config's ftlSavePath does not exist." );
				save_location = null;
			}
		} else {
			// log.trace( "No FTL save path previously set." );
		}
		// FTL Homeworld.sav Path.
		String homeworldSaveString = config.getProperty("HomeworldSave");
		if (homeworldSaveString != null) {
			// log.info( "Using FTL dats path from config: " + savePathString );
			homeworld_save = new File(homeworldSaveString);
			if (homeworld_save.exists() == false) {
				// System.out.println("Didn't grab homeworld save from config");
				// log.error( "The config's ftlSavePath does not exist." );
				homeworld_save = null;
			}
		}
		// FTL HomeworldAE.sav Path.
		String homeworldSaveStringAE = config.getProperty("HomeworldSaveAE");
		if (homeworldSaveStringAE != null) {
			// log.info( "Using FTL dats path from config: " + savePathString );
			homeworld_saveAE = new File(homeworldSaveStringAE);
			if (homeworld_saveAE.exists() == false) {
				homeworld_saveAE = null;
			}
		}
		// Find/prompt for the dats path to set in the config.
		if (datsPath == null) {
			datsPath = findFtlPath();
			if (datsPath != null) {
				int response = JOptionPane.showConfirmDialog(null, "FTL resources were found in:\n"
						+ datsPath.getPath() + "\nIs this correct?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.NO_OPTION)
					datsPath = null;
			}
			if (datsPath == null) {
				// log.debug("FTL dats path was not located automatically. Prompting user for location.");
				datsPath = promptForFtlPath();
			}
			if (datsPath != null) {
				config.setProperty("ftlDatsPath", datsPath.getAbsolutePath());
				writeConfig = true;
				// log.info( "FTL dats located at: " + datsPath.getAbsolutePath() );
			}
		}
		if (datsPath == null) {
			showErrorDialog("FTL dats path was not found.\nFTL Homeworld will now exit.");
			// log.debug( "No FTL dats path found, exiting." );
			System.exit(1);
		}
		// Find/prompt for the save path to set in the config.
		if (save_location == null) {
			for (File file : FTLHomeworld.getPossibleUserDataLocations("prof.sav")) {
				if (file.exists()) {
					save_location = file.getParentFile();
					break;
				}
			}
			if (save_location != null) {
				int response = JOptionPane.showConfirmDialog(null, "FTL saves were found in:\n"
						+ save_location.getPath() + "\nIs this correct?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.NO_OPTION)
					save_location = null;
			}
			if (save_location == null) {
				// log.debug("FTL saves path was not located automatically. Prompting user for location.");
				save_location = promptForSavePath();
			}
			if (save_location != null) {
				config.setProperty("ftlSavePath", save_location.getAbsolutePath());
				writeConfig = true;
				// log.info( "FTL saves located at: " + save_location.getAbsolutePath() );
			}
		}
		if (save_location == null) {
			showErrorDialog("FTL save folder was not found.\nFTL Homeworld will now exit.");
			// log.debug( "No FTL save path found, exiting." );
			System.exit(1);
		}
		// Find/prompt for the Homeworld.sav path to set in the config.
		if (homeworld_save == null) {
			// Is it with the other saves, wherever that is?
			File file = new File(save_location + "//Homeworld.sav");
			if (file.exists()) {
				System.out.println("Homeworld found in saves");
				homeworld_save = file;
			} else {
				for (File f : FTLHomeworld.getPossibleUserDataLocations("Homeworld.sav")) {
					if (f.exists()) {
						homeworld_save = file;
						System.out.println("Homeworld found in possible user location");
						break;
					}
				}
			}
			if (homeworld_save != null) {
				int response = JOptionPane.showConfirmDialog(null, "FTL Homeworld.sav found in:\n"
						+ save_location.getPath() + "\nIs this correct?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.NO_OPTION) {
					homeworld_save = null;
				}
			}
			if (homeworld_save == null) {
				// log.debug("FTL saves path was not located automatically. Prompting user for location.");
				homeworld_save = promptForHomeworldSave("Homeworld.sav");
			}
			if (homeworld_save != null) {
				config.setProperty("HomeworldSave", homeworld_save.getAbsolutePath());
				writeConfig = true;
				// log.info( "FTL saves located at: " + save_location.getAbsolutePath() );
			}
		}
		// Find/prompt for the HomeworldAE.sav path to set in the config.
		if (homeworld_saveAE == null) {
			// Is it with the other saves, wherever that is?
			File file = new File(save_location + "//HomeworldAE.sav");
			if (file.exists()) {
				System.out.println("Homeworld AE found in saves");
				homeworld_saveAE = file;
			} else {
				for (File f : FTLHomeworld.getPossibleUserDataLocations("HomeworldAE.sav")) {
					if (f.exists()) {
						homeworld_saveAE = file;
						System.out.println("Homeworld AE found in possible user location");
						break;
					}
				}
			}
			if (homeworld_saveAE != null) {
				int response = JOptionPane.showConfirmDialog(null, "FTL HomeworldAE.sav found in:\n"
						+ save_location.getPath() + "\nIs this correct?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.NO_OPTION) {
					homeworld_saveAE = null;
				}
			}
			if (homeworld_saveAE == null) {
				// log.debug("FTL saves path was not located automatically. Prompting user for location.");
				homeworld_saveAE = promptForHomeworldSave("HomeworldAE.sav");
			}
			if (homeworld_saveAE != null) {
				config.setProperty("HomeworldSaveAE", homeworld_saveAE.getAbsolutePath());
				writeConfig = true;
				// log.info( "FTL saves located at: " + save_location.getAbsolutePath() );
			}
		}

		// Parse the dats.
		System.out.println("Parsing dats");
		try {
			DefaultDataManager dataManager = new DefaultDataManager( datsPath );
//			DefaultDataManager dataManager = new DefaultDataManager(datsPath);
			DataManager.setInstance(dataManager);
			dataManager.setDLCEnabledByDefault(true);
		} catch (Exception e) {
			// log.error( "Error parsing FTL resources.", e );
			showErrorDialog("Error parsing FTL resources.");
			System.exit(1);
		}
		// gen homeworld save if needed. Needs to run after data parse
		if (homeworld_save == null) {
			showErrorDialog("Homeworld.sav was not found.\nFTL Homeworld will create one in the save folder.\nIf this is the first time running FTL Homeworld, don't worry, this should happen.");
			// log.debug( "No FTL dats path found, exiting." );
			// properly update cfg
			SavedGameParser parser = new SavedGameParser();
			File homeworldFile = new File(save_location + "\\Homeworld.sav");
			SavedGameState homeSave = new SavedGameState();
			homeSave.setPlayerShipState(new ShipState("Spacedock Storage", "PLAYER_SHIP_EASY", "kestral", "kestral", false));
			createShip(homeSave, DataManager.get().getShip("PLAYER_SHIP_EASY"), false);
			homeSave.setPlayerShipName("Spacedock Storage");
			homeSave.setDLCEnabled(false);
			// homeSave.setRebelFlagshipState(new RebelFlagshipState(new String[1]));
			// homeSave.getPlayerShipState().setShipName("Spacedock Storage");
			OutputStream out = null;
			try {
				out = new FileOutputStream(homeworldFile);
				parser.writeSavedGame(out, homeSave);
				// write the new save to config
				homeworld_save = homeworldFile;
				config.setProperty("HomeworldSave", homeworld_save.getAbsolutePath());
				writeConfig = true;
				// log.info( "FTL saves located at: " + save_location.getAbsolutePath() );
			} catch (FileNotFoundException e) {
				// Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (homeworld_save == null) {
				showErrorDialog("Homeworld save file was not able to be created.\nFTL Homeworld will now exit.");
				// log.debug( "No Homeworld.sav found, exiting." );
				System.exit(1);
			}
		}
		// gen homeworldAE save if needed. Needs to run after data parse
		if (homeworld_saveAE == null) {
			showErrorDialog("HomeworldAE.sav was not found.\nFTL Homeworld will create one in the save folder.\nIf this is the first time running FTL Homeworld AE Update (3.0 or higher), don't worry, this should happen.");
			// log.debug( "No FTL dats path found, exiting." );
			// properly update cfg
			SavedGameParser parser = new SavedGameParser();
			File homeworldFile = new File(save_location + "\\HomeworldAE.sav");
			SavedGameState homeSave = new SavedGameState();
			homeSave.setPlayerShipState(new ShipState("Spacedock Storage AE", "PLAYER_SHIP_EASY", "kestral", "kestral", false));
			createShip(homeSave, DataManager.get().getShip("PLAYER_SHIP_EASY"), false);
			homeSave.setPlayerShipName("Spacedock Storage AE");
			homeSave.setDLCEnabled(true);
			// homeSave.setRebelFlagshipState(new RebelFlagshipState(new String[1]));
			// homeSave.getPlayerShipState().setShipName("Spacedock Storage");
			OutputStream out = null;
			try {
				out = new FileOutputStream(homeworldFile);
				parser.writeSavedGame(out, homeSave);
				// write the new save to config
				homeworld_saveAE = homeworldFile;
				config.setProperty("HomeworldSaveAE", homeworld_saveAE.getAbsolutePath());
				writeConfig = true;
				// log.info( "FTL saves located at: " + save_location.getAbsolutePath() );
			} catch (FileNotFoundException e) {
				// Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (homeworld_saveAE == null) {
				showErrorDialog("HomeworldAE save file was not able to be created.\nFTL Homeworld will now exit.");
				// log.debug( "No Homeworld.sav found, exiting." );
				System.exit(1);
			}
		}
		if (writeConfig) {
			// System.out.println("Attempting to write config!");
			OutputStream out = null;
			try {
				out = new FileOutputStream(propFile);
				config.store(out, "FTL Homeworld - Config File");
			} catch (IOException e) {
				// log.error( "Error saving config to " + propFile.getPath(), e );
				showErrorDialog("Error saving config to " + propFile.getPath());
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		// moved from runnable so cargobay can init properly
		try {
			// FTLFrame frame = new FTLFrame( APP_NAME, APP_VERSION );
			HomeworldFrame frame = new HomeworldFrame(APP_NAME, APP_VERSION);
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			 log.error( "Exception while creating FTLFrame.", e );
			System.exit(1);
		}
	}
	public static void launchFTL() {
		System.out.println("launched");
		// File gameFolder = new File(FTLHomeworld.datsPath.getParentFile() + "\\");
		// String[] command = {new String(FTLHomeworld.datsPath.getParentFile() + "\\FTLGame.exe" )};
		// Runtime.getRuntime().exec(command);
		// Ripped from KartoFlane's code. Thanks!
		File ftl = new File(FTLHomeworld.datsPath.getParentFile().getAbsolutePath()
				+ "/FTLGame.exe");
		// log.info(String.format("Running FTL... [%s]", ftl.getAbsolutePath()));
		if (ftl.exists())
			try {
				ProcessBuilder builder = new ProcessBuilder(ftl.getAbsolutePath());
				builder.directory(ftl.getParentFile()); // this call corrects the working directory for the exe
				builder.start();
			} catch (IOException ex) {
				// log.error("An exception occured while executing FTL.");
				ex.printStackTrace();
			}
	}
	// else
	// // log.error("Could not find FTL executable.");
	// }
	// some functions ripped straight from FTLProfileEditor because they were private
	public static void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	private static boolean isDatsPathValid(File path) {
		return (path.exists() && path.isDirectory() && new File(path, "data.dat").exists());
	}
	private static boolean isSavePathValid(File path) {
		return (path.exists() && path.isDirectory());
	}
	private static File promptForFtlPath() {
		File ftlPath = null;
		String message = "FTL Homeworld uses images and data from FTL,\n";
		message += "but the path to FTL's resources could not be guessed.\n\n";
		message += "You will now be prompted to locate FTL manually.\n";
		message += "Select '(FTL dir)/resources/data.dat'.\n";
		message += "Or 'FTL.app', if you're on OSX.";
		JOptionPane.showMessageDialog(null, message, "FTL Not Found", JOptionPane.INFORMATION_MESSAGE);
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find data.dat or FTL.app");
		fc.addChoosableFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "FTL Data File - (FTL dir)/resources/data.dat";
			}
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().equals("data.dat")
						|| f.getName().equals("FTL.app");
			}
		});
		fc.setMultiSelectionEnabled(false);
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			if (f.getName().equals("data.dat"))
				ftlPath = f.getParentFile();
			else if (f.getName().endsWith(".app") && f.isDirectory()) {
				File contentsPath = new File(f, "Contents");
				if (contentsPath.exists() && contentsPath.isDirectory()
						&& new File(contentsPath, "Resources").exists())
					ftlPath = new File(contentsPath, "Resources");
			}
			// log.trace( "User selected: " + ftlPath.getAbsolutePath() );
		} else {
			// log.trace( "User cancelled FTL dats path selection." );
		}
		if (ftlPath != null && isDatsPathValid(ftlPath)) {
			return ftlPath;
		}
		return null;
	}
	public static File promptForSavePath() {
		File ftlPath = null;
		String message = "FTL Homeworld manages saves from FTL,\n";
		message += "but the path to FTL's saves could not be guessed.\n\n";
		message += "You will now be prompted to locate them manually.\n";
		message += "Select '/Documents/My Games/FasterThanLight/continue.sav'\n";
		JOptionPane.showMessageDialog(null, message, "FTL Save Not Found", JOptionPane.INFORMATION_MESSAGE);
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find continue.sav or ae continue.sav ");
		fc.addChoosableFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "FTL Data File - /Documents/My Games/FasterThanLight/continue.sav";
			}
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().equals("continue.sav")
						|| f.getName().equals("prof.sav");
			}
		});
		fc.setMultiSelectionEnabled(false);
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			ftlPath = f.getParentFile();
			// log.trace( "User selected: " + ftlPath.getAbsolutePath() );
		} else {
			// log.trace( "User cancelled FTL dats path selection." );
		}
		if (ftlPath != null && isSavePathValid(ftlPath)) {
			return ftlPath;
		}
		return null;
	}
	public static File promptForHomeworldSave(final String title) {
		File f = null;
		String message = "FTL Homeworld stores items in Homeworld.sav and AE items in HomeworldAE.sav,\n";
		message += "but the path to " + title +" could not be guessed.\n\n";
		message += "You will now be prompted to locate it manually.\n";
		message += "If you don't hava a " + title +" yet, just cancel the next dialogue and a new " + title + " will be created.'\n";
		JOptionPane.showMessageDialog(null, message, title + " Not Found", JOptionPane.INFORMATION_MESSAGE);
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find "+ title +" - press cancel to create new " + title);
		fc.addChoosableFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "FTL Data File - /Documents/My Games/FasterThanLight/continue.sav";
			}
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().equals(title);
			}
		});
		fc.setMultiSelectionEnabled(false);
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			f = fc.getSelectedFile();
			// log.trace( "User selected: " + ftlPath.getAbsolutePath() );
		} else {
			// log.trace( "User cancelled FTL dats path selection." );
		}
		if (f != null && f.exists()) {
			return f;
		}
		return null;
	}
	private static File findFtlPath() {
		String steamPath = "Steam/steamapps/common/FTL Faster Than Light/resources";
		String gogPath = "GOG.com/Faster Than Light/resources";
		String xdgDataHome = System.getenv("XDG_DATA_HOME");
		if (xdgDataHome == null)
			xdgDataHome = System.getProperty("user.home") + "/.local/share";
		File[] paths = new File[] {
				// Windows - Steam
		new File(new File("" + System.getenv("ProgramFiles(x86)")), steamPath), new File(new File(""
				+ System.getenv("ProgramFiles")), steamPath),
				// Windows - GOG
		new File(new File("" + System.getenv("ProgramFiles(x86)")), gogPath), new File(new File(""
				+ System.getenv("ProgramFiles")), gogPath),
				// Linux - Steam
		new File(xdgDataHome
				+ "/Steam/SteamApps/common/FTL Faster Than Light/data/resources"),
				// OSX - Steam
		new File(System.getProperty("user.home")
				+ "/Library/Application Support/Steam/SteamApps/common/FTL Faster Than Light/FTL.app/Contents/Resources"),
				// OSX
		new File("/Applications/FTL.app/Contents/Resources") };
		File ftlPath = null;
		for (File path : paths) {
			if (isDatsPathValid(path)) {
				ftlPath = path;
				break;
			}
		}
		return ftlPath;
	}
	public static File[] getPossibleUserDataLocations(String fileName) {
		if (fileName == null)
			fileName = "";
		String xdgDataHome = System.getenv("XDG_DATA_HOME");
		if (xdgDataHome == null)
			xdgDataHome = System.getProperty("user.home") + "/.local/share";
		File[] locations = new File[] {
				// Windows XP
		new File(System.getProperty("user.home")
				+ "/My Documents/My Games/FasterThanLight/" + fileName),
				// Windows Vista/7
		new File(System.getProperty("user.home") + "/Documents/My Games/FasterThanLight/"
				+ fileName),
				// Linux
		new File(xdgDataHome + "/FasterThanLight/" + fileName),
				// OSX
		new File(System.getProperty("user.home")
				+ "/Library/Application Support/FasterThanLight/" + fileName) };
		return locations;
	}
	/**
	 * Create a complete 'defualt' ship ready to be written to a save file
	 * 
	 * @param game
	 *            - savedgame state can be newly created, needs a playershipstate added
	 * @param shipBlueprint
	 *            - the blueprint needed to build the ship
	 * @param auto
	 *            - auto ship or have crew
	 */
	private static void createShip(SavedGameState game, ShipBlueprint shipBlueprint,
			boolean auto) {
		SavedGameParser.SavedGameState gameState = game;
		if (gameState == null)
			return;
		// Apply all other pending changes.
		// frame.updateGameState( gameState );
		ShipLayout shipLayout = DataManager.get().getShipLayout(shipBlueprint.getLayout());
		SavedGameParser.ShipState shipState = new SavedGameParser.ShipState("Spacedock Storage", shipBlueprint, auto);
		// Systems.
		int reservePowerCapacity = 0;
		for (SystemType systemType : SystemType.values()) {
			SavedGameParser.SystemState systemState = new SavedGameParser.SystemState(systemType);
			// Set capacity for systems that're initially present.
			ShipBlueprint.SystemList.SystemRoom[] systemRoom = shipBlueprint.getSystemList().getSystemRoom(systemType);
			if (systemRoom != null) {
				Boolean start = systemRoom[0].getStart();
				if (start == null || start.booleanValue() == true) {
					SystemBlueprint systemBlueprint = DataManager.get().getSystem(systemType.getId());
					systemState.setCapacity(systemBlueprint.getStartPower());
					if (systemType.isSubsystem()) {
						systemState.setPower(systemState.getCapacity());
					} else {
						reservePowerCapacity += systemState.getCapacity();
					}
				}
			}
			shipState.addSystem(systemState);
		}
		reservePowerCapacity = Math.max(reservePowerCapacity, shipBlueprint.getMaxPower().amount);
		shipState.setReservePowerCapacity(reservePowerCapacity);
		// Rooms.
		for (int r = 0; r < shipLayout.getRoomCount(); r++) {
			EnumMap<ShipLayout.RoomInfo, Integer> roomInfoMap = shipLayout.getRoomInfo(r);
			int squaresH = roomInfoMap.get(ShipLayout.RoomInfo.SQUARES_H).intValue();
			int squaresV = roomInfoMap.get(ShipLayout.RoomInfo.SQUARES_V).intValue();
			SavedGameParser.RoomState roomState = new SavedGameParser.RoomState();
			for (int s = 0; s < squaresH * squaresV; s++) {
				roomState.addSquare(new SquareState());
				// roomState.addSquare( 0, 0, -1);
			}
			shipState.addRoom(roomState);
		}
		// Doors.
		Map<ShipLayout.DoorCoordinate, EnumMap<ShipLayout.DoorInfo, Integer>> layoutDoorMap = shipLayout.getDoorMap();
		for (Map.Entry<ShipLayout.DoorCoordinate, EnumMap<ShipLayout.DoorInfo, Integer>> entry : layoutDoorMap.entrySet()) {
			ShipLayout.DoorCoordinate doorCoord = entry.getKey();
			EnumMap<ShipLayout.DoorInfo, Integer> doorInfo = entry.getValue();
			shipState.setDoor(doorCoord.x, doorCoord.y, doorCoord.v, new SavedGameParser.DoorState());
		}
		// Augments.
		if (shipBlueprint.getAugments() != null) {
			for (ShipBlueprint.AugmentId augId : shipBlueprint.getAugments())
				shipState.addAugmentId(augId.name);
		}
		// Supplies.
		shipState.setHullAmt(shipBlueprint.getHealth().amount);
		shipState.setFuelAmt(0);
		if (shipBlueprint.getDroneList() != null)
			shipState.setDronePartsAmt(shipBlueprint.getDroneList().drones);
		if (shipBlueprint.getWeaponList() != null)
			shipState.setMissilesAmt(shipBlueprint.getWeaponList().missiles);
		gameState.setPlayerShipState(shipState);
		// Sync session's redundant ship info with player ship.
		gameState.setPlayerShipName(gameState.getPlayerShipState().getShipName());
		gameState.setPlayerShipBlueprintId(gameState.getPlayerShipState().getShipBlueprintId());
		
		// set additional flags that need to not be null for writeship
		game.setRebelFlagshipState(new RebelFlagshipState());
		game.setHeaderAlpha(9);
		game.setDLCEnabled(true);
		ArrayList<SavedGameParser.ExtendedSystemInfo> infos = new ArrayList<SavedGameParser.ExtendedSystemInfo>();
		ShieldsInfo s = new ShieldsInfo();
		infos.add(s);
		//TODO - bug preventign this from working currently in origional code -submitted github
		shipState.setExtendedSystemInfoList(infos);
		game.setEncounter(new EncounterState());
		game.setEnvironment(new EnvironmentState());
		game.setUnknownZeus(new UnknownZeus());
		game.getUnknownZeus().setPlayerExtendedShipInfo(new ExtendedShipInfo());
		// frame.loadGameState( gameState );
//		System.out.println("Created ship");
	}
}
