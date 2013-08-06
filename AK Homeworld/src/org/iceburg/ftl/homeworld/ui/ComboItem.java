package org.iceburg.ftl.homeworld.ui;

import net.blerf.ftl.parser.SavedGameParser.WeaponState;

public class ComboItem {
	public static class WeaponItem {
		private String title;
	    private WeaponState weapon;

		public WeaponItem(WeaponState weapon, String title) {
	        this.title = title;
	        this.weapon = weapon;
	    }
	    
		public String getTitle() {
			return title;
		}
	
		public void setTitle(String title) {
			this.title = title;
		}
	
		public WeaponState getWeapon() {
			return weapon;
		}
	
		public void setWeapon(WeaponState weapon) {
			this.weapon = weapon;
		}
		@Override
		public String toString() {
			return title;
		}
	}
    

	
	
}
