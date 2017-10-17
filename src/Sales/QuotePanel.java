package Sales; 

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

/*	
	}
}
 */
