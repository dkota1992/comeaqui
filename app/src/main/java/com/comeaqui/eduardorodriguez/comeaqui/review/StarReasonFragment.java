package com.comeaqui.eduardorodriguez.comeaqui.review;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;

import static com.comeaqui.eduardorodriguez.comeaqui.R.color.secondary_text_default_material_light;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StarReasonFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StarReasonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StarReasonFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    TextView rateMessage1;
    TextView rateMessage2;
    TextView reason0;
    TextView reason1;
    TextView reason2;
    TextView reason3;
    LinearLayout onceRateView;
    EditText review;

    int rating = 5;
    boolean[] reasonB = {false, false, false, false};

    public StarReasonFragment() {}
    public static StarReasonFragment newInstance() {
        StarReasonFragment fragment = new StarReasonFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_star_reason, container, false);
        rateMessage1 = view.findViewById(R.id.rate_message1);
        rateMessage2 = view.findViewById(R.id.rate_message2);
        reason0 = view.findViewById(R.id.reason1);
        reason1 = view.findViewById(R.id.reason2);
        reason2 = view.findViewById(R.id.reason3);
        reason3 = view.findViewById(R.id.reason4);
        onceRateView = view.findViewById(R.id.once_rate_view);
        review = view.findViewById(R.id.review);
        setStars(view);
        setEditTextWatcher();
        setReasonFunctionality();
        return view;
    }

    void setStars(View view){
        ImageView[] starArray = new ImageView[]{
                view.findViewById(R.id.star0),
                view.findViewById(R.id.star1),
                view.findViewById(R.id.star2),
                view.findViewById(R.id.star3),
                view.findViewById(R.id.star4)
        };

        for (int i = 0; i < starArray.length; i++){
            final int finalI = i;
            ImageView star = starArray[i];
            star.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star_empty));
            star.setOnClickListener(v -> {

                rating = finalI + 1;
                mListener.onRating(rating);
                onceRateView.setVisibility(View.VISIBLE);
                int j = 0;
                while (j <= finalI){
                    starArray[j].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star_filled));
                    j++;
                }
                while (j < starArray.length){
                    starArray[j].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star_empty));
                    j++;
                }
                if (finalI == 4){
                    changeReasonText(true, finalI);
                } else {
                    changeReasonText(false, finalI);
                }
                mListener.onHasToScrollIfNeeded();
                mListener.onFragmentInteraction(rating, reasonB, review.getText().toString());
            });
        }
    }

    void changeReasonText(boolean good, int i){
        String[] rateMessage = {"AWFUL", "BAD", "INDIFFERENT", "COULD BE BETTER", "GREAT"};
        if (good){
            rateMessage1.setText(rateMessage[i]);
            rateMessage2.setText("Great! What did you like the most?");
            reason0.setText("More than better");
            reason1.setText("She/he was nice");
            reason2.setText("Clean house");
            reason3.setText("Good conversation");
        } else {
            rateMessage1.setText(rateMessage[i]);
            rateMessage2.setText("We regret hearing that. What was the issue?");
            reason0.setText("Problem with host");
            reason1.setText("Problem with food");
            reason2.setText("Problem with cleaning");
            reason3.setText("Problem with payment");
        }

    }

    void setReasonFunctionality(){
        TextView[] reasons = new TextView[]{
                reason0,
                reason1,
                reason2,
                reason3
        };

        for (int i = 0; i < reasons.length; i++){
            TextView reason = reasons[i];
            final int finalI = i;
            reason.setOnClickListener(v -> {
                reasonB[finalI] = !reasonB[finalI];
                mListener.onFragmentInteraction(rating, reasonB, review.getText().toString());
                if (reasonB[finalI]){
                    reason.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.box_primary_color));
                    reason.setTextColor(Color.WHITE);
                } else {
                    reason.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.box_thik_stroke_grey));
                    reason.setTextColor(ContextCompat.getColor(getContext(), secondary_text_default_material_light));
                }
            });
        }
    }

    void setEditTextWatcher(){
        review.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mListener.onHasToScrollIfNeeded();
                mListener.onFragmentInteraction(rating, reasonB, review.getText().toString());
            }
        });
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int rating, boolean[] reasonB, String review);
        void onHasToScrollIfNeeded();
        void onRating(int rating);
    }
}
