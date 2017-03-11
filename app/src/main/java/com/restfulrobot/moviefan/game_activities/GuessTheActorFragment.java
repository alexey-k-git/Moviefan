package com.restfulrobot.moviefan.game_activities;


import android.app.Activity;
import android.content.Context;
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

import com.restfulrobot.moviefan.GeneralEvents;
import com.restfulrobot.moviefan.R;
import com.restfulrobot.moviefan.achievement.UserStatistics;
import com.restfulrobot.moviefan.GeneralMethods;
import com.restfulrobot.moviefan.structure.ActorPicture;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static com.restfulrobot.moviefan.GeneralEvents.trueAnswer;
import static com.restfulrobot.moviefan.GeneralMethods.makeToast;
import static com.restfulrobot.moviefan.logs.MovieFanLogs.LOG_GUESS_THE_ACTOR;

public class GuessTheActorFragment extends Fragment {

    private File cacheFolder;
    private ArrayList<String> variantsOfActors;
    private Boolean btnSkipEnableFlag = true;
    private Boolean rotateFlag = false;
    private Boolean wasAnswerFlag = false;
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

    private GuessTheActorFragment.FragListener listener;

    static interface FragListener {
        void itemClicked();
    }

    public void setVariants(ArrayList<String> variantsOfActors) {
        this.variantsOfActors = variantsOfActors;
    }

    public GuessTheActorFragment() {
    }

    public void setActor(ActorPicture ap, File file) {

        pictureName = ap.getImageFileName();
        answer = ap.getName();
        cacheFolder = file;
    }

    public void hintUsingLessVariants() {
        int answerIndex = 0;
        for (int b = 0; b < 3; b++) {
            if (arrayBtn[b].getText().equals(answer)) {
                answerIndex = b;
                continue;
            }
            arrayBtn[b].setEnabled(false);
            variantsEnable[b] = false;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(3);
        while (randomIndex == answerIndex) {
            randomIndex = random.nextInt(3);
        }
        arrayBtn[randomIndex].setEnabled(true);
        variantsEnable[randomIndex] = true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.listener = ((GuessTheActorActivity) context);
            Log.d(LOG_GUESS_THE_ACTOR, "Fragment добавлен к " + context.getClass());
        }
        Log.d(LOG_GUESS_THE_ACTOR, "Attach Fragment'а завершен. Новая версия Attach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            pictureName = (String) savedInstanceState.getSerializable("AN");
            answer = (String) savedInstanceState.getSerializable("A");
            cacheFolder = (File) savedInstanceState.getSerializable("Folder");
            variants = (String[]) savedInstanceState.getSerializable("Var");
            rotateFlag = true;
            variantsEnable = savedInstanceState.getBooleanArray("VarEnable");
            wasAnswerFlag = savedInstanceState.getBoolean("Question");
            btnSkipEnableFlag = savedInstanceState.getBoolean("Skip");
            ((GuessTheActorActivity) getActivity()).setNewFrag(this);
            rotateFlag = true;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = ((GuessTheActorActivity) activity);
        Log.d(LOG_GUESS_THE_ACTOR, "Attach Fragment'а завершен. Старая версия Attach");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("AN", pictureName);
        outState.putSerializable("A", answer);
        outState.putSerializable("Folder", cacheFolder);
        outState.putSerializable("Var", variants);
        outState.putBooleanArray("VarEnable", variantsEnable);
        outState.putSerializable("Question", wasAnswerFlag);
        outState.putSerializable("Skip", btnSkipEnableFlag);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            // Заполнение фрагмента
            Log.d(LOG_GUESS_THE_ACTOR, "Элемент View найден");
            nextBtn = (Button) view.findViewById(R.id.btnSkip);
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.itemClicked();
                }
            });
            nextBtn.setEnabled(btnSkipEnableFlag);
            Bitmap picture = GeneralMethods.getBitmap(pictureName, cacheFolder);
            screenImageView = (ImageView) view.findViewById(R.id.imageActor);
            screenImageView.setImageBitmap(picture);
            scrollView = (ScrollView) view.findViewById(R.id.scrollView);
            Random randNumber = new Random();
            int iNumber = randNumber.nextInt(3);
            arrayBtn = new Button[3];
            arrayBtn[0] = (Button) view.findViewById(R.id.btnVar1);
            arrayBtn[1] = (Button) view.findViewById(R.id.btnVar2);
            arrayBtn[2] = (Button) view.findViewById(R.id.btnVar3);
            GuessTheActorFragment.ButtonListener bl = new GuessTheActorFragment.ButtonListener();
            if (rotateFlag) {
                for (int b = 0; b < 3; b++) {
                    arrayBtn[b].setText(variants[b]);
                    arrayBtn[b].setEnabled(variantsEnable[b]);
                    arrayBtn[b].setOnClickListener(bl);
                }
            } else {
                variants = new String[3];
                variantsEnable = new boolean[3];
                for (int b = 0; b < 3; b++) {
                    if (b == iNumber) {
                        arrayBtn[b].setText(answer);
                        variants[b] = answer;
                    } else {
                        int rand = randNumber.nextInt(variantsOfActors.size());
                        variants[b] = variantsOfActors.remove(rand);
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
                for (int b = 0; b < 3; b++) {
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
            Log.d(LOG_GUESS_THE_ACTOR, "Элемент View не найден!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guess_the_actor, container, false);
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
                ((GuessTheActorActivity) getActivity()).setColor(Color.GREEN);
                UserStatistics.appendTrueAnswerActor(getActivity());
                Log.d(LOG_GUESS_THE_ACTOR, "Сработал правильный ответ");

            } else {
                makeToast(getActivity(),
                        GeneralEvents.falseAnswer(getActivity().getApplicationContext()), Toast.LENGTH_SHORT);
                scrollView.setBackgroundColor(Color.RED);
                ((GuessTheActorActivity) getActivity()).setColor(Color.RED);
                UserStatistics.appendFalseAnswerActor(getActivity());
                Log.d(LOG_GUESS_THE_ACTOR, "Сработал ложный ответ");
            }
            wasAnswerFlag = true;
            nextBtn.setText(getResources().getString(R.string.next));
            ((GuessTheActorActivity) getActivity()).wasAnswer();
            if (!btnSkipEnableFlag) {
                makeToast(getActivity(),
                        "Викторина закончена!", Toast.LENGTH_SHORT);
                Log.d(LOG_GUESS_THE_ACTOR, "Последний вопрос");
            }
        }
    }

}
