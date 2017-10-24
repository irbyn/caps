package Installs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
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
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Main.GetJobs;
import Permit.PermitPane;
import net.proteanit.sql.DbUtils;

class BookingsPanel extends JPanel {

	
	private int [] columnWidth = {30, 100, 100, 80, 40, 40, 40, 40}; 	
	private String upCCCClient = "{Call AWS_WCH_DB.dbo.[p_PermitUpdateCCCToClient] (?,?)}";
	private Color LtGray = Color.decode("#eeeeee");
	
	private String param = "";  
	private ResultSet rs;
	
	private Boolean rowSelected = false;
	
	private CreateConnection connecting;
	
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable installTbl;
	private DefaultTableModel tmod2;
	
	private JTextArea detailsTxtArea;
	private JTextArea stockTxtArea;
	
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	
	private CreateConnection conn;
		
	private Boolean lockForm;
	private ConnDetails conDeets;
	private InstallsPane ip;


public BookingsPanel(Boolean lockForm, ConnDetails conDetts, InstallsPane ipn) {

		this.lockForm = lockForm;
		this.conDeets = conDetts;
		this.ip = ipn;

		connecting = new CreateConnection();
		  	
		tmod2 = new DefaultTableModel();  
		tmod2.setRowCount(0);
	    installTbl = new JTable(tmod2);
	    installTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    installTbl.setAutoCreateRowSorter(true);
      
	    JScrollPane scrollPane = new JScrollPane(installTbl);
	  
	    header= installTbl.getTableHeader();
	    columnModel = header.getColumnModel();
	    add(header); 
              	        
	    tablePanel = new JPanel();
	    tablePanel.setBounds(20, 20, 1025, 400);  //setPreferredSize(new Dimension(0, 300));      
	    tablePanel.setLayout(new BorderLayout());
      
	    infoPanel = new JPanel();
	    infoPanel.setBounds(0, 420, 1100, 140);  //setPreferredSize(new Dimension(0, 300));
	    infoPanel.setLayout(null);
      
	    detailsTxtArea = new JTextArea("");
	    detailsTxtArea.setBounds(20, 20, 250, 120);
	    detailsTxtArea.setBorder(BorderFactory.createEtchedBorder());
	    detailsTxtArea.setBackground(LtGray);
	    detailsTxtArea.setLineWrap(true);
	    detailsTxtArea.setEditable(false);
	    infoPanel.add(detailsTxtArea);
	    
	    stockTxtArea = new JTextArea("");
	    stockTxtArea.setBounds(290, 20, 430, 120);
	    stockTxtArea.setBorder(BorderFactory.createEtchedBorder());
	    stockTxtArea.setBackground(LtGray);
	    stockTxtArea.setTabSize(6);
	    stockTxtArea.setLineWrap(true);
	    stockTxtArea.setEditable(false);
	    infoPanel.add(stockTxtArea);
      
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
	  	tablePanel.add(installTbl.getTableHeader(), BorderLayout.NORTH);        

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
				   }
			   }
			}
		});
		installTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					rowSelected=true;
					clearStock();
		//			pp.setFormsLocked();
					try{
					param = installTbl.getValueAt(installTbl.getSelectedRow(), 0).toString();
					JOptionPane.showMessageDialog(null, ""+stockTxtArea.getTabSize());
					stockTxtArea.setText(ip.DisplayStockOnOrder(param));
					} catch (IndexOutOfBoundsException e){
						//Ignoring IndexOutOfBoundsExceptions!
					}
					}
				}
		  	});
}

protected void clearStock() {
	detailsTxtArea.setText("");
	stockTxtArea.setText("");
	tmod2.setRowCount(0);
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
	
	ResultSet rs = ip.getResults(4,  conDeets);
	installTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
  	spaceHeader(columnModel, columnWidth);  	
	rowSelected=false;
	param = "";
	clearStock();
}	

public JTable getInstallTbl(){
	return installTbl;
}
public JPanel getInfoPanel(){
	return infoPanel;
}
}
