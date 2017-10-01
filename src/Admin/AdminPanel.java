package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Permit.PermitPane;

import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

public class AdminPanel extends JFrame {

	//------------------JTable----------------------------------------------
	private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
	private String result3 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
	private String param = "";  

	private String dbURL = "";
	private ResultSet rs;
	private CreateConnection connecting;
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable permitsTbl;
	private DefaultTableModel model1;
	private JTextArea detailsTxtArea;
	private JLabel nelsonLbl;
	private JTextField nelsonTxtBx;
	private JButton prntConsentBtn; 
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	private CreateConnection conn;
	private ConnDetails conDets;
	//------------------JTable----------------------------------------------

	private JTextField txtBxUsername;
	private JTextField txtBxNZHHANum;
	private JTextField fNameTxtBx;
	private JTextField lNameTxtBx;
	private JTextField phoneTxtBx;
	private JTextField emailtxtbx;
	private JTextField sAddrtxtbx;
	private JTextField sSuburbtxtbx;
	private JTextField sAreaCodetxtbx;
	private JTextField pAddrtxtbx;
	private JTextField pSuburbtxtbx;
	private JTextField pAreaCodetxtbx;
	private JLabel contactlbl;
	private JLabel fNameLbl;
	private JLabel lNameLbl;
	private JLabel lblPhone;
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
	private JButton logOutBtn;
	private JTextField NZHHANumTxtBx;
	private static String user;
	private static String pass;

