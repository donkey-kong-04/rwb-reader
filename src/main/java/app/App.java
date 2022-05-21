package app;


import java.awt.EventQueue;
import java.io.IOException;

import org.json.simple.parser.ParseException;

import utils.PRUtil;



public class App {
	
	public static void main(String[] args) {
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

}
