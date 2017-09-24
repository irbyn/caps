package Permit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import net.proteanit.sql.DbUtils;

class CCCToCounPanel extends JPanel {
	
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
	private DefaultTableModel model1;
	
	private JTextArea detailsTxtArea;
	

	private JLabel nelsonLbl;
	private JTextField nelsonTxtBx;
	
	private JButton prntConsentBtn; 
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	
	private String user = "";
	private String pass = "";
	private String dbURL = "";
	
	public CCCToCounPanel(ConnDetails conDeets, PermitPane pp) {

     	//Get User connection details
  		user = conDeets.getUser();
  		pass = conDeets.getPass();
  		dbURL = conDeets.getURL();
  		
/*  		System.out.println("user  : " + user);
  		System.out.println("pass  : " + pass);
  		System.out.println("dbURL : " + dbURL);
*/		  
		  connecting = new CreateConnection();
	  	 		  	
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
	        detailsTxtArea.setBounds(20, 20, 1025, 140);
	        detailsTxtArea.setBorder(BorderFactory.createLineBorder(Color.black));
	        detailsTxtArea.setLineWrap(true);
	        detailsTxtArea.setEditable(false);
	        infoPanel.add(detailsTxtArea);
	        
	        nelsonLbl = new JLabel("Nelson:");
	        nelsonLbl.setBounds(825, 170, 70, 20);
	        infoPanel.add(nelsonLbl);
	        nelsonTxtBx = new JTextField(10);
	        nelsonTxtBx.setBounds(895, 170, 150, 20);
	        infoPanel.add(nelsonTxtBx);
	        
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
		  	
	//	  	rs = pp.getTableData();		  	
	//	  	permitsTbl.setModel(DbUtils.resultSetToTableModel(rs));  	
	//	  	spaceHeader();
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
	        	 }
	        	 
	        	 
		        	PreparedStatement st3 =conn.prepareStatement(result3 + parameter);
		        	
		        	ResultSet rs3 = null;
		        	rs3 = st3.executeQuery();
		    
		        	 while(rs3.next()){
		        		 
		        	if (!rs3.getString("FireID").equals(parameter)){
		                //Retrieve by column name

		    	        nelsonTxtBx.setText("");

		    	        nelsonTxtBx.setText(rs3.getString("Nelson"));
		        	 }
		        } 
		        
	        	conn.close();	
	        }
	        catch(Exception ex)
	        { 
	        JOptionPane.showMessageDialog(null, ex.toString());
	        }	  	
 	}	
		
}
