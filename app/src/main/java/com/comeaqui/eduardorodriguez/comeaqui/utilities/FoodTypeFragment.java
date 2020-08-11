package com.comeaqui.eduardorodriguez.comeaqui.utilities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.comeaqui.eduardorodriguez.comeaqui.R;

import java.util.ArrayList;

public class FoodTypeFragment extends Fragment {

    View view;

    public FoodTypeFragment() {
        // Required empty public constructor
    }

    public static FoodTypeFragment newInstance(String type) {
        FoodTypeFragment fragment = new FoodTypeFragment();
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        fragment.setArguments(args);
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
        view = inflater.inflate(R.layout.fragment_food_type, container, false);
        setTypes(getArguments().getString("type"));
        return view;
    }
    void setTypes(String types){
        ImageView[] imageViews = new ImageView[]{
                view.findViewById(R.id.vegetarian),
                view.findViewById(R.id.vegan),
                view.findViewById(R.id.cereal),
                view.findViewById(R.id.spicy),
                view.findViewById(R.id.fish),
                view.findViewById(R.id.meat),
                view.findViewById(R.id.dairy)
        };
        ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
        for (ImageView imageView: imageViews){
            imageView.setVisibility(View.GONE);
            imageViewArrayList.add(imageView);
        }
        int[] resources = new int[]{
                R.drawable.vegetarianfill,
                R.drawable.veganfill,
                R.drawable.cerealfill,
                R.drawable.spicyfill,
                R.drawable.fishfill,
                R.drawable.meatfill,
                R.drawable.dairyfill,
        };
        for (int i = 0; i < types.length(); i++){
            if (types.charAt(i) == '1'){
                imageViewArrayList.get(i).setImageResource(resources[i]);
                imageViewArrayList.get(i).setVisibility(View.VISIBLE);
            }
        }
    }
}
