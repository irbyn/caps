package documents;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JOptionPane;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import DB_Comms.CreateConnection;
import Main.ConnDetails;

public class PrintSiteCheck {
	private String getSCDetails = "EXEC AWS_WCH_DB.dbo.sc_PrintSiteChecks";
	private ResultSet qryResults;
//	private String scForm = "//C:/pdfs/Invoice/InstType_FS.pdf"; 

	private String file = "//C:/pdfs/Invoice/SC_Sale"; 
	private CreateConnection connecting;
	private ConnDetails condeets;
	private String saleID;
	
	private String scPath = "";
	private String salesperson = "";
	private String customer = "";
	private String site = "";
	private String phone = "";
	private String mobile = "";
	private String email = "";
	private String fire = "";
	private String lot = "";
	private String dp = "";
	private String date = "";
	private String time = "";	
		
	public PrintSiteCheck(String saleId, ConnDetails condeets){
	
		this.condeets = condeets;
		this.saleID = saleId;
		
		connecting = new CreateConnection();
   	
        try
        {
        	Connection conn = connecting.CreateConnection(condeets);
        	PreparedStatement st2 =conn.prepareStatement(getSCDetails + ' ' +  saleID);	    	
        	qryResults = st2.executeQuery();
        	if (qryResults==null){

    			  JOptionPane.showMessageDialog(null, "null query");
        	}
        	else{
				while(qryResults.next()){
					
					scPath			= qryResults.getString("scPath");
					salesperson 	= qryResults.getString("salesperson");
		        	customer		= qryResults.getString("customer");
	 	     		site 			= qryResults.getString("site");
					phone 	 		= qryResults.getString("phone");
					mobile 			= qryResults.getString("mobile");
					email 			= qryResults.getString("email");
					fire 			= qryResults.getString("fire");
					date 			= qryResults.getString("date");
					time 			= qryResults.getString("time");       	
				
				}
        	}
        }
        catch(Exception ex)
        { 
        JOptionPane.showMessageDialog(null, ex.toString());
        }      		            

        try (PDDocument pdfDocument = PDDocument.load(new File(scPath)))
        {
            // get the document catalog
            PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
            
            // as there might not be an AcroForm entry a null check is necessary
            if (acroForm != null)
            {
                // Retrieve an individual field and set its value.
                PDTextField field = (PDTextField) acroForm.getField( "salesperson" );
                field.setValue(salesperson);
                field = (PDTextField) acroForm.getField( "customer" );
                field.setValue(customer);
                field = (PDTextField) acroForm.getField( "site" );
                field.setValue(site);
                field = (PDTextField) acroForm.getField( "phone" );
                field.setValue(phone);
                field = (PDTextField) acroForm.getField( "mobile" );
                field.setValue(mobile);
                field = (PDTextField) acroForm.getField( "email" );
                field.setValue(email);
                field = (PDTextField) acroForm.getField( "fire" );
                field.setValue(fire);
                field = (PDTextField) acroForm.getField( "date" );
                field.setValue(date);
                field = (PDTextField) acroForm.getField( "time" );
                field.setValue(time);

            }	            
            // Save and close the filled out form.
            pdfDocument.save(file+"_"+saleID+".pdf");
            
	      if (Desktop.isDesktopSupported()) {
	    	    try {
	    	        File myFile = new File(file+"_"+saleID+".pdf");
	    	        Desktop.getDesktop().open(myFile);
	    	    } catch (java.io.FileNotFoundException f){

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
	

}
