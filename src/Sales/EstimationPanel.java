package Sales; 

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.Font;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.Choice;
import java.awt.List;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.util.Date;
import java.util.Calendar;

class EstimationPanel extends JPanel {
	private JTextField txtBxFire;
	private JTextField txtBxPrice;
	private JTextField txtBxComment;
	

	public EstimationPanel() {
		setLayout(null);
		
		JTable table = new JTable(new DefaultTableModel(null, new Object[]{"Name", "Site Address", "Phone number"}));
		table.setShowGrid(false);
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		//I don't know how to display the column names 
		model.addRow(new Object[]{"Name", "Site Address", "Phone number"});
		//Sample data
		model.addRow(new Object[]{"Ben Smith", "123 Sesame Street", "0225698531"});
		
		//Make table scrollable
		
		table.setBounds(37, 26, 377, 458);
		add(table);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(497, 36, 104, 14);
		add(lblName);
		
		JLabel lblSAddr = new JLabel("Site Address:");
		lblSAddr.setBounds(497, 68, 104, 14);
		add(lblSAddr);
		
		JLabel lblSSuburb = new JLabel("Suburb:");
		lblSSuburb.setBounds(497, 93, 104, 14);
		add(lblSSuburb);
		
		JLabel lblPostalAddress = new JLabel("Postal Address:");
		lblPostalAddress.setBounds(497, 118, 104, 14);
		add(lblPostalAddress);
		
		JLabel lblPhoneNumber = new JLabel("Phone Number:");
		lblPhoneNumber.setBounds(497, 168, 104, 14);
		add(lblPhoneNumber);
		
		JLabel lblPAddress = new JLabel("Suburb:");
		lblPAddress.setBounds(497, 143, 104, 14);
		add(lblPAddress);
		
		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setBounds(497, 193, 104, 14);
		add(lblEmail);
		
		JTextArea txtAreaCustInfo = new JTextArea();
		txtAreaCustInfo.setEditable(false);
		txtAreaCustInfo.setBounds(631, 31, 428, 195);
		add(txtAreaCustInfo);
		
		JLabel siteAddrLbl = new JLabel("Fire Place");
	  	siteAddrLbl.setBounds(434, 218, 201, 20);
	  	siteAddrLbl.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
	  	add(siteAddrLbl);
		
		txtBxFire = new JTextField();
		txtBxFire.setBounds(631, 249, 218, 20);
		add(txtBxFire);
		txtBxFire.setColumns(10);
		
		txtBxPrice = new JTextField();
		txtBxPrice.setColumns(10);
		txtBxPrice.setBounds(930, 249, 129, 20);
		add(txtBxPrice);
		
		JLabel lblPrice = new JLabel("Price:");
		lblPrice.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblPrice.setBounds(880, 252, 46, 14);
		add(lblPrice);
		
		JComboBox comBxInstType = new JComboBox();
		comBxInstType.setBounds(631, 284, 428, 20);
		add(comBxInstType);
		
		JLabel lblInstallType = new JLabel("Install Type:");
		lblInstallType.setBounds(494, 281, 104, 14);
		add(lblInstallType);
		
		JLabel lblSiteCheck = new JLabel("Site Check:");
		lblSiteCheck.setBounds(497, 315, 104, 14);
		add(lblSiteCheck);
		
		JCheckBox chckbxToBook = new JCheckBox("To Book:");
		chckbxToBook.setBounds(631, 311, 74, 23);
		add(chckbxToBook);
		
		Choice drpBxSChkDoneBy = new Choice();
		drpBxSChkDoneBy.setBounds(631, 340, 218, 20);
		add(drpBxSChkDoneBy);
		
		JLabel lblSChkDoneBy = new JLabel("Site Check Done By:");
		lblSChkDoneBy.setBounds(497, 346, 128, 14);
		add(lblSChkDoneBy);
		
		JSpinner spnTimeDate = new JSpinner();
		spnTimeDate.setModel(new SpinnerDateModel(new Date(1505908800000L), null, null, Calendar.DAY_OF_YEAR));
		spnTimeDate.setBounds(724, 315, 335, 20);
		add(spnTimeDate);
		
		JCheckBox chckbxSChkComp = new JCheckBox("Site Check Completed");
		chckbxSChkComp.setBounds(880, 340, 171, 23);
		add(chckbxSChkComp);
		
		txtBxComment = new JTextField();
		txtBxComment.setBounds(631, 370, 428, 62);
		add(txtBxComment);
		txtBxComment.setColumns(10);
		
		JLabel lblComment = new JLabel("Comment:");
		lblComment.setBounds(497, 370, 124, 14);
		add(lblComment);
		
		JLabel lblSalesPrsn = new JLabel("Sales Person:");
		lblSalesPrsn.setBounds(497, 449, 104, 14);
		add(lblSalesPrsn);
		
		JComboBox comBxSlsPerson = new JComboBox();
		comBxSlsPerson.setBounds(631, 443, 218, 20);
		add(comBxSlsPerson);
		
		JLabel lblFire = new JLabel("Fire:");
		lblFire.setBounds(494, 252, 104, 14);
		add(lblFire);
		
		JButton btnSendEmail = new JButton("Send Email");
		btnSendEmail.setBounds(493, 500, 148, 23);
		add(btnSendEmail);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(690, 500, 148, 23);
		add(btnCancel);
		
		JButton btnSave = new JButton("Save Details");
		btnSave.setBounds(911, 500, 148, 23);
		add(btnSave);
		
		
		/*JComboBox jcb = new JComboBox();
		jcb.addItem("Vanilla");
		jcb.addItem("Chocolate");
		jcb.addItem("Strawberry");
		add(jcb);*/
		
	}
}
