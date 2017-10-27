package Sales; 

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.TransferHandler;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Sales.SalesPane;
import net.proteanit.sql.DbUtils;

class QuotePanel extends JPanel {

	private int [] columnWidth = {50, 50, 100, 70, 100, 100, 60, 50, 50, 50}; 	
	//private String getSaleID = "EXEC AWS_WCH_DB.dbo.[i_InstallsGetSaleID] ";
	private String getSCDetails = "{Call AWS_WCH_DB.dbo.[s_SalesSCDetails] (?)}";
	private String updateQuote = "{Call AWS_WCH_DB.dbo.[s_SalesUpdateQuote] (?,?,?,?,?,?,?)}";
	private String rmvSiteCheck = "{Call AWS_WCH_DB.dbo.[s_SalesRmvSiteCheck] (?)}";

	//private String slsID = "";  
	private String custID = "";
	private String saleID = "";  
	private ResultSet rs;
	private Color LtGray = Color.decode("#eeeeee");

	private File quote;
	private File site;
	private File photo;
	private File[] photosArr;
	private int photoLimit = 5;
	private String folder = "//C:/pdfs/Invoice/";
	private String qutPfx = "QUT_";
	private String sitePfx = "SC_";
	private String photoPfx = "PH_";

	private String qut;
	private String sck;
	private String pht;

	private String qutFile;
	private String siteFile;
	private String photoFile;

	private Boolean rowSelected = false;
	private Boolean qutExists = false;
	private Boolean siteExists = false;
	private Boolean photoExists = false;
	private Boolean saveQUT = false;
	private Boolean saveSC = false;
	private Boolean savePH = false;
	private Boolean saveAllowed = true;

	private String msg;

	private CreateConnection connecting;

	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable salesTbl;
	private DefaultTableModel model1;

	private JTextArea detailsTxtArea;
	private JPanel qutPanel;
	private DefaultListModel qutLM;
	private JList qutDZ;
	private JScrollPane qutSP;
	private JButton removeQutBtn;
	private JButton viewQutBtn;

	private JPanel sitePanel;
	private DefaultListModel siteLM;
	private JList siteDZ;
	private JScrollPane siteSP;

	private JPanel photoPanel;	
	private DefaultListModel photoLM;
	private JList photoDZ;
	private JScrollPane photoSP;

	private JLabel lblReeseCode; 
	private JLabel lblQuoteNum; 
	private JTextField txtBxReesCode;
	private JTextField txtBxQuoteNum;

	private JCheckBox chBcRmvSC;
	private JButton removBtn;	
	private JButton removeSiteBtn;
	private JButton viewSiteBtn;
	private JButton removePhotoBtn;
	private JButton viewPhotoBtn;

	private JTextField installTxtBx;
	private JTextField siteTxtBx;
	private JTextField photoTxtBx;

	private JButton cancelSaleReqBtn; 
	private JButton saveSaleReqBtn; 

	private CreateConnection conn;

	private ImageIcon fll;
	private ImageIcon pic;

	private Boolean lockForm;
	private ConnDetails conDeets;
	private SalesPane sp;


