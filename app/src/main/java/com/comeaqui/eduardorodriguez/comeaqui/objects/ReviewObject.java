package com.comeaqui.eduardorodriguez.comeaqui.objects;

import com.comeaqui.eduardorodriguez.comeaqui.utilities.DateFormatting;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ReviewObject implements Serializable {
    public int id;
    public User owner;
    public FoodPost post;
    public String review;
    public String reason;
    public float rating;
    public ArrayList<ReviewReplyObject> replies;
    public String createdAt;

    public ReviewObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        review = jo.get("review").getAsString();
        reason = jo.get("star_reason").getAsString();
        rating = jo.get("rating").getAsFloat();

        replies = new ArrayList<>();
        if (jo.get("replies") != null){
            for (JsonElement je: jo.get("replies").getAsJsonArray()){
                try{
                    replies.add(new ReviewReplyObject(je.getAsJsonObject()));
                }catch (Exception ignore){}
            }
        }
        createdAt = DateFormatting.todayYesterdayWeekDay(jo.get("created_at").getAsString());
    }
}