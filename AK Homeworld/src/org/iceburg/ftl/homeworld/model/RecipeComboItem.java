package org.iceburg.ftl.homeworld.model;

import org.iceburg.ftl.homeworld.xml.Recipe;

public class RecipeComboItem {
	private String title;
	private Recipe recipe;

	public RecipeComboItem(String title, Recipe recipe) {
		this.title = title;
		this.recipe = recipe;
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
}
