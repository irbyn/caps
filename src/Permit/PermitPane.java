package Permit;

import java.awt.Component;
import java.awt.Dimension;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import Main.*;
import DB_Comms.*;
import Permit.*;
import net.proteanit.sql.DbUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class PermitPane extends JPanel
{	
	public CreateConnection connecting;
	
	private ConnDetails conDeets;
	
	private ResultSet results;
	private ResultSet qryResults;
	private int tabIndex = 0;
	
	//Panels loaded in Tabs
	private PermitsReqPanel permitReq;
	private RecvPermitPanel permitRecv;
	private ProdStatementPanel prodStmnt;
//	private CCCToCounPanel cccToCouncil;
	private CCCApprovedPanel cccApproved;
	private CCCToClientPanel cccToClient;
	
	private Boolean lockForm;
	private String custDetails = "EXEC AWS_WCH_DB.dbo.p_CustomerDetails";
	
	// Stored procedures to fill tables (Triggered by tab selection)
	private String[] procedure = new String[]{	"EXEC AWS_WCH_DB.dbo.p_PermitsRequired", // procedure[0]
												"EXEC AWS_WCH_DB.dbo.p_PermitsReceived", // procedure[1]
												"EXEC AWS_WCH_DB.dbo.p_PermitsProdStat", // procedure[2]
											//	"EXEC AWS_WCH_DB.dbo.p_PermitsCCC_Council", // procedure[3]
												"EXEC AWS_WCH_DB.dbo.p_PermitsCCC_Apprvd", // procedure[4]
												"EXEC AWS_WCH_DB.dbo.p_PermitsCCC_Client"};// procedure[5]

	
        public PermitPane(ConnDetails conDeets)
        {   
        	this.conDeets = conDeets;
        	lockForm = false;
    		
  		  	connecting = new CreateConnection();
        	
    		//Adding Jpanels to the SAles panel area 
    		JTabbedPane permitP = new JTabbedPane();
    		permitP.setPreferredSize(new Dimension(1070, 610));
 
    		permitReq = new PermitsReqPanel(lockForm, conDeets, this);
    		permitRecv = new RecvPermitPanel(lockForm, conDeets, this);
    		prodStmnt = new ProdStatementPanel(lockForm, conDeets, this);
   // 		cccToCouncil = new CCCToCounPanel(conDeets, this);
    		cccApproved = new CCCApprovedPanel(lockForm, conDeets, this);
    		cccToClient = new CCCToClientPanel(conDeets, this);
    		
    		 JTable[] tablez = new JTable[]{permitReq.getPermitsTbl(), 
    				 						permitRecv.getPermitsTbl(), 
    				 						prodStmnt.getPermitsTbl(), 
  //  				 						cccToCouncil.getPermitsTbl(), 
    				 						cccApproved.getPermitsTbl(), 
    				 						cccToClient.getPermitsTbl()};
    
    		permitP.addTab("Permits Required", permitReq);
    		permitP.addTab("Receive Permits", permitRecv);
    		permitP.addTab("Producer Statement", prodStmnt);
  //  		permitP.addTab("CCC to Council", cccToCouncil);
    		permitP.addTab("CCC Approved", cccApproved);
    		permitP.addTab("CCC to Client", cccToClient);
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
                 //       ResultSet r1 = results;
                        
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
//                }
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

}
