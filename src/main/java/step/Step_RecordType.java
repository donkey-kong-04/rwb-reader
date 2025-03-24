package step;

import java.awt.Color;
import java.util.ArrayList;

import org.w3c.dom.Node;

import file.XML_Object;
import utils.U;
import workbook.PRWorkbook;


public class Step_RecordType extends Step {
	String object;
	
	ArrayList<Node> sheetRecordTypes = new ArrayList<Node>();
	ArrayList<String> recordTypeLabels = new ArrayList<String>();
 	
	@Override
	public String[] getSteps() {
		return new String[] {"INDEX", "OBJECT_AND_RT", "RT_IS_ACTIVE", "POSITIONATE_AFTER_FIELD", "READ"};
	}

	@Override
	public Type runStep(PRWorkbook w) {
		
		
		this.doesStepExist(w);
		
		String stp = STEPS[STEP];
		
		if(stp.equals("INDEX")) {
			return runINDEX(w);
		} else if(stp.equals("OBJECT_AND_RT")) {
			return runOBJECT_NAME_AND_RT(w);
		} else if(stp.equals("RT_IS_ACTIVE")) {
			return runRT_ACTIVE(w);
		} else if(stp.equals("POSITIONATE_AFTER_FIELD")) {
			return runPOSITIONATE_AFTER_FIELD(w);
		} else if(stp.equals("READ")) {
			return runREAD(w);
		}
		
		return Type.STOP;
	}

	private Type runRT_ACTIVE(PRWorkbook w) {
		
		String objectPermissionPosition = U.getCell(w, 0);
		
		if(objectPermissionPosition.equalsIgnoreCase("Object Permissions")) {
			Integer numberOfHiddenColumn = 0;
			for(int i=index_start; i<=index_end;i++) {
				int tabIndex = i - index_start - numberOfHiddenColumn;
				boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
				
				if(isColumnHidden == false) {
					String isActive = (U.getCell(w, i).equalsIgnoreCase("x") ? "true" : "false");
					
					if(isActive.equals("false")) {
						U.writeMsg( "INFO Record type is set as inactive : " + headers.get(tabIndex) + " " + w.currentSheet.getSheetName(), Color.BLACK, false);
						
					}
					
					
					XML_Object f = (XML_Object) w.getCorrectCorrectFile(XML_Object.class, IDS.get(tabIndex));
					
					Node recordTypes = f.file.createElement("recordTypes");
					Node fullname  = f.file.createElement("fullName");
					Node active  = f.file.createElement("active");
					Node label  = f.file.createElement("label");
					
					recordTypes.appendChild(fullname).appendChild(f.file.createTextNode(headers.get(tabIndex).trim()));
					recordTypes.appendChild(active).appendChild(f.file.createTextNode(isActive));
					recordTypes.appendChild(label).appendChild(f.file.createTextNode(recordTypeLabels.get(tabIndex).trim()));
					
					sheetRecordTypes.add(recordTypes);
					f.recordTypes.add(recordTypes);
				} else {
					numberOfHiddenColumn ++;
				}
			}
			return Type.NEXT_STEP;
		} else {
			return Type.STAY_IN_SAME_STEP;
		}
		
		
		
	}

	private Type runPOSITIONATE_AFTER_FIELD(PRWorkbook w) {
		String t0 = U.getCell(w, 0);
		String t1 = U.getCell(w, 1);
		
		if(t0.equalsIgnoreCase("Field") && t1.equalsIgnoreCase("Field API Name")) {
			return Type.NEXT_STEP;
		}
		return Type.STAY_IN_SAME_STEP;
	}

	private Type runOBJECT_NAME_AND_RT(PRWorkbook w) {
		object = U.getCell(w, 0);
		
		
		if(U.isBlank(object)) {
			U.writeMsg("Object API Name should be found in cell A2 - " + w.currentSheet.getSheetName(), Color.RED, true);
			
		}
		
		this.getSheetIDS(w);
		
		return Type.NEXT_STEP;
	}
	/*
	private boolean runPOSITION(PRWorkbook w) {
		
		this.getSheetIDS(w);
		
		//Consider success, code will fail before reach here because of debug()
		return true;
	}*/
	
