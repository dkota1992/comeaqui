package com.comeaqui.eduardorodriguez.comeaqui.utilities;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;

public class RatingFragment extends Fragment {
    private static final String RATING = "rating";
    private static final String RATING_N = "rating_n";

    private float rating;
    private int ratingN;

    ImageView[] starArray;
    TextView ratingTextView;
    TextView ratingNTextView;

    public RatingFragment() {}

    public static Fragment newInstance(float rating, int ratingN) {
        Fragment fragment = new RatingFragment();
        Bundle args = new Bundle();
        args.putFloat(RATING, rating);
        args.putInt(RATING_N, ratingN);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rating = getArguments().getFloat(RATING);
            ratingN = getArguments().getInt(RATING_N);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        ratingTextView = view.findViewById(R.id.profile_rating);
        ratingNTextView = view.findViewById(R.id.rating_n);
        setStars(view);
        return view;
    }

    void setStars(View view){
        starArray = new ImageView[]{
                view.findViewById(R.id.star0),
                view.findViewById(R.id.star1),
                view.findViewById(R.id.star2),
                view.findViewById(R.id.star3),
                view.findViewById(R.id.star4)
        };

        for (int i = 0; i < starArray.length; i++){
            starArray[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star_empty));
        }

        for (int i = 0; i < (int) rating; i++){
            starArray[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star_filled));
        }

        double decimal = rating - Math.floor(rating);
        if (decimal >= 0.75){
            starArray[(int) rating].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star_filled));
        } else if (decimal >= 0.25) {
            starArray[(int) rating].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star_half_filled));
        }

        if (rating == 0){
            ratingTextView.setText("--");
            ratingNTextView.setText("(-)");
        } else {
            ratingTextView.setText(String.format("%.01f", rating) + "");
            ratingNTextView.setText("(" + ratingN + ")");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
