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
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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

import com.microsoft.sqlserver.jdbc.SQLServerException;

import DB_Comms.CreateConnection;
import Installs.LoadDocsPanel.FileCellRenderer;
import Installs.LoadDocsPanel.ListTransferHandler;
import Main.ConnDetails;
import Permit.PermitPane;
import net.proteanit.sql.DbUtils;

class CheckForOrdersPanel extends JPanel {
	
	private int [] columnWidth = {20, 150, 150, 100, 100, 100, 100, 100}; 	
	private String getSaleID = "EXEC AWS_WCH_DB.dbo.[i_InstallsGetSaleID] ";
	private String upStkList = "{Call AWS_WCH_DB.dbo.[i_InstallsUpdateFire] (?,?,?,?)}";
	private String upStkItem = "{Call AWS_WCH_DB.dbo.[i_InstallsUpdateItems] (?,?,?,?)}";
	private String folder = "//C:/pdfs/Invoice/";	
	private String invPfx = "INV_";
	private String sitePfx = "SC_";
	private String photoPfx = "PH_";

	private String invoiceNum = ""; 
	private String saleID = ""; 
	private String stkItems = "";
	private String qt = ""; 
	private String code = "";
	private String dsc = ""; 
	private ResultSet rs;
	
	private String[] colNames = {"Qty", "Code", "Description"};
	private int [] colWidth = {30, 80, 280};
	private String[] ocolNames = {"Row", "Description"};
	private int [] ocolWidth = {40, 280};
	
	private Color LtGray = Color.decode("#eeeeee");
	
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
	
	private JTable orderTbl;
	private JTableHeader ohd;
	private TableColumnModel ocm;
	private DefaultTableModel omodel;
	private JPanel orderPanel;
	
	private JTextArea detailsTxtArea;	
	
	private JButton getFireBtn;
	private JButton getOrdersBtn;
	
	private JLabel fireLbl;
	private JTextField fireTxtBx;

	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	
	JButton viewInvBtn;
	JButton viewSiteBtn;
	JButton viewPhotoBtn;
	
	boolean invExists;
	boolean siteExists;
	boolean photoExists;
	
	File inv;
	File site;
	File[] photosArr;
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
	 	tablePanel.setBounds(20, 20, 1025, 260);        
	 	tablePanel.setLayout(new BorderLayout());
      
	 	infoPanel = new JPanel();
	 	infoPanel.setBounds(0, 280, 1100, 300);  
	 	infoPanel.setLayout(null);
	 	
	 	orderPanel = new JPanel();
	 	orderPanel.setBounds(720, 120, 325, 130);
	 	orderPanel.setLayout(new BorderLayout());
      
	 	stockPanel = new JPanel();
	 	stockPanel.setBounds(290, 20, 410, 230); 
	 	stockPanel.setLayout(new BorderLayout());
      
	 	detailsTxtArea = new JTextArea("");
	 	detailsTxtArea.setBounds(20, 20, 250, 265);
	 	detailsTxtArea.setBorder(BorderFactory.createEtchedBorder());
	 	detailsTxtArea.setBackground(LtGray);
	 	detailsTxtArea.setLineWrap(true);
	 	detailsTxtArea.setEditable(false);      
	 	infoPanel.add(detailsTxtArea);
      
	    model2 = new DefaultTableModel(colNames,0);
	    stockTbl = new JTable(model2);       
        JScrollPane sp = new JScrollPane(stockTbl);
        hd= stockTbl.getTableHeader();        
        cm = hd.getColumnModel();
	  	spaceHeader(cm, colWidth);
        add(hd);
        
	    viewInvBtn = new JButton("View Invoice");
	    viewInvBtn.setBounds(290, 260, 123, 25);
	    viewInvBtn.setVisible(false);
	    infoPanel.add(viewInvBtn);
	    
	    viewSiteBtn = new JButton("View SiteCheck");
	    viewSiteBtn.setBounds(433, 260, 123, 25);
	    viewSiteBtn.setVisible(false);
	    infoPanel.add(viewSiteBtn);
	    
