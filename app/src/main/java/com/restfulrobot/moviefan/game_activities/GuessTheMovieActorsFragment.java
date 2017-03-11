package com.restfulrobot.moviefan.game_activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import android.widget.Toast;

import com.restfulrobot.moviefan.GeneralEvents;
import com.restfulrobot.moviefan.R;
import com.restfulrobot.moviefan.achievement.UserStatistics;
import com.restfulrobot.moviefan.database.BaseActorPicture;
import com.restfulrobot.moviefan.database.BaseMoviePicture;
import com.restfulrobot.moviefan.GeneralMethods;
import com.restfulrobot.moviefan.database.MoviefanDatabaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.restfulrobot.moviefan.GeneralEvents.almostTrueAnswer;
import static com.restfulrobot.moviefan.GeneralEvents.trueAnswer;
import static com.restfulrobot.moviefan.GeneralMethods.makeToast;
import static com.restfulrobot.moviefan.logs.MovieFanLogs.LOG_GUESS_THE_MOVIE_ACTOR;


public class GuessTheMovieActorsFragment extends Fragment {

    private File cacheFolder;
    private ArrayList<String> variantsOfMovies;
    private Boolean btnSkipEnableFlag = true;
    private Boolean wasAnswerFlag = false;
    private Boolean rotateFlag = false;
    private Boolean wasHintShowScreensFlag = false;
    private Boolean wasHintShowActorsFlag = false;
    private boolean variantsEnable[];
    private boolean variantsChecked[];
    private String variants[];
    private String[] answers;
    private String movie;
    private Button btnNext;
    private Button btnConfirm;
    private TextView movieTextView;
    private CheckBox[] arrayCheckBox;
    private CheckBox[] imageArrayCheckBox;
    private ImageView[] imageArray;
    private ImageView movieScreen1ImageView;
    private ImageView movieScreen2ImageView;
    private ScrollView scrollView;
    private LinearLayout movieScreensLinearLayout;
    private GridLayout actorsPhotosGridLayout;
    private GridLayout checkBoxGridLayout;


