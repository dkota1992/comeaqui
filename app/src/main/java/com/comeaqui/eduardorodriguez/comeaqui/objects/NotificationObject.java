package com.comeaqui.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class NotificationObject implements Serializable {
    public int id;
    public User owner;
    public User from_user;
    public String type;
    public String title;
    public String body;
    public String extra;
    public String createdAt;
    public int type_id;

    public NotificationObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        from_user = new User(jo.get("from_user").getAsJsonObject());
        type = jo.get("type").getAsString();
        type_id = jo.get("type_id").getAsInt();
        title = jo.get("title").getAsString();
        body = jo.get("body").getAsString();
        extra = jo.get("extra").getAsString();
        createdAt = jo.get("created_at").getAsString();
    }
}
