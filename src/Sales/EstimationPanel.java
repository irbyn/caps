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
import Main.GetJobs;
import net.proteanit.sql.DbUtils;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

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

	private String spResults = "EXEC AWS_WCH_DB.dbo.[s_SalesGetSalesPerson] ";
	//private String result3 = "EXEC AWS_WCH_DB.dbo.[s_SalesEstimation] ";
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
	private JLabel siteAddrLbl;
	private JLabel lblPrice;
	private JComboBox<String> comBxInstType;
	private JLabel lblInstallType;
	private JLabel lblSiteCheck;
	private JCheckBox chckBxToBook;
	private JComboBox<String> comBxSChkDoneBy;
	private JLabel lblSChkDoneBy;
	private JSpinner spnTimeDate;
	private JCheckBox chckBxSChkComp;
	private JLabel lblComment;
	private JLabel lblSalesPrsn;
	private JComboBox<String> comBxSlsPerson;
	private JLabel lblFire;
	private JButton btnSendEmail;
	private JButton btnCancel;
	private JButton btnSave;

	private SalesPane sp;
	private ConnDetails conDeets;

	public EstimationPanel(ConnDetails ConDeets, SalesPane sp) {
		this.sp = sp;
		this.conDeets = ConDeets;
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
		tablePanel.setBounds(20, 20, 1025, 260);      
		tablePanel.setLayout(new BorderLayout());

		//Content panel
		infoPanel = new JPanel();
		infoPanel.setBounds(0, 280, 1100, 289);
		infoPanel.setLayout(null);

		siteAddrLbl = new JLabel("Fire Place");
		siteAddrLbl.setBounds(451, 10, 201, 20);
		infoPanel.add(siteAddrLbl);
		siteAddrLbl.setFont(new java.awt.Font("Arial", Font.BOLD, 20));

		txtAreaCustInfo = new JTextArea();
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

		lblPrice = new JLabel("Price:");
		lblPrice.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblPrice.setBounds(837, 33, 46, 14);
		infoPanel.add(lblPrice);

		comBxInstType = new JComboBox<String>();
		comBxInstType.setBackground(Color.WHITE);
		comBxInstType.setBounds(588, 65, 428, 20);
		infoPanel.add(comBxInstType);

		lblInstallType = new JLabel("Install Type:");
		lblInstallType.setBounds(451, 62, 104, 14);
		infoPanel.add(lblInstallType);

		lblSiteCheck = new JLabel("Site Check:");
		lblSiteCheck.setBounds(454, 96, 104, 14);
		infoPanel.add(lblSiteCheck);

		chckBxToBook = new JCheckBox("To Book:");
		chckBxToBook.setBounds(588, 92, 74, 23);
		infoPanel.add(chckBxToBook);

		comBxSChkDoneBy = new JComboBox<String>();
		comBxSChkDoneBy.setBackground(Color.WHITE);
		comBxSChkDoneBy.setBounds(755, 124, 261, 20);
		infoPanel.add(comBxSChkDoneBy);

		lblSChkDoneBy = new JLabel("Site Check Done By:");
		lblSChkDoneBy.setBounds(454, 127, 128, 14);
		infoPanel.add(lblSChkDoneBy);

		SimpleDateFormat dt = new SimpleDateFormat("dd.MMM.yyyy");
		spnTimeDate = new JSpinner(new SpinnerDateModel());
		spnTimeDate.setEditor(new JSpinner.DateEditor(spnTimeDate, dt.toPattern()));
		spnTimeDate.setBounds(681, 96, 335, 20);
		infoPanel.add(spnTimeDate);

		chckBxSChkComp = new JCheckBox("Site Check Completed");
		chckBxSChkComp.setBounds(588, 124, 171, 23);
		infoPanel.add(chckBxSChkComp);

		txtBxComment = new JTextField();
		txtBxComment.setBounds(588, 151, 428, 62);
		infoPanel.add(txtBxComment);
		txtBxComment.setColumns(10);

		lblComment = new JLabel("Comment:");
		lblComment.setBounds(454, 151, 124, 14);
		infoPanel.add(lblComment);

		lblSalesPrsn = new JLabel("Sales Person:");
		lblSalesPrsn.setBounds(454, 230, 104, 14);
		infoPanel.add(lblSalesPrsn);

		comBxSlsPerson = new JComboBox<String>();
		comBxSlsPerson.setBackground(Color.WHITE);
		comBxSlsPerson.setBounds(588, 224, 218, 20);
		infoPanel.add(comBxSlsPerson);

		lblFire = new JLabel("Fire:");
		lblFire.setBounds(451, 33, 104, 14);
		infoPanel.add(lblFire);

		btnSendEmail = new JButton("Send Email");
		btnSendEmail.setBounds(451, 255, 148, 23);
		infoPanel.add(btnSendEmail);

		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(644, 255, 148, 23);
		infoPanel.add(btnCancel);
		btnCancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0){
				//if there is unsaved data
				if (checkTxtBx()){
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to cancel?","Warning",dialogButton);
					if(dialogResult == JOptionPane.YES_OPTION){
						resetTable();
						//reset all the blank fields within the estimation tab
						txtBxFire.setText(null);
						txtBxPrice.setText(null);
						//comBxInstType.setSelectedIndex(0);
						chckBxToBook.setSelected(false);
						comBxSChkDoneBy.setSelectedIndex(0);
						spnTimeDate.setEditor(new JSpinner.DateEditor(spnTimeDate, dt.toPattern()));
						chckBxSChkComp.setSelected(false);
						txtBxComment.setText(null);
						comBxSlsPerson.setSelectedItem(0);
					}
				}				
			}
		});

		btnSave = new JButton("Save Details");
		btnSave.setBounds(855, 255, 148, 23);
		infoPanel.add(btnSave);

		GetJobs job = new GetJobs(conDeets);

		String[] installUr = job.getInstallers();

		/*DefaultComboBoxModel model = new DefaultComboBoxModel( installUr );
		comBxSChkDoneBy.setModel( model );   
		 */		String[] SC = job.getSiteChecker();
		 DefaultComboBoxModel modelSC = new DefaultComboBoxModel( SC );
		 comBxSChkDoneBy.setModel( modelSC );
		 /*		DefaultComboBoxModel modelSC = new DefaultComboBoxModel( SC );
		comBxSlsPerson.setModel( modelSC ); */

		 String[] SELL = job.getSales();

		 DefaultComboBoxModel modelSELL = new DefaultComboBoxModel( SELL );
		 comBxSlsPerson.setModel( modelSELL ); 

		 this.setLayout(null);
		 this.add(tablePanel); 
		 this.add(infoPanel);

		 tablePanel.add(scrollPane, BorderLayout.CENTER);
		 tablePanel.add(salesTbl.getTableHeader(), BorderLayout.NORTH);


		 salesTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			 @Override
			 public void valueChanged(ListSelectionEvent arg0) {
				 if (!arg0.getValueIsAdjusting()){
					 rowSelected=true;
					 try{
						 //Get the customer ID as a paramater to feed into the SQL procedure 
						 param = salesTbl.getValueAt(salesTbl.getSelectedRow(), 1).toString();
						 //displayClientDetails(param);
						 txtAreaCustInfo.setText(sp.DisplayClientDetails(param));
					 } catch (IndexOutOfBoundsException e){

					 }
				 }
			 }
		 });
	}

	protected void resetTable() {
		ResultSet rs = sp.getResults(1,  conDeets);
		salesTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
		//spaceHeader(columnModel, columnWidth);
		//sentChk.setSelected(false);

		rowSelected=false;
		param = "";
		txtAreaCustInfo.setText("");
	}

	public JTable getSalesTbl(){
		return salesTbl;
	}

	public void validateData(){
		Boolean errorChk = false;
		String error = " ";
		if (txtBxFire.getText().equals("")){
			errorChk = true;
			//Cannot be null or more than 15 chars
			error = error + "FIRE: can not be empty\n";
		}else if(txtBxFire.getText().length() > 50){
			errorChk = true;
			error = error + "FIRE: can not be more than 15 letters\n";
		}

		try {
			Integer.parseInt(txtBxPrice.getText());
			if(4 <= txtBxPrice.getText().length() && txtBxPrice.getText().length() >= 5){
				errorChk = true;
				error = error + "PRCIE: must be between 2000 - 99999\n";
			}
		}
		catch (NumberFormatException e) {
			//Display number error message 
			errorChk = true;
			error = error + "PRICE: can only contain numbers\n";
		}

	}

	public Boolean checkTxtBx(){
		Boolean newData = false;
		//add in combx for Install type and the date spinner 
		if (!txtAreaCustInfo.getText().equals("") || !txtBxFire.getText().equals("") || chckBxToBook.isSelected()
				|| !(comBxSChkDoneBy.getSelectedIndex() == 0) || !txtBxComment.getText().equals("") 
				|| !(comBxSlsPerson.getSelectedIndex() == 0 )){
			newData = true;
		}
		return newData;
	}
}