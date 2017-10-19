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
	
	private int [] columnWidth = {30, 100, 120, 80}; 	
	private String getSaleID = "EXEC AWS_WCH_DB.dbo.[i_InstallsGetSaleID] ";
	private String updateDocs = "{Call AWS_WCH_DB.dbo.[i_InstallsUpdateDocs] (?,?,?,?,?)}";
	
	private String quoteNum = "";  
	private String saleID = "";  
	private ResultSet rs;
	private Color LtGray = Color.decode("#eeeeee");

	private File quote;
	private File site;
	private File photo;
	private File[] photosArr;
	private int photoLimit = 5;
	private String folder = "//C:/pdfs/Invoice/";
	private String qutPfx = "INV_";
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
    
    private JButton removeSiteBtn;
    private JButton viewSiteBtn;
    private JButton removePhotoBtn;
    private JButton viewPhotoBtn;
	
	private JTextField installTxtBx;
	private JTextField siteTxtBx;
	private JTextField photoTxtBx;
	
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	
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
	    sitePanel.setBounds(386, 74, 200, 175); 
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
      
      	cancelPermitReqBtn = new JButton("Cancel");
      	cancelPermitReqBtn.setBounds(672, 260, 150, 25);
      	infoPanel.add(cancelPermitReqBtn);
      
      	savePermitReqBtn = new JButton("Save Documents");
      	savePermitReqBtn.setBounds(895, 260, 150, 25);
      	infoPanel.add(savePermitReqBtn);
      
      	this.setLayout(null);
      	this.add(tablePanel); 
      	infoPanel.add(qutPanel);
      	infoPanel.add(sitePanel);
      	infoPanel.add(photoPanel);
      	this.add(infoPanel);
      
	  	tablePanel.add(scrollPane, BorderLayout.CENTER);
	  	tablePanel.add(salesTbl.getTableHeader(), BorderLayout.NORTH);        

	  	
	  	viewQutBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 				   
					if (qutExists){
					      if (Desktop.isDesktopSupported()) {
					    	    try {
					    	        Desktop.getDesktop().open(quote);
					    	    } catch (IOException ex) {
					    	    }
					    	}
					}			   
			   }
			}
		});
	  	viewSiteBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 				   
					if (siteExists){
					      if (Desktop.isDesktopSupported()) {
					    	    try {
					    	        Desktop.getDesktop().open(site);
					    	    } catch (IOException ex) {
					    	    }
					    	}
					}			   
			   }
			}
		});
	  	viewPhotoBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 		
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
			}
		});
	  	removeQutBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   int i = qutDZ.getSelectedIndex();	//getSelectedValue();//getSelectedItem();
				   if(i!=-1){
					   qutLM.remove(i);
				   }
				   else{
					   sp.showMessage("Please Select a File to Remove");
				   }
			   }
			}
		});
	  	removeSiteBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   int i = siteDZ.getSelectedIndex();	//getSelectedValue();//getSelectedItem();
				   if(i!=-1){
					   siteLM.remove(i);
				   }
				   else{
					   sp.showMessage("Please Select a File to Remove");
				   }
			   }
			}
		});
	  	removePhotoBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   int i = photoDZ.getSelectedIndex();	//getSelectedValue();//getSelectedItem();
				   if(i!=-1){
					   photoLM.remove(i);
				   }
				   else{
					   sp.showMessage("Please Select a File to Remove");
				   }
			   }
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
		savePermitReqBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   if(rowSelected){
					   if(allowSave()){						   
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
						quoteNum = salesTbl.getValueAt(salesTbl.getSelectedRow(), 0).toString();

					detailsTxtArea.setText(sp.DisplayClientDetails(quoteNum));
					displayClientDetails(quoteNum);
					checkForFiles();
										
					} catch (IndexOutOfBoundsException e){
						//Ignoring IndexOutOfBoundsExceptions!
					}
					}
				}
		  	});
	  	resetTable();
	}

