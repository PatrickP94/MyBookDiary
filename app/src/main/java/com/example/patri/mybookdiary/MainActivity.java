package com.example.patri.mybookdiary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patri.db.DBVerbindung;
import com.example.patri.db.DataBaseHelper;
import com.example.patri.select.GetSerie;
import com.example.patri.select.MyListview;
import com.example.patri.select.OnlineBooks;
import com.example.patri.select.getAutor;
import com.example.patri.select.getKategorie;
import com.example.patri.select.readDB;
import com.example.patri.update.DeleteMe;
import com.example.patri.update.updateLeseliste;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    DBVerbindung dv;
    Boolean schongelesen, verbindungOk = false, erfolg;
    ResultSet testText;
    Spinner spKategorie;
    Integer seriegelesen;
    String buch = "";
    private String isbn, autorID, kategorieID, serieID, titel,seriennr, tableadd;
    private AutoCompleteTextView autorlist, serieTxt;
    private Integer bewert, countserie;
    private Button scanBtn, newwishlist,neugelesen;
    private TextView isbnTxt, titelTxt, serieNmb;
    private CheckBox cbSerie;
    private EditText searchlv;
    private RatingBar bewertung;
    private ListView lv;
    private DataBaseHelper dbh;
    private String[] items;
    private ArrayList autor,serielist, kategorielist;
    private String user;
    private ImageButton delete;
    private ArrayList mybooks;
    private ArrayAdapter<String> adapterB, adapterC;
    private ProgressDialog progressBar1;
    private String buchtext;
    private MenuItem shareMenuItem;
    private ShareActionProvider sAP;
    private Intent shareIntent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        shareMenuItem = menu.findItem(R.id.action_teile_buch);
        sAP = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.setType("text/plain");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
        delete = (ImageButton) findViewById(R.id.delete);
        delete.setOnClickListener(this);

        cbSerie = (CheckBox) findViewById(R.id.cbSerie);
        dbh = new DataBaseHelper(this);


        isbnTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 13) {
                    readDB rdb = new readDB();
                    rdb.readDB(s.toString(), autorlist, titelTxt, bewertung, autorID, spKategorie, kategorieID, serieID, serieNmb, cbSerie, serieTxt, newwishlist, neugelesen, MainActivity.this, dbh, delete, verbindungOk, null, shareIntent, sAP);
                    rdb.readmyDatabase();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 13) {
                    readDB rdb = new readDB();
                    rdb.readDB(s.toString(), autorlist, titelTxt, bewertung, autorID, spKategorie, kategorieID, serieID, serieNmb, cbSerie, serieTxt, newwishlist, neugelesen, MainActivity.this, dbh, delete, verbindungOk, null, shareIntent, sAP);
                    rdb.readmyDatabase();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

            getAutor autorenliste = new getAutor();
            autor = null;
            try {
                autor = autorenliste.autorenlisteAbrufen(dbh);
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
                serielist = gS.serienlistAbrufen(dbh);
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
        Spinner dropdown = (Spinner)findViewById(R.id.spKategorie);
        getKategorie gK = new getKategorie();
       // String[] items = new String[]{"","Biografie", "Komödie", "Comic","Esoterik","Fantasy","Fiction Mashup", "Gegenwartsliteratur", "Historische Romane", "Jugendromane/Schullektüre", "Kriminalroman", "Liebesromane", "Politik", "Psychothriller", "Science Fiction","Thriller"};
        kategorielist =null;
        try {
            kategorielist = gK.kategorieListeabrufen(dbh);
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

    public Boolean getVerbindungOk() {
        return verbindungOk;
    }

    public DataBaseHelper getDBH() {

        return dbh;

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void onClick(View v) {
        Log.i("click", String.valueOf(v.getId()));
        bewertung.setVisibility(View.VISIBLE);
        if (v.getId() == R.id.scan_button) {
            resetFields();
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
            if (serieID == null && serieTxt.getText().toString() != "Reihentitel" || serieTxt.getText().toString() == "") {
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
            try {
                erfolg = ul.updateLeseliste(serieID, autorID, kategorieID, tableadd, isbnTxt.getText().toString(), titel, bewert, seriennr, countserie, seriegelesen, schongelesen, MainActivity.this, dbh, verbindungOk);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (erfolg) {
                resetFields();
            }

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
            if (serieID == null && serieTxt.getText().toString() != "Reihentitel" || serieTxt.getText().toString() == "") {
                serieID = serieTxt.getText().toString();
            }


            tableadd = "neue_bücher";
            if (kategorieID ==null){
                kategorieID = spKategorie.getItemAtPosition(spKategorie.getSelectedItemPosition()).toString();
            }
            try {
                erfolg = ul.updateLeseliste(serieID, autorID, kategorieID, tableadd, isbn, titel, bewert, seriennr, countserie, seriegelesen, schongelesen, MainActivity.this, dbh, verbindungOk);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (erfolg) {
                finish();
                startActivity(getIntent());
            }
        }
        if (v.getId() == R.id.delete) {
            DeleteMe dme = new DeleteMe();
            try {
                dme.DeleteMe(MainActivity.this, isbn, dbh, verbindungOk);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            isbnTxt.setText(scanContent);
            isbn=scanContent;
                //new readDB().execute();
            progressBar1 = new ProgressDialog(this);
            progressBar1.setCancelable(true);
            progressBar1.setMessage("Lade Daten...");
            progressBar1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar1.show();


                readDB rdb = new readDB();
            rdb.readDB(isbn, autorlist, titelTxt, bewertung, autorID, spKategorie, kategorieID, serieID, serieNmb, cbSerie, serieTxt, newwishlist, neugelesen, MainActivity.this, dbh, delete, verbindungOk, progressBar1, shareIntent, sAP);
                rdb.readmyDatabase();
            Intent empfaengerIntent = this.getIntent();
            if (empfaengerIntent != null && empfaengerIntent.hasExtra(Intent.EXTRA_TEXT)) {
                buch = empfaengerIntent.getStringExtra(Intent.EXTRA_TEXT);
            }


            //progressBar1.dismiss();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_show_mybooks:
                setContentView(R.layout.listview);
                searchlv = (EditText) findViewById(R.id.searchList);
                lv = (ListView) findViewById(R.id.booklist);
                lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                        String item = lv.getItemAtPosition(position).toString();
                        items = item.split(" ");
                        isbn = items[0];
                        setContentView(R.layout.activity_main);
                        delete.setVisibility(View.VISIBLE);
                        delete.setOnClickListener(MainActivity.this);
                        delete.setClickable(true);
                        isbnTxt = (TextView)findViewById(R.id.scan_isbn) ;
                        isbnTxt.setText(isbn);
                        titelTxt = (TextView)findViewById(R.id.scan_titel);
                        autorlist = (AutoCompleteTextView) findViewById(R.id.autoren);
                        bewertung = (RatingBar) findViewById(R.id.ratingBar1);
                        spKategorie = (Spinner)findViewById(R.id.spKategorie);
                        Spinner dropdown = (Spinner)findViewById(R.id.spKategorie);
                        getKategorie gK = new getKategorie();
                        kategorielist =null;
                        try {
                            kategorielist = gK.kategorieListeabrufen(dbh);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, kategorielist);
                        dropdown.setAdapter(adapter);
                        dropdown.setSelection(0);
                        serieNmb = (TextView)findViewById(R.id.serieNmb);
                        neugelesen = (Button)findViewById(R.id.neu_gelesen);
                        newwishlist = (Button)findViewById(R.id.neuwishlist);
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
                        serieTxt = (AutoCompleteTextView) findViewById(R.id.serienreihe);

                        readDB rdb = new readDB();
                        rdb.readDB(isbn, autorlist, titelTxt, bewertung, autorID, spKategorie, kategorieID, serieID, serieNmb, cbSerie, serieTxt, newwishlist, neugelesen, MainActivity.this, dbh, delete, verbindungOk, null, shareIntent, sAP);
                        rdb.readmyDatabase();

                    }
                });

                MyListview mlv = new MyListview();

                mybooks = null;
                try {
                    mybooks = mlv.booklist("gelesen", dbh, verbindungOk);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final Context cont = this;
                searchlv.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        MainActivity.this.adapterB.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                adapterB = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mybooks);
                lv.setAdapter(adapterB);
                break;
            case R.id.action_show_newbooks:
                setContentView(R.layout.listview);
                searchlv = (EditText) findViewById(R.id.searchList);
                lv = (ListView) findViewById(R.id.booklist);
                lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                        String item = lv.getItemAtPosition(position).toString();
                        items = item.split(" ");
                        isbn = items[0];
                        setContentView(R.layout.activity_main);
                        delete.setVisibility(View.VISIBLE);
                        isbnTxt = (TextView)findViewById(R.id.scan_isbn) ;
                        isbnTxt.setText(isbn);
                        titelTxt = (TextView)findViewById(R.id.scan_titel);
                        autorlist = (AutoCompleteTextView) findViewById(R.id.autoren);
                        bewertung = (RatingBar) findViewById(R.id.ratingBar1);
                        spKategorie = (Spinner)findViewById(R.id.spKategorie);
                        Spinner dropdown = (Spinner)findViewById(R.id.spKategorie);
                        getKategorie gK = new getKategorie();
                        kategorielist =null;
                        try {
                            kategorielist = gK.kategorieListeabrufen(dbh);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, kategorielist);
                        dropdown.setAdapter(adapter);
                        dropdown.setSelection(0);
                        serieNmb = (TextView)findViewById(R.id.serieNmb);
                        neugelesen = (Button)findViewById(R.id.neu_gelesen);
                        newwishlist = (Button)findViewById(R.id.neuwishlist);
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
                        serieTxt = (AutoCompleteTextView) findViewById(R.id.serienreihe);
                        readDB rdb = new readDB();
                        rdb.readDB(isbn, autorlist, titelTxt, bewertung, autorID, spKategorie, kategorieID, serieID, serieNmb, cbSerie, serieTxt, newwishlist, neugelesen, MainActivity.this, dbh, delete, verbindungOk, null, shareIntent, sAP);
                        rdb.readmyDatabase();
                    }
                });
                MyListview nlv = new MyListview();
                ArrayList newbooks;
                newbooks = null;
                try {
                    newbooks = nlv.booklist("wishlist", dbh, verbindungOk);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                searchlv.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        MainActivity.this.adapterC.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                adapterC = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, newbooks);
                lv.setAdapter(adapterC);
                break;
            case R.id.action_show_home:
                setContentView(R.layout.activity_main);
                finish();
                startActivity(getIntent());
                break;

            case R.id.action_show_statistic:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.action_logout:
                Intent intent2 = new Intent(this, LogoutActivity.class);
                startActivity(intent2);
                break;

            case R.id.action_delete_user:
                Intent intent3 = new Intent(this, DeleteUserActivity.class);
                startActivity(intent3);
                break;


        }
        return true;
    }


    public void resetFields() {

        this.isbnTxt.setText("");
            //autorTxt.setText("Autor");
        this.autorlist.setText("");
        this.titelTxt.setText("");
        this.spKategorie.setSelection(0);
        if (this.cbSerie.isChecked()) {
            this.cbSerie.setChecked(false);
            }
        this.serieTxt.setText("");
        this.serieNmb.setText("");
        this.serieID = null;
        this.autorID = null;
        this.bewertung.setRating(0);

        }

}

