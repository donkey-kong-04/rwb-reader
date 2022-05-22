package app;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import utils.PRUtil;
import workbook.PRWorkbook;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;

public class UserInterface {

	JFrame frame;
	private JTextField filepath;
	private JTextField folderpath;
	private JTextField apex;
	private JTextField tabvisibility;
	private JTextField layoutassign;
	private JTextField rtassign;
	private JTextField listview;
	private JTextField sharingrule;
	private JTextField doNotRemovePermission;
	private JTextField name;
	private JTextArea profilesToIgnore;
	private JTextArea sheetsToIgnore;
	private JLabel label_filepath;
	private JLabel label_folder;
	private JLabel config_name;
	public static JTextPane debugInfos2;
	
	private JButton selected;
	/**
	 * Create the application.
	 */
	public UserInterface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		frame.setBounds(100, 100, 772, 632);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		int i=0;
		
		for(String key : ConfigManager.configs.keySet()) {
		
			Config c = ConfigManager.configs.get(key);
			JButton btnNewButton = new JButton(c.Name);
			btnNewButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					JButton button = (JButton) e.getSource();
					selected.setBackground(Color.WHITE);
					
					button.setBackground(Color.ORANGE);
					selected = button;
					loadConfig(button.getText());
				}
			});
			btnNewButton.setBounds(0, i, 104, 31);
			if(c.selected) {
				btnNewButton.setBackground(Color.ORANGE);
				selected = btnNewButton;
			} else {
				btnNewButton.setBackground(Color.WHITE);
			}
			frame.getContentPane().add(btnNewButton);
			i += 32;
		}
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.ORANGE);
		tabbedPane.setBounds(110, 0, 646, 593);
		frame.getContentPane().add(tabbedPane);
		
		JPanel execution_panel2 = new JPanel();
		tabbedPane.addTab("Execution", null, execution_panel2, null);
		execution_panel2.setLayout(null);
		
		config_name = new JLabel("Config Name:" + ConfigManager.selected.Name);
		config_name.setBounds(10, 2, 621, 26);
		execution_panel2.add(config_name);
		
		label_filepath = new JLabel("Filepath: " + ConfigManager.selected.filepath);
		label_filepath.setBounds(10, 39, 621, 26);
		execution_panel2.add(label_filepath);
		
		label_folder = new JLabel("Folder path: " + ConfigManager.selected.package_folder);
		label_folder.setBounds(10, 76, 621, 18);
		execution_panel2.add(label_folder);
		
		JButton btnNewButton_2 = new JButton("RUN");
		btnNewButton_2.setBackground(Color.GREEN);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PRUtil.exit = false;
				
				System.setProperty("line.separator", "\n");
				try {
					PRWorkbook w = new PRWorkbook();
					debugInfos2.setText("");
					PRUtil.info(null, "BEGIN", "");
					
					w.read();
					
					if(PRUtil.exit == true) {
						w.end();
						return;
					}
					
					w.postCheck();
					w.writeFiles();
					
					
					w.end();
					PRUtil.info(null, "END", "");
				} catch(Exception e1) {
					String stacktrace = ExceptionUtils.getStackTrace(e1);
					
					PRUtil.fatal(null, stacktrace);
				}
			}
		});
		btnNewButton_2.setBounds(181, 105, 250, 43);
		execution_panel2.add(btnNewButton_2);
		
		JLabel lblNewLabel_1 = new JLabel("Debug info:");
		lblNewLabel_1.setBounds(10, 161, 116, 14);
		execution_panel2.add(lblNewLabel_1);
		
		JPanel debug_scroll_panel = new JPanel();
		debug_scroll_panel.setBounds(10, 186, 621, 368);
		execution_panel2.add(debug_scroll_panel);
		debug_scroll_panel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 621, 368);
		debug_scroll_panel.add(scrollPane);
		
		debugInfos2 = new JTextPane();
		debugInfos2.setEditable(false);
		
		scrollPane.setViewportView(debugInfos2);
		
		
		
		JPanel config_panel = new JPanel();
		tabbedPane.addTab("Config", null, config_panel, null);
		config_panel.setLayout(null);
		
		JLabel File = new JLabel("Filepath");
		File.setBounds(10, 35, 58, 31);
		config_panel.add(File);
		
		filepath = new JTextField();
		filepath.setEditable(false);
		filepath.setBounds(78, 35, 553, 31);
		config_panel.add(filepath);
		filepath.setColumns(10);
		
		JLabel folder = new JLabel("Folder path");
		folder.setBounds(10, 74, 58, 31);
		config_panel.add(folder);
		
		folderpath = new JTextField();
		folderpath.setEditable(false);
		folderpath.setColumns(10);
		folderpath.setBounds(78, 74, 553, 31);
		config_panel.add(folderpath);
		
		JLabel sheetApex = new JLabel("Apex sheet's name");
		sheetApex.setBounds(10, 399, 142, 31);
		config_panel.add(sheetApex);
		
		apex = new JTextField();
		apex.setEditable(false);
		apex.setColumns(10);
		apex.setBounds(211, 399, 420, 31);
		config_panel.add(apex);
		
		JLabel lblTabVisibilitySheets = new JLabel("Tab Visibility sheet's name");
		lblTabVisibilitySheets.setBounds(10, 441, 142, 31);
		config_panel.add(lblTabVisibilitySheets);
		
		tabvisibility = new JTextField();
		tabvisibility.setEditable(false);
		tabvisibility.setColumns(10);
		tabvisibility.setBounds(211, 441, 420, 31);
		config_panel.add(tabvisibility);
		
		JLabel layoutassignment = new JLabel("Layout Assignment sheet's name");
		layoutassignment.setBounds(10, 314, 173, 31);
		config_panel.add(layoutassignment);
		
		layoutassign = new JTextField();
		layoutassign.setEditable(false);
		layoutassign.setColumns(10);
		layoutassign.setBounds(211, 314, 420, 31);
		config_panel.add(layoutassign);
		
		JLabel recordtypeassignment = new JLabel("Record Type Assignment sheet's name");
		recordtypeassignment.setBounds(10, 356, 201, 31);
		config_panel.add(recordtypeassignment);
		
		rtassign = new JTextField();
		rtassign.setEditable(false);
		rtassign.setColumns(10);
		rtassign.setBounds(211, 356, 420, 31);
		config_panel.add(rtassign);
		
		JLabel lblListViewSheet = new JLabel("List view sheet's name");
		lblListViewSheet.setBounds(10, 234, 161, 31);
		config_panel.add(lblListViewSheet);
		
		listview = new JTextField();
		listview.setEditable(false);
		listview.setColumns(10);
		listview.setBounds(211, 234, 420, 31);
		config_panel.add(listview);
		
		JLabel lblSharingRuleSheet = new JLabel("Sharing rule sheet's name");
		lblSharingRuleSheet.setBounds(10, 272, 161, 31);
		config_panel.add(lblSharingRuleSheet);
		
		sharingrule = new JTextField();
		sharingrule.setEditable(false);
		sharingrule.setColumns(10);
		sharingrule.setBounds(211, 272, 420, 31);
		config_panel.add(sharingrule);
		
		JLabel lblSheetsToIgnore = new JLabel("Sheet(s) to ignore");
		lblSheetsToIgnore.setBounds(10, 116, 161, 31);
		config_panel.add(lblSheetsToIgnore);
		
		sheetsToIgnore = new JTextArea();
		sheetsToIgnore.setLineWrap(true);
		sheetsToIgnore.setEditable(false);
		sheetsToIgnore.setBounds(183, 116, 448, 48);
		config_panel.add(sheetsToIgnore);
		
		JLabel lblProfilesToIgnore = new JLabel("Profile(s) to ignore");
		lblProfilesToIgnore.setBounds(10, 175, 161, 31);
		config_panel.add(lblProfilesToIgnore);
		
		profilesToIgnore = new JTextArea();
		profilesToIgnore.setLineWrap(true);
		profilesToIgnore.setEditable(false);
		profilesToIgnore.setBounds(183, 175, 448, 48);
		config_panel.add(profilesToIgnore);
		
		JLabel lblProfilesDo = new JLabel("Profile(s) - Do no remove permissions");
		lblProfilesDo.setBounds(10, 483, 201, 31);
		config_panel.add(lblProfilesDo);
		
		doNotRemovePermission = new JTextField();
		doNotRemovePermission.setEditable(false);
		doNotRemovePermission.setColumns(10);
		doNotRemovePermission.setBounds(211, 483, 420, 31);
		config_panel.add(doNotRemovePermission);
		
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(10, 0, 58, 31);
		config_panel.add(lblName);
		
		name = new JTextField();
		name.setEditable(false);
		name.setColumns(10);
		name.setBounds(78, 0, 553, 31);
		config_panel.add(name);
		
		loadConfig(ConfigManager.selected.Name);
	}
	
	public void loadConfig(String buttonLabel) {
		Config c = ConfigManager.configs.get(buttonLabel);
		
		config_name.setText("Config Name: " + c.Name);
		label_filepath.setText("Filepath: " + c.filepath);
		label_folder.setText("Folder: " + c.package_folder);
		
		filepath.setText(c.filepath);
		doNotRemovePermission.setText(String.join(",", c.KEEP_PROFILES_PERMISSIONS));
		name.setText(c.Name);
		sharingrule.setText(c.SHEET_SHARING_RULES);
		layoutassign.setText(c.SHEET_LAYOUT_ASSIGNMENT);
		rtassign.setText(c.SHEET_RECORD_TYPE_ASSIGNMENT);
		profilesToIgnore.setText(String.join(",", c.PROFILES_TO_IGNORE));
		sheetsToIgnore.setText(String.join(",", c.SHEETS_TO_IGNORE));
		folderpath.setText(c.package_folder);
		apex.setText(c.SHEET_APEX);
		listview.setText(c.SHEET_LISTVIEW);
		tabvisibility.setText(c.SHEET_TAB_VISIBILITY);
		
		ConfigManager.selected = c;
		
	}
}
