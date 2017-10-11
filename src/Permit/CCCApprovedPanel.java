package Permit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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


class CCCApprovedPanel extends JPanel {

	private int [] columnWidth = {30, 100, 120, 80, 40, 40, 40, 40, 40}; 
 
	private String[] colNames = {"Invoice", "Customer", "Address", "Consent"};
	private int [] colWidth = {30, 100, 120, 80};
	
	private String upCCCApp = "{Call AWS_WCH_DB.dbo.[p_PermitUpdateCCCApprove] (?,?)}";
	private Color LtGray = Color.decode("#eeeeee");
	private Boolean rowSelected = false;
	private String param = "";  
	private ResultSet rs;
	
	private CreateConnection connecting;
	
	private JTable permitsTbl;
	private JTableHeader header;
	private TableColumnModel columnModel;
	private DefaultTableModel model1;	
	
	private JTable cccTbl;
	private JTableHeader hd;
	private TableColumnModel cm;
	private DefaultTableModel model2;
	
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JPanel cccPanel;
	
	private JTextArea detailsTxtArea;

	private JLabel cccLbl;
	private JSpinner CCCDate;
	
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn;
	private JButton addCCCBtn; 
	private JButton remCCCBtn;
	
	private CreateConnection conn;
	
	private Boolean lockForm;
	private ConnDetails conDeets;
	private PermitPane pp;

	
	public CCCApprovedPanel(Boolean lockForm, ConnDetails conDetts, PermitPane ppn) {

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
	        tablePanel.setBounds(20, 20, 1025, 260);     
	        tablePanel.setLayout(new BorderLayout());
	        
	        infoPanel = new JPanel();
	        infoPanel.setBounds(0, 280, 1100, 300);  
	        infoPanel.setLayout(null);
	        
	        cccPanel = new JPanel();
	        cccPanel.setBounds(290, 20, 580, 100);  
	        cccPanel.setLayout(new BorderLayout());
	        
	        detailsTxtArea = new JTextArea("");
	        detailsTxtArea.setBounds(20, 20, 250, 260);
	        detailsTxtArea.setBorder(BorderFactory.createEtchedBorder());
	        detailsTxtArea.setBackground(LtGray);
	        detailsTxtArea.setLineWrap(true);
	        detailsTxtArea.setEditable(false);
	        infoPanel.add(detailsTxtArea);

		    model2 = new DefaultTableModel(colNames,0);
	        cccTbl = new JTable(model2);
	        cccTbl.setAutoCreateRowSorter(true);
	        
	        JScrollPane sp = new JScrollPane(cccTbl);
		  
	        hd= cccTbl.getTableHeader();
	        
	        cm = hd.getColumnModel();
		  	spaceHeader(cm, colWidth);
	        add(hd);
	        
		    cccLbl = new JLabel("Date for CCC:");
		    cccLbl.setBounds(800, 170, 95, 20);
		    infoPanel.add(cccLbl);

		    SimpleDateFormat psModel = new SimpleDateFormat("dd.MMM.yyyy");
		    CCCDate = new JSpinner(new SpinnerDateModel());
			CCCDate.setEditor(new JSpinner.DateEditor(CCCDate, psModel.toPattern()));
			CCCDate.setBounds(895, 170, 150, 20);
			infoPanel.add(CCCDate);  
	        
			addCCCBtn = new JButton("Add Consent");
			addCCCBtn.setBounds(895, 20, 150, 25);
	        infoPanel.add(addCCCBtn);
	        
	        remCCCBtn = new JButton("Remove Consent");
	        remCCCBtn.setBounds(895, 50, 150, 25);
	        infoPanel.add(remCCCBtn);
			
	        cancelPermitReqBtn = new JButton("Cancel");
	        cancelPermitReqBtn.setBounds(720, 260, 150, 25);
	        infoPanel.add(cancelPermitReqBtn);
	        
	        savePermitReqBtn = new JButton("Save Permit Details");
	        savePermitReqBtn.setBounds(895, 260, 150, 25);
	        infoPanel.add(savePermitReqBtn);

	        this.setLayout(null);
	        this.add(tablePanel); 
	        infoPanel.add(cccPanel);
	        this.add(infoPanel);
	        
		  	cccPanel.add(sp, BorderLayout.CENTER);
		  	cccPanel.add(cccTbl.getTableHeader(), BorderLayout.NORTH); 
		  	
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
					   if (rowSelected){
					   getCCCApproved();
					   pp.showMessage("Updating Permit...");
					   }else {
						   pp.showMessage("No Customer Selected");
					   }
					}					
				}
			});
			addCCCBtn.addActionListener( new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) {
				   { 
					   if(rowSelected){
						   moveRow(permitsTbl.getSelectedRow());
					   }else {
						   pp.showMessage("No Customer Selected");
					   }
					}					
				}
			});
			 
			remCCCBtn.addActionListener( new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) {
				   { 
					   int rw = cccTbl.getSelectedRow();
					   if (rw != -1){
						   model2.removeRow(rw);
					   }else{
						   pp.showMessage("No Row Selected to Remove");
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

		  	permitsTbl.addMouseListener(new MouseAdapter() {
		  	    @Override
		  	    public void mouseClicked(MouseEvent evt) {
		  	        if (evt.getClickCount() == 2) {
			  	        moveRow(permitsTbl.rowAtPoint(evt.getPoint()));				  	  
		  	         }		  	      
		  	    }
		  	});
		  	cccTbl.addMouseListener(new MouseAdapter() {
		  	    @Override
		  	    public void mouseClicked(MouseEvent evt) {
		  	        if (evt.getClickCount() == 2) {
		  	        	model2.removeRow(cccTbl.rowAtPoint(evt.getPoint()));			  	  
		  	         }		  	      
		  	    }
		  	});		  	
	  }

	
	protected void updateCCCApproved(String invoice) {
		
		CallableStatement pm = null;

		try {
				
			String update = upCCCApp;	
		    Connection conn = connecting.CreateConnection(conDeets);	        	   	
		
		    pm = conn.prepareCall(update);
			
		    pm.setString(1, invoice);		    
		    pm.setString(2, getCCCDate());
			
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
	
	protected void getCCCApproved() {
		int upD = model2.getRowCount();
		
		if (upD > 0){
			for (int i = 0 ; i< upD ; i++){
				String st = permitsTbl.getValueAt( i, 0).toString();
				
				updateCCCApproved(st);		
			}		
			pp.showMessage("Updating Consent ");
			resetTable();
		}
	}

	private void moveRow(int row){
		
		String[] rowData = new String[]{permitsTbl.getValueAt(row, 0).toString(),
				permitsTbl.getValueAt(row, 1).toString(),
				permitsTbl.getValueAt(row, 2).toString() + ", " + 
				permitsTbl.getValueAt(row, 3).toString(),
				permitsTbl.getValueAt(row, 6).toString()};
		
		model2.addRow(rowData);
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
		
		ResultSet rs = pp.getResults(3);
	  	permitsTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
	  	spaceHeader(columnModel, columnWidth);
	  	
		rowSelected=false;
		param = "";
		detailsTxtArea.setText("");
		int cccRows = model2.getRowCount();
		for (int i = 0;i<cccRows;i++){
			model2.removeRow(0);
		}
}		
    
		private void displayClientDetails(String parameter) {
			 detailsTxtArea.setText(pp.DisplayClientDetails(param));
		}

	    
	    public JTable getPermitsTbl(){
	    	return permitsTbl;
	    }
	      
	    public String getCCCDate(){  	
	    	Date dte = (Date) CCCDate.getValue(); 
	       	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	    	String dt = sdf1.format(dte);
	    	return dt;
	    } 	    
    
}
