package com.restfulrobot.moviefan.database;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseHints {

    final private static int TOTAL_HINTS=18;

    public static boolean updateHintBase(SQLiteOpenHelper database)
    {
        boolean newHints = false;
        String currentDate = getDateInString();
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.query("HINTS",
                new String[]{"TODAY_DATE"},
                "TODAY_DATE = ?",
                new String[]{currentDate},
                null, null, null);
        int countRow = cursor.getCount();
        cursor.close();
        if (countRow==0)
        {
            ContentValues values = new ContentValues();
            values.put("TODAY_DATE", currentDate);
            values.put("SPENT", 0);
            values.put("TOTAL", TOTAL_HINTS);
            db.insert("HINTS", null, values);
            newHints = true;
        }
        db.close();
        return newHints;
    }

    public static int[] getHintsInfo(SQLiteOpenHelper database)
    {
        int[] hints = new int[2];
        String currentDate = getDateInString();
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.query("HINTS",
                new String[]{"TODAY_DATE", "SPENT", "TOTAL"},
                "TODAY_DATE = ?",
                new String[]{currentDate},
                null, null, null);
        if (cursor.getCount()>0)
        {
            cursor.moveToLast();
            hints[0] = cursor.getInt(1);
            hints[1] = cursor.getInt(2);
        }
        cursor.close();
        db.close();
        return hints;
    }


    public static void updateUsedHint(SQLiteOpenHelper database, int usedHints)
    {
        SQLiteDatabase db = database.getReadableDatabase();
        String currentDate = getDateInString();
        ContentValues values = new ContentValues();
        values.put("SPENT", usedHints);
        db.update("HINTS",
                values,
                "TODAY_DATE = ?",
                new String[]{currentDate});
        db.close();
    }

    private static String getDateInString()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        return date;
    }

}
