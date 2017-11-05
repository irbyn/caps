package Sales;

//Description: This class allows a salesperson to book a site check for the sale


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Main.GetJobs;
import net.proteanit.sql.DbUtils;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;


class SiteCheckPanel extends JPanel {
	private String siteDetails = "call AWS_WCH_DB.dbo.s_SalesSiteCheckDetails";
	private String updateSite = "call AWS_WCH_DB.dbo.s_SalesUpdateSiteCheck";
	private String getInstID = "call AWS_WCH_DB.dbo.s_SaleGetInstID";
	private int [] columnWidth = {50, 50, 100, 100, 50, 100, 60, 50, 50, 70};
	private String param = "";
	private String paramSID = "";
	private ResultSet rs;
	private Color LtGray = Color.decode("#eeeeee");
	private CreateConnection connecting;
	private ConnDetails conDeets;
	private SalesPane sp;
	private DefaultTableModel model1;
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable salesTbl;
	private JTextArea custInfoTxtArea;
	private JLabel sChkBookingLbl;
	private JLabel siteCheckByLbl;
	private JSpinner dateSpn;
	private JComboBox<String> timeComboBox;
	private JComboBox<String> sChkDoneByComBx;
	private JButton cancelBtn;
	private JButton saveBtn;
	private Boolean rowSelected;
	private JCheckBox sCheckBookedChckBx;
	private JCheckBox sCheckCompChckBx;
	private Date date;
	private TimeZone timeZone;

