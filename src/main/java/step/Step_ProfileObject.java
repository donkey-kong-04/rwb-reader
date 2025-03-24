package step;

import java.awt.Color;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.w3c.dom.Node;

import file.XML_Profile;
import utils.U;
import workbook.PRWorkbook;

public class Step_ProfileObject extends Step {
	
	private String object;
	
	
	
	@Override
	public String[] getSteps() {
		return new String[] {"PROFILE_INDEX", "OBJ_AND_PROFILE_POSITION", "OBJ_PERM", "READ_FIELDS", "END"};
	}
	
	
	@Override
	public Type runStep(PRWorkbook w) {
		
		this.doesStepExist(w);
		
		String theStep = this.STEPS[STEP];
		
		if(theStep.equals("PROFILE_INDEX")) {
			return runPROFILE_INDEX(w);
		}
		else if(theStep.equals("OBJ_AND_PROFILE_POSITION")) {
			runOBJ_API_NAME(w);
			runPROFILE_POSITION(w);
			return Type.NEXT_STEP;
		} else if(theStep.contentEquals("OBJ_PERM")) {
			return runOBJ_PERM(w);
		}
		else if(theStep.equals("READ_FIELDS")) {
			return runREAD_FIELDS(w);
		} else if(theStep.equals("END")) {
			return Type.STOP;
		}
		
		return Type.STOP;
	}
	
	public void debugStepNotPassed(PRWorkbook w) {
		if(this.STEPS[this.STEP].equals("OBJ_PERM")) {
			U.writeMsg("MARKUP MISSING 'Object permissions' markup should be found in object's sheet " + w.currentSheet.getSheetName(), Color.BLACK, false);
			
		}
	}
	
	private Type runPROFILE_INDEX(PRWorkbook w) 
	{
		
		this.getIndexes(w, "Profiles");

		
		boolean success = (this.index_start != -1 && this.index_end != -1);
		
		return success ? Type.NEXT_STEP : Type.STOP;
	}
	
	private void runOBJ_API_NAME(PRWorkbook w) 
	{
		this.object = U.getCell(w, 0);
				
		boolean success = !U.isBlank(this.object);
		
		if(!success) {
			U.writeMsg("Object API Name should be found in cell A2" + w.currentSheet.getSheetName(), Color.RED, true);
			
		}
		
	}
	
	public void getSheetIDS(PRWorkbook w) {
		IDS = new ArrayList<String>();
		headers = new ArrayList<String>();
		
		this.reviewEndingIndex(w);
		
		for(int i=index_start; i<=index_end; i++) {
			boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
			
			String header = U.getCell(w, i);
			if(isColumnHidden == false) {	
				String filename = header + ".profile";
				IDS.add(filename);
				headers.add(header);
			
			
				w.fpackage.p_profiles.add(header);
			} else {
				U.writeMsg("HIDDEN PROFILE " + header + " - "  + w.currentSheet.getSheetName(), Color.ORANGE, false);
				
			}
		}
	}
	
	
	private void runPROFILE_POSITION(PRWorkbook w) 
	{
		//w.Indexes(w.profiles, this.index_start, this.index_end, "Profile", "profile", ".\\unpackaged\\profiles\\", XML_File.TYPE_BASIC, null);
		this.getSheetIDS(w);
		
	}
	
