package Schedule;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
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
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
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
import Installs.InstallsPane;
import Main.ConnDetails;
import Main.PrintSiteCheck;
import net.proteanit.sql.DbUtils;


class ViewSiteChecksPanel extends JPanel {

	private int [] columnWidth = {40, 100, 100, 60, 60, 60, 60}; 	
	private String upReceived = "{Call AWS_WCH_DB.dbo.[i_updateStockReceived] (?,?)}";
	private String[] colNames = {"Sale ID", "Customer Name", "Street Address", "Suburb", "SC Date", "SC Time", "SC By"};

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
	
	private JTable timeTbl;
	private DefaultTableModel model1;
		
	private JTable scTbl;
	private JTableHeader hd2;
	private TableColumnModel cmod2;
	private DefaultTableModel tmod2;
	
	
	private JButton printSiteCheckBtn; 
	private JButton cancelGoodsRcvdBtn; 
//	private JButton saveGoodsRcvdBtn; 
	private JSpinner reportDate;
	
	private CreateConnection conn;
	
	private Boolean lockForm;
	private ConnDetails conDeets;
	private SchedulePane sp;
	
	public ViewSiteChecksPanel(Boolean lockForm, ConnDetails conDetts, SchedulePane spn) {

		this.lockForm = lockForm;
		this.conDeets = conDetts;
		this.sp = spn;

//		connecting = new CreateConnection();
		  	
		model1 = new DefaultTableModel();  
		model1.setRowCount(0);
		timeTbl = new JTable(model1);
		timeTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		timeTbl.setAutoCreateRowSorter(true);
      
		JScrollPane scrollPane = new JScrollPane(timeTbl);
	  
		header= timeTbl.getTableHeader();
		columnModel = header.getColumnModel();
		add(header); 
              	        
		tablePanel = new JPanel();
		tablePanel.setBounds(20, 20, 1025, 260);      
		tablePanel.setLayout(new BorderLayout());
      
		infoPanel = new JPanel();
		infoPanel.setBounds(0, 280, 1100, 300);  
		infoPanel.setLayout(null);
/*      
		detailsTxtArea = new JTextArea("");
		detailsTxtArea.setBounds(20, 20, 250, 260);
        detailsTxtArea.setBorder(BorderFactory.createEtchedBorder());
        detailsTxtArea.setBackground(LtGray);
		detailsTxtArea.setLineWrap(true);
		detailsTxtArea.setEditable(false);
		infoPanel.add(detailsTxtArea);
*/		
	      poPanel = new JPanel();
	      poPanel.setBounds(20, 20, 1025, 170);		  
	      poPanel.setLayout(new BorderLayout());
	           
		  tmod2 = new DefaultTableModel(colNames,0);
	      scTbl = new JTable(tmod2);
	      scTbl.setAutoCreateRowSorter(true);        
	      JScrollPane spane = new JScrollPane(scTbl);	  
	      hd2= scTbl.getTableHeader();        
	      cmod2 = hd2.getColumnModel();
		  spaceHeader(cmod2, columnWidth);
	      add(hd2);
		
		printSiteCheckBtn = new JButton("Print Site Check");
		printSiteCheckBtn.setBounds(545, 260, 150, 25);
		infoPanel.add(printSiteCheckBtn);
		
		cancelGoodsRcvdBtn = new JButton("Cancel");
		cancelGoodsRcvdBtn.setBounds(720, 260, 150, 25);
		infoPanel.add(cancelGoodsRcvdBtn);
      
/*		saveGoodsRcvdBtn = new JButton("Save Received");
		saveGoodsRcvdBtn.setBounds(895, 260, 150, 25);
		infoPanel.add(saveGoodsRcvdBtn);
*/
		this.setLayout(null);
		this.add(tablePanel); 
		infoPanel.add(poPanel);
		this.add(infoPanel);
	      
		poPanel.add(spane, BorderLayout.CENTER);
		poPanel.add(scTbl.getTableHeader(), BorderLayout.NORTH); 
		  
	  	tablePanel.add(scrollPane, BorderLayout.CENTER);
	  	tablePanel.add(timeTbl.getTableHeader(), BorderLayout.NORTH);        

	  	printSiteCheckBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   int sChks = scTbl.getRowCount();
				   if(sChks !=0){
					   for(int i = 0; i < sChks; i++){
						   String saleID = scTbl.getValueAt(i, 0).toString();
						   PrintSiteCheck sc = new PrintSiteCheck(saleID, conDeets);
					   }
					   
				   }else {
					   sp.showMessage("Select a Sale to Print (Double-Right-Click)");
				   }

				   //showReceivedItems();
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
	  	timeTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					rowSelected=true;
		//			pp.setFormsLocked();
					try{
//						invoiceNum = timeTbl.getValueAt(timeTbl.getSelectedRow(), 0).toString();
					
//					detailsTxtArea.setText(ip.DisplayClientDetails(invoiceNum));
					
					} catch (IndexOutOfBoundsException e){
						//Ignoring IndexOutOfBoundsExceptions!
					}
					}
				}
		  	});
	  	timeTbl.addMouseListener(new MouseAdapter() {
	  	    @Override
	  	    public void mouseClicked(MouseEvent evt) {
	  	        if (evt.getClickCount() == 2) {
	  	        	
	  	        	int actualRow = timeTbl.convertRowIndexToModel(timeTbl.rowAtPoint(evt.getPoint())); 
	  	        	int clickedRow = timeTbl.rowAtPoint(evt.getPoint());	
	  	        	moveRow(clickedRow, timeTbl, scTbl);		
	  	        	((DefaultTableModel)timeTbl.getModel()).removeRow(actualRow);	
	  	         }		  	      
	  	    }
	  	});
	  	scTbl.addMouseListener(new MouseAdapter() {
	  	    @Override
	  	    public void mouseClicked(MouseEvent evt) {
	  	        if (evt.getClickCount() == 2) {
	  	        	int actualRow = scTbl.convertRowIndexToModel(scTbl.rowAtPoint(evt.getPoint()));
	  	        	int clickedRow = scTbl.rowAtPoint(evt.getPoint());	 
	  	        	moveRow(clickedRow, scTbl, timeTbl);
//	  	        	returnRow(clickedRow);
	  	        	tmod2.removeRow(actualRow);	
	  	         }		  	      
	  	    }
	  	});
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
  	
