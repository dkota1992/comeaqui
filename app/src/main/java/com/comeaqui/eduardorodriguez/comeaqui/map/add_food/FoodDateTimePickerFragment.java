package com.comeaqui.eduardorodriguez.comeaqui.map.add_food;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.map.DatePickerFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.DateFormatting;

import java.sql.Time;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class FoodDateTimePickerFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    LinearLayout timePicker;
    TextView timeTextView;
    TextView date;
    TextView startTime;
    TextView endTime;

    Date datePicked;
    Long startMilli;
    Long endMilli;

    public FoodDateTimePickerFragment() {}
    public static FoodDateTimePickerFragment newInstance() {
        return new FoodDateTimePickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setErrorBackground(){
        if (date.getText().toString().isEmpty())
            date.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape_error));
        if (startTime.getText().toString().isEmpty())
            startTime.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape_error));
        if (endTime.getText().toString().isEmpty())
            endTime.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape_error));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_time_picker, container, false);
        timePicker = view.findViewById(R.id.timePicker);
        timeTextView = view.findViewById(R.id.time_text);
        date = view.findViewById(R.id.date);
        startTime = view.findViewById(R.id.start_time);
        endTime = view.findViewById(R.id.end_time);

        setTimePickerLogic();
        getRidOfErrors();
        return view;
    }

    void getRidOfErrors(){
        getRidOfError(date);
        getRidOfError(startTime);
        getRidOfError(endTime);
    }

    public void setDateTime(String start_time, String end_time){
        if (!start_time.isEmpty()){
            try {
                Date startDate = DateFormatting.convertToDate(start_time);
                String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(startDate.getTime());
                date.setText(currentDateString);
                startTime.setText(DateFormatting.hPost(start_time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!end_time.isEmpty()){
            try {
                Date endDate = DateFormatting.convertToDate(end_time);
                String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(endDate.getTime());
                date.setText(currentDateString);
                endTime.setText(DateFormatting.hPost(end_time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    void getRidOfError(TextView view){
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s,int start, int count, int after){ }
            @Override
            public void onTextChanged ( final CharSequence s, int start, int before, int count){
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_shape));
            }
            @Override
            public void afterTextChanged ( final Editable s){}
        });
    }

    void setTimePickerLogic(){
        date.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getFragmentManager(), "date picker");
        });

        startTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getContext(), (timePicker, selectedHour, selectedMinute) ->{
                if (datePicked == null){
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    datePicked = c.getTime();
                    String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(datePicked);
                    date.setText(currentDateString);
                }
                if (endMilli == null){
                    endMilli = TimeUnit.HOURS.toMillis(22) + TimeUnit.MINUTES.toMillis(00);
                    endTime.setText("10:00 PM");
                }
                startMilli = TimeUnit.HOURS.toMillis(selectedHour) + TimeUnit.MINUTES.toMillis(selectedMinute);
                startTime.setText(getTime(selectedHour, selectedMinute));
                checkDateAndSend(
                        new Date(datePicked.getTime() + startMilli),
                        new Date(datePicked.getTime() + endMilli)
                );
            }, hour, minute, false);
            mTimePicker.setTitle("Start time");
            mTimePicker.show();
        });

        endTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getContext(), (timePicker, selectedHour, selectedMinute) ->{
                if (datePicked == null){
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    datePicked = c.getTime();
                    String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(datePicked);
                    date.setText(currentDateString);
                }
                if (startMilli == null){
                    startMilli = TimeUnit.HOURS.toMillis(19) + TimeUnit.MINUTES.toMillis(30);
                    startTime.setText("7:30 PM");
                }
                endMilli = TimeUnit.HOURS.toMillis(selectedHour) + TimeUnit.MINUTES.toMillis(selectedMinute);
                endTime.setText(getTime(selectedHour, selectedMinute));
                checkDateAndSend(
                        new Date(datePicked.getTime() + startMilli),
                        new Date(datePicked.getTime() + endMilli)
                );

            }, hour, minute, false);
            mTimePicker.setTitle("End time");
            mTimePicker.show();
        });
    }

    private String getTime(int hr,int min) {
        Time tme = new Time(hr,min,0);
        Format formatter = new SimpleDateFormat("h:mm a");
        return formatter.format(tme);
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        datePicked = c.getTime();
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(datePicked);
        date.setText(currentDateString);

        if (startMilli == null){
            startMilli = TimeUnit.HOURS.toMillis(19) + TimeUnit.MINUTES.toMillis(30);
            startTime.setText("7:30 PM");
        }
        if (endMilli == null){
            endMilli = TimeUnit.HOURS.toMillis(22) + TimeUnit.MINUTES.toMillis(00);
            endTime.setText("10:00 PM");
        }
        checkDateAndSend(
                new Date(datePicked.getTime() + startMilli),
                new Date(datePicked.getTime() + endMilli)
        );
    }



    private void checkDateAndSend(Date startDate, Date endDate){
        Date now = new Date(System.currentTimeMillis());
        Date newEndDate;
        if (endDate.getTime() < startDate.getTime()){
            newEndDate = new Date(endDate.getTime() + TimeUnit.DAYS.toMillis(1));
        } else {
            newEndDate = endDate;
        }
        timeTextView.setVisibility(View.GONE);
        if (System.currentTimeMillis() > newEndDate.getTime()){
            DateFormat formatter = new SimpleDateFormat("HH:mm a");
            formatter.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
            String dateFormatted = formatter.format(now);
            timeTextView.setVisibility(View.VISIBLE);
            timeTextView.setText("Please pick a end time greater than today at " + dateFormatted);
        } else {
            DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm a");
            formatter.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            mListener.onFragmentInteraction(format.format(startDate), format.format(newEndDate));
        }
    }

    void showTimePicker(boolean show){
        int duration = 200;
        if (show){
            timePicker.setScaleX(0);
            timePicker.setVisibility(View.VISIBLE);
            timePicker.animate().scaleX(1).setDuration(duration);
        } else {
            timePicker.animate().scaleX(0).setDuration(duration).withEndAction(() -> timePicker.setVisibility(View.GONE));
        }
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
        void onFragmentInteraction(String startDateTime, String endDateTime);
    }
}
