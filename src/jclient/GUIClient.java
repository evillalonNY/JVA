/**
 * GUIClient.java
 *
 * Created on January 19, 2007, 10:03 PM
 * Description: it creates the main GUI that launches 
 * all the applications.  Display URL's, file choosers
 * brings GUIDatabase and performs the statistics
 * USES GUIDatabase.java, htmlEditor.java.
 * It has two internal classes RightPanel.java, 
 * LeftPanel.java that fill the left and hand 
 * parts of the screen; 
 * the bottom of the screen has a textarea.
 *
 * @author  Elena  Villalon
 */
package jclient;
import jalgo.Student;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;


public class GUIClient extends JFrame{
	
	static final long serialVersionUID = 7788L;
	private static final int DEFAULT_WIDTH= 600; 
	private static final int DEFAULT_HEIGTH= 700;
	private float scale = 4;
	private JPanel statpanel; 
	private final String [] vidf = {"file://elephant.mpg", "file://bailey.mpg"}; 
	final JTextArea outputarea= new JTextArea("Video analysis", 100,20); 
	private final VideoDescribe desc = new VideoDescribe(); 
	private final JComboBox tags = new JComboBox();
	final static int fsz =14; 
	private static Executor exec;
	final static int NTHREADS = 7;
	String [] nameMenu; 
	final JTextField videotxt; 
	//videos selected for classification; index 0 is the video to test
	List<String> vidSelected; 
	private static Logger mLog = 
	    Logger.getLogger(GUIClient.class.getName());
	private boolean debug = false;
public GUIClient(){
	if(!debug)
		mLog.setLevel(Level.WARNING);
	exec = Executors.newFixedThreadPool(NTHREADS);
	setTitle("Video Analyzer");
	setSize(DEFAULT_WIDTH,DEFAULT_HEIGTH); 
	setLayout(new BorderLayout(1,2)); 
	vidSelected = new ArrayList<String>(); 
	Font f = new Font("menuF", Font.BOLD, fsz-1); 
	videotxt= new JTextField(vidf[0], 20);
	JMenuBar menubar = new JMenuBar();
    setJMenuBar(menubar);
    nameMenu = new String[7];
    
	JMenu statdisp = new JMenu("File");
	statdisp.setMnemonic(KeyEvent.VK_F);
	statdisp.setFont(f); 
	MenuItemHandler handmenu = new MenuItemHandler(); 
	nameMenu[0] = "Add Video"; 
	JMenuItem vidItem = new JMenuItem(nameMenu[0], KeyEvent.VK_V);
	vidItem.setToolTipText("Enter video from directory into database.");
	vidItem.addActionListener(handmenu);
	vidItem.setFont(f); 
	nameMenu[1] = "Add Label"; 
	JMenuItem labelItem = new JMenuItem(nameMenu[1],  KeyEvent.VK_L);
	labelItem.setToolTipText("Enter classification tags/labels.");
	labelItem.addActionListener(handmenu);
	labelItem.setFont(f); 
	nameMenu[2]= "Test Videos";
	JMenuItem testItem = new JMenuItem(nameMenu[2], KeyEvent.VK_T);
	testItem.setToolTipText("Text tile with video primary keys for classification.");
	testItem.addActionListener(handmenu);
	testItem.setFont(f); 
	statdisp.add(vidItem);
	statdisp.add(labelItem);
	statdisp.add(testItem);
	menubar.add(statdisp); 
	
	JMenu databasedisp = new JMenu("Database");
	databasedisp.setMnemonic(KeyEvent.VK_D); 
	databasedisp.setFont(f); 
	nameMenu[3]= "Analyze/Show"; 
	JMenuItem showItem = new JMenuItem(nameMenu[3],KeyEvent.VK_A);
	showItem.setToolTipText("Show table with database of videos.");
	showItem.addActionListener(handmenu);
	showItem.setFont(f); 
	nameMenu[4] = "Mckoy/SQL Exit"; 
	JMenuItem queryItem = new JMenuItem(nameMenu[4], KeyEvent.VK_M);
	queryItem.addActionListener(handmenu);
	queryItem.setFont(f); 
	queryItem.setToolTipText("Show Mckoy SQL JDBC Query tool.");
	databasedisp.add(showItem);
	databasedisp.add(queryItem);
	databasedisp.addSeparator();
	nameMenu[5] = "URL Remove"; 
	JMenuItem vpkItem = new JMenuItem(nameMenu[5],KeyEvent.VK_U);
	vpkItem.setToolTipText("Eliminate video primary Key (URL) from database.");
	vpkItem.addActionListener(handmenu);
	vpkItem.setFont(f); 
	databasedisp.add(vpkItem); 
	menubar.add(databasedisp); 
	
	JMenu studio = new JMenu("JMF");
	studio.setMnemonic(KeyEvent.VK_J); 
	studio.setFont(f);
	nameMenu[6] = "JMStudio"; 
	JMenuItem studItem = new JMenuItem(nameMenu[6],KeyEvent.VK_S);
	studItem.setToolTipText("Show JMF Studio.");
	studItem.addActionListener(handmenu);
	studItem.setFont(f); 
	studio.add(studItem); 
	menubar.add(studio); 
	
	/////////////////////////////////////////////////////////
	RightPanel rght = new RightPanel();
	JPanel congo = new JPanel();
	congo.setLayout(new BorderLayout(0,0)); 
	congo.add(rght.statpanel, BorderLayout.EAST);
	LeftPanel left = new LeftPanel();
	congo.add(left.textpanel,BorderLayout.WEST ); 

	add(congo, BorderLayout.NORTH);
	GUISelectVid vidlst = new GUISelectVid(vidSelected);
	vidSelected =vidlst.getSelectedVid(); 
	 
	add(vidlst,BorderLayout.CENTER); 
	outputarea.setLineWrap(true); 
	outputarea.setWrapStyleWord(true);
	Font font3 = new Font("Courier", Font.PLAIN,fsz);
	outputarea.setFont(font3);
	outputarea.setToolTipText("Send your message to a file, write in the text area select it "); 

	 outputarea.addCaretListener(new CaretListener() {
		      public void caretUpdate(CaretEvent caretEvent) {
			       mLog.info("dot:"+ caretEvent.getDot());
			       mLog.info("mark"+caretEvent.getMark());
			        mLog.info(outputarea.getSelectedText()); 
			         mLog.info(outputarea.getText()); 
			        if(outputarea.getSelectedText()!=null)
			      new SendYourComments("emailFile.txt", outputarea);  
		      }
			       
	 });
	

	JScrollPane scroller= new JScrollPane(outputarea);
	scroller.setPreferredSize(new Dimension(DEFAULT_WIDTH-10, DEFAULT_HEIGTH/3));
	JPanel panel = new JPanel();
    panel.add(scroller);
   add(panel, BorderLayout.SOUTH); 
}
private class MenuItemHandler implements ActionListener{
	 
