package com.restfulrobot.moviefan.game_activities;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.restfulrobot.moviefan.R;

import java.io.File;

public class GameSelectionActivityOld extends ListActivity {
    private File cacheFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listDrinks = getListView();
        Resources resources = getResources();
        String[] typeOfGames = resources.getStringArray(R.array.typeOfGames);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, typeOfGames);
        Intent intent = getIntent();
        cacheFolder = (File) intent.getSerializableExtra("cacheFolder");
        listDrinks.setAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView listView,
                                View itemView,
                                int position,
                                long id) {
        Class activity=null;
        switch (position) {
            case 0:
                activity = GuessTheMovieActivity.class;
                break;
            case 1:
                activity = GuessTheActorActivity.class;
                break;
            case 2:
                activity = GuessTheMovieActorsActivity.class;
                break;
            default:
                activity = GuessTheMovieActorsActivity.class;
                break;
        }
        Intent intent = new Intent(this, activity);
        intent.putExtra("cacheFolder", cacheFolder);
        startActivity(intent);
    }
}