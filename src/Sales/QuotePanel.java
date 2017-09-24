package Sales; 

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JTextField;

class QuotePanel extends JPanel {
	private JTextField txtBxReesCode;
	private JTextField txtBxQuoteNum;

	public QuotePanel() {
		setLayout(null);
		
		JTable table = new JTable(new DefaultTableModel(null, new Object[]{"Name", "Site Address", "Phone number"}));
		table.setShowGrid(false);
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		//I don't know how to display the column names 
		model.addRow(new Object[]{"Name", "Site Address", "Phone number"});
		//Sample data
		model.addRow(new Object[]{"Ben Smith", "123 Sesame Street", "0225698531"});
		
		//Make table scrollable
		
		table.setBounds(37, 26, 1004, 251);
		add(table);
		
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(31, 304, 104, 14);
		add(lblName);
		
		JLabel lblSAddr = new JLabel("Site Address:");
		lblSAddr.setBounds(31, 329, 104, 14);
		add(lblSAddr);
		
		JLabel lblSSuburb = new JLabel("Suburb:");
		lblSSuburb.setBounds(31, 354, 104, 14);
		add(lblSSuburb);
		
		JLabel lblPostalAddress = new JLabel("Postal Address:");
		lblPostalAddress.setBounds(31, 379, 104, 14);
		add(lblPostalAddress);
		
		JLabel lblPhoneNumber = new JLabel("Phone Number:");
		lblPhoneNumber.setBounds(31, 429, 104, 14);
		add(lblPhoneNumber);
		
		JLabel lblPAddress = new JLabel("Suburb:");
		lblPAddress.setBounds(31, 404, 104, 14);
		add(lblPAddress);
		
		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setBounds(31, 454, 104, 14);
		add(lblEmail);
		
		JLabel lblFire = new JLabel("Fire:");
		lblFire.setBounds(31, 479, 46, 14);
		add(lblFire);
		
		JLabel lblInstType = new JLabel("Install Type:");
		lblInstType.setBounds(31, 504, 104, 14);
		add(lblInstType);
		
		JTextArea txtAreaCustInfo = new JTextArea();
		txtAreaCustInfo.setEditable(false);
		txtAreaCustInfo.setBounds(165, 299, 382, 237);
		add(txtAreaCustInfo);
		
		JLabel lblNewLabel = new JLabel("Rees Code:");
		lblNewLabel.setBounds(591, 329, 77, 14);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Quote Number:");
		lblNewLabel_1.setBounds(848, 329, 110, 14);
		add(lblNewLabel_1);
		
		txtBxReesCode = new JTextField();
		txtBxReesCode.setBounds(678, 326, 140, 20);
		add(txtBxReesCode);
		txtBxReesCode.setColumns(10);
		
		txtBxQuoteNum = new JTextField();
		txtBxQuoteNum.setBounds(928, 326, 113, 20);
		add(txtBxQuoteNum);
		txtBxQuoteNum.setColumns(10);
		
		JTextArea txtBxSCheck = new JTextArea();
		txtBxSCheck.setBounds(591, 399, 97, 94);
		add(txtBxSCheck);
		
		JTextArea txtBxQuote = new JTextArea();
		txtBxQuote.setBounds(765, 399, 97, 94);
		add(txtBxQuote);
		
		JTextArea txtBxPhoto = new JTextArea();
		txtBxPhoto.setBounds(944, 399, 97, 94);
		add(txtBxPhoto);
		
		JLabel lblSiteCheck = new JLabel("Site Check");
		lblSiteCheck.setBounds(616, 379, 72, 14);
		add(lblSiteCheck);
		
		JLabel lblQuote = new JLabel("Quote");
		lblQuote.setBounds(795, 379, 46, 14);
		add(lblQuote);
		
		JLabel lblPhoto = new JLabel("Photo");
		lblPhoto.setBounds(964, 379, 46, 14);
		add(lblPhoto);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(601, 502, 148, 23);
		add(btnCancel);
		
		JButton btnSave = new JButton("Save Quote Details");
		btnSave.setBounds(893, 502, 148, 23);
		add(btnSave);
		
		
		
	}
}

