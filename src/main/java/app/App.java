package app;


import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.json.simple.parser.ParseException;

import utils.U;




public class App {
	
	public static void main(String[] args) throws FileNotFoundException {
		
		if(!lock()) {
			U.fatal_message("You already have one reader opened");
		}
		run();
		
	}
	
	public static void run() {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
					
				try {
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					Config.selected = new Config();
					UserInterface window = new UserInterface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					U.fatal_message("Unexpected error: " + e.toString());
				}	
			}
		});
	}
	private static boolean lock() {
	   try
	    {
	        final File file=new File("PRReader.lock");
	        if (file.createNewFile())
	        {
	            file.deleteOnExit();
	            return true;
	        }
	        return false;
	    }
	    catch (IOException e)
	    {
	        return false;
	    }
	}
}
