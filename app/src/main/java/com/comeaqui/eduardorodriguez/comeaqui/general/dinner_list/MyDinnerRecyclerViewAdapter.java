package com.comeaqui.eduardorodriguez.comeaqui.general.dinner_list;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.general.dinner_list.DinnerFragment.OnListFragmentInteractionListener;
import com.comeaqui.eduardorodriguez.comeaqui.objects.OrderObject;

import java.util.List;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class MyDinnerRecyclerViewAdapter extends RecyclerView.Adapter<MyDinnerRecyclerViewAdapter.ViewHolder> {

    private final List<OrderObject> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyDinnerRecyclerViewAdapter(List<OrderObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dinner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.dinnerName.setText(holder.mItem.owner.first_name + ", " + holder.mItem.owner.last_name);

        if(!holder.mItem.owner.profile_photo.contains("no-image")) {
            Glide.with(holder.mView.getContext()).load(holder.mItem.owner.profile_photo).into(holder.dinnerImage);
        } else {
            holder.dinnerImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.no_profile_photo));
        }

        if (holder.mItem.owner.id == USER.id){
            holder.dinnerChat.setVisibility(View.GONE);
        } else {
            holder.dinnerChat.setVisibility(View.VISIBLE);
        }

        if (holder.mItem.additionalGuests > 0){
            holder.plus.setVisibility(View.VISIBLE);
            holder.plus.setText("+" + holder.mItem.additionalGuests);
        } else {
            holder.plus.setVisibility(View.GONE);
        }

        holder.dinnerChat.setOnClickListener(v -> {
            if (null != mListener){
                mListener.onChatInteraction(holder.mItem);
            }
        });

        holder.mView.setOnClickListener(v -> {
            if (null != mListener){
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }



    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView dinnerName;
        public final ImageView dinnerChat;
        public final ImageView dinnerImage;
        public final TextView plus;
        public OrderObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            dinnerName = view.findViewById(R.id.dinner_name);
            dinnerChat = view.findViewById(R.id.dinner_chat);
            dinnerImage = view.findViewById(R.id.dinner_image);
            plus = view.findViewById(R.id.plus);
        }
    }
}
