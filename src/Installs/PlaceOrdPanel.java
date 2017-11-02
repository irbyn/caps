package Installs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
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
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Permit.PermitPane;
import net.proteanit.sql.DbUtils;

class PlaceOrdPanel extends JPanel {
	
	private int [] columnWidth = {20, 100, 120, 20, 80, 400};	
	private String[] colNames = {"Invoice","Customer Name","Street Address", "Qty", "Stock Code", "Description"};
	private String upPONum = "{Call AWS_WCH_DB.dbo.[i_updateStockPONumber] (?,?,?)}";
	private String[][] poItems;
	
	private String param = "";  
	private ResultSet rs;
	
	private CreateConnection connecting;
	
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JPanel poPanel;
	
	private JTable installTbl;	
	private JTableHeader header;
	private TableColumnModel columnModel;
	private DefaultTableModel model1;
	
	private JTable poTbl;
	private JTableHeader hd2;
	private TableColumnModel cmod2;
	private DefaultTableModel tmod2;
	
//	private JTextArea detailsTxtArea;
	
	private JLabel poLbl;
	private JTextField poTxtBx;
	
	private JButton cancelPONumBtn; 
	private JButton savePONumBtn; 
	
	private String inv; 
	private String stk; 
	private String po; 

	
	private CreateConnection conn;
	
	private Boolean lockForm;
	private ConnDetails conDeets;
	private InstallsPane ip;


public PlaceOrdPanel(Boolean lockForm, ConnDetails conDetts, InstallsPane ipn) {

	  this.lockForm = lockForm;
	  this.conDeets = conDetts;
	  this.ip = ipn;

	  connecting = new CreateConnection();
		  	
	  model1 = new DefaultTableModel();  
	  model1.setRowCount(0);
	  installTbl = new JTable(model1);
	  installTbl.setRowSelectionAllowed(false);
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
      
      poPanel = new JPanel();
      poPanel.setBounds(20, 20, 1025, 170);		  
      poPanel.setLayout(new BorderLayout());
           
	  tmod2 = new DefaultTableModel(colNames,0);
      poTbl = new JTable(tmod2);
      poTbl.setAutoCreateRowSorter(true);        
      JScrollPane sp = new JScrollPane(poTbl);	  
      hd2= poTbl.getTableHeader();        
      cmod2 = hd2.getColumnModel();
	  spaceHeader(cmod2, columnWidth);
      add(hd2);
       
      poLbl = new JLabel("PO Number:");
      poLbl.setBounds(825, 200, 70, 20);
      infoPanel.add(poLbl);
      poTxtBx = new JTextField(10);
      poTxtBx.setBounds(895, 200, 150, 20);
      infoPanel.add(poTxtBx);
     
      cancelPONumBtn = new JButton("Cancel");
      cancelPONumBtn.setBounds(720, 260, 150, 25);
      infoPanel.add(cancelPONumBtn);
      
      savePONumBtn = new JButton("Save Order Number");
      savePONumBtn.setBounds(895, 260, 150, 25);
      infoPanel.add(savePONumBtn);

      this.setLayout(null);
      this.add(tablePanel); 
      infoPanel.add(poPanel);
      this.add(infoPanel);
      
	  poPanel.add(sp, BorderLayout.CENTER);
	  poPanel.add(poTbl.getTableHeader(), BorderLayout.NORTH); 
      
	  tablePanel.add(scrollPane, BorderLayout.CENTER);
	  tablePanel.add(installTbl.getTableHeader(), BorderLayout.NORTH);        

	  cancelPONumBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   resetTable();
				}					
			}
		});
	  savePONumBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   if(tmod2.getRowCount()!=0){ 
					   if (poTxtBx.getText().length() < 8){
						   saveStock();
					   } else {
						   ip.showMessage("Purchase Order Number must be less than 8 digits");
					   }
				   } else {
					   ip.showMessage("Select a Stock Item to Order");
				   }
			   }
			}
		});
		installTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){

					try{
					param = installTbl.getValueAt(installTbl.getSelectedRow(), 0).toString();			
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


protected void saveStock() {
	   if (poTxtBx.getText().isEmpty()){
		   ip.showMessage("Please Enter a PO Number");
	   }else{
		   int rows = tmod2.getRowCount();
		   int i;
		   inv = ""; 
		   stk = "";
		   po = "";

		   for (i = 0; i < rows; i++ ){
			   inv = tmod2.getValueAt(i, 0).toString(); 
			   stk = tmod2.getValueAt(i, 4).toString();
			   po = poTxtBx.getText();
			   updateStockItem();
		   }		
		   
		   resetTable();
	   }
	   
}


protected void updateStockItem() {
	CallableStatement pm = null;

	try {
				
	    Connection conn = connecting.CreateConnection(conDeets);	        	   	
	
	    pm = conn.prepareCall(upPONum);
		
	    pm.setInt(1, Integer.parseInt(inv));
	    pm.setString(2, stk);
	    pm.setString(3,	po);

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


private void moveRow(int row){
String[] rowData = new String[]{installTbl.getValueAt(row, 0).toString(),
								installTbl.getValueAt(row, 1).toString(),
								installTbl.getValueAt(row, 2).toString(),
								installTbl.getValueAt(row, 3).toString(),
								installTbl.getValueAt(row, 4).toString(),
								installTbl.getValueAt(row, 5).toString()};
		tmod2.addRow(rowData);
}

private void returnRow(int row){
	DefaultTableModel mod = (DefaultTableModel) installTbl.getModel();
String[] rowData = new String[]{poTbl.getValueAt(row, 0).toString(),
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
	
	ResultSet rs = ip.getResults(2,  conDeets);
	installTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
  	spaceHeader(columnModel, columnWidth);
	param = "";
	poTxtBx.setText("");
	tmod2.setRowCount(0);
	

}		
public JTable getInstTbl(){
	return installTbl;
}
public JPanel getInfoPanel(){
	return infoPanel;
}
}