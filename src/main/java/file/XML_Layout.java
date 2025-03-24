package file;

import java.util.ArrayList;

import org.w3c.dom.Node;

public class XML_Layout extends XML_File {
	
	public ArrayList<Node> sections;
	public Node currentLayoutColumn;//left column
	public Node currentLayoutColumn2;//right column
	public boolean oneColumn;
	public Node relatedObjects;
	
	public ArrayList<Node> relatedLists;
	public Node currentRelatedList;
	
	public XML_Layout(String filename) {
		
		super("Layout", filename, "unpackaged/layouts/");
		
		this.relatedObjects = this.file.createElement("relatedObjects");
		this.sections = new ArrayList<Node>();
		this.relatedLists = new ArrayList<Node>();
	}

	@Override
	public void buildFile() {
		
		for(int i=0; i<sections.size(); i++) {
			this.root.appendChild(sections.get(i));
		}
		
		
		for(int i=0; i<relatedLists.size(); i++) {
			
			root.appendChild(relatedLists.get(i));
		}
		
		if(this.relatedObjects.hasChildNodes()) {
			root.appendChild(relatedObjects);
		}
	}

}
