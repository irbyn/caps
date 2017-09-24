package Schedule;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import Schedule.TimeTablePanel;

public class SchedulePane extends JPanel
{

	public SchedulePane() {	
		
		//Adding Jpanels to the SAles panel area 
        JTabbedPane scheduleP = new JTabbedPane();
        scheduleP.setPreferredSize(new Dimension(1070, 570));
        scheduleP.addTab("Install Schedule", new TimeTablePanel());
        scheduleP.addTab("Site Checks", new ViewSiteChecksPanel());
        
        add(scheduleP); 
	}
}
