package utils;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.poi.ss.usermodel.Cell;

import app.ConfigManager;
import app.UserInterface;
import workbook.PRWorkbook;


public class PRUtil {
	public static boolean exit = false;
	
	public static boolean isBlank(String str) {
		return (str == null || str.trim().equals(""));
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
		
		if(ConfigManager.selected.PROFILES_TO_IGNORE.contains(name.toLowerCase()) == true) {
			doNotDeploy = true;
			PRUtil.writeMsg("DO NOT DEPLOY - " + name + "(" + why + ")", Color.ORANGE, false);
		}
		return doNotDeploy;
	}
	
}
