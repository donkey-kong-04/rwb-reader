package file;

import java.util.ArrayList;

import org.w3c.dom.Node;

public class XML_SharingRules extends XML_File {
	
	public ArrayList<Node> n_sharingCriteriaRules;
	
	public XML_SharingRules(String filename) {
		super("SharingRules", filename, "unpackaged\\sharingRules\\");
		
		n_sharingCriteriaRules = new ArrayList<Node>();
	}

	@Override
	public void buildFile() {
		
		for(Node n : n_sharingCriteriaRules) {
			root.appendChild(n);
		}
	}

}
