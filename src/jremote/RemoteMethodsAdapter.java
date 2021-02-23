/**
 * Possible operations available to remote client.
*
*
* @author  Elena  Villalon
*/

package jremote;

import java.util.List;
import java.util.Vector;


public class RemoteMethodsAdapter implements RemoteMethods{
public RemoteMethodsAdapter(){
	super();
}
/**add video url to database*/
public void addVideo(String url){
	return; 
}
/**retrieve videos under the classification tag*/ 
public String videoLabel(String tag) {
	return ""; 
}
/**show statistics of database*/
public String databaseStat(){
	return ""; 
	
}
/**connect to remote server and database*/
public void connect(int clientID){
	return;
}
public String classifyVideo(String method, List<String>lstVideos){
	return "";
}
/**retrieve video metadata*/
public String describeVideo(String url){
	return "";
}
public void addLabel(String tag, Vector<String>urls){
	
}
public String findSimilarCategory(String str){
	return ""; 
}


}
