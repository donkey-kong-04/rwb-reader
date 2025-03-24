package step;


import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import utils.U;
import workbook.PRWorkbook;

/*
 * This class represent the action to make inside a Sheet
 * It has to be reset at each Sheet to restart from the beginning
 * 
 */
public abstract class Step {
	public enum Type {STAY_IN_SAME_STEP, NEXT_STEP, STOP, FAILURE};
	/*
	 * This is to know which:
	 * 		Cell index inside the CURRENT SHEET = Destination file ID
	 */
	ArrayList<String> IDS = new ArrayList<String>();
	/*
	 * This is to know inside the CURRENT SHEET :
	 * 		Cell index = Data of the header previously read
	 */
	ArrayList<String> headers = new ArrayList<String>();
	
	
	protected String[] STEPS;
	protected Map<String, Method> STEPS2;
	
	protected int STEP;
	
	protected int index_start;
	protected int index_end;
	public Type status;
	
	public Step() {
		
		this.STEPS = this.getSteps();
		index_start = -1;
		index_end = -1;
		this.STEP = 0;
		status = Type.STAY_IN_SAME_STEP;
	}
	
	public Type run(PRWorkbook w) {
		if(U.exit == true) {
			return Type.STOP;
		}
		
		if(this.isCorrectSheet(w, w.currentSheet.getSheetName())) {
			Type t = runStep(w);
			if(t == Type.NEXT_STEP) {
				this.STEP++;
			}
			return t;
		} else {
			return Type.STOP;
		}
		
	}

	public abstract void getSheetIDS(PRWorkbook w);
	
	public abstract String[] getSteps();
	
	public abstract Type runStep(PRWorkbook w);
	
	public Type runStep2(PRWorkbook w) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(STEP > (STEPS.length - 1) ) {
			U.writeMsg("ALGORITHM ERROR : Asking for a STEP that is not configured" + w.currentSheet.getSheetName(), Color.RED, true);
			
		}
		
		Method m = STEPS2.get("2");
		return (Type) m.invoke(this, w);
	}
	
	public abstract boolean isCorrectSheet(PRWorkbook w, String sheetName);
	
	
	public abstract void debugStepNotPassed(PRWorkbook w);
	
	public boolean isLastStep() {
		return (STEP == (STEPS.length - 1));
	}
	
	
	public void getIndexes(PRWorkbook w, String searchText) {

		this.index_start = -1; 
		this.index_end = -1;
		Iterator<Cell> cit = w.currentRow.cellIterator();
		int i = -1;
		
		
		boolean keepGoing = true;
		
		while(cit.hasNext() && keepGoing) {
			i++;
			w.currentCell = i;
			
			Cell c = cit.next();
			
			if(CellType.STRING == c.getCellType()) {
				String content = c.getStringCellValue().trim();
				
				if(content.equalsIgnoreCase(searchText)) {
					this.index_start = i;
					
				} else if(this.index_start != -1) {
					if(!U.isBlank(content)) {
						this.index_end = i-1;
						keepGoing = false;
					}
				}
			} else if(CellType.BLANK != c.getCellType()){
				
				U.writeMsg("WARNING Wrong cell type " + i + ", content is ignored: " + c.getCellType() + w.currentSheet.getSheetName(), Color.BLACK, false);
				
			}
		}
		
		if(this.index_end == -1) {
			
			this.index_end = i;
		}
		//System.out.println(searchText + " - index start - end: " + index_start + " - " + index_end);
	}
	
	protected boolean ignoreField(String fieldApiName, String type) {
		boolean ignore = false;
		String[] standardFields = new String[] {"Name", "Field API Name", "LastModifiedById", "CreatedById", "LastModifiedBy", "CreatedBy", "CreatedDate", "Owner", "OwnerId", "CurrencyIsoCode"};
		
		if(type.toLowerCase().contains("master") || type.toLowerCase().contains("record") || type.toLowerCase().contains("required")) {
			ignore = true;
		}
		
		if(ignore == false) {
			for(String s : standardFields) {
				if(fieldApiName.equalsIgnoreCase(s)) {
					ignore = true;
					
				}
			}
		}
		
		return ignore;
	}
	
	protected void doesStepExist(PRWorkbook w) {
		if(STEP > (STEPS.length - 1) ) {
			U.writeMsg("ALGORITHM ERROR : Step does not exist" + w.currentSheet.getSheetName(), Color.RED, true);
			
		}
	}
	
	/*
	 * Update index end if it finish earlier
	 */
	protected void reviewEndingIndex(PRWorkbook w) {
		for(int i=index_start; i<=index_end; i++) {
			String header = U.getCell(w, i);
			
			//At least one or we consider the file in an incorrect format
			if(U.isBlank(header) && i==index_start) {
				index_end = index_start - 1;
				//U.writeMsg("Markup is present inside the sheet, but no associated member" + w.currentSheet.getSheetName(), Color.BLACK, false);
				
			} 
			//End earlier the loop
			else if(U.isBlank(header)) {
				index_end = i - 1;
				//U.writeMsg("INFO Markup ends earlier. Total members in the sheet = " + (index_end - index_start + 1) + w.currentSheet.getSheetName(), Color.BLACK, false);
				
			} 
		}
		//System.out.println("reviewing index start - end: " + index_start + " - " + index_end);
	}
	
	
	protected ArrayList<ArrayList<String>> parse(PRWorkbook w, String shareTo) {
		ArrayList<ArrayList<String>> parsedResult = new ArrayList<ArrayList<String>>();
		
		
		for(String line : shareTo.split("\n")) {
			System.out.println(line);
			Pattern p = Pattern.compile("([^\\[\\]]+)\\[([^\\[\\]]+)]");
			Matcher m = p.matcher(line);
			
			if(m.matches()) {
				System.out.println("Maaaatch");
				ArrayList<String> arrayLine = new ArrayList<String>();
				arrayLine.add(m.group(1));
				for(String el : m.group(2).split(",")) {
					arrayLine.add(el);
				}
				
				parsedResult.add(arrayLine);
			}
		}
		
		System.out.println(parsedResult);
		
		return parsedResult;
	}
}
