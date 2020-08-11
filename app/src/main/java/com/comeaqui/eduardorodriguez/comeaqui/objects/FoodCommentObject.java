package com.comeaqui.eduardorodriguez.comeaqui.objects;

import com.comeaqui.eduardorodriguez.comeaqui.general.food_post_comments.MyFoodCommentRecyclerViewAdapter;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.DateFormatting;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class FoodCommentObject implements Serializable {
    public int id;
    public User owner;
    public FoodCommentObject comment;
    public FoodPost post;
    public ArrayList<FoodCommentObject> replies;
    public HashMap<Integer, FoodCommentObject> repliesHashMap;
    public String message;
    public int votes_n;
    public Boolean is_user_up_vote;
    public boolean is_max_depth;
    public Integer extra_comments_in_list;
    public String createdAt;

    public FoodCommentObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        try{comment = new FoodCommentObject(jo.get("comment").getAsJsonObject());} catch (Exception e){};
        try{post = new FoodPost(jo.get("post").getAsJsonObject());} catch (Exception e){};
        message = jo.get("message").getAsString();

        replies = new ArrayList<>();
        repliesHashMap = new HashMap<>();
        try{
            for (JsonElement je : jo.get("replies").getAsJsonArray()) {
                try {
                    FoodCommentObject comment = new FoodCommentObject(je.getAsJsonObject());
                    replies.add(comment);
                    repliesHashMap.put(comment.id, comment);
                } catch (Exception e){
                    replies.get(replies.size() - 1).extra_comments_in_list = je.getAsInt();
                }

            }
        }catch (Exception e){};

        votes_n = jo.get("votes_n").getAsInt();
        try{is_user_up_vote = jo.get("is_user_up_vote").getAsBoolean();} catch (Exception e){};
        is_max_depth = jo.get("is_max_depth").getAsBoolean();
        createdAt = DateFormatting.hYesterdayWeekDay(jo.get("created_at").getAsString());
    }
}