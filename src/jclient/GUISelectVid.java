/**
 * GUISelectVid.java
 *
 * Created on March 17 2007, 10:03 PM
 * 
 * Description: it creates two JLists, a button and a JText 
 * The left JList contains the PK or URL of videos in database
 * The button copies some of the videos PK's to the right JList 
 * We test a selected video in the JText against 
 * all the others that were copied from the main  
 * Jlist to the left component.  
 * It is part of GUIClient.java 
 *
 * @author  Elena  Villalon
 */
package jclient;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jmckoi.ListVideos;

public class GUISelectVid extends JPanel{
		
	private JList videoLst, selectLst;
	private JButton copyButton; 
	private ListVideos lstVid;
	List<String> selectedVid; //selected videos
	
	int[] ixselect;//selected indeces of videos
	final static int ROWCNT = 8; 
	private static Logger mLog = 
		    Logger.getLogger(GUISelectVid.class.getName());
    private boolean debug = false;
	public List<String> getSelectedVid(){
		return selectedVid; 
	}
	
	public GUISelectVid(List<String> sVid){
		if(!debug)
			mLog.setLevel(Level.WARNING);
		selectedVid = sVid; 
	this.setLayout(new BorderLayout(2,1)); 	
	JPanel center = new JPanel();
	center.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Select Videos from Database"),
            BorderFactory.createEmptyBorder(0,0,0,0)));
	final JTextField videotxt = new JTextField("", 20);
	videotxt.setFont(new Font("", Font.ITALIC,12)); 
	selectedVid = new ArrayList<String>(); 	
	lstVid = new ListVideos("TbVideoColt");
	List<String> videos=  lstVid.videoStore();
	final int ln = videos.size(); 
	ixselect = new int[ln];
	for(int kk=0; kk < ln; ++kk)
		ixselect[kk] = -1; 
	final String [] videoNames= videos.toArray(new String[videos.size()]);
	//names too long for displaying 
	for(int n=0; n < videoNames.length; ++n){
		if(!videoNames[n].contains("http://"))
			continue; 
		String [] webstr =videoNames[n].split("/");
		videoNames[n] = webstr[2]+"/"+webstr[webstr.length-1]; 
		}		
	videoLst= new JList(videoNames);
	videoLst.setVisibleRowCount(ROWCNT); 
	videoLst.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	center.add(new JScrollPane(videoLst));
	Box buts = Box.createVerticalBox();
	
	copyButton = new JButton("Copy >>>");
	buts.add(copyButton); 
	copyButton.setFont(new Font(" ", Font.ITALIC, 12)); 
	copyButton.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent eve){
				selectLst.setListData(videoLst.getSelectedValues());
				ixselect = videoLst.getSelectedIndices(); 
				selectLst.repaint(); 
				selectedVid.clear(); 
				for(int kk=0; kk < selectLst.getModel().getSize(); ++kk)
					
						selectedVid.add((String) selectLst.getModel().getElementAt(kk));
			
			}
	});

	
	center.add(buts);
	final DefaultListModel listModel = new DefaultListModel();
	
	selectLst= new JList(listModel);
	selectLst.setVisibleRowCount(ROWCNT); 
	selectLst.setFixedCellWidth(195);
	selectLst.setFixedCellHeight(18); 
	selectLst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	selectLst.addListSelectionListener(new ListSelectionListener() {
	      public void valueChanged(ListSelectionEvent evt) {
	        if (!evt.getValueIsAdjusting())
	        	return; 
	        Object obj = selectLst.getSelectedValue();
	        String objstr = (String) obj; 
	        objstr.trim(); 
	        videotxt.setText((String) selectLst.getSelectedValue());
	       
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
	
	center.add(new JScrollPane(selectLst));
	add(center, BorderLayout.CENTER); 
	Box  vidtotest = Box.createHorizontalBox(); 
	vidtotest.setAlignmentX(Component.LEFT_ALIGNMENT);
	vidtotest.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Test Video"),
            BorderFactory.createEmptyBorder(0,0,0,0)));
	
	videotxt.setMaximumSize(videotxt.getPreferredSize()); 
	videotxt.setText((String) selectLst.getSelectedValue());
	JLabel lbl =new JLabel(" Selected: ");
	lbl.setFont(new Font("", Font.BOLD, 13)); 
	vidtotest.add(lbl);
	
	vidtotest.add(videotxt);
	this.add(vidtotest, BorderLayout.SOUTH); 		
	}
	public static void main(String [] args){
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	JFrame frm = new JFrame();
	    		GUISelectVid vid = new GUISelectVid(new ArrayList<String>());
	    		frm.add(vid);
	          frm.setSize(500, 330); 
	          frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  	      frm.setVisible(true); 
	  	      
	        }
	    });
		
	}
}