      	viewPhotoBtn = new JButton("View Photos");
      	viewPhotoBtn.setBounds(577, 260, 123, 25);
      	viewPhotoBtn.setVisible(false);
      	infoPanel.add(viewPhotoBtn);
        
        getFireBtn = new JButton("Mark Fire");
        getFireBtn.setBounds(720, 40, 150, 25);
        infoPanel.add(getFireBtn);
        
        getOrdersBtn = new JButton("Order Stock Item");
        getOrdersBtn.setBounds(720, 80, 150, 25);
        infoPanel.add(getOrdersBtn);

        fireLbl = new JLabel("Fire:");
        fireLbl.setBounds(895, 40, 30, 25);
        infoPanel.add(fireLbl);
        
        fireTxtBx = new JTextField();
        fireTxtBx.setBounds(925, 40, 120, 25);
        infoPanel.add(fireTxtBx);
 	
    	omodel = new DefaultTableModel(ocolNames,0);
	    orderTbl = new JTable(omodel);       
        JScrollPane osp = new JScrollPane(orderTbl);
        ohd= orderTbl.getTableHeader();        
        ocm = ohd.getColumnModel();
	  	spaceHeader(ocm, ocolWidth);
        add(ohd);
      
        cancelPermitReqBtn = new JButton("Cancel");
        cancelPermitReqBtn.setBounds(720, 260, 150, 25);
        infoPanel.add(cancelPermitReqBtn);
      
        savePermitReqBtn = new JButton("Save Stock Info");
        savePermitReqBtn.setBounds(895, 260, 150, 25);
        infoPanel.add(savePermitReqBtn);

        this.setLayout(null);
        this.add(tablePanel);
        infoPanel.add(orderPanel);
        infoPanel.add(stockPanel);
        this.add(infoPanel);
      
        stockPanel.add(sp, BorderLayout.CENTER);
        stockPanel.add(stockTbl.getTableHeader(), BorderLayout.NORTH); 
        orderPanel.add(osp, BorderLayout.CENTER);
        orderPanel.add(orderTbl.getTableHeader(), BorderLayout.NORTH); 
	  	
	  	tablePanel.add(scrollPane, BorderLayout.CENTER);
	  	tablePanel.add(permitsTbl.getTableHeader(), BorderLayout.NORTH);        

	  	
	  	viewInvBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 				   
					if (invExists){
					      if (Desktop.isDesktopSupported()) {
					    	    try {
					    	        Desktop.getDesktop().open(inv);
					    	    } catch (IOException ex) {
					    	    }
					    	}
					}			   
			   }
			}
		});
	  	viewSiteBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 				   
					if (siteExists){
					      if (Desktop.isDesktopSupported()) {
					    	    try {
					    	        Desktop.getDesktop().open(site);
					    	    } catch (IOException ex) {
					    	    }
					    	}
					}			   
			   }
			}
		});
	  	viewPhotoBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 		
				   int ph = photosArr.length;
				   ip.showMessage("" + ph);
					for (int i =0; i< ph; i++){
						if (photosArr[i].exists())
					      if (Desktop.isDesktopSupported()) {
					    	    try {
					    	        Desktop.getDesktop().open(photosArr[i]);
					    	    } catch (IOException ex) {
					    	    }
					    	}
					}			   
			   }
			}
		});
	  	getFireBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   recordFire();
				}					
			}
		});
	  	getOrdersBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   if(stockTbl.getSelectedRow()!=-1){
				   int row = stockTbl.getSelectedRow();
				   orderStock(row);
				   }
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
				   saveStockLines();
			   }
			}
		});
	  	permitsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					rowSelected=true;
					clearStock();
		//			pp.setFormsLocked();
					try{
						invoiceNum = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 0).toString();
					
						detailsTxtArea.setText(ip.DisplayClientDetails(invoiceNum));
						displayClientDetails(invoiceNum);
						readInvoice();
						checkForFiles();
					
					} catch (IndexOutOfBoundsException e){
						//Ignoring IndexOutOfBoundsExceptions!
					}
					}
				}
		  	});
	  	stockTbl.addMouseListener(new MouseAdapter() {
	  	    @Override
	  	    public void mouseClicked(MouseEvent evt) {
	  	        if (evt.getClickCount() == 2) {
		  	        orderStock(stockTbl.rowAtPoint(evt.getPoint()));				  	  
	  	         }		  	      
	  	    }
	  	});
	  	orderTbl.addMouseListener(new MouseAdapter() {
	  	    @Override
	  	    public void mouseClicked(MouseEvent evt) {
	  	        if (evt.getClickCount() == 2) {
	  	        	omodel.removeRow(orderTbl.rowAtPoint(evt.getPoint()));				  	  
	  	         }		  	      
	  	    }
	  	});
}

