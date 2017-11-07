package Permit;

/*
 * class to validate user entries when there are multiple fields to check 
 */

import java.awt.Component;
import java.util.Calendar;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;


public class validator {

	private String msg;
	private Boolean validEntries;
	private PermitsReqPanel req;
	private String str;
	
	public  validator(PermitsReqPanel pReq){
		this.req = pReq;
	}
	
	
	public void validatePermit() {
		  
		validEntries = true;
		msg = "";	
		
		if(req.getLot().length()>30){
			validEntries = false;
			msg = msg + "LOT: can not be longer than 30 letters\n";
		}
		if(req.getDP().length()>30){
			validEntries = false;
			msg = msg + "DP: can not be longer than 30 letters\n";
		}
		if(req.getConsent().length()>16){
			validEntries = false;
			msg = msg + "CONSENT: can not be longer than 16 letters\n";
		}
		if(req.getBuilding().length()>30){
			validEntries = false;
			msg = msg + "BUILDING NAME: can not be longer than 30 letters\n";
		}
		if(req.getLevel().length()>16){
			validEntries = false;
			msg = msg + "LEVEL: can not be longer than 16 letters\n";
		}	
		if(req.getFireLocation().length()>30){
			validEntries = false;
			msg = msg + "LOCATION: can not be longer than 16 letters\n";
		}
		str = req.getValue();
		if (str.isEmpty()){
    		validEntries = false;
    		msg = msg + "VALUE: Please enter a Value\n";
    	} else {
			if(str.contains("$")){
				int dollar = str.indexOf("$");
				String before = str.substring(0, dollar);
				String after = str.substring(dollar+1, str.length());
				req.setValue(before + after);
				str = before + after;
			}
			try {
            	int value = Integer.parseInt(str);
            	if ((2000 > value) || (value >= 16000)){
            		validEntries = false;
            		msg = msg + "VALUE: This entry is not in a valid range (2000-15000)\n";
            	}
        	} 
        catch (NumberFormatException nfe) {
        		//If it is not a number...
            	validEntries = false;
            	msg = msg + "VALUE: This entry is not a valid value\n";
        	}
		}		
		if(!req.getYear().isEmpty()){
			try {
            	int year = Integer.parseInt(req.getYear());
            	int thisYear = Calendar.getInstance().get(Calendar.YEAR);//get current year
            	//year must be between 1880 & this year
            	if ((1880 > year) || (year >= thisYear)){
            		validEntries = false;
            		msg = msg + "YEAR: This entry is not a valid year\n";
            	}
        	} 
			catch (NumberFormatException nfe) {
        		//If it is not a number...
            	validEntries = false;
            	msg = msg + "YEAR: Please enter Numbers only\n";
        	}
		}
	}
	
	public void validateFire() {
		  
		validEntries = true;
		msg = "def";	
		
		if(req.getFireCode().length()>20){
			validEntries = false;
			msg = msg + "FIRE CODE: can not be longer than 20 letters\n";
		}
		if(req.getMake().length()>20){
			validEntries = false;
			msg = msg + "MAKE: can not be longer than 20 letters\n";
		}
		if(req.getModel().length()>20){
			validEntries = false;
			msg = msg + "MODEL: can not be longer than 20 letters\n";
		}
		if(req.getEcan().length()>20){
			validEntries = false;
			msg = msg + "ECAN: can not be longer than 20 letters\n";
		}	
		if(req.getNelson().length()>20){
			validEntries = false;
			msg = msg + "NELSON: can not be longer than 20 letters\n";
		}	
	}

		
		public Boolean getValidEntries(){
			return validEntries;
		}
		public String getMsg(){
			return msg;
		}	
	}
