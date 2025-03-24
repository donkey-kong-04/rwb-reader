package file;

import java.util.ArrayList;

import org.w3c.dom.Node;

public class XML_PermissionSet extends XML_File {
	
	
	
	public String label;
	public Node license;
	
	public XML_PermissionSet(String filename) {
		
		super("PermissionSet", filename, "unpackaged/permissionsets/");
		this.label = filename.replaceAll("\\.permissionset", "").replaceAll("_", " ");
	}

	@Override
	public void buildFile() {
		//System.out.println("BEGIN");
		Node nodeLabel = this.file.createElement("label");
		
		this.root.appendChild(nodeLabel).appendChild(file.createTextNode(label));
		// TODO Auto-generated method stub
		
		//System.out.println("Label:" + label);
		for(int i=0; i<fieldPerms.size(); i++) {
			this.root.appendChild(fieldPerms.get(i));
			//System.out.println("fieldPerms:" + fieldPerms.get(i).getTextContent());
		}
		
		for(int i=0; i<tabSettings.size(); i++) {
			this.root.appendChild(tabSettings.get(i));
			//System.out.println("tabSettings:" + tabSettings.get(i).getTextContent());
		}
		
		System.out.println("SIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIZE : " + applicationVisibilities.size());
		for(int i=0; i<applicationVisibilities.size(); i++) {
			this.root.appendChild(applicationVisibilities.get(i));
			
		}
		
		for(int i=0; i<objectPerms.size(); i++) {
			this.root.appendChild(objectPerms.get(i));
			//System.out.println("objectPerms:" + objectPerms.get(i).getTextContent());
		}
		
		for(int i=0; i<rtvPerms.size(); i++) {
			
			this.root.appendChild(rtvPerms.get(i));
			//System.out.println("rtvPerms:" + rtvPerms.get(i).getTextContent());
		}
		
		if(license != null) {
			this.root.appendChild(license);
		}
		

		for(int i=0; i<userPermissions.size(); i++) {
			this.root.appendChild(this.userPermissions.get(i));
			
		}
		//System.out.println("END");
		
	}

}