    public void hintUsingLessVariants() {
        int answerIndex[] = new int[6];
        ArrayList<String> arrayAnswers = new ArrayList<>(Arrays.asList(answers));
        for (int b = 0, i = 0; b < 6; b++) {
            if (arrayAnswers.contains(arrayCheckBox[b].getText())) {
                answerIndex[i] = b;
                i++;
                continue;
            }
            if (arrayCheckBox[b].isChecked()) {
                arrayCheckBox[b].setChecked(false);
                imageArrayCheckBox[b].setChecked(false);
            }
            arrayCheckBox[b].setEnabled(false);
            imageArrayCheckBox[b].setEnabled(false);
            imageArray[b].setEnabled(false);
            variantsEnable[b] = false;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(6);
        int countAnotherVariants = 2;
        while (countAnotherVariants > 0) {
            while (arrayAnswers.contains(arrayCheckBox[randomIndex])) {
                randomIndex = random.nextInt(6);
            }
            arrayCheckBox[randomIndex].setEnabled(true);
            imageArrayCheckBox[randomIndex].setEnabled(true);
            imageArray[randomIndex].setEnabled(true);
            variantsEnable[randomIndex] = true;
            randomIndex = random.nextInt(6);
            countAnotherVariants--;
        }
        for (ImageView currentImageView : imageArray) {
            if (!currentImageView.isEnabled()) {
                currentImageView.setImageResource(R.drawable.white);
            }
        }
    }

    public void hintUsingShowActors() {
        String[] actors = new String[arrayCheckBox.length];
        for (int i = 0; i < arrayCheckBox.length; i++) {
            actors[i] = arrayCheckBox[i].getText().toString();
        }
        String[] pictures =
                BaseActorPicture
                        .getPicturesForTheseActors
                                (new MoviefanDatabaseHelper(this.getActivity().getApplication()), actors);
        for (int i = 0; i < pictures.length; i++) {
            if (imageArrayCheckBox[i].isEnabled()) {
                imageArray[i].setImageBitmap(GeneralMethods.getBitmap(pictures[i], cacheFolder));
            } else {
                imageArray[i].setImageResource(R.drawable.white);
            }
        }
        for (int i = 0; i < arrayCheckBox.length; i++) {
            imageArrayCheckBox[i].setChecked(arrayCheckBox[i].isChecked());
        }
        actorsPhotosGridLayout.setVisibility(View.VISIBLE);
        checkBoxGridLayout.setVisibility(View.GONE);
        wasHintShowActorsFlag = true;
    }

    public void hintUsingShowScreens() {
        String[] pictures = BaseMoviePicture.
                getPicturesForThisMovieActors
                        (new MoviefanDatabaseHelper(this.getActivity().getApplication()), movie);
        movieScreen1ImageView.setImageBitmap(GeneralMethods.getBitmap(pictures[0], cacheFolder));
        movieScreen2ImageView.setImageBitmap(GeneralMethods.getBitmap(pictures[1], cacheFolder));
        movieScreensLinearLayout.setVisibility(View.VISIBLE);
        wasHintShowScreensFlag = true;
    }

    public void last() {
        btnSkipEnableFlag = false;
    }

    private GuessTheMovieActorsFragment.FragListener listener;

    static interface FragListener {
        void itemClicked();
    }

    public void setVariants(ArrayList<String> variantsOfActors) {
        this.variantsOfMovies = variantsOfActors;
    }

    public GuessTheMovieActorsFragment() {
    }

    public void setMovieActors(String movie, String[] answers, File cacheFolder) {
        this.cacheFolder = cacheFolder;
        this.movie = movie;
        this.answers = answers;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.listener = ((GuessTheMovieActorsActivity) context);
            Log.d(LOG_GUESS_THE_MOVIE_ACTOR, "Fragment добавлен к " + context.getClass());
        }
        Log.d(LOG_GUESS_THE_MOVIE_ACTOR, "Attach Fragment'а завершен. Новая версия Attach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            movie = (String) savedInstanceState.getSerializable("M");
            answers = (String[]) savedInstanceState.getSerializable("A");
            cacheFolder = (File) savedInstanceState.getSerializable("Folder");
            variants = (String[]) savedInstanceState.getSerializable("Var");
            variantsEnable = savedInstanceState.getBooleanArray("VarEnable");
            wasAnswerFlag = savedInstanceState.getBoolean("Question");
            btnSkipEnableFlag = savedInstanceState.getBoolean("Skip");
            wasHintShowScreensFlag = savedInstanceState.getBoolean("wasHintShowScreensFlag");
            wasHintShowActorsFlag = savedInstanceState.getBoolean("wasHintShowActorsFlag");
            variantsChecked = savedInstanceState.getBooleanArray("variantsChecked");
            ((GuessTheMovieActorsActivity) getActivity()).setNewFrag(this);
            rotateFlag = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("M", movie);
        outState.putStringArray("A", answers);
        outState.putSerializable("Folder", cacheFolder);
        outState.putStringArray("Var", variants);
        outState.putBooleanArray("VarEnable", variantsEnable);
        outState.putBooleanArray("variantsChecked", variantsChecked);
        outState.putBoolean("Question", wasAnswerFlag);
        outState.putBoolean("Skip", btnSkipEnableFlag);
        outState.putBoolean("wasHintShowScreensFlag", wasHintShowScreensFlag);
        outState.putBoolean("wasHintShowActorsFlag", wasHintShowActorsFlag);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = ((GuessTheMovieActorsActivity) activity);
        Log.d(LOG_GUESS_THE_MOVIE_ACTOR, "Attach Fragment'а завершен. Старая версия Attach");
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            // Заполнение фрагмента
            Log.d(LOG_GUESS_THE_MOVIE_ACTOR, "Элемент View найден");
            movieScreensLinearLayout = (LinearLayout) view.findViewById(R.id.movieScreens);
            actorsPhotosGridLayout = (GridLayout) view.findViewById(R.id.gridActorPhotos);
            checkBoxGridLayout = (GridLayout) view.findViewById(R.id.gridText);
            btnNext = (Button) view.findViewById(R.id.btnSkip);
            btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
            movieTextView = (TextView) view.findViewById(R.id.movieName);
            movieTextView.setText(movie);
            btnConfirm.setOnClickListener(new ButtonListener());
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.itemClicked();
                }
            });
            btnNext.setEnabled(btnSkipEnableFlag);
            scrollView = (ScrollView) view.findViewById(R.id.scrollView);
            movieScreen1ImageView = (ImageView) view.findViewById(R.id.movieScreen1);
            movieScreen2ImageView = (ImageView) view.findViewById(R.id.movieScreen2);
            Random randNumber = new Random();
            arrayCheckBox = new CheckBox[6];
            arrayCheckBox[0] = (CheckBox) view.findViewById(R.id.checkVar1);
            arrayCheckBox[1] = (CheckBox) view.findViewById(R.id.checkVar2);
            arrayCheckBox[2] = (CheckBox) view.findViewById(R.id.checkVar3);
            arrayCheckBox[3] = (CheckBox) view.findViewById(R.id.checkVar4);
            arrayCheckBox[4] = (CheckBox) view.findViewById(R.id.checkVar5);
            arrayCheckBox[5] = (CheckBox) view.findViewById(R.id.checkVar6);
            imageArrayCheckBox = new CheckBox[6];
            imageArrayCheckBox[0] = (CheckBox) view.findViewById(R.id.checkBox1);
            imageArrayCheckBox[1] = (CheckBox) view.findViewById(R.id.checkBox2);
            imageArrayCheckBox[2] = (CheckBox) view.findViewById(R.id.checkBox3);
            imageArrayCheckBox[3] = (CheckBox) view.findViewById(R.id.checkBox4);
            imageArrayCheckBox[4] = (CheckBox) view.findViewById(R.id.checkBox5);
            imageArrayCheckBox[5] = (CheckBox) view.findViewById(R.id.checkBox6);
            imageArray = new ImageView[6];
            imageArray[0] = (ImageView) view.findViewById(R.id.imageCheckBox1);
            imageArray[1] = (ImageView) view.findViewById(R.id.imageCheckBox2);
            imageArray[2] = (ImageView) view.findViewById(R.id.imageCheckBox3);
            imageArray[3] = (ImageView) view.findViewById(R.id.imageCheckBox4);
            imageArray[4] = (ImageView) view.findViewById(R.id.imageCheckBox5);
            imageArray[5] = (ImageView) view.findViewById(R.id.imageCheckBox6);
            if (rotateFlag) {
                for (int b = 0; b < 6; b++) {
                    arrayCheckBox[b].setText(variants[b]);
                    arrayCheckBox[b].setEnabled(variantsEnable[b]);
                    imageArrayCheckBox[b].setEnabled(variantsEnable[b]);
                    imageArrayCheckBox[b].setChecked(variantsChecked[b]);
                    imageArray[b].setEnabled(variantsEnable[b]);
                    imageArray[b].setImageResource(R.drawable.white);
                }
            } else {
                variantsChecked = new boolean[6];
                for (boolean every : variantsChecked) every = false;
                variants = new String[6];
                variantsEnable = new boolean[6];
                ArrayList<Integer> positionArray = new ArrayList<Integer>();
                for (int i = 0; i < answers.length; i++) {
                    int randomPosition = randNumber.nextInt(6);
                    if (!positionArray.contains(randomPosition)) {
                        positionArray.add(randomPosition);
                    } else {
                        i--;
                    }
                    if (positionArray.size() == answers.length) {
                        break;
                    }
                }
                Integer[] positions = new Integer[answers.length];
                for (int i = 0; i < positions.length; i++) {
                    positions[i] = positionArray.get(i);
                }
                Arrays.sort(positions);
                int j = 0;
                for (int b = 0; b < 6; b++) {
                    if (j < positions.length && b == positions[j]) {
                        variants[b] = answers[j];
                        arrayCheckBox[b].setText(variants[b]);
                        j++;
                    } else {
                        int rand = randNumber.nextInt(variantsOfMovies.size());
                        variants[b] = variantsOfMovies.remove(rand);
                        arrayCheckBox[b].setText(variants[b]);

                    }
                    variantsEnable[b] = true;
                }

            }

            CheckBoxListener checkBoxImageListener = new CheckBoxListener();
            for (CheckBox currentCheckBox : imageArrayCheckBox) {
                currentCheckBox.setOnCheckedChangeListener(checkBoxImageListener);
            }
            for (CheckBox currentCheckBox : arrayCheckBox) {
                currentCheckBox.setOnCheckedChangeListener(checkBoxImageListener);
            }
            ImageListener imageListener = new ImageListener();
            for (ImageView currentImageView : imageArray) {
                currentImageView.setOnClickListener(imageListener);
            }
            if (wasHintShowActorsFlag) {
                hintUsingShowActors();
            }
            if (wasHintShowScreensFlag) {
                hintUsingShowScreens();
            }
            if (btnSkipEnableFlag) {
                btnNext.setText(getResources().getString(R.string.skip));
            } else {
                btnNext.setEnabled(false);
                btnNext.setText(getResources().getString(R.string.the_end));
            }
            if (wasAnswerFlag) {
                btnNext.setText(getResources().getString(R.string.next));
                int result = checkAnswer();
                btnConfirm.setEnabled(false);
                switch (result) {
                    case 0: {
                        scrollView.setBackgroundColor(Color.YELLOW);
                    }
                    ;
                    break;
                    case 1: {
                        scrollView.setBackgroundColor(Color.GREEN);

                    }
                    ;
                    break;
                    case -1: {
                        scrollView.setBackgroundColor(Color.RED);
                    }
                    ;
                    break;
                    default:
                        break;
                }

            }
            for (int i = 0; i < arrayCheckBox.length; i++) {
                arrayCheckBox[i].setChecked(variantsChecked[i]);
                imageArrayCheckBox[i].setChecked(variantsChecked[i]);
            }
        } else {
            Log.d(LOG_GUESS_THE_MOVIE_ACTOR, "Элемент View не найден!");
        }
    }

    private int checkAnswer() {
        ArrayList<String> list = new ArrayList(Arrays.asList(answers));
        Integer countOfTrueAnswers = answers.length;
        Integer countOfMyAnswers = 0;
        Integer check = 0;
        for (int i = 0; i < arrayCheckBox.length; i++) {
            if (arrayCheckBox[i].isChecked() || imageArrayCheckBox[i].isChecked()) {
                if (list.contains(arrayCheckBox[i].getText())) {
                    countOfMyAnswers++;
                }
                check++;
            }
            arrayCheckBox[i].setEnabled(false);
            imageArrayCheckBox[i].setEnabled(false);
        }
        int result;
        if (countOfMyAnswers == countOfTrueAnswers && check <= countOfTrueAnswers) {
            result = 1;
        } else if (0 < countOfMyAnswers && check <= countOfTrueAnswers) {
            result = 0;
        } else {
            result = -1;
        }
        return result;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guess_the_movie_actors, container, false);
    }


    // Обработчик события проверки корректоности
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int result = checkAnswer();
            v.setEnabled(false);
            switch (result) {
                case 0: {
                    makeToast(getActivity(),
                            almostTrueAnswer(getActivity().getApplicationContext()), Toast.LENGTH_SHORT);
                    scrollView.setBackgroundColor(Color.YELLOW);
                    UserStatistics.appendTrueAnswerMovieActors(getActivity());
                    Log.d(LOG_GUESS_THE_MOVIE_ACTOR, "Сработал почти правильный ответ");

                }
                ;
                break;
                case 1: {
                    makeToast(getActivity(),
                            trueAnswer(getActivity().getApplicationContext()), Toast.LENGTH_SHORT);
                    scrollView.setBackgroundColor(Color.GREEN);
                    UserStatistics.appendTrueAnswerMovieActors(getActivity());
                    Log.d(LOG_GUESS_THE_MOVIE_ACTOR, "Сработал правильный ответ");
                }
                ;
                break;
                case -1: {
                    makeToast(getActivity(),
                            GeneralEvents.falseAnswer(getActivity().getApplicationContext()), Toast.LENGTH_SHORT);
                    scrollView.setBackgroundColor(Color.RED);
                    UserStatistics.appendFalseAnswerMovieActors(getActivity());
                    Log.d(LOG_GUESS_THE_MOVIE_ACTOR, "Сработал ложный ответ");
                }
                ;
                break;
                default:
                    break;
            }
            wasAnswerFlag = true;
            btnNext.setText(getResources().getString(R.string.next));
            ((GuessTheMovieActorsActivity) getActivity()).wasAnswer();
            if (!btnSkipEnableFlag) {
                makeToast(getActivity(),
                        "Викторина закончена!", Toast.LENGTH_SHORT);
                Log.d(LOG_GUESS_THE_MOVIE_ACTOR, "Последний вопрос");
            }
        }
    }

    // Обработчик события нажатия на картинку
    private class ImageListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            int index = 0;
            switch (id) {
                case (R.id.imageCheckBox1):
                    index = 0;
                    break;
                case (R.id.imageCheckBox2):
                    index = 1;
                    break;
                case (R.id.imageCheckBox3):
                    index = 2;
                    break;
                case (R.id.imageCheckBox4):
                    index = 3;
                    break;
                case (R.id.imageCheckBox5):
                    index = 4;
                    break;
                case (R.id.imageCheckBox6):
                    index = 5;
                    break;

            }
            boolean itWas = !imageArrayCheckBox[index].isChecked();
            imageArrayCheckBox[index].setChecked(itWas);
            arrayCheckBox[index].setChecked(itWas);
            variantsChecked[index] = itWas;
        }
    }


    private class CheckBoxListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            switch (id) {
                case R.id.checkBox1: {
                    arrayCheckBox[0].setChecked(isChecked);
                    variantsChecked[0] = isChecked;
                }
                break;
                case R.id.checkBox2: {
                    arrayCheckBox[1].setChecked(isChecked);
                    variantsChecked[1] = isChecked;
                }
                break;
                case R.id.checkBox3: {
                    arrayCheckBox[2].setChecked(isChecked);
                    variantsChecked[2] = isChecked;
                }
                break;
                case R.id.checkBox4: {
                    arrayCheckBox[3].setChecked(isChecked);
                    variantsChecked[3] = isChecked;
                }
                break;
                case R.id.checkBox5: {
                    arrayCheckBox[4].setChecked(isChecked);
                    variantsChecked[4] = isChecked;
                }
                break;
                case R.id.checkBox6: {
                    arrayCheckBox[5].setChecked(isChecked);
                    variantsChecked[5] = isChecked;
                }
                break;
                case R.id.checkVar1: {
                    imageArrayCheckBox[0].setChecked(isChecked);
                    variantsChecked[0] = isChecked;
                }
                break;
                case R.id.checkVar2: {
                    imageArrayCheckBox[1].setChecked(isChecked);
                    variantsChecked[1] = isChecked;
                }
                break;
                case R.id.checkVar3: {
                    imageArrayCheckBox[2].setChecked(isChecked);
                    variantsChecked[2] = isChecked;
                }
                break;
                case R.id.checkVar4: {
                    imageArrayCheckBox[3].setChecked(isChecked);
                    variantsChecked[3] = isChecked;
                }
                break;
                case R.id.checkVar5: {
                    imageArrayCheckBox[4].setChecked(isChecked);
                    variantsChecked[4] = isChecked;
                }
                break;
                case R.id.checkVar6: {
                    imageArrayCheckBox[5].setChecked(isChecked);
                    variantsChecked[5] = isChecked;
                }
                break;
            }
        }

    }
}
