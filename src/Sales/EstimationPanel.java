package Sales; 

//Description: This class allows the salesperson to email out an 
//estimation of a quote to the customer on the spot based of the entered information  

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
import net.proteanit.sql.DbUtils;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import java.sql.ResultSet;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class EstimationPanel extends JPanel {
	private JTextField fireTxtBx;
	private JTextField priceTxtBx;
	private JTextField commentTxtBx;	
	private int [] columnWidth = {50, 50, 70, 100, 100, 100, 100, 50, 100, 100}; 
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
	private JLabel PriceLbl;
	private JLabel InstallTypelbl;
	private JLabel commentlbl;
	private JLabel salesPrsnlbl;
	private JComboBox<String> instTypeComBx;
	private JComboBox<String> slsPersonComBx;
	private JLabel firelbl;
	private String error;
	private JButton sendEmailBtn;
	private JButton cancelBtn;
	private JButton saveBtn;
	private SalesPane sp;
	private ConnDetails conDeets;

	public EstimationPanel(ConnDetails ConDeets, SalesPane sp) {
		this.sp = sp;
		this.conDeets = ConDeets;
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

		fireTxtBx = new JTextField();
		fireTxtBx.setBounds(588, 30, 218, 20);
		infoPanel.add(fireTxtBx);
		fireTxtBx.setColumns(10);

		priceTxtBx = new JTextField();
		priceTxtBx.setColumns(10);
		priceTxtBx.setBounds(887, 30, 129, 20);
		infoPanel.add(priceTxtBx);

		PriceLbl = new JLabel("Price:");
		PriceLbl.setBounds(837, 33, 46, 14);
		infoPanel.add(PriceLbl);

		instTypeComBx = new JComboBox<String>();
		instTypeComBx.setBackground(Color.WHITE);
		instTypeComBx.setBounds(588, 65, 428, 20);
		infoPanel.add(instTypeComBx);

		InstallTypelbl = new JLabel("Install Type:");
		InstallTypelbl.setBounds(451, 62, 104, 14);
		infoPanel.add(InstallTypelbl);

		commentTxtBx = new JTextField();
		commentTxtBx.setBounds(588, 151, 428, 62);
		infoPanel.add(commentTxtBx);
		commentTxtBx.setColumns(10);

		commentlbl = new JLabel("Email Comment:");
		commentlbl.setBounds(454, 151, 124, 14);
		infoPanel.add(commentlbl);

		salesPrsnlbl = new JLabel("Sales Person:");
		salesPrsnlbl.setBounds(454, 230, 104, 14);
		infoPanel.add(salesPrsnlbl);

		slsPersonComBx = new JComboBox<String>();
		slsPersonComBx.setBackground(Color.WHITE);
		slsPersonComBx.setBounds(588, 224, 218, 20);
		infoPanel.add(slsPersonComBx);

		firelbl = new JLabel("Fire:");
		firelbl.setBounds(451, 33, 104, 14);
		infoPanel.add(firelbl);

		sendEmailBtn = new JButton("Send Email");
		sendEmailBtn.setBounds(868, 255, 148, 23);
		infoPanel.add(sendEmailBtn);

		cancelBtn = new JButton("Cancel");
		cancelBtn.setBounds(451, 255, 148, 23);
		infoPanel.add(cancelBtn);

		saveBtn = new JButton("Save Details");
		saveBtn.setBounds(658, 255, 148, 23);
		infoPanel.add(saveBtn);

		//Get all the current install types 
		GetJobs job = new GetJobs(conDeets);
		String[] installType = job.getInstallType();
		DefaultComboBoxModel<String> modelInst = new DefaultComboBoxModel<String>(installType);
		instTypeComBx.setModel( modelInst );
		instTypeComBx.setSelectedItem(null);

		//Get the active users whi can sell
		String[] sell = job.getSales();
		DefaultComboBoxModel<String> modelSell = new DefaultComboBoxModel<String>(sell);
		slsPersonComBx.setModel(modelSell); 
		slsPersonComBx.setSelectedItem(null);

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

		sendEmailBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				//Validating that the email can actually be sent
				if (rowSelected){


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
									String custName = sp.getCustName();
									JOptionPane.showMessageDialog(null,  custName +" has been moved to Site Checks!");
								} catch (URISyntaxException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								//The date the estimation was sent to the customer
								updateEstimistionDate();
								resetTable();
								clearFields();

							} else {
								// TODO fallback to some Runtime.exec(..) voodoo?
								throw new RuntimeException("desktop doesn't support mailto; mail is dead anyway ;)");
							}
						}
						else if (dialogResult == JOptionPane.NO_OPTION){


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

							} else {
								// TODO fallback to some Runtime.exec(..) voodoo?
								throw new RuntimeException("desktop doesn't support mailto; mail is dead anyway ;)");
							}
						}
					}
				}else{
					JOptionPane.showMessageDialog(null,  "You must select a row first!");
				}
			}
		});

		cancelBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				//if there is unsaved data
				if (checkTxtBx()){
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to cancel?","Warning",dialogButton);
					if(dialogResult == JOptionPane.YES_OPTION){
						resetTable();
						//reset all the blank fields within the estimation tab
						fireTxtBx.setText(null);
						priceTxtBx.setText(null);
						instTypeComBx.setSelectedItem(null);
						commentTxtBx.setText(null);
						slsPersonComBx.setSelectedItem(null);
					}
				}				
			}
		});

		saveBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				if (rowSelected){
					//If there isn't an error add the details
					if (validateData() == false){
						updateEstimation();
						sp.showMessage("Adding the Estimation Details");
						resetTable();
						clearFields();
					}else{
						JOptionPane.showMessageDialog(null, error);
					}
				}else{
					JOptionPane.showMessageDialog(null, "You must first select a row!");
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

	private void updateEstimistionDate() {
		CallableStatement sm = null;
		try {

			String update = "{" + addEstimationDate +"(?,?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			sm = conn.prepareCall(update);

			sm.setString(1, paramSID);
			sm.setDate(2, getDate());

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

	public void getEstDetails(String parameter){
		CallableStatement sm = null;
		try {

			String update = "{" + getEstimationDetails +"(?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			sm = conn.prepareCall(update);
			sm.setInt(1, Integer.parseInt(parameter));

			rs = sm.executeQuery();	 
			//
			if (rs==null){
				JOptionPane.showMessageDialog(null, "null query");
			}
			else
			{	
				while (rs.next()){

					String fire 			= rs.getString("Fire");
					String price 			= rs.getString("Estimate Price");
					String instType 		= rs.getString("Install Type");
					String salesPerson 		= rs.getString("Salesperson");

					fireTxtBx.setText(fire);
					priceTxtBx.setText(price);
					instTypeComBx.setSelectedItem(instType);
					slsPersonComBx.setSelectedItem(salesPerson);
				}
			}
			conn.close();
		}
		catch(Exception ex)
		{ 
			JOptionPane.showMessageDialog(null, ex.toString());
		}      		            
	}

	public void clearFields(){
		fireTxtBx.setText("");
		priceTxtBx.setText("");
		instTypeComBx.setSelectedItem(null);
		commentTxtBx.setText("");
		slsPersonComBx.setSelectedItem(null);
	}

	public boolean validateData(){
		Boolean isError = false;
		error = "";
		if (fireTxtBx.getText().equals("")){
			isError = true;
			//Cannot be null or more than 15 chars
			error = error + "FIRE: can not be empty\n";
		}else if(fireTxtBx.getText().length() > 50){
			isError = true;
			error = error + "FIRE: can not be more than 50 letters\n";
		}
		try {

			if(!priceTxtBx.getText().equals("")){
				Integer.parseInt(priceTxtBx.getText());
				if(4 <= priceTxtBx.getText().length() && priceTxtBx.getText().length() >= 5){
					isError = true;
					error = error + "PRCIE: must be between 2000 - 99999\n";
				}
			}
			else{
				isError = true;
				error = error + "PRCIE: cannot be empty\n";
			}
		}
		catch (NumberFormatException e) {
			//Display number error message 
			isError = true;
			error = error + "PRICE: can only contain numbers\n";
		}
		if (instTypeComBx.getSelectedItem() == null){
			isError = true;
			error = error + "INSTALL TYPE: must be selected\n";
		}
		if (slsPersonComBx.getSelectedItem() == null){
			isError = true;
			error = error + "SALESPERSON: must be selected\n";
		}
		return isError;
	}

	public Boolean checkTxtBx(){
		Boolean newData = false;
		//add in combx for Install type and the date spinner 
		if (!txtAreaCustInfo.getText().equals("") || !fireTxtBx.getText().equals("") || priceTxtBx.getText().equals("")
				|| !(instTypeComBx.getSelectedItem()== null) || !commentTxtBx.getText().equals("") || !(slsPersonComBx.getSelectedItem() == null )){
			newData = true;
		}
		return newData;
	}

	public String getFire(){
		return fireTxtBx.getText();
	}

	public String getPrice(){
		return priceTxtBx.getText();
	}

	//get the ID from what's in the combo box 
	public int getInstTypeID(){
		int instID = 0;
		String instType = (String) instTypeComBx.getSelectedItem();
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

		return instID;

	}

	//get the ID from what's in the combo box
	public int getSlsPersonID(){
		int slsID = 0;
		String slSName = (String) slsPersonComBx.getSelectedItem();
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
		return slsID;

	}

	public String getComment(){
		return commentTxtBx.getText();
	}	

	public Date getDate(){
		java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
		return sqlDate;
	}

	public boolean emailCanBeSent(){
		getEstDetails(paramSID);
		//if its not filled out show pop up to save details 
		String instType = (String) instTypeComBx.getSelectedItem();
		String slsPerson = (String) slsPersonComBx.getSelectedItem();

		if (getFire().equals("") || getPrice().equals("") || instType.equals("") || slsPerson.equals("")){
			JOptionPane.showMessageDialog(null, "Cannot send email! \nEnsure all fields are saved \nin the database first");
			return false;
		}else{
			return true;
		}		
	}

	public String getEmailBody(){
		String form = "";
		int basePrice = 0;
		String instType = (String) instTypeComBx.getSelectedItem();
		CallableStatement sm = null;
		try {

			String search = "{" + getEmBody +"(?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	
			sm = conn.prepareCall(search);

			sm.setString(1, instType);

			ResultSet rs = sm.executeQuery();
			while (rs.next()){
				form = rs.getString("EmailFromLetter");
				basePrice = rs.getInt("BasePrice");
			}
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

		String custName = sp.getCustName();
		String comment = commentTxtBx.getText();
		int price = Integer.parseInt(priceTxtBx.getText()) + basePrice;
		String email = "";
		if (comment.equals("")){
			email = "Hello " + custName + "\n\n--------------------------------------------\n\n" + form + "\n\nESTIMATED PRICE: $" + price + "\n\n--------------------------------------------\n\n";
		}else{
			email = "Hello%20" + custName + "\n\n" + comment  + "\n\n--------------------------------------------\n\n" + form + "\n\nESTIMATED PRICE: $" + price + "\n\n--------------------------------------------\n\n" ;
		}		
		String emailBody = email.replaceAll(" ", "%20");
		emailBody = emailBody.replaceAll("\n", "%0D");
		return emailBody;

	}

	public JPanel getInfoPanel(){
		return infoPanel;
	}

}
