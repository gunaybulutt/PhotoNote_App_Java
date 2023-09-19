package com.gunay.photonoteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.gunay.photonoteapp.databinding.ActivityAddBinding;
import com.gunay.photonoteapp.databinding.ActivityEverythingBinding;

import java.util.ArrayList;

public class EverythingActivity extends AppCompatActivity {

    ArrayList<Photo> photoArrayList;
    PhotoAdapter photoAdapter;
    private ActivityEverythingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEverythingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        photoArrayList = new ArrayList<>();
        binding.eveRcyclerView.setLayoutManager(new LinearLayoutManager(this));
        photoAdapter = new PhotoAdapter(photoArrayList);
        binding.eveRcyclerView.setAdapter(photoAdapter);
        getData();


    }

    public void getData() {

        try {

            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("Photos", MODE_PRIVATE, null);

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM photoNote", null);

            int nameIX = cursor.getColumnIndex("photoName");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIX);
                int id = cursor.getInt(idIx);
                Photo foto = new Photo(name, id);
                photoArrayList.add(foto);
            }
            photoAdapter.notifyDataSetChanged();
            cursor.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.eve_acti_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.back_menu) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}