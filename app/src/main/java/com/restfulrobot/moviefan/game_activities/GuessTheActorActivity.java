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
import com.restfulrobot.moviefan.database.BaseActorPicture;
import com.restfulrobot.moviefan.database.BaseHints;
import com.restfulrobot.moviefan.database.MoviefanDatabaseHelper;
import com.restfulrobot.moviefan.structure.ActorPicture;

import java.io.File;
import java.util.Random;

import static com.restfulrobot.moviefan.GeneralMethods.makeToast;

public class GuessTheActorActivity extends Activity implements GuessTheActorFragment.FragListener, View.OnClickListener {

    public static File cacheFolder;
    private ActorPicture actorPicture;
    private int sourceColor;
    private int currentColor;
    private int countOfUsedHint = 2;
    private int countOfHint = 10;
    private Boolean wasHintLessVariantsFlag = false;
    private Boolean rotateFlag = false;
    private Boolean wasAnswerFlag;
    private Boolean hintEnableFlag;
    private RelativeLayout mainBackgroundLayout;
    private TextView hintCountTextView;
    private Button hintBtn;
    private HintActorDialog dialog;
    private GuessTheActorFragment guessTheActorFragment;

    @Override
    public void onClick(View v) {
        int buttonId = ((Button) v).getId();
        if (buttonId != R.id.btnMovieHintCancel) {
            usingHints();
            if (buttonId == R.id.btnActorHintLessVariants) {
                guessTheActorFragment.hintUsingLessVariants();
                wasHintLessVariantsFlag = true;
        //        makeToast(this, "Меньше вариантов", Toast.LENGTH_SHORT);
            }
            updateHints();
        }
        if (dialog != null) {
            dialog.dismiss();
        } else {
            ((HintActorDialog) getFragmentManager().findFragmentByTag("HintActorDialog")).dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_the_actor);
        mainBackgroundLayout = (RelativeLayout) findViewById(R.id.mainBackground);
        hintBtn = (Button) findViewById(R.id.hintBtn);
        hintCountTextView = (TextView) findViewById(R.id.hintCountTxt);
        Intent intent = getIntent();
        cacheFolder = (File) intent.getSerializableExtra("cacheFolder");
        hintEnableFlag = true;
        boolean newHInts = false;
        if (savedInstanceState == null) {
            wasAnswerFlag = false;
            Drawable background = mainBackgroundLayout.getBackground();
            if (background instanceof ColorDrawable) {
                sourceColor = ((ColorDrawable) background).getColor();
            }
            currentColor = sourceColor;
            try {
                BaseActorPicture.createListMyActorsPictures(new MoviefanDatabaseHelper(this.getApplicationContext()));
                newHInts = BaseHints.updateHintBase(new MoviefanDatabaseHelper(this.getApplicationContext()));
                int hints[] = BaseHints.getHintsInfo(new MoviefanDatabaseHelper(this.getApplicationContext()));
                countOfUsedHint = hints[0];
                countOfHint = hints[1];
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        } else {
            actorPicture = (ActorPicture) savedInstanceState.getSerializable("AP");
            wasHintLessVariantsFlag = savedInstanceState.getBoolean("wasHintLessVariantsFlag");
            hintEnableFlag = savedInstanceState.getBoolean("hintEnableFlag");
            countOfUsedHint = savedInstanceState.getInt("countOfUsedHint");
            countOfHint = savedInstanceState.getInt("countOfHint");
            sourceColor = savedInstanceState.getInt("sourceColor");
            currentColor = savedInstanceState.getInt("currentColor");
            wasAnswerFlag = savedInstanceState.getBoolean("wasAnswerFlag");
            rotateFlag = true;
            if (getFragmentManager().findFragmentByTag("HintActorDialog") != null) {
                dialog = (HintActorDialog) getFragmentManager().findFragmentByTag("HintActorDialog");
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

    public void wasAnswer() {
        wasAnswerFlag = true;
        hintBtn.setEnabled(false);
    }

    public void setNewFrag(GuessTheActorFragment frag) {
        this.guessTheActorFragment = frag;
    }


    public void useHints(View v) {
        dialog = new HintActorDialog();
        dialog.setHintsEnable(wasHintLessVariantsFlag);
        dialog.show(getFragmentManager(), "HintActorDialog");
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
        outState.putSerializable("AP", actorPicture);
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
        guessTheActorFragment = new GuessTheActorFragment();
        Random randNumber = new Random();
        int iNumber = randNumber.nextInt(BaseActorPicture.sizeActors());
        actorPicture = BaseActorPicture.getActor(iNumber);
        guessTheActorFragment.setActor(actorPicture, cacheFolder);
        if (BaseActorPicture.isLastActor()) guessTheActorFragment.last();
        guessTheActorFragment.setVariants(BaseActorPicture.otherVariants(actorPicture.getName(), actorPicture.getGender()));
        transitFragment();
        updateHints();
    }

    //подмена фрагмента
    private void transitFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, guessTheActorFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}
