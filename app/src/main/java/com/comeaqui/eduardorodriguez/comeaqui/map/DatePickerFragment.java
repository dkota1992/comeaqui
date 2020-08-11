package com.comeaqui.eduardorodriguez.comeaqui.map;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (getActivity() instanceof DatePickerDialog.OnDateSetListener){
            return new DatePickerDialog(getContext(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        } else{
            if (getParentFragment() instanceof DatePickerDialog.OnDateSetListener) {
                return new DatePickerDialog(getContext(), (DatePickerDialog.OnDateSetListener) getParentFragment(), year, month, day);
            }
        }
        return new DatePickerDialog(getContext(), (DatePickerDialog.OnDateSetListener) getParentFragment(), year, month, day);
    }
}
