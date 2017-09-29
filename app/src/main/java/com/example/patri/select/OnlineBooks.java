package com.example.patri.select;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patri.mybookdiary.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class OnlineBooks extends AppCompatActivity {
    public String isbn;
    public TextView autorTxt, titelTxt;
    public RatingBar bewertung;
    private MainActivity ma = new MainActivity();
    private Boolean verbindungok = ma.getVerbindungOk();



    public void OnlineBooks(String isbn, TextView autorTxt, TextView titelTxt, RatingBar bewertung){
        this.isbn = isbn;
        this.autorTxt = autorTxt;
        this.bewertung = bewertung;
        this.titelTxt = titelTxt;
        new MyTask().execute();

    }


    private class MyTask extends AsyncTask<Void, Void, Elements> {
        @Override
        protected Elements doInBackground(Void... params) {
            Elements ele;
            ele = null;
            Document doc;
            try {
                doc = Jsoup.connect("https://www.worldcat.org/search?q="+isbn).get();
                Elements masthead = doc.select("div#donerefinesearch");
                if (masthead.size()==0){

                    ele=null;
                }
                else{
                    ele = masthead.select("table#br-table-results");
                }

            } catch (Exception e) {

            }


            return ele;

        }


        @Override
        protected void onPostExecute(Elements result) {
            //if you had a ui element, you could display the title
            if (result==null){

            }
            else {
                Element autor = result.select("tr.menuElem").first();
                try {
                    String autorname = autor.select("div.author").first().text().toString().replace("by ", "");
                    if (autorname != null) {
                        if (autorname.indexOf(";") > 0) {
                            autorTxt.setText(autorname.substring(0, autorname.indexOf(";")));
                        }
                        String titel = result.select("tr.menuElem").first().text().toString();
                        Integer i = titel.indexOf("1. ");
                        Integer j = titel.indexOf(" 1. ");
                        titelTxt.setText(titel.substring(i + 3, j));
                    }
                } catch (Exception e) {
                    Toast.makeText(OnlineBooks.this, "Buch nicht gefunden", Toast.LENGTH_LONG).show();
                }


            }

        }
    }
}
