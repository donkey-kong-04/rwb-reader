package step;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.w3c.dom.Node;

import file.XML_Profile;
import step.Step.Type;
import utils.U;
import workbook.PRWorkbook;



public class Step_Profile extends Step {

	@Override
	public void getSheetIDS(PRWorkbook w) {
		// TODO Auto-generated method stub
		IDS = new ArrayList<String>();
		headers = new ArrayList<String>();
		
		this.reviewEndingIndex(w);
		
		for(int i=index_start; i<=index_end; i++) {
			boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
			
			String header = U.getCell(w, i);
			if(isColumnHidden == false) {	
				String filename = header + ".profile";
				IDS.add(filename);
				headers.add(header);
			
			
				w.fpackage.p_profiles.add(header);
			} else {
				U.writeMsg("HIDDEN PROFILE " + header + " - "  + w.currentSheet.getSheetName(), Color.ORANGE, false);
				
			}
		}
	}

	@Override
	public String[] getSteps() {
		// TODO Auto-generated method stub
		return new String[] {"INDEX", "POSITION", "PERMISSIONS"};
	}
	
	
	
	@Override
	public Type runStep(PRWorkbook w) {
		// TODO Auto-generated method stub
		
		String stp = STEPS[STEP];
		if(stp.equals("INDEX")) {
		
			return run_index(w);
		} else if(stp.equals("POSITION")) {
			
			runPROFILE_POSITION(w);
			return Type.NEXT_STEP;
		} else if(stp.equals("PERMISSIONS")) {
			
			return run_read(w);
		}
		return Type.STOP;
	}
	
	private void runPROFILE_POSITION(PRWorkbook w) 
	{
		//w.Indexes(w.profiles, this.index_start, this.index_end, "Profile", "profile", ".\\unpackaged\\profiles\\", XML_File.TYPE_BASIC, null);
		this.getSheetIDS(w);
		
	}
	
	private Type run_read(PRWorkbook w) {
		String type = U.getCell(w, 0).trim();
		String apiname = U.getCell(w, 1).trim() ;
		
		String[] typeAllowed = {"userPermissions", 
				"tabSettings", 
				"applicationVisibilities", 
				"apexClass", 
				"apexPage",
				"userLicense",
				"custom"
			};
		
		 
		if(type.trim().equals("")) {
			return Type.STOP;
		} else {
			boolean found = false;
	        for (String str : typeAllowed) {
	            if (str.equals(type)) {
	                found = true;
	                break;
	            }
	        }

	        if (!found) {
	        	U.writeMsg("Type not recognized '" + type + "' " + w.currentSheet.getSheetName(), Color.RED, true);
	        }
	        
	        for(int i=index_start; i<=index_end; i++) {
				int tabIndex = i - index_start;
				
				boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
				if(isColumnHidden == false) {
					String apiName = headers.get(tabIndex);
					String fullFileName = IDS.get(tabIndex);
					
					XML_Profile f = (XML_Profile) w.getCorrectCorrectFile(XML_Profile.class, fullFileName);
					w.fpackage.p_profiles.add(apiName);
					//System.out.println(index_start + " " + index_end);
					String value = w.currentRow.getCell(i).getStringCellValue().trim();
					
					if(U.isBlank(value) == false) {//Do something only if we have explicit value
						if(type.equals("custom")) {
							
							Node custom = f.file.createElement("custom");
							custom.appendChild(f.file.createTextNode(value));
							f.custom = custom;
						} else if(type.equals("userLicense")) {
							Node userLicense = f.file.createElement("userLicense");
							userLicense.appendChild(f.file.createTextNode(value));
							
							f.userLicense = userLicense;
							
						} else if(type.equals("userPermissions")) {
							
							
							Node up = f.file.createElement("userPermissions");
							Node
								enabled = f.file.createElement("enabled"),
								name = f.file.createElement("name");
							
							up.appendChild(enabled).appendChild(f.file.createTextNode(value));
							up.appendChild(name).appendChild(f.file.createTextNode(apiname));
							
							f.userPermissions.add(up);
						} else if(type.equals("tabSettings")) {
							
							Node tabSettings = f.file.createElement("tabSettings");
							Node 
								tab = f.file.createElement("tab"),
								visibility = f.file.createElement("visibility");
							
							tabSettings.appendChild(tab).appendChild(f.file.createTextNode(apiname.trim()));
							tabSettings.appendChild(visibility).appendChild(f.file.createTextNode(value.trim()));
							
							f.tabSettings.add(tabSettings);
							
						} else if(type.equals("applicationVisibilities")) {
							
							Node applicationVisibilities = f.file.createElement("applicationVisibilities");
							
							Node application = f.file.createElement("application"),
									ndefault = f.file.createElement("default"),
									visible = f.file.createElement("visible");
							
							applicationVisibilities.appendChild(application).appendChild(f.file.createTextNode(apiname));
							applicationVisibilities.appendChild(ndefault).appendChild(f.file.createTextNode("false"));
							applicationVisibilities.appendChild(visible).appendChild(f.file.createTextNode(value));
							
							f.applicationVisibilities.add(applicationVisibilities);
							
						} else if(type.equals("apexClass")) {
							
							Node apexPerm = f.file.createElement("classAccesses");
							
							Node 
								apexPage =  f.file.createElement("apexClass"),
								enabled = f.file.createElement("enabled");
							
							apexPerm.appendChild(apexPage).appendChild(f.file.createTextNode(apiname.trim()));
							apexPerm.appendChild(enabled).appendChild(f.file.createTextNode(value));
							
							f.apexPerms.add(apexPerm);
							
						} else if(type.equals("apexPage")) {
							
							Node vfPerm = f.file.createElement("pageAccesses");
							
							Node 
								apexPage =  f.file.createElement("apexPage"),
								enabled = f.file.createElement("enabled");
							
							vfPerm.appendChild(apexPage).appendChild(f.file.createTextNode(apiname.trim()));
							vfPerm.appendChild(enabled).appendChild(f.file.createTextNode(value));
							
							
							f.vfPerms.add(vfPerm);
							
						} else {
						
						}
					}
				}
	        }
		}
		return Type.STAY_IN_SAME_STEP;
	}

	private Type run_index(PRWorkbook w) {
		
		this.getIndexes(w, "toto");

		
		boolean success = (this.index_start != -1 && this.index_end != -1);
		
		return success ? Type.NEXT_STEP : Type.STOP;
	}

	@Override
	public boolean isCorrectSheet(PRWorkbook w, String sheetName) {
		// TODO Auto-generated method stub
		return sheetName.equalsIgnoreCase(w.c.SHEET_PROFILE);
	}

	@Override
	public void debugStepNotPassed(PRWorkbook w) {
		// TODO Auto-generated method stub
		
	}
	
	
}
