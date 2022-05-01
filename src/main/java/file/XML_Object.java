package file;

import java.util.ArrayList;

import org.w3c.dom.Node;

public class XML_Object extends XML_File {
	
	public ArrayList<Node> recordTypes;
	public ArrayList<Node> listViews;
	
	public XML_Object(String filename) {
		super("CustomObject", filename, "unpackaged\\objects\\");
		
		listViews = new ArrayList<Node>();
		recordTypes = new ArrayList<Node>();
	}

	@Override
	public void buildFile() {
		
		for(int i=0; i<listViews.size(); i++) {
			this.root.appendChild(listViews.get(i));
		}
				
		for(int i=0; i<recordTypes.size(); i++) {
			this.root.appendChild(recordTypes.get(i));
		}
	}

}
