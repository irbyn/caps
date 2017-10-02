package Admin;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumnModel;

import Main.ConnDetails;
import Main.Homescreen;

//import Admin.AdminPanel;
/*import Permit.CCCToClientPanel;
import Permit.CCCToCounPanel;
import Permit.PermitsReqPanel;
import Permit.ProdStatementPanel;
import Permit.RecvPermitPanel;*/

import net.proteanit.sql.DbUtils;
import Admin.AdminPanel;
import DB_Comms.CreateConnection;

import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;

public class AdminLogin extends JFrame{
	private ConnDetails conDeets;
	private Homescreen homescreen;
	private AdminPanel adminPanel;
	private JLabel lbllogIn;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JTextField txtBxUsername;
	private JPasswordField txtBxPassword;	
	private JButton loginBtn;
	private JButton cancelBtn;
	private JPanel logPanel;
	private JTabbedPane adminP;
	// Stored procedures to fill tables (Triggered by tab selection)
	private String[] procedure = new String[]{	"EXEC AWS_WCH_DB.dbo.a_adminPanel"}; // procedure[0]

	public AdminLogin() {

/*		//---------------DOWN------------------------------------
		connecting = new CreateConnection();

		//Adding Jpanels to the SAles panel area 
		adminP = new JTabbedPane();
		adminP.setPreferredSize(new Dimension(1070, 610));

		adminPanel = new AdminPanel(conDeets, adminPanel);
		//permitRecv = new RecvPermitPanel(conDeets, this);
		
		JTable[] tablez = new JTable[]{adminPanel.getPermitsTbl()}; 				

		adminP.addTab("Admin", adminPanel);
		add(adminP); 

		//		getResults(0);  

		adminP.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() instanceof JTabbedPane) {

					JTabbedPane pane = (JTabbedPane) e.getSource();
					tabIndex = pane.getSelectedIndex();

					getResults(tabIndex, conDeets); 
					//       ResultSet r1 = results;

					// add ResultSet into Selected Tab JTable.
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
					}
				}
			}
		}); */  		
	//-----------^^^^^^^^^^^^^^^^^^-----------------------------------------------

	// setting up JFrame
	getContentPane().setLayout(null);
	setPreferredSize(new Dimension(400, 250));
	setResizable(false);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


	// creating main button JPanel (blue)
	logPanel = new JPanel();
	logPanel.setBounds(0, 0, 392, 221);
	logPanel.setLayout(null);
	logPanel.setPreferredSize(new Dimension(500, 250));
	getContentPane().add(logPanel);

	//Personal Customer Information
	lbllogIn = new JLabel("Admmin Log in");
	lbllogIn.setBounds(36, 25, 162, 28);
	logPanel.add(lbllogIn);
	lbllogIn.setFont(new Font("Arial", Font.BOLD, 20));

	lblUsername = new JLabel("Username:");
	lblUsername.setBounds(98, 64, 74, 14);
	logPanel.add(lblUsername);

	lblPassword = new JLabel("Password:");
	lblPassword.setBounds(98, 113, 86, 14);
	logPanel.add(lblPassword);

	txtBxUsername = new JTextField("Khgv92367hdkfug9");
	txtBxUsername.setBounds(194, 61, 123, 20);
	txtBxUsername.setColumns(10);
	logPanel.add(txtBxUsername);

	txtBxPassword = new JPasswordField("Locei02h84b5KJUVaW");
	txtBxPassword.setBounds(194, 110, 123, 20);
	logPanel.add(txtBxPassword);

	loginBtn = new JButton("Log In");
	loginBtn.setBounds(231, 164, 86, 24);
	logPanel.add(loginBtn);

	cancelBtn = new JButton("Cancel");              
	cancelBtn.setBounds(98, 164, 86, 24);
	logPanel.add(cancelBtn);

	//Action listeners for each button
	loginBtn.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
			String user=txtBxUsername.getText();
    		String pwd = new String(txtBxPassword.getPassword());
			adminPanel = new AdminPanel(user, pwd);
			adminPanel.setVisible(true);				
			setVisible(false); //Make the screen invisible
			//dispose();//Close the login window
		}
	});

	cancelBtn.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			//homescreen = new Homescreen();
			homescreen.setVisible(true);
			setVisible(false); //Make the screen invisible
			//dispose();//Close the login window
		}
	});      

	//Load the frame
	pack();
	}



//---------------------------------------------------      

/*private void spaceHeader(int[] widths, TableColumnModel tcm){
	int cols = tcm.getColumnCount();
	for (int i = 0; i < cols; i++){
		tcm.getColumn(i).setPreferredWidth(widths[i]);
	}
}


public ResultSet getResults(int ind, ConnDetails connDeets){      	

	try
	{
		Connection conn = connecting.CreateConnection(connDeets);
		PreparedStatement st =conn.prepareStatement(procedure[ind]);	//ind]);
		results = st.executeQuery();
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

public ResultSet getDetails(String qry, String param, ConnDetails connDeets){      	

	try
	{
		Connection conn = connecting.CreateConnection(connDeets);
		PreparedStatement st2 =conn.prepareStatement(qry + param);	
		qryResults = st2.executeQuery();
		if (qryResults==null){
			System.out.println("null query");
		}
	}
	catch(Exception ex)
	{ 
		JOptionPane.showMessageDialog(null, ex.toString());
	}
	return qryResults;       		            
}




*/
//-----------^^^^^^^^^^^^^^^^^^-----------------------------------------------


}