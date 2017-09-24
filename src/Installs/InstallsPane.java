package Installs;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class InstallsPane extends JPanel
{
	public InstallsPane()
	{
		//Adding Jpanels to the SAles panel area 
		JTabbedPane installsP = new JTabbedPane();
		installsP.setPreferredSize(new Dimension(1080, 570));
		installsP.addTab("Load Documents", new LoadDocsPanel());
		installsP.addTab("Check for Orders", new CheckForOrdersPanel());
		installsP.addTab("Place Orders", new PlaceOrdPanel());
		installsP.addTab("Recieve Orders", new RecvOrderPanel());
		installsP.addTab("Bookings", new BookingsPanel());

		add(installsP);   
	}
}