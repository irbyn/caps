package Main;

/*
 * Fills combo boxes for staff able to perform different roles,
 * Installers, Salespeople & Sitecheckers 
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JOptionPane;

import DB_Comms.CreateConnection;


public class GetJobs {

	private String installers = "EXEC AWS_WCH_DB.dbo.m_getInstallers";
	private String sellers = "EXEC AWS_WCH_DB.dbo.m_getSellers";
	private String checkers = "EXEC AWS_WCH_DB.dbo.m_getCheckers";
	private String installTypes = "EXEC AWS_WCH_DB.dbo.m_getInstallType";
	public CreateConnection connecting;
	private String[] users; 

	private Homescreen hs;
	private ConnDetails conDeets;

	private ResultSet results;
	private ResultSet qryResults;	
	private int i = 0;


	public GetJobs(ConnDetails conDeets){
		this.conDeets = conDeets;
		connecting = new CreateConnection();

	}

	public String[] getInstallers(){
		i=0;
		users = new String[10];
		try
		{
			Connection conn = connecting.CreateConnection(conDeets);
			PreparedStatement st2 =conn.prepareStatement(installers);	    	
			qryResults = st2.executeQuery();
			if (qryResults==null){

				JOptionPane.showMessageDialog(null, "null query");
			}
			else{
				while(qryResults.next()){

					String installer 	= qryResults.getString("INST");

					users[i] = installer;
					i++;
				}
			}
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}      		            
		return users;
	}


	public String[] getSiteChecker(){
		i=0;
		users = new String[10];
		try
		{
			Connection conn = connecting.CreateConnection(conDeets);
			PreparedStatement st2 =conn.prepareStatement(checkers);	    	
			qryResults = st2.executeQuery();
			if (qryResults==null){

				JOptionPane.showMessageDialog(null, "null query");
			}
			else{
				while(qryResults.next()){

					String installer 	= qryResults.getString("SC");

					users[i] = installer;
					i++;
				}
			}
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}      		            
		return users;
	}


	public String[] getSales(){
		i=0;
		users = new String[10];
		try
		{
			Connection conn = connecting.CreateConnection(conDeets);
			PreparedStatement st2 =conn.prepareStatement(sellers);	    	
			qryResults = st2.executeQuery();
			if (qryResults==null){

				JOptionPane.showMessageDialog(null, "null query");
			}
			else{
				while(qryResults.next()){

					String installer 	= qryResults.getString("SELL");

					users[i] = installer;
					i++;
				}
			}
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}      		            
		return users;
	}

	public String[] getInstallType(){
		i=0;
		users = new String[10];
		try
		{
			Connection conn = connecting.CreateConnection(conDeets);
			PreparedStatement st2 =conn.prepareStatement(installTypes);	    	
			qryResults = st2.executeQuery();
			if (qryResults==null){

				JOptionPane.showMessageDialog(null, "null query");
			}
			else{
				while(qryResults.next()){

					String installer 	= qryResults.getString("InstallDescription");

					users[i] = installer;
					i++;
				}
			}
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}      		            
		return users;
	}

}


