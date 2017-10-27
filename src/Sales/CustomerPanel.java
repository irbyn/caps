package Sales;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JTextField;
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JCheckBox;

class CustomerPanel extends JPanel {
	private String qryCustDetails = "EXEC AWS_WCH_DB.dbo.[s_CustomerDetails] ";
	private String upReceived = "call AWS_WCH_DB.dbo.s_SalesUpdateCustomer";
	private String getCustID = "call AWS_WCH_DB.dbo.s_SaleGetCustID";
	private String createCustANDSale = "call AWS_WCH_DB.dbo.s_CreateCustomerSale";
	private String createNewCust = "call AWS_WCH_DB.dbo.s_CreateNewCustomer";
	private String createNewSale = "call AWS_WCH_DB.dbo.s_CreateNewSale";
	private String searchExstCust = "call AWS_WCH_DB.dbo.s_SalesSearchExistingCust";
	private String qryOneValSearch = "call AWS_WCH_DB.dbo.s_SalesSearchValOneCustomer";
	private String qryTwoValSearch = "call AWS_WCH_DB.dbo.s_SalesSearchValTwoCustomer";
	private String qryAllValSearch = "call AWS_WCH_DB.dbo.s_SalesSearchValAllCustomer";
	private int [] columnWidth = {50, 70, 150, 100, 100, 100}; 	
	private String param = "";  
	private ResultSet rs;
	private ResultSet rs2;

	private CreateConnection connecting;

	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel searchPanel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable salesTbl;
	private DefaultTableModel model1;
	private Boolean rowSelected;

	private JTextField mobileNumTxtBx;
	private JTextField sFNameTxtBx;
	private JTextField sLNameTxtBx;
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
	private JLabel mobileNumLbl;
	private JLabel emaillbl;
	private JLabel pAddrlbl;
	private JLabel pSuburblbl;
	private JLabel pAreaCodelbl;
	private JLabel siteAddrLbl;
	private JLabel postAddrlbl; 
	private JLabel sSuburblbl;
	private JLabel sAddrlbl;
	private JLabel searchLbl;
	private JLabel sFNameLbl;
	private JLabel sLNameLbl;
	private JCheckBox pAddChbx;
	private JButton btnSearch;
	private JButton cancelBtn;
	private JButton updateBtn;
	private JButton createCustBtn;

	private SalesPane sp;
	private ConnDetails conDeets;
	private JButton clearSchBtn;
	private JTextField reesTxtBx;


