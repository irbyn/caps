package Installs;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumnModel;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Main.Homescreen;
import Installs.*;
import net.proteanit.sql.DbUtils;

public class InstallsPane extends JPanel
{		
		public CreateConnection connecting;

		private Homescreen hs;
		private ConnDetails conDeets;

		private ResultSet results;
		private ResultSet qryResults;
		private int tabIndex = 0;

		//Panels loaded in Tabs
		private LoadDocsPanel loadDocPnl;
		private CheckForOrdersPanel checkOrderPnl;
		private PlaceOrdPanel placeOrderPnl;
		private RecvOrderPanel recvOrderPnl;
		private BookingsPanel bookingPnl;

		private Boolean lockForm;
		private String custDetails = "EXEC AWS_WCH_DB.dbo.p_CustomerDetails";				

// Stored procedures to fill tables (Triggered by tab selection)
private String[] procedure = new String[]{	"EXEC AWS_WCH_DB.dbo.i_InstallsToLoad", // procedure[0]
											"EXEC AWS_WCH_DB.dbo.i_InstallsToCheck", // procedure[1]
											"EXEC AWS_WCH_DB.dbo.i_InstallsPlaceOrders", // procedure[2]
											"EXEC AWS_WCH_DB.dbo.i_InstallsReceiveOrders", // procedure[3]
											"EXEC AWS_WCH_DB.dbo.i_InstallsToBook"};// procedure[4]


