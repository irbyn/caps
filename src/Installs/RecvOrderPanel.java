package Installs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
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
import Permit.PermitPane;
import net.proteanit.sql.DbUtils;

class RecvOrderPanel extends JPanel {

	private int [] columnWidth = {20, 100, 100, 40, 20, 80, 300, 50}; 	
	private String upReceived = "{Call AWS_WCH_DB.dbo.[i_updateStockReceived] (?,?)}";
	private String[] colNames = {"Invoice", "PO Number", "Qty", "Stock Code", "Description", "Install Date"};
	private int [] colWidth = {30, 30, 20, 80, 300, 50};
	private StringBuilder sb;
	
	private String invoiceNum = "";
	private String reportFrom = "";
	private String inv; 
	private String stk;
	
	private ResultSet rs;
	private Color LtGray = Color.decode("#eeeeee");
	
	private Boolean rowSelected = false;
	
	private CreateConnection connecting;
	
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JPanel poPanel;
	
	private JTable installTbl;
	private DefaultTableModel model1;
	
	private JTextArea detailsTxtArea;
	
	private JTable poTbl;
	private JTableHeader hd2;
	private TableColumnModel cmod2;
	private DefaultTableModel tmod2;
	
	
	private JButton printAllGoodsRcvdBtn; 
	private JButton cancelGoodsRcvdBtn; 
	private JButton saveGoodsRcvdBtn; 
	private JSpinner reportDate;
	
	private CreateConnection conn;
	
	private Boolean lockForm;
	private ConnDetails conDeets;
	private InstallsPane ip;


	public RecvOrderPanel(Boolean lockForm, ConnDetails conDetts, InstallsPane ipn) {

		this.lockForm = lockForm;
		this.conDeets = conDetts;
		this.ip = ipn;

		connecting = new CreateConnection();
		  	
		model1 = new DefaultTableModel();  
		model1.setRowCount(0);
		installTbl = new JTable(model1);
		installTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		installTbl.setAutoCreateRowSorter(true);
      
		JScrollPane scrollPane = new JScrollPane(installTbl);
	  
		header= installTbl.getTableHeader();
		columnModel = header.getColumnModel();
		add(header); 
              	        
		tablePanel = new JPanel();
		tablePanel.setBounds(20, 20, 1025, 260);      
		tablePanel.setLayout(new BorderLayout());
      
		infoPanel = new JPanel();
		infoPanel.setBounds(0, 280, 1100, 300);  
		infoPanel.setLayout(null);
      
		detailsTxtArea = new JTextArea("");
		detailsTxtArea.setBounds(20, 20, 250, 260);
        detailsTxtArea.setBorder(BorderFactory.createEtchedBorder());
		detailsTxtArea.setBackground(LtGray);
		detailsTxtArea.setLineWrap(true);
		detailsTxtArea.setEditable(false);
		infoPanel.add(detailsTxtArea);
		
	      poPanel = new JPanel();
	      poPanel.setBounds(290, 20, 755, 170);		  
	      poPanel.setLayout(new BorderLayout());
	           
		  tmod2 = new DefaultTableModel(colNames,0);
	      poTbl = new JTable(tmod2);
	      poTbl.setAutoCreateRowSorter(true);        
	      JScrollPane sp = new JScrollPane(poTbl);	  
	      hd2= poTbl.getTableHeader();        
	      cmod2 = hd2.getColumnModel();
		  spaceHeader(cmod2, colWidth);
	      add(hd2);
		
		printAllGoodsRcvdBtn = new JButton("Print Received List");
		printAllGoodsRcvdBtn.setBounds(545, 260, 150, 25);
		infoPanel.add(printAllGoodsRcvdBtn);
		
		cancelGoodsRcvdBtn = new JButton("Cancel");
		cancelGoodsRcvdBtn.setBounds(720, 260, 150, 25);
		infoPanel.add(cancelGoodsRcvdBtn);
      
		saveGoodsRcvdBtn = new JButton("Save Received");
		saveGoodsRcvdBtn.setBounds(895, 260, 150, 25);
		infoPanel.add(saveGoodsRcvdBtn);

		this.setLayout(null);
		this.add(tablePanel); 
		infoPanel.add(poPanel);
		this.add(infoPanel);
	      
		poPanel.add(sp, BorderLayout.CENTER);
		poPanel.add(poTbl.getTableHeader(), BorderLayout.NORTH); 
		  
	  	tablePanel.add(scrollPane, BorderLayout.CENTER);
	  	tablePanel.add(installTbl.getTableHeader(), BorderLayout.NORTH);        

	  	printAllGoodsRcvdBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   showReceivedItems();
				}					
			}
		});
	  	cancelGoodsRcvdBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   resetTable();
				}					
			}
		});
	  	saveGoodsRcvdBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   if(tmod2.getRowCount()!=-1){ 
					   receiveStock();
					   
				   }
			   }
			}
		});
	  	installTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					rowSelected=true;
		//			pp.setFormsLocked();
					try{
						invoiceNum = installTbl.getValueAt(installTbl.getSelectedRow(), 0).toString();
					
					detailsTxtArea.setText(ip.DisplayClientDetails(invoiceNum));
					
					} catch (IndexOutOfBoundsException e){
						//Ignoring IndexOutOfBoundsExceptions!
					}
					}
				}
		  	});
		installTbl.addMouseListener(new MouseAdapter() {
	  	    @Override
	  	    public void mouseClicked(MouseEvent evt) {
	  	        if (evt.getClickCount() == 2) {
	  	        	
	  	        	int actualRow = installTbl.convertRowIndexToModel(installTbl.rowAtPoint(evt.getPoint())); 
	  	        	int clickedRow = installTbl.rowAtPoint(evt.getPoint());	
	  	        	moveRow(clickedRow);		
	  	        	((DefaultTableModel)installTbl.getModel()).removeRow(actualRow);	
	  	         }		  	      
	  	    }
	  	});
	  	poTbl.addMouseListener(new MouseAdapter() {
	  	    @Override
	  	    public void mouseClicked(MouseEvent evt) {
	  	        if (evt.getClickCount() == 2) {
	  	        	int actualRow = poTbl.convertRowIndexToModel(poTbl.rowAtPoint(evt.getPoint()));
	  	        	int clickedRow = poTbl.rowAtPoint(evt.getPoint());	  	        	
	  	        	returnRow(clickedRow);
	  	        	tmod2.removeRow(actualRow);	
	  	         }		  	      
	  	    }
	  	});
}

