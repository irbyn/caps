package Main;

import java.awt.Component;
import java.util.Calendar;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

public class validator {

	private String msg;
	private Boolean validEntries;
	
	
	public void validatePermit(JTextField[] jp) {
			  
		validEntries = true;
		msg = "";
		
		validateValue(jp[0]);
		validateYear(jp[1]);
		validateString16(jp[2]);
		validateString16(jp[3]);
		
			for(int i = 4; i<13; i++)
		      	{
		//		validateString30(jp[i]);
		      	}
 /* 	        
		  	        if (!validEntries){
		  	        	 JOptionPane.showMessageDialog(null, msg);
		  	        }else{
		  	        	JOptionPane.showMessageDialog(null, "SAVE TO DB!");
		  	        } 
*/		  }
		  

		private void validateYear(JTextField ctrl2) {
			//get user entry
			String yr = ctrl2.getText();
			//year can be blank
			if (!yr.isEmpty()){
				//attempt to convert it to an int
		        try {
		            	int year = Integer.parseInt(yr);
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

		private void validateValue(JTextField ctrl2) {
			//get user entry
			String vl = ctrl2.getText();
			//Value can not be blank
			if (vl.isEmpty()){
	    		validEntries = false;
	    		msg = msg + "VALUE: Please enter a Value\n";
	    	}			
			else {
				if(vl.contains("$")){
					int dollar = vl.indexOf("$");
					String before = vl.substring(0, dollar);
					String after = vl.substring(dollar+1, vl.length());
					ctrl2.setText(before + after);
					vl = before + after;
				}
				try {
	            	int value = Integer.parseInt(vl);
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
		}

		  private void validateString16(JTextField ctrl2) {
			// TODO Auto-generated method stub
			
		}

		private void validateString30(JTextField ctrl2) {
			// TODO Auto-generated method stub
			
		}
		
		public String getmsg(){
			return msg;
		}
		
		public Boolean getValidEntries(){
			return validEntries;
		}

		
	}
