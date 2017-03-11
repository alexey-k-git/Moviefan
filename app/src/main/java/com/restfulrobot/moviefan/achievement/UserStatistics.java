package com.restfulrobot.moviefan.achievement;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.restfulrobot.moviefan.AchievementActivity;
import com.restfulrobot.moviefan.database.MoviefanDatabaseHelper;

import java.util.HashMap;
import java.util.Map;

public class UserStatistics {

    static private void appendOnetoValueOfThisColumn(Activity activity, String column)
    {
        SQLiteDatabase db = null;
        try {
            db = new MoviefanDatabaseHelper(activity.getApplicationContext()).getWritableDatabase();
            Cursor cursor = db.query("USER_STATISTICS",
                    new String[]{"_id, " + column}, null, null, null, null, null);
            int count=0;
            int _id=0;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                _id = cursor.getInt(0);
                count = cursor.getInt(1);
                count++;
            }
            cursor.close();
            ContentValues values = new ContentValues();
            values.put(column, count);
            db.update("USER_STATISTICS",
                    values,
                    "_id = ?",
                    new String[]{String.valueOf(_id)});
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (db != null) {
            db.close();
        }
    }




    static public void appendTrueAnswerMovie(Activity activity) {
        appendOnetoValueOfThisColumn(activity, "TRUE_ANSWER_MOVIE");
    }

    static public void appendFalseAnswerMovie(Activity activity) {
        appendOnetoValueOfThisColumn(activity, "FALSE_ANSWER_MOVIE");
    }

    static public void appendTrueAnswerActor(Activity activity) {
        appendOnetoValueOfThisColumn(activity, "TRUE_ANSWER_ACTOR");
    }

    static public void appendFalseAnswerActor(Activity activity) {
        appendOnetoValueOfThisColumn(activity, "FALSE_ANSWER_ACTOR");
    }

    static public void appendTrueAnswerMovieActors(Activity activity) {
        appendOnetoValueOfThisColumn(activity, "TRUE_ANSWER_MOVIE_ACTORS");
    }

    static public void appendFalseAnswerMovieActors(Activity activity) {
        appendOnetoValueOfThisColumn(activity, "FALSE_ANSWER_MOVIE_ACTORS");
    }

    static public Map<String, Integer> getTrueFalseAnswersMap(Activity activity) {
        HashMap<String, Integer> map = new HashMap<>();
        SQLiteDatabase db = null;
        try {
            db = new MoviefanDatabaseHelper(activity.getApplicationContext()).getWritableDatabase();
            Cursor cursor = db.query("USER_STATISTICS",
                    new String[]{"TRUE_ANSWER_MOVIE", "TRUE_ANSWER_ACTOR", "TRUE_ANSWER_MOVIE_ACTORS", "FALSE_ANSWER_MOVIE", "FALSE_ANSWER_ACTOR", "FALSE_ANSWER_MOVIE_ACTORS"}, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                map.put("TRUE_ANSWER_MOVIE", cursor.getInt(0));
                map.put("TRUE_ANSWER_ACTOR", cursor.getInt(1));
                map.put("TRUE_ANSWER_MOVIE_ACTORS", cursor.getInt(2));
                map.put("FALSE_ANSWER_MOVIE", cursor.getInt(3));
                map.put("FALSE_ANSWER_ACTOR", cursor.getInt(4));
                map.put("FALSE_ANSWER_MOVIE_ACTORS", cursor.getInt(5));
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (db != null) {
            db.close();
        }
        return map;
    }

    @Deprecated
    static public int getTrueAnswerTotal(Activity activity) {
        SQLiteDatabase db = null;
        int countTrue=0;
        try {
            db = new MoviefanDatabaseHelper(activity.getApplicationContext()).getWritableDatabase();
            Cursor cursor = db.query("USER_STATISTICS",
                    new String[]{"TRUE_ANSWER_MOVIE", "TRUE_ANSWER_ACTOR", "TRUE_ANSWER_MOVIE_ACTORS"}, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                countTrue += cursor.getInt(0);
                countTrue += cursor.getInt(1);
                countTrue += cursor.getInt(2);
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (db != null) {
            db.close();
        }
        return countTrue;
    }

    @Deprecated
    static public int getFalseAnswerTotal(Activity activity) {
        SQLiteDatabase db = null;
        int countFalse=0;
        try {
            db = new MoviefanDatabaseHelper(activity.getApplicationContext()).getWritableDatabase();
            Cursor cursor = db.query("USER_STATISTICS",
                    new String[]{"FALSE_ANSWER_MOVIE", "FALSE_ANSWER_MOVIE", "FALSE_ANSWER_MOVIE_ACTORS"}, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                countFalse += cursor.getInt(0);
                countFalse += cursor.getInt(1);
                countFalse += cursor.getInt(2);
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (db != null) {
            db.close();
        }
        return countFalse;
    }

    static public String getUserName(Activity activity)
    {
        SQLiteDatabase db = null;
        String name = "Пользователь";
        try {
            db = new MoviefanDatabaseHelper(activity.getApplicationContext()).getWritableDatabase();
            Cursor cursor = db.query("USER_STATISTICS",
                    new String[]{"USER_NAME" }, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                name = cursor.getString(0);
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (db != null) {
            db.close();
        }
        return name;
    }


    static public void updateBlitzRecord (Activity activity, Float newRecord)
    {
        SQLiteDatabase db = null;
        try {
            db = new MoviefanDatabaseHelper(activity.getApplicationContext()).getWritableDatabase();
            Cursor cursor = db.query("USER_STATISTICS",
                    new String[]{"_id, " + "BLITZ_RECORD"}, null, null, null, null, null);
            float currentRecord=0f;
            int _id=0;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                _id = cursor.getInt(0);
                currentRecord = cursor.getFloat(1);
            }
            cursor.close();
            if (newRecord>currentRecord)
            {
                ContentValues values = new ContentValues();
                values.put("BLITZ_RECORD", newRecord);
                db.update("USER_STATISTICS",
                        values,
                        "_id = ?",
                        new String[]{String.valueOf(_id)});
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (db != null) {
            db.close();
        }
    }


    static public void resetStatistics(Activity activity) {
        SQLiteDatabase db = null;
        try {
            db = new MoviefanDatabaseHelper(activity.getApplicationContext()).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("TRUE_ANSWER_MOVIE", 0);
            values.put("FALSE_ANSWER_MOVIE", 0);
            values.put("TRUE_ANSWER_ACTOR", 0);
            values.put("FALSE_ANSWER_ACTOR", 0);
            values.put("TRUE_ANSWER_MOVIE_ACTORS", 0);
            values.put("FALSE_ANSWER_MOVIE_ACTORS", 0);
            values.put("BLITZ_RECORD", 0);
            db.update("USER_STATISTICS",
                    values,
                    null,
                    null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (db != null) {
            db.close();
        }
    }

    public static float getBlitzRecord(Activity activity) {
        float currentRecord=0f;
        SQLiteDatabase db = null;
        try {
            db = new MoviefanDatabaseHelper(activity.getApplicationContext()).getWritableDatabase();
            Cursor cursor = db.query("USER_STATISTICS",
                    new String[]{"BLITZ_RECORD"}, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                currentRecord = cursor.getFloat(0);
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (db != null) {
            db.close();
        }
        return currentRecord;
    }
}
