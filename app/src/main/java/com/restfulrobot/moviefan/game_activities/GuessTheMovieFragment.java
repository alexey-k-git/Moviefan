package com.restfulrobot.moviefan.game_activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.restfulrobot.moviefan.R;
import com.restfulrobot.moviefan.database.BaseMoviePicture;
import com.restfulrobot.moviefan.GeneralMethods;
import com.restfulrobot.moviefan.structure.MoviePicture;
import com.restfulrobot.moviefan.achievement.UserStatistics;

import static android.widget.Toast.makeText;
import static com.restfulrobot.moviefan.GeneralEvents.falseAnswer;
import static com.restfulrobot.moviefan.GeneralEvents.trueAnswer;
import static com.restfulrobot.moviefan.GeneralMethods.makeToast;
import static com.restfulrobot.moviefan.logs.MovieFanLogs.*;

public class GuessTheMovieFragment extends Fragment {

    private File cacheFolder;
    private ArrayList<String> variantsOfMovies;
    private MoviePicture mp;
    private Boolean btnSkipEnableFlag = true;
    private Boolean wasAnswerFlag = false;
    private Boolean rotateFlag = false;
    private boolean variantsEnable[];
    private String pictureName;
    private String answer;
    private String variants[];
    private Button nextBtn;
    private Button[] arrayBtn;
    private ImageView screenImageView;
    private ScrollView scrollView;

    public void last() {
        btnSkipEnableFlag = false;
    }

    private FragListener listener;

    static interface FragListener {
        void itemClicked();
    }

    public void setVariants(ArrayList<String> variantsOfMovies) {
        this.variantsOfMovies = variantsOfMovies;
    }

    public GuessTheMovieFragment() {
    }

    public void setMovie(MoviePicture mp, File file) {

        this.mp = mp;
        pictureName = mp.getImageFileName();
        answer = mp.getAnswer();
        cacheFolder = file;
    }

