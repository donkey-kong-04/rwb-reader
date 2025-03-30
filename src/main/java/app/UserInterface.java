package app;



import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import utils.U;
import utils.ZipDirectory;
import workbook.PRWorkbook;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.ScrollPaneConstants;

public class UserInterface {
	
	private int height = 738;
	private int width = 871;
	public static JFrame frame;
	private JTextField filepath;
	private JTextField folderpath;
	private JTextField profile;
	private JTextField permissionset;
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
	
	public static void reloadFullPage() {
		
		Config.selected = null;
		
		frame.setVisible(false);
		frame.dispose();
		App.run();
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		
		frame = new JFrame();
		frame.setResizable(false);
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		
		frame.setSize(new Dimension(width, height));
		frame.setLocation(new Point(100, 100));
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.ORANGE);
		tabbedPane.setBounds(0, 0, width, height);
		frame.getContentPane().add(tabbedPane);
		
		JPanel execution_panel2 = new JPanel();
		tabbedPane.addTab("Execution", null, execution_panel2, null);
		execution_panel2.setLayout(null);
		
		config_name = new JLabel("Config Name:" + Config.selected.Name);
		config_name.setBounds(10, 2, width-20, 26);
		execution_panel2.add(config_name);
		
		label_filepath = new JLabel("Filepath: " + Config.selected.filepath);
		label_filepath.setBounds(10, 39, width-20, 26);
		execution_panel2.add(label_filepath);
		
		label_folder = new JLabel("Folder path: " + Config.selected.package_folder);
		label_folder.setBounds(10, 76, width-20, 18);
		execution_panel2.add(label_folder);
		
		JButton btnNewButton_2 = new JButton("Create Package");
		btnNewButton_2.setBackground(Color.GREEN);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
				LocalDateTime now = LocalDateTime.now();  
				debugInfos2.setText("");
				U.writeMsg("BEGIN - " + dtf.format(now) + " for " + Config.selected.Name, Color.BLUE, false);
				
				
				
				String unpackageFolderPath = Config.selected.package_folder + "unpackaged";
				File file = new File(unpackageFolderPath);
				
				int input = 0;
				
				if(file.exists()) {
					input = JOptionPane.showConfirmDialog(null, "Careful the 'unpackaged/' folder hasn't been removed.\nIf you have remove components from your workbook they will not be removed from this folder 'unpackaged/'.\n\nAre you sure you want to proceed?", "Warning", JOptionPane.YES_NO_OPTION);
				}
				
