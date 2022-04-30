package workbook;

import javax.xml.transform.TransformerException;



public class App {

	public static void main(String[] args) {
		System.setProperty("line.separator", "\n");
		
		PRWorkbook w = new PRWorkbook();
		
		w.read();
		
		w.postCheck();
		try {
			w.writeFiles();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		w.end();
		System.out.println("END");
	}

}
