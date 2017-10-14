package Sales;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Permit.PermitPane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;

class CustomerPanel extends JPanel {
	private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
	private String result3 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
	private String param = "";  
	private ResultSet rs;

	private CreateConnection connecting;

	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable salesTbl;
	private DefaultTableModel model1;

	private ConnDetails conDets;

	private JTextField fNameTxtBx;
	private JTextField lNameTxtBx;
	private JTextField homeNumTxtBx;
	private JTextField emailTxtBx;
	private JTextField sAddrTxtBx;
	private JTextField sSuburbTxtBx;
	private JTextField pAddrTxtBx;
	private JTextField pSuburbTxtBx;
	private JTextField pAreaCodeTxtBx;
	private JLabel contactLbl;
	private JLabel fNameLbl;
	private JLabel lNameLbl;
	private JLabel lblHomeNum;
	private JLabel emaillbl;
	private JLabel pAddrlbl;
	private JLabel pSuburblbl;
	private JLabel pAreaCodelbl;
	private JLabel siteAddrLbl;
	private JLabel postAddrlbl; 
	private JLabel sSuburblbl;
	private JLabel sAddrlbl;
	private JCheckBox pAddChbx;
	private JButton searchCustBtn;
	private JButton cancelBtn;
	private JButton createCustBtn;
	private JTextField mobileNumTxtBx;

