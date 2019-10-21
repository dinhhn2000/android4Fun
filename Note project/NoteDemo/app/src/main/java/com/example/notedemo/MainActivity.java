package com.example.notedemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        renderListView(false, builder);

//        Floating Add Btn
        FloatingActionButton addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditNote.class);
                ArrayList<String> mockTagList = new ArrayList<>();
                mockTagList.add("");
                Note sendData = new Note("", "", mockTagList, new Date());
                intent.putExtra("note_data", sendData);
                startActivity(intent);
            }
        });

//    Floating Search Btn
        FloatingActionButton searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

//        Swipe refresh layout
        final SwipeRefreshLayout refreshLayout = findViewById(R.id.SwipeLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                renderListView(true, builder);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void renderListView(boolean isRefreshing, final AlertDialog.Builder builder) {
        final ListView listViewNote = findViewById(R.id.notesList);
        final DataBase db = new DataBase(this);

//        List view
        ArrayList<Note> arrNote = db.getAllNote();
//        Go to Edit Note Activity if do not have any note
        if (arrNote.size() == 0) {
            Intent intent = new Intent(MainActivity.this, EditNote.class);
            intent.putExtra("note_data", new Note());
            startActivity(intent);
        }

//        Change arrNote when receive from other intent
        if (!isRefreshing) {
            Intent intent = getIntent();
            if (intent.hasExtra("note_data")) {
                arrNote = (ArrayList<Note>) intent.getSerializableExtra("note_data");
            } else {
                if (arrNote.size() == 0)
                    Toast.makeText(getApplicationContext(), "Cannot find any note", Toast.LENGTH_SHORT).show();
            }
        }
        final CustomNoteAdapter customAdapter = new CustomNoteAdapter(this, R.layout.note_list_element, arrNote);
        final ArrayList<Note> finalArrNote = arrNote;
        listViewNote.setAdapter(customAdapter);

        listViewNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditNote.class);
                Note sendData = finalArrNote.get(position);
                intent.putExtra("note_data", sendData);
                startActivity(intent);
            }
        });
        listViewNote.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                builder.setTitle("Delete confirm");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        db.deleteNote(finalArrNote.get(position));
                        renderListView(true, builder);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });
    }

}
