/**
 * File		:	JMApp.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Open a child process for JMStudio
 *              
 *               May 28, 2007
 *                     
 */
package jclient;

public class JMApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
	  try{
		  Runtime runtime = Runtime.getRuntime();
          Process proc = runtime.exec("java JMStudio"); 
	  }catch(Exception err){}

	}

}
