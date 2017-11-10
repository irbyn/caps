package Permit;

/*
 * GUI PANEL:	PERMITS - Producer Statement
 * Allows User to Print Producer Statement documents for Council after install
 */

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
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
import documents.ProducerStatement;
import net.proteanit.sql.DbUtils;

class ProdStatementPanel extends JPanel {

	private int [] columnWidth = {20, 80, 100, 80, 30, 30, 40, 40, 60, 30, 30};
	private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsCCC] ";
	private String upPS3 = "{call AWS_WCH_DB.dbo.p_PermitUpdatePS3 (?,?)}";
	private Color LtGray = Color.decode("#eeeeee");
	private String invoiceNum = "";  
	private ResultSet rs;
	private Boolean rowSelected = false;
	private CreateConnection connecting;

	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable permitsTbl;
	private DefaultTableModel model;
	private JScrollPane scrollPane;

	private JTextArea detailsTxtArea;	
	private JTextPane warningLbl;
	private JLabel pSLbl;
	private JSpinner pSDate;

	private JButton prntConsentBtn; 
	private JButton cancelPermitReqBtn; 

	private Boolean lockForm;
	private ConnDetails conDeets;
	private PermitPane pp;

	private ProducerStatement ps;

	public ProdStatementPanel(Boolean lockForm, ConnDetails conDetts, PermitPane ppn) {

		this.lockForm = lockForm;
		this.conDeets = conDetts;
		this.pp = ppn;
		connecting = new CreateConnection();
		ps = new ProducerStatement();

		model = new DefaultTableModel();  
		model.setRowCount(0);
		permitsTbl = new JTable(model);
		permitsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		permitsTbl.setAutoCreateRowSorter(true);
		scrollPane = new JScrollPane(permitsTbl);
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
		detailsTxtArea.setBorder(BorderFactory.createEtchedBorder());
		detailsTxtArea.setBackground(LtGray);
		detailsTxtArea.setLineWrap(true);
		detailsTxtArea.setEditable(false);
		infoPanel.add(detailsTxtArea);

		warningLbl = new JTextPane(); 
		warningLbl.setEditable(false);
		warningLbl.setText("");
		warningLbl.setVisible(false);
		warningLbl.setForeground(Color.RED);
		warningLbl.setBounds(400, 20, 400, 80);
		infoPanel.add(warningLbl);		    

		pSLbl = new JLabel("Date for PS4:");
		pSLbl.setBounds(800, 170, 95, 20);
		infoPanel.add(pSLbl);

		SimpleDateFormat psModel = new SimpleDateFormat("dd.MMM.yyyy");
		pSDate = new JSpinner(new SpinnerDateModel());
		pSDate.setEditor(new JSpinner.DateEditor(pSDate, psModel.toPattern()));
		pSDate.setBounds(895, 170, 150, 20);
		infoPanel.add(pSDate);        

		prntConsentBtn = new JButton("Print Consent");
		prntConsentBtn.setBounds(895, 260, 150, 25);
		infoPanel.add(prntConsentBtn);

		cancelPermitReqBtn = new JButton("Cancel");
		cancelPermitReqBtn.setBounds(720, 260, 150, 25);
		infoPanel.add(cancelPermitReqBtn);

		this.setLayout(null);
		this.add(tablePanel); 
		this.add(infoPanel);

		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(permitsTbl.getTableHeader(), BorderLayout.NORTH); 

		permitsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					rowSelected=true;
					try{
						invoiceNum = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 0).toString();
						checkInstallDate(invoiceNum);
						detailsTxtArea.setText(pp.DisplayClientDetails(invoiceNum));

					} catch (IndexOutOfBoundsException e){
						//Ignoring IndexOutOfBoundsExceptions!
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

		prntConsentBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Check a Customer has been selected
				if (rowSelected){

					int input = JOptionPane.showConfirmDialog(null,"Mark PS3 as Printed and Sent to Council?\n "
							+ "This Customer will no longer display on this page!",  "Mark Consent Sent?", JOptionPane.YES_NO_OPTION);
					if (input == 0){
						ps.fillPS3(invoiceNum, getPSDate(), conDeets);
						updatePS3();
						resetTable();	        		
						pp.showMessage("Updating Permit");
					} else{
						ps.fillPS3(invoiceNum, getPSDate(), conDeets);
					}
				}else {	//	No Customer selected
					pp.showMessage("No details to Print");			        				      
				}				
			}
		});
	}


	protected void updatePS3() {

		CallableStatement pm = null;

		try {
			String update = upPS3;	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	
			pm = conn.prepareCall(update);
			pm.setString(1, invoiceNum);
			pm.setString(2, getPSDate());
			pm.executeUpdate();
			conn.close();
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

	public void spaceHeader() {
		int i;
		TableColumn tabCol = columnModel.getColumn(0);
		for (i=0; i<columnWidth.length; i++){
			tabCol = columnModel.getColumn(i);
			tabCol.setPreferredWidth(columnWidth[i]);
		}
		header.repaint();
	}      

	protected void resetTable() {

		ResultSet rs = pp.getTable(2);
		permitsTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
		spaceHeader();
		rowSelected=false;
		invoiceNum = "";
		detailsTxtArea.setText("");
		warningLbl.setVisible(false);
	}		    

	private void checkInstallDate(String parameter) {

		try
		{
			Connection conn = connecting.CreateConnection(conDeets);
			PreparedStatement st2 =conn.prepareStatement(result2 + parameter);
			ResultSet rs2 = st2.executeQuery();
			//Retrieve by column name
			while(rs2.next()){

				Date consentDate		= rs2.getDate("ConsentDate");
				Date installDate		= rs2.getDate("InstallDate");

				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
				String cnst = sdf.format(consentDate);
				String inst = sdf.format(installDate);
				pSDate.setValue(installDate);

				if (installDate.before(consentDate)){
					warningLbl.setVisible(true);
					warningLbl.setText("\tWarning, Install Date is Before Consent Date\n\n"
							+ "\tInstalled On:\t\t" + inst + "\n"
							+ "\tConsent Issued:\t" + cnst);	
				}
				else {
					warningLbl.setVisible(false);
				}					 
			}
			conn.close();	
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}	  	

	}	

	public String getPSDate(){  	
		Date dte = (Date) pSDate.getValue(); 
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
		String dt = sdf1.format(dte);
		return dt;
	} 

	public JTable getPermitsTbl(){
		return permitsTbl;
	}
	public JPanel getInfoPanel(){
		return infoPanel;
	}
}
