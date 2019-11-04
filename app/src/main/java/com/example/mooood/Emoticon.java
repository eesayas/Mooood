package com.example.mooood;

public class Emoticon{

    private String emotionalState;
    private int imageLink;

    public Emoticon(String emotionalState, int imageLink){
        this.emotionalState = emotionalState;
        this.imageLink = imageLink;
    }

    public void setEmotionalState(String emotionalState){
        this.emotionalState = emotionalState;
    }

    public String getEmotionalState(){
        return this.emotionalState;
    }

    public void setImageLink(int imageLink){
        this.imageLink = imageLink;
    }

    public int getImageLink(){
        return this.imageLink;
    }

}
