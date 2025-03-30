package utils;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.poi.ss.usermodel.Cell;

import app.App;
import app.Config;
import app.UserInterface;
import workbook.PRWorkbook;


public class U {
	public static boolean exit = false;
	
	public static boolean isBlank(String str) {
		return (str == null || str.trim().equals(""));
	}
	
	public static void print(String type, String message) {
		if(type=="T")
			System.out.println("----------------------------------------   " + message);
		
	}
	
	public static void fatal_message(String message) {
		JOptionPane.showConfirmDialog(null, message, "Error", JOptionPane.PLAIN_MESSAGE);
		System.exit(0);
	}
	
	
	/*
	 * Basically there are 2 types of sheet:
	 * - Specific sheet in a specific format (Record type assignment, layout assignment, etc.)
	 * - The rest: objects definition that contains: field permission, layout definition, record type definition, etc.
	 * 
	 * When reading objects, by default we read the sheet, unless it's a specific one (or it's highlighted as to be ignored in the configuration)
	 */
	public static boolean isSpecialSheet(PRWorkbook w, String sheetName) {
		boolean isSpecificSheet = (w.c.SHEET_PS.equalsIgnoreCase(sheetName) ||
				w.c.SHEET_LAYOUT_ASSIGNMENT.equalsIgnoreCase(sheetName) ||
				w.c.SHEET_RECORD_TYPE_ASSIGNMENT.equalsIgnoreCase(sheetName) ||
				w.c.SHEET_PS.equalsIgnoreCase(sheetName) ||
				w.c.SHEET_PROFILE.equalsIgnoreCase(sheetName)/* ||
				w.c.SHEET_LISTVIEW.equalsIgnoreCase(sheetName) ||
				w.c.SHEET_SHARING_RULES.equalsIgnoreCase(sheetName)*/);
		
		boolean isToIgnore = false;
		for(String toIgnore : w.c.SHEETS_TO_IGNORE) {
			if(sheetName.equalsIgnoreCase(toIgnore)) {
				isToIgnore = true;
			}
		}
		
		
		return isSpecificSheet || isToIgnore;
	}
	
	
	
	
	
	public static void writeMsg(String msg, Color c, boolean stopExecution) {
		appendToPane(UserInterface.debugInfos2, msg + "\n", c, Color.WHITE);
		
		if(stopExecution) exit = true;
	}
	
	public static void appendToPane(JTextPane textPane, String text, Color foregroundColor, Color backgroundColor) {

        StyledDocument doc = textPane.getStyledDocument();
        
    	SimpleAttributeSet keyWord = new SimpleAttributeSet();
    	StyleConstants.setForeground(keyWord, foregroundColor);
    	StyleConstants.setBold(keyWord, true);
        
        
        try {
			doc.insertString(doc.getLength(), text, keyWord );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public static boolean doNotDeploy(String name, String why) {
		boolean doNotDeploy = false;
		
		if(Config.selected.PROFILES_TO_IGNORE.contains(name.toLowerCase()) == true) {
			doNotDeploy = true;
			U.writeMsg("DO NOT DEPLOY - " + name + "(" + why + ")", Color.ORANGE, false);
		}
		return doNotDeploy;
	}
	
}
