package com.example.patri.select;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.patri.db.DBVerbindung;
import com.example.patri.db.DataBaseHelper;
import com.example.patri.mybookdiary.MainActivity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by patri on 28.07.2017.
 */

public class readDB extends Activity {

    private String isbn, autorID, kategorieID, serieID;
    private TextView autorlist, titelTxt, serieNmb;
    private RatingBar bewertung;
    private Spinner spKategorie;
    private CheckBox cbSerie;
    private AutoCompleteTextView serieTxt;
    private Boolean schongelesen;
    private Context context;
    private Button newwishlist, neugelesen;
    private ImageButton delete;
    private MainActivity ma = new MainActivity();
    private Boolean verbindungok;
    private DataBaseHelper mDatabase;
    private ProgressDialog progressBar1;
    private String buchtext;
    private Intent shareIntent;
    private ShareActionProvider sAP;
    private MenuItem shareMenuItem;


    public void readDB(String isbn, TextView autorlist, TextView titelTxt, RatingBar bewertung, String autorID, Spinner spKategorie, String kategorieID, String serieID, TextView serieNmb,
                       CheckBox cbSerie, AutoCompleteTextView serieTxt, Button newwishlist, Button neugelesen, Context context, DataBaseHelper dbh, ImageButton delete, Boolean verbindungok,
                       ProgressDialog progressbar1, Intent shareIntent, ShareActionProvider sAP, MenuItem shareMenuItem) {
        this.isbn = isbn;
        this.autorlist = autorlist;
        this.titelTxt = titelTxt;
        this.bewertung = bewertung;
        this.autorID = autorID;
        this.spKategorie = spKategorie;
        this.kategorieID = kategorieID;
        this.serieID = serieID;
        this.serieNmb = serieNmb;
        this.cbSerie = cbSerie;
        this.serieTxt = serieTxt;
        this.neugelesen = neugelesen;
        this.newwishlist = newwishlist;
        this.context = context;
        this.delete = delete;
        this.verbindungok = verbindungok;
        mDatabase = dbh;
        this.progressBar1 = progressbar1;
        this.shareMenuItem = shareMenuItem;
        this.shareIntent = shareIntent;
        this.sAP = sAP;


    }

