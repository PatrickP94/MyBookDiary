package com.example.patri.update;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.patri.db.DBVerbindung;
import com.example.patri.db.DataBaseHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

/**
 * Created by patri on 05.08.2017.
 */

public class updateLeseliste {
    String serieID, autorID, kategorieID, tableadd, isbn, titel, seriennr;
    Integer countserie, bewert, seriegelesen;
    Boolean schongelesen, erfolg, verbindungok;
    Context context;
    Object rsa;
    private DataBaseHelper mDatabase;


    public Boolean updateLeseliste(String serieID, String autorID, String kategorieID, String tableadd, String isbn, String titel, Integer bewert, String seriennr, Integer countserie, Integer seriegelesen, Boolean schongelesen, Context context, DataBaseHelper dbh, Boolean verbindungok) throws ExecutionException, InterruptedException {
        this.serieID = serieID;
        this.autorID = autorID;
        this.kategorieID = kategorieID;
        this.tableadd = tableadd;
        this.serieID = serieID;
        this.isbn = isbn;
        this.titel = titel;
        this.bewert = bewert;
        this.seriennr = seriennr;
        this.countserie = countserie;
        this.seriegelesen = seriegelesen;
        this.schongelesen = schongelesen;
        if (schongelesen == null) {
            this.schongelesen = false;
        }
        this.context = context;
        mDatabase = dbh;
        this.verbindungok = verbindungok;
        if (verbindungok == true) {
            rsa = new updateLeseliste1().execute().get();
        } else {
            rsa = new updateLeseliste2().execute().get();
        }
        erfolg = true;
        return erfolg;

    }


