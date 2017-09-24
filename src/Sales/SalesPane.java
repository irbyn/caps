package Sales;


import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class SalesPane extends JPanel
{
        public SalesPane()
        {
        		//Adding Jpanels to the SAles panel area 
                JTabbedPane salesP = new JTabbedPane();
                salesP.setPreferredSize(new Dimension(1080, 570));
                salesP.addTab("Customer", new CustomerPanel());
                salesP.addTab("Estimation", new EstimationPanel());
                salesP.addTab("Site Checks", new SiteCheckPanel());
                salesP.addTab("Quotes", new QuotePanel());
                salesP.addTab("Follow ups", new FollowUpPanel());
                
                add(salesP);   
        }
}