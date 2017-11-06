package Schedule;

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
import Main.Homescreen;
import Schedule.TimeTablePanel;
import net.proteanit.sql.DbUtils;

public class SchedulePane extends JPanel
{

	public CreateConnection connecting;

	private Homescreen hs;
	private ConnDetails conDeets;

	private ResultSet results;
	private ResultSet qryResults;
	private int tabIndex = 0;

	//Panels loaded in Tabs
	private TimeTablePanel ttpanel;
	private ViewSiteChecksPanel scpanel;


	private Boolean lockForm;
	private String custDetails = "EXEC AWS_WCH_DB.dbo.p_CustomerDetails";

	private String sunDate = "'2017-10-01"; //creates a default date for schedule

	// Stored procedures to fill tables (Triggered by tab selection)
	private String[] procedure = new String[]{	"EXEC AWS_WCH_DB.dbo.sc_ViewSchedule ", // procedure[0]
	"EXEC AWS_WCH_DB.dbo.sc_ViewSiteChecks "}; // procedure[1]
	private int[][] spacing = new int[][]	{	{80, 20, 50, 100, 100, 100, 100, 100, 50}, 	// procedure[0]
		{40, 100, 100, 60, 60, 60, 60}};	 		// procedure[1]

		public SchedulePane(ConnDetails conDeets, Homescreen hs) {	


			this.hs = hs;
			this.conDeets = conDeets;
			lockForm = false;    		
			connecting = new CreateConnection();

			//Adding Jpanels to the SAles panel area 
			JTabbedPane scheduleP = new JTabbedPane();
			scheduleP.setPreferredSize(new Dimension(1070, 610));

			ttpanel = new TimeTablePanel(lockForm, conDeets, this);
			scpanel = new ViewSiteChecksPanel(lockForm, conDeets, this);


			JTable[] tablez = new JTable[]{ttpanel.getScheduleTbl(),scpanel.getScheduleTbl()};	    
			scheduleP.addTab("Installations", ttpanel);
			scheduleP.addTab("Site Checks", scpanel);

			add(scheduleP); 

			scheduleP.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (e.getSource() instanceof JTabbedPane) {
						//  	if(!lockForm){

						JTabbedPane pane = (JTabbedPane) e.getSource();
						tabIndex = pane.getSelectedIndex();

						getResults(tabIndex, sunDate); 

						tablez[tabIndex].setModel(DbUtils.resultSetToTableModel(results));  
						if (tabIndex==0){
							ttpanel.renderTable();
						}
						TableColumnModel tcm = tablez[tabIndex].getColumnModel();
						int cols = tcm.getColumnCount();

						if (cols == 9){
							int[] colWidths = new int[]{80, 20, 50, 100, 100, 100, 100, 100, 50};
							spaceHeader(colWidths, tcm);
						}else {
							int[] colWidths = new int[]{20, 80, 100, 80, 30, 30, 40, 40, 60, 30, 30};    
							spaceHeader(colWidths, tcm);
						}
					}
				}
				//	                }
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


		public ResultSet getResults(int ind, String sunDate){      	
			this.sunDate = sunDate;
			try
			{
				Connection conn = connecting.CreateConnection(conDeets);
				PreparedStatement st =conn.prepareStatement(procedure[ind] + sunDate);	//ind]);


				results = st.executeQuery();
				if (results==null){
					getResults(0, sunDate);
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

}
