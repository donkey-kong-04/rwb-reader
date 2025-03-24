package file;


import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import utils.U;






public abstract class XML_File {
	
	public String location;
	public String filename;
	
	public Document file;
	public Element root;
	
	
	
	public ArrayList<Node> rtvPerms = new ArrayList<Node>();
	public ArrayList<Node> objectPerms = new ArrayList<Node>();
	public ArrayList<Node> fieldPerms = new ArrayList<Node>();
	public ArrayList<Node> apexPerms = new ArrayList<Node>();
	public ArrayList<Node> vfPerms = new ArrayList<Node>();
	public ArrayList<Node> applicationVisibilities = new ArrayList<Node>();
	public ArrayList<Node> tabSettings = new ArrayList<Node>();
	public ArrayList<Node> userPermissions = new ArrayList<Node>();
	
	public static String parseForPackage(String str) {
		return str.replaceAll("/", "%2F");
	}
	
	
	public XML_File(String strRoot, String filename, String location) {
		
		this.location = location;
		this.filename = XML_File.parseForPackage(filename);
		
		System.out.println(filename);
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentFactory.newDocumentBuilder();
			file = documentBuilder.newDocument();
			root = this.file.createElement(strRoot);
			
			
			
			Attr attr = this.file.createAttribute("xmlns");
            attr.setValue("http://soap.sforce.com/2006/04/metadata");
			this.root.setAttributeNode(attr);
			
			this.file.appendChild(this.root);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			
			U.writeMsg("Exception: " + e.getMessage(), Color.RED, true);
			System.exit(1);
		}
	}
	
	
	public static void writeCSV(String fullpath, String content) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(fullpath, "UTF-8");
			writer.println(content);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			U.writeMsg("Exception: " + e.getMessage(), Color.RED, true);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			U.writeMsg("Exception: " + e.getMessage(), Color.RED, true);
		}
		
	}
	public void write(String filename, String folder) throws TransformerException {
		
		this.buildFile();
		
		//System.out.println(folder + filename);
		File f = new File(folder);
		//System.out.println(folder);
		//System.out.println(filename);
		if(!f.exists()) {
			f.mkdir();
		}
		//System.out.println("Try to write : " + path);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        //System.out.println(this.file.getTextContent());
        DOMSource domSource = new DOMSource(this.file);
        StreamResult streamResult;
	
		streamResult = new StreamResult(new File(folder + filename));
		//System.out.println(folder + filename);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        
        transformer.transform(domSource, streamResult);
		

	}

	
	
	public abstract void buildFile();
}
