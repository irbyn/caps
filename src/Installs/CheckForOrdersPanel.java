package Installs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import DB_Comms.CreateConnection;
import Installs.LoadDocsPanel.FileCellRenderer;
import Installs.LoadDocsPanel.ListTransferHandler;
import Main.ConnDetails;
import Permit.PermitPane;
import net.proteanit.sql.DbUtils;

class CheckForOrdersPanel extends JPanel {
	
	private int [] columnWidth = {30, 100, 120, 80, 40, 40, 40, 40}; 	
	private String upCCCClient = "{Call AWS_WCH_DB.dbo.[p_PermitUpdateCCCToClient] (?,?)}";
	private String folder = "//C:/pdfs/Invoice/INV_";
//	private String invPfx = "INV_";
	private String param = "";  
	private ResultSet rs;
	
	private String[] colNames = {"Qty", "Code", "Description"};
	private int [] colWidth = {30, 80, 280};
	
	private Boolean rowSelected = false;
	
	private CreateConnection connecting;
	
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable permitsTbl;
	private DefaultTableModel model1;
	
	private JTable stockTbl;
	private JTableHeader hd;
	private TableColumnModel cm;
	private DefaultTableModel model2;
	private JPanel stockPanel;
	
	private JTextArea detailsTxtArea;	
	private JLabel sentLbl;
	private JCheckBox sentChk;
	private JLabel sentDateLbl;
	private JSpinner sentDate;
	
	private JButton getStockBtn;
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 

	private String text;
	private CreateConnection conn;
	
	private Boolean lockForm;
	private ConnDetails conDeets;
	private InstallsPane ip;


public CheckForOrdersPanel(Boolean lockForm, ConnDetails conDetts, InstallsPane ipn) {

	  this.lockForm = lockForm;
	  this.conDeets = conDetts;
	  this.ip = ipn;

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
      
      stockPanel = new JPanel();
      stockPanel.setBounds(290, 20, 410, 265); 
      stockPanel.setLayout(new BorderLayout());
      
      detailsTxtArea = new JTextArea("");
      detailsTxtArea.setBounds(20, 20, 250, 265);
      detailsTxtArea.setBorder(BorderFactory.createLineBorder(Color.black));
      detailsTxtArea.setLineWrap(true);
      detailsTxtArea.setEditable(false);      
      infoPanel.add(detailsTxtArea);
      
	    model2 = new DefaultTableModel(colNames,0);
	    stockTbl = new JTable(model2);
	    model2.setRowCount(0);
	    stockTbl.setPreferredSize(new Dimension(0, 300));
	    stockTbl.setAutoCreateRowSorter(true);
        
        JScrollPane sp = new JScrollPane(stockTbl);
	  
        hd= stockTbl.getTableHeader();
        
        cm = hd.getColumnModel();
	  	spaceHeader(cm, colWidth);
        add(hd);
      
      getStockBtn = new JButton("Get Stock Items");
      getStockBtn.setBounds(720, 20, 150, 25);
      infoPanel.add(getStockBtn);
      
      cancelPermitReqBtn = new JButton("Cancel");
      cancelPermitReqBtn.setBounds(720, 260, 150, 25);
      infoPanel.add(cancelPermitReqBtn);
      
      savePermitReqBtn = new JButton("Save Stock Info");
      savePermitReqBtn.setBounds(895, 260, 150, 25);
      infoPanel.add(savePermitReqBtn);

      this.setLayout(null);
      this.add(tablePanel);
      infoPanel.add(stockPanel);
      this.add(infoPanel);
      
      stockPanel.add(sp, BorderLayout.CENTER);
      stockPanel.add(stockTbl.getTableHeader(), BorderLayout.NORTH); 
	  	
	  	tablePanel.add(scrollPane, BorderLayout.CENTER);
	  	tablePanel.add(permitsTbl.getTableHeader(), BorderLayout.NORTH);        

	  	getStockBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   readInvoice();
				}					
			}
		});	
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
	  	permitsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					rowSelected=true;
		//			pp.setFormsLocked();
					try{
					param = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 0).toString();
					
					detailsTxtArea.setText(ip.DisplayClientDetails(param));
					
					
					} catch (IndexOutOfBoundsException e){
						//Ignoring IndexOutOfBoundsExceptions!
					}
					}
				}
		  	});
}

protected void readInvoice() {

	   String src = folder + param + ".pdf";
	      //Loading an existing document
	      File file = new File(src);
	          		      
	      PDDocument document;
		try {
			document = PDDocument.load(file);
		      //Instantiate PDFTextStripper class
		      PDFTextStripper pdfStripper = new PDFTextStripper();

		      //Retrieving text from PDF document
		      text = pdfStripper.getText(document); 
		      
		} catch (IOException e) {
			e.printStackTrace();
		}
	      
   
	      
	      
	      String patternString = "=========================================================="
	      		+ "========================================================================";
	        Pattern pattern = Pattern.compile(patternString);
	        
	        String[] split = pattern.split(text);
	        
	        String inv = split[3];
	        

	        int chars = inv.length();

	        String[] stkLine = new String[25];
	        int i=0;
	        int lineLength = 130;
	        int start = 0;
	        int end = lineLength + start;
	        String foundPermit = "";
	        String stockCode = "";
	        String stockDesc = "";
	        String stockQty = "";
	        
	        System.out.println("");

	        for (i=0;i<25;i++){
	        	
	        	stkLine[i] = inv.substring(start, end);
	        	
	        	foundPermit = stkLine[i].substring(2,6);
	        	
	        	if (foundPermit.equals("INST") || foundPermit.equals("PERM")){	        		
	        	}
	        	else {

			    stockCode = stkLine[i].substring(2,20);
			    stockDesc = stkLine[i].substring(23,82);
			    stockQty = stkLine[i].substring(96,99);
			    
			    model2.addRow(new Object[]{stockQty, stockCode, stockDesc});

/*	        	System.out.println("Qty:  " + stockQty);  // + "          ReesCode: " + stockCode);
	        	System.out.println("CODE: " + stockCode);
	        	System.out.println("Desc: " + stockDesc);
			    System.out.println("");
*/	        	
	        	start = start + lineLength;
	        	end = end + lineLength;
	        	}
	        	
	        	
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
	
	ResultSet rs = ip.getResults(1,  conDeets);
  	permitsTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
  	spaceHeader(columnModel, columnWidth);
  	
	rowSelected=false;
	param = "";
	detailsTxtArea.setText("");
	int stkRows = model2.getRowCount();
	for (int i = 0;i<stkRows;i++){
		model2.removeRow(0);
	}

}	
public JTable getPermitsTbl(){
	return permitsTbl;
}
}