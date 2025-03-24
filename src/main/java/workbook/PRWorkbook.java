package workbook;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.TransformerException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.w3c.dom.Node;

import app.Config;
import app.ConfigManager;
import file.XML_Application;
import file.XML_File;
import file.XML_Layout;
import file.XML_Object;
import file.XML_Package;
import file.XML_PermissionSet;
import file.XML_Profile;
import file.XML_SharingRules;
import step.*;
import utils.U;
import utils.ZipDirectory;


public class PRWorkbook {
	
	public Workbook workbook;
	
	public Sheet currentSheet;
	public Row currentRow;
	public int row_index;
	public Step currentStep;
	public int currentCell;
	
	public ArrayList<XML_File> Allfiles;
	public ArrayList<Step> steps;
	
	public XML_Package fpackage;
	
	
	public ArrayList<String> layouts = new ArrayList<String>();
	public ArrayList<String> layoutLinks = new ArrayList<String>();
	public ArrayList<String> recordTypes = new ArrayList<String>();
	public ArrayList<String> recordTypeLinks = new ArrayList<String>();
	
	public Config c;
	public PRWorkbook() {
		
		//Loading properties file
		//c = Config.loadConfig();
		c = ConfigManager.selected;
		
		
		Allfiles = new ArrayList<XML_File>();
		fpackage = new XML_Package("package.xml");
		Allfiles.add(fpackage);
		
		try {
			workbook = XSSFWorkbookFactory.createWorkbook(new File(c.filepath), true);
			
		} catch (Exception e1) {
			
			
		}
	}
	
	public void read() {
		for(int i=0; i<workbook.getNumberOfSheets(); i++) {
			Sheet sheet = workbook.getSheetAt(i);
			System.out.println(sheet.getSheetName());
			if(U.exit == true) {
				return;
			}
			//ignore hidden sheets
			this.currentSheet = sheet;
			
			if(this.workbook.isSheetHidden(i) == false) {
				
				steps = new ArrayList<Step>();
				
				steps.add(new Step_ProfileObject());//NEED TO BE FIRST !!! - not sure why
				steps.add(new Step_Layout());
				steps.add(new Step_RecordType());
				steps.add(new Step_PermissionSetObject());
				steps.add(new Step_Profile());
				steps.add(new Step_PermissionSet());
				steps.add(new Step_LayoutAssignment());
				steps.add(new Step_RecordTypeAssignment());
				
				if(c.READ_LIST_VIEW) {
					steps.add(new Step_ListView());
				}
				
				if(c.READ_SHARING_RULES) {
					steps.add(new Step_SharingRule());
				}
				
				
				
				
				readRows(sheet);
				
				for(int j=0; j<steps.size(); j++) {
					Step step = steps.get(j);
					if(step.isCorrectSheet(this, sheet.getSheetName()) && !step.isLastStep()) {
						step.debugStepNotPassed(this);
					}
				}
			} else {
				U.writeMsg("HIDDEN SHEET - " + sheet.getSheetName(), Color.ORANGE, false);
				
			}
		}
	}
	
	
	private void readRows(Sheet s) {
		Iterator<Row> rit = s.iterator();
		
		
		row_index = 0;
		while(rit.hasNext()) {
			Row r = rit.next();
			
			if(U.exit == true) {
				return;
			}
			
			this.currentRow = r;
			readRow(s, r);
			row_index ++;
		}
	}
	
	private void readRow(Sheet s, Row r) {
		
		for(int i=0; i<steps.size(); i++) {
			
			if(U.exit == true) {
				return;
			}
			currentStep = steps.get(i);
			
			if(currentStep.status == Step.Type.STAY_IN_SAME_STEP || currentStep.status == Step.Type.NEXT_STEP) {
				Step.Type t = currentStep.run(this);
				currentStep.status = t;
			}
		}
	}
	
	/*
	 * Write XML to files
	 */
	public void writeFiles() throws TransformerException, IOException {
		List<XML_File> profiles = new ArrayList<XML_File>();
		
		System.out.println("WriteFiles");
		for(int i=0; i<Allfiles.size(); i++) {
			XML_File f = Allfiles.get(i);
			System.out.println("Write: " + f.filename);
			for(Node n : f.rtvPerms) {
				System.out.println(n.getTextContent());
			}
			//The name of the profile file contains .profile, but we initialize the list to ignore without it
			if(U.doNotDeploy(f.filename.replace(".profile", ""), "profile file") == false) {
			
				if(f instanceof XML_Profile) {
					profiles.add(f);
				}
				//System.out.println(f.filename);
				//System.out.println(this.c.package_folder);
				//System.out.println(f.location);
				f.write(f.filename, this.c.package_folder + f.location);
				
			}
		}
		
		
		String csv = XML_Profile.buildCopadoFile(profiles);
		XML_File.writeCSV(this.c.package_folder + "/copado_JSON_File.json", csv);
		
		
	}
	
	
	
	public XML_File getCorrectCorrectFile(Class<?> type, String filename) {
		
		for(int i=0; i<Allfiles.size(); i++) {
			
			XML_File f = Allfiles.get(i);
			//System.out.println("Compare with existing file:" + f.filename);
			if(f.filename.equalsIgnoreCase(filename)) {
				//System.out.println("Found");
				if(!f.filename.equals(filename)) {
					U.writeMsg("WARNING - Filename - " + filename + "' & '" + f.filename + "' do not match on a sensitive level. It can create deployment issue.", Color.ORANGE, false);
				
				}
				
				return f;
			}
		}
		
		XML_File f = null;
		if(type == XML_Profile.class) {
			f = new XML_Profile(filename);
		} 
		else if(type == XML_Application.class) {
			f = new XML_Application(filename);
		} 
		else if(type == XML_SharingRules.class) {
			f = new XML_SharingRules(filename);
		}
		else if(type == XML_Layout.class) {
			f = new XML_Layout(filename);
		} 
		else if(type == XML_Object.class) {
			f = new XML_Object(filename);
		} 
		else if(type == XML_PermissionSet.class) {
			
			f = new XML_PermissionSet(filename);
			
		} 
		else {
			U.writeMsg("FILE TYPE unfound", Color.RED, true);
			
		}
		
		//U.writeMsg("Adding in all files " + filename, Color.BLACK, false);
		
		this.Allfiles.add(f);
		
		return f;
	}
	
	
	public void end() {
		try {
			workbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void postCheck() {
		//System.out.println("------------------------------------------------------------------------");
		TreeSet<String> sets = new TreeSet<String>();
		for(String s : recordTypeLinks) {
			boolean found = false;
			for(String s2 : recordTypes) {
				if(s.equals(s2)) {
					found = true;
				}
			}
			
			if(found == false && sets.add(s)) {
				U.writeMsg("Record Type definition not found : " + s, Color.ORANGE, false);
				
			}
		}
		sets = new TreeSet<String>();
		for(String s : layoutLinks) {
			boolean found = false;
			for(String s2 : layouts) {
				if(s.equals(s2)) {
					found = true;
				}
			}
			if(found == false && sets.add(s)) {
				U.writeMsg("Layout definition not found : " + s, Color.BLACK, false);
			}
		}
	}
	
	
	public boolean doNotRemoveProfilePermission(String filename) {
		
		for(String s : c.KEEP_PROFILES_PERMISSIONS) {
			if(s.equalsIgnoreCase(filename)) {
				return true;
			}
		}
		return false;
	}
}
