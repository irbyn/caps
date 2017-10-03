package Permit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
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



class RecvPermitPanel extends JPanel {
	
	private String qry = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
//	private String qry2 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
	private String param = "";  
	private ResultSet rs;
	private ResultSet rs2;
	private Boolean rowSelected;
	
//	private CreateConnection connecting;
	
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	
	private JTable permitsTbl;
	private DefaultTableModel model2;	
	private JTextArea detailsTxtArea;
	
	private JLabel consentLbl;
	private JTextField consentTxtBx;

	private JLabel receivedLbl;
	private JCheckBox receivedChk;
	
	private JLabel receivedDateLbl;
	private JSpinner receivedDate;
	
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	
	private Boolean lockForm;
	private ConnDetails conDeets;
	private PermitPane pp;

	  public RecvPermitPanel(Boolean lockForm, ConnDetails conDetts, PermitPane ppn)
      {   
		  this.lockForm = lockForm;
		  this.conDeets = conDetts;
		  this.pp = ppn;
      	//Get User connection details
 // 		user = conDeets.getUser();
 // 		pass = conDeets.getPass();
  //		dbURL = conDeets.getURL();
  			  
//		  connecting = new CreateConnection();
	  	 		  	
		    model2 = new DefaultTableModel();  
		    model2.setRowCount(0);
		    permitsTbl = new JTable(model2);
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
	        
	        consentLbl = new JLabel("Consent:");
	        consentLbl.setBounds(825, 80, 70, 20);
	        infoPanel.add(consentLbl);
	        consentTxtBx = new JTextField(10);
	        consentTxtBx.setBounds(895, 80, 150, 20);
	        infoPanel.add(consentTxtBx);
	        
	        receivedLbl = new JLabel("Permit Issued:");
	        receivedLbl.setBounds(800, 110, 95, 20);
	        infoPanel.add(receivedLbl);			
	        receivedChk = new JCheckBox("");
	        receivedChk.setSelected(false);
	        receivedChk.setBounds(895, 110, 150, 20);
	        infoPanel.add(receivedChk);
	        
	        receivedDateLbl = new JLabel("Date Issued:");
	        receivedDateLbl.setVisible(false);
	        receivedDateLbl.setBounds(800, 140, 95, 20);
	        infoPanel.add(receivedDateLbl);
	        
	        SimpleDateFormat psModel = new SimpleDateFormat("dd.MMM.yyyy");
	        receivedDate = new JSpinner(new SpinnerDateModel());
	        receivedDate.setVisible(false);
	        receivedDate.setEditor(new JSpinner.DateEditor(receivedDate, psModel.toPattern()));
	        receivedDate.setBounds(895, 140, 150, 20);
			infoPanel.add(receivedDate);
	        
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
		  	
		  	savePermitReqBtn.addActionListener( new ActionListener()
		  	{
				@Override
				public void actionPerformed(ActionEvent arg0) {
			        { 	// Check a Customer has been selected
			        	if (rowSelected){
			        		if (getReceived()){
				        		if (getConsentNum().length() == 0){
				        			JOptionPane.showMessageDialog(null, "Consent Number must be entered!");
				        		}else {
				        			JOptionPane.showMessageDialog(null, "Validate/transform Consent number, then update with received timestamp");
				        		}
				        		
			        		} else {
			        			JOptionPane.showMessageDialog(null, "Validate/transform, then update Consent number");
			        		}

			        			
			        		
			        		

			        	}else {	//	No Customer selected
			        		JOptionPane.showMessageDialog(null, "No details to Save");			        		
			        	}
				    }					
				}
		  	});

		  	cancelPermitReqBtn.addActionListener( new ActionListener()
		  	{
				@Override
				public void actionPerformed(ActionEvent arg0) {
			        { 
			        	clearFields();
				    }					
				}
		  	});
		  	
		  	receivedChk.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	if (getReceived()){
	            		receivedDate.setVisible(true);
	            		receivedDateLbl.setVisible(true);
	            	}else {
	            		receivedDate.setVisible(false);
	            		receivedDateLbl.setVisible(false);
	            	}
	            	
	            }
	        });
	        
		  	permitsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent arg0) {
					if (!arg0.getValueIsAdjusting()){
						rowSelected=true;
			//			pp.setFormsLocked();
						try{
						param = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 0).toString();
			        	updatePermitDetails(param);
						} catch (IndexOutOfBoundsException e){
							//
						}
					}
				}
		  	});
		  	
	  }
	  
	  
	  
	    
	public String getConsentNum() {
		return consentTxtBx.getText();
	}
    public Boolean getReceived(){
    	if (receivedChk.isSelected()){
    		return true;
    	}
    	else{
	    	return false;	    		
    	}
    }
    public String getReceivedDate(){
    	return receivedDate.toString();
    }
    
    
	public JTable getPermitsTbl(){
	    return permitsTbl;
	}
		
	private void clearFields(){
			  
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
		
	private void updatePermitDetails(String parameter) {
		
		rs2 = pp.getDetails(qry, param, conDeets);
		
        	 try {
	        	 while(rs2.next()){
	        									    					
	        	String invoice 			= rs2.getString("Invoice");
	        	String customerName 	= rs2.getString("CustomerName");
	        	String customerAddress 	= rs2.getString("CustomerAddress");
	        	String customerSuburb 	= rs2.getString("CustomerSuburb");
	        	String customerPostCode= rs2.getString("CustomerPostCode");
	        	String customerPhone 	= rs2.getString("CustomerPhone");
	        	String customerMobile 	= rs2.getString("CustomerMobile");
	        	String customerEmail 	= rs2.getString("CustomerEmail");
	        	String streetAddress 	= rs2.getString("StreetAddress");
	        	String suburb 			= rs2.getString("Suburb");					
	        							
	        	String sb = " INVOICE: " + param + "\n" +
	        				" CLIENT:\t" + customerName + "\n\n" + 
	        				" SITE:\t" + streetAddress + "\n" +
	        				"\t" + suburb + "\n\n" + 
	        				" POSTAL:\t" + customerAddress + "\n" +
	        				"\t" + customerSuburb + "\n" + 
	        				"\t" + customerPostCode + "\n\n" +
	        				" PHONE:\t" + customerPhone + "\n" + 
	   					 	" MOBILE:\t" + customerMobile + "\n\n" +
	        				" EMAIL:\t" + customerEmail + "\n";
	        	detailsTxtArea.setText(sb);
	        							 						        	 					 }
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	 
	
 	}	
		
}