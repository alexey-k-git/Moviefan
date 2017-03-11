package com.restfulrobot.moviefan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.restfulrobot.moviefan.database.BaseActorPicture;
import com.restfulrobot.moviefan.database.MoviefanDatabaseHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.restfulrobot.moviefan.logs.MovieFanLogs.LOG_DOWNLOAD;

public class FullUpdate extends AsyncTask<String, String, String> {
    final private static String SITE_PATH = "http://moviefan.000webhostapp.com/movies/";
    final private static String MOVIE_BASE_FILE = "movies.m";
    final private static String ACTOR_BASE_FILE = "actors.m";
    final private static String MOVIE_ACTOR_BASE_FILE = "mov-act.m";
    private MainActivity mainActivity = null;
    private DownloadDialog dialog = null;

    public void setActivity(MainActivity activity) {
        mainActivity = activity;
    }

    public void setDialog(DownloadDialog dialog) {
        this.dialog = dialog;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... strings) {
        updateAsync();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... progress) {

        if (dialog.getMainActivity().checkCancelFlag()) {
            this.cancel(true);
            return;
        }

        if (progress[0].equals("start")) {
            dialog.setCount(Integer.valueOf(progress[1]));
        } else {
            dialog.setProgress(Integer.parseInt(progress[0]));
        }


        /*

            if (mainActivity.checkCancelFlag())
            {
                this.cancel(true);
                return;
            }

            if (progress[0].equals("start"))
            {
                mainActivity.setCountOfFilesForDownloadDialog(Integer.valueOf(progress[1]));
            }
        else {
                mainActivity.setProgressForDownloadDialog(Integer.parseInt(progress[0]));
            }

        */
        ////////////////////////////////////////
        /*    if (progress[0].equals("start"))
            {
                mainActivity.setCountOfFilesForDownload(Integer.valueOf(progress[1]));
                mainActivity.showDialog(mainActivity.progress_bar_type);

            }
        else {
                mainActivity.getProgressBar().setProgress(Integer.parseInt(progress[0]));
            } */
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dialog.dismiss();
        //  mainActivity.dismissDialog(mainActivity.progress_bar_type);
    }


    private void updateAsync() {
        Boolean success = true;
        File cacheDir = MainActivity.cacheFolder;
        try {
            Map mapOfMovies = downloadMovieBaseAsync(cacheDir);
            Map mapOfActors = downloadActorBaseAsync(cacheDir);
            Map mapOfMoviesActors = downloadMoviesActorsBaseAsync(cacheDir);
            Set files = getFilesAsync(mapOfMovies, mapOfActors);
            publishProgress(new String[]{"start", String.valueOf(files.size())});
            downloadFilesAsync(files, cacheDir);
            if (isCancelled()) {
                dialog.dismiss();
                return;
            }
            updateSQLBaseAsync(mapOfMovies, mapOfActors, mapOfMoviesActors);
        } catch (Exception ex) {
            success = false;
        }
        if (success) {
            thereWasUpdateAsync(1);
        } else {
            thereWasUpdateAsync(0);
        }
        dialog.getMainActivity().endOfUpdate(success);
    }


