package com.comeaqui.eduardorodriguez.comeaqui.general.attend_message;

import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;

public class AttendFragment extends Fragment {
    private static final String DINNERS_LEFT = "dinners";
    private static final String PRICE = "price";
    private int dinners_left;
    private int price;
    private OnFragmentInteractionListener mListener;

    ConstraintLayout background;
    CardView cardView;
    TextView addGuestsText;
    Button minusButton;
    Button plusButton;
    Button confirmAttendButton;
    TextView priceView;

    int additionalGuests;

    public AttendFragment() {}

    public static AttendFragment newInstance(int dinnersLeft, int price) {
        AttendFragment fragment = new AttendFragment();
        Bundle args = new Bundle();
        args.putInt(DINNERS_LEFT, dinnersLeft);
        args.putInt(PRICE, price);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dinners_left = getArguments().getInt(DINNERS_LEFT);
            price = getArguments().getInt(PRICE);
        }
    }

    public void show(boolean show){
        if (show){
            cardView.setScaleX(0);
            cardView.setScaleY(0);
            background.setVisibility(View.VISIBLE);
            cardView.animate().scaleX(1).setDuration(100);
            cardView.animate().scaleY(1).setDuration(100);
        } else {
            cardView.setScaleX(1);
            cardView.setScaleY(1);
            cardView.animate().scaleX(0).setDuration(100);
            cardView.animate().scaleY(0).setDuration(100).withEndAction(() -> background.setVisibility(View.GONE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attend, container, false);
        background = view.findViewById(R.id.background);
        cardView = view.findViewById(R.id.card);
        addGuestsText = view.findViewById(R.id.add_guests_text);
        plusButton = view.findViewById(R.id.plus_button);
        minusButton = view.findViewById(R.id.minus_button);
        confirmAttendButton = view.findViewById(R.id.confirm_button);
        priceView = view.findViewById(R.id.price);

        background.setOnClickListener(v -> show(false));
        plusButton.setOnClickListener(v -> {
            if (additionalGuests < dinners_left - 1){
                additionalGuests++;
                priceView.setText("$" + String.format("%.02f", (price * (1 + additionalGuests)) / 100.f));
            }
            addGuestsText.setText("" + additionalGuests);
        });
        minusButton.setOnClickListener(v -> {
            if (additionalGuests > 0){
                additionalGuests--;
                priceView.setText("$" + String.format("%.02f", (price * (1 + additionalGuests)) / 100.f));
            }
            addGuestsText.setText("" + additionalGuests);
        });
        confirmAttendButton.setOnClickListener(v -> {
            mListener.onConfirmAttend(additionalGuests);
            show(false);
        });

        priceView.setText("$" + price / 100.f);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
        void onConfirmAttend(int additionalGuests);
    }
}
