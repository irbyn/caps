package Main;

/*
 * Details of current Login to be stored here to provide connection data for 
 * app comms
 * Security protocols to be developed!
 */
public class ConnDetails {
	
	private String dbURL;
	private String user;
	private String pass;
	
	public ConnDetails(){
/*
		dbURL = "jdbc:sqlserver://wchonaws.cnfoxyxq90wv.ap-southeast-2.rds.amazonaws.com:47947";
//		dbURL = "jdbc:sqlserver://wch-db.cnfoxyxq90wv.ap-southeast-2.rds.amazonaws.com:47947";
		user = "LKiy63iLFuh23ci5";
		pass = "Lsubibigxuiohaoha97sH";
	*/
//		dbURL = "jdbc:sqlserver://wchonaws.cnfoxyxq90wv.ap-southeast-2.rds.amazonaws.com:47947";
		dbURL = "jdbc:sqlserver://wch-db.cnfoxyxq90wv.ap-southeast-2.rds.amazonaws.com:47947";
		user = "nhfnKGF519hinsd897665465jbsdsf";
		pass = "NJiugi7btdjyFbe99n9n09UU";
	}

/*	public ConnDetails(String usr, String pwd){
		this.user = usr;
		this.pass = pwd;		
	}
	*/
	
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