    private class updateLeseliste1 extends AsyncTask<Void, Void, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... params) {
            ResultSet rsa;
            rsa = null;
            Cursor crs = null;
            DBVerbindung db = new DBVerbindung();


            String vname = "", nname = "", vorname = "", nachname = "";
            String serientitel;
            String insert, delete;
            insert = "";
            serientitel = serieID;
            //ul.updateLeseliste(isbn,titelTxt.getText().t2oString(),autorID, kategorieID, seriegelesen,serieID, serieNmb.getText().toString(),(int)bewertung.getRating()*2);
            if (serieID.toString().equals("Reihentitel") || serieID.toString().equals("keine Serie") || serieID.toString().equals("Reihe") || serieID.toString().equals("")) {
                serieID = "keine";
                serientitel = "";
            } else {
                rsa = db.lesen("Select serienID from serie where serientitel like '" + serieID + "';");
                try {
                    if (rsa.next()) {
                        try {
                            serieID = rsa.getString(1).toString();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }


                    } else {
                        String serie[] = serieID.split(" ");
                        Integer serien = serie.length - 1;
                        serieID = serie[0];
                        if (serien < 1 && serieID.length() > 6) {
                            serieID = serieID.substring(0, 6);
                        } else {
                            for (Integer i = 1; i < serien; i++) {
                                if (serie[i].length() > serieID.length()) {
                                    serieID = serie[1];
                                }
                            }
                            if (serieID.length() > 6) {
                                serieID = serieID.substring(0, 6);
                            }

                        }
                        rsa = db.lesen("Select serienID, Count(*) from serie where serientitel = '" + serieID + "';");
                        if (rsa.next()) {

                        } else {
                            serieID = countserie.toString();
                        }
                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                }


                if (serieID != "keine") {
                    Log.i("aender", "INSERT INTO serie Values ('" + serieID + "','" + serientitel + "','');");
                    db.aendern("INSERT INTO serie Values ('" + serieID + "','" + serientitel + "','" + 11 + "');");
                }
            }
            rsa = db.lesen("Select idAutoren, Concat(vorname, nachname) as name from autoren where CONCAT(vorname, ' ', nachname) like '" + autorID + "';");
            try {
                if (!rsa.next()) {
                    String name[] = autorID.split(" ");
                    Integer names = name.length - 1;
                    vorname = name[0];
                    nachname = "";
                    for (Integer i = 1; i <= names; i++) {
                        nachname = nachname + name[i];
                    }
                    vname = name[0];
                    nname = name[names];
                    if (nname.length() > 4) {
                        nname = nname.substring(0, 4);
                    }
                    if (vname.length() > 3) {
                        vname = vname.substring(0, 3);
                    }
                    autorID = nname + vname;
                    if (autorID != "Auto") {
                        db.aendern("INSERT INTO autoren Values('" + autorID + "','" + vorname + "','" + nachname + "');");
                    }
                } else {
                    try {
                        autorID = rsa.getString(1).toString();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.i("lesen", "Select kategorieID From kategorie Where name = '" + kategorieID + "' or kategorieID ='" + kategorieID + "';");
            rsa = db.lesen("Select kategorieID From kategorie Where name = '" + kategorieID + "' or kategorieID ='" + kategorieID + "';");
            try {
                while (rsa.next()) {
                    kategorieID = rsa.getString(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (tableadd == "neue_bücher") {


                insert = "Insert into neue_bücher Values('" + isbn + "','" + titel + "','" + autorID + "'," + seriegelesen + ",'" + serieID + "','" + seriennr + "','" + kategorieID + "','');";
                Log.i("insert", insert);
            } else if (tableadd == "bücher_gelesen") {
                insert = "Insert into bücher_gelesen Values('" + isbn + "','" + titel + "','" + autorID + "'," + seriegelesen + ",'" + serieID + "','" + seriennr + "','" + kategorieID + "'," + bewert + ",''" + ",'');";
            }

            Log.i("SQLBefehl", insert);
            db.aendern(insert);
            delete = "Delete from neue_bücher Where isbn='" + isbn + "';";
            try {
                db.aendern(delete);
            } catch (Exception e) {

            }
            erfolg = true;

            return rsa;
        }

        @Override
        protected void onPostExecute(ResultSet result) {

            AlertDialog.Builder myAlert2 = new AlertDialog.Builder(context);
            myAlert2.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            myAlert2.setMessage("Buch erfolgreich hinzugefügt").create();
            AlertDialog alert = myAlert2.create();

            alert.show();


        }


    }

    private class updateLeseliste2 extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            Cursor rsa = null;

            String vname = "", nname = "", vorname = "", nachname = "";
            String serientitel;
            String insert, delete;
            insert = "";
            serientitel = serieID;
            //ul.updateLeseliste(isbn,titelTxt.getText().t2oString(),autorID, kategorieID, seriegelesen,serieID, serieNmb.getText().toString(),(int)bewertung.getRating()*2);
            if (serieID.toString().equals("Reihentitel") || serieID.toString().equals("keine Serie") || serieID.toString().equals("Reihe") || serieID.toString().equals("")) {
                serieID = "keine";
                serientitel = "";
            } else {
                rsa = mDatabase.readData("Select _id from serie where serientitel like '" + serieID + "';");
                try {
                    if (rsa.moveToFirst()) {
                        try {
                            serieID = rsa.getString(0).toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        String serie[] = serieID.split(" ");
                        Integer serien = serie.length - 1;
                        serieID = serie[0];
                        if (serien < 1 && serieID.length() > 6) {
                            serieID = serieID.substring(0, 6);
                        } else {
                            for (Integer i = 1; i < serien; i++) {
                                if (serie[i].length() > serieID.length()) {
                                    serieID = serie[1];
                                }
                            }
                            if (serieID.length() > 6) {
                                serieID = serieID.substring(0, 6);
                            }

                        }
                        rsa = mDatabase.readData("Select _id, Count(*) from serie where serientitel = '" + serieID + "';");
                        if (rsa.moveToFirst()) {

                        } else {
                            serieID = countserie.toString();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (serieID != "keine") {
                    Log.i("aender", "INSERT INTO serie Values ('" + serieID + "','" + serientitel + "','');");
                    mDatabase.aendern("INSERT INTO serie Values ('" + serieID + "','" + serientitel + "','" + 11 + "');");
                }
            }
            rsa = mDatabase.readData("Select _id, vorname || nachname as name from autoren where vorname || nachname like '" + autorID + "';");
            try {
                if (!rsa.moveToFirst()) {
                    String name[] = autorID.split(" ");
                    Integer names = name.length - 1;
                    vorname = name[0];
                    nachname = "";
                    for (Integer i = 1; i <= names; i++) {
                        nachname = nachname + name[i];
                    }
                    vname = name[0];
                    nname = name[names];
                    if (nname.length() > 4) {
                        nname = nname.substring(0, 4);
                    }
                    if (vname.length() > 3) {
                        vname = vname.substring(0, 3);
                    }
                    autorID = nname + vname;
                    if (autorID != "Auto") {
                        mDatabase.aendern("INSERT INTO autoren Values('" + autorID + "','" + vorname + "','" + nachname + "');");
                    }
                } else {
                    try {
                        autorID = rsa.getString(1).toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            rsa = mDatabase.readData("Select _id From kategorie Where name = '" + kategorieID + "' or _id ='" + kategorieID + "';");
            try {
                if (rsa.moveToFirst()) {
                    kategorieID = rsa.getString(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (tableadd == "neue_bücher") {


                insert = "Insert into neue_bücher Values('" + isbn + "','" + titel + "','" + autorID + "'," + seriegelesen + ",'" + serieID + "','" + seriennr + "','" + kategorieID + "','');";
                Log.i("insert", insert);
            } else if (tableadd == "bücher_gelesen") {
                insert = "Insert into bücher_gelesen Values('" + isbn + "','" + titel + "','" + autorID + "'," + seriegelesen + ",'" + serieID + "','" + seriennr + "','" + kategorieID + "'," + bewert + ",''" + ",'');";
            }

            Log.i("SQLBefehl", insert);
            mDatabase.aendern(insert);
            if (schongelesen == true) {
                delete = "Delete from neue_bücher Where isbn='" + isbn + "';";
                mDatabase.aendern(delete);
            }
            erfolg = true;

            return rsa;
        }

        @Override
        protected void onPostExecute(Cursor result) {

            AlertDialog.Builder myAlert2 = new AlertDialog.Builder(context);
            myAlert2.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            myAlert2.setMessage("Buch erfolgreich hinzugefügt").create();
            AlertDialog alert = myAlert2.create();

            alert.show();


        }


    }

}


