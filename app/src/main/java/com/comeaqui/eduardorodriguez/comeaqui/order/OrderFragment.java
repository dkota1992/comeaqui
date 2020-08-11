package com.comeaqui.eduardorodriguez.comeaqui.order;

import android.content.Intent;
import android.os.Bundle;

import com.comeaqui.eduardorodriguez.comeaqui.general.FoodLookActivity;
import com.comeaqui.eduardorodriguez.comeaqui.map.AddFoodActivity;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPost;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comeaqui.eduardorodriguez.comeaqui.R;

public class OrderFragment extends Fragment implements HostingFragment.OnListFragmentInteractionListener{

    FragmentManager fragmentManager;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        fragmentManager = getChildFragmentManager();
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new TestPagerAdapter(getChildFragmentManager()));
        TabLayout tabLayout = view.findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void goToPostLook(FoodPost foodPost) {
        Intent foodLook = new Intent(getContext(), FoodLookActivity.class);
        foodLook.putExtra("foodPostId", foodPost.id);
        getContext().startActivity(foodLook);
    }

    @Override
    public void goToPostEdit(FoodPost foodPost) {
        Intent foodLook = new Intent(getContext(), AddFoodActivity.class);
        foodLook.putExtra("foodPostId", foodPost.id);
        getContext().startActivity(foodLook);
    }

    static class TestPagerAdapter extends FragmentPagerAdapter {

        public TestPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment[] tabFragment = {GuestingFragment.newInstance(), HostingFragment.newInstance()};
            return tabFragment[position];
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = {"Guesting", "Hosting"};
            return titles[position];
        }
    }
}
