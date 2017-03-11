package com.restfulrobot.moviefan.game_activities;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.restfulrobot.moviefan.R;
import com.restfulrobot.moviefan.achievement.UserStatistics;
import com.restfulrobot.moviefan.database.BaseActorPicture;
import com.restfulrobot.moviefan.database.BaseMovieActors;
import com.restfulrobot.moviefan.database.BaseMoviePicture;
import com.restfulrobot.moviefan.database.MoviefanDatabaseHelper;
import com.restfulrobot.moviefan.structure.ActorPicture;
import com.restfulrobot.moviefan.structure.MovieActors;
import com.restfulrobot.moviefan.structure.MoviePicture;
import com.restfulrobot.moviefan.structure.Question;

import java.io.File;
import java.util.Random;
import java.util.Stack;

public class BlitzActivity extends Activity implements BlitzFragment.FragListener,  BlitzTimeFragment.FragListener {

    Stack<MoviePicture> moviePicturesStack;
    Stack<ActorPicture> actorPicturesStack;
    Stack<MovieActors> movieActorsStack;
    TextView blitzHelloTextView;
    FrameLayout frameLayout;
    BlitzTimeFragment blitzTimeFragment;
    BlitzFragment blitzFragment;
    File cacheFolder;
    private float userPoints = 0;
    private boolean gameIsStarted=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blitz);
        frameLayout = (FrameLayout) findViewById(R.id.fragment_container);
        blitzTimeFragment = (BlitzTimeFragment)getFragmentManager().findFragmentById(R.id.blitz_time_frag);
        blitzHelloTextView = (TextView) findViewById(R.id.blitz_hello_text);
        blitzHelloTextView.setOnClickListener(blitzTimeFragment);
        Intent intent = getIntent();
        cacheFolder = (File) intent.getSerializableExtra("cacheFolder");
        if (savedInstanceState == null) {
            moviePicturesStack = new Stack<>();
            actorPicturesStack = new Stack<>();
            movieActorsStack = new Stack<>();
            try {
                MoviefanDatabaseHelper moviefanDatabaseHelper = new MoviefanDatabaseHelper(this.getApplicationContext());
                BaseMoviePicture.createListMyMoviesPictures(moviefanDatabaseHelper);
                BaseActorPicture.createListMyActorsPictures(moviefanDatabaseHelper);
                BaseMovieActors.createListMyMoviesAndActorsPictures(moviefanDatabaseHelper);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            BaseMoviePicture.setMoviePictureForBlitz(moviePicturesStack);
            BaseActorPicture.setActorPictureForBlitz(actorPicturesStack);
            BaseMovieActors.setMovieActorsForBlitz(movieActorsStack);

        } else
        {
            userPoints = savedInstanceState.getFloat("userPoints");
            moviePicturesStack = (Stack<MoviePicture>) savedInstanceState.getSerializable("moviePicturesStack");
            actorPicturesStack = (Stack<ActorPicture>)savedInstanceState.getSerializable("actorPicturesStack");
            movieActorsStack = (Stack<MovieActors>) savedInstanceState.getSerializable("movieActorsStack");
            gameIsStarted =  savedInstanceState.getBoolean("gameIsStarted");
            if (gameIsStarted)
            {
                blitzHelloTextView.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
            }
        }
    }

        public Question getNextQuestion() {
        Question question=null;
        if (moviePicturesStack.empty() && actorPicturesStack.empty() && movieActorsStack.empty())
        {
            return question;
        }
        int typeOfAnswer = new Random().nextInt(3);
        switch (typeOfAnswer)
        {
            case 0:
            {
                if (!moviePicturesStack.empty())
                {
                    question = moviePicturesStack.pop();
                }
                else
                {
                    question = getNextQuestion();
                }
                break;
            }
            case 1:
            {
                if (!actorPicturesStack.empty())
                {
                    question = actorPicturesStack.pop();
                }
                else
                {
                    question = getNextQuestion();
                }
                break;
            }

            case 2:
            {
                if (!movieActorsStack.empty())
                {
                    question = movieActorsStack.pop();

                }
                else
                {
                    question = getNextQuestion();
                }
                break;
            }

        }
        return  question;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("moviePicturesStack", moviePicturesStack);
        outState.putSerializable("actorPicturesStack", actorPicturesStack);
        outState.putSerializable("movieActorsStack", movieActorsStack);
        outState.putFloat("userPoints", userPoints);
        outState.putBoolean("gameIsStarted", gameIsStarted);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void blitzItemClicked(float points) {
        addPoints(points);
        blitzFragment = new BlitzFragment();
        blitzFragment.setQuestion(getNextQuestion(), cacheFolder);
        setVariants(blitzFragment);
        transitFragment();
    }

    private void addPoints(float points) {
        userPoints+=points;
        if (userPoints<0) {
            userPoints = 0;
            blitzTimeFragment.cutTime();
        }
        blitzTimeFragment.setPoints(userPoints);
    }

    public void stopGame()
    {
        if (blitzFragment ==null)
        {
            blitzFragment = (BlitzFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
        }
        UserStatistics.updateBlitzRecord(this, userPoints);
        blitzFragment.finishGame(userPoints);
    }

    //подмена фрагмента
    private void transitFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, blitzFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void blitzStartTimeClicked() {
        blitzHelloTextView.setVisibility(View.GONE);
        frameLayout.setVisibility(View.VISIBLE);
        gameIsStarted = true;
        blitzItemClicked(0f);
    }

    public void setVariants(BlitzFragment frag) {
        TypeOfQuestion typeOfQuestion = frag.getTypeOfQuestion();
        switch (typeOfQuestion)
        {
            case MOVIE: {
                frag.setMovieVariants(BaseMoviePicture.otherVariants(frag.getMoviePicture().getAnswer()));
            } ; break;
            case ACTOR: {
                ActorPicture actorPicture = frag.getActorPicture();
                frag.setActorVariants(BaseActorPicture.otherVariants(actorPicture.getName(),
                        actorPicture.getGender()));
            } ; break;
            case MOVIE_ACTORS: {
                frag.setMovieActorsVariants(BaseMovieActors.otherVariants(frag.getMovieActors()));
            } ; break;
        }
    }

    public enum TypeOfQuestion {
        MOVIE,
        ACTOR,
        MOVIE_ACTORS
    }
}
