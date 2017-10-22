package Installs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
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
import Permit.PermitPane;
import net.proteanit.sql.DbUtils;



class LoadDocsPanel extends JPanel {
	
	private int [] columnWidth = {30, 100, 120, 80}; 	
	private String getSaleID = "EXEC AWS_WCH_DB.dbo.[i_InstallsGetSaleID] ";
	private String updateDocs = "{Call AWS_WCH_DB.dbo.[i_InstallsUpdateDocs] (?,?,?,?,?)}";
	
	private String invoiceNum = "";  
	private String saleID = "";  
	private ResultSet rs;
	private Color LtGray = Color.decode("#eeeeee");

	private File inv;
	private File site;
	private File photo;
	private File[] photosArr;
	private int photoLimit = 5;
	private String folder = "//C:/pdfs/Invoice/";
	private String invPfx = "INV_";
	private String sitePfx = "SC_";
	private String photoPfx = "PH_";
	
	private String invc;
	private String sck;
	private String pht;
	
	private String invFile;
	private String siteFile;
	private String photoFile;
	
	private Boolean rowSelected = false;
	private Boolean invExists = false;
	private Boolean siteExists = false;
	private Boolean photoExists = false;
	private Boolean saveINV = false;
	private Boolean saveSC = false;
	private Boolean savePH = false;
	private Boolean saveAllowed = true;
	
	private String msg;
	
	private CreateConnection connecting;
	
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable permitsTbl;
	private DefaultTableModel model1;
	
	private JTextArea detailsTxtArea;
	private JPanel invPanel;
	private DefaultListModel invLM;
	private JList invDZ;
    private JScrollPane invSP;
    private JButton removeInvBtn;
    private JButton viewInvBtn;
    
	private JPanel sitePanel;
	private DefaultListModel siteLM;
	private JList siteDZ;
    private JScrollPane siteSP;
    
	private JPanel photoPanel;	
	private DefaultListModel photoLM;
	private JList photoDZ;
    private JScrollPane photoSP;
    
    private JButton removeSiteBtn;
    private JButton viewSiteBtn;
    private JButton removePhotoBtn;
    private JButton viewPhotoBtn;
	
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	
	private CreateConnection conn;
	
	private ImageIcon fll;
	private ImageIcon pic;
	
	private Boolean lockForm;
	private ConnDetails conDeets;
	private InstallsPane ip;


public LoadDocsPanel(Boolean lockForm, ConnDetails conDetts, InstallsPane ipn) {

		this.lockForm = lockForm;
		this.conDeets = conDetts;
		this.ip = ipn;

		connecting = new CreateConnection();
		fll = new ImageIcon(getClass().getResource("pdf.png"));
		pic = new ImageIcon(getClass().getResource("pictures.png"));
		  	
	    model1 = new DefaultTableModel();  
	    model1.setRowCount(0);
	    
	    permitsTbl = new JTable(model1);
	    permitsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    permitsTbl.setAutoCreateRowSorter(true);
      
	    JScrollPane scrollPane = new JScrollPane(permitsTbl);
	  
	    header= permitsTbl.getTableHeader();
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
	    detailsTxtArea.setFont(new Font("Arial", Font.BOLD, 12));
	    infoPanel.add(detailsTxtArea);
      
	    invPanel = new JPanel();
	    invPanel.setBounds(405, 20, 200, 210); 
	    invPanel.setOpaque(true);
	    invPanel.setVisible(true);
	    invPanel.setLayout(null);
      
	    invLM = new DefaultListModel();
	    invDZ = new JList(invLM);
	    invDZ.setCellRenderer(new FileCellRenderer());
	    invDZ.setTransferHandler(new ListTransferHandler(invDZ));
	    invDZ.setDragEnabled(true);
	    invDZ.setDropMode(javax.swing.DropMode.INSERT);
	    invDZ.setBorder(new TitledBorder("Drop Invoice pdf here"));
	    invSP = new javax.swing.JScrollPane(invDZ);
	    invSP.setBounds(0, 0, 200, 100);
	    invPanel.add(invSP);

	    viewInvBtn = new JButton(fll);
	    viewInvBtn.setBounds(0, 110, 40, 40);
	    invPanel.add(viewInvBtn);
	    removeInvBtn = new JButton("Remove Invoice");
	    removeInvBtn.setBounds(50, 110, 150, 25);
	    invPanel.add(removeInvBtn);
      
	    sitePanel = new JPanel();
	    sitePanel.setBounds(625, 20, 200, 210); 
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
	    removeSiteBtn = new JButton("Remove SiteCheck");
	    removeSiteBtn.setBounds(50, 110, 150, 25);
	    sitePanel.add(removeSiteBtn);
      
	    photoPanel = new JPanel();
	    photoPanel.setBounds(845, 20, 200, 210); 
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
      	cancelPermitReqBtn.setBounds(720, 260, 150, 25);
      	infoPanel.add(cancelPermitReqBtn);
      
      	savePermitReqBtn = new JButton("Save Documents");
      	savePermitReqBtn.setBounds(895, 260, 150, 25);
      	infoPanel.add(savePermitReqBtn);
      
      	this.setLayout(null);
      	this.add(tablePanel); 
      	infoPanel.add(invPanel);
      	infoPanel.add(sitePanel);
      	infoPanel.add(photoPanel);
      	this.add(infoPanel);    
      
	  	tablePanel.add(scrollPane, BorderLayout.CENTER);
	  	tablePanel.add(permitsTbl.getTableHeader(), BorderLayout.NORTH);        

	  	
	  	viewInvBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 				   
					if (invExists){
					      if (Desktop.isDesktopSupported()) {
					    	    try {
					    	        Desktop.getDesktop().open(inv);
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
				   ip.showMessage("" + ph);
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
	  	removeInvBtn.addActionListener( new ActionListener()
		{	@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				   int i = invDZ.getSelectedIndex();	//getSelectedValue();//getSelectedItem();
				   if(i!=-1){
					   invLM.remove(i);
				   }
				   else{
					   ip.showMessage("Please Select a File to Remove");
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
					   ip.showMessage("Please Select a File to Remove");
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
					   ip.showMessage("Please Select a File to Remove");
				   }
			   }
			}
		});
	  	
		cancelPermitReqBtn.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
			   { 
				    String fonts[] = 
				    	      GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

				    	    for ( int i = 0; i < fonts.length; i++ )
				    	    {
				    	      System.out.println(fonts[i]);
				    	    }
		//		   resetTable();
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
					   ip.showMessage("No Customer Selected");
				   }
			   }
			}
		});
	  	permitsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					rowSelected=true;
					invLM.removeAllElements();
					siteLM.removeAllElements();
					photoLM.removeAllElements();						
					
		//			pp.setFormsLocked();
					try{
						invoiceNum = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 0).toString();

					detailsTxtArea.setText(ip.DisplayClientDetails(invoiceNum));
					displayClientDetails(invoiceNum);
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
		    pm.setString(2, getInvLoaded());
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
	invc = "";
	sck = "";
	pht = "";	
	saveINV = false;
	saveSC = false;
	savePH = false;
	
	if (invExists || invLM.getSize() >0 ){
		invc = "Loaded";
	}
	if (invExists && invLM.getSize() >0){
    	int input = JOptionPane.showConfirmDialog(null,"This Invoice already exists!\n "
    			+ "Do you want to Overwrite existing file?",  "Overwrite existing Invoice?", JOptionPane.YES_NO_OPTION);
    	if (input == 0){    		
    		saveInv(invLM.getElementAt(0));   		
    			} 
	} else if (invLM.getSize() >0){
		saveInv(invLM.getElementAt(0));		
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
    				ip.showMessage("dont save inv");
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
	updateInstallDocs(invoiceNum);
	resetTable();
}

