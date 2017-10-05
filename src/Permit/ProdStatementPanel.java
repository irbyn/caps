package Permit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import net.proteanit.sql.DbUtils;

class ProdStatementPanel extends JPanel {

	private int [] columnWidth = {20, 80, 100, 80, 30, 30, 40, 40, 60, 30, 30};
	private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsCCC] ";
	private String upPS3 = "call AWS_WCH_DB.dbo.p_PermitUpdateReceived ";

	private String param = "";  
	private ResultSet rs;
	private Boolean rowSelected = false;
	
	private String ps3 = "//C:/pdfs/Invoice/ps3.pdf"; 
	private String file = "//C:/pdfs/Invoice/ps3"; 
	
	private CreateConnection connecting;
	
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JPanel updatePanel;
	private JTable permitsTbl;
	private JTable updateTbl;
	private DefaultTableModel model;
	private DefaultTableModel model1;
	
	private JTextArea detailsTxtArea;
	private JTextArea tmpTxt;
	
	private JTextPane warningLbl;

	private JLabel pSLbl;
	private JSpinner pSDate;
	
	private JButton prntConsentBtn; 
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	
	private String inst = "";
	private String auth = "";
	private String consnt = "";
	private String site = "";
	private String legal = "";
	private String owner = "";
	private String fire = "";
	private String dateinst = "";
	
	
	private ConnDetails conDets;

	private CreateConnection conn;

	  public ProdStatementPanel(ConnDetails conDeets, PermitPane pp) {
		  			
			conDets = conDeets;
	  		
			connecting = new CreateConnection();
		  	 		  	
			model = new DefaultTableModel();  
			model.setRowCount(0);
		    permitsTbl = new JTable(model);
		    permitsTbl.setPreferredSize(new Dimension(0, 300));
		    permitsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		    permitsTbl.setAutoCreateRowSorter(true);
		        
		    JScrollPane scrollPane = new JScrollPane(permitsTbl);
			  
		    header= permitsTbl.getTableHeader();
		    columnModel = header.getColumnModel();
		    add(header); 
			               	        
		    tablePanel = new JPanel();
		    tablePanel.setBounds(20, 20, 1025, 260);  //setPreferredSize(new Dimension(0, 300));      
		    tablePanel.setLayout(new BorderLayout());
		    
		    infoPanel = new JPanel();
		    infoPanel.setBounds(0, 280, 1100, 300);  //setPreferredSize(new Dimension(0, 300));
		    infoPanel.setLayout(null);
	        
		    detailsTxtArea = new JTextArea("");
		    detailsTxtArea.setBounds(20, 20, 250, 260);
		    detailsTxtArea.setBorder(BorderFactory.createLineBorder(Color.black));
		    detailsTxtArea.setLineWrap(true);
		    detailsTxtArea.setEditable(false);
		    infoPanel.add(detailsTxtArea);
		    
		    warningLbl = new JTextPane(); 
		    warningLbl.setEditable(false);
		    warningLbl.setText("");
		    warningLbl.setVisible(false);
		    warningLbl.setForeground(Color.RED);
		    warningLbl.setBounds(400, 20, 400, 80);
		    infoPanel.add(warningLbl);		    
		    
		    pSLbl = new JLabel("Date for PS4:");
		    pSLbl.setBounds(800, 170, 95, 20);
		    infoPanel.add(pSLbl);

		    SimpleDateFormat psModel = new SimpleDateFormat("dd.MMM.yyyy");
			pSDate = new JSpinner(new SpinnerDateModel());
			pSDate.setEditor(new JSpinner.DateEditor(pSDate, psModel.toPattern()));
			pSDate.setBounds(895, 170, 150, 20);
			infoPanel.add(pSDate);        
		       
		    prntConsentBtn = new JButton("Print Consent");
		    prntConsentBtn.setBounds(545, 260, 150, 25);
		    infoPanel.add(prntConsentBtn);
		        
		    cancelPermitReqBtn = new JButton("Cancel");
		    cancelPermitReqBtn.setBounds(720, 260, 150, 25);
		    infoPanel.add(cancelPermitReqBtn);
		        
		    savePermitReqBtn = new JButton("Save Permit Details");
		    savePermitReqBtn.setBounds(895, 260, 150, 25);
		    infoPanel.add(savePermitReqBtn);

		    this.setLayout(null);
		    this.add(tablePanel); 
		    this.add(infoPanel);
		       
			tablePanel.add(scrollPane, BorderLayout.CENTER);
			tablePanel.add(permitsTbl.getTableHeader(), BorderLayout.NORTH); 
			  	
			permitsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent arg0) {
					if (!arg0.getValueIsAdjusting()){
						rowSelected=true;
			//			pp.setFormsLocked();
						try{
						param = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 0).toString();
						
						displayClientDetails(param);
						
						} catch (IndexOutOfBoundsException e){
							//Ignoring IndexOutOfBoundsExceptions!
						}
						}
					}
			  	});
			  	
		savePermitReqBtn.addActionListener( new ActionListener()
	  	{
			@Override
			public void actionPerformed(ActionEvent arg0) {
		        { 
		        	if (rowSelected){
		        		
		        	} else {//	No Customer selected
		        		JOptionPane.showMessageDialog(null, "No details to Save");			        		
		        	}
			    }					
			}
	  	});

		cancelPermitReqBtn.addActionListener( new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) {
				   { 
				        clearFields();
					}					
				}
			});

		prntConsentBtn.addActionListener( new ActionListener()
			 {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Check a Customer has been selected
			        if (rowSelected){
	        	
			        	int input = JOptionPane.showConfirmDialog(null,"Mark PS3 as Printed and Sent to Council?\n "
			        			+ "This Customer will no longer display on this page!",  "Mark Consent Sent?", JOptionPane.YES_NO_OPTION);
			        	if (input == 0){
			        		

						fillPS3();
						updatePS3();
		     	        		
 JOptionPane.showMessageDialog(null, "UPDATE DB!\n reset table");		        		

			        	} else{
							fillPS3();
JOptionPane.showMessageDialog(null, "View only");	
			        	}
		        	}else {	//	No Customer selected
		        		JOptionPane.showMessageDialog(null, "No details to Print");			        				      
			       	}				
				}
			});
	  	}

	  
	  protected void fillPS3()   {
	//	  String formTemplate = "//C:/pdfs/Invoice/ps3.pdf";
	//	  String filledForm = "//C:/pdfs/Invoice/ps3OUT.pdf";	  
	        
	        try (PDDocument pdfDocument = PDDocument.load(new File(ps3)))
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
	                field.setValue(getPSDate());
	            }
	            
	            // Save and close the filled out form.
	            pdfDocument.save(file+param+".pdf");
	            

		      if (Desktop.isDesktopSupported()) {
		    	    try {
		    	        File myFile = new File(file+param+".pdf");
		    	        Desktop.getDesktop().open(myFile);
		    	    } catch (IOException ex) {
		    	        // no application registered for PDFs
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

	  
		protected void updatePS3() {
			
			CallableStatement pm = null;

			try {
					
				String update = "{" + upPS3 +"(?,?,?)}";	
			    Connection conn = connecting.CreateConnection(conDeets);	        	   	
			
			    pm = conn.prepareCall(update);
				
			    pm.setString(1, param);
			    pm.setString(2, getConsentNum());
			    pm.setString(3, getReceivedDate());
				
			    pm.executeUpdate();
			    }
		        catch (SQLServerException sqex)
		        {
		           	JOptionPane.showMessageDialog(null, "DB_ERROR: " + sqex);
		        }
		        catch(Exception ex)
		        { 
		           JOptionPane.showMessageDialog(null, "CONNECTION_ERROR: " + ex);
		        }			
		}
*/		
	private void clearFields(){			  
		permitsTbl.clearSelection();
		rowSelected=false;
		param = "";
		detailsTxtArea.setText("");
		 warningLbl.setText("");
	}	    

		    
			
	private void displayClientDetails(String parameter) {
		try
		    {
		        Connection conn = connecting.CreateConnection(conDets);
		        PreparedStatement st2 =conn.prepareStatement(result2 + parameter);
		        ResultSet rs2 = st2.executeQuery();
		    
		        //Retrieve by column name
		        while(rs2.next()){
				                        
				String invoice 		= rs2.getString("Invoice");
				String customerName 	= rs2.getString("CustomerName");					//4
				String customerAddress = rs2.getString("CustomerAddress");					//5a
				String customerSuburb 	= rs2.getString("CustomerSuburb");					//5b
				String customerPostCode= rs2.getString("CustomerPostCode");				//6
				String customerPhone 	= rs2.getString("CustomerPhone");					//
				String customerMobile 	= rs2.getString("CustomerMobile");					//
				String customerEmail 	= rs2.getString("CustomerEmail");					//
				String streetAddress 	= rs2.getString("StreetAddress");					//3a
				String suburb 			= rs2.getString("Suburb");							//3b
				String consent 			= rs2.getString("Consent");							//1		2=DATE GRANTED!		
				Date consentDate		= rs2.getDate("ConsentDate");
				String lot 				= rs2.getString("Lot");
				String dP				= rs2.getString("DP"); 
				String installerID 		= rs2.getString("Installer_ID"); 
				Date installDate		= rs2.getDate("InstallDate");
				String instName 		= rs2.getString("InstName"); 
				String instMobile 		= rs2.getString("InstMobile"); 
				String instAuth 		= rs2.getString("InstAuth"); 
				String instNZHHA 		= rs2.getString("InstNZHHA"); 
				String make_model 		= rs2.getString("Fire"); 
				
				inst = instName;
				auth = instAuth;
				consnt = consent;
				site = streetAddress + ", " + suburb;
				legal = "LOT: " + lot + ",  DP: " + dP;
				owner = customerName;
				fire = make_model;
				dateinst = getPSDate();
				
					
		       	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
				String cnst = sdf.format(consentDate);
				String inst = sdf.format(installDate);
						
				String sb =	" CLIENT:\t" + customerName + "\n\n" + 
							" SITE:\t" + streetAddress + "\n" +
							"\t" + suburb + "\n\n" + 
							" POSTAL:\t" + customerAddress + "\n" +
							"\t" + customerSuburb + "\n" + 
							"\t" + customerPostCode + "\n\n" +
							" PHONE:\t" + customerPhone + "\n" + 
							" MOBILE:\t" + customerMobile + "\n\n" +
							" EMAIL:\t" + customerEmail+ "\n\n" +
							" Consent:\t" + consentDate;

						 detailsTxtArea.setText(sb);
						 
						 pSDate.setValue(installDate);
						 
						 if (installDate.before(consentDate)){
							 warningLbl.setVisible(true);
							 warningLbl.setText("\tWarning, Install Date is Before Consent Date\n\n"
							 			+ "\tInstalled On:\t\t" + inst + "\n"
							 			+ "\tConsent Issued:\t" + cnst);	
						 }
						 else {
							 warningLbl.setVisible(false);
						 }


					 
		        	 }
		        	conn.close();	
		        }
		        catch(Exception ex)
		        { 
		        JOptionPane.showMessageDialog(null, ex.toString());
		        }	  	
	 	}	
		
    public String getPSDate(){  	
    	Date dte = (Date) pSDate.getValue(); 
       	SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
    	String dt = sdf1.format(dte);
    	return dt;
    } 
			
		public JTable getPermitsTbl(){
		    return permitsTbl;
		}
	}