protected void updateInstallDocs(String invoice) {
	
		CallableStatement pm = null;
		try {
					
		    Connection conn = connecting.CreateConnection(conDeets);	        	   	
		
		    pm = conn.prepareCall(updateDocs);
			
		    pm.setString(1, invoice);
		    pm.setString(2, getQutLoaded());
		    pm.setString(3,	saleID);
		    pm.setString(4, getSiteLoaded());
		    pm.setString(5, getPhotoLoaded());
		    
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
    	int input = JOptionPane.showConfirmDialog(null,"This Invoice already exists!\n "
    			+ "Do you want to Overwrite existing file?",  "Overwrite existing Invoice?", JOptionPane.YES_NO_OPTION);
    	if (input == 0){    		
    		saveInv(qutLM.getElementAt(0));   		
    			} 
	} else if (qutLM.getSize() >0){
		saveInv(qutLM.getElementAt(0));		
	}
//	invLM.removeElementAt(0);
	
	
	if (siteExists || siteLM.getSize() >0 ){
		sck = "Loaded";
	}
	if (siteExists && siteLM.getSize() >0){
    	int input = JOptionPane.showConfirmDialog(null,"This Site Check already exists!\n "
    			+ "Do you want to Overwrite existing file?",  "Overwrite existing Site Check?", JOptionPane.YES_NO_OPTION);
    	if (input == 0){
    		saveSite(siteLM.getElementAt(0));
    			} else{
    				sp.showMessage("dont save inv");
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
	updateInstallDocs(quoteNum);
	resetTable();
}

/*
 * Saves INVOICE to the correct file path
 */
protected void saveInv(Object f){
	if(f instanceof File){
		quote = (File)f;
		File src = new File(quote.getAbsolutePath());
		File target = new File(folder+qutPfx+quoteNum+".pdf");

		try {
			Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
		} catch (IOException e) {
			sp.showMessage("Saving Invoice Failed!");
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
protected Boolean allowSave() {

	saveAllowed=true;
	msg = "";
	if (qutLM.getSize()>0 || siteLM.getSize()>0 || photoLM.getSize()>0 ){
	
	if (qutLM.getSize()>1){
		msg = msg +"Only one Invoice can be saved\n";
		saveAllowed=false;
	}	
	if (qutLM.getSize()==1){
		String file = qutLM.get(0).toString();
		if(!file.endsWith(".pdf")){
			msg = msg +"Only .pdf files are allowed for Invoice\n";
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
		msg = msg +"No Files to Save";
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
 * updates Boolean values invExists, siteExists, photoExists, 
 */
	protected void checkForFiles() {
	//Check for stored Invoice
	quote = new File(folder+qutPfx+quoteNum+".pdf");//Uses InstallID/Invoice number
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

	private void displayClientDetails(String parameter) {
	
	rs = sp.getDetails(getSaleID, quoteNum);
	
    	 try {
			while(rs.next()){
						    					
				 saleID 		= rs.getString("SID");						 						

			}
		} catch (SQLException e) {
			e.printStackTrace();
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
	quoteNum = "";
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
	public Boolean doesInvExist(){
		
		return qutExists;
	}
	public Boolean doesSiteExist(){
		return siteExists;
	}
	public Boolean doesPhotoExist(){
		return photoExists;
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
}

/*
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import net.proteanit.sql.DbUtils;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class QuotePanel extends JPanel {
	//private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
	//private String result3 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
	private String param = "";  
	private ResultSet rs;

	private JTableHeader header;
	private TableColumnModel columnModel;
	private Color LtGray = Color.decode("#eeeeee");
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable salesTbl;
	private DefaultTableModel model1;

	private SalesPane sp;
	private ConnDetails conDeets;
	private CreateConnection connecting;

	private JTextField txtBxReesCode;
	private JTextField txtBxQuoteNum;

	private JScrollPane scrollPane = new JScrollPane(salesTbl);
	private JTextArea txtBxSCheck;
	private JTextArea txtBxQuote;
	private JTextArea txtBxPhoto;
	private JTextArea txtAreaCustInfo;
	private JLabel lblSiteCheck;
	private JLabel lblQuote;
	private JLabel lblPhoto;
	private JLabel lblReeseCode;
	private JLabel lblQuoteNum;
	private JButton btnCancel;
	private JButton btnSave;

	public QuotePanel(ConnDetails ConDeets, SalesPane sp) {
		this.sp = sp;
		this.conDeets = ConDeets;


		connecting = new CreateConnection();

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

				txtAreaCustInfo = new JTextArea("");
				txtAreaCustInfo.setBounds(20, 20, 250, 260);
				txtAreaCustInfo.setBorder(BorderFactory.createEtchedBorder());
				txtAreaCustInfo.setBackground(LtGray);
				txtAreaCustInfo.setLineWrap(true);
				txtAreaCustInfo.setEditable(false);
			        infoPanel.add(txtAreaCustInfo);


				lblReeseCode = new JLabel("Reese Code");
				lblReeseCode.setBounds(583, 54, 74, 14);
				infoPanel.add(lblReeseCode);

				lblQuoteNum = new JLabel("Quote Number");
				lblQuoteNum.setBounds(820, 54, 91, 14);
				infoPanel.add(lblQuoteNum);

				txtBxReesCode = new JTextField();
				txtBxReesCode.setBounds(670, 51, 140, 20);
				infoPanel.add(txtBxReesCode);
				txtBxReesCode.setColumns(10);

				txtBxQuoteNum = new JTextField();
				txtBxQuoteNum.setBounds(920, 51, 113, 20);
				infoPanel.add(txtBxQuoteNum);
				txtBxQuoteNum.setColumns(10);

				txtBxSCheck = new JTextArea();
				txtBxSCheck.setBounds(583, 124, 97, 94);
				infoPanel.add(txtBxSCheck);

				txtBxQuote = new JTextArea();
				txtBxQuote.setBounds(757, 124, 97, 94);
				infoPanel.add(txtBxQuote);

				txtBxPhoto = new JTextArea();
				txtBxPhoto.setBounds(936, 124, 97, 94);
				infoPanel.add(txtBxPhoto);

				lblSiteCheck = new JLabel("Site Check");
				lblSiteCheck.setBounds(593, 104, 72, 14);
				infoPanel.add(lblSiteCheck);

				lblQuote = new JLabel("Quote");
				lblQuote.setBounds(787, 104, 46, 14);
				infoPanel.add(lblQuote);

				lblPhoto = new JLabel("Photo");
				lblPhoto.setBounds(956, 104, 46, 14);
				infoPanel.add(lblPhoto);

				btnCancel = new JButton("Cancel");
				btnCancel.setBounds(593, 227, 148, 23);
				infoPanel.add(btnCancel);
				btnCancel.addActionListener( new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0) {
						{ 
							resetTable();
							//resetting the other textboxes
							txtBxReesCode.setText(null);
							txtBxQuoteNum.setText(null);
							txtBxSCheck.setText(null);
							txtBxQuote.setText(null);
							txtBxPhoto.setText(null);
						}					
					}
				});

				btnSave = new JButton("Save Quote Details");
				btnSave.setBounds(885, 227, 148, 23);
				infoPanel.add(btnSave);

				this.setLayout(null);
				this.add(tablePanel); 
				this.add(infoPanel);

				tablePanel.add(scrollPane, BorderLayout.CENTER);
				tablePanel.add(salesTbl.getTableHeader(), BorderLayout.NORTH);

				salesTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent arg0) {
						if (!arg0.getValueIsAdjusting()){
							//rowSelected=true;
							try{
								param = salesTbl.getValueAt(salesTbl.getSelectedRow(), 0).toString();
								//displayClientDetails(param);
								txtAreaCustInfo.setText(sp.DisplayClientDetails(param));
							} catch (IndexOutOfBoundsException e){
							}
						}
					}
				});
	}

	protected void resetTable() {
		//Fix this little null error 
		ResultSet rs = sp.getResults(3);
		salesTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
		//spaceHeader(columnModel, columnWidth);
		//sentChk.setSelected(false);
		//rowSelected=false;
		param = "";
		txtAreaCustInfo.setText("");

	}	

	public JTable getSalesTbl(){
		return salesTbl;
	}
}

//}

	
	}
}
 
*/