	  public MenuItemHandler(){
		  super();
		  
	  }
	  public void actionPerformed(ActionEvent event)
	  {
		  String src = ((JMenuItem) event.getSource()).getText(); 
		  
			   
			 
		  
		  if(src.equals(nameMenu[0])){ //"Add Video"
			  mLog.info(""+vidf[0]); 
				
				String[] which = vidf[0].split("\\.");
				 
				String ending = which[which.length-1].trim(); 
				
				Runnable r = new DatabaseRunnable.VideoRunnable(vidf[0], "false");  
				new Thread(r).start(); 
				
				return; 
			}
	        	
		  if(src.equals(nameMenu[1]) || src.equals(nameMenu[2])){
			  String[] which = vidf[0].split("\\.");
				 
			String ending = which[which.length-1].trim(); 
			  if(!ending.equalsIgnoreCase("txt"))  
				if(!ending.equalsIgnoreCase("text"))
				 if(!ending.equalsIgnoreCase("tex"))
				  return;
			  
				 
		  }
		 	
			  if(src.equals(nameMenu[1])){ //"Add Label"
				  HashMap<String, Set<String>> usr= readFile(vidf[0], true); 
					desc.setUserlabel(usr); 
					Set<String>	tagval= usr.keySet();
					if(tagval.isEmpty())
						return;
						
					for(String str:tagval)
				       tags.addItem(str); 
					
					return; 
				}
			  
		  if(src.equals(nameMenu[2])){//"Test Videos"
			  HashMap<String, Set<String>> usr= readFile(vidf[0], false);
			  String key = "NOTAG"; 
			  Set<String> vid = usr.keySet(); 
			  
			  if(vid.isEmpty()){
				  mLog.info("EMPTY");
			  
				  return;
			  }
			  String [] argsf = vid.toArray(new String[vid.size()]);
			 
		  
				Runnable r = new DatabaseRunnable.StudentRunnable(argsf, outputarea);
		        exec.execute(r);  
			  
	       
		  }
		  if(src.equals(nameMenu[3])){ //"Show/Analyze";
			  Runnable r = new DatabaseRunnable.GUIDatabaseRunnable();
	        	exec.execute(r);  
		  }
		  if(src.equals(nameMenu[4])){//"Mckoy/SQL Exit";
			  Runnable r = new DatabaseRunnable.MckoiQueryRunnable();
			  exec.execute(r);
			  
		  }
		  if(src.equals(nameMenu[5])){//"URL Remove"
			  String []vdrm = {(videotxt.getText()).trim()};
			  Runnable r = new DatabaseRunnable.DropVideoRunnable(vdrm[0]);
			  exec.execute(r);
				
		  }
		  if(src.equals(nameMenu[6])){//"JMStudio"
			  
			  Runnable r = new DatabaseRunnable.JMStudioRunnable();
			  exec.execute(r);
				
		  }
}
}


/**
 * Internal class that creates a panel for video classification
 * 
 */
public class RightPanel extends JPanel{
	JPanel statpanel;
	private JPanel textpanel; 
	
	
	public RightPanel(){
	
	textpanel = new JPanel(); 
	statpanel  = new JPanel() {
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
    desc.classifiedVideos();

    statpanel.setLayout(new GridLayout(1, 1)); 
	statpanel. setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Video Classifier"),
            BorderFactory.createEmptyBorder(0,0,0,0)));
	
