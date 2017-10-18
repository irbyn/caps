package Installs;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.Date;
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
		
		private String stockRcvd   = "{CALL AWS_WCH_DB.dbo.i_InstallsReportReceived (?)}";
		private StringBuilder sb;
		private String str;

		private Boolean lockForm;
		private String custDetails = "EXEC AWS_WCH_DB.dbo.p_CustomerDetails";				

// Stored procedures to fill tables (Triggered by tab selection)
private String[] procedure = new String[]{	"EXEC AWS_WCH_DB.dbo.i_InstallsToLoad", 	// procedure[0]
											"EXEC AWS_WCH_DB.dbo.i_InstallsToCheck", 	// procedure[1]
											"EXEC AWS_WCH_DB.dbo.i_InstallsPlaceOrders",// procedure[2]
											"EXEC AWS_WCH_DB.dbo.i_InstallsReceiveOrders",//procedure[3]
											"EXEC AWS_WCH_DB.dbo.i_InstallsToBook"};	// procedure[4]

private int[][] spacing = new int[][]	{{30, 100, 120, 80, 40, 40, 40}, 				// procedure[0]
										 {30, 100, 120, 80, 40, 40, 40, 40},	 		// procedure[1]
										 {20, 100, 120, 20, 80, 400}, 					// procedure[2]
										 {20, 100, 100, 40, 20, 80, 300, 50}, 				// procedure[3]
										 {30, 100, 120, 80, 40, 40, 40, 40, 40}};		// procedure[4]
		

	public InstallsPane(ConnDetails conDeets, Homescreen hs)
    {   
    	this.hs = hs;
    	this.conDeets = conDeets;
    	lockForm = false;
		
		connecting = new CreateConnection();
		sb = new StringBuilder();
    	
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
				 						placeOrderPnl.getInstTbl(),  
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

                     spaceHeader(spacing[tabIndex], tcm);
                     
                }
                }
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
	

    public StringBuilder reportStockReceived(String date){
    	sb.setLength(0);
		sb.append("STOCK RECEIVED SINCE " + date); 
		sb.append(System.getProperty("line.separator"));
		
    	sb.append("----------------------------------------------------------------------------------");
    	sb.append(System.getProperty("line.separator"));

        try
        {
        	
        	Connection conn = connecting.CreateConnection(conDeets);
        	PreparedStatement st2 =conn.prepareStatement(stockRcvd);
        	st2.setString(1, date);
        	qryResults = st2.executeQuery();
        	if (qryResults==null){

    			  JOptionPane.showMessageDialog(null, "null query");
        	}
        	else{
				while(qryResults.next()){
					
					String invoice			= qryResults.getString("Invoice");
		        	String po				= qryResults.getString("Number");
		        	String installDate	 	= qryResults.getString("InstallDate");
					String customerName 	= qryResults.getString("CustomerName");
					String site		 		= qryResults.getString("Site");
					String qty			 	= qryResults.getString("Quantity");
					String item 			= qryResults.getString("Desc");
					String recvdDate	 	= qryResults.getString("Received");
	
					sb.append(String.format("%-15s %-20.12s %-32s", "PO: "+po, "Rcvd: "+recvdDate, customerName)); 
					sb.append(System.getProperty("line.separator"));
					sb.append(String.format("%-15s %-20.12s %-32s ", "INV:"+invoice, "Inst: "+installDate, site)); 
					sb.append(System.getProperty("line.separator"));
					sb.append(String.format("%3s %-11s %-50s", " ", qty, item )); 
					sb.append(System.getProperty("line.separator"));
					sb.append(System.getProperty("line.separator"));
				}
				return sb;
        	}
        }
        catch(Exception ex)
        { 
        JOptionPane.showMessageDialog(null, ex.toString());
        }      		            
		return sb;
    }

	
}
