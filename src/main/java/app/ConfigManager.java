package app;


import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ConfigManager {
	public static HashMap<String, Config> configs = new HashMap<String, Config>();
	public static Config selected;
	
	public ConfigManager() throws IOException, ParseException {
		
		JSONParser p = new JSONParser();
		
			FileReader reader = new FileReader("./reader.json");
			
			Object obj = p.parse(reader);
			
			JSONArray jsonConfigs = (JSONArray) obj;
			
			for(Object o : jsonConfigs) {
				
				JSONObject j = (JSONObject) o;
				
				Config c = new Config();
				
				c.Name = (String) j.get("name");
				
				c.selected = (Boolean) j.get("selected");
				c.SHEET_APEX = (String) j.get("SHEET_APEX");
				c.SHEET_TAB_VISIBILITY = (String) j.get("");
				c.SHEET_LAYOUT_ASSIGNMENT = (String) j.get("SHEET_LAYOUT_ASSIGNMENT");
				c.SHEET_RECORD_TYPE_ASSIGNMENT = (String) j.get("SHEET_RECORD_TYPE_ASSIGNMENT");
				c.SHEET_APEX = (String) j.get("SHEET_APEX");
				
				c.SHEET_LISTVIEW = (String) j.get("SHEET_LISTVIEW");
				c.SHEET_SHARING_RULES = (String) j.get("SHEET_SHARING_RULES");
				c.SHEET_TAB_VISIBILITY = (String) j.get("SHEET_TAB_VISIBILITY");
				String[] toIgnores = ((String) j.get("SHEETS_TO_IGNORE")).split(",");
			
				c.SHEETS_TO_IGNORE = new ArrayList<String>();
				/*
				
				*/
				for(String i : toIgnores) {
					c.SHEETS_TO_IGNORE.add(i);
				}
				
				String[] keepPerms = ((String) j.get("KEEP_PROFILES_PERMISSIONS")).split(",");
				
				c.KEEP_PROFILES_PERMISSIONS = new ArrayList<String>();
				for(String i : keepPerms) {
					c.KEEP_PROFILES_PERMISSIONS.add(i);
				}
				
				String[] profilesRemove = ((String) j.get("PROFILES_TO_IGNORE")).split(",");
				
				c.PROFILES_TO_IGNORE = new ArrayList<String>();
				for(String i : profilesRemove) {
					c.PROFILES_TO_IGNORE.add(i.trim().toLowerCase());
				}
				c.filepath = (String) j.get("file");
				c.package_folder = (String) j.get("folder");
				c.full_debug = (Boolean) j.get("full_debug");
				
				if(c.selected) {
					selected = c;
				}
				
				configs.put(c.Name, c);
			}
			
			reader.close();
		
	}
	
}
