package Installs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
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
	private String stock = "";

	private Boolean lockForm;
	private String custDetails = "EXEC AWS_WCH_DB.dbo.i_CustomerDetails";
	private String custBookings = "EXEC AWS_WCH_DB.dbo.i_CustomerBooking";
	private String stockDetails = "EXEC AWS_WCH_DB.dbo.i_StockDetails";	
	private String instNote = "EXEC AWS_WCH_DB.dbo.i_getInstallerNote";	

	private String invoice="";			
	private String rees="";
	private String customerName="";
	private String customerAddress="";
	private String customerSuburb="";
	private String customerPostCode="";
	private String customerPhone="";
	private String customerMobile="";
	private String customerEmail="";
	private String streetAddress="";
	private String suburb="";
	private String bookedDate="";
	private String bookedTime=""; 		
	private String bookedInstaller="";
	private String fromInstaller="";
	private String toInstaller="";



	// Stored procedures to fill tables (Triggered by tab selection)
	private String[] procedure = new String[]{	"EXEC AWS_WCH_DB.dbo.i_InstallsToLoad", 	// procedure[0]
			"EXEC AWS_WCH_DB.dbo.i_InstallsToCheck", 	// procedure[1]
			"EXEC AWS_WCH_DB.dbo.i_InstallsPlaceOrders",// procedure[2]
			"EXEC AWS_WCH_DB.dbo.i_InstallsReceiveOrders",//procedure[3]
	"EXEC AWS_WCH_DB.dbo.i_InstallsToBook"};	// procedure[4]

	private int[][] spacing = new int[][]	{{30, 100, 120, 80, 40, 40, 40}, 				// procedure[0]
		{30, 100, 120, 80, 40, 40, 40, 40},	 		// procedure[1]
		{20, 100, 120, 20, 80, 400}, 					// procedure[2]
		{20, 100, 100, 40, 20, 80, 300, 50}, 			// procedure[3]
		{30, 100, 100, 80, 50, 50, 50, 40}};		// procedure[4]


		public InstallsPane(ConnDetails conDeets, Homescreen hs)
		{   
			this.hs = hs;
			this.conDeets = conDeets;
			lockForm = false;

			connecting = new CreateConnection();
			sb = new StringBuilder();

			//Adding Jpanels to the SAles panel area 
			JTabbedPane installP = new JTabbedPane();
			installP.setPreferredSize(new Dimension(1070, 610));

			loadDocPnl = new LoadDocsPanel(lockForm, conDeets, this);
			checkOrderPnl = new CheckForOrdersPanel(lockForm, conDeets, this);
			placeOrderPnl = new PlaceOrdPanel(lockForm, conDeets, this);
			recvOrderPnl = new RecvOrderPanel(lockForm, conDeets, this);
			bookingPnl = new BookingsPanel(lockForm, conDeets, this, hs.getSchedule());

			JTable[] tablez = new JTable[]{loadDocPnl.getInstallTbl(), 
					checkOrderPnl.getInstallTbl(), 
					placeOrderPnl.getInstTbl(),  
					recvOrderPnl.getInstallTbl(), 
					bookingPnl.getInstallTbl()};

			JPanel[] fieldz = new JPanel[]{	loadDocPnl.getInfoPanel(), 
					checkOrderPnl.getInfoPanel(), 
					placeOrderPnl.getInfoPanel(),  
					recvOrderPnl.getInfoPanel(), 
					bookingPnl.getInfoPanel()};

			installP.addTab("Load Documents", loadDocPnl);
			installP.addTab("Check for Orders", checkOrderPnl);
			installP.addTab("Place Orders", placeOrderPnl);
			installP.addTab("Receive Orders", recvOrderPnl);
			installP.addTab("Bookings", bookingPnl);
			add(installP); 

			installP.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (e.getSource() instanceof JTabbedPane) {
						//  	if(!lockForm){

						ClearFields(fieldz[tabIndex]);
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
		//	}

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


		public String DisplayClientDetails(String instNumber){

			try
			{
				Connection conn = connecting.CreateConnection(conDeets);
				PreparedStatement st2 =conn.prepareStatement(custDetails + ' ' +  instNumber);	    	
				qryResults = st2.executeQuery();
				if (qryResults==null){

					JOptionPane.showMessageDialog(null, "null query");
				}
				else{

					while(qryResults.next()){

						invoice 			= qryResults.getString("Invoice");
						rees				= qryResults.getString("Rees");
						customerName 		= qryResults.getString("CustomerName");
						customerAddress  	= qryResults.getString("CustomerAddress");
						customerSuburb 		= qryResults.getString("CustomerSuburb");
						customerPostCode 	= qryResults.getString("CustomerPostCode");
						customerPhone 		= qryResults.getString("CustomerPhone");
						customerMobile 		= qryResults.getString("CustomerMobile");
						customerEmail 		= qryResults.getString("CustomerEmail");
						streetAddress 		= qryResults.getString("StreetAddress");
						suburb 				= qryResults.getString("Suburb");

						str = 	" INVOICE:\t" + instNumber + "\n" +
								" REES CODE:\t" + rees +"\n" +
								" CLIENT:\t" + customerName + "\n\n" + 
								" SITE:\t" + streetAddress + "\n" +
								"\t" + suburb + "\n\n" + 
								" POSTAL:\t" + customerAddress + "\n" +
								"\t" + customerSuburb + "\n" + 
								"\t" + customerPostCode + "\n\n" +
								" PHONE:\t" + customerPhone + "\n" + 
								" MOBILE:\t" + customerMobile + "\n\n" +
								" EMAIL:\t" + customerEmail;
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

		public String DisplayClientShortDetails(String instNumber){

			try
			{
				Connection conn = connecting.CreateConnection(conDeets);
				PreparedStatement st2 =conn.prepareStatement(custBookings + ' ' +  instNumber);	    	
				qryResults = st2.executeQuery();
				if (qryResults==null){

					JOptionPane.showMessageDialog(null, "null query");
				}
				else{
					while(qryResults.next()){

						invoice 			= qryResults.getString("Invoice");
						rees				= qryResults.getString("Rees");
						customerName 		= qryResults.getString("CustomerName");
						customerPhone 		= qryResults.getString("CustomerPhone");
						customerMobile 		= qryResults.getString("CustomerMobile");
						customerEmail 		= qryResults.getString("CustomerEmail");
						bookedTime 			= qryResults.getString("bookedTime");
						bookedInstaller		= qryResults.getString("bookedInstaller");
						fromInstaller		= qryResults.getString("fromInstaller");

						str = 	" INVOICE:\t" + instNumber + "\n" +
								" REES CODE:\t" + rees +"\n" +
								" CLIENT:\t" + customerName + "\n\n" + 
								" PHONE:\t" + customerPhone + "\n" + 
								" MOBILE:\t" + customerMobile + "\n" +
								" EMAIL:\t" + customerEmail;	        		
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

		public String DisplayNoteToInstaller(String instNumber){

			try
			{
				Connection conn = connecting.CreateConnection(conDeets);
				PreparedStatement st2 =conn.prepareStatement(instNote + ' ' +  instNumber);	    	
				qryResults = st2.executeQuery();
				if (qryResults==null){

					JOptionPane.showMessageDialog(null, "null query");
				}
				else{
					while(qryResults.next()){

						toInstaller		= qryResults.getString("toInstaller");

						/*	        str = 	" INVOICE:\t" + instNumber + "\n" +
			        				" REES CODE:\t" + rees +"\n" +
			        				" CLIENT:\t" + customerName + "\n\n" + 
			        				" PHONE:\t" + customerPhone + "\n" + 
			        				" MOBILE:\t" + customerMobile + "\n" +
			        				" EMAIL:\t" + customerEmail;	        	*/	
						return toInstaller;
					}
				}
			}
			catch(Exception ex)
			{ 
				JOptionPane.showMessageDialog(null, ex.toString());
			}      		            
			return "";
		}

		public String DisplayStockOnOrder(String instNumber){

			try
			{
				Connection conn = connecting.CreateConnection(conDeets);
				PreparedStatement st2 =conn.prepareStatement(stockDetails + ' ' +  instNumber);	    	
				qryResults = st2.executeQuery();
				if (qryResults==null){
					JOptionPane.showMessageDialog(null, "null query");
				}
				else{        		            		
					stock = " PO:\tStock Item\t\n";
					String po 	= "";
					String desc = "";
					while(qryResults.next()){
						po 			= qryResults.getString("POrd");
						desc 		= qryResults.getString("Stock");
						stock = stock + po + "\t" + desc + "\n";
					}	     
					stock = "NOTE TO INSTALLER: \n" + DisplayNoteToInstaller(instNumber) + "\n\n" + stock;
					return stock;				
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
				stmt.execute(update); 

				JOptionPane.showMessageDialog(null, "Permit "+ param + " Updated successfully");
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

		public String getInstTime(){
			return bookedTime;
		}
		public String getInstaller(){
			return bookedInstaller;
		}
		public String getNoteFromInstaller(){
			return fromInstaller;
		}


}
