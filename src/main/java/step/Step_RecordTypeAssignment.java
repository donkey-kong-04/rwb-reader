package step;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.w3c.dom.Node;

import file.XML_File;
import file.XML_PermissionSet;
import file.XML_Profile;
import utils.U;
import workbook.PRWorkbook;

public class Step_RecordTypeAssignment extends Step {
	String object;
	ArrayList<String> recordTypes;
	
	@Override
	public void getSheetIDS(PRWorkbook w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getSteps() {
		return new String[] {"START", "READ_OBJECT_AND_RECORD_TYPE", "READ"};
	}

	@Override
	public Type runStep(PRWorkbook w) {
		//U.writeMsg("RUN STEP " + w.currentSheet.getSheetName(), Color.GREEN, false);
		this.doesStepExist(w);
		
		boolean isRowHidden = w.currentRow.getZeroHeight();
		if(isRowHidden) {
			return Type.STAY_IN_SAME_STEP;
		}
		
		String stp = STEPS[STEP];
		
		return runSTART(w);
	}

	private Type runREAD(PRWorkbook w) {
		//U.writeMsg("RUN READ " + w.currentSheet.getSheetName(), Color.GREEN, false);
		
		String type = U.getCell(w, 0).trim();
		String apiNameP_PS = U.getCell(w, 1).trim();
		
		
		if(!U.isBlank(apiNameP_PS) && !U.isBlank(type)) {
			String fullFileName = (type.equalsIgnoreCase("P") ? apiNameP_PS + ".profile" : apiNameP_PS + ".permissionset");
			
			
			XML_File f = null;
			if(type.equalsIgnoreCase("P")) {
				f = (XML_Profile) w.getCorrectCorrectFile(XML_Profile.class, fullFileName);
				w.fpackage.p_profiles.add(apiNameP_PS);
			} else if(type.equalsIgnoreCase("PS")) {
				f = (XML_PermissionSet) w.getCorrectCorrectFile(XML_PermissionSet.class, fullFileName);
				w.fpackage.p_pm.add(apiNameP_PS);
			} else {
				U.writeMsg("'P' or 'PS' should have been found there in record type assignment" + w.currentSheet.getSheetName(), Color.RED, true);
			}
			
			for(int i=0; i<recordTypes.size(); i++) {
				
				String recordTypeApiName = recordTypes.get(i);
				String recordTypeVisibility = U.getCell(w, i+2);
				
				
				//System.out.println(f.rtvPerms);
				Node rtv = f.file.createElement("recordTypeVisibilities");
					Node rtv_rt = f.file.createElement("recordType");
					Node rtv_visible = f.file.createElement("visible");
					Node rtv_default = f.file.createElement("default");
					
					
					
				
				w.recordTypeLinks.add(recordTypeApiName);
				
				String RT_ID = object + "." + recordTypeApiName;
				
				boolean isRTVisible = recordTypeVisibility.toLowerCase().trim().contains("x");
				boolean isRTDefault = recordTypeVisibility.trim().toLowerCase().contains("default");
				
				if(type.equalsIgnoreCase("P")) {
					rtv.appendChild(rtv_default).appendChild(f.file.createTextNode(isRTDefault ? "true" : "false"));
				}
				
				rtv.appendChild(rtv_rt).appendChild(f.file.createTextNode(RT_ID.trim()));
				rtv.appendChild(rtv_visible).appendChild(f.file.createTextNode(isRTVisible ? "true" : "false"));
				//System.out.println(rtv);
				f.rtvPerms.add(rtv);
					
				
			}
			
		}
			
			
		
		
		
		return Type.STAY_IN_SAME_STEP;
	}
	
	private Type runReadObjectAndRT(PRWorkbook w) {
		//U.writeMsg("RUN RT " + w.currentSheet.getSheetName(), Color.GREEN, false);
		int index_cell = -1;
		Iterator<Cell> cit = w.currentRow.cellIterator();
		
		
		
		recordTypes = new ArrayList<String>();
		while(cit.hasNext()) {
			cit.next(); 
			index_cell++;
			w.currentCell = index_cell;
			
			
			
			
			if(index_cell == 0) {
				/* Useless we already check it in previous step
				String isObjectLine = U.getCell(w, 0);
				
				if(!(isObjectLine.equalsIgnoreCase("p") || isObjectLine.equalsIgnoreCase("ps"))) {
					U.writeMsg("'P' or 'PS' should have been found there in record type assignment" + w.currentSheet.getSheetName(), Color.RED, true);
					
				*/
			} else if(index_cell == 1) {
				object = U.getCell(w, index_cell);
				if(U.isBlank(object)) {
					U.writeMsg("Object API Name cannot be empty in column B when you declare new object using 'P'/'PS' in column A" + w.currentSheet.getSheetName(), Color.RED, true);
					
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

	private Type runSTART(PRWorkbook w) {
		//U.writeMsg("START " + w.currentSheet.getSheetName(), Color.GREEN, false);
		String beginning = U.getCell(w, 0);
		
		if(beginning.equalsIgnoreCase("X")) {
			
			this.STEP = 1;
			this.object = null;
			//U.writeMsg("FOUND p/ps " + w.currentSheet.getSheetName(), Color.GREEN, false);
			return this.runReadObjectAndRT(w);
		} else {
			return this.runREAD(w);
		}
	}
	

	@Override
	public boolean isCorrectSheet(PRWorkbook w, String sheetName) {
		return sheetName.equalsIgnoreCase(w.c.SHEET_RECORD_TYPE_ASSIGNMENT);
	}

	@Override
	public void debugStepNotPassed(PRWorkbook w) {
		// TODO Auto-generated method stub
		
	}


}
