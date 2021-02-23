/**
 * Commands.java
 * DESCRIPTION: Implemented remote operations 
 * are identified with the following commands 
  *ADDVIDEO= add new video url to database
  *SHOWLABEL= retrieve all videos categorized under a label
  *STATDATABASE= shows metadata about database of videos.
  *DESCRIBEVIDEO= retrieve metadata about a video
  *SIMILARVIDEO= gets the classification labels for a video,
  *              and lists all videos under those labels.
 * Elena Villalon
 * April, 19 2007
 */ 
package jremote;

public enum Commands {
	SHOWLABEL, STATDATABASE, SIMILARVIDEO,  DESCRIBEVIDEO, ADDVIDEO 

}
