package com.comeaqui.eduardorodriguez.comeaqui.utilities.place_autocomplete;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class PlaceAutocompleteFragment extends Fragment {
    private static final String ADDRESS = "address";
    private static final String WITH_CLOSE = "close";
    private String address;
    private boolean searchMode;
    private OnFragmentInteractionListener mListener;


    View view;
    EditText addressView;
    TextView loadingView;
    RecyclerView recyclerView;
    View wholePlaceACView;
    ImageButton myLocationButton;
    ImageButton closeButton;
    ImageButton deleteText;

    boolean placeClicked = false;

    MyPlacesAutocompleteRecyclerViewAdapter adapter;
    private static ArrayList<String[]> data;

    static long last_text_edit = 0;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    public PlaceAutocompleteFragment() {}

    public static PlaceAutocompleteFragment newInstance(String address, boolean searchMode) {
        PlaceAutocompleteFragment fragment = new PlaceAutocompleteFragment();
        Bundle args = new Bundle();
        args.putString(ADDRESS, address);
        args.putBoolean(WITH_CLOSE, searchMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            address = getArguments().getString(ADDRESS);
            searchMode = getArguments().getBoolean(WITH_CLOSE);
        }
    }

    public void showSearchBox(boolean show){
        if (show){
            wholePlaceACView.setVisibility(View.VISIBLE);
        } else {
            hideKeyboard();
            wholePlaceACView.setVisibility(View.GONE);
        }
    }

    public void showList(boolean show){
        if (!show){
            hideKeyboard();
        }
        if (show && !placeClicked){
            recyclerView.setVisibility(View.VISIBLE);
        } else{
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void setErrorBackground(boolean error){
        if (error){
            wholePlaceACView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape_error));
        } else {
            wholePlaceACView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_place_autocomplete, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        addressView = view.findViewById(R.id.address);
        loadingView = view.findViewById(R.id.loading_places);
        recyclerView = view.findViewById(R.id.places_list);
        wholePlaceACView = view.findViewById(R.id.wholePlaceACView);
        myLocationButton = view.findViewById(R.id.my_locatoin_button);
        closeButton = view.findViewById(R.id.close_button);
        deleteText = view.findViewById(R.id.delete_text);

        loadingView.setVisibility(View.GONE);
        addressView.setText(address);
        addressView.setText(address);

        myLocationButton.setVisibility(searchMode ? View.VISIBLE: View.GONE);
        closeButton.setVisibility(searchMode ? View.VISIBLE: View.GONE);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new MyPlacesAutocompleteRecyclerViewAdapter(data, this, mListener);
        recyclerView.setAdapter(adapter);

        myLocationButton.setOnClickListener(v -> mListener.myLocationButton());
        closeButton.setOnClickListener(v -> mListener.closeButtonPressed());
        detectTypingAndSetLocationPrediction();
        deleteText.setOnClickListener(v -> addressView.setText(""));
        return view;
    }

    public String[] createStringArray(JsonObject jo){
        String description = jo.get("description").getAsNumber().toString();
        String place_id = jo.get("place_id").getAsNumber().toString();
        String[] add = new String[]{description, place_id};
        return add;
    }

    public void makeList(String jsonString){
        try {
            data = new ArrayList<>();
            JsonParser parser = new JsonParser();
            JsonObject joo = parser.parse(jsonString).getAsJsonObject();
            JsonArray jsonArray = joo.get("predictions").getAsJsonArray();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                data.add(createStringArray(jo));
            }
            adapter.updateData(data);
            adapter.notifyDataSetChanged();

            recyclerView.hasPendingAdapterUpdates();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void showLoading(boolean show){
        if (show){
            loadingView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            loadingView.setVisibility(View.GONE);
            if (!placeClicked)
                recyclerView.setVisibility(View.VISIBLE);
        }
    }

    void getPlacesListFromGoogle(){
        loadingView.setText("Loading...");
        String uri = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + addressView.getText().toString() +
                "&types=geocode&language=en&key=" + getResources().getString(R.string.google_key);
        showLoading(true);
        tasks.add(new GetAsyncTask(uri).execute());
    }
    private class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            showLoading(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                makeList(response);
                showLoading(false);
            } else {
                loadingView.setText("No results");
            }
            showLoading(false);
            super.onPostExecute(response);
        }

    }

    private void detectTypingAndSetLocationPrediction(){

        final long delay = 1000;
        final Handler handler = new Handler();
        final Runnable input_finish_checker = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                getPlacesListFromGoogle();
            }
        };

        addressView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s,int start, int count,
                                           int after){
            }
            @Override
            public void onTextChanged ( final CharSequence s, int start, int before,
                                        int count){
                //You need to remove this to run only once

                showList(true);
                mListener.onPlacesAutocompleteChangeText(placeClicked);
                handler.removeCallbacks(input_finish_checker);
                placeClicked = false;
            }
            @Override
            public void afterTextChanged ( final Editable s){
                //avoid triggering event when text is empty
                if (s.length() > 0) {
                    deleteText.setVisibility(View.VISIBLE);
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                } else {
                    deleteText.setVisibility(View.GONE);
                    showList(false);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            if (getParentFragment() instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) getParentFragment();
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    public void setAddress(String text, String id){
        addressView.setText(text);
        placeClicked = true;
        mListener.onPlacesAutocompleteChangeText(placeClicked);
    }

    private void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onListPlaceChosen(String address, String place_id, Double lat, Double lng, HashMap<String, String> address_elements);
        void onPlacesAutocompleteChangeText(boolean isAddressValid);
        void closeButtonPressed();
        void myLocationButton();
    }

}
