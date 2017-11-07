package Installs;

/*
 * GUI PANEL:	Bookings
 * Allows User to view install status & Make or delete bookings
 */

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultCaret;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Main.bookingColumnRenderer;
import Schedule.SchedulePane;
import net.proteanit.sql.DbUtils;

class BookingsPanel extends JPanel {

	private String [] columns = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	private int [] colWidth = {80, 20, 50, 100, 100, 100, 100, 100, 50};
	private int [] columnWidth = {30, 100, 100, 80, 50, 50, 50, 40}; 	

	private String saveBooking = "{Call AWS_WCH_DB.dbo.[sc_SaveBooking] (?,?,?,?)}";
	private String delBooking = "{Call AWS_WCH_DB.dbo.[sc_DeleteBooking] (?,?,?,?)}";
	private String updateNote = "{Call AWS_WCH_DB.dbo.[i_UpdateNoteToInstaller] (?,?)}";
	private String updateClose = "{Call AWS_WCH_DB.dbo.[i_UpdateInstallClosed] (?)}";

	private String weekOf ="'2017-10-08'";
	private String[] week= new String[7]; 
	private String weekInitial ="'2017-10-08'";
	private Date dayOfWeek;
	private int day;        
	private String month; 
	private int mnth;
	private int year;
	private int row;
	private int col;

	private Calendar cal = new GregorianCalendar();

	private ImageIcon left;
	private ImageIcon right;
	private JButton backBtn;
	private JLabel weekLbl;
	private JButton forBtn;

	private String invoiceNum = ""; 
	private int invRow=-1;
	private String note;
	private String inst;
	private String tme;
	private String dte;
	private ResultSet rs;
	private Boolean lockSelection = false;
	private Boolean rowSelected = false;

	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel schedulePanel;
	private JPanel infoPanel;
	private JTable installTbl;
	private DefaultTableModel tmod2;
	private JScrollPane scrollPane;

	private JTableHeader hdr;
	private TableColumnModel colMod;
	private JTable timeTbl;
	private DefaultTableModel tm;
	private JScrollPane scrPane;

	private JScrollPane spSTK;

	private JTextArea detailsTxtArea;
	private JTextArea stockTxtArea;
	private JTextArea noteToInstaller;

	private JLabel instLbl;
	private JTextField instTxtBx;
	private JLabel dateLbl;
	private JTextField dateTxtBx;

	private JButton installerNoteBtn;
	private JButton closeInstallBtn;
	private JButton cancelPermitReqBtn; 
	private JButton editBookingBtn; 	

	private CreateConnection conn;
	private CreateConnection connecting;

	private Boolean lockForm;
	private ConnDetails conDeets;
	private InstallsPane ip;
	private SchedulePane schP;


	public BookingsPanel(Boolean lockForm, ConnDetails conDetts, InstallsPane ipn, SchedulePane schPn) {

		this.lockForm = lockForm;
		this.conDeets = conDetts;
		this.ip = ipn;
		this.schP = schPn;

		connecting = new CreateConnection();
		left = new ImageIcon(getClass().getResource("left20.png"));
		right = new ImageIcon(getClass().getResource("right20.png"));

		tmod2 = new DefaultTableModel();  
		tmod2.setRowCount(0);
		installTbl = new JTable(tmod2);
		installTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		installTbl.setAutoCreateRowSorter(true);      
		scrollPane = new JScrollPane(installTbl);	  
		header= installTbl.getTableHeader();
		columnModel = header.getColumnModel();
		add(header); 

		tm = new DefaultTableModel();  
		tm.setRowCount(0);
		timeTbl = new JTable(tm);
		timeTbl.setRowSelectionAllowed(false);
		timeTbl.setAutoCreateRowSorter(false);
		timeTbl.setRowHeight(50);
		scrPane = new JScrollPane(timeTbl);	  
		hdr= timeTbl.getTableHeader();
		colMod = hdr.getColumnModel();
		add(header); 	    

		backBtn = new JButton(left);
		backBtn.setBounds(189, 0, 84, 20);
		backBtn.setVisible(false);
		this.add(backBtn);

		weekLbl = new JLabel();
		weekLbl.setBounds(300, 0, 200, 20);
		this.add(weekLbl);

		forBtn = new JButton(right);
		forBtn.setBounds(943, 0, 84, 20);
		forBtn.setVisible(false);
		this.add(forBtn);

		tablePanel = new JPanel();
		tablePanel.setBounds(20, 20, 1025, 400);       
		tablePanel.setLayout(new BorderLayout());

		schedulePanel = new JPanel();
		schedulePanel.setBounds(20, 20, 1025, 400);
		schedulePanel.setVisible(false);
		schedulePanel.setLayout(new BorderLayout());

		infoPanel = new JPanel();
		infoPanel.setBounds(0, 420, 1100, 160);  
		infoPanel.setLayout(null);

		detailsTxtArea = new JTextArea("");
		detailsTxtArea.setBounds(20, 20, 230, 120);
		detailsTxtArea.setBorder(BorderFactory.createEtchedBorder());
		detailsTxtArea.setOpaque(false);
		detailsTxtArea.setLineWrap(true);
		detailsTxtArea.setEditable(false);
		infoPanel.add(detailsTxtArea);

		stockTxtArea = new JTextArea("");
		stockTxtArea.setBorder(BorderFactory.createEtchedBorder());
		stockTxtArea.setOpaque(false);
		stockTxtArea.setTabSize(6);
		stockTxtArea.setLineWrap(true);
		stockTxtArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret) stockTxtArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		spSTK = new JScrollPane(stockTxtArea);
		spSTK.setBounds(270, 20, 430, 120);

