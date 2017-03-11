package com.restfulrobot.moviefan;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.restfulrobot.moviefan.MainActivity;
import com.restfulrobot.moviefan.R;
import com.restfulrobot.moviefan.database.MoviefanDatabaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GeneralMethods {

    static public void makeToast(Activity activity, String message, int duration)
    {
        Toast toast = Toast.makeText(activity.getApplicationContext(), message, duration);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        View toastView = toast.getView();
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(18);
        toastView.setBackgroundResource(R.drawable.toast_background_color);
        toast.show();
    }

    static public Boolean checkOpportunityForStart(Context context)
    {
        Boolean result = false;
        SQLiteDatabase db=null;
        try {
            db = new MoviefanDatabaseHelper(context).getReadableDatabase();
            Cursor cursor = db.query("GENERAL_INFO",
                    new String[]{"UPDATE_SUCCESS"}, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.getInt(0)==1)
                {
                    result = true;
                }
            }
            cursor.close();
        } catch (SQLiteException e) {
            result=false;
        }
        if (db != null) {
            db.close();
        }
        return result;
    }

    static public void resetDB(Context context)
    {
        SQLiteDatabase db=null;
        try {
            db = new MoviefanDatabaseHelper(context).getReadableDatabase();
            Cursor cursor = db.query("GENERAL_INFO",
                    new String[]{"_id, UPDATE_COUNT"}, null, null, null, null, null);
            int _id=0;
            int count=0;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                _id = cursor.getInt(0);
            }
            cursor.close();
            ContentValues values = new ContentValues();
            values.put("UPDATE_SUCCESS", 0);
            db.update("GENERAL_INFO",
                    values,
                    "_id = ?",
                    new String[]{String.valueOf(_id)});

            db.execSQL("DELETE FROM MOVIES");
            db.execSQL("DELETE FROM MOVIES_IKNOW");
            db.execSQL("DELETE FROM ACTORS");
            db.execSQL("DELETE FROM MOVIE_ACTORS");

        } catch (SQLiteException e) {

        }
        if (db != null) {
            db.close();
        }
    }

    public static String formatDateTime(Context context, String timeToFormat) {

        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        Date date = null;
        if (timeToFormat != null) {
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            }

            if (date != null) {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
                        when + TimeZone.getDefault().getOffset(when), flags);
            }
        }
        return finalDateTime;
    }

    static public  Bitmap getBitmap(String fileName, File cacheFolder) {
        File cacheFile = new File(cacheFolder, fileName);
        InputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(cacheFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeStream(fileInputStream, null, bitmapOptions);
    }

}
