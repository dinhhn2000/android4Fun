package com.example.notedemo;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

public class Note implements Serializable {
    private String header;
    private String content;
    private ArrayList<String> tags;
    private Date editTime;

    public Note(){
        this.header = "";
        this.content = "";
        this.tags = new ArrayList<>();
        this.tags.add("");
        this.editTime = new Date();
    }

    public Note(String header, String content, ArrayList<String> tags, Date editTime) {
        this.header = header;
        this.content = content;
        this.tags = tags;
        this.editTime = editTime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public String getHeader() {
        return header;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void addTags(String input) {
        tags.add(input);
    }

    public void resetTags() {
        tags.clear();
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }
}
