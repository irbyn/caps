package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Schedule.SchedulePane;
import Sales.SalesPane;
import Installs.InstallsPane;
import Permit.PermitPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class Homescreen extends JFrame {

	private JFrame frame;
	private JButton scheduleBtn;
	private JButton salesBtn;
	private JButton installBtn;
	private JButton permitBtn;
	private JButton adminLoginBtn;
    private JPanel btnPanel;
    private JPanel contentPanel;
    private SchedulePane schedulePanel;
    private SalesPane salesPanel;
    private InstallsPane installPanel;
    private PermitPane permitPanel;    
    
    public Color Mycolour;

    public Homescreen() {
    	
    	// TEMP SETUP OF PASSWORDS - TO BE DELETED!!!!!!!!
    	
 	    String user = "Khgv92367hdkfug9";
 	    String pass = "Locei02h84b5KJUVaW";
 	    
 	    //PASS THE LOGIN DETAILS TO Class connectionDetails
 	   ConnDetails conDeets = new ConnDetails(user, pass);
 	   
 	   CreateConnection conn = new CreateConnection();
 	   
    	Mycolour = Color.decode("#eeeeee");
    	
        // setting up JFrame
        getContentPane().setLayout(null);
        setPreferredSize(new Dimension(1100, 700));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        // creating main button JPanel (blue)
        btnPanel = new JPanel();
        btnPanel.setLayout(null);
   //     btnPanel.setBackground(Mycolour);
        btnPanel.setBounds(0, 0, 1100, 40);
        btnPanel.setPreferredSize(new Dimension(1100, 40));
        getContentPane().add(btnPanel);
        
        // creating JButtons in the main button JPanel (blue)
        scheduleBtn = new JButton("SCHEDULE");
        scheduleBtn.setBounds(246, 11, 128, 24);
        
        salesBtn = new JButton("SALES"); 
        salesBtn.setBounds(394, 11, 128, 24);
        
        installBtn = new JButton("INSTALLS");
        installBtn.setBounds(542, 11, 128, 24);
        
        permitBtn = new JButton("PERMIT");
        permitBtn.setBounds(690, 11, 128, 24);
        
        adminLoginBtn = new JButton("Admin Login");
        adminLoginBtn.setBounds(962, 11, 128, 24);
        
        btnPanel.add(scheduleBtn);
        btnPanel.add(salesBtn);
        btnPanel.add(installBtn);
        btnPanel.add(permitBtn);
        btnPanel.add(adminLoginBtn);
        
        JLabel lblUser = new JLabel("Logged in as: ...");
        lblUser.setBounds(10, 16, 100, 14);
        btnPanel.add(lblUser);
        
        // creating main content JPanel (red)
        contentPanel = new JPanel();
        contentPanel.setBounds(0, 40, 1100, 630);
        contentPanel.setPreferredSize(new Dimension(1100, 630));
        getContentPane().add(contentPanel);
        
        // creating new panel objects from the panel classes containing their content
        schedulePanel = new SchedulePane(); 
        salesPanel = new SalesPane();  
        installPanel = new InstallsPane();
        permitPanel = new PermitPane(conDeets);
        
        //Action listeners for each button
        scheduleBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) { 
        		if (salesBtn.isDisplayable()|| installPanel.isDisplayable() || permitPanel.isDisplayable()) {
        			contentPanel.removeAll();
        		} 
        		// adding schedule JPanel to main content JPanel
        		contentPanel.add(schedulePanel);        		
        		pack();
        		contentPanel.repaint();
        	}
        });
        
        salesBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (schedulePanel.isDisplayable() || installPanel.isDisplayable() || permitPanel.isDisplayable()) {
        			contentPanel.removeAll();
        		}  		
        		contentPanel.add(salesPanel);        		
        		pack();
        		contentPanel.repaint();
        	}
        });
        
        installBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (salesBtn.isDisplayable()|| schedulePanel.isDisplayable() || permitPanel.isDisplayable()) {
        			contentPanel.removeAll();
        		} 		
        		contentPanel.add(installPanel);        		
        		pack();
        		contentPanel.repaint();
        	}
        });
        
        permitBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (salesBtn.isDisplayable()|| schedulePanel.isDisplayable() || installPanel.isDisplayable()) {
        			contentPanel.removeAll();
        		} 	
        		contentPanel.add(permitPanel);        		
        		pack();
        		contentPanel.repaint();
        	}
        });
        
        //Log into the Admin Panel
        adminLoginBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {	
        		
        	}
        });
        
        //Making the schedule the first view the user sees.  
        contentPanel.add(schedulePanel);        		        
        //Load the frame
        pack();
    }
    
    
    public static void main(String[] arguments) {
        // creating JFrame object and setting it visible
        Homescreen frame = new Homescreen();
        frame.setVisible(true);
        

    }
}
