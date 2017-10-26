package Sales;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
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
import javax.swing.JComboBox;
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
import net.proteanit.sql.DbUtils;

import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

class FollowUpPanel extends JPanel {
	private int [] columnWidth = {30, 30, 80, 100, 100, 100, 40, 40, 40, 50, 50}; 
	private String createInst = "call AWS_WCH_DB.dbo.s_SalesNewInstall";
	private String updateFollowUp = "call AWS_WCH_DB.dbo.s_SalesUpdateFollowUp";
	private String param = "";
	private String paramSID = ""; 
	private ResultSet rs;
	private DefaultTableModel model1;
	private CreateConnection connecting;
	private ConnDetails conDeets;
	private SalesPane sp;
	private JTableHeader header;
	private TableColumnModel columnModel;
	private Color LtGray = Color.decode("#eeeeee");
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable salesTbl;
	private JTextArea txtAreaCustInfo;
	private JSpinner spnDate;
	private SimpleDateFormat dt;
	private JRadioButton rdBtnNxtFlwUp;
	private JRadioButton rdBtnInvoice;
	private JRadioButton rdBtnSoldEls;
	private JButton btnCancel;
	private JButton btnSave;
	//private JButton btnViewSC;
	//private JButton btnViewQuote;
	//private JButton btnViewPhoto;
	private JButton viewSiteBtn;
	private JButton viewQutBtn;
	private JButton viewPhotoBtn;
	private Boolean rowSelected;
	private Date date;
	public ButtonGroup group;
	
	private File quote;
	private File site;
	private File photo;
	private Boolean qutExists = false;
	private Boolean siteExists = false;
	private Boolean photoExists = false;
	private ImageIcon fll;
	private ImageIcon pic;
	private File[] photosArr;
	
	private String folder = "//C:/pdfs/Invoice/";
	private String qutPfx = "QUT_";
	private String sitePfx = "SC_";
	private String photoPfx = "PH_";

	private JTextField txtBxInvNumb;

