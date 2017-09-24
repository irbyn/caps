package Sales;

import java.awt.Choice;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;

class SiteCheckPanel extends JPanel {
	private JTable table;

	public SiteCheckPanel() {
		
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
		lblInstType.setBounds(31, 497, 104, 14);
		add(lblInstType);
		
		JLabel lblEstTotal = new JLabel("Estimate Total:");
		lblEstTotal.setBounds(31, 522, 104, 14);
		add(lblEstTotal);
		
		JTextArea txtAreaCustInfo = new JTextArea();
		txtAreaCustInfo.setEditable(false);
		txtAreaCustInfo.setBounds(165, 299, 382, 237);
		add(txtAreaCustInfo);
		
		
		
		JLabel lblSChkBooking = new JLabel("Site Check Booking:");
		lblSChkBooking.setBounds(601, 361, 128, 14);
		add(lblSChkBooking);
		
		JSpinner spnTimeDate = new JSpinner();
		spnTimeDate.setModel(new SpinnerDateModel(new Date(1505908800000L), null, null, Calendar.DAY_OF_YEAR));
		spnTimeDate.setBounds(757, 358, 284, 20);
		add(spnTimeDate);
		
		Choice drpBxSChkDoneBy = new Choice();
		drpBxSChkDoneBy.setBounds(757, 411, 284, 20);
		add(drpBxSChkDoneBy);
		
		JLabel lblSiteCheckBy = new JLabel("Site Check By:");
		lblSiteCheckBy.setBounds(601, 411, 128, 14);
		add(lblSiteCheckBy);
		
		JCheckBox chckbxSChkComp = new JCheckBox("Site Check Completed");
		chckbxSChkComp.setBounds(757, 452, 180, 23);
		add(chckbxSChkComp);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(601, 502, 148, 23);
		add(btnCancel);
		
		JButton btnSave = new JButton("Save Details");
		btnSave.setBounds(893, 502, 148, 23);
		add(btnSave);
		
		
		
	}
}
