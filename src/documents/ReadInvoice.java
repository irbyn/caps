package documents;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.table.DefaultTableModel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/*
 * Reads selected file, strips away unwanted Text and fills a JTable
 */
public class ReadInvoice {

/*	private String folder = "//C:/pdfs/Invoice/";	
	private String invPfx = "INV_";
	private String sitePfx = "SC_";
	private String photoPfx = "PH_";*/
	private String text;
	private FileSystem fs;


	public void readInvoice(String invoiceNum, DefaultTableModel mod) {

		fs = new FileSystem();
		
		String src = fs.getInvoice() + invoiceNum + ".pdf";
		//Loading an existing document
		File file = new File(src);
		if (file.exists()){

			PDDocument document;
			try {
				document = PDDocument.load(file);
				//Instantiate PDFTextStripper class
				PDFTextStripper pdfStripper = new PDFTextStripper();

				//Retrieving text from PDF document
				text = pdfStripper.getText(document); 
				document.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			//Find split point on invoice
			String patternString = "=========================================================="
					+ "========================================================================";
			Pattern pattern = Pattern.compile(patternString);	        
			String[] split = pattern.split(text);	        
			String inv = split[3];

			//	Initialise variables to search each line for text
			String foundPermit = "";
			String stockCode = "";
			String stockDesc = "";
			String stockQty = "";
			int chars = inv.length();				//total number of characters
			int maxRows = 25;						// 25 rows
			//        String[] stkLine = new String[maxRows];
			String newLine = "";
			int i=0;
			int lineLength = 130;
			int start = 0;
			int end = lineLength + start;
			//For each line of text
			for (i=0;i<maxRows;i++){        	
				newLine = inv.substring(start, end);		//split off 130 chars as a line
				foundPermit = newLine.substring(2,6);
				if (i == 0){

				}
				if (foundPermit.equals("INST") || foundPermit.equals("PERM")){	
					//Stop if either term is found as the first 4 characters (stock code)
				}
				else {
					// create a row for table
					stockCode = newLine.substring(2,20);
					stockDesc = newLine.substring(23,82);
					stockQty = newLine.substring(96,99);			    
					mod.addRow(new Object[]{stockQty, stockCode, stockDesc});

					//	next newLine starting points
					start = start + lineLength;
					end = end + lineLength;
				}	
			}	
		}
	}
}
