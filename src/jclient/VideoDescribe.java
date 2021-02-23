/**
 * VideoDescribe.java
 *
 * Created on February 5, 2007
 * Description: it stores information about the contents of all 
 * videos in the database.  The collections contain my 
 * own observations of the videos.
 * In addition, it also has a collection for the user 
 * to create its own labels for classification.  
 *
 * @author  Elena  Villalon
 */
package jclient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VideoDescribe {
	enum TAB {animal, indoor, food, outdoor, people, sport, text, fantasy, transport};
	//my own classification tags
private HashMap<String, String> outdoor;  
private HashMap<String, String> indoor; 
private HashMap<String, String> sport; 
private HashMap<String, String> food;
private HashMap<String, String> animal;
private HashMap<String, String> people;
private HashMap<String, String> text;
private HashMap<String, String> fantasy;
private HashMap<String, String> transport;
private HashMap<String, String> describe;
//the user classification tags get stored in the HashMap
private HashMap<String, Set<String>> userlabel; 
 //to store the strings with tags names
private ArrayList<String> videourl;
//String has all videos under the classification label
private String describeTag = ""; 
//String describing the database
private String databaseStat=""; 
private final int allfrms = 45; 
private final int maxfrm = 1690;
private final int maxdur = 66;
private static int avgfrm = 470; 
private static Logger mLog = 
    Logger.getLogger(VideoDescribe.class.getName());
private static boolean debug = false;
/**
 * assesor for classification tags
 */
public HashMap<String, String> getTag(String what){
	
  if(what.equalsIgnoreCase("animal")) {
	  
	  return animal;
  }
  if(what.equalsIgnoreCase("people")) return people;
  if(what.equalsIgnoreCase("text"))return text;
  if(what.equalsIgnoreCase("indoor"))return indoor;
  if(what.equalsIgnoreCase("outdoor"))return outdoor;
  if(what.equalsIgnoreCase("sport"))return sport;
  if(what.equalsIgnoreCase("food"))return food;
  if(what.equalsIgnoreCase("fantasy"))return fantasy;
  if(what.equalsIgnoreCase("transport"))return transport;
  return null;
  
}
public ArrayList<String> getVideourl(){
	
	 
	return videourl; 
}
//for given userlabel key (classification tag) treturns the videos
public Set<String> getUserLabel(String what){
	Set<String> usr = userlabel.get(what);
	return usr; 
}
//setter for user labels & tags
public void setUserlabel(HashMap<String, Set<String>> usr){
	userlabel=usr; 
}
public ArrayList<String> getLabels(){
	ArrayList<String> tags = new ArrayList<String>();
	tags.addAll(videourl);
	Set<String>	tagval= userlabel.keySet();
	if(tagval.isEmpty())
		return tags;
		
	for(String str:tagval)
       tags.add(str);  	
	return tags;		
}
public void setDescribeTag(String what){
	String res [] = what.split("file");
	String sum = ""; 
	for(int n=0; n < res.length; ++n){
		if(n > 0)
		res[n] ="file".concat(res[n]);
	    sum = sum + "\n" + res[n]; 
	}
	describeTag = sum;
}

public synchronized String getDescribeTag(){
	return describeTag; 
}
public synchronized String getDatabaseStat(){
	return databaseStat; 
}
public VideoDescribe(){ 
    if(!debug)
    	mLog.setLevel(Level.WARNING);
	outdoor = new HashMap<String, String>();
	indoor = new HashMap<String, String>();
	sport = new HashMap<String, String>();
	food = new HashMap<String, String>();
	animal = new HashMap<String, String>();
	people = new HashMap<String, String>();
	text = new HashMap<String, String>();
	fantasy = new HashMap<String, String>();
	transport = new HashMap<String, String>();
	describe = new HashMap<String, String>();
	userlabel = new HashMap<String,Set<String>>();
	 videourl = new ArrayList<String>();
	fillarray(); 
	    
}
public VideoDescribe(String label){ 
this(); 
classifiedVideos();
HashMap<String, String> tmp = getTag(label);
String allvid = tmp.toString();
setDescribeTag(allvid); 
   
}
/**
 * Client can add a new label or tag for classification,  
 * which creates an entry in the HashMap of key =tag
 * and value a new HashSet<string> to put videos 
 * that are classified under the key entry tag.
 */
public void addLabel(String tag, String vid){
	tag.trim();
	vid.trim();
	if(!userlabel.isEmpty() && userlabel.containsKey(tag)){
		Set<String>	tagval= userlabel.get(tag);
		tagval.add(vid);
		return;
	}
	Set<String>	tagval = new HashSet<String>();
	tagval.add(vid);
	userlabel.put(tag, tagval);
		
}
/**
 * For classification tag gets all videos 
 * Primary key under the label
 */
public String retrieveLabel(String tag){
	tag.trim();
	
	String videos="";
	for(int n=0; n< videourl.size(); ++n ){
		
		if(videourl.get(n).contentEquals(tag)){
		HashMap<String, String> tmp = 	getTag(tag);
		Collection<String> val = tmp.values();
		for(String str:val)
		    videos = videos + str + "\n"; 
		return videos;
		}
	}
		
	if(userlabel.isEmpty() || !userlabel.containsKey(tag))
		return videos; 
	
	Set<String>	tagval= userlabel.get(tag);
	for(String str:tagval)
       videos = videos + str + "\n"; 	
	return videos;		
}
/**Retrieve all labels for classification that includes
 * the ones I have defined and the ones that the users define.
*/
public String getTodo(){
	String ret=""; 	
	for(int n=0; n< videourl.size(); ++n ){
		if(videourl.get(n)==null) continue; 
		ret = ret + videourl.get(n) + "\n"; 
	    mLog.info(ret);
	}
		
	if(userlabel.isEmpty()){
	
		return ret;
	}
		
	
	Set<String>	tagval= userlabel.keySet();
	if(tagval.isEmpty())
		return ret;
		
	for(String str:tagval)
       ret = ret + str + "\n"; 	
	return ret;		
}
/**
 * Returns the String that describes video contents
 */
public synchronized String getVideoDesc(String vid){
	
	addDescription();  
    vid.trim();
	Set<String> urls =this.describe.keySet();
	String ddd=""; 
    for(String str:urls){
    	mLog.info(str); 
    	if(str.contentEquals(vid)){
    		ddd = this.describe.get(str);
    		mLog.info(ddd); 
    	}
    }
    return(ddd);
}


public  VideoDescribe(boolean b){
	this();  
	addDescription(); 
	classifiedVideos();
	if(b)
	databaseStat =calculateStat(); 
}

public synchronized String calculateStat(){
	StringBuffer buf = new StringBuffer("Number of videos in database " + describe.size()+ "\n");
	String res ="";
	res= "# OUTDOOR Videos: "+ outdoor.size() +"\n";
	buf.append(res); 
	res= "# INDOOR Videos: "+ indoor.size() +"\n"; 
	buf.append(res); 
	res= "# SPORT Videos: "+ sport.size() +"\n"; 
	buf.append(res); 
	res= "# Videos with FOOD contents: "+ food.size() +"\n"; 
	buf.append(res); 
	res= "# Videos with ANIMAL: "+ animal.size() +"\n"; 
	buf.append(res); 
	res= "#  Videos withb PEOPLE: "+ people.size() +"\n"; 
	buf.append(res); 
	res= "# Videos with Text contents: "+ text.size() +"\n"; 
	buf.append(res); 
	res= "# FANTASY Videos: "+ fantasy.size() +"\n"; 
	buf.append(res); 
	res= "# TRANSPORT Videos: "+ transport.size() +"\n"; 
	buf.append(res); 
	res= "Max# of frames : " + maxfrm + "\t Max duration sc "+ maxdur+ "\n"; 
	buf.append(res); 
	res= "Average# of frames : " + avgfrm +"\n"; 
	buf.append(res); 
	
	return buf.toString(); 
}
/** Given a video PK as represented by String str, 
 * it finds the HasMap categories as in enum the video 
 * belongs to. List those categories and names (PK) 
 * of the other videos under the same categories.
 * Returns the String with the output of the search
 * for categories and videos similar to the input PK str.
 *
*/
public synchronized String getSimilar(String str){
	StringBuffer buf = new StringBuffer("Categories of labels " +
			str +" belongs to:\n");
	List<HashMap<String, String>> lstmaps = new ArrayList<HashMap<String, String>>(); 
	List<String> lsttags = new ArrayList<String>(); 
	for (TAB tab: TAB.values()){
		String tabstr = tab.toString();
		
		HashMap<String, String> label = getTag(tabstr);
		if(label.isEmpty()) continue; 
		if(label.containsKey(str)){
			lstmaps.add(label);
			lsttags.add(tabstr); 
			buf.append(tabstr +",\t");
		}
	}
    buf.append("\nListing of videos under each category:\n"); 
	int cnt =0; 
	for(HashMap<String,String> label: lstmaps){
		buf.append(lsttags.get(cnt)+":\n"); 
		cnt++; 
		for(String vid:label.keySet()){
				buf.append(vid+"\n"); 
			}
		buf.append("\n"); 
		}
	return buf.toString(); 		
	}
public void fillarray(){
	videourl.add("animal");
	videourl.add("text");
	videourl.add("indoor");
	videourl.add("outdoor");
	videourl.add("people");
	videourl.add("sport");
	videourl.add("fantasy");
	videourl.add("transport");
	videourl.add("food");
}
/**
 * Has all categories and labels that  any video enters 
 * the database belongs to
 */
public void classifiedVideos(){
	 
//bailey.mpg: little barking dog
animal.put(new String("file:videos/bailey.mpg"), new String("dog"));
indoor.put(new String("file:videos/bailey.mpg"), new String("bedroom"));

//apina.mpg: pissing monkey
animal.put(new String("file:videos/apina.mpg"), new String("monkey"));
outdoor.put(new String("file:videos/apina.mpg"), new String("grassy"));

//elephant.mpeg: parade
animal.put(new String("file:videos/elephant.mpeg"),new String("elephant")); 
outdoor.put(new String("file:videos/elephant.mpeg"),new String("park, parade"));
people.put(new String("file:videos/elephant.mpeg"),new String("adult, child"));

//file:videos/Beerlives.mpg: drink beer or die
people.put(new String("file:videos/Beerlives.mpg"), new String("adult"));
outdoor.put(new String("file:videos/Beerlives.mpg"), 
		new String("mountain, green, sky, road"));
sport.put(new String("file:videos/Beerlives.mpg"), new String("bycicle"));
food.put(new String("file:videos/Beerlives.mpg"), new String("beer"));
text.put(new String("file:videos/Beerlives.mpg"), new String("ad"));

//file:videos/alien.mpg

fantasy.put(new String("file:videos/alien.mpg"),new String("red"));

//file:videos/barney-origin.mov

fantasy.put(new String("file:videos/barney-origin.mov"),new String("black-white"));

//file:videos/bluescreen2.mov

animal.put(new String("file:videos/bluescreen2.mov"),new String("fish")); 
outdoor.put(new String("file:videos/bluescreen2.mov"),new String("sea, ocean plants"));

//file:videos/antzeladimitriou.mpg
people.put(new String("file:videos/antzeladimitriou.mpg"), new String("adult"));
outdoor.put(new String("file:videos/antzeladimitriou.mpg"), 
		new String("tree, sky, city"));
text.put(new String("file:videos/antzeladimitriou.mpg"), new String("ad")); 

//file:videos/millertime.mpg	
animal.put(new String("file:videos/millertime.mpg"),new String("dog")); 
indoor.put(new String("file:videos/millertime.mpg"),new String("house"));
people.put(new String("file:videos/millertime.mpg"),new String("adult"));
food.put(new String("file:videos/millertime.mpg"), new String("beer"));
text.put(new String("file:videos/millertime.mpg"), new String("ad")); 

//file:videos/porky.mov

fantasy.put(new String("file:videos/porky.mov"),new String("black-white"));

//file:videos/faceburn.mpg	
people.put(new String("file:videos/faceburn.mpg"), new String("adult"));
outdoor.put(new String("file:videos/faceburn.mpg"), 
		new String("night, fire, light"));

//file:videos/aliensong.mpeg

fantasy.put(new String("file:videos/aliensong.mpeg"),new String("blue, green"));
text.put(new String("file:videos/aliensong.mpeg"), new String("ad")); 

//file:videos/babythrowup.mpeg
indoor.put(new String("file:videos/babythrowup.mpeg"),new String("bedroom"));
people.put(new String("file:videos/babythrowup.mpeg"),new String("adult, child"));
food.put(new String("file:videos/babythrowup.mpeg"), new String("baby-food"));
text.put(new String("file:videos/babythrowup.mpeg"), new String("ad")); 

//file:videos/bebekarate.mpeg
indoor.put(new String("file:videos/bebekarate.mpeg"),new String("hospital"));
people.put(new String("file:videos/bebekarate.mpeg"),new String("adult, child"));
text.put(new String("file:videos/bebekarate.mpeg"), new String("ad")); 

//file:videos/bicycle.mpg
outdoor.put(new String("file:videos/bicycle.mpg"),new String("road, country, trees"));
people.put(new String("file:videos/bicycle.mpg"),new String("adult"));
transport.put(new String("file:videos/bicycle.mpg"),new String("bicycle"));
sport.put(new String("file:videos/bicycle.mpg"),new String("cycling"));

//file:videos/bigbend.mpg	
outdoor.put(new String("file:videos/bigbend.mpg"),new String("country,ocean,city,landscape, mountain"));

//file:videos/bikeoops.mpeg
outdoor.put(new String("file:videos/bikeoops.mpeg"),new String("road, country, trees"));
people.put(new String("file:videos/bikeoops.mpeg"),new String("adult"));
transport.put(new String("file:videos/bikeoops.mpeg"),new String("motorbike, car"));
sport.put(new String("file:videos/bikeoops.mpeg"),new String("race"));

//file:videos/broken_ribs.mpg
outdoor.put(new String("file:videos/broken_ribs.mpg"),new String("park, trees"));
people.put(new String("file:videos/broken_ribs.mpg"),new String("adult"));
transport.put(new String("file:videos/broken_ribs.mpg"),new String("bicycle"));
sport.put(new String("file:videos/broken_ribs.mpg"),new String("jump"));

//file:videos/BudDancing.mpge: no sucess to put in database
indoor.put(new String("file:videos/BudDancing.mpge"),new String("party, disco"));
people.put(new String("file:videos/BudDancing.mpge"),new String("adult"));

//file:videos/c431v.mpg	
outdoor.put(new String("file:videos/c431v.mpg"),new String("mountain, country, dry-land"));
people.put(new String("file:videos/c431v.mpg"),new String("adult"));

//file:videos/c427v.mpg
indoor.put(new String("file:videos/c427v.mpg"),new String("water, dish"));
people.put(new String("file:videos/c427v.mpg"),new String("adult"));

//file:videos/c450v.mpg
outdoor.put(new String("file:videos/c450v.mpg"),new String("mountain, country, dry-land"));
people.put(new String("file:videos/c450v.mpg"),new String("adult"));
transport.put(new String("file:videos/c450v.mpg"),new String("train"));

//file:videos/catfight.mpeg
animal.put(new String("file:videos/"),new String("cat")); 
indoor.put(new String("file:videos/"),new String("room"));
text.put(new String("file:videos/"), new String("ad")); 

//file:videos/elvis-movie.mov
outdoor.put(new String("file:videos/elvis-movie.mov"),new String("mountain, country, road"));
people.put(new String("file:videos/elvis-movie.mov"),new String("adult, doll"));
transport.put(new String("file:videos/elvis-movie.mov"),new String("car"));

//file:videos/fire_zone_51.mov
outdoor.put(new String("file:videos/fire_zone_51.mov"),new String("house,night, light,fire"));
people.put(new String("file:videos/fire_zone_51.mov"),new String("adult"));
sport.put(new String("file:videos/fire_zone_51.mov"),new String("game"));

//file:videos/flippingcar.mpeg
outdoor.put(new String("file:videos/flippingcar.mpeg"),new String("country, road, green"));
transport.put(new String("file:videos/flippingcar.mpeg"),new String("car"));

//file:videos/fordka-bird.mpg
outdoor.put(new String("file:videos/fordka-bird.mpg"),new String("house,road, green, tree"));
transport.put(new String("file:videos/fordka-bird.mpg"),new String("car"));
animal.put(new String("file:videos/fordka-bird.mpg"),new String("bird")); 
text.put(new String("file:videos/fordka-bird.mpg"), new String("ad"));

//file:videos/fordka-cat.mpg
outdoor.put(new String("file:videos/fordka-cat.mpg"),new String("house,road, flowers,tree"));
transport.put(new String("file:videos/fordka-cat.mpg"),new String("car"));
animal.put(new String("file:videos/fordka-cat.mpg"),new String("cat")); 
text.put(new String("file:videos/fordka-cat.mpg"), new String("ad"));

//file:videos/frens2.mpeg
outdoor.put(new String("file:videos/frens2.mpeg"),new String("country,grass"));
animal.put(new String("file:videos/frens2.mpeg"),new String("monkey")); 
people.put(new String("file:videos/frens2.mpeg"), new String("adult"));

//file:videos/fs14ss.mpeg	
outdoor.put(new String("file:videos/fs14ss.mpeg"),new String("ocean,sky, blue"));
transport.put(new String("file:videos/fs14ss.mpeg"),new String("airplane, boat"));
people.put(new String("file:videos/fs14ss.mpeg"),new String("adult")); 
text.put(new String("file:videos/fs14ss.mpeg"), new String("ad"));

//file:videos/hitbycars.mpeg
outdoor.put(new String("file:videos/hitbycars.mpeg"),new String("city,road, blue, house"));
transport.put(new String("file:videos/hitbycars.mpeg"),new String("car"));
people.put(new String("file:videos/hitbycars.mpeg"),new String("adult")); 

//file:videos/Javelin.mpg	
outdoor.put(new String("file:videos/Javelin.mpg"),new String("outdoor,sports, stadium"));
sport.put(new String("file:videos/Javelin.mpg"),new String("javelin"));
people.put(new String("file:videos/Javelin.mpg"),new String("adult")); 
text.put(new String("file:videos/Javelin.mpg"), new String("ad"));

//file:videos/jet.mpg
fantasy.put(new String("file:videos/jet.mpg"),new String("blue, brown, camara-motion"));

//file:videos/Liegerad_newyork.mpg
outdoor.put(new String("file:videos/Liegerad_newyork.mpg"),new String("outdoor,road, bridge,city, suburbs, NY"));
sport.put(new String("file:videos/Liegerad_newyork.mpg"),new String("bike race"));
people.put(new String("file:videos/Liegerad_newyork.mpg"),new String("adult")); 
text.put(new String("file:videos/Liegerad_newyork.mpg"), new String("ad"));
transport.put(new String("file:videos/Liegerad_newyork.mpg"),new String("car, bikes"));

//file:videos/malcolmsballs.mpg	
indoor.put(new String("file:videos/malcolmsballs.mpg"),new String("screen,white"));
sport.put(new String("file:videos/malcolmsballs.mpg"),new String("game, balls"));
people.put(new String("file:videos/malcolmsballs.mpg"),new String("adult")); 
text.put(new String("file:videos/malcolmsballs.mpg"), new String("ad"));

//file:videos/noboss1.mpeg
indoor.put(new String("file:videos/noboss1.mpeg"),new String("office,white, blue"));
sport.put(new String("file:videos/noboss1.mpeg"),new String("rowing, bike"));
people.put(new String("file:videos/noboss1.mpeg"),new String("adult")); 
text.put(new String("file:videos/noboss1.mpeg"), new String("website"));

//file:videos/noboss2.mpeg	
indoor.put(new String("file:videos/noboss2.mpeg"),new String("office,white"));
sport.put(new String("file:videos/noboss2.mpeg"),new String("jumping, running"));
people.put(new String("file:videos/noboss2.mpeg"),new String("adult")); 

//file:videos/p342v.mpg
animal.put(new String("file:videos/p342v.mpg"),new String("turtle")); 
outdoor.put(new String("file:videos/p342v.mpg"),new String("outdoor,stones,light"));

//file:videos/p546v.mpg
animal.put(new String("file:videos/p546v.mpg"),new String("serpent"));
outdoor.put(new String("file:videos/p546v.mpg"),new String("bushes,rocks,light"));

//file:videos/p827v.mpg	
animal.put(new String("file:videos/p827v.mpg"),new String("coyote"));
outdoor.put(new String("file:videos/p827v.mpg"),new String("bushes,rocks,light"));

//file:videos/parkinglotkiss.mpeg
indoor.put(new String("file:videos/parkinglotkiss.mpeg"),new String("garage"));
people.put(new String("file:videos/parkinglotkiss.mpeg"),new String("adult")); 
text.put(new String("file:videos/parkinglotkiss.mpeg"), new String("website,ad"));
transport.put(new String("file:videos/parkinglotkiss.mpeg"),new String("cars"));

//file:videos/Photocopies.mpeg	
indoor.put(new String("file:videos/Photocopies.mpeg"),new String("office, white"));
people.put(new String("file:videos/Photocopies.mpeg"),new String("adult")); 
text.put(new String("file:videos/Photocopies.mpeg"), new String("text"));
food.put(new String("file:videos/Photocopies.mpeg"), new String("drinks"));

//file:videos/pianoplayer1.mpeg	
indoor.put(new String("file:videos/pianoplayer1.mpeg"),new String("interior, dance"));
people.put(new String("file:videos/pianoplayer1.mpeg"),new String("adult"));

//file:videos/pinata.mpeg
indoor.put(new String("file:videos/pinata.mpeg"),new String("interior, game, balloon"));
people.put(new String("file:videos/pinata.mpeg"),new String("adult, child"));

//file:videos/pole.mpeg
indoor.put(new String("file:videos/pole.mpeg"),new String("stadium, game, lights"));
people.put(new String("file:videos/pole.mpeg"),new String("adults"));
sport.put(new String("file:videos/pole.mpeg"),new String("jump, pole"));

//file:videos/pullup.mpeg	
outdoor.put(new String("file:videos/pullup.mpeg"),new String("road, house,tree,city"));
people.put(new String("file:videos/pullup.mpeg"),new String("adult")); 
transport.put(new String("file:videos/pullup.mpeg"),new String("car, bikes"));
sport.put(new String("file:videos/pullup.mpeg"),new String("bike-jump"));

//file:videos/rally.mpeg	
outdoor.put(new String("file:videos/rally.mpeg"),new String("road, country"));
people.put(new String("file:videos/rally.mpeg"),new String("adult")); 
transport.put(new String("file:videos/rally.mpeg"),new String("car"));
sport.put(new String("file:videos/rally.mpeg"),new String("car-race"));

//file:videos/raversretarded.mpg
outdoor.put(new String("file:videos/raversretarded.mpg"),new String("road, trees yellow"));
people.put(new String("file:videos/raversretarded.mpg"),new String("adult")); 
transport.put(new String("file:videos/raversretarded.mpg"),new String("car"));

//file:videos/roll.mpg
outdoor.put(new String("file:videos/roll.mpg"),new String("night, black, ligths, house"));
people.put(new String("file:videos/roll.mpg"),new String("adult")); 
text.put(new String("file:videos/roll.mpg"), new String("website, ad")); 
sport.put(new String("file:videos/roll.mpg"),new String("aerobic"));

//file:videos/sage_beamingpoi.mpg
outdoor.put(new String("file:videos/sage_beamingpoi.mpg"),new String("night, black, ligths, house"));
people.put(new String("file:videos/sage_beamingpoi.mpg"),new String("adult")); 
text.put(new String("file:videos/sage_beamingpoi.mpg"), new String("website, ad")); 
sport.put(new String("file:videos/sage_beamingpoi.mpg"),new String("aerobic"));

//file:videos/senator.mpeg	
indoor.put(new String("file:videos/senator.mpeg"),new String("interiors,TV broadcast, flag"));
people.put(new String("file:videos/senator.mpeg"),new String("adult")); 
text.put(new String("file:videos/senator.mpeg"), new String("text, ad")); 

//file:videos/sharkbite.mpe	
animal.put(new String("file:videos/sharkbite.mpe"),new String("shark"));
outdoor.put(new String("file:videos/sharkbite.mpe"),new String("ocean,sky,blue"));
people.put(new String("file:videos/sharkbite.mpe"),new String("adult")); 
text.put(new String("file:videos/sharkbite.mpe"), new String("website"));
transport.put(new String("file:videos/sharkbite.mpe"),new String("boat"));

//file:videos/peli1.mov
animal.put(new String("file:videos/peli1.mov"),new String("fish")); 
outdoor.put(new String("file:videos/peli1.mov"),new String("sea, ocean plants"));

//file:videos/sports.mpeg	
outdoor.put(new String("file:videos/sports.mpeg"),new String("road,country,grass, bridge,river"));
people.put(new String("file:videos/sports.mpeg"),new String("adult")); 
text.put(new String("file:videos/sports.mpeg"), new String("ad"));
transport.put(new String("file:videos/sports.mpeg"),new String("bike"));
sport.put(new String("file:videos/sports.mpeg"),new String("race"));

//file:videos/t38x1.mpg
outdoor.put(new String("file:videos/t38x1.mpg"),new String("country,grass,tre,road, house"));
people.put(new String("file:videos/t38x1.mpg"),new String("adult")); 
transport.put(new String("file:videos/t38x1.mpg"),new String("bike"));

//file:videos/t38x2.mpg
outdoor.put(new String("file:videos/t38x2.mpg"),new String("country,grass,tree,road, house"));
people.put(new String("file:videos/t38x2.mpg"),new String("adult")); 
transport.put(new String("file:videos/t38x2.mpg"),new String("bike"));

//file:videos/vibraspot.mpg
outdoor.put(new String("file:videos/vibraspot.mpg"),new String("tree,cafe"));
people.put(new String("file:videos/vibraspot.mpg"),new String("adult")); 
text.put(new String("file:videos/vibraspot.mpg"), new String("ad"));

//file:videos/waterski.mpg
outdoor.put(new String("file:videos/waterski.mpg"),new String("beach,sand,ocean"));
people.put(new String("file:videos/waterski.mpg"),new String("adult, child"));
transport.put(new String("file:videos/waterski.mpg"),new String("boat"));

//file:videos/wedding.mpeg
outdoor.put(new String("file:videos/wedding.mpeg"),new String("road,country,house, black, white"));
people.put(new String("file:videos/wedding.mpeg"),new String("adult")); 
text.put(new String("file:videos/wedding.mpeg"), new String("ad"));
transport.put(new String("file:videos/wedding.mpeg"),new String("car"));
indoor.put(new String("file:videos/wedding.mpeg"),new String("party, wedding,fun"));

//file:videos/bellyflop.mpg
indoor.put(new String("file:videos/bellyflop.mpg"),new String("stadium, water"));
people.put(new String("file:videos/bellyflop.mpg"),new String("adults"));
sport.put(new String("file:videos/bellyflop.mpg"),new String("jump,dive, swimming pool"));

//file:videos/c448v.mpg
outdoor.put(new String("file:videos/c448v.mpg"),new String("rail road,country,desert, brown"));

//file:videos/c449v.mpg	
outdoor.put(new String("file:videos/c449v.mpg"),new String("rail road,country,desert, brown, black,tunel"));

//file:videos/karatechimp.mpeg
indoor.put(new String("file:videos/karatechimp.mpeg"),new String("gym"));
people.put(new String("file:videos/karatechimp.mpeg"),new String("adults"));
sport.put(new String("file:videos/karatechimp.mpeg"),new String("karate,fight"));
animal.put(new String("file:videos/karatechimp.mpeg"),new String("chimp"));

//file:videos/kitty-gore.mpg	
indoor.put(new String("file:videos/kitty-gore.mpg"),new String("butchery"));
text.put(new String("file:videos/kitty-gore.mpg"),new String("website"));
food.put(new String("file:videos/kitty-gore.mpg"),new String("meat"));
animal.put(new String("file:videos/kitty-gore.mpg"),new String("cat"));


//file:videos/bikercollision.mpg		
outdoor.put(new String("file:videos/bikercollision.mpg"),new String("road, town, house"));
text.put(new String("file:videos/bikercollision.mpg"),new String("ad"));
transport.put(new String("file:videos/bikercollision.mpg"),new String("car, motorbike"));
people.put(new String("file:videos/bikercollision.mpg"),new String("adult"));

//file:videos/harriercrashing.mpg	
outdoor.put(new String("file:videos/harriercrashing.mpg"),new String("ocean,sky,blue"));
people.put(new String("file:videos/harriercrashing.mpg"),new String("adult")); 
transport.put(new String("file:videos/harriercrashing.mpg"),new String("airplane"));
sport.put(new String("file:videos/harriercrashing.mpg"),new String("jumping parashutting"));

//file:videos/KFee_Auto.mpg	
outdoor.put(new String("file:videos/KFee_Auto.mpg"),new String("woods,trees,road"));
people.put(new String("file:videos/KFee_Auto.mpg"),new String("adult")); 
transport.put(new String("file:videos/KFee_Auto.mpg"),new String("car"));
text.put(new String("file:videos/KFee_Auto.mpg"),new String("ad"));

//file:videos/snakenosemouth.mpeg
people.put(new String("file:videos/snakenosemouth.mpeg"),new String("adults"));
animal.put(new String("file:videos/snakenosemouth.mpeg"),new String("snake"));

//file:videos/piewibdentiste.mpeg
outdoor.put(new String("file:videos/piewibdentiste.mpeg"),new String("ocean,clouds,grey,house"));
people.put(new String("file:videos/piewibdentiste.mpeg"),new String("adult")); 
text.put(new String("file:videos/piewibdentiste.mpeg"),new String("ad"));

//file:videos/pinguinsinks.mpg
animal.put(new String("file:videos/pinguinsinks.mpg"),new String("pinguin"));
outdoor.put(new String("file:videos/pinguinsinks.mpg"),new String("ocean,ice,snow"));

//file:videos/foot-theiko2.mpeg
outdoor.put(new String("file:videos/foot-theiko2.mpeg"),new String("stadium, green"));
people.put(new String("file:videos/foot-theiko2.mpeg"),new String("adults"));
sport.put(new String("file:videos/foot-theiko2.mpeg"),new String("soccer"));
text.put(new String("file:videos/foot-theiko2.mpeg"),new String("ad, website"));

//file:videos/EarlyC.mpeg	
indoor.put(new String("file:videos/EarlyC.mpeg"),new String("house, TV"));
people.put(new String("file:videos/EarlyC.mpeg"),new String("baby"));
//file:videos/sumo.mpeg	587	26.14
indoor.put(new String("file:videos/sumo.mpeg"),new String("interior"));
people.put(new String("file:videos/sumo.mpeg"),new String("adults"));
sport.put(new String("file:videos/sumo.mpeg"),new String("wrestling"));

//file:videos/blowupraft.mpeg
outdoor.put(new String("file:videos/blowupraft.mpeg"),new String("yard, stairs"));
people.put(new String("file:videos/blowupraft.mpeg"),new String("adult, child"));
transport.put(new String("file:videos/blowupraft.mpeg"),new String("raft"));
text.put(new String("file:videos/blowupraft.mpeg"),new String("ad, website"));

//file:videos/bbquiet.mpg
indoor.put(new String("file:videos/bbquiet.mpg"),new String("home, rooms"));
people.put(new String("file:videos/bbquiet.mpg"),new String("adults"));
transport.put(new String("file:videos/bbquiet.mpg"),new String("car"));
text.put(new String("file:videos/bbquiet.mpg"),new String("ad, website"));

//file:videos/acc - f1pits-verstapp.mpeg
outdoor.put(new String("file:videos/acc - f1pits-verstapp.mpeg"),new String("stadium, fire, smoke"));
people.put(new String("file:videos/acc - f1pits-verstapp.mpeg"),new String("adults"));
transport.put(new String("file:videos/acc - f1pits-verstapp.mpeg"),new String("cars"));
sport.put(new String("file:videos/acc - f1pits-verstapp.mpeg"),new String("racing automobiles"));

//file:videos/footySmart-theiko.mpg	
outdoor.put(new String("file:videos/footySmart-theiko.mpg"),new String("stadium, black-white"));
people.put(new String("file:videos/footySmart-theiko.mpg"),new String("adults"));
sport.put(new String("file:videos/footySmart-theiko.mpg"),new String("soccer"));
text.put(new String("file:videos/footySmart-theiko.mpg"),new String("ad, website"));

//file:videos/football-ayhana.mov	
outdoor.put(new String("file:videos/football-ayhana.mov"),new String("stadium, green"));
people.put(new String("file:videos/football-ayhana.mov"),new String("adults"));
sport.put(new String("file:videos/football-ayhana.mov"),new String("soccer"));
text.put(new String("file:videos/football-ayhana.mov"),new String("ad, website"));

//file:videos/gym2.mpeg
indoor.put(new String("file:videos/gym2.mpeg"),new String("stadium, gym, blue, jump"));
people.put(new String("file:videos/gym2.mpeg"),new String("adults"));
sport.put(new String("file:videos/gym2.mpeg"),new String("leap jump"));
text.put(new String("file:videos/gym2.mpeg"),new String("ad, website"));

//file:videos/kick-ghpedo.mpg
outdoor.put(new String("file:videos/kick-ghpedo.mpg"),new String("stadium"));
people.put(new String("file:videos/kick-ghpedo.mpg"),new String("adults"));

//file:videos/gym1.mpeg
indoor.put(new String("file:videos/gym1.mpeg"),new String("gym, red carpet, jump"));
people.put(new String("file:videos/gym1.mpeg"),new String("adults"));
sport.put(new String("file:videos/gym1.mpeg"),new String("leap jump"));

//file:videos/acc - pooljump.mpeg	
outdoor.put(new String("file:videos/acc - pooljump.mpeg"),new String("trees, water, blue, green"));
people.put(new String("file:videos/acc - pooljump.mpeg"),new String("adults"));

//file:videos/ben5.mpg
outdoor.put(new String("file:videos/ben5.mpg"),new String("night, black, ligths"));
people.put(new String("file:videos/ben5.mpg"),new String("adult")); 
text.put(new String("file:videos/ben5.mpg"), new String("website, ad")); 
sport.put(new String("file:videos/ben5.mpg"),new String("aerobic"));
//file:videos/sage_dbl_staff.mpg	
outdoor.put(new String("file:videos/sage_dbl_staff.mpg"),new String("night, black, ligths"));
people.put(new String("file:videos/sage_dbl_staff.mpg"),new String("adult")); 
text.put(new String("file:videos/sage_dbl_staff.mpg"), new String("website, ad")); 
sport.put(new String("file:videos/sage_dbl_staff.mpg"),new String("aerobic"));

//file:videos/t38x3.mpg	
outdoor.put(new String("file:videos/t38x3.mpg"),new String("country,grass,tree,road, house"));
people.put(new String("file:videos/t38x3.mpg"),new String("adult")); 
transport.put(new String("file:videos/t38x3.mpg"),new String("bike"));
//file:videos/wyms1.mpg
outdoor.put(new String("file:videos/wyms1.mpg"),new String("country,grass,tree,road, house"));
people.put(new String("file:videos/wyms1.mpg"),new String("adult")); 
transport.put(new String("file:videos/wyms1.mpg"),new String("bike"));
//file:videos/wyms2.mpg
outdoor.put(new String("file:videos/wyms2.mpg"),new String("country,grass,tree,road, house"));
people.put(new String("file:videos/wyms2.mpg"),new String("adult")); 
transport.put(new String("file:videos/wyms2.mpg"),new String("bike"));
//file:videos/wyms3.mpg
outdoor.put(new String("file:videos/wyms3.mpg"),new String("country,grass,tree,road, house"));
people.put(new String("file:videos/wyms3.mpg"),new String("adult")); 
transport.put(new String("file:videos/wyms3.mpg"),new String("bike"));
//file:videos/wyms4.mpg
outdoor.put(new String("file:videos/wyms4.mpg"),new String("country,grass,tree,road, house"));
people.put(new String("file:videos/wyms4.mpg"),new String("adult")); 
transport.put(new String("file:videos/wyms4.mpg"),new String("bike"));
//file:videos/c430v.mpg	
outdoor.put(new String("file:videos/c430v.mpg"),new String("country,desert, brown"));
people.put(new String("file:videos/c430v.mpg"),new String("adult")); 
//file:videos/c432v.mpg	
outdoor.put(new String("file:videos/c432v.mpg"),new String("country,desert, brown"));
people.put(new String("file:videos/c432v.mpg"),new String("adult")); 
//file:videos/p346v.mpg
animal.put(new String("file:videos/p346v.mpg"),new String("lizard")); 
outdoor.put(new String("file:videos/p346v.mpg"),new String("outdoor,stones,light"));
text.put(new String("file:videos/p346v.mpg"), new String("ad"));
//file:videos/c436v.mpg	
outdoor.put(new String("file:videos/c436v.mpg"),new String("country,desert, brown"));
people.put(new String("file:videos/c436v.mpg"),new String("adult")); 
//file:videos/acc - almost crash.mpeg	
outdoor.put(new String("file:videos/acc - almost crash.mpeg"),new String("road,rain, blue"));
people.put(new String("file:videos/acc - almost crash.mpeg"),new String("adult")); 
text.put(new String("file:videos/acc - almost crash.mpeg"), new String("ad"));
transport.put(new String("file:videos/acc - almost crash.mpeg"),new String("car"));
//file:videos/c425v.mpg	
indoor.put(new String("file:videos/c425v.mpg"),new String("water, dish"));
people.put(new String("file:videos/c425v.mpg"),new String("adult"));
//file:videos/fakeplaybehind.mpeg	
outdoor.put(new String("file:videos/fakeplaybehind.mpeg"),new String("fields, green"));
people.put(new String("file:videos/fakeplaybehind.mpeg"),new String("adult")); 
sport.put(new String("file:videos/fakeplaybehind.mpeg"),new String("soccer, ball"));

//file:videos/beardsley.mpeg	
outdoor.put(new String("file:videos/beardsley.mpeg"),new String("fields, green"));
people.put(new String("file:videos/beardsley.mpeg"),new String("adult")); 
sport.put(new String("file:videos/beardsley.mpeg"),new String("soccer, ball"));
//file:videos/maradonaturn.mpeg	
outdoor.put(new String("file:videos/maradonaturn.mpeg"),new String("fields, green"));
people.put(new String("file:videos/maradonaturn.mpeg"),new String("adult")); 
sport.put(new String("file:videos/maradonaturn.mpeg"),new String("soccer, ball"));
//file:videos/soccerinjury.mpg	
outdoor.put(new String("file:videos/soccerinjury.mpg"),new String("stadium, night, green"));
people.put(new String("file:videos/soccerinjury.mpg"),new String("adults, players")); 
sport.put(new String("file:videos/soccerinjury.mpg"),new String("soccer, ball"));
//file:videos/NeverTrustAWoman.mpg	
indoor.put(new String("file:videos/NeverTrustAWoman.mpg"),new String("stadium, ligths, white"));
people.put(new String("file:videos/NeverTrustAWoman.mpg"),new String("adults")); 
sport.put(new String("file:videos/NeverTrustAWoman.mpg"),new String("wrestling"));
//file:videos/tennisankleinjury.mpg	
people.put(new String("file:videos/tennisankleinjury.mpg"),new String("adults")); 
sport.put(new String(""),new String("tennis"));
//file:videos/polevault.mpg
indoor.put(new String("file:videos/polevault.mpg"),new String("stadium, game, lights"));
people.put(new String("file:videos/polevault.mpg"),new String("adults, players"));
sport.put(new String("file:videos/polevault.mpg"),new String("jump, pole"));
//file:videos/referee.mpg
outdoor.put(new String("file:videos/referee.mpg"),new String("fields, green"));
people.put(new String("file:videos/referee.mpg"),new String("adult")); 
sport.put(new String("file:videos/referee.mpg"),new String("soccer, ball"));
//file:videos/fstupid_soccer_goal.mpeg	
outdoor.put(new String("file:videos/fstupid_soccer_goal.mpeg"),new String("fields, green"));
people.put(new String("file:videos/fstupid_soccer_goal.mpeg"),new String("adult, players")); 
sport.put(new String("file:videos/fstupid_soccer_goal.mpeg"),new String("soccer, ball"));
text.put(new String("file:videos/fstupid_soccer_goal.mpeg"), new String("ad, website"));

//file:videos/funny-football_fight.mpeg
outdoor.put(new String("file:videos/funny-football_fight.mpeg"),new String("fields, green"));
people.put(new String("file:videos/funny-football_fight.mpeg"),new String("adult, players")); 
sport.put(new String("file:videos/funny-football_fight.mpeg"),new String("soccer, ball"));
text.put(new String("file:videos/funny-football_fight.mpeg"), new String("ad, website"));

String yellow = "http://www.jakesjokes.com/gallery/albums/userpics/10001/yellowcard.mpg"; 
outdoor.put(new String(yellow),new String("fields, green"));
people.put(new String(yellow),new String("adult, players")); 
sport.put(new String(yellow),new String("soccer, ball"));
}
/**
 * For all videos in database adds the description of video 
 * contents to HashMap describe, which is append to the 
 * video metadata.
 */
public void addDescription(){
	
	String spc = "  "; 
    String str = ""; 
//  bailey.mpg: little barking dog
	
	str= String.format("%sdog, indoor, funny, %sKB\n", spc, new String("4583"));
	describe.put(new String("file:videos/bailey.mpg"), new String(str));
    
   //apina.mpg: pissing monkey
	
	str= String.format("%smonkey, outdoor, funny, %sKB\n", spc, new String("937"));
	describe.put(new String("file:videos/apina.mpg"), new String(str)); 
	
	//elephant.mpeg: parade
	
	str= String.format("%selephant, park, parade," +
			"\n%sadult, child, funny, %sKB\n", spc,spc, new String("1611"));
	describe.put(new String("file:videos/elephant.mpeg"),new String(str));
	
	//file:videos/Beerlives.mpg: drink beer or die

	str= String.format("%sadult, mountain, road," +
			"\n%sbeer, advertisement, tragic, %sKB",spc, spc,new String("3285"));
	describe.put(new String("file:videos/Beerlives.mpg"), new String(str));
	
//file:videos/alien.mpg
	str= String.format("%sred, orange, camera motion, Mars, %sKB", spc, new String("358"));
	describe.put(new String("file:videos/alien.mpg"), new String(str));
	
	
//	file:videos/barney-origin.mov
	str= String.format("%sblack-white, hitler, cartoon,%sKB", spc,new String("393"));
	describe.put(new String("file:videos/barney-origin.mov"), new String(str));
	
	
//	file:videos/bluescreen2.mov
	str= String.format("%sblue, red, sea, fish,cartoon, %sKB", spc, new String("1383"));
	describe.put(new String("file:videos/bluescreen2.mov"), new String(str));

//	file:videos/antzeladimitriou.mpg
	
	str= String.format("%sadult, tree, text, %sKB",spc, new String("833"));
	describe.put(new String("file:videos/antzeladimitriou.mpg"), new String(str));
	
//	file:videos/millertime.mpg	

	str= String.format("%sdog, adult, beer,ad, %sKB" , spc, new String("1905"));
	describe.put(new String("file:videos/millertime.mpg"),new String(str));
	
//	file:videos/porky.mov
	str= String.format("%sblack-white, comic, cartoon,%sKB", spc, new String("1108"));
	describe.put(new String("file:videos/porky.mov"), new String(str));
	
	
//file:videos/faceburn.mpg	
	
	str= String.format("%sadult,night, fire, light, %sKB",spc, new String("395"));
	describe.put(new String("file:videos/faceburn.mpg"), new String(str));
	
	//file:videos/aliensong.mpeg
	str= String.format("%scolor, comic, text,cartoon,%sKB", spc, new String("3127"));
	describe.put(new String("file:videos/aliensong.mpeg"), new String(str));
	
//	file:videos/babythrowup.mpeg
	
	str= String.format("%sbedroom, adult, child,ad,funny, %sKB" , spc, new String("553"));
	describe.put(new String("file:videos/babythrowup.mpeg"),new String(str));
	
//	file:videos/bebekarate.mpeg
	
	str= String.format("%shospital, adult, child,ad,funny, %sKB" , spc, new String("537"));
	describe.put(new String("file:videos/bebekarate.mpeg"),new String(str));
	
//	file:videos/bicycle.mpg
	
	str= String.format("%sroad, country, trees, adult, %sKB\n", spc, new String("703"));
	describe.put(new String("file:videos/bicycle.mpg"),new String(str));
	
	//file:videos/bigbend.mpg	
	
	str= String.format("%scountry, ocean, landscape,%smountain %sKB\n", spc,spc, new String("352"));
	describe.put(new String("file:videos/bicycle.mpg"),new String(str));
	
	//file:videos/bikeoops.mpeg
	
	str= String.format("%sroad, country, trees, adult,\n"+ "%s" +
			"motorbikes, cars, %sKB\n", spc, spc, new String("455"));
	describe.put(new String("file:videos/bikeoops.mpeg"),new String(str));
	
	//file:videos/broken_ribs.mpg

	str= String.format("%spark, trees, adult,bicycle,%sjump,tragic,%sKB\n", spc,spc, new String("3260"));
	describe.put(new String("file:videos/broken_ribs.mpg"),new String(str));
	
	//file:videos/BudDancing.mpge: no sucess to put in database

	str= String.format("%sparty, disco, adult,%sKB\n", spc, new String("2488"));
	describe.put(new String("file:videos/BudDancing.mpge"),new String(str));
	
	//file:videos/c431v.mpg	

	str= String.format("%scountry, mountain, dry-land, adult,%sKB\n", spc, new String("1051"));
	describe.put(new String("file:videos/c431v.mpg"),new String(str));
	
	//file:videos/c427v.mpg

	str= String.format("%swater, dish, adult,%sKB\n", spc, new String("787"));
	describe.put(new String("file:videos/c427v.mpg"),new String(str));
	
	//file:videos/c450v.mpg

	str= String.format("%scountry, mountain, train,%sdry-land, adult,%sKB\n", spc,spc, new String("843"));
	describe.put(new String("file:videos/c450v.mpg"),new String(str));
	
	//file:videos/catfight.mpeg

	str= String.format("%scat, room, ad, %sKB" , spc, new String("1730"));
	describe.put(new String("file:videos/millertime.mpg"),new String(str));
	
	//file:videos/elvis-movie.mov
	
	str= String.format("%scountry, mountain, car,%sroad, adult,%sKB\n", spc,spc, new String("2101"));
	describe.put(new String("file:videos/elvis-movie.mov"),new String(str));
	
	//file:videos/fire_zone_51.mov

	str= String.format("%shouse,night, light,fire,%sgame, adult,%sKB\n", spc,spc, new String("2207"));
	describe.put(new String("file:videos/fire_zone_51.mov"),new String(str));
	
	//file:videos/flippingcar.mpeg

	str= String.format("%scountry, car,road,%sgreen,tragic,%sKB\n", spc,spc, new String("917"));
	describe.put(new String("file:videos/flippingcar.mpeg"),new String(str));
	
	//file:videos/fordka-bird.mpg
	str= String.format("%soutdoor,house,tree, car,%sroad, bird,ad, %sKB\n", spc,spc, new String("1071"));
	describe.put(new String("file:videos/fordka-bird.mpg"), new String(str));
	
	//file:videos/fordka-cat.mpg	
	str= String.format("%soutdoor,house,tree, flowers,%scar,road, cat,ad,%sKB\n", spc,spc, new String("995"));
	describe.put(new String("file:videos/fordka-cat.mpg"), new String(str));
	
	//file:videos/frens2.mpeg
	str= String.format("%scountry,grass, adult,%smonkey,fun,%sKB\n", spc,spc, new String("2617"));
	describe.put(new String("file:videos/frens2.mpeg"), new String(str));
	
//	file:videos/fs14ss.mpeg
	str= String.format("%socean,blue, adult, sky,%sairplane,boat,%sKB\n", spc,spc, new String("1320"));
	describe.put(new String("file:videos/fs14ss.mpeg"), new String(str));
	
	//file:videos/hitbycars.mpeg	
	str= String.format("%scity,road, people, blue,%scar,tragic,%sKB\n", spc,spc, new String("596"));
	describe.put(new String("file:videos/hitbycars.mpeg"), new String(str));
	
//	file:videos/Javelin.mpg	
	str= String.format("%sstadium,outdoor, people,%ssport, ad,%sKB\n", spc,spc, new String("765"));
	describe.put(new String("file:videos/Javelin.mpg"), new String(str));
	
//	file:videos/jet.mpg
	str= String.format("%sblue,brown,camera-motion,%sKB\n", spc, new String("809"));
	describe.put(new String("file:videos/jet.mpg"), new String(str));
	
//	file:videos/Liegerad_newyork.mpg
	str= String.format("%scity,NY, bikes,cars, roads,%sbridge, suburbs,%sKB\n", spc,spc, new String("5259"));
	describe.put(new String("file:videos/Liegerad_newyork.mpg"),new String(str));
	
//	file:videos/malcolmsballs.mpg	
	str= String.format("%sindoor,white, adult,game,%sballs,ad,%sKB\n", spc,spc, new String("3038"));
	describe.put(new String("file:videos/malcolmsballs.mpg"),new String(str));
	
//	file:videos/noboss1.mpeg	
	str= String.format("%sindoor,white,blue, adult,game,%soffice, rowing,bike, ad,%sKB\n", spc,spc, new String("741"));
	describe.put(new String("file:videos/noboss1.mpeg"),new String(str));
//	file:videos/noboss2.mpeg
	str= String.format("%sindoor,white, adult,game,%soffice, jumping,running,%sKB\n", spc,spc, new String("637"));
	describe.put(new String("file:videos/noboss2.mpeg"),new String(str));
	
//	file:videos/p342v.mpg	
	str= String.format("%sturtle, grey,light,%sKB\n", spc, new String("627"));
	describe.put(new String("file:videos/p342v.mpg"),new String(str));
	
//	file:videos/p546v.mpg	
	str= String.format("%sserpent, grey,rocks,light,%sKB\n", spc, new String("531"));
	describe.put(new String("file:videos/p546.mpg"),new String(str));
	
//	file:videos/p827v.mpg
	str= String.format("%scoyote, grey,rocks,%sbushes, light,%sKB\n", spc,spc, new String("436"));
	describe.put(new String("file:videos/p827v.mpg"),new String(str));
	
//	file:videos/parkinglotkiss.mpeg	
	str= String.format("%sgarage, cars,adults,%swebsite, text,%sKB\n", spc,spc, new String("943"));
	describe.put(new String("file:videos/parkinglotkiss.mpeg"),new String(str));
	
//	file:videos/Photocopies.mpeg	
	str= String.format("%soffice, white,adults,%sfood, text,%sKB\n", spc,spc, new String("642"));
	describe.put(new String("file:videos/Photocopies.mpeg"),new String(str));
	
	//file:videos/pianoplayer1.mpeg	
	str= String.format("%sinterior, adult,dance,%sKB\n", spc, new String("485"));
	describe.put(new String("file:videos/pianoplayer1.mpeg"),new String(str));
	
//	file:videos/pinata.mpeg
	str= String.format("%sinterior, adult,child,game,%sKB\n", spc, new String("1433"));
	describe.put(new String("file:videos/pinata.mpeg"),new String(str));
	
//	file:videos/pole.mpeg
	str= String.format("%sstadium, adult,sport,%slights, pole,jump,%sKB\n", spc,spc, new String("1505"));
	describe.put(new String("file:videos/pole.mpeg"),new String(str));
	
//	file:videos/pullup.mpeg	
	str= String.format("%sadult,road,city,house,%scars, bike,jump,tree,%sKB\n", spc,spc, new String("802"));
	describe.put(new String("file:videos/pullup.mpeg"),new String(str));
	
//	file:videos/rally.mpeg	
	str= String.format("%sadult,road,country,%scar, race,%sKB\n", spc,spc, new String("1000"));
	describe.put(new String("file:videos/rally.mpeg"),new String(str));
	
//	file:videos/raversretarded.mpg	
	str= String.format("%sadult,road,tree,dance,%syellow leaves, car,%sKB\n", spc,spc, new String("1283"));
	describe.put(new String("file:videos/raversretarded.mpg"),new String(str));
	
//	file:videos/roll.mpg
	str= String.format("%sadult,night,white ligths,%stext,house,game,%sKB\n", spc,spc, new String("1446"));
	describe.put(new String("file:videos/roll.mpg"),new String(str));
	
//	file:videos/sage_beamingpoi.mpg	
	str= String.format("%sadult,night,orange ligths,%stext,house, game,%sKB\n", spc,spc, new String("1415"));
	describe.put(new String("file:videos/sage_beamingpoi.mpg"),new String(str));
	
//file:videos/senator.mpeg	
	str= String.format("%sadult,interiors,TV broadcast,%stext,flags,%sKB\n", spc,spc, new String("549"));
	describe.put(new String("file:videos/senator.mpeg"),new String(str));

//	file:videos/sharkbite.mpe
	str= String.format("%sadult,boat,sky,ocean,blue,%stext,shark,%sKB\n", spc,spc, new String("339"));
	describe.put(new String("file:videos/sharkbite.mpe"),new String(str));
	
//	file:videos/peli1.mov
	str= String.format("%sblue, red, sea, fish,cartoon, %sKB", spc, new String("597"));
	describe.put(new String("file:videos/peli1.mov"), new String(str));

//	file:videos/sports.mpeg	
	str= String.format("%scountry, grass, river, road, bikes,%srace, bridge,splash,%sadult,fun,%sKB", spc,spc,spc, new String("1811"));
	describe.put(new String("file:videos/sports.mpeg"), new String(str));
	
//	file:videos/t38x1.mpg	
	str= String.format("%scountry, grass, tree, house, bikes,%srecreation, adult,%sKB", spc,spc, new String("421"));
	describe.put(new String("file:videos/t38x1.mpg"), new String(str));
	
//	file:videos/t38x2.mpg	
	str= String.format("%scountry, grass, tree, house, bikes,%srecreation, adult,%sKB", spc,spc, new String("421"));
	describe.put(new String("file:videos/t38x2.mpg"), new String(str));
		
//	file:videos/vibraspot.mpg
	str= String.format("%sbushes,cafe, adult, boops,%srecreation, ad,%sKB", spc,spc, new String("2097"));
	describe.put(new String("file:videos/vibraspot.mpg"), new String(str));
	
//	file:videos/waterski.mpg			
	str= String.format("%sadult,child,recreation,%sbeach,sand,ocean,blue,boat,%sKB", spc,spc, new String("408"));
	describe.put(new String("file:videos/waterski.mpg"), new String(str));
	
//	file:videos/wedding.mpeg	
	str= String.format("%sadult,party,house,%sbedroon,black-white,car,text,%sKB", spc,spc, new String("1587"));
	describe.put(new String("file:videos/wedding.mpeg"), new String(str));
	
//	file:videos/bellyflop.mpg	
	str= String.format("%sadult,swimming pool, stadium,%swater,blue,%sKB", spc,spc, new String("563"));
	describe.put(new String("file:videos/bellyflop.mpg"), new String(str));
	
//	file:videos/c448v.mpg
	str= String.format("%scountry,desert, rail tracks,%sKB", spc, new String("435"));
	describe.put(new String("file:videos/c448v.mpg"), new String(str));
	
//	file:videos/c449v.mpg	
	str= String.format("%scountry,desert, rail tracks,%sblack, tunel, light%sKB", spc,spc, new String("532"));
	describe.put(new String("file:videos/c449v.mpg"), new String(str));

//	file:videos/karatechimp.mpeg
	str= String.format("%sgym, ligth, adult, chimp,karate,%sKB", spc,new String("2997"));
	describe.put(new String("file:videos/karatechimp.mpeg"), new String(str));
	
//	file:videos/kitty-gore.mpg	
	str= String.format("%scruel, butcher, adult, cat,food,%sKB", spc,new String("3871"));
	describe.put(new String("file:videos/kitty-gore.mpg"), new String(str));
	
//	file:videos/bikercollision.mpg	
	str= String.format("%saccident, adult, car,%smotorbike,road,house,%sKB", spc,spc,new String("3225"));
	describe.put(new String("file:videos/bikercollision.mpg"), new String(str));
	
//	file:videos/harriercrashing.mpg		
	
	str= String.format("%socean, beach, sky, blue, %sadult, parashut, airplane,%sKB", spc,spc,new String("2302"));
	describe.put(new String("file:videos/harriercrashing.mpg"), new String(str));
	
//	file:videos/KFee_Auto.mpg	
	str= String.format("%sgreen, trees, woods,%sroad, car, sadult, ad,%sKB", spc,spc,new String("3465"));
	describe.put(new String("file:videos/KFee_Auto.mpg"), new String(str));
	
//	file:videos/snakenosemouth.mpeg	
	str= String.format("%sbright, adult, snake,%sKB", spc,new String("1685"));
	describe.put(new String("file:videos/snakenosemouth.mpeg"), new String(str));
	
//	file:videos/piewibdentiste.mpeg	
	str= String.format("%sclouds,ocean, adult, glass,ad,%sKB", spc,new String("1071"));
	describe.put(new String("file:videos/piewibdentiste.mpeg"), new String(str));
	
//	file:videos/pinguinsinks.mpg	
	str= String.format("%swhite,ocean, ice, snow, pinguins,%sKB", spc,new String("577"));
	describe.put(new String("file:videos/pinguinsinks.mpg"), new String(str));
	
//	file:videos/foot-theiko2.mpeg	
	str= String.format("%sgreen,stadium, adult, players, soccer,%sKB", spc,new String("2044"));
	describe.put(new String("file:videos/foot-theiko2.mpeg"), new String(str));
	
//	file:videos/EarlyC.mpeg	
	str= String.format("%shouse,TV, video, porno,%sKB", spc,new String("801"));
	describe.put(new String("file:videos/EarlyC.mpeg"), new String(str));
	
//"file:videos/sumo.mpeg"
	str= String.format("%sinterior,sport, adults, wrestling,%sKB", spc,new String("1267"));
	describe.put(new String("file:videos/sumo.mpeg"), new String(str));

	//file:videos/blowupraft.mpeg	
	str= String.format("%soutdoor,raft, adult, child,play,%sKB", spc,new String("1075"));
	describe.put(new String("file:videos/blowupraft.mpeg"), new String(str));
	
	//file:videos/bbquiet.mpg
	str= String.format("%sindoor,house,car, adults,%sKB", spc,new String("1509"));
	describe.put(new String("file:videos/bbquiet.mpg"), new String(str));
	
	//file:videos/acc - f1pits-verstapp.mpeg	354	14.16
	str= String.format("%soutdoor,stadium,racing-cars, %sadults,fire, smoke, accident,%sKB",spc, spc,new String("1132"));
	describe.put(new String("file:videos/acc - f1pits-verstapp.mpeg"), new String(str));
//	file:videos/footySmart-theiko.mpg		
	str= String.format("%sblack-white,stadium, adults, players, soccer,%sKB", spc,new String("2254"));
	describe.put(new String("file:videos/footySmart-theiko.mpg"), new String(str));
	
//	file:videos/football-ayhana.mov	
	str= String.format("%sgreen,stadium, adults, players, soccer,%sKB", spc,new String("322"));
	describe.put(new String("file:videos/football-ayhana.mov"), new String(str));
	
//	file:videos/gym2.mpeg
	str= String.format("%sblue carpet,gym, adults,players ,leap jump,%sKB", spc,new String("937"));
	describe.put(new String("file:videos/gym2.mpeg"), new String(str));
	
//	file:videos/kick-ghpedo.mpg	
	str= String.format("%sstadium, adults,dirty-game,%sKB", spc,new String("626"));
	describe.put(new String("file:videos/kick-ghpedo.mpg"), new String(str));
	
//	file:videos/gym1.mpeg
	str= String.format("%sred carpet,gym, adults,players ,leap jump,%sKB", spc,new String("505"));
	describe.put(new String("file:videos/gym1.mpeg"), new String(str));
	
//	file:videos/acc - pooljump.mpeg	
	str= String.format("%strees, swimming-pool, water,adult,jump ,recreation,%sKB", spc,new String("653"));
	describe.put(new String("file:videos/acc - pooljump.mpeg"), new String(str));
//	file:videos/ben5.mpg
	str= String.format("%sadult,night,orange ligths,%stext,game,%sKB\n", spc,spc, new String("1803"));
	describe.put(new String("file:videos/ben5.mpg"),new String(str));
//	file:videos/sage_dbl_staff.mpg	
	str= String.format("%sadult,night,ligths,house,%stext,game,%sKB\n", spc,spc, new String("952"));
	describe.put(new String("file:videos/sage_dbl_staff.mpg"),new String(str));
//	file:videos/t38x3.mpg	
	str= String.format("%scountry, grass, tree, house, bikes,%srecreation, adult,%sKB", spc,spc, new String("886"));
	describe.put(new String("file:videos/t38x3.mpg"), new String(str));
//	file:videos/wyms1.mpg		
	str= String.format("%scountry, grass, tree, house, bikes,%srecreation, adult,%sKB", spc,spc, new String("886"));
	describe.put(new String("file:videos/wyms1.mpg"), new String(str));
	
//	file:videos/wyms2.mpg		
	str= String.format("%scountry, grass, tree, house, bikes,%srecreation, adult,%sKB", spc,spc, new String("316"));
	describe.put(new String("file:videos/wyms2.mpg"), new String(str));
	
//	file:videos/wyms3.mpg		
	str= String.format("%scountry, grass, tree, house, bikes,%srecreation, adult,%sKB", spc,spc, new String("356"));
	describe.put(new String("file:videos/wyms3.mpg"), new String(str));
	//file:videos/wyms4.mpg	
	str= String.format("%scountry, grass, tree, house, bikes,%srecreation, adult,%sKB", spc,spc, new String("1520"));
	describe.put(new String("file:videos/wyms4.mpg"), new String(str));
//	file:videos/c430v.mpg	
	str= String.format("%scountry,desert, adults,%sKB", spc,new String("524"));
	describe.put(new String("file:videos/c430v.mpg"), new String(str));
//	file:videos/c432v.mpg	
	str= String.format("%scountry,desert, adults,%sKB", spc,new String("1500"));
	describe.put(new String("file:videos/c432v.mpg"), new String(str));
//	file:videos/p346v.mpg
	str= String.format("%slizard, grey,rocks,%sbushes, light,%sKB\n", spc,spc, new String("859"));
	describe.put(new String("file:videos/p346v.mpg"),new String(str));
//	file:videos/c436v.mpg	
	str= String.format("%scountry,desert, adults,%sKB", spc, new String("692"));
	describe.put(new String("file:videos/c436v.mpg"), new String(str));	
	//file:videos/acc - almost crash.mpeg	
	str= String.format("%sblue, rain,adult,cars,text,%sKB", spc,new String("305"));
	describe.put(new String("file:videos/acc - almost crash.mpeg"), new String(str));	
	//file:videos/c425v.mpg	
	str= String.format("%sblue,adults,water,dish,%sKB", spc,new String("443"));
	describe.put(new String("file:videos/c425v.mpg"), new String(str));	
	//file:videos/fakeplaybehind.mpeg
	str= String.format("%sgreen,adult,ball,soccer,%sKB", spc,new String("2083"));
	describe.put(new String("file:videos/fakeplaybehind.mpeg"), new String(str));	
//	file:videos/NeverTrustAWoman.mpg
	str= String.format("%swhite,adulta,sport,wrestling,%sKB", spc,new String("896"));
	describe.put(new String("file:videos/NeverTrustAWoman.mpg"), new String(str));
	//file:videos/tennisankleinjury.mpg	
	str= String.format("%sadult,sport,tennis,%sKB", spc,new String("325"));
	describe.put(new String("file:videos/tennisankleinjury.mpg"), new String(str));
	
//	file:videos/beardsley.mpeg	
	str= String.format("%sgreen,adult,ball,soccer,%sKB", spc,new String("1342"));
	describe.put(new String("file:videos/beardsley.mpeg"), new String(str));	
	//file:videos/maradonaturn.mpeg	
	str= String.format("%sgreen,adult,ball,soccer,%sKB", spc,new String("2321"));
	describe.put(new String("file:videos/maradonaturn.mpeg"), new String(str));	
	//file:videos/soccerinjury.mpg	
	str= String.format("%sstadium,adult,ball,soccer,%sKB", spc,new String("2029"));
	describe.put(new String("file:videos/soccerinjury.mpg"), new String(str));	
	//file:videos/polevault.mpg
	str= String.format("%sstadium, adult,sport,%slights, pole,jump,%sKB\n", spc,spc, new String("1505"));
	describe.put(new String("file:videos/polevault.mpg"), new String(str));	
	//file:videos/referee.mpg
	str= String.format("%sstadium, adult,soccer,%sKB\n", spc, new String("713"));
	describe.put(new String("file:videos/referee.mpg"), new String(str));
	//file:videos/fstupid_soccer_goal.mpeg
	str= String.format("%sstadium, players,soccer,%sKB\n", spc, new String("2044"));
	describe.put(new String("file:videos/fstupid_soccer_goal.mpeg"), new String(str));
//	file:videos/funny-football_fight.mpeg
	str= String.format("%sstadium, players,soccer,%sKB\n", spc, new String("1841"));
	describe.put(new String("file:videos/funny-football_fight.mpeg"), new String(str));
	
//	"http://www.jakesjokes.com/gallery/albums/userpics/10001/yellowcard.mpg"
	str= String.format("%sstadium, players,soccer,%sKB\n", spc, new String("NA"));
	describe.put(new String("http://www.jakesjokes.com/gallery/albums/userpics/10001/yellowcard.mpg"), new String(str));
}
//for testing
public static void main(String[] args) {
	
	 VideoDescribe des = new VideoDescribe(false);
	 mLog.info(des.getSimilar("file:videos/elvis-movie.mov"));
	 mLog.info(des.getDescribeTag());
	 
}
}



