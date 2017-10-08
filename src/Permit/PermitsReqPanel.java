package Permit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import net.proteanit.sql.DbUtils;
import DB_Comms.CreateConnection;
import Main.*;


class PermitsReqPanel extends JPanel {
	
	private int [] columnWidth = {20, 150, 150, 100, 100, 100};
	private String qry = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
	private String qry2 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
								
	private String upConsent = "call AWS_WCH_DB.dbo.p_PermitUpdateConsent ";
	private String upSent = "{call AWS_WCH_DB.dbo.p_PermitUpdateSent (?)}";
	private String upFire = "{call AWS_WCH_DB.dbo.p_PermitUpdateFire (?,?,?,?,?,?,?,?)}";
	
	private CreateConnection connecting;
	
	private String param = "";  
	private ResultSet rs;
	private ResultSet rs2;
	private ResultSet rs3;
	private Boolean rowSelected;
	private Boolean validEntries = true;	
	private String msg ="";
	
//	private JTextField ctrl;
	
	private String[] doctype = {"NONE","[Rates Notice]","[Certificate of Title]", "[Lease Agreement]", "[Sale & Purchase]", "Other"};
	private String[] firestyle = {"FS","IS", "IB","Other"};
	private String[] fueltype = {"Wood","Pellet", "Oil","Other"};
	
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable permitsTbl;
	private DefaultTableModel model1;
	
	private JTextArea detailsTxtArea;	
	private JLabel lotLbl;
	private JTextField lotTxtBx;
	private JLabel dpLbl;
	private JTextField dpTxtBx;
	private JLabel consentLbl;
	private JTextField consentTxtBx;
	
	private JLabel buildingLbl;
	private JTextField buildingTxtBx;
	private JLabel levelLbl;
	private JTextField levelTxtBx;
	private JLabel valueLbl;
	private JTextField valueTxtBx;
	
	private JLabel yearLbl;
	private JTextField yearTxtBx;
	private JLabel locationLbl;
	private JTextField locationTxtBx;
	private JLabel ownerLbl;
	private JComboBox ownerCmbo;
	
	private JLabel fireIDLbl;
	private JTextField fireIDTxtBx;
	private JLabel makeLbl;
	private JTextField makeTxtBx;
	private JLabel modelLbl;
	private JTextField modelTxtBx;
    
	private JLabel lifeLbl;
	private JTextField lifeTxtBx;	
	private JLabel ecanLbl;
	private JTextField ecanTxtBx;
	private JLabel nelsonLbl;
	private JTextField nelsonTxtBx;
	
	private JLabel fireLbl;
	private JComboBox fireCmbo;
	private JLabel fuelLbl;
	private JComboBox fuelCmbo;
	private JLabel wetLbl;
	private JCheckBox wetChk;
	
	private JTextField[] infoFields;
	
	private JButton prntConsentBtn; 
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	private JButton saveFireBtn; 

	private Boolean lockForm;
	private ConnDetails conDeets;
	private PermitPane pp;

