package com.comeaqui.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

public class FoodPostReview extends FoodPost implements Serializable {
    public ArrayList<ReviewObject> reviews;
    public float rating;
    public String rating_to_show;

    public FoodPostReview(JsonObject jo){
        super(jo);
        rating = jo.get("rating").getAsInt();
        rating_to_show = rating == 0 ? "--" : String.format("%.02f", rating);

        try {
            reviews = new ArrayList<>();
            for (JsonElement je: jo.get("reviews").getAsJsonArray()){
                reviews.add(new ReviewObject(je.getAsJsonObject()));
            }
        } catch (Exception e){}
    }
}