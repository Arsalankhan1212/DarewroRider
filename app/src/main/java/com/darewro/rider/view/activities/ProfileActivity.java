package com.darewro.rider.view.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.darewro.rider.R;
import com.darewro.rider.view.customViews.CustomDatePicker;
import com.darewro.rider.view.customViews.RoundedEdgesImageView;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.PermissionsRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileActivity extends BaseFullScreenActivity {

    Bitmap bitmap = null;
    String userChoosenTask = "";
    RoundedEdgesImageView profile_pic;
    private TextView dob;
    private CustomDatePicker datePicker;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeViews();
    }

    @Override
    public void initializeViews() {
        dob = (TextView) findViewById(R.id.dob);
        datePicker = new CustomDatePicker(ProfileActivity.this, dob);
        profile_pic = (RoundedEdgesImageView) findViewById(R.id.profile_pic);
        profile_pic.setToCircleWithBorder(getResources().getColor(R.color.app_yellow),10);
        save = findViewById(R.id.save);
        setListeners();
    }

    @Override
    public void setListeners() {
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.switchActivity(ProfileActivity.this,MainActivity.class,null);
            }
        });
    }

    @Override
    public void handleIntent() {

    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Clear Photo",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, PermissionsRequest.CAMERA_REQUEST);
                    } else
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (PermissionsRequest.checkPermission(ProfileActivity.this)) {
                        galleryIntent();
                    }
                } else if (items[item].equals("Clear Photo")) {
                    if (bitmap != null) {
                        bitmap = null;
                        profile_pic.setImageResource(R.drawable.ic_account_circle);
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);

        }
    }

    private void onSelectFromGalleryResult(Intent data) {

        bitmap = null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        profile_pic.setImageBitmap(bitmap);
    }

    private void onCaptureImageResult(Intent data) {
        bitmap = null;
        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        profile_pic.setImageBitmap(bitmap);

    }
}
