package Sales; 

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;

import javax.swing.JLabel;
import javax.swing.JTextField;

class QuotePanel extends JPanel {
	private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
	private String result3 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
	private String param = "";  
	private ResultSet rs;
	
	private CreateConnection connecting;
	
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable salesTbl;
	private DefaultTableModel model1;
	
	private ConnDetails conDets;

	
	private JTextField txtBxReesCode;
	private JTextField txtBxQuoteNum;

	public QuotePanel(ConnDetails conDeets, SalesPane sp) {
		 conDets = conDeets;


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
			
			JTextArea txtAreaCustInfo = new JTextArea();
			txtAreaCustInfo.setEditable(false);
			txtAreaCustInfo.setBounds(23, 24, 382, 237);
			infoPanel.add(txtAreaCustInfo);
		
		txtBxReesCode = new JTextField();
		txtBxReesCode.setBounds(670, 51, 140, 20);
		infoPanel.add(txtBxReesCode);
		txtBxReesCode.setColumns(10);
		
		txtBxQuoteNum = new JTextField();
		txtBxQuoteNum.setBounds(920, 51, 113, 20);
		infoPanel.add(txtBxQuoteNum);
		txtBxQuoteNum.setColumns(10);
		
		JTextArea txtBxSCheck = new JTextArea();
		txtBxSCheck.setBounds(583, 124, 97, 94);
		infoPanel.add(txtBxSCheck);
		
		JTextArea txtBxQuote = new JTextArea();
		txtBxQuote.setBounds(757, 124, 97, 94);
		infoPanel.add(txtBxQuote);
		
		JTextArea txtBxPhoto = new JTextArea();
		txtBxPhoto.setBounds(936, 124, 97, 94);
		infoPanel.add(txtBxPhoto);
		
		JLabel lblSiteCheck = new JLabel("Site Check");
		lblSiteCheck.setBounds(608, 104, 72, 14);
		infoPanel.add(lblSiteCheck);
		
		JLabel lblQuote = new JLabel("Quote");
		lblQuote.setBounds(787, 104, 46, 14);
		infoPanel.add(lblQuote);
		
		JLabel lblPhoto = new JLabel("Photo");
		lblPhoto.setBounds(956, 104, 46, 14);
		infoPanel.add(lblPhoto);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(593, 227, 148, 23);
		infoPanel.add(btnCancel);
		
		JButton btnSave = new JButton("Save Quote Details");
		btnSave.setBounds(885, 227, 148, 23);
		infoPanel.add(btnSave);
		
        this.setLayout(null);
        this.add(tablePanel); 
        this.add(infoPanel);
	  
        
  	  tablePanel.add(scrollPane, BorderLayout.CENTER);
  	  tablePanel.add(salesTbl.getTableHeader(), BorderLayout.NORTH);
  	
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
