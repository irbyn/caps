package Sales; 

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Main.GetJobs;
import Main.validator;
import net.proteanit.sql.DbUtils;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class EstimationPanel extends JPanel {
	private JTextField txtBxFire;
	private JTextField txtBxPrice;
	private JTextField txtBxComment;	
	private int [] columnWidth = {50, 50, 70, 150, 100, 100, 100, 50, 50, 50}; 
	private String getEstimationDetails = "call AWS_WCH_DB.dbo.s_SalesEstimationDetails";
	private String updateEstimation = "call AWS_WCH_DB.dbo.s_SalesUpdateEstimation";
	private String addEstimationDate = "call AWS_WCH_DB.dbo.s_SalesUpdateEstimationDate";
	private String getInstID = "call AWS_WCH_DB.dbo.s_SaleGetInstTypeID";
	private String getSlsID = "call AWS_WCH_DB.dbo.s_SaleGetSlsID";
	private String getEmBody = "call AWS_WCH_DB.dbo.s_SaleGetEmailBody";
	private String param = ""; 
	private String paramSID = ""; 
	private ResultSet rs;
	private Boolean rowSelected = false;
	private CreateConnection connecting;
	private JTableHeader header;
	private TableColumnModel columnModel;
	private Color LtGray = Color.decode("#eeeeee");
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable salesTbl;
	private DefaultTableModel model1;
	private JTextArea txtAreaCustInfo;
	private JLabel siteAddrLbl;
	private JLabel lblPrice;
	private JLabel lblInstallType;
	//private JLabel lblSiteCheck;
	private JLabel lblComment;
	private JLabel lblSalesPrsn;
	//private JLabel lblSChkDoneBy;
	private JComboBox<String> comBxInstType;
	//private JComboBox<String> comBxSChkDoneBy;
	//private JCheckBox chckBxToBook;
	//private JCheckBox chckBxSChkComp;
	//private JSpinner spnTimeDate;
	private JComboBox<String> comBxSlsPerson;
	private JLabel lblFire;
	private String error;

	private JButton btnSendEmail;
	private JButton btnCancel;
	private JButton btnSave;
	private SalesPane sp;
	private validator vv;
	private GetJobs gj;
	private ConnDetails conDeets;

	public EstimationPanel(ConnDetails ConDeets, SalesPane sp) {
		this.sp = sp;
		this.conDeets = ConDeets;
		//vp = new validator();
		rowSelected = false;

		connecting = new CreateConnection();

		model1 = new DefaultTableModel();  
		model1.setRowCount(0);
		salesTbl = new JTable(model1);
		model1.setRowCount(0);

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
		infoPanel.setBounds(0, 280, 1100, 289);
		infoPanel.setLayout(null);

		siteAddrLbl = new JLabel("Fire Place");
		siteAddrLbl.setBounds(451, 10, 201, 20);
		infoPanel.add(siteAddrLbl);
		siteAddrLbl.setFont(new java.awt.Font("Arial", Font.BOLD, 20));

		txtAreaCustInfo = new JTextArea("");
		txtAreaCustInfo.setBounds(20, 20, 250, 260);
		txtAreaCustInfo.setBorder(BorderFactory.createEtchedBorder());
		txtAreaCustInfo.setBackground(LtGray);
		txtAreaCustInfo.setLineWrap(true);
		txtAreaCustInfo.setEditable(false);
		infoPanel.add(txtAreaCustInfo);


		txtBxFire = new JTextField();
		txtBxFire.setBounds(588, 30, 218, 20);
		infoPanel.add(txtBxFire);
		txtBxFire.setColumns(10);

		txtBxPrice = new JTextField();
		txtBxPrice.setColumns(10);
		txtBxPrice.setBounds(887, 30, 129, 20);
		infoPanel.add(txtBxPrice);

		lblPrice = new JLabel("Price:");
		lblPrice.setBounds(837, 33, 46, 14);
		infoPanel.add(lblPrice);

		comBxInstType = new JComboBox<String>();
		comBxInstType.setBackground(Color.WHITE);
		comBxInstType.setBounds(588, 65, 428, 20);
		infoPanel.add(comBxInstType);

		lblInstallType = new JLabel("Install Type:");
		lblInstallType.setBounds(451, 62, 104, 14);
		infoPanel.add(lblInstallType);

		/*lblSiteCheck = new JLabel("Site Check:");
		lblSiteCheck.setBounds(454, 96, 104, 14);
		infoPanel.add(lblSiteCheck);

		chckBxToBook = new JCheckBox("To Book:");
		chckBxToBook.setBounds(588, 92, 74, 23);
		infoPanel.add(chckBxToBook);

/*		comBxSChkDoneBy = new JComboBox<String>();
		comBxSChkDoneBy.setBackground(Color.WHITE);
		comBxSChkDoneBy.setBounds(755, 124, 261, 20);
		infoPanel.add(comBxSChkDoneBy);

		lblSChkDoneBy = new JLabel("Site Check Done By:");
		lblSChkDoneBy.setBounds(454, 127, 128, 14);
		infoPanel.add(lblSChkDoneBy);*/

		/*SimpleDateFormat dt = new SimpleDateFormat("dd.MMM.yyyy");
		spnTimeDate = new JSpinner(new SpinnerDateModel());
		spnTimeDate.setEditor(new JSpinner.DateEditor(spnTimeDate, dt.toPattern()));
		spnTimeDate.setBounds(755, 96, 261, 20);
		infoPanel.add(spnTimeDate);

		chckBxSChkComp = new JCheckBox("Site Check Completed");
		chckBxSChkComp.setBounds(588, 124, 171, 23);
		infoPanel.add(chckBxSChkComp);*/

		txtBxComment = new JTextField();
		txtBxComment.setBounds(588, 151, 428, 62);
		infoPanel.add(txtBxComment);
		txtBxComment.setColumns(10);

		lblComment = new JLabel("Email Comment:");
		lblComment.setBounds(454, 151, 124, 14);
		infoPanel.add(lblComment);

		lblSalesPrsn = new JLabel("Sales Person:");
		lblSalesPrsn.setBounds(454, 230, 104, 14);
		infoPanel.add(lblSalesPrsn);

		comBxSlsPerson = new JComboBox<String>();
		comBxSlsPerson.setBackground(Color.WHITE);
		comBxSlsPerson.setBounds(588, 224, 218, 20);
		infoPanel.add(comBxSlsPerson);

		lblFire = new JLabel("Fire:");
		lblFire.setBounds(451, 33, 104, 14);
		infoPanel.add(lblFire);

		btnSendEmail = new JButton("Send Email");
		btnSendEmail.setBounds(868, 255, 148, 23);
		infoPanel.add(btnSendEmail);

		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(451, 255, 148, 23);
		infoPanel.add(btnCancel);

		btnSave = new JButton("Save Details");
		btnSave.setBounds(658, 255, 148, 23);
		infoPanel.add(btnSave);

		GetJobs job = new GetJobs(conDeets);
		String[] installType = job.getInstallType();
		DefaultComboBoxModel<String> modelInst = new DefaultComboBoxModel<String>(installType);
		comBxInstType.setModel( modelInst );
		comBxInstType.setSelectedItem(null);
		/*		
		String[] SC = job.getSiteChecker();
		DefaultComboBoxModel<String> modelSC = new DefaultComboBoxModel<String>( SC );
		comBxSChkDoneBy.setModel( modelSC );*/

		/*DefaultComboBoxModel modelSC = new DefaultComboBoxModel( SC );
		comBxSlsPerson.setModel( modelSC ); */

		String[] SELL = job.getSales();
		DefaultComboBoxModel<String> modelSell = new DefaultComboBoxModel<String>(SELL);
		comBxSlsPerson.setModel(modelSell); 
		comBxSlsPerson.setSelectedItem(null);

		this.setLayout(null);
		this.add(tablePanel); 
		this.add(infoPanel);

		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(salesTbl.getTableHeader(), BorderLayout.NORTH);

		salesTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					rowSelected=true;
					try{
						//Get the customer ID as a paramater to feed into the SQL procedure 
						param = salesTbl.getValueAt(salesTbl.getSelectedRow(), 1).toString();
						paramSID = salesTbl.getValueAt(salesTbl.getSelectedRow(), 0).toString();
						txtAreaCustInfo.setText(sp.DisplayClientDetails(param));
						getEstDetails(paramSID);
					} catch (IndexOutOfBoundsException e){

					}
				}
			}
		});

		btnSendEmail.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				//Validating that the email can actually be sent
				if (emailCanBeSent()){


					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to mark this email as sent?\n"
							+ "There is no going back!\n"
							+ "This customer will be moved to Site checks.","Warning",dialogButton);
					if(dialogResult == JOptionPane.YES_OPTION){

						Desktop desktop;
						if (Desktop.isDesktopSupported() 
								&& (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {

							String custEmail = sp.getEmailAddr();
							String emailBody = getEmailBody();

							URI mailto;
							try {
								mailto = new URI("mailto:" + custEmail + "?subject=Fire%20Estimation&body=" + emailBody);
								desktop.mail(mailto);
							} catch (URISyntaxException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							updateEstimistionDate();
							resetTable();

						} else {
							// TODO fallback to some Runtime.exec(..) voodoo?
							throw new RuntimeException("desktop doesn't support mailto; mail is dead anyway ;)");
						}
					}
				}
			}
		});

		btnCancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				//if there is unsaved data
				if (checkTxtBx()){
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to cancel?","Warning",dialogButton);
					if(dialogResult == JOptionPane.YES_OPTION){
						resetTable();
						//reset all the blank fields within the estimation tab
						txtBxFire.setText(null);
						txtBxPrice.setText(null);
						comBxInstType.setSelectedItem(null);
						//chckBxToBook.setSelected(false);
						//comBxSChkDoneBy.setSelectedItem(null);
						//spnTimeDate.setEditor(new JSpinner.DateEditor(spnTimeDate, dt.toPattern()));
						//chckBxSChkComp.setSelected(false);
						txtBxComment.setText(null);
						comBxSlsPerson.setSelectedItem(null);
					}
				}				
			}
		});

		btnSave.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				//If there isn't an error add the details
				if (validateData() == false){
					updateEstimation();
					sp.showMessage("Adding the Estimation Details");
					resetTable();
					clearFields();
				}else{
					JOptionPane.showMessageDialog(null, error);
				}
			}
		});
	}


	protected void resetTable() {
		ResultSet rs = sp.getResults(1);
		salesTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
		spaceHeader(columnModel, columnWidth);
		rowSelected=false;
		error = "";
		param = "";
		txtAreaCustInfo.setText("");
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

	public JTable getSalesTbl(){
		return salesTbl;
	}

	public void updateEstimation(){
		CallableStatement sm = null;
		try {

			String update = "{" + updateEstimation +"(?,?,?,?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			sm = conn.prepareCall(update);

			sm.setString(1, paramSID);
			sm.setString(2, getFire());
			sm.setString(3, getPrice());
			sm.setInt(4, getInstTypeID());
			sm.setInt(5, getSlsPersonID());

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

	private void updateEstimistionDate() {
		CallableStatement sm = null;
		try {

			String update = "{" + addEstimationDate +"(?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			sm = conn.prepareCall(update);

			sm.setString(1, paramSID);
			sm.setDate(2, getDate());

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

	public void getEstDetails(String parameter){
		CallableStatement sm = null;
		try {

			String update = "{" + getEstimationDetails +"(?)}";	
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
				
					String fire 			= rs.getString("Fire");
					String price 			= rs.getString("Price");
					String instType 		= rs.getString("Install Type");
					String salesPerson 		= rs.getString("Salesperson");
					
					txtBxFire.setText(fire);
					txtBxPrice.setText(price);
					comBxInstType.setSelectedItem(instType);
					comBxSlsPerson.setSelectedItem(salesPerson);
				}
			}
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}      		            
	}

	public void clearFields(){
		txtBxFire.setText("");
		txtBxPrice.setText("");
		comBxInstType.setSelectedItem(null);
		txtBxComment.setText("");
		comBxSlsPerson.setSelectedItem(null);
	}

	public boolean validateData(){
		Boolean isError = false;
		error = " ";
		if (txtBxFire.getText().equals("")){
			isError = true;
			//Cannot be null or more than 15 chars
			error = error + "FIRE: can not be empty\n";
		}else if(txtBxFire.getText().length() > 30){
			isError = true;
			error = error + "FIRE: can not be more than 30 letters\n";
		}
		try {
			Integer.parseInt(txtBxPrice.getText());
			if(4 <= txtBxPrice.getText().length() && txtBxPrice.getText().length() >= 5){
				isError = true;
				error = error + "PRCIE: must be between 2000 - 99999\n";
			}
		}
		catch (NumberFormatException e) {
			//Display number error message 
			isError = true;
			error = error + "PRICE: can only contain numbers\n";
		}
		return isError;
	}

	public Boolean checkTxtBx(){
		Boolean newData = false;
		//add in combx for Install type and the date spinner 
		if (!txtAreaCustInfo.getText().equals("") || !txtBxFire.getText().equals("") || txtBxPrice.getText().equals("")
				|| !(comBxInstType.getSelectedItem()== null) || !txtBxComment.getText().equals("") || !(comBxSlsPerson.getSelectedItem() == null )){
			newData = true;
		}
		return newData;
	}

	public String getFire(){
		return txtBxFire.getText();
	}

	public String getPrice(){
		return txtBxPrice.getText();
	}

	//get the ID from what's in the combo box 
	public int getInstTypeID(){
		int instID = 0;
		String instType = (String) comBxInstType.getSelectedItem();
		CallableStatement sm = null;
		try {

			String update = "{" + getInstID +"(?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	
			sm = conn.prepareCall(update);

			sm.setString(1, instType);

			ResultSet rs = sm.executeQuery();
			while (rs.next()){
				instID = rs.getInt("InstallTypeID");
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

		return instID;

	}

	//get the ID from what's in the combo box
	public int getSlsPersonID(){
		int slsID = 0;
		String slSName = (String) comBxSlsPerson.getSelectedItem();
		CallableStatement sm = null;
		try {
			String update = "{" + getSlsID +"(?)}";	
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

	public String getComment(){
		return txtBxComment.getText();
	}	

	public Date getDate(){
		java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
		return sqlDate;
	}

	public boolean emailCanBeSent(){
		getEstDetails(paramSID);
		//if its not filled out show pop up to save details 
		String instType = (String) comBxInstType.getSelectedItem();
		String slsPerson = (String) comBxSlsPerson.getSelectedItem();

		if (getFire().equals("") || getPrice().equals("") || instType.equals("") || slsPerson.equals("")){
			JOptionPane.showMessageDialog(null, "Cannot send email! \nEnsure all fields are complete");
			return false;
		}else{
			return true;
		}		
	}

	public String getEmailBody(){
		String form = "";
		String instType = (String) comBxInstType.getSelectedItem();
		CallableStatement sm = null;
		try {

			String search = "{" + getEmBody +"(?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	
			sm = conn.prepareCall(search);

			sm.setString(1, instType);

			ResultSet rs = sm.executeQuery();
			while (rs.next()){
				form = rs.getString("EmailFromLetter");
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

		String custName = sp.getCustName();
		String comment = txtBxComment.getText();
		String email = "";
		if (comment.equals("")){
			email = "Hello " + custName + "\n\n--------------------------------------------\n\n" + form + "\n\n--------------------------------------------\n\n";
		}else{
			email = "Hello%20" + custName + "\n\n" + comment  + "\n\n--------------------------------------------\n\n" + form + "\n\n--------------------------------------------\n\n" ;
		}		
		String emailBody = email.replaceAll(" ", "%20");
		emailBody = emailBody.replaceAll("\n", "%0D");
		return emailBody;

	}
}
