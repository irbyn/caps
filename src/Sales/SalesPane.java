package Sales;


import java.awt.Component;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumnModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Main.Homescreen;
import Sales.CustomerPanel;
import Sales.EstimationPanel;
import Sales.FollowUpPanel;
import Sales.QuotePanel;
import Sales.SiteCheckPanel;
import net.proteanit.sql.DbUtils;

public class SalesPane extends JPanel
{
	private CreateConnection connecting;
	private Homescreen hs;
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
	private String custEmail;
	private String custName;
	private Date date;

	private Boolean lockForm;
	//private String custDetails = "EXEC AWS_WCH_DB.dbo.p_CustomerDetails";
	private String custDetails = "EXEC AWS_WCH_DB.dbo.s_CustomerDetails";

	// Stored procedures to fill tables (Triggered by tab selection)
	private String[] procedure = new String[]{	"EXEC AWS_WCH_DB.dbo.s_SalesCustomer", // procedure[0]
			"EXEC AWS_WCH_DB.dbo.s_SalesEstimation", // procedure[1]
			"EXEC AWS_WCH_DB.dbo.s_SalesSiteCheck", // procedure[2]
			"EXEC AWS_WCH_DB.dbo.s_SalesQuote", // procedure[3]
	"EXEC AWS_WCH_DB.dbo.s_SalesFollowUp"};// procedure[4]

	
	private int[][] spacing = new int[][]	{{50, 70, 150, 100, 100, 100}, // procedure[0]
		{50, 50, 70, 150, 100, 100, 100, 50, 50, 50},// procedure[1]
		{50, 50, 100, 70, 100, 100, 60, 50, 50, 50},// procedure[2]
		 {50, 50, 100, 70, 100, 100, 60, 50, 50, 50},// procedure[3]
		 {30, 30, 80, 100, 100, 100, 40, 40, 40, 50, 50}};// procedure[4]

	public SalesPane(ConnDetails conDeets, Homescreen hs)
	{
		this.hs = hs;
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
		
		JPanel[] fieldz = new JPanel[]{	customer.getInfoPanel(), 
				estimation.getInfoPanel(), 
				siteCheck.getInfoPanel(),  
				quote.getInfoPanel(), 
				followUp.getInfoPanel()};


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
					ClearFields(fieldz[tabIndex]);
					JTabbedPane pane = (JTabbedPane) e.getSource();
					tabIndex = pane.getSelectedIndex();

					getResults(tabIndex); 
					//ResultSet r1 = results;

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
					
		        	 String quote 			= qryResults.getString("Quote");
		        	 String rees			= qryResults.getString("customerReeseCode");
					 String customerFName 	= qryResults.getString("CustomerFName");
					 String customerLName 	= qryResults.getString("CustomerLName");
					 String customerAddress = qryResults.getString("customerPStreetAddress");
					 String customerSuburb 	= qryResults.getString("CustomerPSuburb");
					 String customerPostCode= qryResults.getString("CustomerPostCode");
					 String customerPhone 	= qryResults.getString("CustomerPhone");
					 String customerMobile 	= qryResults.getString("CustomerMobile");
					 String customerEmail 	= qryResults.getString("CustomerEmail");
					 String siteStreetAddress 	= qryResults.getString("StreetAddress");
					 String siteSuburb			= qryResults.getString("Suburb");
										
			        String str = " QUOTE:\t" + quote + "\n" +
			        		     " REES CODE:\t" + rees +"\n\n" +
		        				 " CLIENT:\t" + customerFName +  " " + customerLName + "\n\n" + 
							 	 " SITE:\t" + siteStreetAddress + "\n" +
							 	 "\t" + siteSuburb + "\n\n" + 
							 	 " POSTAL:\t" + customerAddress + "\n" +
							 	 "\t" + customerSuburb + "\n" + 
							 	 "\t" + customerPostCode + "\n\n" +
							 	 " PHONE:\t" + customerPhone + "\n" + 
							 	 " MOBILE:\t" + customerMobile + "\n\n" +
							 	 " EMAIL:\t" + customerEmail + "\n";	
			        
			        setCustName(customerFName + ' ' + customerLName);
			        setEmailAddr(customerEmail);
			        
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
	
	public ResultSet getResults(int ind){      		
		try
		{
			Connection conn = connecting.CreateConnection(conDeets);
			PreparedStatement st =conn.prepareStatement(procedure[ind]);
			results = st.executeQuery();    	
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}
		return results;       		            
	}

	public ResultSet getSearchResults(String qry, String param){      		
		try
		{
			Connection conn = connecting.CreateConnection(conDeets);
			PreparedStatement st =conn.prepareStatement(qry + param);
			results = st.executeQuery();    	
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}
		return results;       		            
	}

	//Get the results set for the customer details
	public ResultSet getDetails(String qry, String param){      	
		try
		{
			Connection conn = connecting.CreateConnection(conDeets);
			PreparedStatement st2 =conn.prepareStatement(qry + param);	
			qryResults = st2.executeQuery();
			if (qryResults==null){
				System.out.println("null query");
			}
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}
		return qryResults;       		            
	}	

	/*
	 * Clears Contents of InfoPanel on each tab when another tab is selected
	 */
    protected void ClearFields(JPanel infPanel) {
      	for(Component control : infPanel.getComponents())
      	{	//Set TextFields to ""
      	    if(control instanceof JTextField)
      	    {
      	        JTextField ctrl = (JTextField) control;
      	        ctrl.setText("");
      	    }
      	    else if (control instanceof JTextArea)
      	    {	//Set JTextArea to ""
      	    	JTextArea ctrl = (JTextArea) control;
      	        ctrl.setText("");
      	    }
      	    else if (control instanceof JComboBox)
      	    {	//Set JTextArea to ""
      	    	JComboBox ctrl = (JComboBox) control;
      	        ctrl.setSelectedIndex(0);
      	    }
      	    else if (control instanceof JCheckBox)
      	    {	//Set JTextArea to ""
      	    	JCheckBox ctrl = (JCheckBox) control;
      	        ctrl.setSelected(false);
      	    }   
      	    else if (control instanceof JSpinner)
    	    {	//Set JTextArea to ""
      	    	JSpinner ctrl = (JSpinner) control;
      	    	date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
    	        ctrl.setValue(date);
    	    }  
      	    
      	}
}

	
	
	public void showMessage(String msg) {
		hs.showMsg(msg);
	}
	
	public void setCustName(String CustName){
		custName = CustName;
	}
	
	public String getCustName(){
		return custName;
	}
	
	public void setEmailAddr(String CustEmail){
		custEmail = CustEmail;
	}
	
	public String getEmailAddr(){
		return custEmail;
	}

}