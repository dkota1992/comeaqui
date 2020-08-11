package com.comeaqui.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.io.Serializable;
import java.util.TimeZone;

public class User implements Serializable {
    public int id;
    public String username;
    public String email;
    public String first_name;
    public String last_name;
    public String bio;
    public String phone_number = "";
    public String phone_code = "";
    public String profile_photo;
    public String is_active;
    public String is_admin;
    public String background_photo;
    public float rating;
    public int ratingN;
    public String timeZone = TimeZone.getDefault().getID();

    public User(JsonObject jo){
        id = jo.get("id").getAsInt();
        email = jo.get("email").getAsString();
        username = jo.get("username").getAsString();
        first_name = jo.get("first_name").getAsString();
        last_name = jo.get("last_name").getAsString();
        bio = jo.get("bio").getAsString();
        try{
            phone_number = jo.get("phone_number").getAsString();
            phone_code = jo.get("phone_number").getAsString();
        } catch (Exception ignored){}
        profile_photo = jo.get("profile_photo") instanceof JsonNull ? "no-image" : ImageStringProcessor.processString(jo.get("profile_photo").getAsString());
        background_photo = jo.get("background_photo") instanceof JsonNull ? "no-image" : ImageStringProcessor.processString(jo.get("background_photo").getAsString());
        is_active = jo.get("is_active").getAsString();
        is_admin = jo.get("is_admin").getAsString();
        rating = jo.get("rating").getAsFloat();
        ratingN = jo.get("rating_n").getAsInt();
    }
}
