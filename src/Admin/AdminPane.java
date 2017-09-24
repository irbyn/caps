package Admin;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Installs.InstallsPane;
import Main.ConnDetails;
import Main.Homescreen;
import Permit.PermitPane;
import Sales.SalesPane;
import Schedule.SchedulePane;

import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AdminPane extends JFrame{

	private JFrame frame;
	private JButton loginBtn;
	private JButton cancelBtn;

    private JPanel logPanel;

    
    


    public AdminPane() {
    	
  
        // setting up JFrame
        getContentPane().setLayout(null);
        setPreferredSize(new Dimension(400, 200));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        // creating main button JPanel (blue)
        logPanel = new JPanel();
        logPanel.setBounds(0, 0, 400, 200);
        logPanel.setPreferredSize(new Dimension(400, 200));
        getContentPane().add(logPanel);
        
        // creating JButtons in the main button JPanel (blue)
        loginBtn = new JButton("LOGIN");
        cancelBtn = new JButton("SALES");              

        
        logPanel.add(loginBtn);
        logPanel.add(cancelBtn);

        
 
        
        
        //Action listeners for each button
        loginBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) { 
 
        		// adding schedule JPanel to main content JPanel

        	}
        });
        
        cancelBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {

        	}
        });
        
      		        
        //Load the frame
        pack();
    }
    
    
    public static void main(String[] arguments) {
        // creating JFrame object and setting it visible
        AdminPane frame = new AdminPane();
        frame.setVisible(true);
        

    }
}
