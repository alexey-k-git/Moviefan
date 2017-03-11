package com.restfulrobot.moviefan.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.restfulrobot.moviefan.structure.ActorPicture;
import com.restfulrobot.moviefan.structure.MoviePicture;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import static com.restfulrobot.moviefan.logs.MovieFanLogs.LOG_GUESS_THE_ACTOR_DB;

public class BaseActorPicture {
    static private List<ActorPicture> myActorsPictures;
    static private List mActors;
    static private List wActors;
    static private SQLiteOpenHelper databaseHelper = null;

    // работа с SQLLite
    static public void createListMyActorsPictures(SQLiteOpenHelper database) throws SQLiteException {
        databaseHelper = database;
        myActorsPictures = new ArrayList<ActorPicture>();
        mActors = new ArrayList<String>();
        wActors = new ArrayList<String>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        int gendermass[] = getGendersId(db);
        int manId = gendermass[0];
        int womanId = gendermass[1];
        Cursor cursorForAllActors = db.query("ACTORS",
                new String[]{"ACTOR_NAME", "GENDER", "ACTOR_IMAGE_NAME"},
                null, null, null, null, null);
        Log.d(LOG_GUESS_THE_ACTOR_DB, "Записей в курсоре ACTORS  = " + cursorForAllActors.getCount() + ".");
        while (cursorForAllActors.moveToNext()) {
            String name = cursorForAllActors.getString(0);
            String fileName = cursorForAllActors.getString(2);
            int gender_id = cursorForAllActors.getInt(1);
            String gender_idf = cursorForAllActors.getString(1);
            Object per = cursorForAllActors.getType(1);
            byte gender = 0;
            if (cursorForAllActors.getInt(1) == womanId) {
                gender = 1;
                wActors.add(name);
            }
            if (cursorForAllActors.getInt(1) == manId) {
                mActors.add(name);
            }
            myActorsPictures.add(new ActorPicture(name, gender, fileName));
        }
        cursorForAllActors.close();
        db.close();
    }

    static public String[] getPicturesForTheseActors(SQLiteOpenHelper database, String[] actors) throws SQLiteException {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.query("ACTORS",
                new String[]{"ACTOR_NAME", "ACTOR_IMAGE_NAME"},
                "ACTOR_NAME IN (?,?,?,?,?,?)",
                actors,
                null, null, null);
        String[] files = new String[cursor.getCount()];
        while (cursor.moveToNext())
        {
            int index=0;
            for (;index<actors.length;index++)
            {
                if (actors[index].equals(cursor.getString(0)))
                    break;
            }
            files[index] = cursor.getString(1);
        }
        cursor.close();
        db.close();
        return files;
    }


    static public List getListMyActorsPictures() {
        return myActorsPictures;
    }

    static public ActorPicture getActor(int number) {
        return (ActorPicture) myActorsPictures.remove(number);
    }

    static public boolean isLastActor() {
        return (myActorsPictures.size() == 1) ? true : false;
    }

    static public int sizeActors() {
        Log.d(LOG_GUESS_THE_ACTOR_DB, "Текущий размер myActorsPictures = " + myActorsPictures.size() + ".");
        return myActorsPictures.size();

    }

    static public ArrayList otherVariants(String name, byte gender) {
        ArrayList variants = null;
        if (gender == 0) {
            variants = new ArrayList(mActors);
        }
        if (gender == 1) {
            variants = new ArrayList(wActors);
        }
        if (variants != null) {
            variants.remove(name);
        }
        return variants;
    }

    public static int[] getGendersId(SQLiteDatabase db)
    {
        int[] mass = new int[2];
        try {
            Cursor cursor = db.query("GENDERS",
                    new String[]{"_id", "GENDER"}, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    if (cursor.getString(1).equals("WOMAN")) {
                        mass[1] = cursor.getInt(0);
                    }
                    if (cursor.getString(1).equals("MAN")) {
                        mass[0] = cursor.getInt(0);
                    }
                }
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return mass;
    }

    static public  String[] getThreeRandomActors()
    {
        Random randNumber = new Random();
        String[] threeActors = new String[3];
        ArrayList<String> threeMoviesList = new ArrayList<String>();
        for (int i=0;i<threeActors.length;i++)
        {
            int randomPosition = randNumber.nextInt(myActorsPictures.size());
            String file = ((ActorPicture)myActorsPictures.get(randomPosition)).getImageFileName();
            if (!threeMoviesList.contains(file))
            {
                threeMoviesList.add(file);
                threeActors[i]=file;
            }
            else
            {
                i--;
            }
        }
        return threeActors;
    }


    static public void setActorPictureForBlitz(Stack<ActorPicture> arrayList)
    {
        Random randNumber = new Random();
        int countOfMovies = randNumber.nextInt(myActorsPictures.size()/3)+ myActorsPictures.size()/3;;
        for (int i=0;i<countOfMovies;i++)
        {
            int randomPosition = randNumber.nextInt(myActorsPictures.size());
            ActorPicture actorPicture = myActorsPictures.get(randomPosition);
            if (!arrayList.contains(actorPicture))
            {
                arrayList.add(actorPicture);
            }
            else
            {
                i--;
            }
        }
    }

}
