package step;

import java.util.ArrayList;

import org.w3c.dom.Node;

import file.XML_Object;
import utils.PRUtil;
import workbook.PRWorkbook;

public class Step_RecordType extends Step {
	String object;
	
	ArrayList<Node> sheetRecordTypes = new ArrayList<Node>();
	
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
		
		String objectPermissionPosition = PRUtil.getCell(w, 0);
		
		if(objectPermissionPosition.equalsIgnoreCase("Object Permissions")) {
			Integer numberOfHiddenColumn = 0;
			for(int i=index_start; i<=index_end;i++) {
				int tabIndex = i - index_start - numberOfHiddenColumn;
				boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
				
				if(isColumnHidden == false) {
					String isActive = (PRUtil.getCell(w, i).equalsIgnoreCase("x") ? "true" : "false");
					
					if(isActive.equals("false")) {
						PRUtil.info(w, "INFO", "Record type is set as inactive : " + headers.get(tabIndex));
					}
					
					
					XML_Object f = (XML_Object) w.getCorrectCorrectFile(XML_Object.class, IDS.get(tabIndex));
					
					Node recordTypes = f.file.createElement("recordTypes");
					Node fullname  = f.file.createElement("fullName");
					Node active  = f.file.createElement("active");
					Node label  = f.file.createElement("label");
					
					recordTypes.appendChild(fullname).appendChild(f.file.createTextNode(headers.get(tabIndex).trim()));
					recordTypes.appendChild(active).appendChild(f.file.createTextNode(isActive));
					recordTypes.appendChild(label).appendChild(f.file.createTextNode(header_labels.get(tabIndex).trim()));
					
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
		String t0 = PRUtil.getCell(w, 0);
		String t1 = PRUtil.getCell(w, 1);
		
		if(t0.equalsIgnoreCase("Field") && t1.equalsIgnoreCase("Field API Name")) {
			return Type.NEXT_STEP;
		}
		return Type.STAY_IN_SAME_STEP;
	}

	private Type runOBJECT_NAME_AND_RT(PRWorkbook w) {
		object = PRUtil.getCell(w, 0);
		
		
		if(PRUtil.isBlank(object)) {
			PRUtil.fatal(w, "Object API Name should be found in cell A2");
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
		String fieldAPIName = PRUtil.getCell(w, 1);
		String type = PRUtil.getCell(w, 2);
		
		Integer numberOfHiddenColumn = 0;
		if(!w.currentRow.getZeroHeight()) {
			for(int i=index_start; i<=index_end; i++) {
				int tabIndex = i - index_start - numberOfHiddenColumn;
				
				boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
				if(isColumnHidden == false) {
					String currentId = IDS.get(tabIndex);
					
					XML_Object f = (XML_Object) w.getCorrectCorrectFile(XML_Object.class, currentId);
					String recordTypePicklistValues = PRUtil.getCell(w, i);
					
					//Created in previous step
					Node recordTypes = sheetRecordTypes.get(tabIndex);
					
					if(!PRUtil.isBlank(recordTypePicklistValues)) {
						if(!type.toLowerCase().contains("picklist")) {
							PRUtil.info(w, "WARNING", fieldAPIName + " is not defined as a picklist but it has picklist values assigned.");
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
		
		//PRUtil.debug(w.currentSheet, "INDEX : " + index_start + " " + index_end, false);
		
		if(index_start == -1) {//It is okay here, all object do not have record types, so we never go next step
			return Type.STOP;
		} 
		
		boolean success = (this.index_start != -1 && this.index_end != -1);
		
		if(!success) {
			PRUtil.info(w, "MARKUP MISSING", "'Record Types' markup not found in object sheet");
		}
		
	
		return success ? Type.NEXT_STEP : Type.STOP;
	}

	@Override
	public boolean isCorrectSheet(PRWorkbook w, String sheetName) {
		String name = sheetName;
		return !(
				name.equalsIgnoreCase("Apex Components") ||
				name.equalsIgnoreCase("Revision History") ||
				name.equalsIgnoreCase("Sharing Rules") ||
				name.equalsIgnoreCase("Groups") ||
				name.equalsIgnoreCase("Email Template") ||
				name.equalsIgnoreCase("Tab Visibility") ||
				name.equalsIgnoreCase("List Views") ||
				name.equalsIgnoreCase("Documents") ||
				name.equalsIgnoreCase("Tab Visibility") ||
				name.equalsIgnoreCase("Role Hierarchy") ||
				name.equalsIgnoreCase("User") ||
				name.equalsIgnoreCase("Page Layout assignment") ||
				name.equalsIgnoreCase("Brand Security Picklist Value") ||
				name.equalsIgnoreCase("Record Type assignment")
				);
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
			
			String filename = object + ".object";
			if(!isColumnHidden) {
				IDS.add(filename);
				headers.add(recordTypeApi);
				header_labels.add(recordTypeLabel);
				
				w.recordTypes.add(recordTypeApi);
				w.fpackage.p_recordTypes.add(object + "." + recordTypeApi);
			} else {
				PRUtil.info(w, "HIDDEN RECORD TYPE", object + "." + recordTypeApi);
			}
		}
	}



}