    private void thereWasUpdateAsync(int attempt) {
        SQLiteDatabase db = null;
        try {
            db = new MoviefanDatabaseHelper(dialog.getMainActivity().getApplicationContext()).getWritableDatabase();
            Cursor cursor = db.query("GENERAL_INFO",
                    new String[]{"_id, UPDATE_COUNT"}, null, null, null, null, null);
            int _id = 0;
            int count = 0;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                _id = cursor.getInt(0);
                count = cursor.getInt(1);
                count++;
            }
            cursor.close();
            ContentValues values = new ContentValues();
            values.put("UPDATE_SUCCESS", attempt);
            values.put("UPDATE_COUNT", count);
            db.update("GENERAL_INFO",
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

    private void updateSQLBaseAsync(Map<String, String[]> movies, Map<String, String[]> actors, Map<String, String[]> moviesActors) {
        SQLiteDatabase db = null;
        try {
            db = new MoviefanDatabaseHelper(dialog.getMainActivity().getApplicationContext()).getWritableDatabase();
            updateMovieSQLBaseAsync(db, movies);
            updateActorSQLBaseAsync(db, actors);
            updateMoviesActorsSQLBaseAsync(db, moviesActors);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (db != null) {
            db.close();
        }
    }

    private void updateMovieSQLBaseAsync(SQLiteDatabase db, Map<String, String[]> map) {
        ContentValues movieValues = new ContentValues();
        for (String name : map.keySet()) {
            movieValues.put("MOVIE_NAME", name);
            String[] mass = map.get(name);
            movieValues.put("MOVIE_IMAGE_NAME_1", mass[0]);
            movieValues.put("MOVIE_IMAGE_NAME_2", mass[1]);
            db.insert("MOVIES", null, movieValues);
            movieValues.clear();
        }
    }

    private void updateMoviesActorsSQLBaseAsync(SQLiteDatabase db, Map<String, String[]> map) {
        ContentValues moviesActorsValues = new ContentValues();
        for (String name : map.keySet()) {
            moviesActorsValues.put("MOVIE_NAME", name);
            String[] mass = map.get(name);
            moviesActorsValues.put("COUNT", mass.length);
            for (int i = 0; i < mass.length; i++) {
                moviesActorsValues.put("ACTOR" + String.valueOf(i + 1), mass[i]);
            }
            db.insert("MOVIE_ACTORS", null, moviesActorsValues);
            moviesActorsValues.clear();
        }
    }


    private void updateActorSQLBaseAsync(SQLiteDatabase db, Map<String, String[]> map) {
        ContentValues actorValues = new ContentValues();
        int gendermass[] = BaseActorPicture.getGendersId(db);
        int manId = gendermass[0];
        int womanId = gendermass[1];
        for (String name : map.keySet()) {
            actorValues.put("ACTOR_NAME", name);
            String[] mass = map.get(name);
            if (mass[0].equals(String.valueOf(1))) {
                actorValues.put("GENDER", womanId);
            } else {
                actorValues.put("GENDER", manId);
            }
            actorValues.put("ACTOR_IMAGE_NAME", mass[1]);
            db.insert("ACTORS", null, actorValues);
            actorValues.clear();
        }
    }


    private void downloadFilesAsync(Set<String> files, File cacheDir) {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        int half = (int) (files.size()) / 2;
        byte percent = 1;
        Log.d(LOG_DOWNLOAD, "Всё готово для загрузки");
        for (String end : files) {
            if (isCancelled()) break;
            try {
                builder.append(SITE_PATH);
                builder.append(end);
                URL url = new URL(builder.toString());
                Log.d(LOG_DOWNLOAD, builder.toString());
                builder = new StringBuilder();
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();
                String str = String.valueOf(urlConnection.getResponseCode());
                Log.d(LOG_DOWNLOAD, str);
                File file = new File(cacheDir, end);
                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                }
                fileOutput.close();
                urlConnection.disconnect();
                count++;
                publishProgress(String.valueOf(count));
                if (count == (half + 1)) {
                    dialog.getMainActivity().updateStatus((byte) 50);
                }
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final Exception e) {
            }

        }
        if (isCancelled()) {
            dialog.dismiss();
            return;
        }
        dialog.getMainActivity().updateStatus((byte) 100);
    }

    private Map downloadMovieBaseAsync(File cacheDir) {
        Map movieMap = new HashMap<String, String[]>();
        File file = new File(cacheDir, MOVIE_BASE_FILE);
        try {
            URL url = new URL(SITE_PATH.concat(MOVIE_BASE_FILE));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();
            urlConnection.disconnect();
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final Exception e) {
        }
        String cvsSplitBy = ";";
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);
                movieMap.put(values[0], new String[]{values[1], values[2]});
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return movieMap;
    }

    private Map downloadActorBaseAsync(File cacheDir) {
        Map actorMap = new HashMap<String, String[]>();
        File file = new File(cacheDir, ACTOR_BASE_FILE);
        try {
            URL url = new URL(SITE_PATH.concat(ACTOR_BASE_FILE));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();
            urlConnection.disconnect();
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final Exception e) {
        }
        String cvsSplitBy = ";";
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);
                actorMap.put(values[0], new String[]{values[1], values[2]});
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return actorMap;
    }

