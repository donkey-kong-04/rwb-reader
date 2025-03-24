package step;

import java.awt.Color;
import java.util.ArrayList;

import org.w3c.dom.Node;

import file.XML_Object;
import utils.U;
import workbook.PRWorkbook;

public class Step_ListView extends Step {

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
		if(stp.equals("POSITION")) 
		{
			return runPOSITION(w);
		}
		else if(stp.equals("READ")) 
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
		
		String objectName = U.getCell(w, 0);
		String label = U.getCell(w, 1);
		String apiName = U.getCell(w, 2);
		String sharing = U.getCell(w, 3);
		String filters = U.getCell(w, 4);
		String fields = U.getCell(w, 5);
		
		System.out.println(objectName);
		System.out.println(apiName);
		System.out.println(label);
		System.out.println(filters);
		System.out.println(sharing);
		System.out.println(filters);
		
		if(!U.isBlank(objectName) && !U.isBlank(label) && !U.isBlank(apiName) && !U.isBlank(sharing) && !U.isBlank(fields)) {
			w.fpackage.p_listviews.add(objectName + "." + apiName);
			
			XML_Object f = (XML_Object) w.getCorrectCorrectFile(XML_Object.class, objectName + ".object");
			
			Node listView = f.file.createElement("listViews");
			
			
			
			listView.appendChild(f.file.createElement("fullName")).appendChild(f.file.createTextNode(apiName));
			ArrayList<Node> nfilters = null;
			if(!U.isBlank(filters)) {
				Node logic = null;
				nfilters = buildFilter(w, f, filters, logic);
				
				if(logic != null) {
					listView.appendChild(logic);
				} 
			}
			for(String s : fields.split(",")) {
				listView.appendChild(f.file.createElement("columns")).appendChild(f.file.createTextNode(s.trim()));
			}
			listView.appendChild(f.file.createElement("filterScope")).appendChild(f.file.createTextNode("Everything"));
			if(!U.isBlank(filters)) {
				for(Node n : nfilters) {
					listView.appendChild(n);
				}
			}
			listView.appendChild(f.file.createElement("label")).appendChild(f.file.createTextNode(label));
			listView.appendChild(f.file.createElement("language")).appendChild(f.file.createTextNode("en_US"));
			
			//Add sharing logic and filters logics
			if(!U.isBlank(sharing)) {
				Node shareTo = f.file.createElement("sharedTo");
				for(ArrayList<String> share : this.parse(w, sharing)) {
					
					shareTo.appendChild(f.file.createElement(share.get(0))).appendChild(f.file.createTextNode(share.get(1)));
				}
				listView.appendChild(shareTo);
			}
			
			
			f.listViews.add(listView);
		} else {
			U.writeMsg("Mandatory information missing in ListView: " + apiName, Color.RED, true);
			
		}
		return Type.STAY_IN_SAME_STEP;
	}

	@Override
	public void debugStepNotPassed(PRWorkbook w) {
		String stp = STEPS[STEP];
		
		if(stp.equals("POSITION")) {
			U.writeMsg("MARKUP MISSING : 'Object API Name' in List views has not been found in column A - " + w.currentSheet.getSheetName(), Color.RED, true);
			
		}
	}
	
	@Override
	public boolean isCorrectSheet(PRWorkbook w, String sheetName) {
		return sheetName.equalsIgnoreCase(w.c.SHEET_LISTVIEW);
	}
	
	
	
	public ArrayList<Node> buildFilter(PRWorkbook w, XML_Object f, String filters, Node logic) {
		ArrayList<Node> nfilters = new ArrayList<Node>();
		
		System.out.println(this.parse(w, filters));
		
		
		for(ArrayList<String> filter : this.parse(w, filters)) {
			if(filter.get(0).equalsIgnoreCase("logic")) {
				logic = f.file.createElement("booleanFilter");
				logic.appendChild(f.file.createTextNode(filter.get(1)));
			} else {
				Node nfilter = f.file.createElement("filters");
				nfilter.appendChild(f.file.createElement("field")).appendChild(f.file.createTextNode(filter.get(1)));
				nfilter.appendChild(f.file.createElement("operation")).appendChild(f.file.createTextNode(filter.get(2)));
				String value = "";
				for(int i=3; i<filter.size(); i++) {
					value += filter.get(i) + ((i == (filter.size()-1)) ? "" : ",");
				}
				nfilter.appendChild(f.file.createElement("value")).appendChild(f.file.createTextNode(value));
				nfilters.add(nfilter);
			}
			
		}
		
		return nfilters;
	}
}
