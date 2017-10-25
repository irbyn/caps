package Installs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
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

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
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
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Main.GetJobs;
import Permit.PermitPane;
import Schedule.SchedulePane;
import net.proteanit.sql.DbUtils;

class BookingsPanel extends JPanel {

	private String [] columns = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	private int [] colWidth = {80, 20, 50, 100, 100, 100, 100, 100, 50};
	
	private int [] columnWidth = {30, 100, 100, 80, 50, 50, 50, 40}; 	
	private String upCCCClient = "{Call AWS_WCH_DB.dbo.[p_PermitUpdateCCCToClient] (?,?)}";
	
	private String weekOf ="'2017-10-22'";
	private String weekInitial ="'2017-10-22'";
	private int day;        
	private String month; 
	private int mnth;
	private int year;
	private int row;
	private int col;
	
	private Color LtBlue = Color.decode("#e8e8ff");	
	private Color DkBlue = Color.decode("#174082");
	private Color LtGray = Color.decode("#eeeeee");	
	private Color inst1 = Color.decode("#eeffd6");
	private Color inst2 = Color.decode("#fff1e2");
	private Color inst3 = Color.decode("#e2fff5");
	private Color inst4 = Color.decode("#ffe2f8");
	private Color[] instColors = {inst1, inst2, inst3, inst4};
	
	private Calendar cal = new GregorianCalendar();
	
	private ImageIcon left;
	private ImageIcon right;
	private JButton backBtn;
	private JButton forBtn;
	
	private String invoiceNum = "";  
	private ResultSet rs;
	
	private Boolean rowSelected = false;
	private Boolean showSchedule = false;
	
	private CreateConnection connecting;
	
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
	
	private JTextArea detailsTxtArea;
	private JTextArea stockTxtArea;
	
	private JLabel instLbl;
	private JTextField instTxtBx;
	private JLabel dateLbl;
	private JTextField dateTxtBx;
    
	private JButton cancelPermitReqBtn; 
	private JButton editBookingBtn; 
	
	private CreateConnection conn;
		
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
	    detailsTxtArea.setBackground(LtGray);
	    detailsTxtArea.setLineWrap(true);
	    detailsTxtArea.setEditable(false);
	    infoPanel.add(detailsTxtArea);
	    
