package Permit;

import java.awt.Component;
import java.awt.Dimension;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
	private ResultSet results;
	private int tabIndex = 0;
	
	private PermitsReqPanel permitReq;
	private RecvPermitPanel permitRecv;
	private ProdStatementPanel prodStmnt;
	private CCCToCounPanel cccToCouncil;
	private CCCApprovedPanel cccApproved;
	private CCCToClientPanel cccToClient;

	private String[] procedure = new String[]{	"EXEC AWS_WCH_DB.dbo.p_PermitsRequired", // procedure[0]
												"EXEC AWS_WCH_DB.dbo.p_PermitsReceived", // procedure[1]
												"EXEC AWS_WCH_DB.dbo.p_PermitsRequired", // procedure[2]
												"EXEC AWS_WCH_DB.dbo.p_PermitsRequired", // procedure[3]
												"EXEC AWS_WCH_DB.dbo.p_PermitsRequired", // procedure[4]
												"EXEC AWS_WCH_DB.dbo.p_PermitsRequired"};// procedure[5]

	
        public PermitPane(ConnDetails conDeets)
        {   
        	
        	//Get User connection details
    		user = conDeets.getUser();
    		pass = conDeets.getPass();
    		dbURL = conDeets.getURL();
    		
  		  connecting = new CreateConnection();
        	
    		//Adding Jpanels to the SAles panel area 
    		JTabbedPane permitP = new JTabbedPane();
    		permitP.setPreferredSize(new Dimension(1070, 610));
 
    		permitReq = new PermitsReqPanel(conDeets, this);
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

    	//	getResults(0);  
    		
    		permitP.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    if (e.getSource() instanceof JTabbedPane) {
                        JTabbedPane pane = (JTabbedPane) e.getSource();
                        tabIndex = pane.getSelectedIndex();
                        
                        getResults(tabIndex); 
                        ResultSet r1 = results;
                        
                        tablez[tabIndex].setModel(DbUtils.resultSetToTableModel(results));               

/*                		permitReq.spaceHeader();
                		permitRecv.spaceHeader();
                		prodStmnt.spaceHeader();
                		cccToCouncil.spaceHeader();
                		cccApproved.spaceHeader();
                		cccToClient.spaceHeader();
       */                 

                    }
                }
            });
    		
    		
        }
        
        
        private ResultSet getResults(int ind){
            try
	        {
	        	Connection conn = connecting.CreateConnection();
	        	PreparedStatement st =conn.prepareStatement(procedure[ind]);	//ind]);
	        	results = st.executeQuery();
	        }
	        catch(Exception ex)
	        { 
	        JOptionPane.showMessageDialog(null, ex.toString());
	        }
        		return results;       		            
        }
        
        
        
        public ResultSet getTableData(){
        	if (results==null){
        		getResults(0);
        		return results;
        	}else {

        		return results;
        	}       	
        }
               
}
