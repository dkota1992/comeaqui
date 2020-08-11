package com.comeaqui.eduardorodriguez.comeaqui.utilities.message_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;

import static com.comeaqui.eduardorodriguez.comeaqui.R.color.canceled;
import static com.comeaqui.eduardorodriguez.comeaqui.R.color.colorPrimary;

public class TwoOptionsMessageFragment extends Fragment {
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String LEFT_BUTTON = "left";
    private static final String RIGHT_BUTTON = "right";
    private static final String MODE = "mode";

    private String title;
    private String message;
    private String leftButtonMessage;
    private String rightButtonMessage;
    private boolean cancelMode;

    ConstraintLayout background;
    CardView cardView;
    Button leftButton;
    Button rightButton;

    private OnFragmentInteractionListener mListener;

    public TwoOptionsMessageFragment() {}
    public static TwoOptionsMessageFragment newInstance(String title, String message, String leftButtonMessage, String rightButtonMessage, boolean cancelMode) {
        TwoOptionsMessageFragment fragment = new TwoOptionsMessageFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(TITLE, title);
        args.putString(LEFT_BUTTON, leftButtonMessage);
        args.putString(RIGHT_BUTTON, rightButtonMessage);
        args.putBoolean(MODE, cancelMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(MESSAGE);
            title = getArguments().getString(TITLE);
            leftButtonMessage = getArguments().getString(LEFT_BUTTON);
            rightButtonMessage = getArguments().getString(RIGHT_BUTTON);
            cancelMode = getArguments().getBoolean(MODE);
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
        View view = inflater.inflate(R.layout.fragment_two_options_message, container, false);
        TextView titleView = view.findViewById(R.id.error_title);
        TextView messageView = view.findViewById(R.id.error_message);
        background = view.findViewById(R.id.background);
        cardView = view.findViewById(R.id.card);
        leftButton = view.findViewById(R.id.left_button);
        rightButton = view.findViewById(R.id.right_button);

        titleView.setText(title);
        messageView.setText(message);
        leftButton.setText(leftButtonMessage);
        rightButton.setText(rightButtonMessage);

        if (cancelMode){
            leftButton.setTextColor(ContextCompat.getColor(getContext(), colorPrimary));
            rightButton.setTextColor(ContextCompat.getColor(getContext(), canceled));
        }

        background.setOnClickListener(v -> show(false));
        leftButton.setOnClickListener(v -> {
            mListener.leftButtonPressed();
            show(false);
        });
        rightButton.setOnClickListener(v -> {
            mListener.rightButtonPressed();
            show(false);
        });
        return view;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void leftButtonPressed();
        void rightButtonPressed();
    }
}
