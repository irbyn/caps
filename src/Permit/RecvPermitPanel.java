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



class RecvPermitPanel extends JPanel {
	

	private int [] columnWidth = {6, 30, 30, 20, 20, 20};
	private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
	private String result3 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
	private String param = "";  
	private ResultSet rs;
	
	private CreateConnection connecting;
	
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
	
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	
	private String user = "";
	private String pass = "";
	private String dbURL = "";
	

	  public RecvPermitPanel(ConnDetails conDeets, PermitPane pp1)
      {   
      	//Get User connection details
  		user = conDeets.getUser();
  		pass = conDeets.getPass();
  		dbURL = conDeets.getURL();
  			  
		  connecting = new CreateConnection();
	  	 		  	
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
	        consentLbl.setBounds(825, 20, 70, 20);
	        infoPanel.add(consentLbl);
	        consentTxtBx = new JTextField(10);
	        consentTxtBx.setBounds(895, 20, 150, 20);
	        infoPanel.add(consentTxtBx);
	        
	        receivedLbl = new JLabel("Wetback:");
	        receivedLbl.setBounds(825, 110, 70, 20);
	        infoPanel.add(receivedLbl);
	        receivedChk = new JCheckBox("");
	        receivedChk.setSelected(false);
	        receivedChk.setBounds(895, 110, 150, 20);
	        infoPanel.add(receivedChk);
	        
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
	//	  	this.add(infoPanel, BorderLayout.SOUTH);
		  	
		  	permitsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent arg0) {
					if (!arg0.getValueIsAdjusting()){
						param = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 0).toString();
			        	updatePermitDetails(param);
					}
				}
		  	});
		  	
		  	rs = pp1.getTableData();		  	
		  	permitsTbl.setModel(DbUtils.resultSetToTableModel(rs));  	
		  	spaceHeader();
	  }
	  
	    public void spaceHeader() {
	        int i;
	        TableColumn tabCol = columnModel.getColumn(0);
	        for (i=0; i<columnWidth.length; i++){
	             tabCol = columnModel.getColumn(i);
	            tabCol.setPreferredWidth(columnWidth[i]*5);
	        }
	        header.repaint();
	  }
	  
	    
	    public JTable getPermitsTbl(){
	    	return permitsTbl;
	    }
		
	private void updatePermitDetails(String parameter) {
	        try
	        {
	        	Connection conn = connecting.CreateConnection();
	        	PreparedStatement st2 =conn.prepareStatement(result2 + parameter);
	        	ResultSet rs2 = st2.executeQuery();
	    
	                //Retrieve by column name
	        	 while(rs2.next()){
	        		 
	        		 detailsTxtArea.setText("\n INVOICE:\t" + param + "\n");
	        		 detailsTxtArea.append( " CLIENT:\t" + rs2.getString("CustomerName") + "\n\n");
	        		 detailsTxtArea.append( " SITE:\t" + rs2.getString("StreetAddress") + "\n");
	        		 detailsTxtArea.append( "\t" + rs2.getString("Suburb") + "\n\n");
	        		 detailsTxtArea.append( " POSTAL:\t" + rs2.getString("CustomerAddress") + "\n");
	        		 detailsTxtArea.append( "\t" + rs2.getString("CustomerSuburb") + "\n");
	        		 detailsTxtArea.append( "\t" + rs2.getString("CustomerPostCode") + "\n\n");
	        		 detailsTxtArea.append( " PHONE:\t" + rs2.getString("CustomerPhone") + "\n");
	        		 detailsTxtArea.append( " MOBILE:\t" + rs2.getString("CustomerMobile") + "\n\n");
	        		 detailsTxtArea.append( " EMAIL:\t" + rs2.getString("CustomerEmail") + "\n");

/*	    	        lotTxtBx.setText(rs2.getString("Lot"));
	    	        dpTxtBx.setText(rs2.getString("DP"));
	    	        consentTxtBx.setText(rs2.getString("Consent"));
	    	        buildingTxtBx.setText(rs2.getString("Building"));
	    	        valueTxtBx.setText(rs2.getString("Unit_Level"));
	    	        yearTxtBx.setText(rs2.getString("YearConstructed"));
	    	        locationTxtBx.setText(rs2.getString("Fire_Location"));
	    	        valueTxtBx.setText(rs2.getString("Value"));	                
*/	        	 }
	        	 
	        	 
		        	PreparedStatement st3 =conn.prepareStatement(result3 + parameter);
		        	
		        	ResultSet rs3 = null;
		        	rs3 = st3.executeQuery();
		    
		        	 while(rs3.next()){
	/*	        		 
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
	*/	        } 
		        
	        	conn.close();	
	        }
	        catch(Exception ex)
	        { 
	        JOptionPane.showMessageDialog(null, ex.toString());
	        }	

 	}	
		
}