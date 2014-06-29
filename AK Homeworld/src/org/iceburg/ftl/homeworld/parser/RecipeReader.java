package org.iceburg.ftl.homeworld.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import net.blerf.ftl.parser.DataManager;
//import net.blerf.ftl.parser.MappedDatParser;

import org.iceburg.ftl.homeworld.xml.Recipe;

//Modeled after net.blerf.ftl.parser
//Reads the recipe.xml file
//TODO
//set up so it reads recipe.xml from ftl.dat, but if ftl.dat has no xml, then it reads the default from within the jar
public class RecipeReader {
	private static final String BOM_UTF8 = "\uFEFF";

	public Recipe readBlueprints(InputStream stream, String fileName) throws IOException,
			JAXBException {
		// log.trace("Reading blueprints XML");
		// Need to clean invalid XML and comments before JAXB parsing
		BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF8"));
		StringBuilder sb = new StringBuilder();
		String line;
		boolean comment = false;
		String ptn;
		Pattern p;
		Matcher m;
		while ((line = in.readLine()) != null) {
			line = line.replaceAll("<[?]xml [^>]*[?]>", "");
			line = line.replaceAll("<!--.*?-->", "");
			// Remove multiline comments
			if (comment && line.contains("-->"))
				comment = false;
			else if (line.contains("<!--"))
				comment = true;
			else if (!comment)
				sb.append(line).append("\n");
		}
		in.close();
		if (sb.substring(0, BOM_UTF8.length()).equals(BOM_UTF8))
			sb.replace(0, BOM_UTF8.length(), "");
		// XML has multiple root nodes so need to wrap.
		sb.insert(0, "<blueprints>\n");
		sb.append("</blueprints>\n");
		// Add the xml header.
		sb.insert(0, "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		if ("blueprints.xml".equals(fileName)) {
			// blueprints.xml: PLAYER_SHIP_HARD_2 shipBlueprint (FTL 1.03.1)
			ptn = "";
			ptn += "(<shields *(?: [^>]*)?>\\s*";
			ptn += "<slot *(?: [^>]*)?>\\s*";
			ptn += "(?:<direction>[^<]*</direction>\\s*)?";
			ptn += "(?:<number>[^<]*</number>\\s*)?";
			ptn += "</slot>\\s*)";
			ptn += "</slot>"; // Wrong closing tag.
			p = Pattern.compile(ptn);
			m = p.matcher(sb);
			if (m.find()) {
				sb.replace(m.start(), m.end(), m.group(1) + "</shields>");
				m.reset();
			}
			// blueprints.xml: SYSTEM_CASING augBlueprint (FTL 1.02.6)
			ptn = "";
			ptn += "\\s*<title>Reinforced System Casing</title>"; // Extra title.
			ptn += "(\\s*<title>Titanium System Casing</title>)";
			p = Pattern.compile(ptn);
			m = p.matcher(sb);
			if (m.find()) {
				sb.replace(m.start(), m.end(), m.group(1));
				m.reset();
			}
		}
		if ("blueprints.xml".equals(fileName) || "autoBlueprints.xml".equals(fileName)) {
			// blueprints.xml: DEFAULT shipBlueprint (FTL 1.03.1)
			// autoBlueprints.xml: AUTO_BASIC shipBlueprint (FTL 1.03.1)
			// autoBlueprints.xml: AUTO_ASSAULT shipBlueprint (FTL 1.03.1)
			ptn = "";
			ptn += "(<shipBlueprint *(?: [^>]*)?>\\s*";
			ptn += "<class>[^<]*</class>\\s*";
			ptn += "<systemList *(?: [^>]*)?>\\s*";
			ptn += "(?:<[a-zA-Z]+ *(?: [^>]*)?/>\\s*)*";
			ptn += "</systemList>\\s*";
			ptn += "(?:<droneList *(?: [^>]*)?>\\s*";
			ptn += "(?:<[a-zA-Z]+ *(?: [^>]*)?/>\\s*)*";
			ptn += "</droneList>\\s*)?";
			ptn += "(?:<weaponList *(?: [^>]*)?>\\s*";
			ptn += "(?:<[a-zA-Z]+ *(?: [^>]*)?/>\\s*)*";
			ptn += "</weaponList>\\s*)?";
			ptn += "(?:<[a-zA-Z]+ *(?: [^>]*)?/>\\s*)*)";
			ptn += "</ship>"; // Wrong closing tag.
			p = Pattern.compile(ptn);
			m = p.matcher(sb);
			while (m.find()) {
				sb.replace(m.start(), m.end(), m.group(1) + "</shipBlueprint>");
				m.reset();
			}
		}
		// Parse cleaned XML
		// Recipe re = null;
		// TODO
		// Recipe re = (Recipe)DataManager.get().unmarshalFromSequence( Recipe.class, sb.toString() );
		Recipe re = new Recipe("");
		return re;
	}
}
