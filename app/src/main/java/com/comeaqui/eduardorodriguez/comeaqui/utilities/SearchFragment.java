package com.comeaqui.eduardorodriguez.comeaqui.utilities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.comeaqui.eduardorodriguez.comeaqui.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText searchTextView;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchTextView = view.findViewById(R.id.search_text);
        ImageView searchClear = view.findViewById(R.id.search_clear);
        ImageView searchSearch = view.findViewById(R.id.search_image);

        searchClear.setScaleX(0);

        searchTextView.setOnFocusChangeListener((view1, hasFocus) -> {
            if (hasFocus) {
                searchTextView.animate().x(20).setDuration(200);
                animateSearch(searchSearch, searchClear);
            }
        });

        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchTextView.getText().toString().trim().length() > 0){
                    searchTextView.animate().x(20).setDuration(200);
                    animateSearch(searchSearch, searchClear);
                    mListener.onFragmentInteraction(searchTextView.getText().toString());
                } else {
                    searchTextView.animate().x(80).setDuration(200);
                    animateSearch(searchClear, searchSearch);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchClear.setOnClickListener(v -> searchTextView.setText(""));

        return view;
    }

    void animateSearch(ImageView i1, ImageView i2){
        i1.animate().scaleX(0).setDuration(200).withEndAction(() -> {
            i1.setVisibility(View.GONE);
        });
        i2.setVisibility(View.VISIBLE);
        i2.animate().scaleX(1).setDuration(200);
    }

    public void onButtonPressed(String message) {
        if (mListener != null) {
            mListener.onFragmentInteraction(message);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchFragment.OnFragmentInteractionListener) {
            mListener = (SearchFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String string);
    }
}
