package step;

import java.awt.Color;

import org.apache.poi.ss.usermodel.Cell;
import org.w3c.dom.Node;

import file.XML_Profile;
import utils.PRUtil;
import workbook.PRWorkbook;

public class Step_ProfileLicense extends Step {

	@Override
	public void getSheetIDS(PRWorkbook w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getSteps() {
		// TODO Auto-generated method stub
		return new String[] {"INDEX", "READ"};
	}

	@Override
	public Type runStep(PRWorkbook w) {
		// TODO Auto-generated method stub
		
		String stp = STEPS[STEP];
		if(stp.equals("INDEX")) {
			return run_index(w);
		} else if(stp.equals("READ")) {
			return run_read(w);
		}
		return Type.STOP;
	}
	
	
	private Type run_read(PRWorkbook w) {
		String profileName = PRUtil.getCell(w, 0);
		String licenseName = PRUtil.getCell(w, 1);
		
		if(profileName.trim().equals("")) {
			return Type.STOP;
		} else {
			String profileID = profileName + ".profile";
			XML_Profile f = (XML_Profile) w.getCorrectCorrectFile(XML_Profile.class, profileID);
			f.license = licenseName;
			if(PRUtil.isBlank(licenseName)) {
				PRUtil.writeMsg("License Name cannot be empty - " + w.currentSheet.getSheetName(), Color.RED, true);
			}
		}
		return null;
	}

	private Type run_index(PRWorkbook w) {
		String rowHeader = PRUtil.getCell(w, 0);
		
		if(rowHeader.equalsIgnoreCase("salesforce profile")) {
			return Type.NEXT_STEP;
		} else {
			PRUtil.writeMsg("A1 should contains 'Salesforce Profile' - " + w.currentSheet.getSheetName(), Color.RED, true);
		}
		return Type.STOP;
	}

	@Override
	public boolean isCorrectSheet(PRWorkbook w, String sheetName) {
		// TODO Auto-generated method stub
		return sheetName.equalsIgnoreCase(w.c.PROFILE_LICENSES);
	}

	@Override
	public void debugStepNotPassed(PRWorkbook w) {
		// TODO Auto-generated method stub
		
	}

}