	public FollowUpPanel(ConnDetails ConDeets, SalesPane sp) {

		this.sp = sp;
		this.conDeets = ConDeets;
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

				date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
				
				SimpleDateFormat dtModel = new SimpleDateFormat("dd.MMM.yyyy");
				spnDate = new JSpinner(new SpinnerDateModel());
				spnDate.setEditor(new JSpinner.DateEditor(spnDate, dtModel.toPattern()));
				spnDate.setBounds(753, 22, 208, 20);
				spnDate.setValue(date);
				infoPanel.add(spnDate);
				
				
/*				SimpleDateFormat dt = new SimpleDateFormat("dd.MMM.yyyy");
				spnTimeDate = new JSpinner(new SpinnerDateModel());
				spnTimeDate.setEditor(new JSpinner.DateEditor(spnTimeDate, dt.toPattern()));
				spnTimeDate.setBounds(753, 29, 208, 20);
				infoPanel.add(spnTimeDate);*/

				rdBtnNxtFlwUp = new JRadioButton("Next Follow Up");
				rdBtnNxtFlwUp.setBounds(563, 25, 151, 23);
				infoPanel.add(rdBtnNxtFlwUp);

				rdBtnInvoice = new JRadioButton("Invoice");
				rdBtnInvoice.setBounds(563, 55, 109, 23);
				infoPanel.add(rdBtnInvoice);

				rdBtnSoldEls = new JRadioButton("Sold Elsewhere");
				rdBtnSoldEls.setBounds(563, 85, 151, 23);
				infoPanel.add(rdBtnSoldEls);

				group = new ButtonGroup();
				group.add(rdBtnNxtFlwUp);
				group.add(rdBtnInvoice);
				group.add(rdBtnSoldEls);

				txtBxInvNumb = new JTextField();
				txtBxInvNumb.setBounds(753, 58, 208, 20);
				infoPanel.add(txtBxInvNumb);
				txtBxInvNumb.setColumns(10);

				btnCancel = new JButton("Cancel");
				btnCancel.setBounds(563, 240, 164, 23);
				infoPanel.add(btnCancel);

				btnSave = new JButton("Save Details");
				btnSave.setBounds(797, 240, 164, 23);
				infoPanel.add(btnSave);

				/*btnViewSC = new JButton("Visit Site Check");
				btnViewSC.setBounds(569, 117, 125, 23);
				infoPanel.add(btnViewSC);

				btnViewQuote = new JButton("View Quotation");
				btnViewQuote.setBounds(753, 117, 125, 23);
				infoPanel.add(btnViewQuote);

				btnViewPhoto = new JButton("View Photo");
				btnViewPhoto.setBounds(912, 117, 125, 23);
				infoPanel.add(btnViewPhoto);
*/
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
								//displayClientDetails(param);
								txtAreaCustInfo.setText(sp.DisplayClientDetails(param));
								
								checkForFiles();
							} catch (IndexOutOfBoundsException e){

							}
						}
					}
				});

				btnSave.addActionListener( new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						//if invoice is sleceted create a new install
						if (rdBtnInvoice.isSelected()){
							createInstall();
						}
						else{
							updateQuote();
						}
						//else save the information
						
						
						resetTable();
				
					}
				});
				
				btnCancel.addActionListener( new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0) {
						{ 
							
							int dialogButton = JOptionPane.YES_NO_OPTION;
							int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to cancel?","Warning",dialogButton);
							if(dialogResult == JOptionPane.YES_OPTION){
									resetTable();
									clearFields();
							}
						
							//Resetting the other attributes within the tab
							//		   spnTimeDate.setValue(null);

							//   !!ADD THESE TO RESET TABLE					   
							/*					   rdBtnNxtFlwUp.setSelected(false);
					   rdBtnInvoice.setSelected(false);
					   txtBxInvNumb.setText(null);
					   rdBtnSoldEls.setSelected(false);
				        spnTimeDate.setEditor(new JSpinner.DateEditor(spnTimeDate, dt.toPattern()));
							 */					}					
					}
				});
	
				//-------------------------------------------------------
				
				viewSiteBtn = new JButton("View Site Check PDF");
				viewSiteBtn.setBounds(563, 126, 164, 23);
				infoPanel.add(viewSiteBtn);
				
				viewQutBtn = new JButton("View Quote PDF");
				viewQutBtn.setBounds(563, 165, 164, 23);
				infoPanel.add(viewQutBtn);
				
				viewPhotoBtn = new JButton("View Photo(s)");
				viewPhotoBtn.setBounds(797, 126, 164, 23);
				infoPanel.add(viewPhotoBtn);

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
	}

	protected void resetTable() {

		salesTbl.clearSelection();

		rs = sp.getResults(4);
		salesTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
		spaceHeader(columnModel, columnWidth);

		rowSelected=false;
		param = "";
		txtAreaCustInfo.setText("");

		rdBtnNxtFlwUp.setSelected(false);
		rdBtnInvoice.setSelected(false);
		txtBxInvNumb.setText(null);
		rdBtnSoldEls.setSelected(false);
		//spnTimeDate.setEditor(new JSpinner.DateEditor(spnTimeDate, dt.toPattern()));


		/*			
			salesTbl.clearSelection();
			//Fix this little null error 
			//ResultSet rs = sp.getResults(4,  conDeets);
			//salesTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
		  	//spaceHeader(columnModel, columnWidth);
		  	//sentChk.setSelected(false);

			//rowSelected=false;
			param = "";
			txtAreaCustInfo.setText("");
		 */
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
	
	public void clearFields(){
		spnDate.setValue(date);
		txtBxInvNumb.setText("");
		group.clearSelection();
	}

	public JTable getSalesTbl(){
		return salesTbl;
	}

	public JPanel getInfoPanel(){
		return infoPanel;
	}
	
	public void updateQuote(){
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
		return Integer.parseInt(txtBxInvNumb.getText());
	}
	
	protected void checkForFiles() {
		//Check for stored Quote
		quote = new File(folder+qutPfx+paramSID+".pdf");//Uses InstallID/Quote number
		if (quote.exists()){
			viewQutBtn.setVisible(true);
			qutExists = true;
		}else{
			viewQutBtn.setVisible(false);
			qutExists = false;
		}	
		//Check for stored SiteCheck Forms	
		site = new File(folder+sitePfx+paramSID+".pdf");//Uses SaleID number
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
				return name.startsWith(photoPfx+paramSID+"_");	//Uses SaleID number
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

