package com.isaac.practice.workmanagerpractice.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.isaac.practice.workmanagerpractice.R;
import com.isaac.practice.workmanagerpractice.databinding.ActivitySelectImageBinding;

import java.util.Arrays;
import java.util.List;

public class SelectImageActivity extends AppCompatActivity {

    private ActivitySelectImageBinding mActivitySelectImageBinding;

    private final static String KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT";
    private final static int MAX_NUMBER_REQUEST_PERMISSIONS = 2;
    private final static int REQUEST_CODE_IMAGE = 100;
    private final static int REQUEST_CODE_PERMISSIONS = 101;
    private final static String KEY_IMAGE_URI = "KEY_IMAGE_URI";
    private final static String UNKNOWN_REQUEST = "UNKNOWN_REQUEST";
    private final static String UNEXPECTED_RESULT = "UNEXPECTED_RESULT";

    // list of permissions
    private List<String> permissions = Arrays.asList(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    );

    private int permissionRequestCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySelectImageBinding = ActivitySelectImageBinding.inflate(getLayoutInflater());
        setContentView(mActivitySelectImageBinding.getRoot());

        if (savedInstanceState != null) {
            permissionRequestCount = savedInstanceState.getInt(KEY_PERMISSIONS_REQUEST_COUNT, 0);
        }

        // Ensure app has correct permissions to run
        requestPermissionsIfNecessary();

        // create a request to get image from filesystem when button is clicked
        mActivitySelectImageBinding.selectImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent chooseIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(chooseIntent, REQUEST_CODE_IMAGE);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_PERMISSIONS_REQUEST_COUNT, permissionRequestCount);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            requestPermissionsIfNecessary(); // no-op if permissions are granted already
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (resultCode == REQUEST_CODE_IMAGE) {
                handleImageRequestResult(data);
            } else {
                Log.d(UNKNOWN_REQUEST, "Unknown request code");
            }
        } else {
            Log.d(UNEXPECTED_RESULT, "Unknown request code");
        }
    }

    // check app permissions
    private Boolean checkAllPermissions() {
        Boolean hasPermissions = true;
        for(String permission: permissions) {
            hasPermissions = hasPermissions && isPermissionGranted(permission);
        }
        return hasPermissions;
    }

    private boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionsIfNecessary() {
        if (!checkAllPermissions()) {
            if (permissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                permissionRequestCount += 1;
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
            } else {
                Toast.makeText(this, R.string.set_permissions_in_settings,Toast.LENGTH_LONG).show();
                mActivitySelectImageBinding.selectImage.setEnabled(false);
            }
        }
    }

    private void handleImageRequestResult(Intent intent) {
        // if clip data is available, we use it, otherwise we use data
        Uri imageUri = intent.getClipData() != null ? intent.getClipData().getItemAt(0).getUri() : intent.getData();
        if (imageUri == null) {
            Log.d("IMAGE_URI_DEBUG", "Invalid input image Uri.");
            return;
        }
        Intent filterIntent = new Intent(this, BlurActivity.class);
        filterIntent.putExtra(KEY_IMAGE_URI, imageUri.toString());
        startActivity(filterIntent);
    }



}