protected void receiveStock() {
	
	   int rows = tmod2.getRowCount();
	   int i;
	   inv = ""; 
	   stk = "";

	   for (i = 0; i < rows; i++ ){
		   inv = tmod2.getValueAt(i, 0).toString(); 
		   stk = tmod2.getValueAt(i, 3).toString();
//		   JOptionPane.showMessageDialog(null, "inv: " + inv + "  stk: " + stk);
		   updateStockReceived();
	   }		
	   
	   resetTable();
		
	}

private void updateStockReceived() {
	CallableStatement pm = null;

	try {
				
	    Connection conn = connecting.CreateConnection(conDeets);	        	   	
	
	    pm = conn.prepareCall(upReceived);
		
	    pm.setInt(1, Integer.parseInt(inv));
	    pm.setString(2, stk);
   	    
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

protected void showReceivedItems() {
	
    SimpleDateFormat repModel = new SimpleDateFormat("dd.MMM.yyyy");
	reportDate = new JSpinner(new SpinnerDateModel());
	reportDate.setEditor(new JSpinner.DateEditor(reportDate, repModel.toPattern()));
	int option = JOptionPane.showOptionDialog(null, reportDate, "Enter Date of Report start", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
	if (option == JOptionPane.CANCEL_OPTION)
	{
	    // user hit cancel
	} else if (option == JOptionPane.OK_OPTION)
	{
    	Date dte = (Date) reportDate.getValue(); 
       	SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
    	String dt = sdf1.format(dte);
  	
    	sb = ip.reportStockReceived(dt);

	    String prefix = "stockReceived_";
	    String suffix = ".txt";


	    File tempFile;
		try {
			tempFile = File.createTempFile(prefix, suffix);
		    tempFile.deleteOnExit();
		    
		    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
		        writer.write(sb.toString());
		    }
		      if (Desktop.isDesktopSupported()) {
		    	    try {
		    	        Desktop.getDesktop().open(tempFile);
		    	    } catch (IOException ex) {
		    	    }
		    	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

private void moveRow(int row){
	String po;
	if (installTbl.getValueAt(row, 3)!=null){
		po = installTbl.getValueAt(row, 3).toString();
	} else {
		po = "";
	}
	String instDate;
	if (installTbl.getValueAt(row, 7)!=null){
		instDate = installTbl.getValueAt(row, 7).toString();
	} else {
		instDate = "";
	}

String[] rowData = new String[]{installTbl.getValueAt(row, 0).toString(),
								po,
								installTbl.getValueAt(row, 4).toString(),
								installTbl.getValueAt(row, 5).toString(),
								installTbl.getValueAt(row, 6).toString(),
								instDate};
		tmod2.addRow(rowData);
}
private void returnRow(int row){
	DefaultTableModel mod = (DefaultTableModel) installTbl.getModel();
String[] rowData = new String[]{poTbl.getValueAt(row, 0).toString(), "", "",
								poTbl.getValueAt(row, 1).toString(),
								poTbl.getValueAt(row, 2).toString(),
								poTbl.getValueAt(row, 3).toString(),
								poTbl.getValueAt(row, 4).toString(),
								poTbl.getValueAt(row, 5).toString()};
		
mod.addRow(rowData);
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
	detailsTxtArea.setText("");
	ResultSet rs = ip.getResults(3,  conDeets);
	installTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
  	spaceHeader(columnModel, columnWidth);
	tmod2.setRowCount(0);
	rowSelected=false;
	invoiceNum = "";


}		
public JTable getInstallTbl(){
	return installTbl;
}
public JPanel getInfoPanel(){
	return infoPanel;
}
}

