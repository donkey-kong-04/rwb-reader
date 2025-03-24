package step;

import java.awt.Color;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import file.XML_SharingRules;
import utils.U;
import workbook.PRWorkbook;

public class Step_SharingRule extends Step {

	@Override
	public void getSheetIDS(PRWorkbook w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getSteps() {
		return new String[] {"POSITION", "READ"};
	}

	@Override
	public Type runStep(PRWorkbook w) {

		this.doesStepExist(w);
		
		String stp = STEPS[STEP];
		
		if(stp.contentEquals("POSITION")) 
		{
			return runPOSITION(w);
		}
		else if(stp.contentEquals("READ")) 
		{
			return runREAD(w);
		}
		
		return Type.STOP;
	}

	private Type runPOSITION(PRWorkbook w) {
		String markup = U.getCell(w, 0);
		
		return markup.equalsIgnoreCase("Object API Name") ? Type.NEXT_STEP : Type.STAY_IN_SAME_STEP;
	}

	private Type runREAD(PRWorkbook w) {
		String objectApiName = U.getCell(w, 0);
		String sharingRuleName = U.getCell(w, 2);
		String sharingRuleApiName = U.getCell(w, 3);
		String sharingCriteria = U.getCell(w, 4);
		String sharingTo = U.getCell(w, 5);
		String accessType = U.getCell(w, 6);
		
		if(!U.isBlank(objectApiName) && !U.isBlank(sharingRuleApiName) && !U.isBlank(sharingCriteria) && !U.isBlank(sharingTo)) {
			XML_SharingRules f = (XML_SharingRules) w.getCorrectCorrectFile(XML_SharingRules.class, objectApiName + ".sharingRules");
			
			Node sharingCriteriaRule = f.file.createElement("sharingCriteriaRules");
			
			Node fullname = f.file.createElement("fullName");
			fullname.appendChild(f.file.createTextNode(sharingRuleApiName));
			sharingCriteriaRule.appendChild(fullname);
			
			Node access = f.file.createElement("accessLevel");
			access.appendChild(f.file.createTextNode(accessType));
			sharingCriteriaRule.appendChild(access);
			
			Node label = f.file.createElement("label");
			label.appendChild(f.file.createTextNode(sharingRuleName));
			sharingCriteriaRule.appendChild(label);
			
			Node shareTo = f.file.createElement("shareTo");
			ArrayList<ArrayList<String>> shareToParsed = this.parse(w, sharingTo);
			if(shareToParsed.size() != 1) {
				U.writeMsg("sharedTo should have only one element" + w.currentSheet.getSheetName(), Color.RED, true);
				
			}
			ArrayList<String> stp = shareToParsed.get(0);
			Node sharedTo = f.file.createElement("sharedTo");
			
			sharedTo.appendChild(f.file.createElement(stp.get(0))).appendChild(f.file.createTextNode(stp.get(1)));
			sharingCriteriaRule.appendChild(shareTo);
			
			
			
			Node logic = null;
			ArrayList<ArrayList<String>> nfilters = this.parse(w, sharingCriteria);
			ArrayList<Node> criterias = new ArrayList<Node>();
			for(ArrayList<String> nfilter : nfilters) {
				
				if(nfilter.get(0).equalsIgnoreCase("logic")) {
					logic = f.file.createElement("booleanFilter");
					logic.appendChild(f.file.createTextNode(nfilter.get(1)));
					sharingCriteriaRule.appendChild(logic);
				}
				else {
					Node criteria = f.file.createElement("criteriaItems");
					criteria.appendChild(f.file.createElement("field")).appendChild(f.file.createTextNode(nfilter.get(1)));
					criteria.appendChild(f.file.createElement("operation")).appendChild(f.file.createTextNode(nfilter.get(2)));
					criteria.appendChild(f.file.createElement("value")).appendChild(f.file.createTextNode(nfilter.get(3)));
					
					criterias.add(criteria);
				}
			}
			
			//Add it later in case they change the order in the excel
			for(Node c : criterias) {
				sharingCriteriaRule.appendChild(c);
			}
			
			f.n_sharingCriteriaRules.add(sharingCriteriaRule);
			
			
		}
		
		return Type.STAY_IN_SAME_STEP;
	}
	

	@Override
	public void debugStepNotPassed(PRWorkbook w) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isCorrectSheet(PRWorkbook w, String sheetName) {
		return sheetName.equalsIgnoreCase(w.c.SHEET_SHARING_RULES);
	}
	
	
}
