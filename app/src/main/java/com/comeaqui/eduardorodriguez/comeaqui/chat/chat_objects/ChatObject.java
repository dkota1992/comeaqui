package com.comeaqui.eduardorodriguez.comeaqui.chat.chat_objects;

import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatObject implements Serializable {
    public int id;
    public ArrayList<User> users;
    public MessageObject last_message;
    public String createdAt;
    public HashMap<Integer, Integer> userUnseenCount;

    public ChatObject(JsonObject jo){
        users = new ArrayList<>();
        userUnseenCount = new HashMap<>();

        id = jo.get("id").getAsInt();
        createdAt = jo.get("created_at").getAsString();
        for (JsonElement je: jo.get("users").getAsJsonArray()){
            users.add(new User(je.getAsJsonObject()));
        }

        try{
            last_message = new MessageObject(jo.get("last_message").getAsJsonObject());
        } catch (Exception ignored){}

        for (JsonElement je: jo.get("user_chat_status").getAsJsonArray()){
            JsonObject json = je.getAsJsonObject();
            userUnseenCount.put(json.get("user_id").getAsInt(), json.get("unseen_messages_count").getAsInt());
        }
    }
}
