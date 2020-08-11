package com.comeaqui.eduardorodriguez.comeaqui.notification;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.general.FoodLookActivity;
import com.comeaqui.eduardorodriguez.comeaqui.objects.NotificationObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.SavedFoodPost;
import com.comeaqui.eduardorodriguez.comeaqui.order.OrderLookActivity;
import com.comeaqui.eduardorodriguez.comeaqui.review.food_review_look.FoodPostReviewLookActivity;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.DateFormatting;
import com.comeaqui.eduardorodriguez.comeaqui.R;

import java.util.ArrayList;
import java.util.List;

public class MyNotificationsRecyclerViewAdapter extends RecyclerView.Adapter<MyNotificationsRecyclerViewAdapter.ViewHolder> {

    public  List<NotificationObject> mValues;
    Context context;

    public MyNotificationsRecyclerViewAdapter(Context context, ArrayList<NotificationObject> data){
        this.context = context;
        this.mValues = data;
    }

    public void addData(List<NotificationObject> data){
        this.mValues = data;
        this.notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_notifications_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.usernameView.setText(holder.mItem.from_user.username);
        holder.notificationView.setText(holder.mItem.title);
        holder.body.setText(holder.mItem.body);
        holder.date.setText(DateFormatting.hYesterdayWeekDay(holder.mItem.createdAt));
        if (!holder.mItem.from_user.profile_photo.contains("no-image")) {
            Glide.with(holder.mView.getContext()).load(holder.mItem.from_user.profile_photo).into(holder.senderImageView);
        } else {
            holder.senderImageView.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.no_profile_photo));
        }

        switch (holder.mItem.type){
            case "PENDING":
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.order_pending_not));
                holder.mView.setOnClickListener(v -> {
                    goToNotificationLook(holder.mItem.type_id);
                });
                break;
            case "CONFIRMED":
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.order_confirmed_not));
                holder.mView.setOnClickListener(v -> {
                    goToOrderLook(holder.mItem.type_id);
                });
                break;
            case "REJECTED":
            case "CANCELED":
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.order_canceled_not));
                holder.mView.setOnClickListener(v -> {
                    goToOrderLook(holder.mItem.type_id);
                });
                break;
            case "REVIEW":
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.social_not));
                holder.mView.setOnClickListener(v -> goToFoodPostReview(holder.mItem.type_id));
                break;
            case "COMMENT":
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.comment_not));
                holder.mView.setOnClickListener(v -> goToFoodPost(holder.mItem.type_id));
                break;
            case "INFO":
                break;
        }
    }

    void goToFoodPostReview(int foodPostId){
        Intent notification = new Intent(context, FoodPostReviewLookActivity.class);
        notification.putExtra("foodPostId", foodPostId);
        context.startActivity(notification);
    }

    void goToFoodPost(int foodPostId){
        Intent foodLook = new Intent(context, FoodLookActivity.class);
        foodLook.putExtra("foodPostId", foodPostId);
        context.startActivity(foodLook);
    }
    void goToNotificationLook(int orderId){
        Intent notification = new Intent(context, NotificationLookActivity.class);
        notification.putExtra("orderId", orderId);
        context.startActivity(notification);
    }
    void goToOrderLook(int orderId){
        Intent orderLook = new Intent(context, OrderLookActivity.class);
        orderLook.putExtra("orderId", orderId);
        context.startActivity(orderLook);
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView usernameView;
        public final TextView notificationView;
        public final TextView body;
        public final ImageView typeImage;
        public final TextView date;
        public final ImageView senderImageView;
        public NotificationObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            usernameView = view.findViewById(R.id.username);
            notificationView = view.findViewById(R.id.notification);
            senderImageView = view.findViewById(R.id.dinner_image);
            typeImage = view.findViewById(R.id.type_image);
            date = view.findViewById(R.id.date);
            body = view.findViewById(R.id.body);
        }
    }
}
