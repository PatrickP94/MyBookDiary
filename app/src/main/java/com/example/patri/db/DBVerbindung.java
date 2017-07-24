package com.example.patri.db;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class DBVerbindung {

	Connection mdbBistro = null;  // Verbindungsobjekt
	Statement stmtSQL = null; // Hier wird das "SQL-Statement-Objekt" deklariert
	ResultSet rs;
	
	 public void oeffneDB() {
	      try {
	            Class.forName("com.mysql.jdbc.Driver").newInstance();
	            //mdbBistro = DriverManager.getConnection("jdbc:mysql://Diskstation_01/bücher","Patrick","pa1906pr");
              mdbBistro = DriverManager.getConnection("jdbc:mysql://Diskstation_01/katrin_books","Patrick","pa1906pr");
              stmtSQL = mdbBistro.createStatement();
	            Log.i("DBVerbindung", "Erfolgreiche Datenbankverbindung hergestellt");
	      }
	      catch (ClassNotFoundException e) {
	            System.err.println(e);
	            System.out.println("Fehler bei ClassNotFoundException");
			  	Log.e("DBVerbindung", e.toString());
	      }
	      catch (SQLException e) {
	            System.err.println(e);
	            System.out.println("Fehler bei SQLException");
			    Log.e("DBVerbindung", e.toString());
	            System.out.println(e.toString());
	      } catch (InstantiationException e) {
			  e.printStackTrace();
		  } catch (IllegalAccessException e) {
			  e.printStackTrace();
		  }
	 }
	 public void schliesseDB() {
		  try
		  {
			rs.close();
			stmtSQL.close();
			stmtSQL=null;
			mdbBistro.close();
			mdbBistro=null;
			Log.i("Close","DBClose erfolfgreich");
		  }
		  catch (SQLException err)   {
			System.err.println(err);
			Log.e("DBVerbindung", err.toString());
		  }
	    }
	 public ResultSet lesen(String sqlString) {
	      this.oeffneDB();
	      try {
	           rs = stmtSQL.executeQuery(sqlString);
			  Log.i("DBVerbindung", "Erfolgreicher Datenabruf");
	        } 
	      catch (SQLException e) {
	            e.printStackTrace();
	            System.out.println("Fehler beim Lesen der Datens�tze");
			  Log.e("DBVerbindung", e.toString());
	            rs = null;
	        }
	      catch (Exception e) 
	        {
	            System.out.println("allgemeiner Fehler");
				Log.e("DBVerbindung", e.toString());
	            rs = null;
	        }
	      return rs;
	    }
	    public void aendern (String sqlString) {
				this.oeffneDB();
				try {
					stmtSQL.executeUpdate(sqlString);
					Log.i("DBVerbindung", "Erfolgreicher Datenabruf");
				} catch (SQLException e) {
					Log.e("DBVerbindung", e.toString());
				}
			}
}