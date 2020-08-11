package com.comeaqui.eduardorodriguez.comeaqui.utilities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectImageFromFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectImageFromFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectImageFromFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "is_profile";

    // TODO: Rename and change types of parameters
    private Boolean isProfile;

    private OnFragmentInteractionListener mListener;

    View view;
    CardView card;
    ConstraintLayout outOfCard;

    String SAMPLE_CROP_IMAGE_NAME = "SampleCropImage";
    public static final int PICK_IMAGE = 1;

    public SelectImageFromFragment() {
        // Required empty public constructor
    }

    public static SelectImageFromFragment newInstance(Boolean isProfile) {
        SelectImageFromFragment fragment = new SelectImageFromFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isProfile);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isProfile = getArguments().getBoolean(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_image_from, container, false);

        LinearLayout selectFromCamera = view.findViewById(R.id.select_from_camera);
        LinearLayout selectFromGallery = view.findViewById(R.id.select_from_gallery);
        card = view.findViewById(R.id.select_card);
        outOfCard = view.findViewById(R.id.out_of_card);

        card.setVisibility(View.GONE);
        outOfCard.setVisibility(View.GONE);

        outOfCard.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    hideCard();
                    break;
                default:
                    return true;
            }
            return true;
        });

        selectFromCamera.setOnClickListener(v -> {
            checkPermissions();
        });

        selectFromGallery.setOnClickListener(v -> {
            openGallery();
        });
        return view;
    }

    public void showCard(){
        card.setVisibility(View.VISIBLE);
        outOfCard.setVisibility(View.VISIBLE);
        card.setScaleX(0);
        card.setScaleY(0);
        card.animate().scaleX(1).scaleY(1).setDuration(200);
    }
    public void hideCard(){
        card.animate().scaleX(0).scaleY(0).setDuration(200).withEndAction(() -> {
            card.setVisibility(View.GONE);
            outOfCard.setVisibility(View.GONE);
        });
    }

    File photoFile;
    static final int REQUEST_TAKE_PHOTO = 2;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.comeaqui.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    String currentPhotoPath;
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case PICK_IMAGE:
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        if (isProfile){
                            startCrop(selectedImage);
                        } else {
                            onButtonPressed(selectedImage);
                        }
                    }
                    break;
                case REQUEST_CROP:
                    Uri imageUri = UCrop.getOutput(data);
                    if(imageUri != null){
                        onButtonPressed(imageUri);
                    }
                    break;
                case REQUEST_TAKE_PHOTO:
                    Uri photoUri = Uri.fromFile(photoFile);
                    if (photoUri != null) {
                        if (isProfile) {
                            startCrop(photoUri);
                        } else {
                            onButtonPressed(photoUri);
                        }
                    }
                    break;
            }
        }
    }

    private void startCrop(Uri uri){
        String destinationFileName = SAMPLE_CROP_IMAGE_NAME;
        destinationFileName += ".jpg";

        UCrop.of(uri, Uri.fromFile(new File(view.getContext().getCacheDir(), destinationFileName)))
                .withAspectRatio(1, 1)
                .withMaxResultSize(450, 450)
                .withOptions(getCropOptions())
                .start(view.getContext(), SelectImageFromFragment.this);
    }

    private UCrop.Options getCropOptions(){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        // options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarColor(Color.parseColor("#ffffff"));
        options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarTitle("Image Crop");

        return options;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else if(getParentFragment() == null){
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        if (getParentFragment() instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) getParentFragment();
        } else if(getParentFragment() != null){
            throw new RuntimeException("The parent fragment must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    void showNoAccessNotification(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EX_STORAGE_AND_CAMERA);
                })
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode){
            case READ_EX_STORAGE_AND_CAMERA:
                if (
                        grantResults[0] != PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    showNoAccessNotification("Camera and storage access",
                            "In order to obtain best quality images we need access to camera and external storage");
                } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showNoAccessNotification("Camera access","In order to capture images we need access to camera");
                } else if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    showNoAccessNotification("Storage access", "In order to obtain best quality images we need access external storage");
                } else {
                    dispatchTakePictureIntent();
                }
                return;
        }
    }

    private static final int READ_EX_STORAGE_AND_CAMERA = 3;
    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||  ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showNoAccessNotification("Camera and storage access",
                        "In order to obtain best quality images we need access to camera and external storage");
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                showNoAccessNotification("Camera access","In order to capture images we need access to camera");
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                showNoAccessNotification("Storage access", "In order to obtain best quality images we need access external storage");
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EX_STORAGE_AND_CAMERA);
            }
        } else {
            dispatchTakePictureIntent();
        }
    }
}
