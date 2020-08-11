package com.comeaqui.eduardorodriguez.comeaqui.order;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.OrderObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.SavedFoodPost;
import com.comeaqui.eduardorodriguez.comeaqui.order.GuestingFragment.OnListFragmentInteractionListener;

import java.util.List;


public class MyGuestingRecyclerViewAdapter extends RecyclerView.Adapter<MyGuestingRecyclerViewAdapter.ViewHolder> {

    private List<OrderObject> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyGuestingRecyclerViewAdapter(List<OrderObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void addData(List<OrderObject> data){
        this.mValues = data;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.posterName.setText(holder.mItem.poster.first_name + " " + holder.mItem.poster.last_name);
        holder.mealTitle.setText(holder.mItem.post.plate_name);
        holder.postAddress.setText(holder.mItem.post.formatted_address);
        String priceTextE = holder.mItem.price_to_show + " - ";
        holder.price.setText(priceTextE);
        holder.orderStatus.setText(holder.mItem.status);
        holder.orderStatus.setBackgroundColor(Color.TRANSPARENT);

        if (!holder.mItem.poster.profile_photo.contains("no-image")){
            Glide.with(holder.mView.getContext()).load(holder.mItem.poster.profile_photo).into(holder.orderImage);
        } else {
            holder.orderImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.no_profile_photo));
        }

        if (!holder.mItem.seen){
            if (holder.mItem.status.equals("CONFIRMED")){
                holder.orderStatus.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_order_status_confirmed));
            } else if (holder.mItem.status.equals("REJECTED") || holder.mItem.status.equals("CANCELED")){
                holder.orderStatus.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_order_status_canceled));
            } else {
                holder.orderStatus.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_order_status));
            }
            holder.orderStatus.setTypeface(null, Typeface.BOLD);
            holder.orderStatus.setTextColor(Color.WHITE);
        } else if (holder.mItem.status.equals("CONFIRMED")){
            holder.orderStatus.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.success));
        } else if (holder.mItem.status.equals("REJECTED") || holder.mItem.status.equals("CANCELED")){
            holder.orderStatus.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.canceled));
        } else {
            holder.orderStatus.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.colorPrimary));
        }

        holder.mView.setOnClickListener(v -> {
            Intent orderLook = new Intent(holder.mView.getContext(), OrderLookActivity.class);
            orderLook.putExtra("orderId", holder.mItem.id);
            holder.mView.getContext().startActivity(orderLook);
        });
        holder.dateView.setText(holder.mItem.post.time_to_show);
    }

    @Override
    public int getItemCount() {
        return (mValues != null) ? mValues.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView orderImage;
        public final TextView posterName;
        public final TextView price;
        public final TextView mealTitle;
        public final TextView postAddress;
        public final TextView orderStatus;
        public final TextView dateView;
        public OrderObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            orderImage = view.findViewById(R.id.order_image);
            posterName = view.findViewById(R.id.poster_name);
            price = view.findViewById(R.id.price);
            mealTitle = view.findViewById(R.id.meal);
            postAddress = view.findViewById(R.id.address);
            orderStatus = view.findViewById(R.id.order_status);
            dateView = view.findViewById(R.id.date);
        }
    }
}
