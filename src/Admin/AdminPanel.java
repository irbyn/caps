package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import DB_Comms.CreateConnection;
import Installs.InstallsPane;
import Main.ConnDetails;
import Main.Homescreen;
import net.proteanit.sql.DbUtils;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class AdminPanel extends JFrame {
	private int[] columnWidth = new int[]{50, 70, 150, 100, 100, 100};
	private String qryList = new String("EXEC AWS_WCH_DB.dbo.[a_UserList]");
	private String qryDetails = new String("EXEC AWS_WCH_DB.dbo.[a_UserDetails]");
	private String qryCreateUser = "call AWS_WCH_DB.dbo.a_createNewUser";
	private String upUser = "call AWS_WCH_DB.dbo.a_UpdateUser";
	private String param = "";  
	private Homescreen hs;

	//private String dbURL = "";
	private ResultSet rs;
	private ResultSet rs2;
	private CreateConnection connecting;
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable adminTbl;
	private DefaultTableModel model1;
	private JTextArea detailsTxtArea;
	private CreateConnection conn;

	private JTextField fNameTxtBx;
	private JTextField lNameTxtBx;
	private JTextField phoneTxtBx;
	private JTextField emailtxtBx;
	private JTextField pAddrtxtBx;
	private JTextField pSuburbtxtbx;
	private JTextField pAreaCodetxtBx;
	private JLabel contactlbl;
	private JLabel fNameLbl;
	private JLabel lNameLbl;
	private JLabel lblPhone;
	private JLabel mobileLbl;
	private JLabel emaillbl;
	private JLabel pAddrlbl;
	private JLabel pSuburblbl;
	private JLabel pAreaCodelbl;
	private JLabel reesNumLbl;
	private JLabel userlbl;
	private JLabel passLbl;
	private JLabel rePassLbl;
	private JButton modifyUserBtn;
	private JButton cancelBtn;
	private JButton createNewUserBtn;
	private JButton logOutBtn;
	private JButton updateBtn;
	private JTextField NZHHANumTxtBx;
	private static String user;
	private static String pass;
	private JTextField usertxtBx;
	private JPasswordField passtxtBx;
	private JPasswordField reConnPasstxtBx;
	private JTextField councNumtxtBx;
	private JTextField reeseNumbtxtBx;
	private JCheckBox chckbxAccAct;
	private JComboBox<String> roleTypeCmbBx;

	private JLabel lblAbilities; 
	private JCheckBox chckbxSCheck ;
	private JCheckBox chckbxInstaller; 
	private JCheckBox chckbxSales;
	private JLabel lblNZHHANumb;
	private JLabel roleTypelbl;

	private ConnDetails conDeets;

	private ResultSet results;
	private ResultSet qryResults;
	private int tabIndex = 0;
	//private PermitsReqPanel permitReq;

	// Stored procedures to fill tables (Triggered by tab selection)

	private JTextField mobileTxtBx;
	private JButton saveNewUserBtn;
	private Boolean rowSelected;
	private JLabel ranklbl;
	private JTextField rankTxtBx;
	private Homescreen homescreen;

	public AdminPanel(String User, String Pass, Homescreen hs){
		rowSelected = false;
		user = User;
		pass = Pass;

		homescreen = new Homescreen(user, pass);

		//PASS THE LOGIN DETAILS TO Class connectionDetails
		conDeets = new ConnDetails(user, pass);



		getContentPane().setLayout(null);
		setPreferredSize(new Dimension(1100, 700));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		connecting = new CreateConnection();

		model1 = new DefaultTableModel();  
		model1.setRowCount(0);
		adminTbl = new JTable(model1);
		adminTbl.setAutoCreateRowSorter(true);

		JScrollPane scrollPane = new JScrollPane(adminTbl);

		header= adminTbl.getTableHeader();
		columnModel = header.getColumnModel();
		getContentPane().add(header); 

		tablePanel = new JPanel();
		tablePanel.setBounds(29, 44, 1025, 279);      
		tablePanel.setLayout(new BorderLayout());

		infoPanel = new JPanel();
		infoPanel.setBounds(10, 334, 1100, 326);
		infoPanel.setLayout(null);

		logOutBtn = new JButton("Log Out ");
		logOutBtn.setBounds(982, 12, 102, 24);
		getContentPane().add(logOutBtn);

		userlbl = new JLabel("Username:");
		userlbl.setBounds(359, 205, 73, 14);
		infoPanel.add(userlbl);

		lblAbilities = new JLabel("Abilities:");
		lblAbilities.setBounds(738, 205, 63, 14);
		infoPanel.add(lblAbilities);

		chckbxSCheck = new JCheckBox("Site Check");
		chckbxSCheck.setBounds(834, 200, 98, 23);
		chckbxSCheck.setEnabled(false);
		infoPanel.add(chckbxSCheck);

		chckbxInstaller = new JCheckBox("Installer");
		chckbxInstaller.setBounds(996, 200, 73, 23);
		chckbxInstaller.setEnabled(false);
		infoPanel.add(chckbxInstaller);

		chckbxSales = new JCheckBox("Sales");
		chckbxSales.setBounds(934, 200, 60, 23);
		chckbxSales.setEnabled(false);
		infoPanel.add(chckbxSales);

		lblNZHHANumb = new JLabel("NZHHA Number:");
		lblNZHHANumb.setBounds(738, 155, 109, 14);
		infoPanel.add(lblNZHHANumb);

		//Personal Customer Information
		contactlbl = new JLabel("User Information");
		contactlbl.setBounds(26, 0, 201, 20);
		infoPanel.add(contactlbl);
		contactlbl.setFont(new Font("Arial", Font.BOLD, 20));

		fNameLbl = new JLabel("First Name:");
		fNameLbl.setBounds(26, 105, 85, 14);
		infoPanel.add(fNameLbl);

		fNameTxtBx = new JTextField();
		fNameTxtBx.setBounds(121, 100, 201, 24);
		infoPanel.add(fNameTxtBx);
		fNameTxtBx.setColumns(10);

		lNameLbl = new JLabel("Last Name:");
		lNameLbl.setBounds(26, 155, 85, 15);
		infoPanel.add(lNameLbl);

		lNameTxtBx = new JTextField();
		lNameTxtBx.setBounds(121, 150, 201, 24);
		infoPanel.add(lNameTxtBx);
		lNameTxtBx.setColumns(10);

		lblPhone = new JLabel("Phone Number:");
		lblPhone.setBounds(26, 205, 85, 15);
		infoPanel.add(lblPhone);

		phoneTxtBx = new JTextField();
		phoneTxtBx.setBounds(121, 200, 201, 24);
		infoPanel.add(phoneTxtBx);
		phoneTxtBx.setColumns(10);

		emaillbl = new JLabel("Email:");
		emaillbl.setBounds(26, 305, 46, 15);
		infoPanel.add(emaillbl);

		emailtxtBx = new JTextField();
		emailtxtBx.setBounds(121, 300, 201, 24);
		infoPanel.add(emailtxtBx);
		emailtxtBx.setColumns(10);

		roleTypeCmbBx = new JComboBox<String> ();
		roleTypeCmbBx.setModel(new DefaultComboBoxModel<String>(new String[] {"Salesperson", "Installer", "Admin", "Shop"}));
		roleTypeCmbBx.setBackground(Color.WHITE);
		roleTypeCmbBx.setBounds(498, 3, 199, 20);
		infoPanel.add(roleTypeCmbBx);

		roleTypelbl = new JLabel("Role Type");
		roleTypelbl.setBounds(359, 6, 118, 14);
		infoPanel.add(roleTypelbl);

		pAddrlbl = new JLabel("Postal Street Address:");
		pAddrlbl.setBounds(359, 55, 134, 15);
		infoPanel.add(pAddrlbl);

		pAddrtxtBx = new JTextField();
		pAddrtxtBx.setBounds(496, 50, 201, 24);
		infoPanel.add(pAddrtxtBx);
		pAddrtxtBx.setColumns(10);

		pSuburblbl = new JLabel("Postal Suburb:");
		pSuburblbl.setBounds(359, 105, 118, 15);
		infoPanel.add(pSuburblbl);

		pSuburbtxtbx = new JTextField();
		pSuburbtxtbx.setBounds(496, 100, 201, 24);
		infoPanel.add(pSuburbtxtbx);
		pSuburbtxtbx.setColumns(10);

		pAreaCodelbl = new JLabel("Post Code:");
		pAreaCodelbl.setBounds(359, 155, 118, 15);
		infoPanel.add(pAreaCodelbl);

		pAreaCodetxtBx = new JTextField();
		pAreaCodetxtBx.setBounds(496, 150, 201, 24);
		infoPanel.add(pAreaCodetxtBx);
		pAreaCodetxtBx.setColumns(10);

		NZHHANumTxtBx = new JTextField();
		NZHHANumTxtBx.setBounds(857, 150, 201, 24);
		infoPanel.add(NZHHANumTxtBx);
		NZHHANumTxtBx.setColumns(10);

		passLbl = new JLabel("Password:");
		passLbl.setBounds(359, 255, 73, 14);
		infoPanel.add(passLbl);

		usertxtBx = new JTextField();
		usertxtBx.setBounds(496, 200, 201, 24);
		infoPanel.add(usertxtBx);
		usertxtBx.setColumns(10);

		passtxtBx = new JPasswordField();
		passtxtBx.setBounds(496, 250, 201, 24);
		infoPanel.add(passtxtBx);

		reConnPasstxtBx = new JPasswordField();
		reConnPasstxtBx.setBounds(496, 300, 201, 24);
		infoPanel.add(reConnPasstxtBx);

		rePassLbl = new JLabel("Re Confirm Password:");
		rePassLbl.setBounds(359, 305, 134, 14);
		infoPanel.add(rePassLbl);

		ranklbl = new JLabel("Ranked Number");
		ranklbl.setBounds(734, 6, 109, 14);
		infoPanel.add(ranklbl);

		rankTxtBx = new JTextField();
		rankTxtBx.setBounds(857, 0, 201, 20);
		infoPanel.add(rankTxtBx);
		rankTxtBx.setColumns(10);

		JLabel councNumblbl = new JLabel("Council Number:");
		councNumblbl.setBounds(738, 55, 95, 14);
		infoPanel.add(councNumblbl);

		councNumtxtBx = new JTextField();
		councNumtxtBx.setBounds(857, 50, 201, 24);
		infoPanel.add(councNumtxtBx);
		councNumtxtBx.setColumns(10);

		reesNumLbl = new JLabel("Rees Number:");
		reesNumLbl.setBounds(738, 105, 108, 14);
		infoPanel.add(reesNumLbl);

		reeseNumbtxtBx = new JTextField();
		reeseNumbtxtBx.setColumns(10);
		reeseNumbtxtBx.setBounds(857, 100, 201, 24);
		infoPanel.add(reeseNumbtxtBx);

		chckbxAccAct = new JCheckBox("Active Account");
		chckbxAccAct.setBounds(26, 50, 134, 23);
		infoPanel.add(chckbxAccAct);

		modifyUserBtn = new JButton("Modify");
		modifyUserBtn.setBounds(859, 251, 100, 23);
		modifyUserBtn.setEnabled(false);
		infoPanel.add(modifyUserBtn);

		updateBtn = new JButton("Update");
		updateBtn.setBounds(969, 251, 100, 23);
		updateBtn.setEnabled(false);
		infoPanel.add(updateBtn);

		cancelBtn = new JButton("Cancel");
		cancelBtn.setBounds(738, 250, 100, 23);
		infoPanel.add(cancelBtn);

		createNewUserBtn = new JButton("Create New User");
		createNewUserBtn.setBounds(734, 300, 148, 23);
		infoPanel.add(createNewUserBtn);

		saveNewUserBtn= new JButton("Save New User");
		saveNewUserBtn.setBounds(921, 300, 148, 23);
		saveNewUserBtn.setEnabled(false);
		infoPanel.add(saveNewUserBtn);

		mobileLbl = new JLabel("Mobile:");
		mobileLbl.setBounds(26, 255, 46, 14);
		infoPanel.add(mobileLbl);

		mobileTxtBx = new JTextField();
		mobileTxtBx.setBounds(121, 250, 201, 24);
		infoPanel.add(mobileTxtBx);
		mobileTxtBx.setColumns(10);

		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(adminTbl.getTableHeader(), BorderLayout.NORTH);

		rs = getResults();
		adminTbl.setModel(DbUtils.resultSetToTableModel(rs));

		modifyUserBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				if (rowSelected){
					createNewUserBtn.setEnabled(false);
					saveNewUserBtn.setEnabled(false);
					updateBtn.setEnabled(true);
					modifyUserBtn.setEnabled(false);
					enableFields();

				} else{
					JOptionPane.showMessageDialog(null, "You must first select a row");
				}

			}
		});

		updateBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				//showMessage("Updating Consent Received");
				updateUser();
				disableFields();
				clearFields();
				updateBtn.setEnabled(false);
				resetTable();
			}
		});

		cancelBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				if (checkFields()){
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to cancel?","Warning",dialogButton);
					if(dialogResult == JOptionPane.YES_OPTION){
						updateBtn.setEnabled(false);
						modifyUserBtn.setEnabled(false);
						createNewUserBtn.setEnabled(true);
						clearFields();
						disableFields();
						resetTable();

					}
				}
			}
		});

		createNewUserBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				chckbxAccAct.setSelected(true);
				modifyUserBtn.setEnabled(false);
				createNewUserBtn.setEnabled(false);
				saveNewUserBtn.setEnabled(true);
				clearFields();
				enableFields();
			}

		});

		saveNewUserBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				createNewUser();
				saveNewUserBtn.setEnabled(false);
				clearFields();
				disableFields();
				resetTable();
			}
		});

		logOutBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				int dialogButton = JOptionPane.YES_NO_OPTION;
				int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to log out as an admin?","Warning",dialogButton);
				if(dialogResult == JOptionPane.YES_OPTION){
					homescreen.setVisible(true);
					setVisible(false); //Make the screen invisible
					dispose();//Close the login window
				}
			}
		});


		//Display admin details in the text boxes
		adminTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					//rowSelected=true;
					try{
						//Get the customer ID as a paramater to feed into the SQL procedure 
						param = adminTbl.getValueAt(adminTbl.getSelectedRow(), 0).toString();
						displayUserDetails(param);
						rowSelected = true;
						modifyUserBtn.setEnabled(true);

					} catch (IndexOutOfBoundsException e){

					}
				}
			}
		});

		disableFields();

		getContentPane().setLayout(null);
		getContentPane().add(tablePanel); 
		getContentPane().add(infoPanel);

		pack();
	}

	private void displayUserDetails(String parameter) {
		rs2 = getDetails(qryDetails, param);
		try {

			while(rs2.next()){
				String userFName 		= rs2.getString("FirstName");
				String userLName 		= rs2.getString("LastName");
				String userAddress 		= rs2.getString("PostalAddress");
				String userSuburb 		= rs2.getString("PostalSuburb");
				String userPostCode		= rs2.getString("PostalCode");
				String userPhone 		= rs2.getString("Phone");
				String userMobile 		= rs2.getString("Mobile");
				String userEmail 		= rs2.getString("Email");
				String userUserName		= rs2.getString("userName");
				String userCNumber 		= rs2.getString("CouncilNumber");
				String userReeseNum 	= rs2.getString("ReesNumber");
				String userNZHHANum 	= rs2.getString("NZHHA_Number");
				String userActAcc 		= rs2.getString("AccountActive");
				String userRoleTyp		= rs2.getString("RoleType");
				String userRankedNum	= rs2.getString("Ranked");
				String userChBxInstall	= rs2.getString("Install");
				String userChBxSell 	= rs2.getString("Sell");
				String userChBxSiteCheck = rs2.getString("SiteCheck");

				fNameTxtBx.setText(userFName);
				lNameTxtBx.setText(userLName);			 
				phoneTxtBx.setText(userPhone);
				mobileTxtBx.setText(userMobile);
				emailtxtBx.setText(userEmail);
				pAddrtxtBx.setText(userAddress);
				pSuburbtxtbx.setText(userSuburb);
				pAreaCodetxtBx.setText(userPostCode);
				NZHHANumTxtBx.setText(userNZHHANum);
				usertxtBx.setText(userUserName);
				councNumtxtBx.setText(userCNumber);
				reeseNumbtxtBx.setText(userReeseNum);
				rankTxtBx.setText(userRankedNum);
				if (userActAcc.equals("1")){
					chckbxAccAct.setSelected(true);
				}else{
					chckbxAccAct.setSelected(false);
				}

				if (userRoleTyp.equals("Salesperson")){
					roleTypeCmbBx.setSelectedIndex(0);
				}else if (userRoleTyp.equals("Installer")){
					roleTypeCmbBx.setSelectedIndex(1);
				}else if (userRoleTyp.equals("Admin")){
					roleTypeCmbBx.setSelectedIndex(2);
				}else {
					roleTypeCmbBx.setSelectedIndex(3);
				}

				if (userChBxInstall.equals("1")){
					chckbxInstaller.setSelected(true);
				}else{
					chckbxInstaller.setSelected(false);
				}
				if (userChBxSell.equals("1")){
					chckbxSales.setSelected(true);
				}else{
					chckbxSales.setSelected(false);
				}
				if (userChBxSiteCheck.equals("1")){
					chckbxSCheck.setSelected(true);
				}else{
					chckbxSCheck.setSelected(false);
				}




			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

	public JTable getAdminTbl(){
		return adminTbl;
	}

	public ResultSet getResults(){      	
		try
		{
			Connection conn = connecting.CreateConnection(conDeets);
			PreparedStatement st =conn.prepareStatement(qryList);
			results = st.executeQuery();
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}
		return results;       		            
	}

	//Get the results set for the customer details
	public ResultSet getDetails(String qry, String param){      	
		try{
			Connection conn = connecting.CreateConnection(conDeets);
			PreparedStatement st2 =conn.prepareStatement(qry + param);	
			qryResults = st2.executeQuery();
			if (qryResults==null){
				System.out.println("null query");
			}
		}catch(Exception ex){ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}
		return qryResults;       		            
	}


	private void createNewUser() {
		CallableStatement stm = null;
		try {

			String update = "{" + qryCreateUser +"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			stm = conn.prepareCall(update);

			stm.setString(1, getFName());
			stm.setString(2, getLName());
			stm.setString(3, getPhone());
			stm.setString(4, getMobile());
			stm.setString(5, getEmail());
			stm.setString(6, getPAddr());
			stm.setString(7, getPSuburb());
			stm.setString(8, getPAreaCode());
			stm.setString(9, getNZHHANum());
			stm.setString(10, getUsername());
			stm.setString(11, getCouncNum());
			stm.setInt(12, getReeseNum());
			stm.setBoolean(13, getSiteCheck());
			stm.setBoolean(14, getInstall());
			stm.setBoolean(15, getSell());
			stm.setInt(16, getRanked());
			stm.setString(17, getRoleType());
			stm.setBoolean(18, getAccStatus());

			stm.executeUpdate();
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


	protected void updateUser() {

		CallableStatement stm = null;
		try {

			String update = "{" + upUser +"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			stm = conn.prepareCall(update);

			stm.setString(1, param);
			stm.setString(2, getFName());
			stm.setString(3, getLName());
			stm.setString(4, getPhone());
			stm.setString(5, getMobile());
			stm.setString(6, getEmail());
			stm.setString(7, getPAddr());
			stm.setString(8, getPSuburb());
			stm.setString(9, getPAreaCode());
			stm.setString(10, getNZHHANum());
			stm.setString(11, getUsername());
			stm.setString(12, getCouncNum());
			stm.setInt(13, getReeseNum());
			stm.setBoolean(14, getSiteCheck());
			stm.setBoolean(15, getInstall());
			stm.setBoolean(16, getSell());
			stm.setInt(17, getRanked());
			stm.setString(18, getRoleType());
			stm.setBoolean(19, getAccStatus());

			stm.executeUpdate();
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

	public Boolean checkFields(){
		Boolean newData = false;
		if(!fNameTxtBx.getText().equals("") || !lNameTxtBx.getText().equals("") || !phoneTxtBx.getText().equals("") 
				|| !mobileTxtBx.getText().equals("") || !emailtxtBx.getText().equals("") || !pAddrtxtBx.getText().equals("") 
				|| !pSuburbtxtbx.getText().equals("") || !pAreaCodetxtBx.getText().equals("") || /*passtxtBx.getText().equals("")*/
				/*|| !reConnPasstxtBx.getText().equals("") ||*/ NZHHANumTxtBx.getText().equals("")|| !usertxtBx.getText().equals("") 
				|| !councNumtxtBx.getText().equals("") || reeseNumbtxtBx.getText().equals("")){
			newData = true; 
		}else {
			newData = false;
		}
		return newData;
	}

	protected void resetTable() {
		ResultSet rs = getResults();
		adminTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
		spaceHeader(columnWidth,columnModel);
		rowSelected=false;
		param = "";
	}

	private void validateData(){

	}

	private void disableFields(){
		fNameTxtBx.setEditable(false);
		lNameTxtBx.setEditable(false);		 
		phoneTxtBx.setEditable(false);
		mobileTxtBx.setEditable(false);
		emailtxtBx.setEditable(false);
		pAddrtxtBx.setEditable(false);
		pSuburbtxtbx.setEditable(false);
		pAreaCodetxtBx.setEditable(false);
		passtxtBx.setEditable(false);
		reConnPasstxtBx.setEditable(false);
		NZHHANumTxtBx.setEditable(false);
		usertxtBx.setEditable(false);
		councNumtxtBx.setEditable(false);
		reeseNumbtxtBx.setEditable(false);
		chckbxAccAct.setEnabled(false);
		chckbxSales.setEnabled(false);
		chckbxInstaller.setEnabled(false);
		chckbxSCheck.setEnabled(false);
		roleTypeCmbBx.setEnabled(false);
		roleTypeCmbBx.setBackground(Color.LIGHT_GRAY);
		rankTxtBx.setEditable(false);
	}

	private void enableFields(){
		fNameTxtBx.setEditable(true);
		lNameTxtBx.setEditable(true);		 
		phoneTxtBx.setEditable(true);
		mobileTxtBx.setEditable(true);
		emailtxtBx.setEditable(true);
		pAddrtxtBx.setEditable(true);
		pSuburbtxtbx.setEditable(true);
		pAreaCodetxtBx.setEditable(true);
		passtxtBx.setEditable(true);
		reConnPasstxtBx.setEditable(true);
		NZHHANumTxtBx.setEditable(true);
		usertxtBx.setEditable(true);
		councNumtxtBx.setEditable(true);
		reeseNumbtxtBx.setEditable(true);
		chckbxAccAct.setEnabled(true);
		roleTypeCmbBx.setEnabled(true);
		chckbxSales.setEnabled(true);
		chckbxInstaller.setEnabled(true);
		chckbxSCheck.setEnabled(true);
		roleTypeCmbBx.setBackground(Color.WHITE);
		rankTxtBx.setEditable(true);
	}

	private void clearFields(){
		fNameTxtBx.setText("");
		lNameTxtBx.setText("");		 
		phoneTxtBx.setText("");
		mobileTxtBx.setText("");
		emailtxtBx.setText("");
		pAddrtxtBx.setText("");
		pSuburbtxtbx.setText("");
		pAreaCodetxtBx.setText("");
		passtxtBx.setText("");
		reConnPasstxtBx.setText("");
		NZHHANumTxtBx.setText("");
		usertxtBx.setText("");
		councNumtxtBx.setText("");
		reeseNumbtxtBx.setText("");
		chckbxAccAct.setSelected(false);
		chckbxSales.setSelected(false);
		chckbxInstaller.setSelected(false);
		chckbxSCheck.setSelected(false);
		roleTypeCmbBx.setSelectedIndex(0);
		rankTxtBx.setText("");
	}

	private void spaceHeader(int[] widths, TableColumnModel tcm){
		int cols = tcm.getColumnCount();
		for (int i = 0; i < cols; i++){
			tcm.getColumn(i).setPreferredWidth(widths[i]);
		}
	} 

	public String getFName(){
		return fNameTxtBx.getText();
	}
	public String getLName(){
		return lNameTxtBx.getText();
	}
	public String getPhone(){
		return phoneTxtBx.getText();
	}
	public String getMobile(){
		return mobileTxtBx.getText();
	}
	public String getEmail(){
		return emailtxtBx.getText();
	}
	public String getPAddr(){
		return pAddrtxtBx.getText();
	}
	public String getPSuburb(){
		return pSuburbtxtbx.getText();
	}
	public String getPAreaCode(){
		return pAreaCodetxtBx.getText();
	}
	public String getNZHHANum(){
		return NZHHANumTxtBx.getText();
	}
	public String getUsername(){
		return usertxtBx.getText();
	}
	public String getCouncNum(){
		return councNumtxtBx.getText();
	}
	public int getReeseNum(){
		return Integer.parseInt(reeseNumbtxtBx.getText());
	}
	public Boolean getSiteCheck(){
		if (chckbxSCheck.isSelected()){
			return true;
		}else{
			return false;
		}

	}	
	public Boolean getInstall(){
		if (chckbxInstaller.isSelected()){
			return true;
		}else{
			return false;
		}
	}
	public Boolean getSell(){
		if (chckbxSales.isSelected()){
			return true;
		}else{
			return false;
		}
	}
	public int getRanked(){
		return Integer.parseInt(rankTxtBx.getText()); 
	}
	public String getRoleType(){
		return (String) roleTypeCmbBx.getSelectedItem();
	}
	public boolean getAccStatus(){
		boolean active;
		if (chckbxAccAct.isSelected()){
			active = true;
		}else{
			active = false;
		}
		return active;
	}

	public void showMessage(String msg) {
		hs.showMsg(msg);
	}
}



