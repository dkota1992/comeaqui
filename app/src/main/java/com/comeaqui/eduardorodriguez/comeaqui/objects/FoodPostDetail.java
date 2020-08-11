package com.comeaqui.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;

public class FoodPostDetail extends FoodPost {
    public ArrayList<OrderObject> confirmedOrdersList;

    public FoodPostDetail(JsonObject jo){
        super(jo);

        confirmedOrdersList = new ArrayList<>();
        for (JsonElement je: jo.get("confirmed_orders").getAsJsonArray()){
            confirmedOrdersList.add(new OrderObject(je.getAsJsonObject()));
        }
    }
}
