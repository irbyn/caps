package Sales;


import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumnModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Sales.CustomerPanel;
import Sales.EstimationPanel;
import Sales.FollowUpPanel;
import Sales.QuotePanel;
import Sales.SiteCheckPanel;
import net.proteanit.sql.DbUtils;

public class SalesPane extends JPanel
{
	private CreateConnection connecting;

	private ConnDetails conDeets;

	private ResultSet results;
	private ResultSet qryResults;
	private int tabIndex = 0;

	//Panels loaded in Tabs
	private CustomerPanel customer;
	private EstimationPanel estimation;
	private FollowUpPanel followUp;
	private QuotePanel quote;
	private SiteCheckPanel siteCheck;

	private Boolean lockForm;
	private String custDetails = "EXEC AWS_WCH_DB.dbo.s_CustomerDetails";
	
	// Stored procedures to fill tables (Triggered by tab selection)
	private String[] procedure = new String[]{	"EXEC AWS_WCH_DB.dbo.s_SalesCustomer", // procedure[0]
			"EXEC AWS_WCH_DB.dbo.s_SalesEstimation", // procedure[1]
			"EXEC AWS_WCH_DB.dbo.s_SalesSiteCheck", // procedure[2]
			"EXEC AWS_WCH_DB.dbo.s_SalesQuote", // procedure[3]
			"EXEC AWS_WCH_DB.dbo.s_SalesFollowUp"};// procedure[4]

	public SalesPane(ConnDetails conDeets)
	{
		
		this.conDeets = conDeets;

		lockForm = false;

		connecting = new CreateConnection();

		//Adding Jpanels to the SAles panel area 
		JTabbedPane salesP = new JTabbedPane();
		salesP.setPreferredSize(new Dimension(1070, 610));

		customer = new CustomerPanel(conDeets, this);
		estimation = new EstimationPanel(conDeets, this);
		siteCheck = new SiteCheckPanel(conDeets, this);
		quote = new QuotePanel(conDeets, this);
		followUp = new FollowUpPanel(conDeets, this);

		JTable[] tablez = new JTable[]{customer.getSalesTbl(), 
										estimation.getSalesTbl(), 
										siteCheck.getSalesTbl(), 
										quote.getSalesTbl(), 
										followUp.getSalesTbl()};

		//Adding tabs to the content panel 
		salesP.addTab("Customer", customer);
		salesP.addTab("Estimation", estimation);
		salesP.addTab("Site Checks", siteCheck);
		salesP.addTab("Quotes", quote);
		salesP.addTab("Follow ups", followUp);
		add(salesP);   

		//If a different tab is clicked
		salesP.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() instanceof JTabbedPane) {
					//  	if(!lockForm){

					JTabbedPane pane = (JTabbedPane) e.getSource();
					tabIndex = pane.getSelectedIndex();

					getResults(tabIndex, conDeets); 
					//ResultSet r1 = results;

					// add ResultSet into Selected Tab JTable.
					tablez[tabIndex].setModel(DbUtils.resultSetToTableModel(results));

					TableColumnModel tcm = tablez[tabIndex].getColumnModel();
					int cols = tcm.getColumnCount();

					if (cols == 6){
						int[] colWidths = new int[]{20, 150, 150, 100, 100, 100}; 
						spaceHeader(colWidths, tcm);
					} else if (cols == 7){
						int[] colWidths = new int[]{20, 150, 150, 100, 100, 100, 100};   
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
		});   		
	}

	
	 public void setFormsLocked() {
	    	lockForm = true;
	  }	    
	    public void setFormsUnLocked() {
	    	lockForm = false;
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
//Only displays the first one and not the 
		        		 //String invoice 		= qryResults.getString("Invoice");
						 String customerName 	= qryResults.getString("CustomerName");
						 String customerAddress = qryResults.getString("CustomerAddress");
						 String customerSuburb 	= qryResults.getString("CustomerSuburb");
						 String customerPostCode= qryResults.getString("CustomerPostCode");
						 String customerPhone 	= qryResults.getString("CustomerPhone");
						// String customerMobile 	= qryResults.getString("CustomerMobile");
						 String customerEmail 	= qryResults.getString("CustomerEmail");
						 //String streetAddress 	= qryResults.getString("StreetAddress");
						 //String suburb 			= qryResults.getString("Suburb");
						 //String status 			= qryResults.getString("PermitStatus");

						 String str = "INVOICE:\t\t" + parameter + "\n\n"
								 + "CLIENT:\t\t" + customerName + "\n\n"
								 + "SITE ADDRESS:\t" + customerAddress + "\n\n"
								 + "SUBURB:\t\t" + customerSuburb + "\n\n"
								 + "POST CODE:\t\t" + customerPostCode + "\n\n"
								 + "PHONE NUMBER:\t" + customerPhone + "\n\n"
								 + "EMAIL:\t\t" + customerEmail;
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
	    	JOptionPane.showMessageDialog(null, "about to try");
	        try
	        {
	        	 JOptionPane.showMessageDialog(null, "Index: " + ind + " " + connDeets);
	        	Connection conn = connecting.CreateConnection(connDeets);
	        	 JOptionPane.showMessageDialog(null, "+++++++++++");
	        	PreparedStatement st =conn.prepareStatement(procedure[ind]);	//ind]);
	        	 JOptionPane.showMessageDialog(null, "dddddddddd");
	        	results = st.executeQuery();
	        	 JOptionPane.showMessageDialog(null, "sssssssssssssssssss");
	        	
	        	if (results==null){
	        		 JOptionPane.showMessageDialog(null, "RESULTS ARE NULL");
	        		getResults(0, conDeets);
	        	}
	        }
	        catch(Exception ex)
	        { 
	        JOptionPane.showMessageDialog(null, ex.toString());
	        }
	    		return results;       		            
	    }
	    
        //Get the results set for the customer details
        public ResultSet getDetails(String qry, String param, ConnDetails connDeets){      	
        	
            try
	        {
	        	Connection conn = connecting.CreateConnection(connDeets);
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
}