	public AdminPanel(String User, String Pass){

		user = User;
		pass = Pass;
		
		//PASS THE LOGIN DETAILS TO Class connectionDetails
		ConnDetails conDeets = new ConnDetails(user, pass);

		
		getContentPane().setLayout(null);
		setPreferredSize(new Dimension(1100, 700));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



		//-----JTable----------------------------------------------------------------


		//public CCCApprovedPanel(ConnDetails conDeets, PermitPane pp) {

		conDets = conDeets;
		connecting = new CreateConnection();

		model1 = new DefaultTableModel();  
		model1.setRowCount(0);
		permitsTbl = new JTable(model1);
		permitsTbl.setPreferredSize(new Dimension(0, 250));
		permitsTbl.setAutoCreateRowSorter(true);

		JScrollPane scrollPane = new JScrollPane(permitsTbl);

		header= permitsTbl.getTableHeader();
		columnModel = header.getColumnModel();
		getContentPane().add(header); 

		tablePanel = new JPanel();
		tablePanel.setBounds(29, 44, 1025, 236);  //setPreferredSize(new Dimension(0, 300));      
		tablePanel.setLayout(new BorderLayout());

		infoPanel = new JPanel();
		infoPanel.setBounds(0, 280, 1100, 380);  //setPreferredSize(new Dimension(0, 300));
		infoPanel.setLayout(null);


		getContentPane().setLayout(null);
		getContentPane().add(tablePanel); 
		getContentPane().add(infoPanel);
		//-----JTable^^^^^^^^^^^^^^^^^^^^----------------------------------------------------------------

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(549, 199, 73, 14);
		infoPanel.add(lblUsername);

		JLabel lblRole = new JLabel("Role:");
		lblRole.setBounds(10, 298, 46, 14);
		infoPanel.add(lblRole);

		JCheckBox chckbxSCheck = new JCheckBox("Site Check");
		chckbxSCheck.setBounds(91, 294, 85, 23);
		infoPanel.add(chckbxSCheck);

		JCheckBox chckbxInstaller = new JCheckBox("Installer");
		chckbxInstaller.setBounds(242, 294, 73, 23);
		infoPanel.add(chckbxInstaller);

		JCheckBox chckbxSales = new JCheckBox("Sales");
		chckbxSales.setBounds(178, 294, 62, 23);
		infoPanel.add(chckbxSales);

		JLabel lblNZHHANumb = new JLabel("NZHHA Number:");
		lblNZHHANumb.setBounds(10, 250, 85, 14);
		infoPanel.add(lblNZHHANumb);

		//Personal Customer Information
		contactlbl = new JLabel("Contact Information");
		contactlbl.setBounds(23, 30, 201, 20);
		infoPanel.add(contactlbl);
		contactlbl.setFont(new Font("Arial", Font.BOLD, 20));

		fNameLbl = new JLabel("First Name:");
		fNameLbl.setBounds(10, 77, 79, 14);
		infoPanel.add(fNameLbl);

		fNameTxtBx = new JTextField();
		fNameTxtBx.setBounds(105, 77, 201, 20);
		infoPanel.add(fNameTxtBx);
		fNameTxtBx.setColumns(10);

		lNameLbl = new JLabel("Last Name:");
		lNameLbl.setBounds(10, 120, 102, 15);
		infoPanel.add(lNameLbl);

		lNameTxtBx = new JTextField();
		lNameTxtBx.setBounds(105, 120, 201, 20);
		infoPanel.add(lNameTxtBx);
		lNameTxtBx.setColumns(10);

		lblPhone = new JLabel("Phone Number:");
		lblPhone.setBounds(10, 148, 85, 15);
		infoPanel.add(lblPhone);

		phoneTxtBx = new JTextField();
		phoneTxtBx.setBounds(105, 151, 201, 20);
		infoPanel.add(phoneTxtBx);
		phoneTxtBx.setColumns(10);

		emaillbl = new JLabel("Email:");
		emaillbl.setBounds(10, 184, 79, 15);
		infoPanel.add(emaillbl);

		emailtxtbx = new JTextField();
		emailtxtbx.setBounds(105, 199, 201, 20);
		infoPanel.add(emailtxtbx);
		emailtxtbx.setColumns(10);

		//Postal Address Information
		postAddrlbl = new JLabel("Postal Address");
		postAddrlbl.setBounds(357, 28, 201, 20);
		infoPanel.add(postAddrlbl);
		postAddrlbl.setFont(new Font("Arial", Font.BOLD, 20));

		pAddrlbl = new JLabel("Postal Street Address:");
		pAddrlbl.setBounds(357, 75, 118, 15);
		infoPanel.add(pAddrlbl);

		pAddrtxtbx = new JTextField();
		pAddrtxtbx.setBounds(486, 72, 201, 20);
		infoPanel.add(pAddrtxtbx);
		pAddrtxtbx.setColumns(10);

		pSuburblbl = new JLabel("Postal Suburb:");
		pSuburblbl.setBounds(357, 118, 118, 15);
		infoPanel.add(pSuburblbl);

		pSuburbtxtbx = new JTextField();
		pSuburbtxtbx.setBounds(486, 115, 201, 20);
		infoPanel.add(pSuburbtxtbx);
		pSuburbtxtbx.setColumns(10);

		pAreaCodelbl = new JLabel("Post Code");
		pAreaCodelbl.setBounds(357, 146, 118, 15);
		infoPanel.add(pAreaCodelbl);

		pAreaCodetxtbx = new JTextField();
		pAreaCodetxtbx.setBounds(486, 143, 201, 20);
		infoPanel.add(pAreaCodetxtbx);
		pAreaCodetxtbx.setColumns(10);

		//Site Address Information

		siteAddrLbl = new JLabel("Site Address");
		siteAddrLbl.setBounds(743, 30, 201, 20);
		infoPanel.add(siteAddrLbl);
		siteAddrLbl.setFont(new java.awt.Font("Arial", Font.BOLD, 20));

		pAddChbx = new JCheckBox("Postal Address is Site Address");
		pAddChbx.setBounds(743, 73, 222, 23);
		infoPanel.add(pAddChbx);

		sAddrlbl = new JLabel("Site Street Address:");
		sAddrlbl.setBounds(743, 120, 135, 15);
		infoPanel.add(sAddrlbl);

		sAddrtxtbx = new JTextField();
		sAddrtxtbx.setBounds(860, 117, 201, 20);
		infoPanel.add(sAddrtxtbx);
		sAddrtxtbx.setColumns(10);

		sSuburblbl = new JLabel("Site Suburb:");
		sSuburblbl.setBounds(743, 154, 135, 15);
		infoPanel.add(sSuburblbl);

		sSuburbtxtbx = new JTextField();
		sSuburbtxtbx.setBounds(860, 151, 201, 20);
		infoPanel.add(sSuburbtxtbx);
		sSuburbtxtbx.setColumns(10);
		
		NZHHANumTxtBx = new JTextField();
		NZHHANumTxtBx.setBounds(105, 250, 201, 20);
		infoPanel.add(NZHHANumTxtBx);
		NZHHANumTxtBx.setColumns(10);

		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(permitsTbl.getTableHeader(), BorderLayout.NORTH);        
		//	  	this.add(infoPanel, BorderLayout.SOUTH);

		permitsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					try{
						param = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 0).toString();
						updatePermitDetails(param);
					} catch (IndexOutOfBoundsException e){
						//
					}
				}
			}
		});

		logOutBtn = new JButton("Log Out ");
		logOutBtn.setBounds(982, 12, 102, 24);
		getContentPane().add(logOutBtn);

		pack();

	}

	//-----JTable----------------------------------------------------------------

	public JTable getPermitsTbl(){
		return permitsTbl;
	}


	private void updatePermitDetails(String parameter) {
		try
		{
			Connection conn = connecting.CreateConnection(conDets);
			PreparedStatement st2 =conn.prepareStatement(result2 + parameter);
			ResultSet rs2 = st2.executeQuery();

			//Retrieve by column name
			while(rs2.next()){

				detailsTxtArea.setText("\n INVOICE:\t" + param + "\n");
				detailsTxtArea.append( " CLIENT:\t" + rs2.getString("CustomerName") + "\n\n");
				detailsTxtArea.append( " SITE:\t" + rs2.getString("StreetAddress") + "\n");
				detailsTxtArea.append( "\t" + rs2.getString("Suburb") + "\n\n");
				detailsTxtArea.append( " POSTAL:\t" + rs2.getString("CustomerAddress") + "\n");               
			}


			PreparedStatement st3 =conn.prepareStatement(result3 + parameter);

			ResultSet rs3 = null;
			rs3 = st3.executeQuery();

			while(rs3.next()){

				if (!rs3.getString("FireID").equals(parameter)){
					//Retrieve by column name

					nelsonTxtBx.setText("");

					nelsonTxtBx.setText(rs3.getString("Nelson"));
				}
			} 

			conn.close();	
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}	  	
	}	
}
