package workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.xml.transform.TransformerException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

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
import utils.PRUtil;


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
			e1.printStackTrace();
		}
	}
	
	public void read() {
		for(int i=0; i<workbook.getNumberOfSheets(); i++) {
			Sheet sheet = workbook.getSheetAt(i);
			
			if(PRUtil.exit == true) {
				return;
			}
			//ignore hidden sheets
			this.currentSheet = sheet;
			
			if(this.workbook.isSheetHidden(i) == false) {
				
				steps = new ArrayList<Step>();
				
				steps.add(new Step_Profile());//NEED TO BE FIRST !!!
				steps.add(new Step_ApexComponents());
				steps.add(new Step_TabVisibility());
				steps.add(new Step_Layout());
				steps.add(new Step_RecordType());
				steps.add(new Step_PM());
				steps.add(new Step_LayoutAssignment());
				steps.add(new Step_RecordTypeAssignment());
				//steps.add(new Step_ListView());
				readRows(sheet);
				
				for(int j=0; j<steps.size(); j++) {
					Step step = steps.get(j);
					if(step.isCorrectSheet(this, sheet.getSheetName()) && !step.isLastStep()) {
						step.debugStepNotPassed(this);
					}
				}
			} else {
				PRUtil.info(null, "HIDDEN SHEET", sheet.getSheetName());
			}
		}
	}
	
	
	private void readRows(Sheet s) {
		Iterator<Row> rit = s.iterator();
		
		
		row_index = 0;
		while(rit.hasNext()) {
			Row r = rit.next();
			
			if(PRUtil.exit == true) {
				return;
			}
			
			this.currentRow = r;
			readRow(s, r);
			row_index ++;
		}
	}
	
	private void readRow(Sheet s, Row r) {
		
		for(int i=0; i<steps.size(); i++) {
			
			if(PRUtil.exit == true) {
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
	public void writeFiles() throws TransformerException {
		List<XML_File> profiles = new ArrayList<XML_File>();
		for(int i=0; i<Allfiles.size(); i++) {
			XML_File f = Allfiles.get(i);
			
			if(doNotDeploy(f.filename) == false) {
			
				if(f instanceof XML_Profile) {
					profiles.add(f);
				}
				f.write(f.filename, this.c.package_folder + f.location);
				
			}
		}
		
		String csv = XML_Profile.buildCopadoFile(profiles);
		XML_File.writeCSV(this.c.package_folder + "unpackaged\\copado_JSON_File.json", csv);
		
		
	}
	
	private boolean doNotDeploy(String name) {
		boolean doNotDeploy = false;
		
		if(c.PROFILES_TO_IGNORE.contains(name.replace(".profile", ""))) {
			doNotDeploy = true;
			PRUtil.info(null, "REMOVE FROM PACKAGE", name);
		}
		return doNotDeploy;
	}
	
	public XML_File getCorrectCorrectFile(Class<?> type, String filename) {
		
		for(int i=0; i<Allfiles.size(); i++) {
			
			XML_File f = Allfiles.get(i);
			
			if(f.filename.equalsIgnoreCase(filename)) {
				if(!f.filename.equals(filename)) {
					PRUtil.info(null, "WARNING", "Filename - " + filename + "' & '" + f.filename + "' do not match on a sensitive level. It can create deployment issue.");
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
			PRUtil.fatal(this, "FILE TYPE unfound");
		}
		
		PRUtil.info(this, "Adding in all files", filename);
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
				PRUtil.info(null, "NOT FOUND", "Record Type definition not found : " + s);
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
				PRUtil.info(null, "NOT FOUND", "Layout definition not found : " + s);
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
