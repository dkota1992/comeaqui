package com.comeaqui.eduardorodriguez.comeaqui.objects;

import com.comeaqui.eduardorodriguez.comeaqui.utilities.DateFormatting;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.io.Serializable;
import java.util.ArrayList;

public class FoodPost implements Serializable {
    public int id;
    public User owner;
    public String plate_name;

    public String formatted_address;
    public String place_id;
    public Double lat;
    public Double lng;
    public String street_n;
    public String route;
    public String administrative_area_level_2;
    public String administrative_area_level_1;
    public String country;
    public String postal_code;

    public int max_dinners;
    public int dinners_left;
    public String time_to_show;
    public String time_range;
    public String start_time;
    public String end_time;
    public int price;
    public String price_to_show;
    public String type;
    public String description;
    public String status;

    public ArrayList<FoodPostImageObject> images;

    public boolean favourite = false;
    public int favouriteId;
    public float rating;
    public boolean visible;

    FoodPost(){}

    public FoodPost(JsonObject jo){
        id = jo.get("id").getAsInt();
        owner = new User(jo.get("owner").getAsJsonObject());
        plate_name = jo.get("plate_name") instanceof JsonNull ? "" : jo.get("plate_name").getAsString();

        formatted_address = jo.get("formatted_address") instanceof JsonNull ? "" : jo.get("formatted_address").getAsString();
        place_id = jo.get("place_id") instanceof JsonNull ? "" : jo.get("place_id").getAsString();
        lat = jo.get("lat") instanceof JsonNull ? null : jo.get("lat").getAsDouble();
        lng = jo.get("lng") instanceof JsonNull ? null : jo.get("lng").getAsDouble();
        street_n = jo.get("street_n") instanceof JsonNull ? "" : jo.get("street_n").getAsString();
        route = jo.get("route") instanceof JsonNull ? "" : jo.get("route").getAsString();
        administrative_area_level_2 = jo.get("administrative_area_level_2") instanceof JsonNull ? "" : jo.get("administrative_area_level_2").getAsString();
        administrative_area_level_1 = jo.get("administrative_area_level_1") instanceof JsonNull ? "" : jo.get("administrative_area_level_1").getAsString();
        country = jo.get("country") instanceof JsonNull ? "" : jo.get("country").getAsString();
        postal_code = jo.get("postal_code") instanceof JsonNull ? "" : jo.get("postal_code").getAsString();

        start_time = jo.get("start_time") instanceof JsonNull ? "" : jo.get("start_time").getAsString();
        end_time = jo.get("end_time") instanceof JsonNull ? "" : jo.get("end_time").getAsString();
        try {time_range = DateFormatting.timeRange(start_time, end_time);} catch (Exception ignored){}
        time_to_show = jo.get("start_time") instanceof JsonNull && jo.get("end_time") instanceof JsonNull ? "" : DateFormatting.hhmmHappenedNowTodayYesterdayWeekDay(start_time, end_time);
        max_dinners = jo.get("max_dinners") instanceof JsonNull ? 0 : jo.get("max_dinners").getAsInt();
        dinners_left = jo.get("dinners_left").getAsInt();
        price = jo.get("price") instanceof JsonNull ? 0 :  jo.get("price").getAsInt();
        price_to_show = "$" + String.format("%.02f", (price / 100.f));
        type = jo.get("food_type") instanceof JsonNull ? "" : jo.get("food_type").getAsString();
        description = jo.get("description") instanceof JsonNull ? "" : jo.get("description").getAsString();


        try {status = jo.get("status").getAsString();} catch (Exception ignored){}

        images = new ArrayList<>();
        try {
            for (JsonElement je: jo.get("images").getAsJsonArray()){
                images.add(0, new FoodPostImageObject(je.getAsJsonObject()));
            }
        }catch (Exception ignore){}

        rating = jo.get("rating").getAsInt();
        visible = jo.get("visible").getAsBoolean();
    }
}