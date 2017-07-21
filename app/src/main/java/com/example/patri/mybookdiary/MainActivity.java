package com.example.patri.mybookdiary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patri.db.DBVerbindung;
import com.example.patri.select.GetSerie;
import com.example.patri.select.OnlineBooks;
import com.example.patri.select.getAutor;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private String isbn, autorID, kategorieID, serieID, titel,seriennr, tableadd;
    private AutoCompleteTextView autorlist, serieTxt;
    private Integer bewert;
    private Button scanBtn, newwishlist,neugelesen;
    private TextView isbnTxt, titelTxt, serieNmb;
    private CheckBox cbSerie;
    private RatingBar bewertung;
    DBVerbindung dv;
    Boolean schongelesen;
    ResultSet testText;
    Spinner spKategorie;
    Integer seriegelesen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanBtn = (Button)findViewById(R.id.scan_button);
        isbnTxt = (TextView)findViewById(R.id.scan_isbn);
        //autorTxt = (TextView)findViewById(R.id.scan_Autor);
        titelTxt = (TextView)findViewById(R.id.scan_titel);
        scanBtn.setOnClickListener(this);
        newwishlist = (Button)findViewById(R.id.neuwishlist);
        newwishlist.setOnClickListener(this);
        neugelesen = (Button)findViewById(R.id.neu_gelesen);
        neugelesen.setOnClickListener(this);
        spKategorie = (Spinner)findViewById(R.id.spKategorie);
        ArrayList autor,serielist;
        cbSerie = (CheckBox) findViewById(R.id.cbSerie);
        cbSerie.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if (cbSerie.isChecked())
                {
                   serieTxt.setVisibility(View.VISIBLE);
                   serieNmb.setVisibility(View.VISIBLE);
                }
                else
                {
                    serieTxt.setVisibility(View.INVISIBLE);
                    serieNmb.setVisibility(View.INVISIBLE);
                }
            }
        });
        serieNmb = (TextView)findViewById(R.id.serieNmb);
        bewertung =(RatingBar)findViewById(R.id.ratingBar1);
        autorlist =(AutoCompleteTextView)findViewById(R.id.autoren);
        getAutor autorenliste = new getAutor();
        autor=null;
        try {
            autor = autorenliste.autorenlisteAbrufen();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapterAutor = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,autor);
        autorlist.setAdapter(adapterAutor);
        serieTxt = (AutoCompleteTextView)findViewById(R.id.serienreihe);
        GetSerie gS = new GetSerie();
        try {
            serielist = gS.serienlistAbrufen();
        } catch (SQLException e) {
            e.printStackTrace();
            serielist=null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            serielist=null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            serielist=null;
        }
        ArrayAdapter<String> adapterserie = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,serielist);
        serieTxt.setAdapter(adapterserie);

        Spinner dropdown = (Spinner)findViewById(R.id.spKategorie);
        String[] items = new String[]{"","Biografie", "Komödie", "Comic","Esoterik","Fantasy","Fiction Mashup", "Gegenwartsliteratur", "Historische Romane", "Jugendromane/Schullektüre", "Kriminalroman", "Liebesromane", "Politik", "Psychothriller", "Science Fiction","Thriller"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }
    public void onClick(View v) {
        Log.i("click", String.valueOf(v.getId()));
        if (v.getId() == R.id.scan_button) {
            new reset();
            schongelesen=false;
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();


        }
        if (v.getId() == R.id.neu_gelesen) {
            updateLeseliste ul = new updateLeseliste();
            if (cbSerie.isChecked()){ seriegelesen = 1;}
            else{ seriegelesen =0; }
            titel = titelTxt.getText().toString();
            seriennr = serieNmb.getText().toString();
            if (seriennr.equals("Nummer")){ seriennr = ""; }

            float bewert1 = bewertung.getRating() * 2;
            bewert = Math.round(bewert1);
            tableadd = "bücher_gelesen";
            if (kategorieID ==null){
                kategorieID = spKategorie.getItemAtPosition(spKategorie.getSelectedItemPosition()).toString();
            }
            new updateLeseliste().execute();

        }
        if (v.getId() == R.id.neuwishlist){
            updateLeseliste ul = new updateLeseliste();
            if (cbSerie.isChecked()){ seriegelesen = 1;}
            else{ seriegelesen =0; }
            titel = titelTxt.getText().toString();
            seriennr = serieNmb.getText().toString();
            if (seriennr.equals("Nummer")){ seriennr = ""; }
            float bewert1 = bewertung.getRating() * 2;
            bewert = Math.round(bewert1);
            //autorID = autorTxt.getText().toString();
            if (autorID ==null){
                autorID = autorlist.getText().toString();
            }
            if (serieID ==null && serieTxt.getText().toString() !="Reihentitel"){
                serieID = serieTxt.getText().toString();
            }


            tableadd = "neue_bücher";
            if (kategorieID ==null){
                kategorieID = spKategorie.getItemAtPosition(spKategorie.getSelectedItemPosition()).toString();
            }
            new updateLeseliste().execute();

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            isbnTxt.setText(scanContent);
            isbn=scanContent;
            String wlan = getWifiName(getApplicationContext());
            if (wlan.contentEquals("Preiss WLAN")){

            }
            else {
                AlertDialog.Builder myAlert2 = new AlertDialog.Builder(MainActivity.this);
                myAlert2.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                myAlert2.setMessage("keine Verbindung zur Online Datenbank möglich.").create();
                AlertDialog alert = myAlert2.create();

                alert.show();
                OnlineBooks ob = new OnlineBooks();
                ob.OnlineBooks(isbn, autorlist, titelTxt, bewertung);
            }
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

     private class readDB extends AsyncTask<Void, Void, ResultSet> {
         @Override
         protected ResultSet doInBackground(Void... params) {
             ResultSet rsa;
             rsa = null;
             DBVerbindung dv = new DBVerbindung();
             Log.i("abfrage","Select * From (Select *, 'gelesen' From bücher_gelesen union all select * ,'', '','neu' from neue_bücher)t1" +
                     " inner join autoren on t1.autor = autoren.idAutoren inner join kategorie on t1.kategorie = kategorie.kategorieId inner join serie on serie.serienId = t1.serienid " +
                     "where ISBN = "+isbn);
             rsa=dv.lesen("Select * From (Select *, 'gelesen' From bücher_gelesen union all select * ,'', '','neu' from neue_bücher)t1" +
                     " inner join autoren on t1.autor = autoren.idAutoren inner join kategorie on t1.kategorie = kategorie.kategorieId inner join serie on serie.serienId = t1.serienid " +
                     "where ISBN = "+isbn);
             try {
                 if (rsa.next()) {
                     return rsa;

               }
                 else {
                     rsa=null;
                 }
             } catch (SQLException e) {
                 e.printStackTrace();

             }

             return rsa;
         }

         @Override
         protected void onPostExecute(ResultSet result) {
             Integer i;
             Boolean schongelesen;
             //if you had a ui element, you could display the title
             if (result==null){
                 //new MyTask().execute();
                 OnlineBooks ob = new OnlineBooks();
                 ob.OnlineBooks(isbn, autorlist, titelTxt, bewertung);
             }
             else {
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
                     Log.i("GetString(5)",result.getString(5));
                     if (result.getString(4).toString().contentEquals("0"))  {
                         serieTxt.setText("keine Serie");
                         serieNmb.setText("0");
                     }
                     else {

                         cbSerie.setChecked(true);
                         serieTxt.setText(result.getString(18).toString());
                         serieNmb.setText(result.getString(6).toString());
                     }
                     serieID = result.getString(5);

                     Log.i("Bewertung", result.getString(8).toString());
                     Float rating = result.getFloat(8) / 2;
                     bewertung.setRating(rating);

                     schongelesen = result.getString(11).toString().contentEquals("gelesen");

                     if (schongelesen==true){
                         AlertDialog.Builder myAlert = new AlertDialog.Builder(MainActivity.this);
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

                     }
                     else if(schongelesen==false){
                        neugelesen.setVisibility(View.VISIBLE);
                         newwishlist.setVisibility(View.VISIBLE);
                         bewertung.setVisibility(View.VISIBLE);
                     }
                     else{
                         neugelesen.setVisibility(View.VISIBLE);
                         newwishlist.setText(View.VISIBLE);
                     }

                 } catch (SQLException e) {
                     e.printStackTrace();
                 }
             }

         }

     }
    private class reset{
        private void resetFields() {

            scanBtn.setText("");
            isbnTxt.setText("ISBN");
            //autorTxt.setText("Autor");
            autorlist.setText("Autor");
            titelTxt.setText("Titel");
            spKategorie.setSelection(0);
            if (cbSerie.isChecked()) {
                cbSerie.setChecked(false);
            }
            serieTxt.setText("");
            serieNmb.setText("");
        }

    }

    private class updateLeseliste extends AsyncTask<Void, Void, ResultSet>{

        @Override
        protected ResultSet doInBackground(Void... params) {
            ResultSet rsa;
            rsa = null;
            DBVerbindung dv = new DBVerbindung();
            dv.oeffneDB();
            String insert,delete;
            insert="";
            //ul.updateLeseliste(isbn,titelTxt.getText().t2oString(),autorID, kategorieID, seriegelesen,serieID, serieNmb.getText().toString(),(int)bewertung.getRating()*2);
            if (serieID.toString().equals("Reihentitel")){
                serieID = "keine";
            }
            else{
                rsa = dv.lesen("Select serienID from serie where serientitel = '"+serieID+"';");
                if (rsa == null){
                    String serie[] =  serieID.split(" ");
                    Integer serien = serie.length -1;
                    String vname, nname;
                    serieID = serie[1];
                    if (serien==1){
                        serieID = serieID.substring(1,6);
                    }
                    else {
                        for (Integer i = 1; i < serien; i++) {
                            if (serie[i].length() > serieID.length()){
                                serieID= serie[1];
                            }
                        }
                        if (serieID.length() > 6){serieID=serieID.substring(1,6);}

                    }
                }
                else {
                    try {
                        while (rsa.next()) {
                            serieID = rsa.getString(1).toString();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            rsa = dv.lesen("Select idAutoren, Concat(vorname, nachname) as name from autoren where CONCAT(vorname, ' ', nachname) like '"+autorID+"';");
            if (rsa == null){
                String name[] =  autorID.split(" ");
                Integer names = name.length -1;
                String vname, nname;
                vname= name[0];
                nname = name[names];
                if (nname.length() > 4){
                    nname = nname.substring(0,4);
                }
                if (vname.length() > 3){
                    vname = vname.substring(0,3);
                }
                autorID = nname + vname;
            }
            else {
                try {
                    while (rsa.next()) {
                        autorID = rsa.getString(1).toString();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (tableadd == "neue_bücher"){
                Log.i("lesen", "Select kategorieID From kategorie Where name = '"+kategorieID+"' or kategorieID ='"+kategorieID+"';");
                rsa = dv.lesen("Select kategorieID From kategorie Where name = '"+kategorieID+"' or kategorieID ='"+kategorieID+"';");
                try{
                    while (rsa.next()){
                        kategorieID = rsa.getString(1);
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }
                insert="Insert into neue_bücher Values('"+isbn+"','"+titel.substring(7)+"','"+autorID+"',"+seriegelesen+",'"+serieID+"','"+seriennr+"','"+kategorieID+"','');";
                Log.i("insert", insert);
            }
            else if (tableadd == "bücher_gelesen"){
                insert="Insert into bücher_gelesen Values('"+isbn+"','"+titel+"','"+autorID+"',"+seriegelesen+",'"+serieID+"','"+seriennr+"','"+kategorieID+"',"+bewert+",''"+",'');";
            }

            Log.i("SQLBefehl", insert);
            dv.aendern(insert);
            if (schongelesen == true){
                delete="Delete from neue_bücher Where isbn='"+isbn+"';";
                dv.aendern(delete);
            }


            return rsa;
        }
        @Override
        protected void onPostExecute(ResultSet result) {

            AlertDialog.Builder myAlert2 = new AlertDialog.Builder(MainActivity.this);
            myAlert2.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            myAlert2.setMessage("Buch erfolgreich hinzugefügt").create();
            AlertDialog alert = myAlert2.create();

            alert.show();


            new reset();
        }


        }

    public String getWifiName(Context context) {
        String ssid = "none";
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        ssid = wifiInfo.getSSID();

        return ssid;
    }


}

