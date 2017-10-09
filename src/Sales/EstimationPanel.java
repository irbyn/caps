package Sales; 

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import net.proteanit.sql.DbUtils;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.Date;
import java.util.Calendar;

class EstimationPanel extends JPanel {
	private JTextField txtBxFire;
	private JTextField txtBxPrice;
	private JTextField txtBxComment;	

	private String result2 = "EXEC AWS_WCH_DB.dbo.[s_SalesEstimation] ";
	private String result3 = "EXEC AWS_WCH_DB.dbo.[s_SalesEstimation] ";
	private String param = "";  
	private ResultSet rs;

	private Boolean rowSelected = false;
	
	private CreateConnection connecting;

	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable salesTbl;
	private DefaultTableModel model1;
	private JTextArea txtAreaCustInfo;

	private SalesPane sp;
	private ConnDetails conDeets;

	public EstimationPanel(ConnDetails conDeets, SalesPane sp) {

		conDeets = conDeets;
		rowSelected = false;

		connecting = new CreateConnection();

		model1 = new DefaultTableModel();  
		model1.setRowCount(0);
		salesTbl = new JTable(model1);
		salesTbl.setPreferredSize(new Dimension(0, 300));
		salesTbl.setAutoCreateRowSorter(true);

		JScrollPane scrollPane = new JScrollPane(salesTbl);

		header= salesTbl.getTableHeader();
		columnModel = header.getColumnModel();
		add(header); 

		//Panel for the table
		tablePanel = new JPanel();
				tablePanel.setBounds(20, 20, 1025, 260);  //setPreferredSize(new Dimension(0, 300));      
				tablePanel.setLayout(new BorderLayout());

				//Content panel
				infoPanel = new JPanel();
				infoPanel.setBounds(0, 280, 1100, 289);  //setPreferredSize(new Dimension(0, 300));
				infoPanel.setLayout(null);

				//setLayout(null);

				JLabel siteAddrLbl = new JLabel("Fire Place");
				siteAddrLbl.setBounds(461, 10, 201, 20);
				infoPanel.add(siteAddrLbl);
				siteAddrLbl.setFont(new java.awt.Font("Arial", Font.BOLD, 20));

				JTextArea txtAreaCustInfo = new JTextArea();
				txtAreaCustInfo.setEditable(false);
				txtAreaCustInfo.setBounds(22, 11, 376, 268);
				infoPanel.add(txtAreaCustInfo);

				txtBxFire = new JTextField();
				txtBxFire.setBounds(588, 30, 218, 20);
				infoPanel.add(txtBxFire);
				txtBxFire.setColumns(10);

				txtBxPrice = new JTextField();
				txtBxPrice.setColumns(10);
				txtBxPrice.setBounds(887, 30, 129, 20);
				infoPanel.add(txtBxPrice);

				JLabel lblPrice = new JLabel("Price:");
				lblPrice.setFont(new Font("Tahoma", Font.PLAIN, 11));
				lblPrice.setBounds(837, 33, 46, 14);
				infoPanel.add(lblPrice);

				JComboBox<String> comBxInstType = new JComboBox<String>();
				comBxInstType.setBounds(588, 65, 428, 20);
				infoPanel.add(comBxInstType);

				JLabel lblInstallType = new JLabel("Install Type:");
				lblInstallType.setBounds(451, 62, 104, 14);
				infoPanel.add(lblInstallType);

				JLabel lblSiteCheck = new JLabel("Site Check:");
				lblSiteCheck.setBounds(454, 96, 104, 14);
				infoPanel.add(lblSiteCheck);

				JCheckBox chckbxToBook = new JCheckBox("To Book:");
				chckbxToBook.setBounds(588, 92, 74, 23);
				infoPanel.add(chckbxToBook);

				JComboBox<String> comBxSChkDoneBy = new JComboBox<String>();
				comBxSChkDoneBy.setBounds(755, 124, 261, 20);
				infoPanel.add(comBxSChkDoneBy);

				/*Choice drpBxSChkDoneBy = new Choice();
		drpBxSChkDoneBy.setBounds(798, 127, 218, 20);
		infoPanel.add(drpBxSChkDoneBy);*/

				JLabel lblSChkDoneBy = new JLabel("Site Check Done By:");
				lblSChkDoneBy.setBounds(454, 127, 128, 14);
				infoPanel.add(lblSChkDoneBy);

				JSpinner spnTimeDate = new JSpinner();
				spnTimeDate.setModel(new SpinnerDateModel(new Date(1505908800000L), null, null, Calendar.DAY_OF_YEAR));
				spnTimeDate.setBounds(681, 96, 335, 20);
				infoPanel.add(spnTimeDate);

				JCheckBox chckbxSChkComp = new JCheckBox("Site Check Completed");
				chckbxSChkComp.setBounds(588, 124, 171, 23);
				infoPanel.add(chckbxSChkComp);

				txtBxComment = new JTextField();
				txtBxComment.setBounds(588, 151, 428, 62);
				infoPanel.add(txtBxComment);
				txtBxComment.setColumns(10);

				JLabel lblComment = new JLabel("Comment:");
				lblComment.setBounds(454, 151, 124, 14);
				infoPanel.add(lblComment);

				JLabel lblSalesPrsn = new JLabel("Sales Person:");
				lblSalesPrsn.setBounds(454, 230, 104, 14);
				infoPanel.add(lblSalesPrsn);

				JComboBox<String> comBxSlsPerson = new JComboBox<String>();
				comBxSlsPerson.setBounds(588, 224, 218, 20);
				infoPanel.add(comBxSlsPerson);

				JLabel lblFire = new JLabel("Fire:");
				lblFire.setBounds(451, 33, 104, 14);
				infoPanel.add(lblFire);

				JButton btnSendEmail = new JButton("Send Email");
				btnSendEmail.setBounds(451, 255, 148, 23);
				infoPanel.add(btnSendEmail);

				JButton btnCancel = new JButton("Cancel");
				btnCancel.setBounds(644, 255, 148, 23);
				infoPanel.add(btnCancel);
				btnCancel.addActionListener(new ActionListener(){
					//Fix the null exception error
					@Override
					public void actionPerformed(ActionEvent arg0){
						resetTable();
					}
				});

				JButton btnSave = new JButton("Save Details");
				btnSave.setBounds(855, 255, 148, 23);
				infoPanel.add(btnSave);

				this.setLayout(null);
				this.add(tablePanel); 
				this.add(infoPanel);

				tablePanel.add(scrollPane, BorderLayout.CENTER);
				tablePanel.add(salesTbl.getTableHeader(), BorderLayout.NORTH);


				rs = sp.getResults(1, conDeets);		
				salesTbl.setModel(DbUtils.resultSetToTableModel(rs));  	
				//spaceHeader();  

				//	  	this.add(infoPanel, BorderLayout.SOUTH);

		  	salesTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					rowSelected=true;
					try{
					param = salesTbl.getValueAt(salesTbl.getSelectedRow(), 0).toString();
					//displayClientDetails(param);
					txtAreaCustInfo.setText(sp.DisplayClientDetails(param));
					} catch (IndexOutOfBoundsException e){
						
					}
				}
			}
	  	});
	}

		
	protected void resetTable() {
		
		ResultSet rs = sp.getResults(3,  conDeets);
	  	salesTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
	  	//spaceHeader(columnModel, columnWidth);
	  	//sentChk.setSelected(false);
	  	
		rowSelected=false;
		param = "";
		txtAreaCustInfo.setText("");

	}	
	
/*	
	protected void resetTable() {
		
		//get the results for the second (estimation) results set
		ResultSet rs = sp.getResults(2,  conDeets);
	  	salesTbl.setModel(DbUtils.resultSetToTableModel(rs));
	  	//Blank out all fields 
	  	
	  	//spaceHeader(columnModel, columnWidth);
	  	//sentChk.setSelected(false);
	  	
		rowSelected=false;
		param = "";
		txtAreaCustInfo.setText("");

}	*/
	
	public JTable getSalesTbl(){
		return salesTbl;
	}
}