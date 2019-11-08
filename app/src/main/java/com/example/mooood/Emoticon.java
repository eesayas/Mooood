package com.example.mooood;

import java.util.HashMap;
import java.util.Map;

public class Emoticon{

    private String emotionalState;
    private int imageLink;
    private HashMap<String, Integer> emoticonData = new HashMap<>();

    //this populates the hashmap with proper keys and values
    private void populateEmoticonData(int version){
        if(version == 1){
            emoticonData.put("HAPPY", R.drawable.mooood_logo);
            emoticonData.put("SAD", R.drawable.sad_cow_v1);
            emoticonData.put("LAUGHING", R.drawable.laughing_cow_v1);
            emoticonData.put("IN LOVE", R.drawable.in_love_cow_v1);
            emoticonData.put("ANGRY", R.drawable.angry_cow_v1);
            emoticonData.put("SICK", R.drawable.sick_cow_v1);
            emoticonData.put("AFRAID", R.drawable.afraid_cow_v1);

        } else if(version == 2){
            emoticonData.put("HAPPY", R.drawable.happy_cow_v2);
            emoticonData.put("SAD", R.drawable.sad_cow_v2);
            emoticonData.put("LAUGHING", R.drawable.laughing_cow_v2);
            emoticonData.put("IN LOVE", R.drawable.inlove_cow_v2);
            emoticonData.put("ANGRY", R.drawable.angry_cow_v2);
            emoticonData.put("SICK", R.drawable.sick_cow_v2);
            emoticonData.put("AFRAID", R.drawable.afraid_cow_v2);
        }

    }

    //constructor when you need emotionalState but only have imageLink
    public Emoticon( int imageLink, int version ){
        populateEmoticonData(version);

        this.imageLink = imageLink;
        for(Map.Entry<String, Integer> entry: emoticonData.entrySet()){
            if(this.imageLink == entry.getValue()){
                this.emotionalState = entry.getKey();
            }
        }

    }

    //constructor when you need imageLink but only have emotionalState
    public Emoticon( String emotionalState, int version ){
        populateEmoticonData(version);

        this.emotionalState = emotionalState;
        this.imageLink = emoticonData.get(emotionalState);
    }

    //getters and setters
    public String getEmotionalState() {
        return this.emotionalState;
    }

    public void setEmotionalState(String emotionalState) {
        this.emotionalState = emotionalState;
    }

    public int getImageLink() {
        return this.imageLink;
    }

    public void setImageLink(int imageLink) {
        this.imageLink = imageLink;
    }
}
