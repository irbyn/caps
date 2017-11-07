package Schedule;

/*
 * GUI PANEL:	SCHEDULE - Site Checks
 * Allows User to view upcoming site checks & print the correct form
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import documents.PrintSiteCheck;
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
	private Boolean rowSelected = false;
	private CreateConnection connecting;

	private JPanel tablePanel;
	private JPanel infoPanel;
	private JPanel poPanel;

	private JTableHeader header;
	private TableColumnModel columnModel;
	private JScrollPane scrollPane;private JTable timeTbl;
	private DefaultTableModel model1;

	private JTable scTbl;
	private JTableHeader hd2;
	private TableColumnModel cmod2;
	private DefaultTableModel tmod2;
	private JScrollPane sPane;

	private JButton printSiteCheckBtn; 
	private JButton cancelGoodsRcvdBtn; 
	private JSpinner reportDate;

	private CreateConnection conn;
	private Boolean lockForm;
	private ConnDetails conDeets;
	private SchedulePane sp;

	public ViewSiteChecksPanel(Boolean lockForm, ConnDetails conDetts, SchedulePane spn) {

		this.lockForm = lockForm;
		this.conDeets = conDetts;
		this.sp = spn;

		model1 = new DefaultTableModel();  
		model1.setRowCount(0);
		timeTbl = new JTable(model1);
		timeTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		timeTbl.setAutoCreateRowSorter(true);
		scrollPane = new JScrollPane(timeTbl);
		header= timeTbl.getTableHeader();
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
		scTbl = new JTable(tmod2);
		scTbl.setAutoCreateRowSorter(true);        
		sPane = new JScrollPane(scTbl);	  
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

		this.setLayout(null);
		this.add(tablePanel); 
		infoPanel.add(poPanel);
		this.add(infoPanel);

		poPanel.add(sPane, BorderLayout.CENTER);
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
		String by;
		if (fromTbl.getValueAt(row, 6)!=null){
			by = fromTbl.getValueAt(row, 6).toString();
		} else {
			by = "";
		}

		String[] rowData = new String[]{	fromTbl.getValueAt(row, 0).toString(),
				fromTbl.getValueAt(row, 1).toString(),
				fromTbl.getValueAt(row, 2).toString(),
				fromTbl.getValueAt(row, 3).toString(),
				date,
				time,
				by	};
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