package Permit;

/*
 * GUI PANEL:	PERMITS - JPanel to hold tabs
 * acts as a controller for some common functionality for PERMIT tabs 
 */

import java.awt.Component;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import Main.*;
import DB_Comms.*;
import net.proteanit.sql.DbUtils;

public class PermitPane extends JPanel
{	
	public CreateConnection connecting;

	private Homescreen hs;
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

	private int[][] spacing = new int[][]	{{20, 150, 150, 100, 100, 100}, 				// procedure[0]
											{20, 120, 160, 100, 100, 100},					// procedure[1]
											{20, 80, 100, 80, 30, 30, 40, 40, 60, 30, 30}, 	// procedure[2]
											{30, 100, 120, 80, 40, 40, 40, 40, 40}, 		// procedure[3]
											{30, 100, 120, 80, 40, 40, 40, 40, 40}};		// procedure[4]



	public PermitPane(ConnDetails conDeets, Homescreen hs)
	{   
		this.hs = hs;
		this.conDeets = conDeets;
		lockForm = false;
		connecting = new CreateConnection();

		JTabbedPane permitP = new JTabbedPane();
		permitP.setPreferredSize(new Dimension(1070, 610));

		permitReq = new PermitsReqPanel(lockForm, conDeets, this);
		permitRecv = new RecvPermitPanel(lockForm, conDeets, this);
		prodStmnt = new ProdStatementPanel(lockForm, conDeets, this);
		// 		cccToCouncil = new CCCToCounPanel(conDeets, this);
		cccApproved = new CCCApprovedPanel(lockForm, conDeets, this);
		cccToClient = new CCCToClientPanel(lockForm, conDeets, this);

		//Array to link JTable on each tab
		JTable[] tablez = new JTable[]{permitReq.getPermitsTbl(), 
										permitRecv.getPermitsTbl(), 
										prodStmnt.getPermitsTbl(), 
										//  cccToCouncil.getPermitsTbl(), 
										cccApproved.getPermitsTbl(), 
										cccToClient.getPermitsTbl()};
		//Array to link InfoPanel on each tab (User entry fields) to allow clearing of entries
		JPanel[] fieldz = new JPanel[]{	permitReq.getInfoPanel(), 
										permitRecv.getInfoPanel(), 
										prodStmnt.getInfoPanel(),  
										cccApproved.getInfoPanel(), 
										cccToClient.getInfoPanel()};

		permitP.addTab("Permits Required", permitReq);
		permitP.addTab("Receive Permits", permitRecv);
		permitP.addTab("Producer Statement", prodStmnt);
		//  		permitP.addTab("CCC to Council", cccToCouncil);
		permitP.addTab("CCC Approved", cccApproved);
		permitP.addTab("CCC to Client", cccToClient);
		add(permitP); 

		permitP.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() instanceof JTabbedPane) {
					//  	if(!lockForm){
					ClearFields(fieldz[tabIndex]);
					JTabbedPane pane = (JTabbedPane) e.getSource();
					tabIndex = pane.getSelectedIndex();
					getResults(tabIndex); 
					tablez[tabIndex].setModel(DbUtils.resultSetToTableModel(results));         
					TableColumnModel tcm = tablez[tabIndex].getColumnModel();					
					spaceHeader(spacing[tabIndex], tcm); 					
					int cols = tcm.getColumnCount();
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

	public void showMessage(String msg) {
		hs.showMsg(msg);
	}

	private void spaceHeader(int[] widths, TableColumnModel tcm){
		int cols = tcm.getColumnCount();
		for (int i = 0; i < cols; i++){
			tcm.getColumn(i).setPreferredWidth(widths[i]);
		}
	}

	// Fills client details in textArea (bottom Left of GUI)
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


	public ResultSet getResults(int ind){      	

		try
		{
			Connection conn = connecting.CreateConnection(conDeets);
			PreparedStatement st =conn.prepareStatement(procedure[ind]);	//ind]);
			results = st.executeQuery();
			if (results==null){
				getResults(0);
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
	
	// clears all fields in tab when a new tab is selected
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
			else if (control instanceof JPanel)
			{	//IF component is a JPanel
				Component s[] = ((JPanel) control).getComponents();
				for (int j = 0; j < s.length; j++) {
					//Find ScrollPane
					if (s[j] instanceof JScrollPane)
					{// Look into scrollPane components
						JViewport view = ((JScrollPane) s[j]).getViewport();

						Component v[] = view.getComponents();
						for (int k = 0; k < v.length; k++) {
							// if Component is a JTable
							if(v[k] instanceof JTable)
							{	//Get Table model & set Rowcount to 0
								JTable tbl = (JTable) v[k];
								TableModel tm = tbl.getModel();
								((DefaultTableModel) tm).setRowCount(0);
							}
						}
					}
				} 
			}
		}
	}	
}
