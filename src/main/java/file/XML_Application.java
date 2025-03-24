package file;

import java.util.ArrayList;

import org.w3c.dom.Node;

public class XML_Application extends XML_File {
	
	public ArrayList<Node> tabs;
	public String appName;
	
	public XML_Application(String filename) {
		super("CustomApplication", filename, "unpackaged/applications/");
		
		this.tabs = new ArrayList<Node>();
	}

	@Override
	public void buildFile() {
		// TODO Auto-generated method stub
		root.appendChild(file.createElement("defaultLandingTab"))
					.appendChild(file.createTextNode("standard-home"));
		root.appendChild(file.createElement("description"))
					.appendChild(file.createTextNode(""));
		root.appendChild(file.createElement("formFactors"))
					.appendChild(file.createTextNode("Large"));
		root.appendChild(file.createElement("isNavAutoTempTabsDisabled"))
					.appendChild(file.createTextNode("false"));
		root.appendChild(file.createElement("isNavPersonalizationDisabled"))
					.appendChild(file.createTextNode("false"));
		root.appendChild(file.createElement("label"))
					.appendChild(file.createTextNode(appName));
		
		
		for(int i=0; i<tabs.size(); i++) {
			root.appendChild(tabs.get(i));
		}
	}

}