//    	sb = sp.reportStockReceived(dt);

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
				e.printStackTrace();
			}
		}		
	}

private void moveRow(int row, JTable fromTbl, JTable toTbl){
	String date;
	if (fromTbl.getValueAt(row, 4)!=null){
		date = fromTbl.getValueAt(row, 4).toString();
	} else {
		date = "";
	}
	String time;
	if (fromTbl.getValueAt(row, 5)!=null){
		time = fromTbl.getValueAt(row, 5).toString();
	} else {
		time = "";
	}

String[] rowData = new String[]{	fromTbl.getValueAt(row, 0).toString(),
									fromTbl.getValueAt(row, 1).toString(),
									fromTbl.getValueAt(row, 2).toString(),
									fromTbl.getValueAt(row, 3).toString(),
									date,
									time,
									fromTbl.getValueAt(row, 6).toString()//,instDate
								};
			((DefaultTableModel)toTbl.getModel()).addRow(rowData);

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
//	detailsTxtArea.setText("");
	ResultSet rs = sp.getResults(1, "'2017-10-10'");
	timeTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
  	spaceHeader(columnModel, columnWidth);
	tmod2.setRowCount(0);
	rowSelected=false;
	invoiceNum = "";


}		
public JTable getScheduleTbl(){
	return timeTbl;
}
public JPanel getInfoPanel(){
	return infoPanel;
}
}

