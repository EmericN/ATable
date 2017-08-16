package com.emeric.nicot.atable;

/**
 * Created by Nicot Emeric on 14/08/2017.
 */

class MessageChat {

    private String content;
    private int side;

    public MessageChat(String content, int side){
        this.content=content;
        this.side=side;
    }

    public String getContent(){
        return content;
    }

    public int getSide(){
        return side;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setSide(int side){
        this.side = side;
    }
}