    private Map downloadMoviesActorsBaseAsync(File cacheDir) {
        Map moviesActorsMap = new HashMap<String, String[]>();
        File file = new File(cacheDir, MOVIE_ACTOR_BASE_FILE);
        try {
            URL url = new URL(SITE_PATH.concat(MOVIE_ACTOR_BASE_FILE));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();
            urlConnection.disconnect();
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final Exception e) {
        }
        String cvsSplitBy = ";";
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);

                int countOfActors = Integer.valueOf(values[1]);
                String[] actors = new String[countOfActors];
                for (int j = 2, i = 0; i < countOfActors; j++, i++) {
                    actors[i] = values[j];
                }
                moviesActorsMap.put(values[0], actors);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return moviesActorsMap;
    }

    private Set getFilesAsync(Map<String, String[]> movies, Map<String, String[]> actors) {
        Set<String> allFiles = new HashSet();
        for (String[] files : movies.values()) {
            for (String file : files) {
                allFiles.add(file);
            }
        }
        for (String[] files : actors.values()) {
            allFiles.add(files[1]);
        }
        return allFiles;
    }

    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //---------------Deprecated methods----------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//
    //-------------------------------------------------//


    @Deprecated
    static protected void update(final MainActivity activity) {
        Boolean success = true;
        File cacheDir = MainActivity.cacheFolder;
        try {
            Map mapOfMovies = downloadMovieBase(cacheDir);
            Map mapOfActors = downloadActorBase(cacheDir);
            activity.updateStatus((byte) 10);
            Set files = getFiles(mapOfMovies, mapOfActors);
            downloadFiles(activity, files, cacheDir);
            updateSQLBase(activity, mapOfMovies, mapOfActors);
        } catch (Exception ex) {
            success = false;
        }
        if (success) {
            thereWasUpdate(activity, 1);
        } else {
            thereWasUpdate(activity, 0);
        }
        activity.endOfUpdate(success);
    }

    @Deprecated
    private static void thereWasUpdate(MainActivity activity, int attempt) {
        SQLiteDatabase db = null;
        try {
            db = new MoviefanDatabaseHelper(activity.getApplicationContext()).getWritableDatabase();
            Cursor cursor = db.query("GENERAL_INFO",
                    new String[]{"_id, UPDATE_COUNT"}, null, null, null, null, null);
            int _id = 0;
            int count = 0;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                _id = cursor.getInt(0);
                count = cursor.getInt(1);
                count++;
            }
            cursor.close();
            ContentValues values = new ContentValues();
            values.put("UPDATE_SUCCESS", attempt);
            values.put("UPDATE_COUNT", count);
            db.update("GENERAL_INFO",
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

    @Deprecated
    private static void updateSQLBase(Context activity, Map<String, String[]> movies, Map<String, String[]> actors) {
        SQLiteDatabase db = null;
        try {
            db = new MoviefanDatabaseHelper(activity.getApplicationContext()).getWritableDatabase();
            updateMovieSQLBase(db, movies);
            updateActorSQLBase(db, actors);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (db != null) {
            db.close();
        }
    }

    @Deprecated
    private static void updateMovieSQLBase(SQLiteDatabase db, Map<String, String[]> map) {
        ContentValues movieValues = new ContentValues();
        for (String name : map.keySet()) {
            // ставим имя
            movieValues.put("MOVIE_NAME", name);
            String[] mass = map.get(name);
            // ставим изображения
            movieValues.put("MOVIE_IMAGE_NAME_1", mass[0]);
            movieValues.put("MOVIE_IMAGE_NAME_2", mass[1]);
            db.insert("MOVIES", null, movieValues);
            movieValues.clear();
        }
    }

    @Deprecated
    private static void updateActorSQLBase(SQLiteDatabase db, Map<String, String[]> map) {
        ContentValues actorValues = new ContentValues();
        // достаем _id полов
        int gendermass[] = BaseActorPicture.getGendersId(db);
        int manId = gendermass[0];
        int womanId = gendermass[1];
        for (String name : map.keySet()) {
            // ставим имя
            actorValues.put("ACTOR_NAME", name);
            String[] mass = map.get(name);
            // ставим пол
            if (mass[0].equals(String.valueOf(1))) {
                actorValues.put("GENDER", womanId);
            } else {
                actorValues.put("GENDER", manId);
            }
            // ставим изображение
            actorValues.put("ACTOR_IMAGE_NAME", mass[1]);
            db.insert("ACTORS", null, actorValues);
            actorValues.clear();
        }
    }


    @Deprecated
    private static void downloadFiles(final MainActivity activity, Set<String> files, File cacheDir) {
        StringBuilder builder = new StringBuilder();
        int countOfDownloads = 0;
        int step = (int) (files.size()) / 9;
        byte percent = 20;
        byte tenPercent = 0;
        Log.d(LOG_DOWNLOAD, "Всё готово для загрузки");
        for (String end : files) {
            try {
                builder.append(SITE_PATH);
                builder.append(end);
                URL url = new URL(builder.toString());
                Log.d(LOG_DOWNLOAD, builder.toString());
                builder = new StringBuilder();
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();
                String str = String.valueOf(urlConnection.getResponseCode());
                Log.d(LOG_DOWNLOAD, str);
                File file = new File(cacheDir, end);
                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                }
                fileOutput.close();
                urlConnection.disconnect();
                if (++tenPercent % step == 0) {
                    activity.updateStatus(percent);
                    percent += 10;
                    tenPercent = 0;
                }
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final Exception e) {
            }
        }
    }

    // загрузка файла с именами фильмов и изображений и создание на основе него Map
    @Deprecated
    static private Map downloadMovieBase(File cacheDir) {
        Map movieMap = new HashMap<String, String[]>();
        File file = new File(cacheDir, MOVIE_BASE_FILE);
        try {
            URL url = new URL(SITE_PATH.concat(MOVIE_BASE_FILE));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();
            urlConnection.disconnect();
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final Exception e) {
        }
        String cvsSplitBy = ";";
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);
                movieMap.put(values[0], new String[]{values[1], values[2]});
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return movieMap;
    }

    // загрузка файла с именами актеров и изображений и создание на основе него Map
    @Deprecated
    static private Map downloadActorBase(File cacheDir) {
        Map actorMap = new HashMap<String, String[]>();
        File file = new File(cacheDir, ACTOR_BASE_FILE);
        try {
            URL url = new URL(SITE_PATH.concat(ACTOR_BASE_FILE));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();
            urlConnection.disconnect();
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final Exception e) {
        }
        String cvsSplitBy = ";";
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);
                actorMap.put(values[0], new String[]{values[1], values[2]});
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return actorMap;
    }


    // формирование списка файлов для загрузки
    @Deprecated
    static private Set getFiles(Map<String, String[]> movies, Map<String, String[]> actors) {
        Set<String> allFiles = new HashSet();
        for (String[] files : movies.values()) {
            for (String file : files) {
                allFiles.add(file);
            }
        }
        for (String[] files : actors.values()) {
            allFiles.add(files[1]);
        }
        return allFiles;
    }
}
