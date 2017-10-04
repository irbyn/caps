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

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Permit.PermitPane;
//import Permit.PermitsReqPanel;
import net.proteanit.sql.DbUtils;

import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JPasswordField;

public class AdminPanel extends JFrame {

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
	private JTextField nelsonTxtBx;
	private CreateConnection conn;
	private ConnDetails conDets;

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
	private JButton createUserBtn;
	private JButton logOutBtn;
	private JTextField NZHHANumTxtBx;
	private static String user;
	private static String pass;
	private JTextField usertxtBx;
	private JPasswordField passtxtBx;
	private JPasswordField reConnPasstxtBx;
	private JTextField councNumtxtBx;
	private JTextField reeseNumbtxtBx;
	private JCheckBox chckbxAccAct;
	
//---------------------
	
	private ConnDetails conDeets;
	
	private ResultSet results;
	private ResultSet qryResults;
	private int tabIndex = 0;
	//private PermitsReqPanel permitReq;
	
	// Stored procedures to fill tables (Triggered by tab selection)
	private String procedure = new String("EXEC AWS_WCH_DB.dbo.a_userList");// procedure to call in the db

	

	public AdminPanel(String User, String Pass){

		user = User;
		pass = Pass;
		
		//PASS THE LOGIN DETAILS TO Class connectionDetails
		ConnDetails conDeets = new ConnDetails(user, pass);

		//permitReq = new PermitsReqPanel(conDeets, this);
		//JTable[] tablez = new JTable[]{permitReq.getPermitsTbl()};//ued to feed in the different table so they can be updated. 
		
		getContentPane().setLayout(null);
		setPreferredSize(new Dimension(1100, 700));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		conDets = conDeets;
		connecting = new CreateConnection();

		model1 = new DefaultTableModel();  
		model1.setRowCount(0);
		permitsTbl = new JTable(model1);
		permitsTbl.setPreferredSize(new Dimension(0, 300));
		permitsTbl.setAutoCreateRowSorter(true);

		JScrollPane scrollPane = new JScrollPane(permitsTbl);

		header= permitsTbl.getTableHeader();
		columnModel = header.getColumnModel();
		getContentPane().add(header); 

		tablePanel = new JPanel();
		tablePanel.setBounds(29, 44, 1025, 279);  //setPreferredSize(new Dimension(0, 300));      
		tablePanel.setLayout(new BorderLayout());

		infoPanel = new JPanel();
		infoPanel.setBounds(10, 334, 1100, 326);  //setPreferredSize(new Dimension(0, 300));
		infoPanel.setLayout(null);

		getContentPane().setLayout(null);
		getContentPane().add(tablePanel); 
		getContentPane().add(infoPanel);

		userlbl = new JLabel("Username:");
		userlbl.setBounds(359, 177, 73, 14);
		infoPanel.add(userlbl);

		JLabel lblAbilities = new JLabel("Abilities:");
		lblAbilities.setBounds(734, 227, 63, 14);
		infoPanel.add(lblAbilities);

		JCheckBox chckbxSCheck = new JCheckBox("Site Check");
		chckbxSCheck.setBounds(830, 223, 85, 23);
		infoPanel.add(chckbxSCheck);

		JCheckBox chckbxInstaller = new JCheckBox("Installer");
		chckbxInstaller.setBounds(992, 223, 73, 23);
		infoPanel.add(chckbxInstaller);

		JCheckBox chckbxSales = new JCheckBox("Sales");
		chckbxSales.setBounds(917, 223, 73, 23);
		infoPanel.add(chckbxSales);

		JLabel lblNZHHANumb = new JLabel("NZHHA Number:");
		lblNZHHANumb.setBounds(734, 177, 109, 14);
		infoPanel.add(lblNZHHANumb);

		//Personal Customer Information
		contactlbl = new JLabel("User Information");
		contactlbl.setBounds(26, 22, 201, 20);
		infoPanel.add(contactlbl);
		contactlbl.setFont(new Font("Arial", Font.BOLD, 20));

		fNameLbl = new JLabel("First Name:");
		fNameLbl.setBounds(26, 127, 85, 14);
		infoPanel.add(fNameLbl);

		fNameTxtBx = new JTextField();
		fNameTxtBx.setBounds(121, 127, 201, 24);
		infoPanel.add(fNameTxtBx);
		fNameTxtBx.setColumns(10);

		lNameLbl = new JLabel("Last Name:");
		lNameLbl.setBounds(26, 177, 85, 15);
		infoPanel.add(lNameLbl);

		lNameTxtBx = new JTextField();
		lNameTxtBx.setBounds(121, 177, 201, 24);
		infoPanel.add(lNameTxtBx);
		lNameTxtBx.setColumns(10);

		lblPhone = new JLabel("Phone Number:");
		lblPhone.setBounds(26, 227, 109, 15);
		infoPanel.add(lblPhone);

		phoneTxtBx = new JTextField();
		phoneTxtBx.setBounds(121, 227, 201, 24);
		infoPanel.add(phoneTxtBx);
		phoneTxtBx.setColumns(10);

		emaillbl = new JLabel("Email:");
		emaillbl.setBounds(25, 277, 46, 15);
		infoPanel.add(emaillbl);

		emailtxtBx = new JTextField();
		emailtxtBx.setBounds(121, 277, 201, 24);
		infoPanel.add(emailtxtBx);
		emailtxtBx.setColumns(10);

		pAddrlbl = new JLabel("Postal Street Address:");
		pAddrlbl.setBounds(359, 28, 134, 15);
		infoPanel.add(pAddrlbl);

		pAddrtxtBx = new JTextField();
		pAddrtxtBx.setBounds(496, 22, 201, 24);
		infoPanel.add(pAddrtxtBx);
		pAddrtxtBx.setColumns(10);

		pSuburblbl = new JLabel("Postal Suburb:");
		pSuburblbl.setBounds(359, 74, 118, 15);
		infoPanel.add(pSuburblbl);

		pSuburbtxtbx = new JTextField();
		pSuburbtxtbx.setBounds(496, 72, 201, 24);
		infoPanel.add(pSuburbtxtbx);
		pSuburbtxtbx.setColumns(10);

		pAreaCodelbl = new JLabel("Post Code:");
		pAreaCodelbl.setBounds(359, 127, 118, 15);
		infoPanel.add(pAreaCodelbl);

		pAreaCodetxtBx = new JTextField();
		pAreaCodetxtBx.setBounds(496, 122, 201, 24);
		infoPanel.add(pAreaCodetxtBx);
		pAreaCodetxtBx.setColumns(10);
		
		NZHHANumTxtBx = new JTextField();
		NZHHANumTxtBx.setBounds(853, 172, 201, 24);
		infoPanel.add(NZHHANumTxtBx);
		NZHHANumTxtBx.setColumns(10);
		
		passLbl = new JLabel("Password:");
		passLbl.setBounds(359, 227, 73, 14);
		infoPanel.add(passLbl);
		
		usertxtBx = new JTextField();
		usertxtBx.setBounds(496, 172, 201, 24);
		infoPanel.add(usertxtBx);
		usertxtBx.setColumns(10);
		
		passtxtBx = new JPasswordField();
		passtxtBx.setBounds(496, 222, 201, 24);
		infoPanel.add(passtxtBx);
		
		reConnPasstxtBx = new JPasswordField();
		reConnPasstxtBx.setBounds(496, 272, 201, 24);
		infoPanel.add(reConnPasstxtBx);
		
		rePassLbl = new JLabel("Re Confirm Password:");
		rePassLbl.setBounds(359, 277, 134, 14);
		infoPanel.add(rePassLbl);
		
		JLabel councNumblbl = new JLabel("Council Number:");
		councNumblbl.setBounds(734, 78, 95, 14);
		infoPanel.add(councNumblbl);
		
		councNumtxtBx = new JTextField();
		councNumtxtBx.setBounds(853, 73, 201, 24);
		infoPanel.add(councNumtxtBx);
		councNumtxtBx.setColumns(10);
		
	    reesNumLbl = new JLabel("Rees Number:");
		reesNumLbl.setBounds(734, 127, 108, 14);
		infoPanel.add(reesNumLbl);
		
		reeseNumbtxtBx = new JTextField();
		reeseNumbtxtBx.setColumns(10);
		reeseNumbtxtBx.setBounds(853, 122, 201, 24);
		infoPanel.add(reeseNumbtxtBx);
		
		chckbxAccAct = new JCheckBox("Active Account");
		chckbxAccAct.setBounds(26, 70, 134, 23);
		infoPanel.add(chckbxAccAct);
		
		modifyUserBtn = new JButton("Modify");
		modifyUserBtn.setBounds(718, 273, 102, 23);
		infoPanel.add(modifyUserBtn);
		
		cancelBtn = new JButton("Cancel");
		cancelBtn.setBounds(830, 273, 102, 23);
		infoPanel.add(cancelBtn);
		
		createUserBtn= new JButton("Create New User");
		createUserBtn.setBounds(942, 273, 127, 23);
		infoPanel.add(createUserBtn);

		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(permitsTbl.getTableHeader(), BorderLayout.NORTH);
		
		//Adding JPanels to the 
		JTabbedPane adminP = new JTabbedPane();
		adminP.setPreferredSize(new Dimension(1070, 610));
		
		adminP.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (e.getSource() instanceof JTabbedPane) {
                	
                    JTabbedPane pane = (JTabbedPane) e.getSource();
                    tabIndex = pane.getSelectedIndex();
                    
                    getResults(tabIndex, conDeets); 
             //       ResultSet r1 = results;
                    
                   /* // add ResultSet into Selected Tab JTable.
                    tablez[tabIndex].setModel(DbUtils.resultSetToTableModel(results));               
                    TableColumnModel tcm = tablez[tabIndex].getColumnModel();
                     int cols = tcm.getColumnCount();

                     if (cols == 6){
                    	 int[] colWidths = new int[]{20, 150, 150, 100, 100, 100}; 
                    	 spaceHeader(colWidths, tcm);
                     } else if (cols == 7){
                    	 int[] colWidths = new int[]{20, 150, 150, 100, 100, 100, 100};   
                    	 spaceHeader(colWidths, tcm);                         
                     } else if (cols == 9){
                        	 int[] colWidths = new int[]{30, 100, 120, 80, 40, 40, 40, 40, 40};   
                        	 spaceHeader(colWidths, tcm);
                     }else {
                    	 int[] colWidths = new int[]{30, 100, 120, 80, 30, 30, 40, 40, 40, 30, 30};    
                    	 spaceHeader(colWidths, tcm);
                     }*/
                }
            }
        });   		
    

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

	public JTable getPermitsTbl(){
		return permitsTbl;
	}
	
	 public ResultSet getResults(int ind, ConnDetails connDeets){      	
     	
         try
	        {
	        	Connection conn = connecting.CreateConnection(connDeets);
	        	//PreparedStatement st =conn.prepareStatement(procedure[ind]);	//ind]);
	        	//results = st.executeQuery();
	        	if (results==null){
	        		getResults(0, conDeets);
	        	}
	        }
	        catch(Exception ex)
	        { 
	        JOptionPane.showMessageDialog(null, ex.toString());
	        }
     		return results;       		            
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
