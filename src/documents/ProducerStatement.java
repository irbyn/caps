package documents;
/*
 * Generates a Producer Statement in pdf Form
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

public class ProducerStatement {

	private CreateConnection connecting;
	private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsCCC] ";
/*
	private String ps3 = "//C:/pdfs/Invoice/ps3.pdf"; 
	private String file = "//C:/pdfs/Invoice/ps3"; 
*/
	private String inst = "";
	private String auth = "";
	private String consnt = "";
	private String site = "";
	private String legal = "";
	private String owner = "";
	private String fire = "";
	private String dateinst = "";

	private FileSystem fs;
	private Boolean lockForm;
	private ConnDetails conDeets;
	private PermitPane pp;

	public void fillPS3(String invNum, String dte, ConnDetails condeets)   { 
		this.conDeets = condeets;
		connecting = new CreateConnection();
		fs = new FileSystem();
		displayClientDetails( invNum);
		dateinst = dte;
		try (PDDocument pdfDocument = PDDocument.load(new File(fs.getPS3Form())))
		{
			// get the document catalog
			PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();

			// as there might not be an AcroForm entry a null check is necessary
			if (acroForm != null)
			{
				// Retrieve an individual field and set its value.
				PDTextField field = (PDTextField) acroForm.getField( "installer" );
				field.setValue(inst);
				field = (PDTextField) acroForm.getField( "author" );
				field.setValue(auth);
				field = (PDTextField) acroForm.getField( "consent" );
				field.setValue(consnt);
				field = (PDTextField) acroForm.getField( "site" );
				field.setValue(site);
				field = (PDTextField) acroForm.getField( "lot" );
				field.setValue(legal);
				field = (PDTextField) acroForm.getField( "owner" );
				field.setValue(owner);
				field = (PDTextField) acroForm.getField( "fire" );
				field.setValue(fire);
				field = (PDTextField) acroForm.getField( "date" );
				field.setValue(dte);
			}	            
			// Save and close the filled out form.
			pdfDocument.save(fs.getPS3Storage()+invNum+".pdf");

			if (Desktop.isDesktopSupported()) {
				try {
					File myFile = new File(fs.getPS3Storage()+invNum+".pdf");
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

	private void displayClientDetails(String parameter) {
		try	//some fields currently not in use - form to be extended later
		{
			Connection conn = connecting.CreateConnection(conDeets);
			PreparedStatement st2 =conn.prepareStatement(result2 + parameter);
			ResultSet rs2 = st2.executeQuery();

			//Retrieve by column name
			while(rs2.next()){

//				String invoice 			= rs2.getString("Invoice");
				String customerName 	= rs2.getString("CustomerName");										
				String streetAddress 	= rs2.getString("StreetAddress");					
				String suburb 			= rs2.getString("Suburb");							
				String consent 			= rs2.getString("Consent");									
//				Date consentDate		= rs2.getDate("ConsentDate");
				String lot 				= rs2.getString("Lot");
				String dP				= rs2.getString("DP"); 
//				String installerID 		= rs2.getString("Installer_ID"); 
//				Date installDate		= rs2.getDate("InstallDate");
				String instName 		= rs2.getString("InstName"); 
//				String instMobile 		= rs2.getString("InstMobile"); 
				String instAuth 		= rs2.getString("InstAuth"); 
//				String instNZHHA 		= rs2.getString("InstNZHHA"); 
				String make_model 		= rs2.getString("Fire"); 

				inst = instName;
				auth = instAuth;
				consnt = consent;
				site = streetAddress + ", " + suburb;
				legal = "LOT: " + lot + ",  DP: " + dP;
				owner = customerName;
				fire = make_model;
			}
			conn.close();	
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}	  	
	}	
}
