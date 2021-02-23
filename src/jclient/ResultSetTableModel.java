/**
 * File		:	ResultSetTableModel.java
 * 
 * Author	:	Elena Villalón
 * 
 * Contents	:	Input is ResultSet rs of a query for a table in databas
 *              Ges the database metadata, and counts the rwos, columns
 *              Obtains columns names and specify elements of the query.          
 *                   
 */
package jclient;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.table.*;

public class ResultSetTableModel extends AbstractTableModel{
	static final long serialVersionUID = 42L;
	ResultSet rs;
	ResultSetMetaData rsmd;
	
	public ResultSetTableModel(ResultSet ares){
		rs= ares;
		try{
			rsmd = rs.getMetaData(); 
		}catch(SQLException e){
			e.printStackTrace(); 
		}
		
	}
	public ResultSet getRs(){
		return rs; 
	}
	public ResultSetMetaData getRsmd(){
		return rsmd; 
	}
	
	
	public int getColumnCount()
	{
		try{
			return rsmd.getColumnCount();
		}
		catch(SQLException e){
			e.printStackTrace(); 
			return 0; 
		}
		
	}
	public int getRowCount()
	{
		try{
			rs.last();
			return rs.getRow();
		}
		catch(SQLException e){
			e.printStackTrace(); 
			return 0; 
		}
		
	}
	public Object getValueAt(int r, int c){
		try{
			rs.absolute(r+1);
			
			return rs.getObject(c +1 );
		}catch(SQLException e){
			e.printStackTrace(); 
			return null; 
		}
	}
	public String getColumnName(int c){
		try{
			return rsmd.getColumnName(c+1);
		}catch(SQLException e){
			e.printStackTrace(); 
			return "";
		}
	}
	
}
