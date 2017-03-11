package com.restfulrobot.moviefan.database;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import static com.restfulrobot.moviefan.logs.MovieFanLogs.LOG_GUESS_THE_MOVIE_DB;

public class MoviefanDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "moviefan";
    private static final int DB_VERSION = 16;
    private Context contextForUse;


    public MoviefanDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        contextForUse = context;
    }

    // Создание новой БД
    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db, 0, DB_VERSION);

    }

    // Обновление старой в виде полной перезаписи
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db, oldVersion, newVersion);

    }

    // Общий метод обновления
    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DB_VERSION) {
            db.execSQL("DROP TABLE IF EXISTS " + "MOVIES");
            db.execSQL("DROP TABLE IF EXISTS " + "ACTORS");
            db.execSQL("DROP TABLE IF EXISTS " + "GENDERS");
            db.execSQL("DROP TABLE IF EXISTS " + "MOVIE_ACTORS");
            db.execSQL("DROP TABLE IF EXISTS " + "MOVIES_IKNOW");
            db.execSQL("DROP TABLE IF EXISTS " + "GENERAL_INFO");
            db.execSQL("DROP TABLE IF EXISTS " + "USER_STATISTICS");
            db.execSQL("DROP TABLE IF EXISTS " + "HINTS");
            // Фильмы
            db.execSQL("CREATE TABLE MOVIES (MOVIE_NAME TEXT PRIMARY KEY , "
                    + "MOVIE_IMAGE_NAME_1 TEXT, "
                    + "MOVIE_IMAGE_NAME_2 TEXT);");

            // Фильмы - актеры
            db.execSQL("CREATE TABLE MOVIE_ACTORS (MOVIE_NAME TEXT PRIMARY KEY , "
                    + "COUNT INTEGER, "
                    + "ACTOR1 TEXT, "
                    + "ACTOR2 TEXT, "
                    + "ACTOR3 TEXT, "
                    + "ACTOR4 TEXT, "
                    + "ACTOR5 TEXT);");

            //Пол актера
            db.execSQL("CREATE TABLE GENDERS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "GENDER TEXT);");
            // Актеры
            db.execSQL("CREATE TABLE ACTORS (ACTOR_NAME TEXT PRIMARY KEY , "
                    + "GENDER INT, "
                    + "ACTOR_IMAGE_NAME TEXT, "
                    + "FOREIGN KEY(GENDER) REFERENCES GENDERS(_id));");
            // Фильмы, которые я знаю
            db.execSQL("CREATE TABLE MOVIES_IKNOW (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "MOVIE_NAME TEXT,"
                    + "HIT INTEGER);");

            // Статистика
            db.execSQL("CREATE TABLE USER_STATISTICS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "USER_NAME TEXT,"
                    + "TRUE_ANSWER_ACTOR INTEGER,"
                    + "FALSE_ANSWER_ACTOR INTEGER,"
                    + "TRUE_ANSWER_MOVIE_ACTORS INTEGER,"
                    + "FALSE_ANSWER_MOVIE_ACTORS INTEGER,"
                    + "TRUE_ANSWER_MOVIE INTEGER,"
                    + "FALSE_ANSWER_MOVIE INTEGER,"
                    + "BLITZ_RECORD REAL);");
            firstUser(db);

            // Общие данные
            db.execSQL("CREATE TABLE GENERAL_INFO (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "UPDATE_SUCCESS INTEGER,"
                    + "UPDATE_COUNT INTEGER);");


            // Подсказки
            db.execSQL("CREATE TABLE HINTS  (TODAY_DATE TEXT  PRIMARY KEY , "
                    + "SPENT INTEGER,"
                    + "TOTAL INTEGER);");

            firstUpdate(db);
            Log.d(LOG_GUESS_THE_MOVIE_DB, "Основная  БД создана ");

        }
    }

    private void firstUser(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        String userName = "КиноМан";
        if (ActivityCompat.checkSelfPermission(contextForUse, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            Account[] accounts = AccountManager.get(contextForUse).getAccounts();
            userName="";
            for (Account account : accounts) {
                String[] parts = account.name.split("@");
                if (parts.length > 1)
                    userName = parts[0];
                break;
            }
        }
        values.put("USER_NAME", userName);
        values.put("TRUE_ANSWER_MOVIE", 0);
        values.put("FALSE_ANSWER_MOVIE", 0);
        values.put("TRUE_ANSWER_MOVIE_ACTORS", 0);
        values.put("FALSE_ANSWER_MOVIE_ACTORS", 0);
        values.put("TRUE_ANSWER_ACTOR", 0);
        values.put("FALSE_ANSWER_ACTOR", 0);
        db.insert("USER_STATISTICS", null, values);
    }

    private void firstUpdate(SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();
        values.put("UPDATE_SUCCESS", 0);
        values.put("UPDATE_COUNT", 0);
        db.insert("GENERAL_INFO", null, values);
        values.clear();
        values.put("GENDER", "MAN");
        db.insert("GENDERS", null, values);
        values.clear();
        values.put("GENDER", "WOMAN");
        db.insert("GENDERS", null, values);
    }

}
