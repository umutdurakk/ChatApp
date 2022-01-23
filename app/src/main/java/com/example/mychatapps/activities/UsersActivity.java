package com.example.mychatapps.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mychatapps.R;
import com.example.mychatapps.adapters.UsersAdapter;
import com.example.mychatapps.databinding.ActivityUsersBinding;
import com.example.mychatapps.listeners.UserListener;
import com.example.mychatapps.models.User;
import com.example.mychatapps.utilities.Constans;
import com.example.mychatapps.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        getUsers();
        setListeners();
    }

    private void setListeners(){
    binding.imageBack.setOnClickListener((v->onBackPressed()));

    }



    private void getUsers(){
        loading(true);
        FirebaseFirestore database =FirebaseFirestore.getInstance();
        database.collection(Constans.Key_CollecTion_Users)
                .get()
                 .addOnCompleteListener(task ->{
                     loading(false);
                     String currentUserId = preferenceManager.getString(Constans.KEY_USER_ID);
                     if (task.isSuccessful() && task.getResult()!=null){
                         List<User> users=new ArrayList<>();
                         for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                             if (currentUserId.equals(queryDocumentSnapshot.getId())){
                                 continue;
                             }
                             User user=new User();
                             user.name=queryDocumentSnapshot.getString(Constans.KEY_NAME);
                             user.email=queryDocumentSnapshot.getString(Constans.KEY_EMAİL);
                             user.image=queryDocumentSnapshot.getString(Constans.KEY_IMAGE);
                             user.token=queryDocumentSnapshot.getString(Constans.KEY_FCM_TOKEN);
                             user.id=queryDocumentSnapshot.getId();

                             users.add(user);
                         }
                         if (users.size()>0){
                             UsersAdapter usersAdapter =new UsersAdapter(users,this);
                             binding.usersRecyclerView.setAdapter(usersAdapter);
                             binding.usersRecyclerView.setVisibility(View.VISIBLE);
                         }
                         else{
                             showErrorMessage();
                         }
                     }else{
                         showErrorMessage();
                     }
                 });
    }


    public void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No user avaible"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }
    private void loading(Boolean isLoading) {
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onUserClicked(User user) {
        Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constans.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}