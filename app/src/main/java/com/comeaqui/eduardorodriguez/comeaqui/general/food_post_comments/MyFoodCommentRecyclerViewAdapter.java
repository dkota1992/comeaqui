package com.comeaqui.eduardorodriguez.comeaqui.general.food_post_comments;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodCommentObject;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class MyFoodCommentRecyclerViewAdapter extends RecyclerView.Adapter<MyFoodCommentRecyclerViewAdapter.ViewHolder> {

    Context context;
    private final List<FoodCommentObject> mValues;
    private final HashMap<Integer, FoodCommentObject> mValuesHashMap;
    private final FoodCommentFragment mListener;
    private FoodCommentObject comment;
    private HashMap<Integer, MyFoodCommentRecyclerViewAdapter> commentToAdapter;


    public HashMap<Integer, MyFoodCommentRecyclerViewAdapter> adapters = new HashMap<>();
    ArrayList<AsyncTask> tasks = new ArrayList<>();


    public MyFoodCommentRecyclerViewAdapter(List<FoodCommentObject> items, HashMap<Integer, FoodCommentObject> mValuesHashMap, HashMap<Integer, MyFoodCommentRecyclerViewAdapter> commentToAdapter, FoodCommentFragment listener, FoodCommentObject comment) {
        mValues = items;
        mListener = listener;
        this.mValuesHashMap = mValuesHashMap;
        this.commentToAdapter = commentToAdapter;
        this.comment = comment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_foodcomment, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        context = viewHolder.mView.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        setView(holder);
        holder.wholeView.setVisibility(View.VISIBLE);
        holder.moreInList.setVisibility(View.GONE);

        LinearLayoutManager lln = new LinearLayoutManager(holder.mView.getContext());
        holder.replyList.setLayoutManager(lln);
        MyFoodCommentRecyclerViewAdapter adapter = new MyFoodCommentRecyclerViewAdapter(holder.mItem.replies, holder.mItem.repliesHashMap, commentToAdapter, mListener, holder.mItem);
        commentToAdapter.put(holder.mItem.id, adapter);
        adapters.put(holder.mItem.id, adapter);
        holder.replyList.setAdapter(adapter);

//        if (holder.mItem.replies.size() > 0){
//            holder.replyList.setVisibility(View.VISIBLE);
//        } else {
//            holder.replyList.setVisibility(View.GONE);
//        }

        if (holder.mItem.is_max_depth){
            holder.continueConversation.setVisibility(View.VISIBLE);
            holder.continueConversation.setOnClickListener(v -> mListener.continueConversation(holder.mItem));
        } else {
            holder.continueConversation.setVisibility(View.GONE);
        }
        if (null != holder.mItem.extra_comments_in_list){
            holder.moreInList.setVisibility(View.VISIBLE);
            holder.moreInList.setText(holder.mItem.extra_comments_in_list + " more comments...");
            holder.moreInList.setOnClickListener(v -> {
                getMoreComments(holder, holder.mItem.extra_comments_in_list);
            });
        } else {
            holder.moreInList.setVisibility(View.GONE);
        }

        holder.reviewerImage.setOnClickListener(v -> mListener.onGoToProfile(holder.mItem.owner));
        holder.upVote.setOnClickListener(v -> {
            if (holder.mItem.is_user_up_vote == null){
                onCommentVote(holder, true);
            } else {
                if (holder.mItem.is_user_up_vote){
                    deleteCommentVote(holder);
                } else {
                    onCommentVote(holder, true);
                }
            }
        });
        holder.downVote.setOnClickListener(v -> {
            if (holder.mItem.is_user_up_vote == null){
                onCommentVote(holder, false);
            } else {
                if (holder.mItem.is_user_up_vote){
                    onCommentVote(holder, false);
                } else {

                    deleteCommentVote(holder);
                }
            }
        });
        setOptionsMenu(holder);
    }

    void setView(ViewHolder holder){
        holder.reviewerName.setText(holder.mItem.owner.first_name + ", " + holder.mItem.owner.last_name);
        holder.reviewerUsername.setText(holder.mItem.owner.username);
        holder.review.setText(holder.mItem.message);
        holder.votes.setText(holder.mItem.votes_n + "");

        holder.votes.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.colorPrimary));
        holder.upVote.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.upvote));
        holder.downVote.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.downvote));

        if (holder.mItem.is_user_up_vote != null){
            holder.votes.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.colorSecondary));
            if (holder.mItem.is_user_up_vote){
                holder.upVote.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.upvoted));
            } else {
                holder.downVote.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.downvoted));
            }
        }

        if(!holder.mItem.owner.profile_photo.contains("no-image")) {
            Glide.with(holder.mView.getContext()).load(holder.mItem.owner.profile_photo).into(holder.reviewerImage);
        } else {
            holder.reviewerImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.no_profile_photo));
        }

        if (holder.mItem.owner.id == USER.id){
            holder.replyComment.setVisibility(View.GONE);
        } else {
            holder.replyComment.setVisibility(View.VISIBLE);
            holder.replyComment.setOnClickListener(v -> {
                mListener.onCommentCreate(holder.mItem);
            });
        }
    }

    boolean anyTaskRunning(){
        for (AsyncTask task: tasks){
            if (task != null && task.getStatus() == AsyncTask.Status.RUNNING){
                return true;
            }
        }
        return false;
    }
    public void getMoreComments(ViewHolder holder, int nLasts){
        tasks.add(new GetCommentAsyncTask(context.getString(R.string.server) + "/comment_comments/" + this.comment.id + "/" + nLasts + "/", holder).execute());
    }
    class GetCommentAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        private ViewHolder holder;
        public GetCommentAsyncTask(String uri, ViewHolder holder){
            this.uri = uri;
            this.holder = holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            holder.moreInList.setVisibility(View.GONE);
            holder.moreInListProgress.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(context, this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (null != response){
                JsonArray ja = new JsonParser().parse(response).getAsJsonArray();
                for (JsonElement je: ja){
                    FoodCommentObject moreComment = new FoodCommentObject(je.getAsJsonObject());
                    mValues.add(moreComment);
                    mValuesHashMap.put(moreComment.id, moreComment);
                }
                notifyDataSetChanged();
                holder.mItem.extra_comments_in_list = null;
                holder.moreInListProgress.setVisibility(View.GONE);
            }
            super.onPostExecute(response);
        }
    }

    void deleteComment(ViewHolder holder){
        tasks.add(new DeleteCommentAsyncTask(holder.mView.getResources().getString(R.string.server) + "/food_post_comment/" + holder.mItem.id + "/", holder).execute());
    }

    class DeleteCommentAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        private ViewHolder holder;

        public DeleteCommentAsyncTask(String uri, ViewHolder holder){
            this.uri = uri;
            this.holder = holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.delete(holder.mView.getContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (null != response){
                int deletedCommentId = new JsonParser().parse(response).getAsInt();
                FoodCommentObject commentToDelete = mValuesHashMap.get(deletedCommentId);
                Integer extraCommentsInList =  commentToDelete.extra_comments_in_list;
                int indexInList = mValues.indexOf(commentToDelete);
                notifyItemRemoved(indexInList);
                mValues.remove(mValuesHashMap.get(deletedCommentId));
                if (extraCommentsInList != null){
                    if (indexInList == 0){
                        getMoreComments(holder, extraCommentsInList);
                    } else {
                        mValues.get(indexInList - 1).extra_comments_in_list = extraCommentsInList;
                        notifyItemChanged(indexInList - 1);
                    }
                }
            }
            super.onPostExecute(response);
        }
    }

    void updateComment(FoodCommentObject newComment, FoodCommentObject commentInList){
        commentInList.is_max_depth = newComment.is_max_depth;
        commentInList.is_user_up_vote = newComment.is_user_up_vote;
        commentInList.comment = newComment.comment;
        commentInList.votes_n = newComment.votes_n;
    }


    void onCommentVote(ViewHolder holder, boolean is_up_vote){
        if (!anyTaskRunning()){
            tasks.add(new PostVoteAsyncTask(holder.mView.getResources().getString(R.string.server) + "/vote_comment/" + holder.mItem.id + "/", holder).execute(
                    new String[]{"is_up_vote", is_up_vote ? "True" : "False"}
            ));
        }
    }
    private class PostVoteAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        private ViewHolder holder;

        public PostVoteAsyncTask(String uri, ViewHolder holder){
            this.uri = uri;
            this.holder = holder;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(holder.mView.getContext(), "POST", this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (null != response){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                FoodCommentObject newComment = new FoodCommentObject(jo);
                updateComment(newComment, holder.mItem);
                setView(holder);
            }
            super.onPostExecute(response);
        }
    }


    void deleteCommentVote(ViewHolder holder){
        if (!anyTaskRunning()) {
            tasks.add(new DeleteCommentVoteAsyncTask(holder.mView.getResources().getString(R.string.server) + "/vote_comment/" + holder.mItem.id + "/", holder).execute());
        }
    }
    class DeleteCommentVoteAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        private ViewHolder holder;

        public DeleteCommentVoteAsyncTask(String uri, ViewHolder holder){
            this.uri = uri;
            this.holder = holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.delete(holder.mView.getContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (null != response){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                FoodCommentObject newComment = new FoodCommentObject(jo);
                updateComment(newComment, holder.mItem);
                setView(holder);
            }
            super.onPostExecute(response);
        }
    }

    void setOptionsMenu(MyFoodCommentRecyclerViewAdapter.ViewHolder holder){
        holder.optionsReview.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.mView.getContext(), holder.optionsReview);
            if (holder.mItem.owner.id != USER.id){
                popupMenu.getMenu().add("Reply");
                popupMenu.getMenu().add("Report");
            } else {
                popupMenu.getMenu().add("Delete");
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                setOptionsActions(holder, item.getTitle().toString());
                return true;
            });
            popupMenu.show();
        });
    }

    void setOptionsActions(MyFoodCommentRecyclerViewAdapter.ViewHolder holder, String title){
        switch (title){
            case "Reply":
                mListener.onCommentCreate(holder.mItem);
                break;
            case "Delete":
                deleteComment(holder);
                break;
            case "Report":
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final LinearLayout wholeView;
        public final ImageView reviewerImage;
        public final ImageView replyerImage;
        public final TextView reviewerName;
        public final TextView reviewerUsername;
        public final TextView review;
        public final ImageView optionsReview;
        public final ImageView replyComment;
        public final ImageView upVote;
        public final TextView votes;
        public final ImageView downVote;

        public final TextView replyerName;
        public final TextView replyerUsername;
        public final TextView reply;
        public final TextView moreInList;
        public final TextView continueConversation;
        public final ProgressBar continueProgressBar;
        public final ProgressBar moreInListProgress;
        public final ProgressBar upvoteProgress;
        public final ProgressBar downvoteProgress;

        public final ImageButton optionsReply;
        public final RecyclerView replyList;
        public FoodCommentObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            wholeView = view.findViewById(R.id.whole_view);
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
            replyList = view.findViewById(R.id.reply_list);
            replyComment = view.findViewById(R.id.reply_comment);
            upVote = view.findViewById(R.id.upvote);
            votes = view.findViewById(R.id.votes);
            downVote = view.findViewById(R.id.downvote);
            moreInList = view.findViewById(R.id.more_in_list);
            continueConversation = view.findViewById(R.id.continue_conversation);
            continueProgressBar = view.findViewById(R.id.continue_progress);
            moreInListProgress = view.findViewById(R.id.more_in_list_progress);
            upvoteProgress = view.findViewById(R.id.upvote_progress);
            downvoteProgress = view.findViewById(R.id.downvote_progress);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }
}
