package com.restfulrobot.moviefan.game_activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.restfulrobot.moviefan.R;
import com.restfulrobot.moviefan.database.BaseHints;
import com.restfulrobot.moviefan.database.BaseMovieActors;
import com.restfulrobot.moviefan.database.MoviefanDatabaseHelper;
import com.restfulrobot.moviefan.structure.MovieActors;

import java.io.File;
import java.util.Random;

import static com.restfulrobot.moviefan.GeneralMethods.makeToast;

public class GuessTheMovieActorsActivity extends Activity implements GuessTheMovieActorsFragment.FragListener, View.OnClickListener {

    public static File cacheFolder;
    private MovieActors movieActors;
    private int sourceColor;
    private int currentColor;
    private int countOfUsedHint = 2;
    private int countOfHint = 10;
    private Boolean wasHintLessVariantsFlag = false;
    private Boolean wasHintShowScreensFlag = false;
    private Boolean wasHintShowActorsFlag = false;
    private Boolean rotateFlag = false;
    private Boolean wasAnswerFlag;
    private Boolean hintEnableFlag;
    private Button hintBtn;
    private TextView hintCountTextView;
    private HintMovieActorsDialog dialog;
    private GuessTheMovieActorsFragment guessTheMovieActorsFragment;
    private RelativeLayout mainBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_the_movie_actors);
        mainBackground = (RelativeLayout) findViewById(R.id.mainBackground);
        hintBtn = (Button) findViewById(R.id.hintBtn);
        hintCountTextView = (TextView) findViewById(R.id.hintCountTxt);
        Intent intent = getIntent();
        cacheFolder = (File) intent.getSerializableExtra("cacheFolder");
        hintEnableFlag = true;
        boolean newHInts = false;
        if (savedInstanceState == null) {
            wasAnswerFlag = false;
            Drawable background = mainBackground.getBackground();
            if (background instanceof ColorDrawable) {
                sourceColor = ((ColorDrawable) background).getColor();
            }
            currentColor = sourceColor;
            try {
                BaseMovieActors.createListMyMoviesAndActorsPictures(new MoviefanDatabaseHelper(this.getApplicationContext()));
                newHInts = BaseHints.updateHintBase(new MoviefanDatabaseHelper(this.getApplicationContext()));
                int hints[] = BaseHints.getHintsInfo(new MoviefanDatabaseHelper(this.getApplicationContext()));
                countOfUsedHint = hints[0];
                countOfHint = hints[1];
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        } else {
            movieActors = (MovieActors) savedInstanceState.getSerializable("MA");
            wasHintLessVariantsFlag = savedInstanceState.getBoolean("wasHintLessVariantsFlag");
            wasHintShowScreensFlag = savedInstanceState.getBoolean("wasHintShowScreensFlag");
            wasHintShowActorsFlag = savedInstanceState.getBoolean("wasHintShowActorsFlag");
            hintEnableFlag = savedInstanceState.getBoolean("hintEnableFlag");
            countOfUsedHint = savedInstanceState.getInt("countOfUsedHint");
            countOfHint = savedInstanceState.getInt("countOfHint");
            sourceColor = savedInstanceState.getInt("sourceColor");
            currentColor = savedInstanceState.getInt("currentColor");
            wasAnswerFlag = savedInstanceState.getBoolean("wasAnswerFlag");
            rotateFlag = true;
            if (getFragmentManager().findFragmentByTag("HintMovieActorsDialog") != null) {
                dialog = (HintMovieActorsDialog) getFragmentManager().findFragmentByTag("HintMovieActorsDialog");
                dialog.setHintsEnable(wasHintLessVariantsFlag, wasHintShowScreensFlag, wasHintShowActorsFlag);
            }
        }
        updateHints();
        if (newHInts) {
            makeToast(this, "Новый день. Новые подсказки!", Toast.LENGTH_SHORT);
        }
    }

    public void useHints(View v) {
        dialog = new HintMovieActorsDialog();
        dialog.setHintsEnable(wasHintLessVariantsFlag, wasHintShowScreensFlag, wasHintShowActorsFlag);
        dialog.show(getFragmentManager(), "HintMovieActorsDialog");
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

    public void wasAnswer() {
        wasAnswerFlag = true;
        hintBtn.setEnabled(false);
    }

    public void setNewFrag(GuessTheMovieActorsFragment frag) {
        this.guessTheMovieActorsFragment = frag;
    }

    @Override
    public void onClick(View v) {
        int buttonId = ((Button) v).getId();
        if (buttonId != R.id.btnMovieActorsHintCancel) {
            usingHints();
            if (buttonId == R.id.btnMovieActorsHintLessVariants) {
                guessTheMovieActorsFragment.hintUsingLessVariants();
                wasHintLessVariantsFlag = true;
             //   makeToast(this, "Меньше вариантов", Toast.LENGTH_SHORT);
            }
            if (buttonId == R.id.btnMovieActorsHintShowScreens) {
                guessTheMovieActorsFragment.hintUsingShowScreens();
                wasHintShowScreensFlag = true;
            //    makeToast(this, "Показать кадры", Toast.LENGTH_SHORT);
            }
            if (buttonId == R.id.btnMovieActorsHintShowActors) {
                guessTheMovieActorsFragment.hintUsingShowActors();
                wasHintShowActorsFlag = true;
             //   makeToast(this, "Показать фото актеров", Toast.LENGTH_SHORT);
            }
            updateHints();
        }
        if (dialog != null) {
            dialog.dismiss();
        } else {
            ((HintMovieActorsDialog) getFragmentManager().findFragmentByTag("HintMovieActorsDialog")).dismiss();
        }
    }

    private void usingHints() {
        countOfUsedHint++;
        BaseHints.updateUsedHint(new MoviefanDatabaseHelper(this.getApplicationContext()), countOfUsedHint);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("MA", movieActors);
        outState.putBoolean("wasHintLessVariantsFlag", wasHintLessVariantsFlag);
        outState.putBoolean("wasHintShowScreensFlag", wasHintShowScreensFlag);
        outState.putBoolean("wasHintShowActorsFlag", wasHintShowActorsFlag);
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
        wasHintShowActorsFlag = false;
        wasHintShowScreensFlag = false;
        guessTheMovieActorsFragment = new GuessTheMovieActorsFragment();
        Random randNumber = new Random();
        int iNumber = randNumber.nextInt(BaseMovieActors.sizeMovieActors());
        movieActors = BaseMovieActors.getMovieActors(iNumber);
        String movie = movieActors.getMovie();
        String[] answers = movieActors.getRandomActors();
        guessTheMovieActorsFragment.setMovieActors(movie, answers, cacheFolder);
        if (BaseMovieActors.isLastMovieActors()) guessTheMovieActorsFragment.last();
        guessTheMovieActorsFragment.setVariants(BaseMovieActors.otherVariants(answers));
        transitFragment();
        updateHints();
    }

    //подмена фрагмента
    private void transitFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, guessTheMovieActorsFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

}
