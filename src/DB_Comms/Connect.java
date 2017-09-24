package DB_Comms;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect {
	
//	   static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	   static final String dbURL = "jdbc:sqlserver://wchdb.cnfoxyxq90wv.ap-southeast-2.rds.amazonaws.com:1433";

	   static final String user = "Khgv92367hdkfug9";
	   static final String pass = "Locei02h84b5KJUVaW";
	
	   public static void main(String[] args) {
		   
	        Connection conn = null;
	        Statement stmt = null;
	 
	        try {
	            
	            System.out.println("Connecting to a selected database...");
	            conn = DriverManager.getConnection(dbURL, user, pass);
	            
	            if (conn != null) {
	            
	            stmt = conn.createStatement();
	            
	            String sql = "SELECT CustomerID as CustomerID, "
	            		+ "FirstName AS FirstName, "
	            		+ "LastName AS LastName, "
	            		+ "PostalAddress AS PostalAddress, "
	            		+ "PostalSuburb AS PostalSuburb "
	            		+ "FROM AWS_WCH_DB.dbo.Customer";
	            
	            ResultSet rs2 = stmt.executeQuery(sql);
	                  
            while(rs2.next()){
	                //Retrieve by column name
	                int id  = rs2.getInt("CustomerID");
	                String first = rs2.getString("FirstName");
	                String last = rs2.getString("LastName");
	                String address = rs2.getString("PostalAddress");
	                String suburb = rs2.getString("PostalSuburb");

	              //Display values
	    //            System.out.println("%-2s%-16s%-16s%-30s%-30s", id, first, last, address, suburb + "%n"); 
	                
	                //Display values
	                System.out.print("ID: " + id);
	                System.out.print(",\t" + first);
	                System.out.print(",\t" + last);
	                System.out.print(",\t" + address);
	                System.out.println(",\t" + suburb);
	             }
            System.out.println("");	             
	            rs2.close();
	           
	            
	            }
	            
	 
	        } catch (SQLException ex) {
	          ex.printStackTrace();
	//        } catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
	//			e1.printStackTrace();
			} finally {
	            try {
	                if (conn != null && !conn.isClosed()) {
	                    conn.close();
	                    System.out.println("Connection Closed...");
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	                System.out.println("Connection Exception...");
	            }
	        }
	 	   //	System.out.println("KGIGIYGKGVKH");   
	   }
	   
	}