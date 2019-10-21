package com.example.notedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        setTitle("Search");

        final EditText headerSearchEditText = findViewById(R.id.headerSearchEditText);
        final EditText tagSearchEditText = findViewById(R.id.tagSearchEditText);
        Button headerSearchBtn = findViewById(R.id.headerSearchBtn);
        Button tagSearchBtn = findViewById(R.id.tagSearchBtn);

        headerSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> parameters = new ArrayList<>();
                String headers = headerSearchEditText.getText().toString();
                StringTokenizer tokens = new StringTokenizer(headers, "/");
                if(tokens.countTokens() <= 0) {
                    Toast.makeText(getApplicationContext(),"Please enter headers", Toast.LENGTH_SHORT).show();
                    return;
                }

                while (tokens.countTokens() > 0) {
                    String input = tokens.nextToken().trim();
                    for(int i = 0; i < input.length(); i++){
                        if(input.charAt(i) == '\'') {
                            input = new StringBuffer(input).insert(i + 1,"'").toString();
                            i++;
                        }
                    }
                    parameters.add(input);
                }

                DataBase db = new DataBase(getApplicationContext());
                ArrayList<Note> sendData =  db.getNotesByHeaders(parameters);

                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("note_data", sendData);
                startActivity(intent);
            }
        });

        tagSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> parameters = new ArrayList<>();
                String tags = tagSearchEditText.getText().toString();
                StringTokenizer tokens = new StringTokenizer(tags, "/");
                if(tokens.countTokens() <= 0) {
                    Toast.makeText(getApplicationContext(),"Please enter tags", Toast.LENGTH_SHORT).show();
                    return;
                }

                while (tokens.countTokens() > 0) {
                    String input = tokens.nextToken().trim();
                    for(int i = 0; i < input.length(); i++){
                        if(input.charAt(i) == '\'') {
                            input = new StringBuffer(input).insert(i + 1,"'").toString();
                            i++;
                        }
                    }
                    parameters.add(input);
                }

                DataBase db = new DataBase(getApplicationContext());
                ArrayList<Note> sendData =  db.getNotesByTags(parameters);

                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("note_data", sendData);
                startActivity(intent);
            }
        });
    }
}
