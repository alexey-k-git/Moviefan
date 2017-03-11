package com.restfulrobot.moviefan.game_activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.restfulrobot.moviefan.R;
import com.restfulrobot.moviefan.database.BaseHints;
import com.restfulrobot.moviefan.database.BaseMoviePicture;
import com.restfulrobot.moviefan.structure.MoviePicture;
import com.restfulrobot.moviefan.database.MoviefanDatabaseHelper;

import java.io.File;
import java.util.Random;

import static com.restfulrobot.moviefan.GeneralMethods.makeToast;
import static com.restfulrobot.moviefan.logs.MovieFanLogs.LOG_S;


public class GuessTheMovieActivity extends Activity implements GuessTheMovieFragment.FragListener, View.OnClickListener {

    public static File cacheFolder;
    private MoviePicture moviePicture;
    private int sourceColor;
    private int currentColor;
    private int countOfUsedHint = 95;
    private int countOfHint = 99;
    private Boolean rotateFlag = false;
    private Boolean wasAnswerFlag;
    private Boolean wasHintLessVariantsFlag;
    private Boolean hintEnableFlag;
    private Button hintBtn;
    private TextView hintCountTextView;
    private HintMovieDialog dialog;
    private GuessTheMovieFragment guessTheMovieFragment;
    private RelativeLayout mainBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_S, "onCreate " + "MovieActivity");
        setContentView(R.layout.activity_guess_the_movie);
        mainBackground = (RelativeLayout) findViewById(R.id.mainBackground);
        hintBtn = (Button) findViewById(R.id.hintBtn);
        hintCountTextView = (TextView) findViewById(R.id.hintCountTxt);
        Intent intent = getIntent();
        cacheFolder = (File) intent.getSerializableExtra("cacheFolder");
        hintEnableFlag = true;
        boolean newHInts = false;
        if (savedInstanceState == null) {
            wasHintLessVariantsFlag = true;
            wasAnswerFlag = false;
            Drawable background = mainBackground.getBackground();
            if (background instanceof ColorDrawable) {
                sourceColor = ((ColorDrawable) background).getColor();
            }
            currentColor = sourceColor;
            try {
                BaseMoviePicture.createListMyMoviesPictures(new MoviefanDatabaseHelper(this.getApplicationContext()));
                newHInts = BaseHints.updateHintBase(new MoviefanDatabaseHelper(this.getApplicationContext()));
                int hints[] = BaseHints.getHintsInfo(new MoviefanDatabaseHelper(this.getApplicationContext()));
                countOfUsedHint = hints[0];
                countOfHint = hints[1];
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        } else {
            wasHintLessVariantsFlag = savedInstanceState.getBoolean("wasHintLessVariantsFlag");
            hintEnableFlag = savedInstanceState.getBoolean("hintEnableFlag");
            countOfUsedHint = savedInstanceState.getInt("countOfUsedHint");
            countOfHint = savedInstanceState.getInt("countOfHint");
            sourceColor = savedInstanceState.getInt("sourceColor");
            currentColor = savedInstanceState.getInt("currentColor");
            wasAnswerFlag = savedInstanceState.getBoolean("wasAnswerFlag");
            rotateFlag = true;
            if (getFragmentManager().findFragmentByTag("HintMovieDialog") != null) {
                dialog = (HintMovieDialog) getFragmentManager().findFragmentByTag("HintMovieDialog");
                dialog.setHintsEnable(wasHintLessVariantsFlag);
            }
        }
        updateHints();
        if (newHInts) {
            makeToast(this, "Новый день. Новые подсказки!", Toast.LENGTH_SHORT);
        }
    }

    public void setColor(int color) {
        currentColor = color;
    }

    public void useHints(View v) {
        dialog = new HintMovieDialog();
        dialog.setHintsEnable(wasHintLessVariantsFlag);
        dialog.show(getFragmentManager(), "HintMovieDialog");
    }

    public void setNewFrag(GuessTheMovieFragment frag) {
        this.guessTheMovieFragment = frag;
    }


    @Override
    public void onClick(View v) {
        int buttonId = ((Button) v).getId();
        if (buttonId != R.id.btnMovieHintCancel) {
            usingHints();
            if (buttonId == R.id.btnMovieHintChangeScreen) {
                guessTheMovieFragment.hintUsingAnotherPicture();
             //   makeToast(this, "Изменился скрин", Toast.LENGTH_SHORT);
            }
            if (buttonId == R.id.btnMovieHintLessVariants) {
                guessTheMovieFragment.hintUsingLessVariants();
                wasHintLessVariantsFlag = true;
            //    makeToast(this, "Меньше вариантов", Toast.LENGTH_SHORT);
            }
            updateHints();
        }
        if (dialog != null) {
            dialog.dismiss();
        } else {
            ((HintMovieDialog) getFragmentManager().findFragmentByTag("HintMovieDialog")).dismiss();
        }
    }

    public void wasAnswer() {
        wasAnswerFlag = true;
        hintBtn.setEnabled(false);
    }

    private void usingHints() {
        countOfUsedHint++;
        BaseHints.updateUsedHint(new MoviefanDatabaseHelper(this.getApplicationContext()), countOfUsedHint);
    }

    private void updateHints() {
        hintCountTextView.setText(countOfUsedHint + "/" + countOfHint);
        if (countOfUsedHint >= countOfHint || wasAnswerFlag) {
            hintEnableFlag = false;

        } else {
            hintEnableFlag = true;
        }
        hintBtn.setEnabled(hintEnableFlag);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_S, "onSaveInstanceState " + "MovieActivity");
        outState.putBoolean("wasHintLessVariantsFlag", wasHintLessVariantsFlag);
        outState.putBoolean("hintEnableFlag", hintEnableFlag);
        outState.putBoolean("wasAnswerFlag", wasAnswerFlag);
        outState.putInt("countOfHint", countOfHint);
        outState.putInt("countOfUsedHint", countOfUsedHint);
        outState.putInt("sourceColor", sourceColor);
        outState.putInt("currentColor", currentColor);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!rotateFlag) {
            itemClicked();
        }
    }

    //нажата кнопка следующий
    public void itemClicked() {
        wasAnswerFlag = false;
        wasHintLessVariantsFlag = false;
        guessTheMovieFragment = new GuessTheMovieFragment();
        Random randNumber = new Random();
        int iNumber = randNumber.nextInt(BaseMoviePicture.sizeMovies());
        moviePicture = BaseMoviePicture.getMovie(iNumber);
        guessTheMovieFragment.setMovie(moviePicture, cacheFolder);
        if (BaseMoviePicture.isLastMovie()) guessTheMovieFragment.last();
        guessTheMovieFragment.setVariants(BaseMoviePicture.otherVariants(moviePicture.getAnswer()));
        transitFragment();
        updateHints();
    }

    //подмена фрагмента
    private void transitFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, guessTheMovieFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

}
