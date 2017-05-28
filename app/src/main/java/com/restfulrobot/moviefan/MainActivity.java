package com.restfulrobot.moviefan;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.restfulrobot.moviefan.game_activities.GameSelectionActivityOld;
import com.restfulrobot.moviefan.game_activities.GameSelectionActivity;

import java.io.File;

import static com.restfulrobot.moviefan.GeneralMethods.makeToast;
import static com.restfulrobot.moviefan.logs.MovieFanLogs.LOG_MAIN_MENU;
import static com.restfulrobot.moviefan.logs.MovieFanLogs.LOG_S;

public class MainActivity extends Activity implements View.OnClickListener {
    protected static File cacheFolder;
    private Button startGameBtn;
    private Button settingBtn;
    private Button supportBtn;
    private Button achievementsBtn;
    private Button updateBtn;
    private Button exitBtn;
    private ProgressDialog pDialog;
    private DownloadDialog dialog;
    private boolean cancelDownloadFlag=false;
    private int countOfFilesForDownload;
    private boolean beginDownload=false;

    public static final int progress_bar_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startGameBtn = (Button) findViewById(R.id.btnStartGame);
        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameActivity(Buttons.START_GAME);
            }
        });
        settingBtn = (Button) findViewById(R.id.btnSetting);
        settingBtn.setEnabled(true);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameActivity(Buttons.SETTING);
            }
        });
        supportBtn = (Button) findViewById(R.id.btnSupport);
        supportBtn.setEnabled(false);
        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameActivity(Buttons.SUPPORT);
            }
        });
        achievementsBtn = (Button) findViewById(R.id.btnAchievements);
        achievementsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameActivity(Buttons.ACHIEVEMENTS);
            }
        });
        exitBtn = (Button) findViewById(R.id.btnExit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });
        updateBtn = (Button) findViewById(R.id.btnUpdate);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnline())
                {
                    makeToast(MainActivity.this, "Отсутсвует соединение с интернетом.", Toast.LENGTH_SHORT);
                    return;
                }
                makeMainButtonsActive(false);
                makeToast(MainActivity.this, "Началась загрузка базы данных фильма.", Toast.LENGTH_SHORT);
                Log.d(LOG_MAIN_MENU, "Нажата кнопка обновить");
                FullUpdate update = new FullUpdate();
                dialog = new DownloadDialog();
                dialog.show(getFragmentManager(), "DownloadDialog");
                beginDownload=true;
                cancelDownloadFlag=false;
                update.setDialog(dialog);
                update.setActivity(MainActivity.this);
                update.execute();

                }
                /*
                new Thread(new Runnable() {
                    public void run() {
                        FullUpdate.update(MainActivity.this);
                    }
                }).start();
                */
            });
        cacheFolder = getCacheFolder(MainActivity.this);
        if (savedInstanceState != null)
        {
            Fragment frag = getFragmentManager().findFragmentByTag("DownloadDialog");
            if (frag!=null)
            {
                dialog = (DownloadDialog)frag;
                beginDownload=true;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_S, "onSaveInstanceState " + "MainActivity");
        outState.putBoolean("beginDownload", beginDownload);
        super.onSaveInstanceState(outState);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public void cancelDownload()
    {
        cancelDownloadFlag=true;
        beginDownload=false;
        makeMainButtonsActive(true);
        makeToast(this, "Загрузка Базы данных прервана", Toast.LENGTH_SHORT);
    }

    public boolean checkCancelFlag()
    {
        return  cancelDownloadFlag;
    }


    public void closeDialog()
    {
        beginDownload=false;
    }

    // Устаревший диалог
    @Deprecated
    public ProgressDialog getProgressBar()
    {
        return pDialog;
    }

    @Deprecated
    public void setCountOfFilesForDownload(int countOfFilesForDownload) {
        this.countOfFilesForDownload = countOfFilesForDownload;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Идёт обновление базы данных приложения...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(countOfFilesForDownload);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }



    private void makeMainButtonsActive(Boolean active)
    {
        startGameBtn.setEnabled(active);
        achievementsBtn.setEnabled(active);
        settingBtn.setEnabled(active);
        updateBtn.setEnabled(active);
    }

    public void endOfUpdate(final Boolean success) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (success) {
                    makeToast(MainActivity.this, "Загрузка завершена успешно", Toast.LENGTH_SHORT);
                } else {
                    makeToast(MainActivity.this, "Ошибка при загрузке. Попробуйте позже.", Toast.LENGTH_SHORT);
                }
                makeMainButtonsActive(true);
            }
        });
    }


    public void updateStatus(final byte percent) {
        runOnUiThread(new Runnable() {
            public void run() {
                    makeToast(MainActivity.this, "Загрузка завершена на " + String.valueOf(percent) +" %", Toast.LENGTH_SHORT);
            }
        });
    }

    private void startGameActivity(Buttons currentButton) {
        Class activityClass = null;
        switch (currentButton) {
            case START_GAME:
                activityClass = GameSelectionActivity.class;
                break;
            case SETTING:
                activityClass = SettingActivity.class;
                break;
            case ACHIEVEMENTS:
                activityClass = AchievementActivity.class;
                break;
            case SUPPORT:
                activityClass = GameSelectionActivity.class;
                break;
            default:
                activityClass = GameSelectionActivityOld.class;
                break;
        }
        Log.d(LOG_MAIN_MENU, "Нажата кнопка " + currentButton + " запускается класс " + activityClass);
        Intent intent = new Intent(this, activityClass);
        if (currentButton == Buttons.START_GAME) {
            if (!GeneralMethods.checkOpportunityForStart(this.getApplicationContext()))
            {
                makeToast(MainActivity.this, "Нет возможности для запуска.\nПожалуйста, обновите базу фильмов." , Toast.LENGTH_SHORT);
                return;
            }
            intent.putExtra("cacheFolder", cacheFolder);
        }
        intent.putExtra("cacheFolder", cacheFolder);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int buttonId = ((Button)view).getId();
        if (buttonId==R.id.btnDownloadCancel)
        {
                cancelDownload();
        }
    }

    enum Buttons {
        START_GAME,
        SETTING,
        ACHIEVEMENTS,
        SUPPORT
    }

    protected File getCacheFolder(Context context) {
        File cacheDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "moviefanCache");
            if (!cacheDir.isDirectory()) {
                cacheDir.mkdirs();
            }
        }
        if (!cacheDir.isDirectory()) {
            cacheDir = context.getCacheDir();
        }
        return cacheDir;
    }



}
