package Sales;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JTextField;

import java.awt.Font;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

class CustomerPanel extends JPanel {
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
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;

	  public CustomerPanel() {
	  	setLayout(null);
	  	
	  	//Personal Customer Information
	  	JLabel contactlbl = new JLabel("Contact Information");
	  	contactlbl.setFont(new Font("Arial", Font.BOLD, 20));
	  	contactlbl.setBounds(263, 10, 201, 20);
	  	add(contactlbl);
	  	
	  	JLabel fNameLbl = new JLabel("First Name:");
	  	fNameLbl.setBounds(346, 40, 102, 14);
	  	add(fNameLbl);
	  	
	  	fNameTxtBx = new JTextField();
	  	fNameTxtBx.setBounds(492, 40, 302, 20);
	  	add(fNameTxtBx);
	  	fNameTxtBx.setColumns(10);
	  	
	  	JLabel lNameLbl = new JLabel("Last Name:");
	  	lNameLbl.setBounds(346, 80, 102, 15);
	  	add(lNameLbl);
	  	
	  	lNameTxtBx = new JTextField();
	  	lNameTxtBx.setColumns(10);
	  	lNameTxtBx.setBounds(492, 80, 302, 20);
	  	add(lNameTxtBx);
	  	
	  	JLabel label = new JLabel("Phone Number:");
	  	label.setBounds(346, 120, 102, 15);
	  	add(label);
	  	
	  	phoneTxtBx = new JTextField();
	  	phoneTxtBx.setColumns(10);
	  	phoneTxtBx.setBounds(492, 120, 302, 20);
	  	add(phoneTxtBx);
	  	
	  	JLabel emaillbl = new JLabel("Email:");
	  	emaillbl.setBounds(346, 160, 102, 15);
	  	add(emaillbl);
	  	
	  	emailtxtbx = new JTextField();
	  	emailtxtbx.setColumns(10);
	  	emailtxtbx.setBounds(492, 160, 302, 20);
	  	add(emailtxtbx);
	  		  	
	  	//Postal Address Information
	  	JLabel postAddrlbl = new JLabel("Postal Address");
	  	postAddrlbl.setFont(new Font("Arial", Font.BOLD, 20));
	  	postAddrlbl.setBounds(263, 200, 201, 20);
	  	add(postAddrlbl);
	  	
	  	JLabel pAddrlbl = new JLabel("Postal Street Address:");
	  	pAddrlbl.setBounds(346, 231, 173, 15);
	  	add(pAddrlbl);
	  	
	  	pAddrtxtbx = new JTextField();
	  	pAddrtxtbx.setColumns(10);
	  	pAddrtxtbx.setBounds(492, 228, 302, 20);
	  	add(pAddrtxtbx);
	  	
	  	JLabel pSuburblbl = new JLabel("Postal Suburb:");
	  	pSuburblbl.setBounds(346, 272, 173, 15);
	  	add(pSuburblbl);
	  	
	  	pSuburbtxtbx = new JTextField();
	  	pSuburbtxtbx.setColumns(10);
	  	pSuburbtxtbx.setBounds(492, 269, 302, 20);
	  	add(pSuburbtxtbx);
	  	
	  	JLabel pAreaCodelbl = new JLabel("Post Code");
	  	pAreaCodelbl.setBounds(346, 310, 173, 15);
	  	add(pAreaCodelbl);
	  	
	  	pAreaCodetxtbx = new JTextField();
	  	pAreaCodetxtbx.setColumns(10);
	  	pAreaCodetxtbx.setBounds(492, 307, 302, 20);
	  	add(pAreaCodetxtbx);
	  	
	  	//Site Address Information
	  	
	  	JLabel siteAddrLbl = new JLabel("Site Address");
	  	siteAddrLbl.setBounds(263, 338, 201, 20);
	  	siteAddrLbl.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
	  	add(siteAddrLbl);
	  	
	  	JCheckBox pAddChbx = new JCheckBox("Postal Address is Site Address");
	  	pAddChbx.setBounds(346, 370, 222, 23);
	  	add(pAddChbx);
	  	
	  	JLabel sAddrlbl = new JLabel("Site Street Address:");
	  	sAddrlbl.setBounds(346, 414, 135, 15);
	  	add(sAddrlbl);
	  	
	  	sAddrtxtbx = new JTextField();
	  	sAddrtxtbx.setColumns(10);
	  	sAddrtxtbx.setBounds(492, 411, 302, 20);
	  	add(sAddrtxtbx);
  	
	  	JLabel sSuburblbl = new JLabel("Site Suburb:");
	  	sSuburblbl.setBounds(346, 453, 135, 15);
	  	add(sSuburblbl);
	  	
	  	sSuburbtxtbx = new JTextField();
	  	sSuburbtxtbx.setColumns(10);
	  	sSuburbtxtbx.setBounds(492, 450, 302, 20);
	  	add(sSuburbtxtbx);
	  	
	  	JButton searchCustBtn = new JButton("Search for Existing Customer");
	  	searchCustBtn.setBounds(263, 489, 210, 25);
	  	add(searchCustBtn);
	  	
	  	JButton cancelBtn = new JButton("Cancel");
	  	cancelBtn.setBounds(537, 489, 135, 25);
	  	add(cancelBtn);
	  	
	  	JButton createCustBtn = new JButton("Create Customer");
	  	createCustBtn.setBounds(735, 489, 135, 25);
	  	add(createCustBtn);
	  	
	  }
}