				if(input == 0) {
					U.exit = false;
					
					System.setProperty("line.separator", "\n");
					try {
						PRWorkbook w = new PRWorkbook();
						
						w.read();
						
						if(U.exit == true) {
							w.end();
							return;
						}
						
						w.postCheck();
						w.writeFiles();
						
						w.end();
						
						ZipDirectory zd = new ZipDirectory();
						
						zd.zip(Config.selected.package_folder);
				        
						dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
						now = LocalDateTime.now();  
						
						U.writeMsg("END - " + dtf.format(now) + " for " + Config.selected.Name, Color.BLUE, false);
					} catch(Exception e1) {
						String stacktrace = ExceptionUtils.getStackTrace(e1);
						U.writeMsg(stacktrace, Color.RED, true);
					}
				}
				
				
			}
		});
		btnNewButton_2.setBounds(10, 105, 119, 43);
		execution_panel2.add(btnNewButton_2);
		
		JPanel debug_scroll_panel = new JPanel();
		debug_scroll_panel.setBounds(10, 239, width-20, 420);
		execution_panel2.add(debug_scroll_panel);
		debug_scroll_panel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(0, 10, width-20, 410);
		debug_scroll_panel.add(scrollPane);
		
		debugInfos2 = new JTextPane();
		debugInfos2.setBackground(Color.WHITE);
		debugInfos2.setEditable(false);
		
		scrollPane.setViewportView(debugInfos2);
		
		JButton btnNewButton_1 = new JButton("Open folder");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(Config.selected.package_folder));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton_1.setBounds(468, 105, 108, 43);
		execution_panel2.add(btnNewButton_1);
		
		JButton btnNewButton_3 = new JButton("Edit config");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File("./reader.json"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton_3.setBounds(581, 105, 119, 43);
		execution_panel2.add(btnNewButton_3);
		
		JButton BTN_DELETE_PACKAGE = new JButton("Delete package");
		BTN_DELETE_PACKAGE.setBackground(Color.RED);
		BTN_DELETE_PACKAGE.setForeground(Color.BLACK);
		BTN_DELETE_PACKAGE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				debugInfos2.setText("");
				String unpackageFolderPath = Config.selected.package_folder + "unpackaged";
				String zipFilePath = Config.selected.package_folder + "unpackaged.zip";
				ArrayList<File> fileToDelete = new ArrayList<File>();
				
				
				File folder = new File(unpackageFolderPath);
				if(folder.exists()) {
					fileToDelete.add(folder);
				}
				File zip = new File(zipFilePath);
				if(zip.exists()) {
					fileToDelete.add(zip);
				}
				
				String message = "";
				if(fileToDelete.size() > 0) {
					message = "This will remove from your directory:\n";
					for(File f : fileToDelete) {
						message += f.getPath() + "\n";
					}
					
					message += "\nAre you sure you want to proceed?";
					int input = JOptionPane.showConfirmDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION);
				
					if(input == 0) {
						try {
							FileUtils.deleteDirectory(folder);
							zip.delete();
							debugInfos2.setText("Package deleted");
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				} else {
					debugInfos2.setText("No file found to delete");
				}
			}
			
		});
		BTN_DELETE_PACKAGE.setBounds(139, 105, 136, 43);
		execution_panel2.add(BTN_DELETE_PACKAGE);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Please make sure your workbook is closed before creating/deleting a package. \nIt can make the app crash.");
		
		chckbxNewCheckBox.setForeground(Color.BLACK);
		chckbxNewCheckBox.setEnabled(false);
		chckbxNewCheckBox.setSelected(true);
		chckbxNewCheckBox.setBounds(6, 209, 690, 23);
		execution_panel2.add(chckbxNewCheckBox);
		
		JButton BTN_RELOAD_CONFIG = new JButton("Reload config");
		BTN_RELOAD_CONFIG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reloadFullPage();
			}
		});
		BTN_RELOAD_CONFIG.setBounds(327, 105, 136, 43);
		execution_panel2.add(BTN_RELOAD_CONFIG);
		
		JButton BTN_RELOAD_CONFIG_1 = new JButton("Clear log");
		BTN_RELOAD_CONFIG_1.setBounds(581, 159, 119, 43);
		execution_panel2.add(BTN_RELOAD_CONFIG_1);
		
		JButton BTN_RELOAD_CONFIG_1_1 = new JButton("Open RWB");
		BTN_RELOAD_CONFIG_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(Config.selected.filepath));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		BTN_RELOAD_CONFIG_1_1.setBounds(454, 159, 119, 43);
		execution_panel2.add(BTN_RELOAD_CONFIG_1_1);
		BTN_RELOAD_CONFIG_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				debugInfos2.setText("");
			}
		});
		
		
		
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
		
		profile = new JTextField();
		profile.setEditable(false);
		profile.setColumns(10);
		profile.setBounds(211, 399, 420, 31);
		config_panel.add(profile);
		
		JLabel lblTabVisibilitySheets = new JLabel("Tab Visibility sheet's name");
		lblTabVisibilitySheets.setBounds(10, 441, 142, 31);
		config_panel.add(lblTabVisibilitySheets);
		
		permissionset = new JTextField();
		permissionset.setEditable(false);
		permissionset.setColumns(10);
		permissionset.setBounds(211, 441, 420, 31);
		config_panel.add(permissionset);
		
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
		
		loadConfig(Config.selected.Name);
	}
	
	public void loadConfig(String buttonLabel) {
		Config c = Config.selected;
		
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
		profile.setText(c.SHEET_PROFILE);
		listview.setText(c.SHEET_LISTVIEW);
		permissionset.setText(c.SHEET_PS);
				
	}
}
