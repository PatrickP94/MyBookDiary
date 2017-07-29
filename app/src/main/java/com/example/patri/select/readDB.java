package com.example.patri.select;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.patri.db.DBVerbindung;

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


    public void readDB(String isbn, TextView autorlist, TextView titelTxt, RatingBar bewertung, String autorID, Spinner spKategorie, String kategorieID, String serieID, TextView serieNmb, CheckBox cbSerie, AutoCompleteTextView serieTxt, Button newwishlist, Button neugelesen, Context context) {
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


    }

    public Boolean readmyDatabase() {
        AsyncTask rsa = new readDB1().execute();

        return schongelesen;
    }

    private class readDB1 extends AsyncTask<Void, Void, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... params) {
            ResultSet rsa;
            rsa = null;
            DBVerbindung dv = new DBVerbindung();
            Log.i("abfrage", "Select * From (Select *, 'gelesen' From bücher_gelesen union all select * ,'', '','neu' from neue_bücher)t1" +
                    " inner join autoren on t1.autor = autoren.idAutoren inner join kategorie on t1.kategorie = kategorie.kategorieId inner join serie on serie.serienId = t1.serienid " +
                    "where ISBN = " + isbn);
            rsa = dv.lesen("Select * From (Select *, 'gelesen' From bücher_gelesen union all select * ,'', '','neu' from neue_bücher)t1" +
                    " inner join autoren on t1.autor = autoren.idAutoren inner join kategorie on t1.kategorie = kategorie.kategorieId inner join serie on serie.serienId = t1.serienid " +
                    "where ISBN = " + isbn);
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
                    Log.i("GetString(5)", result.getString(5));
                    if (result.getString(4).toString().contentEquals("0")) {
                        serieTxt.setText("keine Serie");
                        serieNmb.setText("0");
                    } else {

                        cbSerie.setChecked(true);
                        serieTxt.setText(result.getString(18).toString());
                        serieNmb.setText(result.getString(6).toString());
                    }
                    serieID = result.getString(5);

                    Log.i("Bewertung", result.getString(8).toString());
                    Float rating = result.getFloat(8) / 2;
                    bewertung.setRating(rating);
                    Log.i("gelesen?", result.getString(11));
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
                        neugelesen.setVisibility(View.INVISIBLE);
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

        }

    }
}