protected void updateStockList() {
	
	CallableStatement pm = null;

	try {
				
	    Connection conn = connecting.CreateConnection(conDeets);	        	   	
	
	    pm = conn.prepareCall(upStkList);
		
	    pm.setString(1, invoiceNum);
	    pm.setString(2, getFireCode());
	    pm.setString(3,	getStockList());
	    pm.setInt(4, omodel.getRowCount());
	    
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
protected void updateStockItem() {
	
	CallableStatement pm = null;

	try {
				
	    Connection conn = connecting.CreateConnection(conDeets);	        	   	
	
	    pm = conn.prepareCall(upStkItem);
		
	    pm.setString(1, invoiceNum);
	    pm.setString(2, qt);
	    pm.setString(3,	code);
	    pm.setString(4,	dsc);
	    
	    
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

protected void saveStockLines() {
	   if(rowSelected)
	   {
		   if(getFireCode().equals("") || getFireCode().equals(null) ){
			   ip.showMessage("Please Indicate Fire before Saving!");
		   }else{
		   int rows = 0;
		   int rowNum = 0;
		   stkItems = "";					   
		   qt = ""; 
		   code = "";
		   dsc = ""; 
//		   JOptionPane.showMessageDialog(null, "FIRE = " + fireTxtBx.getText());
		   
		   rows = model2.getRowCount();
		   for (int i = 0 ; i< rows ; i++){
			   qt = model2.getValueAt(i, 0).toString(); 
			   dsc = model2.getValueAt(i, 2).toString(); 
			   stkItems = stkItems + qt + " x " + dsc + "\n" ;
		   }
//		   JOptionPane.showMessageDialog(null, stkItems);
		   updateStockList();
		   
		   qt = ""; 
		   code = "";
		   dsc = "";
		   rows = omodel.getRowCount();
//		   JOptionPane.showMessageDialog(null,  "ROW: " + rows);
		   for (int i = 0 ; i< rows ; i++){

			   rowNum = (Integer.parseInt(omodel.getValueAt(i, 0).toString())-1);
//			   JOptionPane.showMessageDialog(null,  "ROW num: " + rowNum);
			   qt = model2.getValueAt(rowNum, 0).toString(); 
			   code = model2.getValueAt(rowNum, 1).toString(); 
			   dsc = model2.getValueAt(rowNum, 2).toString();			   
//			   JOptionPane.showMessageDialog(null,  "[" + qt + "] [" + code + "] [" + dsc + "]");
			   updateStockItem();
		   }	
		   resetTable();
		   }
	   }	
}

protected void recordFire() {
	if(stockTbl.getSelectedRow()!=-1){
		fireTxtBx.setText(stockTbl.getValueAt(stockTbl.getSelectedRow(), 1).toString());
	}
}

protected void orderStock(int row) {
	int lineNum = row + 1;//getValueAtrow(row, 0).toString();
	String desc = stockTbl.getValueAt(row, 2).toString();
	int dLength = desc.length();
	int end = 45;
	if (dLength < 45){
		end = dLength;
	}
	
	String[] rowData = new String [] {""+lineNum,  desc.substring(0, end)};
	omodel.addRow(rowData);
}

/*
 * Reads selected file, strips away unwanted info
 */
protected void readInvoice() {

	   String src = folder + invPfx + invoiceNum + ".pdf";
	      //Loading an existing document
	      File file = new File(src);
	      if (file.exists()){
	          		      
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
   
		//Find split point on invoice
	      String patternString = "=========================================================="
	      		+ "========================================================================";
	        Pattern pattern = Pattern.compile(patternString);	        
	        String[] split = pattern.split(text);	        
	        String inv = split[3];
	        
	        //	Initialise variables to search each line for text
	        String foundPermit = "";
	        String stockCode = "";
	        String stockDesc = "";
	        String stockQty = "";
	        int chars = inv.length();				//total number of characters
	        int maxRows = 25;						// 25 rows
	//        String[] stkLine = new String[maxRows];
	        String newLine = "";
	        int i=0;
	        int lineLength = 130;
	        int start = 0;
	        int end = lineLength + start;
	        //For each line of text
	        for (i=0;i<maxRows;i++){        	
	        	newLine = inv.substring(start, end);		//split off 130 chars as a line
	        	foundPermit = newLine.substring(2,6);
	        	if (i == 0){
	        		
	        	}
	        	if (foundPermit.equals("INST") || foundPermit.equals("PERM")){	
	        		//Stop if either term is found as the first 4 characters (stock code)
	        	}
	        	else {
	        		// create a row for table
			    stockCode = newLine.substring(2,20);
			    stockDesc = newLine.substring(23,82);
			    stockQty = newLine.substring(96,99);			    
			    model2.addRow(new Object[]{stockQty, stockCode, stockDesc});
			    
			    	//	next newLine starting points
	        	start = start + lineLength;
	        	end = end + lineLength;
	        	}	
	        }	
	      }
}

/*
 * Checks if this install (and sale), have files in the file system
 * updates Boolean values invExists, siteExists, photoExists, 
 */
	protected void checkForFiles() {
	//Check for stored Invoice
	inv = new File(folder+invPfx+invoiceNum+".pdf");//Uses InstallID/Invoice number
	if (inv.exists()){
		viewInvBtn.setVisible(true);
		invExists = true;
	}else{
		viewInvBtn.setVisible(false);
		invExists = false;
	}	
	//Check for stored SiteCheck Forms	
	site = new File(folder+sitePfx+saleID+".pdf");//Uses SaleID number
	if (site.exists()){
		viewSiteBtn.setVisible(true);
		siteExists = true;
	}else{
		viewSiteBtn.setVisible(false);
		siteExists = false;
	}
	//Check for stored Photo(s)	
	//Create array of photos
	File f = new File(folder);					
		photosArr = f.listFiles(new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.startsWith(photoPfx+saleID+"_");	//Uses SaleID number
		}
	});

	if (photosArr.length>0){
		viewPhotoBtn.setVisible(true);
		photoExists = true;
	}else{
		viewPhotoBtn.setVisible(false);
		photoExists = false;
	}
	
}

	private void displayClientDetails(String parameter) {
	
	rs = ip.getDetails(getSaleID, invoiceNum);
	
    	 try {
			while(rs.next()){
						    					
				 saleID 		= rs.getString("SID");						 						

			}
		} catch (SQLException e) {
			e.printStackTrace();
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
	invoiceNum = "";
	detailsTxtArea.setText("");
	clearStock();
	
}	

public void clearStock(){
    viewInvBtn.setVisible(false);
    viewSiteBtn.setVisible(false);
  	viewPhotoBtn.setVisible(false);
	fireTxtBx.setText("");
	model2.setRowCount(0);
	omodel.setRowCount(0);
}
public JTable getPermitsTbl(){
	return permitsTbl;
}
protected String getFireCode(){
	return fireTxtBx.getText();
}
protected String getStockList(){
	return stkItems;
}
}