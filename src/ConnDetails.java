
/*
 * Details of current Login to be stored here to provide connection data for 
 * app comms
 * Security protocols to be developed!
 */
public class ConnDetails {
	
	private String user = "default";
	private String pass = "default";
	
	private ConnDetails(){
		
	}

	ConnDetails(String usr, String pwd){
		this.user = usr;
		this.pass = pwd;		
	}
	
	
	public String getUser(){
		return this.user;
	}
	public String getPass(){
		return this.pass;
	}
}