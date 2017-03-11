package com.restfulrobot.moviefan.database;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.restfulrobot.moviefan.structure.ActorPicture;
import com.restfulrobot.moviefan.structure.MovieActors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import static com.restfulrobot.moviefan.logs.MovieFanLogs.LOG_GUESS_THE_MOVIE_ACTOR_DB;

public class BaseMovieActors {

    static private List myActors;
    static private List<MovieActors> myMovieActors;

    static private SQLiteOpenHelper databaseHelper = null;

    // работа с SQLLite
    static public void createListMyMoviesAndActorsPictures(SQLiteOpenHelper database) throws SQLiteException {
        databaseHelper = database;
        myActors = new ArrayList<String>();
        myMovieActors = new ArrayList<MovieActors>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        // актеры
        Cursor cursor = db.query("ACTORS",
                new String[]{"ACTOR_NAME"},
                null, null, null, null, null);
        Log.d(LOG_GUESS_THE_MOVIE_ACTOR_DB, "Записей в курсоре ACTORS  = " + cursor.getCount() + ".");
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            myActors.add(name);
        }
        cursor.close();
        // фильмы с актерами
        cursor = db.query("MOVIE_ACTORS",
                new String[]{"MOVIE_NAME", "COUNT", "ACTOR1", "ACTOR2","ACTOR3","ACTOR4","ACTOR5"},
                null, null, null, null, null);
        Log.d(LOG_GUESS_THE_MOVIE_ACTOR_DB, "Записей в курсоре MOVIE ACTORS  = " + cursor.getCount() + ".");
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            int count = cursor.getInt(1);
            String[] actors = new String[count];
            for (int i=0;i<count;i++)
            {
                actors[i] = cursor.getString(i+2);

            }
            myMovieActors.add(new MovieActors(name, actors));
        }
        cursor.close();
        db.close();
    }



    static public MovieActors getMovieActors(int number) {
        return (MovieActors) myMovieActors.remove(number);
    }


    static public List getListMyMovieActors() {
        return myMovieActors;
    }



    static public boolean isLastMovieActors() {
        return (myMovieActors.size() == 1) ? true : false;
    }

    static public int sizeMovieActors() {
        Log.d(LOG_GUESS_THE_MOVIE_ACTOR_DB, "Текущий размер myActorsPictures = " + myMovieActors.size() + ".");
        return myMovieActors.size();

    }

    static public ArrayList otherVariants(String[] names) {
        ArrayList variants =  new ArrayList(myActors);
        for (String name:names)
        {
            variants.remove(name);
        }
        return variants;
    }

    static public void setMovieActorsForBlitz(Stack<MovieActors> arrayList)
    {
        Random randNumber = new Random();
        int countOfMovies = randNumber.nextInt(myMovieActors.size()/3)+ myMovieActors.size()/3;;
        for (int i=0;i<countOfMovies;i++)
        {
            int randomPosition = randNumber.nextInt(myMovieActors.size());
            MovieActors movieActors = myMovieActors.get(randomPosition);
            if (!arrayList.contains(movieActors))
            {
                arrayList.add(movieActors);
            }
            else
            {
                i--;
            }
        }
    }



}
