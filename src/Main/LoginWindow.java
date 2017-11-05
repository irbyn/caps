package Main;

//Description: This is the class that only allows sales people to log into the application 

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;

public class LoginWindow extends JFrame{

	private String getuserPass = "call AWS_WCH_DB.dbo.a_DeskLoginDetails";
	private Homescreen homescreen;
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
	private ConnDetails conDeets;

	public LoginWindow() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Homescreen.class.getResource("/wfs-logo-16.png")));
		conDeets = new ConnDetails();

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
		lbllogIn = new JLabel("Login");
		lbllogIn.setBounds(36, 25, 87, 28);
		logPanel.add(lbllogIn);
		lbllogIn.setFont(new Font("Arial", Font.BOLD, 20));

		lblUsername = new JLabel("Username:");
		lblUsername.setBounds(98, 64, 74, 14);
		logPanel.add(lblUsername);

		//preetered data needs to be deleted
		txtBxUsername = new JTextField("smitty");
		txtBxUsername.setBounds(194, 61, 123, 20);
		txtBxUsername.setColumns(10);
		logPanel.add(txtBxUsername);

		lblPassword = new JLabel("Password:");
		lblPassword.setBounds(98, 113, 86, 14);
		logPanel.add(lblPassword);

		//preetered data needs to be deleted
		txtBxPassword = new JPasswordField("Password");
		txtBxPassword.setBounds(194, 110, 123, 20);
		logPanel.add(txtBxPassword);

		cancelBtn = new JButton("Cancel");              
		cancelBtn.setBounds(98, 164, 86, 24);
		logPanel.add(cancelBtn);

		loginBtn = new JButton("Login");
		loginBtn.setBounds(231, 164, 86, 24);
		logPanel.add(loginBtn);

		//Action listeners for each button
		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Getting password and username from the text boxes
				String user=txtBxUsername.getText();
				String pwd = new String(txtBxPassword.getPassword());

				computeMD5Hash(pwd+user);
				if (checkLoginDets()){
					//if connect continue
				homescreen = new Homescreen(user, pwd);
				homescreen.setVisible(true);
				setVisible(false); //Make the screen invisible
				dispose();//Close the login window
				}else{
					JOptionPane.showMessageDialog(null, "Failed to log in");
				}				
			}
		});

		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false); //Make the screen invisible
				dispose();//Close the login window
			}
		});      		        
		//Load the frame
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

			//result.setText("MD5 hash generated is: " + " " + MD5Hash);
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
			
			String dbURL = conDeets.getURL();    
			String user = conDeets.getUser();    
			String pass = conDeets.getPass();    

			Connection conn = DriverManager.getConnection(dbURL, user, pass);
			
			sm = conn.prepareCall(update);
			sm.setString(1, md5Hash);
			sm.setString(2, txtBxUsername.getText());

			rs = sm.executeQuery();	 
			conn.close();
			
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


	public static void main(String[] arguments) throws IOException {
		// creating JFrame object and setting it visible
		LoginWindow frame = new LoginWindow();
		frame.setVisible(true);
	}
}
