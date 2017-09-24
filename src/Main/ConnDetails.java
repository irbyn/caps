package Main;

/*
 * Details of current Login to be stored here to provide connection data for 
 * app comms
 * Security protocols to be developed!
 */
public class ConnDetails {
	static final String dbURL = "jdbc:sqlserver://wchdb.cnfoxyxq90wv.ap-southeast-2.rds.amazonaws.com:1433";
	private String user = "default";
	private String pass = "default";
	
	public ConnDetails(){
		
	}

	public ConnDetails(String usr, String pwd){
		this.user = usr;
		this.pass = pwd;		
	}
	
	public String getURL(){
		return this.dbURL;
	}
	public String getUser(){
		return this.user;
	}
	public String getPass(){
		return this.pass;
	}
}