	private Type runOBJ_PERM(PRWorkbook w) 
	{
		
		
		Cell c = w.currentRow.getCell(0);
		if(c != null && "Object Permissions".equalsIgnoreCase(c.getStringCellValue().trim())) {
			Integer numberOfHiddenColumn = 0;
			for(int i=index_start; i<=index_end; i++) {
				int tabIndex = i - index_start - numberOfHiddenColumn;
				
				boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
				if(isColumnHidden == false) {
					String FullFileName = IDS.get(tabIndex);
					String apiName = headers.get(tabIndex);
					
					XML_Profile f = (XML_Profile) w.getCorrectCorrectFile(XML_Profile.class, FullFileName);
					w.fpackage.p_profiles.add(apiName);
					
					//System.out.println(index_start + " " + index_end);
					String perm = w.currentRow.getCell(i).getStringCellValue();
					
					
					
					Node objectPerm = f.file.createElement("objectPermissions");
			        Node objNode = f.file.createElement("object"),
			        		allowCreate = f.file.createElement("allowCreate"), 
			        		allowDelete = f.file.createElement("allowDelete"), 
			        		allowEdit = f.file.createElement("allowEdit"), 
			        		allowRead = f.file.createElement("allowRead"), 
			        		modifyAll = f.file.createElement("modifyAllRecords"), 
			        		readAll = f.file.createElement("viewAllRecords");
			        
			        
			        objectPerm.appendChild(allowCreate).appendChild(f.file.createTextNode(perm.contains("C") ? "true" : "false"));
			        objectPerm.appendChild(allowDelete).appendChild(f.file.createTextNode(perm.contains("D") ? "true" : "false"));
			        objectPerm.appendChild(allowEdit).appendChild(f.file.createTextNode(perm.contains("E") ? "true" : "false"));
					objectPerm.appendChild(allowRead).appendChild(f.file.createTextNode(perm.contains("R") ? "true" : "false"));
					objectPerm.appendChild(modifyAll).appendChild(f.file.createTextNode(perm.contains("M") ? "true" : "false"));
					objectPerm.appendChild(objNode).appendChild(f.file.createTextNode(object.trim()));
					objectPerm.appendChild(readAll).appendChild(f.file.createTextNode(perm.contains("V") ? "true" : "false"));
		        
			        f.objectPerms.add(objectPerm);
				} else {
					numberOfHiddenColumn ++;
				}
			}
			return Type.NEXT_STEP;
		} else {
			return Type.STAY_IN_SAME_STEP;
		}
		
	}
	
	private Type runREAD_FIELDS(PRWorkbook w) {
		String label = U.getCell(w, 0);
		String strField = U.getCell(w, 1);
		String strType = U.getCell(w, 2);
		
		if(label.equalsIgnoreCase("Related Data (Objects & Columns)")) {
			return Type.NEXT_STEP;
		}
		
		if(!U.isBlank(strField) && !U.isBlank(strType) && !(strType.equalsIgnoreCase("Blank Space") || strType.equalsIgnoreCase("VF Page") || strType.equalsIgnoreCase("Section"))) {
			
			boolean isHidden = w.currentRow.getZeroHeight();
			boolean isIgnore = this.ignoreField(strField, strType);
			if(!isIgnore) {
				Integer numberOfHiddenColumn = 0;
				for(int i=index_start; i<=index_end; i++) {
					boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
					if(isColumnHidden == false) {
						String currentId = IDS.get(i - index_start - numberOfHiddenColumn);
						
						XML_Profile f = (XML_Profile) w.getCorrectCorrectFile(XML_Profile.class, currentId);
						
						String perm = U.getCell(w, i);
						
						Node fieldPerm = f.file.createElement("fieldPermissions");
						
						if(!strField.contains("__c")) {
							//System.out.println("--------------------------------------------------------------------------------------------------" + object + "." + strField);
						}
						
						//Specific rule for the eur system admin, we will not remove permission from this profile, 
						//so the permission of this field will not be present in his file
						if(isHidden && !w.doNotRemoveProfilePermission(f.filename)) {
							
							
							Node fieldNode = f.file.createElement("field"),
					        		readP = f.file.createElement("readable"), 
					        		editP = f.file.createElement("editable");
							fieldPerm.appendChild(editP).appendChild(f.file.createTextNode("false"));
							fieldPerm.appendChild(fieldNode).appendChild(f.file.createTextNode(object.trim() + "." + strField.trim()));
							fieldPerm.appendChild(readP).appendChild(f.file.createTextNode("false"));
							
							f.fieldPerms.add(fieldPerm);
						} else if(!isHidden){
							
							
							Node fieldNode = f.file.createElement("field"),
					        		readP = f.file.createElement("readable"), 
					        		editP = f.file.createElement("editable");
							fieldPerm.appendChild(editP).appendChild(f.file.createTextNode(perm.contains("E") ? "true" : "false"));
							fieldPerm.appendChild(fieldNode).appendChild(f.file.createTextNode(object + "." + strField.trim()));
							fieldPerm.appendChild(readP).appendChild(f.file.createTextNode(perm.contains("R") ? "true" : "false"));
							f.fieldPerms.add(fieldPerm);
						}
					} else {
						numberOfHiddenColumn ++;
					}
				}
			}
			
			if(isIgnore) {
				//U.debug(s, "Field remove from package : " + strField, false);
			} else if(isHidden) {
				//U.debug(s, "FIELD is hidden : " + strField, false);
			}
		}
		
		return Type.STAY_IN_SAME_STEP;
	}



	@Override
	public boolean isCorrectSheet(PRWorkbook w, String sheetName) {
		
		return !U.isSpecialSheet(w, sheetName);
	}

	
	
	

	
}
