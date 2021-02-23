/**
 * Interface with the methods that are invoked by
 * a remote clients
 * 
 */
package jremote;
import java.util.*;
public interface RemoteMethods {
	public void addVideo(String url);
	public String videoLabel(String tag);
	public String databaseStat(); 
	public void connect(int clientID);
	public String classifyVideo(String method, List<String>lsstVideos); 
	public String findSimilarCategory(String str); 
	public String describeVideo(String url); 	
	public void addLabel(String tag, Vector<String>urls);

}
