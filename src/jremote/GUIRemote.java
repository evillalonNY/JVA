/**
 * GUIRemote.java
 *
 * Created on January 19, 2007, 10:03 PM
 * Client-side part of the GUI for connecting to database
 * Description: it creates the main GUI that connects 
 * to database.  It has the internal class  
 * LeftPanel.java that fill the upper part 
 * of the screen and the bottom has the text area for display.
 *
 * @author  Elena  Villalon
 */
package jremote;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;


public class GUIRemote extends JFrame{
	
	static final long serialVersionUID = 7788L;
	private static final int DEFAULT_WIDTH= 500; 
	private static final int DEFAULT_HEIGTH= 550;
	private int scale = 3;
	
	private final String [] vidf = {"http://www.jakesjokes.com/gallery/albums/userpics/10001/yellowcard.mpg", "file://bailey.mpg"}; 
	private final static String strMess = "Select operations & submit your request to Server\n"; 
	final JTextArea outputarea= new JTextArea(strMess, 100,20); 
	private final JComboBox tags = new JComboBox();
	final static int fsz =14; 
	final static int NTHREADS = 6;
	String [] nameMenu; 
	final JTextField videotxt; 
	//videos selected for classification; index 0 is the video to test
	List<String> vidSelected; 
	final String[] labels = {"people", "text", "indoor", "outdoor",
	  "sport", "food", "fantasy", "transport"};  
	JCheckBox  metavid;
	JCheckBox  simvid;
	JList videoLst;
	List<Commands> commandsLst; 
	static String host="localhost";
	static int port = 1099; 
	private static Logger mLog = 
        Logger.getLogger(GUIRemote.class.getName());
    private static boolean debug = false;
public GUIRemote(String h, int p){
	if(!debug)
		mLog.setLevel(Level.WARNING);
	host =h;
	port = p; 
	setTitle("Remote Videos");
	setSize(DEFAULT_WIDTH,DEFAULT_HEIGTH); 
	setLayout(new BorderLayout(1,2)); 
	vidSelected = new ArrayList<String>();
	commandsLst = new ArrayList<Commands>();
	Font f = new Font("menuF", Font.BOLD, fsz-1); 
	videotxt= new JTextField(vidf[0], 200);
	LeftPanel left = new LeftPanel(); 
	add(left.textpanel, BorderLayout.NORTH);
	GUIRemoteVid vidlst = new GUIRemoteVid(vidSelected,outputarea);
	simvid =vidlst.simVideo; 
	videoLst = vidlst.videoLst;
	vidSelected =vidlst.getSelectedVid(); 
	add(vidlst,BorderLayout.CENTER); 
	outputarea.setLineWrap(true); 
	outputarea.setWrapStyleWord(true);
	Font font3 = new Font("Courier", Font.PLAIN,fsz);
	outputarea.setFont(font3);
	outputarea.setToolTipText("Send your message to a file; write in the text area select it "); 

	 outputarea.addCaretListener(new CaretListener() {
		      public void caretUpdate(CaretEvent caretEvent) {
			        System.out.println(outputarea.getSelectedText()); 
			        if(outputarea.getSelectedText()!=null)
			      new jclient.SendYourComments("emailFile.txt", outputarea);  
		      }
			       
	 });
	JScrollPane scroller= new JScrollPane(outputarea);
	scroller.setPreferredSize(new Dimension(DEFAULT_WIDTH-10, DEFAULT_HEIGTH/scale));
	JPanel panel = new JPanel();
    panel.add(scroller);
   add(panel, BorderLayout.SOUTH); 
}

/**
 * Internal class that creates a panel for video classification
 * 
 */

	
	public class LeftPanel extends JPanel {
	
    JPanel textpanel; 
    JCheckBox vidadd;
    JCheckBox getTag;
    JCheckBox statServer;
    
