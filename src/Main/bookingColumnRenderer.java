package Main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class bookingColumnRenderer extends DefaultTableCellRenderer
	    {		         
	
	private Color DkBlue = Color.decode("#174082");
	private Color LtGray = Color.decode("#eeeeee");	
	private Color inst1 = Color.decode("#eeffd6");
	private Color inst2 = Color.decode("#fff1e2");
	private Color inst3 = Color.decode("#e2fff5");
	private Color inst4 = Color.decode("#ffe2f8");
	private Color[] instColors = {inst1, inst2, inst3, inst4};
	
	       public bookingColumnRenderer() {
	          super();
	       }
	       public Component getTableCellRendererComponent
	            (JTable table, Object value, boolean isSelected,
	             boolean hasFocus, int row, int column)
	       {
	          Component cell = super.getTableCellRendererComponent
	             (table, value, isSelected, hasFocus, row, column);
	          if (column == 0 || column == 1 ){	
	        	  cell.setBackground( instColors[(row/2)%4]);  
	        	  cell.setForeground( DkBlue );		          
	        	  cell.setFont(cell.getFont().deriveFont(Font.BOLD));	
	        	  setHorizontalAlignment(SwingConstants.CENTER);
	        	  return cell;
	          }else if (column == 2 || column == 8 ){
	        	  cell.setBackground( LtGray);  
	        	  cell.setForeground( DkBlue );
	        	  cell.setFont(cell.getFont().deriveFont(12, Font.BOLD));	
	        	  return cell;
	          }else {  	  
	        	  String contents = (String)value;
	        	  ((JComponent) cell).setToolTipText(contents);		    	  		    	  
	        	  cell.setBackground( instColors[(row/2)%4]);
	        	  return cell;
	          }
	       }
	    }

