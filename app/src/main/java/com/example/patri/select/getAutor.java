package com.example.patri.select;

import android.os.AsyncTask;

import com.example.patri.db.DBVerbindung;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by patri on 20.07.2017.
 */

public class getAutor {
    private ResultSet result;
    private Integer i;
    private ArrayList autorenlist = new ArrayList();

    public ArrayList autorenlisteAbrufen() throws SQLException, ExecutionException, InterruptedException {
        i = 0;
        autorenlist.add(0,"Bitte Autor auswählen");


        result = new MyTask().execute().get();
        return autorenlist;
    }

    private class MyTask extends AsyncTask<Void, Void, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... params) {
            ResultSet rsa;
            rsa = null;
            DBVerbindung dv = new DBVerbindung();
            dv.oeffneDB();
            String select;
            select = "Select * from autoren";
            rsa = dv.lesen(select);
            try {
                while (rsa.next()) {
                    autorenlist.add(i, rsa.getString(2) + " " + rsa.getString(3));
                    i++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            result = rsa;
            return rsa;

        }


    }
}
