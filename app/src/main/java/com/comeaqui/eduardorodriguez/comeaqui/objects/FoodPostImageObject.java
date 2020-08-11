package com.comeaqui.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class FoodPostImageObject implements Serializable {
        public int id;
        public String image;

    public FoodPostImageObject(JsonObject jo){
        id = jo.get("id").getAsInt();
        image = ImageStringProcessor.processString(jo.get("food_photo").getAsString());
    }
}