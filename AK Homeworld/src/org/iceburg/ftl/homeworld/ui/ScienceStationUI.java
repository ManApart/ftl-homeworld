package org.iceburg.ftl.homeworld.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

import org.iceburg.ftl.homeworld.core.FTLHomeworld;
import org.iceburg.ftl.homeworld.xml.Recipe;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.parser.SavedGameParser.SavedGameState;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;

//Create combobox for recipe types
//when type is selected, for each blueprint of that type find matching recipe
//if none, default recipe of type
public class ScienceStationUI extends JPanel implements ActionListener {
	SavedGameState homesave;
	JComboBox recipeTypeCB;
	JComboBox itemCB;

	public ScienceStationUI() {
		init();
	}
	public void init() {
		// get homesave
		SavedGameParser parser = new SavedGameParser();
		try {
			homesave = parser.readSavedGame(FTLHomeworld.homeworld_save);
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		// establish the UI
		setLayout(null);
		String[] recipeTypes = { "Augments", "Weapons", "Drones", "Systems" };
		recipeTypeCB = new JComboBox(recipeTypes);
		recipeTypeCB.setBounds(150, 11, 109, 20);
		recipeTypeCB.addActionListener(this);
		add(recipeTypeCB);
		itemCB = new JComboBox();
		itemCB.setBounds(10, 63, 109, 20);
		itemCB.addActionListener(this);
		add(itemCB);
		JButton btnScrap = new JButton("Scrap");
		btnScrap.setBounds(437, 62, 89, 23);
		btnScrap.addActionListener(this);
		add(btnScrap);
		JButton btnBuild = new JButton("Build");
		btnBuild.setBounds(150, 62, 89, 23);
		btnBuild.addActionListener(this);
		add(btnBuild);
		JLabel lblRequired = new JLabel("Required:");
		lblRequired.setBounds(10, 96, 74, 14);
		add(lblRequired);
		JLabel lblAvailable = new JLabel("Available Stock:");
		lblAvailable.setBounds(150, 96, 74, 14);
		add(lblAvailable);
		JLabel lblYield = new JLabel("Yields:");
		lblYield.setBounds(437, 96, 74, 14);
		add(lblYield);
		JLabel lblIngredientName1 = new JLabel("None");
		lblIngredientName1.setBounds(10, 130, 109, 14);
		add(lblIngredientName1);
		JLabel lblIngredientName2 = new JLabel("None");
		lblIngredientName2.setBounds(10, 155, 109, 14);
		add(lblIngredientName2);
		JLabel lblIngredientName3 = new JLabel("None");
		lblIngredientName3.setBounds(10, 180, 109, 14);
		add(lblIngredientName3);
		JLabel lblIngcount1 = new JLabel("IngCount1");
		lblIngcount1.setBounds(150, 130, 46, 14);
		add(lblIngcount1);
		JLabel lblIngcount2 = new JLabel("IngCount2");
		lblIngcount2.setBounds(150, 155, 46, 14);
		add(lblIngcount2);
		JLabel lblIngcount3 = new JLabel("IngCount3");
		lblIngcount3.setBounds(150, 180, 46, 14);
		add(lblIngcount3);
		JLabel give1 = new JLabel("None");
		give1.setBounds(437, 130, 109, 14);
		add(give1);
		JLabel give2 = new JLabel("None");
		give2.setBounds(437, 155, 109, 14);
		add(give2);
		JLabel give3 = new JLabel("None");
		give3.setBounds(437, 180, 109, 14);
		add(give3);
		JComboBox currentItemsCB = new JComboBox();
		currentItemsCB.setBounds(326, 63, 82, 20);
		add(currentItemsCB);
	}
	// Event Handlers
	public void actionPerformed(ActionEvent ae) {
		// System.out.println("Action performed");
		if (ae.getSource() instanceof JButton) {
			JButton o = (JButton) ae.getSource();
		} else if (ae.getSource() instanceof JComboBox) {
			JComboBox o = (JComboBox) ae.getSource();
			if (o.getSelectedItem().equals("Augments")) {
				System.out.println(o.getSelectedItem() + "selected");
				ArrayList<Recipe> al = new ArrayList<Recipe>();
				ArrayList<String> returnList = new ArrayList<String>();
				// DataManager.get().getAugments()
				Iterator it = DataManager.get().getAugments().entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry) it.next();
					// if recipe exists and is unlocked, add to box
					if (al.contains((String) pairs.getKey())) {
						if (al.get(1).getUnlocked()) { // TODO Get index
							returnList.add((String) pairs.getKey());
						}
					} else {
						// use default recipe
						// TODO need recipe combo item
						// returnList.add(new Recipe((String)pairs.getKey()));
					}
				}
				// itemCB.removeAll();
				// itemCB = new JComboBox<E>(returnList.toArray());
			}
		}
		// get source
		// if source is type, map xml blueprints to recipes
	}
	public void populateRecipes() {
	}
}
