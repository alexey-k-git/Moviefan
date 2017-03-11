package com.restfulrobot.moviefan.game_activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.restfulrobot.moviefan.R;
import com.restfulrobot.moviefan.database.BaseActorPicture;
import com.restfulrobot.moviefan.GeneralMethods;
import com.restfulrobot.moviefan.database.MoviefanDatabaseHelper;
import com.restfulrobot.moviefan.structure.ActorPicture;
import com.restfulrobot.moviefan.structure.MovieActors;
import com.restfulrobot.moviefan.structure.MoviePicture;
import com.restfulrobot.moviefan.structure.Question;
import com.restfulrobot.moviefan.game_activities.BlitzActivity.TypeOfQuestion;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static com.restfulrobot.moviefan.game_activities.BlitzActivity.TypeOfQuestion.MOVIE_ACTORS;

public class BlitzFragment extends Fragment {

    private TextView gameOverResultTextView;
    private FragListener listener;
    private ActorPicture actorPicture = null;
    private MoviePicture moviePicture = null;
    private TypeOfQuestion typeOfQuestion;
    private String[] answers;
    private Button[] arrayBtn;
    private String[] variantsOfAnswers;
    private ArrayList<String> variantsOfMovieActors;
    private ArrayList<String> variantsOfMovies;
    private ArrayList<String> variantsOfActors;
    private File cacheFolder;
    private LinearLayout blitzMovieOrActorLayout;
    private LinearLayout blitzMovieActorsLayout;
    private String actorName;
    private String actorPic;
    private String movie;
    private boolean actorPlaysInMovieFlag;
    private boolean rotateFlag = false;
    private String moviePic;
    private boolean endOfGame = false;
    private float gameOverPoints;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            endOfGame = savedInstanceState.getBoolean("endOfGame");
            if (endOfGame) {
                gameOverPoints = savedInstanceState.getFloat("gameOverPoints");
            } else {
                typeOfQuestion = (TypeOfQuestion) savedInstanceState.getSerializable("typeOfQuestion");
                cacheFolder = (File) savedInstanceState.getSerializable("Folder");
                switch (typeOfQuestion) {
                    case MOVIE: {
                        variantsOfAnswers = savedInstanceState.getStringArray("variantsOfAnswers");
                        moviePicture = (MoviePicture) savedInstanceState.getSerializable("moviePicture");
                        moviePic = savedInstanceState.getString("moviePic");
                    }
                    break;
                    case ACTOR: {
                        variantsOfAnswers = savedInstanceState.getStringArray("variantsOfAnswers");
                        actorPicture = (ActorPicture) savedInstanceState.getSerializable("actorPicture");
                    }
                    break;
                    case MOVIE_ACTORS: {
                        actorName = savedInstanceState.getString("actorName");
                        actorPic = savedInstanceState.getString("actorPic");
                        movie = savedInstanceState.getString("movie");
                        actorPlaysInMovieFlag = savedInstanceState.getBoolean("actorPlaysInMovieFlag");
                    }
                    break;
                }
                rotateFlag = true;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.listener = ((BlitzActivity) context);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = ((BlitzActivity) activity);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            View.OnClickListener listenerOfAnswerBtn = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.blitzItemClicked(checkAnswer(v));
                }
            };
            blitzMovieOrActorLayout = (LinearLayout) view.findViewById(R.id.blitzMovieOrActorLayout);
            blitzMovieActorsLayout = (LinearLayout) view.findViewById(R.id.blitzMovieActorsLayout);
            gameOverResultTextView = (TextView) view.findViewById(R.id.gameOverResult);
            if (endOfGame) {
                finishGame(gameOverPoints);
                return;
            }
            switch (typeOfQuestion) {
                case MOVIE: {
                    blitzMovieOrActorLayout.setVisibility(View.VISIBLE);
                    ImageView movieImage = (ImageView) view.findViewById(R.id.imageBlitzMovie);
                    movieImage.setVisibility(View.VISIBLE);
                    TextView questionTextView = (TextView) view.findViewById(R.id.questionForPicture);
                    questionTextView.setText(R.string.what_is_movie);
                    fillArrayOfButtons(view, moviePicture.getAnswer(), variantsOfMovies, listenerOfAnswerBtn, rotateFlag);
                    if (!rotateFlag) {
                        moviePic = moviePicture.getImageFileName();
                    }
                    Bitmap picture = GeneralMethods.getBitmap(moviePic, cacheFolder);
                    movieImage.setImageBitmap(picture);
                }
                break;
                case ACTOR: {
                    blitzMovieOrActorLayout.setVisibility(View.VISIBLE);
                    ImageView actorImage = (ImageView) view.findViewById(R.id.imageBlitzActor);
                    actorImage.setVisibility(View.VISIBLE);
                    Bitmap picture = GeneralMethods.getBitmap(actorPicture.getImageFileName(), cacheFolder);
                    actorImage.setImageBitmap(picture);
                    TextView questionTextView = (TextView) view.findViewById(R.id.questionForPicture);
                    questionTextView.setText(R.string.who_is_actor);
                    fillArrayOfButtons(view, actorPicture.getName(), variantsOfActors, listenerOfAnswerBtn, rotateFlag);
                }
                break;
                case MOVIE_ACTORS: {
                    blitzMovieActorsLayout.setVisibility(View.VISIBLE);
                    TextView actorNameTextView = (TextView) view.findViewById(R.id.blitzActorName);
                    ImageView actorImage = (ImageView) view.findViewById(R.id.imageBlitzActorForMovie);
                    TextView movieNameTextView = (TextView) view.findViewById(R.id.blitzMovie);
                    Button btnYes = (Button) view.findViewById(R.id.blitzYesBtn);
                    Button btnNo = (Button) view.findViewById(R.id.blitzNoBtn);
                    btnYes.setOnClickListener(listenerOfAnswerBtn);
                    btnNo.setOnClickListener(listenerOfAnswerBtn);
                    if (!rotateFlag) {
                        Random random = new Random();
                        actorPlaysInMovieFlag = random.nextBoolean();
                        if (actorPlaysInMovieFlag) {
                            actorName = answers[random.nextInt(answers.length)];
                        } else {
                            actorName = variantsOfMovieActors.get(random.nextInt(variantsOfMovieActors.size()));
                        }
                        actorPic = BaseActorPicture
                                .getPicturesForTheseActors
                                        (new MoviefanDatabaseHelper(this.getActivity().getApplication()), new String[]{actorName})[0];

                    }
                    actorImage.setImageBitmap(GeneralMethods.getBitmap(actorPic, cacheFolder));
                    actorNameTextView.setText(actorName);
                    movieNameTextView.setText("Снимался в фильме \"" + movie + "\"?");
                }
                break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("endOfGame", endOfGame);
        if (endOfGame) {
            outState.putFloat("gameOverPoints", gameOverPoints);
        } else {
            outState.putSerializable("typeOfQuestion", typeOfQuestion);
            outState.putSerializable("Folder", cacheFolder);
            switch (typeOfQuestion) {
                case MOVIE: {
                    variantsOfAnswers = new String[3];
                    for (int i = 0; i < 3; i++) {
                        variantsOfAnswers[i] = arrayBtn[i].getText().toString();
                    }
                    outState.putStringArray("variantsOfAnswers", variantsOfAnswers);
                    outState.putSerializable("moviePicture", moviePicture);
                    outState.putString("moviePic", moviePic);
                }
                break;
                case ACTOR: {
                    variantsOfAnswers = new String[3];
                    for (int i = 0; i < 3; i++) {
                        variantsOfAnswers[i] = arrayBtn[i].getText().toString();
                    }
                    outState.putStringArray("variantsOfAnswers", variantsOfAnswers);
                    outState.putSerializable("actorPicture", actorPicture);
                }
                break;
                case MOVIE_ACTORS: {
                    outState.putString("actorName", actorName);
                    outState.putString("actorPic", actorPic);
                    outState.putString("movie", movie);
                    outState.putBoolean("actorPlaysInMovieFlag", actorPlaysInMovieFlag);
                }
                break;
            }
        }
        super.onSaveInstanceState(outState);
    }

    private void fillArrayOfButtons(View view, String answer, ArrayList<String> variantsOfAnswers, View.OnClickListener listener, Boolean rotate) {
        arrayBtn = new Button[4];
        arrayBtn[0] = (Button) view.findViewById(R.id.btnVar1);
        arrayBtn[1] = (Button) view.findViewById(R.id.btnVar2);
        arrayBtn[2] = (Button) view.findViewById(R.id.btnVar3);
        if (rotate) {
            for (int b = 0; b < 3; b++) {
                arrayBtn[b].setText(this.variantsOfAnswers[b]);
                arrayBtn[b].setOnClickListener(listener);
            }
        } else {
            Random randNumber = new Random();
            int iNumber = randNumber.nextInt(3);
            for (int b = 0; b < 3; b++) {
                if (b == iNumber) {
                    arrayBtn[b].setText(answer);
                } else {
                    int rand = randNumber.nextInt(variantsOfAnswers.size());
                    arrayBtn[b].setText(variantsOfAnswers.remove(rand));
                }
                arrayBtn[b].setOnClickListener(listener);
            }
        }
    }

    private float checkAnswer(View view) {
        float point = 0f;
        switch (typeOfQuestion) {
            case MOVIE: {
                if (((Button) view).getText().equals(moviePicture.getAnswer())) {
                    point += 1f;
                } else {
                    point -= 0.5f;
                }
            }
            break;
            case ACTOR: {
                if (((Button) view).getText().equals(actorPicture.getName())) {
                    point += 1.5f;
                } else {
                    point -= 1f;
                }
            }
            break;
            case MOVIE_ACTORS: {
                if (actorPlaysInMovieFlag & ((Button) view).getId() == R.id.blitzYesBtn
                        || !actorPlaysInMovieFlag & ((Button) view).getId() == R.id.blitzNoBtn) {
                    point += 2f;
                } else {
                    point -= 1f;
                }
            }
            break;
        }
        return point;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_blitz, container, false);
    }


    public String[] getMovieActors() {
        return answers;
    }

    public ActorPicture getActorPicture() {
        return actorPicture;
    }

    public MoviePicture getMoviePicture() {
        return moviePicture;
    }

    public void setQuestion(Question question, File file) {
        cacheFolder = file;
        if (question instanceof MovieActors) {
            MovieActors movieActors = ((MovieActors) question);
            movie = movieActors.getMovie();
            answers = movieActors.getRandomActors();
            typeOfQuestion = MOVIE_ACTORS;
        } else if (question instanceof ActorPicture) {
            actorPicture = ((ActorPicture) question);
            typeOfQuestion = typeOfQuestion.ACTOR;
        } else if (question instanceof MoviePicture) {
            moviePicture = ((MoviePicture) question);
            typeOfQuestion = typeOfQuestion.MOVIE;
        }
    }

    public void setMovieVariants(ArrayList<String> variantsOfMovies) {
        this.variantsOfMovies = variantsOfMovies;
    }

    public void setActorVariants(ArrayList<String> variantsOfActors) {
        this.variantsOfActors = variantsOfActors;
    }

    public void setMovieActorsVariants(ArrayList<String> variantsOfMovieActors) {
        this.variantsOfMovieActors = variantsOfMovieActors;
    }

    public BlitzActivity.TypeOfQuestion getTypeOfQuestion() {
        return typeOfQuestion;
    }

    public void finishGame(float gameOverPoints) {
        this.gameOverPoints = gameOverPoints;
        gameOverResultTextView.setText(String.valueOf(gameOverPoints));
        blitzMovieOrActorLayout.setVisibility(View.GONE);
        blitzMovieActorsLayout.setVisibility(View.GONE);
        getView().findViewById(R.id.blitzGameOver).setVisibility(View.VISIBLE);
        endOfGame = true;
    }

    public interface FragListener {
        void blitzItemClicked(float points);
    }
}
