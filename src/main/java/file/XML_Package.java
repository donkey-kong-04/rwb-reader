package file;

import java.util.Set;

import java.util.TreeSet;

import org.w3c.dom.Node;

import utils.U;
import java.awt.Color;

public class XML_Package extends XML_File {
	public Set<String> p_recordTypes;
	public Set<String> p_profiles;
	public Set<String> p_layouts;
	public Set<String> p_application;
	public Set<String> p_pm;
	public Set<String> p_listviews;
	
	public XML_Package(String filename) {
		super("Package", filename, "unpackaged/");
		
		
		p_recordTypes = new TreeSet<String>();
		p_profiles = new TreeSet<String>();
		p_layouts = new TreeSet<String>();
		p_application = new TreeSet<String>();
		p_pm = new TreeSet<String>();
		p_listviews = new TreeSet<String>();
	}

	@Override
	public void buildFile() {
		Node version = this.file.createElement("version");
		
		this.root.appendChild(version).appendChild(this.file.createTextNode("58.0"));
		if(p_application.size() > 0)
			this.root.appendChild(buildType("CustomApplication", p_application));
		if(p_layouts.size() > 0)
			this.root.appendChild(buildType("Layout", p_layouts));
		if(p_listviews.size() > 0)
			this.root.appendChild(buildType("ListView", p_listviews));
		if(p_pm.size() > 0)
			this.root.appendChild(buildType("PermissionSet", p_pm));
		if(p_profiles.size() > 0) {
			
			this.root.appendChild(buildType("Profile", p_profiles));
		}
		if(p_recordTypes.size() > 0)
			this.root.appendChild(buildType("RecordType", p_recordTypes));
		
		
		
	}
	
	private Node buildType(String name, Set<String> members) {
		Node n = this.file.createElement("types");
		
		for(String m : members) {
			Node member = this.file.createElement("members");
			//System.out.println("Compare : " + m);
			
			if(U.doNotDeploy(m, "package.xml") == false) {
				n.appendChild(member).appendChild(this.file.createTextNode(m.replaceAll("/", "%2F")));
			}
			
		}
		Node nname = this.file.createElement("name");
		
		n.appendChild(nname).appendChild(this.file.createTextNode(name));
		return n;
	}
	
}
