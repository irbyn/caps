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

	// Stored procedures to fill tables (Triggered by tab selection)
	private String[] procedure = new String[]{	"EXEC AWS_WCH_DB.dbo.s_Customer", // procedure[0]
			"EXEC AWS_WCH_DB.dbo.s_Estimation", // procedure[1]
			"EXEC AWS_WCH_DB.dbo.s_SiteCheck", // procedure[2]
			"EXEC AWS_WCH_DB.dbo.s_Quote", // procedure[3]
			"EXEC AWS_WCH_DB.dbo.s_FollowUp"};// procedure[4]

	public SalesPane(ConnDetails conDeets)
	{

		lockForm = false;

		connecting = new CreateConnection();

		//Adding Jpanels to the SAles panel area 
		JTabbedPane salesP = new JTabbedPane();
		salesP.setPreferredSize(new Dimension(1070, 610));

		customer = new CustomerPanel(conDeets, this);
		estimation = new EstimationPanel(conDeets, this);
		followUp = new FollowUpPanel(conDeets, this);
		quote = new QuotePanel(conDeets, this);
		siteCheck = new SiteCheckPanel(conDeets, this);

		JTable[] tablez = new JTable[]{customer.getSalesTbl(), 
										estimation.getSalesTbl(), 
										followUp.getSalesTbl(), 
										quote.getSalesTbl(), 
										siteCheck.getSalesTbl()};

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
					//       ResultSet r1 = results;

					System.out.println(tablez.length);
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
        
        public ResultSet getDetails(String qry, String param, ConnDetails connDeets){      	
        	
            try
	        {
	        	Connection conn = connecting.CreateConnection(connDeets);
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
  

	
}




