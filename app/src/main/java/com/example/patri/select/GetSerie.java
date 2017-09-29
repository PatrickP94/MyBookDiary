package com.example.patri.select;

import android.database.Cursor;
import android.os.AsyncTask;

import com.example.patri.db.DBVerbindung;
import com.example.patri.db.DataBaseHelper;
import com.example.patri.mybookdiary.MainActivity;

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
    private MainActivity ma = new MainActivity();
    private Boolean verbindungok = ma.getVerbindungOk();
    private DataBaseHelper mDatabase;

    public ArrayList serienlistAbrufen(DataBaseHelper dbh) throws SQLException, ExecutionException, InterruptedException {
        i = 0;
        serienlist.add(0,"Bitte Serientitel eingeben");
        mDatabase = dbh;

        result = new MyTask().execute().get();
        return serienlist;
    }

    private class MyTask extends AsyncTask<Void, Void, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... params) {
            ResultSet rsa;
            rsa = null;
            String select;
            select = "Select * from serie";
            if (verbindungok == true) {
                DBVerbindung dv = new DBVerbindung();
                dv.oeffneDB();

                rsa = dv.lesen(select);
                try {
                    while (rsa.next()) {
                        serienlist.add(i, rsa.getString(2));
                        i++;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } else {
                Cursor crs = mDatabase.readData(select);
                crs.moveToFirst();
                try {
                    while (crs.moveToNext()) {
                        serienlist.add(i, crs.getString(1));
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
