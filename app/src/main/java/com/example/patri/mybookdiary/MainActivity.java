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
import com.example.patri.select.getKategorie;
import com.example.patri.select.readDB;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    DBVerbindung dv;
    Boolean schongelesen, verbindungOk = false;
    ResultSet testText;
    Spinner spKategorie;
    Integer seriegelesen;
    private String isbn, autorID, kategorieID, serieID, titel,seriennr, tableadd;
    private AutoCompleteTextView autorlist, serieTxt;
    private Integer bewert, countserie;
    private Button scanBtn, newwishlist,neugelesen;
    private TextView isbnTxt, titelTxt, serieNmb;
    private CheckBox cbSerie;
    private RatingBar bewertung;

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
        ArrayList autor,serielist, kategorielist;
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
        String wlan = getWifiName(getApplicationContext());
        if (wlan.contains("Preiss WLAN")){
            verbindungOk = true;
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
        autorlist =(AutoCompleteTextView)findViewById(R.id.autoren);
        if (verbindungOk) {
            getAutor autorenliste = new getAutor();
            autor = null;
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
                    android.R.layout.simple_dropdown_item_1line, autor);
            autorlist.setAdapter(adapterAutor);

            serieTxt = (AutoCompleteTextView) findViewById(R.id.serienreihe);
            GetSerie gS = new GetSerie();
            try {
                serielist = gS.serienlistAbrufen();
                countserie =serielist.size()+1;
            } catch (SQLException e) {
                e.printStackTrace();
                serielist = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                serielist = null;
            } catch (ExecutionException e) {
                e.printStackTrace();
                serielist = null;
            }
            ArrayAdapter<String> adapterserie = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, serielist);
            serieTxt.setAdapter(adapterserie);
        }
        Spinner dropdown = (Spinner)findViewById(R.id.spKategorie);
        getKategorie gK = new getKategorie();
       // String[] items = new String[]{"","Biografie", "Komödie", "Comic","Esoterik","Fantasy","Fiction Mashup", "Gegenwartsliteratur", "Historische Romane", "Jugendromane/Schullektüre", "Kriminalroman", "Liebesromane", "Politik", "Psychothriller", "Science Fiction","Thriller"};
        kategorielist =null;
        try {
            kategorielist = gK.kategorieListeabrufen();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, kategorielist);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(0);
    }


    public void onClick(View v) {
        Log.i("click", String.valueOf(v.getId()));
        bewertung.setVisibility(View.VISIBLE);
        if (v.getId() == R.id.scan_button) {
            new reset();
            schongelesen=false;
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
            bewertung.setVisibility(View.VISIBLE);

        }
        if (v.getId() == R.id.neu_gelesen) {
            updateLeseliste ul = new updateLeseliste();
            if (cbSerie.isChecked()){ seriegelesen = 1;}
            else{ seriegelesen =0; }
            titel = titelTxt.getText().toString();
            seriennr = serieNmb.getText().toString();
            if (seriennr.equals("Nummer")){ seriennr = ""; }
            if (serieID == null && serieTxt.getText().toString() !="Reihentitel"){
                serieID = serieTxt.getText().toString();
            }
            if (autorID ==null){
                autorID = autorlist.getText().toString();
            }
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
            if (serieID == null && serieTxt.getText().toString() !="Reihentitel"){
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
            if (verbindungOk){
                //new readDB().execute();
                readDB rdb = new readDB();
                rdb.readDB(isbn, autorlist, titelTxt, bewertung, autorID, spKategorie, kategorieID, serieID, serieNmb, cbSerie, serieTxt, newwishlist, neugelesen, MainActivity.this);
                rdb.readmyDatabase();


            }
            else{
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

    public String getWifiName(Context context) {
        String ssid = "none";
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        ssid = wifiInfo.getSSID();

        return ssid;
    }

    private class reset{
        public void resetFields() {

            isbnTxt.setText("ISBN");
            //autorTxt.setText("Autor");
            autorlist.setText("Autor");
            titelTxt.setText("Titel");
            spKategorie.setSelection(0);
            if (cbSerie.isChecked()) {
                cbSerie.setChecked(false);
            }
            serieTxt.setText("Reihe");
            serieNmb.setText("Nummer");
            serieID = null;
            autorID = null;
            bewertung.setRating(0);

        }

    }

    private class updateLeseliste extends AsyncTask<Void, Void, ResultSet>{

        @Override
        protected ResultSet doInBackground(Void... params) {
            ResultSet rsa;
            rsa = null;
            DBVerbindung dv = new DBVerbindung();
            dv.oeffneDB();
            String vname="", nname="",vorname="", nachname="";
            String serientitel;
            String insert,delete;
            insert="";
            serientitel = serieID;
            //ul.updateLeseliste(isbn,titelTxt.getText().t2oString(),autorID, kategorieID, seriegelesen,serieID, serieNmb.getText().toString(),(int)bewertung.getRating()*2);
            if (serieID.toString().equals("Reihentitel")){
                serieID = "keine";
                serientitel = "";
            }
            else{
                rsa = dv.lesen("Select serienID from serie where serientitel like '"+serieID+"';");
                try {
                    if (rsa.next()){
                        try {
                            serieID = rsa.getString(1).toString();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }


                    }
                    else {
                        String serie[] =  serieID.split(" ");
                        Integer serien = serie.length -1;
                        serieID = serie[0];
                        if (serien<1 && serieID.length()>6){
                            serieID = serieID.substring(0,6);
                        }
                        else {
                            for (Integer i = 1; i < serien; i++) {
                                if (serie[i].length() > serieID.length()){
                                    serieID= serie[1];
                                }
                            }
                            if (serieID.length() > 6){serieID=serieID.substring(0,6);}

                        }
                        rsa = dv.lesen("Select serienID, Count(*) from serie where serientitel = '"+serieID+"';");
                        if (rsa.next()){

                        }
                        else{
                            serieID = countserie.toString();
                        }
                    }


                        } catch (SQLException e) {
                            e.printStackTrace();
                    }




                if (serieID !="keine") {
                    Log.i("aender", "INSERT INTO serie Values ('" + serieID + "','" + serientitel + "','');");
                    dv.aendern("INSERT INTO serie Values ('" + serieID + "','" + serientitel + "','"+11+"');");
                }
            }
            rsa = dv.lesen("Select idAutoren, Concat(vorname, nachname) as name from autoren where CONCAT(vorname, ' ', nachname) like '"+autorID+"';");
            try {
                if (!rsa.next()){
                    String name[] =  autorID.split(" ");
                    Integer names = name.length -1;
                    vorname = name[0];
                    nachname="";
                    for (Integer i=1;i <= names;i++){
                        nachname = nachname+name[1];
                    }
                    vname= name[0];
                    nname = name[names];
                    if (nname.length() > 4){
                        nname = nname.substring(0,4);
                    }
                    if (vname.length() > 3){
                        vname = vname.substring(0,3);
                    }
                    autorID = nname + vname;
                    if (autorID != "Auto") {
                        dv.aendern("INSERT INTO autoren Values('" + autorID + "','" + vorname + "','" + nachname + "');");
                    }
                }
                else {
                    try {
                            autorID = rsa.getString(1).toString();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.i("lesen", "Select kategorieID From kategorie Where name = '"+kategorieID+"' or kategorieID ='"+kategorieID+"';");
            rsa = dv.lesen("Select kategorieID From kategorie Where name = '"+kategorieID+"' or kategorieID ='"+kategorieID+"';");
            try{
                while (rsa.next()){
                    kategorieID = rsa.getString(1);
                }
            } catch (SQLException e){
                e.printStackTrace();
            }

            if (tableadd == "neue_bücher"){


                insert="Insert into neue_bücher Values('"+isbn+"','"+titel+"','"+autorID+"',"+seriegelesen+",'"+serieID+"','"+seriennr+"','"+kategorieID+"','');";
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


            new reset().resetFields();
        }


        }


}

