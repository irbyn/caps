package Sales;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

import javax.swing.JTextField;
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
import javax.swing.BoxLayout;
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




		createCustBtn = new JButton("Create Customer");
		createCustBtn.setBounds(735, 532, 135, 25);
		add(createCustBtn);
		createCustBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				{ 
					//Check if all text boxes are all filled in correctly

					if (fNameTxtBx.getText().equals(null) || fNameTxtBx.getText().length() > 15){
						//Cannot be null or more than 15 chars 
					}
					if (lNameTxtBx.getText().equals(null) || lNameTxtBx.getText().length() > 15){
						System.out.println("Names");
					}
					//Convert Phone numbers to Numbers
					try {

						Integer.parseInt(homeNumTxtBx.getText());
						Integer.parseInt(mobileNumTxtBx.getText());

						if (homeNumTxtBx.getText().equals(null) || homeNumTxtBx.getText().length() > 15){
							System.out.println("Number");
						}
					}
					catch (NumberFormatException e) {
						//Display number error message 
						//System.out.println("Test");
					}
					//Make sure the 
					if (emailTxtBx.getText().equals(null)){

					}
						//If the email contains an @ then check to make sure it isn't too long
					else if (emailTxtBx.getText().contains("@") || emailTxtBx.getText().length() > 255 ){
							System.out.println("Email");
						}
					//otherwise the email field should be valid
					if ( pAddrTxtBx.getText().equals(null) || pAddrTxtBx.getText().length() > 30){
						System.out.println("P Address");

					}
					if (pSuburbTxtBx.getText().equals(null) || pSuburbTxtBx.getText().length() > 20){
						System.out.println("P Suburb");

					}
					if (pAreaCodeTxtBx.getText().equals(null) || pAreaCodeTxtBx.getText().length() > 20){
						System.out.println("P Code");

					}

					if (sAddrTxtBx.getText().equals(null) || sAddrTxtBx.getText().length() > 20){
						System.out.println("S Address");

					}
					if (sSuburbTxtBx.getText().equals(null) || sAddrTxtBx.getText().length() > 20){
						System.out.println("S Suburb");

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
}