	public InstallsPane(ConnDetails conDeets, Homescreen hs)
    {   
    	this.hs = hs;
    	this.conDeets = conDeets;
    	lockForm = false;
		
		  	connecting = new CreateConnection();
    	
		//Adding Jpanels to the SAles panel area 
		JTabbedPane permitP = new JTabbedPane();
		permitP.setPreferredSize(new Dimension(1070, 610));

		loadDocPnl = new LoadDocsPanel(lockForm, conDeets, this);
		checkOrderPnl = new CheckForOrdersPanel(lockForm, conDeets, this);
		placeOrderPnl = new PlaceOrdPanel(lockForm, conDeets, this);
		recvOrderPnl = new RecvOrderPanel(lockForm, conDeets, this);
		bookingPnl = new BookingsPanel(lockForm, conDeets, this);
		
		 JTable[] tablez = new JTable[]{loadDocPnl.getPermitsTbl(), 
				 						checkOrderPnl.getPermitsTbl(), 
				 						placeOrderPnl.getPermitsTbl(),  
				 						recvOrderPnl.getPermitsTbl(), 
				 						bookingPnl.getPermitsTbl()};

		permitP.addTab("Load Documents", loadDocPnl);
		permitP.addTab("Check for Orders", checkOrderPnl);
		permitP.addTab("Place Orders", placeOrderPnl);
		permitP.addTab("Receive Orders", recvOrderPnl);
		permitP.addTab("Bookings", bookingPnl);
		add(permitP); 

//		getResults(0);  
		
		permitP.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (e.getSource() instanceof JTabbedPane) {
              //  	if(!lockForm){
                	
                    JTabbedPane pane = (JTabbedPane) e.getSource();
                    tabIndex = pane.getSelectedIndex();
                    
                    getResults(tabIndex, conDeets); 
                    
                    // add ResultSet into Selected Tab JTable.
                    tablez[tabIndex].setModel(DbUtils.resultSetToTableModel(results));               
                    TableColumnModel tcm = tablez[tabIndex].getColumnModel();
                     int cols = tcm.getColumnCount();

                     if (cols == 7){
                    	 int[] colWidths = new int[]{20, 150, 150, 100, 100, 100, 100}; 
                    	 spaceHeader(colWidths, tcm);
                     } else if (cols == 8){
                    	 int[] colWidths = new int[]{20, 150, 150, 100, 100, 100, 100, 100};   
                    	 spaceHeader(colWidths, tcm);                         
                     } else if (cols == 9){
                        	 int[] colWidths = new int[]{30, 100, 120, 80, 40, 40, 40, 40, 40};   
                        	 spaceHeader(colWidths, tcm);
                     }else {
                    	 int[] colWidths = new int[]{20, 80, 100, 80, 30, 30, 40, 40, 60, 30, 30};    
                    	 spaceHeader(colWidths, tcm);
                     }
                }
                }
//            }
        });   		
    }
    
    public void setFormsLocked() {
    	lockForm = true;
  }	    
    public void setFormsUnLocked() {
    	lockForm = false;
  }	  
    
    public void showMessage(String msg) {
    	hs.showMsg(msg);
    }
    
    private void spaceHeader(int[] widths, TableColumnModel tcm){
    	int cols = tcm.getColumnCount();
        for (int i = 0; i < cols; i++){
       	 tcm.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    
    public String DisplayClientDetails(String parameter){
    	
        try
        {
        	Connection conn = connecting.CreateConnection(conDeets);
        	PreparedStatement st2 =conn.prepareStatement(custDetails + ' ' +  parameter);	    	
        	qryResults = st2.executeQuery();
        	if (qryResults==null){

    			  JOptionPane.showMessageDialog(null, "null query");
        	}
        	else{
				while(qryResults.next()){
					
		        	String invoice 			= qryResults.getString("Invoice");
		        	String rees				= qryResults.getString("Rees");
					 String customerName 	= qryResults.getString("CustomerName");
					 String customerAddress = qryResults.getString("CustomerAddress");
					 String customerSuburb 	= qryResults.getString("CustomerSuburb");
					 String customerPostCode= qryResults.getString("CustomerPostCode");
					 String customerPhone 	= qryResults.getString("CustomerPhone");
					 String customerMobile 	= qryResults.getString("CustomerMobile");
					 String customerEmail 	= qryResults.getString("CustomerEmail");
					 String streetAddress 	= qryResults.getString("StreetAddress");
					 String suburb 			= qryResults.getString("Suburb");
										
			        String str = " INVOICE:\t" + parameter + "\n" +
			        		     " REES CODE:\t" + rees +"\n" +
		        				 " CLIENT:\t" + customerName + "\n\n" + 
							 	 " SITE:\t" + streetAddress + "\n" +
							 	 "\t" + suburb + "\n\n" + 
							 	 " POSTAL:\t" + customerAddress + "\n" +
							 	 "\t" + customerSuburb + "\n" + 
							 	 "\t" + customerPostCode + "\n\n" +
							 	 " PHONE:\t" + customerPhone + "\n" + 
							 	 " MOBILE:\t" + customerMobile + "\n\n" +
							 	 " EMAIL:\t" + customerEmail + "\n";	        		
        		return str;
				}
        	}
        }
        catch(Exception ex)
        { 
        JOptionPane.showMessageDialog(null, ex.toString());
        }      		            
		return "";
    }
    	
    	
    	
    	
    	
    	
    	
    	
    
    
    public ResultSet getResults(int ind, ConnDetails connDeets){      	
    	
        try
        {
        	Connection conn = connecting.CreateConnection(connDeets);
        	PreparedStatement st =conn.prepareStatement(procedure[ind]);	//ind]);
        	results = st.executeQuery();
        	if (results==null){
        		getResults(0, conDeets);
        	}
        }
        catch(Exception ex)
        { 
        JOptionPane.showMessageDialog(null, ex.toString());
        }
    		return results;       		            
    }
    
    public ResultSet getDetails(String qry, String param){      	
    	
        try
        {
        	Connection conn = connecting.CreateConnection(conDeets);
        	PreparedStatement st2 =conn.prepareStatement(qry + param);	    	
        	qryResults = st2.executeQuery();
        	if (qryResults==null){

    			  JOptionPane.showMessageDialog(null, "null query");
        	}
        }
        catch(Exception ex)
        { 
        JOptionPane.showMessageDialog(null, ex.toString());
        }
    		return qryResults;       		            
    }

           
	protected void updatePermit(String update, String param, ConnDetails connDeets) {

		JOptionPane.showMessageDialog(null, update);
        try	// Attempt update
        {
        	Connection conn = connecting.CreateConnection(connDeets);	        	   	
        	Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);           	
//Create Update String
            stmt.execute(update); 

            JOptionPane.showMessageDialog(null, "Permit "+ param + " Updated successfully");
       }
        catch (SQLServerException sqex)
        {
//          	if (sqex.getErrorCode() == 547){
//          		JOptionPane.showMessageDialog(null, "Fire ID NOT IN SYSTEM!");
//         	}else {
        	JOptionPane.showMessageDialog(null, "DB_ERROR: " + sqex);
//          	}
        }
        catch(Exception ex)
        { 
        JOptionPane.showMessageDialog(null, "CONNECTION_ERROR: " + ex);
        }
	}
	
	/*
	 * Checks if this install (and sale), have files in the file system
	 * updates Boolean values invExists, siteExists, photoExists, 
	 */
/*		protected void checkForFiles() {
	
	String folder;
	
	String saleID;
	String invoiceNum;
	String invPfx;
	String sitePfx;
	String photoPfx;
	
	JButton viewInvBtn;
	JButton viewSiteBtn;
	JButton viewPhotoBtn;
	
	boolean invExists;
	boolean siteExists;
	boolean photoExists;
	
	File inv;
	File site;
	File[] photosArr;
			
		//Check for stored Invoice
		inv = new File(folder+invPfx+invoiceNum+".pdf");//Uses InstallID/Invoice number
		if (inv.exists()){
			viewInvBtn.setVisible(true);
			invExists = true;
		}else{
			viewInvBtn.setVisible(false);
			invExists = false;
		}	
		//Check for stored SiteCheck Forms	
		site = new File(folder+sitePfx+saleID+".pdf");//Uses SaleID number
		if (site.exists()){
			viewSiteBtn.setVisible(true);
			siteExists = true;
		}else{
			viewSiteBtn.setVisible(false);
			siteExists = false;
		}
		//Check for stored Photo(s)	
		//Create array of photos
		File f = new File(folder);					
			photosArr = f.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(photoPfx+saleID+"_");	//Uses SaleID number
			}
		});

		if (photosArr.length>0){
			viewPhotoBtn.setVisible(true);
			photoExists = true;
		}else{
			viewPhotoBtn.setVisible(false);
			photoExists = false;
		}
		
	}
*/
}
