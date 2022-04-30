package excel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

abstract class Esheet {
	protected Sheet s;
	
	protected Row r;
	protected int r_index;
	
	protected Cell c;
	protected int c_index;
	
	protected String last_header;
	protected HashMap<String, Integer> headerBegin;
	protected HashMap<String, Integer> headerEnd;
	
	protected HashMap<Integer, String> position;
	
	public Esheet(Sheet sh) {
		s = sh;
		r_index = 0;
		c_index = 0;
		headerBegin = new HashMap<String, Integer>();
		headerEnd = new HashMap<String, Integer>();
		position = new HashMap<Integer, String>();
	}
	
	public Row nextRow() {
		c = null;
		if(r == null) {
			r = s.getRow(r_index);
		} else {
			r_index ++;
			r = s.getRow(r_index);
		}
		
		return r;
	}
	
	public String getSpecificCell(int index) {
		
		Cell specificCell = r.getCell(index);
		
		return specificCell == null ? null : specificCell.getStringCellValue();
	}
	
	public String nextCell() {
		if(c == null) {
			c = r.getCell(c_index);
		} else {
			c_index ++;
			c = r.getCell(c_index);
		}
		
		return c == null ? null : c.getStringCellValue();
	}
	
	public void savePosition(String header, Integer pos) {
		Cell cell = r.getCell(pos);
		if(cell != null) {
			String value = cell.getStringCellValue();
			if(value != null) {
				position.put(c_index, header + ":" + value);
			}
		}
	}
	
	public void goToMarker(String marker) {
		boolean found = false;
		while(found == false) {
			nextRow();
			String currentContent = nextCell();
			
			if(currentContent == null) {
				System.out.println("Market not found : " + marker);
				System.exit(0);
			} else if(currentContent.equalsIgnoreCase(marker)) {
				found = true;
				System.out.println("Marker: " + marker);
			}
		}
	}
	public String getPosition(Integer pos) {
		String value = position.get(pos).split(":")[1];
		return value;
	}
	
	public void saveHeader(String value) {
		if(last_header != null) {
			closeHeader();
		}
		last_header = value;
		headerBegin.put(value, c_index);
	}
	public void closeHeader() {
		headerEnd.put(last_header, c_index - 1);
	}
	
	public Integer getBegin(String header) {
		return headerBegin.get(header);
	}
	public Integer getEnd(String header) {
		return headerEnd.get(header);
	}
	public abstract void reading();
}
