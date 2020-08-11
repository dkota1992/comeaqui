package com.comeaqui.eduardorodriguez.comeaqui.map.add_food;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;

public class WordLimitEditTextFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    EditText editText;
    TextView leterCount;
    View wholeView;

    int MAX_NUMBER_OF_LETTER = 202;
    String str;

    public WordLimitEditTextFragment() {}

    public static WordLimitEditTextFragment newInstance() {
        return  new WordLimitEditTextFragment();
    }

    public void setText(String text){
        editText.setText(text);
    }

    public void setErrorBackground(boolean error){
        if (error){
            wholeView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape_error));
        } else {
            wholeView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_limit_edit_text, container, false);
        editText = view.findViewById(R.id.text);
        leterCount = view.findViewById(R.id.leter_count);
        wholeView = view.findViewById(R.id.whole_word_limit_view);

        setEditText();
        return view;
    }

    void setEditText(){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                str = editText.getText().toString().trim();
                int letters = str.length();
                leterCount.setText(letters + "/" + MAX_NUMBER_OF_LETTER);
                mListener.onTextChanged(str);
                setErrorBackground(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    public interface OnFragmentInteractionListener {
        void onTextChanged(String text);
    }
}