	private Type runREAD(PRWorkbook w) {
		String fieldAPIName = U.getCell(w, 1);
		String type = U.getCell(w, 2);
		
		Integer numberOfHiddenColumn = 0;
		if(!w.currentRow.getZeroHeight()) {
			for(int i=index_start; i<=index_end; i++) {
				int tabIndex = i - index_start - numberOfHiddenColumn;
				
				boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
				if(isColumnHidden == false) {
					String currentId = IDS.get(tabIndex);
					
					XML_Object f = (XML_Object) w.getCorrectCorrectFile(XML_Object.class, currentId);
					String recordTypePicklistValues = U.getCell(w, i);
					
					//Created in previous step
					Node recordTypes = sheetRecordTypes.get(tabIndex);
					
					if(!U.isBlank(recordTypePicklistValues)) {
						if(!type.toLowerCase().contains("picklist")) {
							U.writeMsg("WARNING " + fieldAPIName + " is not defined as a picklist but it has picklist values assigned." + w.currentSheet.getSheetName(), Color.BLACK, false);
							
						}
						
						
						String[] values = recordTypePicklistValues.split("\n");
						Node picklistValues  = f.file.createElement("picklistValues");
						Node picklist = f.file.createElement("picklist");
						picklistValues.appendChild(picklist).appendChild(f.file.createTextNode(fieldAPIName));
						for(String v : values) {
							
							Node nvalue = f.file.createElement("values");
							
							Node fullNameV = f.file.createElement("fullName");
							Node ndefault = f.file.createElement("default");
							
							nvalue.appendChild(fullNameV).appendChild(f.file.createTextNode(v.replaceAll("\\(Default\\)", "").replaceAll("\\(default\\)", "").trim()));
							nvalue.appendChild(ndefault).appendChild(f.file.createTextNode(v.toLowerCase().contains("(default)") ? "true" : "false"));
							
							picklistValues.appendChild(nvalue);
							
							
						}
	
						recordTypes.appendChild(picklistValues);
					}
				} else {
					numberOfHiddenColumn ++;
				}
			}
		}
		
		return Type.STAY_IN_SAME_STEP;
	}

	

	private Type runINDEX(PRWorkbook w) {
		this.getIndexes(w, "Record Types");
		
		//U.debug(w.currentSheet, "INDEX : " + index_start + " " + index_end, false);
		
		if(index_start == -1) {//It is okay here, all object do not have record types, so we never go next step
			return Type.STOP;
		} 
		
		boolean success = (this.index_start != -1 && this.index_end != -1);
		
		if(!success) {
			U.writeMsg("MARKUP MISSING 'Record Types' markup not found in object sheet" + w.currentSheet.getSheetName(), Color.BLACK, false);
			
		}
		
	
		return success ? Type.NEXT_STEP : Type.STOP;
	}

	@Override
	public boolean isCorrectSheet(PRWorkbook w, String sheetName) {		
		return !U.isSpecialSheet(w, sheetName);
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
			
			String header = U.getCell(w, i);
			
			
			String[] recordTypeInfo = header.split("\n");
 			String recordTypeApi = null;
 			String recordTypeLabel = null;
 			if(recordTypeInfo.length == 1) {
 				recordTypeApi = header;
 				recordTypeLabel = header.replaceAll("_", " ");
 			} else {
 				recordTypeApi = recordTypeInfo[0];
 				recordTypeLabel = recordTypeInfo[1];
 			}
 			recordTypeLabels.add(recordTypeLabel);
 			
			String filename = object + ".object";
			if(!isColumnHidden) {
				IDS.add(filename);
				headers.add(recordTypeApi);
				
				w.recordTypes.add(recordTypeApi);
				w.fpackage.p_recordTypes.add(object + "." + recordTypeApi);
			} else {
				U.writeMsg("HIDDEN RECORD TYPE " + object + "." + recordTypeApi, Color.BLACK, false);
				
			}
		}
	}



}
