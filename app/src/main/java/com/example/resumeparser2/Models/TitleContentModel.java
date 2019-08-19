package com.example.resumeparser2.Models;

public class TitleContentModel {
    public String content;
    public String title;

    public TitleContentModel(String content, String title) {
        this.content = content;
        this.title = title;
    }

    public TitleContentModel(){

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
