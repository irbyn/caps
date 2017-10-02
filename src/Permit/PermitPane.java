package Permit;

import java.awt.Component;
import java.awt.Dimension;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	private String user = "";
	private String pass = "";
	private String dbURL = "";
	private CreateConnection connecting;
	
	private ConnDetails conDeets;
	
	private ResultSet results;
	private ResultSet qryResults;
	private int tabIndex = 0;
	
	//Panels loaded in Tabs
	private PermitsReqPanel permitReq;
	private RecvPermitPanel permitRecv;
	private ProdStatementPanel prodStmnt;
	private CCCToCounPanel cccToCouncil;
	private CCCApprovedPanel cccApproved;
	private CCCToClientPanel cccToClient;
	
	private Boolean lockForm;
	
	// Stored procedures to fill tables (Triggered by tab selection)
	private String[] procedure = new String[]{	"EXEC AWS_WCH_DB.dbo.p_PermitsRequired", // procedure[0]
												"EXEC AWS_WCH_DB.dbo.p_PermitsReceived", // procedure[1]
												"EXEC AWS_WCH_DB.dbo.p_PermitsProdStat", // procedure[2]
												"EXEC AWS_WCH_DB.dbo.p_PermitsCCC_Council", // procedure[3]
												"EXEC AWS_WCH_DB.dbo.p_PermitsCCC_Apprvd", // procedure[4]
												"EXEC AWS_WCH_DB.dbo.p_PermitsCCC_Client"};// procedure[5]

	
        public PermitPane(ConnDetails conDeets)
        {   

        	lockForm = false;
    		
  		  connecting = new CreateConnection();
        	
    		//Adding Jpanels to the SAles panel area 
    		JTabbedPane permitP = new JTabbedPane();
    		permitP.setPreferredSize(new Dimension(1070, 610));
 
    		permitReq = new PermitsReqPanel(lockForm, conDeets, this);
    		permitRecv = new RecvPermitPanel(conDeets, this);
    		prodStmnt = new ProdStatementPanel(conDeets, this);
    		cccToCouncil = new CCCToCounPanel(conDeets, this);
    		cccApproved = new CCCApprovedPanel(conDeets, this);
    		cccToClient = new CCCToClientPanel(conDeets, this);
    		
    		 JTable[] tablez = new JTable[]{permitReq.getPermitsTbl(), 
    				 						permitRecv.getPermitsTbl(), 
    				 						prodStmnt.getPermitsTbl(), 
    				 						cccToCouncil.getPermitsTbl(), 
    				 						cccApproved.getPermitsTbl(), 
    				 						cccToClient.getPermitsTbl()};
    
    		permitP.addTab("Permits Required", permitReq);
    		permitP.addTab("Receive Permits", permitRecv);
    		permitP.addTab("Producer Statement", prodStmnt);
    		permitP.addTab("CCC to Council", cccToCouncil);
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
