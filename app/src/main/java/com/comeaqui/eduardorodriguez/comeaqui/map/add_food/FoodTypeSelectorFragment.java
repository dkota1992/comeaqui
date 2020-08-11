package com.comeaqui.eduardorodriguez.comeaqui.map.add_food;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.comeaqui.eduardorodriguez.comeaqui.R;


public class FoodTypeSelectorFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    boolean[] pressed = {false, false, false, false, false, false, false};
    ImageView vegetarian;
    ImageView vegan;
    ImageView cereal;
    ImageView spicy;
    ImageView fish;
    ImageView meat;
    ImageView dairy;

    public FoodTypeSelectorFragment() {}
    public static FoodTypeSelectorFragment newInstance() {
        return new FoodTypeSelectorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setTypes(String types){
        for (int i = 0; i < types.length(); i++){
            if (types.charAt(i) == '1'){
                pressed[i] = true;
            }
        }
        initializeTypeViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_type_selector, container, false);

        vegetarian = view.findViewById(R.id.vegetarian);
        vegan = view.findViewById(R.id.vegan);
        cereal = view.findViewById(R.id.cereal);
        spicy = view.findViewById(R.id.spicy);
        fish =  view.findViewById(R.id.fish);
        meat = view.findViewById(R.id.meat);
        dairy = view.findViewById(R.id.dairy);

        setFoodTypes();
        return view;
    }

    void setFoodTypes(){
        String[] tags = {"vegan", "vegetarian", "contain gluten", "spicy", "fish", "meat", "contain diary"};
        vegetarian.setOnClickListener(v -> {
            if(!pressed[4] && !pressed[5]){
                pressed[0] = !pressed[0];
            } else {
                String message;
                if (pressed[4] && pressed[5]){
                    message = "It can not be vegetarian and contain fish or meat";
                } else if (pressed[4]){
                    message = "It can not be vegetarian and contain fish";
                } else {
                    message = "It can not be vegetarian and contain meat";
                }
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
            if(pressed[0])
                vegetarian.setImageResource(R.drawable.vegetarianfill);
            else
                vegetarian.setImageResource(R.drawable.vegetarian);
            mListener.onFragmentInteraction(pressed);
        });
        vegan.setOnClickListener(v -> {
            if(!pressed[4] && !pressed[5] && !pressed[6]){
                pressed[1] = !pressed[1];
            } else {
                String message;
                if (pressed[4] && pressed[5] && pressed[6]){
                    message = "It can not be vegetarian and contain fish or meat or diary";
                } else if (pressed[4] && pressed[5]){
                    message = "It can not be vegetarian and contain fish or meat";
                } else if (pressed[4] && pressed[6]){
                    message = "It can not be vegetarian and contain fish or diary";
                } else if (pressed[5] && pressed[6]){
                    message = "It can not be vegetarian and contain meat or diary";
                } else if (pressed[4]){
                    message = "It can not be vegetarian and contain fish";
                } else if (pressed[5]){
                    message = "It can not be vegetarian and contain meat";
                } else {
                    message = "It can not be vegetarian and contain diary";
                }
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
            if(pressed[1])
                vegan.setImageResource(R.drawable.veganfill);
            else
                vegan.setImageResource(R.drawable.vegan);
            mListener.onFragmentInteraction(pressed);
        });
        cereal.setOnClickListener(v -> {
            pressed[2] = !pressed[2];
            if(pressed[2])
                cereal.setImageResource(R.drawable.cerealfill);
            else
                cereal.setImageResource(R.drawable.cereal);
            mListener.onFragmentInteraction(pressed);
        });
        spicy.setOnClickListener(v -> {
            pressed[3] = !pressed[3];
            if(pressed[3])
                spicy.setImageResource(R.drawable.spicyfill);
            else
                spicy.setImageResource(R.drawable.spicy);
            mListener.onFragmentInteraction(pressed);
        });

        fish.setOnClickListener(v -> {
            if(!pressed[0] && !pressed[1]){
                pressed[4] = !pressed[4];
            } else {
                String message;
                if (pressed[0] && pressed[1]){
                    message = "It can not contain fish and be vegan or vegetarian";
                } else if (pressed[0]){
                    message = "It can not contain fish and be vegetarian";
                } else {
                    message = "It can not contain fish and be vegan";
                }
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
            if(pressed[4])
                fish.setImageResource(R.drawable.fishfill);
            else
                fish.setImageResource(R.drawable.fish);
            mListener.onFragmentInteraction(pressed);
        });
        meat.setOnClickListener(v -> {
            if(!pressed[0] && !pressed[1]){
                pressed[5] = !pressed[5];
            } else {
                String message;
                if (pressed[0] && pressed[1]){
                    message = "It can not contain meat and be vegan or vegetarian";
                } else if (pressed[0]){
                    message = "It can not contain meat and be vegetarian";
                } else {
                    message = "It can not contain meat and be vegan";
                }
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
            if(pressed[5])
                meat.setImageResource(R.drawable.meatfill);
            else
                meat.setImageResource(R.drawable.meat);
            mListener.onFragmentInteraction(pressed);
        });
        dairy.setOnClickListener(v -> {
            if(!pressed[1]){
                pressed[6] = !pressed[6];
            } else {
                String message;
                message = "It can not contain diary and be vegan";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
            if(pressed[6])
                dairy.setImageResource(R.drawable.dairyfill);
            else
                dairy.setImageResource(R.drawable.dairy);
            mListener.onFragmentInteraction(pressed);
        });
    }
    void initializeTypeViews(){
        if(pressed[0])
            vegetarian.setImageResource(R.drawable.vegetarianfill);
        if(pressed[1])
            vegan.setImageResource(R.drawable.veganfill);
        if(pressed[2])
            cereal.setImageResource(R.drawable.cerealfill);
        if(pressed[3])
            spicy.setImageResource(R.drawable.spicyfill);
        if(pressed[4])
            fish.setImageResource(R.drawable.fishfill);
        if(pressed[5])
            meat.setImageResource(R.drawable.meatfill);
        if(pressed[6])
            dairy.setImageResource(R.drawable.dairyfill);
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
        void onFragmentInteraction(boolean[] pressed);
    }
}