	public QuotePanel(ConnDetails conDetts, SalesPane spn) {

		this.lockForm = lockForm;
		this.conDeets = conDetts;
		this.sp = spn;

		connecting = new CreateConnection();
		fll = new ImageIcon(getClass().getResource("pdf.png"));
		pic = new ImageIcon(getClass().getResource("pictures.png"));

		model1 = new DefaultTableModel();  
		model1.setRowCount(0);
		salesTbl = new JTable(model1);

		salesTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		salesTbl.setAutoCreateRowSorter(true);

		JScrollPane scrollPane = new JScrollPane(salesTbl);

		header= salesTbl.getTableHeader();
		columnModel = header.getColumnModel();
		add(header); 

		tablePanel = new JPanel();
		tablePanel.setBounds(20, 20, 1025, 260);       
		tablePanel.setLayout(new BorderLayout());

		infoPanel = new JPanel();
		infoPanel.setBounds(0, 280, 1100, 300);
		infoPanel.setLayout(null);

		detailsTxtArea = new JTextArea("");
		detailsTxtArea.setBounds(20, 20, 250, 260);
		detailsTxtArea.setBorder(BorderFactory.createEtchedBorder());
		detailsTxtArea.setBackground(LtGray);
		detailsTxtArea.setLineWrap(true);
		detailsTxtArea.setEditable(false);
		infoPanel.add(detailsTxtArea);

		lblReeseCode = new JLabel("Reese Code");
		lblReeseCode.setBounds(386, 25, 74, 14);
		infoPanel.add(lblReeseCode);

		lblQuoteNum = new JLabel("Quote Number");
		lblQuoteNum.setBounds(622, 25, 95, 14);
		infoPanel.add(lblQuoteNum);

		txtBxReesCode = new JTextField();
		txtBxReesCode.setBounds(477, 22, 100, 20);
		infoPanel.add(txtBxReesCode);
		txtBxReesCode.setColumns(10);

		txtBxQuoteNum = new JTextField();
		txtBxQuoteNum.setBounds(722, 22, 100, 20);
		infoPanel.add(txtBxQuoteNum);
		txtBxQuoteNum.setColumns(10);

		qutPanel = new JPanel();
		qutPanel.setBounds(622, 74, 200, 175); 
		qutPanel.setOpaque(true);
		qutPanel.setVisible(true);
		qutPanel.setLayout(null);

		qutLM = new DefaultListModel();
		qutDZ = new JList(qutLM);
		qutDZ.setCellRenderer(new FileCellRenderer());
		qutDZ.setTransferHandler(new ListTransferHandler(qutDZ));
		qutDZ.setDragEnabled(true);
		qutDZ.setDropMode(javax.swing.DropMode.INSERT);
		qutDZ.setBorder(new TitledBorder("Drop Quote pdf here"));
		qutSP = new javax.swing.JScrollPane(qutDZ);
		qutSP.setBounds(0, 0, 200, 100);
		qutPanel.add(qutSP);

		viewQutBtn = new JButton(fll);
		viewQutBtn.setBounds(0, 110, 40, 40);
		qutPanel.add(viewQutBtn);
		removeQutBtn = new JButton("Remove Quote");
		removeQutBtn.setBounds(50, 110, 150, 25);
		qutPanel.add(removeQutBtn);

		sitePanel = new JPanel();
		sitePanel.setBounds(386, 74, 200, 158); 
		sitePanel.setOpaque(true);
		sitePanel.setVisible(true);
		sitePanel.setLayout(null);

		siteLM = new DefaultListModel();
		siteDZ = new JList(siteLM);
		siteDZ.setCellRenderer(new FileCellRenderer());
		siteDZ.setTransferHandler(new ListTransferHandler(siteDZ));
		siteDZ.setDragEnabled(true);
		siteDZ.setDropMode(javax.swing.DropMode.INSERT);
		siteDZ.setBorder(new TitledBorder("Drop Site Check pdf here"));
		siteSP = new javax.swing.JScrollPane(siteDZ);
		siteSP.setBounds(0, 0, 200, 100);
		sitePanel.add(siteSP);

		viewSiteBtn = new JButton(fll);
		viewSiteBtn.setBounds(0, 110, 40, 40);
		sitePanel.add(viewSiteBtn);
		removeSiteBtn = new JButton("Remove Site Check");
		removeSiteBtn.setBounds(50, 110, 150, 25);
		sitePanel.add(removeSiteBtn);

		photoPanel = new JPanel();
		photoPanel.setBounds(845, 74, 200, 175); 
		photoPanel.setOpaque(true);
		photoPanel.setVisible(true);
		photoPanel.setLayout(null);

		photoLM = new DefaultListModel();
		photoDZ = new JList(photoLM);
		photoDZ.setCellRenderer(new FileCellRenderer());
		photoDZ.setTransferHandler(new ListTransferHandler(photoDZ));
		photoDZ.setDropMode(javax.swing.DropMode.INSERT);
		photoDZ.setBorder(new TitledBorder("Drop Photos here"));
		photoSP = new javax.swing.JScrollPane(photoDZ);
		photoSP.setBounds(0, 0, 200, 100);
		photoPanel.add(photoSP);

		viewPhotoBtn = new JButton(pic);
		viewPhotoBtn.setBounds(0, 110, 40, 40);
		photoPanel.add(viewPhotoBtn);
		removePhotoBtn = new JButton("Remove Photo");
		removePhotoBtn.setBounds(50, 110, 150, 25);
		photoPanel.add(removePhotoBtn);

		cancelSaleReqBtn = new JButton("Cancel");
		cancelSaleReqBtn.setBounds(672, 260, 150, 25);
		infoPanel.add(cancelSaleReqBtn);

		saveSaleReqBtn = new JButton("Save Details");
		saveSaleReqBtn.setBounds(895, 260, 150, 25);
		infoPanel.add(saveSaleReqBtn);

		this.setLayout(null);
		this.add(tablePanel); 
		infoPanel.add(qutPanel);
		infoPanel.add(sitePanel);
		infoPanel.add(photoPanel);
		this.add(infoPanel);

		chBcRmvSC = new JCheckBox("Remove Site Check");
		chBcRmvSC.setBounds(436, 231, 141, 23);
		infoPanel.add(chBcRmvSC);

		removBtn = new JButton("Remove");
		removBtn.setBounds(436, 261, 150, 23);
		infoPanel.add(removBtn);

		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(salesTbl.getTableHeader(), BorderLayout.NORTH);        


		viewQutBtn.addActionListener( new ActionListener()
		{	@Override
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

		viewPhotoBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			int ph = photosArr.length;
			sp.showMessage("" + ph);
			for (int i =0; i< ph; i++){
				if (photosArr[i].exists())
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().open(photosArr[i]);
						} catch (IOException ex) {
						}
					}
			}			   
		}

		});

		removeQutBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {

			int i = qutDZ.getSelectedIndex();	//getSelectedValue();//getSelectedItem();
			if(i!=-1){
				qutLM.remove(i);
			}
			else{
				sp.showMessage("Please Select a File to Remove");
			}
		}

		});

		removeSiteBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) { 
			int i = siteDZ.getSelectedIndex();	//getSelectedValue();//getSelectedItem();
			if(i!=-1){
				siteLM.remove(i);
			}
			else{
				sp.showMessage("Please Select a File to Remove");
			}
		}
		});

		removePhotoBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			int i = photoDZ.getSelectedIndex();	//getSelectedValue();//getSelectedItem();
			if(i!=-1){
				photoLM.remove(i);
			}
			else{
				sp.showMessage("Please Select a File to Remove");
			}
		}
		});

		removBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (rowSelected){
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to remove this site check?\n"
							+ "This sale will be moved back into Site checks.","Warning",dialogButton);
					if(dialogResult == JOptionPane.YES_OPTION){
						if (chBcRmvSC.isSelected()){
							removeSiteCheck();
							resetTable();
						}else{
							JOptionPane.showMessageDialog(null, "You have not selected to remove this sale\n"
									+ "from the quoting stage.");
						}
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "You must first selected a sale. ");
				}


			}
		});


		cancelSaleReqBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetTable();					
			}
		});

		saveSaleReqBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				{ 
					if(rowSelected){
						if(validateData()){						   
							saveDocuments();
						}
					}
					else {
						sp.showMessage("No Customer Selected");
					}
				}
			}
		});

		salesTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					rowSelected=true;
					qutLM.removeAllElements();
					siteLM.removeAllElements();
					photoLM.removeAllElements();						

					//			pp.setFormsLocked();
					try{
						saleID = salesTbl.getValueAt(salesTbl.getSelectedRow(), 0).toString();
						custID= salesTbl.getValueAt(salesTbl.getSelectedRow(), 1).toString();

						detailsTxtArea.setText(sp.DisplayClientDetails(custID));
						//displayClientDetails(quoteNum);
						checkForFiles();

					} catch (IndexOutOfBoundsException e){
						//Ignoring IndexOutOfBoundsExceptions!
					}
				}
			}
		});
		resetTable();
	}

	protected void updateQuote(String saleID, int custID) {

		CallableStatement pm = null;
		try {
			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			pm = conn.prepareCall(updateQuote);

			pm.setInt(1, custID);
			pm.setString(2, getReesCode());
			pm.setString(3, getQuoteNum());
			pm.setString(4, getQutLoaded());
			pm.setString(5,	saleID);
			pm.setString(6, getSiteLoaded());
			pm.setString(7, getPhotoLoaded());

			pm.executeUpdate();
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


	protected void saveDocuments() {
		qut = "";
		sck = "";
		pht = "";	
		saveQUT = false;
		saveSC = false;
		savePH = false;

		if (qutExists || qutLM.getSize() >0 ){
			qut = "Loaded";
		}
		if (qutExists && qutLM.getSize() >0){
			int input = JOptionPane.showConfirmDialog(null,"This Quote already exists!\n "
					+ "Do you want to Overwrite existing file?", "Overwrite existing Quote?", JOptionPane.YES_NO_OPTION);
			if (input == 0){    		
				saveQuote(qutLM.getElementAt(0));   		
			} 
		} else if (qutLM.getSize() >0){
			saveQuote(qutLM.getElementAt(0));		
		}
		//	quoteLM.removeElementAt(0);


		if (siteExists || siteLM.getSize() >0 ){
			sck = "Loaded";
		}
		if (siteExists && siteLM.getSize() >0){
			int input = JOptionPane.showConfirmDialog(null,"This Site Check already exists!\n "
					+ "Do you want to Overwrite existing file?",  "Overwrite existing Site Check?", JOptionPane.YES_NO_OPTION);
			if (input == 0){
				saveSite(siteLM.getElementAt(0));
			} else{
				sp.showMessage("dont save Quote");
				siteLM.removeElementAt(0);
			}
		} else if (siteLM.getSize() >0){
			saveSite(siteLM.getElementAt(0));
		}

		int existingFoto = photosArr.length;
		int newFoto = photoLM.getSize();
		if (existingFoto >0 || newFoto>0){
			pht = "Loaded";
		}
		if (existingFoto+newFoto>photoLimit){
			JOptionPane.showMessageDialog(null, "Maximum of " + photoLimit +" Photos reached, Some photos not saved");		
			for(int i = existingFoto; i<photoLimit; i++){
				savePhoto(photoLM.getElementAt(i-existingFoto),i+1);
			}		
		}else {
			for(int i = 0; i<newFoto; i++){
				savePhoto(photoLM.getElementAt(i),i);
			}
		}
		updateQuote(saleID, Integer.parseInt(custID));
		resetTable();
	}

	/*
	 * Saves QUOTE to the correct file path
	 */
	protected void saveQuote(Object f){
		if(f instanceof File){
			quote = (File)f;
			File src = new File(quote.getAbsolutePath());
			File target = new File(folder+qutPfx+saleID+".pdf");

			try {
				Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

			} catch (IOException e) {
				sp.showMessage("Saving Quote Failed!");
			}		
		}
	}

	/*
	 * Saves Sitechecks to the correct file path
	 */
	protected void saveSite(Object f){
		if(f instanceof File){
			site = (File)f;
			File src = new File(site.getAbsolutePath());
			File target = new File(folder+sitePfx+saleID+".pdf");

			try {
				Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

			} catch (IOException e) {
				sp.showMessage("Saving Site Check Failed!");
			}		
		}
	}

	protected void savePhoto(Object f, int fotoNum){
		if(f instanceof File){

			photo = (File)f;
			File src = new File(photo.getAbsolutePath());
			String file = photo.getAbsolutePath().toString();
			String fileExt = file.split("\\.")[1];
			File target = new File(folder+photoPfx+saleID+"_"+fotoNum+"."+fileExt);

			try {
				Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

			} catch (IOException e) {
				sp.showMessage("Saving Photo Failed!");
			}		
		}
	}


	/*
	 * Checks for any reason files should not be saved: too many or wrong type!
	 */
	protected Boolean validateData() {

		saveAllowed=true;
		msg = "";
		if (!txtBxReesCode.getText().equals("") || !txtBxQuoteNum.getText().equals("") || qutLM.getSize()>0 || siteLM.getSize()>0 || photoLM.getSize()>0){

			if (txtBxReesCode.getText().length() > 8){
				msg = msg + "Rees Code can not be more than 8 characters\n";
				saveAllowed=false;
			}else if (!txtBxReesCode.getText().contains("-")){	
				msg = msg + "Rees Code must contain a -\n";
				saveAllowed=false;
			}

			try {
				Integer.parseInt(txtBxQuoteNum.getText());
				if(txtBxQuoteNum.getText().length() > 9){
					msg = msg + "Quote can not be more than 9 numbers\n";
					saveAllowed=false;
				}
			}
			catch (NumberFormatException e) {
				//Display number error message 
				msg = msg + "Quote can only contain numbers\n";
				saveAllowed=false;
			}

			if (qutLM.getSize()>1){
				msg = msg +"Only one Quote can be saved\n";
				saveAllowed=false;
			}	
			if (qutLM.getSize()==1){
				String file = qutLM.get(0).toString();
				if(!file.endsWith(".pdf")){
					msg = msg +"Only .pdf files are allowed for Quote\n";
					saveAllowed=false;	
				}
			}

			if (siteLM.getSize()>1){
				msg = msg +"Only one SiteCheck can be saved\n";
				saveAllowed=false;
			}
			if (siteLM.getSize()==1){
				String file = siteLM.get(0).toString();
				if(!file.endsWith(".pdf")){
					msg = msg +"Only .pdf files are allowed for SiteCheck\n";
					saveAllowed=false;	
				}
			}
			int photos = photoLM.getSize();
			if (photos>0){
				for(int i=0; i<photos;i++){
					String file = photoLM.get(i).toString();
					if(!file.endsWith(".png") && !file.endsWith(".jpg") && !file.endsWith(".jpeg")){
						msg = msg +"Photo: " + (1+i) + ") Only .png, .jpg or .jpeg files are Allowed.\n";
						saveAllowed=false;	
					}
				}
			}
		}else{
			msg = msg +"Nothing to Save";
			saveAllowed=false;
		}
		if (!saveAllowed)	{
			JOptionPane.showMessageDialog(null, msg);
			return saveAllowed;
		}
		return saveAllowed;
	}

	/*
	 * Checks if this install (and sale), have files in the file system
	 * updates Boolean values QuoteExists, siteExists, photoExists, 
	 */
	protected void checkForFiles() {
		//Check for stored Quote
		quote = new File(folder+qutPfx+saleID+".pdf");//Uses InstallID/Quote number
		if (quote.exists()){
			viewQutBtn.setVisible(true);
			qutExists = true;
		}else{
			viewQutBtn.setVisible(false);
			qutExists = false;
		}	
		//Check for stored SiteCheck Forms	
		site = new File(folder+sitePfx+saleID+".pdf");//Uses SaleID number
		if (site.exists()){
			viewSiteBtn.setVisible(true);
			siteExists = true;
		}else{
			viewSiteBtn.setVisible(false);
			siteExists = false;
		}
		//Check for stored Photo(s)	
		//Create array of photos
		File f = new File(folder);					
		photosArr = f.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(photoPfx+saleID+"_");	//Uses SaleID number
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


	public void spaceHeader(TableColumnModel colM, int[] colW) {
		int i;
		TableColumn tabCol = colM.getColumn(0);
		for (i=0; i<colW.length; i++){
			tabCol = colM.getColumn(i);
			tabCol.setPreferredWidth(colW[i]);
		}
		header.repaint();
	}   

	protected void clearDrops(){
		qutLM.removeAllElements();
		siteLM.removeAllElements();
		photoLM.removeAllElements();
	}

	protected void resetTable() {	
		ResultSet rs = sp.getResults(3);
		salesTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
		spaceHeader(columnModel, columnWidth);

		rowSelected=false;
		custID = "";
		txtBxReesCode.setText("");
		txtBxQuoteNum.setText("");
		detailsTxtArea.setText("");
		clearDrops();
		viewQutBtn.setVisible(false);
		viewSiteBtn.setVisible(false);
		viewPhotoBtn.setVisible(false);
		qutExists = false;
		siteExists = false;
		photoExists = false;
	}		

	class FileCellRenderer extends DefaultListCellRenderer {

		/*
		 * Formats the output of the drop to Show an Icon and filename (not full path)
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {

			Component c = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);

			if (c instanceof JLabel && value instanceof File) {
				JLabel l = (JLabel)c;
				File f = (File)value;
				l.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
				l.setText(f.getName());
				//l.setText(f.getAbsolutePath());
				l.setToolTipText(f.getAbsolutePath());
			}
			return c;
		}
	}

	class ListTransferHandler extends TransferHandler {

		private JList list;

		ListTransferHandler(JList list) {
			this.list = list;
		}

		@Override
		public boolean canImport(TransferHandler.TransferSupport info) {
			// we only import FileList
			if (!rowSelected){
				return false;
			}
			if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				return false;
			}
			return true;
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport info) {
			if (!info.isDrop()) {
				return false;
			}

			// Check for FileList flavor
			if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				displayDropLocation("List doesn't accept a drop of this type.");
				return false;
			}


			// Get the fileList that is being dropped.
			Transferable t = info.getTransferable();
			List<File> data;
			try {
				data = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
			}
			catch (Exception e) { return false; }
			DefaultListModel model = (DefaultListModel) list.getModel();
			for (Object file : data) {
				model.addElement((File)file);
			}
			return true;
		}

		private void displayDropLocation(String string) {
			System.out.println(string);
		}
	}

	public JTable getSalesTbl(){
		return salesTbl;
	}

	public String getReesCode(){
		return txtBxReesCode.getText();
	}

	public String getQuoteNum(){
		return txtBxQuoteNum.getText();
	}

	public void removeSiteCheck(){
		CallableStatement pm = null;
		try {

			Connection conn = connecting.CreateConnection(conDeets);	        	   	

			pm = conn.prepareCall(rmvSiteCheck);

			pm.setString(1,	saleID);

			pm.executeUpdate();
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

	public Boolean doesQuoteExist(){
		return qutExists;
	}
	public Boolean doesSiteExist(){
		return siteExists;
	}
	public Boolean doesPhotoExist(){
		return photoExists;
	}
	private String getSaleID(){
		return "1";
	}
	private String getQutLoaded() {
		return qut;
	}
	private String getSiteLoaded() {
		return sck;
	}
	private String getPhotoLoaded() {
		return pht;
	}
	
	public JPanel getInfoPanel(){
		return infoPanel;
	}

}
