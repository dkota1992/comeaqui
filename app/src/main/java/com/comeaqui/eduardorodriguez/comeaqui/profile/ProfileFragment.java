package com.comeaqui.eduardorodriguez.comeaqui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import com.comeaqui.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.comeaqui.eduardorodriguez.comeaqui.chat.conversation.ConversationActivity;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.EditProfileActivity;
import com.comeaqui.eduardorodriguez.comeaqui.profile.post_and_reviews.PostAndReviewsFragment;
import com.comeaqui.eduardorodriguez.comeaqui.profile.settings.SettingsActivity;

import com.comeaqui.eduardorodriguez.comeaqui.profile.user_posts.UserPostFragment;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.SelectImageFromFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.image_view_pager.ImagePagerActivity;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.ProfileImageGalleryFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.RatingFragment;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.R;

import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class ProfileFragment extends Fragment implements SelectImageFromFragment.OnFragmentInteractionListener{

    public static String[] data;
    public View view;
    boolean isBackGound;
    private User user;

    private ImageView profileImageView;
    private ImageView backGroundImage;
    private ImageView messageImage;
    private ImageView editProfileView;
    private ImageView addProfilePhotoView;
    private ImageView addBackGroundPhotoView;

    private TextView emailView;
    private TextView bioView;
    private TextView nameView;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ImageButton settingsButton;
    ProgressBar loadingProgress;

    private SelectImageFromFragment selectImageFromFragment;

    int userId;
    public int page_index;


    ArrayList<AsyncTask> tasks = new ArrayList<>();

    private static final String USER_TO_DISPLAY = "user";

    public ProfileFragment() {}

    public static ProfileFragment newInstance(int userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(USER_TO_DISPLAY, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(USER_TO_DISPLAY);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        selectImageFromFragment.hideCard();
        getUser(userId);
    }

    public void getUser(int userId) {
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/profile_detail/" + userId + "/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                user = new User(new JsonParser().parse(response).getAsJsonObject());
                setProfile(user);
            }
            loadingProgress.setVisibility(View.GONE);
            super.onPostExecute(response);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = view.findViewById(R.id.profile_image);
        backGroundImage = view.findViewById(R.id.backGroundImage);
        messageImage = view.findViewById(R.id.message);
        emailView = view.findViewById(R.id.senderEmail);
        bioView = view.findViewById(R.id.bioView);
        nameView = view.findViewById(R.id.nameView);
        editProfileView = view.findViewById(R.id.edit_profile);
        addProfilePhotoView = view.findViewById(R.id.add_profile_photo);
        addBackGroundPhotoView = view.findViewById(R.id.add_background_photo);
        settingsButton = view.findViewById(R.id.settings_profile_button);
        loadingProgress = view.findViewById(R.id.loading_progress);

        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        selectImageFromFragment = SelectImageFromFragment.newInstance(true);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.select_from, selectImageFromFragment)
                .commit();

        return view;
    }
    public void setProfile(User user){
        viewPager.setAdapter(new TestPagerAdapter(getChildFragmentManager()));
        viewPager.setCurrentItem(page_index);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                page_index = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        getChildFragmentManager().beginTransaction()
                .replace(R.id.profile_rating, RatingFragment.newInstance(user.rating, user.ratingN))
                .commitAllowingStateLoss();

        addProfilePhotoView.setOnClickListener(v -> {
            isBackGound = false;
            selectImageFromFragment.showCard();
        });

        addBackGroundPhotoView.setOnClickListener(v -> {
            isBackGound = true;
            selectImageFromFragment.showCard();

        });
        if (user.id == USER.id){
            settingsButton.setVisibility(View.VISIBLE);
            setSettingsButton();
            editProfileView.setVisibility(View.VISIBLE);
            editProfileView.setOnClickListener(v -> {
                Intent editProfile = new Intent(getContext(), EditProfileActivity.class);
                editProfile.putExtra("userId", user.id);
                getContext().startActivity(editProfile);
            });
            addProfilePhotoView.setVisibility(View.VISIBLE);
            addBackGroundPhotoView.setVisibility(View.VISIBLE);
        } else {
            messageImage.setVisibility(View.VISIBLE);
            messageImage.setOnClickListener(v -> goToConversationWithUser(user));
        }

        nameView.setText(user.first_name + " " + user.last_name);
        emailView.setText(user.username);
        if (user.bio != null && !user.bio.equals("")) {
            bioView.setVisibility(View.VISIBLE);
            bioView.setText(user.bio);
        }

        if(!user.profile_photo.contains("no-image")) {
            Glide.with(view.getContext()).load(user.profile_photo).into(profileImageView);
            profileImageView.setOnClickListener((v) -> {
                Intent imageLook = new Intent(getContext(), ImagePagerActivity.class);
                ArrayList<String> urls = new ArrayList<>();
                urls.add(user.profile_photo);
                imageLook.putExtra("image_urls", urls);
                getContext().startActivity(imageLook);
            });
        }
        if(!user.background_photo.contains("no-image")) {
            Glide.with(view.getContext()).load(user.background_photo).into(backGroundImage);
            backGroundImage.setOnClickListener((v) -> {
                Intent imageLook = new Intent(getContext(), ImagePagerActivity.class);
                ArrayList<String> urls = new ArrayList<>();
                urls.add(user.background_photo);
                imageLook.putExtra("image_urls", urls);
                getContext().startActivity(imageLook);
            });
        }

        int curveRadius = 10;
        backGroundImage.setClipToOutline(true);
        backGroundImage.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), (view.getHeight() + curveRadius), curveRadius);
            }
        });
    }


    void setSettingsButton(){
        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        });
    }

    void goToConversationWithUser(User user){
        tasks.add(new GetConversationAsyncTask(getResources().getString(R.string.server) + "/get_or_create_chat/" + user.id + "/").execute());
    }
    class GetConversationAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        GetConversationAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                ChatObject chat = new ChatObject(new JsonParser().parse(response).getAsJsonObject());
                goToConversationActivity(chat);
            }
            super.onPostExecute(response);
        }
    }

    void goToConversationActivity(ChatObject chat){
        Intent k = new Intent(getContext(), ConversationActivity.class);
        k.putExtra("chatId", chat.id + "");
        startActivity(k);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scrolling, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        saveProfileImage(uri);
    }


    private void saveProfileImage(Uri imageUri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), imageUri);
            if (bitmap != null){
                PatchImageAsyncTask putTask = new PatchImageAsyncTask(getResources().getString(R.string.server) + "/edit_profile/", bitmap);
                if (isBackGound){
                    tasks.add(putTask.execute("background_photo"));
                }else {
                    tasks.add(putTask.execute("profile_photo"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class PatchImageAsyncTask extends AsyncTask<String, Void, String> {
        String uri;
        Bitmap imageBitmap;

        public PatchImageAsyncTask(String uri, Bitmap imageBitmap){
            this.uri = uri;
            this.imageBitmap = imageBitmap;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                return ServerAPI.uploadImage(getContext(),"PATCH", this.uri, params[0], this.imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
        }
    }

    class TestPagerAdapter extends FragmentPagerAdapter {

        public TestPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment[] tabFragment = {
                    UserPostFragment.newInstance(user.id),
                    PostAndReviewsFragment.newInstance(user.id),
                    ProfileImageGalleryFragment.newInstance(user.id)
            };
            return tabFragment[position];
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = {"posts", "posts & reviews", "media"};
            return titles[position];
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public void onDestroy() {
for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks = new ArrayList<>();
        super.onDestroy();
    }
}