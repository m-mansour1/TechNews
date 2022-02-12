package com.example.technews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class ShowWeb extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_web);
        String value="";
        webView = findViewById(R.id.WebView);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("url");
            //The key argument here must match with the other used in MainActivity
        }
        webView.loadUrl(value);

    }
}
