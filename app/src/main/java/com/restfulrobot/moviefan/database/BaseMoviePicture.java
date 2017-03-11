package com.restfulrobot.moviefan.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.restfulrobot.moviefan.structure.MoviePicture;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import static com.restfulrobot.moviefan.logs.MovieFanLogs.LOG_GUESS_THE_MOVIE_DB;

public class BaseMoviePicture {

    static private List<MoviePicture> myMoviesPictures;
    static private List movies;
    static private SQLiteOpenHelper databaseHelper = null;

    // работа с SQLLite
    static public void createListMyMoviesPictures(SQLiteOpenHelper database) throws SQLiteException {
        databaseHelper = database;
        myMoviesPictures = new ArrayList<MoviePicture>();
        movies = new ArrayList<String>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursorForAllMovies = db.query("MOVIES",
                new String[]{"MOVIE_NAME", "MOVIE_IMAGE_NAME_1", "MOVIE_IMAGE_NAME_2"},
                null, null, null, null, null);
        Log.d(LOG_GUESS_THE_MOVIE_DB, "Записей в курсоре MOVIES  = " + cursorForAllMovies.getCount() + ".");
        while (cursorForAllMovies.moveToNext()) {
            String nameMovie = cursorForAllMovies.getString(0);
            String fileName1 = cursorForAllMovies.getString(1);
            String fileName2 = cursorForAllMovies.getString(2);
            movies.add(nameMovie);
            if (!iKnowIt(db, nameMovie)) {
                myMoviesPictures.add(new MoviePicture(fileName1, fileName2, nameMovie));
            }
        }
        cursorForAllMovies.close();
        db.close();
    }


    static public String[] getPicturesForThisMovieActors(SQLiteOpenHelper database, String movie) throws SQLiteException {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.query("MOVIES",
                new String[]{"MOVIE_NAME", "MOVIE_IMAGE_NAME_1", "MOVIE_IMAGE_NAME_2"},
                "MOVIE_NAME = ?",
                new String[] {movie},
                null, null, null);
        String[] files = new String[2];
        cursor.moveToFirst();
        files[0] = cursor.getString(1);
        files[1] = cursor.getString(2);
        cursor.close();
        db.close();
        return files;
    }

    static public List getListMyMoviesPictures() {
        return myMoviesPictures;
    }

    static public MoviePicture getMovie(int number) {
        return (MoviePicture) myMoviesPictures.remove(number);
    }

    static public boolean isLastMovie() {
        return (myMoviesPictures.size() == 1) ? true : false;
    }

    static public int sizeMovies() {
        Log.d(LOG_GUESS_THE_MOVIE_DB, "Текущий размер myMoviesPictures = " + myMoviesPictures.size() + ".");
        return myMoviesPictures.size();

    }

    static public ArrayList otherVariants(String string) {
        ArrayList variants = new ArrayList(movies);
        variants.remove(string);
        return variants;
    }


    static public void iKnowThisMovie(String name) throws SQLiteException {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("MOVIES_IKNOW",
                new String[]{"MOVIE_NAME", "HIT"},
                "MOVIE_NAME = ?",
                new String[]{name},
                null, null, null);
        ContentValues movieValues = new ContentValues();
        Log.d(LOG_GUESS_THE_MOVIE_DB, "Метод iKnowThisMovie. Нашлось " + cursor.getCount() + " элементов.");
        if (cursor.getCount() < 1) {
            movieValues.put("MOVIE_NAME", name);
            movieValues.put("HIT", 1);
            db.insert("MOVIES_IKNOW", null, movieValues);
        } else {
            cursor.moveToFirst();
            int hit = cursor.getInt(1);
            hit++;
            movieValues.put("HIT", hit);
            db.update("MOVIES_IKNOW",
                    movieValues,
                    "MOVIE_NAME = ?",
                    new String[]{name});
        }
    }

    static private Boolean iKnowIt(SQLiteDatabase db, String name) {
        Cursor cursor = db.query("MOVIES_IKNOW",
                new String[]{"MOVIE_NAME", "HIT"},
                "MOVIE_NAME = ?",
                new String[]{name},
                null, null, null);
        Log.d(LOG_GUESS_THE_MOVIE_DB, "MOVIES_I KNOW cursor = " + cursor);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int hit = cursor.getInt(1);
            if (hit > 10) {
                return true;
            }
        }
        return false;
    }

    static public String[] getSixRandomMovies()
    {
        Random randNumber = new Random();
        String[] threeMovies = new String[6];
        ArrayList<String> moviesList = new ArrayList<String>();
        for (int i=0;i<threeMovies.length;i++)
        {
            int randomPosition = randNumber.nextInt(myMoviesPictures.size());
            String file = (myMoviesPictures.get(randomPosition)).getImageFileName();
            if (!moviesList.contains(file))
            {
                moviesList.add(file);
                threeMovies[i]=file;
            }
            else
            {
                i--;
            }
        }
        return threeMovies;
    }

    static public void setMoviePictureForBlitz(Stack<MoviePicture> arrayList)
    {
        Random randNumber = new Random();
        int countOfMovies = randNumber.nextInt(myMoviesPictures.size()/3)+myMoviesPictures.size()/3;;
        for (int i=0;i<countOfMovies;i++)
        {
            int randomPosition = randNumber.nextInt(myMoviesPictures.size());
            MoviePicture moviePicture = myMoviesPictures.get(randomPosition);
            if (!arrayList.contains(moviePicture))
            {
                arrayList.add(moviePicture);
            }
            else
            {
                i--;
            }
        }
    }
}


