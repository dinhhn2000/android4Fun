package com.example.notedemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomNoteAdapter extends ArrayAdapter<Note> {
    private Context context;
    private int resource;
    private List<Note> arrNote;

    public CustomNoteAdapter(Context context, int resource, ArrayList<Note> arrNote) {
        super(context, resource, arrNote);
        this.context = context;
        this.resource = resource;
        this.arrNote = arrNote;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.note_list_element, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.header = convertView.findViewById(R.id.header);
            viewHolder.editTime = convertView.findViewById(R.id.edit_time);
            viewHolder.tags = convertView.findViewById(R.id.tags);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Note Note = arrNote.get(position);
        viewHolder.header.setText(Note.getHeader());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        viewHolder.editTime.setText(simpleDate.format(Note.getEditTime()));
        StringBuilder tags = new StringBuilder();

        int arrSize = Note.getTags().size();
        for (int i = 0; i < arrSize; i++) {
            tags.append(Note.getTags().get(i));
            if (i < arrSize - 1)
                tags.append("_");
        }
        viewHolder.tags.setText(tags.toString());
        return convertView;
    }

    public class ViewHolder {
        TextView header;
        TextView editTime;
        TextView tags;
    }
}
