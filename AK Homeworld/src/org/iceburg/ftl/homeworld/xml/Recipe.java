package org.iceburg.ftl.homeworld.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Recipe")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recipe {
	@XmlAttribute(name = "name")
	private int cost;
	private String ingredient1;
	private String ingredient2;
	private String ingredient3;
	Boolean unlocked;

	public Recipe(String rName) {
		// name = rName;
		cost = 20;
		ingredient1 = null;
		ingredient2 = null;
		ingredient3 = null;
		unlocked = true;
	}
	public Boolean getUnlocked() {
		return unlocked;
	}
	public void setUnlocked(Boolean unlocked) {
		this.unlocked = unlocked;
	}
	public int getCost() {
		return cost;
	}
	public String getIngredient1() {
		return ingredient1;
	}
	public String getIngredient2() {
		return ingredient2;
	}
	public String getIngredient3() {
		return ingredient3;
	}
}
