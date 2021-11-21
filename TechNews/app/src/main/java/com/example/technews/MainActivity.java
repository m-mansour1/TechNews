package com.example.technews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    int atNbr;
    public ArrayList<String> atTitles = new ArrayList<>();
    public ArrayList<String> arrayURLs = new ArrayList<>();
    public String articleInfo="";
    ArrayAdapter arrayAdapter;
    ListView listView;


    public class DownloadTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;


            arrayURLs = new ArrayList<>();
            atTitles = new ArrayList<>();
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                JSONArray jsonArray = new JSONArray(result);
                atNbr = 20;
                for (int i = 0; i < 400; i++) {
                    if (atNbr > 0) {
                        String getId = jsonArray.getString(i);
                        url = new URL("https://hacker-news.firebaseio.com/v0/item/" + getId + ".json?print=pretty");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        in = urlConnection.getInputStream();
                        reader = new InputStreamReader(in);
                        data = reader.read();
                        articleInfo="";
                        while (data != -1) {
                            char part = (char) data;
                            articleInfo += part;

                            data = reader.read();
                        }


                    }
                    JSONObject jsonObject = new JSONObject(articleInfo);
                    String articleTitle = jsonObject.getString("title");
                    String articleURL = jsonObject.getString("url");

                    arrayURLs.add(articleURL);
                    atTitles.add(articleTitle);


                    atNbr--;
                    //STILL SCANNING and ADDING TO ARTICLEINFO
                }

                //ARTICLE INFO COMPLETED SCANNING ARTICLE INFO IS COMPLETE



                return articleInfo;


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            try{
//                JSONObject jsonObject = new JSONObject(articleInfo);
//                if (jsonObject.has("url")) {
//                    String articleTitle = jsonObject.getString("title");
//                    String articleURL = jsonObject.getString("url");
//
//                    arrayURLs.add(articleURL);
//                    atTitles.add(articleTitle);
//                }
//
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            String apiURL = "https://hacker-news.firebaseio.com/v0/topstories.json";

            DownloadTask task = new DownloadTask();// fetch the JSONARRAY and than fetch the JSONOBJECTS
            String executed = task.execute(apiURL).get();
//            for(int i =0;i<arrayURLs.size();i++) {
//                Log.i("articleURL", arrayURLs.get(i));
//                Log.i("ARTICLE TITLE:", atTitles.get(i));
//            }
            SQLiteDatabase db = this.openOrCreateDatabase("database", MODE_PRIVATE, null);
//            db.execSQL("CREATE TABLE IF NOT EXISTS urls (full_url VARCHAR)");
//            for(int i=0;i< arrayURLs.size();i++) {
//                db.execSQL("INSERT INTO urls(full_url) VALUES ('" +  arrayURLs.get(i) + "')");
//            }
//            db.execSQL("CREATE TABLE IF NOT EXISTS titles (full_title VARCHAR)");
//            for(int i=0;i<atTitles.size();i++) {
//                db.execSQL("INSERT INTO titles(full_title) VALUES ('" + atTitles.get(i) + "')");
//            }
              Cursor c = db.rawQuery("SELECT * from urls", null);
              int urlindex = c.getColumnIndex("full_url");
              c.moveToFirst();

              Cursor d = db.rawQuery("SELECT * from titles", null);
              int titleindex = d.getColumnIndex("full_title");
              d.moveToFirst();

              while(c!=null){
                  Log.i("full_url", c.getString(urlindex));
                  c.moveToNext();
              }
              while(d!=null){
                  Log.i("full_title", d.getString(titleindex));
                  d.moveToNext();
              }
//            db.execSQL("delete from urls");
//            db.execSQL("delete from titles");


            Log.i("SiZE: ", String.valueOf(arrayURLs.size()));
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, atTitles);
            listView = findViewById(R.id.listview);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent(MainActivity.this, ShowWeb.class);
                    intent.putExtra("url", arrayURLs.get(position));
                    startActivity(intent);

                }
            });

        }catch(InterruptedException e) {
            throw new RuntimeException(e);
        }catch(ExecutionException e) {
            throw new RuntimeException(e);
        }


    }


}

