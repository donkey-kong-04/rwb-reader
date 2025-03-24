package step;




import java.awt.Color;
import java.util.ArrayList;

import org.w3c.dom.Node;

import file.XML_File;
import file.XML_Layout;
import utils.U;
import workbook.PRWorkbook;

public class Step_Layout extends Step {

	String object;
	
	@Override
	public String[] getSteps() {
		// TODO Auto-generated method stub
		return new String[] {"INDEX_LAYOUT", "OBJECT_AND_LAYOUT_POSITION", "POSITIONATE_FIELD", "READ_FIELDS", "READ_RELATED_LIST", "READ_RELATED_OBJECTS"};//, 
				//"READ_FIELDS", "READ_RELATED_LIST"};
	}

	@Override
	public Type runStep(PRWorkbook w) {
		
		this.doesStepExist(w);
		
		boolean isRowHidden = w.currentRow.getZeroHeight();
		if(isRowHidden) {
			return Type.STAY_IN_SAME_STEP;
		}
		String stp = STEPS[STEP];
		//U.debug(s,  "RUN STEP", false);
		if(stp.equals("INDEX_LAYOUT")) {
			return runINDEX(w);
		} else if(stp.equals("OBJECT_AND_LAYOUT_POSITION")) {
			runOBJECT(w);
			runPOSITION(w);
			return Type.NEXT_STEP;
		} else if(stp.equals("POSITIONATE_FIELD")) {
			return runPOSITION_ON_FIELDS(w);
		} else if(stp.equals("READ_FIELDS")) {
			return runREAD_FIELDS(w);
		}  else if(stp.equals("READ_RELATED_LIST")) {
			return runRELATED(w);
		} else if(stp.equals("READ_RELATED_OBJECTS")) {
			return runRELATED_OBJECTS(w);
		}
		
		return Type.STOP;
	}
	
	private Type runRELATED_OBJECTS(PRWorkbook w) {
		
		Integer numberOfColumnHidden = 0;
		for(int i=index_start; i<=index_end; i++) {
			int tabIndex = i - index_start - numberOfColumnHidden;
			
			boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
			
			if(isColumnHidden == false) {
				String v = U.getCell(w, i).trim();
				//Section
				
				String currentId = IDS.get(tabIndex);
				
				XML_Layout f = (XML_Layout) w.getCorrectCorrectFile(XML_Layout.class, currentId);
				
				//New related list found
				if(!U.isBlank(v)) {
					Node relatedObject = f.file.createElement("relatedObject");
					f.relatedObjects.appendChild(relatedObject).appendChild(f.file.createTextNode(v));
					
				} 
			} else {
				numberOfColumnHidden ++;
			}
		}
		
		return Type.STAY_IN_SAME_STEP;
	}
	private Type runRELATED(PRWorkbook w) {
		String label = U.getCell(w, 0);
		String fieldAPIName = U.getCell(w, 1);
		String relatedAPIName = U.getCell(w, 2);
		
		if(label.equalsIgnoreCase("Related objects")) {
			//U.debug(s,  "NEXT STEP RELATED LIST", false);
			return Type.NEXT_STEP;
		} else {
			Integer numberOfColumnHidden = 0;
			for(int i=index_start; i<=index_end; i++) {
				int tabIndex = i - index_start - numberOfColumnHidden;
				
				boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
				
				if(isColumnHidden == false) {
					String v = U.getCell(w, i);
					//Section
					
					String currentId = IDS.get(tabIndex);
					
					XML_Layout f = (XML_Layout) w.getCorrectCorrectFile(XML_Layout.class, currentId);
					
					//New related list found
					if(!U.isBlank(relatedAPIName)) 
					{
						if(v.equalsIgnoreCase("x")) {
							Node relatedLists = f.file.createElement("relatedLists");
							
							
							String relatedListName  = null;
							if(!U.isBlank(fieldAPIName)) {
								relatedListName = fieldAPIName.trim() + "." + relatedAPIName.trim();
							} else {
								relatedListName  = relatedAPIName.trim();
							}
							Node relatedList = f.file.createElement("relatedList");
							relatedLists.appendChild(relatedList).appendChild(f.file.createTextNode(relatedListName));
							
							f.currentRelatedList = relatedLists;
							
							f.relatedLists.add(relatedLists);
						}
					} 
					//Field of relatedList
					else if(!U.isBlank(fieldAPIName)) 
					{
						if(v.equalsIgnoreCase("x")) {
							
							if(f.currentRelatedList == null) {
								U.writeMsg("FILE CONSTRUCTION ERROR : Related List declaration expected before field of related list - " + relatedAPIName, Color.RED, true);
							}
							
							Node fields = f.file.createElement("fields");
							f.currentRelatedList.appendChild(fields).appendChild(f.file.createTextNode(fieldAPIName.trim()));
						}
					}
				} else {
					numberOfColumnHidden ++;
				}
			}
			
			
			return Type.STAY_IN_SAME_STEP;
		}
	}

