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

public class GetSerie {
    private ResultSet result;
    private Integer i;
    private ArrayList serienlist = new ArrayList();

    public ArrayList serienlistAbrufen() throws SQLException, ExecutionException, InterruptedException {
        i = 0;
        serienlist.add(0,"Bitte Serientitel eingeben");


        result = new MyTask().execute().get();
        return serienlist;
    }

    private class MyTask extends AsyncTask<Void, Void, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... params) {
            ResultSet rsa;
            rsa = null;
            DBVerbindung dv = new DBVerbindung();
            dv.oeffneDB();
            String select;
            select = "Select * from serie";
            rsa = dv.lesen(select);
            try {
                while (rsa.next()) {
                    serienlist.add(i, rsa.getString(2));
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
