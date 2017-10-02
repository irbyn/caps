package Permit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import DB_Comms.CreateConnection;
import Main.ConnDetails;
import net.proteanit.sql.DbUtils;

class ProdStatementPanel extends JPanel {

	private String result2 = "EXEC AWS_WCH_DB.dbo.[p_PermitsCCC] ";
	private String result3 = "EXEC AWS_WCH_DB.dbo.[p_PermitFire] ";
	private String param = "";  
	private ResultSet rs;
	
	private CreateConnection connecting;
	
	private JTableHeader header;
	private TableColumnModel columnModel;
	private JPanel tablePanel;
	private JPanel infoPanel;
	private JPanel updatePanel;
	private JTable permitsTbl;
	private JTable updateTbl;
	private DefaultTableModel model;
	private DefaultTableModel model1;
	
	private JTextArea detailsTxtArea;
	private JTextArea tmpTxt;
	
	private JLabel ecanLbl;
	private JTextField ecanTxtBx;
	private JLabel pSLbl;
	private JSpinner pSDate;
	
	private JButton prntConsentBtn; 
	private JButton cancelPermitReqBtn; 
	private JButton savePermitReqBtn; 
	
	private String user = "";
	private String pass = "";
	private String dbURL = "";
	
	private ConnDetails conDets;


//	private CreateConnection conn;

