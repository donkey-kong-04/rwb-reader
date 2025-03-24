package step;

import java.awt.Color;
import java.util.ArrayList;

import org.w3c.dom.Node;

import file.XML_PermissionSet;
import utils.U;
import workbook.PRWorkbook;

public class Step_PermissionSetObject extends Step {
	String object;
	
	@Override
	public String[] getSteps() {
		return new String[] {"INDEX", "POSITION", "READ_OBJ_PERM", "POSITION_AFTER_FIELDS", "READ"};
	}

	@Override
	public Type runStep(PRWorkbook w) {
		this.doesStepExist(w);
		
		String stp = STEPS[STEP];
		
		if(stp.equals("INDEX")) {
			return runINDEX(w);
		} else if(stp.equals("POSITION")) {
			return runPOSITION(w);
		} else if(stp.equals("READ_OBJ_PERM")) {
			return runOBJ_PERM(w);
		} else if(stp.equals("POSITION_AFTER_FIELDS")) {
			return runWAIT_FIELDS(w);
		} else if(stp.equals("READ")) {
			return runREAD(w);
		}
		
		return Type.STOP;
	}

	private Type runOBJ_PERM(PRWorkbook w) {
		//U.debug(w, "ENTER READ OBJ PERM", false);
		String t0 = U.getCell(w, 0);
		
		if(t0.equalsIgnoreCase("Object Permissions")) {
			Integer numberOfColumnHidden = 0;
			for(int i=index_start; i<=index_end; i++) {
				Integer tabIndex = i - index_start - numberOfColumnHidden;
				boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
				if(isColumnHidden == false) {
					String fullFileName = IDS.get(tabIndex);
					String v = U.getCell(w, i);
					
					if(!U.isBlank(v)) {
						//U.debug(w, currentId, false);
						XML_PermissionSet f = (XML_PermissionSet) w.getCorrectCorrectFile(XML_PermissionSet.class, fullFileName);
						
						Node objPerm = f.file.createElement("objectPermissions");
						
				        
				        Node objNode = f.file.createElement("object"),
				        		allowCreate = f.file.createElement("allowCreate"), 
				        		allowDelete = f.file.createElement("allowDelete"), 
				        		allowEdit = f.file.createElement("allowEdit"), 
				        		allowRead = f.file.createElement("allowRead"), 
				        		modifyAll = f.file.createElement("modifyAllRecords"), 
				        		readAll = f.file.createElement("viewAllRecords");
				        
				        objPerm.appendChild(objNode).appendChild(f.file.createTextNode(object));
				        objPerm.appendChild(allowCreate).appendChild(f.file.createTextNode(v.contains("C") ? "true" : "false"));
				        objPerm.appendChild(allowDelete).appendChild(f.file.createTextNode(v.contains("D") ? "true" : "false"));
				        objPerm.appendChild(allowEdit).appendChild(f.file.createTextNode(v.contains("E") ? "true" : "false"));
				        objPerm.appendChild(allowRead).appendChild(f.file.createTextNode(v.contains("R") ? "true" : "false"));
				        objPerm.appendChild(modifyAll).appendChild(f.file.createTextNode(v.contains("M") ? "true" : "false"));
				        objPerm.appendChild(readAll).appendChild(f.file.createTextNode(v.contains("V") ? "true" : "false"));
				        
				        f.objectPerms.add(objPerm);
				        //System.out.println(f.objectPerms);
					}
				} else {
					numberOfColumnHidden ++;
				}
			}
			return Type.NEXT_STEP;
		}
		return Type.STAY_IN_SAME_STEP;
	}

	private Type runWAIT_FIELDS(PRWorkbook w) {
		//U.debug(w, "ENTER READ WAIT FIELD", false);
		String t0 = U.getCell(w, 0);
		String t1 = U.getCell(w, 1);
		
		if(t0.equalsIgnoreCase("Field") && t1.equalsIgnoreCase("Field API Name")) {
			return Type.NEXT_STEP;
		} else {
			return Type.STAY_IN_SAME_STEP;
		}
		
	}

	private Type runINDEX(PRWorkbook w) {
		
		this.getIndexes(w, "Permission Sets");
		
		
		boolean success = (this.index_start != -1 && this.index_end != -1);
		
		return success ? Type.NEXT_STEP : Type.STOP;
	}

	private Type runPOSITION(PRWorkbook w) {
		object = U.getCell(w, 0);
		
		if(U.isBlank(object)) {
			U.writeMsg("Object API Name should be found in cell A2 " + w.currentSheet.getSheetName(), Color.RED, true);
			
		}
		
		this.getSheetIDS(w);
		
		
		return Type.NEXT_STEP;
	}

	private Type runREAD(PRWorkbook w) {
		
		String field = U.getCell(w, 0);
		String strField = U.getCell(w, 1);
		String strType = U.getCell(w, 2);
		
		//U.debug(w, "ENTER READ " + field, false);
		if(field.equalsIgnoreCase("Related Data (Objects & Columns)")) {
			//U.debug(w, "should stop", false);
			return Type.STOP;
		}
		
		
		
		
		boolean isHidden = w.currentRow.getZeroHeight();
		boolean isIgnore = this.ignoreField(strField, strType);
		
		if(!isIgnore && !isHidden) {
			Integer numberOfColumnHidden = 0;
			for(int i=index_start; i<=index_end; i++) {
				int tabIndex = i - index_start - numberOfColumnHidden;
				
				boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
				if(isColumnHidden == false) {	
					String FullFileName = IDS.get(tabIndex);
					
					String v = U.getCell(w, i);
					if(!U.isBlank(v)) {
						XML_PermissionSet f = (XML_PermissionSet) w.getCorrectCorrectFile(XML_PermissionSet.class, FullFileName);
						
						
						Node fieldPerm = f.file.createElement("fieldPermissions");
						
						Node fieldNode = f.file.createElement("field"),
				        		readP = f.file.createElement("readable"), 
				        		editP = f.file.createElement("editable");
						fieldPerm.appendChild(fieldNode).appendChild(f.file.createTextNode(object.trim() + "." + strField.trim()));
						fieldPerm.appendChild(readP).appendChild(f.file.createTextNode(v.contains("R") ? "true" : "false"));
						fieldPerm.appendChild(editP).appendChild(f.file.createTextNode(v.contains("E") ? "true" : "false"));

						f.fieldPerms.add(fieldPerm);
					}
				} else {
					numberOfColumnHidden ++;
				}
			}
		}
		return Type.STAY_IN_SAME_STEP;
	}

	

	@Override
	public void debugStepNotPassed(PRWorkbook w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getSheetIDS(PRWorkbook w) {
		IDS = new ArrayList<String>();
		headers = new ArrayList<String>();
		
		this.reviewEndingIndex(w);
		
		for(int i=index_start; i<=index_end; i++) {
			boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
			
			String apiName = U.getCell(w, i);
			if(isColumnHidden == false) {
				
				
				IDS.add(apiName + ".permissionset");
				headers.add(apiName);
				w.fpackage.p_pm.add(apiName);
			} else {
				U.writeMsg("HIDDEN PERMISSION SET " + apiName + " "  + w.currentSheet.getSheetName(), Color.ORANGE, false);
				
			}
		}
	}

	@Override
	public boolean isCorrectSheet(PRWorkbook w, String sheetName) {
		return !U.isSpecialSheet(w, sheetName);
	}

	

}