    public void hintUsingLessVariants() {
        int answerIndex = 0;
        for (int b = 0; b < 4; b++) {
            if (arrayBtn[b].getText().equals(answer)) {
                answerIndex = b;
                continue;
            }
            arrayBtn[b].setEnabled(false);
            variantsEnable[b] = false;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(4);
        while (randomIndex == answerIndex) {
            randomIndex = random.nextInt(4);
        }
        arrayBtn[randomIndex].setEnabled(true);
        variantsEnable[randomIndex] = true;
    }

    public void hintUsingAnotherPicture() {

        String anotherPuicture = mp.getImageFileName();
        while (anotherPuicture.equals(pictureName)) {
            anotherPuicture = mp.getImageFileName();
        }
        pictureName = anotherPuicture;
        Bitmap picture = GeneralMethods.getBitmap(pictureName, cacheFolder);
        screenImageView.setImageBitmap(picture);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(LOG_S, "onAttach " + "MovieFragment");
        if (context instanceof Activity) {
            this.listener = ((GuessTheMovieActivity) context);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_S, "onCreate " + "MovieFragment");
        if (savedInstanceState != null) {
            pictureName = savedInstanceState.getString("PN");
            answer = savedInstanceState.getString("A");
            cacheFolder = (File) savedInstanceState.getSerializable("Folder");
            variants = (String[]) savedInstanceState.getSerializable("Var");
            variantsEnable = savedInstanceState.getBooleanArray("VarEnable");
            mp = (MoviePicture) savedInstanceState.getSerializable("Mp");
            wasAnswerFlag = savedInstanceState.getBoolean("Question");
            btnSkipEnableFlag = savedInstanceState.getBoolean("Skip");
            ((GuessTheMovieActivity) getActivity()).setNewFrag(this);
            rotateFlag = true;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LOG_S, "onAttachOld " + "MovieFragment");
        this.listener = ((GuessTheMovieActivity) activity);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_S, "onSaveInstanceState " + "MovieFragment");
        outState.putString("PN", pictureName);
        outState.putString("A", answer);
        outState.putSerializable("Folder", cacheFolder);
        outState.putStringArray("Var", variants);
        outState.putBooleanArray("VarEnable", variantsEnable);
        outState.putSerializable("Mp", mp);
        outState.putSerializable("Question", wasAnswerFlag);
        outState.putSerializable("Skip", btnSkipEnableFlag);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_S, "onStart " + "MovieFragment");
        View view = getView();
        if (view != null) {
            // Заполнение фрагмента
            Log.d(LOG_GUESS_THE_MOVIE, "Элемент View найден");
            nextBtn = (Button) view.findViewById(R.id.btnSkip);
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.itemClicked();
                }
            });
            nextBtn.setEnabled(btnSkipEnableFlag);
            Bitmap picture = GeneralMethods.getBitmap(pictureName, cacheFolder);
            screenImageView = (ImageView) view.findViewById(R.id.imageMovie);
            screenImageView.setImageBitmap(picture);
            scrollView = (ScrollView) view.findViewById(R.id.scrollView);
            Random randNumber = new Random();
            int iNumber = randNumber.nextInt(4);
            arrayBtn = new Button[4];
            arrayBtn[0] = (Button) view.findViewById(R.id.btnVar1);
            arrayBtn[1] = (Button) view.findViewById(R.id.btnVar2);
            arrayBtn[2] = (Button) view.findViewById(R.id.btnVar3);
            arrayBtn[3] = (Button) view.findViewById(R.id.btnVar4);
            ButtonListener bl = new ButtonListener();
            if (rotateFlag) {
                for (int b = 0; b < 4; b++) {
                    arrayBtn[b].setText(variants[b]);
                    arrayBtn[b].setEnabled(variantsEnable[b]);
                    arrayBtn[b].setOnClickListener(bl);
                }
            } else {
                variants = new String[4];
                variantsEnable = new boolean[4];
                for (int b = 0; b < 4; b++) {
                    if (b == iNumber) {
                        arrayBtn[b].setText(answer);
                        variants[b] = answer;
                    } else {
                        int rand = randNumber.nextInt(variantsOfMovies.size());
                        variants[b] = variantsOfMovies.remove(rand);
                        arrayBtn[b].setText(variants[b]);

                    }
                    arrayBtn[b].setOnClickListener(bl);
                    variantsEnable[b] = true;
                }
            }
            if (btnSkipEnableFlag) {
                nextBtn.setText(getResources().getString(R.string.skip));
            } else {
                nextBtn.setEnabled(false);
                nextBtn.setText(getResources().getString(R.string.the_end));
            }
            if (wasAnswerFlag) {
                nextBtn.setText(getResources().getString(R.string.next));
                int indexAnswer = 0;
                for (int b = 0; b < 4; b++) {
                    if (variantsEnable[b]) {
                        indexAnswer = b;
                        break;
                    }
                }
                if (arrayBtn[indexAnswer].getText().equals(answer)) {
                    scrollView.setBackgroundColor(Color.GREEN);
                } else {
                    scrollView.setBackgroundColor(Color.RED);
                }
            }
        } else {
            Log.d(LOG_GUESS_THE_MOVIE, "Элемент View не найден!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_S, "onCreateView " + "MovieFragment");
        return inflater.inflate(R.layout.fragment_guess_the_movie, container, false);
    }

    // Обработчик события проверки корректоности выбранного фильма
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            for (Button btn : arrayBtn) btn.setEnabled(false);
            v.setEnabled(true);
            for (int i = 0; i < arrayBtn.length; i++) {
                if (arrayBtn[i].equals((Button) v)) {
                    variantsEnable[i] = true;
                } else {
                    variantsEnable[i] = false;
                }

            }
            if (((Button) v).getText().equals(answer)) {
                makeToast(getActivity(),
                        trueAnswer(getActivity().getApplicationContext()), Toast.LENGTH_SHORT);
                scrollView.setBackgroundColor(Color.GREEN);
                ((GuessTheMovieActivity) getActivity()).setColor(Color.GREEN);
                UserStatistics.appendTrueAnswerMovie(getActivity());
                try {
                    BaseMoviePicture.iKnowThisMovie(answer);
                } catch (SQLiteException e) {
                    makeToast(getActivity(), "Ошибка правильного ответа.\n" +
                            " База данных недоступна.", Toast.LENGTH_SHORT);
                }
                Log.d(LOG_GUESS_THE_MOVIE, "Сработал правильный ответ");

            } else {
                makeToast(getActivity(),
                        falseAnswer(getActivity().getApplicationContext()), Toast.LENGTH_SHORT);
                scrollView.setBackgroundColor(Color.RED);
                ((GuessTheMovieActivity) getActivity()).setColor(Color.RED);
                UserStatistics.appendFalseAnswerMovie(getActivity());
                Log.d(LOG_GUESS_THE_MOVIE, "Сработал ложный ответ");
            }
            wasAnswerFlag = true;
            nextBtn.setText(getResources().getString(R.string.next));
            ((GuessTheMovieActivity) getActivity()).wasAnswer();
            if (!btnSkipEnableFlag) {
                makeToast(getActivity(),
                        "Викторина закончена!", Toast.LENGTH_SHORT);
                Log.d(LOG_GUESS_THE_MOVIE, "Последний вопрос");
            }
        }

    }
}
