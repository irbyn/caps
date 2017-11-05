package Main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Schedule.SchedulePane;
import Sales.SalesPane;
import Installs.InstallsPane;
import Permit.PermitPane;
import Admin.AdminLogin;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
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
	private SchedulePane schedulePanel;
	private SalesPane salesPanel;
	private InstallsPane installPanel;
	private PermitPane permitPanel; 
	private AdminLogin adminLogin;
	private LoginWindow loginWindow;
	private Homescreen hs;
	private String loggedInUser = "call AWS_WCH_DB.dbo.h_loggedInUser";
	private ResultSet rs;
	private ConnDetails conDeets;
	private String user;
	public Color selected= Color.decode("#70a6ff");

	public Homescreen(String User, String Pass) {
		//Setting JFrame icon
		setIconImage(Toolkit.getDefaultToolkit().getImage(Homescreen.class.getResource("/wfs-logo-16.png")));
		user = User;
		hs = this;

		//PASS THE LOGIN DETAILS TO Class connectionDetails
		conDeets = new ConnDetails();

		// setting up JFrame
		getContentPane().setLayout(null);
		setPreferredSize(new Dimension(1100, 700));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("WorkFlow Solutions");

		// creating main button JPanel (blue)
		btnPanel = new JPanel();
		btnPanel.setLayout(null);
		btnPanel.setBounds(0, 0, 1100, 40);
		btnPanel.setPreferredSize(new Dimension(1100, 40));
		getContentPane().add(btnPanel);

		// creating JButtons in the main button JPanel (blue)
		scheduleBtn = new JButton("SCHEDULE");
		scheduleBtn.setBounds(188, 12, 128, 24);

		salesBtn = new JButton("SALES"); 
		salesBtn.setBounds(336, 12, 128, 24);

		installBtn = new JButton("INSTALLS");
		installBtn.setBounds(484, 12, 128, 24);

		permitBtn = new JButton("PERMIT");
		permitBtn.setBounds(632, 12, 128, 24);

		lblUser = new JLabel("Logged in as: ");
		lblUser.setBounds(10, 16, 168, 14);
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
				if(salesPanel==null){					
					salesPanel = new SalesPane(conDeets, hs);
				}	
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

				if(installPanel==null){
					installPanel = new InstallsPane(conDeets, hs);
				}				
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
				if(permitPanel==null){				
					permitPanel = new PermitPane(conDeets, hs);
				}
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
				adminLogin = new AdminLogin(hs);
				adminLogin.setVisible(true);
				setVisible(false); //Make the screen invisible
			}
		});

		logOutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false); //Make the screen invisible
				loginWindow = new LoginWindow();
				loginWindow.setVisible(true);
				dispose();	
			}
		});

		// creating main content JPanel
		contentPanel = new JPanel();
		contentPanel.setBounds(0, 40, 1100, 630);
		contentPanel.setPreferredSize(new Dimension(1100, 630));
		getContentPane().add(contentPanel);

		// creating new panel objects from the panel classes containing their content
		schedulePanel = new SchedulePane(conDeets, this); 
		salesPanel = null;	
		installPanel = null;	
		permitPanel = null;		

		getuser();

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

	protected void getuser(){
		CallableStatement sm = null;
		try {

			String update = "{" + loggedInUser +"(?)}";	
			CreateConnection connecting = new CreateConnection();
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			sm = conn.prepareCall(update);

			sm.setString(1, user);

			ResultSet qryResults = sm.executeQuery();
			rs = qryResults;

			//Setting who is logged into the application 
			while(qryResults.next()){
				lblUser.setText("Logged in as: " + rs.getString("Name"));
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
	}
}
