package app;


import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.json.simple.parser.ParseException;




public class App {
	
	public static void main(String[] args) {
		
		if(!lock()) {
			JOptionPane.showConfirmDialog(null, "Only one version of the program can be opened at once. The program will close");
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
