package app;

import java.util.ArrayList;


public class Config {
	public String Name;
	
	
	public String SHEET_LAYOUT_ASSIGNMENT;
	
	public String SHEET_RECORD_TYPE_ASSIGNMENT;
	
	public boolean READ_LIST_VIEW;
	public String SHEET_LISTVIEW;
	
	
	public String SHEET_PS;
	public String SHEET_PROFILE;
	
	public boolean READ_SHARING_RULES;
	public String SHEET_SHARING_RULES;
	
	public ArrayList<String> SHEETS_TO_IGNORE;
	public ArrayList<String> KEEP_PROFILES_PERMISSIONS;
	public ArrayList<String> PROFILES_TO_IGNORE;
	
	public String filepath;
	public String package_folder;
	public boolean full_debug;
	public boolean selected;
	public static Config c;
	
}
