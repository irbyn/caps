package Sales;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import Main.GetJobs;
import net.proteanit.sql.DbUtils;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;

class SiteCheckPanel extends JPanel {
	//private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
	//private String result3 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
	private int [] columnWidth = { 50, 50, 100, 100, 100};
	private String param = "";  
	private ResultSet rs;

	private CreateConnection connecting;

	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JTable salesTbl;
	private DefaultTableModel model1;
	private JTextArea txtAreaCustInfo;
	private Color LtGray = Color.decode("#eeeeee");
	private SalesPane sp;
	private ConnDetails conDeets;

	private JLabel lblSChkBooking;
	private JSpinner spnTimeDate;
	private JComboBox comBxSChkDoneBy;
	private JLabel lblSiteCheckBy;
	private JCheckBox chckbxSChkComp;
	private JButton btnCancel;
	private JButton btnSave;
	private JTable table;

	public SiteCheckPanel(ConnDetails conDeets, SalesPane sp) {

		this.sp = sp;
		this.conDeets = conDeets;

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

		SimpleDateFormat dt = new SimpleDateFormat("dd.MMM.yyyy");
		spnTimeDate = new JSpinner(new SpinnerDateModel());
		spnTimeDate.setEditor(new JSpinner.DateEditor(spnTimeDate, dt.toPattern()));
		spnTimeDate.setBounds(634, 65, 284, 20);
		infoPanel.add(spnTimeDate);

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

		DefaultComboBoxModel modelSC = new DefaultComboBoxModel( SC );
		comBxSChkDoneBy.setModel( modelSC );
		
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

		chckbxSChkComp = new JCheckBox("Site Check Completed");
		chckbxSChkComp.setBounds(634, 159, 180, 23);
		infoPanel.add(chckbxSChkComp);

		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(478, 209, 148, 23);
		infoPanel.add(btnCancel);
		btnCancel.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				{ 
					if (checkTxtBx()) {
						int dialogButton = JOptionPane.YES_NO_OPTION;
						int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to cancel?","Warning",dialogButton);
						if(dialogResult == JOptionPane.YES_OPTION){
							resetTable();
							//Resetting the other attributes within the tab
							spnTimeDate.setEditor(new JSpinner.DateEditor(spnTimeDate, dt.toPattern()));
							comBxSChkDoneBy.setSelectedItem(null);
							chckbxSChkComp.setSelected(false);
						}
					}
				}					
			}
		});
		
		btnSave = new JButton("Save Details");
		btnSave.setBounds(770, 209, 148, 23);
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
				|| !(comBxSChkDoneBy.getSelectedIndex() == 0) || chckbxSChkComp.isSelected()){
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

}