	private void runOBJECT(PRWorkbook w) {
		object = U.getCell(w, 0);
		
		if(U.isBlank(object)) {
			U.writeMsg("Object API Name should be found in cell A2 - " + w.currentSheet.getSheetName(), Color.RED, true);
		}
		object = object.trim();
	}

	private Type runPOSITION_ON_FIELDS(PRWorkbook w) {
		String field = U.getCell(w, 0);
		String apiName = U.getCell(w, 1);
		if(field.trim().equalsIgnoreCase("Field") && apiName.trim().equalsIgnoreCase("Field API Name")) {
			return Type.NEXT_STEP;
		}
		
		return Type.STAY_IN_SAME_STEP;
	}

	private Type runREAD_FIELDS(PRWorkbook w) {
		
		String label = U.getCell(w, 0);
		String apiname = U.getCell(w, 1);
		String type = U.getCell(w, 2);
		
		//U.debug(s, "Read field", false);
		if(label.equalsIgnoreCase("Related Data (Objects & Columns)")) {
			//U.debug(s,  "NEXT STEP RELATED LIST", false);
			return Type.NEXT_STEP;
		} else {
			//U.debug(s, t1 + " " + t2, false);
			Integer numberOfColumnHidden = 0;
			for(int i=index_start; i<=index_end; i++) {
				boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
				
				if(isColumnHidden == false) {
				
					String v = U.getCell(w, i);
					
					
					String currentId = IDS.get(i - index_start - numberOfColumnHidden);
					
					XML_Layout f = (XML_Layout) w.getCorrectCorrectFile(XML_Layout.class, currentId);
					
					//Section 
					if(type.equalsIgnoreCase("section") || type.equalsIgnoreCase("section 1") || type.equalsIgnoreCase("section 2")) 
					{
						f.oneColumn = false;
						if(type.equalsIgnoreCase("section 1")) {
							f.oneColumn = true;
						}
						if(v.equalsIgnoreCase("x")) {
							
							createSection(w, f, label);
						} else {
							f.currentLayoutColumn = null;//reset
						}
					} 
					else if(type.toLowerCase().contains("vf page")) {
						if(v.equalsIgnoreCase("x")) {
							if(f.currentLayoutColumn == null) {
								U.writeMsg("FILE CONSTRUCTION ERROR : Section should be found before any layout element - " + w.currentSheet.getSheetName(), Color.RED, true);
							}
							
							Node layoutItems = f.file.createElement("layoutItems");
							
							Node height = f.file.createElement("height");
							Node page = f.file.createElement("page");
							Node showLabel = f.file.createElement("showLabel");
							Node showScrollBar = f.file.createElement("showScrollbars");
							Node width = f.file.createElement("width");
							
							layoutItems.appendChild(height).appendChild(f.file.createTextNode("300"));
							layoutItems.appendChild(page).appendChild(f.file.createTextNode(apiname.trim()));
							layoutItems.appendChild(showLabel).appendChild(f.file.createTextNode("false"));
							layoutItems.appendChild(showScrollBar).appendChild(f.file.createTextNode("true"));
							layoutItems.appendChild(width).appendChild(f.file.createTextNode("100%"));
							
							f.currentLayoutColumn.appendChild(layoutItems);
						}
					}
					//Blank space field
					else if(type.toLowerCase().contains("blank space")) 
					{
						//System.out.println("=====>BLANK SPACE FOUND for : " + (f.currentLayoutColumn != null ? f.currentLayoutColumn.getChildNodes().item(0).getTextContent() : "null" ));
						if(v.equalsIgnoreCase("x")) {
							if(f.currentLayoutColumn == null) {
								U.writeMsg("FILE CONSTRUCTION ERROR : Section should be found before any layout element - " + w.currentSheet.getSheetName(), Color.RED, true);
								
							}
							
							Node layoutItems = f.file.createElement("layoutItems");
							Node emptySpace = f.file.createElement("emptySpace");
							layoutItems.appendChild(emptySpace).appendChild(f.file.createTextNode("true"));
							
							this.addLayoutItemsToLayoutColumn(w, f, false, layoutItems);
						}
					} 
					//Display field
					else if(!U.isBlank(label) && !U.isBlank(apiname)) 
					{
						
						
						boolean isOnTheRightColumn = type.toLowerCase().contains("right");
						boolean isRead = v.toLowerCase().contains("r");
						boolean isEdit = v.toLowerCase().contains("e");
						boolean isRequired = v.toLowerCase().contains("m");//m for mandatory
						
						if(isRead || isEdit ||isRequired) {
							
							if(f.currentLayoutColumn == null) {
								U.writeMsg("FILE CONSTRUCTION ERROR : Section should be found before any layout element - " + w.currentSheet.getSheetName(), Color.RED, true);
								
							}
							
							if((isEdit || isRequired) && type.toLowerCase().contains("formul")) {
								U.writeMsg("WARNING "+ apiname + " should be read-only since it is a formula", Color.ORANGE, false);
								
							}
							
							Node layoutItems = f.file.createElement("layoutItems");
							Node behavior = f.file.createElement("behavior");
							Node field = f.file.createElement("field");
							
							layoutItems.appendChild(behavior).appendChild(f.file.createTextNode(
								(isRequired ? "Required" :
									(isEdit ? "Edit" :
										(isRead ? "Readonly" : "")))
							));;
							layoutItems.appendChild(field).appendChild(f.file.createTextNode(apiname.trim()));
							
							this.addLayoutItemsToLayoutColumn(w, f, isOnTheRightColumn, layoutItems);
						}
					}
				} else {
					numberOfColumnHidden ++;
				}
			}//end for loop layouts of current sheet
		}//end "is field"
		
		return Type.STAY_IN_SAME_STEP;
	}

