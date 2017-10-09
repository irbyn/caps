package Installs;

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
import java.io.IOException;
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

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Permit.PermitPane;
import net.proteanit.sql.DbUtils;



class LoadDocsPanel extends JPanel {
	
	private int [] columnWidth = {30, 100, 120, 80}; 	
	private String getSaleID = "EXEC AWS_WCH_DB.dbo.[i_InstallsGetSaleID] ";
	private File inv;
	private File site;
	private File photo;
	private String folder = "//C:/pdfs/Invoice/";
	private String invFile;
	private String siteFile;
	private String photoFile;
	private String param = "";  
	private String saleID = "";  
	private ResultSet rs;
	
	private Boolean rowSelected = false;
	private Boolean invExists = false;
	private Boolean siteExists = false;
	private Boolean photoExists = false;

	
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
    
	
	private JTextField installTxtBx;
	private JTextField siteTxtBx;
	private JTextField photoTxtBx;
	
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	
	private CreateConnection conn;
	
	private Boolean lockForm;
	private ConnDetails conDeets;
	private InstallsPane ip;


public LoadDocsPanel(Boolean lockForm, ConnDetails conDetts, InstallsPane ipn) {

	  this.lockForm = lockForm;
	  this.conDeets = conDetts;
	  this.ip = ipn;

	  connecting = new CreateConnection();
		  	
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
      detailsTxtArea.setBorder(BorderFactory.createLineBorder(Color.black));
      detailsTxtArea.setLineWrap(true);
      detailsTxtArea.setEditable(false);
      infoPanel.add(detailsTxtArea);
      
      invPanel = new JPanel();
      invPanel.setBounds(405, 20, 200, 135); 
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
      
      viewInvBtn = new JButton("View");
      viewInvBtn.setBounds(0, 110, 95, 25);
      invPanel.add(viewInvBtn);
      removeInvBtn = new JButton("Remove");
      removeInvBtn.setBounds(105, 110, 95, 25);
      invPanel.add(removeInvBtn);
      
      sitePanel = new JPanel();
      sitePanel.setBounds(625, 20, 200, 135); 
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
      
      viewSiteBtn = new JButton("View");
      viewSiteBtn.setBounds(0, 110, 95, 25);
      sitePanel.add(viewSiteBtn);
      removeSiteBtn = new JButton("Remove");
      removeSiteBtn.setBounds(105, 110, 95, 25);
      sitePanel.add(removeSiteBtn);
      
      photoPanel = new JPanel();
      photoPanel.setBounds(845, 20, 200, 135); 
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
      
      viewPhotoBtn = new JButton("View");
      viewPhotoBtn.setBounds(0, 110, 95, 25);
      photoPanel.add(viewPhotoBtn);
      removePhotoBtn = new JButton("Remove");
      removePhotoBtn.setBounds(105, 110, 95, 25);
      photoPanel.add(removePhotoBtn);
      
      cancelPermitReqBtn = new JButton("Cancel");
      cancelPermitReqBtn.setBounds(720, 260, 150, 25);
      infoPanel.add(cancelPermitReqBtn);
      
      savePermitReqBtn = new JButton("Save Permit Details");
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
					if (photoExists){
					      if (Desktop.isDesktopSupported()) {
					    	    try {
					    	        Desktop.getDesktop().open(photo);
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
				//	   ip.showMessage("HI");
					   
					   JOptionPane.showMessageDialog(null, invLM.getElementAt(0).toString());
					   JOptionPane.showMessageDialog(null, siteLM.getElementAt(0).toString());
					   JOptionPane.showMessageDialog(null, photoLM.getElementAt(0).toString());					   
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
					param = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 0).toString();
					
					detailsTxtArea.setText(ip.DisplayClientDetails(param));
					displayClientDetails(param);
					checkForFiles();
										
					} catch (IndexOutOfBoundsException e){
						//Ignoring IndexOutOfBoundsExceptions!
					}
					}
				}
		  	});
	  	resetTable();
}
protected void checkForFiles() {
	inv = new File(folder+"INV_"+param+".pdf");
	if (inv.exists()){
		viewInvBtn.setVisible(true);
		invExists = true;
	}else{
		viewInvBtn.setVisible(false);
		invExists = false;
	}					
	site = new File(folder+"SC_"+saleID+".pdf");
	if (site.exists()){
		viewSiteBtn.setVisible(true);
		siteExists = true;
	}else{
		viewSiteBtn.setVisible(false);
		siteExists = false;
	}
	photo = new File(folder+"PH_"+saleID+".pdf");
	if (photo.exists()){
		viewPhotoBtn.setVisible(true);
		photoExists = true;
	}else{
		viewPhotoBtn.setVisible(false);
		photoExists = false;
	}
	
}
private void displayClientDetails(String parameter) {
	
	rs = ip.getDetails(getSaleID, param);
	
    	 try {
			while(rs.next()){
						    					
				 saleID 		= rs.getString("SID");						 						

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

protected void resetTable() {
	
	ResultSet rs = ip.getResults(0,  conDeets);
  	permitsTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
  	spaceHeader(columnModel, columnWidth);
  	
	rowSelected=false;
	param = "";
	detailsTxtArea.setText("");
	invLM.removeAllElements();
	siteLM.removeAllElements();
	photoLM.removeAllElements();
	viewInvBtn.setVisible(true);
	viewSiteBtn.setVisible(true);
	viewPhotoBtn.setVisible(true);
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

}
