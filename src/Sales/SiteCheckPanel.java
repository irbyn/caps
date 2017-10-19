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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
	//private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
	//private String result3 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
	private String updateSite = "call AWS_WCH_DB.dbo.s_SalesUpdateSiteCheck";
	private int [] columnWidth = { 50, 50, 100, 100, 100};
	private String param = "";  
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
	private JRadioButton rdbtnSCheckBooked;
	private JRadioButton rdbtnSCheckCompleted;

	public SiteCheckPanel(ConnDetails conDeets, SalesPane sp) {

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

		/*		SimpleDateFormat dt = new SimpleDateFormat("dd.MMM.yyyy");
		spnTimeDate = new JSpinner(new SpinnerDateModel());
		spnTimeDate.setEditor(new JSpinner.DateEditor(spnTimeDate, dt.toPattern()));
		//spnTimeDate.setBounds(634, 65, 284, 20);
		infoPanel.add(spnTimeDate);*/

		spnDate = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnDate, "dd.MMM.yyyy");
		spnDate.setEditor(dateEditor);
		spnDate.setBounds(634, 65, 130, 20);
		spnDate.setValue(new Date());
		infoPanel.add(spnDate);

		spnTime = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spnTime, "HH:mm");
		spnTime.setEditor(timeEditor);
		spnTime.setBounds(784, 65, 130, 20);
		spnTime.setValue(new Date());
		infoPanel.add(spnTime);


		comBxSChkDoneBy = new JComboBox<String>();
		comBxSChkDoneBy.setBackground(Color.WHITE);
		comBxSChkDoneBy.setBounds(634, 115, 284, 20);
		infoPanel.add(comBxSChkDoneBy);


		GetJobs job = new GetJobs(conDeets);

		//String[] installUr = job.getInstallers();

		/*DefaultComboBoxModel model = new DefaultComboBoxModel( installUr );
			      comBxSChkDoneBy.setModel( model );   
		 */   
		String[] SC = job.getSiteChecker();

		DefaultComboBoxModel<String> modelSC = new DefaultComboBoxModel<String>( SC );
		comBxSChkDoneBy.setModel(modelSC);
		comBxSChkDoneBy.setSelectedItem(null);

		//GetJobs job = new GetJobs(conDeets);
		/*		String[] installType = job.getInstallers();

		DefaultComboBoxModel model = new DefaultComboBoxModel( installType );
		comBxSChkDoneBy.setModel( model );*/
		/*		
		String[] SC = job.getSiteChecker();
		DefaultComboBoxModel<String> modelSC = new DefaultComboBoxModel<String>( SC );
		comBxSChkDoneBy.setModel( modelSC );*/


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
		
		rdbtnSCheckBooked = new JRadioButton("Site Check Booked");
		rdbtnSCheckBooked.setBounds(634, 168, 128, 23);
		infoPanel.add(rdbtnSCheckBooked);
		
		rdbtnSCheckCompleted = new JRadioButton("Site Check Completed");
		rdbtnSCheckCompleted.setBounds(770, 168, 150, 23);
		infoPanel.add(rdbtnSCheckCompleted);
		
		group = new ButtonGroup();
		group.add(rdbtnSCheckBooked);
		group.add(rdbtnSCheckCompleted);

		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(salesTbl.getTableHeader(), BorderLayout.NORTH);

		btnSave.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) { 

				if(rowSelected){
					//If validate data is false then there is no error
					//if (validatedata() == false){
						//Check to see if the user is sure about creating the customer
						int dialogButton = JOptionPane.YES_NO_OPTION;
						int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to create this customer","Warning",dialogButton);
						if(dialogResult == JOptionPane.YES_OPTION){
							// Saving code here -- add customer to the database
							sp.showMessage("Updating Consent Received");
							updateCustomer();
							resetTable();
							clearFields();
						}
					//}
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
						//Resetting the other attributes within the tab
						spnDate.setValue(new Date());
						spnTime.setValue(new Date());
						comBxSChkDoneBy.setSelectedItem(null);
						rdbtnSCheckCompleted.setSelected(false);
						group.clearSelection();
					}
				}

			}
		});



		salesTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					//rowSelected=true;
					try{
						param = salesTbl.getValueAt(salesTbl.getSelectedRow(), 1).toString();
						//displayClientDetails(param);
						txtAreaCustInfo.setText(sp.DisplayClientDetails(param));
						rowSelected=true;
					} catch (IndexOutOfBoundsException e){

					}
				}
			}
		});	

	}

	protected void resetTable() {
		//Fix this little null error 
		ResultSet rs = sp.getResults(2);
		salesTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
		spaceHeader();
		//sentChk.setSelected(false);
		//rowSelected=false;
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
				|| !(comBxSChkDoneBy.getSelectedIndex() == 0) || rdbtnSCheckBooked.isSelected() || rdbtnSCheckCompleted.isSelected()){
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
		spnDate.setValue(new Date());
		spnTime.setValue(new Date());
		comBxSChkDoneBy.setSelectedItem(null);
		rdbtnSCheckBooked.setSelected(false);
		rdbtnSCheckCompleted.setSelected(false);
	}
	
	public boolean validatedata(){
		Boolean errorChk = false;
		String error = " ";
		//Check if all text boxes are all filled in correctly
		//List<String> errors = new ArrayList<String>();

		//Validate year 
		if (!spnDate.getValue().equals(new Date())){
			errorChk = true;
			//Cannot be null or more than 15 chars
			error = error + "FIRST NAME: can not be empty\n";
		}
		
		//validate time 
		 
		
		//validate
		if (comBxSChkDoneBy.getSelectedItem() == null){
			errorChk = true;
			error = error + "DONE BY: can not be empty\n";
		} 
		
		//Check to see if any errors has occured
		if (errorChk == true){
			JOptionPane.showMessageDialog(null, error);
		}
		return errorChk;
	}

	protected void updateCustomer() {

		CallableStatement sm = null;
		try {

			String update = "{" + updateSite +"(?,?,?,?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			sm = conn.prepareCall(update);

			sm.setString(1, param);
			sm.setString(2, getDate());
			sm.setString(3, getTime());
			//sm.setString(4, getDoneBy());
			sm.setBoolean(4, getBooked());
			sm.setBoolean(5, getCompleted());

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
       	SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
    	String dt = sdf1.format(dte);
    	return dt;
    }
	public String getTime(){
		return (String) spnTime.getValue();
	}
	public String getDoneBy(){
		return (String) comBxSChkDoneBy.getSelectedItem();
	}
	public Boolean getBooked(){
		if (rdbtnSCheckBooked.isSelected()){
			return true;
		}else{
			return false;
		}
	}
	
	public Boolean getCompleted(){
		if (rdbtnSCheckCompleted.isSelected()){
			return true;
		}else{
			return false;
		}
	}
}