	  public PermitsReqPanel(Boolean lockForm, ConnDetails conDetts, PermitPane ppn)
      {   
		  this.lockForm = lockForm;
		  this.conDeets = conDetts;
		  this.pp = ppn;

  		  connecting = new CreateConnection();	//
  		  
		  infoFields = new JTextField[14];		// Array to hold user JTextField names 
		  rowSelected = false;
		  
		    model1 = new DefaultTableModel();  
		    model1.setRowCount(0);
	        permitsTbl = new JTable(model1);
	        permitsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        permitsTbl.setAutoCreateRowSorter(true);
	        
	        JScrollPane scrollPane = new JScrollPane(permitsTbl);
		  
	        header= permitsTbl.getTableHeader();
	        columnModel = header.getColumnModel();
	        add(header); 
	                	        
	        tablePanel = new JPanel();
	        tablePanel.setBounds(20, 20, 1025, 260);  //setPreferredSize(new Dimension(0, 300));      
	        tablePanel.setLayout(new BorderLayout());
	        
	        infoPanel = new JPanel();
	        infoPanel.setBounds(0, 280, 1100, 300);  //setPreferredSize(new Dimension(0, 300));
	        infoPanel.setLayout(null);
	        
	        detailsTxtArea = new JTextArea("");
	        detailsTxtArea.setBounds(20, 20, 250, 260);
	        detailsTxtArea.setBorder(BorderFactory.createLineBorder(Color.black));
	        detailsTxtArea.setLineWrap(true);
	        detailsTxtArea.setEditable(false);
	        infoPanel.add(detailsTxtArea);
     	        
// User entry fields
	        lotLbl = new JLabel("Lot:");
	        lotLbl.setBounds(305, 20, 70, 20);
	        infoPanel.add(lotLbl);
	        lotTxtBx = new JTextField(" ",10);
	        lotTxtBx.setBounds(375, 20, 150, 20);
	        infoPanel.add(lotTxtBx);
	        infoFields[4] = lotTxtBx;
	        
	        dpLbl = new JLabel("DP:");
	        dpLbl.setBounds(565, 20, 70, 20);
	        infoPanel.add(dpLbl);
	        dpTxtBx = new JTextField(10);
	        dpTxtBx.setBounds(635, 20, 150, 20);
	        infoPanel.add(dpTxtBx);
	        infoFields[5] = dpTxtBx;
	        
	        consentLbl = new JLabel("Consent:");
	        consentLbl.setBounds(825, 20, 70, 20);
	        infoPanel.add(consentLbl);
	        consentTxtBx = new JTextField(10);
	        consentTxtBx.setBounds(895, 20, 150, 20);
	        infoPanel.add(consentTxtBx);
	        infoFields[2] = consentTxtBx;
	        
	        buildingLbl = new JLabel("Building:");
	        buildingLbl.setBounds(305, 50, 70, 20);
	        infoPanel.add(buildingLbl);
	        buildingTxtBx = new JTextField("Residence", 10);
	        buildingTxtBx.setBounds(375, 50, 150, 20);
	        infoPanel.add(buildingTxtBx);
	        infoFields[6] = buildingTxtBx;
	        
	        levelLbl = new JLabel("Unit/Level:");	  
	        levelLbl.setBounds(565, 50, 70, 20);	
	        infoPanel.add(levelLbl);
	        levelTxtBx = new JTextField(10);		
	        levelTxtBx.setBounds(635, 50, 150, 20);	
	        infoPanel.add(levelTxtBx);
	        infoFields[3] = levelTxtBx;
	        
	        valueLbl = new JLabel("Value:");	      
	        valueLbl.setBounds(825, 50, 70, 20);  
	        infoPanel.add(valueLbl);
	        valueTxtBx = new JTextField(10);	    
	        valueTxtBx.setBounds(895, 50, 150, 20);	
	        infoPanel.add(valueTxtBx);
	        infoFields[0] = valueTxtBx;
	        	        
	        yearLbl = new JLabel("Year:");	           
	        yearLbl.setBounds(305, 80, 70, 20);	
	        infoPanel.add(yearLbl);
	        yearTxtBx = new JTextField(10);	         
	        yearTxtBx.setBounds(375, 80, 150, 20); 
	        infoPanel.add(yearTxtBx);
	        infoFields[1] = yearTxtBx;
	        
	        locationLbl = new JLabel("Location:");
	        locationLbl.setBounds(565, 80, 70, 20);
	        infoPanel.add(locationLbl);
	        locationTxtBx = new JTextField(10);
	        locationTxtBx.setBounds(635, 80, 150, 20);
	        infoPanel.add(locationTxtBx);
	        infoFields[7] = locationTxtBx;
	        	        	        
	        ownerLbl = new JLabel("Proof:");
	        ownerLbl.setBounds(825, 80, 70, 20);
	        infoPanel.add(ownerLbl);
	        ownerCmbo = new JComboBox(doctype);
	        ownerCmbo.setSelectedIndex(0);
	        ownerCmbo.setBackground(Color.WHITE);
	        ownerCmbo.setBounds(895, 80, 150, 20);
	        infoPanel.add(ownerCmbo);
	        
	        wetLbl = new JLabel("Wetback:");
	        wetLbl.setBounds(825, 110, 70, 20);
	        infoPanel.add(wetLbl);
	        wetChk = new JCheckBox("");
	        wetChk.setSelected(false);
	        wetChk.setBounds(895, 110, 150, 20);
	        infoPanel.add(wetChk);
	        
	        fireIDLbl = new JLabel("Fire Code:");
	        fireIDLbl.setBounds(305, 140, 70, 20);
	        infoPanel.add(fireIDLbl);
	        fireIDTxtBx = new JTextField(10);
	        fireIDTxtBx.setBounds(375, 140, 150, 20);
	        infoPanel.add(fireIDTxtBx);
	        infoFields[8] = fireIDTxtBx;
	        
	        makeLbl = new JLabel("Make:");
	        makeLbl.setBounds(565, 140, 70, 20);
	        infoPanel.add(makeLbl);
	        makeTxtBx = new JTextField(10);
	        makeTxtBx.setBounds(635, 140, 150, 20);
	        infoPanel.add(makeTxtBx);
	        infoFields[9] = makeTxtBx;
	        
	        modelLbl = new JLabel("Model:");
	        modelLbl.setBounds(825, 140, 70, 20);
	        infoPanel.add(modelLbl);
	        modelTxtBx = new JTextField(10);
	        modelTxtBx.setBounds(895, 140, 150, 20);
	        infoPanel.add(modelTxtBx);
	        infoFields[10] = modelTxtBx;
	        
	        lifeLbl = new JLabel("Life Time:");
	        lifeLbl.setBounds(305, 170, 70, 20);
	        infoPanel.add(lifeLbl);
	        lifeTxtBx = new JTextField(10);
	        lifeTxtBx.setBounds(375, 170, 150, 20);
	        infoPanel.add(lifeTxtBx);
	        infoFields[11] = lifeTxtBx;
	        
	        ecanLbl = new JLabel("ECAN:");
	        ecanLbl.setBounds(565, 170, 70, 20);
	        infoPanel.add(ecanLbl);
	        ecanTxtBx = new JTextField(10);
	        ecanTxtBx.setBounds(635, 170, 150, 20);
	        infoPanel.add(ecanTxtBx);
	        infoFields[12] = ecanTxtBx;
	        
	        nelsonLbl = new JLabel("Nelson:");
	        nelsonLbl.setBounds(825, 170, 70, 20);
	        infoPanel.add(nelsonLbl);
	        nelsonTxtBx = new JTextField(10);
	        nelsonTxtBx.setBounds(895, 170, 150, 20);
	        infoPanel.add(nelsonTxtBx);
	        infoFields[13] = nelsonTxtBx;
	            	        	           	        
	        fireLbl = new JLabel("Fire Type:");
	        fireLbl.setBounds(565, 200, 70, 20);
	        infoPanel.add(fireLbl);
	        fireCmbo = new JComboBox(firestyle);
	        fireCmbo.setSelectedIndex(0);
	        fireCmbo.setBackground(Color.WHITE);
	        fireCmbo.setBounds(635, 200, 150, 20);
	        infoPanel.add(fireCmbo);
	        
	        fuelLbl = new JLabel("Fuel:");
	        fuelLbl.setBounds(825, 200, 70, 20);
	        infoPanel.add(fuelLbl);
	        fuelCmbo = new JComboBox(fueltype);
	        fuelCmbo.setSelectedIndex(0);
	        fuelCmbo.setBackground(Color.WHITE);
	        fuelCmbo.setBounds(895, 200, 150, 20);
	        infoPanel.add(fuelCmbo);
	        
//	Form Buttons
	        prntConsentBtn = new JButton("Print Consent");
	        prntConsentBtn.setBounds(545, 260, 150, 25);
	        infoPanel.add(prntConsentBtn);
	        
	        cancelPermitReqBtn = new JButton("Cancel");
	        cancelPermitReqBtn.setBounds(720, 260, 150, 25);
	        infoPanel.add(cancelPermitReqBtn);
	        
	        savePermitReqBtn = new JButton("Save Permit Details");
	        savePermitReqBtn.setBounds(895, 260, 150, 25);
	        infoPanel.add(savePermitReqBtn);
	        
	        saveFireBtn = new JButton("Save Fire Details");
	        saveFireBtn.setBounds(375, 200, 150, 25);
	        infoPanel.add(saveFireBtn);


	        this.setLayout(null);
	        this.add(tablePanel); 
	        this.add(infoPanel);
	        
		  	tablePanel.add(scrollPane, BorderLayout.CENTER);
		  	tablePanel.add(permitsTbl.getTableHeader(), BorderLayout.NORTH);        
		  	
		  	permitsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent arg0) {
					if (!arg0.getValueIsAdjusting()){
						rowSelected=true;
			//			pp.setFormsLocked();
						try{
						param = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 0).toString();
						
						displayClientDetails(param);
						detailsTxtArea.setText(pp.DisplayClientDetails(param));
						
						} catch (IndexOutOfBoundsException e){
							//Ignoring IndexOutOfBoundsExceptions!
						}
					}
				}
		  	});
	        
	        
		  	savePermitReqBtn.addActionListener( new ActionListener()
		  	{
				@Override
				public void actionPerformed(ActionEvent arg0) {
			        { 	// Check a Customer has been selected
			        	if (rowSelected){
			        		validator val = new validator();	
			        		val.validatePermit(infoFields);		// validate/transform user data
			        		if (!val.getValidEntries()){		// display errors
			        			JOptionPane.showMessageDialog(null, val.getmsg());
			        		}
			        		else {	// update database
			        			updateConsent();
			        			pp.showMessage("Updating Consent...");
			        		}
			        	}else {	//	No Customer selected
			        		pp.showMessage("No details to Save");			        		
			        	}
				    }					
				}
		  	});

		  	cancelPermitReqBtn.addActionListener( new ActionListener()
		  	{
				@Override
				public void actionPerformed(ActionEvent arg0) {
			        { 
			        	resetTable();
				    }					
				}
		  	});

	        prntConsentBtn.addActionListener( new ActionListener()
		  	{
				@Override
				public void actionPerformed(ActionEvent arg0) 
			    { 
			        // Check a Customer has been selected
			        if (rowSelected){
			        		        	
			        	int input = JOptionPane.showConfirmDialog(null,"Mark Consent as Printed and Sent to Council?\n "
			        			+ "This Customer will no longer display on this page!",  "Mark Consent Sent?", JOptionPane.YES_NO_OPTION);
			        	if (input == 0){

			        		updateAsSent();		        		
			        		pp.showMessage("Sorry... Printing not enabled");		        		

					        resetTable();
			        	}
		        	}else {	//	No Customer selected
		        		pp.showMessage("No details to Print");			        				      
			       	}			        
				}
		  	});
		  	
	
		  	saveFireBtn.addActionListener( new ActionListener()
		  	{
				@Override
				public void actionPerformed(ActionEvent arg0) {
			        { 			       
			        		validator val = new validator();	
			        		 JOptionPane.showMessageDialog(null, "Validation not running!");
			        		 
			        		 //CHECK 			        		 
//			        		val.validateFire(infoFields);		// validate/transform user data
//			        		if (!val.getValidEntries()){		// display errors
//			        			JOptionPane.showMessageDialog(null, val.getmsg());
//			        		}
//			        		else {	// update database
			        		 	pp.showMessage("Updating Fire...");
			        			updateFire();
//			        		}	
				    }					
				}
		  	});

