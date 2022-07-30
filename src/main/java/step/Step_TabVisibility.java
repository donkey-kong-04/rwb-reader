package step;


import java.awt.Color;

import org.w3c.dom.Node;

import file.XML_Application;
import file.XML_PermissionSet;
import file.XML_Profile;
import utils.PRUtil;
import workbook.PRWorkbook;

public class Step_TabVisibility extends Step {
	
	public String appName;
	public String pmName;
	
	@Override
	public String[] getSteps() {
		return new String[] {"POSITION", "READ"};
	}

	@Override
	public Type runStep(PRWorkbook w) {
		this.doesStepExist(w);
		
		String theStep = STEPS[STEP];
		
		if(theStep.equals("POSITION")) {
			return runPOSITION(w);
		} else if(theStep.equals("READ")) {
			return runREAD(w);
		}
		
		return Type.STOP;
	}

	private Type runPOSITION(PRWorkbook w) {
		String content = PRUtil.getCell(w, 0);
		
		if(content.equalsIgnoreCase("Object Name")) {
			pmName = PRUtil.getCell(w, 2);
			String pmIDName = pmName.replaceAll(" ", "_") + ".permissionset";
			appName = PRUtil.getCell(w, 3);
			String appIDName = appName.replaceAll(" ", "_") + ".app";
			
			XML_PermissionSet pmF = (XML_PermissionSet) w.getCorrectCorrectFile(XML_PermissionSet.class, pmIDName);
			XML_Application appF = (XML_Application) w.getCorrectCorrectFile(XML_Application.class, appIDName);
			
			appF.appName = appName;
			pmF.label = pmName.replaceAll("_", " ");
			
			IDS.add(pmIDName);
			IDS.add(appIDName);
			
			w.fpackage.p_application.add(appName.replaceAll(" ", "_"));
			w.fpackage.p_pm.add(pmName.replaceAll(" ", "_"));
			
			if(PRUtil.isBlank(appIDName)) {
				PRUtil.writeMsg("MISSING INFORMATION : Application Name in Tab Visibility cannot be null in column D" + w.currentSheet.getSheetName(), Color.RED, true);
				
			}
			if(PRUtil.isBlank(pmIDName)) {
				PRUtil.writeMsg("MISSING INFORMATION : Permission Name in Tab Visibility cannot be null in column C" + w.currentSheet.getSheetName(), Color.RED, true);
				
			}
			
			for(int i=0; i<w.Allfiles.size(); i++) {
				if(w.Allfiles.get(i).getClass() == XML_Profile.class) {
					XML_Profile f = (XML_Profile) w.Allfiles.get(i);

					Node applicationVisibilities = f.file.createElement("applicationVisibilities");
					
					Node application = f.file.createElement("application"),
							ndefault = f.file.createElement("default"),
							visible = f.file.createElement("visible");
					
					applicationVisibilities.appendChild(application).appendChild(f.file.createTextNode(appName.trim().replaceAll(" ", "_")));
					applicationVisibilities.appendChild(ndefault).appendChild(f.file.createTextNode("true"));
					applicationVisibilities.appendChild(visible).appendChild(f.file.createTextNode("true"));
					
					f.appPerms.add(applicationVisibilities);
				}
				
					
					
			}
			
			return Type.NEXT_STEP;
		}
		
		return Type.STAY_IN_SAME_STEP;
	}

	private Type runREAD(PRWorkbook w) {
		String apiname = PRUtil.getCell(w, 1);
		String value = PRUtil.getCell(w, 2);
		
		if(!PRUtil.isBlank(apiname) && !PRUtil.isBlank(value)) {
			XML_PermissionSet pmF = (XML_PermissionSet) w.getCorrectCorrectFile(XML_PermissionSet.class, IDS.get(0));
			Node tabSettings = pmF.file.createElement("tabSettings");
			
			Node tab = pmF.file.createElement("tab"),
					visibility = pmF.file.createElement("visibility");
			
			tabSettings.appendChild(tab).appendChild(pmF.file.createTextNode(apiname.trim()));
			tabSettings.appendChild(visibility).appendChild(pmF.file.createTextNode(value.trim()));
			
			pmF.tabSettings.add(tabSettings);
			
			if(value.trim().equalsIgnoreCase("visible")) {
				//System.out.println("==========================================================================================" + value);
				XML_Application appF = (XML_Application) w.getCorrectCorrectFile(XML_Application.class, IDS.get(1));
				
				Node tabs = appF.file.createElement("tabs");
				tabs.appendChild(appF.file.createTextNode(apiname.trim()));
				appF.tabs.add(tabs);
			}
			
		}
		
		return Type.STAY_IN_SAME_STEP;
	}

	@Override
	public boolean isCorrectSheet(PRWorkbook w, String sheetName) {
		return sheetName.trim().equalsIgnoreCase(w.c.SHEET_TAB_VISIBILITY);
	}

	@Override
	public void debugStepNotPassed(PRWorkbook w) {
		String theStep = STEPS[STEP];
		
		if(theStep.equals("POSITION")) {
			PRUtil.writeMsg("MARKUP MISSING : 'Base Object Settings' markup not found" + w.currentSheet.getSheetName(), Color.RED, true);
			
		}
	}

	@Override
	public void getSheetIDS(PRWorkbook w) {
		
		
	}


}
