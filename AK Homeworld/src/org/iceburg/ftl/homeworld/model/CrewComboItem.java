package org.iceburg.ftl.homeworld.model;

import java.util.ArrayList;

import net.blerf.ftl.parser.SavedGameParser.CrewState;
import net.blerf.ftl.parser.SavedGameParser.ShipState;

public class CrewComboItem {
	private String title = null;
	private CrewState cs = null;
	
	
	public CrewComboItem(String title, CrewState cs) {
        this.title = title;
        this.cs = cs;
    }
    
	public CrewState getCrewState() {
		return cs;
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
	

	//Convert crew list to string array for combo box
	public static ArrayList<CrewComboItem> crewToCombo(ShipState state) {
		ArrayList<CrewComboItem> al = new ArrayList<CrewComboItem>();	
		if (state.getCrewList().size() > 0){
			for (CrewState c: state.getCrewList()) {
				al.add(new CrewComboItem(c.getName(), c));
			}
		}
		else {
			al.add(new CrewComboItem("No Crew!", new CrewState()));
		}
		return al;
	}	
	public static String toStringSummary(CrewState cs){
		StringBuilder result = new StringBuilder();
//		result.append(String.format("Crewman:              %s\n", cs.getName()));
		result.append(String.format("Race:              %s\n", cs.getRace()));
		result.append(String.format("Sex:               %s\n", (cs.isMale() ? "Male" : "Female") ));
		result.append(String.format("\nPilot Skill:       %3d\n", cs.getPilotSkill()));
		result.append(String.format("Engine Skill:      %3d\n", cs.getEngineSkill()));
		result.append(String.format("Shield Skill:      %3d\n", cs.getShieldSkill()));
		result.append(String.format("Weapon Skill:      %3d\n", cs.getWeaponSkill()));
		result.append(String.format("Repair Skill:      %3d\n", cs.getRepairSkill()));
		result.append(String.format("Combat Skill:      %3d\n", cs.getCombatSkill()));
		result.append(String.format("\nRepairs:           %3d\n", cs.getRepairs()));
		result.append(String.format("Combat Kills:      %3d\n", cs.getCombatKills()));
		result.append(String.format("Piloted Evasions:  %3d\n", cs.getPilotedEvasions()));
		result.append(String.format("Jumps Survived:    %3d\n", cs.getJumpsSurvived()));
		result.append(String.format("Skill Masteries:   %3d\n", cs.getSkillMasteries()));
		return result.toString();
	}
}