    public Boolean readmyDatabase() {
        if (verbindungok == true) {
            AsyncTask rsa = new readDB1().execute();
        } else {
            AsyncTask readDB2 = new readDB2().execute();
        }


        return schongelesen;
    }
    private class readDB1 extends AsyncTask<Void, Void, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... params) {
            ResultSet rsa;
            rsa = null;
            String select;
            select = ("Select * From (Select *, 'gelesen' From bücher_gelesen union all select * ,'', '','neu' from neue_bücher)t1" +
                    " inner join autoren on t1.autor = autoren.idAutoren inner join kategorie on t1.kategorie = kategorie.kategorieId inner join serie on serie.serienId = t1.serienid " +
                    "where ISBN = " + isbn);
            DBVerbindung dv = new DBVerbindung();
            rsa = dv.lesen(select);
            try {
                if (rsa.next()) {
                    return rsa;

                } else {
                    rsa = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();

            }

            return rsa;
        }

        @Override
        protected void onPostExecute(ResultSet result) {
            Integer i;
            //if you had a ui element, you could display the title
            if (result == null) {
                //new MyTask().execute();
                OnlineBooks ob = new OnlineBooks();
                ob.OnlineBooks(isbn, autorlist, titelTxt, bewertung);
            } else {
                try {
                    //autorTxt.setText(result.getString(13).toString() + " " + result.getString(14).toString());
                    autorlist.setText(result.getString(13).toString() + " " + result.getString(14).toString());
                    autorID = result.getString(3).toString();
                    titelTxt.setText(result.getString(2).toString());
                    for (Integer j = 1; j < spKategorie.getCount(); j++) {
                        if (spKategorie.getItemAtPosition(j).equals(result.getString(16).toString())) {
                            spKategorie.setSelection(j);
                        }
                    }
                    kategorieID = result.getString(15).toString();
                    if (result.getString(4).toString().contentEquals("0")) {
                        serieTxt.setText("keine Serie");
                        serieNmb.setText("0");
                    } else {

                        cbSerie.setChecked(true);
                        serieTxt.setVisibility(View.VISIBLE);
                        serieNmb.setVisibility(View.VISIBLE);
                        serieTxt.setText(result.getString(18).toString());
                        serieNmb.setText(result.getString(6).toString());
                    }
                    serieID = result.getString(5);
                    buchtext = result.getString(2).toString() + " von " + result.getString(13).toString() + " " + result.getString(14).toString();
                    Float rating = result.getFloat(8) / 2;
                    bewertung.setRating(rating);
                    schongelesen = result.getString(11).toString().contentEquals("gelesen");
                    if (schongelesen == true) {
                        AlertDialog.Builder myAlert = new AlertDialog.Builder(context);
                        myAlert.setPositiveButton("Continue..", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        myAlert.setMessage("Buch bereits gelesen").create();
                        AlertDialog alert = myAlert.create();

                        alert.show();
                        //neugelesen.setVisibility(View.INVISIBLE);
                        neugelesen.setText("Änderungen speichern");
                        newwishlist.setVisibility(View.INVISIBLE);

                    } else if (schongelesen == false) {
                        neugelesen.setVisibility(View.VISIBLE);
                        newwishlist.setVisibility(View.VISIBLE);
                    } else {
                        neugelesen.setVisibility(View.VISIBLE);
                        newwishlist.setText(View.VISIBLE);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (progressBar1 != null) {
                progressBar1.dismiss();
            }
            shareMenuItem.setVisible(true);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hallo! Sieh mal was ich für ein tolles Buch gefunden habe: " + buchtext + ". Wäre das nicht auch was für dich");
            if (sAP != null) {
                sAP.setShareIntent(shareIntent);
            } else {
                String Log_TAG = MainActivity.class.getSimpleName();
                Log.d(Log_TAG, "Kein ShareActionProvider vorhanden!");
            }


        }

    }

    private class readDB2 extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... params) {
            Cursor rsa;
            rsa = null;
            String select;
            select = ("Select * From (Select *, 'gelesen' From bücher_gelesen union all select * ,'', '','neu' from neue_bücher)t1" +
                    " inner join autoren on t1.autor = autoren._id inner join kategorie on t1.kategorie = kategorie._id inner join serie on serie._id = t1.serienid " +
                    "where t1._id = " + isbn);
            Log.i("lesen", select);
            Cursor crs = mDatabase.readData(select);
            if (crs.moveToFirst()) {
                rsa = crs;
            } else {
                rsa = null;
            }

            return rsa;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            Integer i;
            //if you had a ui element, you could display the title
            if (result == null) {
                //new MyTask().execute();
                OnlineBooks ob = new OnlineBooks();
                ob.OnlineBooks(isbn, autorlist, titelTxt, bewertung);
            } else {
                try {
                    //autorTxt.setText(result.getString(13).toString() + " " + result.getString(14).toString());
                    autorlist.setText(result.getString(12).toString() + " " + result.getString(13).toString());
                    autorID = result.getString(2).toString();
                    titelTxt.setText(result.getString(1).toString());
                    for (Integer j = 1; j < spKategorie.getCount(); j++) {
                        if (spKategorie.getItemAtPosition(j).equals(result.getString(15).toString())) {
                            spKategorie.setSelection(j);
                        }
                    }
                    kategorieID = result.getString(14).toString();
                    if (result.getString(3).toString().contentEquals("0")) {
                        serieTxt.setText("keine Serie");
                        serieNmb.setText("0");
                    } else {

                        cbSerie.setChecked(true);
                        serieTxt.setVisibility(View.VISIBLE);
                        serieNmb.setVisibility(View.VISIBLE);
                        serieTxt.setText(result.getString(17).toString());
                        serieNmb.setText(result.getString(5).toString());
                    }
                    serieID = result.getString(4);

                    Float rating = result.getFloat(7) / 2;
                    bewertung.setRating(rating);
                    delete.setVisibility(View.VISIBLE);
                    schongelesen = result.getString(10).toString().contentEquals("gelesen");
                    if (schongelesen == true) {
                        AlertDialog.Builder myAlert = new AlertDialog.Builder(context);
                        myAlert.setPositiveButton("Continue..", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        myAlert.setMessage("Buch bereits gelesen").create();
                        AlertDialog alert = myAlert.create();

                        alert.show();
                        delete.setVisibility(View.VISIBLE);
                        //neugelesen.setVisibility(View.INVISIBLE);
                        neugelesen.setText("Änderungen speichern");
                        newwishlist.setVisibility(View.INVISIBLE);

                    } else if (schongelesen == false) {
                        neugelesen.setVisibility(View.VISIBLE);
                        newwishlist.setVisibility(View.VISIBLE);

                    } else {
                        neugelesen.setVisibility(View.VISIBLE);
                        newwishlist.setText(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (progressBar1 != null) {

                progressBar1.dismiss();
            }
            buchtext = titelTxt.getText() + " von " + autorlist.getText().toString();

        }

    }

}