	textpanel.setLayout(new BoxLayout(textpanel, BoxLayout.Y_AXIS));
	textpanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	final Box buttonpanel = Box.createVerticalBox(); 
	final String [] nmbut = {"Student", "Rank", "SVD", "Anova"};  
	final ButtonGroup group = new ButtonGroup();
	
	JRadioButton butmean = new JRadioButton("Paired Student Test",false);
	mLog.info("Student Test Button " + butmean.isSelected()); 
	butmean.setActionCommand(nmbut[0]);
	group.add(butmean);
	buttonpanel.add(butmean);
	JRadioButton butmedian = new JRadioButton("Signed Rank Test");
	butmedian.setSelected(false); 
	butmedian.setActionCommand(nmbut[1]); 
	group.add(butmedian);
	buttonpanel.add(butmedian);
	JRadioButton butAnova = new JRadioButton("ANOVA",false);
	butAnova.setActionCommand(nmbut[3]);
	group.add(butAnova);
	buttonpanel.add(butAnova);
	JRadioButton butSVD = new JRadioButton("SVD & PCA",false);
	butSVD.setActionCommand(nmbut[2]);
	group.add(butSVD);
	buttonpanel.add(butSVD);
	RadioButtonHandler buts = new RadioButtonHandler(nmbut);
	butmean.addActionListener(buts); 
	butmedian.addActionListener(buts);
	butSVD.addActionListener(buts); 
	butAnova.addActionListener(buts); 
	statpanel.add(buttonpanel); 
	
	
	
	
	statpanel.add(textpanel);

	

}
}

private class RadioButtonHandler implements ActionListener{
	 String [] nmbut; 
	
	 public RadioButtonHandler(String[] b){
		  super();
		  nmbut=b; 
		 
	  }
	  public void actionPerformed(ActionEvent event)
	  {
		  String but = event.getActionCommand();
		  if(vidSelected.size()<= 0){
				Student.prUsage("No videos selected");
			return;
			}
			String url[] = vidSelected.toArray(new String[vidSelected.size()]); 
		   for(int n=0; n < url.length; ++n){
		  mLog.info(url[n]); 
		    url[n] = "'"+url[n]+"'"; 
		    mLog.info(url[n]); 
		   }
		 mLog.info(but);
			if(but.equalsIgnoreCase(nmbut[0])){
			
				Runnable r = new DatabaseRunnable.StudentRunnable(url, outputarea);
      	exec.execute(r);
      	
      	return; 
			}
			if(but.equalsIgnoreCase(nmbut[1])){
			
			mLog.info(nmbut[1]); 
				Runnable r = new DatabaseRunnable.SumRankTestRunnable(url, outputarea);
      	exec.execute(r); 
      
      	return; 
			}
			if(but.equalsIgnoreCase(nmbut[2])){
				
			mLog.info(nmbut[2]); 
			
				Runnable r = new DatabaseRunnable.SVDVideoTestRunnable(url, outputarea);
				Thread t = new Thread(r);
				t.start(); 
			
				
				return; 
			}
			if(but.equalsIgnoreCase(nmbut[3])){
				
				mLog.info(nmbut[3]); 
				
					Runnable r = new DatabaseRunnable.ANOVATestRunnable(url, outputarea);
					Thread t = new Thread(r);
					t.start(); 
				
					
					return; 
				}
		}
	
	  }
	 


public void addRadioButton(String name, boolean selected, 
		ButtonGroup group, JPanel panel){

	JRadioButton button = new JRadioButton(name,selected);
	group.add(button);
    panel.add(button);	
}
/**
 * Internal class that creates a panel for url display and 
 * the video database. 
 * 
 */
public class LeftPanel extends JPanel {
	
