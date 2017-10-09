package Permit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import net.proteanit.sql.DbUtils;



class CCCToClientPanel extends JPanel {

	private int [] columnWidth = {30, 100, 120, 80, 40, 40, 40, 40, 40}; 	
	private String upCCCClient = "{Call AWS_WCH_DB.dbo.[p_PermitUpdateCCCToClient] (?,?)}";
	
	private String param = "";  
	private ResultSet rs;
	
	private Boolean rowSelected = false;
	
	private CreateConnection connecting;
	
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable permitsTbl;
	private DefaultTableModel model1;
	
	private JTextArea detailsTxtArea;
	
	private JLabel sentLbl;
	private JCheckBox sentChk;
	
	private JLabel sentDateLbl;
	private JSpinner sentDate;
	
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	
	private CreateConnection conn;
	
	private Boolean lockForm;
	private ConnDetails conDeets;
	private PermitPane pp;

	
	public CCCToClientPanel(Boolean lockForm, ConnDetails conDetts, PermitPane ppn) {

		  this.lockForm = lockForm;
		  this.conDeets = conDetts;
		  this.pp = ppn;
  		  
		  connecting = new CreateConnection();
	  	 		  	
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
	        	    	
	    	sentLbl = new JLabel("Customer Balance Paid:");
	    	sentLbl.setBounds(800, 110, 150, 20);
	        infoPanel.add(sentLbl);			
	        sentChk = new JCheckBox("");
	        sentChk.setSelected(false);
	        sentChk.setBounds(950, 110, 95, 20);
	        infoPanel.add(sentChk);
	        
	        sentDateLbl = new JLabel("Close Permit:");
	        sentDateLbl.setBounds(800, 140, 95, 20);
	        infoPanel.add(sentDateLbl);
	        
	        SimpleDateFormat dt = new SimpleDateFormat("dd.MMM.yyyy");
	        sentDate = new JSpinner(new SpinnerDateModel());
	        sentDate.setEditor(new JSpinner.DateEditor(sentDate, dt.toPattern()));
	        sentDate.setBounds(895, 140, 150, 20);
			infoPanel.add(sentDate);
	        
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

			cancelPermitReqBtn.addActionListener( new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) {
				   { 
					   resetTable();
					}					
				}
			});
			savePermitReqBtn.addActionListener( new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) {
				   { 
					   if(rowSelected){
						   if(sentChk.isSelected()){
					        	int input = JOptionPane.showConfirmDialog(null,"Update database and Close Permit?\n "
					        			+ "This Customer will no longer display on this page!",  "Close Permit?", JOptionPane.YES_NO_OPTION);
					        	if (input == 0){
					        		updateCCCToClient(param);	        		
					        		pp.showMessage("Updating Consent...");	        		
							        resetTable();
					        	}
						   }else{
								pp.showMessage("Check Customer Balance Paid before Saving");
						   }
					   }else {
						   pp.showMessage("Select a Customer before Saving");
					   }
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
						
						//	displayClientDetails(param);
						detailsTxtArea.setText(pp.DisplayClientDetails(param));
						
						} catch (IndexOutOfBoundsException e){
							//Ignoring IndexOutOfBoundsExceptions!
						}
						}
					}
			  	});
	}
	
	protected void updateCCCToClient(String invoice) {
		
		CallableStatement pm = null;

		try {
				
			String update = upCCCClient;	
		    Connection conn = connecting.CreateConnection(conDeets);	        	   	
		
		    pm = conn.prepareCall(update);
			
		    pm.setString(1, invoice);
		    pm.setString(2, getCCCSentDate());
			
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
	
	public void spaceHeader(TableColumnModel colM, int[] colW) {
	    int i;
	   	TableColumn tabCol = colM.getColumn(0);
	   	for (i=0; i<colW.length; i++){
	      	tabCol = colM.getColumn(i);
	      	tabCol.setPreferredWidth(colW[i]);
	   	}
	  	header.repaint();
	  }      

	protected void resetTable() {
		
		ResultSet rs = pp.getResults(4);
	  	permitsTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
	  	spaceHeader(columnModel, columnWidth);
	  	sentChk.setSelected(false);
	  	
		rowSelected=false;
		param = "";
		detailsTxtArea.setText("");

}		
    
		private void displayClientDetails(String parameter) {
			 detailsTxtArea.setText(pp.DisplayClientDetails(param));
		}

	    public String getCCCSentDate(){  	
	    	Date dte = (Date) sentDate.getValue(); 
	       	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	    	String dt = sdf1.format(dte);
	    	return dt;
	    } 	
	    
	    public JTable getPermitsTbl(){
	    	return permitsTbl;
	    }		
}