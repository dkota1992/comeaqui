package com.comeaqui.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class ReviewReplyObject implements Serializable {
    public int id;
    public User owner;
    public ReviewObject review;
    public String reply;
    public String createdAt;

    public ReviewReplyObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        review = new ReviewObject(jo.get("review").getAsJsonObject());
        reply = jo.get("reply").getAsString();
        createdAt = jo.get("created_at").getAsString();
    }
}