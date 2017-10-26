package Sales;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import net.proteanit.sql.DbUtils;

import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

class FollowUpPanel extends JPanel {
	//private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
	//private String result3 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
	private String param = "";  
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
	private JSpinner spnTimeDate;
	private SimpleDateFormat dt;
	private JRadioButton rdBtnNxtFlwUp;
	private JRadioButton rdBtnInvoice;
	private JRadioButton rdBtnSoldEls;
	private JButton btnCancel;
	private JButton btnSave;
	private JButton btnViewSC;
	private JButton btnViewQuote;
	private JButton btnViewPhoto;
	private Boolean rowSelected;


	private JTextField txtBxInvNumb;

	  public FollowUpPanel(ConnDetails ConDeets, SalesPane sp) {
		  
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

			
			SimpleDateFormat dt = new SimpleDateFormat("dd.MMM.yyyy");
			spnTimeDate = new JSpinner(new SpinnerDateModel());
	        spnTimeDate.setEditor(new JSpinner.DateEditor(spnTimeDate, dt.toPattern()));
			spnTimeDate.setBounds(753, 29, 208, 20);
			infoPanel.add(spnTimeDate);
			
			rdBtnNxtFlwUp = new JRadioButton("Next Follow Up");
			rdBtnNxtFlwUp.setBounds(563, 25, 151, 23);
			infoPanel.add(rdBtnNxtFlwUp);
			
			rdBtnInvoice = new JRadioButton("Invoice");
			rdBtnInvoice.setBounds(563, 55, 109, 23);
			infoPanel.add(rdBtnInvoice);
						
			rdBtnSoldEls = new JRadioButton("Sold Elsewhere");
			rdBtnSoldEls.setBounds(563, 83, 151, 23);
			infoPanel.add(rdBtnSoldEls);
			
			ButtonGroup group = new ButtonGroup();
			group.add(rdBtnNxtFlwUp);
			group.add(rdBtnInvoice);
			group.add(rdBtnSoldEls);
			
			txtBxInvNumb = new JTextField();
			txtBxInvNumb.setBounds(753, 58, 208, 20);
			infoPanel.add(txtBxInvNumb);
			txtBxInvNumb.setColumns(10);

			
			btnCancel = new JButton("Cancel");
			btnCancel.setBounds(563, 204, 148, 23);
			infoPanel.add(btnCancel);
			btnCancel.addActionListener( new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) {
				   { 
					   resetTable();
					   
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
			
			btnSave = new JButton("Save Details");
			btnSave.setBounds(855, 204, 148, 23);
			infoPanel.add(btnSave);
			
			btnViewSC = new JButton("Visit Site Check");
			btnViewSC.setBounds(569, 117, 125, 23);
			infoPanel.add(btnViewSC);
			
			btnViewQuote = new JButton("View Quotation");
			btnViewQuote.setBounds(753, 117, 125, 23);
			infoPanel.add(btnViewQuote);
			
			btnViewPhoto = new JButton("View Photo");
			btnViewPhoto.setBounds(912, 117, 125, 23);
			infoPanel.add(btnViewPhoto);
			
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
			
			salesTbl.clearSelection();
			
			rs = sp.getResults(4);
			salesTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
	//	  	spaceHeader();
		  	
			rowSelected=false;
			param = "";
			txtAreaCustInfo.setText("");
			
			   rdBtnNxtFlwUp.setSelected(false);
			   rdBtnInvoice.setSelected(false);
			   txtBxInvNumb.setText(null);
			   rdBtnSoldEls.setSelected(false);
	//	        spnTimeDate.setEditor(new JSpinner.DateEditor(spnTimeDate, dt.toPattern()));

	
/*			
			salesTbl.clearSelection();
			//Fix this little null error 
//			ResultSet rs = sp.getResults(4,  conDeets);
//		  	salesTbl.setModel(DbUtils.resultSetToTableModel(rs)); 		  	
		  	//spaceHeader(columnModel, columnWidth);
		  	//sentChk.setSelected(false);
		  	
			//rowSelected=false;
			param = "";
			txtAreaCustInfo.setText("");
*/
		}	
  
  public JTable getSalesTbl(){
  	return salesTbl;
  }
  
  public JPanel getInfoPanel(){
		return infoPanel;
	}
	
	
}