		infoPanel.add(spSTK);

		instLbl = new JLabel("Installer:");
		instLbl.setBounds(825, 20, 70, 20);
		infoPanel.add(instLbl);
		instTxtBx = new JTextField(" ",10);
		instTxtBx.setBounds(895, 20, 150, 20);
		instTxtBx.setEditable(false);
		infoPanel.add(instTxtBx);

		dateLbl = new JLabel("Date:");
		dateLbl.setBounds(825, 50, 70, 20);
		infoPanel.add(dateLbl);
		dateTxtBx = new JTextField(10);
		dateTxtBx.setBounds(895, 50, 150, 20);
		dateTxtBx.setEditable(false);
		infoPanel.add(dateTxtBx);

		installerNoteBtn = new JButton("Add Note to Installer");
		installerNoteBtn.setBounds(895, 80, 150, 25);
		installerNoteBtn.setVisible(false);
		infoPanel.add(installerNoteBtn);

		closeInstallBtn = new JButton("Close Install");
		closeInstallBtn.setBounds(720, 80, 150, 25);
		closeInstallBtn.setVisible(false);
		infoPanel.add(closeInstallBtn);

		cancelPermitReqBtn = new JButton("Cancel");
		cancelPermitReqBtn.setBounds(720, 115, 150, 25);
		infoPanel.add(cancelPermitReqBtn);

		editBookingBtn = new JButton("Edit Booking");
		editBookingBtn.setBounds(895, 115, 150, 25);
		infoPanel.add(editBookingBtn);

		this.setLayout(null);
		this.add(tablePanel);
		this.add(schedulePanel);
		this.add(infoPanel);     

		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(installTbl.getTableHeader(), BorderLayout.NORTH); 
		schedulePanel.add(scrPane, BorderLayout.CENTER);
		schedulePanel.add(timeTbl.getTableHeader(), BorderLayout.NORTH); 	  	

		backBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				cal.add(Calendar.DATE, -7);
				updateWeek();
			}
		});
		forBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				cal.add(Calendar.DATE, +7);
				updateWeek();
			}
		});
		installerNoteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				addNoteToInstaller();
				stockTxtArea.setText(ip.DisplayStockOnOrder(invoiceNum));
			}
		});
		closeInstallBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				closeInstall();
			}
		});
		cancelPermitReqBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				{ 
					resetTable();
				}					
			}
		});
		editBookingBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				{ 
					if(rowSelected){
						tablePanel.setVisible(false);
						schedulePanel.setVisible(true);   
						backBtn.setVisible(true);
						forBtn.setVisible(true);
						editBookingBtn.setVisible(false);			   
						ResultSet rs = schP.getResults(0, weekOf );
						timeTbl.setModel(DbUtils.resultSetToTableModel(rs));
						updateWeek();			 
					}
				}
			}
		});
		installTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					rowSelected=true;
					clearStock();
					try{
						invoiceNum = installTbl.getValueAt(installTbl.getSelectedRow(), 0).toString();
						stockTxtArea.setText(ip.DisplayStockOnOrder(invoiceNum));
						detailsTxtArea.setText(ip.DisplayClientShortDetails(invoiceNum));

						instTxtBx.setText(ip.getInstaller());        
						dateTxtBx.setText(ip.getInstTime());
						note = ip.getNoteFromInstaller();
						installerNoteBtn.setVisible(true); 
						closeInstallBtn.setVisible(true); 						
						if( note.equals("NULL")){ 
						} else if (note.equals("")){
						}
						else{
							stockTxtArea.setText("NOTE FROM INSTALLER:\n" + note);
						}					
					} catch (IndexOutOfBoundsException e){
						//Ignoring IndexOutOfBoundsExceptions!
					}
				}
			}
		});
		timeTbl.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if (!lockSelection){
					checkClick(evt);
				}
			}
		});

		// Initialize Calendar for today
		cal.getInstance();   
		day = cal.get(Calendar.DAY_OF_MONTH);           
		mnth = cal.get(Calendar.MONTH)+1;
		month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
		year = cal.get(Calendar.YEAR);
		weekInitial = "'"+ year +  "-" +  month + "-" +  day + "'";
	}

	protected void reselectRow(String inv) {
		for (int i = 0; i < installTbl.getRowCount(); i++){
			if (inv.equals(installTbl.getModel().getValueAt(i, 0).toString())){
				installTbl.setRowSelectionInterval(i, i);
			}
		}
	}

	protected void checkClick(java.awt.event.MouseEvent evt){
		row = timeTbl.rowAtPoint(evt.getPoint());
		col = timeTbl.columnAtPoint(evt.getPoint());
		if (col >= 2) {		
			inst = timeTbl.getValueAt(row,0).toString();
			tme = timeTbl.getValueAt(row,1).toString();	
			dte = week[col-2];

			if (timeTbl.getValueAt(row, col) == null || timeTbl.getValueAt(row, col).equals("")) {

				try {
					dayOfWeek = new SimpleDateFormat("yyyy-MM-dd").parse(dte);
				} catch (ParseException e) {
				}
				StringBuilder sbuf = new StringBuilder();
				Formatter fmt = new Formatter(sbuf);
				fmt.format("%tA %td %tb %tY", dayOfWeek, dayOfWeek, dayOfWeek, dayOfWeek);

				int input = JOptionPane.showConfirmDialog(null,"Make Booking for Install " + invoiceNum + "?" +
						"\nON: " + sbuf +  
						"\nIN: " + tme + 
						"\nBY: " + inst,  "Place Booking?", JOptionPane.YES_NO_OPTION);
				if (input == 0){
					lockSelection = true;
					String inv = invoiceNum;
					modifyBooking(saveBooking);	        		
					ip.showMessage("Saving Booking");
					resetTable();
					reselectRow(inv);        		
				}					
			}else {
				String str = timeTbl.getValueAt(row, col).toString();

				int start = str.indexOf("[");
				int end = str.indexOf("]");
				String st = str.substring(start+1, end);

				if (st.equals(invoiceNum)){
					int input = JOptionPane.showConfirmDialog(null,"Do you wish to delete booking for " + 
							invoiceNum + "?",  "Delete Booking?", JOptionPane.YES_NO_OPTION);
					if (input == 0){
						lockSelection = true;
						String inv = invoiceNum;
						modifyBooking(delBooking);
						ip.showMessage("Deleting Booking for "+ invoiceNum);
						resetTable();	
						reselectRow(inv);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Delete this Existing Install before making this Booking");
				}						
			}
		}
	}

	protected void closeInstall() {
											 
		if(installTbl.getValueAt(installTbl.getSelectedRow(), 7).toString()!=null){
				
		String str = installTbl.getValueAt(installTbl.getSelectedRow(), 7).toString();
		if (str.length()> 8){
		str = str.substring(0,8);
		}
		if (str.equals("Complete")){

			int input = JOptionPane.showConfirmDialog(null,"Close Install?\n "
					+ "This Action is final, Install will no longer be tracked!",  "Close Install?", JOptionPane.YES_NO_OPTION);
			if (input == 0){		
				ip.showMessage("Closing Install");
				UpdateInstallClosed(invoiceNum);
				resetTable();
			}
			
		}else {
			JOptionPane.showMessageDialog(null, "Installer must mark this Install as complete before Closing");
		}
		}
	}



	private void UpdateInstallClosed(String inv) {
		CallableStatement pm = null;

		try {
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			pm = conn.prepareCall(updateClose);

			pm.setString(1, inv);

			pm.executeUpdate();
			conn.close();
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

	protected void modifyBooking(String modify) {
		CallableStatement pm = null;

		try {

			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			pm = conn.prepareCall(modify);

			pm.setString(1, invoiceNum);
			pm.setString(2, dte);
			pm.setString(3, tme);
			pm.setString(4, inst);

			pm.executeUpdate();
			conn.close();
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

	protected void resetSchedule() {

		ResultSet rs = schP.getResults(0, weekOf );
		timeTbl.setModel(DbUtils.resultSetToTableModel(rs)); 	  				
		spaceSchedule();			  	
		rowSelected=false;
		updateWeek();
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
		weekLbl.setText("WEEK OF: " + day + "-" +month + "-" + year);

		int numberOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		weekOf = "'" + year + "-" +  mnth + "-" +   day + "'";


		ResultSet rs = schP.getResults(0, weekOf );
		timeTbl.setModel(DbUtils.resultSetToTableModel(rs)); 

		JTableHeader header= timeTbl.getTableHeader();
		TableColumnModel colMod = header.getColumnModel();

		int i;

		boolean newMonth = false; 
		int mth = mnth;
		int yr = year;

		//iterate thru the days of the week
		for (i=0; i<7; i++){
			TableColumn tabCol = colMod.getColumn(i+2);
			int thisDay = (day + i);
			//reset day, month & year if week passes thru the end of the month
			if(thisDay > numberOfDays){
				thisDay = thisDay - numberOfDays;				
				mth = mnth+1;
				if(mth > 12){
					mth = 1;
					yr = year + 1;
				}
			}
			//fill array of this weeks dates
			week[i] = yr + "-" +  mth + "-" +   thisDay;
			//add day number to day of week in header
			tabCol.setHeaderValue(columns[i] + " "+ thisDay);
			header.setFont(new Font("SansSerif", Font.BOLD, 14));
		}
		header.repaint(); 
		renderTable();
	}


	public void renderTable() {
		spaceSchedule();
		TableColumn tc = timeTbl.getColumnModel().getColumn(0);	
		int cols = timeTbl.getColumnModel().getColumnCount();

		for(int i = 0; i<cols; i++){
			tc = timeTbl.getColumnModel().getColumn(i);
			tc.setCellRenderer(new bookingColumnRenderer());
		}
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

	public void spaceSchedule() {
		int i;
		TableColumn tabCol = colMod.getColumn(0);
		for (i=0; i<colWidth.length; i++){
			tabCol = colMod.getColumn(i);
			tabCol.setPreferredWidth(colWidth[i]);
		}
		header.repaint();
	}


	protected void clearStock() {
		detailsTxtArea.setText("");
		stockTxtArea.setText("");
		tmod2.setRowCount(0);
	}

	public void spaceHeader(TableColumnModel colM, int[] colW) {
		int i;
		TableColumn tabCol = colM.getColumn(0);
		for (i=0; i<colW.length; i++){
			tabCol = colM.getColumn(i);
			tabCol.setPreferredWidth(colW[i]);
		}
		header.repaint();
	}   

	protected void resetTable() {	
		ResultSet rs = ip.getResults(4,  conDeets);
		installTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
		spaceHeader(columnModel, columnWidth);  	
		rowSelected=false;
		invoiceNum = "";
		instTxtBx.setText("");
		dateTxtBx.setText("");
		clearStock();
		tablePanel.setVisible(true);
		schedulePanel.setVisible(false);
		backBtn.setVisible(false);
		forBtn.setVisible(false);
		editBookingBtn.setVisible(true);
		installerNoteBtn.setVisible(false);
		closeInstallBtn.setVisible(false); 
		lockSelection = false;
	}	

	protected void addNoteToInstaller() {

		noteToInstaller = new JTextArea(5, 32);
		noteToInstaller.setLineWrap(true);
		noteToInstaller.setWrapStyleWord(true);
		noteToInstaller.setText(ip.DisplayNoteToInstaller(invoiceNum));
		JScrollPane scr = new JScrollPane(noteToInstaller);

		int option = JOptionPane.showOptionDialog(null, scr, "Enter Note To Installer of Inv "+invoiceNum, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
		if (option == JOptionPane.CANCEL_OPTION)
		{
			// user hit cancel
		} else if (option == JOptionPane.OK_OPTION)
		{
			String note = noteToInstaller.getText();
			int noteSize = note.length();
			if (noteSize > 250){
				ip.showMessage("Note Shortened, Max of 250 Characters reached");
				note = note.substring(0, 250);
				updateNote(note);

			} else {
				updateNote(note);
			}
		}	
	}

	private void updateNote(String note2) {

		CallableStatement pm = null;

		try {

			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			pm = conn.prepareCall(updateNote);

			pm.setString(1, invoiceNum);
			pm.setString(2, note2);

			pm.executeUpdate();
			conn.close();
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


	public JTable getInstallTbl(){
		return installTbl;
	}
	public JPanel getInfoPanel(){
		return infoPanel;
	}
}
