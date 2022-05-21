package step;

import java.util.ArrayList;

import org.w3c.dom.Node;

import file.XML_PermissionSet;
import utils.PRUtil;
import workbook.PRWorkbook;

public class Step_PM extends Step {
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
		//PRUtil.debug(w, "ENTER READ OBJ PERM", false);
		String t0 = PRUtil.getCell(w, 0);
		
		if(t0.equalsIgnoreCase("Object Permissions")) {
			Integer numberOfColumnHidden = 0;
			for(int i=index_start; i<=index_end; i++) {
				Integer tabIndex = i - index_start - numberOfColumnHidden;
				boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
				if(isColumnHidden == false) {
					String currentId = IDS.get(tabIndex);
					String v = PRUtil.getCell(w, i);
					
					if(!PRUtil.isBlank(v)) {
						//PRUtil.debug(w, currentId, false);
						XML_PermissionSet f = (XML_PermissionSet) w.getCorrectCorrectFile(XML_PermissionSet.class, currentId);
						f.label = header_labels.get(tabIndex);
						
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
		//PRUtil.debug(w, "ENTER READ WAIT FIELD", false);
		String t0 = PRUtil.getCell(w, 0);
		String t1 = PRUtil.getCell(w, 1);
		
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
		object = PRUtil.getCell(w, 0);
		
		if(PRUtil.isBlank(object)) {
			PRUtil.fatal(w, "Object API Name should be found in cell A2");
		}
		
		this.getSheetIDS(w);
		
		
		return Type.NEXT_STEP;
	}

	private Type runREAD(PRWorkbook w) {
		
		String field = PRUtil.getCell(w, 0);
		String strField = PRUtil.getCell(w, 1);
		String strType = PRUtil.getCell(w, 2);
		
		//PRUtil.debug(w, "ENTER READ " + field, false);
		if(field.equalsIgnoreCase("Related Data (Objects & Columns)")) {
			//PRUtil.debug(w, "should stop", false);
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
					String currentId = IDS.get(tabIndex);
					
					String v = PRUtil.getCell(w, i);
					if(!PRUtil.isBlank(v)) {
						XML_PermissionSet f = (XML_PermissionSet) w.getCorrectCorrectFile(XML_PermissionSet.class, currentId);
						f.label = header_labels.get(tabIndex);
						
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
			String header = PRUtil.getCell(w, i);
			if(isColumnHidden == false) {
				
				String[] IDS_info = header.split("\n");
				String api_name = null;
				String label = null;
				if(IDS_info.length == 1) {
					api_name = header;
					label = header.replaceAll("_", " ");
				} else {
					api_name = IDS_info[0];
					label = IDS_info[1];
				}
				
				IDS.add(api_name + ".permissionset");
				headers.add(api_name);
				header_labels.add(label);
				w.fpackage.p_pm.add(header);
			} else {
				PRUtil.info(w, "HIDDEN PERMISSION SET", header + " " + w.currentSheet.getSheetName());
			}
		}
	}

	

}
