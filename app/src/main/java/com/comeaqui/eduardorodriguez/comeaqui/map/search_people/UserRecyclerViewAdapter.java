package com.comeaqui.eduardorodriguez.comeaqui.map.search_people;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodPost;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    ArrayList<User> mValues;
    StorageReference firebaseStorage;
    SearchPeopleActivity activity;

    public UserRecyclerViewAdapter(ArrayList<User> items, SearchPeopleActivity activity) {
        mValues = items;
        this.activity = activity;
    }

    public void addData(ArrayList<User> data){
        this.mValues = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user_element, parent, false);
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.name.setText(holder.mItem.first_name + " " + holder.mItem.last_name);
        holder.username.setText(holder.mItem.username);
        if (holder.mItem.bio != null){
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(holder.mItem.bio);
        } else {
            holder.description.setVisibility(View.GONE);
        }
        holder.mView.setOnClickListener(v -> {
            activity.onFragmentInteraction(holder.mItem);
        });

        if (!holder.mItem.profile_photo.contains("no-image")) {
            Glide.with(holder.mView.getContext()).load(holder.mItem.profile_photo).into(holder.image);
        } else {
            holder.image.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.no_profile_photo));
        }
    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size(): 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView name;
        public final TextView username;
        public final TextView description;
        public final ImageView image;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.name);
            username = view.findViewById(R.id.username);
            description = view.findViewById(R.id.description);
            image = view.findViewById(R.id.image);
        }
    }
}