/*
 * Saves INVOICE to the correct file path
 */
protected void saveInv(Object f){
	if(f instanceof File){
		inv = (File)f;
		File src = new File(inv.getAbsolutePath());
		File target = new File(folder+invPfx+invoiceNum+".pdf");

		try {
			Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
		} catch (IOException e) {
			ip.showMessage("Saving Invoice Failed!");
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
			ip.showMessage("Saving Site Check Failed!");
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
			ip.showMessage("Saving Photo Failed!");
		}		
	}
}


/*
 * Checks for any reason files should not be saved: too many or wrong type!
 */
protected Boolean allowSave() {

	saveAllowed=true;
	msg = "";
	if (invLM.getSize()>0 || siteLM.getSize()>0 || photoLM.getSize()>0 ){
	
	if (invLM.getSize()>1){
		msg = msg +"Only one Invoice can be saved\n";
		saveAllowed=false;
	}	
	if (invLM.getSize()==1){
		String file = invLM.get(0).toString();
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
	inv = new File(folder+invPfx+invoiceNum+".pdf");//Uses InstallID/Invoice number
	if (inv.exists()){
		viewInvBtn.setVisible(true);
		invExists = true;
	}else{
		viewInvBtn.setVisible(false);
		invExists = false;
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
	
	rs = ip.getDetails(getSaleID, invoiceNum);
	
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
		invLM.removeAllElements();
		siteLM.removeAllElements();
		photoLM.removeAllElements();
	}

	protected void resetTable() {	
	ResultSet rs = ip.getResults(0,  conDeets);
  	permitsTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
  	spaceHeader(columnModel, columnWidth);
  	
	rowSelected=false;
	invoiceNum = "";
	detailsTxtArea.setText("");
	clearDrops();
	viewInvBtn.setVisible(false);
	viewSiteBtn.setVisible(false);
	viewPhotoBtn.setVisible(false);
	invExists = false;
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

	public JTable getPermitsTbl(){
		return permitsTbl;
	}
	public Boolean doesInvExist(){
		
		return invExists;
	}
	public Boolean doesSiteExist(){
		return siteExists;
	}
	public Boolean doesPhotoExist(){
		return photoExists;
	}
	private String getInvLoaded() {
		return invc;
	}
	private String getSiteLoaded() {
		return sck;
	}
	private String getPhotoLoaded() {
		return pht;
	}

}
