/**
 * File		:	GUIDatabase.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Opens database connection and gets the metadata
 *              and the ResultSet of querying the tables 
 *              Display the table's names and labels data in a JFrame
 *              The tables names are in a combo-box. After selecting 
 *              one of the tables names, the JFrame display in the 
 *              center panel the results of the query in ListVideos.java 
 *              The menu bar has menu components: Play (to play the video);
 *              Histogram for a frame (enter a number in text-field) 
 *              or the mean of all frames; data-points for every color 
 *              value of each of RGB for the entire video 
 *              (median and two quartiles distributions); 
 *              summary display in texArea of a JFrame 
 *              the mean, median, max, min and quartiles 
 *              for every color value of each of RGB. 
 *              KeyFrames seeks statistical frames of video.
 *              Fingerprints for the RGB matrices.  
 *              
 * Uses: MenuHandler and MenuItemHandler internal classes, 
 *       ResultSetTableModel.java and DatabaseRunnable.java                
 *              
 *   
 *  Uses: jmckoi.ListVideos, jmckoi.TablesVideo, 
 *        ResultSetTableModel.java                   
 */
package jclient;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.rowset.CachedRowSet;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import jmckoi.ListVideos;
import jmckoi.TablesVideo;

import com.sun.rowset.CachedRowSetImpl;

public class GUIDatabase extends JFrame{
	static final long serialVersionUID = 42L;
	 private static final int DEFAULT_WIDTH= 600;
	 private static final int DEFAULT_HEIGTH= 400;
	 private static JScrollPane scrollPane;
	 private static JComboBox tablesNames;
	 private boolean scrolling=true;
	 private ResultSetTableModel model; 
	 private TablesVideo tbv; 
	 private JMenu histo; 
	 private JMenu video;
	 private JMenu graph; 
	 private JTable table; 
	 private final JTextField frameNo = new JTextField(8);
	 private static final int NTHREADS = 10;
	 private static Executor exec;
	 private static Logger mLog = 
		    Logger.getLogger(GUIDatabase.class.getName());
		private boolean debug = false;
    public Executor getExecutor(){
    	return exec;
    }
	 
	  public GUIDatabase(){
		  if(!debug)
			  mLog.setLevel(Level.WARNING);
		  setTitle("Video DataBase");
		  setSize(DEFAULT_WIDTH, DEFAULT_HEIGTH);
		  
		  tbv = new TablesVideo(false);
		  tablesNames = tbv.videoStore(new JComboBox()); 
		  this.scrolling = tbv.getScrolling(); 
		   frameNo.setText("-1"); 
		 
		   exec = Executors.newFixedThreadPool(NTHREADS);
		  tablesNames.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				try{
					Connection con = tbv.getCon(); 
				if(scrollPane != null) remove(scrollPane); 
				String tbName = (String) tablesNames.getSelectedItem();
		
				ListVideos lvid = new ListVideos(tbName, con);
				lvid.videoStore(); 
				ResultSet rs = lvid.getRs(); 
				
				if(scrolling)
				model = new ResultSetTableModel(rs); 
			    
				else{
					
					CachedRowSet crs = new CachedRowSetImpl(); 
					
				 
					crs.populate(rs);
					model = new ResultSetTableModel(rs);  
				}
				String ff = (String) model.getValueAt(0, 0);
				mLog.info(ff); 
			    table = new JTable(model);
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			 
				scrollPane = new JScrollPane(table);
				add(scrollPane, BorderLayout.CENTER);
				validate(); 
			}
				catch(SQLException e){
					e.printStackTrace();
				}
				catch(Exception err){
					err.printStackTrace();
				}
			}
		});
		
		JPanel p = new JPanel();

		p.add(tablesNames);
		p.add(new JLabel("   Frame Number:"));
		p.add(frameNo); 
		add(p, BorderLayout.NORTH); 
		//add a menu bar to display videos or graphics
      JMenuBar menubar = new JMenuBar();
      setJMenuBar(menubar);
      MenuHandler mhandler = new  MenuHandler(); 
      JMenu video = new JMenu("Play");
      video.setMnemonic(KeyEvent.VK_P); 
      video.addMenuListener(mhandler); 
		menubar.add(video);
		JMenu histo = new JMenu("Histogram");
		histo.setMnemonic(KeyEvent.VK_H);
		histo.addMenuListener(mhandler); 		
	   menubar.add(histo); 
	   JMenu graph = new JMenu("DataPoints");
		graph.setMnemonic(KeyEvent.VK_D);
		graph.addMenuListener(mhandler); 
	   menubar.add(graph); 
	   JMenu statdisp = new JMenu("Summary");
		statdisp.setMnemonic(KeyEvent.VK_M);
		MenuItemHandler handmenu = new MenuItemHandler(); 
		JMenuItem metaItem = new JMenuItem("Metadata");
		metaItem.addActionListener(handmenu);
		JMenuItem redItem = new JMenuItem("Red");
		redItem.addActionListener(handmenu);
		JMenuItem greenItem = new JMenuItem("Green");
		greenItem.addActionListener(handmenu);
		JMenuItem blueItem = new JMenuItem("Blue");
		blueItem.addActionListener(handmenu);
		statdisp.add(metaItem);
		statdisp.add(redItem);
		statdisp.add(greenItem);
		statdisp.add(blueItem); 
		statdisp.addMenuListener(mhandler); 
	    menubar.add(statdisp); 
	    JMenu keyfrm = new JMenu("KeyFrms");
	    statdisp.setMnemonic(KeyEvent.VK_Y);
	    keyfrm.addMenuListener(mhandler); 
	    menubar.add(keyfrm);
	    JMenu finger = new JMenu("FingerPrint");
	    finger.setMnemonic(KeyEvent.VK_F); 
	    finger.addMenuListener(mhandler); 
	    menubar.add(finger);
     //  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   setVisible(true);
	  }
