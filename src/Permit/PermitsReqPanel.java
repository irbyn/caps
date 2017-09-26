package Permit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.proteanit.sql.DbUtils;
import DB_Comms.CreateConnection;
import Main.*;


class PermitsReqPanel extends JPanel {
	
	private int [] columnWidth = {20, 150, 150, 100, 100, 100};
	private String qry = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
	private String qry2 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
	private String param = "";  
	private ResultSet rs;
	private ResultSet rs2;
	private ResultSet rs3;
	
	private String[] doctype = {"NONE","Rates Notice","Certificate of Title", "Lease Agreement", "Sale & Purchase", "Other"};
	private String[] firestyle = {"FS","IS", "IB","Other"};
	private String[] fueltype = {"Wood","Pellet", "Oil","Other"};
	
	private CreateConnection connecting;
	
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
	
	private JButton prntConsentBtn; 
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	
	private String user = "";
	private String pass = "";
	private String dbURL = "";
	
	private ConnDetails conDeets;	
	private PermitPane pp;

	  public PermitsReqPanel(ConnDetails conDetts, PermitPane ppn)
      {   
		  this.conDeets = conDetts;
		  this.pp = ppn;
/*      	//Get User connection details
  		user = conDeets.getUser();
  		pass = conDeets.getPass();
  		dbURL = conDeets.getURL();
*/	  	 		  	
		    model1 = new DefaultTableModel();  
		    model1.setRowCount(0);
	        permitsTbl = new JTable(model1);
	        permitsTbl.setPreferredSize(new Dimension(0, 300));
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
     	        
	        lotLbl = new JLabel("Lot:");
	        lotLbl.setBounds(305, 20, 70, 20);
	        infoPanel.add(lotLbl);
	        lotTxtBx = new JTextField(10);
	        lotTxtBx.setBounds(375, 20, 150, 20);
	        infoPanel.add(lotTxtBx);
	        
	        dpLbl = new JLabel("DP:");
	        dpLbl.setBounds(565, 20, 70, 20);
	        infoPanel.add(dpLbl);
	        dpTxtBx = new JTextField(10);
	        dpTxtBx.setBounds(635, 20, 150, 20);
	        infoPanel.add(dpTxtBx);
	        
	        consentLbl = new JLabel("Consent:");
	        consentLbl.setBounds(825, 20, 70, 20);
	        infoPanel.add(consentLbl);
	        consentTxtBx = new JTextField(10);
	        consentTxtBx.setBounds(895, 20, 150, 20);
	        infoPanel.add(consentTxtBx);
	        
	        buildingLbl = new JLabel("Building:");
	        buildingLbl.setBounds(305, 50, 70, 20);
	        infoPanel.add(buildingLbl);
	        buildingTxtBx = new JTextField("Residence", 10);
	        buildingTxtBx.setBounds(375, 50, 150, 20);
	        infoPanel.add(buildingTxtBx);
	        
	        levelLbl = new JLabel("Unit/Level:");	  
	        levelLbl.setBounds(565, 50, 70, 20);	
	        infoPanel.add(levelLbl);
	        levelTxtBx = new JTextField(10);		
	        levelTxtBx.setBounds(635, 50, 150, 20);	
	        infoPanel.add(levelTxtBx);
	        
	        valueLbl = new JLabel("Value:");	      
	        valueLbl.setBounds(825, 50, 70, 20);  
	        infoPanel.add(valueLbl);
	        valueTxtBx = new JTextField(10);	    
	        valueTxtBx.setBounds(895, 50, 150, 20);	
	        infoPanel.add(valueTxtBx);
	        	        
	        yearLbl = new JLabel("Year:");	           
	        yearLbl.setBounds(305, 80, 70, 20);	
	        infoPanel.add(yearLbl);
	        yearTxtBx = new JTextField(10);	         
	        yearTxtBx.setBounds(375, 80, 150, 20); 
	        infoPanel.add(yearTxtBx);
	        
	        locationLbl = new JLabel("Location:");
	        locationLbl.setBounds(565, 80, 70, 20);
	        infoPanel.add(locationLbl);
	        locationTxtBx = new JTextField(10);
	        locationTxtBx.setBounds(635, 80, 150, 20);
	        infoPanel.add(locationTxtBx);
	        	        	        
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
	        
	        makeLbl = new JLabel("Make:");
	        makeLbl.setBounds(565, 140, 70, 20);
	        infoPanel.add(makeLbl);
	        makeTxtBx = new JTextField(10);
	        makeTxtBx.setBounds(635, 140, 150, 20);
	        infoPanel.add(makeTxtBx);
	        
	        modelLbl = new JLabel("Model:");
	        modelLbl.setBounds(825, 140, 70, 20);
	        infoPanel.add(modelLbl);
	        modelTxtBx = new JTextField(10);
	        modelTxtBx.setBounds(895, 140, 150, 20);
	        infoPanel.add(modelTxtBx);
	        
	        lifeLbl = new JLabel("Life Time:");
	        lifeLbl.setBounds(305, 170, 70, 20);
	        infoPanel.add(lifeLbl);
	        lifeTxtBx = new JTextField(10);
	        lifeTxtBx.setBounds(375, 170, 150, 20);
	        infoPanel.add(lifeTxtBx);
	        
	        ecanLbl = new JLabel("ECAN:");
	        ecanLbl.setBounds(565, 170, 70, 20);
	        infoPanel.add(ecanLbl);
	        ecanTxtBx = new JTextField(10);
	        ecanTxtBx.setBounds(635, 170, 150, 20);
	        infoPanel.add(ecanTxtBx);
	        
	        nelsonLbl = new JLabel("Nelson:");
	        nelsonLbl.setBounds(825, 170, 70, 20);
	        infoPanel.add(nelsonLbl);
	        nelsonTxtBx = new JTextField(10);
	        nelsonTxtBx.setBounds(895, 170, 150, 20);
	        infoPanel.add(nelsonTxtBx);
	            	        	           	        
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
	        
	        prntConsentBtn = new JButton("Print Consent");
	        prntConsentBtn.setBounds(545, 260, 150, 25);
	        infoPanel.add(prntConsentBtn);
	        
	        cancelPermitReqBtn = new JButton("Cancel");
	        cancelPermitReqBtn.setBounds(720, 260, 150, 25);
	        infoPanel.add(cancelPermitReqBtn);
	        
	        savePermitReqBtn = new JButton("Save Permit Details");
	        savePermitReqBtn.setBounds(895, 260, 150, 25);
	        infoPanel.add(savePermitReqBtn);

	        this.setLayout(null);
	        this.add(tablePanel); 
	        this.add(infoPanel);
	        
		  	tablePanel.add(scrollPane, BorderLayout.CENTER);
		  	tablePanel.add(permitsTbl.getTableHeader(), BorderLayout.NORTH);        
		  	
		  	permitsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent arg0) {
					if (!arg0.getValueIsAdjusting()){
						try{
						param = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 0).toString();
						
						updatePermitDetails(param);
						} catch (IndexOutOfBoundsException e){
							//
						}
					}
				}
		  	});
		  	
		  	rs = pp.getResults(0, conDeets);		
		  	permitsTbl.setModel(DbUtils.resultSetToTableModel(rs));  	
		  	spaceHeader();  	
	  }
	  

		
		private void updatePermitDetails(String parameter) {
			
			rs2 = pp.getDetails(qry, param, conDeets);
			
	        	 try {
					while(rs2.next()){
						
/*						ResultSetMetaData  rsmd =  rs2.getMetaData();
			            int cols = rsmd.getColumnCount();
			            
			                        System.out.printf("The query fetched %d columns\n",cols);
			            
			                        System.out.println("These columns are: ");
			            
			                        for (int i=1;i<=cols;i++) {
			            
			                            String colName = rsmd.getColumnName(i);
			            
			                            String colType = rsmd.getColumnTypeName(i);
			            
			                            System.out.println(colName+" of type "+colType);
			            
			                             
			            
			                        }*/

						
						 String customerName 	= rs2.getString("CustomerName");
						 String streetAddress 	= rs2.getString("StreetAddress");
						 String suburb 			= rs2.getString("Suburb");
						 String customerAddress = rs2.getString("CustomerAddress");
						 String customerSuburb 	= rs2.getString("CustomerSuburb");
						 String customerPostCode= rs2.getString("CustomerPostCode");
						 String customerPhone 	= rs2.getString("CustomerPhone");
						 String customerMobile 	= rs2.getString("CustomerMobile");
						 String customerEmail 	= rs2.getString("CustomerEmail");						
 
						 String sb =" CLIENT:\t" + customerName + "\n\n" + 
								 	" SITE:\t" + streetAddress + "\n" +
								 	"\t" + suburb + "\n\n" + 
								 	" POSTAL:\t" + customerAddress + "\n" +
								 	"\t" + customerSuburb + "\n" + 
								 	"\t" + customerPostCode + "\n\n" +
								 	" PHONE:\t" + customerPhone + "\n" + 
								 	" MOBILE:\t" + customerMobile + "\n\n" +
								 	" EMAIL:\t" + customerEmail + "\n";
						 detailsTxtArea.setText(sb);
						 
						 String lot = rs2.getString("Lot");
						 String dP = rs2.getString("DP");
						 String consent = rs2.getString("Consent");
						 String building = rs2.getString("Building");
						 String unit_Level = rs2.getString("Unit_Level");
						 String value = rs2.getString("Value");
						 String yearConstructed = rs2.getString("YearConstructed");
						 String fire_Location = rs2.getString("Fire_Location");
						 						 
					    lotTxtBx.setText(lot);
					    dpTxtBx.setText(dP);
					    consentTxtBx.setText(consent);
					    buildingTxtBx.setText(building);
					    levelTxtBx.setText(unit_Level);
					    valueTxtBx.setText(value);
					    yearTxtBx.setText(yearConstructed);
					    locationTxtBx.setText(fire_Location);
					    
					    String ownershipDoc = rs2.getString("Value");
					    if (ownershipDoc == "None"){
					    	ownerCmbo.setSelectedIndex(0); 
					    } else if(ownershipDoc == "Rates Notice"){
					    	ownerCmbo.setSelectedIndex(1); 
					    } else if(ownershipDoc == "Certificate of Title"){
					    	ownerCmbo.setSelectedIndex(2); 
					    } else if(ownershipDoc == "Lease Agreement"){
					    	ownerCmbo.setSelectedIndex(3); 
					    } else if(ownershipDoc == "Sale & Purchase€"){
					    	ownerCmbo.setSelectedIndex(4); 
					    }else
					    	ownerCmbo.setSelectedIndex(5);                
					 	}
					
					System.out.println("" + rs2.getMetaData());
	/*				
					
				    int wetback = rs2.getByte("Wetback");
				    if (wetback == 0){
				    	wetChk.setSelected(false); 
				    	System.out.println("WB = 0 ");
				    } else {
				    	System.out.println("WB = 1 ");
				    	wetChk.setSelected(true);  
				    } 				
					*/
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	 updateFireDetails(param);
		}        	 
	        
		
		private void updateFireDetails(String parameter) {
			
			rs3 = pp.getDetails(qry2, param, conDeets);

			 try {
		    
		        	 while(rs3.next()){
		        		 
		        	if (!rs3.getString("FireID").equals(parameter)){
		                //Retrieve by column name
		        		fireIDTxtBx.setText("");
		        		makeTxtBx.setText("");
		        		makeTxtBx.setText("");
		        		modelTxtBx.setText("");
		    	        ecanTxtBx.setText("");
		    	        nelsonTxtBx.setText("");
		    	        lifeTxtBx.setText("");
		    	        fireCmbo.setSelectedIndex(0);
		    	        fireCmbo.setSelectedIndex(0);
		        	
		        		fireIDTxtBx.setText(rs3.getString("FireID"));
		        		makeTxtBx.setText(rs3.getString("Make"));
		        		makeTxtBx.setText(rs3.getString("Make"));
		        		modelTxtBx.setText(rs3.getString("Model"));
		    	        ecanTxtBx.setText(rs3.getString("ECAN"));
		    	        nelsonTxtBx.setText(rs3.getString("Nelson"));
		   // 	        lifeTxtBx.setText(rs2.getString("Life"));
		    	        
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
	    

}
