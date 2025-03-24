package file;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import utils.U;


public class XML_Profile extends XML_File {
	
	public ArrayList<Node> layoutPerms = new ArrayList<Node>();
	public Node userLicense;
	public Node custom;
	public XML_Profile(String filename) {
		super("Profile", filename, "unpackaged/profiles/");
		
	}
	
	public static String buildCopadoFile(List<XML_File> profiles) {
		Set<String> components = new HashSet<String>();
		/*
		 * t = Type (ApexClass)
		 * n = Name (EUR_CRM_TerritoryClass)
		 * s = selected = false always
		 * r = retrieve only = true always
		 * b = last modified by id (empty we don't care)
		 * d = last modified date (empty we don't care)
		 * cb = created by id (empty we don't care)
		 * cd = created date  (empty we don't care)
		 * 
		 */
		String SEP = ",";
		String json = "[";
		ArrayList<String> jsonRepresentation = new ArrayList<String>();
		
		for(int i=0; i<profiles.size(); i++) {
			String filename = profiles.get(i).filename;
			//The profiles to ignore contains only the name of the profile, so we need to remove before comparing
			if(U.doNotDeploy(filename.replace(".profile", ""), "copado") == false) {
				jsonRepresentation.add("{\"t\":\"Profile\",\"n\":\"" + filename.replace(".profile", "") + "\",\"s\":true,\"r\":false,\"b\":\"\",\"d\":\"\",\"cb\":\"\",\"cd\":\"\"}");
				
			}
		}
		
		for(int j=0; j<profiles.size(); j++) {
			XML_Profile p = (XML_Profile) profiles.get(j);
			
			for(int i=0; i<p.applicationVisibilities.size(); i++) {
				Element e = (Element) p.applicationVisibilities.get(i);
				
				addPermissionToCopadoFile(components, "CustomApplication", e.getElementsByTagName("application").item(0).getTextContent().trim(), SEP, jsonRepresentation);
				
			}
			
			
			for(int i=0; i<p.fieldPerms.size(); i++) {
				Element e = (Element) p.fieldPerms.get(i);
				addPermissionToCopadoFile(components, "CustomField", e.getElementsByTagName("field").item(0).getTextContent().trim(), SEP, jsonRepresentation);
				
			}
			
			for(int i=0; i<p.layoutPerms.size(); i++) {
				Element e = (Element) p.layoutPerms.get(i);
				addPermissionToCopadoFile(components, "Layout", e.getElementsByTagName("layout").item(0).getTextContent().trim(), SEP, jsonRepresentation);
				addPermissionToCopadoFile(components, "RecordType", e.getElementsByTagName("recordType").item(0).getTextContent().trim(), SEP, jsonRepresentation);
				
			}
			
			for(int i=0; i<p.objectPerms.size(); i++) {
				Element e = (Element) p.objectPerms.get(i);
				addPermissionToCopadoFile(components, "CustomObject", e.getElementsByTagName("object").item(0).getTextContent().trim(), SEP, jsonRepresentation);
				
			}
			
			for(int i=0; i<p.vfPerms.size(); i++) {
				Element e = (Element) p.vfPerms.get(i);
				addPermissionToCopadoFile(components, "ApexPage", e.getElementsByTagName("apexPage").item(0).getTextContent().trim(), SEP, jsonRepresentation);
				
			}
			
			for(int i=0; i<p.rtvPerms.size(); i++) {
				Element e = (Element) p.rtvPerms.get(i);
				addPermissionToCopadoFile(components, "RecordType", e.getElementsByTagName("recordType").item(0).getTextContent().trim(), SEP, jsonRepresentation);
				
			}
			
			for(int i=0; i<p.apexPerms.size(); i++) {
				Element e = (Element) p.apexPerms.get(i);
				addPermissionToCopadoFile(components, "ApexClass", e.getElementsByTagName("apexClass").item(0).getTextContent().trim(), SEP, jsonRepresentation);
				
			}
		}
		
		json += String.join(",", jsonRepresentation);
		json += "]";
		return json;
	}
	
	@Override
	public void buildFile() {
		
		if(custom != null) {
			this.root.appendChild(custom);
		}
		
		if(userLicense != null) {
			this.root.appendChild(userLicense);
		}
		
		for(int i=0; i<applicationVisibilities.size(); i++) {
			this.root.appendChild(this.applicationVisibilities.get(i));
			
		}
		
		for(int i=0; i<userPermissions.size(); i++) {
			this.root.appendChild(this.userPermissions.get(i));
			
		}
		
		for(int i=0; i<apexPerms.size(); i++) {
			this.root.appendChild(this.apexPerms.get(i));
		}
		
		
		/*
		Node description = this.file.createElement("description");
		this.root.appendChild(description).appendChild(this.file.createTextNode(""));
		*/
		
		for(int i=0; i<fieldPerms.size(); i++) {
			this.root.appendChild(this.fieldPerms.get(i));
		}
		
		for(int i=0; i<layoutPerms.size(); i++) {
			this.root.appendChild(layoutPerms.get(i));
		}
		
		for(int i=0; i<objectPerms.size(); i++) {
			this.root.appendChild(objectPerms.get(i));
		}
		
		for(int i=0; i<vfPerms.size(); i++) {
			this.root.appendChild(vfPerms.get(i));
		}
		
		for(int i=0; i<rtvPerms.size(); i++) {
			this.root.appendChild(rtvPerms.get(i));
		}
		
		
		
		
		
	}
	
	private static void addPermissionToCopadoFile(Set<String> components, String type, String component, String SEP, ArrayList<String> jsonList) {
		String key = type + "-" + component;
		
		if(!components.contains(key)) {
			components.add(key);
			
			jsonList.add("{\"t\":\"" + type + "\",\"n\":\"" + component.replaceAll("\\(", "%28").replaceAll("\\)", "%29") + "\",\"s\":true,\"r\":true,\"b\":\"\",\"d\":\"\",\"cb\":\"\",\"cd\":\"\"}");
		}
	}
	
}