/**Listeners for menu bar components Histogram, DataPoints
 * Play and KeyFrms.  When they are selected calls 
 * corresponding Thread in DatabaseRunnable.java
 */  
	  private class MenuHandler implements MenuListener{
		  
		  
	    public MenuHandler(){
	    	super();
	    	
	    }
		public void menuSelected(MenuEvent event){
	 
         String src = ((JMenu) event.getSource()).getText();
         mLog.info(src);
         
         int rw = table.getSelectedRow(); 
         String url = ((String) model.getValueAt(rw, 0)).trim();
         mLog.info(url); 
         String text = frameNo.getText();
         int frmno = Integer.parseInt(text);
         mLog.info("" + frmno);
         
         if(src.equals("Play")){
        	Runnable r = new DatabaseRunnable.VideoRunnable(url);
        	exec.execute(r); 
         
         }else if(src.equals("Histogram")){
        	 Runnable histeria = new DatabaseRunnable.HistoRunnable(url, frmno);
        	 exec.execute(histeria); 
        	
         
         }else if (src.equals("DataPoints")){
        	  Runnable gr = new DatabaseRunnable.GraphRunnable(url); 
        	  exec.execute(gr); 
        	
         }else if(src.equals("StatDisp")){
        	 
         }else if(src.equals("KeyFrms")){
        	 
        	 Runnable kf = new DatabaseRunnable.SeekKeyFrmsRunnable(url, frmno); 
        	 exec.execute(kf);   
       	  /**instead of
       	   * Thread t = new Thread(kf);
       	   * t.start(); 
       	   */  
         } else if(src.equals("FingerPrint")){
        	 System.out.println("Fingerprints");
        	 Runnable fg = new DatabaseRunnable.FingerPrintRunnable(url); 
        	 exec.execute(fg);
         }   
         		
		}
		public void menuCanceled(MenuEvent e){
			mLog.info("Menu: I am canceled..."); 
			Object src = e.getSource();
	         if(src == video)
	         mLog.info("Play me...");
	         else if(src==histo)
	         mLog.info("See histo..."); 
	         else if (src==graph)
	        	  mLog.info("See graph...");  
		}
		
		public void menuDeselected(MenuEvent e){
		    mLog.info("I am deselected..."); 
			Object src = e.getSource();
	         if(src == video)
	         mLog.info("Play me");
	         else if(src==histo)
	         mLog.info("See histo"); 
	         else if (src==graph)
	        	  mLog.info("See graph");  
		}
	  }
		/**
		 * @Menu item listeners for components Red, Green, Blue
		 * it starts the threads in TextRunnable static class
		 * of DatabaseRunnable. 
		 */
	  private class MenuItemHandler implements ActionListener{
		 
		  public MenuItemHandler(){
			  super();
			  
		  }
		  public void actionPerformed(ActionEvent event)
		  {
			  String src = ((JMenuItem) event.getSource()).getText(); 
			  int rw = table.getSelectedRow(); 
		      String url = ((String) model.getValueAt(rw, 0)).trim();
		      if(src.equals("Red")){
				  Runnable textred = new DatabaseRunnable.TextRunnable(url, 'R');
		        	exec.execute(textred);  
				
			  }
			  if(src.equals("Green")){
				  Runnable textgreen = new DatabaseRunnable.TextRunnable(url, 'G');
				  exec.execute(textgreen); 
		        	
			  }
			  if(src.equals("Blue")){
				  Runnable textblue = new DatabaseRunnable.TextRunnable(url, 'B');
				  exec.execute(textblue);
		      
			  }
			  if(src.equals("Metadata")){
				  Runnable textmeta = new DatabaseRunnable.TextRunnable(url, 'D');
				  exec.execute(textmeta);
		        
			  }
	  }
	  }
		public static void main(String[] args) {
			// TODO Auto-generated method stub
			exec = Executors.newFixedThreadPool(NTHREADS);
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	              GUIDatabase client =   new GUIDatabase();
	            //  client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      	      client.setVisible(true); 
	            }
	        });
	      
	     
	     
	      
	}
}



