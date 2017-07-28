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

public class getKategorie {
    private ResultSet result;
    private Integer i;
    private ArrayList kategorieList = new ArrayList();

    public ArrayList kategorieListeabrufen() throws SQLException, ExecutionException, InterruptedException {
        i = 0;
        result = new MyTask().execute().get();
        kategorieList.add(0,"");
        return kategorieList;
    }

    private class MyTask extends AsyncTask<Void, Void, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... params) {
            ResultSet rsa;
            rsa = null;
            DBVerbindung dv = new DBVerbindung();
            dv.oeffneDB();
            String select;
            select = "Select * from kategorie";
            rsa = dv.lesen(select);
            try {
                while (rsa.next()) {
                    kategorieList.add(i, rsa.getString(2));
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
