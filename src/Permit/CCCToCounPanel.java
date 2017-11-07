package Permit;

import javax.swing.JPanel;
import javax.swing.JTable;

import Main.ConnDetails;

/*
 * NOT IN USE! CURRENTLY MERGED WITH PRODUCER STATEMENT TAB!!!
 * GUI PANEL:	PERMITS - CCC To Council
 * Allows User to record CCC application sent to  council 
 */


class CCCToCounPanel extends JPanel {
	/*
	private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsDetails] ";
	private String result3 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
	private String invoiceNum = "";  
	private ResultSet rs;
	private Color LtGray = Color.decode("#eeeeee");
	private CreateConnection connecting;

	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;		
*/	private JPanel infoPanel;
	private JTable permitsTbl;	/*
	private DefaultTableModel model1;
	private JScrollPane scrollPane;

	private JTextArea detailsTxtArea;

	private JLabel nelsonLbl;
	private JTextField nelsonTxtBx;

	private JButton prntConsentBtn; 
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 

	private String user = "";
	private String pass = "";
	private String dbURL = "";

	private CreateConnection conn;
	private ConnDetails conDets;
*/
	public CCCToCounPanel(ConnDetails conDeets, PermitPane pp) {
		/*
		conDets = conDeets;
		//Get User connection details
		user = conDeets.getUser();
		pass = conDeets.getPass();
		dbURL = conDeets.getURL();

		connecting = new CreateConnection();

		model1 = new DefaultTableModel();  
		model1.setRowCount(0);
		permitsTbl = new JTable(model1);
		permitsTbl.setPreferredSize(new Dimension(0, 300));
		permitsTbl.setAutoCreateRowSorter(true);
		scrollPane = new JScrollPane(permitsTbl);
		header= permitsTbl.getTableHeader();
		columnModel = header.getColumnModel();
		add(header); 

		tablePanel = new JPanel();
		tablePanel.setBounds(20, 20, 1025, 260);  //setPreferredSize(new Dimension(0, 300));      
		tablePanel.setLayout(new BorderLayout());

		infoPanel = new JPanel();
		infoPanel.setBounds(0, 280, 1100, 300);  //setPreferredSize(new Dimension(0, 300));
		infoPanel.setLayout(null);

		detailsTxtArea = new JTextArea("");
		detailsTxtArea.setBounds(20, 20, 1025, 140);
		detailsTxtArea.setBorder(BorderFactory.createEtchedBorder());
		detailsTxtArea.setBackground(LtGray);
		detailsTxtArea.setLineWrap(true);
		detailsTxtArea.setEditable(false);
		infoPanel.add(detailsTxtArea);

		nelsonLbl = new JLabel("Nelson:");
		nelsonLbl.setBounds(825, 170, 70, 20);
		infoPanel.add(nelsonLbl);
		nelsonTxtBx = new JTextField(10);
		nelsonTxtBx.setBounds(895, 170, 150, 20);
		infoPanel.add(nelsonTxtBx);

		prntConsentBtn = new JButton("Print Consent");
		prntConsentBtn.setBounds(545, 260, 150, 25);
		infoPanel.add(prntConsentBtn);

		cancelPermitReqBtn = new JButton("Cancel");
		cancelPermitReqBtn.setBounds(720, 260, 150, 25);
		infoPanel.add(cancelPermitReqBtn);

		savePermitReqBtn = new JButton("Save Permit Details");
		savePermitReqBtn.setBounds(895, 260, 150, 25);
		infoPanel.add(savePermitReqBtn);

		this.setLayout(null);
		this.add(tablePanel); 
		this.add(infoPanel);

		tablePanel.add(scrollPane, BorderLayout.CENTER);
		tablePanel.add(permitsTbl.getTableHeader(), BorderLayout.NORTH);        

		permitsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					try{
						invoiceNum = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 0).toString();
						detailsTxtArea.setText(pp.DisplayClientDetails(invoiceNum));
					} catch (IndexOutOfBoundsException e){
						//
					}
				}
			}
		});
		*/
	}


	public JTable getPermitsTbl(){
		return permitsTbl;
	}
	public JPanel getInfoPanel(){
		return infoPanel;
	}

}