	public CustomerPanel(ConnDetails conDeets, SalesPane sp) {
		this.sp = sp;
		this.conDeets = conDeets;
		rowSelected = false;

		connecting = new CreateConnection();

		model1 = new DefaultTableModel();  
		model1.setRowCount(0);
		salesTbl = new JTable(model1);
		salesTbl.setAutoCreateRowSorter(true);

		JScrollPane scrollPane = new JScrollPane(salesTbl);

		header= salesTbl.getTableHeader();
		columnModel = header.getColumnModel();
		add(header); 

		searchPanel = new JPanel();
		searchPanel.setBounds(0, 0, 611, 77);
		searchPanel.setLayout(null);

		//Search Panel contents
		searchLbl = new JLabel("Search");
		searchLbl.setFont(new Font("Arial", Font.BOLD, 20));
		searchLbl.setBounds(20, 11, 122, 20);
		searchPanel.add(searchLbl);

		sFNameLbl = new JLabel("First Name:");
		sFNameLbl.setBounds(20, 42, 68, 14);
		searchPanel.add(sFNameLbl);

		sLNameLbl = new JLabel("Last Name:");
		sLNameLbl.setBounds(217, 42, 68, 14);
		searchPanel.add(sLNameLbl);

		sFNameTxtBx = new JTextField();
		sFNameTxtBx.setBounds(87, 39, 120, 20);
		searchPanel.add(sFNameTxtBx);
		sFNameTxtBx.setColumns(10);

		sLNameTxtBx = new JTextField();
		sLNameTxtBx.setColumns(10);
		sLNameTxtBx.setBounds(282, 39, 120, 20);
		searchPanel.add(sLNameTxtBx);

		btnSearch = new JButton("Search");
		btnSearch.setBounds(482, 6, 120, 25);
		searchPanel.add(btnSearch);

		clearSchBtn = new JButton("Clear Search");
		clearSchBtn.setBounds(282, 6, 120, 25);
		searchPanel.add(clearSchBtn);

		//Panel for the table
		tablePanel = new JPanel();
		tablePanel.setBounds(10, 78, 601, 494);      
		tablePanel.setLayout(new BorderLayout());
		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(salesTbl.getTableHeader(), BorderLayout.NORTH);

		//Content panel
		infoPanel = new JPanel();
		infoPanel.setBounds(621, 0, 424, 585);
		infoPanel.setLayout(null);

		contactLbl = new JLabel("Contact Information");
		contactLbl.setFont(new Font("Arial", Font.BOLD, 20));
		contactLbl.setBounds(20, 11, 201, 20);
		infoPanel.add(contactLbl);

		fNameLbl = new JLabel("First Name:");
		fNameLbl.setBounds(55, 41, 101, 14);
		infoPanel.add(fNameLbl);

		fNameTxtBx = new JTextField();
		fNameTxtBx.setBounds(201, 41, 210, 20);
		infoPanel.add(fNameTxtBx);
		fNameTxtBx.setColumns(10);

		lNameLbl = new JLabel("Last Name:");
		lNameLbl.setBounds(55, 81, 101, 15);
		infoPanel.add(lNameLbl);

		lNameTxtBx = new JTextField();
		lNameTxtBx.setColumns(10);
		lNameTxtBx.setBounds(201, 81, 210, 20);
		infoPanel.add(lNameTxtBx);

		lblHomeNum = new JLabel("Home Number:");
		lblHomeNum.setBounds(55, 121, 101, 15);
		infoPanel.add(lblHomeNum);

		homeNumTxtBx = new JTextField();
		homeNumTxtBx.setColumns(10);
		homeNumTxtBx.setBounds(201, 121, 210, 20);
		infoPanel.add(homeNumTxtBx);

		mobileNumLbl = new JLabel("Mobile Number:");
		mobileNumLbl.setBounds(55, 158, 101, 14);
		infoPanel.add(mobileNumLbl);

		mobileNumTxtBx = new JTextField();
		mobileNumTxtBx.setBounds(201, 152, 210, 20);
		infoPanel.add(mobileNumTxtBx);

		mobileNumTxtBx.setColumns(10);
		emaillbl = new JLabel("Email:");
		emaillbl.setBounds(55, 198, 101, 15);
		infoPanel.add(emaillbl);

		emailTxtBx = new JTextField();
		emailTxtBx.setColumns(10);
		emailTxtBx.setBounds(201, 195, 210, 20);
		infoPanel.add(emailTxtBx);

		//Postal Address Information
		postAddrlbl = new JLabel("Postal Address");
		postAddrlbl.setFont(new Font("Arial", Font.BOLD, 20));
		postAddrlbl.setBounds(20, 229, 182, 20);
		infoPanel.add(postAddrlbl);

		pAddrlbl = new JLabel("Postal Street Address:");
		pAddrlbl.setBounds(55, 265, 135, 15);
		infoPanel.add(pAddrlbl);

		pAddrTxtBx = new JTextField();
		pAddrTxtBx.setColumns(10);
		pAddrTxtBx.setBounds(201, 262, 210, 20);
		infoPanel.add(pAddrTxtBx);

		pSuburblbl = new JLabel("Postal Suburb:");
		pSuburblbl.setBounds(55, 306, 132, 15);
		infoPanel.add(pSuburblbl);

		pSuburbTxtBx = new JTextField();
		pSuburbTxtBx.setColumns(10);
		pSuburbTxtBx.setBounds(201, 303, 210, 20);
		infoPanel.add(pSuburbTxtBx);

		pAreaCodelbl = new JLabel("Post Code");
		pAreaCodelbl.setBounds(55, 344, 132, 15);
		infoPanel.add(pAreaCodelbl);

		pAreaCodeTxtBx = new JTextField();
		pAreaCodeTxtBx.setColumns(10);
		pAreaCodeTxtBx.setBounds(201, 341, 210, 20);
		infoPanel.add(pAreaCodeTxtBx);

		//Site Address Information

		siteAddrLbl = new JLabel("Site Address");
		siteAddrLbl.setBounds(29, 411, 158, 20);
		siteAddrLbl.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
		infoPanel.add(siteAddrLbl);

		pAddChbx = new JCheckBox("Postal Address is Site Address");
		pAddChbx.setBounds(201, 413, 210, 23);
		infoPanel.add(pAddChbx);

		sAddrlbl = new JLabel("Site Street Address:");
		sAddrlbl.setBounds(55, 458, 135, 15);
		infoPanel.add(sAddrlbl);

		sAddrTxtBx = new JTextField();
		sAddrTxtBx.setColumns(10);
		sAddrTxtBx.setBounds(201, 455, 210, 20);
		infoPanel.add(sAddrTxtBx);		

		sSuburblbl = new JLabel("Site Suburb:");
		sSuburblbl.setBounds(55, 497, 135, 15);
		infoPanel.add(sSuburblbl);

		sSuburbTxtBx = new JTextField();
		sSuburbTxtBx.setColumns(10);
		sSuburbTxtBx.setBounds(201, 494, 210, 20);
		infoPanel.add(sSuburbTxtBx);

		cancelBtn = new JButton("Cancel");
		cancelBtn.setBounds(22, 533, 120, 25);
		infoPanel.add(cancelBtn);


		updateBtn = new JButton("Update");
		updateBtn.setBounds(161, 533, 120, 25);
		updateBtn.setEnabled(false);
		infoPanel.add(updateBtn);

		createCustBtn = new JButton("Create Customer");
		createCustBtn.setBounds(237, 372, 135, 25);
		infoPanel.add(createCustBtn);

		this.setLayout(null);
		this.add(searchPanel);

		JLabel reesLbl = new JLabel("Reese #:");
		reesLbl.setBounds(425, 42, 62, 14);
		searchPanel.add(reesLbl);

		reesTxtBx = new JTextField();
		reesTxtBx.setColumns(10);
		reesTxtBx.setBounds(482, 39, 120, 20);
		searchPanel.add(reesTxtBx);
		this.add(tablePanel); 
		this.add(infoPanel);

		JButton createSaleBtn = new JButton("Create Sale");
		createSaleBtn.setBounds(291, 533, 120, 25);
		infoPanel.add(createSaleBtn);	
		createSaleBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {

				Boolean errorChk = false;
				String error = " ";
				//otherwise the email field should be valid
				if (sAddrTxtBx.getText().equals("")){
					errorChk = true;
					error = error + "STREET ADDRESS: can not be empty\n";
				}else if (pAddrTxtBx.getText().length() > 30 || sAddrTxtBx.getText().length() > 30){
					errorChk = true;
					error = error + "STREET ADDRESS: can not be longer than 30 letters\n";
				}

				if (sSuburbTxtBx.getText().equals("")){
					errorChk = true;
					error = error + "SUBURB: can not be empty\n";
				} else if(sAddrTxtBx.getText().length() > 20){
					errorChk = true;
					error = error + "SUBURB: can not be longer than 30 letters\n";
				}

				//Check to see if any errors has occured
				if (errorChk == true){
					JOptionPane.showMessageDialog(null, error);
				}else if (validatedata() == false){
					//If the customer doesn't yet exist create a new customer and sale
					if (rowSelected=false){
						//getID();
						createCustAndSale();
						sp.showMessage("Creating New Customer and Sale");
						resetTable();
						clearFields();
						createCustBtn.setEnabled(true);
					}
					//Otherwise just create a new sale
					else{
						createSale();
						sp.showMessage("Creating new Sale");
						resetTable();
						clearFields();
						createCustBtn.setEnabled(true);
					}

				}

			}
		});

		salesTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					rowSelected=true;
					createCustBtn.setEnabled(false);
					updateBtn.setEnabled(true);
					try{
						//Get the customer ID as a paramater to feed into the SQL procedure 
						param = salesTbl.getValueAt(salesTbl.getSelectedRow(), 0).toString();
						displayClientDetails(param);
						rowSelected = true;
						updateBtn.setEnabled(true);
					} catch (IndexOutOfBoundsException e){

					}
				}
			}
		});

		//Display the initial table
		//rs = sp.getResults(0);		
		//salesTbl.setModel(DbUtils.resultSetToTableModel(rs));  

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

		updateBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				{ 
					if (rowSelected){
						//If validate data is false then there is no error
						if (validatedata() == false){
							//Check to see if the user is sure about creating the customer
							int dialogButton = JOptionPane.YES_NO_OPTION;
							int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to modify this customer","Warning",dialogButton);
							if(dialogResult == JOptionPane.YES_OPTION){
								// Saving code here -- add customer to the database
								sp.showMessage("Updating Customer Details");
								updateCustomer();
								resetTable();
								clearFields();
								createCustBtn.setEnabled(true);
							}
						}
					}
				}					
			}
		});

		createCustBtn.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				{ 
					//If validate data is false then there is no error
					if (validatedata() == false){
						//Check to see if the user is sure about creating the customer
						int dialogButton = JOptionPane.YES_NO_OPTION;
						int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to create this customer","Warning",dialogButton);
						if(dialogResult == JOptionPane.YES_OPTION){
							//Check to see if customer already exists
							try {
								//if the statement doesn't return any customers then create a new customer
								if (!checkDBForCust().isBeforeFirst()){
									createNewCustomer();
									sp.showMessage("Creating new customer");
									//createCustomer();
									resetTable();
									clearFields();
								}else{
									JOptionPane.showMessageDialog(null, "This customer already exists! \nPlease search and edit them.");
								}
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}
				}					
			}
		});

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
						resetTable();
						clearFields();
						createCustBtn.setEnabled(true);
						updateBtn.setEnabled(false);
					}
				}
			}
		});

		clearSchBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				resetTable();
				clearFields();
				createCustBtn.setEnabled(true);
				updateBtn.setEnabled(false);
				sFNameTxtBx.setText("");
				sLNameTxtBx.setText("");
				reesTxtBx.setText("");
			}
		});

		btnSearch.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				//If there is actually something in the search boxes
				if (sFNameTxtBx.getText().equals("") && sLNameTxtBx.getText().equals("") && reesTxtBx.getText().equals(""))
				{
					JOptionPane.showMessageDialog(null, "You have not choosen anything to search! \nPlease try again.");	
				}else{
					String qry;	

					if (!sFNameTxtBx.getText().equals("") && !sLNameTxtBx.getText().equals("") && !reesTxtBx.getText().equals("")){
						qry = qryAllValSearch;	
					}		
					else if ((!sFNameTxtBx.getText().equals("") && !sLNameTxtBx.getText().equals("")) //If there is text in 2 text boxes  
							||	(!sFNameTxtBx.getText().equals("") && !reesTxtBx.getText().equals("")) 
							||	(!sLNameTxtBx.getText().equals("") && !reesTxtBx.getText().equals(""))){				
						qry = qryTwoValSearch;	
					}
					else{
						qry = qryOneValSearch;
					}

					ResultSet rs = searchCust(qry);
					salesTbl.setModel(DbUtils.resultSetToTableModel(rs));
					//spaceHeader(columnModel, columnWidth);
					rowSelected=false;
					param = "";		
				}	
			}


		});
	}


	private void displayClientDetails(String parameter) {
		rs2 = sp.getDetails(qryCustDetails, param);
		try {
			while(rs2.next()){
				String customerFName 	= rs2.getString("customerFName");
				String customerLName 	= rs2.getString("customerLName");
				String customerAddress 	= rs2.getString("customerPStreetAddress");
				String customerSuburb 	= rs2.getString("customerPSuburb");
				String customerPostCode = rs2.getString("customerPostCode");
				String customerPhone 	= rs2.getString("customerPhone");
				String customerMobile 	= rs2.getString("customerMobile");
				String customerEmail 	= rs2.getString("customerEmail");				 						

				fNameTxtBx.setText(customerFName);
				lNameTxtBx.setText(customerLName);			 
				homeNumTxtBx.setText(customerPhone);
				mobileNumTxtBx.setText(customerMobile);
				emailTxtBx.setText(customerEmail);
				pAddrTxtBx.setText(customerAddress);
				pSuburbTxtBx.setText(customerSuburb);
				pAreaCodeTxtBx.setText(customerPostCode);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		ResultSet rs = sp.getResults(0);
		salesTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
		spaceHeader(columnModel, columnWidth);
		rowSelected=false;
		param = "";
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
		if (homeNumTxtBx.getText().equals("") && mobileNumTxtBx.getText().equals("")){
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
		if (pAddrTxtBx.getText().equals("")){
			errorChk = true;
			error = error + "STREET ADDRESS: can not be empty\n";
		}else if (pAddrTxtBx.getText().length() > 30){
			errorChk = true;
			error = error + "STREET ADDRESS: can not be longer than 30 letters\n";
		}

		if (pSuburbTxtBx.getText().equals("")){
			errorChk = true;
			error = error + "SUBURB: can not be empty\n";
		} else if(pSuburbTxtBx.getText().length() > 20){
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

	public void clearFields(){
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

	protected ResultSet checkDBForCust(){

		try {
			CallableStatement sm = null;

			String search = "{" + searchExstCust +"(?,?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   
			sm = conn.prepareCall(search);

			sm.setString(1, getFName());
			sm.setString(2, getLName());
			sm.setString(3, getEmail());

			ResultSet qryResults = sm.executeQuery();
			rs = qryResults;

		}catch (SQLServerException sqex){
			JOptionPane.showMessageDialog(null, "DB_ERROR: " + sqex);
		}catch(Exception ex){ 
			JOptionPane.showMessageDialog(null, "CONNECTION_ERROR: " + ex);
		}	 

		return rs;
	}

	protected ResultSet searchCust(String query){
		try {	
			CallableStatement sm = null;

			String qry = "{" + query +"(?,?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	
			sm = conn.prepareCall(qry);

			if (!sFNameTxtBx.getText().equals("")){
				sm.setString(1, sFNameTxtBx.getText());
			}else{
				sm.setString(1, null);
			}
			if (!sLNameTxtBx.getText().equals("")){
				sm.setString(2, sLNameTxtBx.getText());
			}else{
				sm.setString(2, null);
			}
			if (!reesTxtBx.getText().equals("")){
				sm.setString(3, reesTxtBx.getText());
			}else{
				sm.setString(3, null);
			}
			ResultSet qryResults = sm.executeQuery();
			rs = qryResults;

		}catch (SQLServerException sqex){
			JOptionPane.showMessageDialog(null, "DB_ERROR: " + sqex);
		}catch(Exception ex){ 
			JOptionPane.showMessageDialog(null, "CONNECTION_ERROR: " + ex);
		}				
		return rs;
	}

	protected void createSale() {

		CallableStatement sm = null;
		try {

			String update = "{" + createNewSale +"(?,?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			sm = conn.prepareCall(update);

			sm.setInt(1, getID());
			sm.setString(2, getSAddr());
			sm.setString(3, getSSuburb());

			sm.executeUpdate();
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

	protected void createCustAndSale() {

		CallableStatement sm = null;
		try {

			String update = "{" + createCustANDSale +"(?,?,?,?,?,?,?,?,?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			sm = conn.prepareCall(update);

			sm.setString(1, getFName());
			sm.setString(2, getLName());
			sm.setString(3, getPAddr());
			sm.setString(4, getPSuburb());
			sm.setString(5, getPAreaCode());
			sm.setString(6, getPhone());
			sm.setString(7, getMobile());
			sm.setString(8, getEmail());
			sm.setString(9, getSAddr());
			sm.setString(10, getSSuburb());

			sm.executeUpdate();
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

	protected void createNewCustomer() {

		CallableStatement sm = null;
		try {

			String update = "{" + createNewCust +"(?,?,?,?,?,?,?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			sm = conn.prepareCall(update);

			sm.setString(1, getFName());
			sm.setString(2, getLName());
			sm.setString(3, getPAddr());
			sm.setString(4, getPSuburb());
			sm.setString(5, getPAreaCode());
			sm.setString(6, getPhone());
			sm.setString(7, getMobile());
			sm.setString(8, getEmail());

			sm.executeUpdate();
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

	protected void updateCustomer() {

		CallableStatement sm = null;
		try {

			String update = "{" + upReceived +"(?,?,?,?,?,?,?,?,?,?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			sm = conn.prepareCall(update);

			sm.setString(1, param);
			sm.setString(2, getFName());
			sm.setString(3, getLName());
			sm.setString(4, getPhone());
			sm.setString(5, getMobile());
			sm.setString(6, getEmail());
			sm.setString(7, getPAddr());
			sm.setString(8, getPSuburb());
			sm.setString(9, getPAreaCode());
			sm.setString(10, getSAddr());
			sm.setString(11, getSSuburb());

			sm.executeUpdate();
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

	public int getID(){
		int customerID = 0;
		try {
			CallableStatement sm = null;
			String search = "{" + getCustID +"(?,?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   
			sm = conn.prepareCall(search);

			sm.setString(1, getFName());
			sm.setString(2, getLName());
			sm.setString(3, getEmail());

			ResultSet qryResults = sm.executeQuery();
			rs = qryResults;

			while(qryResults.next()){
				customerID	= qryResults.getInt("customerID");
			}

		}catch (SQLServerException sqex){
			JOptionPane.showMessageDialog(null, "DB_ERROR: " + sqex);
		}catch(Exception ex){ 
			JOptionPane.showMessageDialog(null, "CONNECTION_ERROR: " + ex);
		}	 
		return customerID;
	}

	public String getFName(){
		return fNameTxtBx.getText();
	}
	public String getLName(){
		return lNameTxtBx.getText();
	}
	public String getPhone(){
		return homeNumTxtBx.getText();
	}
	public String getMobile(){
		return mobileNumTxtBx.getText();
	}
	public String getEmail(){
		return emailTxtBx.getText();
	}
	public String getPAddr(){
		return pAddrTxtBx.getText();
	}
	public String getPSuburb(){
		return pSuburbTxtBx.getText();
	}
	public String getPAreaCode(){
		return pAreaCodeTxtBx.getText();
	}
	public String getSAddr(){
		return sAddrTxtBx.getText();
	}
	public String getSSuburb(){
		return sSuburbTxtBx.getText();
	}
	public JPanel getInfoPanel(){
		return infoPanel;
	}


}
