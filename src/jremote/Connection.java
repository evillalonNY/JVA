/**
 * Connection.java
 * 
 * It is a modal frame, which you need to dismiss before any 
 * other part of the program may execute.
 * Contains fields for the remote server Internet address  
 * and the port to establish the connection. 
 * 
 * Also, a text filed to hold the url of a video 
 * that may be added to the remote database. 
 * A JList with the operations currently supported, 
 * which can be divided into two categories,
 * i.e. database and collections.  
 * The remote requests may need to connect to 
 * the database of videos, or access the server 
 * collections that store the videos metadata.   
 * After dismissing the GUI it returns to JClient.java. 
 * 
 * Elena Villalon
 * May 4, 2007
 * 
 */
package jremote;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;


public class Connection extends JDialog{
	String host;
	int port;
	String url;  
	private final JComboBox tags = new JComboBox();
	protected String oper=Commands.DESCRIBEVIDEO.toString();
	final int HEIGTH = 250;
	final int WIDTH = 450; 
	boolean database = false; 
	private final String [] vidf = 
	{"http://www.jakesjokes.com/gallery/albums/funny-videos-5/fstupid_soccer_goal.mpeg", 
	 "http://www.jakesjokes.com/gallery/albums/funny-videos-5/stupid_player.mpeg",
	 "http://www.jakesjokes.com/gallery/albums/funny-videos-5/ferrari_blows_engine.mpeg",
	 "http://www.jakesjokes.com/gallery/albums/funny-videos-5/ferrari_blows_engine.mpeg",
	 "file://hustle1.mpeg"}; 
	
public Connection(JFrame frame, String h, int p){
		super(frame, true); 

	host = h;
	port = p;
	final JTextField hfield = new JTextField(host, 20);
	hfield.setMaximumSize(new Dimension(10, 10)); //getPreferredSize()); 
	final JTextField pfield = new JTextField(""+port, 20);
	pfield.setMaximumSize(pfield.getPreferredSize());
	final JTextField urlfield = new JTextField(vidf[0], 30);
	urlfield.setMaximumSize(urlfield.getPreferredSize()); 
	JLabel hlab = new JLabel("Enter host  ");
	JLabel plab = new JLabel("Enter port  ");
	JLabel urllab = new JLabel("Add url  ");
	JLabel submit = new JLabel("Remote Server:");
	JButton butdata = new JButton("OK");

	
	                   
	this.setLayout(new GridLayout(2, 2));
//	this.setLayout(new BorderLayout());
	Box box= Box.createVerticalBox();
	
	JPanel[] pan = new JPanel[5];
	for(int n=0; n <pan.length; ++n)
	pan[n] = new JPanel(new FlowLayout()); 
	pan[0].add(hlab);
	pan[0].add(hfield);
	pan[1].add(plab);
	pan[1].add(pfield);
	pan[2].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Video Remote"),
            BorderFactory.createEmptyBorder(5,5,5,5))); 
	pan[2].add(urllab);
	pan[2].add(urlfield); 
	box.add(pan[0]);
	box.add(pan[1]);
	Box.createVerticalStrut(10);
    box.add(pan[2]); 
    add(box); 
    Object[] options = Commands.values();
	 String [] opt = new String[options.length+1]; 
	 for(int k=0; k <= options.length; ++k){
		 if(k == options.length) opt[k]= "CANCEL"; 
		 else
			 opt[k] = options[k].toString(); 
		 tags.addItem(opt[k]); 
	 }
   
	tags.setEditable(true); 
	tags.setMaximumSize(tags.getPreferredSize());
	tags.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent event){
			String strselect = (String) tags.getSelectedItem();
			strselect.trim(); 
			oper = new String(strselect);
		}
	});
	JLabel tg = new JLabel("Database Operations");
	pan[3].add(tg);
	pan[3].add(tags); 
	
	Box b1 = Box.createVerticalBox();
	b1.add(pan[3]);
	Box.createVerticalStrut(10);
	butdata.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					  String txt = "";
					  txt = hfield.getText().trim();
			          if(txt != null && !txt.equalsIgnoreCase(""))
			        	  host = txt;
			          txt = pfield.getText().trim();
			          int pp=0; 
			          if(txt != null && !txt.equalsIgnoreCase(""))
			           pp = Integer.parseInt(txt);
			          if(pp != 0)
			        	  port = pp;
			          txt = urlfield.getText().trim();
			          if(txt != null && !txt.equalsIgnoreCase(""))
			        	  url = txt;
			          setVisible(false); 
			          return;
				
				}
			});
	pan[4].add(new JLabel("Submit request"));
	pan[4].add(butdata);
	 
	b1.add(pan[4]);
	add(b1); 
	    pack();
	    setSize(new Dimension(WIDTH, HEIGTH)); 
	    setVisible(true); 
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
}
}
