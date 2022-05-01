package file;

import java.util.ArrayList;

import org.w3c.dom.Node;

public class XML_PermissionSet extends XML_File {
	
	public ArrayList<Node> objectPerms;
	public ArrayList<Node> fieldPerms;
	public ArrayList<Node> tabSettings;
	
	public String label;
	
	
	public XML_PermissionSet(String filename) {
		
		super("PermissionSet", filename, "unpackaged\\permissionsets\\");
		
		fieldPerms = new ArrayList<Node>();
		objectPerms = new ArrayList<Node>();
		tabSettings = new ArrayList<Node>();
	}

	@Override
	public void buildFile() {
		Node lab = this.file.createElement("label");
		
		System.out.println("Label: " + label);
		this.root.appendChild(lab).appendChild(file.createTextNode(label));
		// TODO Auto-generated method stub
		
		
		for(int i=0; i<fieldPerms.size(); i++) {
			this.root.appendChild(fieldPerms.get(i));
		}
		
		for(int i=0; i<tabSettings.size(); i++) {
			this.root.appendChild(tabSettings.get(i));
		}
		
		for(int i=0; i<objectPerms.size(); i++) {
			this.root.appendChild(objectPerms.get(i));
		}
	}

}
