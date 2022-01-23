package com.example.mychatapps.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.example.mychatapps.R;
import com.example.mychatapps.databinding.ActivityProfileBinding;
import com.example.mychatapps.databinding.ActivityUsersBinding;
import com.example.mychatapps.utilities.Constans;
import com.example.mychatapps.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        setListeners();
    }
    private void loadUserDetails(){
        binding.inputName.setText(preferenceManager.getString(Constans.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constans.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }
    private void setListeners() {
        binding.imageBack.setOnClickListener((v -> onBackPressed()));
        binding.buttonUpdate.setOnClickListener((v -> Update()));

    binding.layoutImage.setOnClickListener(v-> {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
    });
    }
    private void Update() {

        FirebaseFirestore database=FirebaseFirestore.getInstance();
       /* if (binding.inputName.getText().toString()!=null || encodedImage ==null  ){
            database.collection("users").document(Constans.KEY_USER_ID).update("name",binding.inputName.getText().toString());
        }
       else if (binding.inputName.getText().toString()==null || encodedImage !=null  ){
            database.collection("users").document(Constans.KEY_USER_ID).update("image",encodedImage);
        }
        else if (binding.inputName.getText().toString()!=null && encodedImage !=null  ){
        database.collection("users").document(Constans.KEY_USER_ID).update("name",binding.inputName.getText().toString());
        database.collection("users").document(Constans.KEY_USER_ID).update("image",encodedImage);}

        else{
            binding.imageBack.setOnClickListener((v->onBackPressed()));
        }*/
        database.collection("users").document(preferenceManager.getString(Constans.KEY_USER_ID)).update("name",binding.inputName.getText().toString());
        Log.d("mtag",preferenceManager.getString(Constans.KEY_USER_ID));
        database.collection("users").document(preferenceManager.getString(Constans.KEY_USER_ID)).update("image",encodedImage);
    }
    public String encodeImage(Bitmap bitmap){
        int previewWidth =150;
        int previewHeight=bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }


    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result-> {
                if (result.getResultCode()==RESULT_OK){
                    if (result.getData()!=null){
                        Uri imageUri =result.getData().getData();
                        try {
                            InputStream inputStream= getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }


                    }
                }

            });

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.buttonUpdate.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility((View.INVISIBLE));
            binding.buttonUpdate.setVisibility(View.VISIBLE);
        }

    }
}