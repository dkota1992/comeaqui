package com.comeaqui.eduardorodriguez.comeaqui.map.search_food.filter_fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.behaviors.SlideHideBehavior;
import com.comeaqui.eduardorodriguez.comeaqui.map.add_food.FoodDateTimePickerFragment;
import com.comeaqui.eduardorodriguez.comeaqui.map.add_food.FoodTypeSelectorFragment;

import static com.comeaqui.eduardorodriguez.comeaqui.R.color.colorPrimaryLight;
import static com.comeaqui.eduardorodriguez.comeaqui.R.color.grey_light;
import static com.comeaqui.eduardorodriguez.comeaqui.R.color.secondary_text_default_material_light;

public class FilterFragment extends Fragment implements
        FoodTypeSelectorFragment.OnFragmentInteractionListener,
        FoodDateTimePickerFragment.OnFragmentInteractionListener,
        DatePickerDialog.OnDateSetListener,
        SlideHideBehavior.OnBehaviorListener
{
    private static final String SORT_OPTION = "sort";
    private static final String PRICE_OPTION = "price";
    private static final String START_TIME = "s_time";
    private static final String END_TIME = "e_time";
    private static final String DISTANCE = "distance";
    private static final String TYPE = "dietary";
    private static final String FILTER = "filter";
    private Integer sortOption;
    private Integer priceOption;
    private String startTime;
    private String endTime;
    private String dietary;
    private int distance;
    private int filter;

    private OnFragmentInteractionListener mListener;

    private CoordinatorLayout wholeView;
    private View background;
    private NestedScrollView cardView;
    private LinearLayout sortWhole;
    private LinearLayout priceWhole;
    private LinearLayout mealTimeWhole;
    private LinearLayout distanceWhole;
    private LinearLayout dietaryWhole;
    private Button applyFilterButton;

    private TextView distanceText;
    private SeekBar distanceSeekbar;

    private Button[] sortOptions;
    private Button[] priceOptions;

    FoodDateTimePickerFragment foodTimePickerFragment;
    FoodTypeSelectorFragment foodTypeSelectorFragment;

    public FilterFragment() {}

    public static FilterFragment newInstance(Integer sortOption, Integer priceOption,  String startTime, String endTime,  int distance,  String dietary,  int filter) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putInt(SORT_OPTION, sortOption == null ? 0 : sortOption);
        args.putInt(PRICE_OPTION, priceOption == null ? 0 : priceOption);
        args.putString(START_TIME, startTime);
        args.putString(END_TIME, endTime);
        args.putInt(DISTANCE, distance);
        args.putString(TYPE, dietary);
        args.putInt(FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sortOption = getArguments().getInt(SORT_OPTION);
            priceOption = getArguments().getInt(PRICE_OPTION);
            startTime = getArguments().getString(START_TIME);
            endTime = getArguments().getString(END_TIME);
            distance = getArguments().getInt(DISTANCE);
            dietary = getArguments().getString(TYPE);
            filter = getArguments().getInt(FILTER);
        }
    }

    void setFilterInfo(){
        if (sortOption != null){
            for (int j = 0; j < sortOptions.length; j++){
                if (j == sortOption){
                    sortOptions[j].setBackgroundColor(ContextCompat.getColor(getContext(), colorPrimaryLight));
                    sortOptions[j].setTextColor(Color.WHITE);
                } else {
                    sortOptions[j].setBackgroundColor(ContextCompat.getColor(getContext(), grey_light));
                    sortOptions[j].setTextColor(ContextCompat.getColor(getContext(), secondary_text_default_material_light));
                }
            }
        }
        if (priceOption != null){
            for (int j = 0; j < priceOptions.length; j++){
                if (j == priceOption){
                    priceOption = priceOption;
                    priceOptions[j].setBackgroundColor(ContextCompat.getColor(getContext(), colorPrimaryLight));
                    priceOptions[j].setTextColor(Color.WHITE);
                } else {
                    priceOptions[j].setBackgroundColor(ContextCompat.getColor(getContext(), grey_light));
                    priceOptions[j].setTextColor(ContextCompat.getColor(getContext(), secondary_text_default_material_light));
                }
            }
        }
        if (startTime != null && endTime != null) {
            foodTimePickerFragment.setDateTime(startTime, endTime);
        }
        if (dietary != null){
            foodTypeSelectorFragment.setTypes(dietary);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        wholeView = view.findViewById(R.id.whole);
        background = view.findViewById(R.id.background);
        cardView = view.findViewById(R.id.card_view);
        sortWhole = view.findViewById(R.id.sort_whole);
        priceWhole = view.findViewById(R.id.price_whole);
        mealTimeWhole = view.findViewById(R.id.meal_time_whole);
        distanceWhole = view.findViewById(R.id.distance_whole);
        dietaryWhole = view.findViewById(R.id.dietary_whole);
        applyFilterButton = view.findViewById(R.id.apply_filter_button);

        distanceText = view.findViewById(R.id.distance_text);
        distanceText.setText(distance + "m");
        distanceSeekbar = view.findViewById(R.id.distance_seekbar);
        setPriceSeekBar();

        sortOptions = new Button[]{
                view.findViewById(R.id.sort0),
                view.findViewById(R.id.sort1),
                view.findViewById(R.id.sort2)
        };
        priceOptions = new Button[]{
                view.findViewById(R.id.price0),
                view.findViewById(R.id.price1),
                view.findViewById(R.id.price2)
        };
        foodTimePickerFragment = FoodDateTimePickerFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.date_frame, foodTimePickerFragment)
                .commit();

        foodTypeSelectorFragment = FoodTypeSelectorFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.dietary_frame, foodTypeSelectorFragment)
                .commit();

        SlideHideBehavior.setListener(this);
        showFilter(true);

        setSortButtons();
        setPirceButtons();

        wholeView.setOnTouchListener((v, event) -> {
            showFilter(false);
            return true;
        });

        view.findViewById(R.id.close).setOnClickListener(v -> {
            showFilter(false);
        });

        applyFilterButton.setOnClickListener(v -> sendFilterOptions());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilterInfo();
    }

    public void showFilter(boolean show){
        int move = cardView.getMeasuredHeight() + ((CoordinatorLayout.LayoutParams) cardView.getLayoutParams()).bottomMargin * 2;
        if (show) {
            View[] filters = new View[]{sortWhole, priceWhole, mealTimeWhole, distanceWhole, dietaryWhole};
            for (View f : filters) {
                f.setVisibility(View.GONE);
            }
            if (filter > 4) {
                for (View f : filters) {
                    f.setVisibility(View.VISIBLE);
                }
            } else {
                filters[filter].setVisibility(View.VISIBLE);
            }

            wholeView.setVisibility(View.VISIBLE);
            background.animate().alpha(1).setDuration(250);
        } else {
            cardView.animate().translationY(move).setDuration(move / 4);
            background.animate().alpha(0).setDuration(move / 4).withEndAction(() -> {
                wholeView.setVisibility(View.INVISIBLE);
            });
        }
    }

    void setPriceSeekBar(){
        distanceSeekbar.setMax(2000);
        distanceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                distance = progress;
                String priceText = distance + "m";
                distanceText.setText(priceText);
                seekBar.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_shape));
            }
        });
        distanceSeekbar.setProgress(distance);
    }

    void sendFilterOptions(){
        switch (filter){
            case 0:
                mListener.onSort(sortOption);
                break;
            case 1:
                mListener.onPrice(priceOption);
                break;
            case 2:
                if (startTime != null && !startTime.isEmpty() && endTime != null && !endTime.isEmpty()){
                    mListener.onMealTime(startTime, endTime);
                }
                break;
            case 3:
                mListener.onDistance(distance);
                break;
            case 4:
                if (dietary != null && !dietary.isEmpty()){
                    mListener.onDietary(dietary);
                }
                break;
            case 5:
                mListener.onSort(sortOption);
                mListener.onPrice(priceOption);
                if (startTime != null && !startTime.isEmpty() && endTime != null && !endTime.isEmpty()){
                    mListener.onMealTime(startTime, endTime);
                }
                mListener.onDistance(distance);
                if (dietary != null && !dietary.isEmpty()){
                    mListener.onDietary(dietary);
                }
                break;
        }
        mListener.onApplyFilterClicked();
        showFilter(false);
    }

    void setSortButtons(){
        for (int i = 0; i < sortOptions.length; i++){
            final int finalI = i;
            sortOptions[i].setOnClickListener(v -> {
                for (int j = 0; j < sortOptions.length; j++){
                    if (j == finalI){
                        sortOption = finalI;
                        sortOptions[j].setBackgroundColor(ContextCompat.getColor(getContext(), colorPrimaryLight));
                        sortOptions[j].setTextColor(Color.WHITE);
                    } else {
                        sortOptions[j].setBackgroundColor(ContextCompat.getColor(getContext(), grey_light));
                        sortOptions[j].setTextColor(ContextCompat.getColor(getContext(), secondary_text_default_material_light));
                    }
                }
            });
        }
    }

    void setPirceButtons(){
        for (int i = 0; i < priceOptions.length; i++){
            final int finalI = i;
            priceOptions[i].setOnClickListener(v -> {
                for (int j = 0; j < priceOptions.length; j++){
                    if (j == finalI){
                        priceOption = finalI;
                        priceOptions[j].setBackgroundColor(ContextCompat.getColor(getContext(), colorPrimaryLight));
                        priceOptions[j].setTextColor(Color.WHITE);
                    } else {
                        priceOptions[j].setBackgroundColor(ContextCompat.getColor(getContext(), grey_light));
                        priceOptions[j].setTextColor(ContextCompat.getColor(getContext(), secondary_text_default_material_light));
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) getParentFragment();
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

    @Override
    public void onFragmentInteraction(boolean[] pressed) {
        StringBuilder s = new StringBuilder();
        for (boolean b: pressed){
            s.append(b ? "1" : "0");
        }
        dietary = s.toString();
    }

    @Override
    public void onFragmentInteraction(String startDateTime, String endDateTime) {
        this.startTime = startDateTime;
        this.endTime= endDateTime;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        foodTimePickerFragment.onDateSet(view, year, month, dayOfMonth);
    }

    @Override
    public void onCloseBehavior() {
        background.animate().alpha(0).setDuration(100).withEndAction(() -> {
            wholeView.setVisibility(View.GONE);
        });
    }

    public interface OnFragmentInteractionListener {
        void onApplyFilterClicked();
        void onSort(int option);
        void onPrice(int option);
        void onMealTime(String startDateTime, String endDateTime);
        void onDistance(int distance);
        void onDietary(String dietary);
    }
}
