package utils;

import org.apache.poi.ss.usermodel.Cell;

import workbook.PRWorkbook;


public class PRUtil {
	public static boolean isBlank(String str) {
		return (str == null || str.trim().equals(""));
	}
	
	public static void info(PRWorkbook w, String subject, String msg) {
		debug(w, subject, msg, false);
	}
	
	private static void debug(PRWorkbook w, String subject, String msg, boolean required) {
		if(required) {
			System.err.println("------------------------------------------------------------------------");
			System.err.println(w.currentSheet.getSheetName().trim() + " : " +   String.valueOf((char) (65 + w.currentCell)) + w.row_index +  (w.currentStep == null ? "" : " : " +  w.currentStep.getClass().toString().split("\\.")[1]));
			
			System.err.println(">> " + subject + " : " +  msg);
			System.exit(0);
		} else {
			System.out.println("------------------------------------------------------------------------");
			System.out.println(w.currentSheet.getSheetName().trim() + " : " +   String.valueOf((char) (65 + w.currentCell)) + w.row_index +  (w.currentStep == null ? "" : " : " +  w.currentStep.getClass().toString().split("\\.")[1]));
			
			System.out.println("");
			System.out.println(">> " + subject + " : " + msg);
		}
		
		
		
	}
	
	public static String getCell(PRWorkbook w, int i) {
		
		w.currentCell = i;
		
		
		Cell c = w.currentRow.getCell(i);
		String str = "";
		
		if(c!=null) {
			str = c.getStringCellValue().trim();
		}
		
		return str;
	}
	
	public static void fatal(PRWorkbook w, String msg) {
		debug(w, "FATAL", msg, true);
	}
}
