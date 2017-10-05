package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class LoginWindow extends JFrame{

	private Homescreen homescreen;
	private JLabel lbllogIn;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JTextField txtBxUsername;
	private JPasswordField txtBxPassword;	
	private JButton loginBtn;
	private JButton cancelBtn;
    private JPanel logPanel;

    public LoginWindow() {
  
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
        lbllogIn = new JLabel("Log in");
        lbllogIn.setBounds(36, 25, 87, 28);
        logPanel.add(lbllogIn);
        lbllogIn.setFont(new Font("Arial", Font.BOLD, 20));
        
        lblUsername = new JLabel("Username:");
        lblUsername.setBounds(98, 64, 74, 14);
        logPanel.add(lblUsername);
        
        //preetered data needs to be deleted
        txtBxUsername = new JTextField("Khgv92367hdkfug9");
	  	txtBxUsername.setBounds(194, 61, 123, 20);
	  	txtBxUsername.setColumns(10);
	  	logPanel.add(txtBxUsername);
	  	
        lblPassword = new JLabel("Password:");
        lblPassword.setBounds(98, 113, 86, 14);
        logPanel.add(lblPassword);
	  	
        //preetered data needs to be deleted
	  	txtBxPassword = new JPasswordField("Locei02h84b5KJUVaW");
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
        		homescreen = new Homescreen(user, pwd);
        		homescreen.setVisible(true);
        		setVisible(false); //Make the screen invisible
        		dispose();//Close the login window
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
    
    public static void main(String[] arguments) throws IOException {
        // creating JFrame object and setting it visible
        LoginWindow frame = new LoginWindow();
        frame.setVisible(true);
    }
}
