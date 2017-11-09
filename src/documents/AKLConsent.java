package documents;
/*
 * Generates a Council Consent form as a pdf filled-form drawing on DB fields for data
 * uses apache.pdfbox
 */
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Permit.PermitPane;

public class AKLConsent {

	private CreateConnection connecting;
	private String consentDetails = "EXEC AWS_WCH_DB.dbo.[p_PermitsConsent] ";
	private ResultSet rs;

	private String consent = ""; 
	private String file = ""; 

	private String site = "";
	private String lot = "";
	private String dp = "";
	private String building = "";
	private String location = "";	
	private String level = "";
	private String year = "";
	private String owner = "";
	private String postalAddress = "";
	private String postalSuburb = "";	
	private String postCode = "";
	private String homePhone = "";
	private String mobile = "";
	private String email = "";		
	private String invoiceNumber = "";
	private String date = "";
	private String make = "";
	private String model = "";
	private String life = "";
	private String ecan = "";
	private String nelson = "";
	private String value = "";
	private String wb = "";

	private String docs = ""; 	//ct lease saleAndP other 
	private String fire = ""; //fs2 = ib2 is2 =  rep2 = "";
	private String fuel = ""; //fopen fout foil fwood ;

	//		if wb = true	wetY  wetN	wetYO = O  	wetNO = O
	//		if fire = is2   chimYO = O  chimNO = O
	private Boolean lockForm;
	private ConnDetails conDeets;
	private FileSystem fs;


	public void fillConsent(String invNum, ConnDetails condeets)   { 
		this.conDeets = condeets;
		connecting = new CreateConnection();
		fs = new FileSystem();

		consent = fs.getConsentForm();	//	"//C:/pdfs/Invoice/ps3.pdf"; 
		file = fs.getConsentStorage();	//	"//C:/pdfs/Invoice/ps3"; 

		getConsentDetails( invNum);

		try (PDDocument pdfDocument = PDDocument.load(new File(consent)))
		{
			// get the document catalog
			PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();

			// as there might not be an AcroForm entry a null check is necessary
			if (acroForm != null)
			{
				// Retrieve an individual field and set its value.
				PDTextField field = (PDTextField) acroForm.getField( "invoiceNumber" );
				field.setValue(invoiceNumber);
				field = (PDTextField) acroForm.getField( "site" );
				field.setValue(site);
				field = (PDTextField) acroForm.getField( "lot" );
				field.setValue(lot);
				field = (PDTextField) acroForm.getField( "dp" );
				field.setValue(dp);
				field = (PDTextField) acroForm.getField( "building" );
				field.setValue(building);
				field = (PDTextField) acroForm.getField( "location" );
				field.setValue(location);
				field = (PDTextField) acroForm.getField( "level" );
				field.setValue(level);
				field = (PDTextField) acroForm.getField( "year" );
				field.setValue(year);
				field = (PDTextField) acroForm.getField( "owner" );
				field.setValue(owner);
				field = (PDTextField) acroForm.getField( "postalAddress" );
				field.setValue(postalAddress);
				field = (PDTextField) acroForm.getField( "postalSuburb" );
				field.setValue(postalSuburb);	                
				field = (PDTextField) acroForm.getField( "lot" );
				field.setValue(lot);
				field = (PDTextField) acroForm.getField( "dp" );
				field.setValue(dp);
				field = (PDTextField) acroForm.getField( "postCode" );
				field.setValue(postCode);
				field = (PDTextField) acroForm.getField( "homePhone" );
				field.setValue(homePhone);
				field = (PDTextField) acroForm.getField( "mobile" );
				field.setValue(mobile);
				field = (PDTextField) acroForm.getField( "email" );
				field.setValue(email);
				field = (PDTextField) acroForm.getField( "date" );
				field.setValue(date);
				field = (PDTextField) acroForm.getField( "make" );
				field.setValue(make);
				field = (PDTextField) acroForm.getField( "model" );
				field.setValue(model);
				field = (PDTextField) acroForm.getField( "life" );
				field.setValue(life);
				field = (PDTextField) acroForm.getField( "value" );
				field.setValue(value);
				field = (PDTextField) acroForm.getField( "ECAN" );
				field.setValue(ecan);
				field = (PDTextField) acroForm.getField( "Nelson" );
				field.setValue(nelson);

				/*  
				    field = (PDTextField) acroForm.getField( "wb" );
	                field.setValue(wb);
	                field = (PDTextField) acroForm.getField( "docs" );
	                field.setValue(docs);
	                field = (PDTextField) acroForm.getField( "fire" );
	                field.setValue(fire);
	                field = (PDTextField) acroForm.getField( "fuel" );
	                field.setValue(fuel);
				 */        
			}	            
			// Save and close the filled out form.
			pdfDocument.save(file+"_"+invNum+".pdf");

			if (Desktop.isDesktopSupported()) {
				try {
					File myFile = new File(file+"_"+invNum+".pdf");
					Desktop.getDesktop().open(myFile);
				} catch (FileNotFoundException f){

				} catch (IOException ex) {
					// no application registered for PDF		    	    	
				}
			}
		} catch (java.lang.NoClassDefFoundError s){
			JOptionPane.showMessageDialog(null, s.toString());
		} catch (InvalidPasswordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getConsentDetails(String inv) {
		try
		{
			Connection conn = connecting.CreateConnection(conDeets);
			PreparedStatement st2 =conn.prepareStatement(consentDetails + inv);
			ResultSet rs2 = st2.executeQuery();

			//Retrieve by column name
			while(rs2.next()){
				invoiceNumber 	= rs2.getString("invoiceNumber");  
				site 			= rs2.getString("site");
				lot 			= rs2.getString("lot");
				dp 				= rs2.getString("dp");
				building 		= rs2.getString("building");
				location 		= rs2.getString("location");	
				level 			= rs2.getString("level");
				year 			= rs2.getString("year");
				owner 			= rs2.getString("owner");
				postalAddress	= rs2.getString("postalAddress");
				postalSuburb 	= rs2.getString("postalSuburb");
				postCode 		= rs2.getString("postCode");
				homePhone 		= rs2.getString("homePhone");
				mobile 			= rs2.getString("mobile");
				email 			= rs2.getString("email");	
				date			= rs2.getString("date");
				make			= rs2.getString("make");
				model			= rs2.getString("model");
				life			= rs2.getString("life");
				ecan			= rs2.getString("ecan");
				nelson			= rs2.getString("nelson");
				value			= rs2.getString("value");
				wb				= rs2.getString("wb");
				docs			= rs2.getString("docs");
				fire			= rs2.getString("fire");
				fuel			= rs2.getString("fuel");


			}
			conn.close();	
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}	  	
	}	
}