	  public ProdStatementPanel(ConnDetails conDeets, PermitPane pp) {
		  
			
			conDets = conDeets;


	     	//Get User connection details
	  		user = conDeets.getUser();
	  		pass = conDeets.getPass();
	  		dbURL = conDeets.getURL();
	  		
			  connecting = new CreateConnection();
		  	 		  	
			    model = new DefaultTableModel();  
			    model.setRowCount(0);
		        permitsTbl = new JTable(model);
		        permitsTbl.setPreferredSize(new Dimension(0, 300));
		        permitsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		        permitsTbl.setAutoCreateRowSorter(true);
		        
		        JScrollPane scrollPane = new JScrollPane(permitsTbl);
			  
		        header= permitsTbl.getTableHeader();
		        columnModel = header.getColumnModel();
		        add(header); 
		        
/*		        
		        
			    model1 = new DefaultTableModel();  
			    model1.setRowCount(0);
			    model1.setColumnCount(5);			    
			    updateTbl = new JTable(model1);
			    updateTbl.setPreferredSize(new Dimension(0, 300));
			    updateTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			    updateTbl.setAutoCreateRowSorter(true);
		        
		        JScrollPane scrollP = new JScrollPane(updateTbl);
*/			  	                	        
		        tablePanel = new JPanel();
		        tablePanel.setBounds(20, 20, 1025, 260);  //setPreferredSize(new Dimension(0, 300));      
		        tablePanel.setLayout(new BorderLayout());
		        
		        infoPanel = new JPanel();
		        infoPanel.setBounds(0, 280, 1100, 300);  //setPreferredSize(new Dimension(0, 300));
		        infoPanel.setLayout(null);
/*		        
		        updatePanel = new JPanel();
		        updatePanel.setBounds(320, 20, 725, 120);  //setPreferredSize(new Dimension(0, 300));
		        updatePanel.setLayout(new BorderLayout());
		        updatePanel.setVisible(true);
		        infoPanel.add(updatePanel);
*/		        
		        detailsTxtArea = new JTextArea("");
		        detailsTxtArea.setBounds(20, 20, 250, 260);
		        detailsTxtArea.setBorder(BorderFactory.createLineBorder(Color.black));
		        detailsTxtArea.setLineWrap(true);
		        detailsTxtArea.setEditable(false);
		        infoPanel.add(detailsTxtArea);
		        	        
		        pSLbl = new JLabel("Date for PS4:");
		        pSLbl.setBounds(800, 170, 95, 20);
		        infoPanel.add(pSLbl);

		        SimpleDateFormat psModel = new SimpleDateFormat("dd.MMM.yyyy");
				pSDate = new JSpinner(new SpinnerDateModel());
				pSDate.setEditor(new JSpinner.DateEditor(pSDate, psModel.toPattern()));
				pSDate.setBounds(895, 170, 150, 20);
				infoPanel.add(pSDate);        
		        
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
			  	
	//		  	updatePanel.add(scrollP, BorderLayout.CENTER);
			  	
			  	permitsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent arg0) {
						if (!arg0.getValueIsAdjusting()){
							try{
								param = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 0).toString();
							
							String	ss = permitsTbl.getValueAt(permitsTbl.getSelectedRow(), 8).toString();
							
							assemblePSDetails(param);
							} catch (IndexOutOfBoundsException e){
								//
							}
						}
					}
			  	});
			  	
			  	savePermitReqBtn.addActionListener( new ActionListener()
			  	{
					@Override
					public void actionPerformed(ActionEvent arg0) {
				        { 
					        JOptionPane.showMessageDialog(null, "Sorry... Printing not enabled");
					    }					
					}
			  	});

			  	cancelPermitReqBtn.addActionListener( new ActionListener()
			  	{
					@Override
					public void actionPerformed(ActionEvent arg0) {
				        { 
				  //      	clearFields();
							 Date date = (Date) pSDate.getValue();
							 SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");
							 detailsTxtArea.setText(sdf.format(date)); 
					    }					
					}
			  	});

		        prntConsentBtn.addActionListener( new ActionListener()
			  	{
					@Override
					public void actionPerformed(ActionEvent arg0) {
				        { 
					        JOptionPane.showMessageDialog(null, "Sorry... Printing not enabled");
					    }					
					}
			  	});
	  }

		    
		    public JTable getPermitsTbl(){
		    	return permitsTbl;
		    }
		    
			
			private void assemblePSDetails(String parameter) {
		        try
		        {
		        	Connection conn = connecting.CreateConnection(conDets);
		        	PreparedStatement st2 =conn.prepareStatement(result2 + parameter);
		        	ResultSet rs2 = st2.executeQuery();
		    
		                //Retrieve by column name
		        	 while(rs2.next()){
				                        
						 String invoice 		= rs2.getString("Invoice");
						 String customerName 	= rs2.getString("CustomerName");					//4
						 String customerAddress = rs2.getString("CustomerAddress");					//5a
						 String customerSuburb 	= rs2.getString("CustomerSuburb");					//5b
						 String customerPostCode= rs2.getString("CustomerPostCode");				//6
						 String customerPhone 	= rs2.getString("CustomerPhone");					//
						 String customerMobile 	= rs2.getString("CustomerMobile");					//
						 String customerEmail 	= rs2.getString("CustomerEmail");					//
						 String streetAddress 	= rs2.getString("StreetAddress");					//3a
						 String suburb 			= rs2.getString("Suburb");							//3b
						 String consent 		= rs2.getString("Consent");							//1		2=DATE GRANTED!		
						 Date consentDate		= rs2.getDate("ConsentDate");
						 String lot 			= rs2.getString("Lot");
						 String dP				= rs2.getString("DP"); 
						 String installerID 	= rs2.getString("Installer_ID"); 
						 Date installDate		= rs2.getDate("InstallDate");
						 String instName 		= rs2.getString("InstName"); 
						 String instMobile 		= rs2.getString("InstMobile"); 
						 String instAuth 		= rs2.getString("InstAuth"); 
						 String instNZHHA 		= rs2.getString("InstNZHHA"); 
					
						
						 String sb =" CLIENT:\t" + customerName + "\n\n" + 
								 	" SITE:\t" + streetAddress + "\n" +
								 	"\t" + suburb + "\n\n" + 
								 	" POSTAL:\t" + customerAddress + "\n" +
								 	"\t" + customerSuburb + "\n" + 
								 	"\t" + customerPostCode + "\n\n" +
								 	" PHONE:\t" + customerPhone + "\n" + 
								 	" MOBILE:\t" + customerMobile + "\n\n" +
								 	" EMAIL:\t" + customerEmail+ "\n\n" +
									" Consent:\t" + consentDate;

						 detailsTxtArea.setText(sb);
						 
						 pSDate.setValue(installDate);
						 
						 if (installDate.before(consentDate)){
						     
							 	JOptionPane.showMessageDialog(null, "Warning, Install Date is Before Consent Date");	
						 }
						 


					 
		        	 }
		        	conn.close();	
		        }
		        catch(Exception ex)
		        { 
		        JOptionPane.showMessageDialog(null, ex.toString());
		        }	  	
	 	}	
			
	}