// PRE-LOAD Table data for first tab		  	
		  	resetTable();
		  	
	  }
	

		
	  
		protected void resetTable() {
			
			ResultSet rs = pp.getResults(0,  conDeets);
		  	permitsTbl.setModel(DbUtils.resultSetToTableModel(rs)); 
		  	
		  	spaceHeader();
		  	
			param = "";  
	      	permitsTbl.clearSelection();
			rowSelected=false;
	      	for(Component control : infoPanel.getComponents())
	      	{
	      	    if(control instanceof JTextField)
	      	    {
	      	        JTextField ctrl = (JTextField) control;
	      	        ctrl.setText("");
	      	    }
	      	    else if (control instanceof JComboBox)
	      	    {
	      	        JComboBox ctrl = (JComboBox) control;
	      	        ctrl.setSelectedIndex(0);
	      	    }
	      	}
	}




		protected void updateConsent() {
						
		    CallableStatement pm = null;

		    try {
				
			String updateConsent = "{" + upConsent +"(?,?,?,?,?,?,?,?,?,?,?,?)}";	
        	Connection conn = connecting.CreateConnection(conDeets);	        	   	
		
        	pm = conn.prepareCall(updateConsent);
        	
        	pm.setString(1, param);
        	pm.setString(2, getLot());
        	pm.setString(3, getDP());
        	pm.setString(4, getConsent());
        	pm.setString(5, getBuilding());
        	pm.setString(6, getLevel());
        	pm.setString(7, getValue());
        	pm.setString(8, getYear()); 
        	pm.setString(9, getFireLocation()); 
        	pm.setString(10, getOwnership()); 
        	pm.setBoolean(11, getWet()); 
        	pm.setString(12, getFireCode()); 
			
        	pm.executeUpdate();
        	
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
		
    	
		protected void updateAsSent() {
			
		    CallableStatement pm = null;

		    try {
				
		    	String updateSent = upSent;	
		    	Connection conn = connecting.CreateConnection(conDeets);	        	   	
		
		    	pm = conn.prepareCall(updateSent);
			
		    	pm.setString(1, param);
			
		    	pm.executeUpdate();
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
		
		
		
		protected void updateFire() {
			
		    CallableStatement pm = null;

		    try {
				
			String updateFire = upFire;	
        	Connection conn = connecting.CreateConnection(conDeets);	        	   	
		
        	pm = conn.prepareCall(updateFire);
			
        	pm.setString(1, getFireCode());
        	pm.setString(2, getFireType());
        	pm.setString(3, getMake());
        	pm.setString(4, getModel());
        	pm.setString(5, getFuel());
        	pm.setString(6, getEcan());
        	pm.setString(7, getNelson());
        	pm.setInt	(8, getLifeTime()); 
			
        	pm.executeUpdate();
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

      	
		private void displayClientDetails(String parameter) {
			
			rs2 = pp.getDetails(qry, param, conDeets);
			
	        	 try {
					while(rs2.next()){
								    					
/*			        	String invoice 			= rs2.getString("Invoice");
			        	String rees				= rs2.getString("Rees");
						 String customerName 	= rs2.getString("CustomerName");
						 String customerAddress = rs2.getString("CustomerAddress");
						 String customerSuburb 	= rs2.getString("CustomerSuburb");
						 String customerPostCode= rs2.getString("CustomerPostCode");
						 String customerPhone 	= rs2.getString("CustomerPhone");
						 String customerMobile 	= rs2.getString("CustomerMobile");
						 String customerEmail 	= rs2.getString("CustomerEmail");
						 String streetAddress 	= rs2.getString("StreetAddress");
						 String suburb 			= rs2.getString("Suburb");
*/						 String status 			= rs2.getString("PermitStatus");
						 String consent 		= rs2.getString("Consent");						 						
						 String lot 			= rs2.getString("Lot");
						 String dP 				= rs2.getString("DP"); 
						 String ownershipDoc 	= rs2.getString("ownershipDoc");							
						 String building 		= rs2.getString("Building");
						 String unit_Level 		= rs2.getString("Unit_Level");						
						 String yearConstructed = rs2.getString("YearConstructed");
						 String fireID 			= rs2.getString("FireID");						 
						 Boolean wetback 		= rs2.getBoolean("Wetback");						 
						 String value 			= rs2.getString("Value");						 
						 String fire_Location 	= rs2.getString("Fire_Location");			
						 						 
						 lotTxtBx.setText(lot);
						 dpTxtBx.setText(dP);
						 consentTxtBx.setText(consent);
						 buildingTxtBx.setText(building);
						 levelTxtBx.setText(unit_Level);
						 valueTxtBx.setText(value);
						 yearTxtBx.setText(yearConstructed);
						 locationTxtBx.setText(fire_Location);
						 fireIDTxtBx.setText(fireID);
					
						 if (wetback == false){
						 	wetChk.setSelected(false); 
						 } else {
						 	wetChk.setSelected(true);  
						 } 
						 	
				//	    String ownershipDoc = rs2.getString("Value");
					    if (ownershipDoc == "None"){
					    	ownerCmbo.setSelectedIndex(0); 
					    } else if(ownershipDoc == "Rates Notice"){
					    	ownerCmbo.setSelectedIndex(1); 
					    } else if(ownershipDoc == "Certificate of Title"){
					    	ownerCmbo.setSelectedIndex(2); 
					    } else if(ownershipDoc == "Lease Agreement"){
					    	ownerCmbo.setSelectedIndex(3); 
					    } else if(ownershipDoc == "Sale & Purchase"){
					    	ownerCmbo.setSelectedIndex(4); 
					    }else
					    	ownerCmbo.setSelectedIndex(5);                
					 	}
				
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	 updateFireDetails(fireIDTxtBx.getText());
		}        	 
	        
		
		private void updateFireDetails(String fireCode) {
			
			rs3 = pp.getDetails(qry2, "[" + fireCode + "]", conDeets);

			 try {
		    
		        	 while(rs3.next()){
		        		        		
						 String make 			= rs3.getString("Make");
						 String model 			= rs3.getString("Model");
						 String fireType 		= rs3.getString("FireType");
						 String fuel 			= rs3.getString("Fuel");
						 String ecan 			= rs3.getString("ECAN");
						 String nelson			= rs3.getString("Nelson");
						 String life 			= rs3.getString("Life");
		        		
		                //Retrieve by column name
		        		makeTxtBx.setText(make);
		        		modelTxtBx.setText(model);
		    	        ecanTxtBx.setText(ecan);
		    	        nelsonTxtBx.setText(nelson);
		    	        lifeTxtBx.setText(life);
		    	        
		    	        String ft = rs3.getString("FireType");
		    	        if (ft.equals("FS")){
		    	        	fireCmbo.setSelectedIndex(0);
		    	        } else if(ft.equals("IS")){
		    	        	fireCmbo.setSelectedIndex(1);
		    	        } else if(ft.equals("IB")){
		    	        	fireCmbo.setSelectedIndex(2);
		    	        } else{
		    	        	fireCmbo.setSelectedIndex(3);
		    	        }
		    	        
		    	        String fl = rs3.getString("Fuel");
		    	        if (fl.equals("Wood")){
		    	        	fireCmbo.setSelectedIndex(0);
		    	        } else if(fl.equals("Pellet")){
		    	        	fireCmbo.setSelectedIndex(1);
		    	        } else if(fl.equals("Oil")){
		    	        	fireCmbo.setSelectedIndex(2);
		    	        } else{
		    	        	fireCmbo.setSelectedIndex(3);
		    	        }
		        	 }
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}    	
		
	  
	    public void spaceHeader() {
	        int i;
	        TableColumn tabCol = columnModel.getColumn(0);
	        for (i=0; i<columnWidth.length; i++){
	             tabCol = columnModel.getColumn(i);
	            tabCol.setPreferredWidth(columnWidth[i]);
	        }
	        header.repaint();
	  }
		      
	    
	    public JTable getPermitsTbl(){
	    	return permitsTbl;
	    }

	    public String getLot(){
	    	if (lotTxtBx.getText() == null){
	    		lotTxtBx.setText("");
	    	}
	    	return lotTxtBx.getText().toString();
	    }
	    public String getDP(){
	    	if (dpTxtBx.getText() == null){
	    		dpTxtBx.setText("");
	    	}
	    	return dpTxtBx.getText().toString();
	    }
	    public String getConsent(){
	    	if (consentTxtBx.getText() == null){
	    		consentTxtBx.setText("");
	    	}
	    	return consentTxtBx.getText().toString();
	    }
	    public String getBuilding(){
	    	String build = buildingTxtBx.getText();
	    	System.out.println(build);
	    	return buildingTxtBx.getText().toString();
	    }
	    public String getLevel(){
	    	return levelTxtBx.getText().toString();
	    }
	    public String getValue(){
	    	return valueTxtBx.getText().toString();
	    }
	    public String getYear(){
	    	return yearTxtBx.getText().toString();
	    }
	    public String getFireLocation(){
	    	if (locationTxtBx.getText().length() == 0){
	    		locationTxtBx.setText(null);
	    		
	    	}
	    	return locationTxtBx.getText().toString();
	    }
	    public String getOwnership(){
	    	String owner = ownerCmbo.getSelectedItem().toString();
	    	return owner;
	    }
	    public Boolean getWet(){
	    	if (wetChk.isSelected()){
	    		return true;
	    	}
	    	else{
		    	return false;	    		
	    	}

	    }
	    
	    public String getFireCode(){

	    	return fireIDTxtBx.getText().toString();
	    }
	    
	    public String getMake(){
	    	return makeTxtBx.getText().toString();
	    }
	    public String getModel(){
	    	return modelTxtBx.getText().toString();
	    }
	    public int getLifeTime(){
	    	try{
	    		int life = Integer.parseInt(lifeTxtBx.getText());
	    		return life;
	    		}catch (NumberFormatException nfe){
	    			return 0;
	    		}
	    }
	    public String getEcan(){
	    	return ecanTxtBx.getText().toString();
	    }
	    public String getNelson(){
	    	return nelsonTxtBx.getText().toString();
	    }    
	    public String getFireType(){
	    	String ft = fireCmbo.getSelectedItem().toString();
	    	return ft;
	    }
	    public String getFuel(){
	    	String fl = fuelCmbo.getSelectedItem().toString();
	    	return fl;
	    }


}
