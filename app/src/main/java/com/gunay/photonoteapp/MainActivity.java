package com.gunay.photonoteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.gunay.photonoteapp.databinding.ActivityEverythingBinding;
import com.gunay.photonoteapp.databinding.ActivityMainBinding;

import java.util.zip.GZIPOutputStream;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    public void add(View view){
        Intent intent = new Intent(this, AddActivity.class);
        intent.putExtra("info","add");
        startActivity(intent);
    }

    public void goAll(View view){
        Intent intent = new Intent(this, EverythingActivity.class);
        startActivity(intent);

    }




}































