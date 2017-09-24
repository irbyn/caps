package Sales;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

class FollowUpPanel extends JPanel {
	private JTextField textField;

	  public FollowUpPanel() {

		  setLayout(null);
			
			JTable table = new JTable(new DefaultTableModel(null, new Object[]{"Name", "Site Address", "Phone number"}));
			table.setShowGrid(false);
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			//I don't know how to display the column names 
			model.addRow(new Object[]{"Name", "Site Address", "Phone number"});
			//Sample data
			model.addRow(new Object[]{"Ben Smith", "123 Sesame Street", "0225698531"});
			
			//Make table scrollable
			
			table.setBounds(37, 26, 519, 155);
			add(table);
			

			JLabel lblName = new JLabel("Name:");
			lblName.setBounds(34, 219, 104, 14);
			add(lblName);
			
			JLabel lblSAddr = new JLabel("Site Address:");
			lblSAddr.setBounds(34, 245, 104, 14);
			add(lblSAddr);
			
			JLabel lblSSuburb = new JLabel("Suburb:");
			lblSSuburb.setBounds(34, 273, 104, 14);
			add(lblSSuburb);
			
			JLabel lblPostalAddress = new JLabel("Postal Address:");
			lblPostalAddress.setBounds(34, 304, 104, 14);
			add(lblPostalAddress);
			
			JLabel lblPhoneNumber = new JLabel("Phone Number:");
			lblPhoneNumber.setBounds(34, 362, 104, 14);
			add(lblPhoneNumber);
			
			JLabel lblPAddress = new JLabel("Suburb:");
			lblPAddress.setBounds(34, 329, 104, 14);
			add(lblPAddress);
			
			JLabel lblEmail = new JLabel("Email:");
			lblEmail.setBounds(34, 387, 104, 14);
			add(lblEmail);
			
			JLabel lblFire = new JLabel("Fire:");
			lblFire.setBounds(35, 412, 46, 14);
			add(lblFire);
			
			JLabel lblInstType = new JLabel("Install Type:");
			lblInstType.setBounds(34, 437, 104, 14);
			add(lblInstType);
			
			JTextArea txtAreaCustInfo = new JTextArea();
			txtAreaCustInfo.setEditable(false);
			txtAreaCustInfo.setBounds(168, 214, 382, 237);
			add(txtAreaCustInfo);
			
			JLabel lblComment = new JLabel("Comment:");
			lblComment.setBounds(37, 462, 103, 14);
			add(lblComment);
			
			JTextArea textArea = new JTextArea();
			textArea.setBounds(168, 462, 382, 63);
			add(textArea);
			
			
			JTable tbtMsg = new JTable(new DefaultTableModel(null, new Object[]{"Date", "Message"}));
			tbtMsg.setShowGrid(false);
			DefaultTableModel modelMsg = (DefaultTableModel) tbtMsg.getModel();
			//I don't know how to display the column names 
			modelMsg.addRow(new Object[]{"Date", "Message"});
			//Sample data
			modelMsg.addRow(new Object[]{"15/09/2017", "Left 1 voice message"});
			
			//Make table scrollable
			
			tbtMsg.setBounds(587, 26, 474, 155);
			add(tbtMsg);
			
			
			JSpinner spnTimeDate = new JSpinner();
			spnTimeDate.setModel(new SpinnerDateModel(new Date(1505908800000L), null, null, Calendar.DAY_OF_YEAR));
			spnTimeDate.setBounds(777, 219, 284, 20);
			add(spnTimeDate);
			
			JRadioButton rdbtnNxtFlwUp = new JRadioButton("Next Follow Up");
			rdbtnNxtFlwUp.setBounds(587, 215, 151, 23);
			add(rdbtnNxtFlwUp);
			
			JRadioButton rdbtnInvoice = new JRadioButton("Invoice");
			rdbtnInvoice.setBounds(587, 273, 109, 23);
			add(rdbtnInvoice);
			
			textField = new JTextField();
			textField.setBounds(777, 276, 284, 20);
			add(textField);
			textField.setColumns(10);
			
			JRadioButton rdbtnNewRadioButton = new JRadioButton("Sold Elsewhere");
			rdbtnNewRadioButton.setBounds(587, 325, 109, 23);
			add(rdbtnNewRadioButton);
			
			
			
			
			JButton btnCancel = new JButton("Cancel");
			btnCancel.setBounds(601, 502, 148, 23);
			add(btnCancel);
			
			JButton btnSave = new JButton("Save Details");
			btnSave.setBounds(893, 502, 148, 23);
			add(btnSave);
			
			JButton btnNewButton = new JButton("Visit Site Check");
			btnNewButton.setBounds(593, 383, 125, 23);
			add(btnNewButton);
			
			JButton btnNewButton_1 = new JButton("View Quotation");
			btnNewButton_1.setBounds(777, 383, 125, 23);
			add(btnNewButton_1);
			
			JButton btnNewButton_2 = new JButton("View Photo");
			btnNewButton_2.setBounds(936, 383, 125, 23);
			add(btnNewButton_2);
			
		  
	  }
	}

