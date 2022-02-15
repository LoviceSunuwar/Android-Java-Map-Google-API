package com.example.FA_LoviceSunuwar_c0835390_Android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.FA_LoviceSunuwar_c0835390_Android.R;

import java.util.ArrayList;
import java.util.List;

public class FavPlaces extends AppCompatActivity {

    DatabaseHelper mDatabase;

    List<FavouritePlace> places;
    SwipeMenuListView swipeMenuListView;
    FavPlaceAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_places);
        swipeMenuListView = (SwipeMenuListView) findViewById(R.id.favPlaceList);
        mDatabase = new DatabaseHelper(this);
        places = new ArrayList<>();
        loadPlaces();


        adapter = new FavPlaceAdapter(this,R.layout.fav_place_cell_layout,places,mDatabase);
        swipeMenuListView.setAdapter(adapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                openItem.setWidth(300);
                openItem.setTitle("Update");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.BLACK);
                menu.addMenuItem(openItem);
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(300);
                deleteItem.setIcon(R.drawable.ic_action_delete);
                menu.addMenuItem(deleteItem);
            }
        };

// set creator
        swipeMenuListView.setMenuCreator(creator);

        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Intent intent = new Intent(FavPlaces.this, DurationAndDistance.class);
                        intent.putExtra("id",places.get(position).id);
                        intent.putExtra("lat",places.get(position).longitude);
                        intent.putExtra("longi",places.get(position).latitude);
                        intent.putExtra("edit",true);
                        startActivity(intent);
                        places.clear();
                        swipeMenuListView.setAdapter(adapter);
                        loadPlaces();
                        break;
                    case 1:
                        // delete
                        Toast.makeText(FavPlaces.this, ""+places.get(position).id, Toast.LENGTH_SHORT).show();
                        if(mDatabase.deletePlace(places.get(position).id)) {
                            Toast.makeText(FavPlaces.this,"",Toast.LENGTH_SHORT).show();
                            places.remove(places.get(position));
                            swipeMenuListView.setAdapter(adapter);
                            loadPlaces();
                        }else {
                            Toast.makeText(FavPlaces.this, ""+mDatabase.deletePlace(position), Toast.LENGTH_SHORT).show();
                        }
                        loadPlaces();
                        break;
                }
                return false;
            }

        });swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(FavPlaces.this, "case 1"+places.get(position).id, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(FavPlaces.this, DurationAndDistance.class);
                intent.putExtra("id",places.get(position).id);
                intent.putExtra("lat",places.get(position).longitude);
                intent.putExtra("longi",places.get(position).latitude);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        places.clear();
        swipeMenuListView.setAdapter(adapter);
        loadPlaces();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlaces();
    }

    private void loadPlaces() {
        places.clear();
        Cursor cursor = mDatabase.getAllPlace();
        if (cursor.moveToFirst()) {

            do {
                places.add(new FavouritePlace(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getDouble(5),cursor.getInt(6)));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}

