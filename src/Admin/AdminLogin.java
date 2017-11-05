package Admin;

//Description: Admin Panel allows for only and admin user to log into the admin panel. 

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import Main.Homescreen;
import Admin.AdminPanel;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;

public class AdminLogin extends JFrame{
	private String getuserPass = "call AWS_WCH_DB.dbo.a_DeskAdminLoginDetails";
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
	private String md5Hash;
	private ResultSet rs; 

	public AdminLogin(Homescreen hs) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Homescreen.class.getResource("/wfs-logo-16.png")));

		this.homescreen = hs;

		// setting up JFrame
		getContentPane().setLayout(null);
		setPreferredSize(new Dimension(400, 250));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("WorkFlow Solutions");

		// creating main button JPanel (blue)
		logPanel = new JPanel();
		logPanel.setBounds(0, 0, 392, 221);
		logPanel.setLayout(null);
		logPanel.setPreferredSize(new Dimension(500, 250));
		getContentPane().add(logPanel);

		//Personal Customer Information
		lbllogIn = new JLabel("Admin Login");
		lbllogIn.setBounds(36, 25, 162, 28);
		logPanel.add(lbllogIn);
		lbllogIn.setFont(new Font("Arial", Font.BOLD, 20));

		lblUsername = new JLabel("Username:");
		lblUsername.setBounds(98, 64, 74, 14);
		logPanel.add(lblUsername);

		lblPassword = new JLabel("Password:");
		lblPassword.setBounds(98, 113, 86, 14);
		logPanel.add(lblPassword);

		txtBxUsername = new JTextField("KurtVs");
		txtBxUsername.setBounds(194, 61, 123, 20);
		txtBxUsername.setColumns(10);
		logPanel.add(txtBxUsername);

		txtBxPassword = new JPasswordField("AdminPassword");
		txtBxPassword.setBounds(194, 110, 123, 20);
		logPanel.add(txtBxPassword);

		loginBtn = new JButton("Login");
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
				computeMD5Hash(pwd+user);
				if (checkLoginDets()){
					//if connect continue
					adminPanel = new AdminPanel(homescreen);
					adminPanel.setVisible(true);				
					setVisible(false); //Make the screen invisible
				}else{
					JOptionPane.showMessageDialog(null, "Failed to log in");
				}		
			}
		});

		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				homescreen.setVisible(true);
				setVisible(false); //Make the screen invisible
				dispose();//Close the login window
			}
		});      

		pack();
	}

	public void computeMD5Hash(String userPass)
	{
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(userPass.getBytes());
			byte messageDigest[] = digest.digest();
			StringBuffer MD5Hash = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
			{
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				MD5Hash.append(h);
			}
			md5Hash = MD5Hash.toString();

		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
	}

	public Boolean checkLoginDets(){
		Boolean login = false;

		CallableStatement sm = null;
		try {

			String update = "{" + getuserPass +"(?,?)}";
			String dbURL = "jdbc:sqlserver://wch-db.cnfoxyxq90wv.ap-southeast-2.rds.amazonaws.com:47947";
			String user = "nhfnKGF519hinsd897665465jbsdsf";
			String pass = "NJiugi7btdjyFbe99n9n09UU";
			Connection conn = DriverManager.getConnection(dbURL, user, pass);

			sm = conn.prepareCall(update);
			sm.setString(1, md5Hash);
			sm.setString(2, txtBxUsername.getText());

			rs = sm.executeQuery();	 
			if (rs.next()){
				login = true;
			}
		}
		catch (SQLServerException sqex)
		{
			JOptionPane.showMessageDialog(null, "DB_ERROR: " + sqex);
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, "CONNECTION_ERROR: " + ex);
		}
		return login;
	}
}
