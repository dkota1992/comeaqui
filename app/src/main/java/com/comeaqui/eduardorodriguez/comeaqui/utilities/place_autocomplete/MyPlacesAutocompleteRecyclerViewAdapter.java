package com.comeaqui.eduardorodriguez.comeaqui.utilities.place_autocomplete;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.server.Server;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MyPlacesAutocompleteRecyclerViewAdapter extends RecyclerView.Adapter<MyPlacesAutocompleteRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String[]> mValues;
    private final PlaceAutocompleteFragment f;
    private PlaceAutocompleteFragment.OnFragmentInteractionListener mListener;

    public MyPlacesAutocompleteRecyclerViewAdapter(ArrayList<String[]> items, PlaceAutocompleteFragment f, PlaceAutocompleteFragment.OnFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.f = f;
    }

    public void updateData(ArrayList<String[]> data){
        this.mValues = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_placesautocomplete, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.contentView.setText(holder.mItem[0]);
        holder.mView.setOnClickListener(v -> {
            f.setAddress(holder.mItem[0], holder.mItem[1]);
            JsonObject jo = getPlacesDetailFromGoogle(holder.mItem[1]);
            if (jo != null){
                JsonArray addss_components = jo.get("address_components").getAsJsonArray();
                String address = jo.get("formatted_address").getAsString();
                String place_id = jo.get("place_id").getAsString();
                JsonObject jsonLocation = jo.get("geometry").getAsJsonObject().get("location").getAsJsonObject();
                Double lat = jsonLocation.get("lat").getAsDouble();
                Double lng = jsonLocation.get("lng").getAsDouble();

                HashMap<String, String> address_elements = new HashMap<>();
                for (JsonElement je: addss_components){
                    address_elements.put(je.getAsJsonObject().get("types").getAsJsonArray().get(0).getAsString(), je.getAsJsonObject().get("long_name").getAsString());
                }
                mListener.onListPlaceChosen(
                        address,
                        place_id,
                        lat,
                        lng,
                        address_elements
                );
                f.setErrorBackground(false);
                f.placeClicked = true;
            }
        });
    }

    JsonObject getPlacesDetailFromGoogle(String placeId){
        String uri = "https://maps.googleapis.com/maps/api/place/details/json?input=bar&placeid=" + placeId + "&key=" + f.getResources().getString(R.string.google_key);
        Server gAPI = new Server(f.getContext(),"GET", uri);
        try {
            String jsonString = gAPI.execute().get(15, TimeUnit.SECONDS);
            if (jsonString != null) {
                JsonObject joo = new JsonParser().parse(jsonString).getAsJsonObject();
                JsonObject jsonLocation = joo.get("result").getAsJsonObject();
                return jsonLocation;

            }
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return (mValues != null) ? mValues.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView contentView;
        public String[] mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            contentView = view.findViewById(R.id.content);
        }
    }
}