    boolean stat = false; 
	public LeftPanel(){
		
	textpanel  = new JPanel() {
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }
        public Dimension getPreferredSize() {
            return new Dimension(300, 
                                 super.getPreferredSize().height);
        }
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
    };
    textpanel.setLayout(new GridLayout(1,2)); 
    //create a sub-panel to add to text panel
    final JPanel textpanel1 = new JPanel(); 
    textpanel1.setLayout(new GridLayout(3,1)); 
	textpanel1.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Server & Video URL & Database Statistics"),
            BorderFactory.createEmptyBorder(5,5,5,5))); 

	textpanel1.setAlignmentX(Component.LEFT_ALIGNMENT);
	
    final JPanel server = new JPanel();
	final JTextField urltxt = new JTextField("localhost", 15);
	final JTextField porttxt = new JTextField("1099", 8);
	urltxt.setEditable(false);
	porttxt.setEditable(false);
	urltxt.setAlignmentX(JTextField.LEFT);
	urltxt.setToolTipText("Remote Database.");
	porttxt.setToolTipText("Remote Database."); 
	
	urltxt.setMaximumSize(urltxt.getPreferredSize()); 
	JButton submit = new JButton("Server"); 
	submit.setMnemonic(KeyEvent.VK_S);
	submit.setToolTipText("Connet Database.");
	submit.addActionListener(
		new ActionListener(){
			public void actionPerformed(ActionEvent event){
		//connecting to the Server: host and port		
		   	String host = urltxt.getText().trim();
		   	if(host.equalsIgnoreCase("")|| host==null)
		   		host = "localhost"; //default
		   	String portstr = porttxt.getText().trim();
		   	int port = 1099; //default
		   	if(!portstr.equalsIgnoreCase("")|| portstr!=null)
		   	 port = Integer.parseInt(portstr);
			 	
		//add video to server database
			String url =null;
			boolean addb = vidadd.isSelected();
			if(addb)
				url = videotxt.getText().trim();
			if(url == null || url.equalsIgnoreCase("")) addb = false;
			if(addb) commandsLst.add(Commands.ADDVIDEO); 
		//retreive videos under label
			boolean  tagb = getTag.isSelected();
			String strselect =null;
			if(tagb)
				strselect = ((String) tags.getSelectedItem()).trim(); 
			if(tagb) commandsLst.add(Commands.SHOWLABEL); 
		//stat the server database
			boolean statb = statServer.isSelected();
			if(statb)commandsLst.add(Commands.STATDATABASE); 
			
		//get similar under label categories for video in JList
			String urld =null;
			boolean simb = simvid.isSelected();
			if(simb)
				urld =((String) videoLst.getSelectedValue()).trim();
			
			if(urld==null) {
				simb = false;
	
			}
			
			if(simb)commandsLst.add(Commands.SIMILARVIDEO); 
		//submit request
			ClientVideo client =null;
			try{
			client= new ClientVideo(host, port, commandsLst, url, urld,strselect);
			StringBuffer responseBuf = client.buildOutput;
			String resp = responseBuf.toString() + strMess;
			outputarea.setText(null); 
			outputarea.append(resp);
		
		        } catch (Exception ae) {
		        	String mess="Exception occurred communicating with Videos";
		            mLog.severe(mess);
		            JOptionPane.showMessageDialog(null, mess, "Error Message",
		            		JOptionPane.INFORMATION_MESSAGE); 
		            ae.printStackTrace();
		        }
		        finally{
		        	try{
		        	 client.vidRemote.getSocket().close();
		        }catch(IOException ioe){
		        	ioe.printStackTrace(System.out); 
			}
		        }
		       
		    }
			
		
		});
	server.add(urltxt);
	server.add(new JLabel("  Port: "));
	server.add(porttxt); 
	addRow("Connect : ", server, submit, textpanel1);
	
	
	videotxt.setMaximumSize(urltxt.getPreferredSize()); 
	videotxt.setFont(new Font("Courier", Font.PLAIN,12));
	videotxt.setAlignmentX(JTextField.LEFT);
	vidadd = new JCheckBox("Add", false);
	vidadd.setToolTipText("Add video URL to server database.");
	vidadd.setMnemonic(KeyEvent.VK_A);
	    	
	//done with the upper part
    
	JPanel metapanel = new JPanel(); 
	
	metapanel.setLayout(new BoxLayout(metapanel, BoxLayout.X_AXIS));
	metapanel.setAlignmentY(Component.LEFT_ALIGNMENT);
	
	JLabel lab = new JLabel("Database Labels: "); 
    
	for(int n=0; n < labels.length; ++n)
		tags.addItem(labels[n]); 
	tags.setEditable(true); 
	tags.setMaximumSize(tags.getPreferredSize());
	getTag = new JCheckBox("Retrieve Videos", false);
	getTag.setToolTipText("Retrieve videos category.");
	getTag.setMnemonic(KeyEvent.VK_R);
	statServer = new JCheckBox("Stat Database", false);
	statServer.setToolTipText("Statistics of Database.");
	statServer.setMnemonic(KeyEvent.VK_S);
	metapanel.add(lab); 
	metapanel.add(tags); 
	metapanel.add(getTag); 
	metapanel.add(statServer);
	textpanel1.add(metapanel);
	textpanel.add(textpanel1); 
	
}
	}
	
/** Takes a file name and reads line by line
 * Each line has the label and all videos 
 * classified under the label; the separation is tab
 * Creates a HashMap with labels and videos. 
 */

public void addRow(String label, final Component field, 
		final JComponent but, JPanel textpanel){
	Box h1= Box.createHorizontalBox();;
	
	if(label.startsWith("V")){
		
	h1.add(Box.createRigidArea(new Dimension(0,15))); 
	
	}
	textpanel.add(h1);
	String filler = "  "; 
	h1.add(new JLabel(label+ filler));
	if(field!=null)
    h1.add(field);
 
    if(but!=null)
    h1.add(but);
    else
    h1.add(new Label("Compare to"));
	textpanel.add(h1);
	
}
public Box ButtonRow(String label1, final JButton but1,String label2, 
		final JButton but2, final JPanel textpanel, int ord){
	
	Box h1 =Box.createVerticalBox();
	final int sep = 10; 
	
	String filler = "  "; 
	h1.add(new JLabel(label1));
    h1.add(but1);
    h1.add(Box.createVerticalStrut(sep));
   // h1.add(Box.createGlue());
    h1.add(new JLabel(label2));
    h1.add(but2);
  
	return(h1);
   
	   
	   
   

}

public static void main(String[] args) {
	// TODO Auto-generated method stub
	
	if(args.length> 1){
		host = args[0].trim();
		port = Integer.parseInt(args[1].trim()); 
		
	}
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          GUIRemote client =   new GUIRemote(host, port);
         client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  	      client.setVisible(true); 
        }
    });
  

  
}
}
