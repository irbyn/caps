package Sales;

//Description: This class allows the sales person to see which sales need following up and record which sales close. 

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import documents.FileSystem;
import net.proteanit.sql.DbUtils;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

class FollowUpPanel extends JPanel {
	private int [] columnWidth = {30, 30, 80, 100, 100, 100, 40, 40, 40, 50, 50};
	private int [] columnWidthComm = {34, 250, 50};
	private String createInst = "call AWS_WCH_DB.dbo.s_SalesNewInstall";
	private String updateFollowUp = "call AWS_WCH_DB.dbo.s_SalesUpdateFollowUp";
	private String comment = "call AWS_WCH_DB.dbo.s_SalesFollowUpComment";
	private String addComment = "call AWS_WCH_DB.dbo.s_SalesAddComment";
	private String param = "";
	private String paramSID = ""; 
	private ResultSet rs;
	private ResultSet rs2;
	private DefaultTableModel model1;
	private DefaultTableModel model2;
	private CreateConnection connecting;
	private ConnDetails conDeets;
	private SalesPane sp;
	private JTableHeader header;
	private JTableHeader headerComm;
	private TableColumnModel columnModel;
	private TableColumnModel columnModelComm;
	private Color LtGray = Color.decode("#eeeeee");
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable salesTbl;
	private JTable commentTbl;
	private JTextArea txtAreaCustInfo;
	private JSpinner spnDate;
	private JRadioButton rdBtnNxtFlwUp;
	private JRadioButton rdBtnInvoice;
	private JRadioButton soldElsrdBtn;
	private JButton cancelBtn;
	private JButton saveBtn;
	private JButton viewSiteBtn;
	private JButton viewQutBtn;
	private JButton viewPhotoBtn;
	private Boolean rowSelected;
	private Date date;
	public ButtonGroup btnGroup;
	private File quote;
	private File site;
	private File photo;
	private Boolean qutExists = false;
	private Boolean siteExists = false;
	private Boolean photoExists = false;
	private ImageIcon fll;
	private ImageIcon pic;
	private File[] photosArr;
/*
	private String folder = "//C:/pdfs/Invoice/";
	private String qutPfx = "QUT_";
	private String sitePfx = "SC_";
	private String photoPfx = "PH_";*/

	private JTextField invNumbTxtBx;
	private JTextField commentTxtBx;
	private FileSystem fs;

