package app;


import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;

import org.json.simple.parser.ParseException;

import utils.U;




public class App {
	
	public static void main(String[] args) throws FileNotFoundException {
		
		if(!lock()) {
			JOptionPane.showConfirmDialog(null, "You already have one reader opened", "Error", JOptionPane.PLAIN_MESSAGE);
			throw new InternalError("Multiple instance detected");
		}
		
		run();
		
	}
	
	public static void run() {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
					
					try {
						new ConfigManager();
						UserInterface window = new UserInterface();
						window.frame.setVisible(true);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
