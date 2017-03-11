package com.restfulrobot.moviefan.game_activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.restfulrobot.moviefan.R;
import com.restfulrobot.moviefan.database.BaseActorPicture;
import com.restfulrobot.moviefan.database.BaseMoviePicture;
import com.restfulrobot.moviefan.GeneralMethods;
import com.restfulrobot.moviefan.database.MoviefanDatabaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class GameSelectionActivity extends ListActivity {
    private File cacheFolder;
    private GamesAdapter adapter;
    private Bitmap moviePicture1Bitmap;
    private Bitmap moviePicture2Bitmap;
    private Bitmap moviePicture3Bitmap;
    private Bitmap moviePicture4Bitmap;
    private Bitmap moviePicture5Bitmap;
    private Bitmap moviePicture6Bitmap;
    private Bitmap actorPicture1Bitmap;
    private Bitmap actorPicture2Bitmap;
    private Bitmap actorPicture3Bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        cacheFolder = (File) intent.getSerializableExtra("cacheFolder");
        ListView listGames = getListView();
        Resources resources = getResources();
        String[] typeOfGames = resources.getStringArray(R.array.typeOfGames);
        ArrayList<String> items = new ArrayList<>(Arrays.asList(typeOfGames));
        uploadThreeMoviesAndThreeActors();
        this.adapter = new GamesAdapter(this, R.layout.game_list_item, items);
        listGames.setAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView listView,
                                View itemView,
                                int position,
                                long id) {
        Class activity = null;
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
            case 3:
                activity = BlitzActivity.class;
                break;

            default:
                activity = GuessTheActorActivity.class;
                break;
        }
        Intent intent = new Intent(this, activity);
        intent.putExtra("cacheFolder", cacheFolder);
        startActivity(intent);
    }

    private void uploadThreeMoviesAndThreeActors() {
        BaseMoviePicture.createListMyMoviesPictures(new MoviefanDatabaseHelper(this.getApplicationContext()));
        String[] moviesFiles = BaseMoviePicture.getSixRandomMovies();
        BaseActorPicture.createListMyActorsPictures(new MoviefanDatabaseHelper(this.getApplicationContext()));
        String[] actorsFiles = BaseActorPicture.getThreeRandomActors();
        moviePicture1Bitmap = GeneralMethods.getBitmap(moviesFiles[0], cacheFolder);
        moviePicture2Bitmap = GeneralMethods.getBitmap(moviesFiles[1], cacheFolder);
        moviePicture3Bitmap = GeneralMethods.getBitmap(moviesFiles[2], cacheFolder);
        moviePicture4Bitmap = GeneralMethods.getBitmap(moviesFiles[3], cacheFolder);
        moviePicture5Bitmap = GeneralMethods.getBitmap(moviesFiles[4], cacheFolder);
        moviePicture6Bitmap = GeneralMethods.getBitmap(moviesFiles[5], cacheFolder);

        actorPicture1Bitmap = GeneralMethods.getBitmap(actorsFiles[0], cacheFolder);
        actorPicture2Bitmap = GeneralMethods.getBitmap(actorsFiles[1], cacheFolder);
        actorPicture3Bitmap = GeneralMethods.getBitmap(actorsFiles[2], cacheFolder);
    }

    private class GamesAdapter extends ArrayAdapter<String> {

        private ArrayList<String> items;
        private int viewResourceId;

        public GamesAdapter(Context context, int viewResourceId, ArrayList<String> items) {
            super(context, viewResourceId, items);
            this.viewResourceId = viewResourceId;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(viewResourceId, null);
            }

            ImageView iw1 = (ImageView) v.findViewById(R.id.image1);
            ImageView iw2 = (ImageView) v.findViewById(R.id.image2);
            ImageView iw3 = (ImageView) v.findViewById(R.id.image3);
            ImageView iw4 = (ImageView) v.findViewById(R.id.image4);
            ImageView iw5 = (ImageView) v.findViewById(R.id.image5);
            ImageView iw6 = (ImageView) v.findViewById(R.id.image6);

            TextView gameText = (TextView) v.findViewById(R.id.gameTextItem);
            gameText.setText("-" + items.get(position) + "-");

            if (position == 0) {
                iw1.setImageBitmap(moviePicture1Bitmap);
                iw2.setImageBitmap(moviePicture2Bitmap);
                iw3.setImageBitmap(moviePicture3Bitmap);
            }
            if (position == 1) {
                iw1.setImageBitmap(actorPicture1Bitmap);
                iw2.setImageBitmap(actorPicture2Bitmap);
                iw3.setImageBitmap(actorPicture3Bitmap);
            }
            if (position == 2) {
                iw1.setImageBitmap(actorPicture2Bitmap);
                iw2.setImageResource(R.drawable.question);
                iw3.setImageBitmap(moviePicture2Bitmap);
                iw1.setImageBitmap(actorPicture2Bitmap);
                iw2.setImageResource(R.drawable.question);
            }

            if (position == 3) {
                LinearLayout layout = (LinearLayout) v.findViewById(R.id.extraRaw);
                layout.setVisibility(View.VISIBLE);
                iw1.setImageBitmap(moviePicture6Bitmap);
                iw2.setImageBitmap(moviePicture2Bitmap);
                iw3.setImageBitmap(moviePicture4Bitmap);
                iw4.setImageBitmap(moviePicture1Bitmap);
                iw5.setImageBitmap(moviePicture5Bitmap);
                iw6.setImageBitmap(moviePicture3Bitmap);

            }
            return v;
        }

    }
}