	public FollowUpPanel(ConnDetails ConDeets, SalesPane sp) {

		this.sp = sp;
		this.conDeets = ConDeets;
		
		fs = new FileSystem();
		connecting = new CreateConnection();
		fll = new ImageIcon(getClass().getResource("pdf.png"));
		pic = new ImageIcon(getClass().getResource("pictures.png"));

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

		txtAreaCustInfo = new JTextArea("");
		txtAreaCustInfo.setBounds(20, 20, 250, 260);
		txtAreaCustInfo.setBorder(BorderFactory.createEtchedBorder());
		txtAreaCustInfo.setBackground(LtGray);
		txtAreaCustInfo.setLineWrap(true);
		txtAreaCustInfo.setEditable(false);
		infoPanel.add(txtAreaCustInfo);

		date = new java.sql.Date(Calendar.getInstance().getTime().getTime());

		SimpleDateFormat dtModel = new SimpleDateFormat("dd.MMM.yyyy");
		spnDate = new JSpinner(new SpinnerDateModel());
		spnDate.setEditor(new JSpinner.DateEditor(spnDate, dtModel.toPattern()));
		spnDate.setBounds(882, 8, 164, 20);
		spnDate.setValue(date);
		infoPanel.add(spnDate);

		rdBtnNxtFlwUp = new JRadioButton("Next Follow Up");
		rdBtnNxtFlwUp.setBounds(708, 7, 151, 23);
		rdBtnNxtFlwUp.setSelected(true);
		infoPanel.add(rdBtnNxtFlwUp);

		rdBtnInvoice = new JRadioButton("Invoice");
		rdBtnInvoice.setBounds(708, 109, 109, 23);
		infoPanel.add(rdBtnInvoice);

		soldElsrdBtn = new JRadioButton("Sold Elsewhere");
		soldElsrdBtn.setBounds(708, 135, 151, 23);
		infoPanel.add(soldElsrdBtn);

		btnGroup = new ButtonGroup();
		btnGroup.add(rdBtnNxtFlwUp);
		btnGroup.add(rdBtnInvoice);
		btnGroup.add(soldElsrdBtn);

		invNumbTxtBx = new JTextField();
		invNumbTxtBx.setBounds(882, 110, 164, 20);
		infoPanel.add(invNumbTxtBx);
		invNumbTxtBx.setColumns(10);

		cancelBtn = new JButton("Cancel");
		cancelBtn.setBounds(708, 255, 164, 23);
		infoPanel.add(cancelBtn);

		saveBtn = new JButton("Save Details");
		saveBtn.setBounds(882, 255, 164, 23);
		infoPanel.add(saveBtn);

		viewSiteBtn = new JButton("View Site Check PDF");
		viewSiteBtn.setBounds(708, 165, 164, 23);
		infoPanel.add(viewSiteBtn);

		viewQutBtn = new JButton("View Quote PDF");
		viewQutBtn.setBounds(708, 202, 164, 23);
		infoPanel.add(viewQutBtn);

		viewPhotoBtn = new JButton("View Photo(s)");
		viewPhotoBtn.setBounds(882, 165, 164, 23);
		infoPanel.add(viewPhotoBtn);

		model2 = new DefaultTableModel();  
		model2.setRowCount(0);	

		JPanel commentpanel = new JPanel();
		commentpanel.setBounds(280, 44, 400, 236);
		commentpanel.setLayout(new BorderLayout());
		infoPanel.add(commentpanel);

		commentTbl = new JTable(model2);
		commentTbl.setAutoCreateRowSorter(true);

		headerComm= commentTbl.getTableHeader();
		columnModelComm = headerComm.getColumnModel();
		add(headerComm);

		//Create the comments panel and table 
		JScrollPane scrollPaneComment = new JScrollPane(commentTbl);		

		commentpanel.add(scrollPaneComment, BorderLayout.CENTER);
		commentpanel.add(commentTbl.getTableHeader(), BorderLayout.NORTH);

		JLabel followUpComLbl = new JLabel("Follow Up Comment");
		followUpComLbl.setBounds(729, 30, 130, 14);
		infoPanel.add(followUpComLbl);

		commentTxtBx = new JTextField();
		commentTxtBx.setBounds(729, 48, 317, 44);
		infoPanel.add(commentTxtBx);
		commentTxtBx.setColumns(10);

		JLabel FollowUplbl = new JLabel("Follow Up Comments");
		FollowUplbl.setBounds(281, 20, 127, 14);
		infoPanel.add(FollowUplbl);

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
						param = salesTbl.getValueAt(salesTbl.getSelectedRow(), 1).toString();
						paramSID = salesTbl.getValueAt(salesTbl.getSelectedRow(), 0).toString();
						txtAreaCustInfo.setText(sp.DisplayClientDetails(param));
						getComments();
						checkForFiles();

					} catch (IndexOutOfBoundsException e){

					}
				}
			}
		});

		saveBtn.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {		
				if (rowSelected){
					if (validateData()){
						//if invoice is sleceted create a new install
						if (rdBtnInvoice.isSelected()){
							createInstall();
						}
						else{
							updateFollowup();
							if (rdBtnNxtFlwUp.isSelected() && !commentTxtBx.getText().equals("")){
								addComment();
							}
						}
						resetTable();
						clearFields();
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "You must first select a row.");
				}		
			}
		});

		cancelBtn.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetTable();
				clearFields();				
			}
		});

		viewSiteBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {

			if (siteExists){
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().open(site);
					} catch (IOException ex) {
					}
				}
			}			   
		}
		});

		viewQutBtn.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (qutExists){
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().open(quote);
						} catch (IOException ex) {
						}
					}
				}			   
			}

		});

		viewPhotoBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			int ph = photosArr.length;
			sp.showMessage("" + ph);
			for (int i =0; i< ph; i++){
				if (photosArr[i].exists()){
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().open(photosArr[i]);
						} catch (IOException ex) {
						}
					}
				}
			}			   
		}

		});
	}

	protected void resetTable() {
		salesTbl.clearSelection();
		commentTbl.clearSelection();
		rs = sp.getResults(4);
		salesTbl.setModel(DbUtils.resultSetToTableModel(rs));
		paramSID = null;
		getComments();
		spaceHeader(columnModel, columnWidth);
		spaceHeader(columnModelComm, columnWidthComm);
		rowSelected=false;
		param = "";
		txtAreaCustInfo.setText("");
	}	

	public void getComments(){
		CallableStatement sm = null;
		try {

			String comm = "{" + comment +"(?)}";	
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			sm = conn.prepareCall(comm);
			sm.setString(1, paramSID);
			rs2 = sm.executeQuery();
			
			commentTbl.setModel(DbUtils.resultSetToTableModel(rs2));
			spaceHeader(columnModelComm, columnWidthComm);
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

	public void spaceHeader(TableColumnModel colM, int[] colW) {
		int i;
		TableColumn tabCol = colM.getColumn(0);
		for (i=0; i<colW.length; i++){
			tabCol = colM.getColumn(i);
			tabCol.setPreferredWidth(colW[i]);
		}
		header.repaint();
		headerComm.repaint();
	}   

	public void clearFields(){
		spnDate.setValue(date);
		invNumbTxtBx.setText("");
		commentTxtBx.setText("");
		rdBtnNxtFlwUp.setSelected(true);
	}

	public JTable getSalesTbl(){
		return salesTbl;
	}

	public JPanel getInfoPanel(){
		return infoPanel;
	}

	public Boolean validateData(){
		boolean valid = true;
		String msg = "";
		if (commentTxtBx.getText().length() > 256){
			valid = false;
			msg = msg + "COMMENT: can not be more that 255 letters\n";
		}
		if(rdBtnInvoice.isSelected())
		{		
			if (!invNumbTxtBx.getText().equals(null) && !invNumbTxtBx.getText().equals("")){		
				try {
					//Convert Invoice number
					Integer.parseInt(invNumbTxtBx.getText());
				}
				catch (NumberFormatException e) {
					//Display number error message 
					valid = false;
					msg = msg + "INVOICE NUMBER: can only contain numbers\n";
					}
			}else{
				valid = false;
				msg = msg + "INVOICE NUMBER: can not be empty\n";
			}
		}
		
		if (!valid){
			JOptionPane.showMessageDialog(null, msg);
		}
		return valid;

	}


	public void updateFollowup(){
		CallableStatement pm = null;
		try {
			String update = "{" + updateFollowUp +"(?,?,?)}";

			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			pm = conn.prepareCall(update);

			pm.setString(1, paramSID);
			pm.setString(2, getStatus());
			if (rdBtnNxtFlwUp.isSelected()){
				pm.setString(3, getDate());
			}else{
				pm.setString(3, null);
			}

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

	public void addComment(){

		date = new java.sql.Date(Calendar.getInstance().getTime().getTime());

		CallableStatement pm = null;
		try {
			String update = "{" + addComment +"(?,?,?)}";

			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			pm = conn.prepareCall(update);			

			pm.setString(1, paramSID);
			pm.setString(2, getComment());
			pm.setDate(3, (java.sql.Date) date);

			pm.executeUpdate();
			//conn.close();
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

	public void createInstall(){
		CallableStatement pm = null;
		try {
			String update = "{" + createInst +"(?,?)}";

			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			pm = conn.prepareCall(update);
			pm.setInt(1, Integer.parseInt(paramSID));
			pm.setInt(2, getInvNum());
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

	public String getStatus(){
		String status = "";

		if (rdBtnInvoice.isSelected()){
			status = "Active";
		}else if(rdBtnNxtFlwUp.isSelected()){
			status = "Pending";
		}
		else{
			status = "Closed";
		}
		//return the status of the sale determined from the radio buttons
		return	status;
	}

	public String getDate(){  	
		Date dte = (Date) spnDate.getValue(); 
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
		String dt = sdf1.format(dte);
		return dt; 
	}

	public int getInvNum(){
		return Integer.parseInt(invNumbTxtBx.getText());
	}

	public String getComment(){
		return commentTxtBx.getText();
	}

	protected void checkForFiles() {
		//Check for stored Quote
		quote = new File(fs.getQuote()+paramSID+".pdf");//Uses InstallID/Quote number
		if (quote.exists()){
			viewQutBtn.setVisible(true);
			qutExists = true;
		}else{
			viewQutBtn.setVisible(false);
			qutExists = false;
		}	
		//Check for stored SiteCheck Forms	
		site = new File(fs.getSiteChk()+paramSID+".pdf");//Uses SaleID number
		if (site.exists()){
			viewSiteBtn.setVisible(true);
			siteExists = true;
		}else{
			viewSiteBtn.setVisible(false);
			siteExists = false;
		}
		//Check for stored Photo(s)	
		//Create array of photos
		File f = new File(fs.getPhotoFolder());					
		photosArr = f.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(fs.getPhotoPrefix()+paramSID+"_");	//Uses SaleID number
			}
		});

		if (photosArr.length>0){
			viewPhotoBtn.setVisible(true);
			photoExists = true;
		}else{
			viewPhotoBtn.setVisible(false);
			photoExists = false;
		}

	}
}

