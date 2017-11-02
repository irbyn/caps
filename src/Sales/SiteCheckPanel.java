package Sales;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
//import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
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
import javax.swing.table.TableModel;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Main.GetJobs;
import net.proteanit.sql.DbUtils;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

class SiteCheckPanel extends JPanel {
	private String siteDetails = "call AWS_WCH_DB.dbo.s_SalesSiteCheckDetails";
	private String updateSite = "call AWS_WCH_DB.dbo.s_SalesUpdateSiteCheck";
	private String getInstID = "call AWS_WCH_DB.dbo.s_SaleGetInstID";
	private int [] columnWidth = {50, 50, 100, 70, 100, 100, 60, 50, 50};
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

	private JTextArea txtAreaCustInfo;
	private JLabel lblSChkBooking;
	private JLabel lblSiteCheckBy;
	private JSpinner spnDate;
	private JSpinner spnTime;
	private JComboBox<String> comBxSChkDoneBy;
	private JButton btnCancel;
	private JButton btnSave;
	private ButtonGroup group;
	private Boolean rowSelected;
	//private JRadioButton rdbtnSCheckBooked;
	//private JRadioButton rdbtnSCheckCompleted;
	private JCheckBox chckBxSCheckBooked;
	private JCheckBox chckBxSCheckComp;
	private Date date;
	private int userID;
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
		tablePanel.setBounds(20, 20, 1025, 260);  //setPreferredSize(new Dimension(0, 300));      
		tablePanel.setLayout(new BorderLayout());

		//Content panel
		infoPanel = new JPanel();
		infoPanel.setBounds(0, 280, 1077, 289);  //setPreferredSize(new Dimension(0, 300));
		infoPanel.setLayout(null);

		/*		txtAreaCustInfo = new JTextArea();
		txtAreaCustInfo.setEditable(false);
		txtAreaCustInfo.setBounds(23, 24, 382, 237);
		infoPanel.add(txtAreaCustInfo);*/

		txtAreaCustInfo = new JTextArea("");
		txtAreaCustInfo.setBounds(20, 20, 250, 260);
		txtAreaCustInfo.setBorder(BorderFactory.createEtchedBorder());
		txtAreaCustInfo.setBackground(LtGray);
		txtAreaCustInfo.setLineWrap(true);
		txtAreaCustInfo.setEditable(false);
		infoPanel.add(txtAreaCustInfo);

		lblSChkBooking = new JLabel("Site Check Booking:");
		lblSChkBooking.setBounds(478, 68, 128, 14);
		infoPanel.add(lblSChkBooking);

		date = new java.sql.Date(Calendar.getInstance(timeZone).getTime().getTime());

		spnDate = new JSpinner(new SpinnerDateModel());
		spnDate.setEditor(new JSpinner.DateEditor(spnDate, "dd.MMM.yyyy"));
		spnDate.setBounds(634, 65, 130, 20);
		spnDate.setValue(date);
		infoPanel.add(spnDate);

		/*		spnDate.addActionListener( new ActionListener(){

		});*/

		SpinnerDateModel modelTime = new SpinnerDateModel();
		modelTime.setCalendarField(Calendar.MINUTE);

		//SimpleDateFormat tmModel = new SimpleDateFormat("HH:MM");
		//spnTime = new JSpinner(modelTime);
		spnTime = new JSpinner(new SpinnerDateModel());
		spnTime.setEditor(new JSpinner.DateEditor(spnTime,"dd.MMM.yyyy HH:MM"));
		spnTime.setBounds(784, 65, 130, 20);
		spnTime.setValue(date);
		infoPanel.add(spnTime);

		System.out.println(spnTime.getValue());

		comBxSChkDoneBy = new JComboBox<String>();
		comBxSChkDoneBy.setBackground(Color.WHITE);
		comBxSChkDoneBy.setBounds(634, 115, 284, 20);
		infoPanel.add(comBxSChkDoneBy);

		GetJobs job = new GetJobs(conDeets);
		String[] SC = job.getSiteChecker();

		DefaultComboBoxModel<String> modelSC = new DefaultComboBoxModel<String>( SC );
		comBxSChkDoneBy.setModel(modelSC);
		comBxSChkDoneBy.setSelectedItem(null);

		lblSiteCheckBy = new JLabel("Site Check By:");
		lblSiteCheckBy.setBounds(478, 118, 128, 14);
		infoPanel.add(lblSiteCheckBy);

		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(478, 209, 148, 23);
		infoPanel.add(btnCancel);

		btnSave = new JButton("Save Details");
		btnSave.setBounds(770, 209, 148, 23);
		infoPanel.add(btnSave);

		this.setLayout(null);
		this.add(tablePanel); 
		this.add(infoPanel);

		chckBxSCheckBooked = new JCheckBox("Booked");
		chckBxSCheckBooked.setBounds(634, 165, 97, 23);
		infoPanel.add(chckBxSCheckBooked);

