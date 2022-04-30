package excel;

import org.apache.poi.ss.usermodel.Sheet;

public class ESheetObject extends Esheet {

	public ESheetObject(Sheet sh) {
		super(sh);
		
	}

	@Override
	public void reading() {
		
		
		this.nextRow();
		
		/*
		 * Read object name
		 */
		
		String object = this.nextCell().split(":")[1];
		
		/*
		 * Read position of Profile, Layout & Permission set
		 */
		String header = this.nextCell();
		while(header != null) {
			header = header.trim();
			if(!header.equalsIgnoreCase("")) {
				this.saveHeader(header);
			}
			header = this.nextCell();
			if(header == null) {
				this.closeHeader();
			}
		}
		
		/*
		 * Read what we expect
		 */
		this.nextRow();
		this.nextCell();
		
		//Save header position
		String headers[] = {"Page Layouts", "Profiles", "Permission Sets", "Record Types" };
		for(String h : headers) {
			for(Integer i=this.getBegin(h); i<=this.getEnd(h); i++) {
				this.savePosition(h, i);
			}
		}
		
		goToMarker("Object Permissions");
		for(Integer i=this.getBegin("Profiles"); i<=this.getEnd("Profiles"); i++) {
			//Treat object permission for each value
			String profileName = this.getPosition(i);
			String objectPerm = this.getSpecificCell(i);
			System.out.println(profileName + " : " + objectPerm);
		}
		//Now read field info
		nextRow();
		String cellValue = nextCell();
		while(cellValue.equalsIgnoreCase("Field")) {
			nextRow();
			cellValue = nextCell();
		}
		nextRow();
		
	}
	
	

}
