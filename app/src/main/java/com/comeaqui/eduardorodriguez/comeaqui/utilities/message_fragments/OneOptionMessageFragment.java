package com.comeaqui.eduardorodriguez.comeaqui.utilities.message_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;

public class OneOptionMessageFragment extends Fragment {
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";

    private String title;
    private String message;

    Button continueButton;
    Button cancelButton;

    private OneOptionMessageFragment.OnFragmentInteractionListener mListener;

    public OneOptionMessageFragment() {}
    public static OneOptionMessageFragment newInstance(String title, String message) {
        OneOptionMessageFragment fragment = new OneOptionMessageFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(MESSAGE);
            title = getArguments().getString(TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_error_message, container, false);
        TextView titleView = view.findViewById(R.id.error_title);
        TextView messageView = view.findViewById(R.id.error_message);
        continueButton = view.findViewById(R.id.ok_button);

        titleView.setText(title);
        messageView.setText(message);

        setButtons();
        return view;
    }

    void setButtons(){
        continueButton.setOnClickListener(v -> {
            onButtonPressed();
        });
    }


    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OneOptionMessageFragment.OnFragmentInteractionListener) {
            mListener = (OneOptionMessageFragment.OnFragmentInteractionListener) context;
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
        void onFragmentInteraction();
    }
}
