package Schedule;

/*
 * GUI PANEL:	SCHEDULE - Installations
 * Allows User to view install status & Make or delete bookings
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.TableView.TableRow;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Main.bookingColumnRenderer;
import Permit.PermitPane;
import Permit.validator;
import net.proteanit.sql.DbUtils;


class TimeTablePanel extends JPanel {

	private String [] columns = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	private int [] columnWidth = {80, 20, 50, 100, 100, 100, 100, 100, 50};
	private String qry = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
	private String qry2 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";

	private String upConsent = "call AWS_WCH_DB.dbo.p_PermitUpdateConsent ";
	private String upSent = "{call AWS_WCH_DB.dbo.p_PermitUpdateSent (?)}";
	private String upFire = "{call AWS_WCH_DB.dbo.p_PermitUpdateFire (?,?,?,?,?,?,?,?)}";
	private CreateConnection connecting;

	private String param = "";  
	private ResultSet rs;
	private ResultSet rs2;
	private ResultSet rs3;
	private Boolean rowSelected;
	private Boolean validEntries = true;	
	private String msg ="";
	private String weekOf ="'2017-10-1'";
	private String weekInitial ="'2017-10-1'";
	private int day;        
	private String month; 
	private int mnth;
	private int year;
	private int row;
	private int col;

	private ImageIcon left;
	private ImageIcon right;

	private JLabel label, today;
	private Calendar cal = new GregorianCalendar();
	private JButton backBtn;
	private JButton forBtn;

	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable timeTbl;
	private DefaultTableModel tm;

	private Boolean lockForm;
	private ConnDetails conDeets;
	private SchedulePane sp;

	public TimeTablePanel(Boolean lockForm, ConnDetails conDetts, SchedulePane spn) {

		this.lockForm = lockForm;
		this.conDeets = conDetts;
		this.sp = spn;

		connecting = new CreateConnection();	

		rowSelected = false;

		tm = new DefaultTableModel();  
		tm.setRowCount(0);
		timeTbl = new JTable(tm);
		timeTbl.setRowHeight(50);
		timeTbl.setRowSelectionAllowed(false);

		JScrollPane scrollPane = new JScrollPane(timeTbl);

		header= timeTbl.getTableHeader();
		columnModel = header.getColumnModel();
		add(header); 

		tablePanel = new JPanel();
		tablePanel.setBounds(20, 50, 1025, 500);  //setPreferredSize(new Dimension(0, 300));      
		tablePanel.setLayout(new BorderLayout());

		infoPanel = new JPanel();
		infoPanel.setBounds(0, 400, 1100, 100);  //setPreferredSize(new Dimension(0, 300));
		infoPanel.setLayout(null);

		left = new ImageIcon(getClass().getResource("left.png"));
		right = new ImageIcon(getClass().getResource("right.png"));

		backBtn = new JButton(left);
		backBtn.setBounds(189, 15, 84, 34);
		this.add(backBtn);

		label = new JLabel();
		label.setBounds(300, 20, 200, 20);		        
		this.add(label);

		today = new JLabel();
		today.setBounds(500, 20, 200, 20);
		this.add(today);	

		forBtn = new JButton(right);
		forBtn.setBounds(943, 15, 84, 34);
		this.add(forBtn);

		this.setLayout(null);
		this.add(tablePanel); 

		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(timeTbl.getTableHeader(), BorderLayout.NORTH);       

		backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cal.add(Calendar.DATE, -7);
				updateWeek();
			}
		});

		forBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cal.add(Calendar.DATE, +7);
				updateWeek();
			}
		});		

		// Calendar for today
		cal.getInstance();   
		day = cal.get(Calendar.DAY_OF_MONTH);           
		mnth = cal.get(Calendar.MONTH)+1;
		month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
		year = cal.get(Calendar.YEAR);

		// Display Today's Date
		today.setText("TODAY IS: " + day + " " +month + " " + year);    
		String str = cal.getTime().toString();
		weekInitial = "'"+ year +  "-" +  month + "-" +  day + "'";

		// PRE-LOAD Table data for first tab		  	
		resetTable();			
	}


	protected void updateWeek() {
		cal.getInstance();

		int currentDay = cal.get(Calendar.DAY_OF_WEEK);		
		int firstOfWeek = 1-currentDay;	    	    
		cal.add(Calendar.DATE, firstOfWeek);  
		int day = cal.get(Calendar.DAY_OF_MONTH); 
		int mnth = cal.get(Calendar.MONTH)+1;

		String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
		int year = cal.get(Calendar.YEAR);
		label.setText("WEEK OF: " + day + "-" +month + "-" + year);

		int numberOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		weekOf = "'" + year + "-" +  mnth + "-" +   day + "'";

		ResultSet rs = sp.getResults(0, weekOf );
		timeTbl.setModel(DbUtils.resultSetToTableModel(rs)); 

		JTableHeader header= timeTbl.getTableHeader();
		TableColumnModel colMod = header.getColumnModel();
		int i;

		for (i=0; i<7; i++){
			TableColumn tabCol = colMod.getColumn(i+2);
			int thisDay = (day + i);
			if(thisDay > numberOfDays){
				thisDay = thisDay - numberOfDays;
			}
			tabCol.setHeaderValue(columns[i] + " "+ thisDay);
			header.setFont(new Font("SansSerif", Font.BOLD, 14));
		}
		header.repaint(); 
		renderTable();
	}


	public void renderTable() {
		spaceHeader();

		JTableHeader th = timeTbl.getTableHeader();		
		TableColumn tc = timeTbl.getColumnModel().getColumn(0);		

		int cols = timeTbl.getColumnModel().getColumnCount();

		for(int i = 0; i<cols; i++){
			tc = timeTbl.getColumnModel().getColumn(i);
			tc.setCellRenderer(new bookingColumnRenderer());
		}
	}


	protected void resetTable() {

		ResultSet rs = sp.getResults(0, weekOf );
		timeTbl.setModel(DbUtils.resultSetToTableModel(rs)); 
		spaceHeader();			  	
		param = "";  
		timeTbl.clearSelection();
		rowSelected=false;
		updateWeek();
	}


	public void spaceHeader() {
		int i;
		TableColumn tabCol = columnModel.getColumn(0);
		for (i=0; i<columnWidth.length; i++){
			tabCol = columnModel.getColumn(i);
			tabCol.setPreferredWidth(columnWidth[i]);
		}
		header.repaint();
	}

	public JTable getScheduleTbl(){
		return timeTbl;
	}
}
