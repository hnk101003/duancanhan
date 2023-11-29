package com.example.assignmentnetwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;



import com.example.assignmentnetwork.databinding.ActivityMainBinding;
import com.example.assignmentnetwork.fragment.AccountFragment;
import com.example.assignmentnetwork.fragment.HomeFragment;
import com.example.assignmentnetwork.fragment.MapFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNav.setBackground(null);
        binding.bottomNav.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if(itemId == R.id.mHome){
                replaceFragment(new HomeFragment());
            }else if(itemId == R.id.mMessage){
                replaceFragment(new MapFragment());
            }else if(itemId == R.id.mAccount){
                replaceFragment(new AccountFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}