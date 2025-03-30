package app;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import utils.U;




public class Config {
	
	public static Config selected;
	
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
	public static Config c;
	
	
	
	public Config() {
        try {
        	Path path = Paths.get("./reader.json");
    		if (!Files.exists(path) || !Files.isRegularFile(path)) {
    			U.fatal_message("Setup file 'reader.json' not found.");
    		}
    		
            // Read the whole file as a String
            String content = new String(Files.readAllBytes(Paths.get("reader.json")));
            
            if(content.trim().startsWith("[")) {
            	U.fatal_message("Latest version accept only a single config in 'reader.json', please remove the [] from your file.");
            }
            // Parse JSON
            JSONObject obj = new JSONObject(content);
            
            ArrayList<String> errors = new ArrayList<String>();
            // Basic structure check
            if (!obj.has("name")) {
                errors.add("Expecting 'name' in your JSON definition as a string");
            }
            if (!obj.has("file")) {
                errors.add("Expecting 'file' in your JSON definition as a string");
            }
            if (!obj.has("folder")) {
                errors.add("Expecting 'folder' in your JSON definition as a string");
            }
            if (!obj.has("full_debug")) {
                errors.add("Expecting 'full_debug' in your JSON definition as a boolean");
            }
            if (!obj.has("SHEET_LAYOUT_ASSIGNMENT")) {
                errors.add("Expecting 'SHEET_LAYOUT_ASSIGNMENT' in your JSON definition as a string");
            }
            if (!obj.has("SHEET_RECORD_TYPE_ASSIGNMENT")) {
                errors.add("Expecting 'SHEET_RECORD_TYPE_ASSIGNMENT' in your JSON definition as a string");
            }
            if (!obj.has("READ_LIST_VIEW")) {
                errors.add("Expecting 'READ_LIST_VIEW' in your JSON definition as a boolean");
            }
            if (!obj.has("SHEET_LISTVIEW")) {
                errors.add("Expecting 'SHEET_LISTVIEW' in your JSON definition as a string");
            }
            if (!obj.has("READ_SHARING_RULES")) {
                errors.add("Expecting 'READ_SHARING_RULES' in your JSON definition as a boolean");
            }
            if (!obj.has("SHEET_SHARING_RULES")) {
                errors.add("Expecting 'SHEET_SHARING_RULES' in your JSON definition as a string");
            }
            if (!obj.has("SHEET_PROFILE")) {
                errors.add("Expecting 'SHEET_PROFILE' in your JSON definition as a string");
            }
            if (!obj.has("SHEET_PS")) {
                errors.add("Expecting 'SHEET_PS' in your JSON definition as a string");
            }
            if (!obj.has("SHEETS_TO_IGNORE")) {
                errors.add("Expecting 'SHEETS_TO_IGNORE' in your JSON definition as a string");
            }
            if (!obj.has("PROFILES_TO_IGNORE")) {
                errors.add("Expecting 'PROFILES_TO_IGNORE' in your JSON definition as a string");
            }
            if (!obj.has("KEEP_PROFILES_PERMISSIONS")) {
                errors.add("Expecting 'KEEP_PROFILES_PERMISSIONS' in your JSON definition as a string");
            }
            
            if (errors.size() > 0) {
            	U.fatal_message("Missing key-value pair inside your 'reader.json':\n" + String.join("\n", errors));
            } else {
            	Name = (String) obj.get("name");
        		
        		SHEET_LAYOUT_ASSIGNMENT = (String) obj.get("SHEET_LAYOUT_ASSIGNMENT");
        		
        		SHEET_RECORD_TYPE_ASSIGNMENT = (String) obj.get("SHEET_RECORD_TYPE_ASSIGNMENT");
        		
        		SHEET_PROFILE = (String) obj.get("SHEET_PROFILE");
        		SHEET_PS = (String) obj.get("SHEET_PS");
        		
        		
        		READ_LIST_VIEW = getBool(obj, "READ_LIST_VIEW");
        		SHEET_LISTVIEW = (String) obj.get("SHEET_LISTVIEW");
        		
        		READ_SHARING_RULES = getBool(obj, "READ_SHARING_RULES");
        		SHEET_SHARING_RULES = (String) obj.get("SHEET_SHARING_RULES");
        		
        		
        		String[] toIgnores = ((String) obj.get("SHEETS_TO_IGNORE")).split(",");
        	
        		SHEETS_TO_IGNORE = new ArrayList<String>();
        	
        		for(String i : toIgnores) {
        			SHEETS_TO_IGNORE.add(i);
        		}
        		
        		String[] keepPerms = ((String) obj.get("KEEP_PROFILES_PERMISSIONS")).split(",");
        		
        		KEEP_PROFILES_PERMISSIONS = new ArrayList<String>();
        		for(String i : keepPerms) {
        			KEEP_PROFILES_PERMISSIONS.add(i);
        		}
        		
        		String[] profilesRemove = ((String) obj.get("PROFILES_TO_IGNORE")).split(",");
        		
        		PROFILES_TO_IGNORE = new ArrayList<String>();
        		for(String i : profilesRemove) {
        			PROFILES_TO_IGNORE.add(i.trim().toLowerCase());
        		}
        		filepath = (String) obj.get("file");
        		package_folder = (String) obj.get("folder");
        		full_debug = (Boolean) obj.get("full_debug");
        		
        		
            	path = Paths.get(filepath);
    			if (!Files.exists(path) || !Files.isRegularFile(path)) {
    				U.fatal_message("The exporer path of your workbook defined within the key-value pair 'filepath' of the setup file 'reader.json' could not be found.\nDon't forget to add doubled antislashes inside the path '\\\\' if you are using anti-slashes.");
    	        }
    			path = Paths.get(package_folder);
    			if (!Files.exists(path) || !Files.isDirectory(path)) {
    				U.fatal_message("The exporer path of your destination folder defined within the key-value pair 'folder' of the setup file 'reader.json' could not be found.\nDon't forget to add doubled antislashes inside the path '\\\\' if you are using antislashes.");
    	        } else {
    	        	if(!package_folder.endsWith("\\") && !package_folder.endsWith("/")) {
    	        		U.fatal_message("Make sure destination folder defined within the key-value pair 'folder' of the setup file 'reader.json' finishes by 2 antislashes: \\\\ (or 1 normal slash if used for path: /)");
    	        	}
    	        }
            }
            

        } catch (Exception e) {
            U.fatal_message("Unexpected issue occured while reading 'reader.json':" + e.getMessage());
        }
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
