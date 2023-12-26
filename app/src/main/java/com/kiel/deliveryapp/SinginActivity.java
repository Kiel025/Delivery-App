package com.kiel.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kiel.deliveryapp.databinding.ActivitySinginBinding;

public class SinginActivity extends AppCompatActivity {

    private ActivitySinginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySinginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}