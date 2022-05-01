package file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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

import workbook.PRWorkbook;





public abstract class XML_File {
	
	public String location;
	public String filename;
	
	public Document file;
	protected Element root;
	
	
	public static String parseForPackage(String str) {
		return str.replaceAll("/", "%2F");
	}
	
	
	public XML_File(String strRoot, String filename, String location) {
		
		this.location = location;
		this.filename = XML_File.parseForPackage(filename);
		
		
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
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
        DOMSource domSource = new DOMSource(this.file);
        StreamResult streamResult;
	
		streamResult = new StreamResult(new File(folder + filename));
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
       
        transformer.transform(domSource, streamResult);
		

	}

	
	
	public abstract void buildFile();
}
