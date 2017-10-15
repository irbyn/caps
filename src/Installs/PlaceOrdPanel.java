package Installs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;

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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Permit.PermitPane;
import net.proteanit.sql.DbUtils;

class PlaceOrdPanel extends JPanel {
	
	private int [] columnWidth = {20, 100, 120, 20, 80, 400};	
	private String[] colNames = {"Invoice","Customer","Address", "Qty", "Stock Code", "Description"};
	private int [] colWidth =	{20, 100, 120, 20, 80, 400};//{30, 10, 80, 400};// {0, 20, 100, 120, 20, 80, 400}; 
	private String upCCCClient = "{Call AWS_WCH_DB.dbo.[p_PermitUpdateCCCToClient] (?,?)}";
	
	private String param = "";  
	private ResultSet rs;
	private Color LtGray = Color.decode("#eeeeee");
	
	private Boolean rowSelected = false;
	
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
	
	private JLabel sentLbl;
	private JCheckBox sentChk;
	
	private JLabel sentDateLbl;
	private JSpinner sentDate;
	
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	
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
//      permitsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	  installTbl.setRowSelectionAllowed(false);
	  installTbl.setAutoCreateRowSorter(true);
      
      JScrollPane scrollPane = new JScrollPane(installTbl);
	  
      header= installTbl.getTableHeader();
      columnModel = header.getColumnModel();
//	    permitsTbl.removeColumn(columnModel.getColumn(0));
      add(header); 
              	        
      tablePanel = new JPanel();
      tablePanel.setBounds(20, 20, 1025, 260);  //    
      tablePanel.setLayout(new BorderLayout());
      
      infoPanel = new JPanel();
      infoPanel.setBounds(0, 280, 1100, 300);  //
      infoPanel.setLayout(null);
      
      poPanel = new JPanel();
      poPanel.setBounds(20, 20, 1025, 200);		//	(290, 20, 755, 200);  
      poPanel.setLayout(new BorderLayout());
      
/*      detailsTxtArea = new JTextArea("");
      detailsTxtArea.setBounds(20, 20, 250, 260);
      detailsTxtArea.setBorder(BorderFactory.createEtchedBorder());
      detailsTxtArea.setBackground(LtGray);
      detailsTxtArea.setLineWrap(true);
      detailsTxtArea.setEditable(false);
      infoPanel.add(detailsTxtArea);
*/      
	    tmod2 = new DefaultTableModel(colNames,0);
        poTbl = new JTable(tmod2);
        poTbl.setAutoCreateRowSorter(true);
        
        JScrollPane sp = new JScrollPane(poTbl);
	  
        hd2= poTbl.getTableHeader();
        
        cmod2 = hd2.getColumnModel();
	  	spaceHeader(cmod2, colWidth);
        add(hd2);
        

      
      cancelPermitReqBtn = new JButton("Cancel");
      cancelPermitReqBtn.setBounds(720, 260, 150, 25);
      infoPanel.add(cancelPermitReqBtn);
      
      savePermitReqBtn = new JButton("Save Permit Details");
      savePermitReqBtn.setBounds(895, 260, 150, 25);
      infoPanel.add(savePermitReqBtn);

      this.setLayout(null);
      this.add(tablePanel); 
      infoPanel.add(poPanel);
      this.add(infoPanel);
      
	  	poPanel.add(sp, BorderLayout.CENTER);
	  	poPanel.add(poTbl.getTableHeader(), BorderLayout.NORTH); 
      
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
		//			pp.setFormsLocked();
					try{
					param = installTbl.getValueAt(installTbl.getSelectedRow(), 0).toString();
					
//					detailsTxtArea.setText(ip.DisplayClientDetails(param));
					
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
	  	        	returnRow(actualRow);
	  	        	tmod2.removeRow(clickedRow);	
	  	         }		  	      
	  	    }
	  	});	
	  	resetTable();
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
  	
	rowSelected=false;
	tmod2.setRowCount(0);
	
	param = "";
//	detailsTxtArea.setText("");

}		
public JTable getInstTbl(){
	return installTbl;
}
}