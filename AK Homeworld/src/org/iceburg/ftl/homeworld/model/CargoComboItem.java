package org.iceburg.ftl.homeworld.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser.DroneState;
import net.blerf.ftl.parser.SavedGameParser.SavedGameState;
import net.blerf.ftl.parser.SavedGameParser.ShipState;
import net.blerf.ftl.parser.SavedGameParser.WeaponState;

public class CargoComboItem {
	private String title;
	private String id;

	public CargoComboItem(String id, String title) {
		this.title = title;
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String toString() {
		return title;
	}
	// Convert weapons list to string array for combo box - use hashmap for count
	public static ArrayList<CargoComboItem> weaponToCombo(ShipState state,
			HashMap<String, Integer> map) {
		ArrayList<CargoComboItem> al = new ArrayList<CargoComboItem>();
		if (state.getWeaponList().size() > 0) {
			for (WeaponState w : state.getWeaponList()) {
				// multiple missles = Hull Missiles x2
				if (map.containsKey(w.getWeaponId())) {
					// increase the item's count
					map.put(w.getWeaponId(), (map.get(w.getWeaponId()) + 1));
				} else {
					// add the first item
					map.put(w.getWeaponId(), 1);
				}
			}
			// convert hashmap to string
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				al.add(new CargoComboItem((String) pairs.getKey(), DataManager.get().getWeapon((String) pairs.getKey()).getTitle()
						+ " x" + pairs.getValue()));
			}
		} else {
			al.add(new CargoComboItem("", "No Weapons!"));
		}
		return al;
	}
	// Convert augments list to string array for combo box - use hashmap for count
	public static ArrayList<CargoComboItem> augmentToCombo(ShipState state,
			HashMap<String, Integer> map) {
		ArrayList<CargoComboItem> al = new ArrayList<CargoComboItem>();
		if (state.getAugmentIdList().size() > 0) {
			for (String s : state.getAugmentIdList()) {
				if (map.containsKey(s)) {
					// increase the item's count
					map.put(s, (map.get(s) + 1));
				} else {
					// add the first item
					map.put(s, 1);
				}
			}
			// convert hashmap to string
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				al.add(new CargoComboItem((String) pairs.getKey(), DataManager.get().getAugment((String) pairs.getKey()).getTitle()
						+ " x" + pairs.getValue()));
			}
		} else {
			al.add(new CargoComboItem("", "No Augments!"));
		}
		return al;
	}
	// Convert drones list to string array for combo box - use hashmap for count
	public static ArrayList<CargoComboItem> droneToCombo(ShipState state,
			HashMap<String, Integer> map) {
		ArrayList<CargoComboItem> al = new ArrayList<CargoComboItem>();
		if (state.getDroneList().size() > 0) {
			for (DroneState d : state.getDroneList()) {
				if (map.containsKey(d.getDroneId())) {
					// increase the item's count
					map.put(d.getDroneId(), (map.get(d.getDroneId()) + 1));
				} else {
					// add the first item
					map.put(d.getDroneId(), 1);
				}
			}
			// convert hashmap to string
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				al.add(new CargoComboItem((String) pairs.getKey(), DataManager.get().getDrone((String) pairs.getKey()).getTitle()
						+ " x" + pairs.getValue()));
			}
		} else {
			al.add(new CargoComboItem("", "No Drones!"));
		}
		return al;
	}
	// Convert cargo list to string array for combo box - use hashmap for count
	public static ArrayList<CargoComboItem> cargoToCombo(SavedGameState state,
			HashMap<String, Integer> map) {
		ArrayList<CargoComboItem> al = new ArrayList<CargoComboItem>();
		if (state.getCargoIdList().size() > 0) {
			for (String s : state.getCargoIdList()) {
				if (map.containsKey(s)) {
					// increase the item's count
					map.put(s, (map.get(s) + 1));
				} else {
					// add the first item
					map.put(s, 1);
				}
			}
			// convert hashmap to string
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				String st = (String) pairs.getKey();
				// find out what type it is and add the appropriate name to the list
				if (DataManager.get().getAugment(st) != null) {
					al.add(new CargoComboItem(st, DataManager.get().getAugment(st).getTitle()
							+ " x" + pairs.getValue()));
				} else if (DataManager.get().getWeapon(st) != null) {
					al.add(new CargoComboItem(st, DataManager.get().getWeapon(st).getTitle()
							+ " x" + pairs.getValue()));
				} else if (DataManager.get().getDrone(st) != null) {
					al.add(new CargoComboItem(st, DataManager.get().getDrone(st).getTitle()
							+ " x" + pairs.getValue()));
				}
			}
		} else {
			al.add(new CargoComboItem("", "No Cargo!"));
		}
		return al;
	}
	public static CargoComboItem getCargoItem(JComboBox box, String id) {
		int i = 0;
		CargoComboItem wi = null;
		while (i < box.getItemCount()) {
			wi = (CargoComboItem) box.getItemAt(i);
			if (wi.getId().equals(id)) {
				return wi;
			}
			i = (i + 1);
		}
		return wi;
	}
	// Returns the first weaponstate with this ID
	// Used to find a weaponstate of the right weapon, then delete it from the save
	public static WeaponState getWSFromID(List<WeaponState> list, String id) {
		int i = 0;
		WeaponState ws = null;
		while (i < list.size()) {
			ws = list.get(i);
			if (ws.getWeaponId().equals(id)) {
				return ws;
			}
			i = (i + 1);
		}
		return ws;
	}
	// Returns the first dronestate with this ID
	// Used to find a dronestate of the right weapon, then delete it from the save
	public static DroneState getDSFromID(List<DroneState> list, String id) {
		int i = 0;
		DroneState ds = null;
		while (i < list.size()) {
			ds = list.get(i);
			if (ds.getDroneId().equals(id)) {
				return ds;
			}
			i = (i + 1);
		}
		return ds;
	}
}
