package step;



import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.w3c.dom.Node;

import file.XML_File;
import file.XML_Profile;
import utils.U;
import workbook.PRWorkbook;

public class Step_LayoutAssignment extends Step {
	String object;
	ArrayList<String> recordTypes;
	
	@Override
	public void getSheetIDS(PRWorkbook w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getSteps() {
		return new String[] {"POSITIONATE", "READ_OBJECT_AND_RECORD_TYPE", "READ"};
	}

	@Override
	public Type runStep(PRWorkbook w) {
		this.doesStepExist(w);
		
		boolean isRowHidden = w.currentRow.getZeroHeight();
		if(isRowHidden) {
			return Type.STAY_IN_SAME_STEP;
		}
		
		String stp = STEPS[STEP];
		
		if(stp.equals("POSITIONATE")) {
			return runPOSITIONATE(w);
		} else if(stp.equals("READ_OBJECT_AND_RECORD_TYPE")) {
			return runReadObjectAndRT(w);
		} else if(stp.equals("READ")) {
			return runREAD(w);
		}
		
		return Type.STOP;
	}

	private Type runREAD(PRWorkbook w) {
		
		String nextObject = U.getCell(w, 0);
		
		
		if(nextObject.equalsIgnoreCase("x")) {
			STEP -= 1;//go back previous step
			object = null;
			return runReadObjectAndRT(w);
		} else {
			String profileName = U.getCell(w, 1);
			
			
			if(!U.isBlank(profileName)) {
				String FullFileName = profileName + ".profile";
				XML_Profile f = (XML_Profile) w.getCorrectCorrectFile(XML_Profile.class, FullFileName);
				
				w.fpackage.p_profiles.add(profileName);
				
				for(int i=0; i<recordTypes.size(); i++) {
					String layout = U.getCell(w, i+2);
					
					
					String RT_ID = object + "." + recordTypes.get(i);
					if(!U.isBlank(layout)) {
						
						Node layoutPerm = f.file.createElement("layoutAssignments");
							Node nlayout = f.file.createElement("layout");
							Node nlayout_rt = f.file.createElement("recordType");
						
							w.layoutLinks.add(XML_File.parseForPackage(layout));
						layoutPerm.appendChild(nlayout).appendChild(f.file.createTextNode(object.trim() + "-" + XML_File.parseForPackage(layout).trim()));
						layoutPerm.appendChild(nlayout_rt).appendChild(f.file.createTextNode(RT_ID.trim()));
						
						f.layoutPerms.add(layoutPerm);
					}
					
				}
				
			}
			
			return Type.STAY_IN_SAME_STEP;
		}
		
		
		
	}

	private Type runReadObjectAndRT(PRWorkbook w) {
		int index_cell = -1;
		Iterator<Cell> cit = w.currentRow.cellIterator();
		
		
		recordTypes = new ArrayList<String>();
		while(cit.hasNext()) {
			cit.next(); 
			index_cell++;
			w.currentCell = index_cell;
			
			if(index_cell == 0) {
				String isChangeObject = U.getCell(w, 0);
				
				if(!isChangeObject.equalsIgnoreCase("x")) {
					U.writeMsg("ALGORITHM ERROR : 'X' should have been found there in layout assignment", Color.RED, true);
					
				}
			} else if(index_cell == 1) {
				object = U.getCell(w, index_cell);
				if(U.isBlank(object)) {
					U.writeMsg("MISSING INFORMATION : Object API Name cannot be empty in column B when you declare new object using 'X' in column A" + w.currentSheet.getSheetName(), Color.RED, true);
					
				}
			} else {
				String rt = U.getCell(w, index_cell);
				
				if(!U.isBlank(rt)) {
					recordTypes.add(rt);
				} else {
					return Type.NEXT_STEP;//Go next step
				}
			}
		}
		return Type.NEXT_STEP;//Never reach?
	}

	private Type runPOSITIONATE(PRWorkbook w) {
		String beginning = U.getCell(w, 0);
		
		if(beginning.equalsIgnoreCase("x")) {
			this.STEP += 1;
			return this.runReadObjectAndRT(w);
		}
		
		return Type.STAY_IN_SAME_STEP;
	}

	@Override
	public boolean isCorrectSheet(PRWorkbook w, String sheetName) {
		return sheetName.equalsIgnoreCase(w.c.SHEET_LAYOUT_ASSIGNMENT);
	}

	@Override
	public void debugStepNotPassed(PRWorkbook w) {
		// TODO Auto-generated method stub
		
	}

	

}