		chckBxSCheckComp = new JCheckBox("Completed");
		chckBxSCheckComp.setBounds(834, 165, 97, 23);
		infoPanel.add(chckBxSCheckComp);

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
						txtAreaCustInfo.setText(sp.DisplayClientDetails(param));
						displaySiteDetails(paramSID);
						rowSelected=true;
					} catch (IndexOutOfBoundsException e){

					}
				}
			}
		});	

		btnSave.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) { 

				if(rowSelected){
					//Check to see if the user is sure about creating the customer
					/*int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to update this sale?","Warning",dialogButton);
					if(dialogResult == JOptionPane.YES_OPTION){*/
					if (!validatedata()){
						// Saving code here -- add customer to the database
						sp.showMessage("Updating Sale");
						updateSiteCheck(paramSID);
						//Displaying confirmation message
						if (chckBxSCheckComp.isSelected()){
							JOptionPane.showMessageDialog(null, sp.getCustName() + " has been moved to quotes\n"
									+ "They will be in the quotes table once the\n"
									+ "sitecheck date has passed");
						}
						resetTable();
						clearFields();
						//}						

					}
				}else{
					JOptionPane.showMessageDialog(null, "You must select a row first.");
				}
			}
		});

		btnCancel.addActionListener( new ActionListener()
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
		txtAreaCustInfo.setText("");
	}	

	public JTable getSalesTbl(){
		return salesTbl;
	}

	public Boolean checkTxtBx(){
		Boolean newData = false;
		//add in combx for Install type and the date spinner 
		if (!txtAreaCustInfo.getText().equals("") 
				|| !(comBxSChkDoneBy.getSelectedIndex() == 0) || chckBxSCheckBooked.isSelected() || chckBxSCheckComp.isSelected()){
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
		spnDate.setValue(date);
		spnTime.setValue(date);
		comBxSChkDoneBy.setSelectedItem(null);
		chckBxSCheckBooked.setSelected(false);
		chckBxSCheckComp.setSelected(false);
	}

	public boolean validatedata(){
		Boolean errorChk = false;
		String error = "";

		Calendar yesterday = Calendar.getInstance(timeZone);
		yesterday.add(Calendar.DATE, -1);

		Date dateVal = (Date) spnDate.getValue();	
		Date timeVal = (Date) spnTime.getValue();

		System.out.println("SPN Date " + spnDate.getValue());
		System.out.println("SPN Time " + spnTime.getValue());

		TimeZone timeZone = TimeZone.getTimeZone("NZDT");
		//Calendar calendar = Calendar.getInstance(timeZone);

		Calendar bef = Calendar.getInstance(timeZone);
		bef.setTime(dateVal);
		bef.set(Calendar.AM_PM, Calendar.PM);
		bef.set(Calendar.HOUR, 19);		
		Date befo = bef.getTime();

		Calendar aft = Calendar.getInstance(timeZone);		
		aft.setTime(dateVal);
		aft.set(Calendar.AM_PM, Calendar.PM);
		aft.set(Calendar.HOUR, 7);
		//aft.clear(Calendar.MILLISECOND);	
		System.out.println("Date Value: " + dateVal);
		System.out.println("Before: " + bef.getTime());
		System.out.println("after: " +aft.getTime());
		System.out.println("TimeValue: " +timeVal);

		Date afte = aft.getTime();

		if (dateVal.before(yesterday.getTime())){
			errorChk = true;
			error = error + "DATE: must be set for the future\n";
		}		
		if (timeVal.before(befo) || timeVal.after(afte)){
			errorChk = true;
			error = error + "TIME: must be between 8am and 8pm\n";
		}
		if (comBxSChkDoneBy.getSelectedItem() == null){
			errorChk = true;
			error = error + "DONE BY: can not be empty\n";
		} 
		if (!chckBxSCheckBooked.isSelected() && !chckBxSCheckComp.isSelected()){
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
					userID						= rs.getInt("User ID");

					if (SCdate==null){
						spnDate.setValue(date);
					}else{
						spnDate.setValue(SCdate);
					}
					if (time == null ){
						spnTime.setValue(date);
					}else{
						spnTime.setValue(time);
					}
					if (booked==false){
						chckBxSCheckBooked.setSelected(false);
						//group.clearSelection();
					}else{
						chckBxSCheckBooked.setSelected(true);
					}
					if(salesPerson == null){
						comBxSChkDoneBy.setSelectedItem(null);
					}
					else{
						comBxSChkDoneBy.setSelectedItem(salesPerson);
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

		Date dte = (Date) spnDate.getValue(); 
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String dt = sdf1.format(dte);
		return dt; 
	}

	public String getTime(){
		Date tim = (Date) spnTime.getValue(); 
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:MM");
		String tm = sdf2.format(tim);
		return tm;
	}

	public int getInstID(){
		//get the ID from what's in the combo box
		int slsID = 0;
		String slSName = (String) comBxSChkDoneBy.getSelectedItem();
		CallableStatement sm = null;
		try {
			String update = "{" + getInstID +"(?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	
			sm = conn.prepareCall(update);

			sm.setString(1, slSName);

			ResultSet rs = sm.executeQuery();

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
		if (chckBxSCheckBooked.isSelected()){
			return true;
		}else{
			return false;
		}
	}

	public boolean getCompleted(){
		if (chckBxSCheckComp.isSelected()){
			return true;
		}else{
			return false;
		}
	}

	public JPanel getInfoPanel(){
		return infoPanel;
	}

}
