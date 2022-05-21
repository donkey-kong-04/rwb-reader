package utils;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.poi.ss.usermodel.Cell;

import app.UserInterface;
import workbook.PRWorkbook;


public class PRUtil {
	public static boolean exit = false;
	
	public static boolean isBlank(String str) {
		return (str == null || str.trim().equals(""));
	}
	
	public static void info(PRWorkbook w, String subject, String msg) {
		debug(w, subject, msg, false);
	}
	public static void fatal(PRWorkbook w, String msg) {
		debug(w, "FATAL", msg, true);
	}
	
	private static void debug(PRWorkbook w, String subject, String msg, boolean required) {
		
		appendToPane(UserInterface.debugInfos2,"======== " + subject + " : \n", required? Color.RED : Color.BLUE, Color.WHITE);
		
		if(required) {
			appendToPane(UserInterface.debugInfos2,">>>> " + msg + "\n", Color.RED, Color.WHITE);
			exit = true;
		} else {
			appendToPane(UserInterface.debugInfos2,">>>> " + msg + "\n", Color.BLACK, Color.WHITE);
		}
		
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
	
	
}
