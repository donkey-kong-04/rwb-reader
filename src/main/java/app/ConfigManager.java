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
				
				c.SHEET_LAYOUT_ASSIGNMENT = (String) j.get("SHEET_LAYOUT_ASSIGNMENT");
				
				c.SHEET_RECORD_TYPE_ASSIGNMENT = (String) j.get("SHEET_RECORD_TYPE_ASSIGNMENT");
				
				c.SHEET_PROFILE = (String) j.get("SHEET_PROFILE");
				c.SHEET_PS = (String) j.get("SHEET_PS");
				
				
				c.READ_LIST_VIEW = getBool(j, "READ_LIST_VIEW");
				c.SHEET_LISTVIEW = (String) j.get("SHEET_LISTVIEW");
				
				c.READ_SHARING_RULES = getBool(j, "READ_SHARING_RULES");
				c.SHEET_SHARING_RULES = (String) j.get("SHEET_SHARING_RULES");
				
				
				String[] toIgnores = ((String) j.get("SHEETS_TO_IGNORE")).split(",");
			
				c.SHEETS_TO_IGNORE = new ArrayList<String>();
			
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
				
				System.out.println(c.READ_LIST_VIEW);
				if(c.selected) {
					selected = c;
				}
				
				configs.put(c.Name, c);
			}
			
			reader.close();
		
	}
	
	private boolean getBool(JSONObject json, String key) {

		Object o = json.get(key);
		if(o instanceof Boolean) {
			return (boolean) o;
		} else {
			return false;
		}
	}
	
}
