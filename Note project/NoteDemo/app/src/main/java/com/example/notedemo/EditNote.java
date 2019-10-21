package com.example.notedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class EditNote extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note_layout);

        setTitle("Editor");

        // Get the transferred data from source activity.
        Intent intent = getIntent();
        final Note oldData = (Note) intent.getSerializableExtra("note_data");

        final EditText headerEditText = findViewById(R.id.headerEditText);
        final EditText tagEditText = findViewById(R.id.tagEditText);
        final EditText contentEditText = findViewById(R.id.contentEditText);
        TextView editTime = findViewById(R.id.editTimeTextView);
        Button saveBtn = findViewById(R.id.saveBtn);

        headerEditText.setText(oldData.getHeader());
        String oldTags = oldData.getTags().get(0);
        for (int i = 1; i < oldData.getTags().size(); i++)
            oldTags += "/" + oldData.getTags().get(i);
        tagEditText.setText(oldTags);
        contentEditText.setText(oldData.getContent());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        editTime.setText(simpleDate.format(oldData.getEditTime()));

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Get data from edit text
                String newHeader = headerEditText.getText().toString();
                String newContent = contentEditText.getText().toString();
                ArrayList<String> newTags = new ArrayList<>();

                String tags = tagEditText.getText().toString();
                StringTokenizer tokens = new StringTokenizer(tags, "/");
                while (tokens.countTokens() > 0) {
                    newTags.add(tokens.nextToken().trim());
                }

                DataBase db = new DataBase(getApplicationContext());
                Note newData = new Note(newHeader, newContent, newTags, new Date());

                db.update(oldData, newData);
                Intent intent = new Intent(EditNote.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
