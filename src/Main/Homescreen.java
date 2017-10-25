package Main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Schedule.SchedulePane;
import Sales.SalesPane;
import Installs.InstallsPane;
import Permit.PermitPane;
import Admin.AdminLogin;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Homescreen extends JFrame {

	private JFrame frame;
	private JLabel lblUser;
	private JButton scheduleBtn;
	private JButton salesBtn;
	private JButton installBtn;
	private JButton permitBtn;
	private JButton adminLoginBtn;
	private JButton logOutBtn;
	private JPanel btnPanel;
	private JPanel contentPanel;
	//private JPanel adminLoginPnl;
	private SchedulePane schedulePanel;
	private SalesPane salesPanel;
	private InstallsPane installPanel;
	private PermitPane permitPanel; 
	private AdminLogin adminLogin;
	private static String user;
	private static String pass;

	public Color selected= Color.decode("#70a6ff");

	public Homescreen(String User, String Pass) {

		user = User;
		pass = Pass;

		//PASS THE LOGIN DETAILS TO Class connectionDetails
		ConnDetails conDeets = new ConnDetails(user, pass);

		// setting up JFrame
		getContentPane().setLayout(null);
		setPreferredSize(new Dimension(1100, 700));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// creating main button JPanel (blue)
		btnPanel = new JPanel();
		btnPanel.setLayout(null);
		btnPanel.setBounds(0, 0, 1100, 40);
		btnPanel.setPreferredSize(new Dimension(1100, 40));
		getContentPane().add(btnPanel);

		// creating JButtons in the main button JPanel (blue)
		scheduleBtn = new JButton("SCHEDULE");
		scheduleBtn.setBounds(160, 10, 128, 24);

		salesBtn = new JButton("SALES"); 
		salesBtn.setBounds(308, 10, 128, 24);

		installBtn = new JButton("INSTALLS");
		installBtn.setBounds(456, 10, 128, 24);

		permitBtn = new JButton("PERMIT");
		permitBtn.setBounds(604, 10, 128, 24);

		lblUser = new JLabel("Logged in as: ...");
		lblUser.setBounds(10, 16, 100, 14);
		btnPanel.add(lblUser);

		logOutBtn = new JButton("Log Out ");

		logOutBtn.setBounds(962, 11, 128, 24);
		btnPanel.add(logOutBtn);

		btnPanel.add(scheduleBtn);
		btnPanel.add(salesBtn);
		btnPanel.add(installBtn);
		btnPanel.add(permitBtn);

		adminLoginBtn = new JButton("Admin Login");
		adminLoginBtn.setBounds(824, 12, 128, 23);
		//adminLogin = new AdminLogin();
		btnPanel.add(adminLoginBtn);

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
				clearButton(btnPanel);
				highlightButton(scheduleBtn);
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
				clearButton(btnPanel);
				highlightButton(salesBtn);
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
				clearButton(btnPanel);
				highlightButton(installBtn);
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
				clearButton(btnPanel);
				highlightButton(permitBtn);
			}
		});
		
		//Log into the Admin Panel
		adminLoginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				adminLogin = new AdminLogin();
				adminLogin.setVisible(true);
				System.out.println("+++++++++++++++");
				setVisible(false); //Make the screen invisible
				dispose();//Close the login window
			}
		});
		
		logOutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false); //Make the screen invisible
				dispose();	
			}
		});
		
		// creating main content JPanel (red)
		contentPanel = new JPanel();
		contentPanel.setBounds(0, 40, 1100, 630);
		contentPanel.setPreferredSize(new Dimension(1100, 630));
		getContentPane().add(contentPanel);

		// creating new panel objects from the panel classes containing their content
		schedulePanel = new SchedulePane(conDeets, this); 
		salesPanel = new SalesPane(conDeets, this);  
		installPanel = new InstallsPane(conDeets, this);
		permitPanel = new PermitPane(conDeets, this);
		

		//Making the schedule the first view the user sees.  
		contentPanel.add(schedulePanel);        		        
		//Load the frame
		highlightButton(scheduleBtn);
		pack();
	}

	protected void highlightButton(JButton btn) {
		btn.setBackground(selected);
		btn.setForeground(Color.WHITE);
	}
//returns all buttons to default color scheme
	protected void clearButton(JPanel infPanel) {
		for(Component control : infPanel.getComponents())
      	{	
      	    if(control instanceof JButton)
      	    {
      	    	JButton ctrl = (JButton) control;
      	    	ctrl.setBackground(null);
      	    	ctrl.setForeground(Color.BLACK);
      	    }
      	}
	}

	public void showMsg(String st){
		  //      JFrame f = new JFrame();
		        final JDialog msgDialog = new JDialog(frame, st);
            	msgDialog.pack();
            	msgDialog.setLocationRelativeTo(this);
            	msgDialog.setSize(new Dimension(400, 0));
            	msgDialog.setResizable(true);       
		        
		        Timer timer = new Timer(2000, new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		            	msgDialog.setVisible(false);
		            	msgDialog.dispose();
		           }
		        });
		        timer.setRepeats(false);
		        timer.start();
		        msgDialog.setVisible(true);
	}
	public SchedulePane getSchedule(){
		return schedulePanel;
	}
}
