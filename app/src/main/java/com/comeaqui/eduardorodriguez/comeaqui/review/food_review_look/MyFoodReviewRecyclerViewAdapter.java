package com.comeaqui.eduardorodriguez.comeaqui.review.food_review_look;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.ReviewReplyObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.ReviewObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;

import java.util.List;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class MyFoodReviewRecyclerViewAdapter extends RecyclerView.Adapter<MyFoodReviewRecyclerViewAdapter.ViewHolder> {

    private final List<ReviewObject> mValues;
    private User poster;

    private OnListFragmentInteractionListener mListener;


    public MyFoodReviewRecyclerViewAdapter(List<ReviewObject> items, Context context, User poster) {
        mValues = items;
        this.poster = poster;

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_foodreview, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.reviewerName.setText(holder.mItem.owner.first_name + ", " + holder.mItem.owner.last_name);
        holder.reviewerUsername.setText(holder.mItem.owner.username);
        holder.review.setText(holder.mItem.review);

        if(!holder.mItem.owner.profile_photo.contains("no-image")) {
            Glide.with(holder.mView.getContext()).load(holder.mItem.owner.profile_photo).into(holder.reviewerImage);
        } else {
            holder.reviewerImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.no_profile_photo));
        }
        holder.reviewerImage.setOnClickListener(v -> mListener.onGoToProfile(holder.mItem.owner));

        if (holder.mItem.replies.size() > 0){
            setOptionsReplyMenu(holder);
            holder.replyWhole.setVisibility(View.VISIBLE);
            holder.replyerName.setText(holder.mItem.replies.get(0).owner.first_name + ", " + holder.mItem.replies.get(0).owner.last_name);
            holder.replyerUsername.setText(holder.mItem.replies.get(0).owner.username);
            holder.reply.setText(holder.mItem.replies.get(0).reply);

            if(!holder.mItem.replies.get(0).owner.profile_photo.contains("no-image")) {
                Glide.with(holder.mView.getContext()).load(holder.mItem.replies.get(0).owner.profile_photo).into(holder.replyerImage);
            } else {
                holder.replyerImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.no_profile_photo));
            }
            holder.replyerImage.setOnClickListener(v -> mListener.onGoToProfile(holder.mItem.replies.get(0).owner));
        } else {
            holder.replyWhole.setVisibility(View.GONE);
        }
        setOptionsMenu(holder);
        setStars(holder, holder.mItem.rating);
    }

    void setOptionsMenu(ViewHolder holder){
        holder.optionsReview.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.mView.getContext(), holder.optionsReview);
            if (poster.id == USER.id){
                if (holder.mItem.replies.size() == 0){
                    popupMenu.getMenu().add("Reply");
                }
                popupMenu.getMenu().add("Report");
            } else if (USER.id == holder.mItem.owner.id){
                popupMenu.getMenu().add("Delete");
            } else {
                popupMenu.getMenu().add("Report");
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                setOptionsActions(holder, item.getTitle().toString(), true);
                return true;
            });
            popupMenu.show();
        });
    }

    void setOptionsReplyMenu(ViewHolder holder){
        holder.optionsReply.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.mView.getContext(), holder.optionsReview);
            if (USER.id == holder.mItem.replies.get(0).owner.id){
                popupMenu.getMenu().add("Delete");
            } else {
                popupMenu.getMenu().add("Report");
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                setOptionsActions(holder, item.getTitle().toString(), false);
                return true;
            });
            popupMenu.show();
        });
    }

    void setOptionsActions(ViewHolder holder, String title, boolean review){
        switch (title){
            case "Reply":
                mListener.onReplyCreate(holder.mItem);
                break;
            case "Delete":
                if (review){
                    mListener.onReviewDelete(holder.mItem);
                    holder.mView.setVisibility(View.GONE);
                } else {
                    mListener.onReplyDelete(holder.mItem.replies.get(0));
                    holder.replyWhole.setVisibility(View.GONE);
                }
                break;
            case "Report":
                break;
        }
    }

    void setStars(ViewHolder holder, float rating){
        ImageView[] starArray = new ImageView[]{
                holder.star0,
                holder.star1,
                holder.star2,
                holder.star3,
                holder.star4
        };

        for (int i = 0; i < starArray.length; i++){
            starArray[i].setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.star_empty));
        }

        for (int i = 0; i < (int) rating; i++){
            starArray[i].setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.star_filled));
        }

        double decimal = rating - Math.floor(rating);
        if (decimal >= 0.75){
            starArray[(int) rating].setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.star_filled));
        } else if (decimal >= 0.25) {
            starArray[(int) rating].setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.star_half_filled));
        }
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView reviewerImage;
        public final ImageView replyerImage;
        public final TextView reviewerName;
        public final TextView reviewerUsername;
        public final TextView review;
        public final ImageButton optionsReview;

        public final TextView replyerName;
        public final TextView replyerUsername;
        public final TextView reply;
        public final ImageButton optionsReply;
        public final LinearLayout replyWhole;
        public ReviewObject mItem;

        public ImageView star0;
        public ImageView star1;
        public ImageView star2;
        public ImageView star3;
        public ImageView star4;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            reviewerImage = view.findViewById(R.id.reviewer_image);
            replyerImage = view.findViewById(R.id.reviewer_image_ans);
            reviewerName = view.findViewById(R.id.reviewer_name);
            reviewerUsername = view.findViewById(R.id.reviewer_username);
            review = view.findViewById(R.id.review);
            optionsReview = view.findViewById(R.id.options_review);
            replyerName = view.findViewById(R.id.reviewer_name_ans);
            replyerUsername = view.findViewById(R.id.reviewer_username_ans);
            reply = view.findViewById(R.id.reply);
            optionsReply = view.findViewById(R.id.options_review_reply);
            replyWhole = view.findViewById(R.id.review_reply);

            star0 = view.findViewById(R.id.star0);
            star1 = view.findViewById(R.id.star1);
            star2 = view.findViewById(R.id.star2);
            star3 = view.findViewById(R.id.star3);
            star4 = view.findViewById(R.id.star4);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onReviewDelete(ReviewObject review);
        void onReplyDelete(ReviewReplyObject reply);
        void onReplyCreate(ReviewObject review);
        void onGoToProfile(User user);
    }
}
