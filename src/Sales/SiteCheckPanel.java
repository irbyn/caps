package Sales;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import net.proteanit.sql.DbUtils;

import javax.swing.JButton;
import javax.swing.JCheckBox;

class SiteCheckPanel extends JPanel {
	//private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
	//private String result3 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
	private String param = "";  
	private ResultSet rs;
	
	private CreateConnection connecting;
	
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable salesTbl;
	private DefaultTableModel model1;
	private JTextArea txtAreaCustInfo;
	private SalesPane sp;
	private ConnDetails conDeets;
	
	private JLabel lblSChkBooking;
	private JSpinner spnTimeDate;
	private JComboBox comBxSChkDoneBy;
	private JLabel lblSiteCheckBy;
	private JCheckBox chckbxSChkComp;
	private JButton btnCancel;
	private JButton btnSave;
	private JTable table;

	public SiteCheckPanel(ConnDetails conDeets, SalesPane sp) {
		
		 conDeets = conDeets;

		  connecting = new CreateConnection();
	  	 		  	
		    model1 = new DefaultTableModel();  
		    model1.setRowCount(0);
	        salesTbl = new JTable(model1);
	        salesTbl.setPreferredSize(new Dimension(0, 300));
	        salesTbl.setAutoCreateRowSorter(true);
	        
	        JScrollPane scrollPane = new JScrollPane(salesTbl);
		  
	        header= salesTbl.getTableHeader();
	        columnModel = header.getColumnModel();
	        add(header); 
	             
	        //Panel for the table
	        tablePanel = new JPanel();
	        tablePanel.setBounds(20, 20, 1025, 260);  //setPreferredSize(new Dimension(0, 300));      
	        tablePanel.setLayout(new BorderLayout());
	        
	        //Content panel
	        infoPanel = new JPanel();
	        infoPanel.setBounds(0, 280, 1077, 289);  //setPreferredSize(new Dimension(0, 300));
	        infoPanel.setLayout(null);
			
			txtAreaCustInfo = new JTextArea();
			txtAreaCustInfo.setEditable(false);
			txtAreaCustInfo.setBounds(23, 24, 382, 237);
			infoPanel.add(txtAreaCustInfo);
		
	/*	JTextArea txtAreaCustInfo = new JTextArea();
		txtAreaCustInfo.setEditable(false);
		txtAreaCustInfo.setBounds(165, 299, 382, 237);
		infoPanel.add(txtAreaCustInfo);*/
		
		
		
		lblSChkBooking = new JLabel("Site Check Booking:");
		lblSChkBooking.setBounds(478, 68, 128, 14);
		infoPanel.add(lblSChkBooking);
		
		spnTimeDate = new JSpinner();
		spnTimeDate.setModel(new SpinnerDateModel(new Date(1505908800000L), null, null, Calendar.DAY_OF_YEAR));
		spnTimeDate.setBounds(634, 65, 284, 20);
		infoPanel.add(spnTimeDate);
		
		comBxSChkDoneBy = new JComboBox();
		comBxSChkDoneBy.setBounds(634, 115, 284, 20);
		infoPanel.add(comBxSChkDoneBy);
		
		lblSiteCheckBy = new JLabel("Site Check By:");
		lblSiteCheckBy.setBounds(478, 118, 128, 14);
		infoPanel.add(lblSiteCheckBy);
		
		chckbxSChkComp = new JCheckBox("Site Check Completed");
		chckbxSChkComp.setBounds(634, 159, 180, 23);
		infoPanel.add(chckbxSChkComp);
		
		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(478, 209, 148, 23);
		infoPanel.add(btnCancel);
		btnCancel.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   resetTable();
				   
				 //Resetting the other attributes within the tab
				   spnTimeDate.setValue(null);
				   comBxSChkDoneBy.setSelectedItem(null);
				   chckbxSChkComp.setSelected(false);
				}					
			}
		});
		
		btnSave = new JButton("Save Details");
		btnSave.setBounds(770, 209, 148, 23);
		infoPanel.add(btnSave);

		 this.setLayout(null);
	     this.add(tablePanel); 
	     this.add(infoPanel);

	     tablePanel.add(scrollPane, BorderLayout.CENTER);
	     tablePanel.add(salesTbl.getTableHeader(), BorderLayout.NORTH);
	  	
	  	salesTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					//rowSelected=true;
					try{
					param = salesTbl.getValueAt(salesTbl.getSelectedRow(), 0).toString();
					//displayClientDetails(param);
					txtAreaCustInfo.setText(sp.DisplayClientDetails(param));
					} catch (IndexOutOfBoundsException e){
						
					}
				}
			}
	  	});	
	  
	  
	 // 	rs = sp.getResults(1, conDeets);		
	  //	salesTbl.setModel(DbUtils.resultSetToTableModel(rs));  	
	  	//spaceHeader();  
	  	
//	  	this.add(infoPanel, BorderLayout.SOUTH);
	  	
/*	  	salesTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					try{
					param = salesTbl.getValueAt(salesTbl.getSelectedRow(), 0).toString();
		        	updatePermitDetails(param);
					} catch (IndexOutOfBoundsException e){
						//
					}
				}
			}
	  	});*/
}
	
	protected void resetTable() {
		//Fix this little null error 
		ResultSet rs = sp.getResults(3,  conDeets);
	  	salesTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
	  	//spaceHeader(columnModel, columnWidth);
	  	//sentChk.setSelected(false);
	  	
		//rowSelected=false;
		param = "";
		txtAreaCustInfo.setText("");

	}	

public JTable getSalesTbl(){
	return salesTbl;
}

	
	/*private void updatePermitDetails(String parameter) {
   try
   {
   	Connection conn = connecting.CreateConnection(conDets);
   	PreparedStatement st2 =conn.prepareStatement(result2 + parameter);
   	ResultSet rs2 = st2.executeQuery();

           //Retrieve by column name
   	 while(rs2.next()){
   		 
   		 txtAreaCustInfo.setText("\n INVOICE:\t" + param + "\n");
   		 txtAreaCustInfo.append( " CLIENT:\t" + rs2.getString("CustomerName") + "\n\n");
   		 txtAreaCustInfo.append( " SITE:\t" + rs2.getString("StreetAddress") + "\n");
   		 txtAreaCustInfo.append( "\t" + rs2.getString("Suburb") + "\n\n");
   		 txtAreaCustInfo.append( " POSTAL:\t" + rs2.getString("CustomerAddress") + "\n");               
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
	}	*/
}

	//}


		
	/*	
	}
}
*/