	    stockTxtArea = new JTextArea("");
	    stockTxtArea.setBounds(270, 20, 430, 120);
	    stockTxtArea.setBorder(BorderFactory.createEtchedBorder());
	    stockTxtArea.setBackground(LtGray);
	    stockTxtArea.setTabSize(6);
	    stockTxtArea.setLineWrap(true);
	    stockTxtArea.setEditable(false);
	    infoPanel.add(stockTxtArea);
	    
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
					 //  renderTable();
				 
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
		//			pp.setFormsLocked();
					try{
						invoiceNum = installTbl.getValueAt(installTbl.getSelectedRow(), 0).toString();

					stockTxtArea.setText(ip.DisplayStockOnOrder(invoiceNum));
					detailsTxtArea.setText(ip.DisplayClientShortDetails(invoiceNum));
					
					instTxtBx.setText(ip.getInstaller());        
					dateTxtBx.setText(ip.getInstTime());
					
					
					} catch (IndexOutOfBoundsException e){
						//Ignoring IndexOutOfBoundsExceptions!
					}
					}
				}
		  	});
	  	timeTbl.addMouseListener(new java.awt.event.MouseAdapter() {
	  		@Override
	  		 public void mouseClicked(java.awt.event.MouseEvent evt) {
			  		    row = timeTbl.rowAtPoint(evt.getPoint());
	  		    col = timeTbl.columnAtPoint(evt.getPoint());
	  		    if (col >= 2) {		

						if (timeTbl.getValueAt(row, col) == null || timeTbl.getValueAt(row, col).equals("")) {
							String inst = timeTbl.getValueAt(row,0).toString();
							String tme = timeTbl.getValueAt(row,1).toString();	
							String dte;
							Date instDate;
							
							DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
							Date weekDate;
							try {
							    weekDate = df.parse(weekOf);
					
								GregorianCalendar cl = new GregorianCalendar();
								cl.setTime(weekDate);
								
				//				instDate = cl.add(Calendar.DATE, (col-2));
								
				//				instDate = addDays(weekDate, (col-2));
							    
				//				dte = df.format(instDate);
							    
							    
							    
					//		    System.out.println(newDateString);
							} catch (ParseException e) {
							//    e.printStackTrace();
							}
							
						try {
				        	int input = JOptionPane.showConfirmDialog(null,"Make Booking for Install " + invoiceNum + "?\n "
				        			+ tme + " BY: " + inst,  "Place Booking?", JOptionPane.YES_NO_OPTION);
				        	if (input == 0){
				        		

							resetTable();	        		
							ip.showMessage("Updating Booking");
				        	} else{
								
				        	}
							/*
								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
								LocalDate date = LocalDate.parse("2017-10-22", formatter);
								date = date.plusDays(col-2);
								ip.showMessage("SLOT: " + inst + ", " + tme + " ON: " + date.toString());	
								*/
							}
							catch (DateTimeParseException exc) {
							}								
						}else {
							JOptionPane.showMessageDialog(null, timeTbl.getValueAt(row, col).toString());
						}
					}
			  		 	}
	  		});
		// Calendar for today
		cal.getInstance();   
	    day = cal.get(Calendar.DAY_OF_MONTH);           
	    mnth = cal.get(Calendar.MONTH)+1;
	    month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
	    year = cal.get(Calendar.YEAR);
	    
	    weekInitial = "'"+ year +  "-" +  month + "-" +  day + "'";
	    resetSchedule();
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
//	label.setText("WEEK OF: " + day + "-" +month + "-" + year);
    	    	  
	int numberOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

	weekOf = "'" + year + "-" +  mnth + "-" +   day + "'";
	
	ResultSet rs = schP.getResults(0, weekOf );
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
		spaceSchedule();
		
		JTableHeader th = timeTbl.getTableHeader();		

		TableColumn tc = timeTbl.getColumnModel().getColumn(0);			    
		tc.setCellRenderer(new installerColumnRenderer());
		tc = timeTbl.getColumnModel().getColumn(1);			    
		tc.setCellRenderer(new installerColumnRenderer());
		tc = timeTbl.getColumnModel().getColumn(2);			    
		tc.setCellRenderer(new installerColumnRenderer());
		tc = timeTbl.getColumnModel().getColumn(3);			    
		tc.setCellRenderer(new installerColumnRenderer());
		tc = timeTbl.getColumnModel().getColumn(4);			    
		tc.setCellRenderer(new installerColumnRenderer());
		tc = timeTbl.getColumnModel().getColumn(5);			    
		tc.setCellRenderer(new installerColumnRenderer());
		tc = timeTbl.getColumnModel().getColumn(6);			    
		tc.setCellRenderer(new installerColumnRenderer());
		tc = timeTbl.getColumnModel().getColumn(7);			    
		tc.setCellRenderer(new installerColumnRenderer());
		tc = timeTbl.getColumnModel().getColumn(8);			    
		tc.setCellRenderer(new installerColumnRenderer());		
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
    
    class installerColumnRenderer extends DefaultTableCellRenderer
    {		         
       public installerColumnRenderer() {
          super();
       }

       public Component getTableCellRendererComponent
            (JTable table, Object value, boolean isSelected,
             boolean hasFocus, int row, int column)
       {
          Component cell = super.getTableCellRendererComponent
             (table, value, isSelected, hasFocus, row, column);
      if (column == 0 || column == 1 ){	
    	  cell.setBackground( instColors[(row/2)%4]);  
          cell.setForeground( DkBlue );		          
          cell.setFont(cell.getFont().deriveFont(Font.BOLD));	
          setHorizontalAlignment(SwingConstants.CENTER);
          return cell;
      }else if (column == 2 || column == 8 ){
          cell.setBackground( LtGray);  
          cell.setForeground( DkBlue );
          cell.setFont(cell.getFont().deriveFont(12, Font.BOLD));	
    	  return cell;
      }else {
//    	  JLabel l = (JLabel)cell;
    	  
    	  String contents = (String)value;
    	  ((JComponent) cell).setToolTipText(contents);		    	  		    	  
    	  cell.setBackground( instColors[(row/2)%4]);

    	  return cell;
      }

       }
    }
protected void clearStock() {
	detailsTxtArea.setText("");
	stockTxtArea.setText("");
	tmod2.setRowCount(0);
}
/*protected void resetTimeTable() {
	
	ResultSet rs = s.getResults(0, weekOf );
	timeTbl.setModel(DbUtils.resultSetToTableModel(rs)); 
	  				
	spaceHeader();			  	
//	param = "";  
	timeTbl.clearSelection();
	rowSelected=false;
	this.updateWeek();
}
*/
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
}	

public JTable getInstallTbl(){
	return installTbl;
}
public JPanel getInfoPanel(){
	return infoPanel;
}
}
