package utils;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import app.UserInterface;
import workbook.PRWorkbook;

public class Debug {
	public static boolean exit = false;
	public static ArrayList<Debug> debugs;
	
	public String sheet;
	public String cell;
	public String subject;
	public String message;
	public boolean isFatal;
	
	public static void addDebug(PRWorkbook w, String subject, String message, boolean isFatal) {
		Debug d = new Debug();
		if(w != null) {
			d.sheet = w.currentSheet == null ? "" : w.currentSheet.getSheetName();
			d.cell = w.currentRow + "" + w.currentCell;
		}
		d.subject = subject;
		d.message = message;
		d.isFatal = isFatal;
		
		if(debugs == null) {
			debugs = new ArrayList<Debug>();
		}
		debugs.add(d);
	}
	
	public static void print(JTextPane textPane, Debug d) {
		
		appendToPane(UserInterface.debugInfos2,"======== " + d.subject + " : \n", d.isFatal? Color.RED : Color.BLUE, Color.WHITE);
		
		if(d.isFatal) {
			appendToPane(UserInterface.debugInfos2,">>>> " + d.message + "\n", Color.RED, Color.WHITE);
			exit = true;
		} else {
			appendToPane(UserInterface.debugInfos2,">>>> " + d.message + "\n", Color.BLACK, Color.WHITE);
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
}
