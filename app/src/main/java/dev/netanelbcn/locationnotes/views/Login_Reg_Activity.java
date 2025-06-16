package dev.netanelbcn.locationnotes.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import dev.netanelbcn.locationnotes.databinding.ActivityLoginRegBinding;

public class Login_Reg_Activity extends AppCompatActivity {

    private ActivityLoginRegBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivityLoginRegBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

}