    JPanel textpanel; 
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
    textpanel.setLayout(new BoxLayout(textpanel, BoxLayout.PAGE_AXIS)); 
    //create a sub-panel to add to text-panel
    final JPanel textpanel1 = new JPanel(); 
	textpanel1. setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("URL's and Files"),
            BorderFactory.createEmptyBorder(5,5,5,5))); 
	textpanel1.setLayout(new BoxLayout(textpanel1, BoxLayout.Y_AXIS));
	textpanel1.setAlignmentX(Component.LEFT_ALIGNMENT);

	final JTextField urltxt = new JTextField("http://www.youtube.com", 15);
	urltxt.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					
				new htmlEditor(urltxt.getText());	
				}
			});
	
	urltxt.setMaximumSize(urltxt.getPreferredSize()); 
	JButton submit = new JButton("Submit"); 
	submit.setMnemonic(KeyEvent.VK_S);
	submit.setToolTipText("Display website.");
	submit.addActionListener(
		new ActionListener(){
			public void actionPerformed(ActionEvent event){
				
			new htmlEditor(urltxt.getText());	
			}
		});
	addRow("URL:", urltxt, submit, textpanel1);
	
	
	videotxt.setMaximumSize(urltxt.getPreferredSize()); 
	JButton browse = new JButton("Browse");
	browse.setToolTipText("Select file or video from directories.");
	browse.setMnemonic(KeyEvent.VK_B);
	addRow("File:  ", videotxt, browse, textpanel1);
	final JFileChooser fc = new JFileChooser();
	    	 
	browse.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					
				int retVal= fc.showOpenDialog(textpanel1); 
				if (retVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            String fstr = file.toString();
		            StringTokenizer tok = new StringTokenizer(fstr, "\\");
		      
		            int n = tok.countTokens(); 
		            String disp = fstr; 
		            while(tok.hasMoreTokens())
		            disp = tok.nextToken().toString();
		            
		            String []which = disp.split(".");
		            int ln = which.length;
		            
		            videotxt.setText("file://".concat(disp)); 
		            vidf[0] = "file://".concat(fstr); 
		            
		            mLog.info(vidf[0]); 
		            
				}
				}
			});
	textpanel.add(textpanel1); 
	//done with the upper part
	

	JPanel metapanel = new JPanel(); 
	metapanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Metadata"),
            BorderFactory.createEmptyBorder(5,5,5,5))); 
	
	metapanel.setLayout(new BoxLayout(metapanel, BoxLayout.X_AXIS));
	metapanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	ArrayList<String> choices = desc.getLabels(); 
	JLabel lab = new JLabel("Labels: "); 

	for(int n=0; n <choices.size(); ++n)
		tags.addItem(choices.get(n)); 
   
	tags.setEditable(true); 
	tags.setMaximumSize(tags.getPreferredSize());
	tags.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent event){
		
			String strselect = (String) tags.getSelectedItem();
			strselect.trim(); 
			String sum = "Label selected: " + strselect; 
			HashMap<String, String> tmp = desc.getTag(strselect);
			if(tmp==null){
				Set<String> usrlb = desc.getUserLabel(strselect);
				if(usrlb.isEmpty()) {
					sum = sum + "\n" + "No videos exist";
					outputarea.setText(sum); 
					return; 
				}
                for(String lab:usrlb)
                	sum = sum +"\n" + lab;
                outputarea.setText(sum); 
                return; 
			}
			String allvid = tmp.toString();
			
			String res [] = allvid.split("file");
			for(int n=0; n < res.length; ++n){
				if(n > 0)
				res[n] ="file".concat(res[n]);
			    sum = sum + "\n" + res[n]; 
			mLog.info(res[n]); 
			}
			outputarea.setText(sum); 
		}
	});
	metapanel.add(lab); 
	metapanel.add(tags); 
	
	
	textpanel.add(metapanel); 
	
	}
}
	
/** Takes a file name and reads line by line
 * Each line has the label and all videos 
 * classified under the label; the separation is tab
 * Creates a HashMap with labels and videos. 
 */
public HashMap<String, Set<String>> readFile(String ff, boolean tg){
	ff = ff.split("://",2)[1]; 
	mLog.info(ff);
	BufferedReader inputStream = null;
    HashMap<String, Set<String>> tags = new HashMap<String, Set<String>>();
    String l;
    String tag="";
    
	try {
        inputStream = 
            new BufferedReader(new FileReader(ff));
      
        while ((l = inputStream.readLine()) != null) {
        String [] res=	l.split("[\t|\n|\f|\r]+");
        int st=0; 
      
        tag= new String(res[0].trim());
        if(tg) st =1;
        
        Set<String> vf = new HashSet<String>(); 
        
        for(int n=st; n < res.length; ++n)
        	vf.add(res[n].trim());
        tags.put(tag, vf); 
            
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
public void addRow(String label, final JTextField field, 
		final JButton but, JPanel textpanel){
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

    h1.add(new JLabel(label2));
    h1.add(but2);
  
	return(h1);
   
	   
	   
   

}

public static void main(String[] args) {
	// TODO Auto-generated method stub
	
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          GUIClient client =   new GUIClient();
          client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  	      client.setVisible(true); 
        }
    });
  

  
}
}
