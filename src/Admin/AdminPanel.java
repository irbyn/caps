package Admin;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

public class AdminPanel extends JFrame {

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

	public AdminPanel(){
		getContentPane().setLayout(null);
		setPreferredSize(new Dimension(1100, 700));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel lblfName = new JLabel("First Name:");
		lblfName.setBounds(10, 324, 85, 14);
		getContentPane().add(lblfName);
		
		JLabel lblLName = new JLabel("Last Name:");
		lblLName.setBounds(10, 349, 85, 14);
		getContentPane().add(lblLName);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(10, 374, 73, 14);
		getContentPane().add(lblUsername);
		
		JLabel lblRole = new JLabel("Role:");
		lblRole.setBounds(10, 399, 46, 14);
		getContentPane().add(lblRole);
		
		JCheckBox chckbxSCheck = new JCheckBox("Site Check");
		chckbxSCheck.setBounds(104, 426, 85, 23);
		getContentPane().add(chckbxSCheck);
		
		JCheckBox chckbxInstaller = new JCheckBox("Installer");
		chckbxInstaller.setBounds(205, 426, 73, 23);
		getContentPane().add(chckbxInstaller);
		
		JCheckBox chckbxSales = new JCheckBox("Sales");
		chckbxSales.setBounds(304, 426, 62, 23);
		getContentPane().add(chckbxSales);
		
		JLabel lblNZHHANumb = new JLabel("NZHHA Number:");
		lblNZHHANumb.setBounds(10, 468, 85, 14);
		getContentPane().add(lblNZHHANumb);
		

		  	
		  	//Personal Customer Information
		  	contactlbl = new JLabel("Contact Information");
		  	contactlbl.setFont(new Font("Arial", Font.BOLD, 20));
		  	contactlbl.setBounds(263, 10, 201, 20);
		  	add(contactlbl);
		  	
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
		  	
		  	lblPhone = new JLabel("Phone Number:");
		  	lblPhone.setBounds(346, 120, 102, 15);
		  	add(lblPhone);
		  	
		  	phoneTxtBx = new JTextField();
		  	phoneTxtBx.setColumns(10);
		  	phoneTxtBx.setBounds(492, 120, 302, 20);
		  	add(phoneTxtBx);
		  	
		  	emaillbl = new JLabel("Email:");
		  	emaillbl.setBounds(346, 160, 102, 15);
		  	add(emaillbl);
		  	
		  	emailtxtbx = new JTextField();
		  	emailtxtbx.setColumns(10);
		  	emailtxtbx.setBounds(492, 160, 302, 20);
		  	add(emailtxtbx);
		  		  	
		  	//Postal Address Information
		  	postAddrlbl = new JLabel("Postal Address");
		  	postAddrlbl.setFont(new Font("Arial", Font.BOLD, 20));
		  	postAddrlbl.setBounds(263, 200, 201, 20);
		  	add(postAddrlbl);
		  	
		  	pAddrlbl = new JLabel("Postal Street Address:");
		  	pAddrlbl.setBounds(346, 231, 173, 15);
		  	add(pAddrlbl);
		  	
		  	pAddrtxtbx = new JTextField();
		  	pAddrtxtbx.setColumns(10);
		  	pAddrtxtbx.setBounds(492, 228, 302, 20);
		  	add(pAddrtxtbx);
		  	
		  	pSuburblbl = new JLabel("Postal Suburb:");
		  	pSuburblbl.setBounds(346, 272, 173, 15);
		  	add(pSuburblbl);
		  	
		  	pSuburbtxtbx = new JTextField();
		  	pSuburbtxtbx.setColumns(10);
		  	pSuburbtxtbx.setBounds(492, 269, 302, 20);
		  	add(pSuburbtxtbx);
		  	
		  	pAreaCodelbl = new JLabel("Post Code");
		  	pAreaCodelbl.setBounds(346, 310, 173, 15);
		  	add(pAreaCodelbl);
		  	
		  	pAreaCodetxtbx = new JTextField();
		  	pAreaCodetxtbx.setColumns(10);
		  	pAreaCodetxtbx.setBounds(492, 307, 302, 20);
		  	add(pAreaCodetxtbx);
		  	
		  	//Site Address Information
		  	
		  	siteAddrLbl = new JLabel("Site Address");
		  	siteAddrLbl.setBounds(263, 338, 201, 20);
		  	siteAddrLbl.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
		  	add(siteAddrLbl);
		  	
		  	pAddChbx = new JCheckBox("Postal Address is Site Address");
		  	pAddChbx.setBounds(346, 370, 222, 23);
		  	add(pAddChbx);
		  	
		  	sAddrlbl = new JLabel("Site Street Address:");
		  	sAddrlbl.setBounds(346, 414, 135, 15);
		  	add(sAddrlbl);
		  	
		  	sAddrtxtbx = new JTextField();
		  	sAddrtxtbx.setColumns(10);
		  	sAddrtxtbx.setBounds(492, 411, 302, 20);
		  	add(sAddrtxtbx);
	  	
		  	sSuburblbl = new JLabel("Site Suburb:");
		  	sSuburblbl.setBounds(346, 453, 135, 15);
		  	add(sSuburblbl);
		  	
		  	sSuburbtxtbx = new JTextField();
		  	sSuburbtxtbx.setColumns(10);
		  	sSuburbtxtbx.setBounds(492, 450, 302, 20);
		  	add(sSuburbtxtbx);
	  	
        
	}
}
