package com.emeric.nicot.atable.models;

public class MessageChat {

    private String content;
    private int side;

    public MessageChat(String content, int side) {
        this.content = content;
        this.side = side;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }
}