	public SiteCheckPanel(ConnDetails conDeets, SalesPane sp) {

		timeZone = TimeZone.getTimeZone("NZDT");

		this.sp = sp;
		this.conDeets = conDeets;

		connecting = new CreateConnection();
		rowSelected = false;
		model1 = new DefaultTableModel();  
		model1.setRowCount(0);
		salesTbl = new JTable(model1);
		salesTbl.setAutoCreateRowSorter(true);

		JScrollPane scrollPane = new JScrollPane(salesTbl);

		header= salesTbl.getTableHeader();
		columnModel = header.getColumnModel();
		add(header); 

		//Panel for the table
		tablePanel = new JPanel();
		tablePanel.setBounds(20, 20, 1025, 260);       
		tablePanel.setLayout(new BorderLayout());

		//Content panel
		infoPanel = new JPanel();
		infoPanel.setBounds(0, 280, 1077, 289);  
		infoPanel.setLayout(null);

		custInfoTxtArea = new JTextArea("");
		custInfoTxtArea.setBounds(20, 20, 250, 260);
		custInfoTxtArea.setBorder(BorderFactory.createEtchedBorder());
		custInfoTxtArea.setBackground(LtGray);
		custInfoTxtArea.setLineWrap(true);
		custInfoTxtArea.setEditable(false);
		infoPanel.add(custInfoTxtArea);

		sChkBookingLbl = new JLabel("Site Check Booking:");
		sChkBookingLbl.setBounds(478, 68, 128, 14);
		infoPanel.add(sChkBookingLbl);

		date = new java.sql.Date(Calendar.getInstance(timeZone).getTime().getTime());

		dateSpn = new JSpinner(new SpinnerDateModel());
		dateSpn.setEditor(new JSpinner.DateEditor(dateSpn, "dd.MMM.yyyy"));
		dateSpn.setBounds(634, 65, 130, 20);
		dateSpn.setValue(date);
		infoPanel.add(dateSpn);

		SpinnerDateModel modelTime = new SpinnerDateModel();
		modelTime.setCalendarField(Calendar.MINUTE);

		timeComboBox = new JComboBox();
		timeComboBox.setModel(new DefaultComboBoxModel(new String[] {"8:00", "8:30", "9:00", "9:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00"}));
		timeComboBox.setBounds(774, 65, 144, 20);
		timeComboBox.setBackground(Color.WHITE);
		infoPanel.add(timeComboBox);
		timeComboBox.setSelectedIndex(8);

		sChkDoneByComBx = new JComboBox<String>();
		sChkDoneByComBx.setBackground(Color.WHITE);
		sChkDoneByComBx.setBounds(634, 115, 284, 20);
		infoPanel.add(sChkDoneByComBx);

		GetJobs job = new GetJobs(conDeets);
		String[] SC = job.getSiteChecker();

		DefaultComboBoxModel<String> modelSC = new DefaultComboBoxModel<String>( SC );
		sChkDoneByComBx.setModel(modelSC);
		sChkDoneByComBx.setSelectedItem(null);

		siteCheckByLbl = new JLabel("Site Check By:");
		siteCheckByLbl.setBounds(478, 118, 128, 14);
		infoPanel.add(siteCheckByLbl);

		cancelBtn = new JButton("Cancel");
		cancelBtn.setBounds(478, 209, 148, 23);
		infoPanel.add(cancelBtn);

		saveBtn = new JButton("Save Details");
		saveBtn.setBounds(770, 209, 148, 23);
		infoPanel.add(saveBtn);

		this.setLayout(null);
		this.add(tablePanel); 
		this.add(infoPanel);

		sCheckBookedChckBx = new JCheckBox("Booked");
		sCheckBookedChckBx.setBounds(634, 165, 97, 23);
		infoPanel.add(sCheckBookedChckBx);

		sCheckCompChckBx = new JCheckBox("Completed");
		sCheckCompChckBx.setBounds(834, 165, 97, 23);
		infoPanel.add(sCheckCompChckBx);

		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(salesTbl.getTableHeader(), BorderLayout.NORTH);

		salesTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					//rowSelected=true;
					try{
						param = salesTbl.getValueAt(salesTbl.getSelectedRow(), 1).toString();
						paramSID = salesTbl.getValueAt(salesTbl.getSelectedRow(), 0).toString();
						custInfoTxtArea.setText(sp.DisplayClientDetails(param));
						displaySiteDetails(paramSID);
						rowSelected=true;
					} catch (IndexOutOfBoundsException e){

					}
				}
			}
		});	

		saveBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) { 

				if(rowSelected){
					//Check to see if the user is sure about creating the customer
					if (!validatedata()){
						sp.showMessage("Updating Sale");
						updateSiteCheck(paramSID);
						//Displaying confirmation message
						if (sCheckCompChckBx.isSelected()){
							JOptionPane.showMessageDialog(null, sp.getCustName() + " has been moved to quotes\n"
									+ "They will be in the quotes table once the\n"
									+ "sitecheck date has passed");
						}
						resetTable();
						clearFields();				
					}
				}else{
					JOptionPane.showMessageDialog(null, "You must select a row first.");
				}
			}
		});

		cancelBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (checkTxtBx()) {
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to cancel?","Warning",dialogButton);
					if(dialogResult == JOptionPane.YES_OPTION){
						resetTable();
						rowSelected=false;
						clearFields();
					}
				}

			}
		});

	}

	protected void resetTable() {
		ResultSet rs = sp.getResults(2);
		salesTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
		spaceHeader();
		rowSelected=false;
		param = "";
		custInfoTxtArea.setText("");
	}	

	public JTable getSalesTbl(){
		return salesTbl;
	}

	public Boolean checkTxtBx(){
		Boolean newData = false;
		//add in combx for Install type and the date spinner 
		if (!custInfoTxtArea.getText().equals("") 
				|| !(sChkDoneByComBx.getSelectedIndex() == 0) || sCheckBookedChckBx.isSelected() || sCheckCompChckBx.isSelected()){
			newData = true;
		}
		return newData;
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

	public void clearFields(){
		//Set all the text boxes to blank
		dateSpn.setValue(date);
		timeComboBox.setSelectedIndex(8);
		sChkDoneByComBx.setSelectedItem(null);
		sCheckBookedChckBx.setSelected(false);
		sCheckCompChckBx.setSelected(false);
	}

	public boolean validatedata(){
		Boolean errorChk = false;
		String error = "";

		Calendar yesterday = Calendar.getInstance(timeZone);
		yesterday.add(Calendar.DATE, -1);

		Date dateVal = (Date) dateSpn.getValue();	

		if (dateVal.before(yesterday.getTime())){
			errorChk = true;
			error = error + "DATE: must be set for the future\n";
		}		
		if (sChkDoneByComBx.getSelectedItem() == null){
			errorChk = true;
			error = error + "DONE BY: can not be empty\n";
		} 
		if (!sCheckBookedChckBx.isSelected() && !sCheckCompChckBx.isSelected()){
			errorChk = true;
			error = error + "BOOKED: please select if the site check is \nbooked or completed\n";
		}

		//Check to see if any errors has occured
		if (errorChk == true){
			JOptionPane.showMessageDialog(null, error);
		}
		return errorChk;
	}

	public void displaySiteDetails(String parameter){
		CallableStatement sm = null;
		try {

			String update = "{" + siteDetails +"(?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			sm = conn.prepareCall(update);
			sm.setInt(1, Integer.parseInt(parameter));

			rs = sm.executeQuery();	 
			conn.close();
			if (rs==null){
				JOptionPane.showMessageDialog(null, "null query");
			}
			else
			{	
				while (rs.next()){

					Date SCdate 				= rs.getDate("Site Check Date");
					Time time 					= rs.getTime("Site Check Time");
					Boolean booked 				= rs.getBoolean("Site Check Booked");
					String salesPerson 			= rs.getString("Done By");

					if (SCdate==null){
						dateSpn.setValue(date);
					}else{
						dateSpn.setValue(SCdate);
					}
					if (time == null ){
						timeComboBox.setSelectedItem(null);
					}else{
						if (time.toString().equals("08:00:00")){
							timeComboBox.setSelectedIndex(0);
						}else if (time.toString().equals("08:30:00")){
							timeComboBox.setSelectedIndex(1);
						}else if (time.toString().equals("09:00:00")){
							timeComboBox.setSelectedIndex(2);
						}else if (time.toString().equals("09:30:00")){
							timeComboBox.setSelectedIndex(3);
						}else if (time.toString().equals("10:00:00")){
							timeComboBox.setSelectedIndex(4);
						}else if (time.toString().equals("10:30:00")){
							timeComboBox.setSelectedIndex(5);
						}else if (time.toString().equals("11:00:00")){
							timeComboBox.setSelectedIndex(6);
						}else if (time.toString().equals("11:30:00")){
							timeComboBox.setSelectedIndex(7);
						}else if (time.toString().equals("12:00:00")){
							timeComboBox.setSelectedIndex(8);
						}else if (time.toString().equals("12:30:00")){
							timeComboBox.setSelectedIndex(9);
						}else if (time.toString().equals("13:00:00")){
							timeComboBox.setSelectedIndex(10);
						}else if (time.toString().equals("13:30:00")){
							timeComboBox.setSelectedIndex(11);
						}else if (time.toString().equals("14:00:00")){
							timeComboBox.setSelectedIndex(12);
						}else if (time.toString().equals("14:30:00")){
							timeComboBox.setSelectedIndex(13);
						}else if (time.toString().equals("15:00:00")){
							timeComboBox.setSelectedIndex(14);
						}else if (time.toString().equals("15:30:00")){
							timeComboBox.setSelectedIndex(15);
						}else if (time.toString().equals("16:00:00")){
							timeComboBox.setSelectedIndex(16);
						}else if (time.toString().equals("16:30:00")){
							timeComboBox.setSelectedIndex(17);
						}else if (time.toString().equals("17:00:00")){
							timeComboBox.setSelectedIndex(18);
						}else if (time.toString().equals("17:30:00")){
							timeComboBox.setSelectedIndex(19);
						}else if (time.toString().equals("18:00:00")){
							timeComboBox.setSelectedIndex(20);
						}else if (time.toString().equals("18:30:00")){
							timeComboBox.setSelectedIndex(21);
						}else if (time.toString().equals("19:00:00")){
							timeComboBox.setSelectedIndex(22);
						}else if (time.toString().equals("19:30:00")){
							timeComboBox.setSelectedIndex(23);
						}else if (time.toString().equals("20:00:00")){
							timeComboBox.setSelectedIndex(24);
						}	
					}
					if (booked==false){
						sCheckBookedChckBx.setSelected(false);
						//group.clearSelection();
					}else{
						sCheckBookedChckBx.setSelected(true);
					}
					if(salesPerson == null){
						sChkDoneByComBx.setSelectedItem(null);
					}
					else{
						sChkDoneByComBx.setSelectedItem(salesPerson);
					}
				}
			}
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}
	}

	protected void updateSiteCheck(String parameter) {

		CallableStatement sm = null;
		try {

			String update = "{" + updateSite +"(?,?,?,?,?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			sm = conn.prepareCall(update);

			sm.setString(1, parameter);
			sm.setString(2, getDate());
			sm.setString(3, getTime());
			sm.setInt(4, getInstID());

			sm.setBoolean(5, getBooked());
			sm.setBoolean(6, getCompleted());

			sm.executeUpdate();
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

	public String getDate(){  	
		Date dte = (Date) dateSpn.getValue(); 
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String dt = sdf1.format(dte);
		return dt; 
	}

	public String getTime(){
		java.sql.Time ppstime = null;
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		java.util.Date d1;
		try {
			d1 = (java.util.Date)format.parse((String) timeComboBox.getSelectedItem());
			ppstime = new java.sql.Time(d1.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (String) ppstime.toString();
	}

	public int getInstID(){
		//get the ID from what's in the combo box
		int slsID = 0;
		String slSName = (String) sChkDoneByComBx.getSelectedItem();
		CallableStatement sm = null;
		try {
			String update = "{" + getInstID +"(?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	
			sm = conn.prepareCall(update);

			sm.setString(1, slSName);

			ResultSet rs = sm.executeQuery();
			conn.close();
			while (rs.next()){
				slsID = rs.getInt("UserID");
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
		return slsID;
	}

	public Boolean getBooked(){
		if (sCheckBookedChckBx.isSelected()){
			return true;
		}else{
			return false;
		}
	}

	public boolean getCompleted(){
		if (sCheckCompChckBx.isSelected()){
			return true;
		}else{
			return false;
		}
	}

	public JPanel getInfoPanel(){
		return infoPanel;
	}
}
