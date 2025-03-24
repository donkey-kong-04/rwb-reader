package app;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.CardLayout;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

public class UserInterface2 {

	private JFrame frame;

	

	/**
	 * Create the application.
	 */
	public UserInterface2() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 929, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel side_panel = new JPanel();
		side_panel.setBounds(0, 0, 245, 441);
		side_panel.setBackground(new Color(54, 33, 89));
		frame.getContentPane().add(side_panel);
		side_panel.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 84, 245, 52);
		side_panel.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnCountry = new JButton("New button");
		btnCountry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCountry.setBackground(new Color(85,65,118));
		btnCountry.setForeground(new Color(204,204,204));
		btnCountry.setBorder(null);
		panel_1.add(btnCountry);
		
		JPanel panel_1_1 = new JPanel();
		panel_1_1.setBounds(0, 136, 245, 52);
		side_panel.add(panel_1_1);
		panel_1_1.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnCountry_1 = new JButton("New button");
		btnCountry_1.setForeground(new Color(204, 204, 204));
		btnCountry_1.setBackground(new Color(64, 43, 100));
		btnCountry_1.setBorder(null);
		panel_1_1.add(btnCountry_1);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(122,72,221));
		panel.setBounds(244, 53, 669, 142);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel label_selected = new JLabel("New label");
		label_selected.setForeground(new Color(204,204,204));
		label_selected.setBounds(24, 11, 182, 27);
		panel.add(label_selected);
		
		JLabel label_selected_1 = new JLabel("Folder Selected");
		label_selected_1.setForeground(new Color(204,204,204));
		label_selected_1.setBounds(24, 49, 182, 27);
		panel.add(label_selected_1);
		
		JLabel label_selected_1_1 = new JLabel("File Selected");
		label_selected_1_1.setForeground(new Color(204,204,204));
		label_selected_1_1.setBounds(24, 87, 182, 27);
		panel.add(label_selected_1_1);
	}
}