	public CustomerPanel(ConnDetails conDeets, SalesPane sp) {
		conDets = conDeets;

		connecting = new CreateConnection();

		model1 = new DefaultTableModel();  
		model1.setRowCount(0);
		salesTbl = new JTable(model1);
		salesTbl.setPreferredSize(new Dimension(0, 0));
		salesTbl.setAutoCreateRowSorter(true);

		JScrollPane scrollPane = new JScrollPane(salesTbl);

		header= salesTbl.getTableHeader();
		columnModel = header.getColumnModel();
		add(header); 

		setLayout(null);

		contactLbl = new JLabel("Contact Information");
		contactLbl.setFont(new Font("Arial", Font.BOLD, 20));
		contactLbl.setBounds(263, 10, 201, 20);
		add(contactLbl);

		fNameLbl = new JLabel("First Name:");
		fNameLbl.setBounds(346, 40, 102, 14);
		add(fNameLbl);

		fNameTxtBx = new JTextField();
		fNameTxtBx.setBounds(492, 40, 302, 20);
		add(fNameTxtBx);
		fNameTxtBx.setColumns(10);

		lNameLbl = new JLabel("Last Name:");
		lNameLbl.setBounds(346, 80, 102, 15);
		add(lNameLbl);

		lNameTxtBx = new JTextField();
		lNameTxtBx.setColumns(10);
		lNameTxtBx.setBounds(492, 80, 302, 20);
		add(lNameTxtBx);

		lblHomeNum = new JLabel("Home Number:");
		lblHomeNum.setBounds(346, 120, 102, 15);
		add(lblHomeNum);

		homeNumTxtBx = new JTextField();
		homeNumTxtBx.setColumns(10);
		homeNumTxtBx.setBounds(492, 120, 302, 20);
		add(homeNumTxtBx);

		emaillbl = new JLabel("Email:");
		emaillbl.setBounds(346, 212, 102, 15);
		add(emaillbl);

		emailTxtBx = new JTextField();
		emailTxtBx.setColumns(10);
		emailTxtBx.setBounds(492, 212, 302, 20);
		add(emailTxtBx);

		//Postal Address Information
		postAddrlbl = new JLabel("Postal Address");
		postAddrlbl.setFont(new Font("Arial", Font.BOLD, 20));
		postAddrlbl.setBounds(263, 243, 201, 20);
		add(postAddrlbl);

		pAddrlbl = new JLabel("Postal Street Address:");
		pAddrlbl.setBounds(346, 274, 173, 15);
		add(pAddrlbl);

		pAddrTxtBx = new JTextField();
		pAddrTxtBx.setColumns(10);
		pAddrTxtBx.setBounds(492, 271, 302, 20);
		add(pAddrTxtBx);

		pSuburblbl = new JLabel("Postal Suburb:");
		pSuburblbl.setBounds(346, 315, 173, 15);
		add(pSuburblbl);

		pSuburbTxtBx = new JTextField();
		pSuburbTxtBx.setColumns(10);
		pSuburbTxtBx.setBounds(492, 312, 302, 20);
		add(pSuburbTxtBx);

		pAreaCodelbl = new JLabel("Post Code");
		pAreaCodelbl.setBounds(346, 353, 173, 15);
		add(pAreaCodelbl);

		pAreaCodeTxtBx = new JTextField();
		pAreaCodeTxtBx.setColumns(10);
		pAreaCodeTxtBx.setBounds(492, 350, 302, 20);
		add(pAreaCodeTxtBx);

		//Site Address Information

		siteAddrLbl = new JLabel("Site Address");
		siteAddrLbl.setBounds(263, 381, 201, 20);
		siteAddrLbl.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
		add(siteAddrLbl);

		pAddChbx = new JCheckBox("Postal Address is Site Address");
		pAddChbx.setBounds(346, 413, 222, 23);
		add(pAddChbx);

		//When check box is selected
		pAddChbx.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				if (pAddChbx.isSelected()){
					if (pAddrTxtBx.getText().equals("") || pSuburbTxtBx.getText().equals("")){
						JOptionPane.showMessageDialog(null, "You must enter a postal address first.");

					}else {
						//Get the text from postal address and add it to the sit address
						sAddrTxtBx.setText(pAddrTxtBx.getText());
						sSuburbTxtBx.setText(pSuburbTxtBx.getText());
					}
				}else {
					sAddrTxtBx.setText("");
					sSuburbTxtBx.setText("");
				}

			}
		});

		sAddrlbl = new JLabel("Site Street Address:");
		sAddrlbl.setBounds(346, 457, 135, 15);
		add(sAddrlbl);

		sAddrTxtBx = new JTextField();
		sAddrTxtBx.setColumns(10);
		sAddrTxtBx.setBounds(492, 454, 302, 20);
		add(sAddrTxtBx);		

		sSuburblbl = new JLabel("Site Suburb:");
		sSuburblbl.setBounds(346, 496, 135, 15);
		add(sSuburblbl);

		sSuburbTxtBx = new JTextField();
		sSuburbTxtBx.setColumns(10);
		sSuburbTxtBx.setBounds(492, 493, 302, 20);
		add(sSuburbTxtBx);

		searchCustBtn = new JButton("Search for Existing Customer");
		searchCustBtn.setBounds(263, 532, 210, 25);
		add(searchCustBtn);

		cancelBtn = new JButton("Cancel");
		cancelBtn.setBounds(537, 532, 135, 25);
		add(cancelBtn);
		cancelBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				//Do any of the text boxes contain data
				if(!fNameTxtBx.getText().equals("") || !lNameTxtBx.getText().equals("") || !homeNumTxtBx.getText().equals("") 
						|| !mobileNumTxtBx.getText().equals("") || !emailTxtBx.getText().equals("") || !pAddrTxtBx.getText().equals("") 
						|| !pSuburbTxtBx.getText().equals("") || !pAreaCodeTxtBx.getText().equals("") || pAddChbx.isSelected()
						|| !sAddrTxtBx.getText().equals("") || sSuburbTxtBx.getText().equals("")){
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to cancel?","Warning",dialogButton);
					if(dialogResult == JOptionPane.YES_OPTION){
						//Set all the text boxes to blank
						fNameTxtBx.setText("");
						lNameTxtBx.setText("");
						homeNumTxtBx.setText("");
						mobileNumTxtBx.setText("");
						emailTxtBx.setText("");
						pAddrTxtBx.setText("");
						pSuburbTxtBx.setText("");
						pAreaCodeTxtBx.setText("");
						pAddChbx.setSelected(false);
						sAddrTxtBx.setText("");
						sSuburbTxtBx.setText("");
					}
				}
			}
		});

		createCustBtn = new JButton("Create Customer");
		createCustBtn.setBounds(735, 532, 135, 25);
		add(createCustBtn);
		createCustBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				{ 
					//If validate data is false then there is no error
					if (validatedata() == false){
						//Check to see if the user is sure about creating the customer
						int dialogButton = JOptionPane.YES_NO_OPTION;
						int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to create this customer","Warning",dialogButton);
						if(dialogResult == JOptionPane.YES_OPTION){
							// Saving code here -- add customer to the database
						}
					}
				}					
			}
		});
		this.setLayout(null);

		JLabel MobileNumLbl = new JLabel("Mobile Number:");
		MobileNumLbl.setBounds(346, 166, 118, 14);
		add(MobileNumLbl);

		mobileNumTxtBx = new JTextField();
		mobileNumTxtBx.setBounds(495, 163, 299, 20);
		add(mobileNumTxtBx);
		mobileNumTxtBx.setColumns(10);
	}

	public JTable getSalesTbl(){
		return salesTbl;
	}

	public boolean validatedata(){
		Boolean errorChk = false;
		String error = " ";
		//Check if all text boxes are all filled in correctly
		//List<String> errors = new ArrayList<String>();

		if (fNameTxtBx.getText().equals("")){
			errorChk = true;
			//Cannot be null or more than 15 chars
			error = error + "FIRST NAME: can not be empty\n";
		}else if(fNameTxtBx.getText().length() > 15){
			errorChk = true;
			error = error + "FIRST NAME: can not be more than 15 letters\n";
		}

		if (lNameTxtBx.getText().equals("")){
			errorChk = true;
			error = error + "LAST NAME: can not be empty\n";
		} else if(lNameTxtBx.getText().length() > 15){
			errorChk = true;
			error = error + "LAST NAME: can not be more than 15 letters\n";
		}
		if (homeNumTxtBx.getText().equals("") && !mobileNumTxtBx.getText().equals("")){
			errorChk = true;
			error = error + "NUMBERS: either home number or mobile number can not be empty\n";
		}
		//Convert Phone numbers to Numbers
		try {

			Integer.parseInt(homeNumTxtBx.getText());
			if(homeNumTxtBx.getText().length() > 15){
				errorChk = true;
				error = error + "HOME NUMBER: can not be more than 15 numbers\n";
			}
		}
		catch (NumberFormatException e) {
			//Display number error message 
			errorChk = true;
			error = error + "HOME NUMBER: can only contain numbers\n";
		}
		//Convert Mobile Numbers
		try {
			Integer.parseInt(mobileNumTxtBx.getText());
			if(homeNumTxtBx.getText().length() > 15){
				errorChk = true;
				error = error + "MOBILE NUMBER: can not be more than 15 numbers\n";
			}
		}
		catch (NumberFormatException e) {
			//Display number error message 
			errorChk = true;
			error = error + "MOBILE NUMBER: can only contain numbers\n";
		}
		//Make sure the email contails 
		if (emailTxtBx.getText().equals("")){
			errorChk = true;
			error = error + "EMAIL: con not be empty\n";
		}else if (!emailTxtBx.getText().contains("@")){
			errorChk = true;
			error = error + "EMAIL: must contain an @ \n";
		}else if(emailTxtBx.getText().length() > 30){
			errorChk = true;
			error = error + "EMAIL: can not be longer than 30 letters\n";
		}

		//otherwise the email field should be valid
		if (pAddrTxtBx.getText().equals("") || sAddrTxtBx.getText().equals("")){
			errorChk = true;
			error = error + "STREET ADDRESS: can not be empty\n";
		}else if (pAddrTxtBx.getText().length() > 30 || sAddrTxtBx.getText().length() > 30){
			errorChk = true;
			error = error + "STREET ADDRESS: can not be longer than 30 letters\n";
		}

		if (pSuburbTxtBx.getText().equals("") || sSuburbTxtBx.getText().equals("")){
			errorChk = true;
			error = error + "SUBURB: can not be empty\n";
		} else if(pSuburbTxtBx.getText().length() > 20 || sAddrTxtBx.getText().length() > 20){
			errorChk = true;
			error = error + "SUBURB: can not be longer than 30 letters\n";
		}

		if (pAreaCodeTxtBx.getText().equals("")){
			errorChk = true;
			error = error + "AREA CODE: can not be empty\n";

		} else if (pAreaCodeTxtBx.getText().length() > 20){
			errorChk = true;
			error = error + "AREA CODE: can not be longer than 20 letters\n";
		}
		
		if (pAddChbx.isSelected())
			if (!pAddrTxtBx.getText().equals(sAddrTxtBx.getText()) || !pSuburbTxtBx.getText().equals(pSuburbTxtBx.getText())){
				errorChk = true;
				error = error + "CHECK BOX: If the check box is selected \nplease ensure the addresses are the same\n";
			}

		//Check to see if any errors has occured
		if (errorChk == true){
			JOptionPane.showMessageDialog(null, error);
		}
		return errorChk;
	}


}
