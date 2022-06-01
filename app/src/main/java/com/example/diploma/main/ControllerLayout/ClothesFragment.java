package com.example.diploma.main.ControllerLayout;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.diploma.R;
import com.example.diploma.databinding.FragmentClothesBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ClothesFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private FragmentClothesBinding binding;
    private FloatingActionButton addButton;
    private FloatingActionButton cameraButton;
    private FloatingActionButton galleryButton;
    private boolean isFABOpen;
    private ImageView imageView;
    static final int GALLERY_REQUEST = 1;
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    public ClothesFragment() {
    }

    @SuppressWarnings("unused")
    public static ClothesFragment newInstance(int columnCount) {
        ClothesFragment fragment = new ClothesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentClothesBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        addButton = rootView.findViewById(R.id.addButton);
        cameraButton = rootView.findViewById(R.id.cameraButton);
        galleryButton = rootView.findViewById(R.id.galleryButton);
        imageView = rootView.findViewById(R.id.listImage);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFABOpen){
                    showFABMenu();
                } else{
                    closeFABMenu();
                }
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasCameraPermission()) {
                    Navigation.findNavController(v).navigate(R.id.action_fragment_clothes_list_to_cameraFragment);
                } else {
                    requestPermission();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == requireActivity().RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageView.setImageURI(selectedImage);
                }
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                requireActivity(),
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    private void showFABMenu(){
        isFABOpen=true;
        cameraButton.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        galleryButton.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        cameraButton.animate().translationY(0);
        galleryButton.animate().translationY(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}