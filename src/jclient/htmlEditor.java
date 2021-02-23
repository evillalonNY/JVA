/**
 * htmlEditor.java
 *
 * Created on April 23, 2004, 8:39 PM
 * Description: it obtains information about the URL
 * connection and access the page to display it in
 * JEditorPane
 *
 * @author  Elena  Villalon
 */
package jclient;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class htmlEditor
    extends JFrame {
  private JScrollPane scroll;
  private JEditorPane editor;
  private JLabel urlBar = null;
  /** Creates a new instance of htmlEditor */
  //@param string the name of the url connection
  static final  String h = "http://www.jpl.nasa.gov/earth/";
  private static Logger mLog = 
	    Logger.getLogger(htmlEditor.class.getName());
  private boolean debug = false;	
  public htmlEditor(String url) {
	  super("Display html");
	  if(!debug)
		  mLog.setLevel(Level.WARNING);
 
    editor = createEditorPane(url);
    scroll = new JScrollPane(editor);
    getContentPane().add(scroll, BorderLayout.CENTER);
    getContentPane().add(getUrlBar(), BorderLayout.SOUTH);

 //   setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(300, 350);
    setVisible(true);
  }

  //@return a JLabel informing about URL connections
  public JLabel getUrlBar() {
    if (urlBar == null) {
      urlBar = new JLabel("[no URL]");
    }
    return urlBar;
  }

  /** @param string with name of URL page
   * @return JEdiortPane to display the page
   * @exception IOException
   * with html rendering format
   */
  private JEditorPane createEditorPane(String txt) {
    editor = new JEditorPane();
    try {
      editor.setPage(txt);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

    editor.setEditable(false);
    editor.addHyperlinkListener(new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          try {
            editor.setPage(e.getURL());
          }
          catch (IOException ex) {
            ex.printStackTrace();
          }
        }
        else if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
          getUrlBar().setText(e.getURL().toString());
        }
        else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
          getUrlBar().setText("[no URL]");
        }
      }
    });

    return editor;
  }
  public static void main(String[] args) {
		// TODO Auto-generated method stub
		final String uuu ="http://www.youtube.com";
	

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
          public void run() {
        	  htmlEditor html= new  htmlEditor(h); 
            html.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	      html.setVisible(true); 
          }
      });
    
   
   
    
}
}