	private void runPOSITION(PRWorkbook w) {
		
		this.getSheetIDS(w);


	}

	private Type runINDEX(PRWorkbook w) {
		this.getIndexes(w, "Page Layouts");
		
		boolean success = (index_start != -1 && index_end != -1);
		
		if(!success) {
			//U.writeMsg("MARKUP MISSING 'Page Layouts' markup not found in " + w.currentSheet.getSheetName(), Color.BLACK, false);
		
		}


		return success ? Type.NEXT_STEP : Type.STOP;
	}


	@Override
	public void debugStepNotPassed(PRWorkbook w) {
		String stp = STEPS[STEP];
		
		if(stp.equals("POSITIONATE_FIELD")) {
			U.writeMsg("MARKUP MISSING : 'Field' and 'Field API Name' should be found before fields declaration" + w.currentSheet.getSheetName(), Color.RED, true);
			
		}
		
		else if(stp.equals("READ_FIELDS")) {
			U.writeMsg("MARKUP MISSING 'Related Data (Objects & Columns)' markup not found " + w.currentSheet.getSheetName(), Color.BLACK, false);
			
		}
	}
	

	private Node createSection(PRWorkbook w, XML_Layout f, String sectionName) {
		
		Node sections = f.file.createElement("layoutSections");
		
		Node customLabel 		= f.file.createElement("customLabel"),
				detailHeading 	= f.file.createElement("detailHeading"),
				editHeading 	= f.file.createElement("editHeading"),
				label 			= f.file.createElement("label"),
				layoutColumns 	= f.file.createElement("layoutColumns"),
				style			= f.file.createElement("style");
		
		sections.appendChild(customLabel).appendChild(f.file.createTextNode("true"));
		sections.appendChild(detailHeading).appendChild(f.file.createTextNode("true"));
		sections.appendChild(editHeading).appendChild(f.file.createTextNode("true"));
		sections.appendChild(label).appendChild(f.file.createTextNode(sectionName.trim()));
		sections.appendChild(layoutColumns);
		
		if(!f.oneColumn) {
			Node layoutColumns2 	= f.file.createElement("layoutColumns");
			sections.appendChild(layoutColumns2);
			f.currentLayoutColumn2 = layoutColumns2;
		}
		
		sections.appendChild(style).appendChild(f.file.createTextNode(f.oneColumn ? "OneColumn" : "TwoColumnsTopToBottom"));
		
		f.sections.add(sections);
		
		f.currentLayoutColumn = layoutColumns;
		
		return layoutColumns;
	}

	@Override
	public void getSheetIDS(PRWorkbook w) {
		IDS = new ArrayList<String>();
		headers = new ArrayList<String>();
		
		this.reviewEndingIndex(w);
		
		for(int i=index_start; i<=index_end; i++) {
			boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
			String header = XML_File.parseForPackage(U.getCell(w, i));
			if(isColumnHidden == false) {
				
				
				String filename = object.trim() + "-" + header + ".layout";
				headers.add(header);
				IDS.add(filename);
				
				w.layouts.add(header);
				w.fpackage.p_layouts.add(object.trim() + "-" + header);
			} else {
				U.writeMsg("HIDDEN LAYOUT - " + header, Color.BLACK, false);
			
			}
		}
	}
	
	
	private void addLayoutItemsToLayoutColumn(PRWorkbook w, XML_Layout f, boolean rightColumn, Node layoutItems) {
		if(rightColumn) {
			if(f.currentLayoutColumn2 == null) {
				U.writeMsg("Cannot add an element on the right column if you have only one column" + w.currentSheet.getSheetName(), Color.RED, true);
			}
			
			f.currentLayoutColumn2.appendChild(layoutItems);
		} else {
			f.currentLayoutColumn.appendChild(layoutItems);
		}
	}

	@Override
	public boolean isCorrectSheet(PRWorkbook w, String sheetName) {
		return !U.isSpecialSheet(w, sheetName);
	}
}
