package org.iceburg.ftl.homeworld.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JButton;

import org.iceburg.ftl.homeworld.core.FTLHomeworld;
import net.blerf.ftl.parser.SavedGameParser.SavedGameState;

public class ShipSave {
	private File shipFilePath;
	private JButton boardbtn;
	private String imageInnerPath;
	private SavedGameState save;

	public void setshipFilePath(File filePath) {
		shipFilePath = filePath;
	}
	public File getshipFilePath() {
		return shipFilePath;
	}
	public JButton getBoardbtn() {
		return boardbtn;
	}
	public void setBoardbtn(JButton boardbtn) {
		this.boardbtn = boardbtn;
	}
	public String getImageInnerPath() {
		return imageInnerPath;
	}
	public void setImageInnerPath(String imageInnerPath) {
		this.imageInnerPath = imageInnerPath;
	}
	
	public SavedGameState getSave() {
		return save;
	}
	public void setSave(SavedGameState save) {
		this.save = save;
	}
	public static boolean dockShip(ShipSave ss1, int listLength) {
		boolean success = false;
		File oldFile = ss1.getshipFilePath();
		File newFile = null;
		String fileName;
		int i = 1;
		// && i <= 50
		while (oldFile.exists() && i <= (listLength + 2)) {
			fileName = "continue_" + i + ".sav";
			newFile = new File(FTLHomeworld.save_location, fileName);
			if (!newFile.exists()) {
				// success = oldFile.renameTo(newFile);
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
		return success;
	}
	public static boolean boardShip(ShipSave ss1) {
		boolean success = false;
		File oldFile = ss1.getshipFilePath();
		File newFile = new File(FTLHomeworld.save_location, "continue.sav");
		if (!newFile.exists()) {
			// success = oldFile.renameTo(newFile);
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
