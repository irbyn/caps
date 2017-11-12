package documents;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

import DB_Comms.CreateConnection;
import Main.ConnDetails;


public class StockReceivedReport {

	public CreateConnection connecting;
	private ConnDetails conDeets;
	private ResultSet qryResults;

	private String stockRcvd   = "{CALL AWS_WCH_DB.dbo.i_InstallsReportReceived (?)}";
	private StringBuilder sb;


		public StockReceivedReport(ConnDetails conDeets)
		{   
	//		this.hs = hs;
			this.conDeets = conDeets;


			connecting = new CreateConnection();
			sb = new StringBuilder();
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
