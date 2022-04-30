package step;

import java.util.ArrayList;

import org.w3c.dom.Node;

import file.XML_Profile;
import utils.PRUtil;
import workbook.PRWorkbook;

public class Step_ApexComponents extends Step {
	

	@Override
	public String[] getSteps() {
		// TODO Auto-generated method stub
		return new String[] {"PROFILE_INDEX", "PROFILE_POSITION", "VF", "APEX", "FINISH"};
	}

	@Override
	public Type runStep(PRWorkbook w) {
		this.doesStepExist(w);
		
		String theStep = STEPS[STEP];
		
		if(theStep.equals("PROFILE_INDEX")) {
			return runPROFILE_INDEX(w);
		} else if(theStep.equals("PROFILE_POSITION")) {
			return runPROFILE_POSITION(w);
		} else if(theStep.equals("VF")) {
			return runVF(w);
		} else if(theStep.equals("APEX")) {
			return runAPEX(w);
		} else if(theStep.equals("FINISH")) {
			//do nothing
		}
		
		return Type.STOP;
	}

	private Type runAPEX(PRWorkbook w) {
		String text = PRUtil.getCell(w, 0);
		
		boolean isHidden = w.currentRow.getZeroHeight();
		if(text.equalsIgnoreCase("Apex Trigger")) {
			return Type.NEXT_STEP;
		} else {
			Integer numberOfHiddenColum = 0;
			for(int i=index_start; i<=index_end; i++) {
				boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
				if(isColumnHidden == false) {	
					String currentId = IDS.get(i - index_start - numberOfHiddenColum);
					
					XML_Profile f = (XML_Profile) w.getCorrectCorrectFile(XML_Profile.class, currentId, isColumnHidden);
					
					String enable = PRUtil.getCell(w, i);
					
					
					Node apexPerm = f.file.createElement("classAccesses");
					
					Node apexPage =  f.file.createElement("apexClass"),
							enabled = f.file.createElement("enabled");
					
					apexPerm.appendChild(apexPage).appendChild(f.file.createTextNode(text.trim()));
					
					
					if(isHidden) {
						apexPerm.appendChild(enabled).appendChild(f.file.createTextNode("false"));
					} else {
						apexPerm.appendChild(enabled).appendChild(f.file.createTextNode(enable.equalsIgnoreCase("Yes") ? "true" : "false"));
					}
					
					f.apexPerms.add(apexPerm);
				} else {
					numberOfHiddenColum ++;
				}
			}
			return Type.STAY_IN_SAME_STEP;
		}
	
		
	}

	private Type runVF(PRWorkbook w) {
		String text = PRUtil.getCell(w, 0);
		
		boolean isHidden = w.currentRow.getZeroHeight();
		
		if(text.trim().equalsIgnoreCase("Apex Class")) {
			return Type.NEXT_STEP;
		} else if(!text.trim().equalsIgnoreCase("VF Page")){
			Integer numberOfHiddenColum = 0;
			for(int i=index_start; i<=index_end; i++) {
				int tabIndex = i - index_start - numberOfHiddenColum;
				
				boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
				if(isColumnHidden == false) {	
					String currentId = IDS.get(tabIndex);
					
					XML_Profile f = (XML_Profile) w.getCorrectCorrectFile(XML_Profile.class, currentId, isColumnHidden);
					String enable = PRUtil.getCell(w, i);
					
					Node vfPerm = f.file.createElement("pageAccesses");
					
					Node apexPage =  f.file.createElement("apexPage"),
							enabled = f.file.createElement("enabled");
					
					vfPerm.appendChild(apexPage).appendChild(f.file.createTextNode(text.trim()));
					
					if(isHidden) {
						vfPerm.appendChild(enabled).appendChild(f.file.createTextNode("false"));
					} else {
						vfPerm.appendChild(enabled).appendChild(f.file.createTextNode(enable.equalsIgnoreCase("Yes") ? "true" : "false"));
					}
					
					f.vfPerms.add(vfPerm);
				} else {
					numberOfHiddenColum ++;
				}
			}
			
		}
	
		return Type.STAY_IN_SAME_STEP;
	}
	

	private Type runPROFILE_INDEX(PRWorkbook w) {
		//System.out.println("enter run_PROFILE_iNDEX");
		
		this.getIndexes(w, "Profiles");
		
		boolean success = (this.index_start != -1 && this.index_end != -1);
		
		if(!success) {
			PRUtil.fatal(w, "MARKUP MISSING : 'Profiles' markup should be found in apex component's sheet");
		}
		
		//PRUtil.debug(s, index_start + " " + index_end, false);
		return success ? Type.NEXT_STEP : Type.STOP;
	}

	

	private Type runPROFILE_POSITION(PRWorkbook w) {
		//PRUtil.debug(s, "runPROFILE_POSITION", false);
		//w.modifyIndexes(w.profiles, this.index_start, this.index_end, "Profile", "profile", ".\\unpackaged\\profiles\\", XML_File.TYPE_BASIC, null);
		this.getSheetIDS(w);
		return Type.NEXT_STEP;
	}

	@Override
	public boolean isCorrectSheet(PRWorkbook w, String sheetName) {
		// TODO Auto-generated method stub
		return sheetName.equalsIgnoreCase(w.c.SHEET_APEX);
	}

	@Override
	public void debugStepNotPassed(PRWorkbook w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getSheetIDS(PRWorkbook w) {
		IDS = new ArrayList<String>();
		headers = new ArrayList<String>();
		
		this.reviewEndingIndex(w);
		
		for(int i=index_start; i<=index_end; i++) {
			String header = PRUtil.getCell(w, i);
			
			String filename = header + ".profile";
			boolean isColumnHidden = w.currentSheet.isColumnHidden(i);
			if(isColumnHidden == false) {
				headers.add(header);
				IDS.add(filename);
			} else {
				System.out.println("HIDDEN PROFILE IN APEX COMPONENTS - " + header);
			}
			
		}
	}


	

}
