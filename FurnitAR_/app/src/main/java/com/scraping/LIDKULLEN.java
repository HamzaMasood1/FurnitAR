package com.scraping;

import android.os.AsyncTask;
import android.widget.TextView;

import com.razi.furnitar.R;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LIDKULLEN {
    public LIDKULLEN(){
        new MyTask().execute();
    }
    private class MyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String title = "";
            Document doc;
            String content = "";
            String width = "";
            String depth = "";
            String height = "";
            try {
                doc = Jsoup.connect("https://www.ikea.com/ie/en/p/odger-swivel-chair-white-beige-70308685/").get();
                title = doc.title();


                Elements measurements = doc.getElementsByClass("product-pip__definition-list-item");

                StringBuilder sb = new StringBuilder();
                for (Element measurement : measurements) {
                    if (measurement.text().toString().equals("Width:")) {
                        width = measurement.nextElementSibling().text().toString();
                    }
                    if (measurement.text().toString().equals("Depth:")) {
                        depth = measurement.nextElementSibling().text().toString();
                    }
                    if (measurement.text().toString().equals("Max. height:")) {
                        height = measurement.nextElementSibling().text().toString();
                    }
                }
                content = width.toString() + " " +depth.toString()+ " " + height.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content;
        }


        @Override
        protected void onPostExecute(String result) {
            //if you had a ui element, you could display the title
            //((TextView) findViewById(R.id.text)).setText(result);

        }


    }
}

