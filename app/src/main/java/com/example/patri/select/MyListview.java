package com.example.patri.select;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.patri.db.DBVerbindung;
import com.example.patri.db.DataBaseHelper;
import com.example.patri.mybookdiary.MainActivity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by patri on 29.07.2017.
 */

public class MyListview extends Activity {
    String table;
    ArrayList books = new ArrayList();
    Integer i;
    ResultSet result;
    private MainActivity ma = new MainActivity();
    private DataBaseHelper mDatabase;
    private Boolean verbindungok;

    public ArrayList booklist(String table4, DataBaseHelper dbh, Boolean verbindungok) throws SQLException, ExecutionException, InterruptedException {
        i = 0;
        this.verbindungok = verbindungok;
        mDatabase = dbh;
        switch (table4) {
            case "gelesen":
                table = "bücher_gelesen";
                break;
            case "wishlist":
                table = "neue_bücher";
                break;
        }

        result = new MyListview.MyTask().execute().get();
        return books;
    }

    private class MyTask extends AsyncTask<Void, Void, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... params) {
            ResultSet rsa;
            rsa = null;
            String select;
            select = "Select t1.isbn, t1.titel, Concat(autoren.vorname,' ', autoren.nachname) From " + table + " as t1 " +
                    " inner join autoren on t1.autor = autoren.idAutoren inner join kategorie on t1.kategorie = kategorie.kategorieId inner join serie on serie.serienId = t1.serienid " +
                    "order by t1.titel;";
            Log.i("test", select);
            if (verbindungok == true) {
                DBVerbindung dv = new DBVerbindung();
                dv.oeffneDB();
                rsa = dv.lesen(select);
                try {
                    while (rsa.next()) {
                        books.add(i, rsa.getString(1) + " - " + rsa.getString(2) + " : " + rsa.getString(3));
                        i++;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                select = "Select " + table + "._id as isbn, " + table + ".titel, autoren.vorname || ' ' || autoren.nachname From " + table +
                        " inner join autoren on " + table + ".autor = autoren._id inner join kategorie on " + table + ".kategorie = kategorie._id inner join serie on serie._id = " + table + ".serienid " +
                        "order by " + table + ".titel;";
                Log.i("select", select);
                Cursor crs = mDatabase.readData(select);
                crs.moveToFirst();
                try {
                    while (crs.moveToNext()) {
                        books.add(i, crs.getString(0) + " - " + crs.getString(1) + " : " + crs.getString(2));
                        i++;
                    }

                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }
            result = rsa;
            return rsa;


        }


    }


}
