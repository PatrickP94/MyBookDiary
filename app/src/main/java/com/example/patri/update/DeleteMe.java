package com.example.patri.update;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.example.patri.db.DBVerbindung;
import com.example.patri.db.DataBaseHelper;

import java.util.concurrent.ExecutionException;

/**
 * Created by patri on 06.08.2017.
 */

public class DeleteMe {
    String isbn;
    DataBaseHelper mDatabase;
    Boolean verbindungok, erfolg;
    Context context;


    public Boolean DeleteMe(Context context, String isbn, DataBaseHelper dbh, Boolean verbindungok) throws ExecutionException, InterruptedException {
        this.isbn = isbn;
        mDatabase = dbh;
        this.context = context;
        this.verbindungok = verbindungok;

        erfolg = new deletefromdb().execute().get();
        erfolg = true;
        return erfolg;

    }

    private class deletefromdb extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean bol = null;
            String delete;
            delete = "Delete from neue_bücher Where isbn='" + isbn + "';";
            if (verbindungok) {
                DBVerbindung dv = new DBVerbindung();
                dv.oeffneDB();

                dv.aendern(delete);
            } else {
                delete = "Delete from neue_bücher Where _id='" + isbn + "';";
                mDatabase.aendern(delete);
            }
            delete = "Delete from bücher_gelesen Where isbn='" + isbn + "';";
            if (verbindungok) {
                DBVerbindung dv = new DBVerbindung();
                dv.oeffneDB();

                dv.aendern(delete);
            } else {
                delete = "Delete from bücher_gelesen Where _id='" + isbn + "';";
                mDatabase.aendern(delete);
            }

            bol = true;

            return bol;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            AlertDialog.Builder myAlert2 = new AlertDialog.Builder(context);
            myAlert2.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            myAlert2.setMessage("Buch erfolgreich gelöscht").create();
            AlertDialog alert = myAlert2.create();

            alert.show();


        }


    }


}
