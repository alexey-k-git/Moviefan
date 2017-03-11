package com.restfulrobot.moviefan.game_activities;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.restfulrobot.moviefan.R;

public class HintMovieActorsDialog extends DialogFragment {
    private Boolean wasHintLessVariantsFlag = false;
    private Button movieActorsHintLessVariantsBtn;
    private Boolean wasHintShowScreensFlag = false;
    private Button movieActorsHintShowScreensBtn;
    private Boolean wasHintShowActorsFlag = false;
    private Button movieActorsHintShowActorsBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.AppThemeDialog);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Использовать подсказку");
        View v = inflater.inflate(R.layout.hint_movie_actors_dialog, null);
        v.findViewById(R.id.btnMovieActorsHintCancel).setOnClickListener((GuessTheMovieActorsActivity) this.getActivity());
        movieActorsHintLessVariantsBtn = (Button) v.findViewById(R.id.btnMovieActorsHintLessVariants);
        movieActorsHintLessVariantsBtn.setOnClickListener((GuessTheMovieActorsActivity) this.getActivity());
        movieActorsHintLessVariantsBtn.setEnabled(!wasHintLessVariantsFlag);

        movieActorsHintShowScreensBtn = (Button) v.findViewById(R.id.btnMovieActorsHintShowScreens);
        movieActorsHintShowScreensBtn.setOnClickListener((GuessTheMovieActorsActivity) this.getActivity());
        movieActorsHintShowScreensBtn.setEnabled(!wasHintShowScreensFlag);

        movieActorsHintShowActorsBtn = (Button) v.findViewById(R.id.btnMovieActorsHintShowActors);
        movieActorsHintShowActorsBtn.setOnClickListener((GuessTheMovieActorsActivity) this.getActivity());
        movieActorsHintShowActorsBtn.setEnabled(!wasHintShowActorsFlag);
        return v;
    }


    public void setHintsEnable(Boolean wasHintLessVariants, Boolean wasHintShowScreens, Boolean wasHintShowActors) {
        this.wasHintLessVariantsFlag = wasHintLessVariants;
        this.wasHintShowScreensFlag = wasHintShowScreens;
        this.wasHintShowActorsFlag = wasHintShowActors;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}
