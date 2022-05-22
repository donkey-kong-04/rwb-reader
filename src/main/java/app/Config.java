package app;

import java.util.ArrayList;
import java.util.ResourceBundle;

import utils.PRUtil;

public class Config {
	public String Name;
	public String SHEET_APEX;
	public String SHEET_TAB_VISIBILITY;
	public String SHEET_LAYOUT_ASSIGNMENT;
	public String SHEET_RECORD_TYPE_ASSIGNMENT;
	public String SHEET_LISTVIEW;
	public String SHEET_SHARING_RULES;
	
	public ArrayList<String> SHEETS_TO_IGNORE;
	public ArrayList<String> KEEP_PROFILES_PERMISSIONS;
	public ArrayList<String> PROFILES_TO_IGNORE;
	
	public String filepath;
	public String package_folder;
	public boolean full_debug;
	public boolean selected;
	public static Config c;
	
	/*
	public static Config loadConfig() {
		if(c == null) {
			c = new Config();
			ResourceBundle wb = ResourceBundle.getBundle("config");
			String config_to_load = wb.getString("LOAD_CONFIG");
			
			ResourceBundle config = ResourceBundle.getBundle(config_to_load.trim());
			
			c.filepath = config.getString("file");
			c.package_folder = config.getString("folder");
			c.full_debug = config.getString("full_debug").toLowerCase().equals("true");
			
			c.SHEET_APEX = config.getString("SHEET_APEX");
			c.SHEET_TAB_VISIBILITY = config.getString("SHEET_TAB_VISIBILITY");
			c.SHEET_LAYOUT_ASSIGNMENT = config.getString("SHEET_LAYOUT_ASSIGNMENT");
			c.SHEET_RECORD_TYPE_ASSIGNMENT = config.getString("SHEET_RECORD_TYPE_ASSIGNMENT");
			c.SHEET_LISTVIEW = config.getString("SHEET_LISTVIEW");
			c.SHEET_SHARING_RULES = config.getString("SHEET_SHARING_RULES");
			
			c.SHEETS_TO_IGNORE = new ArrayList<String>();
			c.SHEETS_TO_IGNORE.add(c.SHEET_APEX);
			c.SHEETS_TO_IGNORE.add(c.SHEET_TAB_VISIBILITY);
			c.SHEETS_TO_IGNORE.add(c.SHEET_LAYOUT_ASSIGNMENT);
			c.SHEETS_TO_IGNORE.add(c.SHEET_RECORD_TYPE_ASSIGNMENT);
			c.SHEETS_TO_IGNORE.add(c.SHEET_LISTVIEW);
			c.SHEETS_TO_IGNORE.add(c.SHEET_SHARING_RULES);
			
			for(String s : config.getString("SHEETS_TO_IGNORE").split(",")) {
				if(!PRUtil.isBlank(s)) {
					c.SHEETS_TO_IGNORE.add(s.trim());
				}
			}
			
			c.PROFILES_TO_IGNORE = new ArrayList<String>();
			for(String s : config.getString("PROFILES_TO_IGNORE").split(",")) {
				if(!PRUtil.isBlank(s)) {
					c.PROFILES_TO_IGNORE.add(s.trim());
				}
			}
			
			c.KEEP_PROFILES_PERMISSIONS = new ArrayList<String>();
			for(String s : config.getString("PROFILES_DO_NOT_REMOVE_PERMISSIONS").split(",")) {
				if(!PRUtil.isBlank(s)) {
					c.KEEP_PROFILES_PERMISSIONS.add(s.trim() + ".profile");
				}
			}
		}
		return c;
	}*/
}
