/**ClientVideo.java
 * 
 * Principal driver for remote operations. 
 * It communicates with the ServerVideo and 
 * establishes the connection. 
 * Creates an object of class VideoProxy.java that accepts 
 * a host and a port or uses the default. 
 * Performs remote operations with
 * video database, and displays them in a GUI. 
 * 
 * Uses: Connection.java, GUIRemote.java, GUIRemoteVid.java
 *       Commands.java, VideProxy.java
 *       
 * Elena Villalon
 * April 22, 2007
 * 
 */
package jremote;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class ClientVideo{
	static String host = "localhost";
	static int port = 1099; 
	VideoProxy vidRemote; 
	protected StringBuffer buildOutput; 
	protected final int DELAY = 5;
    protected String comm=null; 
    protected String url = null;
	public void setHost(String h){host = h;}
	public void setPort(int p){port = p;}
	public String getHost(){return host;}
	public int getPort(){return port;}
	private static Logger mLog = 
        Logger.getLogger(ClientVideo.class.getName());
    private static boolean debug = false;
    
	public ClientVideo(String host, int port){
		if(!debug)
			mLog.setLevel(Level.WARNING); 
		this.host = host;
		this.port = port;
		try{
		vidRemote = new VideoProxy(host, port);
        String str = "Connected to host "+ host+ " and port "+ port+ "...\n";
        mLog.info(str); 
		buildOutput = new StringBuffer(str);
		}catch(UnknownHostException uex){
			uex.printStackTrace(System.out);
			mLog.severe(uex.getMessage());
		}catch(IOException ioex){
			ioex.printStackTrace(System.out);
			mLog.severe(ioex.getMessage());
	}
	}
	

	public ClientVideo(String host, int port,
			List<Commands> comstr, 
			String urlAdd, String urlDesc,
			String labelSelected) 
	{
		this(host, port); 
		
		try{
		for(Commands cuerda:comstr){
		//Add video to database
		//Describe video metadata for urlDesc
		
		//Display videos under label selected
		if(cuerda.equals(Commands.SHOWLABEL) && labelSelected != null){
			buildOutput.append(vidRemote.videoLabel(labelSelected).toString());
		
		//Get stat for database
		}else if(cuerda.equals(Commands.STATDATABASE)){
			buildOutput.append(vidRemote.databaseStat().toString());
		
		//Find videos under same category as urlDesc
		}else if(cuerda.equals(Commands.SIMILARVIDEO) && urlDesc != null){
			buildOutput.append(vidRemote.findSimilarCategory(urlDesc).toString());
		}else{
			buildOutput.append("Commands not specified"); 
		}
	}
		} catch (Exception ae) {
            mLog.severe("Exception occurred communicating with Videos");
            ae.printStackTrace();
        }finally{
        	try{
        	vidRemote.getSocket().close();
        	}catch(IOException err){
        		 err.printStackTrace();
        	}
        }
	}
	public ClientVideo(){
		try{
			vidRemote = new VideoProxy(host, port);
			}catch(UnknownHostException uex){
				uex.printStackTrace(System.out);
				mLog.severe(uex.getMessage());
			}catch(IOException ioex){
				ioex.printStackTrace(System.out);
				mLog.severe(ioex.getMessage());
		}
		
	}
	
	//it invokes operation with database access
	public static boolean databaseOperations(String host, int port, 
			String oper, Connection con, boolean cont ) throws IOException{
		//Operations access database
		ClientVideo client= new ClientVideo(host, port);
		String url = null; 
		//Choose video ...display metadata 
		if(oper.equalsIgnoreCase(Commands.DESCRIBEVIDEO.toString()) ){
		List<String> videos=  GUIRemoteVid.readFile("videos.txt");
		String[] possibilities = videos.toArray(new String[videos.size()]);
		url = (String)JOptionPane.showInputDialog(
		                    null,
		                    "Select the video:\n",
		                    "Videos in Database Dialog",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    possibilities,null);
		url.trim();
		JOptionPane.showMessageDialog(null, "Metadata:\n " + client.vidRemote.describeVideo(url),
       		 "Response from Server database TbVideoColt", JOptionPane.INFORMATION_MESSAGE);
        client.vidRemote.getSocket().close();
		}else if (oper.equalsIgnoreCase(Commands.ADDVIDEO.toString())){
		url = con.url;
		
			 
		 if(url == null || url.equalsIgnoreCase(""))
			 return cont; 
	    	
	     
	      cont = false;
	    	   mLog.info("Add Video:..." );
	           client.vidRemote.addVideo(url);
	           client.vidRemote.getSocket().close();
	       }
		return cont; 
			}

	 public static void main(String[] args) {
		
		 
		 if(args.length>1){
			 host=args[0].trim();
			 port = Integer.parseInt(args[1].trim());
			  
		 }else if(args.length >0){
			 host=args[0].trim(); 
		 }
		
	    boolean cont = true; 
		while(cont){
			
			/**Let you choose connection (host, port) and requests
			 * It can be database transactions or accessing 
			 * collections in Server
			 */ 
			Connection con = new Connection(new JFrame(), host, port);
			if(con.host!=null && !(con.host).equalsIgnoreCase(""))
				host = con.host;
			if(con.port!=0)
				port = con.port;
			
//			Select operation from Commands values
			String oper= 	con.oper.toString();
		 
		if(oper.equalsIgnoreCase("CANCEL")) System.exit(0);
		try{
			String a [] = {host, ""+port};	
	 
		//Operations in Server does not access the database
		if(!oper.equalsIgnoreCase(Commands.ADDVIDEO.toString()) &&
			!oper.equalsIgnoreCase(Commands.DESCRIBEVIDEO.toString())){
			
			GUIRemote.main(a); 
		cont = false; 	
		}else{
	    	cont = databaseOperations(host, port, oper, con, cont); 
	    }
	        } catch (Exception ae) {
	            mLog.severe("Exception occurred communicating with Videos");
	            ae.printStackTrace();
	        }       
	           
	      
	       
	    }
	 }
	    
}

	

