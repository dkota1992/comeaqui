package com.comeaqui.eduardorodriguez.comeaqui.map.map_picker;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.place_autocomplete.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MapPickerFragment extends Fragment implements PlaceAutocompleteFragment.OnFragmentInteractionListener{

    private OnFragmentInteractionListener mListener;

    ConstraintLayout wholeView;
    ImageView shadow;
    ImageView hande;
    ImageView shadowPoint;
    public TextView pickedAdress;
    ConstraintLayout mapPickerPanView;

    PlaceAutocompleteFragment placeAutocompleteFragment;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    public boolean abled;
    String LOADING = "Loading...";

    public MapPickerFragment() {}
    public static MapPickerFragment newInstance() {
        return new MapPickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_picker, container, false);
        wholeView = view.findViewById(R.id.whole);
        mapPickerPanView = view.findViewById(R.id.map_picker_pan);
        shadow = view.findViewById(R.id.shadow);
        hande = view.findViewById(R.id.handle);
        shadowPoint = view.findViewById(R.id.shadow_point);
        pickedAdress = view.findViewById(R.id.pickedAdress);

        placeAutocompleteFragment = PlaceAutocompleteFragment.newInstance("", true);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.search_frame, placeAutocompleteFragment)
                .commit();
        wholeView.setVisibility(View.INVISIBLE);
        return view;
    }

    public void setAddressTextVisible(boolean visible){
        if (visible){

        } else {
            pickedAdress.setVisibility(View.GONE);
        }
    }

    public void getLocationFromGoogle(LatLng latLng){
        String latLngString = latLng.latitude + "," + latLng.longitude;
        String uri = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLngString + "&key=" + getResources().getString(R.string.google_key);
        tasks.add(new GetAsyncTask(uri).execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            pickedAdress.setVisibility(View.VISIBLE);
            pickedAdress.setText(LOADING);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.getNoCredentials(this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                JsonObject joo = new JsonParser().parse(response).getAsJsonObject();
                JsonArray jsonArray = joo.get("results").getAsJsonArray();
                if (jsonArray.size() > 0) {
                    JsonObject jo = jsonArray.get(0).getAsJsonObject();
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
                    pickedAdress.setText(address);
                    if (!address.equals(LOADING)){
                        mListener.refreshFragment(
                                address,
                                place_id,
                                lat,
                                lng,
                                address_elements
                        );
                    }
                }
            }
            super.onPostExecute(response);
        }
    }

    public void showList(boolean show){
        placeAutocompleteFragment.showList(show);
    }

    public void apearMapPicker(boolean apear){
        abled = apear;
        placeAutocompleteFragment.showSearchBox(apear);
        shadowPoint.setVisibility(apear ? View.VISIBLE: View.INVISIBLE);
        mapPickerPanView.setVisibility(apear ? View.VISIBLE: View.INVISIBLE);
        hande.setVisibility(apear ? View.VISIBLE: View.INVISIBLE);
        shadow.setVisibility(apear ? View.VISIBLE: View.INVISIBLE);
        wholeView.setVisibility(apear ? View.VISIBLE: View.INVISIBLE);
    }

    public void moveMapPicker(boolean up){
        int move = 50;
        int secs = move * 2;
        if (up) {
            shadowPoint.animate().alpha(0.5f).setDuration(secs);
            mapPickerPanView.animate().translationY(-move).setDuration(secs);
            hande.animate().translationY(-move).setDuration(secs);
            shadow.animate().translationY(-move * 2 / 3).setDuration(secs);
            shadow.animate().translationX(move * 1 / 3).setDuration(secs);
        } else {
            shadowPoint.animate().alpha(0).setDuration(secs);
            mapPickerPanView.animate().translationY(0).setDuration(secs);
            hande.animate().translationY(0).setDuration(secs);
            shadow.animate().translationY(0).setDuration(secs);
            shadow.animate().translationX(0).setDuration(secs);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException("The parent fragment must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListPlaceChosen(String address, String place_id, Double lat, Double lng, HashMap<String, String> address_elements) {
        mListener.onListPlaceChosen(address, place_id, lat, lng, address_elements);
    }


    @Override
    public void onPlacesAutocompleteChangeText(boolean isAddressValid) {

    }

    @Override
    public void closeButtonPressed() {
        mListener.closeButtonPressed();
    }

    @Override
    public void myLocationButton() {
        mListener.myLocationButton();
    }

    public interface OnFragmentInteractionListener extends PlaceAutocompleteFragment.OnFragmentInteractionListener{
        void refreshFragment(String address, String place_id, Double lat, Double lng, HashMap<String, String> address_elements);
    }

    @Override
    public void onDestroy() {
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        super.onDestroy();
    }
}
