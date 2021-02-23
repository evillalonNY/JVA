/**
 * GUIRemoteVid.java
 *
 * Created on March 17 2007, 10:03 PM
 * Client-side GUI that handles the connections 
 * and some operations to the Server database. 
 * Description: it creates JList, a button and a JText 
 * The main JList contains the PK or URL of all videos 
 * in the database. Selecting one of the videos 
 * displays video metadata stored in database
 *
 * @author  Elena  Villalon
 */
package jremote;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jmckoi.ListVideos;


public class GUIRemoteVid extends JPanel{
	 
	JList videoLst; 
	private ListVideos lstVid;
	List<String> selectedVid; //selected videos
	final JTextArea outputarea;
	int[] ixselect;//selected indeces of videos
	final static int ROWCNT = 7;
	final protected JCheckBox simVideo; 
	final JTextField videotxt;
	private static Logger mLog = 
        Logger.getLogger(GUIRemoteVid.class.getName());
    private static boolean debug = false;
	public List<String> getSelectedVid(){
		return selectedVid; 
	}
	
	public GUIRemoteVid(List<String> sVid, final JTextArea outputarea){
		if(!debug)
			mLog.setLevel(Level.WARNING);
		selectedVid = sVid; 
		
		simVideo = new JCheckBox("Similar Categories ", false);
		simVideo.setToolTipText("Get videos under same tags/labels.");
		simVideo.setMnemonic(KeyEvent.VK_S);
		
		this.outputarea = outputarea; 
	this.setLayout(new BorderLayout(2,1)); 	
	JPanel center = new JPanel();
	center.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Database Videos"),
            BorderFactory.createEmptyBorder(0,0,0,0)));
	videotxt = new JTextField("", 20);
	videotxt.setFont(new Font("", Font.ITALIC,12)); 
	selectedVid = new ArrayList<String>(); 	
	lstVid = new ListVideos("TbVideoColt");
	List<String> videos=  readFile("videos.txt");
	final int ln = videos.size(); 
	ixselect = new int[ln];
	for(int kk=0; kk < ln; ++kk)
		ixselect[kk] = -1; 
	final String [] videoNames= videos.toArray(new String[videos.size()]);
	videoLst= new JList(videoNames);
	videoLst.setVisibleRowCount(ROWCNT); 
	videoLst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	videoLst.addListSelectionListener(new ListSelectionListener() {
	      public void valueChanged(ListSelectionEvent evt) {
	        if (!evt.getValueIsAdjusting())
	        	return; 
	        Object obj = videoLst.getSelectedValue();
	        String objstr = (String) obj; 
	        objstr.trim(); 
	        videotxt.setText((String) videoLst.getSelectedValue());
	       
	        StringBuffer vidbuf = new StringBuffer((String) obj);
	        
	        Iterator<String> itr = selectedVid.iterator(); 
	        while(itr.hasNext()){
	        	String str = itr.next();
	        	str.trim(); 
	        	if (str.contentEquals(vidbuf)){
	        	itr.remove(); 
	        	}	
	        }
	        selectedVid.add(0, objstr); 
	       if(debug){
	        for(String v: selectedVid)
		     mLog.info(v); 
	       }
	       
	        }
	    });
	
	center.add(new JScrollPane(videoLst));
	Box buts = Box.createVerticalBox();
	
		add(center, BorderLayout.CENTER); 
	Box  vidtotest = Box.createHorizontalBox(); 
	vidtotest.setAlignmentX(Component.LEFT_ALIGNMENT);
	vidtotest.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Describe Video"),
            BorderFactory.createEmptyBorder(0,0,0,0)));
	
	videotxt.setMaximumSize(videotxt.getPreferredSize()); 
	JLabel lbl =new JLabel(" Selected: ");
	lbl.setFont(new Font("", Font.BOLD, 13)); 
	vidtotest.add(lbl);
	
	vidtotest.add(videotxt);
	vidtotest.add(new JLabel("   "));
	vidtotest.add(simVideo);
	this.add(vidtotest, BorderLayout.SOUTH); 		
	}
	public static List<String> readFile(String ff){
		
		BufferedReader inputStream = null;
	    List<String> tags = new ArrayList<String>();
	    String l;
	    String tag="";
	    
		try {
	        inputStream = 
	            new BufferedReader(new FileReader(ff));
	      
	        while ((l = inputStream.readLine()) != null) {
	        String [] res=	l.split("[\t|\n|\f|\r]+");
	      
	        tag= new String(res[0].trim());
	        tags.add(tag); 
	        
	            
	        }
		}catch(FileNotFoundException ef){
			ef.printStackTrace();
		}catch(IOException eio){
			eio.printStackTrace();
	    } finally {
	    	if (inputStream != null) 
	        	try{
	        		
	            inputStream.close();
	        	}catch(IOException cio){
	        		cio.printStackTrace(); 
	        	}
	        }
	     return tags;    
	    }
	 static void prUsage(String mess) {
	    	String mess2 =  "Wrong number of frames in MedianTest";
	    	JOptionPane.showMessageDialog(null, mess, "Median Failed",
					JOptionPane.ERROR_MESSAGE);
		mLog.warning(mess);
	    }
	public static void main(String [] args){
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	JFrame frm = new JFrame();
	    		GUIRemoteVid vid = new GUIRemoteVid(new ArrayList<String>(), null);
	    		frm.add(vid);
	          frm.setSize(500, 330); 
	          frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  	      frm.setVisible(true); 
	  	      
	        }
	    });
		
	}
}
